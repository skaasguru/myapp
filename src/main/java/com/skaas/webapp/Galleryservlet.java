package com.skaas.webapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.HttpMethod;


import com.skaas.core.AppConfig;

/**
 * Servlet implementation class Galleryservlet
 */

@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class Galleryservlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String bucket = AppConfig.bucket;
	private static final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(AppConfig.region).build();
			
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Galleryservlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		if(session != null && session.getAttribute("id") != null){  
	        String objectName = URLDecoder.decode(request.getPathInfo(), "UTF-8").substring(1);
	        
			if (request.getParameterMap().containsKey("delete")) {
				s3.deleteObject(new DeleteObjectRequest(bucket, objectName));
				response.sendRedirect(request.getContextPath()+"/gallery.jsp");
	    	} else {
	    		

				Date expiration = new Date();
	            expiration.setTime(expiration.getTime() + (1000 * 60 * 60));
	            
	    		String url = s3.generatePresignedUrl(
            			new GeneratePresignedUrlRequest(bucket, objectName)
		                    .withMethod(HttpMethod.GET)
		                    .withExpiration(expiration)
        			).toString();
				response.sendRedirect(url);
	    	}	    	
		} else {
			PrintWriter out = response.getWriter();
			out.println("You're not logged in");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);  
		if(session != null && session.getAttribute("id") != null){  
	    	String user_id = (String)session.getAttribute("id");
	        String fileName = URLDecoder.decode(request.getPathInfo(), "UTF-8").substring(1);

	        String objectName = "images/"+user_id+"/"+fileName;

			Date expiration = new Date();
            expiration.setTime(expiration.getTime() + (1000 * 60 * 60));
            
    		String url = s3.generatePresignedUrl(
        			new GeneratePresignedUrlRequest(bucket, objectName)
	                    .withMethod(HttpMethod.PUT)
	                    .withContentType(request.getParameter("type"))
	                    .withExpiration(expiration)
    			).toString();

			PrintWriter out = response.getWriter();
	        out.print(url);
		} else {
			PrintWriter out = response.getWriter();
			out.println("You're not logged in");
		}
	}
}
