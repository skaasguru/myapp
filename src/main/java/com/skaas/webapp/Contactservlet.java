package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.skaas.core.MySQLConnector;

/**
 * Handles Operations related to "My Contact"
 */
public class Contactservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Contactservlet() {
        super();
    }

	/**
	 * Deletes a contact by getting the query param "delete" from the request
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session=request.getSession(false);  
		if(session != null && session.getAttribute("id") != null){
			String user_id = (String) session.getAttribute("id");
			
			if (request.getParameter("delete") != null) {
				
				String query = "DELETE FROM contacts WHERE `user_id`=" + user_id + " AND `id`=" + request.getParameter("delete") + ";";

				try {
					MySQLConnector mysql = new MySQLConnector();
					mysql.execute(query);
					mysql.close();
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}

				response.sendRedirect("contacts.jsp");	
			} else {
				out.println("The parameter 'delete' is not found in the request");
			}
		} else {
			out.println("You're not logged in");
		}
	}

	/**
	 * Saves a contact to the DB
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		HttpSession session=request.getSession(false);
		
		if(session != null && session.getAttribute("id") != null){  
			String user_id = (String) session.getAttribute("id");
			
			if (request.getParameter("name") != null && request.getParameter("phone") != null) {
				String query = "INSERT INTO contacts (`user_id`, `name`, `phone`) VALUES ('"+user_id+"', '"+ request.getParameter("name") +"', '"+request.getParameter("phone")+"');";
				try {
					MySQLConnector mysql = new MySQLConnector();
					mysql.execute(query);
					mysql.close();
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				
		        response.sendRedirect("contacts.jsp");
			} else {
				out.println("The parameters 'name' and/or 'phone' is not found in the request");
			}
		} else {
			out.println("You're not logged in");
		}
		
	}
	
}
