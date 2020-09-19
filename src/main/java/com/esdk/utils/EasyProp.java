package com.esdk.utils;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.esdk.esdk;

public class EasyProp{
	public Properties _prop=new Properties();
	public EasyProp(String... propertiesFileNames){
		for(String propfilename:propertiesFileNames) {
			InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(propfilename);
			if(is!=null)
				_prop.putAll(esdk.file.getProperties(is));
		}
	}
	
	public Properties getProperties() {
		return _prop;
	}
	
	public String get(String key) {
		return getString(key);
	}
	
	public String get(String key,String defVal) {
		return getString(key,defVal);
	}
	
	public String getString(String key) {
		String value=_prop.getProperty(key);
		if(value!=null&&value.contains("${")) {
			value=esdk.str.format(value,true,new Map[]{_prop,System.getProperties()});
			_prop.setProperty(key,value);
		}
		return value;
	}

	public String[] getArray(String key) {
		return esdk.str.splits(get(key));
	}

	public String[] getArray(String key,String defValue) {
		return esdk.str.splits(get(key,defValue));
	}
	
	public String getString(String key,String defValue) {
		return esdk.str.getText(getString(key),defValue);
	}

	public Integer getInteger(String key) {
		return esdk.math.toInteger(getString(key));
	}
	
	public Integer getInteger(String key,Integer defValue) {
		return esdk.obj.or(getInteger(key),defValue);
	}
	
	public Boolean getBoolean(String key) {
		Object value=getString(key);
		if(value!=null)
			return esdk.obj.isTrue(value);
		else
			return null;
	}
	
	public Boolean getBoolean(String key,Boolean defValue) {
		return esdk.obj.or(getBoolean(key),defValue);
	}
	public static void main(String[] args){
		EasyProp prop=new EasyProp("sdk.properties","project.properties","application.properties");
		String result=prop.get("LogFilePath","/logs/sql.log");
		System.out.println(result);
	}
}
