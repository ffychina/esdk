package com.esdk.utils;

/**
 * @author franky.fan
 */
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.esdk.esdk;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;

public class EasyArray{
  public final static String[] EmptyStrArr=Constant.EmptyStrArr;
  public final static Long[] EmptyLongArr=Constant.EmptyLongArr;
  public final static Integer[] EmptyIntArr=Constant.EmptyIntArr;

  
	public <T>T first(T[] array){
		if(array==null||array.length==0)
			return null;
		return array[0];
	}

	public static <T extends Comparable>T[] sort(T[] obj){
		return sort(obj,false);
	}

	public static <T extends Comparable>T[] sort(T[] obj,boolean desc){
		if(!desc){
			for(int i=obj.length-1;i>0;i--){
				for(int j=0;j<i;j++){
					if(obj[j].compareTo(obj[j+1])>0){ // >0
						T temp=obj[j+1];
						obj[j+1]=obj[j];
						obj[j]=temp;
					}
				}
			}
		}else{
			for(int i=obj.length-1;i>0;i--){
				for(int j=0;j<i;j++){
					if(obj[j].compareTo(obj[j+1])<0){ // <0
						T temp=obj[j+1];
						obj[j+1]=obj[j];
						obj[j]=temp;
					}
				}
			}
		}
		return obj;
	}
	
  public static List unique(Collection collection) {
  	ArrayList result=new ArrayList();
  	for(Iterator iter=collection.iterator();iter.hasNext();) {
  		Object item=iter.next();
  		if(!result.contains(item))
  			result.add(item);
  	}
  	return result;
  }
  
	public static <T>T[] unique(T... array){
		List<T> result=toList(array);
		for(int i=0;i<result.size();i++) {
			T item=result.get(i);
			int index=result.lastIndexOf(item);
			if(index>=0&&index!=i) {
				result.remove(index);
				i--;
			}
		}
		return toArray(result,getComponentType(array,result));
	}
  
  public static List distinct(List list) {
  	ArrayList result=new ArrayList();
  	for(int i=0;i<list.size();i++) {
  		if(!result.contains(list.get(i)))
  			result.add(list.get(i));
  	}
  	return result;
  }
  
  public static <T>T[] distinct(T... array) {
  	return unique(array);
  }
  
  public static String toStr(Object[] array) {
  	return toStr(array,",");
  }
  
  public static String toStr(Object[] array,String delimter) {
  	StringBuilder result=new StringBuilder();
  	for(Object obj:array) {
  		if(obj!=null) {
  			result.append(result.length()>0?delimter:"").append(obj);
  		}
  	}
  	return result.toString();
  }
	
	public static <T>T[] remove(T[] array,T... removeItems){
		if(array==null||array.length==0)
			return array;
		List<T> result=toList(array);
		result.removeAll(toList(removeItems));
		return result.toArray((T[])Array.newInstance(getComponentType(array),result.size()));
	}

	/**
	 * 获取子数组
	 * */
	public static <T>T[] subArray(T[] array,int begin,int end){
		T[] result=(T[])Array.newInstance(getComponentType(array),end-begin);
		System.arraycopy(array,begin,result,0,result.length);
		return result;
	}
	
	/**默认最大值为数组长度*/
	public static <T>T[] subArray(T[] array,int begin){
		return subArray(array,begin,array.length);
	}
	
	/**取最多limit长度的子数组*/
	public static <T>T[] topArray(T[] array,int limit){
		return subArray(array,0,limit);
	}
	
	public static <T> T[] filter(T[] array,Predicate<T> fn) {
		return filter(array,0,fn);
	}
	
	public static <T> T filterFirst(T[] array,Predicate<T> fn) {
		T[] result=filter(array,1,fn);
		return result.length==0?null:result[0];
	}
	
	public static <T> T[] filter(T[] array,int top, Predicate<T> fn) {
		ArrayList<T> subList=new ArrayList<T>();
		if(top<=0)
			top=array.length;
		for(T item:array) {
			if(fn.test(item)) {
				if(top-->0)
					subList.add(item);
			}
		}
		T[] result=toArray(subList,getComponentType(array,subList));
		return result;
	}
	
	public static <T>T[] remove(T[] array,T removeItems){
		if(array==null||array.length==0)
			return array;
		List<T> result=toList(array);
		result.remove(removeItems);
		return result.toArray((T[])Array.newInstance(getComponentType(array),result.size()));
	}
  
	private static <T> Class getComponentType(T[] array) {
		Class cls=array.getClass().getComponentType();
		while(cls.isArray()&&cls.getComponentType()!=null)
			cls=cls.getComponentType();
		return cls;
	}

	private static <T> Class getComponentType(List<T> list) {
		Class cls=list.isEmpty()?Object.class:list.get(0).getClass();
		return cls;
	}

	private static <T> Class getComponentType(T[] args,List<T> list) {
		Class cls=getComponentType(list);
		if(cls.equals(Object.class))
			cls=getComponentType(args);
		return cls;
	}
	
	/**Don't support int[],double[]...*/
	public static <T> ArrayList<T> toList(T...args){
		ArrayList<T> result=new ArrayList(args.length*4);
		for(int i=0;i<args.length;i++){
			if(args[i]==null)
				continue;
			if(args[i]!=null&&args[i].getClass().isArray())
				result.addAll(Arrays.asList((T[])args[i]));
			else if(args[i] instanceof Collection)
				result.addAll((Collection)args[i]);
			else 
				result.add((T)args[i]);
		}
		return result;
	}
	
	/**把数组或对象合并成数组返回，如果全部参数都是数组，应使用concat()*/
	public static <T>T[] concat(T...args){
		ArrayList<T> result=toList(args);
		return result.toArray((T[])Array.newInstance(getComponentType(args,result),result.size()));
	}
	
	/**强制指定输出的数组类型，把数组或对象合并成数组返回，如果全部参数都是数组，应使用concat()*/
	public static <T>T[] concat(Class<T> cls,Object[] args){
		ArrayList result=null;
		if(!esdk.array.isBlank(args)) { 
			result=toList(args);
			return (T[])result.toArray((T[])Array.newInstance(cls,result.size()));
		}else
			return (T[])Array.newInstance(cls,0);
	}
	
	/**拼接多个数组，返回合并后的数组，不作用重处理，在方法命名上把join区别*/
	public static <T>T[] concat(T[]... arrays){
		ArrayList<T> result=toList((T[])arrays);
		return result.toArray((T[])Array.newInstance(getComponentType((T[])arrays,result),result.size()));
	}
	
	/**拼接多个数组，返回合并后的数组*/
	public static <T>T[] append(T[] array, T...appends){
		ArrayList result=new ArrayList(array.length+appends.length);
		result.addAll(Arrays.asList(array,appends));
		return (T[])result.toArray((T[])Array.newInstance(getComponentType(array,result),result.size()));
	}
	
	/**List不能为空，必须要拿到第一个元素的class*/
	public static <T>T[] toArray(Collection<T> t){
		if(t==null)
			return null;
	  if(t.isEmpty())
			return (T[])new Object[0];//TODO应该会出异常，但目前没有找到更好的办法
		Class cls=t.iterator().next().getClass();
		return t.toArray((T[])Array.newInstance(cls,t.size()));
	}
	
	public static <T>T[] toArray(Collection<T> t,Class cls){
		if(t==null)
			return null; 
		return t.toArray((T[])Array.newInstance(cls,t.size()));
	}
	
	public Long[] toLongArray(String val) {
		return esdk.math.toLongArray(val);
	}
	
	public Double[] toDoubleArray(String val) {
		return esdk.math.toDoubleArray(val);
	}
	
	public Integer[] toIntArray(String val) {
		return esdk.math.toIntArray(val);
	}
	
	/**删除list实例的null元素*/
	public List<?> removeNull(AbstractList<?> list) {
		for(int i=list.size()-1;i>=0;i--){
			if(list.get(i)==null)
				list.remove(i);
		}
		return list;
	}
	
	public static boolean equals(Object[] a,Object[] b) {
		return Arrays.equals(a,b);
	}

	public static boolean contains(Object[] array,Object[] objs){
		for(int i=0;i<objs.length;i++){
			if(ArrayUtil.contains(array,objs[i]))
				return true;
		}
		return false;
	}

	
	/**得到两个集合的交集*/
	public static String[] overlap(String[] c1,String[] c2){
		return (String[])overlap(Arrays.asList(c1),Arrays.asList(c2)).toArray(new String[0]);
	}
	
	/**得到两个集合的并集*/
	public static <C extends Collection> Set merge(C c1,C c2){
		LinkedHashSet result=new LinkedHashSet(c1);
		result.addAll(c2);
		return result;
	}
	
	/**得到两个集合的并集*/
	public static  <C> C[] merge(C[] c1,C[] c2){
		return (C[])toArray(merge(Arrays.asList(c1),Arrays.asList(c2)));
	}

	public static Integer[] distinct(Integer[] array){
		LinkedHashSet result=new LinkedHashSet();
		for(int i=0;i<array.length;i++){
			result.add(array[i]);
		}
		return (Integer[])result.toArray(new Integer[0]);
	}

	public static Long[] distinct(Long[] array){
		LinkedHashSet result=new LinkedHashSet();
		for(int i=0;i<array.length;i++){
			result.add(array[i]);
		}
		return (Long[])result.toArray(new Long[0]);
	}

  /**array1包含array2任意一个数，返回true*/
	public static <C> boolean existOr(C[] array1,C[] array2){
		if(ArrayUtil.isEmpty(array2)||ArrayUtil.isEmpty(array1))
			return false;
		for(int i=0;i<array1.length;i++){
			for(int j=0;j<array2.length;j++){
				if(array1[i]==array2[j])
					return true;
			}
		}
		return false;
	}
  
	 /**array1包含array2所有数，返回true*/
	public static <C> boolean existAnd(C[] array1,C[] array2){
		if(ArrayUtil.isEmpty(array2)||ArrayUtil.isEmpty(array1))
			return false;
		else
			return overlap(array1,array2).length==array2.length;
	}
	
	/**得到两个集合的交集*/
	public static Collection overlap(Collection c1,Collection c2){
		LinkedHashSet result=new LinkedHashSet();
		if(c1==c2)
			return c1;
		else if(c1==null||c1.size()==0||c2==null||c2.size()==0)
			return result;
		for(Iterator iter=c1.iterator();iter.hasNext();){
			Object o1=iter.next();
			for(Iterator iterator=c2.iterator();iterator.hasNext();){
				Object o2=iterator.next();
				if(ObjectUtil.equal(o1,o2)){
					result.add(o2);
				}
			}
		}
		return result;
	}
	
	/**得到两个集合的交集*/
	public static <C> C[] overlap(C[] c1,C[] c2){
		Collection c3=overlap(Arrays.asList(c1),Arrays.asList(c2));
		Class cls=null;
		if(c1!=null&&c1.length>0)
			cls=c1[0].getClass();
		else if(c2!=null&&c2.length>0)
			cls=c2[0].getClass();
		if(cls==null)
			return null;
		else
			return (C[])esdk.array.toArray(c3,cls);
	}

	public static int indexOf(Object[] array,Object obj){
		return ArrayUtil.indexOf(array,obj);
	}

	public static boolean contains(Object[] array,Object obj){
		return ArrayUtil.contains(array,obj);
	}

	public static int indexOf(int[] array,int obj){
		return ArrayUtil.indexOf(array,obj);
	}

	public static boolean contains(int[] array,int obj){
		return ArrayUtil.indexOf(array,obj)>0;
	}

	public static boolean contains(String[] array,String[] objs){
		for(int i=0;i<objs.length;i++){
			if(indexOf(array,objs[i])<0)
				return false;
		}
		return true;
	}
	
	public static <T> String toString(T[] array) {
		return EasyStr.valueOf(array);
	}
	
	public static boolean isEmpty(Object[] arr) {
		return ArrayUtil.isEmpty(arr);
	}

	public static boolean isValid(Object[] arr) {
		return !ArrayUtil.isEmpty(arr);
	}

	public boolean isBlank(Object[] arr){
		return arr==null || arr.length==0;
	}

	public static void main(String[] args){
		esdk.tool.assertEquals(esdk.str.arrToStr(concat(new String[] {})),"");
		Integer[] n1=new Integer[] {1,2,3};
		esdk.tool.assertEquals(esdk.str.valueOf(concat(n1,4,5)),"1,2,3,4,5");
		String[] s1=new String[] {"a","b"};
//		Integer[] b=new Integer[] {1,2};
		String[] s2=new String[] {"c","d"};
//		Tools.assertEquals(concat(a,b).length,4);	
		esdk.tool.assertEquals(esdk.str.valueOf(concat(s1,s2,"e","f","g")),esdk.str.valueOf(distinct("a,a,b,c,d,e,f,g".split(","))));
		esdk.tool.assertEquals(esdk.str.valueOf(remove(s1,"a","b".split(","))),"");
		esdk.tool.assertEquals(esdk.str.valueOf(unique(s1,s2,"a","e","f,g".split(","))),"a,b,c,d,e,f,g");
		Object[] ns=concat(Object.class,n1,s1);
		esdk.tool.assertEquals(esdk.json.toJSON(ns).toString(),"[1,2,3,\"a\",\"b\"]");
		esdk.tool.assertEquals(EasyStr.valueOf(merge(new String[] {"a","c","b"},new String[] {"c","d","a"})),"a,c,b,d");
		String[] arr={"a","b","c","d"};
		esdk.tool.assertEquals(EasyStr.valueOf(subArray(arr,1,4)),"b,c,d");
		String[] s3= {"valid","createTime","createUserId","createUserName"};
		esdk.tool.assertEquals(esdk.str.valueOf(filter(s3,2,e->!e.endsWith("Id"))),"valid,createTime");
	}

}
