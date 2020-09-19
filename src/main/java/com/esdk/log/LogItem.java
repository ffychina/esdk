package com.esdk.log;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogItem implements ILogItem{
  private String _TimeFormat="yy-MM-dd HH:mm:ss";
  private Date _HandleTime;
  private long _ID;
  private String _Name;
  private String _Delimiter=" ";
  private String _Content="";
  private String _EndChar="\r\n";
  private ILogLevel _Level;
  private String _result;
  
  public LogItem(){
    _HandleTime=new Date();
    _ID=_HandleTime.getTime();
    _Level=new DefaultLogLevel(DefaultLogLevel._INFO);
    _Name="";
  }

  public void setTimeFormat(String format){
    _TimeFormat=format;
  }

  public void setDelimiter(String delimit){
    _Delimiter=delimit;    
  }

  public void setName(String value){
    _Name=value;
  }

  public void setContent(String value){
    checkContent(value);
    _Content=value;
  }

  private void checkContent(String value){
    if(value==null)
      System.err.println("content can't be null");
  }
  
  public void setEndChar(String value){
    _EndChar=value;
  }

  public void setLevel(ILogLevel level){
    _Level=level;
  }

  
  public void setResult(String value){
    _result=value;
  }
  
  public long getID(){
    return _ID;
  }

  public String getTime(){
    return new SimpleDateFormat(_TimeFormat).format(_HandleTime);
  }

  public String getName(){
    return _Name;
  }  

  public String getContent(){
    return _Content;
  }

  public String getEndChar(){
    return _EndChar;
  }

  public String getDelimiter(){
    return _Delimiter;
  }

  public ILogLevel getLevel(){
    return _Level;
  }

  public String toString(){
    return getID()+_Delimiter+getTime()+_Delimiter+_Name+_Delimiter+_Level+_Delimiter+_Content+_EndChar;
  }
  
  public String getResult(){
//    if(check())
      if(_result==null)
      	_result=toString();
      return _result;
  }
  
  public String getResultNoTime(){
    return _Name+_Delimiter+_Level+_Delimiter+_Content+_EndChar;
  }
  
  public String getResultNoID(){
    return _Name+_Delimiter+_Level+_Delimiter+_Content+_EndChar;
  }  
  
  public boolean check(){
    int errCount=0;
    try{
      Class cls=this.getClass();
      Method[] method=cls.getMethods();
      for(int i=0;i<method.length;i++){
        if(method[i].getName().indexOf("get")>=0&&method[i].getName().indexOf("getResult")<0){
//          System.out.println(method[i].getName());
          errCount+=method[i].invoke(this,(Object[])null)!=null?errCount++:errCount;
        }
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
   return errCount==0;
  }
  
	@Override
	public int size(){
		return getResult().getBytes().length;
	}
  
  public static void test() {
    String newTimeFormat="yyyy/MM/dd HH:mm:ss";
    String newDelimiter="\t";
    ILogItem item=new LogItem();
    System.out.println(item.toString());
    System.out.println(item.getTime());
    System.out.println(item.getID());
    System.out.println(item.getDelimiter().equals(" "));
    try{
      Thread.currentThread().sleep(10);
    }
    catch(InterruptedException e){
      throw new RuntimeException(e);
    }
    System.out.println(item.getID()<new LogItem().getID());
    item.setName(item.getClass().getName());
    item.setContent("test item");
    item.setDelimiter(newDelimiter);
    item.setEndChar("\n");
    item.setLevel(item.getLevel().getNextHigherLevel());
    item.setTimeFormat(newTimeFormat);
    System.out.println(item.getContent().equals("test item"));
    System.out.println(item.getDelimiter().equals(newDelimiter));
    System.out.println(item.getEndChar().equals("\n"));
    System.out.println(item.getLevel().getProperty().equals(DefaultLogLevel._LEVEL[DefaultLogLevel._WARN]));
    System.out.println(item.getTime().length()==newTimeFormat.length());
    System.out.println(item.toString());
    System.out.println(((LogItem)item).check());
  }
  
  public static void main(String[] args){
    test();
  }

}
