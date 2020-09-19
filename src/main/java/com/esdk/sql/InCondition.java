package com.esdk.sql;
public class InCondition extends Condition{
	public static final String IN=Where.IN,NOTIN=Where.NOTIN;
  private Field field;
  private ISelect select;
  private String expression;

  public InCondition(Field field,ISelect select){
  	this.field=field;
  	this.select=select;
  	expression=IN;
  }
  
  public InCondition(Field field,String express,ISelect select){
  	this.field=field;
  	this.select=select;
  	expression=express;
  }
  
  public InCondition(Field field,boolean isNotIn,ISelect select){
  	this.field=field;
  	this.select=select;
  	expression=isNotIn?NOTIN:IN;
  }
  
  public String toString() {
    return new StringBuffer(field.toString()).append(' ').append(expression).append('(').append(select.getSQL()).append(')').toString();
  }
  
}