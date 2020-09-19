package com.esdk.sql.orm;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import com.alibaba.fastjson.JSONObject;
import com.esdk.esdk;
import com.esdk.sql.SQLAssistant;
import com.esdk.utils.CharAppender;
import com.esdk.utils.EasyMath;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyReflect;
import com.esdk.utils.EasyStr;
import com.esdk.utils.Response;
import com.esdk.utils.TString;

public class RowUtils{
  
	/**手工填入数据库字段的默认值*/
	public static <PR extends ParentRow> PR fillDefaultVals(PR pr){
		Object[] defs=(Object[])esdk.reflect.getFieldValueBySafe(pr.getMetaData(),"ColumnDefs");
		String[] fields=(String[])esdk.reflect.getFieldValue(pr.getMetaData(),"FieldNames");
		for(int i=0;defs!=null&&i<defs.length;i++) {
			if(defs[i]!=null)
				pr.record.put(fields[i],defs[i]);
		}
		return pr;
  }
  
  public static ColumnMetaData findColumnMetaData(Class boclass,String columnName){
    if(columnName==null)
      return null;
    columnName=EasyStr.toCamelCase(columnName,true);
    ColumnMetaData result=null;
    try{
      Method getColumnMethod=boclass.getMethod("get".concat(columnName),new Class[]{});
      Class returncls=getColumnMethod.getReturnType();
      ColumnMetaData cmd=new ColumnMetaData();
      cmd.ColumnName=columnName;
      cmd.ColumnClass=returncls;
      result=cmd;
      return result;
    }
    catch(NoSuchMethodException e){
      return result;
    }
  }
  
  public static boolean hasColumnName(Class boclass,String columnName){
    return findColumnMetaData(boclass,columnName)!=null;
  }
  
  public static String toCsv(List list){
    TString result=new TString();
    if(list.size()>0){
      ParentRow row=(ParentRow)list.get(0);
      String[] columns=(String[])row.record.keySet().toArray(new String[0]);
      result.append(new CharAppender(',').add(columns));
      int size=list.size();
      for(int i=0;i<size;i++){
        row=(ParentRow)list.get(i);
        CharAppender ca=new CharAppender(',');
        for(int j=0;j<columns.length;j++){
          ca.add(EasyStr.getStringNoNull(row.get(columns[j])));
        }
        result.appendLine(ca);
      }
    }
    return result.toString();
  }

	/**会自动把row的所有字段名的下划线方式转为驼峰方式*/
	public static Object loadFrom(Object pojo,IRow row,boolean isNullable,String...excludes){
		Class cls=pojo.getClass();
		String[] columns=row.getNames();
		try{
			for(int i=0,n=columns.length;i<n;i++){
				Method m=EasyReflect.findGetterMethod(cls,columns[i]);
				if(m!=null){
					Object value=m.invoke(pojo,new Object[]{});
					if((isNullable||EasyObj.isValid(value))&&!EasyStr.existOf(excludes,columns[i])){
						row.set(columns[i],value);
					}
				}
			}
			return pojo;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**会自动把row的所有字段名的下划线方式转为驼峰方式*/
  public static Object copyTo(IRow row,Object pojo,boolean isNullable,String...excludes) {
  	Class cls=pojo.getClass();
  	String[] columns=row.getNames();
  	try{
			for(int i=0,n=columns.length;i<n;i++) {
				Object value=row.get(columns[i]);
				if((isNullable||value!=null)&&!EasyStr.existOf(excludes,columns[i])) {
					Method m=EasyReflect.findSetterMethod(cls,esdk.str.toCamelCase(columns[i]));
					if(m==null)
						continue;
					m.invoke(pojo,new Object[] {row.get(columns[i])});
				}
			}
			return pojo;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
  }
  
  public static List toList(IRow row) {
  	return toList(row,row.getNames());
  }
  
  public static List toList(IRow row,String... names) {
  	ArrayList result=new ArrayList();
  	for(int i=0;i<names.length;i++) {
  		result.add(row.get(names[i]));
  	}
  	return result;
  }
  
  public static IRowSet appendColumns(ARowSet rs,String... columns) {
  	rs.columns.addAll(Arrays.asList(columns));
  	for(Iterator iter=rs.iterator();iter.hasNext();) {
  		ABRow row=(ABRow)iter.next();
  		for(int i=0;i<columns.length;i++) {
  			row.set(columns[i],null);
  		}
  	}
  	return rs;
  }
	
	public static <T> T convert(Class cls,Object obj){
		if(cls.equals(Integer.class))
			return (T)EasyMath.toInteger(obj);
		else if(cls.equals(Boolean.class))
			return (T)EasyObj.toBoolean(obj);
		else
			return (T)obj;
	}
	
	public static String[] Keywords=new String[]{
		"",
		"names",
		"map",
		"changed",
		"ISQL",
		"autoIncrement",
		"checkDirty",
		"checkVersion",
		"connection",
		"record",
		"session",
		"existRecord",
		"saved",
		"PKID",
		"pkid",
		"tableName",
		"primaryKeyName",
		"primaryKey",
		"metaData",
		"columnClass",
		"getShort",
		"getInteger",
		"getLong",
		"getDouble",
		"getString",
		"getBoolean",
		"newValues"
	};
	
	public static Object getCurrentValue(ResultSet rs,ResultSetMetaData rsmd,int index) throws SQLException {
		Object result = rs.getObject(index);
		if(result==null)
			return result;
		String columnTypeName=rsmd.getColumnTypeName(index);
		if (columnTypeName.equalsIgnoreCase("TEXT")) // especial for text to string
			result = rs.getString(index);
		else if ("BIT".equalsIgnoreCase(columnTypeName)||("TINYINT".equalsIgnoreCase(columnTypeName))) //mssql bit will be return boolean type,同时，只要是tinyint类型就认为是boolean类型，不再管长度问题，因为view的长度会出现4的情况，按默认为1长度的做法已经行不通了：1==rsmd.getColumnDisplaySize(index)
			result = rs.getBoolean(index);
		else if(SQLAssistant.isOracle()) {
			if(columnTypeName.equalsIgnoreCase("NUMBER")){
				if(rsmd.getPrecision(index)==1)  //oracle number(1) will be return boolean type.
					result = rs.getBoolean(index);
				else
					result = rs.getInt(index);
				}
				else if(columnTypeName.equalsIgnoreCase("DATE"))
						result=rs.getTimestamp(index);	
		}
		return result;
	}
	
	public static Response checkUnique(ParentRow row) throws Exception{
		Object uniqueIndexFields=EasyReflect.getFieldValue(row.getMetaData(),"UniqueIndexFields");
		Response result=new Response(true);
		if(uniqueIndexFields instanceof String[][]) {
			for(String[] item:(String[][])uniqueIndexFields)
				result.append(checkUnique(row,item));
		}
		else
			result.append(checkUnique(row,(String[])uniqueIndexFields));
	
		return result;
	}
	
	public static Response validate(ParentRow row){
		String[] FieldNames=(String[])EasyReflect.getFieldValue(row.getMetaData(),"FieldNames");
		Object[] ColumnDefs=(Object[])EasyReflect.getFieldValue(row.getMetaData(),"ColumnDefs");
//		String[] Remarks=(String[])EasyReflect.getFieldValue(row.getMetaData(),"Remarks");
		boolean[] isNullables=(boolean[])EasyReflect.getFieldValue(row.getMetaData(),"isNullables");
		Map changedMap=row.getChanged();
		Response result=new Response(true).setDelimiter("，");
		for(int i=0;i<FieldNames.length;i++) {
			String field=FieldNames[i];
			if(isNullables[i]==false 
					&& !row.getPrimaryKeyName().equals(field)
					&& changedMap.containsKey(field) 
					&& esdk.obj.isEmpty(changedMap.get(field))
					&& (ColumnDefs!=null && ColumnDefs[i]==null)) {
				String label=(String)EasyReflect.getFieldValue(row.getMetaData(),"R"+esdk.str.toCamelCase(field,true));
				label=getFieldLabel(field,label);
				result.appendErrMsg("字段【"+label+"】不能为空");
			}
		}
		return result;
	}
	
	private static String getFieldLabel(String field,String remark){
		String result=esdk.str.or(remark.toUpperCase(),field);
		if(result.contains("(")||result.contains("（"))
			result=esdk.regex.findSub(result,"(.*?)[\\(,，（：]",1);
		if(result.endsWith("ID"))
			result=result.replace("ID","名称");
		return result.trim();
	}
	
	public static Response checkUnique(ParentRow row,String... uniqueIndexFields) throws Exception{
		Response result=new Response(true);
		if(uniqueIndexFields.length>0&&!row.checkUnique(uniqueIndexFields)) {
			CharAppender uniqueValues=new CharAppender(',');
			for(int i=0;i<uniqueIndexFields.length;i++) {
				String label=findUniqueLabel(row.getMetaData(),uniqueIndexFields[i]);
				if(EasyObj.isBlank(label))
					label=uniqueIndexFields[i];
				String value=EasyStr.valueOf(row.get(uniqueIndexFields[i]));
				uniqueValues.append(label+": "+value);
			}
			result.setErrMsg("唯一值重复【"+uniqueValues.toString()+"】");
		}
		return result;
	}
	
	private static String findUniqueLabel(Object instance,String columnname){
		return (String)EasyReflect.getFieldValue(instance,"R"+EasyStr.toCamelCase(columnname,true));
	}

	public static void loadFromHttpParams(ParentRow row,String httpParams,boolean isFormatToUnderScoreName){
		for(StringTokenizer st=new StringTokenizer(httpParams,"&");st.hasMoreElements();) {
			String v=(String)st.nextToken();
			String[] p=esdk.map.splitFirst(v,"=");
			String key=isFormatToUnderScoreName?esdk.str.toUnderlineCase(p[0]):p[0];
			if(esdk.str.existOf(row.getNames(),key,true)) {
				System.out.println(key);
				row.put(key,p[1]);
			}
		}
	}
	
	
	/**判斷該行內容是否有效、有內容*/
	public static boolean hasContent(IRow row){
		for(Iterator<Entry> iter=row.record().entrySet().iterator();iter.hasNext();) {
			Entry entry=iter.next();
			if(esdk.obj.isValid(entry.getValue()))
				return true;
		}
		return false;
	}

	public static Long genNextPrimaryId(){
		return esdk.sql.genNextPrimaryId();
	}
	
	/**解决Long值转到前端js会丢失精度的问题，会把Long转换为字符串*/
	public static JSONObject toJsonObject(Map<String,Object> rowMap,boolean isCamelCase){
			JSONObject result=new JSONObject(true);
			for(Iterator iter1=rowMap.entrySet().iterator();iter1.hasNext();) {
				Entry<String,Object> entry=(Entry)iter1.next();
				boolean isLongType=entry.getValue()!=null && entry.getValue() instanceof Long;
				result.put(isCamelCase?esdk.str.toCamelCase(entry.getKey()):entry.getKey(),isLongType?entry.getValue().toString():entry.getValue());
			}
			return result;
	}
	
	
	/**删除空格，一般用于excel导入*/
	public static <R extends IRow> R trim(R row) {
		esdk.map.trim(row.record());
		return row;
	}
}
