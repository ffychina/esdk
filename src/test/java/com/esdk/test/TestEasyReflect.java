package com.esdk.test;

import com.esdk.esdk;
import com.esdk.test.orm.CheckStopMetaData;
import com.esdk.test.orm.SampleMetaData;
import com.esdk.utils.EasyReflect;
import com.esdk.utils.EasyStr;
import com.esdk.utils.EasyObj;
import com.esdk.utils.VString;

public class TestEasyReflect extends EasyReflect {
  private static void test() {
    esdk.tool.assertEquals(getDeclaredFieldValue(new VString("TestDeclaredField"),"fs"),"TestDeclaredField");
    esdk.tool.assertEquals(getFieldValue(new CheckStopMetaData(),"lastCheckTime"),"lastCheckTime");
    esdk.tool.assertEquals(EasyStr.arrToStr(EasyStr.toStringArray(getFieldValues(new String[] {"valid","ordernumber","pname","create_time"},new CheckStopMetaData(),new SampleMetaData()))),"Valid,OrderNumber,pName");
  }
  public static void main(String[] args){
    test();
  }


}
