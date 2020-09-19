package com.esdk.test.orm;
import java.util.Date;

import com.esdk.sql.orm.ForeignKey;

public class TestDictMetaData{
  public static final String TABLENAME="test_dict";
  public static final String TableComment="测试表";
  public static final String PrimaryKey="dict_id";
  public static final int IPrimaryKey=0;
  public static final boolean IsAutoIncrement=true;
  public static final String[] UniqueIndexFields=new String[]{};
  public static final String DictId="dict_id";
  public static final String Category="category";
  public static final String Name="name";
  public static final String Content="content";
  public static final String Sequence="sequence";
  public static final String Memo="memo";
  public static final String CenterId="center_id";
  public static final String Valid="valid";
  public static final String CreateTime="create_time";
  public static final String CreateUserId="create_user_id";
  public static final String CreateUserName="create_user_name";
  public static final String ModifyTime="modify_time";
  public static final String ModifyUserId="modify_user_id";
  public static final String ModifyUserName="modify_user_name";
  public static final String DeleteTime="delete_time";
  public static final String DeleteUserId="delete_user_id";
  public static final String DeleteUserName="delete_user_name";
  public static final String Version="version";
  public static final ForeignKey[] ExportKeys=new ForeignKey[]{};
  public static final ForeignKey[] ImportKeys=new ForeignKey[]{};
  public static final String[] FieldNames=new String[]{"dict_id","category","name","content","sequence","memo","center_id","valid","create_time","create_user_id","create_user_name","modify_time","modify_user_id","modify_user_name","delete_time","delete_user_id","delete_user_name","version"};
  public static final String[] Remarks=new String[]{"","分类","名称或Key","内容或值","显示顺序","备注","中心ID","","","","","","","","","","",""};
  public static final Class[] FieldTypes=new Class[]{Long.class,String.class,String.class,String.class,Integer.class,String.class,Long.class,Boolean.class,java.util.Date.class,Long.class,String.class,java.util.Date.class,Long.class,String.class,java.util.Date.class,Long.class,String.class,Integer.class};
  public static final boolean[] isNullables=new boolean[]{false,false,false,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true};
  public static final String RDictId="";
  public static final String RCategory="分类";
  public static final String RName="名称或Key";
  public static final String RContent="内容或值";
  public static final String RSequence="显示顺序";
  public static final String RMemo="备注";
  public static final String RCenterId="中心ID";
  public static final String RValid="";
  public static final String RCreateTime="";
  public static final String RCreateUserId="";
  public static final String RCreateUserName="";
  public static final String RModifyTime="";
  public static final String RModifyUserId="";
  public static final String RModifyUserName="";
  public static final String RDeleteTime="";
  public static final String RDeleteUserId="";
  public static final String RDeleteUserName="";
  public static final String RVersion="";
}