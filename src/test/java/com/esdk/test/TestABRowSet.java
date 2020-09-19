package com.esdk.test;

import com.esdk.esdk;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.test.orm.SampleRow;
import com.esdk.utils.EasyObj;

import java.math.BigDecimal;

public class TestABRowSet {
	public static void main(String[] args){
		ABRowSet<SampleRow> rs=new ABRowSet(SampleRow.class);
		SampleRow row1=rs.append();
		row1.setDictId(1234567890L);
		row1.setName("0001");
		row1.setValid(true);
		row1.set("remark","test");
		SampleRow row2=rs.append();
		row2.setDictId(222222222L);
		row2.setName("0002");
		row2.setValid(true);
		row2.set("remark","大家好");
		System.out.println("打印记录集：\n"+rs.toString());
		esdk.tool.assertEquals("0_SampleRow{\"orderID\":1234567890,\"orderNumber\":\"0001\",\"valid\":true,\"remark\":\"test\"}\n" +
						"1_SampleRow{\"orderID\":222222222,\"orderNumber\":\"0002\",\"valid\":true,\"remark\":\"大家好\"}",rs.toString());
		esdk.tool.printAssertInfo();
	}

	
}