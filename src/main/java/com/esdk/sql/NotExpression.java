package com.esdk.sql;

public class NotExpression extends Expressions{
	public NotExpression(IExpression... exprs){
		super(exprs);
	}
	@Override public boolean compute(){
		return !super.compute();
	}
	
	@Override public String toString(){
		return "!("+super.toString()+")";
	}
}
