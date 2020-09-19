package com.esdk.sql;

public class Field extends Prefix{
  private String fieldName;
  private Table table;
  public Field(String fieldname) {
    super(null);
    setFieldName(fieldname);
  }
  public Field(Table table,String fieldName) {
  	this.table=table;
  	setFieldName(fieldName);
  }
  public Field(String prefix0,String fieldname) {
    super(prefix0);
    setFieldName(fieldname);
  }
  public String getName(){
    return this.fieldName;
  }
  public void setFieldName(String fieldname){
    this.fieldName=fieldname;
  }
  
  public Table getTable() {
  	return this.table;
  }
  
  @Override public String toString() {
  	if(table!=null)
  		setPrefix(table.getAliasName());
    return getPrefix().concat(fieldName);
  }
}
