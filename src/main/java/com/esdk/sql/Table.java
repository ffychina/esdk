package com.esdk.sql;

import com.esdk.utils.EasyObj;


public class Table{
  public static final String INNERJOIN="JOIN",LEFTJOIN="LEFT JOIN",RIGHTJOIN="RIGHT JOIN",FULLJOIN="FULL JOIN";
  private String tableName;
  private String aliasName;
  private String relationShip;
  protected LogicTree onCondition=new LogicTree();
  
  public Table(String tablename) {
    this.tableName=tablename;
  }
  
  public Table(String tablename,String aliasname) {
    this.tableName=tablename;
    aliasName=aliasname;
  }
  
  public Table(String tablename,boolean isJoin) {
  	this.tableName=tablename;
    if(isJoin)
    	relationShip=INNERJOIN;
  }
  public Table(String tablename,String aliasname,boolean isJoin) {
    this.tableName=tablename;
    aliasName=aliasname;
    if(isJoin)
    	relationShip=INNERJOIN;
  }
  
  public Table(String tablename,String aliasname,String jointype) {
    this.tableName=tablename;
    aliasName=aliasname;
    relationShip=jointype;
  }
  
  public Field createField(String columnName) {
  	return new Field(this,columnName);
  }
  
  public String getAliasName(){
    if(aliasName!=null)
      return aliasName;
    return "";
  }
  
  public void setAliasName(String aliasname){
    this.aliasName=aliasname;
  }
  
  public String getTableName(){
    return this.tableName;
  }
  
  public void setTableName(String tablename){
    this.tableName=tablename;
  }

  public void createAliasName() {
		if (aliasName == null)
			aliasName = SQLAssistant.getAbbreviation(this.tableName);
  }
  @Override public String toString(){
  	String oncondition=onCondition.toString();
  	if (oncondition.length() > 0) {
			if (relationShip == null)
				relationShip = INNERJOIN;
			if(this.aliasName==null)
				createAliasName();
		}
    StringBuffer result=new StringBuffer();
    result.append(EasyObj.checkValid(oncondition,relationShip)?" ".concat(relationShip).concat(" "):"");
    result.append(getTableName()).append(getAliasName().length()==0?"":" ").append(getAliasName());
    String s=oncondition.length()>0?" on ".concat(oncondition):"";
    result.append(s);
    return result.toString();
  }

  public String getJoinType(){
    return relationShip;
  }

  public void setRelationShip(String jointype){
    this.relationShip=jointype;
  }

  public String getRelationShip(){
    return this.relationShip;
  }

  public void addOnCondition(ILogic value){
    this.onCondition.addCondition(value);
  }
}
