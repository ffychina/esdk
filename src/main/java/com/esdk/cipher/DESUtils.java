package com.esdk.cipher;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.xmlbeans.impl.util.Base64;

import com.esdk.esdk;
import com.esdk.utils.EasyObj;

/**
 * DES加密介绍 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
 * 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
 * 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现 。
 * 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
 */
public class DESUtils{

	/**
	 * 加密
	 */
	public static byte[] encrypt(byte[] datasource,String key){
		try{
			byte[] deskey=key.getBytes("utf-8");
			DESKeySpec desKey=new DESKeySpec(deskey);
			// 创建一个密匙工厂，然后用它把DESKeySpec转换成
			SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("DES");
			SecretKey secretKey=keyFactory.generateSecret(desKey);
			// Cipher对象实际完成加密操作
			Cipher cipher=Cipher.getInstance("DES/CBC/PKCS5Padding");
			// 用密匙初始化Cipher对象
			IvParameterSpec param=new IvParameterSpec(deskey);
			cipher.init(Cipher.ENCRYPT_MODE,secretKey,param);
			// 现在，获取数据并加密
			// 正式执行加密操作
			return cipher.doFinal(datasource);
		}catch(Throwable e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 */
	public static byte[] decrypt(byte[] src,String key) throws Exception{
		byte[] deskey=key.getBytes("utf-8");
		// 创建一个DESKeySpec对象
		DESKeySpec desKey=new DESKeySpec(key.getBytes("utf-8"));
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey secretKey=keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher=Cipher.getInstance("DES/CBC/PKCS5Padding");
		IvParameterSpec param=new IvParameterSpec(deskey);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE,secretKey,param);
		// 真正开始解密操作
		return cipher.doFinal(src);
	}

	public static String byte2hex(byte[] b){
		String hs="";
		String stmp="";
		for(int n=0;n<b.length;n++){
			stmp=(java.lang.Integer.toHexString(b[n]&0XFF));
			if(stmp.length()==1){
				hs=hs+"0"+stmp;
			}else{
				hs=hs+stmp;
			}
		}
		return hs.toLowerCase();
	}

	// 从十六进制字符串到字节数组转换
	public static byte[] hex2bytes(String hexstr){
		byte[] b=new byte[hexstr.length()/2];
		int j=0;
		for(int i=0;i<b.length;i++){
			char c0=hexstr.charAt(j++);
			char c1=hexstr.charAt(j++);
			b[i]=(byte)((parse(c0)<<4)|parse(c1));
		}
		return b;
	}

	private static int parse(char c){
		if(c>='a')
			return (c-'a'+10)&0x0f;
		if(c>='A')
			return (c-'A'+10)&0x0f;
		return (c-'0')&0x0f;
	}

	// 测试
	public static void main(String args[]) throws Exception{
		String str="2015年";// 待加密内容
		String key="elanbase";// 密码，长度要是8的倍数
		System.out.println("原文："+str);
		byte[] result=DESUtils.encrypt(str.getBytes("utf-8"),key);
		System.out.println("加密后："+byte2hex(result));
		esdk.tool.assertEquals("bfa700140f41b939",byte2hex(result));

		// 直接将如上内容解密
		byte[] decryResult=DESUtils.decrypt(hex2bytes("963dea4583fed6351660cd16214ce311"),key);
		System.out.println("解密后："+new String(decryResult));
		esdk.tool.assertEquals("2015年01月",new String(decryResult,"utf-8"));
	}

}