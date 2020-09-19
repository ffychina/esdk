package com.esdk.cipher;

import java.security.MessageDigest;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;

public class MD5Utils{
	
	public static String md5_16(String text) {
		return encrypt(text).substring(8, 24);
	}
	
	public static String encrypt(String text){
		if(text!=null){
			try{
				MessageDigest md=MessageDigest.getInstance("MD5");
				byte[] results=md.digest(text.getBytes("utf8"));// 一定要加utf8，否则tomcat命令行和eclipse下返回的结果不一致，eclipse才是对的。
				String encrypted=DESUtils.byte2hex(results);
				return encrypted;
			}catch(Exception ex){
				throw new SdkRuntimeException(ex);
			}
		}
		return null;
	}
	
	public static String encrypt(byte[] data){
		if(data.length>0){
			try{
				MessageDigest md=MessageDigest.getInstance("MD5");
				byte[] results=md.digest(data);
				String encrypted=DESUtils.byte2hex(results);
				return encrypted;
			}catch(Exception ex){
				throw new SdkRuntimeException(ex);
			}
		}
		return null;
	}

	//检验md5, 不分大小写
	public static boolean varify(String origin,String md5){
		return encrypt(origin).equals(md5.toLowerCase());
	}
	public static void main(String[] args) {
		esdk.tool.assertEquals(encrypt("1234567890123456789012345678901234567890"),"f5bf3e984432ae6f9f98840951e5cef3");
		esdk.tool.assertEquals(encrypt("88888"),"1c395a8dce135849bd73c6dba3b54809");
		esdk.tool.assertEquals(encrypt("中国"),"c13dceabcb143acd6c9298265d618a9f");
		esdk.tool.assertEquals(varify("88888","1C395A8DCE135849bd73c6dba3b54809"));
		esdk.tool.assertEquals(md5_16("88888"),"ce135849bd73c6db");
	}
   
   
}
