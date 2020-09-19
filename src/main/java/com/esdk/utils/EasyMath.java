package com.esdk.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import com.esdk.esdk;

public class EasyMath{

	/**四舍五入*/
  public static double round(double value,int holdDigit){
    double a=Math.pow(10,holdDigit);
    return Math.round(value*a)/a;
  }

  /**执行向上舍入，即它总是将数值向上舍入为最接近的整数*/
  public static double ceil(double value) {
  	return Math.ceil(value);
  }
  
  /**执行向下舍入，即它总是将数值向下舍入为最接近的整数*/
  public static double floor(double value) {
  	return Math.floor(value);
  }
  
  /**指定数值长度的四舍五入，比较特殊的用法，例如33.5955指定3位长度的四舍五入，返回为33.6*/
  public static double roundByLen(double value,int maxlen){
    String v=String.valueOf(value);
    if(v.length()>maxlen){
      int pos=v.indexOf(".");
      int holdDigit=maxlen-pos;
      value=round(value,holdDigit);
    }
    return value;
  }

  /**对四则运算的字符串计算结果*/
  public static double eval(String exp) {
    String[] strExp=splitFormula(exp);
    String[] postfixExp=infix2postfix(strExp);
    return calculatePostfix(postfixExp); 
  }
  
  /**解析四则运算表达式*/
  private static String[] splitFormula(String exp) {
    LinkedList result=new LinkedList();
    char[] ch=exp.toCharArray();
    StringBuffer numeric=new StringBuffer();
    for(int i=0;i<ch.length;i++){
      if(ch[i]>='0'&&ch[i]<='9')
        numeric.append(ch[i]);
      else if(ch[i]!=' '){
        if(numeric.length()>0) {
          result.add(numeric.toString());
          numeric.delete(0,numeric.length());
        }
        result.add(String.valueOf(ch[i]));
      }
    }
    if(numeric.length()>0) {
      result.add(numeric.toString());
      numeric.delete(0,numeric.length());
    }
    String[] s=(String[])result.toArray(new String[0]);
    return s;
  }
  
  /**中缀形式转变为后缀形式*/
  private static String[] infix2postfix(String[] strExp) {
    ArrayList alOds=new ArrayList();//后缀表达式序列
    Stack sk=new Stack();//工作栈
    String y="";
    sk.push("#");//初始栈
    for(int i=0;i<strExp.length;i++) {
      String op=strExp[i];
      if(isNumeric(op)) {
        alOds.add(op);
      }
      else{// 对于运算符
        if(icp(op)>isp(((String)sk.peek()))){
          // 如果isp栈顶符号<icp扫描的符号就压栈
          sk.push(op);
        }
        else if(op.equals(")")){
          for(y=(String)sk.pop();!y.equals("(");y=(String)sk.pop()){
            alOds.add(y);
          }
        }
        else{
          for(y=(String)sk.pop();isp(y)>icp(op);y=(String)sk.pop()){
            alOds.add(y);
          }
          sk.push(y);
          sk.push(op);
        }
      }
    }
    while(!sk.peek().equals("#")) {
      alOds.add(sk.pop());//弹出所有栈内符号并保存到后缀序列中直至#
    }
    return (String[])alOds.toArray(new String[0]);
  }
  
  private static double calculatePostfix(String[] strExp) {    //计算后缀形式,其中strExp是存放了后缀串序列的数组
    Stack sk=new Stack();
    for(int i=0;i<strExp.length;i++){
      String op=strExp[i];
      if(isNumeric(op)) {//如果是数字压栈
        sk.push(op);
      }
      else {//如果是运算符就计算求值
        double left=Double.parseDouble((String)sk.pop());
        double right=Double.parseDouble((String)sk.pop());
        double r=0;//临时结果
        switch(op.charAt(0)){
          case '+':
            r=right+left;
            break;
          case '-':
            r=right-left;
            break;
          case '*':
            r=right*left;
            break;
          case '/':
            r=right/left;
            break;
        }
        sk.push(String.valueOf(r));
      }
    }
    return new Double((String)sk.peek()).doubleValue();
  }
  
  private static int isp(String value) {//in stack priority 栈内优先级 
    int result=-1;
    char c=value.charAt(0);
    switch(c){
      case '#':
        result=0;break;
      case '(':
        result=1;break;
      case '*':
        result=5;break;
      case '/':
        result=5;break;
      case '%':
        result=5;break;
      case '+':
        result=3;break;
      case '-':
        result=3;break;
      case ')':
        result=6;break;
      default:
        break;
    }
    return result;
  }
  
  private static int icp(String value) {//in coming priority 进栈优先级
    int result=-1;
    char[] c=value.toCharArray();
    switch(c[0]){
      case '#':
        result=0;break;
      case '(':
        result=6;break;
      case '*':
        result=4;break;
      case '/':
        result=4;break;
      case '%':
        result=4;break;
      case '+':
        result=2;break;
      case '-':
        result=2;break;
      case ')':
        result=1;break;
      default:
        break;
    }
    return result;
  }
  
  /**判断是否为数值类型*/
  public static boolean isNumeric(String value) {
  	if(value==null||value.equals(""))
  		return false;
  	return value.matches("-?[\\d\\.]{1,20}(E\\d)?");
  }
  
  /**判断是否为数值类型*/
  public static boolean isNumeric(Object value) {
    if(value!=null&&value instanceof Number)
      return true;
    else 
      return isNumeric((String)value);
  }
  
  /**判断是否为int类型*/
  public static boolean isInt(String value) {
  	if(value==null||value.equals(""))
  		return false;
  	return value.matches("-?\\d+");
  }
  
  /**整数到字节数组的转换*/
  public static byte[] toByteArray(int number){
    int temp=number;
    byte[] b=new byte[4];
    for(int i=b.length-1;i>-1;i--){
      b[i]=new Integer(temp&0xff).byteValue();
      temp=temp>>8;
    }
    return b;
  }

  /**字节数组到整数的转换*/
  public static int toInteger(byte[] b){
    int s=0;
    for(int i=0;i<3;i++){
      if(b[i]>0)
        s=s+b[i];
      else
        s=s+256+b[i];
      s=s*256;
    }
    if(b[3]>0)
      s=s+b[3];
    else
      s=s+256+b[3];
    return s;
  }
  
  /**字节数组到长整数的转换*/
  public static long toLong(byte[] b){
    long s=0;
    for(int i=0;i<3;i++){
      if(b[i]>0)
        s=s+b[i];
      else
        s=s+256+b[i];
      s=s*256;
    }
    if(b[3]>0)
      s=s+b[3];
    else
      s=s+256+b[3];
    return s;
  }
  
  public static long int2long(int value) {
    return Long.valueOf(Integer.toHexString(value),16).longValue(); 
  }
  
  /***/
	public static <T> T convert(Object value,Class<T> cls){
		Object result=value;
		if(esdk.tool.asSubClass(cls,Number.class)){
			if(value.getClass().equals(cls))
				result=value;
			else if(value instanceof String){
				if(value==null||value.toString().equals(""))
					result=null;
				else if(cls.equals(Short.class))
					result=Short.valueOf(value.toString());
				else if(cls.equals(Integer.class))
					result=Integer.valueOf(value.toString());
				else if(cls.equals(Float.class))
					result=Float.valueOf(value.toString());
				else if(cls.equals(Long.class))
					result=Long.valueOf(value.toString());
				else if(cls.equals(Double.class))
					result=Double.valueOf(value.toString());
			}else if(value instanceof Number){
				if(cls.equals(Short.class))
					result=((Number)value).shortValue();
				else if(cls.equals(Integer.class))
					result= ((Number)value).intValue();
				else if(cls.equals(Float.class))
					result=((Number)value).floatValue();
				else if(cls.equals(Long.class))
					result=((Number)value).longValue();
				else if(cls.equals(Double.class))
					result=((Number)value).doubleValue();
			}
		}
		return (T)result;
	}

	/**比较两个数组是否一致*/
	public static boolean equals(Integer[] a,Integer[] b){
		boolean result=true;
		if(a==b)
			return true;
		a=esdk.array.sort(a);
		b=esdk.array.sort(b);
		if(a.length==b.length){
			for(int i=0;i<b.length;i++){
				if(!a[i].equals(b[i])){
					result=false;
					break;
				}
			}
		}else {
			result=false;
		}
		return result;
	}
	
	/**获取最大值*/
  public static int max(int[] value) {
  	int max=value[0];
  	for(int i=1;i<value.length;i++){
  		max=value[i]>max?value[i]:max;
  	}
  	return max;
  }

	/**获取最大值*/
  public static <T extends Number> T max(T... values) {
  	if(values.length==0)
  		return null;
  	T max=values[0];
  	for(int i=1;i<values.length;i++){
  		max=values[i].doubleValue()>max.doubleValue()?values[i]:max;
  	}
  	return max;
  }

	/**获取最小值*/
  public static <T extends Number> T min(T... values) {
  	T min=values[0];
  	for(int i=1;i<values.length;i++){
  		min=values[i].doubleValue()<min.doubleValue()?values[i]:min;
  	}
  	return min;
  }

	/**获取最大值*/
  public static <T extends Number>T max(T a,T b) {
  	if(a==null)
  		return b;
  	else if(b==null)
  		return a;
  	else
  		return ((Comparable)a).compareTo(b)>0?a:b;
  }
  
	/**获取最小值*/ 
  public static <T extends Number> T min(T a,T b) {
  	if(a==null)
  		return a;
  	else if(b==null)
  		return b;
  	else
  		return ((Comparable)a).compareTo(b)<0?a:b;
  }

	/**获取最大值*/
  public static double max(double a,double b) {
    return a>b?a:b;
  }
  
	/**获取最小值*/ 
  public static double min(double a,double b) {
    return a<b?a:b;
  }

	/**获取最大值*/
  public static int max(int a,int b) {
    return a>b?a:b;
  }
  
	/**获取最小值*/ 
  public static int min(int a,int b) {
    return a<b?a:b;
  }

  /**优先返回非0值，找不到就优先返回0，最后才是返回null*/
	public static Integer or(Integer...vars){
		Integer result=null;
		for(int i=0;i<vars.length;i++){
			if(vars[i]!=null){
				if(vars[i]!=0) {
					result=vars[i];
					break;
				}
				else if(vars[i]==0)
					result=vars[i];
			}
		}
		return result;
	}

	/**两个值中取非null和非0的值，p2相当于默认值的意思*/
	public static Integer or(Integer p1,Integer p2){ 
		if(p1!=null&&p1!=0)
			return p1;
		else if(p2!=null&&p2!=0)
			return p2;
		else if(p1!=null)
			return p1;
		else
			return p2;
	}
	
  /**优先返回非0值，找不到就优先返回0，最后才是返回null*/
	public static Double or(Double...vars){
		Double result=null;
		for(int i=0;i<vars.length;i++){
			if(vars[i]!=null){
				if(vars[i]!=0) {
					result=vars[i];
					break;
				}
				else if(vars[i]==0)
					result=vars[i];
			}
		}
		return result;
	}
	
	/**把多余的0移除*/
  public static BigDecimal trimZero(BigDecimal value) {
    return new BigDecimal(String.valueOf(value.doubleValue()));
  }

  /**把用分隔符分隔的多个数值转换成Integer数组*/
  public static Integer[] toIntArray(String value){
    return toIntArray(EasyStr.split(value));
  }
  
  /**把字符串数组转换成Integer数组*/
  public static Integer[] toIntArray(String[] arr){
    if(arr==null)
      return null;
    Integer[] result=new Integer[arr.length];
    for(int i=0;i<result.length;i++){
    	if(arr[i].trim().length()>0)
    		result[i]=new Integer(arr[i].trim());
    }
    return result;
  }  

  /**把用分隔符分隔的多个数值转换成Long数组*/
  public static Long[] toLongArray(String value){
    return toLongArray(EasyStr.splits(value));
  }
  
  /**把用单个数值转换成数组*/
  public static Long[] toLongArray(Long value){
    return new Long[] {value};
  }
  
  /**把字符串数组转换成Long数组*/
  public static Long[] toLongArray(String[] arr){
    if(arr==null)
      return null;
    Long[] result=new Long[arr.length];
    for(int i=0;i<result.length;i++){
    	if(arr[i].trim().length()>0)
    		result[i]=new Long(arr[i].trim());
    }
    return result;
  }  

  /**把用分隔符分隔的多个数值转换成Double数组*/
  public static Double[] toDoubleArray(String value){
    return toDoubleArray(EasyStr.splits(value));
  }
  
  /**把字符串数组转换成Double数组*/
  public static Double[] toDoubleArray(String[] arr){
    if(arr==null)
      return null;
    Double[] result=new Double[arr.length];
    for(int i=0;i<result.length;i++){
    	if(arr[i].trim().length()>0)
    		result[i]=new Double(arr[i].trim());
    }
    return result;
  }
  
	/** param[objects] should be Integer[] or Integer*/
	public static Integer[] join(Integer... intOrArr){
		return (Integer[])EasyArray.concat(intOrArr);
	}
  
	/**把多个数组的元素重新组合成一个数组*/
  public static Integer[] join(Integer[] arr,Integer... arr1){
  	ArrayList<Integer> result=new ArrayList(arr.length+arr1.length);
  	result.addAll(Arrays.asList(arr));
  	result.addAll(result.size(),Arrays.asList(arr1));
		return result.toArray(new Integer[0]);
	}
  
  /**int数组转换为Integer数组*/
  public static Integer[] toIntArray(int[] arr){
    if(arr==null)
      return null;
    Integer[] result=new Integer[arr.length];
    for(int i=0;i<result.length;i++){
      result[i]=new Integer(arr[i]);
    }
    return result;
  }
	
  /**把多个数值转换成数组输出*/
	public static Long[] join(Long... intOrArr){
		return (Long[])EasyArray.concat(intOrArr);
	}
  
	/**把多个数组的元素重新组合成一个数组*/
  public static Long[] join(Long[] arr,Long... arr1){
  	ArrayList<Long> result=new ArrayList(arr.length+arr1.length);
  	result.addAll(Arrays.asList(arr));
  	result.addAll(result.size(),Arrays.asList(arr1));
		return result.toArray(new Long[0]);
	}
  
	/**把对象转换为数组输出，能够自动判断对象的类型并进行类型转换处理*/
  public static Number[] toNumberArray(Object array){
    if(array==null||!array.getClass().isArray())
      return null;
    Number[] result=null;
    if(array instanceof Number[]) {
    	result=(Number[])array;
    }
    else if(array instanceof int[]) {
    	int[] arr=(int[])array;
      result=new Integer[arr.length];
      for(int i=0;i<result.length;i++){
        result[i]=new Integer(arr[i]);
      }
    }
    else if(array instanceof double[]) {
    	double[] arr=(double[])array;
      result=new Double[arr.length];
      for(int i=0;i<result.length;i++){
        result[i]=new Double(arr[i]);
      }
    }
    else if(array instanceof float[]) {
    	float[] arr=(float[])array;
      result=new Float[arr.length];
      for(int i=0;i<result.length;i++){
        result[i]=new Float(arr[i]);
      }
    }
    else if(array instanceof short[]) {
    	short[] arr=(short[])array;
      result=new Short[arr.length];
      for(int i=0;i<result.length;i++){
        result[i]=new Short(arr[i]);
      }
    }
    else if(array instanceof Object[]) {
    	Object[] arr=(Object[])array;
	    result=new Double[arr.length];
	    for(int i=0;i<result.length;i++){
	      result[i]=new Double(arr[i].toString());
	    }
    }
    return result;
  }
  
  /**把字符串数组转换成BigDecimal数组*/
	public static BigDecimal[] toBigDecimalArray(String[] arr){
    if(arr==null)
      return null;
    BigDecimal[] result=new BigDecimal[arr.length];
    for(int i=0;i<result.length;i++){
      result[i]=new BigDecimal(arr[i]);
    }
    return result;
	}
	
	/**把对象实例转换成数值类型*/
	public static Short toShort(Object obj) {
		if(obj==null)
			return null;
		else if(obj instanceof Number)
			return ((Number)obj).shortValue();
		else if(obj instanceof String && ((String)obj).length()==0)
			return null;
		else if(isNumeric(obj))
			return Short.valueOf(obj.toString());
		else
			throw new NumberFormatException(obj.toString());
	}
	
	/**把对象实例转换成数值类型*/
	public static int toInt(Object obj) {
		if(obj==null)
			return 0;
		else if(obj instanceof Number)
			return ((Number)obj).intValue();
		else if(obj instanceof String && ((String)obj).length()==0)
			return 0;
		else if(isNumeric(obj))
			return Double.valueOf(obj.toString()).intValue();
		else
			throw new NumberFormatException(obj.toString());
	}
	
	/**把对象实例转换成数值类型*/
	public static Integer toInteger(Object obj) {
		if(obj==null)
			return null;
		else if(obj instanceof String && ((String)obj).length()==0)
			return null;
		else 
			return toInt(obj);
	}

	/**把对象实例转换成数值类型*/
	public static Long toLong(Object obj) {
		if(obj==null)
			return null;
		else if(obj instanceof Number)
			return ((Number)obj).longValue();
		else if(obj instanceof String && ((String)obj).length()==0)
			return null;
		else if(isNumeric(obj))
			return Long.valueOf(obj.toString());
		else
			throw new NumberFormatException(obj.toString());
	}
	
	/**返回对象，如果为null或空返回null*/
	public static Double toDouble(Object obj) {
		if(obj==null)
			return null;
		else if(obj instanceof Double)
			return (Double)obj;
		else if(obj instanceof Number)
			return ((Number)obj).doubleValue();
		else if(obj instanceof String && ((String)obj).length()==0)
			return null;
		else if(isNumeric(obj))
			return Double.valueOf(obj.toString());
		else
			throw new NumberFormatException(obj.toString());
	} 
	
	/**返回数值，如果为空返回0*/
	public static double toDbl(Object obj) {
		if(obj==null)
			return 0;
		else if(obj instanceof Double)
			return (Double)obj;
		else if(obj instanceof Number)
			return ((Number)obj).doubleValue();
		else if(obj instanceof String && ((String)obj).length()==0)
			return 0;
		else if(isNumeric(obj))
			return Double.valueOf(obj.toString()).doubleValue();
		else
			throw new NumberFormatException(obj.toString());
	}
	
	/**把对象实例转换成数值类型*/
  public static BigDecimal toBigDecimal(Object obj) {
  	return toBigDecimal(obj,null);
  }
  
	/**把对象实例转换成数值类型*/
  public static BigDecimal toBigDecimal(Object obj,BigDecimal def) {
  	if(obj==null||obj.toString().length()==0)
  		return def;
  	else
  		return new BigDecimal(obj.toString());
  }
  
	/**把对象实例转换成数值类型*/
  public static Number toNumber(Object obj) {
  	return toNumber(obj,null);
  }
  
	/**把对象实例转换成数值类型*/
	public static Number toNumber(Object obj,Number def) {
		if(obj==null||obj.toString().length()==0)
			return def;
		String numStr=obj.toString();
		if(obj instanceof CharSequence && isNumeric(numStr)) {
			if(numStr.contains("."))
				return Double.valueOf(numStr);
			else {
				Long result=Long.valueOf(numStr);
				if(result.longValue()>Integer.MAX_VALUE)
					return result;
				else
					return result.intValue();
			}
		}else if(obj instanceof Number) {
			return (Number)obj;
		}
		else {
			throw new NumberFormatException(obj.toString());
		}
	}
	
	/**对多个对象实例进行sum计算*/
	public static int sum(Object... objs) {
		int result=0;
		for(int i=0;objs!=null&&i<objs.length;i++) {
			if(objs[i]==null) 
				continue;
			if(objs[i] instanceof Number) {
				result+=((Number)objs[i]).intValue();
			}
			else if(objs[i] instanceof Number[]) {
				result+=sum(objs[i]);
			}
		}
		return result;
	}
	
	/**对多个对象实例进行sum计算*/
	public static long sum(int... nums) {
		int result=0;
		for(int i=0;i<nums.length;i++) {
			result+=nums[i];
		}
		return result;
	}
  
	private static Random random=new Random();
	/**指定最大值和最小值返回一个随机值*/
  public static int random(int min,int max) {
  	int result=random.nextInt(max-min)+min;
  	return result;
  }
  
  
  
  public static void test() {
  	System.out.println((0.03-0.02)!=0.01);//double类型的坑
  	esdk.tool.assertEquals(isNumeric("-123.0E7"));
  	esdk.tool.assertEquals(isNumeric("-123.01234"));
  	esdk.tool.assertEquals(isNumeric("123"));
  	esdk.tool.assertEquals((double)or(null,0.0),0d);
    esdk.tool.assertEquals(round(33.015,2),33.02);
    esdk.tool.assertEquals(roundByLen(33.5955,3),33.6);
    esdk.tool.assertEquals(roundByLen(333.5955,2),330.0);//只算到前面两位，所以结果没问题。
    esdk.tool.assertEquals(round(33.5955,3),33.596);
    esdk.tool.assertEquals(eval("34 + 55 - ( 78 * 23 ) / 5 - 93"),-362.8);
    esdk.tool.assertEquals(int2long(-215435435),int2long(-215435435));
    esdk.tool.assertEquals(11.1,max(11.1,-2.1));
    esdk.tool.assertEquals(11.1,min(11.5,11.1));
    /*TestHelper.assertEquals(s(new BigDecimal(1.3000).multiply(new BigDecimal(2.0))));*/
    TestHelper.assertEquals(trimZero(new BigDecimal("1.3000").multiply(new BigDecimal("2.000"))).doubleValue(),2.6);
    esdk.tool.assertEquals(trimZero(new BigDecimal(0.042*0.001).setScale(8,BigDecimal.ROUND_HALF_UP)).doubleValue(),0.000042);
    esdk.tool.assertEquals(toNumber(round(130/60d,1)),2.2);
  }
  public static void main(String[] args){
    test();
  }

}
