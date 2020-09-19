package com.esdk.sql.datasource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.sql.DataSource;

import com.esdk.esdk;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.utils.EasySql;

public class FileConnectionPool{
	private static DataSource ds;
	private static Connection singleConn=null;
  private static LinkedHashMap<Thread,Connection> connectionMap=new LinkedHashMap();
  private static long lastCleanTime=System.currentTimeMillis();

  //配置文件的优先级:application.properties < project.properties < db.properties
	private static DataSource getDataSource() {
		if(ds==null) {
			InputStream is=esdk.file.getInputStreamFromResources("application.properties");
			if(is==null)
				is=esdk.file.getInputStreamFromResources("project.properties");
			if(is==null)
				is=esdk.file.getInputStreamFromResources("db.properties");
			ds=DataSourceBuilder.createDataSource(esdk.file.getProperties(is));
		}
		return ds;
	}
	
  public static Connection getConnection(){
  	DataSourceBuilder.printStatus(getDataSource());
  	if(System.currentTimeMillis()-lastCleanTime>60_000) {
  		int removedCount=0;
    	for(Iterator<Thread> iter=connectionMap.keySet().iterator();iter.hasNext();) {
    		Thread thread=iter.next();
    		if(thread.getState().equals(Thread.State.TERMINATED)||thread.getState().equals(Thread.State.WAITING)) {
    			esdk.sql.close(connectionMap.remove(thread));
    			removedCount++;
    		}
    	}
    	esdk.log.info("超时移除了{}个数据库连接,现在仍保持{}个连接",removedCount,connectionMap.size());
    	removedCount=0;
    	lastCleanTime=System.currentTimeMillis();
  	}
    try{
    	Connection conn=ds.getConnection();
    	connectionMap.put(Thread.currentThread(),conn);
			return conn;
		}catch(SQLException e){
			throw new SQLRuntimeException(e);
		}
  }

	public static Connection getSingletonConnection(){
		try{
			if(singleConn!=null&&esdk.sql.IsClosed(singleConn)){
				esdk.sql.close(singleConn);
				singleConn=null;
			}
			if(singleConn==null||singleConn.isClosed()){
				singleConn=getConnection();
			}
		}catch(Exception e){
			throw new SQLRuntimeException(e);
		}
		return singleConn;
	}
	
	public static void closeConnection(){
		esdk.sql.close(connectionMap.remove(Thread.currentThread()));
	}
	
  public static void main(String[] args){
    try{
    	Connection conn=FileConnectionPool.getConnection();
      System.out.println(conn.getMetaData().getDatabaseProductName());
      EasySql.close(conn);
    }
    catch(SQLException e){
      e.printStackTrace();
    }
  }
}

