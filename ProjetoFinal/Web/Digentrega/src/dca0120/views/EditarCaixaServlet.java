package dca0120.views;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import dca0120.dao.CaixasDAO;
import dca0120.dao.TelefonesDAO;
import dca0120.model.Caixa;
import dca0120.utils.Hashing;
import dca0120.utils.TratadorURI;
import dca0120.utils.ValidadorCPF;

@MultipartConfig
public class EditarCaixaServlet extends HttpServlet {

	private static final long serialVersionUID = -7552123280167571493L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");

		//Verifica se o usu�rio est� conectado
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession(true);
			session.setAttribute("mensagem", "Voc� precisa entrar no sistema para acessar esta fun��o.");
			response.sendRedirect(TratadorURI.getRaizURL(request));
			return;
		}

		// Verifica se o usu�rio que quer acessar esta fun��o � o administrador.
		Integer administrador = (Integer) session.getAttribute("administrador");
		if (administrador == null) {
			session.setAttribute("mensagem", "Apenas o administrador pode cadastrar funcion�rios.");
			response.sendRedirect(TratadorURI.getRaizURL(request));
			return;
		}

		CaixasDAO cd = new CaixasDAO();
		TelefonesDAO td = new TelefonesDAO();
		
		try {
			int id = Integer.parseInt(request.getParameter("id"));
			Caixa caixa = cd.getCaixaWithID(id);
			List<String> telefones = td.getTelefones(id);
			if (caixa != null) {
				caixa.setTelefones(telefones);
				request.setAttribute("caixa", caixa);
				request.getRequestDispatcher("/editarCaixa.jsp").forward(request, response);
				return;
			} else {
				response.sendRedirect(TratadorURI.getRaizURL(request));
				return;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);
		if (session == null) {
			session = request.getSession(true);
			session.setAttribute("mensagem", "Voc� precisa entrar no sistema para acessar esta fun��o.");
			response.sendRedirect(TratadorURI.getRaizURL(request));
			return;
		}

		// Verifica se o usu�rio que quer acessar esta fun��o � o administrador.
		Integer administrador = (Integer) session.getAttribute("administrador");
		if (administrador == null) {
			session.setAttribute("mensagem", "Apenas o administrador pode cadastrar funcion�rios.");
			response.sendRedirect(TratadorURI.getRaizURL(request));
			return;
		}
		
		int id = Integer.parseInt(request.getParameter("id"));
		CaixasDAO cd = new CaixasDAO();
		Caixa original = cd.getCaixaWithID(id);
		
		//coletando parametros
		String nome = request.getParameter("nome");
        String cpfStr = request.getParameter("cpf");
        String dataNascimentoStr = request.getParameter("dataNascimento");
        String telefone1 = request.getParameter("telefone_1");
        String senha1 = request.getParameter("senha1");
        String senha2 = request.getParameter("senha2");

        //String cpf = original.getCpf();//inicializando variavel
        
        if(!nome.trim().isEmpty()) {
        	original.setNome(nome);
        }
        
        if(!cpfStr.trim().isEmpty()) {
        	// Transforma o CPF em n�meros apenas.
            String cpf = cpfStr.replace(".", "").replace("-", "");
            
            if(cpf != original.getCpf()) {
	            // Valida o CPF.
	            if(!ValidadorCPF.isValidCPF(cpf)) {
	            	session.setAttribute("mensagem", "CPF inv�lido! Tente novamente.");
	                response.sendRedirect(request.getHeader("referer"));
	                return;
	            }
            }
            
            original.setCpf(cpf);
        }
        
        // Altere a senha somente se ela foi, de fato, alterada.
        if(!senha1.trim().isEmpty()) {
        	// Verifica se as senhas s�o iguais.
            if(!senha1.equals(senha2)) {
            	session.setAttribute("mensagem", "As senhas n�o conferem.");
                response.sendRedirect(request.getHeader("referer"));
                return;
            }
            
        	String senhaCriptografada = Hashing.plainToSHA256(senha1, original.getCpf().getBytes());
        	original.setSenha(senhaCriptografada);
        }    
        
        if(!dataNascimentoStr.trim().isEmpty()) {
        	// Valida a data de nascimento.
            Calendar dataNascimento = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
    			dataNascimento.setTime(sdf.parse(dataNascimentoStr));
    		} catch (ParseException e) {
    			request.setAttribute("mensagem", "Data de nascimento inv�lida!");
                response.sendRedirect(request.getHeader("referer"));
                return;
    		}
            
            original.setDataNascimento(dataNascimento);
        }
        
        
        if(!telefone1.trim().isEmpty()) {
        	// Organiza o vetor de telefones.
            List<String> telefones = new ArrayList<String>();
            int i = 1;
            while(request.getParameter("telefone_" + i) != null) {
            	telefones.add(request.getParameter("telefone_" + i));
            	i++;
            }
            original.setTelefones(telefones);
        }
        
        
        // Insere-o no BD.
        cd.alterarCaixa(original, administrador);
        
        session.setAttribute("mensagem", "Caixa editado com sucesso!");
		response.sendRedirect(TratadorURI.getRaizURL(request));
	}

}
