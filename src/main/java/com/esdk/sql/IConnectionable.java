/**
 * can invoke setConnection(),access database,as if tomdbserver or testsystem.
 */
package com.esdk.sql;

import java.sql.Connection;

public interface IConnectionable{
  public void setConnection(Connection con); 
}
