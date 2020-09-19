package com.esdk.sql.orm;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.esdk.sql.Expression;
import com.esdk.sql.IExpression;
import com.esdk.sql.RowExpression;
import com.esdk.sql.RowExpressions;
import com.esdk.utils.EasyMath;

public class RowFilter implements IRowFilter,ITopable{
	private RowExpressions expressions=new RowExpressions();
	private int top;
	public RowFilter() {
	}
	public RowFilter(IExpression expr){
		this();
		add(expr);
	}
	public RowFilter(String key,Object value){
		this();
		add(key,value);
	}
	
	public RowFilter(String key,String express,Object value){
		this();
		expressions.add(RowExpression.create(null,key,express,value));
	}
	
	public RowFilter(String key,Object min,Object max){
		this();
		expressions.add(RowExpression.create(null,key,Expression.BW,min,max));
	}
	
	public RowFilter(Map<String,Object> map){
		for(Iterator iter=map.entrySet().iterator();iter.hasNext();) {
			Entry entry=(Entry)iter.next();
			expressions.add(new RowExpression(null,(String)entry.getKey(),entry.getValue()));
		}
	}
	
	public RowFilter add(String key,Object value) {
		expressions.add(new RowExpression(null,key,value));
		return this;
	}	
	
	public RowFilter add(IExpression expr) {
		expressions.add(expr);
		return this;
	}
	
	public RowFilter add(String key,String expression,Object value) {
		expressions.add(key,expression,value);
		return this;
	}	
	
	public RowFilter add(String key,Object min,Object max) {
		expressions.add(key,min,max);
		return this;
	}
	
	public RowFilter setTop(int top) {
		this.top=EasyMath.max(top,0);
		return this;
	}
	
	public int getTop() {
		return this.top;
	}
	
	@Override public boolean filter(IRow row,IRowSet rowset,int index){
		expressions.setRow(row);
		return expressions.compute();
	}
	
	public static RowFilter create(String key,Object value) {
		return new RowFilter(key,value);
	}
	
	public static RowFilter create(String key,String expression,Object value) {
		return new RowFilter(key,expression,value);
	}
	
	public static RowFilter create(String key,Object min,Object max) {
		return new RowFilter(key,min,max);
	}
	
	public String toString() {
		return expressions.toString();
	}
}
