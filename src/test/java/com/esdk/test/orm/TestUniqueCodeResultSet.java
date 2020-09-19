package com.esdk.test.orm;
import java.sql.ResultSet;

import java.sql.SQLException;

import com.esdk.sql.orm.ParentResultSet;

import java.util.Date;

public class TestUniqueCodeResultSet extends ParentResultSet<TestUniqueCodeRow>{
  public static final TestUniqueCodeMetaData metaData=TestUniqueCodeSelect.metaData;
  public static final TestUniqueCodeMetaData md=metaData;
  public TestUniqueCodeResultSet(ResultSet value){
    super(value);
  }

  public TestUniqueCodeRow createRowInstance(){
    return new TestUniqueCodeRow();
  }

  public TestUniqueCodeRow getCurrentRow()throws SQLException{
    return (TestUniqueCodeRow)super.gainCurrentRow(rs);
  }

  public TestUniqueCodeRow[] getAllRow()throws SQLException{
    return (TestUniqueCodeRow[])gainAllRow().toArray(new TestUniqueCodeRow[0]);
  }

  public Long getUid()throws SQLException{
    return rs.getLong(TestUniqueCodeMetaData.Uid);
  }

  public Long getMachineId()throws SQLException{
    return rs.getLong(TestUniqueCodeMetaData.MachineId);
  }

  public Long getProductId()throws SQLException{
    return rs.getLong(TestUniqueCodeMetaData.ProductId);
  }

  public Boolean getValid()throws SQLException{
    return rs.getBoolean(TestUniqueCodeMetaData.Valid);
  }

  public Date getCreateTime()throws SQLException{
    return rs.getDate(TestUniqueCodeMetaData.CreateTime);
  }

}
