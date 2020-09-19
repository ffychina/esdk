package com.esdk.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esdk.esdk;

import cn.hutool.core.util.ReUtil;

public class EasyRegex{
  public static boolean find(String str,String subString) {
    Pattern p = Pattern.compile(subString,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    return m.find();
  }
  
  public static int indexOf(String str,String regex) {
    Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    int result=m.find()?m.start():-1;
    return result;
  }
  
  public static String[] findSub(String str,String regex){
  	 Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
     Matcher m=p.matcher(str);
     String[] result=null;
     if(m.find()){
    	 result=new String[m.groupCount()];
    	 for(int i=0;i<result.length;i++)
    		 result[i]=m.group(i+1);
     }
     return result;
  }
  
  public static String findSub(String str,String regex,int groupIndex){
  	 Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
     Matcher m=p.matcher(str);
     if(m.find()){
    	 return m.group(groupIndex);
     }
     return null;
  }
  
  public static String findSubString(String str,String regex) {
    Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    String result=m.find()?str.substring(m.start(),m.end()):null;
    return result;
  }
  
  public static boolean startWith(String str,String sub) {
    Pattern p = Pattern.compile("^".concat(sub),Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    return m.find();
  }

  public static Matcher getMatcher(String str,String regex) {
    Pattern p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    return m;
  }
  
  public static boolean endWith(String str,String subString) {
    Pattern p = Pattern.compile(subString.concat("$"),Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    return m.find();
  }
  
  /**
   *intersection,交集 
   * @param str
   * @param subString
   * @return
   */
  public static boolean isIntersect(String str,String subString) {
    Pattern p = Pattern.compile(subString.concat("+"),Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    return m.find();
  }
  
  public static boolean isIntersect(String str,String subString,boolean insensitive){
    Pattern p=null;
    if(insensitive)
      p=Pattern.compile(subString.concat("+"),Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    else
      p=Pattern.compile(subString.concat("+"));
    Matcher m=p.matcher(str);
    return m.find();
  }

  public static String replaceAll(String str,String regex,String replacement,boolean insensitive){
    Pattern p=null;
    if(insensitive)
      p=Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    else
      p=Pattern.compile(regex);
    Matcher m=p.matcher(str);
    return m.replaceAll(replacement);
  }
  
  public static String replaceAll(String str,String regex,String replacement) {
    Pattern p=Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    return m.replaceAll(replacement);
  }
  
  public static String replaceFirst(String str,String findstr,String replacement,boolean insensitive){
    Pattern p=null;
    if(insensitive)
      p=Pattern.compile(findstr,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    else
      p=Pattern.compile(findstr);
    Matcher m=p.matcher(str);
    return m.replaceFirst(replacement);
  }
  
  public static String replaceFirst(String str,String findstr,String replacement) {
    Pattern p=Pattern.compile(findstr,Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    Matcher m=p.matcher(str);
    return m.replaceFirst(replacement);
  }

  /**
   * String array must be sequent
   */
  public static boolean findAnd(String str,String[] findKeys) {
  	if(findKeys==null||findKeys.length==0)
  		return false;  	
    StringBuffer regex=new StringBuffer();
    for(int i=0;i<findKeys.length;i++){
      regex.append(findKeys[i]).append(".*?");
    }
    return Pattern.compile(regex.toString(),Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(str).find();
  }
  
  public static boolean findOr(String str,String[] findKeys) {
  	String regex="";
  	if(findKeys==null||findKeys.length==0)
  		return false;
  	for(int i=0,n=findKeys.length;i<n;i++) {
  		regex+=quota(findKeys[i]);
  		if(i<n-1)
  			regex+="|";
  	}
    return Pattern.compile(regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(str).find();
  }
/*  
  public static boolean find(String str,String findstr) {
    return Pattern.compile(findstr,Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(str).find();
  }*/
  
  public static boolean matches(String str,String findstr) {
    return Pattern.compile(findstr,Pattern.CASE_INSENSITIVE|Pattern.DOTALL).matcher(str).matches();
  }
  
  /**把注释替换为空格，已经忘记用在什么场景了*/
  public static String filterComment(String s) {
    return EasyRegex.replaceAll(s,"(/\\*(.*?)\\*/)|(--(.*?)\\n)|(//(.*?)\\n)"," ");
  }
  
  public static String quota(String v) {
  	if(v==null)
  		return v;
  	else
  		return v.replaceAll("\\|","\\\\|");
  }
  
  /**用于匹配习惯的模糊表达方式如*.do*/
  public static boolean includes(String str,String includes) {
  	return matches(str,convertToRegex(includes));
  }
  
  /**
   * 把习惯的查询表达方式（如*.do,*.xml）替换为正则（.*?\\.do|.*?\\.xml）
   * */
  public static String convertToRegex(String includes) {
  	if(includes!=null&&!includes.equals(""))
			includes=includes.replace(".","\\.").replace("*",".*?").replaceAll(" +|,+|;+","|");
  	return includes;
  }
  
  /**转义字符串，将正则的关键字转义*/
  public static String escape(String str) {
  	return ReUtil.escape(str);
  }
  
  public static void test() {
    esdk.tool.assertEquals(findSub("jdbc:mysql://192.168.0.9:3306/zss?autoReconnect=true","^.*?//(.*?):?(\\d*)/(\\w*)(\\?.*)",1),"192.168.0.9");
    esdk.tool.assertEquals(findSub("jdbc:mysql://192.168.0.9:3306/zss?autoReconnect=true","^.*?//(.*?):?(\\d*)/(\\w*)(\\?.*)").length,4);
  	esdk.tool.assertEquals("beginDate".matches("^(?!.*?Time$).*$"));
  	esdk.tool.assertEquals(!"beginTime".matches("^(?!.*Time$).*$"));
  	esdk.tool.assertEquals(!"startTime".matches("^.*(?<!start|begin|end)Time$"));
  	esdk.tool.assertEquals("createTime".matches("^.*(?<!start|begin|end)Time$"));
  	esdk.tool.assertEquals("createDate".matches("^create(?!Time).*$"));
  	esdk.tool.assertEquals("abcd".replaceAll("(a)(b)(cd)","b$2$3"),"bbcd");
  	esdk.tool.assertEquals("abc\nefg".replaceAll("(a[^\n]+)\r?\n([^\n]+g)","$1d$2"),"abcdefg");
    esdk.tool.assertEquals(EasyRegex.indexOf("ABbc","b")==1);
    Matcher matcher=EasyRegex.getMatcher("ababa","b");
    if(matcher.find()) esdk.tool.assertEquals(matcher.start(),1);
    if(matcher.find()) esdk.tool.assertEquals(matcher.start(),3);
    matcher=EasyRegex.getMatcher("ababa","b");
    if(matcher.matches()) esdk.tool.assertEquals(matcher.end(),2);
    esdk.tool.assertEquals(!"AB".startsWith("a"));
    esdk.tool.assertEquals(startWith("AB","a"));
    esdk.tool.assertEquals(!startWith("AB","b"));
    esdk.tool.assertEquals(endWith("AB","b"));
    esdk.tool.assertEquals(!endWith("AB","a"));
    esdk.tool.assertEquals(isIntersect("abc","AB"));
    esdk.tool.assertEquals(!isIntersect("abc","BA"));
    esdk.tool.assertEquals(!isIntersect("AB","c"));
    esdk.tool.assertEquals(replaceAll("ABAC","a","Q").equals("QBQC"));
    esdk.tool.assertEquals(replaceFirst("ABAC","a","Q",true).equals("QBAC"));
    esdk.tool.assertEquals(findOr("2007-05-07 16:03:03",new String[] {" ","-","#",null}));
    esdk.tool.assertEquals(findOr("2007-05-07 16:03:03",new String[] {"50","06","2007"}));
    esdk.tool.assertEquals(findOr("memberId=111 || memberId=222",new String[] {" and ","or "," || "," && "}));
    esdk.tool.assertEquals(!findOr("memberId=[111,222]",new String[] {" and ","or "," || "," && "}));
    esdk.tool.assertEquals(!findOr("2007-05-07",new String[] {"70","50","60"}));
    esdk.tool.assertEquals(findAnd("2007-05-07 16:03:03",new String[] {"-"," ",":"}));
    esdk.tool.assertEquals(!findAnd("abc",new String[] {"b","a","c"}));
    esdk.tool.assertEquals(!findAnd("2007-05-07",new String[] {"20","05","06"}));
    esdk.tool.assertEquals("/*dgfg\t\r\n 4*/".matches("/\\*([a-z,A-Z,0-9,\\s,\\n,\\r,\\t]*)\\*/"));
    esdk.tool.assertEquals(!"/*dg天空\t\r\nfg4*/".matches("/\\*(.*?)\\*/"));
    esdk.tool.assertEquals(matches("/*dg天空\t\r\nfg4*/","/\\*(.*?)\\*/"));
    esdk.tool.assertEquals(!"/*dgf要g\r\n 4/".matches("/\\*[a-z,A-Z,0-9,\\s,\\n,\\r,\\t]*\\*/")); 
    esdk.tool.assertEquals(!"--dgf我g\r\n\4\r\n".matches("--.*\\n"));
    esdk.tool.assertEquals(matches("--dgfg\r\n","--.*\\n")); 
    esdk.tool.assertEquals(matches("//dgfg\r\n 4r\n","//.*\\n")); 
    esdk.tool.assertEquals("~1`+_)(*&^%$#?><,.?中国人".matches(".*"));
    esdk.tool.assertEquals("abc".matches("a.*b.*c.*"));
    esdk.tool.assertEquals("abc".matches(".*b.*"));
    esdk.tool.assertEquals("abcd".matches("\\Qab\\Ecd"));
    esdk.tool.assertEquals("我有个\\$符号\\{\\}",escape("我有个$符号{}"));
  }
  
  public static void main(String[] args){
    test();
  }

  
}
