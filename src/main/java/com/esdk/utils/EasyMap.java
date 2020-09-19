package com.esdk.utils;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.esdk.esdk;

import cn.hutool.core.map.MapUtil;

import java.util.StringTokenizer;

import org.nutz.lang.Lang;

public class EasyMap{
	public final static Map<?,?> EmptyMap=Constant.EmptyMap;
	
  public static Map createMap(Object... args) {
  	LinkedHashMap result=new LinkedHashMap();
  	for(int i=0,n=args.length;i<n;i+=2) {
  		result.put(args[i],args[i+1]);
  	}
  	return result;
  }
  
  /**自动转换值的类型*/
  public static Map<String,Object> valueOf(String s){
  	return strToMap(s,",",true);
  }
  
  /**值为字符串，不会自动转换为其他类型*/
  public static Map<String,String> strToMap(String s){
  	return strToMap(s,",",false);
  }
  
  /**字符串转换为Map*/
  public static Map strToMap(String s,String delimter,boolean isConvertDataType){
  	if(s==null||s.length()==0)
  		return new LinkedHashMap();
  	else if(s.charAt(0)=='{'&&s.charAt(s.length()-1)=='}') {
  		s=s.substring(1,s.length()-1);
  	}
		LinkedHashMap result=new LinkedHashMap<String,String>(8);
		for (StringTokenizer st=new StringTokenizer(s,delimter);st.hasMoreElements();) {
			String temp[]=st.nextToken().split("[=:]",2);
			if(temp.length==2){
				result.put(temp[0].trim(),isConvertDataType?temp[1]:convert(temp[1]));
			}
		}
		return result;
  }
  
  private static Object convert(String value){
  	if(value.equals("null"))
			return null;
  	else if(value.equals("true"))
  		return Boolean.TRUE;
  	else if(value.equals("false"))
  		return Boolean.FALSE;
  	else if(value.matches("\\w*"))
  		return value;
  	else if(EasyMath.isNumeric(value))
  		return EasyMath.toNumber(value);
  	else if(EasyTime.isDate(value)){
			return EasyTime.valueOf(value);
  	}
  	else
  		return value;
  }

	public static Map<String,String> urlParamsToMap(String httpParams){
		LinkedHashMap result=new LinkedHashMap();
		if(httpParams!=null) {
			for(StringTokenizer st=new StringTokenizer(httpParams,"&");st.hasMoreElements();) {
				String v=(String)st.nextToken();
				String[] p=splitFirst(v,"=");
				result.put(p[0],p[1]);
			}
		}
		return result;
	}

	public <T extends Map<String,String>> T trim(T map) {
		for(Iterator<Entry<String,String>> iter=map.entrySet().iterator();iter.hasNext();) {
			Entry entry=iter.next();
			if(entry.getValue()==null)
				continue;
			else if(entry.getValue() instanceof CharSequence)
				entry.setValue(esdk.str.trim(entry.getValue().toString()));
			else if(entry.getValue() instanceof Map)
				trim((Map)entry.getValue());
		}
		return map;
	}
	
	public static String toHttpParams(Map<String,String> params){
		return toHttpParams(params,false);
	}
	
	public static String toHttpParams(Map<String,String> params,boolean isEncode){
		StringBuilder result=new StringBuilder();
		if(params!=null) {
			try{
				for(Entry entry:params.entrySet()) {
					if(isEncode)
						result.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(),"utf8")).append("&");
					else
						result.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
				}
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		if(result.length()>0)
			result.deleteCharAt(result.length()-1);
		return result.toString();
	}

	public static String[] splitFirst(String str,String delimter) {
		int pos=str.indexOf(delimter);
		String[] result=new String[] {null,null};
		if(pos>=0) {
			result[0]=str.substring(0,pos);
			result[1]=str.substring(pos+1);
		}
		else {
			result[0]=str;
			result[1]="";
		}
		return result;
	}
  
	public static Map toUnderlineCaseMap(Map<String,?> map) {
		try{
			Map result=(Map)map.getClass().newInstance();
			for(Iterator iter=map.entrySet().iterator();iter.hasNext();){
				Entry entry=(Entry)iter.next();
				String key=(String)entry.getKey();
				result.put(esdk.str.toUnderlineCase(key),entry.getValue());
			}
			return result;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**把key改为驼峰格式*/
	public static Map toCamelCaseMap(Map map){
		try{
			Map result=(Map)map.getClass().newInstance();
			for(Iterator iter=map.entrySet().iterator();iter.hasNext();){
				Entry entry=(Entry)iter.next();
				String key=(String)entry.getKey();
				result.put(esdk.str.toCamelCase(key),entry.getValue());
			}
			return result;
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static Map clearNull(Map map) {
		Object[] keys=map.keySet().toArray();
		for(int i=0;i<keys.length;i++){
			Object key=keys[i];
			if(map.get(key)==null) {
				map.remove(key);
			}
		}
		return map;
	}
	
	public static <K,V>V getValue(Map<K,V> map,K key,V def) {
		V result=map.get(key);
		if(result==null)
			result=def;
		return result;
	}

	public static <K,V>V getValue(Map<K,V> map,K key) {
		V result=map.get(key);
		if(result==null)
			result=(V)key;
		return result;
	}

	/**从多个map中取值，获取不了就返回key值*/
	public static String getValue(String key,Map<String,String>...maps){
		String result=null;
		for(int i=0;i<maps.length;i++){
			result=maps[i].get(key);
			if(result!=null)
				break;
		}
		if(result==null)
			result=key;
		return result;
	}

	/**忽略大小写*/
	public static boolean containsKey(Map map,Object key){
		boolean result=map.containsKey(key);
		if(!result&&(key instanceof CharSequence)){
			for(Iterator iter=map.keySet().iterator();iter.hasNext();){
				String realKey=iter.next().toString();
				if(EasyStr.equals(realKey,key.toString(),true))
					return map.containsKey(realKey);
			}
		}
		return result;
	}

	public static String mapToStr(Map<String, Object> map, char express) {
			if (map == null || map.size() == 0)
					return "";
			StringBuffer result = new StringBuffer().append('{');
			for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
					Entry e = (Entry) iter.next();
					result.append(e.getKey()).append(express).append(e.getValue());
					result.append(iter.hasNext() ? "," : "");
			}
			result.append('}');
			return result.toString();
	}

	public static String mapToStr(Map map) {
			return mapToStr(map, '=');
	}

	/**把多个map合并，注意修改和返回的是第一个map，而不是创建一个新的map实例
	 * @author 范飞宇
	 * @parm isOverwrite:是否允许修改第一个map的值
	 * */
	public static <K,V> Map<K,V> margin(boolean isOverwrite,Map<K,V> result,Map<K,V>... maps){
		for(Map map:maps) {
			for(Object key:map.keySet()) {
				if(isOverwrite || !result.containsKey(key) || esdk.obj.isBlank(result.get(key)))
					result.put((K)key,(V)map.get(key));
			}
		}
		return result;
	}

	public static void main(String[] args){
		String httpParams="NAME=买家1&USERTYPE=0&RTURL=http://128.128.85.133:8080/ccb-epay/epay/execReceipt.do?txType=login_rtn&TRANSID=120424175253546920&TXCODE=EP0013&MERCHANTID=0001";
		esdk.tool.assertEquals(EasyMap.urlParamsToMap(httpParams).toString(),"{NAME=买家1, USERTYPE=0, RTURL=http://128.128.85.133:8080/ccb-epay/epay/execReceipt.do?txType=login_rtn, TRANSID=120424175253546920, TXCODE=EP0013, MERCHANTID=0001}");
		esdk.tool.assertEquals(strToMap("{name=李元富, sex=男}").toString(),"{name=李元富, sex=男}");
		esdk.tool.assertEquals(strToMap("{name:李元富, sex:男}").toString(),"{name=李元富, sex=男}");
		esdk.tool.assertEquals(createMap("name","张三","code","zhang","sex","男","age",18).toString(),"{name=张三, code=zhang, sex=男, age=18}");
		esdk.tool.assertEquals(createMap("name","李远行","age",18,"valid",true,"createTime",LocalDate.parse("2008-12-27")).toString(),"{name=李远行, age=18, valid=true, createTime=2008-12-27}");
		esdk.tool.assertEquals(mapToStr(strToMap("{name=李元富,sex=男}")), "{name=李元富,sex=男}");
		esdk.tool.assertEquals(mapToStr(strToMap("{name=李元富,sex}")), "{name=李元富}");
		esdk.tool.assertEquals(mapToStr(valueOf("{name:李元富,sex:男,isMale:true,age:40}"),':'), "{name:李元富,sex:男,isMale:true,age:40}");
		esdk.tool.assertEquals(margin(false,valueOf("{name:李元富,isMale:true,age:40}"),valueOf("{sex:男,age:50}")).toString(),"{name=李元富, isMale=true, age=40, sex=男}");
		esdk.tool.assertEquals(margin(true,valueOf("{name:李元富,age:40}"),valueOf("{name:李元富,sex:男,age:50}")).toString(),"{name=李元富, age=50, sex=男}");
		esdk.tool.printAssertInfo();
	}

}
