package com.esdk.sql;

import com.esdk.esdk;

public class WhereFactory {

	public static Where create(Field field,String expression,Object value){
		Where where = null;
		if(SQLAssistant.isPostgreSQL()){
			where = new PostgresWhere( field,expression, value);
		}else {
			where = new Where( field,expression, value);
		}
		return where;
	}
	public static Where create(Field field,Object value) {
		return create(field,Where.EQ,value);
	}
	
	private static void test() {
		esdk.tool.assertEquals(WhereFactory.create(new Field("name"),"aB0").toString(),"name = 'aB0'");
		SQLAssistant.setDatabaseProductName(SqlDialect.DatabaseProductName_PostgreSQL);
		esdk.tool.assertEquals(WhereFactory.create(new Field("name"),"aB0").toString(),"name ILIKE 'aB0'");	
	}
	public static void main(String[] args){
		test();
	}
}
