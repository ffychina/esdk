package com.esdk.utils;

import com.esdk.esdk;

abstract public class Parser{
  private CharAppender _err=new CharAppender(),warning=new CharAppender();
  
  protected void appendErr(String err){
    _err.append(err);
    System.err.println(err);
  }
  
  
  protected void appendErr(Exception e){
  	appendErr(esdk.tool.getExceptionStackTrace(e));
  }
  
  protected void appendWarning(String warn){
      warning.append(warn);
  }
  
  abstract public void setSource(Object source);
  
  abstract public void parse();
  
  abstract public String toString();

  abstract public Object getResult();
  
  public void clearErr(){
    _err=new CharAppender();
  }
  
  public boolean isSuccess() {
    return _err.length()==0;
  }
  
  public String getErr(){
    return _err.toString();
  }
  
  public String getWarning() {
    return warning.toString();
  }
}
