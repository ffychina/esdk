package com.esdk.sql;

public class Literal{
	private String _s;
	public Literal(String s){
		_s=s;
	}
	@Override public String toString(){
		return _s;
	}
}
