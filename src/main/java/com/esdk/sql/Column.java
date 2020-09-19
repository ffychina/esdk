package com.esdk.sql;


public class Column{
  public static final String MAX="MAX",Min="MIN",COUNT="COUNT",SUM="SUM",AVG="avg",Asterisk="*";
  private String functionName;
  public String aliasName;
  private Field field;
  boolean isExport;


  public Column(){isExport=true;}
  
  public Column(String fieldname){
    isExport=true;
    field=new Field(fieldname);
  }
  
  public Column(Field field0){
    isExport=true;
    field=field0;
  }
  
  public Column(Field field0,String functionname){
    isExport=true;
    field=field0;
    setFunctionName(functionname);
  }
  
  public Column(Field field0,String functionname,String aliasname){
    isExport=true;
    field=field0;
    setFunctionName(functionname);
    if(aliasname!=null)
      setAliasName(aliasname);
  }  
  
  public Column(String fieldname,String aliasname){
    isExport=true;
    field=new Field(fieldname);
    if(aliasname!=null&&!aliasname.equals(fieldname))
    	setAliasName(aliasname);
  }
  
  public Column(String tableAliasName,String fieldname,String functionname){
    isExport=true;
    field=new Field(tableAliasName,fieldname);
    setFunctionName(functionname);
  }
  
  public Column(String tableAliasName,String fieldname,String functionname,String aliasname){
    isExport=true;
    field=new Field(tableAliasName,fieldname);
    setFunctionName(functionname);
    if(aliasname!=null)
      setAliasName(aliasname);
  }
  
  public boolean isExport(){
    return this.isExport;
  }

  public Column setExport(boolean isexport){
    this.isExport=isexport;
    return this;
  }

  public String getAliasName(){
  	return aliasName;
  }

  public String getLabel() {
  	if(aliasName==null)
  		return field.getName();
  	else
  		return aliasName;
  }
  private String _getAliasName() {
		return aliasName==null?"":" as "+aliasName;
  }

  public Column setAliasName(String aliasName){
    this.aliasName=aliasName;
    return this;
  }

  public String getFullName(){
    if(functionName!=null)
      return functionName.concat("(").concat(field.toString()).concat(")");
    return field.toString(); 
  }

  public Column setFunctionName(String functionname){
    if(functionname!=null&&functionname.trim().length()>0) {
    	this.functionName=functionname.toLowerCase();
  		if(field.getName().indexOf("_")>=0)
  			aliasName=functionName.toLowerCase()+"_"+field.getName();
  		else
  			aliasName=functionname+field.getName();
    }
    return this;
  }

  public Field getField() {
  	return this.field;
  }
  public String toString() {
    StringBuffer result=new StringBuffer();
    result.append(getFullName()).append(_getAliasName());
    return result.toString();
  }
  public static Column createSum(String fieldName) {
  	return new Column("",fieldName,SUM);
  }
  public static Column createSum(Field field) {
  	return new Column(field,SUM);
  }
  public static Column createCount(String aliasName) {
  	return new Column("","*",COUNT,aliasName);
  }
  public static Column createCount() {
  	return new Column("","*",COUNT,"total");
  }
  public static Column createCount(Field field,String aliasName) {
  	return new Column(field,COUNT,aliasName);
  }
}

