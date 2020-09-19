package com.esdk.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Constant{
  public final static String NULL=null,BLANK="",CRLF="\r\n",LF="\n",DOUBLE_QUOTE = "\"",SINGLE_QUOTE = "'";
  public final static BigDecimal ZERO=new BigDecimal(0),ONE=new BigDecimal(1),NEGATIVE=new BigDecimal(-1),POSITIVE=new BigDecimal(1);
  public final static String UTF8="UTF-8",GBK="GBK",ISO8859="iso-8859-1";
  public final static int DefaultCacheSec=600;
  public final static int ResetUseCacheSec=-1;
  public final static String[] EmptyStrArr={};
  public final static Long[] EmptyLongArr={};
  public final static Integer[] EmptyIntArr={};
  public final static Map<?,?> EmptyMap=new HashMap(0);
	public final static String[] SystemFields=new String[] {"valid","create_time","create_user_id","create_user_name","modify_time","modify_user_id","modify_user_name","delete_time","delete_user_id","delete_user_name","version"};
	public final static String RowsetXmlIdentifier="list",RowXmlIdentifier="record";
}
