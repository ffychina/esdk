package com.esdk.sql;
import java.util.Date;

import com.esdk.esdk;


public class Between implements ILogic{
  StringBuffer stmtSql;
  boolean isAnd=true,isNot=false;
  Object[] paramValues;
  StringBuffer pstmtsql;
  protected Field _field;
  protected Between(){
	}
  public Between(Field field,Number startValue,Number endValue){
    this(field,startValue,endValue,false);
  }

	public Between(Field field,Number minvalue,Number maxvalue,boolean isnot){
		init(field,minvalue,maxvalue,isnot);
	}

  public Between(Field field,Date startValue,Date endValue){
    this(field,startValue,endValue,false);
  }

  public Between(Field field,Date minvalue,Date maxvalue,boolean isnot){
  	init(field,minvalue,maxvalue,isnot);
  }

	protected void init(Field field,Number minvalue,Number maxvalue,boolean isnot){
		this._field=field;
		isNot=isnot;
		stmtSql=new StringBuffer().append("{0}").append(isNot?" not":"").append(" between ").append(SQLAssistant.getStmtSqlValue(minvalue))
				.append(" and ").append(SQLAssistant.getStmtSqlValue(maxvalue));
		pstmtsql=new StringBuffer().append("{0}").append(isNot?" not ":"").append(" between ").append("?").append(" and ").append("?");
		paramValues=new Object[]{minvalue,maxvalue};
	}
  
  protected void init(Field field,Date minvalue,Date maxvalue,boolean isnot){
  	java.sql.Timestamp minDate=new java.sql.Timestamp(minvalue.getTime());
  	java.sql.Timestamp maxDate=new java.sql.Timestamp(maxvalue.getTime());
  	this._field=field;
    isNot=isnot;
    stmtSql=new StringBuffer().append("{0}").append(isNot?" not":"").append(" between ")
    .append(SQLAssistant.getStmtSqlValue(minDate)).append(" and ").append(SQLAssistant.getStmtSqlValue(maxDate));

    pstmtsql=new StringBuffer().append("{0}").append(isNot?" not ":"").append(" between ")
    .append("?").append(" and ").append("?");
    paramValues=new Object[]{minDate,maxDate};
  }
  
  public Between setAnd(boolean isAndLogical){
    isAnd=isAndLogical;
    return this;
  }

  public Between setOr(){
    isAnd=false;
    return this;
  }

  public boolean isAnd(){
    return isAnd;
  }
  
  public String toString() {
    return stmtSql.toString(); 
  }

  public Object[] getParameters(){
    return paramValues;
  }

  public String getPstmtSql(){
    return esdk.str.format(pstmtsql.toString(),this._field);
  }

  public String getStmtSql(){
    return esdk.str.format(stmtSql.toString(),this._field); 
  }
  
  @Override public int hashCode() {
  	return toString().hashCode();
  }
  
  @Override public boolean equals(Object obj) {
  	if(obj==this)
  		return true;
  	if(obj==null)
  		return false;
  	if(this.getClass().equals(obj.getClass()))
  		return toString().equals(obj.toString());
  	return false;
  }
}