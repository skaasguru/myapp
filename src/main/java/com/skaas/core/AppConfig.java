package com.skaas.core;

public class AppConfig {

	public static final String 
			bucket = (System.getenv("S3_BUCKET") != null) ? System.getenv("S3_BUCKET") : "myapp-bucket",
			region = (System.getenv("S3_REGION") != null) ? System.getenv("S3_REGION") : "us-west-2",
			dbString = (System.getenv("DB_STRING") != null) ? System.getenv("DB_STRING") : "jdbc:mysql://localhost:3306/myapp",
			dbUsername = (System.getenv("DB_USERNAME") != null) ? System.getenv("DB_USERNAME") : "root",
			dbPassword = (System.getenv("DB_PASSWORD") != null) ? System.getenv("DB_PASSWORD") : "password";
	
	

	
}

