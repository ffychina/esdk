
package com.esdk.utils;

import java.io.DataOutputStream;
import java.io.IOException;

public class Chinese{
	public static void writeUnicode(final DataOutputStream out,final String value){
		try{
			final String unicode=gbEncoding(value);
			final byte[] data=unicode.getBytes();
			final int dataLength=data.length;

			System.out.println(" Data Length is:  "+dataLength);
			System.out.println(" Data is:  "+value);
			out.writeInt(dataLength); // 先写出字符串的长度
			out.write(data,0,dataLength); // 然后写出转化后的字符串
		}catch(IOException e){

		}
	}

	public static String gbEncoding(final String gbString){
		char[] utfBytes=gbString.toCharArray();
		String unicodeBytes="";
		for(int byteIndex=0;byteIndex<utfBytes.length;byteIndex++){
			String hexB=Integer.toHexString(utfBytes[byteIndex]);
			if(hexB.length()<=2){
				hexB=" 00 "+hexB;
			}
			unicodeBytes=unicodeBytes+" \\u "+hexB;
		}
		// System.out.println("unicodeBytes is: " + unicodeBytes);
		return unicodeBytes;
	}

	/**
	 * This method will decode the String to a recognized String in ui.
	 * 功能:将unicod码转为需要的格式(utf-8)
	 * 
	 * @author javajohn
	 * @param dataStr
	 * @return
	 */
	public static StringBuffer decodeUnicode(final String dataStr){
		final StringBuffer buffer=new StringBuffer();
		String tempStr="";
		String operStr=dataStr;
		if(operStr!=null&&operStr.indexOf("\\u")==-1)
			return buffer.append(operStr); //
		if(operStr!=null&&!operStr.equals("")&&!operStr.startsWith("\\u")){ //
			tempStr=operStr.substring(0,operStr.indexOf("\\u")); //
			operStr=operStr.substring(operStr.indexOf("\\u"),operStr.length());// operStr字符一定是以unicode编码字符打头的字符串
		}
		buffer.append(tempStr);
		while(operStr!=null&&!operStr.equals("")&&operStr.startsWith("\\u")){ // 循环处理,处理对象一定是以unicode编码字符打头的字符串
			tempStr=operStr.substring(0,6);
			operStr=operStr.substring(6,operStr.length());
			String charStr="";
			charStr=tempStr.substring(2,tempStr.length());
			char letter=(char)Integer.parseInt(charStr,16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			if(operStr.indexOf("\\u")==-1){ //
				buffer.append(operStr);
			}else{ // 处理operStr使其打头字符为unicode字符
				tempStr=operStr.substring(0,operStr.indexOf("\\u"));
				operStr=operStr.substring(operStr.indexOf("\\u"),operStr.length());
				buffer.append(tempStr);
			}
		}
		return buffer;
	}
}
