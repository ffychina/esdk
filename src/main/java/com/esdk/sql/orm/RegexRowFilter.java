package com.esdk.sql.orm;

import java.util.regex.Pattern;

public class RegexRowFilter implements IRowFilter{
	private Pattern pattern;
	private String[] fields;
	public RegexRowFilter(String regex){
		this.pattern=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
	}
	public RegexRowFilter(String regex,boolean ignoreCase,String... fields){
		this.pattern=Pattern.compile(regex,ignoreCase?Pattern.CASE_INSENSITIVE|Pattern.DOTALL:Pattern.DOTALL);
		this.fields=fields;
	}
	@Override public boolean filter(IRow row,IRowSet rowset,int index){
		String[] fieldNames=fields==null?row.getNames():fields;
		boolean result=false;
		for(int i=0,n=fieldNames.length;i<n;i++){
			result=result||pattern.matcher(row.getString(fieldNames[i])).find();
			if(result)
				break;
		}
		return result;
	}
}
