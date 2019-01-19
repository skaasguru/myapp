package com.skaas.core;

public class AppConfig {

	public static final String 
			fileLocation = (System.getenv("FILE_LOCATION") != null) ? System.getenv("FILE_LOCATION") : "/var/www/uploads",
			dbString = (System.getenv("DB_STRING") != null) ? System.getenv("DB_STRING") : "jdbc:mysql://localhost:3306/myapp",
			dbUsername = (System.getenv("DB_USERNAME") != null) ? System.getenv("DB_USERNAME") : "root",
			dbPassword = (System.getenv("DB_PASSWORD") != null) ? System.getenv("DB_PASSWORD") : "password";
	
}

