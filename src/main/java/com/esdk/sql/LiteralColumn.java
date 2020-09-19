package com.esdk.sql;

import com.esdk.utils.EasyObj;

public class LiteralColumn extends Column{
	private String content;
	private String aliasName;
	public LiteralColumn(String content,String aliasName){
		if(EasyObj.isValid(content)&&EasyObj.isValid(aliasName)) {
			this.content=content;
			this.aliasName=aliasName;
		}
		else
			throw new RuntimeException("params is invalid, content:"+content+",aliasName:"+aliasName);
	}

  public String toString() {
    return content.concat(" as ").concat(aliasName);
  }
}
