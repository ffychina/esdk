package com.esdk.test.orm;
import java.sql.ResultSet;

import java.sql.SQLException;

import com.esdk.sql.orm.ParentResultSet;

import java.util.Date;

public class SampleResultSet extends ParentResultSet<SampleRow>{
  public static final SampleMetaData metaData=SampleSelect.metaData;
  public static final SampleMetaData md=metaData;
  public SampleResultSet(ResultSet value){
    super(value);
  }

  public SampleRow createRowInstance(){
    return new SampleRow();
  }

  public SampleRow getCurrentRow()throws SQLException{
    return (SampleRow)super.gainCurrentRow(rs);
  }

  public SampleRow[] getAllRow()throws SQLException{
    return (SampleRow[])gainAllRow().toArray(new SampleRow[0]);
  }

  public Long getDictId()throws SQLException{
    return rs.getLong(SampleMetaData.DictId);
  }

  public String getCategory()throws SQLException{
    return rs.getString(SampleMetaData.Category);
  }

  public String getName()throws SQLException{
    return rs.getString(SampleMetaData.Name);
  }

  public String getContent()throws SQLException{
    return rs.getString(SampleMetaData.Content);
  }

  public Integer getSequence()throws SQLException{
    return rs.getInt(SampleMetaData.Sequence);
  }

  public String getMemo()throws SQLException{
    return rs.getString(SampleMetaData.Memo);
  }

  public Long getCenterId()throws SQLException{
    return rs.getLong(SampleMetaData.CenterId);
  }

  public Boolean getValid()throws SQLException{
    return rs.getBoolean(SampleMetaData.Valid);
  }

  public Date getCreateTime()throws SQLException{
    return rs.getDate(SampleMetaData.CreateTime);
  }

  public Long getCreateUserId()throws SQLException{
    return rs.getLong(SampleMetaData.CreateUserId);
  }

  public String getCreateUserName()throws SQLException{
    return rs.getString(SampleMetaData.CreateUserName);
  }

  public Date getModifyTime()throws SQLException{
    return rs.getDate(SampleMetaData.ModifyTime);
  }

  public Long getModifyUserId()throws SQLException{
    return rs.getLong(SampleMetaData.ModifyUserId);
  }

  public String getModifyUserName()throws SQLException{
    return rs.getString(SampleMetaData.ModifyUserName);
  }

  public Date getDeleteTime()throws SQLException{
    return rs.getDate(SampleMetaData.DeleteTime);
  }

  public Long getDeleteUserId()throws SQLException{
    return rs.getLong(SampleMetaData.DeleteUserId);
  }

  public String getDeleteUserName()throws SQLException{
    return rs.getString(SampleMetaData.DeleteUserName);
  }

  public Integer getVersion()throws SQLException{
    return rs.getInt(SampleMetaData.Version);
  }

}
