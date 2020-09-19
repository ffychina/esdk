
package com.esdk.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.SQLRuntimeException;

public class EasyTool{
	public static Throwable getRootCause(Throwable e){
		if(e==null)
			return null;
		return e.getCause()!=null?getRootCause(e.getCause()):e;
	}

	/**得到程序目录*/
	public static String GetAppPath(){
		return System.getProperty("user.dir");
	}

	public static String occurID(){
		return occurID(18);
	}

	private static HashSet _occurIdSet=new HashSet(50);
	private static String _lastTime;

	public static String occurID(int len){
		String result=_occurId(len);
		while(_occurIdSet.contains(result)){
			result=_occurId(len);
		}
		_occurIdSet.add(result);
		return result;
	}

	private static String _occurId(int len){
		final int minlen=15;
		String mstime=new SimpleDateFormat("yyMMddHHmmssSSS").format(new Date());
		String result=(mstime+Math.random()).replaceFirst("\\.","");
		if(len>minlen)
			result=result.substring(0,len);
		else
			result=result.substring(minlen-len,minlen);
		if(!mstime.equals(_lastTime)){
			_lastTime=mstime;
			_occurIdSet.clear();
		}
		return result;
	}

	public static String getRunPath(){
		return (String)System.getProperties().get("user.dir");
	}

	public static String getSystemEnvironment(){
		String result="";
		java.util.Properties props=System.getProperties();
		java.util.Enumeration e=props.propertyNames();
		for(;e.hasMoreElements();){
			String name=(String)e.nextElement();
			result+=name+" --- "+(String)props.get(name)+"\n";
		}
		try{
			java.net.InetAddress s=java.net.InetAddress.getLocalHost();
			result+="computer:"+s.getHostName()+"\n";
			return result;
		}catch(Exception ee){
			return ee.toString();
		}
	}

	public static boolean isWindowsSystem() {
		String OS=System.getProperty("os.name").toLowerCase();
		return (OS.indexOf("windows")>=0);
	}
	
	public static String getPropertiesValueWithSystemEnvirnment(String value){
		String result=EasyStr.format(value,System.getProperties());
		return result.replaceAll("\\$\\{.*\\}","");
	}

	@SuppressWarnings("unused")
	public static void emptyFn(Object...o){
	}

	public static int exec(String cmd){
		return EasyRuntime.exec(cmd);
	}

	public static void printJavaEnvirment(){
		Properties p=System.getProperties();
		Set keys=p.keySet();
		for(Object k:keys){
			System.out.println(String.format("%s ==> %s",k,p.getProperty((String)k)));
		}
	}

	public static String getSystemEncoding(){
		//		 System.out.println("file.encoding:"+System.getProperty("file.encoding"));
		/*String encoding;
		if(System.getProperty("os.name").startsWith("Win"))
			encoding="gbk";
		else
			encoding=System.getProperty("file.encoding");
		return encoding.equals("MS950")?"big5":encoding;*/
		return System.getProperty("sun.jnu.encoding");
	}

	public static String getFileEncoding(){
		return System.getProperty("file.encoding");
	}

	public static String getExceptionStackTrace(Throwable e){
		StringWriter sw=new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		// String result=sw.toString() ;
		// sw.close();//it's not effect.
		return sw.toString();
	}

	public static String getExceptionStackTrace(Throwable e,String includesRegex){
		return getExceptionStackTrace(e,includesRegex,null);
	}

	/**isQuickCheck为true：过滤不重要的StackTrace，只保留与项目相关的StackTrace
	 * @throws Exception */
	public static String getExceptionStackTrace(Throwable e,String includesRegex,String excludes){
		StringWriter sw=new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String result=sw.toString();
		boolean[] firstTrackLine=new boolean[]{true};
		if(includesRegex!=null){
			result=(new RegexReplace(result,"(	at )(.*)(\r?\n)"){
				@Override
				public String getReplacement(Matcher matcher){
					if(firstTrackLine[0]){
						firstTrackLine[0]=false;
						return matcher.group();
					}else if(esdk.str.isValid(excludes)&&matcher.group(2).matches(".*("+excludes+").*"))
						return "";
					else if(matcher.group(2).matches(includesRegex))
						return matcher.group();
					else
						return "";
				}
			}).replaceAll();
		}
		return result;
	}

	/**
	 * 用运行时异常包裹抛出对象，如果抛出对象本身就是运行时异常，则直接返回。
	 * 如果是 InvocationTargetException，那么将其剥离，只包裹其 TargetException
	 * @param e 抛出对象
	 * @return 运行时异常
	 */
	public static RuntimeException wrapThrowble(Throwable e){
		if(e instanceof SQLException)
			return (SQLRuntimeException)e;
		else if(e instanceof RuntimeException)
			return (RuntimeException)e;
		else if(e instanceof InvocationTargetException)
			return wrapThrowble(((InvocationTargetException)e).getTargetException());
		return new SdkRuntimeException(e);
	}
	
	public static boolean assertEquals(boolean v){
		return TestHelper.assertEquals(Boolean.valueOf(v),Boolean.TRUE);
	}

	public static boolean assertEquals(double actualValue,double expectValue){
		return TestHelper.assertEquals(new Double(actualValue),new Double(expectValue));
	}

	public static boolean assertEquals(long actualValue,long expectValue){
		return TestHelper.assertEquals(actualValue,expectValue);
	}
	
	public static boolean assertEquals(int actualValue,int expectValue){
		return TestHelper.assertEquals(new Integer(actualValue),new Integer(expectValue));
	}

	public static boolean assertEquals(Object actualValue,Object expectValue){
		return TestHelper.assertEquals(actualValue,expectValue);
	}

	public static boolean aeic(String actualValue,String expectValue){
		return TestHelper.assertEqualsIgnoreCase(actualValue,expectValue);
	}

	public static boolean assertEqualsIgnoreCase(String actualValue,String expectValue){
		return TestHelper.assertEqualsIgnoreCase(actualValue,expectValue);
	}

	public static boolean assertEquals(String actualValue,String expectValue,boolean ignoreCase){
		if(ignoreCase)
			return TestHelper.assertEqualsIgnoreCase(actualValue,expectValue);
		else
			return assertEquals(actualValue,expectValue);
	}

	public static boolean assertEquals(String actualValue,String expectValue){
		return TestHelper.assertEquals(actualValue,expectValue);
	}

	public static void printAssertInfo(){
		TestHelper.printAssertInfo();
	}

	public static boolean asSubClass(Class sub,Class base){
		try{
			sub.asSubclass(base);
		}catch(ClassCastException e){
			return false;
		}
		return true;
	}

	@SuppressWarnings("unused")
	public static void read(Object...o){
	}

	@SuppressWarnings("unused")
	public static void read(Object o){
	}
}
