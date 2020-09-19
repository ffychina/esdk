package com.esdk.sql;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

import com.esdk.esdk;
import com.esdk.utils.EasyStr;
import com.esdk.utils.VString;

class ReflectRowMapper implements IRowMappper{
	private Class _pojoClass;
	public ReflectRowMapper(Class pojoClass){
		_pojoClass=pojoClass;
	}
	
	@Override public Object mapRow(ResultSet resultSet,int index) throws SQLException{
		try{
			Object pojo=_pojoClass.newInstance();
			ResultSetMetaData rsmd=resultSet.getMetaData();
			for(int i=0;i<rsmd.getColumnCount();i++){
				String columnName=rsmd.getColumnLabel(i+1);
				VString setMethodName=new VString("set"+EasyStr.toCamelCase(columnName,true));
				Method method=getMethodMap(_pojoClass).get(setMethodName);
				if(method!=null) {
					method.invoke(pojo,new Object[] {resultSet.getObject(columnName)});
				}
			}
			return pojo;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	private HashMap methodMap=null;
	private HashMap<VString,Method> getMethodMap(Class cls) {
		if(methodMap==null) {
			Method[] methods=cls.getMethods();
			methodMap=new HashMap(methods.length);
			for(int i=0;i<methods.length;i++){
				if(methods[i].getName().startsWith("set"))
					methodMap.put(VString.valueOf(methods[i].getName()),methods[i]);
			}
		}
		return methodMap;
	}
}
