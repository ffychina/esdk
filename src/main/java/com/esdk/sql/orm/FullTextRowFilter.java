package com.esdk.sql.orm;

import java.util.ArrayList;

import com.esdk.utils.EasyStr;
public class FullTextRowFilter implements IRowFilter{
	private String[] searchWord;
	private String[] fields;
	private boolean caseInsensitive=true;
	public FullTextRowFilter(String search){
		this.searchWord=search.split(" ");
	}

	public FullTextRowFilter(String search,boolean ignoreCase,String...fields){
		this(search);
		this.fields=fields;
		this.caseInsensitive=ignoreCase;
	}

	@Override public boolean filter(IRow row,IRowSet rowset,int index){
		String[] fieldNames=fields==null?row.getNames():fields;
		boolean result=false;
		ArrayList searchWords=EasyStr.ArrToList(searchWord);
		for(int i=0,n=fieldNames.length;i<n;i++){
			result=result||search(row.getString(fieldNames[i]),searchWords,caseInsensitive);
			if(result)
				break;
		}
		return result;
	}
	
	private boolean search(String value,ArrayList<String> searchWords,boolean ignoreCase) {
		if(value==null)
			return false;
		for(int i=0;i<searchWords.size();i++) {
			String word=searchWords.get(i);
			if(ignoreCase) {
				value=value.toUpperCase();
				word=word.toUpperCase();
			}
			if(value.indexOf(word)>=0) {
				searchWords.remove(i);
				i--;
			}
		}
		return searchWords.size()==0;
	}
}
