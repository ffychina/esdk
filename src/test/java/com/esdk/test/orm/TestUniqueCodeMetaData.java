package com.esdk.test.orm;
import com.esdk.sql.orm.ForeignKey;

public class TestUniqueCodeMetaData{
  public static final String TABLENAME="test_unique_code";
  public static final String TableComment="唯一码写入测试";
  public static final String PrimaryKey="uid";
  public static final int IPrimaryKey=0;
  public static final boolean IsAutoIncrement=false;
  public static final String[] UniqueIndexFields=new String[]{};
  public static final String Uid="uid";
  public static final String MachineId="machine_id";
  public static final String ProductId="product_id";
  public static final String Valid="valid";
  public static final String CreateTime="create_time";
  public static final ForeignKey[] ExportKeys=new ForeignKey[]{};
  public static final ForeignKey[] ImportKeys=new ForeignKey[]{};
  public static final String[] FieldNames=new String[]{"uid","machine_id","product_id","valid","create_time"};
  public static final String[] Remarks=new String[]{"","","","",""};
  public static final Class[] FieldTypes=new Class[]{Long.class,Long.class,Long.class,Boolean.class,java.util.Date.class};
  public static final boolean[] isNullables=new boolean[]{false,false,false,false,false};
  public static final Object[] ColumnDefs=new Object[]{null,null,null,1,"curtime()"};
  public static final String RUid="";
  public static final String RMachineId="";
  public static final String RProductId="";
  public static final String RValid="";
  public static final String RCreateTime="";
}
