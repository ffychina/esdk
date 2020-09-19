package com.esdk.sql;

import com.esdk.esdk;

public class WrapSelect extends Select{
  public WrapSelect(ISelect select,String alias){
  	this.setTableName("("+select.getSQL().replaceAll("\\r?\\n"," ")+")");
  	this.getTable().setAliasName(alias);
  	this.setTop(0);
  	this.setConnection(select.getConnection());
  }

	public static void main(String[] args){
		SQLAssistant.getDialect().setDatabaseProductName(SqlDialect.DatabaseProductName_MySQL);
		Select s=new Select("dict");
		s.addEqualCondition("valid",true);
		s.setColumns("category","name","content","sequence");
		WrapSelect ns=new WrapSelect(s,"d1");
		ns.setColumns("category,name");
		ns.addOrderBy("name");
		esdk.tool.assertEquals(ns.getSQL(),"Select d1.category,d1.name\r\n" + 
				"From (Select category,name,content,sequence From dict Where valid = 1 Limit 50) d1\r\n" + 
				"Order By d1.name");
	}
}
