package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.skaas.core.AppConfig;
import com.skaas.core.MySQLConnector;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;

/**
 * Servlet implementation class Signupservlet
 */
public class Signupservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String bucket = AppConfig.bucket;
	private static final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(AppConfig.region).build();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signupservlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		System.out.println((session != null) + "--" + (session.getAttribute("id") != null));
		if(session != null && session.getAttribute("id") != null){  
	    	String user_id = (String)session.getAttribute("id");
	    	
	    	String query1 = "DELETE FROM contacts WHERE `user_id`=" + user_id + ";";
	    	String query2 = "DELETE FROM users WHERE `id`=" + user_id + ";";
			try {
				MySQLConnector mysql = new MySQLConnector();
				mysql.execute(query1);
				mysql.execute(query2);
				mysql.close();
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}


			ListObjectsV2Result	result = s3.listObjectsV2(
					(new ListObjectsV2Request()).withBucketName(bucket).withPrefix("images/"+user_id+"/")
			);
			
			ArrayList<KeyVersion> keys = new ArrayList<KeyVersion>();
			for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
				keys.add(new KeyVersion(objectSummary.getKey()));
			}
			
			if (keys.size() > 0) {				
				s3.deleteObjects((new DeleteObjectsRequest(bucket)).withKeys(keys));
			}

		    session.invalidate();
			response.sendRedirect("index.jsp");
		} else {
			PrintWriter out = response.getWriter();
			out.println("You're not logged in");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String  name = request.getParameter("name"),
				email = request.getParameter("email"),
				password = request.getParameter("password");
		
		if (name != null && email != null && password != null) {
			String query = "INSERT INTO users (`name`, `email`, `password`) VALUES ('" + name + "', '" + email + "', '" + password + "');";
			try {
				MySQLConnector mysql = new MySQLConnector();
				mysql.execute(query);
				mysql.close();
				response.sendRedirect("login.jsp");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
				out.println("User may already exist. Please try again with different email ID");
			}
		} else {
			out.println("The parameters 'name', 'email' and/or 'password' is not found in the request");
		}
	}

}
