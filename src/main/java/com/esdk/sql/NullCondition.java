package com.esdk.sql;
public class NullCondition extends Condition{
	private Field field;
	private boolean isnull;
  public NullCondition(Field field){
  	this(field,true);
  }

  public NullCondition(Field field,boolean isnull){
  	this.isnull=isnull;
  	this.field=field;
  }
  
  @Override public String toString(){
  	if(isnull)
      _condition=field.toString().concat(" ").concat(Where.IS).concat(" ").concat(Where.NULL);
    else
      _condition=field.toString().concat(" ").concat(Where.ISNOT).concat(" ").concat(Where.NULL);
  	return _condition;
  }
  
}