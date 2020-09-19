package com.esdk.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import cn.hutool.http.HttpResponse;
import org.nutz.http.Http;
import org.nutz.http.Sender;

import com.esdk.esdk;

public class EasyHttp{
	
	public static String get(String url) {
		return Http.get(url).getContent();
	}
	
	public static String get(String url,Map map) {
		return Http.get(url,map,Sender.Default_Read_Timeout).getContent();
	}
	
	public static String post(String url,Map params) {
		return Http.post(url,params,Sender.Default_Read_Timeout);
	}
	
	public static boolean wget(String url,File targetFile){
		InputStream is=Http.get(url).getStream();
		byte[] b=new byte[10240];
		int len=0;
		try{
			FileOutputStream out=new FileOutputStream(targetFile);
			while((len=is.read(b))!=-1){
				out.write(b,0,len);
			}
			is.close();
			out.close();
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
	}

	public static void main(String[] args){
		String res=EasyHttp.get("https://pay.ca163.net/payment/");
		esdk.tool.assertEquals(res.contains("欢迎进入润沁在线支付系统"));
	}

}
