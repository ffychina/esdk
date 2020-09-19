package com.esdk.sql.orm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import com.esdk.sql.SQLRuntimeException;

public class ORMSessionFactory{
	private static DataSource _ds;
	synchronized public static void setDataSource(DataSource ds) {
		_ds=ds;
	}
	private static HashMap<Integer,ORMSession> _ormsessions=new HashMap<Integer,ORMSession>();
	synchronized public static ORMSession getORMSession(Connection conn){
		ORMSession instance=new ORMSession(conn);
		return instance;
	}
	
	synchronized static public ORMSession addORMSession(ORMSession value){
		if(_ormsessions.containsKey(value.conn.toString().hashCode())) {
			throw new SQLRuntimeException("Connection is existed in other ormsession");
		}
			_ormsessions.put(value.hashCode(),value);
		return value;
	}
	
	synchronized public static ORMSession getORMSession() throws SQLException{
		if(_ds==null)
			throw new SQLRuntimeException("DataSource is null, please setDataSource first");
		else{
			Connection conn=_ds.getConnection();
			return getORMSession(conn);
		}
	}
	
	synchronized public static void closeORMSession(ORMSession value) {
		removeORMSession(value);
		value.close();
	}

	synchronized public static void removeORMSession(ORMSession value){
		value.clear();
		_ormsessions.remove(value.hashCode());
	}

	public static void cleanup(){
		_ormsessions.clear();
	}

}
