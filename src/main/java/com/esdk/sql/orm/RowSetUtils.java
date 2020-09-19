package com.esdk.sql.orm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esdk.esdk;

public class RowSetUtils{
	public static ABRowSet filter(IRowSet rowSet,IRowFilter filter) {
  	return filter(rowSet,filter,Integer.MAX_VALUE);
  }
	
	public static ABRowSet filter(IRowSet rowSet,IRowFilter filter,int top) {
  	ArrayList result=new ArrayList();
  	if(filter instanceof ITopable)
  		top=((ITopable)filter).getTop()==0?Integer.MAX_VALUE:((ITopable)filter).getTop();
  	for(int i=0;result.size()<top&&i<rowSet.size();i++) {
  		IRow row=rowSet.getRow(i);
  		boolean pass=filter.filter(row,rowSet,i);
  		if(pass)
  			result.add(row);
  	}
  	return new ABRowSet(rowSet,result);
  }
	
	public static ABRowSet sortById(ParentResultSet prs,Integer[] ids) {
		ABRowSet result=new ABRowSet(prs.getColumnNames());
		for(int i=0;i<ids.length;i++) {
			result.add(prs.findById(ids[i]));
		}
		return result;
	}
	
	static IRowSet distinct(IRowSet rowSet,String...fieldNames){
		if(fieldNames.length==0){
			return rowSet;
		}else{
			HashSet set=new HashSet(rowSet.size());
			for(int i=0;i<rowSet.size();i++){
				IRow row=(IRow)rowSet.getRow(i);
				StringBuilder key=new StringBuilder();
				for(int j=0;j<fieldNames.length;j++){
					key.append((row.get(fieldNames[j]))+"+");
				}
				if(set.contains(key.toString())){
					rowSet.remove(i);//注意不能使用remove(Collection)
					i--;
				}else
					set.add(key.toString());
			}
		}
		return rowSet;
	}
	
	/**把重复的记录删除。注意，保留第一条未重复的记录，删除的第二条及之后的重复记录，返回的是已删除的记录集，TODO：用removeRedundant还是removeRepeat好呢？*/
	static IRowSet removeRepeat(IRowSet rowSet,String...fieldNames){
		ABRowSet result=new ABRowSet(rowSet.getColumnNames());
		if(fieldNames.length==0){
			return result;
		}else{
			HashSet set=new HashSet(rowSet.size());
			for(int i=0;i<rowSet.size();i++){
				IRow row=(IRow)rowSet.getRow(i);
				StringBuilder key=new StringBuilder();
				for(int j=0;j<fieldNames.length;j++){
					key.append((row.get(fieldNames[j]))+"+");
				}
				if(set.contains(key.toString())){
					result.add(row);
					rowSet.remove(i);//注意不能使用remove(Collection)
					i--;
				}else
					set.add(key.toString());
			}
		}
		return result;
	}
	
	static List group(IRowSet rowSet,String... fieldNames) {
		ArrayList result=new ArrayList<IRowSet>();
		if(fieldNames.length==0){
			result.add(rowSet);
			return result;
		}
		else{
	  	ABRowSet rs=new ABRowSet(rowSet.getRows());
	  	while(rs.first()){
	  		IRow firstRow=rs.getRow(0);
	  		RowFilter filter=new RowFilter();
	  		for(int i=0,n=fieldNames.length;i<n;i++){
	  			filter.add(fieldNames[i],firstRow.get(fieldNames[i])); //注意null和""会分为两个组。
	  		}
	  		ABRowSet sub=rs.filter(filter);
	  		result.add(sub);
	  		for(Iterator<IRow> iter=sub.iterator();iter.hasNext();){
	  			IRow removeRow=iter.next();
		  		rs.remove(removeRow);
	  		}
	  	}
	  	return result;
		}
  }
	
	public static List<ABRowSet> split(IRowSet rowSet,int maxSize) {
		ArrayList<ABRowSet> result=new ArrayList();
		ABRowSet abrs=new ABRowSet(rowSet.getColumnNames());
		for(int i=0,n=rowSet.size();i<n;i++) {
			IRow row=rowSet.getRow(i);
			abrs.add(row);
			if((i+1)%maxSize==0||i==n-1){
				result.add(abrs);
				abrs=new ABRowSet(rowSet.getColumnNames());
			}
		}
		return result;
  }
	
	public static RowFilter createRowFilter(String key,Object value){
		return RowFilter.create(key,value);
	}
	public static RowFilter createRowFilter(String key,String expr,Object value){
		return RowFilter.create(key,expr,value);
	}
	public static RegexRowFilter createRegexRowFilter(String regex){
		return new RegexRowFilter(regex);
	}
	public static RegexRowFilter createRegexRowFilter(String regex,boolean caseInsensitive,String... fields){
		return new RegexRowFilter(regex,caseInsensitive,fields);
	}
	//全文搜索过滤器
	public static FullTextRowFilter createFullTextRowFilter(String search){
		return new FullTextRowFilter(search);
	}
	public static FullTextRowFilter createFullTextRowFilter(String search,boolean ignoreCase,String... fields){
		return new FullTextRowFilter(search,ignoreCase,fields);
	}
	
	public static ABRowSet formatBoolean(IRowSet rs,String trueFormat,String falseFormat,String... fields){
		ArrayList result=new ArrayList();
		for(int i=0;i<rs.size();i++) {
  		IRow row=rs.getRow(i);
  		for(int j=0;j<fields.length;j++) {
  			if(((Boolean)row.get(fields[j]))) {
  				row.set(fields[j],trueFormat);
  			}else {
  				row.set(fields[j],falseFormat);
  			}
  		}
  		result.add(row);
		}
		return new ABRowSet(result);
	}
	
	/**删除空格，一般用于excel导入*/
	public static ABRowSet trim(ABRowSet rs) {
		for(Iterator iter=rs.iterator();iter.hasNext();) {
			IRow row=(IRow)iter.next();
			esdk.map.trim(row.record());
		}
		return rs;
	}
	
	/**删除空行，一般用于excel导入*/
	public static void deleteEmptyRows(ABRowSet rs){
		for(rs.beforeFirst();rs.next();) {
			if(!RowUtils.hasContent(rs.getCurrentRow())) {
				rs.remove(rs.getCurrentRow());
				rs.previous();
			}
		}
	}

	/**解决Long值转到前端js会丢失精度的问题，会把Long转换为字符串*/
	public static JSONArray toJsonArray(List<Map> rowsetMap,boolean isCamelCase){
		JSONArray result=new JSONArray(rowsetMap.size());
		for(Iterator iter=rowsetMap.iterator();iter.hasNext();){
			LinkedHashMap map=(LinkedHashMap)iter.next();
			JSONObject jo=new JSONObject(true);
			for(Iterator iter1=map.entrySet().iterator();iter1.hasNext();) {
				Entry<String,Object> entry=(Entry)iter1.next();
				boolean isLongType=entry.getValue()!=null && entry.getValue() instanceof Long;
				jo.put(isCamelCase?esdk.str.toCamelCase(entry.getKey()):entry.getKey(),isLongType?entry.getValue().toString():entry.getValue());
			}
			result.add(jo);
		
		}
		return result;
	}
	
}
