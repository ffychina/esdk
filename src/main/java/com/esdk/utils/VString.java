package com.esdk.utils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;

import com.esdk.esdk;

/**
 * 不区分大小写的字符串类(转发String类)
 * hasCode  出来的数为全转换为小写后的hasCode
 * equals	可直接使用String、VString类进行对比(小写)
 * compareTo 使用compareToIgnoreCase进行不区分大小写的对比
 */
public class VString implements java.io.Serializable,Comparable<CharSequence>,CharSequence{
  private static final long serialVersionUID=6901851745708225149L;
  private int hash=0;
  private String fs;

  public VString(CharSequence cs){
    this.fs=cs.toString();
  }

  public VString(String fs){
    this.fs=fs;
  }

  public VString(char value[]){
    String s=new String(value);
    this.fs=s;
  }

  public char charAt(int index){
    return fs.charAt(index);
  }

  public int codePointAt(int index){
    return fs.codePointAt(index);
  }

  public int codePointBefore(int index){
    return fs.codePointBefore(index);
  }

  public int codePointCount(int beginIndex,int endIndex){
    return fs.codePointCount(beginIndex,endIndex);
  }

  public int compareTo(CharSequence anotherString){
    return fs.compareToIgnoreCase(anotherString.toString());
  }

  public int compareToIgnoreCase(String str){
    return fs.compareToIgnoreCase(str);
  }

  public String concat(String str){
    return fs.concat(str);
  }

  public boolean contains(CharSequence s){
    return fs.contains(s);
  }

  public boolean contentEquals(CharSequence cs){
    return fs.contentEquals(cs);
  }

  public boolean contentEquals(StringBuffer sb){
    return fs.contentEquals(sb);
  }

  public boolean endsWith(String suffix){
    return fs.endsWith(suffix);
  }

  public boolean equals(Object anObject){
  	if(anObject==null)
  		return false;
    if(this==anObject){
      return true;
    }
    if(anObject instanceof String){
      String anotherString=anObject.toString();
      int n=fs.length();
      if(n==anotherString.length()){
        char v1[]=new char[fs.length()]; // value;
        String s1=fs.toLowerCase();
        s1.getChars(0,s1.length(),v1,0);
        char v2[]=new char[anotherString.length()]; // anotherString.value;
        String s2=anotherString.toLowerCase();
        s2.getChars(0,s2.length(),v2,0);
        int i=0; // offset;
        int j=0; // anotherString.offset;
        while(n--!=0){
          if(v1[i++]!=v2[j++])
            return false;
        }
        return true;
      }
    }
    else if(anObject instanceof VString){
      String anotherString=((VString)anObject).toString();
      // cando = true;
      int n=fs.length();
      if(n==anotherString.length()){
        char v1[]=new char[fs.length()]; // value;
        String s1=fs.toLowerCase();
        s1.getChars(0,s1.length(),v1,0);
        char v2[]=new char[anotherString.length()]; // anotherString.value;
        String s2=anotherString.toLowerCase();
        s2.getChars(0,s2.length(),v2,0);
        int i=0; // offset;
        int j=0; // anotherString.offset;
        while(n--!=0){
          if(v1[i++]!=v2[j++])
            return false;
        }
        return true;
      }
    } // else{ cando = false; }
    return false;
  }

  public boolean equalsIgnoreCase(String anotherString){
    return fs.equalsIgnoreCase(anotherString);
  }

  public byte[] getBytes(){
    return fs.getBytes();
  }

  public byte[] getBytes(Charset charset){
    return fs.getBytes(charset);
  }

  @Deprecated public void getBytes(int srcBegin,int srcEnd,byte[] dst,int dstBegin){
    fs.getBytes(srcBegin,srcEnd,dst,dstBegin);
  }

  public byte[] getBytes(String charsetName) throws UnsupportedEncodingException{
    return fs.getBytes(charsetName);
  }

  public void getChars(int srcBegin,int srcEnd,char[] dst,int dstBegin){
    fs.getChars(srcBegin,srcEnd,dst,dstBegin);
  }

  public int hashCode(){
    // return fs.hashCode();
    int h=hash;
    if(h==0){
      String s1=fs.toLowerCase();
      int off=0;// offset;
      // char val[] = value;
      char val[]=new char[s1.length()];
      s1.getChars(0,s1.length(),val,0);
      int len=s1.length();
      for(int i=0;i<len;i++){
        h=31*h+val[off++];
      }
      hash=h;
    }
    return h;
  }

  public int indexOf(int ch,int fromIndex){
    return fs.indexOf(ch,fromIndex);
  }

  public int indexOf(int ch){
    return fs.indexOf(ch);
  }

  public int indexOf(String str,int fromIndex){
    return fs.indexOf(str,fromIndex);
  }

  public int indexOf(String str){
    return fs.indexOf(str);
  }

  public String intern(){
    return fs.intern();
  }

  public boolean isEmpty(){
    return fs.isEmpty();
  }

  public int lastIndexOf(int ch,int fromIndex){
    return fs.lastIndexOf(ch,fromIndex);
  }

  public int lastIndexOf(int ch){
    return fs.lastIndexOf(ch);
  }

  public int lastIndexOf(String str,int fromIndex){
    return fs.lastIndexOf(str,fromIndex);
  }

  public int lastIndexOf(String str){
    return fs.lastIndexOf(str);
  }

  public int length(){
    return fs.length();
  }

  public boolean matches(String regex){
    return fs.matches(regex);
  }

  public int offsetByCodePoints(int index,int codePointOffset){
    return fs.offsetByCodePoints(index,codePointOffset);
  }

  public boolean regionMatches(boolean ignoreCase,int toffset,String other,int ooffset,int len){
    return fs.regionMatches(ignoreCase,toffset,other,ooffset,len);
  }

  public boolean regionMatches(int toffset,String other,int ooffset,int len){
    return fs.regionMatches(toffset,other,ooffset,len);
  }

  public String replace(char oldChar,char newChar){
    return fs.replace(oldChar,newChar);
  }

  public String replace(CharSequence target,CharSequence replacement){
    return fs.replace(target,replacement);
  }

  public String replaceAll(String regex,String replacement){
    return fs.replaceAll(regex,replacement);
  }

  public String replaceFirst(String regex,String replacement){
    return fs.replaceFirst(regex,replacement);
  }

  public String[] split(String regex,int limit){
    return fs.split(regex,limit);
  }

  public String[] split(String regex){
    return fs.split(regex);
  }

  public boolean startsWith(String prefix,int toffset){
    return fs.startsWith(prefix,toffset);
  }

  public boolean startsWith(String prefix){
    return fs.startsWith(prefix);
  }

  public CharSequence subSequence(int beginIndex,int endIndex){
    return fs.subSequence(beginIndex,endIndex);
  }

  public String substring(int beginIndex,int endIndex){
    return fs.substring(beginIndex,endIndex);
  }

  public String substring(int beginIndex){
    return fs.substring(beginIndex);
  }

  public char[] toCharArray(){
    return fs.toCharArray();
  }

  public String toLowerCase(){
    return fs.toLowerCase();
  }

  public String toLowerCase(Locale locale){
    return fs.toLowerCase(locale);
  }

  public String toString(){
    return fs.toString();
  }

  public String toUpperCase(){
    return fs.toUpperCase();
  }

  public String toUpperCase(Locale locale){
    return fs.toUpperCase(locale);
  }

  public String trim(){
    return fs.trim();
  }

  public static VString valueOf(boolean value){
    return new VString(String.valueOf(value));
  }

  public static VString valueOf(char value){
    return new VString(String.valueOf(value));
  }

  public static VString valueOf(char[] value){
    return new VString(String.valueOf(value));
  }

  public static VString valueOf(char[] value,int start,int end){
    return new VString(String.valueOf(value,start,end));
  }

  public static VString valueOf(double value){
    return new VString(String.valueOf(value));
  }

  public static VString valueOf(float value){
    return new VString(String.valueOf(value));
  }

  public static VString valueOf(int value){
    return new VString(String.valueOf(value));
  }

  public static VString valueOf(Object value){
    return new VString(String.valueOf(value));
  }

  public static VString valueOf(long value){
    return new VString(String.valueOf(value));
  }

  /**This function ensures that hashCodes that differ only by constant multiples at each bit position have a bounded number of collisions (approximately 8 at default load factor).*/
  static int hash(int h) {
    h ^= (h >>> 20) ^ (h >>> 12);
    return h ^ (h >>> 7) ^ (h >>> 4);
  }
  
  private static void test(){
	  String s1 = "1,2;3\n\tabc";
    VString ns1=new VString(s1);
    VString ns2=new VString("1,2;3\n\taBc");
    esdk.tool.assertEquals(ns1.hashCode(),ns2.hashCode());
    esdk.tool.assertEquals(ns1,ns2);
    esdk.tool.assertEquals(ns1.compareTo(ns2),0);
    HashMap map=new HashMap();
    map.put(ns2,"a");
    esdk.tool.assertEquals(map.get(ns1),"a");
    esdk.tool.assertEquals(map.get(ns2),"a");
    System.out.println(s1.hashCode());
    System.out.println(ns1.hashCode());
    
    System.out.println(hash(s1.hashCode()));
  }

  public static void main(String[] args){
    test();
  }
}