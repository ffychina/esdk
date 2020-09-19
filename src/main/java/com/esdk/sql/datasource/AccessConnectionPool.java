package com.esdk.sql.datasource;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.esdk.esdk;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.utils.EasyObj;

public class AccessConnectionPool implements IConnectionPool{

  static DataSource dataSource=null;

  private final static String username="admin";
  private final static String password="tomedi88";
  private final static int maxPoolSize=2;
  private final static int minPoolSize=1;
  private final static File mdbfile=new File("./config/config.mdb");
  static{
    try{
      dataSource=setupDataSource();
      printStatus();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

	@Override
	public DataSource getDataSource(){
		return dataSource;
	}
	
  public static void printStatus(){
  	System.out.println(getStatus(dataSource));
  }
  
  private static String getStatus(DataSource ds){
  	String msg =null;
    if(ds instanceof DruidDataSource) {
    	DruidDataSource dataSource=(DruidDataSource)ds;
      msg = dataSource.getUrl()
          + " driver:"+dataSource.getDriverClassName()
          +", MinIdle:"+dataSource.getMinIdle()+", MaxActive:"
          +dataSource.getMaxActive()+", ActiveCount:"+dataSource.getActiveCount();
    }
    return msg;
  }
  
  public String toString() {
    return dataSource.toString();
  }
  
  private static DruidDataSource setupDataSource() throws PropertyVetoException, SQLException {
  	DruidDataSource ds=new DruidDataSource();
    ds.setDriverClassName("sun.jdbc.odbc.JdbcOdbcDriver");
    ds.setUrl("jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ="+mdbfile.getAbsolutePath());
    ds.setUsername(username);
    ds.setPassword(password);
    ds.setMaxActive(maxPoolSize);
    ds.setMinIdle(minPoolSize);
    ds.setInitialSize(1);
    return ds;
  }
  
  public Connection getConnection(){
    try{
      return dataSource.getConnection();
    }
    catch(SQLException e){
      e.printStackTrace();
      return null;
    }
  }
  
  public Connection createConnection() throws SQLException{
    return getConnection();
  }
  
  public void close() throws IOException{
    ((DruidDataSource)this.dataSource).close();//it is not necessarily 
  }
  
  public static void main(String[] args){
    try{
    	AccessConnectionPool pool=new AccessConnectionPool();
      Connection conn=pool.getConnection();
      Connection conn1=pool.getConnection();
      Statement st=conn.createStatement();
      Connection con=st.getConnection();
      con.setReadOnly(false);
      con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      System.out.println(!con.getAutoCommit());
      con.setAutoCommit(false);
      esdk.tool.assertEquals(!st.execute("insert into test (Code,Name,ID) values('0','0',0)"));
      esdk.tool.assertEquals(st.executeUpdate("update test set code='a' where id=0")==1);
      esdk.tool.assertEquals(!st.execute("insert into test (Code,Name,ID) values('d','d',3)"));
     // esdk.tool.assertEquals(new ABResultSet(st.executeQuery("select * from test")).toXml(),"<list><record><Code>a</Code><Name>b</Name><ID>1</ID></record><record><Code>c</Code><Name>d</Name><ID>2</ID></record><record><Code>a</Code><Name>0</Name><ID>0</ID></record><record><Code>d</Code><Name>d</Name><ID>3</ID></record></list>");
      esdk.tool.assertEquals(!st.execute("delete from test where id in(3,0)"));
      JDBCTemplate jt=new JDBCTemplate(conn1,"select * from test");
     // esdk.tool.assertEquals(jt.toABResultSet().toXml(),"<list><record><Code>a</Code><Name>b</Name><ID>1</ID></record><record><Code>c</Code><Name>d</Name><ID>2</ID></record></list>");
      con.commit();
      st.getConnection().close();
      st.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
