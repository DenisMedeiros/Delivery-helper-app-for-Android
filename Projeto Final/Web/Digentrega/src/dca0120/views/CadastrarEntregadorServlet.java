package dca0120.views;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dca0120.dao.EntregadoresDAO;
import dca0120.model.Entregador;
import dca0120.utils.Hashing;
import dca0120.utils.ValidadorCPF;

public class CadastrarEntregadorServlet extends HttpServlet {


	private static final long serialVersionUID = -7552123270167571493L;
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);	
		if(session == null) {
			session = request.getSession(true);	
			session.setAttribute("mensagem", "Voc� precisa entrar no sistema para acessar esta fun��o.");
        	response.sendRedirect(request.getContextPath());
        	return;
		}
		
		// Verifica se o usu�rio que quer acessar esta fun��o � o administrador.
		Integer administrador = (Integer) session.getAttribute("administrador");
		if(administrador == null) {
	     	session.setAttribute("mensagem", "Apenas o administrador pode cadastrar funcion�rios.");
        	response.sendRedirect(request.getContextPath());
        	return;
		}
		
        request.getRequestDispatcher("/cadastrarEntregador.jsp").forward(request, response);
    }

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		HttpSession session = request.getSession(false);	
		if(session == null) {
			session = request.getSession(true);	
			session.setAttribute("mensagem", "Voc� precisa entrar no sistema para acessar esta fun��o.");
        	response.sendRedirect(request.getContextPath());
        	return;
		}
		
		EntregadoresDAO ed = new EntregadoresDAO();
		
		// Verifica se o usu�rio que quer acessar esta fun��o � o administrador.
		Integer administradorID = (Integer) session.getAttribute("administrador");
		if(administradorID == null) {
	     	session.setAttribute("mensagem", "Apenas o administrador pode cadastrar funcion�rios.");
        	response.sendRedirect(request.getContextPath());
        	return;
		}
		
        String nome = request.getParameter("nome");
        String cpfStr = request.getParameter("cpf");
        String dataNascimentoStr = request.getParameter("dataNascimento");
        String telefone1 = request.getParameter("telefone_1");
        String senha1 = request.getParameter("senha1");
        String senha2 = request.getParameter("senha2");
        String cnh = request.getParameter("cnh");
        String placa = request.getParameter("placa");

        // Verifica se algum campo chegou em branco.
        if(nome.trim().isEmpty() || cpfStr.trim().isEmpty() || dataNascimentoStr.trim().isEmpty() || 
        		telefone1.trim().isEmpty() || senha1.trim().isEmpty() || senha2.trim().isEmpty() || 
        		cnh.trim().isEmpty() || placa.trim().isEmpty()) {
            session.setAttribute("mensagem", "Algum dos campos foi enviado em branco. Tente novamente!");
            response.sendRedirect(request.getHeader("referer"));
            return;
        }
		
     // Transforma o CPF em n�meros apenas.
        String cpf = cpfStr.replace(".", "").replace("-", "");
        
        // Valida o CPF.
        if(!ValidadorCPF.isValidCPF(cpf)) {
        	session.setAttribute("mensagem", "CPF inv�lido! Tente novamente.");
            response.sendRedirect(request.getHeader("referer"));
            return;
        }
        
        // Verifica de o CPF j� existe para algum funcion�rio.
        if(ed.getID(cpf) != -1) {
        	session.setAttribute("mensagem", "J� existe um funcion�rio com este CPF! Tente novamente.");
            response.sendRedirect(request.getHeader("referer"));
            return;
        }
        
        // Verifica se as senhas s�o iguais.
        if(!senha1.equals(senha2)) {
        	session.setAttribute("mensagem", "As senhas n�o conferem.");
            response.sendRedirect(request.getHeader("referer"));
            return;
        }
        
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
        
        // Organiza o vetor de telefones.
        List<String> telefones = new ArrayList<String>();
        int i = 1;
        while(request.getParameter("telefone_" + i) != null) {
        	telefones.add(request.getParameter("telefone_" + i));
        	i++;
        }
        
        //Valida a placa
        placa = placa.toUpperCase(Locale.ROOT);
        
        String senhaCriptografada = Hashing.plainToSHA256(senha1, cpf.getBytes());

        // Cria o objeto Caixa.
        Entregador entregador = new Entregador(0, cpf, senhaCriptografada, nome, dataNascimento, telefones, cnh, placa);
       
        // Insere-o no BD.
        ed.inserirEntregador(entregador, administradorID);
        
        session.setAttribute("mensagem", "Entregador(a) cadastrado(a) com sucesso!");
        response.sendRedirect(request.getContextPath());
    }

} 