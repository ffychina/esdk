package com.esdk.utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.esdk.sql.ConnectionBuilder;
import com.esdk.sql.JDBCTemplate;

public class PostgresHelper{
	public static void main(String[] args){
		try{
			Connection conn=ConnectionBuilder.createConnection("org.postgresql.Driver","jdbc:postgresql://localhost:5432/lst","postgres","lstdb","lst");
			JDBCTemplate jt=new JDBCTemplate(conn,"select * from dict where dictid=? offset 0 limit 3",9);
			System.out.println(jt.toABResultSet().toCsv());
			//System.out.println(conn.getMetaData());
		}catch(SQLException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
}
