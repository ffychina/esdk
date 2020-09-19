
package com.esdk.sql;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.esdk.esdk;

public class ConnectionBuilder{
	public final static String DriverClass="driverClass",Url="url",User="user",Username="username",Password="password";

	public static Connection createConnection(String driverclass,String url,String user,String password)
			throws SQLException,ClassNotFoundException{
		return createConnection(driverclass,url,user,password,null);
	}

	public static Connection createConnection(String driverclass,String url,String user,String password,String dbname)
			throws SQLException,ClassNotFoundException{// ms jdbc has g bug:
																									// [Microsoft][SQLServer 2000
																									// Driver for JDBC]ResultSet
																									// can not re-read row data
																									// for column 3.
		Class.forName(driverclass);
		/*
		 * DriverManager.registerDriver(new
		 * com.microsoft.jdbc.sqlserver.SQLServerDriver());//对于mysql不是必要步骤,
		 * 但对于oracle是必要的,而mssql默认已经注册
		 */
		java.util.Properties prop=new java.util.Properties();
		prop.setProperty("user",user);
		prop.setProperty("password",password);
		if(dbname!=null)
			prop.setProperty("DatabaseName",dbname);
		prop.setProperty("SelectMethod","direct");
		Connection connection=DriverManager.getConnection(url,prop);
		System.out.println(connection.getMetaData().getURL());
		return connection;
	}

	public static Connection createConnection(File file) throws SQLException,ClassNotFoundException,FileNotFoundException,IOException{
		Properties prop=new Properties();
		prop.load(new FileInputStream(file));
		return createConnection(prop);
	}

	public static Connection createConnection(String propFileName) throws Exception{
		return createConnection(esdk.file.getInputStream(propFileName));
	}

	public static Connection createConnection(InputStream is) throws Exception{
		Properties prop=new Properties();
		prop.load(is);
		is.close();
		return createConnection(prop);
	}

	public static Connection createConnection(Properties prop) throws ClassNotFoundException,SQLException{
		if(prop.containsKey("spring.datasource.url")){
			prop.put(Url,prop.get("spring.datasource.url"));
			prop.put(DriverClass,prop.get("spring.datasource.driver-class-name"));
			prop.put(User,prop.get("spring.datasource.username"));
			prop.put(Password,prop.get("spring.datasource.password"));
		}else if(prop.containsKey("jdbc.url")){
			prop.put(Url,prop.get("jdbc.url"));
			prop.put(DriverClass,prop.get("jdbc.driverClass"));
			prop.put(User,prop.get("jdbc.username"));
			prop.put(Password,prop.get("jdbc.password"));
		}else{
			prop.put(User,prop.get("username"));
		}
		Class.forName(prop.getProperty(DriverClass));
		prop.setProperty(User,prop.getProperty(User));
		prop.setProperty("SelectMethod","direct");
		Connection connection=DriverManager.getConnection(prop.getProperty(Url),prop);
		System.out.println(connection.getMetaData().getURL());
		return connection;
	}

}
