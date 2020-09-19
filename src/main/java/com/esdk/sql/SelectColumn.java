package com.esdk.sql;

public class SelectColumn extends Column{
	private ISQL _sql;
	public SelectColumn(ISQL s,String aliasName){
		this._sql=s;
		this.setAliasName(aliasName);
	}
	@Override public String getAliasName(){
		return this.aliasName;
	}
	
	@Override public String toString(){
		return "("+this._sql.getSQL()+") as "+getAliasName();
	}
}
