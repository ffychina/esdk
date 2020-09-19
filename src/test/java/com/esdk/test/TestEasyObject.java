package com.esdk.test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;

import com.esdk.esdk;
import com.esdk.test.bean.Contact;
import com.esdk.test.orm.SampleRow;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyStr;
import com.esdk.utils.EasyTime;
import com.esdk.utils.TestHelper;

public class TestEasyObject{
	
	private static void test1() throws Exception {
  	Contact c1=new Contact();
  	esdk.tool.assertEquals(!esdk.obj.equals(c1,null));
  	SampleRow row=new SampleRow();
  	row.setName(" abcd ");
  	esdk.obj.trim(row);
  	esdk.tool.assertEquals(row.getName(),"abcd");
  }
  
	public static void test() throws Exception{
		esdk.tool.getSystemEnvironment();
		esdk.tool.assertEquals(esdk.tool.getSystemEncoding(),"GBK");
		esdk.tool.assertEquals(esdk.tool.getFileEncoding(),"UTF-8");
		esdk.tool.assertEquals(esdk.tool.isWindowsSystem());
		EasyStr.equals(new File(esdk.tool.getPropertiesValueWithSystemEnvirnment("${java.io.tmpdir}/logs/sql.log")).getPath(),"C:\\Users\\*\\AppData\\Local\\Temp\\logs\\sql.log");
		esdk.tool.assertEquals(esdk.obj.isBlank(Constant.ZERO));
		esdk.tool.assertEquals(!esdk.obj.isBlank(9.275E8));
		esdk.tool.assertEquals(!esdk.obj.isBlank(0.1));
		esdk.tool.assertEquals(esdk.obj.isTrue(true));
// System.out.println(getNowTime("HHmm"));
// System.out.println(RoundByLen(20.56875421587,8));
// System.out.println(RoundByLen(542150.5687,8));
// System.out.println(RoundByLen(0.56875421587,8));
// System.out.println(getSystemEnvironment());
		TestHelper.assertEquals(!esdk.obj.isNumeric('@'));
		TestHelper.assertEquals(esdk.tool.occurID(10).toString().matches("\\d{10}"));
		System.out.println(esdk.tool.occurID().toString().matches("\\d{18}"));
		System.out.println(esdk.tool.occurID(20).toString().matches("\\d{20}"));
		esdk.tool.read("a",null,new BigDecimal(2));
		esdk.tool.assertEquals(esdk.tool.asSubClass(Integer.class,Integer.class));
		esdk.tool.assertEquals(esdk.tool.asSubClass(Integer.class,Number.class));
		esdk.tool.assertEquals(!esdk.tool.asSubClass(Boolean.class,String.class));
		esdk.tool.assertEquals(esdk.obj.isTrue("是"));
		esdk.tool.assertEquals(!esdk.obj.isTrue("N"));
		esdk.tool.assertEquals(esdk.obj.convert(null,int.class),0);
		esdk.tool.assertEquals(esdk.obj.convert("",Integer.class),null);
		esdk.tool.assertEquals(esdk.obj.convert(null,Long.class),null);
		esdk.tool.assertEquals(esdk.obj.convert(null,long.class),0L);
		esdk.tool.assertEquals(esdk.obj.convert(0,boolean.class),false);
		esdk.tool.assertEquals(esdk.obj.convert("true",Boolean.class),Boolean.TRUE);
		esdk.tool.assertEquals(EasyTime.formatDate(((Date)esdk.obj.convert("",Date.class))),null);
		esdk.tool.assertEquals(EasyTime.formatDate((Date)esdk.obj.convert("20080102",Timestamp.class)),"2008-01-02 00:00:00");
		esdk.tool.assertEquals(esdk.obj.convert(10.2,double.class),10.2);
		esdk.tool.assertEquals(esdk.obj.convert(10.2,String.class),"10.2");
		esdk.tool.assertEquals(!esdk.obj.equal(0.2,BigDecimal.valueOf(0.2)));
		esdk.tool.assertEquals(esdk.obj.eq(0.2,BigDecimal.valueOf(0.2)));
		esdk.obj.compareTo(0.2,new Float(0.2));
		esdk.tool.assertEquals(!esdk.obj.eq(1.2,1.2f)); // notice：Float convert to Double will occur different result。
		esdk.tool.assertEquals(esdk.obj.or("",null,"abc"),"abc");
	// test if msSequence arrive to 999 in 1ms, make sure not happen this situation at any server.
		HashSet ids=new HashSet(1000);
			int i=1;
			for(;i<10000;i++){
				Object id=esdk.tool.occurID();
//				System.out.println(id);
				if(!ids.contains(id)) {
					ids.add(id);
				}
				else {
					System.err.println("出现重复ID："+id);
					break;
				}
			}
			esdk.tool.assertEquals(i>=1000);
	}

	public static void main(String[] args) throws Exception{
		 esdk.tool.printJavaEnvirment();
//		System.out.println(getSystemEnvironment());
//		test();
		test1();
		esdk.tool.printAssertInfo();
	}
}
