package com.esdk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;

public class EasyRuntime{
	public static String OS;
	static{
		OS=System.getProperty("os.name").startsWith("Win")?"windows":"linux";
	}
	
	public static boolean isWindowsOs() {
		return OS.equals("windows");
	}
	
	/**只能执行在windows*/
	public static int exec(String cmd,String processName,int tryTimes,int waitSeconds){
		String[] cmds;
		if(OS.equals("windows")){
			cmds=new String[]{"cmd.exe","/c",cmd};
		}else{
			cmds=new String[]{"/bin/sh","-c",cmd};
		}
		try{
			Process ps=Runtime.getRuntime().exec(cmds);
			Integer pid=findPID(processName);
			String out=EasyStr.readerToStr(new InputStreamReader(ps.getInputStream(),esdk.tool.getSystemEncoding()),1024,true);
			String err=EasyStr.readerToStr(new InputStreamReader(ps.getErrorStream(),esdk.tool.getSystemEncoding()),1024,true);
			System.out.println("执行命令行PID["+pid+"]: "+esdk.str.arrToStr(cmds," "));
			int r=ps.waitFor();
			if(out.length()>0)
				System.out.print("命令行执行结果："+out);
			if(!err.equalsIgnoreCase("")){
				if(err.contains("[Warning]"))
					System.err.println(err);
				else
					throw new SdkRuntimeException(err);
			}
			waitProcessFinish(pid,tryTimes,waitSeconds);
			return r;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}
	
	public static int exec(String cmd){
		String[] cmds;
		if(OS.equals("windows")){
			cmds=new String[]{"cmd.exe","/c",cmd};
		}else{
			cmds=new String[]{"/bin/sh","-c",cmd};
		}
		try{
			System.out.println("执行命令行："+esdk.str.arrToStr(cmds," "));
			Process ps=Runtime.getRuntime().exec(cmds);
			String out=EasyStr.readerToStr(new InputStreamReader(ps.getInputStream(),esdk.tool.getSystemEncoding()),1024,true);
			String err=EasyStr.readerToStr(new InputStreamReader(ps.getErrorStream(),esdk.tool.getSystemEncoding()),1024,true);
			int waitFor=ps.waitFor();
			int exitvalue=ps.exitValue();
			System.out.println("命令行执行结果："+esdk.str.or(err,out));
			return exitvalue;
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
	}
	
	public static Integer findPID(String processName){
		return findPID(processName,1);
	}
	public static Integer findPID(String processName,int tryTimes){
		BufferedReader br=null;
		Integer lastPID=null;
		try{
			// 下面这句是列出含有processName的进程图像名
			Process proc=Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq "+processName+"*\"");
			br=new BufferedReader(new InputStreamReader(proc.getInputStream(),esdk.tool.getSystemEncoding()));
			String line=null;
			int i=0;
			while((line=br.readLine())!=null){
				if(i>=3){// 判断指定的进程是否在运行
					if(line.startsWith(processName)) {
						String[] arr=line.split(" +");
						lastPID=esdk.math.toInteger(arr[1]);
					}
				}
				i++;
			}
			if(lastPID==null&&tryTimes>0)
				lastPID=findPID(processName,tryTimes-1);
			return lastPID;
		}catch(Exception e){
			e.printStackTrace();
			return lastPID;
		}
	}	
	
	public static boolean findProcess(String processName){
		BufferedReader br=null;
		try{
			// 下面这句是列出含有processName的进程图像名
			Process proc=Runtime.getRuntime().exec("tasklist /FI \"IMAGENAME eq "+processName+"*\"");
			br=new BufferedReader(new InputStreamReader(proc.getInputStream(),esdk.tool.getSystemEncoding()));
			String line=null;
			int i=0;
			while((line=br.readLine())!=null){
				if(i>=3){// 判断指定的进程是否在运行
					line=line.substring(0,line.indexOf(" "));
					if(line.contains(processName))
						return true;
					else if(line.lastIndexOf("*")>=0&&line.matches(processName.replaceAll("\\*",".*")))
						return true;
				}
				i++;
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			if(br!=null){
				try{
					br.close();
				}catch(Exception ex){
				}
			}

		}
	}

	public static boolean findByPID(Integer pid){
		BufferedReader br=null;
		try{
			// 下面这句是列出含有processName的进程图像名
			Process proc=Runtime.getRuntime().exec("tasklist /FI \"PID eq "+pid+"\"");
			br=new BufferedReader(new InputStreamReader(proc.getInputStream(),esdk.tool.getSystemEncoding()));
			String line=null;
			int i=0;
			while((line=br.readLine())!=null){
				if(i>=3){// 判断指定的进程是否在运行
					String[] arr=line.split("[ ]+");
					Integer findpid=esdk.math.toInteger(arr[1]);
					return pid.equals(findpid);
				}
				i++;
			}
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			if(br!=null){
				try{
					br.close();
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void waitProcessFinish(String processName,int maxWaitTimes,int sleepSec) {
		try{
			TimeMeter tm=new TimeMeter();
			for(int i=0;i<maxWaitTimes&&EasyRuntime.findProcess(processName);i++){
				Thread.currentThread().sleep(sleepSec*1000);
			}
			tm.printElapse("Process["+processName+"]运行了{0}\n");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void waitProcessFinish(Integer pid,int maxWaitTimes,int sleepSec) {
		try{
			TimeMeter tm=new TimeMeter();
			for(int i=0;i<maxWaitTimes&&EasyRuntime.findByPID(pid);i++){
				Thread.currentThread().sleep(sleepSec*1000);
			}
			tm.printElapse("PID["+pid+"]运行了{0}\n");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// 列出所有的进程信息
	public static void listAllProcess(){
		BufferedReader br=null;
		try{
			Process proc=Runtime.getRuntime().exec("tasklist");
			br=new BufferedReader(new InputStreamReader(proc.getInputStream(),esdk.tool.getSystemEncoding()));
			@SuppressWarnings("unused")
			String line=null;
			System.out.println("打印所有正在运行的进程信息");
			while((line=br.readLine())!=null){
				System.out.println(br.readLine());
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(br!=null){
				try{
					br.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args){
//		System.out.println(findProcess("wps.exe"));
//		listAllProcess();
		exec("wps2pdf D:\\ffy\\java\\exam\\webroot\\download\\studyHoursCerts\\刘辉2016年公需18学时证明.doc D:\\\\ffy\\\\java\\\\exam\\\\webroot\\\\download\\\\studyHoursCerts\\\\刘辉2016年公需18学时证明.pdf","wps2pdf",10,3);
	}
}
