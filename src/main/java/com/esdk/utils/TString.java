package com.esdk.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import com.esdk.esdk;

/**
 * this class can easy handle multi line string, has easy way to transfer between multi line string and string array.
 * @author Franky.Fan
 *
 */
public class TString implements java.io.Serializable, CharSequence,Comparable,Cloneable{
  private static final long serialVersionUID=4197784226772778221L;
  private static final char SpecialChar='\r';
  public static final char LF='\n';
  private StringBuffer value=new StringBuffer(4);
  
  public TString(){
  }
  
  public TString(char[] arr){
    load(arr);
  }
  
  public TString(CharSequence s){
    load(s);
  }
  
  public TString(Collection collection){
    load(collection);
  }
  
  public TString(CharSequence[] csArray){
    load(csArray);
  }
  
  public void load(CharSequence[] array){
    for(int i=0;i<array.length;i++){
      append(array[i]);
      if(i<array.length-1)
      	appendNewLine();
    }
  }
  
  public void load(Collection collection){
  	load((CharSequence[])collection.toArray(new CharSequence[0]));
  }
  
  public TString load(char[] str) {
    value=new StringBuffer();
    append(str);
    return this;
  }
  
  public TString load(CharSequence s){
    if(isExistSpecialChar(s,0,s.length()))
      value=new StringBuffer().append(getFilteredSpecialChar(s,0,s.length()));
    else
      value=new StringBuffer().append(s);
    return this;
  }
  
  public TString append(StringBuffer sb) {
    value.append(getFilteredSpecialChar(sb,0,sb.length()));
    return this;
  }
  
  public TString append(String str) {
    value.append(getFilteredSpecialChar(str,0,str.length()));
    return this;
  }
  
  public TString append(char str[]) {
    str=getFilteredSpecialChar(str,0,str.length);
    value.append(str,0,str.length);
    return this;
  }
  
  public TString insert(int dstOffset, CharSequence s,int start,int end){
    if(isExistSpecialChar(s,start,end)) {
      char[] str=getFilteredSpecialChar(s,start,end);
      value.insert(dstOffset,str,0,str.length);
    }
    else
      value.insert(dstOffset,toCharArray(s),start,end);
    return this;
  }
  
  public TString insert(int index,char str[],int offset,int len){
    if(isExistSpecialChar(str,offset,offset+len)) {
      str=getFilteredSpecialChar(str,offset,offset+len);
      value.insert(index,str,0,str.length);
    }
    else
      value.insert(index,str,offset,len);
    return this;
  }

  private int indexOf(char c,int findcount) {
    int ipos=0;
    int len=value.length();
    for(int i=0;i<findcount&&ipos>=0;i++){
      ipos=indexOf(c,ipos,len);
      if(ipos>=0)
        ipos++;
    }
    return ipos;
  }
  
  public TString insertLineBefore(int index,CharSequence str) {
    int start=indexOf(LF,index);
    if(start>=0) {
      insert(start,String.valueOf(str)+LF,0,str.length()+1);
    }
    return this;
  }
  
  public TString insertLineAfter(int index,CharSequence str) {
    int start=indexOf(LF,index);
    if(start>=0) {
      int end=indexOf(LF,start+1,value.length());
      if(end<0)
        start=value.length();
      else
        start=end;
      str=new StringBuffer().append(LF).append(str);
      insert(start,str,0,str.length());
    }
    return this;
  }
  
  public TString replaceLine(int index,CharSequence str) {
    int start=indexOf(LF,index);
    if(start>=0) {
      int end=indexOf(LF,start,value.length());
      if(end<=0)
        end=value.length();
      value.delete(start,end);
      insert(start,str,0,str.length());
    }
    return this;
  }
  
  public TString deleteLine(int index) {
    int start=indexOf(LF,index);
    if(start>=0) {
      int end=indexOf(LF,start+1,value.length());
      if(end<0) {
        start--;
        end=value.length();
      }
      else
        end++;
      value.delete(start,end);
    }
    return this;
  }
  
  private int indexOf(char c,int start,int end) {
    for(int i=start;i<value.length()&&i<end;i++){
      if(value.charAt(i)==c)
        return i;
    }
    return -1;
  }
  
  public TString appendLine(CharSequence s) {
		appendNewLine();
    return append(s);
  }
  
  public TString append(CharSequence s) {
    return append(s,0,s.length());
  }

  public TString append(CharSequence s, int start, int end) {
    if(isExistSpecialChar(s,start,end)) {
      char[] str=getFilteredSpecialChar(s,start,end);
      value.append(str,0,str.length);
    }
    else
      value.append(toCharArray(s),start,end);
    return this;
  }

  public TString append(char str[], int offset, int len) {
    if(isExistSpecialChar(str,offset,offset+len)) {
      str=getFilteredSpecialChar(str,offset,offset+len);
      value.append(str,0,str.length);
    }
    else
      value.append(str,offset,len);
    return this;
  }
  
  public String[] split() {
    return split(String.valueOf(LF));
  }
  
  public String[] split(String regex) {
    return Pattern.compile(regex).split(value, 0);
  }
  
  private boolean isExistSpecialChar(CharSequence charSequence,int start,int end) {
    boolean result=false;
    for(int i=end-1;i>=start;i--){
      if(charSequence.charAt(i)==SpecialChar) {
        result=true;
        break;
      }
    }
    return result;
  }
  
  //first invoke isExistSpecialChar,if true then invoke getFilteredSpecialChar
  private char[] getFilteredSpecialChar(CharSequence charSequence,int start,int end) {
    char[] result=new char[end-start];
    int newCount=0;
    for(int i=start;i<end;i++){
      if(charSequence.charAt(i)!=SpecialChar)
        result[newCount++]=charSequence.charAt(i);
    }
    result=copyOf(result,newCount);
    return result;
  }
  
  private boolean isExistSpecialChar(char str[],int start,int end) {
    boolean result=false;
    for(int i=end-1;i>=start;i--){
      if(str[i]==SpecialChar) {
        result=true;
        break;
      }
    }
    return result;
  }
  
  //first invoke isExistSpecialChar,if true then invoke getFilteredSpecialChar
  private char[] getFilteredSpecialChar(char[] str,int start,int end) {
    char[] result=new char[end-start];
    int newCount=0;
    for(int i=start;i<end;i++){
      if(str[i]!=SpecialChar)
        result[newCount++]=str[i];
    }
    result=copyOf(result,newCount);
    return result;
  }
  
  static char[] copyOf(char[] original,int newLength){
    char[] copy=new char[newLength];
    System.arraycopy(original,0,copy,0,Math.min(original.length,newLength));
    return copy;
  }
  
  public TString appendNewLine() {
    value.append(LF);
    return this;
  }
  
  public boolean equals(CharSequence anObject){
    if(this==anObject){
      return true;
    }
    if(anObject instanceof CharSequence){
      return this.toString().equals(anObject.toString());
    }
    return false;
  }

  public int getLineCount(){
    int result=1;
    for(int i=0;i<value.length();i++){
      if(value.charAt(i)==LF)
        result++;
    }
    return result;
  }
  
  public String toString(){
    return value.toString();
  }

  public boolean save(File file) throws IOException{
    return EasyFile.saveToFile(file,this.toString(),true);
  }

  public Writer save(Writer writer) throws IOException{
    writer.write(toCharArray(),0,length());
    return writer;
  }

  public OutputStream save(OutputStream os) throws IOException{
    os.write(value.toString().getBytes());
    return os;
  }

  public TString load(File file) throws IOException{
    this.load(EasyFile.loadFromFile(file));
    return this;
  }

  public TString load(InputStream is) throws Exception {
    load(EasyStr.isToStr(is));
    is.close();
    return this;
  }
  
  public TString load(Reader reader) throws IOException{
    value=new StringBuffer();
    char[] buffer=new char[1024];
    int len=0;
    while((len=reader.read(buffer))>0) {
      append(buffer,0,len);
    }
    reader.close();
    return this;
  }

  public char charAt(int index){
    return value.charAt(index);
  }

  public int length(){
    return value.length();
  }

  public CharSequence subSequence(int start,int end){
    return value.subSequence(start,end);
  }

  public String getLine(int index){
    int start=indexOf(LF,index);
    /*if(start>0)
      start++;*/
    if(start>=0){
      if(start==value.length())
        return "";
      else{
        int end=indexOf(LF,start,value.length());
        if(end<0)
          end=value.length();
        return value.substring(start,end);
      }
    }
    else
      throw new IndexOutOfBoundsException("expect line index="+index+", but line count="+getLineCount());
  }
  
  public String[] getLines(int index,int len) {
    List result=new ArrayList(len);
    char[] c=new char[value.length()];
    for(int start=indexOf(LF,index),end=start;end<value.length()&&result.size()<len;end++){
      c[end]=value.charAt(end);
      if(c[end]==LF){
        result.add(new String(c,start,end-start));
        start=end+1;
      }
      if(end==value.length()-1) {
        if(c[end]==LF)
          result.add("");
        else
          result.add(new String(c,start,value.length()-start));
      }
    }
    return (String[])result.toArray(new String[result.size()]);
  }
  
  public String[] toArray() {
    List result=new ArrayList(8);
    char[] c=new char[value.length()];
    for(int end=0,start=0;end<value.length();end++){
      c[end]=value.charAt(end);
      if(c[end]==LF){
        result.add(new String(c,start,end-start));
        start=end+1;
      }
      if(end==value.length()-1) {
        if(c[end]==LF)
          result.add("");
        else
          result.add(new String(c,start,value.length()-start));
      }
    }
    return (String[])result.toArray(new String[result.size()]);
  }
  
  public String getFirstLine() {
    if(value.length()==0)
      return "";
    else
      return getLine(0);
  }
  
  public String getLastLine() {
    for(int i=value.length()-1;i>=0;i--){
      if(value.charAt(i)==LF)
        return value.substring(i+1);
    }
    return value.toString();
  }
  
  public TString clear() {
    value.delete(0,value.length());
    return this;
  }
  
  public Object clone() {
    return new TString(this.value);
  }
  
  public char[] toCharArray(){
    char result[]=new char[value.length()];
    value.getChars(0,result.length,result,0);
    return result;
  }
  
  public int compareTo(Object obj){
    if(obj==this)return 0;
    if(obj==null)return 1;
    if(obj instanceof CharSequence){
      CharSequence cs=(CharSequence)obj;
      int l1=this.length(),l2=cs.length();
      int minlen=Math.min(l1,l2);
      for(int i=0;i<minlen;i++){
        char v1=this.charAt(i),v2=cs.charAt(i);
        if(v1!=v2)
          return v1-v2;
      }
      return l1>l2?1:l1==l2?0:-1;
    }
    else 
      return -1;
  }

  public int hashCode(){
    int hash=0;
    for(int i=0,len=value.length();i<len;i++){
      hash=31*hash+value.charAt(i);
    }
    return hash;
  }  
  public static char[] toCharArray(CharSequence cs){
    char[] result=null;
    if(cs instanceof String)
      result=((String)cs).toCharArray();
    else if(cs instanceof StringBuffer){
      result=new char[cs.length()];
      ((StringBuffer)cs).getChars(0,cs.length(),result,0);
    }
    else{
      result=new char[cs.length()];
      for(int i=0;i<cs.length();i++){
        result[i]=cs.charAt(i);
      }
    }
    return result;
  }
  
  public boolean contains(CharSequence cs) {
    return indexOf(toCharArray(),toCharArray(cs))>=0;
  }
 
  public int indexOf(CharSequence cs,boolean isIngoreCase) {
    return indexOf(toCharArray(),toCharArray(cs),isIngoreCase);
  }
  
  public int indexOf(CharSequence cs,int start,boolean isIngoreCase) {
    char[] source=toCharArray(),target=toCharArray(cs);
    return indexOf(source,0,source.length,target,0,target.length,start,isIngoreCase);
  }
  
  public int indexOf(CharSequence cs) {
    return indexOf(toCharArray(),toCharArray(cs),false);
  }
  
  public boolean contains(CharSequence cs,boolean isIngoreCase) {
    return indexOf(toCharArray(),toCharArray(cs),isIngoreCase)>=0;
  }
 
  public static int indexOf(char[] source,char[] target){
    return indexOf(source,0,source.length,target,0,target.length,0,false);
  }
  
  public static int indexOf(char[] source,char[] target,boolean isIngoreCase){
    return indexOf(source,0,source.length,target,0,target.length,0,isIngoreCase);
  }
  
  public static int indexOf(char[] source, int sourceOffset, int sourceCount,char[] target, int targetOffset, int targetCount,int fromIndex,boolean isIgnoreCase){
    if(fromIndex>=sourceCount){
      return (targetCount==0?sourceCount:-1);
    }
    if(fromIndex<0){
      fromIndex=0;
    }
    if(targetCount==0){
      return fromIndex;
    }
    char first= target[targetOffset];
    first=isIgnoreCase?Character.toUpperCase(first):first;
    int max=sourceOffset+(sourceCount-targetCount);
    for(int i=sourceOffset+fromIndex;i<=max;i++){
      /* Look for first character. */
      if(isIgnoreCase)
        source[i]=Character.toUpperCase(source[i]);
      if(source[i]!=first||(isIgnoreCase&&Character.toUpperCase(source[i])!=first)){
        while(++i<=max){
          if(source[i]!=first){
            if(isIgnoreCase){
              if(Character.toUpperCase(source[i])==first)
                break;
            }
            else{
              break;
            }
          }
        }
      }
      /* Found first character, now look at the rest of v2 */
      if(i<=max){
        int j=i+1;
        int end=j+targetCount-1;
        for(int k=targetOffset+1;j<end;j++,k++){
          if(source[j]==target[k])
            continue;
          else if(isIgnoreCase&&Character.toUpperCase(source[j])==Character.toUpperCase(target[k]))
            continue;
          else
            break;
        }
        if(j==end){
          /* Found whole string. */
          return i-sourceOffset;
        }
      }
    }
    return -1;
  }
  
  public TString trim() {
  	value=new StringBuffer(this.value.toString().replaceAll("\\n\\s*\\n","\n").trim());
  	return this;
  }
  
  public Iterator iterator() {
    return new itr();
  }
  
  class itr implements Iterator{
    private int start=0,end=0,removeStart=0,removeEnd=0;
    public boolean hasNext(){
      return start==0||start<value.length();
    }
    
    private int getNextChar(int startt,char c) {
      if(startt>=0){
        if(startt==value.length())
          return startt;
        else{
          int endd=indexOf(c,startt,value.length());
          if(endd<0)
            endd=value.length();
          return endd;
        }
      }
      return -1;
    }

    public Object next(){
      if(start<0)
        throw new NoSuchElementException();
      else {
        end=getNextChar(start,LF);
        String result=value.substring(start,end);
        removeStart=start;
        removeEnd=end==value.length()?end:end+1;
        start=end+1;
        return result;
      }
    }

    public void remove(){
      if(removeStart<removeEnd&&removeStart>=0) {
        value.delete(removeStart,removeEnd);
        start=removeStart;
      }
    }
  }
  
  private static void test() {
    TString s=new TString("a\r\nb\nc\r\n");
    /*Tools.assertEquals(s);*/
    esdk.tool.assertEquals(new TString(s.split("\n")).toString(),"a\nb\nc");
    esdk.tool.assertEquals(new TString(s.toArray()).equals("a\nb\nc\n"));
    esdk.tool.assertEquals(s.length()==6);
    esdk.tool.assertEquals(s.equals("a\nb\nc\n"));
    esdk.tool.assertEquals(s.hashCode()==new TString("a\nb\nc\n").hashCode());
    esdk.tool.assertEquals(s.compareTo("a\nb\nc\n\n"),-1);
    esdk.tool.assertEquals(s.compareTo("a\nb\nc"),1);
    esdk.tool.assertEquals(s.compareTo("b\nb\nc"),-1);
    s.append("d\r\ne");
    /*Tools.assertEquals(s);*/
    esdk.tool.assertEquals(s.equals("a\nb\nc\nd\ne"));
    s.append(new char[] {LF,'f','\r',LF,'g','\r',LF});
    /*Tools.assertEquals(s);*/
    esdk.tool.assertEquals(s.equals("a\nb\nc\nd\ne\nf\ng\n"));
    esdk.tool.assertEquals(s.getLineCount()==8);
    esdk.tool.assertEquals(s.getLine(2).equals("c"));
    esdk.tool.assertEquals(s.getLine(7).equals(""));
    esdk.tool.assertEquals(new TString(s.getLines(2,3)).equals("c\nd\ne"));
    esdk.tool.assertEquals(s.getFirstLine().equals("a"));
    esdk.tool.assertEquals(s.getLastLine().equals(""));
    try{
      File file=new File("./testfiles/testtstring.txt");
      s.save(file);
      s.clear();
      esdk.tool.assertEquals(s.length()==0);
      esdk.tool.assertEquals(s.length()==0);
      s.load(file);
      esdk.tool.assertEquals(s.equals("a\nb\nc\nd\ne\nf\ng\n"));
      s.save(new FileWriter(file)).close();
      esdk.tool.assertEquals(s.load(new FileReader(file)).equals("a\nb\nc\nd\ne\nf\ng\n"));
      esdk.tool.assertEquals(file.delete());
    }
    catch(IOException e){
      e.printStackTrace();
    }
    s.replaceLine(6,"ggg");
    esdk.tool.assertEquals(s.getLine(6).equals("ggg"));
    esdk.tool.assertEquals(s.equals("a\nb\nc\nd\ne\nf\nggg\n"));
    s.replaceLine(7,"h");
    esdk.tool.assertEquals(s.equals("a\nb\nc\nd\ne\nf\nggg\nh"));
    s.deleteLine(7);
    s.deleteLine(0);
    s.deleteLine(1);
    esdk.tool.assertEquals(s.toString(),"b\nd\ne\nf\nggg");
    s.insertLineBefore(0,"a");
    s.insertLineBefore(2,"c");
    esdk.tool.assertEquals(s.equals("a\nb\nc\nd\ne\nf\nggg"));
    s.insertLineBefore(0,"ffy");
    esdk.tool.assertEquals(s.equals("ffy\na\nb\nc\nd\ne\nf\nggg"));
    s.insertLineAfter(7,"h\r\ni");
    esdk.tool.assertEquals(s.equals("ffy\na\nb\nc\nd\ne\nf\nggg\nh\ni"));
    s.insertLineAfter(7,"8");
    esdk.tool.assertEquals(s.equals("ffy\na\nb\nc\nd\ne\nf\nggg\n8\nh\ni"));
    esdk.tool.assertEquals(new TString(s.toArray()).compareTo("ffy\na\nb\nc\nd\ne\nf\nggg\n8\nh\ni")==0);
    
    esdk.tool.assertEquals(indexOf("abcd".toCharArray(),"aBc".toCharArray())==-1);
    esdk.tool.assertEquals(indexOf("abcd".toCharArray(),"aBcD".toCharArray(),true)==0);
    esdk.tool.assertEquals(new TString("abcd").contains("aBc")==false);
    esdk.tool.assertEquals(new TString("abcd").contains("CD",true));
    esdk.tool.assertEquals(new TString("abcd").indexOf("Bc",true)==1);
    esdk.tool.assertEquals(new TString("abcd").indexOf("cD",1,true)==2);
    esdk.tool.assertEquals(new TString("abcd").indexOf("ABCDE",1,true)==-1);
    esdk.tool.assertEquals(new TString("abcd").contains("De",true)==false);
    esdk.tool.assertEquals(new TString("a\n\n\r\nb\nc").split().length>3);
    //test iterator() ,next(),hasNext();
    int iterlen=0;
    TString ts=new TString("\na\nb\r\nc\r");
    String[] ss=new String[ts.getLineCount()];
    for(Iterator iter=ts.iterator();iter.hasNext();){
      ss[iterlen++]=(String)iter.next();
    }
    esdk.tool.assertEquals(new TString(ss).toString(),"\na\nb\nc");
    esdk.tool.assertEquals(iterlen==ss.length);
    
    //test iterator(),next(),remove();
    iterlen=0;
    ts=new TString("\naa\nb\r\nc\r\n\n");
    ss=new String[ts.getLineCount()];
    for(Iterator iter=ts.iterator();iter.hasNext();){
      ss[iterlen++]=(String)iter.next();
      iter.remove();
    }
    esdk.tool.assertEquals(new TString(ss).toString(),"\naa\nb\nc\n\n");
    esdk.tool.assertEquals(iterlen==ss.length);
    esdk.tool.assertEquals(new TString(Arrays.asList(new String[] {"a","b","c"})).toString(),"a\r\nb\r\nc");
    esdk.tool.assertEquals(new TString("\r\n\r\na\r\n\r\nb\r\n \r\n\n\n").trim().toString(),"a\nb");
  }

  public static void main(String[] args){
    test();
  }
}
