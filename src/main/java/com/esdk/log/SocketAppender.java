package com.esdk.log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SocketAppender extends AbstractAppender{
  static final List clientSocketList=new LinkedList();
  static int LOGRECEIVEPORT=5678;
  static ServerSocket server;
  static Thread t;
  final static String FETCH="fetch",DELETE="delete",RECEIVE="receive",EXIT="exit";
  static{
    initServer();
  }
  
  public SocketAppender(){
    if(t!=null)
      return ;
    t=new Thread(new Runnable(){
      public void run(){
        try{
          while(true){
            LogServerSocket mu=new LogServerSocket(server.accept());
            mu.start();
          }
        }
        catch(IOException e){
          e.printStackTrace();
        }
      }
    },"SocketLogServer");
    t.setDaemon(true);
    t.start();
  }

  public static void initServer(){
    for(int i=0;i<10;i++){
      if(server==null){
        try{
          server=new ServerSocket(LOGRECEIVEPORT);
          break;
        }
        catch(BindException e){
          LOGRECEIVEPORT++;
        }
        catch(IOException e){
          e.printStackTrace();
        }        
      }
    }
  }
  
  public void appendEachSocket(String s) {
    for(Iterator iter=clientSocketList.iterator();iter.hasNext();){
      LogServerSocket client=(LogServerSocket)iter.next();
      client._content.append(s);
    }
  }
  
  public void append(ILogItem item){
    appendEachSocket(item.getResult());
  }
  
  class LogServerSocket extends Thread{
    StringBuffer _content=new StringBuffer();
    private Socket client;

    public LogServerSocket(Socket c){
      this.setName("SocketLogClient");
      this.client=c;
      System.out.println("接受客户端IP:"+client.getInetAddress()+",客户端端口:"+client.getPort()+"连接");
      clientSocketList.add(this);
    }

    public void run(){
      try{
        BufferedReader in=new BufferedReader(new InputStreamReader(client.getInputStream()));
        PrintWriter out=new PrintWriter(client.getOutputStream());
        // Mutil User but can't parallel
        while(true){
          String str=in.readLine();
          /*System.out.println(str);*/
          if(RECEIVE.equalsIgnoreCase(str)){
            out.print(_content);
            out.flush();
            _content.delete(0,_content.length());
          }
          else if(str.equalsIgnoreCase(FETCH)){
            out.print(_content);
            out.flush();
          }
          else if(str.equalsIgnoreCase(DELETE)){
            out.flush();
            _content.delete(0,_content.length());
          }
          else if(str.equalsIgnoreCase(EXIT)) {
            System.out.println("关闭客户端IP:"+client.getInetAddress()+",客户端端口:"+client.getPort()+"连接");
            client.close();
            clientSocketList.remove(this);
            break;
          }
        }
      }
      catch(IOException ex){}
      finally{}
    }
  }

  public static void test()throws IOException{
    SocketAppender appender=new SocketAppender();
    LevelEffective leveleffective=new LevelEffective();
    leveleffective.addEffectiveLevel(new DefaultLogLevel(DefaultLogLevel._ERROR));
    leveleffective.addEffectiveLevel(new DefaultLogLevel(DefaultLogLevel._FATAL));
    appender.setLevelEffective(leveleffective);
    LogItem logitem=new LogItem();
    logitem.setLevel(new DefaultLogLevel(DefaultLogLevel._ERROR));
    logitem.setContent("this is err from logsocketservice,please check.");
    SocketAppenderClient sc=new SocketAppenderClient();
    appender.write(new DefaultLayout().format(logitem));
    System.out.println(sc.receive());
    sc.close();
  }
  
  public static void main(String[] args) throws IOException{
    test();
  }

}