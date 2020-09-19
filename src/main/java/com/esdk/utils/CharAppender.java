package com.esdk.utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class CharAppender implements CharSequence,Serializable{
  private static final long serialVersionUID=8009500972373044344L;
  private char[] value;
  private int count;
  private char delimiter,qualifier;
  private boolean isModified=false;
  
  public CharAppender() {
    this('|','"');
  }
  
  public CharAppender(char delimiter0) {
    this(delimiter0,'"');
  }
  
  public CharAppender(char delimiter0,char qualifier0) {
    delimiter=delimiter0;
    qualifier=qualifier0;
    count=0;
    value=new char[16];
  }
  
  public char charAt(int index){
    return value[index];
  }

  public int length(){
    return count;
  }

  public CharAppender append(Collection c) {
   for(Iterator iterator=c.iterator();iterator.hasNext();){
  	 append(EasyStr.valueOf(iterator.next()));		
	}
    return this;
  }
  
  public CharAppender append(CharSequence[] css) {
    for(int i=0;i<css.length;i++){
      append(css[i]);
    }
    return this;
  }
  
  public CharAppender append(CharSequence cs) {
    if(cs==null)
      cs="null";
    int newCount=cs.length()+count+1;
    if(value.length<newCount) {
      value=TString.copyOf(value,newCount);
    }
    if(isModified) {
      value[count++]=delimiter;
    }
    for(int i=0;i<cs.length();i++){
      value[count++]=cs.charAt(i);
    }
    isModified=true;
    return this;
  }
  
  public CharAppender add(CharSequence[] css){
    for(int i=0;i<css.length;i++){
      add(css[i]);
    }
    return this;
  }
  
  //如果包含有特殊的字符,会自动添加限定符,类似于csv文件的格式处理
  public CharAppender add(CharSequence cs){
    if(!contains(cs,delimiter))
      return append(cs);
    else{
      int newCount=cs.length()+count+3;
      if(value.length<newCount){
        value=TString.copyOf(value,newCount);
      }
      if(isModified){
        value[count++]=delimiter;
      }
      value[count++]=qualifier;
      for(int i=0;i<cs.length();i++){
        value[count++]=cs.charAt(i);
      }
      value[count++]=qualifier;
      isModified=true;
      return this;
    }
  }

  static boolean contains(CharSequence cs,char c) {
    if(cs==null)return false;
    for(int i=0;i<cs.length();i++){
      if(cs.charAt(i)==c)
        return true;
    }
    return false;
  }
  
  public String toString() {
    return new String(value,0,count);
  }
  
  public CharSequence subSequence(int start,int end){
    if(start<0){
      throw new StringIndexOutOfBoundsException(start);
    }
    if(end>count){
      throw new StringIndexOutOfBoundsException(end);
    }
    if(start>end){
      throw new StringIndexOutOfBoundsException(end-start);
    }
    CharSequence result=this;
    return ((start==0)&&(end==count))?result:new String(value,start,end-start);
  }
  
  public int hashCode(){
    int hash=0;
    for(int i=0,len=count;i<len;i++){
      hash=31*hash+value[i];
    }
    return hash;
  }
  
  public boolean equals(Object obj) {
    if(obj==null ) return false;
    if(obj==this) return true;
    if(obj instanceof CharSequence)
      return this.hashCode()==obj.hashCode();
    return false;
  }
  
  private static void test(){
    CharAppender ca=new CharAppender('|');
    TestHelper.assertEquals(ca.toString().length()==0);
    TestHelper.assertEquals(ca.hashCode(),0);
    TestHelper.assertEquals(new CharAppender().append("").toString().length(),0);
    TestHelper.assertEquals(ca.append("ab").toString().equals("ab"));
    TestHelper.assertEquals(ca.hashCode(),"ab".hashCode());
    TestHelper.assertEquals(ca.append("cd").toString().equals("ab|cd"));
    TestHelper.assertEquals(ca.append("").toString().equals("ab|cd|"));
    TestHelper.assertEquals(ca.add("e|f").toString(),"ab|cd||\"e|f\"");
    TestHelper.assertEquals(ca.add("|").toString(),"ab|cd||\"e|f\"|\"|\"");
    TestHelper.assertEquals(ca.add((String)null).toString(),"ab|cd||\"e|f\"|\"|\"|null");
    TestHelper.assertEquals(ca.hashCode(),"ab|cd||\"e|f\"|\"|\"|null".hashCode());
    TestHelper.assertEquals(new CharAppender(',').append(Arrays.asList(new String[] {"a","b","c"})).toString(),"a,b,c");
  }
  
  public static void main(String[] args){
    test();
  }
}
