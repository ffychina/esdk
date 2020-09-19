package com.esdk.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import org.nutz.castor.Castors;
import org.nutz.lang.Dumps;
import org.nutz.lang.Lang;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;

import cn.hutool.core.util.ObjectUtil;

public class EasyObj{

	public static String getClassSimpleName(String className) {
		int index=className.lastIndexOf(".");
		return className.substring(index+1);
	}
	
	public static Object getValidObject(Object...arr){
		for(int i=0;i<arr.length;i++){
			if(isValid(arr[i]))
				return arr[i];
		}
		return null;
	}

	public static String getStandardTimeFormat(String value) throws Exception{
		return EasyTime.getStandardTimeFormat(value);
	}

	public static boolean isNumber(String value){
		return EasyMath.isNumeric(value);
	}

	public static String FormatNumber(BigDecimal value,int width,int decimalDigits){
		return EasyStr.BigDecimalToString(value,width,decimalDigits);
	}

	public static int compareTo(Object left,Object right){
		int result=0;
		if(left==right)
			result=0;
		else if(left==null)
			result=-1;
		else if(right==null)
			result=1;
		else if(!left.getClass().equals(right.getClass())&&left instanceof Number&&right instanceof Number){
			result=compareTo(((Number)left).doubleValue(),((Number)right).doubleValue());
		}else if(left instanceof CharSequence&&right instanceof CharSequence){
			result=EasyStr.compareTo(left.toString(),right.toString());
		}else if(left instanceof Comparable&&right instanceof Comparable) {
			result=((Comparable)left).compareTo((Comparable)right);
		}
		else if(left==right)
			result=0;
		else
			result=left.toString().compareTo(right.toString());
		return result;
	}

	public static double min(double left,double right){
		return left<right?left:right;
	}

	public static int min(int left,int right){
		return left<right?left:right;
	}

	public static long max(long left,long right){
		return left>right?left:right;
	}

	public static double max(double left,double right){
		return left>right?left:right;
	}
	
	public static int max(int left,int right){
		return left>right?left:right;
	}

	public static <T extends Comparable>T max(T c1,T c2){
		if(c1==null)
  		return c2;
  	else if(c2==null)
  		return c1;
		return (T)(c1.compareTo(c2)>=0?c1:c2);
	}

	public static <T extends Comparable>T max(T...objs){
		if(objs.length==0)
			return null;
		T max=objs[0];
		for(T item:objs){
			if(item!=null)
				max=item.compareTo(max)>0?item:max;
		}
		return (T)max;
	}

	public static <T extends Comparable>T min(T...objs){
		if(objs.length==0)
			return null;
		T min=objs[0];
		for(T item:objs){
			if(item!=null)
				min=item.compareTo(min)<0?item:min;
		}
		return (T)min;
	}

	public static <T extends Comparable> T min(T c1,T c2){
		if(c1==null)
  		return c1;
  	else if(c2==null)
  		return c2;
		return (T)(c1.compareTo(c2)<=0?c1:c2);
	}
	
	public static boolean isNull(Object o){
		if(o==null)
			return true;
		return false;
	}

	/**其中一个有效就返回true*/
	public static boolean validOr(Object...v){
		if(v==null)
			return false;
		boolean result=false;
		for(int i=0;i<v.length;i++){
			result=result||isValid(v[i]);
			if(result)
				break;
		}
		return result;
	}
	
	/**全部有效就返回true*/
	public static boolean validAnd(Object...v) {
		return checkValid(v);
	}
	
	public static boolean checkValid(Object...v){
		if(v==null)
			return false;
		boolean result=true;
		for(int i=0;i<v.length;i++){
			result=result&&!isBlank(v[i]);
			if(!result)
				break;
		}
		return result;
	}

	public static boolean checkValid(String...v){
		if(v==null)
			return false;
		boolean result=true;
		for(int i=0;i<v.length;i++){
			result=result&&!isBlank(v[i]);
		}
		return result;
	}

	public static boolean checkValid(Number...v){
		if(v==null)
			return false;
		boolean result=true;
		for(int i=0;i<v.length;i++){
			result=result&&!isBlank(v[i]);
		}
		return result;
	}

	public static boolean isValid(Object v){
		return !isBlank(v);
	}

	public static boolean isValid(String v){
		return !isBlank(v);
	}

	public static boolean isValid(Number v){
		return !isBlank(v);
	}

	public static boolean isValid(Boolean v){
		return v!=null&&v.booleanValue();
	}

	public static boolean isBlank(String value){
		return value==null||value.length()==0;
	}

	public static boolean isBlank(Number value){
		if(value==null||value.doubleValue()==0.0)
			return true;
		return false;
	}

	/**
	 * 注意：number为0时返回true
	 * */
	public static boolean isBlank(Object value){
		if(value==null)
			return true;
		if(value instanceof CharSequence)
			return ((CharSequence)value).length()==0;
		else if(value instanceof Number)
			return ((Number)value).doubleValue()==0;
		else if(value.getClass().isArray())
			return ((Object[])value).length==0;
		else if(value instanceof Collection)
			return ((Collection)value).isEmpty();
		else
			return value.toString().length()==0;
	}

	/**
	 * 注意：number为0时返回false
	 * */
	public static boolean isEmpty(Object value){
		if(value==null)
			return true;
		if(value instanceof CharSequence)
			return ((CharSequence)value).length()==0;
		else if(value.getClass().isArray())
			return ((Object[])value).length==0;
		else if(value instanceof Collection)
			return ((Collection)value).isEmpty();
		else
			return value.toString().length()==0;
	}

	/**安全的equal方法，不会出现NullPointException的异常。请注意该方法会调用对象的.equals()的方法*/
  public static boolean equal(Object o1,Object o2) {
		return ObjectUtil.equal(o1,o2);
  }
  
	/**比较对象: 支持数组，集合，和容器。请注意该方法会调用对象的.equals()的方法*/
	public static boolean equals(Object o1,Object o2){
		return Lang.equals(o1,o2);
	}

	/**对象转成字符串后再进行对比，速度最慢，但最安全，不会出现无限比较的错误*/
  public static boolean eq(Object o1,Object o2) {
  	return o1==null?o2==null:o1.getClass().equals(o2.getClass())?equal(Dumps.obj(o1),Dumps.obj(o2)):false;
  }
  
  public static int hashCode(Object o1) {
  	return Objects.hashCode(o1);
  }

	public static long hashCode(String value){
		long h=0;
		int off=0;
		char val[]=value.toCharArray();
		long len=value.length();
		for(int i=0;i<len;i++){
			h=31*h+val[off++];
		}
		return h;
	}

	public static boolean isEnglish(char value){
		return((value>='A'&&value<='Z')||(value>='a'&&value<='z'));
	}

	public static boolean isNumeric(char value){
		return(value>='0'&&value<='9');
	}

	public static Object convert(Object value,Class cls){
		return convert(null, value, cls);
	}
	public static Object convert(Object keyName ,Object value,Class cls){
		long time=0;
		if(cls==null&&value==null)
			return null;
		else if(cls.isPrimitive())
			return valueOfPrimitive(cls,value);
		else if(value==null || value instanceof String && value.toString().equals(""))
			return null;
		else if(value!=null&&EasyObj.equal(value.getClass(),cls))
			return value;
		Object result=null;
		try{
			if(cls.equals(String.class))
				return esdk.str.valueOf(value);
			else if(cls.equals(Boolean.class))
				return Boolean.valueOf(isTrue(value));
			else if(cls.equals(boolean.class))
				return isTrue(value);
			else if(esdk.tool.asSubClass(cls,Number.class)){
				return EasyMath.convert(value,cls);
			}else if(esdk.tool.asSubClass(cls,Date.class)) {
				if(value instanceof Date)
					return value;
				else
					time=esdk.time.valueOf((String)value).getTime();
			}
			result=null;
			if(esdk.tool.asSubClass(cls,Date.class)){
				Constructor c=cls.getConstructor(long.class);
				result=c.newInstance(time);
			}else{
				Constructor c=cls.getConstructor(value.getClass());
				result=c.newInstance(value);
			}
		}catch(Exception e){
			throw new ParseRuntimeException(e.toString()+": class:"+cls.getName()+",value:"+value+((keyName==null)?"":(",key:"+keyName)));
		}
		return result;
	}

	/**调用nutz的casTo*/
	public static <C> C castTo(Object srcObj,Class<C> toCls) {
		return Castors.me().castTo(srcObj,toCls);
	}
	
	/**效果与nutz的castTo一样，性能会比nutz要好一些*/
	public static <C> C valueOf(Class<C> cls,Object value){
		try{
			if(cls==null||value==null)
				return (C)value;
			if(value.getClass().equals(cls))
				return (C)value;
			else if(cls.isPrimitive())
				return valueOfPrimitive(cls,value);
			else if(value instanceof Date){
				if(cls.equals(java.sql.Timestamp.class))
					return (C)new java.sql.Timestamp(((Date)value).getTime());
				else if(cls.equals(java.sql.Date.class))
					return (C)new java.sql.Date(((Date)value).getTime());
				else if(cls.equals(java.util.Date.class))
					return (C)new java.util.Date(((Date)value).getTime());
				else{
					Constructor c=cls.getConstructor(new Class[]{
						long.class
					});
					return (C)c.newInstance(new Object[]{((Date)value).getTime()});
				}
			}
			else if(cls.equals(Integer.class))
				return (C)esdk.math.toInteger(value);
			else if(cls.equals(Double.class))
				return (C)esdk.math.toDouble(value);
			else if(cls.equals(BigDecimal.class))
				return (C)esdk.math.toBigDecimal(value);
			else if(cls.equals(Long.class))
				return (C)esdk.math.toLong(value);
			else if(cls.equals(Short.class))
				return (C)esdk.math.toShort(value);
			else if(cls.equals(BigDecimal.class))
				return (C)esdk.math.toLong(value);
			else if((cls.equals(Boolean.class))&&EasyStr.existOf(new String[]{
					"1","是","Y","yes","true"
			},value.toString()))
				return (C)Boolean.TRUE;
			else if((cls.equals(Boolean.class))&&EasyStr.existOf(new String[]{
					"0","否","N","no","false"
			},value.toString()))
				return (C)Boolean.FALSE;
			else if(cls.equals(Timestamp.class)&&value instanceof String)
				return (C)Timestamp.valueOf((String)value);
			else if(cls.equals(java.sql.Date.class)&&value instanceof String)
				return (C)java.sql.Date.valueOf((String)value);
			else if(cls.equals(Date.class)&& value instanceof String)
				return (C)EasyTime.valueOf((String)value);
			else if(cls.equals(Date.class)&&value instanceof String)
				return (C)EasyTime.valueOf((String)value);
			else if(cls.isArray() && value instanceof String) {
				String clsName=cls.toString();
				if(clsName.contains("String"))
					return (C)esdk.str.splits((String)value);
				if(clsName.contains("Long"))
					return (C)esdk.math.toLongArray((String)value);
				else if(clsName.contains("Integer"))
					return (C)esdk.math.toInteger((String)value);
				else if(clsName.contains("Double"))
					return (C)esdk.math.toDouble((String)value);
				else if(clsName.contains("BigDecimal"))
					return (C)esdk.math.toBigDecimalArray(esdk.str.splits((String)value));
			}
			return Castors.me().castTo(value,cls);
		}catch(Exception e){
			throw new ParseRuntimeException(e.toString()+". value["+value+"] can't be transfer by class["+cls.getName()+"]");
		}
	}

	public static <C>C valueOfPrimitive(Class<C> cls,Object value){
		if(value==null)
			value="0";
		String s=value.toString();
		if(s.length()==0)
			s="0";
		if(cls.equals(short.class))
			return (C)new Short(s);
		else if(cls.equals(int.class))
			return (C)new Integer(s);
		else if(cls.equals(long.class))
			return (C)new Long(s);
		else if(cls.equals(double.class))
			return (C)new Double(s);
		else if(cls.equals(boolean.class)){
			return (C)Boolean.valueOf(isTrue(s));
		}else
			throw new RuntimeException("Can not get the Object by class:"+cls.getName()+" from:"+value);
	}

	public static Integer[] toIntArray(String[] arr){
		return EasyMath.toIntArray(arr);
	}

	public static boolean isFalse(Object value){
		return !isTrue(value);
	}

	public static boolean isTrue(Object value){
		if(value==null)
			return false;
		else if(value instanceof Boolean)
			return (Boolean)value;
		else if(value instanceof Number)
			return ((Number)value).intValue()>0;
		else if(value instanceof String)
			return isTrue((String)value);
		else
			return false;
	}

	/**为空则返回默认值*/
	public static <L>L ifNull(L value,L def){
		if(value!=null)
			return value;
		else
			return def;
	}

	public static boolean and(Object...v){
		if(v==null)
			return false;
		boolean result=true;
		for(int i=0;result&&i<v.length;i++){
			result=result&&v[i]!=null;
			if(v[i] instanceof Boolean)
				result=result&&(Boolean)v[i];
			else if(v[i] instanceof Number)
				result=result&&((Number)v[i]).doubleValue()==0;
		}
		return result;
	}
	
	/** 相当于js的||
	 * 一个文件只能有一个相同类型的T,否则调试时无法Inspect，所以把T改为L。	 */
	public static <L>L or(L...vars){ 
		for(int i=0;i<vars.length;i++){
			if(vars[i]!=null)
				return vars[i];
		}
		return null;
	}

	public static <L>L or(L p1,L p2){ 
		if(p1!=null)
			return p1;
		else
			return p2;
	}
	
	public static String or(String...vars){
		return EasyStr.or(vars);
	}

	public static boolean or(Boolean...vars){
		for(int i=0;i<vars.length;i++){
			if(EasyObj.isTrue(vars[i]))
				return true;
		}
		return false;
	}

	public static boolean and(Boolean...vars){
		for(int i=0;i<vars.length;i++){
			if(EasyObj.isFalse(vars[i]))
				return false;
		}
		return true;
	}

	public static boolean isTrue(String value){
		if(value==null)
			return false;
		return value.matches("1|Y|y|yes|YES|true|TRUE|YES|T|是");
	}

	/**判断是否可转换为boolean值
	 * ignoreEmpty：为true时空值返回true*/
	public static boolean isBoolean(String value,boolean ignoreEmpty){
		if(ignoreEmpty&&isBlank(value))
			return true;
		return value.matches("1|Y|y|yes|YES|true|TRUE|YES|T|是|0|N|y|no|NO|false|FALSE|F|否");
	}
	
	/**判断是否可转换为boolean值,注意空值返回false*/
	public static boolean isBoolean(String value){
		if(isBlank(value))
			return false;
		return value.matches("1|Y|y|yes|YES|true|TRUE|YES|T|是|0|N|y|no|NO|false|FALSE|F|否");
	}
	
	public static String toJson(Object o) {
		if(o==null)
			return null;
		return JsonUtils.stringify(o);
	}

	public static <T> List<T> toList(String jsonarrayStr,Class<T> c) {
  	if(jsonarrayStr==null)
			return null;
  	return JsonUtils.toBeanList(jsonarrayStr,c);
  }
	
	public static <T> List<T> toList(Enumeration<T> e) {
		List result=new LinkedList<T>();
		while(e.hasMoreElements()){
			result.add(e.nextElement());
		}
		return result;
	}
	
	public static <T> T toBean(String jsonobjectStr,Class<T> c) {
  	if(jsonobjectStr==null)
			return null;
  	else
  		return JsonUtils.toBean(jsonobjectStr,c);
  }
	
  /**通用的对象比较函数，通过json字串比较对象是否相同，不存在循环调用Object.equals(obj)的问题*/
  public static boolean equalsByJson(Object o1,Object o2) {
  	if(o1==o2)
  		return true;
  	else if(o1==null||o2==null)
  		return false;
  	else if(o1.getClass().equals(o2.getClass()))
  		return JsonUtils.stringify(o1).equals(JsonUtils.stringify(o2));
  	return false;
  }
  
	/**二维数组转换为List*/
	public static List<List> arr2ToList(Object[][] arr2){
		List result=new ArrayList(arr2.length);
		for(Object[] item:arr2) {
			result.add(Arrays.asList(item));
		}
		return result;
	}

	/**List转换为二维数组*/
	public static Object[][] listToArr2(Collection<Collection> list){
		int rowcount=list.size();
		if(rowcount<=0)
			return new Object[0][];
		Object[][] result=new Object[rowcount][];
		int i=0;
		for(Iterator iter=list.iterator();iter.hasNext();){
			Object object=(Object)iter.next();
			Object[] array=null;
			if(object instanceof List){
				array=((List)object).toArray();
			}else if(object instanceof String){
				array=new String[]{
					(String)object
				};
			}else if(object instanceof String[]){
				array=(String[])object;
			}else if(object instanceof Object[]){
				array=(Object[])object;
			}else if(object instanceof Object){
				array=new Object[]{
					object
				};
			}else{
				array=new Object[0];
			}
			result[i++]=array;
		}
		return result;
	}

	public static Boolean toBoolean(Object value){
		if(value==null || value.toString().length()==0)
			return null;
		else
			return isTrue(value);
	}
	
	public static <T>T trim(T obj) throws Exception {
		if(obj instanceof Map)
			return (T)esdk.map.trim((Map)obj);
		else {
			for(Method m:obj.getClass().getMethods()) {
				if(m.getName().startsWith("get")&&m.getReturnType().equals(String.class)&&m.getParameterTypes().length==0) {
					Method setM=EasyReflect.findSetterMethod(obj.getClass(),m.getName().substring(3));
					if(setM!=null) {
						String v=(String)m.invoke(obj,new Object[] {});
						if(v!=null&&!v.trim().equals(v))
						setM.invoke(obj,v.trim());
					}
				}
			}
			return obj;
		}
	}
}