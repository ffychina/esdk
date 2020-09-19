package com.esdk.sql;

import java.sql.Connection;

import com.esdk.utils.EasyStr;
import com.esdk.utils.Parser;
import com.esdk.utils.EasyRegex;
import com.esdk.utils.TestHelper;

public class SelectParser extends Parser{
  private String sql;
  private ASelect select;
  private int topnumber=5;
  private boolean isTop=true;
  
  public static ITableSelect getSelect(String sql,Connection conn) {
    SelectParser sp=new SelectParser();
    sp.setSource(sql);
    sp.parse();
    ITableSelect result=(ITableSelect)sp.getResult();
    result.setConnection(conn);
    return result;
  }
  
  public static ITableSelect getSelect(String sql) {
    SelectParser sp=new SelectParser();
    sp.setSource(sql);
    sp.parse();
    ITableSelect result=(ITableSelect)sp.getResult();
    result.setConnection(null);
    return result;
  }
  
  public static ITableSelect create(String table,boolean topflag) {
    return create(table,topflag,null);
  }
  
  public static ITableSelect create(String table,boolean topflag,Connection conn) {
    return create(table,topflag,5,conn);
  }
  
  public static ITableSelect create(String table,int topnumber,Connection conn) {
    return create(table,true,topnumber,conn);
  }
  
  public static ITableSelect create(String table,boolean topflag,int max,Connection conn) {
    SelectParser sp=new SelectParser();
    sp.setSource(table);
    sp.setTop(max);
    sp.setTopAvailable(topflag);
    sp.parse();
    ITableSelect result=(ITableSelect)sp.getResult();
    result.setConnection(conn);
    return result;
  }
  
  @Override public Object getResult(){
    return select;
  }

  @Override public void parse(){
    sql=sql.replaceAll("\n"," ");
    if(!EasyStr.isBlank(sql)) {
      select=new ASelect(findTableName(),getStandardSql(sql));
    }
  }

  public void setTop(int max) {
    topnumber=max;
  }
  
  public void setTopAvailable(boolean value) {
    isTop=value;
  }
  
  private String getStandardSql(String s) {
    if(!EasyRegex.startWith(s,"select")) {
      String top="";
      if(EasyRegex.indexOf(s,"where")<0&&isTop) {
        top="top "+topnumber;
      }
      if(EasyRegex.startWith(s,"from"))
        s="select "+top+" * "+s;
      else
        s="select "+top+" * from "+s;
    }
    return s;
  }
  
  private String findTableName() {
    int index=EasyStr.findSubString(sql,"from",true);
    if(index>=0) {
      int start=index+5;
      int end=EasyStr.instr(sql," ",start);
      if(end<0)
        end=sql.length();
      String tablename=sql.substring(start,end);
      return tablename;
    }
    else {
      if(sql.indexOf('\n')<0&&sql.indexOf(' ')<0)
        return sql;
    }
    appendErr("can not found the table name");
    return null;
  }
  
  @Override public void setSource(Object source){
    sql=(String)source;
  }

  @Override public String toString(){
    return select.toString();
  }
  
  public static void test() {
    String s="select top 10 * from checkstop where pname='rbps'";
    ITableSelect select=(ITableSelect)getSelect(s);
    TestHelper.assertEquals(select.getTableName(),"checkstop");
    TestHelper.assertEquals(select.getSQL(),"select top 10 * from checkstop where pname=\'rbps\'");
    String s1="from checkstop";
    ITableSelect select1=(ITableSelect)getSelect(s1);
    TestHelper.assertEquals(select1.getTableName(),"checkstop");
    TestHelper.assertEquals(select1.getSQL(),"select top 5 * from checkstop");
  }
  
  public static void main(String[] args){
    test();
  }
}
