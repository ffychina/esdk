
package com.esdk.sql;

import java.util.Date;

public class SmartBetween extends Between{
	protected SmartBetween(){
	}

	public SmartBetween(Field field,Date minValue,Date maxValue){
		this();
		this._field=field;
		if(minValue!=null&&maxValue!=null){
			init(field,minValue,maxValue,false);
		}else if(minValue!=null){
			java.sql.Timestamp minDate=new java.sql.Timestamp(minValue.getTime());
			stmtSql=new StringBuffer().append("{0}").append(Expression.MOREEQUAL)
					.append(SQLAssistant.getStmtSqlValue(minDate));
			pstmtsql=new StringBuffer().append("{0}").append(Expression.MOREEQUAL).append("?");
			paramValues=new Object[]{minDate};
		}else if(maxValue!=null){
			java.sql.Timestamp maxDate=new java.sql.Timestamp(maxValue.getTime());
			stmtSql=new StringBuffer().append("{0}").append(Expression.LESSEQAL)
					.append(SQLAssistant.getStmtSqlValue(maxDate));
			pstmtsql=new StringBuffer().append("{0}").append(Expression.LESSEQAL).append("?");
			paramValues=new Object[]{maxDate};
		}else{
			stmtSql=new StringBuffer("1=1");
			pstmtsql=stmtSql;
			paramValues=new Object[]{};
		}
	}

	public SmartBetween(Field field,Number minValue,Number maxValue){
		this();
		this._field=field;
		if(minValue!=null&&maxValue!=null){
			init(field,minValue,maxValue,false);
		}else if(minValue!=null){
			stmtSql=new StringBuffer().append("{0}").append(Expression.MOREEQUAL)
					.append(SQLAssistant.getStmtSqlValue(minValue));
			pstmtsql=new StringBuffer().append("{0}").append(Expression.MOREEQUAL).append("?");
			paramValues=new Object[]{minValue};
		}else if(maxValue!=null){
			stmtSql=new StringBuffer().append("{0}").append(Expression.LESSEQAL)
					.append(SQLAssistant.getStmtSqlValue(maxValue));
			pstmtsql=new StringBuffer().append("{0}").append(Expression.LESSEQAL).append("?");
			paramValues=new Object[]{maxValue};
		}else{
			stmtSql=new StringBuffer("1=1");
			paramValues=new Object[]{};
			pstmtsql=stmtSql;
		}
	}
}
