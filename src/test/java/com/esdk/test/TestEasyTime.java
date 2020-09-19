package com.esdk.test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.esdk.esdk;
import com.esdk.utils.EasyTime;
import com.esdk.utils.ParseException;
import com.esdk.utils.EasyObj;

public class TestEasyTime{
	@SuppressWarnings("deprecation") private static void test() throws ParseException{
		esdk.tool.assertEquals(esdk.time.getStandardTimeFormat("2007-2-4 00:23:21"),"2007-02-04 00:23:21");
		esdk.tool.assertEquals(esdk.time.getStandardTimeFormat("2007-2-4"),"2007-02-04 00:00:00");
		Date date=new Date();
		esdk.tool.assertEquals(esdk.time.getStartDate(date).getTime()<date.getTime());
		esdk.tool.assertEquals(esdk.time.getEndDate(date).getTime()>date.getTime());
		esdk.tool.assertEquals(System.currentTimeMillis()-new Date().getTime(),0);
		System.out.println("2015"+EasyTime.getNowTime("-MM-dd"));
		System.out.println(EasyTime.getBeforeTime("yyyyMMdd",1));
		System.out.println(EasyTime.getBeforeMonth("yyyyMMdd",2));
		File file=new File("./testfiles/RTN-0084031817-20060706094647750.ack.xml");
		/* System.out.println(file.lastModified()); */
		file.setLastModified(Long.valueOf("1152234203000"));
		esdk.tool.aeic(EasyTime.getTime("yyyy-MM-dd HH:mm:ss",file.lastModified()),"2006-07-07 09:03:2?");
		Calendar now=Calendar.getInstance();
		System.out.println("距离现在时间"+(now.getTimeInMillis()-file.lastModified())/1000/60+"分钟");


		esdk.tool.assertEquals(EasyTime.getDayOfWeek(EasyTime.valueOf("2012-12-16")),1); //Sun 
		esdk.tool.assertEquals(EasyTime.getDayOfWeek(EasyTime.valueOf("2012-12-15")),7); //Sat
		Date d1 = EasyTime.getDate("2008-05-03 11:30:20",EasyTime.DATETIME_FORMAT);
		Date d2 = EasyTime.getDate("2008-05-04 12:30:20",EasyTime.DATETIME_FORMAT);
		
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.clear();
		c2.clear();
		//c1.setTime(d1);
		//c2.setTime(d2);
		
		esdk.tool.assertEquals(EasyTime.dateDiff(d1,d2,'s'),90000);   //90000
		esdk.tool.assertEquals(EasyTime.intervalTime(d1,d2,Calendar.SECOND),90000);   //90000
		d1 =EasyTime.getDate("2008-05-03 12:30:21",EasyTime.DATETIME_FORMAT);
		d1 = EasyTime.getDate("2008-04-03 12:30:20",EasyTime.DATETIME_FORMAT);
		d1 = EasyTime.getDate("2008-04-03 12:30:20",EasyTime.DATETIME_FORMAT);
		d1 = EasyTime.getDate("2006-04-1 12:30:20",EasyTime.DATETIME_FORMAT);
		esdk.tool.assertEquals(EasyTime.intervalMonth(d1,d2),25);
		esdk.tool.assertEquals(EasyTime.toString(d1),"2006-04-01 12:30:20");
		esdk.tool.assertEquals(EasyTime.formatDate(d1),"2006-04-01 12:30:20");
		d1 = EasyTime.getDate("2007-12-26");
		d2 = EasyTime.getDate("2009-1-5");
		c1.setTime(d1);
		c2.setTime(d2);
		esdk.tool.assertEquals(EasyTime.toString(d1),"2007-12-26 00:00:00");
		esdk.tool.assertEquals(EasyTime.formatDate(d1,EasyTime.DATE_FORMAT),"2007-12-26");
		esdk.tool.assertEquals(EasyTime.intervalWeekNoTime(d1, d2),44);
		
		d1 = EasyTime.getDate("2008-5-4");
		d2 = EasyTime.getDate("2008-5-24");
		c1.setTime(d1);
		c2.setTime(d2);
		esdk.tool.assertEquals(EasyTime.intervalWeekNoTime(d1, d2),2);
		esdk.tool.assertEquals(EasyTime.intervalTime(d1, d2,Calendar.DATE),20);
		esdk.tool.assertEquals(EasyTime.dateDiff(d1,d2),20);
		
		d1 = EasyTime.getDate("2008-12-31");
		c1.setTime(d1);
		esdk.tool.assertEquals(c1.getTime().toString(),"Wed Dec 31 00:00:00 CST 2008");
		esdk.tool.assertEquals(EasyTime.dateDiff(d1,d2,'M'),-8);
		Date[] holidays=new Date[] {EasyTime.toDate("20081001"),EasyTime.toDate("20081002"),EasyTime.toDate("20081003"),EasyTime.toDate("20081004")};
		esdk.tool.assertEquals(EasyTime.networkDays(d2,d1,holidays),155);//20081004是周六,不会重复计算.
		//while (c1.get(Calendar.WEEK_OF_YEAR) == 1){  //与下年的1号在同一个星期内
			c1.set(Calendar.DATE, 0);
		//}
			c1.set(Calendar.DATE, 0);
		System.out.println(c1.getTime());
		esdk.tool.assertEquals(EasyTime.getStartDate(new Date()).toString(),"*00:00:00*");
		esdk.tool.assertEquals(EasyTime.getEndDate(new Date()).toString(),"*23:59:59*");
		
		Calendar c=Calendar.getInstance();
		c.setTime(new Date(108,2,31));
		esdk.tool.assertEquals(c.getTime().toLocaleString(),"2008-3-31 0:00:00");
		c.add(Calendar.YEAR,-1);
		c.add(Calendar.DAY_OF_YEAR,1);
		esdk.tool.assertEquals(c.getTime().toLocaleString(),"2007-4-1 0:00:00");
		Date d=EasyTime.getDate("2008-02-29");
		d.setYear(d.getYear()+1);
		esdk.tool.assertEquals(d.toLocaleString(),"2009-3-1 0:00:00");
		try{
			esdk.tool.assertEquals(EasyTime.valueOf(""),null);
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("200903")),"2009-03-01 00:00:00");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("20090301")),"2009-03-01 00:00:00");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("20090301125959")),"2009-03-01 12:59:59");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("2009")),"2009-01-01 00:00:00");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("2009-03-01")),"2009-03-01 00:00:00");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("2009-03-01 23:59:59")),"2009-03-01 23:59:59");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("2009-3-1 1:3:15")),"2009-03-01 01:03:15");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("01/03/2009")),"2009-03-01 00:00:00");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("01/03/2009 01:02:03")),"2009-03-01 01:02:03");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("01/03/2009 23:59")),"2009-03-01 23:59:00");
			esdk.tool.assertEquals(EasyTime.formatDate(EasyTime.valueOf("01/03/2009 1:2")),"2009-03-01 01:02:00");
			esdk.tool.assertEquals(EasyTime.computeAge(EasyTime.addDate(new Date(),-60,0,1,0,0,0)),59);
			esdk.tool.assertEquals(EasyTime.computeAge(EasyTime.addDate(new Date(),-60,0,0,0,0,0)),60);
			esdk.tool.assertEquals(EasyTime.computeAge(EasyTime.addDate(new Date(),-60,0,-1,0,0,0)),60);
			esdk.tool.assertEquals(EasyTime.calucateDurationDate(EasyTime.valueOf("2000-07-13"),EasyTime.valueOf("2014-04-3")),"13年8月21日");
			esdk.tool.assertEquals(EasyTime.calucateDurationDate(EasyTime.valueOf("2000-07-13"),EasyTime.valueOf("2014-03-31")),"13年8月18日");
			esdk.tool.assertEquals(EasyTime.calucateDurationDate(EasyTime.valueOf("2000-04-01"),EasyTime.valueOf("2014-03-31")),"13年11月30日");
			esdk.tool.assertEquals(EasyTime.calucateDurationDate(EasyTime.valueOf("2015-04-01"),EasyTime.valueOf("2014-03-31")),"-366日");
			esdk.tool.assertEquals(EasyTime.calucateDurationDate(EasyTime.valueOf("2015-04-01"),EasyTime.valueOf("2014-03-31")),"-366日");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		esdk.tool.assertEquals(EasyTime.formatElapse(EasyTime.getDate("2009-01-04 12:00:05.222",EasyTime.FULL_FORMAT).getTime()-EasyTime.getDate("2009-01-03 11:00:04.111",EasyTime.FULL_FORMAT).getTime()),"25小时0分1秒111毫秒");
		esdk.tool.assertEquals(EasyTime.formatElapse(EasyTime.getDate("2009-01-04 12:00:05.222",EasyTime.FULL_FORMAT).getTime()-EasyTime.getDate("2009-01-03 11:00:04.111",EasyTime.FULL_FORMAT).getTime(),false,false),"25小时0分");
		esdk.tool.assertEquals(EasyTime.formatElapse(EasyTime.getDate("2009-01-04 12:00:05.222",EasyTime.FULL_FORMAT).getTime()-EasyTime.getDate("2009-01-03 11:00:04.111",EasyTime.FULL_FORMAT).getTime(),false,true),"25小时0分1秒");
		esdk.tool.assertEquals(EasyTime.createDate(2009,12,31),EasyTime.getDate("2009-12-31"));
		esdk.tool.assertEquals(EasyTime.createDate(2009,12,0),EasyTime.getDate("2009-11-30"));
		esdk.tool.assertEquals(EasyTime.setTime(EasyTime.createDate(2009,12,31),13,05,59),EasyTime.createDate(2009,12,31,13,05,59));
		esdk.tool.assertEquals(EasyTime.getDate(EasyTime.createDate(2012,01,31)),31);
		esdk.tool.assertEquals(EasyTime.getMonth(EasyTime.createDate(2012,12,31)),12);
		esdk.tool.assertEquals(EasyTime.getYear(EasyTime.createDate(2012,01,31)),2012);
		esdk.tool.assertEquals(EasyTime.time2sec("23:59:59"),86399);
		esdk.tool.assertEquals(EasyTime.sec2time(86399),"23:59:59");
		esdk.tool.assertEquals(EasyTime.sec2time(186399,"d天H小时m分s秒"),"3天3小时46分39秒");
		esdk.tool.assertEquals(EasyTime.time2sec("23小时59分59秒","H小时m分s秒"),86399);
		esdk.tool.assertEquals(EasyTime.time2sec("3天3小时46分39秒","d天H小时m分s秒"),186399);
	}
	
	public static void main(String[] args) throws ParseException {
		test();
		esdk.tool.printAssertInfo();
	}}
