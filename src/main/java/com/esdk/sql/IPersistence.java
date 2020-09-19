package com.esdk.sql;


public interface IPersistence extends ISQL{
	IPersistence addFieldValue(String fieldname,Object value);
  boolean hasFieldValues();
  public String getPreparedSql();
  public Object[] getPreparedParameters();
  public String[] getPreparedFields();
}
