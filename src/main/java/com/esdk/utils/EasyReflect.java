package com.esdk.utils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;

import cn.hutool.core.util.ReflectUtil;

/**
 * 扩展Apache Commons BeanUtils, 提供一些反射方面缺失功能的封装.
 */
public class EasyReflect{
	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * @throws NoSuchFieldException 如果没有该Field时抛出.
	 */
	public static Field getDeclaredField(Object object,String propertyName) throws NoSuchFieldException{
		return getDeclaredField(object.getClass(),propertyName);
	}

	/**
	 * 循环向上转型,获取对象的DeclaredField.
	 * @throws NoSuchFieldException 如果没有该Field时抛出.
	 */
	public static Field getDeclaredField(Class clazz,String propertyName) throws NoSuchFieldException{
		for(Class superClass=clazz;superClass!=Object.class;superClass=superClass.getSuperclass()){
			try{
				return superClass.getDeclaredField(propertyName);
			}
			catch(NoSuchFieldException e){
				// Field不在当前类定义,继续向上转型
			}
		}
		throw new NoSuchFieldException("No such field: "+clazz.getName()+'.'+propertyName);
	}

	public static Object getFieldValue(Object instance,String fieldName) {
		if(instance==null)return null;
		try{
			return instance.getClass().getField(fieldName).get(instance);
		}
		catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public static Object getFieldValue(Object instance,String fieldName,boolean ignoreCase) {
		if(instance==null)
			return null;
		if(!ignoreCase)
			return getFieldValue(instance,fieldName);
		else {
			Field f=findField(instance.getClass(),fieldName,ignoreCase);
			if(f!=null){
				try{
					return f.get(instance);
				}catch(Exception e){
					throw new RuntimeException(e);
				}
			}
			return null;
		}
	}

	public static void setFieldValue(Object instance,String fieldName,Object fieldValue) {
		ReflectUtil.setFieldValue(instance,fieldName,fieldValue);
	}
	
	public static Object getFieldValue(Object obj,Field field){
		try{
			return field.get(obj);
		}catch(IllegalArgumentException|IllegalAccessException e){
			throw new SdkRuntimeException(e);
		}
	}
	
	public static Object[] getFieldValues(String[] fieldNames,Object... objs){
		if(objs==null||EasyObj.isBlank(fieldNames))
			return null;
		ArrayList list=new ArrayList(fieldNames.length);
			try{
				for(int i=0;i<fieldNames.length;i++) {
					String fieldname=fieldNames[i];
					for(int j=0;j<objs.length;j++){
						Object obj=objs[j];
						Field field=findField(obj.getClass(),fieldname,true);
						if(field!=null) {
							list.add(field.get(obj));
							break;
						}
					}
				}
			}
			catch(IllegalArgumentException e){
				throw new RuntimeException(e);
			}
			catch(IllegalAccessException e){
				throw new RuntimeException(e);
			}
			return list.toArray();
	}
	
	public static ArrayList getGetMethodValues(String propertyName,Object... objs) {
		ArrayList result=new ArrayList(objs.length); 
		for(int i=0;i<objs.length;i++) {
			result.add(getGetMethodValue(objs[i],propertyName));
		}
		return result;
	}
	
	public static ArrayList getGetMethodValues(String propertyName,List list) {
		return getGetMethodValues(propertyName,list.toArray());
	}
	
	/**
	 * 暴力获取对象变量值,忽略private,protected修饰符的限制.
	 * @throws NoSuchFieldException 如果没有该Field时抛出.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Object getDeclaredFieldValue(Object object,String propertyName){
		try{
			Field field=getDeclaredField(object,propertyName);
			return getDeclaredFieldValue(object,field);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object getDeclaredFieldValue(Object object,Field field){
		boolean accessible=field.isAccessible();
		field.setAccessible(true);
		Object result=null;
		result=getFieldValue(object,field);
		field.setAccessible(accessible);
		return result;
	}
		

	/**
	 * 暴力设置对象变量值,忽略private,protected修饰符的限制.
	 * @throws NoSuchFieldException 如果没有该Field时抛出.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void forceSetProperty(Object object,String propertyName,Object newValue) throws NoSuchFieldException,IllegalArgumentException,IllegalAccessException{
		Field field=getDeclaredField(object,propertyName);
		boolean accessible=field.isAccessible();
		field.setAccessible(true);
		field.set(object,newValue);
		field.setAccessible(accessible);
	}

	/**
	 * 暴力调用对象函数,忽略private,protected修饰符的限制.
	 * @throws NoSuchMethodException 如果没有该Method时抛出.
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static Object invokePrivateMethod(Object object,String methodName,Object...params) throws NoSuchMethodException,IllegalArgumentException,IllegalAccessException,InvocationTargetException{
		Class[] types=new Class[params.length];
		for(int i=0;i<params.length;i++){
			types[i]=params[i].getClass();
		}
		Class clazz=object.getClass();
		Method method=null;
		Class superClass=clazz;
		while(superClass!=Object.class){
			method=superClass.getDeclaredMethod(methodName,types);
			if(method!=null)
				break;
			else
				superClass=superClass.getSuperclass();
		}
		if(method==null){
			throw new NoSuchMethodException("No Such Method:"+clazz.getSimpleName()+methodName);
		}
		boolean accessible=method.isAccessible();
		method.setAccessible(true);
		Object result=null;
		result=method.invoke(object,params);
		method.setAccessible(accessible);
		return result;
	}

	/**
	 * 按Filed的类型取得Field列表.
	 */
	public static List<Field> getFieldsByType(Object object,Class type){
		List<Field> list=new ArrayList<Field>();
		Field[] fields=object.getClass().getDeclaredFields();
		for(Field field:fields){
			if(field.getType().isAssignableFrom(type)){
				list.add(field);
			}
		}
		return list;
	}

	/**
	 * 按FiledName获得Field的类型.
	 */
	public static Class getPropertyType(Class type,String name) throws NoSuchFieldException{
		return getDeclaredField(type,name).getType();
	}

	/**
	 * 获得field的getter函数名称.
	 */
	public static String getGetterName(Class type,String fieldName){
		if(type.getName().equals("boolean")){
			return "is"+esdk.str.upperFirst(fieldName);
		}
		else{
			return "get"+esdk.str.upperFirst(fieldName);
		}
	}
	
	public static String getFieldName(Method method,boolean usePropertyName,boolean useUnderscoreName) {
		String methodname=method.getName();
		String fieldname;
		if(methodname.startsWith("get"))
			fieldname=methodname.substring(3);
		else if(methodname.startsWith("is"))
			fieldname=methodname.substring(2);
		else
			fieldname=methodname;
		if(usePropertyName)
			fieldname=esdk.str.toCamelCase(fieldname);
		else if(useUnderscoreName)
			fieldname=esdk.str.toUnderlineCase(fieldname);
		return fieldname;
	}

	/**
	 * 获得field的getter函数,如果找不到该方法,返回null.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static Method getGetterMethod(Class type,String fieldName) throws SecurityException,NoSuchMethodException{
		return type.getMethod(getGetterName(type,fieldName));
	}
	public static Object getGetMethodValue(Object object,String propertyName){
		Method m=findGetterMethod(object.getClass(),propertyName);
		try{
			return m.invoke(object,new Object[]{});
		}catch(Exception e){
			throw new SdkRuntimeException("can not find the getter method name:"+propertyName);
		}
	}
	
  public static Object getPropertyValue(Object object,String propertyName){
  	if(object==null)
  		return null;
  	if(object instanceof Map)
  		return ((Map)object).get(propertyName);
  	else
  		return getGetMethodValue(object,propertyName);
  }
  
	public static boolean hasGetterMethod(Class cls,String propertyName) {
		return findGetterMethod(cls,propertyName)!=null;
	}
	
	public static Field findField(Class cls,String fieldName,boolean isIgnoreCase) {
		Field[] fields=cls.getFields();
		for(int i=0;i<fields.length;i++){
			if(isIgnoreCase&&fields[i].getName().equalsIgnoreCase(fieldName))
				return fields[i];
			else if(fields[i].getName().equals(fieldName))
				return fields[i];
		}
		return null;
	}

	public static boolean hasSetterMethod(Class cls,String propertyName) {
		return findSetterMethod(cls,propertyName)!=null;
	}
	
	public static boolean copyBeanProperty(Object from,Object to,String...excludes) {
		return copyBeanProperty(from,to,null,false,excludes);
	}
	
	public static boolean copyBeanProperty(Object from,Object to,Map map,String...excludes) {
		return copyBeanProperty(from,to,map,false,excludes);
	}
	
	public static boolean copyBeanProperty(Object from,Object to,boolean acceptNull,String...excludes) {
		return copyBeanProperty(from,to,null,acceptNull,excludes);
	}
	
	private static Map convertToBeanProperty(Map<String,String> map) {
		if(map==null)return map;
		HashMap result=new HashMap(map.size());
		for(Iterator iter=map.entrySet().iterator();iter.hasNext();) {
			Entry<String,String> entry=(Entry)iter.next();
			String key=EasyStr.toCamelCase(entry.getKey(),true);
			String value=EasyStr.toCamelCase(entry.getValue(),true);
			result.put(key,value);
		}
		return result;
	}
	
	/**默认忽略字段值为null的复制*/
	public static <T> T copyBeanProperties(Object from,T to,String...excludes) {
		return copyBeanProperties(from,to,false,true,excludes);
	}
	
	public static <T> T copyBeanProperties(Object from,T to,boolean acceptNull,boolean ignoreCase,String...excludes){
		return copyBeanProperties(from,to,acceptNull,ignoreCase,null,excludes);
	}
	/**
	 * 这个方法是最新的,推荐优先使用这个,其他类似的copyProperty方法会在以后删除.
	 * @param from
	 * @param to
	 * @param acceptNull
	 * @param excludes
	 * @return
	 */
	public static <T> T copyBeanProperties(Object from,T to,boolean acceptNull,boolean ignoreCase,
			Map propertyNameMap,String...excludes){
		if(from==null||to==null)
			return null;
		List<Method[]> list=findMethodsInterSection(from,to,ignoreCase,propertyNameMap,excludes);
		try{
			for(Iterator iter=list.iterator();iter.hasNext();){
				Method[] methods=(Method[])iter.next();
				Method getMethod=methods[0];
				Method setMethod=methods[1];
				if(setMethod.getParameterTypes().length==1){
					Object getValue=getMethod.invoke(from,new Object[]{});
					if(acceptNull||getValue!=null){
						setMethod.invoke(to,new Object[]{getValue});
					}
				}
			}
			return to;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}
	
	public static <T> T clearBlankProperty(T obj) {
		if(obj!=null) {
			Method[] m=obj.getClass().getMethods();
			for(int i=0;i<m.length;i++) {
				if(validGetMethod(m[i])){
					try{
						Object v=m[i].invoke(obj,new Object[] {});
						if(v!=null&&v instanceof String&&v.equals("")) {
							String propertyname=m[i].getName().startsWith("get")?m[i].getName().substring(3):m[i].getName().substring(2);
							Method setter=findSetterMethod(obj.getClass(),propertyname);
							if(setter!=null)
								setter.invoke(obj,new Object[] {null});
						}
					}catch(Exception e){
						throw new SdkRuntimeException(e);
					}
				}
			}
		}
		return obj;
	}
		
	public static List<Method[]> findMethodsInterSection(Object from,Object to,boolean ignoreCase,Map<String,String> fromPropertyNameMap,String...excludes) {
		if(from==null||to==null)
			return new ArrayList();
		else {
			List<Method[]> result=new LinkedList<Method[]>();
			HashMap<String,Method> fromMap=new HashMap();
			Method[] fromMethods=from.getClass().getMethods();
			for(int i=0;i<fromMethods.length;i++) {
				if(validGetMethod(fromMethods[i],excludes)) {
					String getPropertyName=fromMethods[i].getName().startsWith("get")?fromMethods[i].getName().substring(3):fromMethods[i].getName().substring(2); //get|is
					fromPropertyNameMap=convertToBeanProperty(fromPropertyNameMap);
					if(fromPropertyNameMap!=null&&fromPropertyNameMap.get(getPropertyName)!=null)
						getPropertyName=fromPropertyNameMap.get(getPropertyName);
					if(ignoreCase)
						getPropertyName=getPropertyName.toUpperCase();
					fromMap.put(getPropertyName,fromMethods[i]);
				}
			}
			Method[] toMethods=to.getClass().getMethods();
			for(int i=0;i<toMethods.length;i++) {
				if(validSetMethod(toMethods[i],excludes)) {
					String setPropertyName=ignoreCase?toMethods[i].getName().substring(3).toUpperCase():toMethods[i].getName().substring(3);
					Method getMethod=fromMap.get(setPropertyName);
					if(getMethod!=null)
						result.add(new Method[] {getMethod,toMethods[i]});
				}
			}
			return result;
		}
	}
	
  /**TODO 需要优化性能*/
	private static boolean validGetMethod(Method method,String... excludes) {
		excludes=esdk.str.toArray(excludes,"class");
		return method.getName().matches("(get|is).+")&&method.getParameterTypes().length==0
		&&!EasyStr.existOf(excludes,method.getName().substring(3),true);
	}
	
	private static boolean validSetMethod(Method method,String... excludes) {
		excludes=EasyStr.toArray(excludes,"class");
		return method.getName().matches("set.+")&&!EasyStr.existOf(excludes,method.getName().substring(3),true);
	}

	/**
	 * suggest replace with copyBeanProperties
	 * @param from
	 * @param to
	 * @param map
	 * @param acceptNull
	 * @param excludes
	 * @return
	 */
	@Deprecated 
	public static boolean copyBeanProperty(Object from,Object to,Map<String,String> map,boolean acceptNull,String...excludes) {
		boolean result=false;
		if(from!=null&&to!=null&&from!=to) {
			Method[] methods=from.getClass().getMethods();
			if(excludes==null)
				excludes=new String[] {"class"};
			else
				excludes=new CharAppender(',').append(excludes).append("class").toString().split(",");
				try{
					map=convertToBeanProperty(map);
					for(int i=0;i<methods.length;i++){
						Pattern p=Pattern.compile("(get|is)(.+)",Pattern.CASE_INSENSITIVE);
						Matcher m=p.matcher(methods[i].getName());
						if(m.matches()){
							String beanPropertyName=m.group(2);
							if(map!=null&&map.get(beanPropertyName)!=null)
								beanPropertyName=map.get(beanPropertyName);
							if(!EasyStr.existOf(excludes,beanPropertyName,true)&&methods[i].getParameterTypes().length==0) {
								Object getValue=methods[i].invoke(from,new Object[] {});
								if(acceptNull||getValue!=null) {
									//System.out.println(beanPropertyName);
									Method toMethod=findSetterMethod(to.getClass(),beanPropertyName);
									if(toMethod!=null)
										toMethod.invoke(to,new Object[] {getValue});
								}
							}
						}
					}
					result=true;
				}catch(IllegalArgumentException e){
					e.printStackTrace();
				}catch(IllegalAccessException e){
					e.printStackTrace();
				}catch(InvocationTargetException e){
					e.printStackTrace();
				}
		}
		return result;
	}
	
  public static boolean hasPropertyName(Class cls,String fieldName) {
		try{
	  	cls.getDeclaredField(fieldName);
	  	return true;
		}catch(SecurityException e){
			throw new RuntimeException(e);
		}catch(NoSuchFieldException e){
			return false;
		}
  }
  
	private static HashMap<String,Class> _ClassMap=new HashMap();
	public static Class findClass(String... clsNames){
		Class result=null;
		for(String clsName:clsNames) {
			result=_ClassMap.get(clsName);
			if(result!=null) {
				if(!result.getClassLoader().equals(Thread.currentThread().getContextClassLoader().getParent())) {
					_ClassMap.clear();
					result=null;
				}
			}
			try{
				if(result==null) {
					result=Thread.currentThread().getContextClassLoader().loadClass(clsName);
					_ClassMap.put(clsName,result);
				}
				if(result!=null)
					break;
			}catch(ClassNotFoundException e){
				continue;
			}
		}
		return result;
	}
	
	public static <C> C safeNewInstance(Class<C> cls){
		return ReflectUtil.newInstance(cls);
	}
	
	public static <C> C safeNewInstance(String clsName){
		Class cls=findClass(clsName);
		if(cls==null)
			return null;
		else
			try{
				return (C)cls.newInstance();
			}catch(Exception e){
				throw new SdkRuntimeException(e);
			}
	}
	
	
	public static void setSetterMethodValue(Object obj,String propertyName,Object value){
		if(obj!=null){
			Method m=findSetterMethod(obj.getClass(),propertyName);
			if(m==null)
				throw new SdkRuntimeException("can not find the setter method name:"+propertyName);
			setSetterMethodValue(obj,m,value);
		}
	}
	
  public static void setSetterMethodValue(Object obj,Method m,Object value) {
  	if(obj!=null&&m!=null) {
			try{
				m.invoke(obj,new Object[]{value});
			}catch(Exception e){
				throw new SdkRuntimeException(e);
			}
  	}
  }

	
	private static HashMap<String,Method> setterMethodPool=new HashMap<String,Method>();

	/**注意：会自动把propertyName的下划线方式转为驼峰方式*/
	public static Method findSetterMethod(Class cls,String propertyName) {
		Method result=null;
		propertyName=esdk.str.upperFirst(propertyName);
		String key=cls.getName()+".set"+propertyName;
		if(setterMethodPool.containsKey(key)) {
			result=setterMethodPool.get(key);
			if(result!=null&&!result.getDeclaringClass().getClassLoader().equals(Thread.currentThread().getContextClassLoader().getParent())) {
				setterMethodPool.clear();
				result=null;
			}else
				return result;
		}
		Method[] methods=cls.getMethods();
		Method suspectMethod=null;
		for(Method method:methods){
			if(method.getName().matches("set"+propertyName)) {
				if(method.getParameterTypes().length==1 && !method.getParameterTypes()[0].isArray()) {
					result=method;
					break;
				}
				else
					suspectMethod=method;
			}
		}
		if(result==null&&suspectMethod!=null)
			result=suspectMethod;
		if(setterMethodPool.size()>1000) {
			//setterMethodPool.remove(getterMethodPool.keySet().iterator().next());
			setterMethodPool.clear();
		}
		setterMethodPool.put(key,result);
		return result;
	}
	
	private static HashMap<String,Method> getterMethodPool=new HashMap<String,Method>();
	public static Method findGetterMethod(Class cls,String propertyName) {
		Method result=null;
		propertyName=esdk.str.upperFirst(propertyName);
		String key=cls.getName()+".get"+propertyName;
		if(getterMethodPool.containsKey(key)) {
			result=getterMethodPool.get(key);
			if(result!=null&&!result.getDeclaringClass().getClassLoader().equals(Thread.currentThread().getContextClassLoader().getParent())) {
				getterMethodPool.clear();
				result=null;
			}else
				return result;
		}
		Method[] methods=cls.getMethods();
		for(int i=0;i<methods.length;i++){
			if(methods[i].getParameterTypes().length==0 && methods[i].getName().matches("get"+propertyName)) {
				result=methods[i];
				break;
			}
		}
		if(getterMethodPool.size()>1000) {
			//getterMethodPool.remove(getterMethodPool.keySet().iterator().next());
			getterMethodPool.clear();
		}
		getterMethodPool.put(key,result);
		return result;
	}
	
	/**mapToObject*/
	public static <T> T mapToObj(Map<String,Object> map,T bean) {
		if(bean==null||map==null)
			return bean;
		try{
			for(Iterator<Entry<String,Object>> item=map.entrySet().iterator();item.hasNext();){
				Entry<String,Object> entry=item.next();
				if(entry.getValue()!=null){
					Method mtd=findSetterMethod(bean.getClass(),entry.getKey());
					if(mtd!=null){
						if(mtd.getParameterTypes()[0].equals(entry.getValue().getClass()))
							mtd.invoke(bean,entry.getValue());
						else {
							mtd.invoke(bean,esdk.obj.valueOf(mtd.getParameterTypes()[0],entry.getValue()));
						}
					}
				}
			}
			return bean;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}
	
	/**beanToMap*/
	public static Map toMap(Object bean) {
		return toMap(bean,false,true);
	}
	
	/**beanToMap
	 * @param ignoreNull: 忽略null
	 * @param usePropertyName: 强制驼峰命名key
	 * */
	public static Map toMap(Object bean,boolean ignoreNull,boolean usePropertyName){
		Method[] methods=bean.getClass().getMethods();
		Map result=new LinkedHashMap(methods.length/2);
		try{
			for(int i=0;i<methods.length;i++){
				String methodname=methods[i].getName();
				if(methodname.matches("(get|is).*")&&!methodname.equals("getClass")){
					Object value=methods[i].invoke(bean,new Object[]{});
					if(value!=null||ignoreNull==false)
						result.put(getFieldName(methods[i],usePropertyName,false),value);
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static Map toMap(Object bean,boolean ignoreNull,boolean usePropertyName,boolean useUnderscoreName){
		Method[] methods=bean.getClass().getMethods();
		Map result=new LinkedHashMap(methods.length/2);
		try{
			for(int i=0;i<methods.length;i++){
				String methodname=methods[i].getName();
				if(methodname.matches("(get|is).*")&&!methodname.equals("getClass")){
					Object value=methods[i].invoke(bean,new Object[]{});
					if(value!=null||ignoreNull==false)
						result.put(getFieldName(methods[i],usePropertyName,useUnderscoreName),value);
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return result;
	}
	
	/**
	 * 引用链接：https://www.jianshu.com/p/85a44ee21af8
   * 根据泛型类的已经声明泛型类型的子类获取指定位置的泛型
   * @author 北北
   * @date 2018年1月18日上午11:34:59
   * @param clazz -- 当前类
   * @param index -- 第几个泛型, 从0开始
   * @return
   */
	public static <T> Class<T> getGenericClass(Class<T> clazz,int index){
		Class<T> result=null;
		if(clazz==null)
			return null;
		// 提取泛型类数组
		Object superclass=clazz.getGenericSuperclass();
		if(superclass instanceof ParameterizedType) {
		Type[] genericTypes=((ParameterizedType)superclass).getActualTypeArguments();
		// 越界判断
		if((index+1)>genericTypes.length){
			return null;
		}
		Object obj=genericTypes[index];
		if(obj!=null && obj instanceof Class)
			result=(Class<T>)obj;
		else if(obj.getClass().getName().equals("TypeVariableImpl") || obj.toString()=="T") //即没有找到Class
			result=null;
		}
		return result;
	}

}
