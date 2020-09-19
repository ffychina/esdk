package com.esdk.sql;
public class Condition implements ILogic{
  String _condition="";
  boolean isAnd=true;

  public Condition(){}

  public Condition(String condtion){
    _condition=condtion;
  }

  public void setAnd(boolean isAndLogical){
    isAnd=isAndLogical;
  }

  public boolean isAnd(){
    return isAnd;
  }

  public ILogic setAnd(){
    this.isAnd=true;
    return this;
  }

  public ILogic setOr(){
    this.isAnd=false;
    return this;
  }
  
  @Override public String toString() {
    return _condition;
  }

  public Object[] getParameters(){
    return null;
  }

  public String getPstmtSql(){
    return getStmtSql();
  }

  public String getStmtSql(){
    return toString();
  }
  
  public static Condition getFalse() {
  	return new Condition("1=2");
  }
  
  public static Condition getTrue() {
  	return new Condition("1=1");
  }
  
  @Override public int hashCode() {
  	return toString().hashCode();
  }
  
  @Override public boolean equals(Object obj) {
  	if(obj==this)
  		return true;
  	if(obj==null)
  		return false;
  	if(this.getClass().equals(obj.getClass()))
  		return toString().equals(obj.toString());
  	return false;
  }
}