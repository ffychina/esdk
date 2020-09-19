package com.esdk.sql.orm;

public class ForeignKey{
	private String pkTableCat,pkTableSchem,pkTableName,pkColumnName,fkTableCat,fkTableSchem,fkTableName,fkColumnName,fkName,pkName;
	private int keySeq,updateRule,deleteRule,deferrability;

	public String getPkTableCat(){
		return pkTableCat;
	}

	public void setPkTableCat(String pkTableCat){
		this.pkTableCat=pkTableCat;
	}

	public String getPkTableSchem(){
		return pkTableSchem;
	}

	public void setPkTableSchem(String pkTableSchem){
		this.pkTableSchem=pkTableSchem;
	}

	public String getPkTableName(){
		return pkTableName;
	}

	public void setPkTableName(String pkTableName){
		this.pkTableName=pkTableName;
	}

	public String getPkColumnName(){
		return pkColumnName;
	}

	public void setPkColumnName(String pkColumnName){
		this.pkColumnName=pkColumnName;
	}

	public String getFkTableCat(){
		return fkTableCat;
	}

	public void setFkTableCat(String fkTableCat){
		this.fkTableCat=fkTableCat;
	}

	public String getFkTableSchem(){
		return fkTableSchem;
	}

	public void setFkTableSchem(String fkTableSchem){
		this.fkTableSchem=fkTableSchem;
	}

	public String getFkTableName(){
		return fkTableName;
	}

	public void setFkTableName(String fkTableName){
		this.fkTableName=fkTableName;
	}

	public String getFkColumnName(){
		return fkColumnName;
	}

	public void setFkColumnName(String fkColumnName){
		this.fkColumnName=fkColumnName;
	}

	public int getKeySeq(){
		return keySeq;
	}

	public void setKeySeq(int keySeq){
		this.keySeq=keySeq;
	}

	public int getUpdateRule(){
		return updateRule;
	}

	public void setUpdateRule(int updateRule){
		this.updateRule=updateRule;
	}

	public int getDeleteRule(){
		return deleteRule;
	}

	public void setDeleteRule(int deleteRule){
		this.deleteRule=deleteRule;
	}

	public String getFkName(){
		return fkName;
	}

	public void setFkName(String fkName){
		this.fkName=fkName;
	}

	public String getPkName(){
		return pkName;
	}

	public void setPkName(String pkName){
		this.pkName=pkName;
	}

	public int getDeferrability(){
		return deferrability;
	}

	public void setDeferrability(int deferrability){
		this.deferrability=deferrability;
	}}
