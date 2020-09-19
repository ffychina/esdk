package com.esdk.sql.datasource;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.sql.DataSource;

import com.esdk.esdk;
import com.esdk.sql.SQLRuntimeException;

public class ConnectionPoolBuilder{
	
  public static IConnectionPool createPool(final File configfile) throws IOException, PropertyVetoException, SQLException{
    return createPool(DataSourceBuilder.getProperty(configfile));
  }
  
	public static IConnectionPool createPool(String propertiesFileName){
		Properties prop=new Properties();
		try{
			prop.load(esdk.file.getInputStream(propertiesFileName));
			return createPool(prop);
		}catch(Exception e){
			throw new SQLRuntimeException(e);
		}
	}
	
  public static IConnectionPool createPool(Properties prop) throws IOException, PropertyVetoException, SQLException{
    return new IConnectionPool() {
      private DataSource ds=DataSourceBuilder.createDataSource(prop);
      private LinkedHashMap<Thread,Connection> connectionMap=new LinkedHashMap();
      private long lastCleanTime=System.currentTimeMillis();
      
      public Connection getConnection(){
      	DataSourceBuilder.printStatus(ds);
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
			@Override
			public DataSource getDataSource(){
				return ds;
			}
			@Override
			public void close() throws IOException{
				esdk.sql.close(connectionMap.remove(Thread.currentThread()));
			}
    };
  }
}
