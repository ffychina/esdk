package com.esdk.sql.orm;

import java.io.Closeable;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import com.esdk.interfaces.IRequestClose;
import com.esdk.sql.Delete;
import com.esdk.sql.ISelect;
import com.esdk.sql.Insert;
import com.esdk.sql.Select;
import com.esdk.sql.SmartPersistence;
import com.esdk.sql.Update;
import com.esdk.sql.datasource.ConnectionPoolBuilder;
import com.esdk.sql.datasource.IConnectionPool;

public class ORMSessionBuilder implements Closeable,IRequestClose{
  private IConnectionPool pool;
  private ORMSession session;
  private File defaultPropertyFile;
  
  public ORMSessionBuilder(File file) throws SQLException {
    defaultPropertyFile=file;
    init();
    session=new ORMSession(pool.getConnection());  
  }
  
  public ORMSessionBuilder() throws SQLException {
    this(new File("./config/db.txt"));
  }
  
  public ORMSessionBuilder(IConnectionPool connpool) throws SQLException {
    pool=connpool;
    session=new ORMSession(pool.getConnection());  
  }
  
  public ORMSessionBuilder(Connection conn) throws SQLException {
    session=new ORMSession(conn);  
  }
  
  
  public void close() {
    session.close();
  }
  
  private void init() {
    try{
      if(pool==null)
        pool=ConnectionPoolBuilder.createPool(defaultPropertyFile);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  public Select createSelect(String tablename) throws SQLException {
    return new Select(tablename,session.getConnection());
  }
  
  public <T extends AbstractSelect> T createSelect(Class<T> selectClass) throws Exception{
    java.lang.reflect.Constructor constructor = selectClass.getConstructor(new Class[] {Connection.class});
    T result=(T)constructor.newInstance(new Object[] {session.getConnection()});
    result.setSession(session);
    return result;
  }
  
  public <T extends ParentRow> T createRow(Class<T> parentRowClass) throws Exception{
    T row=(T)parentRowClass.newInstance();
    row.setConnection(session.getConnection());
    row.setSession(session);
    return row;
  }
  
  public <T extends ParentRow> T createRow(Class<T> parentRowClass,Object pk) throws Exception{
    T row=(T)parentRowClass.newInstance();
    row.setConnection(session.getConnection());
    row.setSession(session);
    row.setPrimaryKey(pk);
    row.refresh();
    return row;
  }
  
  public Insert createInsert(String tablename) throws SQLException {
    return new Insert(tablename,session.getConnection());
  }
  
  public Update createUpdate(String tablename) throws SQLException {
    return new Update(tablename,session.getConnection());
  }
  
  public Delete createDelete(String tablename) throws SQLException {
    return new Delete(tablename,session.getConnection());
  }

  public SmartPersistence createSave(String tablename) throws SQLException {
    return new SmartPersistence(tablename,session.getConnection());
  }
  
}
