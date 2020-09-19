package com.esdk.sql;

import com.esdk.sql.orm.IRow;

public class RowExpression extends Expression{
	private IRow _row;
	private String _fieldName;
	public RowExpression(IRow row,String fieldName,Object rightValue){
		super(fieldName,rightValue);
		this._fieldName=fieldName;
		this._row=row;
	}

	public RowExpression(IRow row,String fieldName,String expression,Object rightValue){
		super(fieldName,expression,rightValue);
		this._fieldName=fieldName;
		this._row=row;
	}
	
	public RowExpression(IRow row,String fieldName,Object min,Object max){
		super(fieldName,min,max);
		this._fieldName=fieldName;
		this._row=row;
	}

	public RowExpression(IRow row,String fieldName,String expression,Object min,Object max){
		super(fieldName,expression,min,max);
		this._fieldName=fieldName;
		this._row=row;
	}

	@Override public boolean compute(){
		setLeftValue(_row.get(_fieldName));
		return super.compute();
	}
	
	@Override public String toString(){
		setLeftValue(_fieldName);
		String result=super.toString();
		if(_row!=null)
			setLeftValue(_row.get(_fieldName));
		return result;
	}

	public static RowExpression create(IRow row,String fieldName,String expression,Object rightValue){
		return new RowExpression(row,fieldName,expression,rightValue);
	}
	
	public static RowExpression create(IRow row,String fieldName,String expression,Object min,Object max){
		return new RowExpression(row,fieldName,expression,min,max);
	}
	
	public static RowExpression create(IRow row,String fieldName,Object rightValue){
		return new RowExpression(row,fieldName,rightValue);
	}

	public void setRow(IRow row){
		this._row=row;
	}
	
}
