package com.esdk.log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.esdk.interfaces.IRequestClose;

public class SocketAppenderClient implements IRequestClose{
  Socket socket;
  PrintWriter out;
  BufferedReader in;
  String LogServerAddress;

  public SocketAppenderClient() throws IOException{
    init();
  }
  
  public SocketAppenderClient(String address) throws IOException{
    LogServerAddress=address;
    init();
  }
  
  private void init() throws IOException{
    if(LogServerAddress==null)
      socket=new Socket(InetAddress.getLocalHost(),SocketAppender.LOGRECEIVEPORT);
    else
      socket=new Socket(InetAddress.getByName("beta"),SocketAppender.LOGRECEIVEPORT);
    System.out.println(socket.getSoTimeout());
    System.out.println(socket.getKeepAlive());
    System.out.println(socket.getLocalPort());
    in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out=new PrintWriter(socket.getOutputStream());
  }

  public String fetch() throws Exception{
    String result="";
    out.println(SocketAppender.FETCH);
    out.flush();
    Thread.currentThread().sleep(10);
    while(in.ready()){
      result+=in.readLine()+("\r\n");
    }
    return result;
  }
  
  public String receive() throws IOException{
    String result="";
    out.println(SocketAppender.RECEIVE);
    out.flush();
    while(in.ready()){
      result+=in.readLine()+("\r\n");
    }
    return result;
  }

  public String exit() throws IOException{
    String result="";
    out.println(SocketAppender.EXIT);
    out.flush();
    while(in.ready()){
      result+=in.readLine();
    }
    return result;
  }

  public void close() throws IOException{
    exit();
    socket.close();
  }
  
  private static void test(){
    try{
      SocketAppenderClient sc=new SocketAppenderClient();
      System.out.println(sc.receive());
      sc.exit();
    }
    catch(Exception e){
      e.printStackTrace();
    }    
  }
  
  private static void test1(){
    SocketAppenderClient sc;
    try{
      sc=new SocketAppenderClient("beta");
      for(int i=0;i<100;i++){
        String s=sc.receive();
        if(s.length()>0)
          System.out.println(s);
        Thread.currentThread().sleep(500);
      }
      sc.exit();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args){
    test();
    test1();
  }

}
