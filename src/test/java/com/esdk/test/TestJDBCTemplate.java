package com.esdk.test;

import com.esdk.esdk;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.Literal;
import com.esdk.sql.datasource.FileConnectionPool;
import com.esdk.sql.orm.RowUtils;
import com.esdk.test.orm.CheckStopRow;
import com.esdk.utils.EasyStr;
import com.esdk.utils.JsonUtils;
import com.esdk.utils.EasyObj;

import java.math.BigDecimal;
import java.sql.Connection;

public class TestJDBCTemplate {
  private static void test() {
    try{
      esdk.tool.assertEquals(new JDBCTemplate("select * from member where name like ?","($1ab$10')").getSQL(),"select * from member where name like '($1ab$10'')");
/* 		Connection myconn=ConnectionBuilder.createConnection("com.mysql.jdbc.Driver","jdbc:mysql://127.0.0.1:3306/ych","sys","aha801801","ych");
 		Tools.assertEquals(new JDBCTemplate(myconn,"select top 10 * from member where member_type='abcd'").getSQL(),"select * from member where member_type='abcd' limit 10");
 		Tools.assertEquals(new JDBCTemplate(myconn,"select top 10 * from member where member_id in (select top 15 member_id from member) where member_type='abcd' and (valid=1)").getSQL(),"select * from member where member_id in (select member_id from member limit 15) where member_type='abcd' and (valid=1) limit 10");
 		Tools.assertEquals(new JDBCTemplate(myconn,"SELECT dbo.getDateDiff('2009-01-02','2009-01-05')").getSQL(),"SELECT getDateDiff('2009-01-02','2009-01-05')");*/
      Connection msconn= FileConnectionPool.getConnection();
      esdk.tool.assertEquals(new JDBCTemplate(msconn,"select * from member where member_id in (select member_id from member limit 1) where (member_type='abcd') and valid=1  limit 10 offset 6").getSQL(),"select top 16 * from member where member_id in (select top 1 member_id from member) where (member_type='abcd') and valid=1");
      esdk.tool.assertEquals(new JDBCTemplate(msconn,"SELECT dbo.getDateDiff('2009-01-02','2009-01-05')").getSQL(),"SELECT dbo.getDateDiff('2009-01-02','2009-01-05')");
      esdk.tool.assertEquals(new JDBCTemplate(msconn,"SELECT getDateDiff('2009-01-02','2009-01-05')").getSQL(),"SELECT dbo.getDateDiff('2009-01-02','2009-01-05')");
      JDBCTemplate in=new JDBCTemplate(msconn,"select * from checkstop where pname in(?) or intertime in (?) or pname=?",new Literal("'avash','AWZYDO'"),new Literal("5,10"),"SECASN");
      esdk.tool.assertEquals(in.toABResultSet().toCsv(),"PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID\r\n" +"AVASH,20090410130020,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n"+"AWZYDO,20080501075702,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + "SECASN,20080501104729,,\"ffy,someone\",project.ffy@pgl-world.com,true");
      JDBCTemplate in1=new JDBCTemplate(msconn,"select * from checkstop where pname in(?) or intertime in (?) or pname=?",new String[] {"avash","AWZYDO"},new int[] {5,10},"SECASN");
      esdk.tool.assertEquals(in1.toABResultSet().toCsv(),"PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID\r\n" +"AVASH,20090410130020,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n"+"AWZYDO,20080501075702,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + "SECASN,20080501104729,,\"ffy,someone\",project.ffy@pgl-world.com,true");
      JDBCTemplate insert=new JDBCTemplate(msconn,"insert into checkstop (pname,lastchecktime,intertime,alertlist,maillist,valid) values('octopus',20081231000000,null,'admin','admin@sage.com',true)");
      esdk.tool.assertEquals(insert.getSQL(),"insert into checkstop (pname,lastchecktime,intertime,alertlist,maillist,valid) values('octopus',20081231000000,null,'admin','admin@sage.com',true)");
      esdk.tool.assertEquals(insert.perform());
      JDBCTemplate select=new JDBCTemplate("select * from checkstop where pname=? and valid=? order by lastchecktime desc",new Object[] {"octopus",true},msconn);
      esdk.tool.assertEquals(select.count(),1);
      esdk.tool.assertEquals(select.getSQL(),"select * from checkstop where pname='octopus' and valid=1 order by lastchecktime desc");
      esdk.tool.assertEquals(select.perform());
      esdk.tool.aeic(select.toABResultSet().toCsv(),
              "PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID\r\n" +
                      "octopus,20081231000000,,admin,admin@sage.com,true");
      esdk.tool.assertEquals(JsonUtils.toJsonArray(select.toList(CheckStopRow.class,0,1),JsonUtils.getPropertyPreFilter(EasyStr.distinct(RowUtils.Keywords,"PKID"))).toString(),"[{\"alertList\":\"admin\",\"lastCheckTime\":20081231000000,\"mailList\":\"admin@sage.com\",\"pname\":\"octopus\",\"valid\":true}]");
      JDBCTemplate update=new JDBCTemplate("update checkstop set lastchecktime=?,intertime=? where pname=?",new Object[] {new BigDecimal("20081201235959"),10,"octopus"},msconn);
      esdk.tool.assertEquals(update.getSQL(),"update checkstop set lastchecktime=20081201235959,intertime=10 where pname='octopus'");
      esdk.tool.assertEquals(update.perform());
      esdk.tool.aeic(select.toABResultSet().toCsv(),
              "PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID\r\n" +
                      "octopus,20081201235959,10,admin,admin@sage.com,true");
      JDBCTemplate delete=new JDBCTemplate("delete from checkstop where pname=?",new Object[] {"octopus"},msconn);
      esdk.tool.assertEquals(delete.perform());
      esdk.tool.aeic(select.toABResultSet().toCsv(),"PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID");
      JDBCTemplate jt=new JDBCTemplate(msconn,"select count(*) from checkstop where alertlist=?","ffy,someone");
      esdk.tool.assertEquals(jt.queryForInt(),5);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
  public static void main(String[] args){
    test();
  }

}
