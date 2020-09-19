package com.esdk.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Expressions implements IExpression{
	protected boolean isOr=true;
	
	protected List list=new ArrayList<IExpression>();
	
	public Expressions(IExpression... expr){
		this(false,expr);
	}
	public Expressions(boolean isOrCondition,IExpression... expr){
		this.isOr=isOrCondition;
		list.addAll(Arrays.asList(expr));
	}
	
	public Expressions add(IExpression... expr){
		list.addAll(list.size(),Arrays.asList(expr));
		return this;
	}
	
	public Expressions add(String key,String expression,Object value){
		list.add(new Expression(null,key,expression,value));
		return this;
	}
	
	public Iterator<IExpression> iterator(){
		return list.iterator();
	}
	@Override public boolean compute(){
		boolean result=true;
		for(Iterator<IExpression> iter=list.iterator();iter.hasNext();){
			IExpression expr=iter.next();
			if(isOr()){
				result=expr.compute();
				if(result)
					break;
			}else{
				result=result&&expr.compute();
				if(!result)
					break;
			}
		}
		return result;
	}

	public Expressions setOr(boolean value){
		this.isOr=value;
		return this;
	}

	public boolean isOr(){
		return isOr;
	}
	
	public static Expressions or(IExpression... expr){
		return new Expressions(true,expr);
	}
	
	public static Expressions and(IExpression... expr){
		return new Expressions(false,expr);
	}
	
	@Override public String toString(){
		StringBuilder result=new StringBuilder("(");
		String operator=isOr()?" or ":" and ";
		for(Iterator<IExpression> iter=list.iterator();iter.hasNext();){
			IExpression expr=iter.next();
			if(isOr)
				result.append("(").append(expr.toString()).append(")").append(iter.hasNext()?operator:"");
			else
				result.append(expr.toString()).append(iter.hasNext()?operator:"");
		}
		result.append(")");
		return result.toString();
	}
}
