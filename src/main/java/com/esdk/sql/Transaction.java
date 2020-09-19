package com.esdk.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

import com.esdk.esdk;
import com.esdk.sql.orm.ParentRow;

public class Transaction{
  private Connection conn;
  private LinkedList sqlList=new LinkedList();
 
  public Transaction(Connection connection) {
    conn=connection;
  }
  
  public void addSQL(ParentRow row) {
    addSQL((IPersistence)esdk.reflect.getDeclaredFieldValue(row,"ISQL"));
  }
  
  public void addSQL(ISQL iSQL) {
    if(!sqlList.contains(iSQL))
      sqlList.add(iSQL);
    else
      System.err.println(iSQL);
  }
  
  public void addBatchSQL(ISQL[] iSQLArray) {
    for(int i=0;i<iSQLArray.length;i++){
      sqlList.add(iSQLArray[i]);
    }
  }
 
  public boolean commit(){
    int errCount=0;
    try{
      boolean oldstate=conn.getAutoCommit();
      for(int i=0;i<sqlList.size();i++){
        ((ISQL)sqlList.get(i)).parse();
        ((ISQL)sqlList.get(i)).setConnection(conn);
      }
      conn.setAutoCommit(false);
      for(int i=0;i<sqlList.size();i++){
        if(!((ISQL)sqlList.get(i)).perform())
        	errCount++;
      }
      conn.commit();
      conn.setAutoCommit(oldstate);
    }
    catch(SQLException e){
      e.printStackTrace();
      errCount++;
      conn.rollback();
    }
    catch(Exception e){
      e.printStackTrace();
      errCount++;
      conn.rollback();
    }
    finally {
      return errCount==0;
    }
  }
}
