package com.esdk.sql;
import java.util.Iterator;

import com.esdk.sql.orm.IRow;

public class RowExpressions extends Expressions{
	public RowExpressions(IExpression...expr){
		super(false,expr);
	}

	public RowExpressions(boolean isOrCondition,IExpression...expr){
		super(isOrCondition,expr);
	}

	public void setRow(IRow row){
		setRow(row,this);
	}

	private void setRow(IRow row,Expressions expressions){
		for(Iterator<IExpression> iter=expressions.iterator();iter.hasNext();){
			IExpression iexpr=iter.next();
			if(iexpr instanceof RowExpression)
				((RowExpression)iexpr).setRow(row);
			else if(iexpr instanceof RowExpressions)
				((RowExpressions)iexpr).setRow(row);
			else if(iexpr instanceof Expressions)
				setRow(row,((Expressions)iexpr));
		}
	}
	
	public RowExpressions add(String key,String expression,Object value){
		list.add(new RowExpression(null,key,expression,value));
		return this;
	}

	public RowExpressions add(String key,Object min,Object max){
		list.add(new RowExpression(null,key,min,max));
		return this;
	}

	public static RowExpressions or(IExpression... expr){
		return new RowExpressions(true,expr);
	}
	
	public static RowExpressions and(IExpression... expr){
		return new RowExpressions(false,expr);
	}
	
}
