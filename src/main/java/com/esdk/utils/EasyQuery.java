/**
 * Simuate jquery feature, the function name just like jquery.
 * 对List<T>和数组进行筛选,排序等常用处理的工具类,方便对Hibernate或ibatis的数据集进行二次处理.	
 * 
 * @author fanfy
 */
package com.esdk.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.regex.Matcher;

import com.esdk.esdk;
import com.esdk.sql.Expression;
import com.esdk.sql.Expressions;
import com.esdk.sql.IExpression;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.IRow;

public class EasyQuery<T> implements Cloneable,Iterable<T>{
	private List<T> items=new ArrayList();
	
	public static <T> EasyQuery<T> instanceOfCopy(List<T> list) {
		return new EasyQuery<T>((Collection)list);
	}
	
	public static <T> EasyQuery<T> instanceOfShare(List<T> list) {
		return new EasyQuery<T>(list);
	}
	
	public EasyQuery(){
	}
	
	public EasyQuery(String json,Class c) {
		this.items.addAll(EasyObj.toList(json,c));
	}
	
	public EasyQuery(List<T> list){
		if(list!=null)
			items=list;
	}
	
	public EasyQuery(Collection<T> c){
		items.addAll(c);
	}
	public EasyQuery(T[] array){
		items.addAll(Arrays.asList(array));
	}
	
	public T first() {
		if(items.size()>0)
			return items.get(0);
		else
			return null;
	}
	
	public T last() {
		if(items.size()>0)
			return items.get(items.size()-1);
		else
			return null;
	}

	/**使用lambda表达式过滤*/
	public EasyQuery<T> remove(Predicate<T> fn){
		this.items.removeAll(filter(0,fn).items);
		return this;
	}
	
	/**使用lambda表达式过滤，获取第一条记录返回，找不到返回null*/
	public T filterFirst(Predicate<T> fn){
		EasyQuery<T> result=filter(1,fn);
		return result.first();
	}
	
	/**使用lambda表达式过滤*/
	public EasyQuery<T> filter(Predicate<T> fn){
		return filter(0,fn);
	}
	
	/**使用lambda表达式过滤*/
	public EasyQuery<T> filter(int top,Predicate<T> fn){
		List<T> result=new ArrayList();
		if(top<=0)
			top=Integer.MAX_VALUE;
		for(T row:this.items) {
			if(fn.test(row)) {
				if(top-->0)
					result.add(row);
			}
		}
		return new EasyQuery(result);
	}
	
	/**
	 * @param expr like jquery, need extend and refactor the expression in future.
	 * notice: for the safe reason, filter won't remvoe items from original list, it will create 
	 * new instance of EasyQuery for the result of filter, so make sure replace the list by toList();
	 * eg: list=new EasyQuery(list).filter('name=mike').toList();
	 * @return
	 */
	public EasyQuery<T> filter(String expr) {
		if(!EasyRegex.findOr(expr,new String[] {" and ","or "," || "," && "}))
			return filter(new FieldExpression(expr));
		else
			return filter(new FieldExpressions(expr));
	}
	public EasyQuery<T> filter(String expr,int top) {
		if(!EasyRegex.findOr(expr,new String[] {" and ","or "," || "," && "}))
			return filter(new FieldExpression(expr),top);
		else
			return filter(new FieldExpressions(expr),top);
	}
	
	public EasyQuery<T> filter(String fieldname,Object value) {
		return filter(new FieldExpression(fieldname,value));
	}
	
	public EasyQuery<T> filter(String fieldname,String expression,Object value) {
		return filter(new FieldExpression(fieldname,expression,value));
	}
	
	public EasyQuery<T> filter(String fieldname,Object value,int top) {
		return filter(new FieldExpression(fieldname,value),top);
	}
	
	public EasyQuery<T> filter(String fieldname,String expression,Object value,int top) {
		return filter(new FieldExpression(fieldname,expression,value),top);
	}
	
	public EasyQuery<T> filter(IExpression exp) {
		return filter(exp,0);
	}
	
	private boolean match(T item,IExpression exp) {
		boolean result=true;
		boolean isOr=false;
		if(exp instanceof Expressions) {
			isOr=((Expressions)exp).isOr();
		}
		for(Iterator iter1=((Expressions)exp).iterator();iter1.hasNext();) {
			IExpression exp1=(IExpression)iter1.next();
			boolean success=false;
			if(exp1 instanceof FieldExpression) {
				FieldExpression fexp=(FieldExpression)exp1;
				Object obj=getFieldValue(item,fexp.fieldName);
				fexp.setFieldValue(obj);
				success=fexp.compute();
			}
			else if(exp1 instanceof FieldExpressions) {
				success=match(item,exp1);
			}
			else {
				success=exp1.compute();
			}
			
			if(isOr&&success) {
				result=true;
				break;
			}
			else if(!isOr&&!success) {
				result=false;
				break;
			}
			else
				result=result&&success;
		}
		return result;
	}

	public EasyQuery<T> filter(FieldExpression exp) {
		return filter(exp,0);
	}
	
	public synchronized EasyQuery<T> filter(IExpression exp,int top) {
		ArrayList list=new ArrayList();
		try{
			for(int i=0;i<this.items.size();i++){
				T item=this.items.get(i);
				boolean success=match(item,exp);
				if(success) {
					list.add(item);
					if(top>0&&list.size()>=top)
						break;
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return new EasyQuery(list);
	}
	public synchronized EasyQuery<T> filter(FieldExpression exp,int top) {
		ArrayList list=new ArrayList();
		for(int i=0;i<this.items.size();i++){
			T item=this.items.get(i);
			Object fieldValue=getFieldValue(item,exp.fieldName);
			exp.setFieldValue(fieldValue);
			if(exp.compute()) {
				list.add(item);
				if(top>0&&list.size()>=top)
					break;
			}
		}
		return new EasyQuery(list);
	}

	public EasyQuery detach(String expr) {
		EasyQuery sub=filter(expr);
		this.items.removeAll(sub.toList());
		return sub;
	}
	
	public EasyQuery remove(String expr) {
		EasyQuery sub=filter(expr);
		this.items.removeAll(sub.toList());
		return this;
	}
	
	public T get(int index) {
		return items.get(index);
	}
	
	public EasyQuery eq(int index) {
		return new EasyQuery(new Object[] {items.get(index)});
	}
	
	public EasyQuery empty() {
		items.clear();
		return this;
	}
	
	public EasyQuery clear() {
		return empty();
	}
	
	public EasyQuery<T> slice(int start,int end){
		return new EasyQuery(this.items.subList(start,end));
	}
	
	public List<T> toList(){
		return this.items;
	}
	
	@Override
	public EasyQuery<T> clone(){
		return new EasyQuery(this.items);
	}
	
	@Override
	public Iterator<T> iterator(){
		return this.items.iterator();
	}
	
	public EasyQuery<T> load(Collection<T> c) {
		this.items.addAll(c);
		return this;
	}
	
	public EasyQuery<T> load(T[] array) {
		this.items.addAll(Arrays.asList(array));
		return this;
	}
	
	public EasyQuery<T> add(T... t) {
		this.items.addAll(this.items.size(),Arrays.asList(t));
		return this;
	}
	
	public EasyQuery<T> add(Collection c) {
		this.items.addAll(this.items.size(),c);
		return this;
	}
	
	public String toJson() {
		return EasyObj.toJson(this.items);
	}
	
	@Override public String toString() {
		return this.toJson();
	}
	
	public EasyQuery<T> load(String json,Class c) {
		this.items.addAll(EasyObj.toList(json,c));
		return this;
	}
	
	//notice: at least one record can judge whether javabean, if size==0, return false.
	private boolean isJavaBean() {
		if(items.size()==0)
			return false;
		Object obj=items.get(0);
		boolean result=obj!=null &&!(obj instanceof String || obj instanceof Number ||obj instanceof Boolean );
		return result;
	}

	public Number sum(String... fields) {
		double result=0;
		if(isJavaBean()){
			for(T item:this.items){
				for(int j=0;j<fields.length;j++){
					Object o1=getFieldValue(item,fields[j]);
					if(o1!=null&&EasyMath.isNumeric(o1.toString()))
						result+=Double.valueOf(o1.toString());
				}
			}
		}else{
			for(T item:this.items) {
				if(item!=null&&EasyMath.isNumeric(item.toString()))
					result+=Double.valueOf(item.toString());
			}
		}
		return EasyMath.toNumber(result);
	}

	
	/**使用lambda计算数量*/
  public int reduceInt(ToIntFunction<T> fn) {
		int result=0;
		for(T row:this.items){
			result+=fn.applyAsInt(row);
		}
		return result;
	}
	
	/**使用lambda计算数量*/
	public long reduceLong(ToLongFunction<T> fn) {
		long result=0;
		for(T row:this.items){
			result+=fn.applyAsLong(row);
		}
		return result;
	}
	
  /**使用lambda计算数量*/
	public double reduce(ToDoubleFunction<T> fn) {
		Double result=0D;
		for(T row:this.items){
			result+=fn.applyAsDouble(row);
		}
		return result;
	}
  
	/**使用lambda表达式修改记录内容*/
	public EasyQuery<T> update(Consumer<T> fn) {
		for(T row:this.items) {
			fn.accept(row);
		}
		return this;
	}
	
	/**使用lambda表达式过滤*/
  public String[] getStrings(Function<T,String> fn) {
  	String[] result=new String[this.items.size()];
  	int i=0;
  	for(T row:this.items){
			result[i++]=fn.apply(row);
		}
  	return result;
  }
	
	/**使用lambda表达式过滤*/
  public Long[] getLongs(Function<T,Long> fn) {
  	Long[] result=new Long[this.items.size()];
  	int i=0;
  	for(T row:this.items){
			result[i++]=fn.apply(row);
		}
  	return result;
  }
	

	public Number max(String... fields) {
		Number max=0;
		for(T item:this.items){
			for(int j=0;j<fields.length;j++){
				Object obj=getFieldValue(item,fields[j]);
				Number num=esdk.math.toNumber(obj);
				max=esdk.math.max(num,max);
			}
		}
		return max;
	}
	
	public Number min(String... fields) {
		Number min=0;
		for(T item:this.items){
			for(int j=0;j<fields.length;j++){
				Object obj=getFieldValue(item,fields[j]);
				Number num=esdk.math.toNumber(obj);
				min=esdk.math.min(num,min);
			}
		}
		return min;
	}
	
	public EasyQuery<T> sort(String... fields) {
		return sort(fields,false);
	}

	public EasyQuery<T> sort(boolean isdesc) {
		return sort(new String[] {},isdesc);
	}
	
	public EasyQuery<T> sort(String fields) {
		return sort(fields,false);
	}
	
	public EasyQuery<T> sort(final String fields,final boolean isdesc) {
		return sort(fields.split(",|;"),isdesc);
	}
	
	public EasyQuery<T> sort(final String[] fields,final boolean isdesc) {
		Collections.sort(this.items,new Comparator(){
			boolean isJavaBean=isJavaBean();
			@Override public int compare(Object o1,Object o2){
					T t1=isdesc?(T)o2:(T)o1;
					T t2=isdesc?(T)o1:(T)o2;
					int result=0;
					if(isJavaBean) {
						for(int i=0;i<fields.length;i++){
							Object c1=getFieldValue(t1,fields[i]);
							Object c2=getFieldValue(t2,fields[i]);
							if(c1 instanceof Comparable) {
								result=esdk.obj.compareTo(c1,c2);
							}
							if(result!=0)
								break;
						}
					}
					else {
						result=((Comparable)t1).compareTo(t2);
					}
					return result;
			}
		});
	
		return this;
	}
	
	public EasyQuery<T> unique(String...fields) {
		return distinct(fields);
	}
	
	public EasyQuery<T> distinct(String...fields){
		if(isJavaBean()){
			HashSet set=new HashSet(items.size());
			try{
				for(int i=0;i<this.items.size();i++){
					T item=items.get(i);
					StringBuilder key=new StringBuilder();
					for(int j=0;j<fields.length;j++){
						Object o1=getFieldValue(item,fields[j]);
						key.append(EasyStr.valueOf(o1)).append('|');
					}
					if(set.contains(key.toString())) {
						items.remove(i);
						i--;
					}
					else {
						set.add(key.toString());
					}
				}
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}else{
			this.items.addAll(EasyArray.unique(this.items));
		}
		return this;
	}
	public int size() {
		return this.items.size();
	}
	

	public static class FieldExpression extends Expression{
		public String fieldName;
		private boolean isParsed;
		public FieldExpression(String field, String expression, Object value){
			super(field,expression,value);
			this.fieldName=field;
		}
		public FieldExpression(String field, Object value){
			super(field,value);
			this.fieldName=field;
		}
		public FieldExpression(String expr){
			super(null,null,null);
			try{
				parse(expr);
				setLeftValue(fieldName);
				isParsed=true;
			}catch(Exception e){
				throw new RuntimeException(e);
			}
		}
		public FieldExpression setFieldValue(Object leftvalue) {
			setLeftValue(leftvalue);
			adjustRightValue(leftvalue);
			return this;
		}
		
		private void adjustRightValue(Object leftvalue){
			if((leftvalue==null||!leftvalue.getClass().equals(String.class))
					&&rightValue!=null
					&&"null".equalsIgnoreCase(rightValue.toString()))
				rightValue=null;
			else if(leftvalue==null&&rightValue!=null&&rightValue.getClass().isArray()) {
				Object[] arr=(Object[])rightValue;
				for(int i=0;i<arr.length;i++) {
					if("null".equals(arr[i])) {
						arr[i]=null;
					}
				}
			}
			else if(rightValue!=null&&leftvalue!=null) {
				if(!rightValue.getClass().isArray()&&!rightValue.getClass().equals(leftvalue.getClass()))
					rightValue=EasyObj.valueOf(leftvalue.getClass(),rightValue);
				else if(rightValue.getClass().isArray()) {
					Object[] tmp=new Object[((Object[])rightValue).length];
					for(int i=0;i<tmp.length;i++) {
						tmp[i]=EasyObj.valueOf(leftvalue.getClass(),((Object[])rightValue)[i]);
					}
					rightValue=tmp;
				}
			}
		}
		
		public FieldExpression parse(String expr) throws Exception {
			new RegexReplace(expr,"(^[a-z_A-Z1-9\u4E00-\u9FA5]*)([\\*\\!\\$\\^=<>]*)(.*$)"){
				@Override public String getReplacement(Matcher matcher){
					fieldName=matcher.group(1);
					setLeftValue(fieldName);
					setExpression(matcher.group(2));
					String value=esdk.str.trim(matcher.group(3));
					if(value.startsWith("[")&&value.endsWith("]")) {
						String[] values=value.substring(1,value.length()-1).split(",");
						setRightValue(values);
						if(getExpression().equalsIgnoreCase(EQUAL))
							setExpression(IN);
						else if(NOTEQ.equals(getExpression())||NOTEQUAL.equals(getExpression()))
							setExpression(NOTIN);
					}
					else {
						setRightValue(value);
					}
					return null;
				}
			}.replaceFirst();
			return this;
		}
		public boolean isParsed(){
			return isParsed;
		}
	}
	
	public static class FieldExpressions extends Expressions{
		private static String andSplitRegex="( and )|( ?\\&\\& ?)";
		private static String orSplitRegex="( or )|( ?\\|\\| ?)";
		private static String andMatchesRegex="^.*?( and | \\&\\& ).*?$";
		private static String orMatchesRegex="^.*( or | ?\\|\\| ?).*$";
		public FieldExpressions(String expr){
			parse(expr);
		}
		
		public FieldExpressions(IExpression... iexps) {
			super(iexps);
		}
		
		public FieldExpressions(boolean isOr,String... exprs) {
			super(isOr,getFieldExpressions(exprs));
		}
		static private IExpression[] getFieldExpressions(String...exprs) {
			IExpression[] result=new IExpression[exprs.length];
			for(int i=0;i<exprs.length;i++) {
				result[i]=new FieldExpression(exprs[i]);
			}
			return result;
		}
		public FieldExpressions(boolean isOr,IExpression[] iexps) {
			super(isOr,iexps);
		}
		public static FieldExpressions or(IExpression... expr){
			return new FieldExpressions(true,expr);
		}
		
		public static FieldExpressions and(IExpression... expr){
			return new FieldExpressions(false,expr);
		}
		public static FieldExpressions or(String... exprs){
			return FieldExpressions.or(getFieldExpressions(exprs));
		}
		public static FieldExpressions and(String... exprs){
			return FieldExpressions.and(getFieldExpressions(exprs));
		}
		public void parse(String expr) {
			MatchString ms=new MatchString(expr);
			if(ms.getMatchList().size()==1) {
				String[] exprs=expr.split(andSplitRegex);
				if(exprs.length==1) {
					String[] orexprs=expr.split(orSplitRegex);
					if(orexprs.length==1)
						add(new FieldExpression(expr));
					else {
						add(new FieldExpressions(true,orexprs));
					}
				}
				else
					add(getFieldExpressions(exprs));
			}
			else
				this.add(parse(ms));
		}
		public FieldExpressions(String[] exprs){
			add(getFieldExpressions(exprs));
		}
		
		private FieldExpressions parse(MatchString ms){
			FieldExpressions result=new FieldExpressions(new String[]{});
			for(Iterator iter=ms.getMatchList().iterator();iter.hasNext();){
				Object subexpr=iter.next();
				if(subexpr instanceof String){
					if(((String)subexpr).matches(orMatchesRegex)){
						String[] orSubexprs=subexpr.toString().split(orSplitRegex);
						result.setOr(true);
						for(int i=0;i<orSubexprs.length;i++){
							result.add(new FieldExpression(orSubexprs[i]));
						}
					}else if(((String)subexpr).matches(andMatchesRegex)){
						String[] andSubexprs=subexpr.toString().split(andSplitRegex);
						result.add(new FieldExpressions(getFieldExpressions(andSubexprs)));
					}else
						result.add(new FieldExpression(subexpr.toString()));
				}else{
					result.add(parse((MatchString)subexpr));
				}
			}
			return result;
		}
	}
	
	/**
	 * 安全获取List的sub记录，不会抛IndexOutOfBoundsException异常，返回list的size以实际的为准
	 * @param fromIndex
	 * @param toIndex
	 * @param safe
	 * @return
	 */
	public EasyQuery<T> subsafe(int fromIndex, int toIndex) {
		if(fromIndex>=0&&fromIndex<this.items.size()&&toIndex>fromIndex) {
			int end=toIndex<=this.size()?toIndex:this.size();
			return new EasyQuery<T>(this.items.subList(fromIndex, end));
		}
		else
			return new EasyQuery<T>(new LinkedList());                            
	}
	
	public EasyQuery<T> sub(int fromIndex, int toIndex) {
		return new EasyQuery<T>(this.items.subList(fromIndex, toIndex));
	}

	public EasyQuery<T> sub(int fromIndex, int toIndex,T defaultItem) {
		int to=EasyMath.min(toIndex, this.items.size());
		int from=fromIndex<this.items.size()?fromIndex:this.items.size();
		EasyQuery<T> result= new EasyQuery<T>(this.items.subList(from, to));
		for(int i=toIndex-this.items.size();i>0;i--)
			result.add(defaultItem);
		return result;
	}
	
	public EasyQuery<T> update(String fieldName,Object value){
		for(T item:this.items) {
			setFieldValue(item,fieldName,value);
		}
		return this;
	}
	
	private Object getFieldValue(Object obj, String fieldName) {
		if(obj==null)
			return null;
		else if(obj instanceof Map)
			return ((Map)obj).get(fieldName);
		else if(obj instanceof IRow)
			return ((IRow)obj).get(fieldName);
		else
			return EasyReflect.getPropertyValue(obj,fieldName);
	}
	
	private HashMap<String,Method> setterMethodMap=new HashMap();
	private Method getSetterMethod(String methodName) {
		Method result=setterMethodMap.get(methodName);
		if(result==null) {
			T t=first();
			result=(t!=null)?EasyReflect.findSetterMethod(t.getClass(),methodName):null;
			if(result!=null)
				setterMethodMap.put(methodName,result);
		}
		return result;
	}
	
	private Object setFieldValue(Object obj,String fieldName,Object value){
		Object oldValue=null;
		if(obj==null)
			return null;
		else if(obj instanceof Map)
			oldValue=((Map)obj).put(fieldName,value);
		else if(obj instanceof IRow) {
			oldValue=((IRow)obj).get(fieldName);
			((IRow)obj).set(fieldName,value);
		}
		else{
			Method m=getSetterMethod(fieldName);
			if(m!=null){
				oldValue=EasyReflect.getPropertyValue(obj,fieldName);
				EasyReflect.setSetterMethodValue(obj,m,value);
			}
		}
		return oldValue;
	}
	
	public Object[] getValues(String fieldName) {
		Object[] result=new Object[this.size()];
		int i=0;
		for(T item:this.items) {
			result[i++]=getFieldValue(item,fieldName);
		}
		return result;
	}
	
	public List<List<T>> group(String... fields){
		LinkedHashMap<String,List<T>> result=new LinkedHashMap();
		for(T item :this.items) {
			String key="";
			for(String field:fields) {
				key+=getFieldValue(item,field);
			}
			if(!result.containsKey(key))
				result.put(key,new LinkedList());
			result.get(key).add(item);
		}
		return new ArrayList(result.values());
	}

}

