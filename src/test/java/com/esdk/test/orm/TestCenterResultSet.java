package com.esdk.test.orm;
import java.sql.ResultSet;

import java.sql.SQLException;

import com.esdk.sql.orm.ParentResultSet;

import java.util.Date;

public class TestCenterResultSet extends ParentResultSet<TestCenterRow>{
  public static final TestCenterMetaData metaData=TestCenterSelect.metaData;
  public static final TestCenterMetaData md=metaData;
  public TestCenterResultSet(ResultSet value){
    super(value);
  }

  public TestCenterRow createRowInstance(){
    return new TestCenterRow();
  }

  public TestCenterRow getCurrentRow()throws SQLException{
    return (TestCenterRow)super.gainCurrentRow(rs);
  }

  public TestCenterRow[] getAllRow()throws SQLException{
    return (TestCenterRow[])gainAllRow().toArray(new TestCenterRow[0]);
  }

  public Integer getCenterId()throws SQLException{
    return rs.getInt(TestCenterMetaData.CenterId);
  }

  public String getCode()throws SQLException{
    return rs.getString(TestCenterMetaData.Code);
  }

  public String getName()throws SQLException{
    return rs.getString(TestCenterMetaData.Name);
  }

  public String getEnglishName()throws SQLException{
    return rs.getString(TestCenterMetaData.EnglishName);
  }

  public String getAbbr()throws SQLException{
    return rs.getString(TestCenterMetaData.Abbr);
  }

  public String getTel()throws SQLException{
    return rs.getString(TestCenterMetaData.Tel);
  }

  public String getFax()throws SQLException{
    return rs.getString(TestCenterMetaData.Fax);
  }

  public String getAddress()throws SQLException{
    return rs.getString(TestCenterMetaData.Address);
  }

  public String getRegion()throws SQLException{
    return rs.getString(TestCenterMetaData.Region);
  }

  public String getEmail()throws SQLException{
    return rs.getString(TestCenterMetaData.Email);
  }

  public String getContact()throws SQLException{
    return rs.getString(TestCenterMetaData.Contact);
  }

  public String getType()throws SQLException{
    return rs.getString(TestCenterMetaData.Type);
  }

  public String getRemark()throws SQLException{
    return rs.getString(TestCenterMetaData.Remark);
  }

  public String getLogoUrl()throws SQLException{
    return rs.getString(TestCenterMetaData.LogoUrl);
  }

  public String getAppLogoUrl()throws SQLException{
    return rs.getString(TestCenterMetaData.AppLogoUrl);
  }

  public Boolean getValid()throws SQLException{
    return rs.getBoolean(TestCenterMetaData.Valid);
  }

  public Date getCreateTime()throws SQLException{
    return rs.getDate(TestCenterMetaData.CreateTime);
  }

  public Integer getCreateUserId()throws SQLException{
    return rs.getInt(TestCenterMetaData.CreateUserId);
  }

  public String getCreateUserName()throws SQLException{
    return rs.getString(TestCenterMetaData.CreateUserName);
  }

  public Date getModifyTime()throws SQLException{
    return rs.getDate(TestCenterMetaData.ModifyTime);
  }

  public Integer getModifyUserId()throws SQLException{
    return rs.getInt(TestCenterMetaData.ModifyUserId);
  }

  public String getModifyUserName()throws SQLException{
    return rs.getString(TestCenterMetaData.ModifyUserName);
  }

  public Date getDeleteTime()throws SQLException{
    return rs.getDate(TestCenterMetaData.DeleteTime);
  }

  public Integer getDeleteUserId()throws SQLException{
    return rs.getInt(TestCenterMetaData.DeleteUserId);
  }

  public String getDeleteUserName()throws SQLException{
    return rs.getString(TestCenterMetaData.DeleteUserName);
  }

  public Integer getVersion()throws SQLException{
    return rs.getInt(TestCenterMetaData.Version);
  }

}
