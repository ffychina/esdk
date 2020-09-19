package com.esdk.sql;

import java.io.Serializable;
import java.util.Map;

public class MysqlDalect extends SqlDialect{

	public MysqlDalect(){
		super();
	}
	
  @Override public StringBuilder getSQL(Columns columns,Froms froms,Wheres wheres,String distinct,int offset
			,int limit,OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
  	if(limit>0){
    	StringBuilder stmtSql=join("SELECT",distinct,columns.assemble(),froms.assemble(),wheres.assemble()
    			,groupbys.assemble(),orderbys.assemble(),limit>0?"LIMIT ".concat(String.valueOf(limit)):""
    			,offset>=0?"OFFSET ".concat(String.valueOf(offset)):"");
			return stmtSql;
		}else
			return super.getSQL(columns,froms,wheres,distinct,offset,limit,orderbys,groupbys,otherArgs);
	}

	@Override
	public StringBuilder parse(Columns columns,Froms froms,Wheres wheres,String distinct,int offset,int limit,
			OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
		if(limit>0){
			pstmtSql=join("SELECT",distinct,columns.assemble(),froms.assemble(),wheres.getPstmtSql()
					,groupbys.assemble(),orderbys.assemble(),limit>0?"LIMIT ".concat(String.valueOf(limit)):""
					,offset>0?"OFFSET ".concat(String.valueOf(offset)):"");
			parameters=wheres.getParameters();
			return pstmtSql;
		}else
			return super.parse(columns,froms,wheres,distinct,offset,limit,orderbys,groupbys,otherArgs);
	}
}
