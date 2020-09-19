package com.esdk.utils;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.esdk.esdk;
import com.esdk.sql.orm.RowUtils;

public class JsonUtils{

	/* @Deprecated public static JsonConfig getJsonConfig() { JsonConfig result =
	 * new JsonConfig(); //
	 * jc.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT); //
	 * jc.setSkipJavaIdentifierTransformationInMapKeys(true); //
	 * jc.setExcludes(RowFacility.Keywords);
	 * result.registerJsonValueProcessor(Timestamp.class, new
	 * DateJsonValueProcessor()); result.registerJsonValueProcessor(Date.class,
	 * new DateJsonValueProcessor());
	 * result.registerJsonValueProcessor(java.sql.Date.class, new
	 * DateJsonValueProcessor()); return result; } */

	private static SerializeConfig _SerializeConfig=SerializeConfig.getGlobalInstance();
	public static PropertyPreFilter IRowPropertyPreFilter=getPropertyPreFilter(RowUtils.Keywords);

	public static PropertyPreFilter IResultSetPropertyPreFilter=IRowPropertyPreFilter;

	public static PropertyPreFilter getPropertyPreFilter(final String...excludeNames){
		return new PropertyPreFilter(){
			@Override
			public boolean apply(JSONSerializer serializer,Object source,String name){
				return esdk.str.indexOf(excludeNames,name)<0;
			}
		};
	}

	static{
		SimpleDateFormatSerializer simpleDateFormatSerializer=new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss");
		_SerializeConfig.put(Date.class,simpleDateFormatSerializer);
		_SerializeConfig.put(Timestamp.class,simpleDateFormatSerializer);
		_SerializeConfig.put(java.sql.Date.class,simpleDateFormatSerializer);
	}

	public static SerializeConfig getSerializeConfig(){
		return _SerializeConfig;
	}

	/** JavaObject|JSONArray|JSONObject-->string */
	public static String stringify(Object obj){
		return toJSONString(obj);
	}

	/** string-->JSONObject|JSONArray */
	public static Object toJson(String jsonstr){
		return JSON.parse(jsonstr);
	}

	public static String toJSONString(Object obj){
		return JSON.toJSONString(obj,_SerializeConfig);
	}

	public static String toJSONString(Object obj,SerializeFilter filter){
		return toJSONString(obj,_SerializeConfig,filter);
	}

	private static final String toJSONString(Object object,SerializeConfig config,SerializeFilter filter,SerializerFeature...features){
		SerializeWriter out=new SerializeWriter();

		try{
			JSONSerializer serializer=new JSONSerializer(out,config);
			for(com.alibaba.fastjson.serializer.SerializerFeature feature:features){
				serializer.config(feature,true);
			}

			serializer.config(SerializerFeature.WriteDateUseDateFormat,true);

			if(filter!=null){
				if(filter instanceof PropertyPreFilter){
					serializer.getPropertyPreFilters().add((PropertyPreFilter)filter);
				}

				if(filter instanceof NameFilter){
					serializer.getNameFilters().add((NameFilter)filter);
				}

				if(filter instanceof ValueFilter){
					serializer.getValueFilters().add((ValueFilter)filter);
				}

				if(filter instanceof PropertyFilter){
					serializer.getPropertyFilters().add((PropertyFilter)filter);
				}
			}

			serializer.write(object);

			return out.toString();
		}finally{
			out.close();
		}
	}

	public static JSONArray toJsonArray(Object obj){
		if(obj!=null&&obj instanceof String)
			return toJsonArray((String)obj);
		else{
			List list=(List)obj;
			JSONArray result=new JSONArray(list.size());
			for(Iterator iter=list.iterator();iter.hasNext();){
				Object o=iter.next();
				if(o instanceof LinkedHashMap){
					LinkedHashMap map=(LinkedHashMap)o;
					JSONObject jo=new JSONObject(true);
					for(Iterator iter1=map.entrySet().iterator();iter1.hasNext();) {
						Entry entry=(Entry)iter1.next();
						jo.put((String)entry.getKey(),entry.getValue());
					}
					result.add(jo);
				}else {
					result.add(JSONObject.toJSON(o));
				}
			}
			return result;
		}
	}

	public static JSONArray toJsonArray(Collection obj,PropertyPreFilter filter){
		return (JSONArray)toJSON(obj,filter);
	}

	public static JSONArray toJsonArray(String jsonstr){
		return JSON.parseArray(jsonstr);
	}

	public static JSONObject toJsonObject(String jsonstr,boolean ordered){
		if(ordered)
			return JSONObject.parseObject(jsonstr,Feature.OrderedField);
		else
			return JSONObject.parseObject(jsonstr);
	}

	public static JSONObject toJsonObject(String jsonstr){
		return JSONObject.parseObject(jsonstr);
	}

	public static JSONObject toJsonObject(Object obj){
		if(obj instanceof LinkedHashMap){
			LinkedHashMap map=(LinkedHashMap)obj;
			JSONObject result=new JSONObject(true);
			for(Iterator iter1=map.entrySet().iterator();iter1.hasNext();) {
				Entry entry=(Entry)iter1.next();
				result.put((String)entry.getKey(),entry.getValue());
			}
			return result;
		}else {
			return (JSONObject)JSONObject.toJSON(obj);
		}
	}

	public static JSONObject toJsonObject(Object obj,PropertyPreFilter filter){
		return (JSONObject)toJSON(obj,filter);
	}

	public static JSONObject toJsonObject(Object obj,ParserConfig parserConfig){
		return (JSONObject)JSONObject.toJSON(obj,parserConfig);
	}

	/** string-->javabean */
	public static <T>T toJavaBean(Map map,Class<T> clazz){
		return JSONObject.toJavaObject((JSON)toJSON(map),clazz);
	}

	/** string-->javabean */
	public static <T>T toBean(String jsonstr,Class<T> clazz){
		return JSONObject.parseObject(jsonstr,clazz);
	}

	/** jsonobject-->javabean */
	public static <T>T toBean(JSON json,Class<T> clazz){
		return JSONObject.toJavaObject(json,clazz);
	}

	/** string-->list<javabean> */
	public static <T>List<T> toBeanList(String jsonstr,Class<T> clazz){
		return JSONArray.parseArray(jsonstr,clazz);
	}

	public static <T>List<T> toBeanList(JSONArray jsonarray,Class<T> clazz){
		LinkedList result=new LinkedList<T>();
		for(Iterator iter=jsonarray.iterator();iter.hasNext();){
			JSONObject jo=(JSONObject)iter.next();
			result.add(toBean(jo,clazz));
		}
		return result;
		/* return toBeanList(jsonarray.toJSONString(),clazz); */
	}

	public static String toJSONString(JSONObject jo,SerializeConfig config){
		SerializeWriter out=new SerializeWriter();
		try{
			new JSONSerializer(out,config).write(jo);
			return out.toString();
		}finally{
			out.close();
		}
	}

	public static final Object toJSON(Object javaObject){
		if(javaObject instanceof JSON)
			return javaObject;
		else
			return JSON.toJSON(javaObject);
	}

	public static final Object toJSON(Object javaObject,PropertyPreFilter filter){
		if(javaObject instanceof JSON)
			return javaObject;
		else
			return JSONObject.parse(JSON.toJSONString(javaObject,filter));
	}

	public static JSONObject decodeUnicode(JSONObject jo){
		if(jo!=null){
			for(Iterator iter=jo.keySet().iterator();iter.hasNext();){
				Object key=iter.next();
				Object value=jo.get(key);
				if(value instanceof String&&((String)value).contains("\\u")){
					value=esdk.str.decodeUnicode((String)value);
					jo.put((String)key,value);
				}else if(value instanceof JSONObject)
					decodeUnicode((JSONObject)value);
				else if(value instanceof JSONArray)
					decodeUnicode((JSONArray)value);
			}
		}
		return jo;
	}

	public static JSONArray decodeUnicode(JSONArray ja){
		for(Iterator iter=ja.iterator();iter.hasNext();){
			JSONObject jo=(JSONObject)iter.next();
			decodeUnicode(jo);
		}
		return ja;
	}
}
