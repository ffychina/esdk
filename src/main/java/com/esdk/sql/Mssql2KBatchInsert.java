package com.esdk.sql;

import java.sql.Connection;
import java.util.Iterator;

import com.esdk.utils.EasyStr;

public class Mssql2KBatchInsert extends BatchInsert{

	public Mssql2KBatchInsert(String tablename,Connection conn){
		super(tablename,conn);
	}
  
	public Mssql2KBatchInsert(String tablename,String[] fields,Connection conn){
		super(tablename,fields,conn);
  }
  
	@Override public void parse(){

    StringBuffer sb=new StringBuffer().append("Insert Into ").append(tableName).append(" (").append(EasyStr.arrToStr(fields)).append(")").append(" select ");
    for(Iterator<Object[]> iter=_values.iterator();iter.hasNext();){
    	Object[] array=iter.next();
    	//sb.append("(");
    	for(int i=0,n=array.length;i<n;i++){
    		sb.append(SQLAssistant.getStmtSqlValue(array[i]));
    		if(i<n-1)
    			sb.append(",");
    	}
    	//sb.append(")");
    	if(iter.hasNext())
    		sb.append(" union all select ");
    }
    insertSQL=sb.toString();
  
	}
	
}
