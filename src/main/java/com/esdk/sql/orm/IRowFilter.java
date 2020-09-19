package com.esdk.sql.orm;

public interface IRowFilter{
	boolean filter(IRow row,IRowSet rowset,int index);
}
