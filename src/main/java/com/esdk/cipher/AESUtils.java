package com.esdk.cipher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import com.esdk.esdk;
import com.esdk.utils.EasyObj;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 编码工具类 1.将byte[]转为各种进制的字符串 2.base 64 encode 3.base 64 decode 4.获取byte[]的md5值
 * 5.获取字符串md5值 6.结合base64实现md5加密 7.AES加密 8.AES加密为base 64 code 9.AES解密 10.将base
 * 64 code AES解密
 * 
 * @author uikoo9
 * @version 0.0.7.20140601
 */
public class AESUtils{
	private static final String DEFAULT_AES_CRYPT_KEY="AHAAES";

	/**
	 * 将byte[]转为各种进制的字符串
	 * 
	 * @param bytes byte[]
	 * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
	 * @return 转换后的字符串
	 */
	public static String binary(byte[] bytes,int radix){
		return new BigInteger(1,bytes).toString(radix);// 这里的1代表正数
	}

	/**
	 * base 64 encode
	 * 
	 * @param bytes 待编码的byte[]
	 * @return 编码后的base 64 code
	 */
	public static String base64Encode(byte[] bytes){
		return Base64.getEncoder().encodeToString(bytes);
	}

	/**
	 * base 64 decode
	 * 
	 * @param base64Code 待解码的base 64 code
	 * @return 解码后的byte[]
	 * @throws Exception
	 */
	public static byte[] base64Decode(String base64Code){
		return esdk.str.isBlank(base64Code)?null:Base64.getDecoder().decode(base64Code);
	}

	/**
	 * AES加密
	 * 
	 * @param content 待加密的内容
	 * @param encryptKey 加密密钥
	 * @return 加密后的byte[]
	 * @throws Exception
	 */
	public static byte[] aesEncryptToBytes(String content,String encryptKey){
		try{
			KeyGenerator kgen=KeyGenerator.getInstance("AES");
			kgen.init(128,new SecureRandom(encryptKey.getBytes()));
			Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(kgen.generateKey().getEncoded(),"AES"));
			return cipher.doFinal(content.getBytes("utf-8"));
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * AES加密为base 64 code
	 * 
	 * @param content 待加密的内容
	 * @param encryptKey 加密密钥
	 * @return 加密后的base 64 code
	 * @throws Exception
	 */
	public static String encryptAES(String content,String encryptKey){
		return base64Encode(aesEncryptToBytes(content,encryptKey));
	}

	public static String encryptAES(String content){
		return base64Encode(aesEncryptToBytes(content,DEFAULT_AES_CRYPT_KEY));
	}

	/**
	 * AES解密
	 * 
	 * @param encryptBytes 待解密的byte[]
	 * @param decryptKey 解密密钥
	 * @return 解密后的String
	 * @throws Exception
	 */
	public static String aesDecryptByBytes(byte[] encryptBytes,String decryptKey){
		try{
			KeyGenerator kgen=KeyGenerator.getInstance("AES");
			kgen.init(128,new SecureRandom(decryptKey.getBytes()));
			Cipher cipher=Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE,new SecretKeySpec(kgen.generateKey().getEncoded(),"AES"));
			byte[] decryptBytes=cipher.doFinal(encryptBytes);
			return new String(decryptBytes);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	/**
	 * 将base 64 code AES解密
	 * 
	 * @param encryptStr 待解密的base 64 code
	 * @param decryptKey 解密密钥
	 * @return 解密后的string
	 * @throws Exception
	 */
	public static String decryptAES(String encryptStr,String decryptKey){
		return esdk.str.isBlank(encryptStr)?null:aesDecryptByBytes(base64Decode(encryptStr),decryptKey);
	}

	public static String decryptAES(String encryptStr){
		return esdk.str.isBlank(encryptStr)?null:aesDecryptByBytes(base64Decode(encryptStr),DEFAULT_AES_CRYPT_KEY);
	}

	public static void main(String[] args){
		String key="ca163.net";
		String decrptText="merId=777290058133529&platform=unionpay_test&orderNo=2016083117131604&orderAmt=1&commodityMsg=大家好";
		System.out.println("未加密："+decrptText);
		System.out.println("加密后："+encryptAES(decrptText,key));
		System.out.println(decryptAES("waqP6tD0EmxElmcqbakw5bR3EZswyYuwMDpFhVH40cK4SyfaNcZqnL2hJfuOziCobNTlOj9G3MOv\r\n"
				+"2zq7+Qqtk2DXNhTGYvE9RZ/YlsNeH78UCDtXBJtHwgVrHqvX9XQzPePPl47AHSO1CcHcMf63rw==",key));
		esdk.tool.assertEquals(decryptAES(encryptAES(decrptText,key),key),decrptText);
		/* String encrptText=aesEncrypt(decrptText,"ca163net");
		 * System.out.println(encrptText);
		 * Tools.assertEquals(aesDecrypt(aesEncrypt(decrptText
		 * ,"ca163net"),"ca163net"),decrptText); */

	}
}
