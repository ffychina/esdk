package com.esdk.log;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.naming.NamingException;
import javax.naming.Reference;

import com.esdk.interfaces.IRequestClose;

public class Logger implements ILogger,IRequestClose,javax.naming.Referenceable{
  private ILogFormater formater;
  protected ILogExporter exporter;
  protected String loggerName;
  private File logFile;
	private boolean isSilence=false; //为true时，不打印到控制台（console)
  final static Object mutex=new Object();

  public Logger(){
  }

  public Logger(File logfile,String loggerName){
	  this();
  	this.setLogFile(logfile);
  	this.loggerName=loggerName;
  	FileLoggerConfiguration.config(this);
  }
  
  public Logger(String loggername){
    this();
    loggerName=loggername;
//    DefaultLoggerConfiguration.config(this);
    FileLoggerConfiguration.config(this);
  }
  
  public String getLoggerName(){
    return loggerName;
  }
  
  public void setLogFormater(ILogFormater value) {
    formater=value;
  }
  
  public void setLogExporter(ILogExporter appender) {
    exporter=appender;
  }
  
  private ILogItem format(ILogItem item){
   return formater.format(item);
  }

  private void write(ILogItem item){
    if(exporter!=null) {
    	if(isSilence()==true&&exporter instanceof ConsoleAppender){
    		//if silence log, don't output to console.
    		exporter.nextExporter().write(item);
    	}
    	else {
    		exporter.write(item);
    	}
    }
    else
      new NullPointerException("appender is null").printStackTrace();
  }

  public void addMessage(Object msg){
    add(createItem((String)msg));
  }

  public void add(ILogItem item){
      synchronized(mutex) {
//    System.out.println(item.getLevel());
      item=format(item);
      write(item);
    }
  }

  public void println(String msg) {
    ILogItem logItem=createItem(msg);
    logItem.setLevel(new DefaultLogLevel(DefaultLogLevel._INFO));
    logItem.setResult(logItem.getContent().concat("\n"));
    write(logItem);
  }
  
  private ILogItem createItem(String msg){
    LogItem result=new LogItem();
    result.setContent(msg);
    result.setName(loggerName);
    return result;
  }

  public void debug(String msg){
    ILogItem logItem=createItem(msg);
    logItem.setLevel(new DefaultLogLevel(DefaultLogLevel._DEBUG));
    add(logItem);
  }
  
  public void info(String msg){
    ILogItem logItem=createItem(msg);
    logItem.setLevel(new DefaultLogLevel(DefaultLogLevel._INFO));
    add(logItem);
  }

  public void warn(String msg){
    ILogItem logItem=createItem(msg);
    logItem.setLevel(new DefaultLogLevel(DefaultLogLevel._WARN));
    add(logItem);
  }

  public void error(String msg){
    ILogItem logItem=createItem(msg);
    logItem.setLevel(new DefaultLogLevel(DefaultLogLevel._ERROR));
    add(logItem);
  }

  public void error(Throwable e) {
    error(exceptionStackTraceToString(e));
  }
  
  public static String exceptionStackTraceToString(Throwable e){
    StringWriter sw = new StringWriter();
    e.printStackTrace(new PrintWriter(sw));
    //String result=sw.toString() ;
    //sw.close();//it's not effect.
    return sw.toString();
  }
  
  public void flush(){
    exporter.flush();
  }

  @Override
  public void close()throws IOException{
    ILogExporter logexporter=exporter;
    while(logexporter!=null){
      if(logexporter instanceof IRequestClose){
        ((IRequestClose)logexporter).close();
      }
      logexporter=logexporter.nextExporter();
    }
  }

  public static void test(){
    Logger log=new Logger("test");
    FileLoggerConfiguration.config(log);
    FileLoggerConfiguration.config(log,"log.config");
    log.debug("打印调试日志..");
    log.info("打印信息日志..");
    log.error("打印错误日志..");
    log.flush();
    try{
      log.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void main(String[] args){
    test();
  }

  public Reference getReference() throws NamingException{
    Reference r=new Reference(this.getClass().getName());
    return r;
  }

	public void setLogFile(File logFile){
		this.logFile = logFile;
	}

	public File getLogFile(){
		return logFile;
	}

	public boolean isSilence(){
		return isSilence;
	}

	public void setSilence(boolean isSilence){
		this.isSilence = isSilence;
	}

}
