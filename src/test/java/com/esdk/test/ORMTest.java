package com.esdk.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.nutz.lang.Streams;
import org.nutz.lang.Xmls;

import com.esdk.esdk;
import com.esdk.interfaces.IRequestClose;
import com.esdk.sql.Delete;
import com.esdk.sql.Field;
import com.esdk.sql.Insert;
import com.esdk.sql.OrderBy;
import com.esdk.sql.Select;
import com.esdk.sql.SmartPersistence;
import com.esdk.sql.Update;
import com.esdk.sql.datasource.FileConnectionPool;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.IRow;
import com.esdk.sql.orm.IRowSet;
import com.esdk.sql.orm.ORMSession;
import com.esdk.sql.orm.ORMSessionBuilder;
import com.esdk.sql.orm.ORMSessionFactory;
import com.esdk.sql.orm.ParentRow;
import com.esdk.sql.orm.RowUtils;
import com.esdk.sql.orm.TableResultSet;
import com.esdk.sql.orm.TableRow;
import com.esdk.test.orm.CheckStopMetaData;
import com.esdk.test.orm.CheckStopResultSet;
import com.esdk.test.orm.CheckStopRow;
import com.esdk.test.orm.CheckStopSelect;
import com.esdk.test.orm.OrderFeedbackExtentResultSet;
import com.esdk.test.orm.OrderFeedbackExtentRow;
import com.esdk.test.orm.OrderFeedbackExtentSelect;
import com.esdk.test.orm.SampleRow;
import com.esdk.test.orm.SampleSelect;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyJson;
import com.esdk.utils.EasyStr;
import com.esdk.utils.TString;
import com.esdk.utils.TestHelper;

import cn.hutool.core.util.XmlUtil;

public class ORMTest{
  public static void test0(){
    try{
      Connection conn=FileConnectionPool.getConnection();
      conn.setTransactionIsolation(conn.TRANSACTION_READ_COMMITTED);
      TestHelper.assertEquals(conn.getTransactionIsolation(),2);
      conn.setTransactionIsolation(conn.getTransactionIsolation()-1);
      TestHelper.assertEquals(conn.getTransactionIsolation(),1);
      OrderFeedbackExtentSelect select=new OrderFeedbackExtentSelect(conn);
      select.setOutputAllFields(true);
      select.setTop(10);
      select.setOrderNumber("0201031570");
      select.setClientID(new BigDecimal("401181359590000001"));
      OrderFeedbackExtentResultSet rs=select.toOrderFeedbackExtentResultSet();
      OrderFeedbackExtentRow[] rows=rs.getAllRow();
      for(int i=0;i<rows.length;i++){
        TestHelper.assertEquals(rows[i].getOrderNumber(),"0201031570");
        TestHelper.assertEquals(rows[i].getHandleTime().toString().length(),14);
        Date nowtime=new Date();
        rows[i].setHandleTime(nowtime);
        TestHelper.assertEquals(rows[i].getHandleTime().longValue(),(nowtime.getTime()));
        TestHelper.assertEquals(rows[i].isChanged());
        rows[i].update();
        rows[i].flush();
        OrderFeedbackExtentRow deleteRow=(OrderFeedbackExtentRow)rows[i].clone();
        deleteRow.delete();
        TestHelper.assertEquals(deleteRow.flush());
        OrderFeedbackExtentRow insertRow=(OrderFeedbackExtentRow)rows[i].clone();
        insertRow.testinsert();
        insertRow.insert();
        TestHelper.assertEquals(insertRow.flush());
        rows[i].setPrimaryKey(new BigDecimal("611211831308436669"));
        rows[i].refresh();
        TestHelper.assertEquals(rows[i].getOrderNumber(),"0080001433");
        TestHelper.assertEquals(rows[i].getCurrentStep(),"PSSIGNRTN");
        TestHelper.assertEquals(!rows[i].isChanged());
        TestHelper.assertEquals(rows[i].toXml(),"<OrderFeedbackExtent><DictId>611211831308436669</DictId><ClientID>212091553306395008</ClientID><OrderTypeID>609061536199374763</OrderTypeID><Name>0080001433</Name><FeedbackResult></FeedbackResult><CurrentStep>PSSIGNRTN</CurrentStep><WarehouseOutFeedbackTime>20070124160413</WarehouseOutFeedbackTime><WarehouseInFeedbackTime>0</WarehouseInFeedbackTime><FirstCarryFeedbackTime>0</FirstCarryFeedbackTime><LastCarryFeedbackTime>0</LastCarryFeedbackTime><FullCarryFeedbackTime>0</FullCarryFeedbackTime><ArrivedFeedbackTime>0</ArrivedFeedbackTime><SignFeedbackTime>20061231172234</SignFeedbackTime><FinanceFeedbackTime></FinanceFeedbackTime><IsFeedbackFinish>true</IsFeedbackFinish><HandleTime>20061231172235</HandleTime><Valid>true</Valid><ReceiptTime></ReceiptTime></OrderFeedbackExtent>");
        TestHelper.assertEquals(rows[i].toCsv(),
            "DictId,ClientID,OrderTypeID,Name,FeedbackResult,CurrentStep,WarehouseOutFeedbackTime,WarehouseInFeedbackTime,FirstCarryFeedbackTime,LastCarryFeedbackTime,FullCarryFeedbackTime,ArrivedFeedbackTime,SignFeedbackTime,FinanceFeedbackTime,IsFeedbackFinish,HandleTime,Valid,ReceiptTime\n" + 
            "611211831308436669,212091553306395008,609061536199374763,0080001433,,PSSIGNRTN,20070124160413,0,0,0,0,0,20061231172234,,true,20061231172235,true,"
            );
        TestHelper.assertEquals(rows[i].toXml("DictId,Name,CurrentStep,HandleTime,Valid".split(",")),"<OrderFeedbackExtent><DictId>611211831308436669</DictId><Name>0080001433</Name><CurrentStep>PSSIGNRTN</CurrentStep><HandleTime>20061231172235</HandleTime><Valid>true</Valid></OrderFeedbackExtent>");
        rows[i].load(Xmls.xml(Streams.wrap(rows[i].toXml().getBytes())));
        TestHelper.assertEquals(!rows[i].isChanged());
      }
      TestHelper.assertEquals(XmlUtil.parseXml(new ABRowSet(rows).toXml()).toString(),
          "<list>\r\n" + 
          "  <OrderFeedbackExtent>\r\n" + 
          "    <DictId>611211831308436669</DictId>\r\n" + 
          "    <ClientID>212091553306395008</ClientID>\r\n" + 
          "    <OrderTypeID>609061536199374763</OrderTypeID>\r\n" + 
          "    <Name>0080001433</Name>\r\n" + 
          "    <FeedbackResult />\r\n" + 
          "    <CurrentStep>PSSIGNRTN</CurrentStep>\r\n" + 
          "    <WarehouseOutFeedbackTime>20070124160413</WarehouseOutFeedbackTime>\r\n" + 
          "    <WarehouseInFeedbackTime>0</WarehouseInFeedbackTime>\r\n" + 
          "    <FirstCarryFeedbackTime>0</FirstCarryFeedbackTime>\r\n" + 
          "    <LastCarryFeedbackTime>0</LastCarryFeedbackTime>\r\n" + 
          "    <FullCarryFeedbackTime>0</FullCarryFeedbackTime>\r\n" + 
          "    <ArrivedFeedbackTime>0</ArrivedFeedbackTime>\r\n" + 
          "    <SignFeedbackTime>20061231172234</SignFeedbackTime>\r\n" + 
          "    <FinanceFeedbackTime />\r\n" + 
          "    <IsFeedbackFinish>true</IsFeedbackFinish>\r\n" + 
          "    <HandleTime>20061231172235</HandleTime>\r\n" + 
          "    <Valid>true</Valid>\r\n" + 
          "    <ReceiptTime />\r\n" + 
          "  </OrderFeedbackExtent>\r\n" + 
          "</list>");
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  public static void test1() throws Exception{
    Connection conn=FileConnectionPool.getConnection();
    SampleSelect s=new SampleSelect(conn);
    s.setName("GZ0080252352");
    List list=s.list();
    for(int i=0;i<list.size();i++){
      SampleRow row=(SampleRow)list.get(i);
      System.out.println(row.getName());
      row.setName("GZ0080252352-1");
      row.setName("GZ0080252352");
      row.save();
      TestHelper.assertEquals(row.flush());
    }
    conn.close();
  }

  public static void test2() throws Exception{
    Connection conn=FileConnectionPool.getConnection();
    ORMSession session=ORMSessionFactory.getORMSession(conn);
    SampleSelect s=(SampleSelect)session.createSelect(SampleSelect.class);
    s.setName("GZ0080252352");
    List list=s.list();
    for(int i=0;i<list.size();i++){
      SampleRow row=(SampleRow)list.get(i);
      System.out.println(row.getName());
      row.setName("GZ0080252352-1");
      row.setName("GZ0080252352");
      row.save();
      TestHelper.assertEquals(row.flush());
    }
    SampleSelect s1=(SampleSelect)session.createSelect(SampleSelect.class);
    s1.setName("GZ0080252352");
    //get same address of list by session
    TestHelper.assertEquals(s1.list()==s.list());
    SampleRow row=new SampleRow();
    row.setSession(session);
    row.setPrimaryKey(new BigDecimal(111));
    row.setName("test");
    row.insert();
    row.preCommit();//无需调用commit()
    SampleSelect s2=(SampleSelect)session.createSelect(SampleSelect.class);
    s2.setTop(0);
    s2.setDictId(111L);
    TestHelper.assertEquals(((Number)s2.getFirstRow().getPrimaryKey()).intValue(),111);
    conn.close();
  }

  public static void testParantRowByVersion() throws SQLException {
    Connection conn=FileConnectionPool.getConnection();
    Select o=new Select("OrderMaster",conn);
    o.addEqualCondition("Name","lv20070115-1");
    TableResultSet trs=o.toTableResultSet();
    trs.next();
    TableRow row=(TableRow)trs.getRow(0);
    row.setCheckVersion(true);
    row.set("UserValue1",new Date());
    TestHelper.assertEquals(row.flush());
    conn.close();
  }
  
  public static void testParentRow() throws Exception{
    CheckStopRow row=new CheckStopRow();
    Connection conn=FileConnectionPool.getConnection();
    row.setConnection(conn);
    row.setPrimaryKey("test");
    row.refresh();
    TestHelper.assertEquals(!row.isExistRecord());
    row.delete();
    row.flush();
    row.setLastCheckTime(new BigDecimal("20080912121212"));
    row.setMailList("project.ffy@163.com");
    row.setInterTime(new BigDecimal(10));
    row.insert();
    TestHelper.assertEquals(row.flush());
    row.setInterTime(null);
    TestHelper.assertEquals(row.get(CheckStopMetaData.Valid),null);
    row.refresh();
    TestHelper.assertEquals(!row.getValid());
    row.setValid(true);
    row.update();
    row.setCheckDirty(true);
    TestHelper.assertEquals(row.flush());
    row.delete();
    TestHelper.assertEquals(row.flush());
    conn.close();
  }
  
  public static void testParentResultSet() throws Exception{
    Connection conn=FileConnectionPool.getConnection();
    CheckStopSelect s=new CheckStopSelect(conn);
    CheckStopResultSet rs=s.toCheckStopResultSet();
    TestHelper.assertEquals(rs.next());
    TestHelper.assertEquals(rs.getCursor(),0);
    ParentRow firstRow=rs.getCurrentRow();
    TestHelper.assertEquals(rs.absolute(1));
    TestHelper.assertEquals(rs.previous());
    TestHelper.assertEquals(rs.getCursor(),0);
    TestHelper.assertEquals(firstRow,rs.gainCurrentRow());
    TestHelper.assertEquals(rs.first());
    TestHelper.assertEquals(rs.getCursor(),0);
    TestHelper.assertEquals(rs.last());
    TestHelper.assertEquals(!rs.next());
    ParentRow[] rows=rs.getAllRow();
    TestHelper.assertEquals(rs.isAfterLast());
    TestHelper.assertEquals(rows.length,rs.getCursor());
    esdk.tool.aeic(EasyStr.arrToStr(rs.getColumns()),"pName,lastCheckTime,interTime,alertList,mailList,Valid");
    TestHelper.assertEquals(rs.relative(-rows.length));
    TestHelper.assertEquals(rs.isFirst());
    TestHelper.assertEquals(new TString(rs.toCsv()).getLineCount(),6);
    TestHelper.assertEquals(rs.subRowSet(1,3).toCsv(),
    		"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\n" + 
    		"AVASH,20090410130020,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AWZYDO,20080501075702,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"CNPCGZSO,20080501075603,,\"ffy,someone\",project.ffy@pgl-world.com,true");
    
    TestHelper.assertEquals(rs.subRowSet(0,10).toCsv(),
    		"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\n" + 
    		"AVAOrderTypeAssign,20080501104806,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AVASH,20090410130020,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AWZYDO,20080501075702,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"CNPCGZSO,20080501075603,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"SECASN,20080501104729,,\"ffy,someone\",project.ffy@pgl-world.com,true");    
    conn.close();
    TestHelper.assertEquals(EasyJson.toJSONString(rs.subRowSet(0,10).toMapList()).toString(),"[{\"PNAME\":\"AVAOrderTypeAssign\",\"LASTCHECKTIME\":20080501104806,\"INTERTIME\":null,\"ALERTLIST\":\"ffy,someone\",\"MAILLIST\":\"project.ffy@pgl-world.com\",\"VALID\":true},{\"PNAME\":\"AVASH\",\"LASTCHECKTIME\":20090410130020,\"INTERTIME\":null,\"ALERTLIST\":\"ffy,someone\",\"MAILLIST\":\"project.ffy@pgl-world.com\",\"VALID\":true},{\"PNAME\":\"AWZYDO\",\"LASTCHECKTIME\":20080501075702,\"INTERTIME\":null,\"ALERTLIST\":\"ffy,someone\",\"MAILLIST\":\"project.ffy@pgl-world.com\",\"VALID\":true},{\"PNAME\":\"CNPCGZSO\",\"LASTCHECKTIME\":20080501075603,\"INTERTIME\":null,\"ALERTLIST\":\"ffy,someone\",\"MAILLIST\":\"project.ffy@pgl-world.com\",\"VALID\":true},{\"PNAME\":\"SECASN\",\"LASTCHECKTIME\":20080501104729,\"INTERTIME\":null,\"ALERTLIST\":\"ffy,someone\",\"MAILLIST\":\"project.ffy@pgl-world.com\",\"VALID\":true}]");
  }

  private static void testABRowSet()throws Exception{
    Connection conn=FileConnectionPool.getConnection();
    ParentRow[] rows=new CheckStopSelect(conn).toCheckStopRowArray();
    ABRowSet rowset=new ABRowSet(rows);
    TestHelper.assertEquals(rowset.first());
    rowset.next();
    TestHelper.assertEquals(rowset.getCursor(),1);
    TestHelper.assertEquals(rowset.last());
    TestHelper.assertEquals(!rowset.next());
    TestHelper.assertEquals(rowset.absolute(rowset.size()-1));
    TestHelper.assertEquals(!rowset.absolute(rowset.size()));
    rowset.afterLast();
    TestHelper.assertEquals(rowset.getCursor()==-1);
    rowset.beforeFirst();
    TestHelper.assertEquals(rowset.next());
    TestHelper.assertEquals(rowset.hasColumnName(CheckStopMetaData.pName));
    TestHelper.assertEquals(rowset.getColumnNames(),CheckStopMetaData.FieldNames);
    TestHelper.assertEquals(!rowset.isEmpty());
    ABRowSet newRowSet=new ABRowSet(rowset.getColumnNames());
    newRowSet.add(rowset.getRow(0));
    newRowSet.add(Arrays.asList(rowset.getRow(1)));
    TestHelper.assertEquals(newRowSet.toCsv(),
        "lastCheckTime,interTime,alertList,mailList,Valid\r\n" + 
        "AVAOrderTypeAssign,*,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
        "AVASH,*,,\"ffy,someone\",project.ffy@pgl-world.com,true");
    TestHelper.assertEquals(newRowSet.getRow(0)==rowset.getRow(0));
    TestHelper.assertEquals(newRowSet.getRow(1)==rowset.getRow(1));
    IRowSet cloneRowSet=(IRowSet)newRowSet.clone();
    TestHelper.assertEquals(cloneRowSet.size(),2);
    TestHelper.assertEquals(!cloneRowSet.getRow(1).equals(newRowSet.getRow(1)));
    TestHelper.assertEquals(cloneRowSet.getRow(1)!=newRowSet.getRow(1));
//    TestHelper.assertEquals(rowset.getRow(0).toXml(),"<CheckStop><pName>AVAOrderTypeAssign</pName><lastCheckTime>*</lastCheckTime><interTime></interTime><alertList>ffy,someone</alertList><mailList>project.ffy@pgl-world.com</mailList><Valid>true</Valid></CheckStop>");
    TestHelper.assertEquals(rowset.getRow(0).toCsv(),"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\nAVAOrderTypeAssign,*,,\"ffy,someone\",project.ffy@pgl-world.com,true");
    IRow replacerow=rowset.getRow(1);
    TestHelper.assertEquals(rowset.setRow(1,rowset.getRow(0)),replacerow);
    TestHelper.assertEquals(!rowset.remove(replacerow));
    TestHelper.assertEquals(rowset.getRow(0),rowset.remove(0));
    conn.close();
  }
  
  public static void testABRow() throws Exception {
    Connection conn=FileConnectionPool.getConnection();
    ABResultSet rs=new CheckStopSelect(conn).toABResultSet();
    rs.next();
    IRow row=rs.getRow();
    esdk.tool.aeic(row.toCsv(),"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\nAVAOrderTypeAssign,*,,\"ffy,someone\",project.ffy@pgl-world.com,true");
//    esdk.tool.aeic(row.toXml(),"<record><pName>AVAOrderTypeAssign</pName><lastCheckTime>*</lastCheckTime><interTime></interTime><alertList>ffy,someone</alertList><mailList>project.ffy@pgl-world.com</mailList><Valid>true</Valid></record>");
    esdk.tool.aeic(EasyStr.arrToStr(row.getNames(),"|"),"pName|lastCheckTime|interTime|alertList|mailList|Valid");
    TestHelper.assertEquals(row.hasColumnName(CheckStopMetaData.interTime));
    rs.absolute(1);
    IRow clone=(IRow)row.clone();
    row.set(CheckStopMetaData.mailList,"ffy");
    TestHelper.assertEquals(!clone.get(CheckStopMetaData.mailList).equals(row.get(CheckStopMetaData.mailList)));
    clone.load(rs.getRow());
    TestHelper.assertEquals(!clone.get(CheckStopMetaData.pName).equals(row.get(CheckStopMetaData.pName)));
    TestHelper.assertEquals(new TString(rs.toCsv(new String[] {CheckStopMetaData.pName,CheckStopMetaData.lastCheckTime})).getLineCount(),6);
    conn.close();
  }

  public static void testTableResultSet() throws Exception {
    Connection conn=FileConnectionPool.getConnection();
    TableResultSet rs=new TableResultSet(new Select("CheckStop",conn));
    rs.next();
    IRow row=rs.getRow();
    TestHelper.assertEquals(EasyStr.arrToStr(row.getNames()),"PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID");
    esdk.tool.aeic(row.toCsv(),"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\nAVAOrderTypeAssign,*,,\"ffy,someone\",project.ffy@pgl-world.com,true");
//    esdk.tool.aeic(row.toXml(),"<CheckStop><pName>AVAOrderTypeAssign</pName><lastCheckTime>*</lastCheckTime><interTime></interTime><alertList>ffy,someone</alertList><mailList>project.ffy@pgl-world.com</mailList><Valid>true</Valid></CheckStop>");
    esdk.tool.aeic(EasyStr.arrToStr(row.getNames(),"|"),"pName|lastCheckTime|interTime|alertList|mailList|Valid");
    TestHelper.assertEquals(row.hasColumnName(CheckStopMetaData.interTime));
    rs.absolute(1);
    IRow clone=(IRow)row.clone();
    row.set(CheckStopMetaData.mailList,"ffy");
    TestHelper.assertEquals(!clone.get(CheckStopMetaData.mailList).equals(row.get(CheckStopMetaData.mailList)));
    clone.load(rs.getRow());
    TestHelper.assertEquals(!clone.get(CheckStopMetaData.pName).equals(row.get(CheckStopMetaData.pName)));
    TestHelper.assertEquals(new TString(rs.toCsv(new String[] {CheckStopMetaData.pName,CheckStopMetaData.lastCheckTime})).getLineCount(),6);
    TableRow newRow=rs.insertRow();
    newRow.setPrimaryKey("test");
    newRow.set("Valid",1);
    TestHelper.assertEquals(newRow.get("Valid"),1);
    newRow.set("lastCheckTime",new Date());
    newRow.set("interTime",10);
    newRow.flush();
    newRow.set("Valid",0);
    TestHelper.assertEquals(newRow.get("Valid"),0);
    newRow.set(CheckStopMetaData.mailList,"project.ffy@pgl-world.com");
    newRow.update();
    esdk.tool.assertEquals(newRow.isChanged());
    TestHelper.assertEquals(newRow.flush());
    esdk.tool.assertEquals(!newRow.isChanged());
    //test checkDirty()
    newRow.refresh();
    newRow.setCheckDirty(true);
    newRow.set("Valid",true);
    newRow.set("interTime",20);
    newRow.set("lastCheckTime",new BigDecimal("20080202"));
    newRow.save();
    newRow.flush();
    //test delete();
    newRow.delete();
    TestHelper.assertEquals(newRow.flush());
    conn.close();
    System.out.println(rs.toCsv());
    rs.beforeFirst();
    rs.sort(true,"Valid","interTime","pName");
    esdk.tool.assertEquals(rs.toCsv(),
    		"PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID\r\n" + 
    		"test,20080202,20,,project.ffy@pgl-world.com,true\r\n" + 
    		"SECASN,20080501104729,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"CNPCGZSO,20080501075603,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AWZYDO,20080501075702,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AVASH,20080501075804,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AVAOrderTypeAssign,20080501104806,,\"ffy,someone\",ffy,true");
    rs.sort(false,"Valid","interTime","pName");
    esdk.tool.assertEquals(rs.toCsv(),
    		"PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID\r\n" + 
    		"AVAOrderTypeAssign,20080501104806,,\"ffy,someone\",ffy,true\r\n" + 
    		"AVASH,20080501075804,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AWZYDO,20080501075702,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"CNPCGZSO,20080501075603,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"SECASN,20080501104729,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"test,20080202,20,,project.ffy@pgl-world.com,true");
    rs.afterLast();
    rs.sort(new OrderBy(new Field("lastCheckTime")),new OrderBy(new Field("pName"),true));
    esdk.tool.assertEquals(rs.toCsv(),
    		"PNAME,LASTCHECKTIME,INTERTIME,ALERTLIST,MAILLIST,VALID\r\n" + 
    		"test,20080202,20,,project.ffy@pgl-world.com,true\r\n" + 
    		"CNPCGZSO,20080501075603,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AWZYDO,20080501075702,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AVASH,20080501075804,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"SECASN,20080501104729,,\"ffy,someone\",project.ffy@pgl-world.com,true\r\n" + 
    		"AVAOrderTypeAssign,20080501104806,,\"ffy,someone\",ffy,true");
  }
  
  private static void testJsonArray() throws SQLException{
  	Connection conn=FileConnectionPool.getConnection();
  	OrderFeedbackExtentSelect s=new OrderFeedbackExtentSelect(conn);
    TestHelper.assertEquals(s.toOrderFeedbackExtentResultSet().toJsonArray().toString(),"[{\"PKID\":509131510416876656,\"arrivedFeedbackTime\":0,\"autoIncrement\":false,\"changed\":{},\"clientID\":401181359590000001,\"currentStep\":\"CheckShpinfFeedbackCuccessed\",\"existRecord\":true,\"feedbackResult\":\"MessageNumber:SI0080300841-01-04,Genres:[SS] ATD is out of ranges. CP010 | [SS] Event Type Invalid : CP090 | [SS] Event Type Invalid : CP090 | [SS] Event Type Invalid : CP090 | [SS] Event Type Invalid : CP090 | [SS] ATD is out of ranges. CP\",\"financeFeedbackTime\":0,\"firstCarryFeedbackTime\":0,\"fullCarryFeedbackTime\":20070312112918,\"handleTime\":20080430202445,\"isFeedbackFinish\":true,\"lastCarryFeedbackTime\":20070313141046,\"map\":{\"ARRIVEDFEEDBACKTIME\":0,\"CLIENTID\":401181359590000001,\"CURRENTSTEP\":\"CheckShpinfFeedbackCuccessed\",\"FEEDBACKRESULT\":\"MessageNumber:SI0080300841-01-04,Genres:[SS] ATD is out of ranges. CP010 | [SS] Event Type Invalid : CP090 | [SS] Event Type Invalid : CP090 | [SS] Event Type Invalid : CP090 | [SS] Event Type Invalid : CP090 | [SS] ATD is out of ranges. CP\",\"FINANCEFEEDBACKTIME\":0,\"FIRSTCARRYFEEDBACKTIME\":0,\"FULLCARRYFEEDBACKTIME\":20070312112918,\"HANDLETIME\":20080430202445,\"ISFEEDBACKFINISH\":true,\"LASTCARRYFEEDBACKTIME\":20070313141046,\"DictId\":509131510416876656,\"Name\":\"0201031570\",\"ORDERTYPEID\":506061458579539691,\"SIGNFEEDBACKTIME\":0,\"VALID\":true,\"WAREHOUSEOUTFEEDBACKTIME\":0},\"metaData\":{},\"names\":[\"DictId\",\"ClientID\",\"OrderTypeID\",\"Name\",\"FeedbackResult\",\"CurrentStep\",\"WarehouseOutFeedbackTime\",\"WarehouseInFeedbackTime\",\"FirstCarryFeedbackTime\",\"LastCarryFeedbackTime\",\"FullCarryFeedbackTime\",\"ArrivedFeedbackTime\",\"SignFeedbackTime\",\"FinanceFeedbackTime\",\"IsFeedbackFinish\",\"HandleTime\",\"Valid\",\"ReceiptTime\"],\"newValues\":{},\"DictId\":509131510416876656,\"Name\":\"0201031570\",\"orderTypeID\":506061458579539691,\"primaryKey\":509131510416876656,\"primaryKeyName\":\"DictId\",\"saved\":true,\"signFeedbackTime\":0,\"tableName\":\"OrderFeedbackExtent\",\"valid\":true,\"warehouseOutFeedbackTime\":0},{\"PKID\":611211831308436669,\"arrivedFeedbackTime\":0,\"autoIncrement\":false,\"changed\":{},\"clientID\":212091553306395008,\"currentStep\":\"PSSIGNRTN\",\"existRecord\":true,\"feedbackResult\":\"\",\"firstCarryFeedbackTime\":0,\"fullCarryFeedbackTime\":0,\"handleTime\":20061231172235,\"isFeedbackFinish\":true,\"lastCarryFeedbackTime\":0,\"map\":{\"ARRIVEDFEEDBACKTIME\":0,\"CLIENTID\":212091553306395008,\"CURRENTSTEP\":\"PSSIGNRTN\",\"FEEDBACKRESULT\":\"\",\"FIRSTCARRYFEEDBACKTIME\":0,\"FULLCARRYFEEDBACKTIME\":0,\"HANDLETIME\":20061231172235,\"ISFEEDBACKFINISH\":true,\"LASTCARRYFEEDBACKTIME\":0,\"DictId\":611211831308436669,\"Name\":\"0080001433\",\"ORDERTYPEID\":609061536199374763,\"SIGNFEEDBACKTIME\":20061231172234,\"VALID\":true,\"WAREHOUSEINFEEDBACKTIME\":0,\"WAREHOUSEOUTFEEDBACKTIME\":20070124160413},\"metaData\":{},\"names\":[\"DictId\",\"ClientID\",\"OrderTypeID\",\"Name\",\"FeedbackResult\",\"CurrentStep\",\"WarehouseOutFeedbackTime\",\"WarehouseInFeedbackTime\",\"FirstCarryFeedbackTime\",\"LastCarryFeedbackTime\",\"FullCarryFeedbackTime\",\"ArrivedFeedbackTime\",\"SignFeedbackTime\",\"FinanceFeedbackTime\",\"IsFeedbackFinish\",\"HandleTime\",\"Valid\",\"ReceiptTime\"],\"newValues\":{},\"DictId\":611211831308436669,\"Name\":\"0080001433\",\"orderTypeID\":609061536199374763,\"primaryKey\":611211831308436669,\"primaryKeyName\":\"DictId\",\"saved\":true,\"signFeedbackTime\":20061231172234,\"tableName\":\"OrderFeedbackExtent\",\"valid\":true,\"warehouseInFeedbackTime\":0,\"warehouseOutFeedbackTime\":20070124160413}]");
	}

  public static void testSessionBuilder() throws Exception{
    ORMSessionBuilder sb=new ORMSessionBuilder();
    Select s=sb.createSelect("CheckStop");
    s.addEqualCondition("pName","SECASN");
    esdk.tool.aeic(s.toABResultSet().toCsv(),"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\nSECASN,*,,\"ffy,someone\",project.ffy@pgl-world.com,true");
    CheckStopSelect select=(CheckStopSelect)sb.createSelect(CheckStopSelect.class);
    select.setPname("SECASN");
    esdk.tool.aeic(select.toABResultSet().toCsv(),"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\nSECASN,*,,\"ffy,someone\",project.ffy@pgl-world.com,true");
    IRow row=sb.createRow(CheckStopRow.class,new String("SECASN"));
    esdk.tool.aeic(row.toCsv(),"pName,lastCheckTime,interTime,alertList,mailList,Valid\r\nSECASN,*,,\"ffy,someone\",project.ffy@pgl-world.com,true");
    Insert insert=sb.createInsert("CheckStop");
    insert.addFieldValue("pName","test");
    insert.addFieldValue("pName","test");
    insert.addFieldValue("lastCheckTime",new Date());
    TestHelper.assertEquals(insert.perform());
    Update update=sb.createUpdate("CheckStop");
    update.addEqualCondition("pName","test");
    update.addFieldValue("alertList","test");
    update.addFieldValue("interTime",10);
    SmartPersistence save=sb.createSave("CheckStop");
    save.setPkFieldValue("pName","SECASN");
    save.addFieldValue("Valid",true);
    TestHelper.assertEquals(save.perform());
    TestHelper.assertEquals(update.perform());
    TestHelper.assertEquals(update.getUpdatedCount(),1);
    Delete delete=sb.createDelete("CheckStop");
    delete.addEqualCondition("pName","test");
    TestHelper.assertEquals(delete.perform());
    ((IRequestClose)sb).close();
  }
  
  private static void testSetNullValueCondition() throws Exception {
    ORMSessionBuilder sb=new ORMSessionBuilder();
    CheckStopSelect s=(CheckStopSelect)sb.createSelect(CheckStopSelect.class);
    esdk.tool.assertEquals(s.isNullable());
    s.setTop(0);
    s.setNullable(false);
    s.setPname(null);
    s.setMailList(null);
    s.setAlertList("ffy");
    s.setInterTime(Constant.ZERO);
    esdk.tool.assertEquals(s.getSQL(),"Select *\r\n" +"From CheckStop \r\n" +"Where alertList = 'ffy'");
  }
  
  private static void test() {
    try{
      testJsonArray();
      testParantRowByVersion();
      testParentResultSet();      
      test0();
      test1();
      test2();
      testParentRow();
      testABRowSet();
      testABRow();
      testTableResultSet();
      testSessionBuilder();
      testSetNullValueCondition();
      TestHelper.printAssertInfo();
    }
    catch(Exception e){
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
      e.printStackTrace();
      TestHelper.errCount++;
    }  
  }

	public static void main(String[] args){
    test();
  }
}
