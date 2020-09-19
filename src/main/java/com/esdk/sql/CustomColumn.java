package com.esdk.sql;

import com.esdk.utils.EasyObj;

public class CustomColumn extends Column{
	private Object content;
	private String aliasName;
	public CustomColumn(Object content,String aliasName){
		if(content!=null&&EasyObj.isValid(aliasName)) {
			this.content=content;
			this.aliasName=aliasName;
		}
		else
			throw new RuntimeException("params is invalid, content:"+content+",aliasName:"+aliasName);
	}

  public String toString() {
    return SQLAssistant.getStmtSqlValue(content).concat(" as ").concat(aliasName);
  }
}
