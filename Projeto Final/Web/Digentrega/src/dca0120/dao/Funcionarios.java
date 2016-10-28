package dca0120.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

/**
 * 
 * @author ney
 * @author denis
 *
 * Classe respons�vel por interconectar a classe Funcionario com a tabela Funcionarios.
 */
public abstract class Funcionarios {
	
	protected Connection conexao;
	
	
	/**
	 * Construtor padr�o da classe Funcionarios que abre conex�o com o banco de dados
	 */
	public Funcionarios() {
		try {
			conexao = ConnectionFactory.getConexao();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cria a tabela Funcionarios no banco de dados.
	 */
	public void criarTabelaFuncionarios() { 
		try {
			Statement st = conexao.createStatement();
	        String sql = "CREATE TABLE IF NOT EXISTS Funcionarios (" +
	                 "ID INTEGER AUTO_INCREMENT, " +
	                 "Nome VARCHAR(255) NOT NULL, " +
	                 "CPF VARCHAR(11) NOT NULL UNIQUE, " +
	                 "Senha VARCHAR(64) NOT NULL, " +
	                 "DataNascimento DATE NOT NULL, " +
	                 "AdministradorID INTEGER NOT NULL, " +
	                 "PRIMARY KEY (ID), " +
	                 "FOREIGN KEY (AdministradorID ) REFERENCES Funcionarios(ID), " +
	                 ")";
	      //testar o primeiro dado inserido na tabela (foreign key)
	        st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Insere um Funcionario no banco de dados
	 * @param nome Atributo do tipo String da classe abstrata Funcionario a ser inserido no banco de dados 
	 * @param cpf Atributo do tipo String da classe abstrata Funcionario a ser inserido no banco de dados
	 * @param senha Atributo do tipo String da classe abstrata Funcionario a ser inserido no banco de dados
	 * @param dataNascimento Atributo do tipo Calendar da classe abstrata Funcionario a ser inserido no banco de dados
	 * @param AdmID Atributo do tipo int da classe abstrata Funcionario a ser inserido no banco de dados
	 */
	public void inserirFuncionario(String nome, String cpf, String senha, Calendar dataNascimento, int AdmID) {
		try {
			PreparedStatement pst = conexao.prepareStatement("INSERT INTO Funcionarios(Nome, CPF, "
					+ "Senha, DataNascimento, AdministradorID) VALUES (?, ?, ?, ?, ?)");
			
	        pst.setString(1, nome);
	        pst.setString(2, cpf);
	        pst.setString(3, senha);
	        	        
	        Calendar calendar = dataNascimento;
	        java.sql.Date javaSqlDate;
	        
	        if(calendar != null) {
		        calendar.set(Calendar.HOUR_OF_DAY, 0);
		        calendar.set(Calendar.MINUTE, 0);
		        calendar.set(Calendar.SECOND, 0);
		        calendar.set(Calendar.MILLISECOND, 0);
		        javaSqlDate = new java.sql.Date(calendar.getTime().getTime());  
	        } else {
	        	javaSqlDate = new java.sql.Date(0);
	        }
	        
	        pst.setDate(4, javaSqlDate);
	        pst.setInt(5, AdmID);
	        
	        pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 	
	 * Retorna a ID do funcionario que possui o cpf especificado.
	 * 
	 * @param cpf CPF do usu�rio.
	 * @return id ID de Funcionario com o cpf especificado (ou -1, caso n�o exista).
	 */
	public int getID(String cpf) {
		int id = -1;
		try {
			String sql = "SELECT ID FROM Funcionarios WHERE CPF=?;";
			PreparedStatement pst = conexao.prepareStatement(sql);
			
			pst.setString(1, cpf);
	        ResultSet res = pst.executeQuery();
	       
	        if (res.wasNull()) {
	        	return id;
	        }
	        
	        if(res.next()) {
	        	id = res.getInt("ID");       	        	
	        }
	        
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return id;
	}
	
	public String getNome(int ID) {
		String nome = null;
		try {
			String sql = "SELECT Nome FROM Funcionarios WHERE ID=?;";
			PreparedStatement pst = conexao.prepareStatement(sql);
			
			pst.setInt(1, ID);
	        ResultSet res = pst.executeQuery();
	       
	        if (res.wasNull()) {
	        	return nome;
	        }
	        
	        if(res.next()) {
	        	nome = res.getString("Nome");       	        	
	        }
	        
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return nome;
	}
	
	public String getSenha(int ID) {
		String senha = null;
		try {
			String sql = "SELECT Senha FROM Funcionarios WHERE ID=?;";
			PreparedStatement pst = conexao.prepareStatement(sql);
			
			pst.setInt(1, ID);
	        ResultSet res = pst.executeQuery();
	       
	        if (res.wasNull()) {
	        	return senha;
	        }
	        
	        if(res.next()) {
	        	senha = res.getString("Senha");       	        	
	        }
	        
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return senha;
	}
	
	public Calendar getDataNascimento(int ID) {
		Calendar dataNascimento = null;
		try {
			String sql = "SELECT DataNascimento FROM Funcionarios WHERE ID=?;";
			PreparedStatement pst = conexao.prepareStatement(sql);
			
			pst.setInt(1, ID);
	        ResultSet res = pst.executeQuery();
	       
	        if (res.wasNull()) {
	        	return dataNascimento;
	        }
	        
	        if(res.next()) {
	        	dataNascimento = Calendar.getInstance();
	        	dataNascimento.setTime(res.getDate("DataNascimento"));       	        	
	        }
	        
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dataNascimento;
	}
	
	public String getCPF(int ID) {
		String cpf = null;
		try {
			String sql = "SELECT CPF FROM Funcionarios WHERE ID=?;";
			PreparedStatement pst = conexao.prepareStatement(sql);
			
			pst.setInt(1, ID);
	        ResultSet res = pst.executeQuery();
	       
	        if (res.wasNull()) {
	        	return cpf;
	        }
	        
	        if(res.next()) {
	        	cpf = res.getString("CPF");   	        	
	        }
	        
	        
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return cpf;
	}
}