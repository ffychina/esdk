package com.esdk.sql;

import com.esdk.utils.EasyStr;
import com.esdk.esdk;
import com.esdk.utils.EasyObj;

public class PostgresWhere extends Where{

	public static final String ILIKE="ILIKE";
	
	public PostgresWhere(Field field0,String expression,Object value){
		super(field0,expression,value);
	}	
	
	public PostgresWhere(Field field0,Object value){
		super(field0,Where.EQ,value);
	}

	@Override public void setExpression(String value){
    if(rightValue!=null)
      System.out.println("notice:RightValue is "+rightValue);
    if(dataType == CHAR&&containsExpression(value)&&containsEnglish((String)rightValue)){
    	 value = this.ILIKE;
    }
    super.setExpression(value);
  }
	@Override public void setRightValue(Object value){
		if(value!=null&&value instanceof CharSequence&&containsEnglish(value.toString())
				&&containsExpression(EasyStr.trim(this.expression)))
			setExpression(this.ILIKE);
		super.setRightValue(value);
	}
	
	private boolean containsExpression(String value) {
		return value!=null&&(value.equals(Where.EQ)||value.equals(Where.LIKE));
	}
	private boolean containsEnglish(String value) {
		for(int i=0;value!=null&&i<value.length();i++) {
			char c=value.charAt(i);
			if((c>='A'&&c<='Z')||(c>='a'&&c<='z'))
				return true;
		}
		return false;
	}
	
	private static void test() {
		esdk.tool.assertEquals(new PostgresWhere(new Field("name"),"李大民").toString(),"name = '李大民'");
		esdk.tool.assertEquals(new PostgresWhere(new Field("name"),"傻B").toString(),"name ILIKE '傻B'");
		esdk.tool.assertEquals(new PostgresWhere(new Field("name"),null).toString(),"name is null");
	}
	public static void main(String[] args){
		test();
	}
}
