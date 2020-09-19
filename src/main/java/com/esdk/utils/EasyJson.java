package com.esdk.utils;

import org.nutz.json.Json;

import com.esdk.esdk;

/**仅为了可以用Easy开头找到JsonUtils的方法，具体的功能都放到JsonUtils.java类中实现*/
public class EasyJson extends JsonUtils{

	/**Json字符串转Java对象*/
	public static <T> T toBean(String jsonStr,Class<T> cls) {
		return Json.fromJson(cls,jsonStr);
	}
	
	/**Json字符串转对象数组*/
	public static <T> T[] fromJsonAsArray(String jsonStr,Class<T> cls) {
		return Json.fromJsonAsArray(cls,jsonStr);
	}

	/**对象转Json字符串*/
	public static String toJson(Object obj) {
		return Json.toJson(obj);
	}

	public boolean isJsonObject(String requestBody){
		if(esdk.str.isBlank(requestBody))
			return false;
		else if(requestBody.matches("^\\{[.\\s\\S]*?\\}$"))
			return true;
		return false;
	}
	
	/**
	 * [.\\s\\S]*可以匹配换行
	 * 只判断是否为JsonArray
	 * */
	public boolean isJsonArray(String requestBody){
		if(esdk.str.isBlank(requestBody))
			return false;
		else if(requestBody.matches("^\\[[.\\s\\S]*?\\]$"))
			return true;
		return false;
	}

	/**
	 * [.\\s\\S]*可以匹配换行
	 * 只判断是否为JsonObject
	 * */
	public boolean isJson(String requestBody){
		if(esdk.str.isBlank(requestBody))
			return false;
		else if(esdk.regex.matches(requestBody,"^(\\[|\\{)(.*?)(\\}|\\])$"))
			return true;
		return false;
	}

	/**
	 * [.\\s\\S]*可以匹配换行
	 * 判断JsonArray或JsonObject
	 * */
	public static String trim(String jsonstr){
		if(esdk.json.isJson(jsonstr))
			return jsonstr.replaceAll("[\r\n\t ]","");
		else
			return jsonstr;
	}
}
