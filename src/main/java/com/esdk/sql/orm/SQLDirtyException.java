package com.esdk.sql.orm;

import java.sql.SQLException;

public class SQLDirtyException extends SQLException{
  private static final long serialVersionUID=252207941943864830L;

  public SQLDirtyException(){
    super();
  }

  public SQLDirtyException(String s){
    super(s);
  }

}
