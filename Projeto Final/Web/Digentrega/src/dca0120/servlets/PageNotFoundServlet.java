package dca0120.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PageNotFoundServlet extends HttpServlet {

	private static final long serialVersionUID = -999600673680107168L;
	
	 private String message;

	  public void init() throws ServletException
	  {
	      // Do required initialization
	      message = "Hello World";
	  }

	  public void doGet(HttpServletRequest request,
	                    HttpServletResponse response)
	            throws ServletException, IOException
	  {
	      // Set response content type
	      response.setContentType("text/html");

	      // Actual logic goes here.
	      PrintWriter out = response.getWriter();
	      out.println("<h1>" + message + "</h1>");
	  }
	  
	  public void destroy()
	  {
	      // do nothing.
	  }
	
	
}
