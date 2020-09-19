package com.esdk.utils;

import java.security.MessageDigest;

import org.apache.commons.codec.digest.Md5Crypt;

import com.esdk.esdk;
import com.esdk.cipher.MD5Utils;
import com.esdk.exception.SdkRuntimeException;

/**
 * 对密码进行加密和验证的程序
 */
public class PasswordUtil {
	
	/**
	 * 把inputString加密。
	 * @param text	待加密的字符串
	 * @return
	 */
	public static String encodeByMD5(String text){
		if(text==null||text.trim().length()==0)
			return text;
		else
			return MD5Utils.encrypt(text);
	}
	/**
	 * 验证输入的密码是否正确
	 * @param md5Password		真正的密码（加密后的真密码）
	 * @param password	输入的字符串
	 * @return
	 */
	public static boolean authenticatePassword(String md5Password, String password){
		if(md5Password==null)
			md5Password="";
		if(password==null)
			password="";
		if(md5Password.length()==0&&password.length()==0)
			return true;
		else if(md5Password.length()==32){
			return md5Password.toLowerCase().equals(MD5Utils.encrypt(password));
		}
		else if(md5Password.length()==16) {
			return md5Password.toLowerCase().equals(MD5Utils.md5_16(password));
		}
		else {
			return false;
		}
	}

	public static void main(String[] args) {
		esdk.tool.assertEquals(encodeByMD5("888888"),"21218cca77804d2ba1922c33e0151105");
		esdk.tool.assertEquals(encodeByMD5("88888"),"1c395a8dce135849bd73c6dba3b54809");
		esdk.tool.assertEquals(encodeByMD5("123456"),"e10adc3949ba59abbe56e057f20f883e");
		esdk.tool.assertEquals(encodeByMD5(""),"");
		esdk.tool.assertEquals(encodeByMD5(null),null);
	}
   
   
}