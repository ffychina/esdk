package com.esdk.log;
import java.io.IOException;
import java.io.OutputStream;
public class ConsoleAppender extends AbstractAppender{
  private OutputStream _infoOutStream;
  private OutputStream _errOutStream;
  
  public ConsoleAppender(){
    _infoOutStream=System.out;
    _errOutStream=System.err;
  }

  public void append(ILogItem item){
    if(item!=null){
      try{
        if(item.getLevel().getLevel()<=DefaultLogLevel._INFO){
          _infoOutStream.write(item.getResult().getBytes());
          // System.out.println(item.getResult());
        }
        else{
          _errOutStream.write(item.getResult().getBytes());
          // System.err.println(item.getResult());
        }
      }
      catch(IOException e){
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args){}

}
