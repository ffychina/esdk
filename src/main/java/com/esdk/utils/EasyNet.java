package com.esdk.utils;

import cn.hutool.Hutool;
import cn.hutool.core.util.NetUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.Enumeration;
import java.util.Map;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;

public class EasyNet{
	/**获取域名IP*/
  public static String nameToIp(String name) throws Exception{
    String ipAddrStr="";
    try{
      InetAddress addr=InetAddress.getByName(name);
      byte[] ipAddr=addr.getAddress();
      //Convert to dot representation
      for(int i=0;i<ipAddr.length;i++){
        if(i>0){
          ipAddrStr+=".";
        }
        ipAddrStr+=ipAddr[i]&0xFF;
      }
    }
    catch(UnknownHostException ex){
    	throw new SdkRuntimeException(ex);
    }
    return ipAddrStr;
  }

	/**通过IP获取域名*/
  public static String ipToName(byte[] ip){
    if(ip.length<4){
      return "!error byte Array";
    }
    String hostname="";
    //get hostname by a byte array containing the ip address
    try{
      InetAddress addr=InetAddress.getByAddress(ip);
      hostname=addr.getHostName();
    }
    catch(UnknownHostException ex){
    }
    return hostname;
  }

	/**通过IP获取域名*/
  public static String ipToName(String ip){
    String hostname="";
    try{
      //Get hostname by textual representation of ip address
      InetAddress addr=InetAddress.getByName(ip);
      /*//get canonical host name
            String CanonicalHostName=addr.getCanonicalHostName();*/
     //get the host name
      hostname=addr.getHostName();
    }
    catch(UnknownHostException ex){
      ex.printStackTrace();
    }
    return hostname;
  }

  /**根据网卡取本机配置的IP*/
  public static final String LocalIp= NetUtil.getLocalhostStr();
  
/*	public static String getLocalIp(){
		if(localIp==null){
			Enumeration<NetworkInterface> netInterfaces=null;
			try{
				netInterfaces=NetworkInterface.getNetworkInterfaces();
				while(netInterfaces.hasMoreElements()){
					NetworkInterface ni=netInterfaces.nextElement();
					Enumeration<InetAddress> ips=ni.getInetAddresses();
					while(ips.hasMoreElements()){
						InetAddress ip=ips.nextElement();
						if(ip.isSiteLocalAddress()){
							localIp=ip.getHostAddress();
							System.out.println("当前服务器IP地址："+localIp);
							return localIp;
						}
					}
				}
			}catch(Exception e){
				throw new SdkRuntimeException(e);
			}
		}
		return localIp;
	}*/
	
  public final static String LocalName=NetUtil.getLocalhost().getHostName();

/*	public static String getLocalName(){
		if(localName==null){
			InetAddress addr=null;
			try{
				addr=InetAddress.getLocalHost();
			}catch(UnknownHostException ex){
				throw new SdkRuntimeException(ex);
			}
			// get hostname
			localName=addr.getHostName();
			System.out.println("服务器名称是："+localName);
		}
		return localName;
	}*/

  /**获取本地IP，在Window下正常，但在Linux下只能取得127.0.0.1*/
/*  public static String getLocalIpOld(){
    InetAddress addr=null;
    try{
      addr=InetAddress.getLocalHost();
    }
    catch(UnknownHostException ex){
    	throw new SdkRuntimeException(ex);
    }
    return addr.getHostAddress();
  }*/

  public static String SISToStr(InputStream sis) throws Exception{
    InputStreamReader isr=new InputStreamReader(sis,"ISO-8859-1");
    String result=EasyStr.readerToStr(isr);
    isr.close();
    return result;
  }

  public static String isrToStr(InputStreamReader isr) throws Exception{
    StringBuffer result=new StringBuffer("");
    char[] buf=new char[10240];
    int readlen=0;
    while((readlen=isr.read(buf))>0){
      result=result.append(String.valueOf(buf,0,readlen));
    }
    return result.toString();
  }

  public static void netSend(String[] machinenames,String message) throws Exception{
    for(int i=0;i<machinenames.length;i++){
      String[] runString={
          "net","send",machinenames[i],message}; //可以是机器名或IP地址
      java.lang.Runtime.getRuntime().exec(runString);
    }
  }

  public static String toUtf8String(String s){
    return EasyStr.ConvertUTF8(s);
  }
  
  public static String get(String url,Map map){
  	if(url==null)
  		return null;
  	String pp=url.indexOf('?')<0?"?":"&";
  	String params=EasyMap.toHttpParams(map,true);	
  	HttpURLConnection httpConn=null;
		InputStream is=null;
		String result="";
		try{
			URL url1=new URL(url+pp+params);
			httpConn=null;
			is=null;
			httpConn=(HttpURLConnection)url1.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setUseCaches(false);
			/*httpConn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; "
				+ "Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");*/
			httpConn.setDoInput(true);
			is=httpConn.getInputStream();
			result=EasyStr.isToStr(is);
		}catch(Exception e){
			try{
				if(httpConn!=null)
					result+=e.toString()+"\r\n"+EasyStr.isToStr(httpConn.getErrorStream());
			}catch(Exception e1){
				throw new RuntimeException(e1);
			}
		}
		finally {
      try{
				if(is!=null)
				  is.close();
			}catch(IOException e){
				e.printStackTrace();
				result+=e.toString();
			}
      if(httpConn!=null)
        httpConn.disconnect();
    }
    return result;
  }

  public static String post(String url,Map map){
  	if(url==null)
  		return null;
  	String params=EasyMap.toHttpParams(map,false);	
  	HttpURLConnection httpConn=null;
		InputStream is=null;
		OutputStream os=null;
		String result="";
		try{
			URL url1=new URL(url);
			httpConn=null;
			is=null;
			httpConn=(HttpURLConnection)url1.openConnection();
			httpConn.setRequestMethod("POST");
			/*httpConn.setRequestProperty(
					"User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; "
				+ "Windows NT 5.1; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");*/
			httpConn.setUseCaches(false);
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			os=httpConn.getOutputStream();
			os.write(params.getBytes(),0,params.length());
			os.flush();
			os.close();
			is=httpConn.getInputStream();
			result=EasyStr.isToStr(is);
			is.close();
		}catch(Exception e){
			result+=e.toString();
			try{
				if(httpConn!=null)
					result+="\r\n"+EasyStr.isToStr(httpConn.getErrorStream());
			}catch(Exception e1){
				throw new RuntimeException(e1);
			}
		}
		finally {
      try{
				if(is!=null)
				  is.close();
				if(os!=null)
					os.close();
			}catch(IOException e){
				e.printStackTrace();
				result+=e.toString();
			}
      if(httpConn!=null)
        httpConn.disconnect();
    }
    return result;
  }
	
  public static void main(String[] args) throws Exception{
    esdk.tool.assertEquals(LocalIp,"*");
  	esdk.tool.assertEquals(LocalName,"*");
  	esdk.tool.assertEquals(nameToIp("db.server"),"172.16.250.236");
  	esdk.tool.assertEquals(nameToIp("jmjs.ca163.net"),"42.159.198.73");
  	esdk.tool.assertEquals(ipToName("42.159.198.73"),"42.159.198.73");
/*      boolean result=UploadToFtp("web","test","test","",new String[]{"f:/test.xml","f:/result.xml"});
    result=DownLoadFromFtp("web","dap","thebest","","f:/test/","*txt*","csv");
    System.out.println(result);
*/    
  }
}
