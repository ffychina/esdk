package com.esdk.test.orm;
import java.sql.ResultSet;

import java.sql.SQLException;

import com.esdk.sql.orm.ParentResultSet;

import java.util.Date;

public class TestDictResultSet extends ParentResultSet<TestDictRow>{
  public static final TestDictMetaData metaData=TestDictSelect.metaData;
  public static final TestDictMetaData md=metaData;
  public TestDictResultSet(ResultSet value){
    super(value);
  }

  public TestDictRow createRowInstance(){
    return new TestDictRow();
  }

  public TestDictRow getCurrentRow()throws SQLException{
    return (TestDictRow)super.gainCurrentRow(rs);
  }

  public TestDictRow[] getAllRow()throws SQLException{
    return (TestDictRow[])gainAllRow().toArray(new TestDictRow[0]);
  }

  public Long getDictId()throws SQLException{
    return rs.getLong(TestDictMetaData.DictId);
  }

  public String getCategory()throws SQLException{
    return rs.getString(TestDictMetaData.Category);
  }

  public String getName()throws SQLException{
    return rs.getString(TestDictMetaData.Name);
  }

  public String getContent()throws SQLException{
    return rs.getString(TestDictMetaData.Content);
  }

  public Integer getSequence()throws SQLException{
    return rs.getInt(TestDictMetaData.Sequence);
  }

  public String getMemo()throws SQLException{
    return rs.getString(TestDictMetaData.Memo);
  }

  public Long getCenterId()throws SQLException{
    return rs.getLong(TestDictMetaData.CenterId);
  }

  public Boolean getValid()throws SQLException{
    return rs.getBoolean(TestDictMetaData.Valid);
  }

  public Date getCreateTime()throws SQLException{
    return rs.getDate(TestDictMetaData.CreateTime);
  }

  public Long getCreateUserId()throws SQLException{
    return rs.getLong(TestDictMetaData.CreateUserId);
  }

  public String getCreateUserName()throws SQLException{
    return rs.getString(TestDictMetaData.CreateUserName);
  }

  public Date getModifyTime()throws SQLException{
    return rs.getDate(TestDictMetaData.ModifyTime);
  }

  public Long getModifyUserId()throws SQLException{
    return rs.getLong(TestDictMetaData.ModifyUserId);
  }

  public String getModifyUserName()throws SQLException{
    return rs.getString(TestDictMetaData.ModifyUserName);
  }

  public Date getDeleteTime()throws SQLException{
    return rs.getDate(TestDictMetaData.DeleteTime);
  }

  public Long getDeleteUserId()throws SQLException{
    return rs.getLong(TestDictMetaData.DeleteUserId);
  }

  public String getDeleteUserName()throws SQLException{
    return rs.getString(TestDictMetaData.DeleteUserName);
  }

  public Integer getVersion()throws SQLException{
    return rs.getInt(TestDictMetaData.Version);
  }

}
