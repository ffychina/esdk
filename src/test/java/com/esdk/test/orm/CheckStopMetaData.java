package com.esdk.test.orm;
import java.math.BigDecimal;
public class CheckStopMetaData{
  public static final String TABLENAME="CheckStop";
  public static final String PrimaryKey="pName";
  public static final int IPrimaryKey=0;
  public static final String[] UniqueIndexFields=new String[]{};
  public static final String pName="pName";
  public static final String lastCheckTime="lastCheckTime";
  public static final String interTime="interTime";
  public static final String alertList="alertList";
  public static final String mailList="mailList";
  public static final String Valid="Valid";
  public static final String[] FieldNames=new String[]{"pName","lastCheckTime","interTime","alertList","mailList","Valid"};
  public static final Class[] FieldTypes=new Class[]{String.class,BigDecimal.class,BigDecimal.class,String.class,String.class,Boolean.class};
  public static final int IpName=0;
  public static final int IlastCheckTime=1;
  public static final int IinterTime=2;
  public static final int IalertList=3;
  public static final int ImailList=4;
  public static final int IValid=5;
}
