package com.esdk.sql;
import java.sql.Timestamp;
import java.util.Date;

import com.esdk.utils.CharAppender;
public class Where implements ILogic{
  protected boolean isAnd=true;
  public static final String AND="AND", OR="OR";
  public static final int CHAR=0,NUMERIC=1,BOOL=2,TIME14=3,OTHER=4,TIMESTEMP=5,DateTime=6;
  public static final String EQ=Expression.EQ,EQUAL=Expression.EQUAL,NOTEQUAL=Expression.NOTEQUAL,LESS=Expression.LESS,
  LESSEQAL=Expression.LESSEQAL,MORE=Expression.MORE,MOREEQUAL=Expression.MOREEQUAL,
  LIKE="LIKE",NOTLIKE="NOT LIKE",NULL="NULL",ISNOT="IS NOT",IN="IN",NOTIN="NOT IN",IS="IS";
  String functionName;
  Field field;
  Object rightValue;
  String expression;
  int dataType=CHAR;
  String pstmtSQL;
  Object[] paramValue;

  public Where() {}
  
  public Where(Field field0,String expression0,Object value){
    field=field0;
    if(EQ.equals(expression0)&&value==null)
    	setExpression(IS);
    else if(NOTEQUAL.equals(expression0)&&value==null)
    	setExpression(ISNOT);
    else
    	setExpression(expression0);
    if(value instanceof String)
      dataType=CHAR;
    else if(value instanceof Boolean)
      dataType=BOOL;
    else if(value instanceof Number)
      dataType=NUMERIC;
    else if(value instanceof java.util.Date)
      dataType=DateTime;
    setRightValue(value);
  }

  public Where(String fieldname,Object value){
    this(new Field(fieldname),value);
  }

  public Where(Field field0,Object value){
    field=field0;
    setDataType(value);
    setEqualCondition(value);
  }

  public Field getField() {
  	return field;
  }
  
  public Object getValue() {
  	return rightValue;
  }
  
  private void setDataType(Object value) {
    if(value==null)
      dataType=OTHER;
    else if(value instanceof String)
      dataType=CHAR;
    else if(value instanceof Boolean)
      dataType=BOOL;
    else if(value instanceof Number)
      dataType=NUMERIC;
    else if(value instanceof Timestamp)
      dataType=TIMESTEMP;
    else if(value instanceof java.sql.Date||value instanceof java.util.Date)
      dataType=DateTime;
  }
  
  public Where(Field field0,int datetype,Object[] value){
    field=field0;
    dataType=datetype;
    setInCondition(value);
  }

  public Where(Field field0,int datetype,Object value){
    field=field0;
    dataType=datetype;
    setEqualCondition(value);
  }

  public Where(String fieldName,String expression0,Object value){
    this(new Field(fieldName),expression0,value);
  }
  
  public Where(Field field0,int datetype,String expression0,Object value){
    field=field0;
    dataType=datetype;
    setExpression(expression0);
    setRightValue(value);
  }
  
  public Where(Field field0,Object[] value) {
    field=field0;
    setInCondition(value);
  }

  public void setEqualCondition(Object value){
    setExpression(EQ);
    setRightValue(value);
  }

  public void setInCondition(Object[] value){
    setExpression(IN);
    setRightValue(value);
  }

  @Override public String toString(){
    return getStmtSql();
  }

	public void setRightValue(Object[] value){
		if(value!=null&&value.length==0)
			value=new Object[]{null};
		rightValue=value;
		CharAppender ca=new CharAppender(',');
		for(int i=0;i<value.length;i++){
			ca.append("?");
		}
		paramValue=value;
		pstmtSQL="(".concat(ca.toString()).concat(")");
	}

  public void setRightValue(Object value){
  	if(value==null)
  		rightValue=value;
  	else if(value.getClass().isArray())
      setRightValue((Object[])value);
    else{
      rightValue=value;
      if(rightValue.getClass().equals(Date.class))
      	rightValue=new java.sql.Timestamp(((Date)rightValue).getTime());
      else if(dataType==BOOL&&SQLAssistant.isOracle())
      	rightValue=((Boolean)rightValue).booleanValue()?1:0;
      paramValue=new Object[]{rightValue};
      pstmtSQL="?";
    }
  }

  public void setExpression(String value){
    if(value!=null&&(value.indexOf('=')>=0||value.indexOf('>')>=0||value.indexOf('<')>=0))
    	this.expression=value;
    else
    	this.expression=" "+value+" ";
  }

  @Override public boolean isAnd(){
    return isAnd;
  }

  public void setAnd(boolean isAndLogical){
    this.isAnd=isAndLogical;
  }

  public Where setAnd(){
    this.isAnd=true;
    return this;
  }

  public Where setOr(){
    this.isAnd=false;
    return this;
  }

  public void setDataType(int datatype){
    this.dataType=datatype;
  }

  public String getFullName(){
    StringBuffer result=new StringBuffer();
    if(functionName!=null)
      result.append(functionName).append("(").append(field).append(")");
    else
      result.append(field);
    return result.toString();
  }

  public void setFunctionName(String functionname){
    this.functionName=functionname;
  }

  public void setField(Field field0){
    this.field=field0;
  }

  @Override public Object[] getParameters(){
    return paramValue;
  }

  @Override public String getPstmtSql(){
    return new StringBuffer().append(getFullName()).append(expression).append(pstmtSQL).toString();
  }

  @Override public String getStmtSql(){
    StringBuffer result=new StringBuffer();
    result.append(getFullName()).append(expression).append(SQLAssistant.getStmtSqlValue(rightValue));
    return result.toString();
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
