package com.esdk.sql.datasource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.esdk.esdk;

public class DataSourceBuilder{
  public final static String datasourceClass="dataSourceClass",DriverClass="driverClass",Url="url",User="user",Password="password"
  		,MaxActive="maxActive",MinIdle="minIdle",InitialSize="initialSize";

  public static DataSource createDataSource(File configfile) throws Exception{
    return createDataSource(getProperty(configfile));
  }
  
  public static DataSource createDataSource(String propFileName) throws Exception{
  	return createDataSource(esdk.file.getInputStream(propFileName));
  }
  
  public static DataSource createDataSource(InputStream is) throws Exception{
    Properties prop=new Properties();
    prop.load(is);
    is.close();
    return createDataSource(prop);
  }
  
  public static DataSource createDataSource(Properties prop){
  	DataSource dataSource=null;
    dataSource=setupDataSource(prop);
    printStatus(dataSource);
    return dataSource;
  }
  
  static Properties getProperty(File configfile)throws IOException{
    Properties result=new Properties();
    FileInputStream fis=new FileInputStream(configfile);
    result.load(fis);
    fis.close();
    if(result.keySet().contains("driverClass"))
    	result.put("DriverClassName",result.get(DriverClass));
    return result;
  }
  
  static DataSource setupDataSource(Properties prop) {
  	if(prop.containsKey("spring.datasource.url")) {
  		prop.put(Url,prop.get("spring.datasource.url"));
  		prop.put(DriverClass,prop.get("spring.datasource.driver-class-name"));
  		prop.put(User,prop.get("spring.datasource.username"));
  		prop.put(Password,prop.get("spring.datasource.password"));
  	}else if(prop.containsKey("jdbc.url")) {
  		prop.put(Url,prop.get("jdbc.url"));
  		prop.put(DriverClass,prop.get("jdbc.driverClass"));
  		prop.put(User,prop.get("jdbc.username"));
  		prop.put(Password,prop.get("jdbc.password"));
  	}else {
  		prop.put(User,prop.get("username"));
  	}
  	DruidDataSource ds=prop.containsKey(datasourceClass)?(DruidDataSource)esdk.reflect.safeNewInstance(prop.getProperty(datasourceClass)):new DruidDataSource();
    ds.setDriverClassName(prop.getProperty(DriverClass));
    ds.setUrl(prop.getProperty(Url));
    ds.setUsername(prop.getProperty(User));
    ds.setPassword(prop.getProperty(Password));
    ds.setMaxActive(Integer.valueOf(prop.getProperty(MaxActive,"10")));
    ds.setMinIdle(Integer.valueOf(prop.getProperty(MinIdle,"1")));
    ds.setInitialSize(Integer.valueOf(prop.getProperty(InitialSize,"1")));
    return ds;
  }
  
  public static String getStatus(DataSource ds){
  	String msg =null;
    if(ds instanceof DruidDataSource) {
    	DruidDataSource dataSource=(DruidDataSource)ds;
      msg = dataSource.getUrl()
          + ", driver:"+dataSource.getDriverClassName()
          +", MinIdle:"+dataSource.getMinIdle()+", MaxActive:"
          +dataSource.getMaxActive()+", ActiveCount:"+dataSource.getActiveCount();
    }
    return msg;
  }
  
  public static void printStatus(DataSource ds){
  	System.out.println(getStatus(ds));
  }
  
  public static void main(String[] args) throws Exception{
  	DataSource ds=new DataSourceBuilder().createDataSource("db.properties");
  	Connection conn=ds.getConnection();
  	conn.setAutoCommit(false);
  	printStatus(ds);
	}
}
