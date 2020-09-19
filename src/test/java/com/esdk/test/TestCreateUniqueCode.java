package com.esdk.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.esdk.esdk;
import com.esdk.sql.BatchInsert;
import com.esdk.sql.JDBCTemplate;
import com.esdk.sql.datasource.FileConnectionPool;
import com.esdk.sql.orm.RowUtils;
import com.esdk.test.orm.TestUniqueCodeMetaData;
import com.esdk.test.orm.TestUniqueCodeRow;
import com.esdk.test.orm.TestUniqueCodeSelect;
import com.esdk.utils.TimeMeter;

public class TestCreateUniqueCode{
	public static void main(String[] args) throws SQLException{
		test1();
	}

	private static void test1() throws SQLException{
		Connection conn=FileConnectionPool.getConnection();
		TestUniqueCodeMetaData md=TestUniqueCodeRow.md;
		new JDBCTemplate(conn,"truncate "+md.TABLENAME).perform();
		TimeMeter tm=new TimeMeter();
		int max=10000*1000;
		BatchInsert bi=new BatchInsert(md.TABLENAME,conn);
		bi.setFields(md.Uid,md.ProductId,md.MachineId);
		for(int i=0;i<max;i++) {
			TestUniqueCodeRow tucr=new TestUniqueCodeRow();
			tucr.setUid(RowUtils.genNextPrimaryId());
			tucr.setMachineId(RowUtils.genNextPrimaryId());
			tucr.setProductId(RowUtils.genNextPrimaryId());
			bi.addValue(tucr);
			if(i%1000==999 || i==max-1) {
				boolean succ=bi.perform();
				if(succ)
					bi.clearValues();
			}
		}
		int count=new TestUniqueCodeSelect(conn).count();
		esdk.str.println("总记录数：{}",count);
		tm.printElapse();
	}
}
