package com.esdk.sql;

import java.io.Serializable;
import java.util.Map;

import com.esdk.utils.EasyArray;

public class Mssql2000Dialect extends SqlDialect{

	public Mssql2000Dialect(){
		super();
	}
  @Override public StringBuilder getSQL(Columns columns,Froms froms,Wheres wheres,String distinct,int offset
			,int limit,OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
  	StringBuilder stmtSql;
  	if(limit>0&&offset>0){
  		stmtSql=join("SELECT TOP",limit,distinct,columns.assemble(),froms.assemble(),wheres.assemble(),"AND",
					otherArgs.get("primaryKeyFieldName"),"NOT IN(SELECT",distinct,"TOP",(offset),
					otherArgs.get("primaryKeyFieldName"),froms.assemble(),wheres.assemble(),orderbys.assemble(),
					groupbys.assemble(),")",orderbys.assemble(),groupbys.assemble());
		}else{
			stmtSql=join("SELECT",distinct,(SQLAssistant.isMsSQL()&&limit>0?"TOP "+limit:""),columns.assemble(),
					froms.assemble(),wheres.assemble(),orderbys.assemble(),groupbys.assemble());
		}
		return stmtSql;
	}
  
	@Override public StringBuilder parse(Columns columns,Froms froms,Wheres wheres,String distinct,int offset
				,int limit,OrderBys orderbys,GroupBys groupbys,Map<String,Serializable> otherArgs){
		if(limit>0&&offset>0){
			String whereClause=wheres.getPstmtSql();
			pstmtSql=join("SELECT TOP",limit,distinct,columns.assemble(),froms.assemble(),whereClause,whereClause.trim().length()>0?"AND":"WHERE",
					otherArgs.get("primaryKeyFieldName"),"NOT IN(SELECT",distinct,"TOP",(offset),
					otherArgs.get("primaryKeyFieldName"),froms.assemble(),wheres.getPstmtSql(),orderbys.assemble(),
					groupbys.assemble(),")",orderbys.assemble(),groupbys.assemble());
			parameters=EasyArray.concat(wheres.getParameters(),wheres.getParameters());
		}else{
			pstmtSql=join("SELECT",distinct,(SQLAssistant.isMsSQL()&&limit>0?"TOP "+limit:""),columns.assemble(),
					froms.assemble(),wheres.getPstmtSql(),orderbys.assemble(),groupbys.assemble());
			parameters=wheres.getParameters();
		}
		return pstmtSql;
	}
}
