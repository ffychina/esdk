package com.esdk.sql;

import java.io.Serializable;
import java.util.Map;

import com.esdk.utils.CharAppender;

public class OracleDialect extends SqlDialect{

	public OracleDialect(){
		super();
	}
	
  @Override public StringBuilder getSQL(Columns columns,Froms froms,Wheres wheres,String distinct,int offset
			,int limit,OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
  	StringBuilder stmtSql;
		if(limit>0&&offset<=0){
			stmtSql=join("SELECT * FROM (SELECT",distinct,columns.assemble(),froms.assemble()
    			,wheres.assemble(),orderbys.assemble(),groupbys.assemble(),") Where ROWNUM<=",limit);
    }else if(limit>0&&offset>=0) {
    	stmtSql=join("SELECT",distinct,columns.assemble(),"FROM (SELECT ROWNUM ROW_NUM_,tmp.* FROM (SELECT "
    			,columns.assemble(),froms.assemble(),wheres.assemble(),orderbys.assemble(),groupbys.assemble()
    			,")tmp WHERE ROWNUM<=",limit+offset,") WHERE ROW_NUM_>",offset);
    }
    else {
    	return super.getSQL(columns,froms,wheres,distinct,offset,limit,orderbys,groupbys,otherArgs);
    }
		return stmtSql;
	}
  
	@Override public StringBuilder parse(Columns columns,Froms froms,Wheres wheres,String distinct,int offset
				,int limit,OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
		if(limit>0&&offset<=0){
    	pstmtSql=join("SELECT * FROM (SELECT",distinct,columns.assemble(),froms.assemble()
    			,wheres.getPstmtSql(),orderbys.assemble(),groupbys.assemble(),") Where ROWNUM<=",limit);
    }else if(limit>0&&offset>=0) {
    	CharAppender labels=new CharAppender(',');
    	for(Column item:columns.toArray()) {
    		labels.add(item.getLabel());
    	}
    	pstmtSql=join("SELECT",labels,"FROM (SELECT ROWNUM ROW_NUM_,tmp.* FROM (SELECT "
    			,distinct,columns.assemble(),froms.assemble(),wheres.getPstmtSql(),orderbys.assemble(),groupbys.assemble()
    			,")tmp WHERE ROWNUM<=",limit+offset,") WHERE ROW_NUM_>",offset);
    }
    else {
    	return super.parse(columns,froms,wheres,distinct,offset,limit,orderbys,groupbys,otherArgs);
    }
		parameters=wheres.getParameters();
		return pstmtSql;
	}
}
