package com.esdk.sql;

import java.util.Collection;
import java.util.Date;

import com.esdk.esdk;
import com.esdk.utils.EasyStr;
import com.esdk.utils.EasyTime;
import com.esdk.utils.ParseException;
import com.esdk.utils.EasyObj;

public class Expression implements IExpression{
	//EQIC表示忽略大小写的等于（equalIgnoreCase）
  public static final String EQ="=",LESS="<",LESSEQAL="<=",MORE=">",MOREEQUAL=">=",EQUAL="=",NOTEQUAL="<>",NOTEQ="!=",
  		LIKE="LIKE",NOTLIKE="NOT LIKE",IN="IN",NOTIN="NOTIN",BW="BETWEEN",NOTBW="NOTBWTEEN",MATCHES="matches",ISBLANK="isblank",
  ISVALID="isvalid",INCLUDE="*=",STARTWITH="^=",ENDWITH="$=",EQIC="~=";
  public static final String[] All=new String[] {EQ,LESS,LESSEQAL,MORE,MOREEQUAL,EQUAL,NOTEQUAL,NOTEQ,LIKE,NOTLIKE,IN,NOTIN,BW,NOTBW,MATCHES,INCLUDE,STARTWITH,ENDWITH,EQIC}; //exclude isvalid and isbvlank
	public Object leftValue,rightValue,rightMinValue,rightMaxValue;
	public String expression;

	public Expression(Object leftValue,String expression,Object rightValue){
		setLeftValue(leftValue);
		setExpression(expression);
		setRightValue(rightValue);
	}

	public Expression(Object leftValue,Object min,Object max){
		setLeftValue(leftValue);
		setExpression(BW);
		setRightValue(min,max);
	}
	
	public Expression(Object leftValue,String expression,Object min,Object max){
		setLeftValue(leftValue);
		setExpression(expression);
		setRightValue(min,max);
	}
	
	public Expression(Object leftValue,Object rightValue){
		setLeftValue(leftValue);
		if(rightValue==null) {
			setExpression(EQ);
		}
		else if(rightValue.getClass().isArray()) {
			setExpression(IN);
			setRightValue(rightValue);
		}
		else if(rightValue instanceof Collection) {
  		setExpression(IN);
  		setRightValue(((Collection)rightValue).toArray());
		}
		else {
  		setExpression(EQ);
  		setRightValue(rightValue);
		}
	}

	public Object getLeftValue(){
		return leftValue;
	}
	public void setLeftValue(Object leftValue){
		this.leftValue=leftValue;
	}
	public Object getRightValue(){
		return rightValue;
	}
	public void setRightValue(Object rightValue){
		this.rightValue=rightValue;
	}

	public void setRightValue(Object min,Object max){
		this.rightMinValue=min;
		this.rightMaxValue=max;
	}

	public String getExpression(){
		return expression;
	}
	public void setExpression(String expression){
		if(expression==null)
			expression=EQ;
		this.expression=expression;
	}
	
	public static Expression addEqualBlank(Object leftValue){
		return new Expression(leftValue,ISBLANK,null);
	}

	public static Expression addEqualValid(Object leftValue){
		return new Expression(leftValue,ISVALID,null);
	}

	public static Expression create(Object leftValue,String expression,Object rightValue){
		return new Expression(leftValue,expression,rightValue);
	}
	
	public static Expression create(Object leftValue,Object min,Object max){
		return new Expression(leftValue,min,max);
	}
	
	public static Expression create(Object leftValue,String expression,Object min,Object max){
		return new Expression(leftValue,expression,min,max);
	}

	public static Expression create(Object leftValue,Object rightValue){
		return new Expression(leftValue,rightValue);
	}
	
	@Override public String toString(){
		if(IN.equalsIgnoreCase(expression)||NOTIN.equalsIgnoreCase(expression))
			return EasyStr.valueOf(leftValue)+" "+expression+" "+SQLAssistant.getStmtSqlValue(rightValue);
		else if(BW.equalsIgnoreCase(expression)||NOTBW.equalsIgnoreCase(expression))
			return EasyStr.valueOf(leftValue)+" "+expression+" "+SQLAssistant.getStmtSqlValue(rightMinValue)+" and "+SQLAssistant.getStmtSqlValue(rightMaxValue);
		else
			return EasyStr.valueOf(leftValue)+" "+expression+" "+EasyStr.valueOf(rightValue);
	}
	
	public boolean equal() {
		if(leftValue instanceof Date &&rightValue instanceof Date)
			return ((Date)leftValue).compareTo((Date)rightValue)==0;
		else if(leftValue instanceof Number)
			return EasyObj.compareTo(leftValue,rightValue)==0;
		else  
			return EasyObj.equal(leftValue,rightValue);
	}
	
	public boolean compute() {
		if(EQ.equalsIgnoreCase(expression))
			return equal();
		else if(EQIC.equalsIgnoreCase(expression))
			return esdk.str.equals((String)leftValue,(String)rightValue,true);
		else if(NOTEQUAL.equalsIgnoreCase(expression)||NOTEQ.equalsIgnoreCase(expression))
			return !equal();
		else if(ISVALID.equalsIgnoreCase(expression))
			return EasyObj.isValid(leftValue);
		else if(ISBLANK.equalsIgnoreCase(expression))
			return EasyObj.isBlank(leftValue);
		/*if(!IN.equalsIgnoreCase(expression)&&!NOTIN.equalsIgnoreCase(expression)&&
				!Tools.and(leftValue,Tools.or(rightValue,Tools.and(rightMinValue,rightMaxValue))))
			return false;*/
		else if(LESS.equalsIgnoreCase(expression))
			return EasyObj.compareTo(leftValue,rightValue)<0;
		else if(LESSEQAL.equalsIgnoreCase(expression))
			return EasyObj.compareTo(leftValue,rightValue)<=0;
		else if(MORE.equalsIgnoreCase(expression))
			return EasyObj.compareTo(leftValue,rightValue)>0;
		else if(MOREEQUAL.equalsIgnoreCase(expression))
			return EasyObj.compareTo(leftValue,rightValue)>=0;
		else if(IN.equalsIgnoreCase(expression)) {
			if(rightValue instanceof int[])
				return esdk.array.contains((int[])rightValue,(int)leftValue);
			else
				return esdk.array.contains((Object[])rightValue,leftValue);
		}
		else if(NOTIN.equalsIgnoreCase(expression))
			return !esdk.array.contains((Object[])rightValue,leftValue);
		else if(BW.equalsIgnoreCase(expression))
			return EasyObj.compareTo(leftValue,rightMinValue)>=0&&EasyObj.compareTo(leftValue,rightMaxValue)<=0;
		else if(NOTBW.equalsIgnoreCase(expression))
			return EasyObj.compareTo(leftValue,rightMinValue)<0||EasyObj.compareTo(leftValue,rightMaxValue)>0;
			else if(LIKE.equalsIgnoreCase(expression)) {
				return esdk.str.getStringNoNull(leftValue).matches("^"+rightValue.toString().replaceAll("%",".*?")+"$");
			}
			else if(NOTLIKE.equalsIgnoreCase(expression)) {
				return !esdk.str.getStringNoNull(leftValue).matches("^"+rightValue.toString().replaceAll("%",".*?")+"$");
			}
		else if(MATCHES.equalsIgnoreCase(expression)) {
			return EasyStr.getStringNoNull(leftValue).matches("^"+EasyStr.getStringNoNull(rightValue)+"$");
		}
		else if(STARTWITH.equalsIgnoreCase(expression)) {
			return EasyStr.valueOf(leftValue).startsWith(EasyStr.valueOf(rightValue));
		}
		else if(ENDWITH.equalsIgnoreCase(expression)) {
			return EasyStr.valueOf(leftValue).endsWith(EasyStr.valueOf(rightValue));
		}
		else if(INCLUDE.equalsIgnoreCase(expression)) {
			return EasyStr.indexOf(EasyStr.valueOf(leftValue),EasyStr.valueOf(rightValue),true)>=0;
		}
		else
			throw new RuntimeException("unknown expression:"+expression);
	}

	public static void main(String[] args) throws ParseException{
	///	Tools.assertEquals(Expression.create("abc","abc").compute());
		esdk.tool.assertEquals(Expression.create("abcd",Expression.LIKE,"%a%").compute());
		esdk.tool.assertEquals(Expression.create(1,Expression.LESSEQAL,3).compute());
		esdk.tool.assertEquals(Expression.create(1,Expression.IN,new Object[] {1,3}).compute());
		esdk.tool.assertEquals(Expression.create(1,Expression.NOTIN,new Object[] {2,3}).compute());
		esdk.tool.assertEquals(Expression.create(3,Expression.BW,1,3).compute());
		esdk.tool.assertEquals(Expression.create(3,Expression.NOTBW,1,2).compute());
		esdk.tool.assertEquals(Expression.create(EasyTime.valueOf("20091201"),Expression.MOREEQUAL,EasyTime.valueOf("20091101")).compute());
		esdk.tool.assertEquals(Expression.create("",null).compute()==false);
		esdk.tool.assertEquals(Expression.create(null,null).compute());
	}

}
