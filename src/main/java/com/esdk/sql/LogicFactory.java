package com.esdk.sql;

import java.util.Date;

public class LogicFactory{
	public static Where eq(Field field,Object value){
		return where(field,value);
	}
	public static Where notEq(Field field,Object value){
		return where(field,Expression.NOTEQUAL,value);
	}
	public static Where where(Field field,Object value){
		return WhereFactory.create(field,value);
	}
	public static Where where(Field field,String expression,Object value){
		return WhereFactory.create(field,expression,value);
	}
	
	public static Between between(Field field,Date min,Date max){
		return new Between(field,min,max);
	}
	public static Between between(Field field,Date min,Date max,boolean isNotBetween){
		return new Between(field,min,max,isNotBetween);
	}
	public static Between between(Field field,Number min,Number max){
		return new Between(field,min,max);
	}
	public static Between between(Field field,Number min,Number max,boolean isNotBetween){
		return new Between(field,min,max,isNotBetween);
	}
	
	public static Between smartBetween(Field field,Date min,Date max){
		return new SmartBetween(field,min,max);
	}
	public static SmartBetween smartBetween(Field field,Number min,Number max){
		return new SmartBetween(field,min,max);
	}
	
	public static InCondition in(Field field,ISelect select){
		return new InCondition(field,select);
	}
	public static Where in(Field field,Number... values){
		return new Where(field,values);
	}
	public static Where in(Field field,String... values){
		return new Where(field,values);
	}
	public static InCondition in(Field field,boolean isNotIn,ISelect select){
		return new InCondition(field,isNotIn,select);
	}
	
	public static NullCondition nullCondition(Field field){
		return new NullCondition(field);
	}
	public static NullCondition notNullCondition(Field field){
		return new NullCondition(field,false);
	}
	public static FieldCondition fieldCondition(Field field1,Field field2){
		return new FieldCondition(field1,field2);
	}
	public static Condition condition(String condition){
		return new Condition(condition);
	}
	public static LogicTree logicTree(ILogic... value){
		return new LogicTree(value);
	}
	
	public static LogicTree or(ILogic... conditions){
		for(ILogic condition:conditions) {
			condition.setOr();
		}
		return new LogicTree(conditions);
	}
	
	/**多个字段的or条件模糊查询，简化了or条件表达方式*/
	public static LogicTree likeOr(String keyword,Field...fields){
		ILogic[] wheres=new ILogic[fields.length];
		for(int i=0;i<fields.length;i++) {
			Where where=new Where(fields[i],Where.LIKE,keyword);
			where.setOr();
			wheres[i]=where;
		}
		return new LogicTree(wheres);
	}	
	
}
