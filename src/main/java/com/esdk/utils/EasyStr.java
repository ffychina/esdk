
package com.esdk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.Character.UnicodeBlock;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Strings;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
/***
 * @author 范飞宇
 * @since 2003.?.?
 */
public class EasyStr{
	public final static String crlf=Constant.CRLF;

	public static String getFileExtName(String filename){ // 注意:后缀名可能不止3个字节
		for(int i=filename.length()-1;i>=0;i--){
			if(filename.substring(i,i+1).equals(".")){
				return filename.substring(i+1);
			}
		}
		return "";
	}

	public static void InputStreamToOutputStream(InputStream is,OutputStream os) throws IOException{
		int bufferSize=1024;
		byte buff[]=new byte[bufferSize];
		int readSize;
		while((readSize=is.read(buff))!=-1){
			os.write(buff,0,readSize);
		}
		is.close();
		os.close();
	}

	public static String isToStr(InputStream is){
		return readerToStr(new InputStreamReader(is),1024);
	}

	public static String isToStr(InputStream is,boolean isAutoClose){
		return readerToStr(new InputStreamReader(is),1024,isAutoClose);
	}
	
	public static String isToStr(InputStream is,String charset) throws UnsupportedEncodingException{
		InputStreamReader isr=charset==null?new InputStreamReader(is):new InputStreamReader(is,charset);
		return readerToStr(isr,1024);
	}

	public static InputStream strToIs(String Value){
		// StringBufferInputStream is=new StringBufferInputStream(Value);
		ByteArrayInputStream is=new ByteArrayInputStream(Value.getBytes());
		return is;
	}

	public static InputStreamReader strToIsr(String Value,String charset) throws Exception{
		InputStream is=new ByteArrayInputStream(Value.getBytes());
		InputStreamReader isr=new InputStreamReader(is,charset);// gb2312
		// ByteArrayInputStream isb=new ByteArrayInputStream(Value.getBytes());
		return isr;
	}

	public static InputStreamReader strToISR(String Value) throws Exception{
		InputStream is=new ByteArrayInputStream(Value.getBytes());
		InputStreamReader isr=new InputStreamReader(is,"GB2312");// gb2312
		// ByteArrayInputStream isb=new ByteArrayInputStream(Value.getBytes());
		return isr;
	}

	public static String readerToStr(Reader isr){
		return readerToStr(isr,4096);
	}

	public static String readerToStr(Reader isr,int BufferSize){
		return readerToStr(isr,BufferSize,true);
	}

	public static String readerToStr(Reader isr,int BufferSize,boolean isCloseReader){
		StringBuffer result=new StringBuffer();
		char[] buf=new char[BufferSize];
		int readlen=0;
		try{
			while((readlen=isr.read(buf))>0){
				result=result.append(buf,0,readlen);
				/*
				 * if(readlen<BufferSize) break;
				 */
			}
			if(isCloseReader)
				isr.close();
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
		return result.toString();
	}

	public static String getFileNameNotExt(String filename){
		for(int i=filename.length()-1;i>=0;i--){
			if(filename.substring(i,i+1).equals(".")){
				return filename.substring(0,i);
			}
		}
		return null;
	}

	public static String SubStringByBytePos(String value,int start){
		return getStringByByteLength(value,start,value.length()-start);
	}

	public static String SubStringByBytePos(String value,int start,int end){
		return getStringByByteLength(value,start,end-start);
	}

	public static String ellipsis(String s,int maxByteLen){
		if(s.getBytes().length>maxByteLen)
			s=getSubASCIILength(s,0,maxByteLen-3)+"...";
		return s;
	}

	public static String ellipseUTF8(String s,int maxByteLen){
		return ellipse(s,maxByteLen,"utf8");
	}

	public static String urlDecode(String value){
		try{
			return URLDecoder.decode(value,Constant.UTF8);
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public static String urlEncode(String value){
		try{
			return URLEncoder.encode(value,Constant.UTF8);
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public static String ellipse(String s,int maxByteLen,String charset){
		byte[] src=null;
		try{
			src=s.getBytes(charset);
			if(src.length>maxByteLen){
				int newlen=src[maxByteLen-1]<0?maxByteLen-3:maxByteLen-4;
				String s1=new String(Arrays.copyOf(src,newlen),charset);
				if((int)s1.charAt(s1.length()-1)==65533)
					s1=s1.substring(0,s1.length()-1);
				return s1+"...";
			}else
				return s;
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public static String ellipse(String s,int bytelen){
		if(isBlank(s))
			return s;
		char[] ch=s.toCharArray();
		bytelen=bytelen*10;
		int vlen=0,rlen=0,offset=20;
		for(int n=ch.length;rlen<n;){
			int chlen=isChinese(ch[rlen])?20:(ch[rlen]>='A'&&ch[rlen]<='Z'&&ch[rlen]!='I'?11:10);
			vlen+=chlen;
			rlen++;
			if(vlen>=bytelen&&rlen<n){
				while(vlen+offset>bytelen){
					chlen=isChinese(ch[rlen])?20:(ch[rlen]>='A'&&ch[rlen]<='Z'&&ch[rlen]!='I'?11:10);
					vlen-=chlen;
					rlen--;
				}
				break;
			}
		}
		if(rlen<ch.length){
			char[] newch=new char[rlen];
			System.arraycopy(ch,0,newch,0,newch.length);
			return String.valueOf(newch)+"...";
		}else
			return s;
	}

	public static String getStringByByteLength(String s,int start,int len){
		String result="";
		byte b[]=s.getBytes();
		byte v[]=new byte[len];
		if(b.length<=len)
			result=s;
		else if(b.length-1>=start&&b.length>=start+len){
			for(int i=start,n=len+start;i<n;i++){
				v[i-start]=b[i];
			}
			result=new String(v);
		}
		return result;
	}

	public static String getSubASCIILength(String s,int start,int len){
		String result=getStringByByteLength(s,start,len);
		boolean iscut=result.length()>0&&(int)result.charAt(result.length()-1)==65533;
		return iscut?result.substring(0,result.length()-1):result;
	}

	public static String[] getStringsByLength(String s,int len){
		char[] cArray=s.toCharArray();
		byte[] bArray=s.getBytes();
		ArrayList<String> result=new ArrayList<String>(s.length()/len+1);
		int start=0,end=start;
		for(int i=0,n=cArray.length;i<n;i++){
			end+=isChinese(cArray[i])?2:1;
			if(end-start==len){ // it may be 69 or 70
				result.add(new String(bArray,start,end-start));
				start=end;
			}else if(end-start==len+1){
				result.add(new String(bArray,start,end-start-2));
				start=end-2;
			}
		}
		if(start<end)
			result.add(new String(bArray,start,end-start));
		return result.toArray(new String[0]);
	}

	public static String IntToString(int value,int outputLength){
		if(value<0){
			return "";
		}
		String result=fillchar('0',outputLength);
		String strIntValue=String.valueOf(value);
		if(strIntValue.length()>=outputLength)
			result=strIntValue;
		else
			result=result.substring(0,outputLength-strIntValue.length())+strIntValue;
		return result;
	}

	public static String DoubleToString(double value,int outputLength){
		if(value<0){
			return "";
		}
		String result=fillchar('0',outputLength);
		String NumberValue=value==(int)value?String.valueOf((int)value):String.valueOf(value);
		if(NumberValue.length()>=outputLength)
			result=NumberValue;
		else
			result=result.substring(0,outputLength-NumberValue.length())+NumberValue;
		return result;
	}

	public static String BigDecimalToString(BigDecimal value,int outputLength,int digit){
		String result=null;
		double d=round(value.doubleValue(),digit);
		if(d==(int)d)
			result=IntToString(value.intValue(),outputLength);
		else
			result=DoubleToString(d,outputLength);
		return result;
	}

	public static String fillchar(char sign,int len){
		len=len>0?len:0;
		char[] result=new char[len];
		for(int i=0;i<len;i++){
			result[i]=sign;
		}
		return new String(result);
	}

	public static String[][] bubbleSortStringArray(String[][] strArray,int[] poskey){
		return bubbleSortStringArray(strArray,poskey,true);
	}

	public static String[][] bubbleSortStringArray(String[][] strArray,int[] poskey,boolean isAscending){
		ArrayList list=new ArrayList();
		if(poskey.length==0)
			return strArray;
		else{
			bubbleSortStringArray(strArray,poskey[0],isAscending);
			List al=SplitIniArray(poskey[0],strArray);
			for(int j=0;j<al.size();j++){
				String[][] data=(String[][])al.get(j);
				if(poskey.length>1){
					int[] s=new int[poskey.length-1];
					System.arraycopy(poskey,1,s,0,s.length);
					String[][] value=bubbleSortStringArray(data,s,isAscending);
					list.add(value);
				}else{
					bubbleSortStringArray(data,poskey[0],isAscending);
					list.add(data);
				}
			}
		}
		String[][] result=null;
		try{
			result=getCsvArr(list);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;
	}

	public static void bubbleSortStringArray(String[][] strArray,int sortkey){ // 起泡排序法,对二维字符串数组排序
		bubbleSortStringArray(strArray,sortkey,true);
	}

	/**
	 * 处理中文数字的排序问题
	 */
	private final static String regex="零|一|二|三|四|五|六|七|八|九|十|上|中|下";

	public static int compareTo(String s1,String s2){
		int result=0;
		if(s1==s2)
			result=0;
		else if(s1==null&&s2!=null)
			result=-1;
		else if(s1!=null&&s2==null)
			result=1;
		else if(s1.equals(s2))
			return 0;
		if(s1.matches(".*(\\d+).*"))
			s1=replaceSerialNo(s1);
		if(s2.matches(".*(\\d+).*"))
			s2=replaceSerialNo(s2);
		if(s1!=null&&EasyRegex.find(s1,regex)){
			s1=replaceChineseNum(s1);
		}
		if(s2!=null&&EasyRegex.find(s2,regex)){
			s2=replaceChineseNum(s2);
		}
		result=s1.compareTo(s2);
		return result;
	}

	/**
	 * 暂时只能解决100之内的汉字数字
	 */
	public static String replaceChineseNum(String s){
		String result="";
		try{
			result=new RegexReplace(s,"([零一二三四五六七八九十上中下]+)"){
				@Override
				public String getReplacement(Matcher matcher){
					return chineseNumber2Number(matcher.group(0));
				}
			}.replaceAll();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 暂时只能解决100之内的汉字数字
	 */
	private static String replaceSerialNo(String s){
		String result="";
		try{
			result=new RegexReplace(s,"(\\d+)"){
				@Override
				public String getReplacement(Matcher matcher){
					return insertZero(matcher.group(1),4);
				}
			}.replaceAll();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

	private static String chineseNumber2Number(String s){
		if(s.length()==1){
			s=s.replaceAll("十","10");
		}else if(s.startsWith("十")){
			s=s.replaceAll("十","1");
		}else if(s.endsWith("十")){
			s=s.replaceAll("十","0");
		}else if(s.length()>2)
			s=s.replaceAll("十","");
		s=s.replaceAll("零","0").replaceAll("一","1").replaceAll("二","2").replaceAll("三","3").replaceAll("四","4")
				.replaceAll("五","5").replaceAll("六","6").replaceAll("七","7").replaceAll("八","8").replaceAll("九","9")
				.replaceAll("上","001").replaceAll("中","002").replaceAll("下","003");
		return IntToString(esdk.math.toInt(s),3);
	}

	public static void bubbleSortStringArray(String[][] strArray,int sortkey,boolean isAscending){ // 起泡排序法,对二维字符串数组排序
		String[] tempstrarray=null;
		for(int i=0,m=strArray.length-1;i<m;i++){
			for(int j=0,n=strArray.length-i-1;j<n;j++){
				if((isAscending&&EasyStr.compareTo(strArray[j+1][sortkey],strArray[j][sortkey])<0)
						||(!isAscending&&EasyStr.compareTo(strArray[j+1][sortkey],strArray[j][sortkey])>0)){
					tempstrarray=strArray[j];
					strArray[j]=strArray[j+1];
					strArray[j+1]=tempstrarray;
				}
			}
		}
	}

	public static boolean existOf(String s,String findkey,boolean isIgnoreCase){
		if(findSubString(s,findkey,isIgnoreCase)>=0){
			return true;
		}
		return false;
	}

	private static String[][] getCsvArr(List al) throws Exception{
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<al.size();i++){
			if(!al.get(i).getClass().equals(String[][].class))
				throw new Exception("CSV格式有误");
			String[][] value=(String[][])al.get(i);
			if(i<al.size())
				sb.append(arrToCsv(value,",")+crlf);
			else
				sb.append(arrToCsv(value,","));
		}
		return csvToArr(sb.toString(),",");
	}

	public static boolean existOf(String s,String findkey){
		if(s==null||findkey==null)
			return false;
		if(findSubString(s,findkey,true)>=0){
			return true;
		}
		return false;
	}

	public static boolean existAnd(String s,String[] findkeys,boolean isIgnoreCase){
		if(s==null||findkeys==null||findkeys.length==0)
			return false;
		for(int i=0;i<findkeys.length;i++){
			if(findSubString(s,findkeys[i],isIgnoreCase)<0){
				return false;
			}
		}
		return true;
	}

	public static boolean existOf(String[] array,String find,boolean isIgnoreCase){
		if(EasyObj.isValid(array)&&EasyObj.isValid(find)){
			for(int i=0;i<array.length;i++){
				if(equals(array[i],find,isIgnoreCase))
					return true;
			}
		}
		return false;
	}

	public static boolean existAnd(String s,String[] findkeys){
		return existAnd(s,findkeys,false);
	}

	public static boolean existOr(String s,String[] findkeys){
		return existOr(s,findkeys,false);
	}

	public static boolean existOr(String s,String[] findkeys,boolean isIgnoreCase){
		for(int i=0;i<findkeys.length;i++){
			if(findSubString(s,findkeys[i],isIgnoreCase)>=0)
				return true;
		}
		return false;
	}

	public static int instr(String s,String findkey){
		return instr(s,findkey,0);
	}

	public static int instr(String s,String key,int start){
		int result=0;
		if(start<0){
			start=0;
		}
		if(s.length()<start+1){
			return -1;
		}
		s=s.substring(start,s.length()).toLowerCase();
		result=s.indexOf(key.toLowerCase());
		if(result>=0){
			result=start+result;
			return result;
		}else{
			return -1;
		}
	}

	public static String GetAppPath(){ // 得到程序目录
		return System.getProperty("user.dir");
	}

	public static String XmlStringFilter(String xml){
		if(xml.indexOf("&amp;")>=0){
			xml=ReplaceAll(xml,"&amp;","＆",false); // 避免&字符串的错误
		}else if(xml.indexOf("&#38;#38;")>=0){
			xml=ReplaceAll(xml,"&#38;#38;","＆",false); // 避免&字符串的错误
		}else if(xml.indexOf("&x#26;x#26")>=0){
			xml=ReplaceAll(xml,"&x#26;x#26","＆",false); // 避免&字符串的错误
		}else if(xml.indexOf("&")>=0){
			xml=ReplaceAll(xml,"&","＆",false); // 避免&字符串的错误
		}
		return xml;
	}

	public static String setXmlNodeValue(String xmlString,String NodeName,String value){
		String startnodename="<"+NodeName+">";
		String endnodename="</"+NodeName+">";
		int i=instr(xmlString,startnodename,0)+startnodename.length();
		int j=instr(xmlString,endnodename,i);
		if((i<j)&&(j>=0)){
			xmlString=xmlString.substring(0,i)+value+xmlString.substring(j);
		}
		return xmlString;
	}

	public static String getDirPath(String value){
		return esdk.file.getDirPath(value);
	}

	public static double round(double value,int holdDigit){
		double a=java.lang.Math.pow(10,Double.valueOf(String.valueOf(holdDigit)).doubleValue());
		return (new Long(java.lang.Math.round(value*a))).doubleValue()/a;
	}

	public static String[][] GetStringArray(List list){ // 二维链表转换成二维字符串数组
		int one=0,two=0;
		one=list.size();
		if(one<=0){
			return new String[0][0];
		}
		two=((List)list.get(0)).size();
		String[][] result=new String[one][two];
		for(int i=0;i<one;i++){
			List o=(List)list.get(i);
			result[i]=(String[])o.toArray(new String[0]);
		}
		return result;
	}

	/**
	 * two-dimensional array to list<list>
	 */
	public static List<List> arrArrToListList(String[][] arr){
		ArrayList result=new ArrayList(arr.length);
		for(int i=0,n=arr.length;i<n;i++){
			ArrayList sub=new ArrayList(arr[i].length);
			for(int j=0;j<arr[i].length;j++){
				sub.add(arr[i][j]);
			}
			result.add(sub);
		}
		return result;
	}

	/**
	 * list<list> to two-dimensional array
	 */
	public static String[][] listListToArrArr(List al) throws Exception{
		int arrlen=((String[])al.get(0)).length;
		String[][] result=new String[al.size()][arrlen];
		int checklen=0;
		for(int i=0;i<result.length;i++){
			checklen=((String[])al.get(i)).length;
			if(checklen!=arrlen)
				throw new Exception("第"+al.size()+"行字段数与默认不相等");
			System.arraycopy(al.get(i),0,result[i],0,checklen);
		}
		return result;
	}

	public static int findSubString(String s,String sub,boolean isIgnoreCase){
		return indexOf(s,sub,isIgnoreCase);
	}

	public static int indexOf(String s,String subs,boolean isIgnoreCase){
		int pos=-1;
		if(isIgnoreCase){
			s=s.toLowerCase();
			subs=subs.toLowerCase();
			pos=s.indexOf(subs);
		}else{
			pos=s.indexOf(subs);
		}
		return pos;
	}

	/**避免NullPointException*/
	public boolean contains(String str,String key){
		return str!=null && str.contains(key);
	}
	
	public static ArrayList SplitIniArray(String[][] IniArray,String[][] CsvArray){ // 把一个二维数组分成多个二维数组
		/*
		 * int a=CsvArray.length; int b=CsvArray[1].length;
		 */
		String[][] ArrayPlanar;
		ArrayList result=new ArrayList();
		String strNow,strBefor;
		for(int i=0;i<IniArray.length;i++){
			for(int j=0;j<IniArray[j].length;j++){
				if(IniArray[i][j].equalsIgnoreCase("OrderNumber")){
					if(IniArray[i][j+1].substring(0,1).equalsIgnoreCase("[")){
						String str=IniArray[i][j+1].substring(1,IniArray[i][j+1].length()-1);
						int dd=Integer.parseInt(str);
						strNow=CsvArray[0][dd];
						strBefor=strNow;
						int copylength=0;
						for(int k=0;k<CsvArray.length;k++){
							strNow=CsvArray[k][dd];
							if(strNow.equalsIgnoreCase(strBefor)){
								copylength+=1;
							}else{
								ArrayPlanar=new String[copylength][CsvArray[0].length];
								System.arraycopy(CsvArray,k-copylength,ArrayPlanar,0,copylength);
								result.add(ArrayPlanar);
								strBefor=strNow;
								copylength=1;
							}
						}
						// 结束循环后要把最后相同的数组加入ArrayList;
						ArrayPlanar=new String[copylength][CsvArray[0].length];
						System.arraycopy(CsvArray,CsvArray.length-copylength,ArrayPlanar,0,copylength);
						result.add(ArrayPlanar);
						// int bbbb=result.size();
						return result;
					}
				}
			}
		}
		return(null);
	}

	public static String[] deleteArray(String[] s,int index,int deletelen){
		String[] result=new String[s.length-deletelen];
		System.arraycopy(s,0,result,0,index+1);
		System.arraycopy(s,index+deletelen,result,index,s.length-index-deletelen);
		return result;
	}

	public static List SplitIniArray(int value,String[][] CsvArray){ // 把一个二维数组分成多个二维数组
		if(CsvArray==null)
			return null;
		if(CsvArray.length==0)
			return new ArrayList();
		int copylength=1; // 初始值必需为1
		String[][] ArrayPlanar;
		LinkedList result=new LinkedList();
		String strbefore="",strnext="";
		for(int i=1;i<CsvArray.length;i++){
			strbefore=CsvArray[i-1][value];
			strnext=CsvArray[i][value];
			if(strbefore.equalsIgnoreCase(strnext)){
				copylength+=1;
				continue;
			}else{
				ArrayPlanar=new String[copylength][CsvArray[0].length];
				System.arraycopy(CsvArray,i-copylength,ArrayPlanar,0,copylength);
				result.add(ArrayPlanar);
				copylength=1;
			}
		}
		// 结束循环后要把最后相同的数组加入ArrayList;
		ArrayPlanar=new String[copylength][CsvArray[0].length];
		System.arraycopy(CsvArray,CsvArray.length-copylength,ArrayPlanar,0,copylength);
		result.add(ArrayPlanar);
		return result;
	}

	public static String getSubString(String str,int start,int end){
		String result="";
		if(start<end&&str.length()>0){
			if(start<str.length())
				if(end>str.length())
					end=str.length();
			result=str.substring(start,end);
		}
		return result;
	}

	public static String GetSubString(String str,int start,int end,String findString){
		String result="";
		int p=-1;
		if(findString.length()==0){ // 如果搜索字串为空,则通过start,end来获得字串
			if(start<str.length()&&str.length()>0){
				result=str.substring(start,end);
			}else{
				result="";
			}
		}else{
			p=str.indexOf(findString); // 通过字串搜索
			if(start>=0){ // 从左边起得到字串
				if(p>=0){
					result=str.substring(start,p);
				}else{
					result=str.substring(start,str.length());
				}
			}else if(end>=0){ // 从右边起得到字串
				if(p>=0){
					result=str.substring(p,end);
				}else{
					result=str.substring(0,end);
				}
			}
		}
		return result.trim();
	}

	public static void netSend(String[] machinenames,String message) throws Exception{
		for(int i=0;i<machinenames.length;i++){
			String[] runString={"net","send",machinenames[i],message}; // 可以是机器名或IP地址
			java.lang.Runtime.getRuntime().exec(runString);
		}
	}

	public static String getRamdomTime(String value,int returnStringlength){
		String result="";
		java.text.SimpleDateFormat formatter=new java.text.SimpleDateFormat(value); // value="yyMMddHHmmss"
		String id=formatter.format(new java.util.Date())+String.valueOf(Math.random()*100000);
		id=id.substring(0,returnStringlength);
		result=id.toString();
		return result;
	}

	public static int getRandom(int returnlen){
		double value=round(Math.random(),returnlen);
		double a=java.lang.Math.pow(10,Double.valueOf(String.valueOf(returnlen)).doubleValue());
		double b=new Double(java.lang.Math.round(value*a)).doubleValue();
		int result=new Double(b).intValue();
		return result;
	}

	public static String getXmlNodeValue(String xmlString,String NodeName){
		String strresult="";
		String startnodename="<"+NodeName+">";
		String endnodename="</"+NodeName+">";
		int i=instr(xmlString,startnodename,0)+startnodename.length();
		int j=instr(xmlString,endnodename,i);
		if((i<j)&&(j>=0)){
			strresult=xmlString.substring(i,j);
		}
		return strresult;
	}

	public static String getXmlNodeValueAll(String xmlString,String NodeName,String splitSign){
		StringBuffer result=new StringBuffer();
		int i=0;
		while(i>=0){
			i=getXmlNodeValueAll(xmlString,NodeName,i,result,splitSign);
		}
		return result.toString();
	}

	private static int getXmlNodeValueAll(String xmlString,String NodeName,int position,StringBuffer result,
			String splitSign){
		String startnodename="<"+NodeName+">";
		String endnodename="</"+NodeName+">";
		if(instr(xmlString,startnodename,position)<0)
			return -1;
		int i=instr(xmlString,startnodename,position)+startnodename.length();
		int j=instr(xmlString,endnodename,i);
		if((i<j)&&(j>=0)){
			result.append(xmlString.substring(i,j)+splitSign);
		}
		return j;
	}

	// <hongyuanjiao>
	public static String numericToDateFormat(String numericStr){
		if(numericStr.length()<14||numericStr.length()>14)
			return numericStr;// 如果不足14位，例如0，就直接返回，以免出错
		String dateTimeStr=numericStr.substring(0,4)+"-"+numericStr.substring(4,6)+"-"+numericStr.substring(6,8)+" "
				+numericStr.substring(8,10)+":"+numericStr.substring(10,12)+":"+numericStr.substring(12,14);
		return dateTimeStr;
	}

	public static String dateFormatToNumeric(String dateTimeStr){
		String numericStr=dateTimeStr.substring(0,4)+dateTimeStr.substring(5,7)+dateTimeStr.substring(8,10)
				+dateTimeStr.substring(11,13)+dateTimeStr.substring(14,16)+dateTimeStr.substring(17,19);
		return numericStr;
	}

	public static String convertString(String chinastring){ // 解决中文乱码问题
		if(chinastring==null){
			return null;
		}
		String result=chinastring;
		try{
			byte[] b=chinastring.getBytes("iso-8859-1");
			byte[] b1=chinastring.getBytes();
			if(b.length>0&&b1.length==b.length){
				result=new String(b);
			}
			return result;
		}catch(UnsupportedEncodingException ex){
			throw new SdkRuntimeException("not support iso-8859-1");
		}
	}

	/**
	 * base64加密
	 */
	public static String encode(String value){
		return Base64.getEncoder().encodeToString(value.getBytes());
	}

	/**
	 * base64解密
	 */
	public static String decode(String value){
		return new String(Base64.getDecoder().decode(value));
	}

	public static String toCsv(List list){
		return EasyCsv.toCsv(listToArr(list));
	}

	public static String toCsv(String[][] csv){
		return EasyCsv.toCsv(csv);
	}

	public static String toCsv(String[][] csv,boolean forceText,boolean isTrim){
		return EasyCsv.toCsv(csv,forceText,isTrim);
	}

	public static String arrToCsv(String[][] strArray,String delimit){
		return EasyCsv.toCsv(strArray,delimit,crlf,false);
	}

	public static String arrToCsv(String[][] strArray,String delimit,String endlinesign){
		return EasyCsv.toCsv(strArray,delimit,endlinesign,false);
	}

	public static String[][] fromCsv(String csv){
		return EasyCsv.fromCsv(csv);
	}

	public static String[][] fromCsv(String csv,boolean forceText){
		return EasyCsv.fromCsv(csv,forceText);
	}

	public static String[][] csvToArr(String csvString,String delimit,boolean isCheck){
		return csvToArr(csvString,delimit,"\n",isCheck,false,true);
	}

	@Deprecated
	/** replace to fromCsv **/
	public static String[][] csvToArr(String csv,String delimit,boolean isCheck,boolean delmark){
		return csvToArr(csv,delimit,"\n",isCheck,false,delmark);
	}

	@Deprecated
	/** replace to fromCsv **/
	public static String[][] csvToArr(String csvString,String delimit){
		return csvToArr(csvString,delimit,"\n",true,false,true);
	}

	@Deprecated
	/** replace to fromCsv **/
	public static String[][] csvToArr(String csvString,String delimit,String endlinesign){
		return csvToArr(csvString,delimit,endlinesign,true,false,true);
	}

	@Deprecated
	/** replace to fromCsv **/
	public static String[][] csvToArr(String csvString,String delimit,String endlinesign,boolean isCheck,boolean isTrim){
		return csvToArr(csvString,delimit,endlinesign,isCheck,isTrim,true);
	}

	@Deprecated
	/** replace to fromCsv **/
	public static String[][] csvToArr(String csvString,String delimit,String endlinesign,boolean isCheck,boolean isTrim,
			boolean isDelMark){
		if(csvString==null||csvString.length()==0){
			return new String[0][0];
		}
		String value="";
		int arrlen=-1;
		ArrayList al=new ArrayList();
		if(csvString.indexOf(crlf)>=0&&endlinesign.equals("\n"))
			endlinesign=crlf;
		else if(csvString.indexOf(crlf)<0&&endlinesign.equals(crlf))
			endlinesign="\n";
		StringTokenizer temp=new StringTokenizer(csvString,endlinesign);
		int indexline=0;
		while(temp.hasMoreElements()){
			List arline=new ArrayList(temp.countTokens());
			value=temp.nextToken();
			arline=Arrays.asList(SmartStrToArr(value,delimit,isTrim,isDelMark));
			if(arrlen==-1){
				arrlen=arline.size();
			}else{
				if(arrlen!=arline.size()&&isCheck){
					throw new SdkRuntimeException("第"+indexline+"行字段数与默认不相等");
				}
			}
			al.add(arline);
			indexline+=1;
		}
		String[][] result=GetStringArray(al);
		return result;
	}

	public static ArrayList ArrToList(String[] value){
		try{
			return ArrToList(value,0,value.length,true);
		}catch(Exception ex){
			return null;
		}
	}

	public static ArrayList ArrToList(String[] arr,int startpos,int len,boolean ischeck) throws Exception{
		if(ischeck){
			if(len<0){
				throw new Exception("长度不可以为负值");
			}
			if(arr.length<startpos+len){
				throw new Exception("复制的长度超过数组长度");
			}
		}
		ArrayList result=new ArrayList(len);
		for(int i=startpos;i<startpos+len;i++){
			result.add(arr[i]);
		}
		return result;
	}

	public static String arrToStr(String[] arr){
		return arrToStr(arr,",");
	}

	/**
	 * now throw Exception,because array's len impossible occur incorrect.
	 *
	 * @param arr
	 * @param breaksign
	 * @return
	 */
	public static String arrToStr(String[] arr,String breaksign){
		if(arr==null)
			return null;
		try{
			return arrToStr(arr,breaksign,arr.length);
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}

	public static String arrToStr(String[] arr,String breaksign,int length) throws Exception{
		if(length>arr.length){
			throw new Exception("数组长度有误");
		}
		StringBuffer result=new StringBuffer("");
		for(int i=0;i<length;i++){
			if(arr[i]==null)
				arr[i]="";
			if(arr[i].indexOf(breaksign)>=0)
				result.append("\"").append(arr[i]).append("\"");
			else
				result.append(arr[i]);
			if(i<length-1)
				result.append(breaksign);
		}
		return result.toString();
	}

	public static String ListToStr(List list,String breaksign){
		return EasyStr.arrToStr(EasyStr.listToArray(list),breaksign);
	}

	public static String ListToStr(List list){
		return EasyStr.arrToStr(EasyStr.listToArray(list));
	}

	public static String[] listToArray(List list){
		return toArray(list);
	}

	public static String[] toStringArray(Object[] array){
		if(array==null)
			return null;
		String[] result=new String[array.length];
		for(int i=0;i<result.length;i++){
			if(array[i]!=null){
				result[i]=valueOf(array[i]);
			}
		}
		return result;
	}

	public static String[][] listToArr(List list){
		int rowcount=list.size();
		if(rowcount<=0)
			return new String[0][];
		String[][] result=new String[rowcount][];
		int i=0;
		for(Iterator iter=list.iterator();iter.hasNext();){
			Object object=(Object)iter.next();
			String[] array=null;
			if(object instanceof List){
				array=toArray((List)object);
			}else if(object instanceof String){
				array=new String[]{(String)object};
			}else if(object instanceof String[]){
				array=(String[])object;
			}else if(object instanceof Object[]){
				array=toStringArray((Object[])object);
			}else if(object instanceof Object){
				array=new String[]{valueOf(object)};
			}else{
				array=new String[0];
			}
			result[i++]=array;
		}
		return result;
	}

	public static String[] toArray(Collection collection){
		if(collection==null)
			return null;
		int i=0;
		String[] result=new String[collection.size()];
		for(Iterator iter=collection.iterator();iter.hasNext();){
			Object obj=(Object)iter.next();
			if(obj!=null)
				result[i++]=valueOf(obj);
		}
		return result;
	}

	public static String[][] csvToArr(String value,int[] sa,boolean isTrim) throws Exception{
		String[][] result=new String[0][];
		if(value.length()==0)
			return result;
		else{
			value=value.replaceAll(crlf,"\n");
			StringTokenizer st=new StringTokenizer(value,"\n");
			if(st.hasMoreElements()){
				result=new String[st.countTokens()][];
				int i=0;
				while(st.hasMoreElements()){
					result[i]=StrToArr(st.nextToken(),sa,isTrim);
					i+=1;
				}
			}
			return result;
		}
	}

	public static String[] StrToArr(String value,int[] sa,boolean isTrim) throws Exception{
		ArrayList al=new ArrayList(sa.length);
		for(int i=0;i<sa.length-1;i++){// 记录每个字段的起始位置,包括结尾位置
			if(value.getBytes().length<sa[i+1])
				throw new Exception("定义长度超出字串长度");
			String temp=getStringByByteLength(value,sa[i],sa[i+1]-sa[i]);
			if(isTrim)
				temp=temp.trim();
			al.add(temp);
		}
		String[] result=(String[])al.toArray(new String[0]);
		return result;
	}

	public static String[] StrToArr(String value){
		return StrToArr(value,",");
	}

	public static String[] StrToArr(String value,String breaksign){
		return StrToArr(value,breaksign,false);
	}

	public static String[] StrToArr(String value,String breaksign,boolean isTrim){
		return StrToArr(value,breaksign,isTrim,false);
	}

	public static String[] StrToArr(String value,String breaksign,boolean isTrim,boolean isdelmark){
		if(value==null||value.length()==0){
			return new String[0];
		}
		int ipos=0;
		ArrayList al=new ArrayList();
		String temp;
		while(ipos>=0){
			ipos=value.indexOf(breaksign);
			if(ipos>=0){
				temp=value.substring(0,ipos);
				value=value.substring(ipos+breaksign.length());
			}else{
				temp=value;
			}
			if(isTrim&&temp.indexOf(" ")>=0)
				temp=temp.trim();
			if(isdelmark)
				if(temp.indexOf("\"")==0&&temp.lastIndexOf("\"")==temp.length()-1&&temp.length()>1)
					temp=temp.substring(1,temp.length()-1);
			al.add(temp);
		}
		String[] result=(String[])al.toArray(new String[0]);
		return result;
	}

	public static String[] SmartStrToArr(String value,String breaksign,boolean isTrim,boolean isdelmark){
		return SmartStrToArr(value,breaksign,"\"",isTrim,isdelmark);
	}

	public static String[] SmartStrToArr(String value,String breaksign,String qualifier,boolean isTrim,boolean isdelmark){
		if(value==null||value.length()==0){
			return new String[0];
		}
		int ipos=0;
		ArrayList al=new ArrayList();
		while(ipos>=0){
			ipos=value.indexOf(breaksign);
			String temp=null;
			if(ipos>=0){
				temp=value.substring(0,ipos);
				if(temp.indexOf(qualifier)==0&&temp.lastIndexOf(qualifier)==temp.length()-1&&temp.length()>1){
					if(isdelmark)
						temp=temp.substring(1,temp.length()-1);
				}
				if((temp.indexOf(qualifier)==0&&temp.lastIndexOf(qualifier)!=temp.length()-1)
						||(temp.indexOf(qualifier)==0&&temp.length()==1)){
					int ipostail=value.indexOf(qualifier,ipos+1);
					int iposnext=value.indexOf(breaksign,ipostail+1);
					if(iposnext>ipostail&&ipostail>=0){
						if(isdelmark)
							temp=value.substring(1,ipostail);
						else
							temp=value.substring(0,ipostail+1);
						value=value.substring(iposnext+breaksign.length());
					}else if(iposnext<0){
						if(isdelmark)
							temp=value.substring(1,ipostail);
						else
							temp=value.substring(0,ipostail+1);
						value=value.substring(ipostail+breaksign.length());
						ipos=-1;
					}else if(ipostail<0){
						temp=value;
						ipos=-1;
					}
				}else{
					if(ipos+1<=value.length())
						value=value.substring(ipos+breaksign.length());
				}
			}else{
				if(isdelmark&&value.indexOf(qualifier)==0&&value.lastIndexOf(qualifier)==value.length()-1&&value.length()>1)
					temp=value.substring(1,value.length()-1);
				else
					temp=value;
			}
			if(isTrim&&temp.indexOf(" ")>=0)
				temp=temp.trim();
			al.add(temp);
		}
		String[] result=(String[])al.toArray(new String[0]);
		return result;
	}

	public static String getlineString(String s,int indexline,int indexlen){
		String result="";
		while(indexline>0){
			s=s.substring(s.indexOf("\n")+1,s.length());
			indexline--;
		}
		for(int i=0;i<indexlen;i++){
			if(s.indexOf("\n")!=-1){
				result+=(s.substring(0,s.indexOf("\n")));
				if(i+1<indexlen){
					s=s.substring(s.indexOf("\n")+1,s.length());
					result=result.concat("\n");
				}
			}else{
				result=result.concat(s);
			}
		}
		if(result.indexOf("\r")>=0){
			result=ReplaceAll(result,"\r","",false);
		}
		return result;
	}

	public static String convertToCRLF(String value){
		if(value.indexOf(crlf)>=0)
			return value;
		return value.replaceAll("\n",crlf);
	}

	public static int GetSubStringCount(String str,String substring){
		int result=0;
		int pos=1;
		while((pos=str.indexOf(substring))>=0){
			result++;
			if(pos>=0)
				str=str.substring(pos+1,str.length());
		}
		return result;
	}

	/**
	 * get standard enter sign.
	 */
	public static String getCRLF(){
		return crlf;
	}

	public static String ReplaceFirst(String str,String fs,String rs,boolean isIgnoreCase){
		int ipos=0;
		if(isIgnoreCase){
			ipos=instr(str,fs,ipos);
			if(ipos>=0){
				str=str.substring(0,ipos)+rs+str.substring(ipos+fs.length());
			}
		}else{
			ipos=str.indexOf(fs,ipos);
			if(ipos>=0){
				str=str.substring(0,ipos)+rs+str.substring(ipos+fs.length());
			}
		}
		return str;
	}

	public static String ReplaceAll(String str,String fs,String rs,boolean isIgnoreCase){
		int ipos=0;
		StringBuffer sb=new StringBuffer(str);
		if(isIgnoreCase){
			while(ipos>=0){
				ipos=instr(sb.toString(),fs,ipos);
				if(ipos>=0){
					sb=sb.replace(ipos,ipos+fs.length(),rs);
					ipos+=rs.length();
				}
			}
		}else{
			while(ipos>=0){
				ipos=sb.indexOf(fs,ipos);
				if(ipos>=0){
					// sb=sb.substring(0,ipos)+rs+sb.substring(ipos+fs.length());
					sb=sb.replace(ipos,ipos+fs.length(),rs);
					ipos+=rs.length();
				}
			}
		}
		return sb.toString();
	}

	public static String ReplaceAll(String str,String fs,String rs,boolean isIgnoreCase,boolean isCycle){
		if(isCycle){
			while(existOf(str,fs)){
				str=ReplaceAll(str,fs,rs,isIgnoreCase);
			}
			return str;
		}else
			return ReplaceAll(str,fs,rs,isIgnoreCase);
	}

	public static String insertZero(String value,int outputlen){
		return padLeft(value,outputlen,'0');
	}

	public static String getRepeatString(String value,int times){
		StringBuffer result=new StringBuffer();
		for(int i=0;i<times;i++)
			result.append(value);
		return result.toString();
	}

	public static String getRepeatString(String value,int times,char delimiter){
		CharAppender ca=new CharAppender(delimiter);
		for(int i=0;i<times;i++){
			ca.append(value);
		}
		return ca.toString();
	}

	public static String ArrayListToStr(ArrayList list) throws Exception{
		return arrToStr(toArray(list));
	}

	public static String ChangeDateTimeFormat(String value) throws Exception{
		value=ReplaceAll(value,"/","-",false).trim();
		if((value.length()<8||value==null||value.length()>19)||value.length()==8&&value.indexOf("-")>=0){
			throw new Exception("日期:"+value+"格式有误,正确的格式:2004-09-22 00:00:00"+crlf);
			// return "1900-01-01 00:00:00";
		}
		String result;
		if(value.length()==8)
			value=value.substring(0,4)+"-"+value.substring(4,6)+"-"+value.substring(6,8)+" 00:00:00";
		if(value.length()==19)
			return value;
		int ipos=0;
		String year=null;
		String month=null;
		String date=null;
		String hour=null;
		String minute=null;
		String second=null;
		try{
			year=value.substring(0,value.indexOf("-"));
			value=value.substring(value.indexOf("-")+1,value.length());
			month=insertZero(value.substring(0,value.indexOf("-")),2);
			value=value.substring(value.indexOf("-")+1,value.length());
			while(GetSubStringCount(value,"  ")>0)
				value=ReplaceAll(value,"  "," ",false);
			if(value.indexOf(" ")<0)
				value=value.concat(" ");
			date=insertZero(value.substring(0,value.indexOf(" ")),2);
			value=value.substring(value.indexOf(" ")+1,value.length());
			ipos=value.indexOf(":");
			if(ipos>=0)
				hour=insertZero(value.substring(0,value.indexOf(":")),2);
			else
				hour="00";
			value=value.substring(value.indexOf(":")+1,value.length());
			ipos=value.indexOf(":");
			if(ipos>=0)
				minute=insertZero(value.substring(0,value.indexOf(":")),2);
			else if(value.length()>0)
				minute=insertZero(value.trim(),2);
			else
				minute="00";

			value=value.substring(value.indexOf(":")+1,value.length());
			ipos=value.indexOf(" ");
			if(ipos>=0)
				second=insertZero(value.trim(),2);
			else
				second="00";
			result=year.concat("-")+month.concat("-")+date.concat(" ")+hour.concat(":")+minute.concat(":")+second;
		}catch(Exception ex){
			throw new Exception("日期格式有误".concat(ex.toString()+crlf));
		}
		return result;
	}

	public static String WriterToStr(Writer writer) throws Exception{
		writer.flush();
		char[] a=new char[1];
		writer.write(a);
		return writer.toString();
	}

	public static String ConvertUTF8(String s){
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<s.length();i++){
			char c=s.charAt(i);
			if(c>=0&&c<=255){
				sb.append(c);
			}else{
				byte[] b;
				try{
					b=Character.toString(c).getBytes("utf-8");
				}catch(Exception ex){
					System.out.println(ex);
					b=new byte[0];
				}
				for(int j=0;j<b.length;j++){
					int k=b[j];
					if(k<0)
						k+=256;
					sb.append("%"+Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	public static String StrInverse(String value){
		char[] tmp=value.toCharArray();
		char[] result=new char[tmp.length];
		for(int i=0;i<tmp.length;i++){
			result[i]=tmp[tmp.length-i-1];
		}
		return new String(result);
	}

	public static String ConvertChinesePunctuation(String value){
		char[] chararr=value.toCharArray();
		for(int i=0;i<chararr.length;i++){
			chararr[i]=ConvertChinesePunctuation(chararr[i]);
		}
		return new String(chararr);
	}

	public static char ConvertChinesePunctuation(char character){
		char result=character;
		// char[] punctuations=new
		// char[]{',','，','|','｜','/','／','<','〈','>','〉','\"','＂','$','＄','&','＆',':','：'};
		char[] punctuations=new char[]{',','，','|','｜','<','〈','>','〉','\"','＂','$','＄','&','＆',':','：'};
		for(int i=0;i<punctuations.length;){
			if(punctuations[i]==character){
				result=punctuations[i+1];
				break;
			}
			i+=2;
		}
		return result;
	}

	public static boolean isEmpty(String value){
		return EasyObj.isBlank(value);
	}
	
	public static boolean isBlank(String value){
		return EasyObj.isBlank(value);
	}

	public static boolean isValid(String value){
		return value!=null&&value.length()>0;
	}

	public static int getISN(char c,String charsetName){
		int value=c;
		byte[] bs=null;
		if(charsetName.length()==0)
			charsetName="gb2312";
		try{
			bs=String.valueOf(c).getBytes(charsetName);
			if(bs.length>=2){ // 长度>=2就有可能是中文
				int b1=bs[0]&0xff;
				int b2=bs[1]&0xff;
				value=(b1<<8)|b2;
			}
			return value;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}

	public static String inputStreamConvert(InputStream is) throws Exception{
		int bufferSize=1024;
		byte buff[]=new byte[bufferSize];
		OutputStream xOutputStream=new ByteArrayOutputStream(bufferSize);
		int readSize;
		StringBuffer result=new StringBuffer();
		while((readSize=is.read(buff))!=-1){
			xOutputStream.write(buff,0,readSize);
			result.append(xOutputStream.toString());
		}
		return result.toString();
	}

	public static String getResourceAsString(String fileName) throws Exception{
		return EasyStr.isToStr(EasyStr.class.getClassLoader().getResourceAsStream(fileName));
		// getClass().getClassLoader()

	}

	public static String left(String value,int len){
		if(len>value.length())
			len=value.length();
		else if(len<0)
			len=0;
		return value.substring(0,len);
	}

	public static String right(String value,int len){
		if(isBlank(value))
			return value;
		if(len>value.length())
			len=value.length();
		else if(len<0)
			len=0;
		return value.substring(value.length()-len,value.length());
	}

	public static String mid(String value,int start,int len){
		if(start<0)
			start=0;
		if(len<0)
			len=0;
		if(len+start>value.length())
			len=value.length()-start;
		return value.substring(start,start+len);
	}

	/**返回非null的字符串*/
	public static String getText(String value){
		return ifnull(value,"");
	}

	public static String getText(String value,String defaultValue){
		return ifblank(value,defaultValue);
	}
	
	/**返回非null的字符串*/
	public static String getText(Object obj) {
		return getText((String)esdk.str.valueOf(obj));
	}
	
	public static String getText(Object obj,String defVal) {
		return or((String)esdk.str.valueOf(obj),defVal);
	}
	
	/**
	 * 如果为空，返回默认值
	 */
	public static String ifblank(String value,String defaultValue){
		if(value!=null&&value.length()>0)
			return value;
		return defaultValue;
	}

	/**
	 * 如果为null，返回默认值
	 */
	public static String ifnull(String value,String defaultValue){
		if(value!=null&&value.length()>0)
			return value;
		return defaultValue;
	}

	public static String[] sort(String[] obj){
		return (String[])esdk.array.sort(obj);
	}

	public static String[] SortArray(String[] obj){
		return (String[])esdk.array.sort(obj);
	}

	public static boolean equals(String[] a,String[] b){
		boolean result=true;
		if(a==b)
			return true;
		a=SortArray(a);
		b=SortArray(b);
		if(a.length==b.length){
			for(int i=0;i<b.length;i++){
				if(!a[i].equals(b[i])){
					result=false;
					break;
				}
			}
		}else{
			result=false;
		}
		return result;
	}

	public static String[] split(String s,String regex[],int inx){
		if(isBlank(s))
			return new String[0];
		String arr[];
		for(int i=0;i<regex.length;i++){
			arr=s.split(regex[i]);
			if(arr.length!=1){
				if(inx==-1){
					return arr;
				}else if(inx>=0&&inx<arr.length){
					return new String[]{arr[inx]};
				}
			}
		}

		return new String[0];
	}

	public static String[] split(String s,String regex[]){
		return split(s,regex,-1);
	}

	public static String splitFirst(String s,String regex[]){
		if(isBlank(s))
			return s;
		return split(s,regex,0)[0];
	}

	/**
	 * 适用于properties的多种分隔符（eg:逗号、分号、回车、竖号、制表符、空格等）同时共存的值进行分割
	 */
	public static String[] splits(String values){
		if(isBlank(values))
			return new String[0];
		String[] result=values.split(",|，+| +|;|；|\r?\n|	|\t+|\\|");
		return result;
	}

	/**
	 * 适用于多种分隔符（eg:逗号、分号、回车、竖号、制表符等）按优先级进行分割
	 */
	public static String[] split(String values){
		if(isBlank(values))
			return new String[0];
		else if(values.indexOf('\t')>=0)
			return values.split("\t");
		else if(values.contains(crlf))
			return values.split(crlf);
		else if(values.contains("\n"))
			return values.split("\n");
		else if(values.contains(";"))
			return values.split(";");
		else if(values.contains(","))
			return values.split(",");
		else if(values.contains("|"))
			return values.split("\\|");
		else if(values.contains("；"))
			return values.split("；");
		else if(values.contains("，"))
			return values.split("，");
		else if(values.contains("	"))
			return values.split("	");
		else
			return new String[]{values};
	}

	public static String[] splitWithTrim(String values){
		return split(getText(values).replaceAll(" ",""));
	}

	public static String[] split(String s,String delimiter){
		if(isBlank(s))
			return new String[0];
		else
			return s.split(delimiter);
	}

	/*
	 * public static boolean isIncludeChinese(String value) { if(value==null)
	 * return false; boolean result=false; for(int i=0,n=value.length();i<n;i++){
	 * if(isChinese(value.charAt(i))) { result=true; break; } } return result; }
	 */

	public static boolean isContainChinese(String value){
		return isIncludeChinese(value);
	}

	public static boolean isIncludeChinese(String value){
		if(value==null)
			return false;
		String regExp="[\u4E00-\u9FA5]";
		Pattern p=Pattern.compile(regExp);
		Matcher m=p.matcher(value);
		return m.find();
	}

	public static boolean isChinese(char value){
		byte numeric=((byte)(value>>8));
		boolean result=numeric!=0;
		return result;
	}

	public static boolean isEnglish(char value){
		return (value>='A'&&value<='Z')||(value>='a'&&value<='z');
	}

	public static boolean isCapital(char value){
		return value>='A'&&value<='Z';
	}

	public static boolean isNumeric(char value){
		return(value>='0'&&value<='9');
	}

	public static boolean equals(String str1,String str2){
		return str1==null?str2==null:str1.equals(str2);
	}

	public static boolean equals(String o1,String o2,boolean isIgnoreCase){
		boolean result=false;
		if(isIgnoreCase){
			if(o1==o2)
				result=true;
			else if(o1==null||o2==null)
				result=false;
			else if(o1!=null)
				result=o1.equalsIgnoreCase(o2);
		}else
			result=ObjectUtil.equal(o1,o2);
		return result;
	}

	public static int indexOf(String[] array,String obj){
		for(int i=0;i<array.length;i++){
			if(equals(array[i],obj,true))
				return i;
		}
		return -1;
	}

	public static boolean existOf(String[] array,String obj){
		return indexOf(array,obj)>=0;
	}

	/**等同于existAnd*/
	public static boolean existAnd(String[] array,String[] subArray){
		for(int i=0,len=subArray.length;i<len;i++){
			if(!existOf(array,subArray[i])){
				return false;
			}
		}
		return true;
	}

	public static String getStringNoNull(Object obj){
		if(obj==null)
			return "";
		return valueOf(obj);
	}

	public static String getStringWhenBlank(String str,String defaultValue){
		return !isBlank(str)?str:defaultValue;
	}

	/**key忽略大小写写入value*/
	public static Object putIgnoreCase(Map map,String key,Object value){
		key=findKeyIgnoreCase(map,key);
		return map.put(key,value);
	}

	/**安全获取value*/
	public static Object get(Map map,Object key){
		if(key==null || map==null)
			return null;
		return map.get(key);
	}

	/**key忽略大小写获取value*/
	public static Object getIgnoreCase(Map map,Object key){
		Object result=get(map,key);
		if(result==null&&(key instanceof CharSequence)){
			for(Iterator iter=map.keySet().iterator();iter.hasNext();){
				String realKey=EasyStr.valueOf(iter.next());
				if(equals(realKey,key.toString(),true))
					return map.get(realKey);
			}
		}
		return result;
	}
	
	/**查找key不分大小写*/
	public static String findKeyIgnoreCase(Map<String,?> map,String key){
		boolean result=map.containsKey(key);
		if(result)
			return key;
		if(!result&&(key instanceof CharSequence)){
			for(Iterator iter=map.keySet().iterator();iter.hasNext();){
				String realKey=iter.next().toString();
				if(equals(realKey,key.toString(),true))
					return realKey;
			}
		}
		return key;
	}

	public static boolean contains(Collection map,Object key){
		boolean result=map.contains(key);
		if(!result&&(key instanceof CharSequence)){
			for(Iterator iter=map.iterator();iter.hasNext();){
				String realKey=iter.next().toString();
				if(equals(realKey,key.toString(),true)){
					result=true;
					break;
				}
			}
		}
		return result;
	}

	public static Object getFirstElement(Object[] array){
		if(array==null||array.length==0)
			return null;
		else
			return array[0];
	}

	public static String getFirstElement(String[] array){
		if(array==null||array.length==0)
			return null;
		else
			return array[0];
	}

	public static boolean wildCardMatch(String str,String find,boolean isIgnoreCase){
		if(isIgnoreCase)
			return TestHelper.wildCardMatch(str.toLowerCase(),find.toLowerCase());
		else
			return TestHelper.wildCardMatch(str,find);
	}

	public static boolean wildCardMatch(String str,String find){
		return TestHelper.wildCardMatch(str,find);
	}

	public static String increase(String strnumber,int addend){
		String result=strnumber;
		if(addend==0)
			return result;
		for(int i=strnumber.length()-1;i>=0;i--){
			char c=strnumber.charAt(i);
			if(!(c>='0'&&c<='9')){
				String str=strnumber.substring(0,i+1);
				String number=strnumber.substring(i+1);
				if(number.length()==0){
					if(addend>0)
						result=str+addend;
				}else{
					result=str+(Integer.valueOf(number).intValue()+addend);
				}
				break;
			}
		}
		return result;
	}

	public static String getStringBetween(String s,String left,String right){
		int start=s.indexOf(left)+left.length();
		int end=s.lastIndexOf(right);
		if(end>start&&end>=0&&start-left.length()>=0)
			return s.substring(start,end);
		return s;
	}

	public static String format(String template,Map map){
		return format(template,false,new Map[]{map});
	}

	/**
	 * 格式化字符串，参数格式用${key}表示
	 * @ignoreNotFound 为true时null自动改为""
	 * @maps 参数集合，支持多个map
	 * */
	public static String format(String template,boolean ignoreNotFound,Map...maps){
		Pattern p=Pattern.compile("(\\$?\\{)([\\w\\.\\u4E00-\\u9FA5]+)(\\})"); // 匹配中文、英文、数字
		Matcher m=p.matcher(template);
		StringBuffer result=new StringBuffer();
		while(m.find()){
			String value=null;
			for(Map map:maps){
				value=(String)map.get(m.group(2));
				if(value!=null)
					break;
			}
			if(value==null&&!ignoreNotFound)
				throw new SdkRuntimeException("Argument['"+m.group(2)+"'] can not found");
			else{
				// value=getText(value,"").replaceAll("\\\\","/");//TODO 为什么这里要把\\替换为/？
				value=getText(value,"");
				for(int i=0;i<3&&value.contains("${");i++){
					String temp=format(value,ignoreNotFound,maps);
					if(value.equals(temp)){
						value=temp;
						break;
					}else{
						value=temp;
					}
				}
			}
			if(value.contains("\\"))
				value=value.replace("\\","\\\\");
			m.appendReplacement(result,value);
		}
		result=m.appendTail(result);
		return result.toString();
	}

	/**传参数的方式格式化字符串，参数模板用{}或{0...n}的方式表示，注意不是${}*/
	public static String format(String template,String...params){
		for(int i=0;i<params.length;i++){
			int start=template.indexOf("{}");
			String param=params[i]+"";
			if(start>=0)
				template=template.substring(0,start).concat(param).concat(template.substring(start+2));
			else
				template=template.replace("{"+i+"}",params[i]+"");
		}
		return template;
	}

	public static String format(String templet,Object...params){
		for(int i=0;i<params.length;i++){
			int start=templet.indexOf("{}");
			if(start>=0)
				templet=templet.substring(0,start).concat(ifnull(valueOf(params[i]),"null")).concat(templet.substring(start+2));
			else
				templet=templet.replace("{"+i+"}",ifnull(valueOf(params[i]),"null"));
		}
		return templet;
	}

	public static String toUnderlineCase(String javaBeanName){
		return StrUtil.toUnderlineCase(javaBeanName);
	}

	/** 驼峰表示 */
	public static String toCamelCase(String columnName,boolean isCaptial){
		String result=StrUtil.toCamelCase(columnName);
		if(isCaptial)
			result=Strings.upperFirst(result);
		else
			result=Strings.lowerFirst(result);
		return result;
	}

	/** 驼峰表示，首字母小写 */
	public static String toCamelCase(String columnName){
		return toCamelCase(columnName,false);
	}

	/**首字母大写*/
	public static String upperFirst(String columnName){
		return Strings.upperFirst(columnName);
	}

	/**首字母小写*/
	public static String lowerFirst(String columnName){
		return Strings.lowerFirst(columnName);
	}
	
	/**把数组连接为字符串*/
	public static String concat(Object...array){
		StringBuilder result=new StringBuilder();
		if(array!=null){
			for(int i=0;i<array.length;i++){
				result.append(array[i]==null?"":array[i]);
			}
		}
		return result.toString();
	}

	/**
	 * 去掉多个字符串中重复的内容，合并成一个字符串
	 */
	public static String unistr(char delimiter,String...strings){
		String[][] arrarr=new String[strings.length][];
		for(int i=0;i<strings.length;i++){
			arrarr[i]=esdk.str.split(strings[i],delimiter+"");
		}
		String[] result=distinct(arrarr);
		return join(result,delimiter);
	}

	public static <T> String[] unique(T...array){
		return distinct(array);
	}

	public static <T> String[] distinct(T...array){
		return (String[])EasyArray.unique(array);
	}

	public static String[] remove(String[] arr,String...removeItems){
		return EasyArray.remove(arr,removeItems);
	}

	public static String remove(String arr,String subarr){
		return join(remove(split(arr),split(subarr)),',');
	}

	public static String remove(String arr,String subarr,char delimiter){
		return join(remove(split(arr),split(subarr)),delimiter);
	}

	public static String[] append(String[] arr,String... appendArr) {
		return esdk.array.append(arr,appendArr);
	}
	
	/**
	 * param[objects] should be String[] or String
	 */
	public static <T> String[] toArray(T...strOrArr){
		return EasyArray.concat(String.class,strOrArr);
	}

	public static String join(Object[] arr){
		return join(arr,',');
	}
	
	/**把数组连接成字符串*/
	public static String join(Object[] arr,char delimiter){
		StringBuilder result=new StringBuilder();
		for(int i=0;i<arr.length;i++){
			if(i>0)
				result.append(delimiter+valueOf(arr[i]));
			else
				result.append(valueOf(arr[0]));
		}
		return result.toString();
	}

	/**把数组连接成字符串*/
	public static String join(char delimiter,String...arr){
		StringBuilder result=new StringBuilder();
		for(int i=0;i<arr.length;i++){
			if(i>0)
				result.append(delimiter+arr[i]);
			else
				result.append(arr[0]);
		}
		return result.toString();
	}
	
	/**把数组连接成字符串*/
	public static String join(List<?> list,char delimiter){
		StringBuilder result=new StringBuilder();
		for(int i=0;i<list.size();i++){
			if(i>0)
				result.append(delimiter+valueOf(list.get(i)));
			else
				result.append(valueOf(list.get(i)));
		}
		return result.toString();
	}
	
	public static String arrToStr(Object[] array,char delimer){
		CharAppender result=new CharAppender(delimer);
		for(int i=0;i<array.length;i++){
			if(array[i]==null)
				array[i]="null";
			result.add(array[i].toString());
		}
		return result.toString();
	}

	/**
	 * Produce a string in double quotes with backslash sequences in all the right
	 * places. A backslash will be inserted within </, allowing JSON text to be
	 * delivered in HTML. In JSON text, a string cannot contain a control
	 * character or an unescaped quote or backslash.<br>
	 * <strong>CAUTION:</strong> if <code>string</code> represents a javascript
	 * function, translation of characters will not take place. This will produce
	 * a non-conformant JSON text.
	 *
	 * @param string
	 *          A String
	 * @return A String correctly formatted for insertion in a JSON text.
	 */
	public static String quote(String string){
		if(string==null||string.length()==0){
			return "\"\"";
		}
		char b;
		char c=0;
		int i;
		int len=string.length();
		StringBuilder sb=new StringBuilder(len*2);
		String t;
		char[] chars=string.toCharArray();
		char[] buffer=new char[1030];
		int bufferIndex=0;
		sb.append('"');
		for(i=0;i<len;i+=1){
			if(bufferIndex>1024){
				sb.append(buffer,0,bufferIndex);
				bufferIndex=0;
			}
			b=c;
			c=chars[i];
			switch(c){
			case '\\':
			case '"':
				buffer[bufferIndex++]='\\';
				buffer[bufferIndex++]=c;
				break;
			case '/':
				if(b=='<'){
					buffer[bufferIndex++]='\\';
				}
				buffer[bufferIndex++]=c;
				break;
			default:
				if(c<' '){
					switch(c){
					case '\b':
						buffer[bufferIndex++]='\\';
						buffer[bufferIndex++]='b';
						break;
					case '\t':
						buffer[bufferIndex++]='\\';
						buffer[bufferIndex++]='t';
						break;
					case '\n':
						buffer[bufferIndex++]='\\';
						buffer[bufferIndex++]='n';
						break;
					case '\f':
						buffer[bufferIndex++]='\\';
						buffer[bufferIndex++]='f';
						break;
					case '\r':
						buffer[bufferIndex++]='\\';
						buffer[bufferIndex++]='r';
						break;
					default:
						t="000"+Integer.toHexString(c);
						int tLength=t.length();
						buffer[bufferIndex++]='\\';
						buffer[bufferIndex++]='u';
						buffer[bufferIndex++]=t.charAt(tLength-4);
						buffer[bufferIndex++]=t.charAt(tLength-3);
						buffer[bufferIndex++]=t.charAt(tLength-2);
						buffer[bufferIndex++]=t.charAt(tLength-1);
					}
				}else{
					buffer[bufferIndex++]=c;
				}
			}
		}
		sb.append(buffer,0,bufferIndex);
		sb.append('"');
		return sb.toString();
	}

	public static String serial(String filename,int serialLength){
		String newfilename=new RegexReplace(filename,"(.*?)(\\d*)$"){
			@Override
			public String getReplacement(Matcher matcher){
				int serial=esdk.math.toInt(matcher.group(2))+1;
				return matcher.group(1)+padLeft(serial+"",serialLength,'0');
			}
		}.replaceFirst();
		return newfilename;
	}

	/**
	 * get the next number from string
	 */
	public static String serial(String value){
		if(isBlank(value))
			return "1";
		Pattern p=Pattern.compile("(.*?)(\\d*)");
		Matcher m=p.matcher(value);
		if(m.matches()){
			value=m.group(1)+(m.group(2).length()==0?1:insertZero(String.valueOf((Integer.valueOf(m.group(2))+1)),m.group(2).length()));
		}
		return value;
	}

	public static String trim(CharSequence value){
		if(value!=null)
			value=value.toString().trim().replaceAll("\\xa0","");
		return((String)value);
	}

	/*
	 * private static Map getArguments(String... args) { //sample:
	 * format=yyyy年MM月dd日 HashMap result=new HashMap(); for(int
	 * i=0;i<args.length;i++) { if(args[i]==null) continue; String[]
	 * arr=args[i].split(":| |="); if(arr.length>1) result.put(arr[0],arr[1]); }
	 * return result; }
	 *
	 * private static String getArgument(String[] args,String parameterName) {
	 * //sample: format=yyyy年MM月dd日 for(int i=0;i<args.length;i++) {
	 * if(args[i]==null) continue; String[] arr=args[i].split("=");
	 * //不可以用空格，因为日期格式中有空格符号 if(arr.length>1&&arr[0].equals(parameterName)) return
	 * arr[1]; } return null; }
	 */

	public static String valueOf(Object obj){
		return valueOf(obj,null);
	}

	public static String valueOf(Object obj,String dateFormat){
		return valueOf(obj,dateFormat,null);
	}

	public static String valueOf(Object obj,String dateFormat,String defultValue){
		if(obj==null){
			if(defultValue==null)
				return null;
			else
				return defultValue;
		}else if(obj instanceof CharSequence)
			return obj.toString();
		else if(obj instanceof Number)
			return obj.toString();
		else if(obj instanceof java.sql.Timestamp&&dateFormat==null)
			return obj.toString();
		else if(obj instanceof Date){
			if(dateFormat==null)
				dateFormat=EasyTime.DATE_FORMAT;
			return EasyTime.formatDate((Date)obj,dateFormat);
		}else if(obj instanceof Object[]){
			Object[] array=(Object[])obj;
			CharAppender ca=new CharAppender(',');
			for(int i=0;i<array.length;i++){
				ca.append(valueOf(array[i]));
			}
			return ca.toString();
		}else if(obj instanceof Collection){
			CharAppender ca=new CharAppender(',');
			for(Iterator iter=((Collection)obj).iterator();iter.hasNext();){
				ca.append(valueOf(iter.next()));
			}
			return ca.toString();
		}else if(obj.getClass().getName().equals("oracle.sql.TIMESTAMP")){
			Timestamp timestamp=EasySql.getOracleTimestamp(obj);
			if(dateFormat==null)
				return timestamp.toString();
			else
				return EasyTime.formatDate(timestamp,dateFormat);
		}else
			return obj.toString();
	}

	public static String native2Ascii(String str){
		char[] chars=str.toCharArray();
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<chars.length;i++){
			sb.append(char2Ascii(chars[i]));
		}
		return sb.toString();
	}

	private static String PREFIX="\\u";

	private static String char2Ascii(char c){
		if(c>255){
			StringBuilder sb=new StringBuilder();
			sb.append(PREFIX);
			int code=(c>>8);
			String tmp=Integer.toHexString(code);
			if(tmp.length()==1){
				sb.append("0");
			}
			sb.append(tmp);
			code=(c&0xFF);
			tmp=Integer.toHexString(code);
			if(tmp.length()==1){
				sb.append("0");
			}
			sb.append(tmp);
			return sb.toString();
		}else{
			return Character.toString(c);
		}
	}

	public static String ascii2Native(String str){
		StringBuilder sb=new StringBuilder();
		int begin=0;
		int index=str.indexOf(PREFIX);
		while(index!=-1){
			sb.append(str.substring(begin,index));
			sb.append(ascii2Char(str.substring(index,index+6)));
			begin=index+6;
			index=str.indexOf(PREFIX,begin);
		}
		sb.append(str.substring(begin));
		return sb.toString();
	}

	private static char ascii2Char(String str){
		if(str.length()!=6){
			throw new IllegalArgumentException("Ascii string of a native character must be 6 character.");
		}
		if(!PREFIX.equals(str.substring(0,2))){
			throw new IllegalArgumentException("Ascii string of a native character must start with \"\\u\".");
		}
		String tmp=str.substring(2,4);
		int code=Integer.parseInt(tmp,16)<<8;
		tmp=str.substring(4,6);
		code+=Integer.parseInt(tmp,16);
		return (char)code;
	}

	/* 如果为null或空都认为是false，只返回有值的字串 */
	public static String or(String...vars){
		for(int i=0;i<vars.length;i++){
			if(vars[i]!=null&&vars[i].length()>0)
				return vars[i];
		}
		return vars[vars.length-1];
	}

	public static String or(String p1,String p2){
		if(p1!=null&&!p1.equals(""))
			return p1;
		else if(p2!=null&&!p2.equals(""))
			return p2;
		else if(p1!=null)
			return p1;
		else
			return p2;
	}

	public static LinkedHashSet strToSet(String value){
		return strToSet(value,null);
	}

	public static LinkedHashSet strToSet(String value,String separator){
		LinkedHashSet result=new LinkedHashSet();
		if(value==null)
			return result;
		if(separator==null)
			separator=",";
		String[] array=value.split(separator);
		for(int i=0;i<array.length;i++){
			result.add(array[i]);
		}
		return result;
	}

	public static String setToStr(HashSet set){
		return setToStr(set,null);
	}

	public static String setToStr(HashSet set,String separator){
		if(set==null)
			return null;
		else if(set.size()==0)
			return "";
		if(separator==null)
			separator=",";
		String result="";
		for(Iterator iter=set.iterator();iter.hasNext();){
			result+=iter.next()+"";
			if(iter.hasNext())
				result+=separator;
		}
		return result;
	}

	public static String encodeUnicode(String decodeStr){
		char[] myBuffer=decodeStr.toCharArray();

		StringBuffer sb=new StringBuffer();
		for(int i=0;i<decodeStr.length();i++){
			UnicodeBlock ub=UnicodeBlock.of(myBuffer[i]);
			if(ub==UnicodeBlock.BASIC_LATIN){
				// 英文及数字等
				sb.append(myBuffer[i]);
			}else if(ub==UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
				// 全角半角字符
				int j=(int)myBuffer[i]-65248;
				sb.append((char)j);
			}else{
				// 汉字
				short s=(short)myBuffer[i];
				String hexS=Integer.toHexString(s);
				String unicode="\\u"+hexS;
				sb.append(unicode.toLowerCase());
			}
		}
		return sb.toString();
	}

	/**
	 * unicode 转换成 中文
	 *
	 * @param theString
	 * @return
	 * @author fanhui 2007-3-15
	 */
	public static String decodeUnicode(String theString){
		char aChar;
		int len=theString.length();
		StringBuffer outBuffer=new StringBuffer(len);
		for(int x=0;x<len;){
			aChar=theString.charAt(x++);
			if(aChar=='\\'){
				aChar=theString.charAt(x++);
				if(aChar=='u'){
					int value=0;
					for(int i=0;i<4;i++){
						aChar=theString.charAt(x++);
						switch(aChar){
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value=(value<<4)+aChar-'0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value=(value<<4)+10+aChar-'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value=(value<<4)+10+aChar-'A';
							break;
						default:
							throw new IllegalArgumentException("Malformed encoding.");
						}
					}
					outBuffer.append((char)value);
				}else{
					if(aChar=='t'){
						aChar='\t';
					}else if(aChar=='r'){
						aChar='\r';
					}else if(aChar=='n'){
						aChar='\n';
					}else if(aChar=='f'){
						aChar='\f';
					}
					outBuffer.append(aChar);
				}
			}else{
				outBuffer.append(aChar);
			}
		}
		return outBuffer.toString();
	}

	public byte[] getBytes(String s,String encoding){
		if(s==null)
			return null;
		try{
			return s.getBytes(encoding);
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public String getString(byte[] bytes,String encoding){
		if(bytes==null)
			return null;
		try{
			return new String(bytes,encoding);
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public byte[] getUTF8Bytes(String s){
		if(s==null)
			return null;
		try{
			return s.getBytes(Constant.UTF8);
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public String getUTF8String(byte[] utf8bytes){
		if(utf8bytes==null)
			return null;
		try{
			return new String(utf8bytes,Constant.UTF8);
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public String encoding(String v,String encodingFrom,String encodingTo){
		if(v==null||encodingFrom==null||encodingFrom==null)
			return v;
		try{
			return new String(v.getBytes(encodingFrom),encodingTo);
		}catch(UnsupportedEncodingException e){
			throw new SdkRuntimeException(e);
		}
	}

	public String encodeBySystem(String v){
		if(v==null)
			return v;
		if(!esdk.tool.getFileEncoding().equals(Constant.UTF8)){
			try{
				return new String(v.getBytes(esdk.tool.getFileEncoding()),Constant.UTF8);
			}catch(UnsupportedEncodingException e){
				throw new SdkRuntimeException(e);
			}
		}else
			return v;
	}

	public static String getUTF8StringFromGBKString(String gbkStr){
		try{
			return new String(getUTF8BytesFromGBKString(gbkStr),"UTF-8");
		}catch(UnsupportedEncodingException e){
			throw new InternalError();
		}
	}

	public static byte[] getUTF8BytesFromGBKString(String gbkStr){
		int n=gbkStr.length();
		byte[] utfBytes=new byte[3*n];
		int k=0;
		for(int i=0;i<n;i++){
			int m=gbkStr.charAt(i);
			if(m<128&&m>=0){
				utfBytes[k++]=(byte)m;
				continue;
			}
			utfBytes[k++]=(byte)(0xe0|(m>>12));
			utfBytes[k++]=(byte)(0x80|((m>>6)&0x3f));
			utfBytes[k++]=(byte)(0x80|(m&0x3f));
		}
		if(k<utfBytes.length){
			byte[] tmp=new byte[k];
			System.arraycopy(utfBytes,0,tmp,0,k);
			return tmp;
		}
		return utfBytes;
	}

	/**往右边填入指定长度的字符*/
	public static String padRight(String src,int len,char ch){
		int diff=len-src.length();
		if(diff<=0){
			return src;
		}
		char[] charr=new char[len];
		System.arraycopy(src.toCharArray(),0,charr,0,src.length());
		for(int i=src.length();i<len;i++){
			charr[i]=ch;
		}
		return new String(charr);
	}

	/**往左边填入指定长度的字符*/
	public static String padLeft(String src,int len,char ch){
		int diff=len-src.length();
		if(diff<=0){
			return src;
		}

		char[] charr=new char[len];
		System.arraycopy(src.toCharArray(),0,charr,diff,src.length());
		for(int i=0;i<diff;i++){
			charr[i]=ch;
		}
		return new String(charr);
	}

	public void println(String template,Object...args){
		System.out.println(format(template,args));
	}

}
