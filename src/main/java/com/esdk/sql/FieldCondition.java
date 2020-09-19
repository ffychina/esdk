package com.esdk.sql;
public class FieldCondition extends Condition{

  public FieldCondition(Field field1,Field field2){
  	this(field1,true,field2);
  }

  public FieldCondition(Field field1,boolean isEqual,Field field2){
  	if(field1.toString().equals(field2.toString())||field1.getName().equals(field2.getName())) {
  		if(field1.getTable().equals(field2.getTable())) {
  			throw new SQLRuntimeException("cannot acceipt same table and same field");
  		}
  		field1.getTable().createAliasName();
  		field2.getTable().createAliasName();
  	}
    if(isEqual)
      _condition=field1.toString().concat(Where.EQ).concat(field2.toString());
    else
      _condition=field1.toString().concat(Where.NOTEQUAL).concat(field2.toString());
  }
}