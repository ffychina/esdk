package com.esdk.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.esdk.esdk;

public class EasyLogger{
	public String ERROR="error",INFO="info",WARN="warn",DEBUG="debug";
	private File logFile;
	private long limitByteLength=50*EasyFile._MB;

	public EasyLogger(File logFile){
		this.logFile=logFile;
	}

	public EasyLogger(File logFile,int limitSizeOfMB) {
		this(logFile);
		this.limitByteLength=limitSizeOfMB*EasyFile._MB;
	}

	public void log(boolean success,String successMsg,String errorMsg){
		if(success)
			log(successMsg,INFO,System.out);
		else
		log(errorMsg,ERROR,System.err);
	}

	public void error(String msg){
		log(msg,ERROR,System.err);
	}

	public void error(Throwable e){
		error(esdk.tool.getExceptionStackTrace(e));
	}

	public void debug(String msg){
		log(msg,DEBUG,System.out);
	}

	public void info(String msg){
		log(msg,INFO,System.out);
	}
	
	public void warn(String msg){
		log(msg,WARN,System.out);
	}

	public void write(String msg){
		if(msg!=null&&msg.startsWith("!"))
			error(msg);
		else
			info(msg);
	}

	public void write(String msg,boolean isError){
		if(isError)
			error(msg);
		else
			info(msg);
	}

	protected void log(String msg,String level,PrintStream ps){
		try{
			msg=EasyTime.getNowTime(EasyTime.DATETIME_FORMAT)+" "+level+" "+msg+"\r\n";
			ps.print(msg);
			FileOutputStream out=getFileOutputStream();
			out.write(msg.getBytes(EasyFile.UTF8));
			out.close();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	private FileOutputStream getFileOutputStream() throws FileNotFoundException{
		if(!logFile.exists()) {
			EasyFile.createFolder(logFile.getParent(),false);
		}
		if(logFile.exists()&&logFile.length()>=limitByteLength) {
			logFile.renameTo(getBackupFile(logFile));
		}
		return new FileOutputStream(logFile,true);
	}

  private File getBackupFile(File sourcefile){
    String descfilename=sourcefile.getPath();
    descfilename=sourcefile.getParent()+"/"+EasyFile.getFileNameNotExt(descfilename)+"_"+EasyTime.getNowTime("yyMMddHHmmss")+"."+EasyFile.getFileExtName(descfilename);
    return new File(descfilename);
  }
  
	public static EasyLogger createLog(File logfile) {
		return new EasyLogger(logfile);
	}
  
	public static EasyLogger createLog(File logfile,int limitSizeOfMB) {
		return new EasyLogger(logfile,limitSizeOfMB);
	}
	
	private static void test() {
		File file=new File("./testfiles/test.log");
		EasyLogger.createLog(file).debug("測試調試");
		EasyLogger.createLog(file).info("測試你好");
		EasyLogger.createLog(file).warn("測試警告");
		EasyLogger.createLog(file,2).error("測試錯誤");
		file.deleteOnExit();
	}
	
	public static void main(String[] args) throws Exception{
		test();
	}
}
