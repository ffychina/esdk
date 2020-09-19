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
import com.esdk.utils.EasyArray;
import com.esdk.utils.EasyMath;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyStr;
import com.esdk.utils.EasyTime;
import com.esdk.utils.TestHelper;
import com.esdk.utils.EasyObj;

public class TestEasyArray{
	
	public static void test() throws Exception{
		Long[] la=EasyMath.toLongArray("1,2");
		la=EasyArray.concat(la);
		esdk.tool.assertEquals(esdk.str.valueOf(la),"1,2");
		la=null;
		la=EasyArray.concat(la);
		esdk.tool.assertEquals(la.length,0);
	}

	public static void main(String[] args) throws Exception{
		test();
		esdk.tool.printAssertInfo();
	}
}
