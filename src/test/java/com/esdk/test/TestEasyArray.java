package com.esdk.test;

import com.esdk.esdk;
import com.esdk.utils.EasyArray;

public class TestEasyArray{
	
	public static void test() throws Exception{
		Long[] la=esdk.math.toLongArray("1,2");
		la=EasyArray.concat(la);
		esdk.tool.assertEquals(esdk.str.valueOf(la),"1,2");
		la=null;
		la=esdk.array.concat(Long.class,la);
		esdk.tool.assertEquals(la.length,0);
	}

	public static void main(String[] args) throws Exception{
		test();
		esdk.tool.printAssertInfo();
	}
}
