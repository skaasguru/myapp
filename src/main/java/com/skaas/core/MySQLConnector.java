package com.skaas.core;

import com.skaas.core.AppConfig;

import java.sql.*;

public class MySQLConnector {

	Connection connection;	
	Statement statement;
    
    
	public MySQLConnector() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connection  = DriverManager.getConnection(AppConfig.dbString,AppConfig.dbUsername,AppConfig.dbPassword);
		statement = connection.createStatement();
	}
	
	public ResultSet executeQuery(String query) throws SQLException {
		ResultSet resultset = statement.executeQuery(query);
		return resultset;		
	}
		
	public void close(ResultSet resultset) throws SQLException {
		resultset.close();
		statement.close();
		connection.close();
	}

	public boolean execute(String query) throws SQLException {
		return statement.execute(query);	
	}
	
	public void close() throws SQLException {
		statement.close();
		connection.close();
	}
}