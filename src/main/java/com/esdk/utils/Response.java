package com.esdk.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esdk.esdk;
import com.esdk.sql.orm.ABRow;
import com.esdk.sql.orm.IResultSet;
import com.esdk.sql.orm.IRow;

/***
 * 返回类，用于返回错误标志和错误描述，可以追加错误信息，嵌套多个返回类等功能。
 * 
 * @author Franky
 */
public class Response{
	private StringBuffer resultMsg=new StringBuffer(),errMsg=new StringBuffer();
	private boolean success;
	private String errorPrefix="",errorPostix="！";
	private String delimiter=" | ";
	private static String _SuccessMsg="成功",_ErrorMsg="失败";
	private JSONObject attributes=new JSONObject(true);

	public Response(){
	}

	public Response(String jsonObj) {
		JSONObject jo=esdk.json.toJsonObject(jsonObj);
		setSuccess(jo.getBooleanValue("success"),jo.getString("msg"),jo.getString("msg"));
		jo.remove("success");
		jo.remove("msg");
		for(Iterator iter=jo.keySet().iterator();iter.hasNext();) {
			String key=iter.next().toString();
			put(key,jo.getString(key));
		}
	}
	
	public Response(boolean v){
		setSuccess(v);
	}

	public Response(Throwable e){
		setThrowable(e);
	}

	public Response(boolean v,String msg){
		setMsg(msg,v);
	}

	public Response setErrorPrefix(String value){
		errorPrefix=value;
		return this;
	}

	public Response setErrorPostix(String value){
		errorPostix=value;
		return this;
	}
	public Response(boolean v,String successMsg,String errMsg){
		setSuccess(v,successMsg,errMsg);
	}

	public String toString(){
		return toJson().toString();
	}

	public Response setThrowable(Throwable ex){
		success=false;
		resultMsg.delete(0,resultMsg.length());
		if(ex.getMessage()!=null)
			errMsg.append(ex.getMessage());
		else
			errMsg.append(ex.toString());
		return this;
	}

	public Response setSuccessMsg(String msg){
		return setMsg(msg,true);
	}

	public Response setMsg(String msg){
		setMsg(msg,this.success);
		return this;
	}
	
	public Response setMsg(String msg,boolean isSucceed){
		if(isSucceed){
			resultMsg.delete(0,resultMsg.length());
			if(msg!=null)
				resultMsg.append(msg);
			success=true;
		}else
			appendErrMsg(msg);
		return this;
	}

	public boolean hasErrMsg(){
		return errMsg!=null&&errMsg.length()>0;
	}

	public boolean hasReturnMsg(){
		return resultMsg!=null&&resultMsg.length()>0;
	}

	public Response appendErrMsg(String errorMsg){
		if(!EasyStr.isBlank(errorMsg)){
			success=false;
			if(errMsg.length()==0){
				errMsg.append(errorMsg);
			}else
				errMsg.append(delimiter).append(errorMsg);
		}
		return this;
	}

	public Response appendMsg(String msg){
		return appendReturnMsg(msg);
	}
	
	public Response appendReturnMsg(String msg){
		if(this.resultMsg.length()==0)
			this.resultMsg.append(msg);
		else if(esdk.str.isValid(msg))
			this.resultMsg.append(delimiter).append(msg);
		return this;
	}

	public Response appendUniqueErrMsg(String errorMsg){
		if(errMsg.indexOf(errorMsg)<0)
			appendErrMsg(errorMsg);
		return this;
	}

	public Response appendUniqueMsg(String msg){
		if(resultMsg.indexOf(msg)<0)
			appendMsg(msg);
		return this;
	}
	
	public Response appendUnique(Response ri){
		if(ri.success&&!this.getMsg().contains(ri.getMsg()) || !ri.success&&!this.getErrMsg().contains(ri.getErrMsg()))
			this.append(ri);
		return this;
	}
	

	public Response setErrMsg(String errorMsg){
		errMsg.delete(0,errMsg.length());
		if(!EasyStr.isBlank(errorMsg)){
			success=false;
			errMsg.append(errorMsg);
		}
		return this;
	}

	public String getMsg(){
		if(success)
			return resultMsg.toString();
		else
			return getErrMsg();
	}

	public String getErrMsg(){
		if(errMsg.length()>0&&esdk.str.isValid(errorPostix)&&errMsg.lastIndexOf(errorPostix)<0)
			errMsg.append(errorPostix);
		if(esdk.str.isValid(errorPrefix)&&errMsg.indexOf(errorPrefix)<0)
			errMsg.insert(0,errorPrefix);
		return errMsg.toString();
	}

	public void clear(){
		errMsg.delete(0,errMsg.length());
		resultMsg.delete(0,resultMsg.length());
		success=false;
	}

	public boolean success(){
		return success;
	}

	public Response setSuccess(boolean isSuccess){
		this.success=isSuccess;
		return this;
	}

	public Response setSuccess(boolean isSuccess,String successMsg,String errMsg){
		if(isSuccess)
			setMsg(successMsg,true);
		else
			setErrMsg(errMsg);
		return this;
	}

	public Response setSuccess(boolean isSuccess,String successMsg){
		if(isSuccess)
			setMsg(successMsg,true);
		else
			setErrMsg(esdk.str.or(successMsg,_ErrorMsg));
		return this;
	}

	public Response setValid(boolean v){
		return setSuccess(v);
	}

	public Response append(String jsonObj) {
		append(new Response(jsonObj));
		return this;
	}
	
	public Response append(Response ri){
		if(ri==this)
			return this;
		if(this!=ri){
			if(ri==null)
				return this;
			else if(ri.success()&&!this.success()&&this.resultMsg.length()==0&&this.errMsg.length()==0)
				setValid(true);
			else if(!ri.success()&&ri.errMsg.length()>0)
				this.appendUniqueErrMsg(ri.errMsg.toString());
			else if(EasyObj.isValid(ri.resultMsg.toString()))
				appendUniqueMsg(ri.getMsg());
		}
		this.attributes.putAll(ri.attributes);
		return this;
	}

	public Response append(boolean b){
		return append(new Response(b));
	}

	public Response put(String key,String value){
		attributes.put(key,value);
		return this;
	}

	public Response putAll(Map value){
		attributes.putAll(value);
		return this;
	}
	
	public Response put(String key,JSONArray jsonArray){
		attributes.put(key,jsonArray);
		return this;
	}
	
	public Response put(String key,JSONObject jsonObject){
		attributes.put(key,jsonObject);
		return this;
	}

	public Response put(String key,boolean value){
		attributes.put(key,value);
		return this;
	}

	public Response put(String key,Number value){
		if(value==null)
			value=0;
		attributes.put(key,value);
		return this;
	}

	public Response put(String key,Integer value){
		attributes.put(key,value);
		return this;
	}

	public Response put(String key,IResultSet rs){
		if(rs==null)
			return this;
		attributes.put(key,rs.toMapList(true));
		return this;
	}
	
	public Response put(String key,IRow row){
		if(row==null)
			return this;
		attributes.put(key,row.toJsonObject(true));
		return this;
	}

	public Response put(String key,List list){
		if(list!=null&&list.size()>0){
			if(list.get(0) instanceof IResultSet) {
			List<List<Map>> mapList=new ArrayList(list.size());
			for(int i=0;i<list.size();i++){
				mapList.add(((IResultSet)list.get(i)).toMapList(true));
			}
			attributes.put(key,mapList);
			}else if(list.get(0) instanceof IRow){
				List<JSONObject> rowList=new ArrayList(list.size());
				for(int i=0;i<list.size();i++){
					rowList.add(((IRow)list.get(i)).toJsonObject(true));
				}
				attributes.put(key,rowList);
			}
		}else {
			attributes.put(key,list);
		}
		return this;
	}
	
	public Object get(String key){
		return attributes.get(key);
	}
	
	public String getString(String key){
		return (String)attributes.get(key);
	}

	public Response setDelimiter(String value){
		this.delimiter=value;
		return this;
	}

	public JSONObject toJson(){
		attributes.put("success", success());
		if(esdk.str.isValid(getMsg()))
			attributes.put("msg", getMsg());
		return attributes;
	}

	public static boolean isResponse(String res) {
		return res!=null&&res.matches(".*?\"success\":(true|false).*?");
	}
	public static void main(String[] args){
		esdk.tool.assertEquals(new Response(true).toString(),"{\"success\":true,\"msg\":\"成功\"}");
		esdk.tool.assertEquals(new Response(false).toString(),"{\"success\":false,\"msg\":\"失败！\"}");
		esdk.tool.assertEquals(new Response(false).toString(),"{\"success\":false,\"msg\":\"失败！\"}");
		esdk.tool.assertEquals(new Response(true).append(new Response(true)).toString(),"{\"success\":true,\"msg\":\"成功\"}");
		esdk.tool.assertEquals(new Response(false,"测试重复错误").append(new Response(false,"测试重复错误")).toString(),"{\"success\":false,\"msg\":\"测试重复错误！\"}");
		Response ri=new Response(false,"除零错误");
		esdk.tool.assertEquals(ri.toString(),"{\"success\":false,\"msg\":\"除零错误！\"}");
		esdk.tool.assertEquals(new Response(true).setErrMsg("金额不能为空").toString(),"{\"success\":false,\"msg\":\"金额不能为空！\"}");
		ri.put("id","8001");
		esdk.tool.assertEquals(ri.toString(),"{\"success\":false,\"msg\":\"除零错误！\",\"id\":\"8001\"}");
		JSONObject jo=new JSONObject();
		jo.put("userId","1234");
		jo.put("userName","张三");
		ri.put("user",jo);
//		System.out.println(ri);
		esdk.tool.assertEquals(esdk.json.toJSON(ri.toString()).toString(),"{\"success\":false,\"msg\":\"除零错误！\",\"id\":\"8001\",\"user\":{\"userName\":\"张三\",\"userId\":\"1234\"}}");
	}

	public boolean isValid(){
		return success;
	}

}
