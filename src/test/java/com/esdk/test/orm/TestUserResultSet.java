package com.esdk.test.orm;
import java.sql.ResultSet;

import java.sql.SQLException;

import com.esdk.sql.orm.ParentResultSet;

import java.util.Date;

public class TestUserResultSet extends ParentResultSet<TestUserRow>{
  public static final TestUserMetaData metaData=TestUserSelect.metaData;
  public static final TestUserMetaData md=metaData;
  public TestUserResultSet(ResultSet value){
    super(value);
  }

  public TestUserRow createRowInstance(){
    return new TestUserRow();
  }

  public TestUserRow getCurrentRow()throws SQLException{
    return (TestUserRow)super.gainCurrentRow(rs);
  }

  public TestUserRow[] getAllRow()throws SQLException{
    return (TestUserRow[])gainAllRow().toArray(new TestUserRow[0]);
  }

  public Long getUserId()throws SQLException{
    return rs.getLong(TestUserMetaData.UserId);
  }

  public String getUserCode()throws SQLException{
    return rs.getString(TestUserMetaData.UserCode);
  }

  public String getUserName()throws SQLException{
    return rs.getString(TestUserMetaData.UserName);
  }

  public String getUserAccount()throws SQLException{
    return rs.getString(TestUserMetaData.UserAccount);
  }

  public String getUserAlias()throws SQLException{
    return rs.getString(TestUserMetaData.UserAlias);
  }

  public String getTel()throws SQLException{
    return rs.getString(TestUserMetaData.Tel);
  }

  public String getMobile()throws SQLException{
    return rs.getString(TestUserMetaData.Mobile);
  }

  public String getEmail()throws SQLException{
    return rs.getString(TestUserMetaData.Email);
  }

  public String getPassword()throws SQLException{
    return rs.getString(TestUserMetaData.Password);
  }

  public String getRemark()throws SQLException{
    return rs.getString(TestUserMetaData.Remark);
  }

  public String getPhotoUrl()throws SQLException{
    return rs.getString(TestUserMetaData.PhotoUrl);
  }

  public Long getCenterId()throws SQLException{
    return rs.getLong(TestUserMetaData.CenterId);
  }

  public Long getLoginCenterId()throws SQLException{
    return rs.getLong(TestUserMetaData.LoginCenterId);
  }

  public String getCenterIds()throws SQLException{
    return rs.getString(TestUserMetaData.CenterIds);
  }

  public String getCenterNames()throws SQLException{
    return rs.getString(TestUserMetaData.CenterNames);
  }

  public Long getOrgId()throws SQLException{
    return rs.getLong(TestUserMetaData.OrgId);
  }

  public Long getDeptId()throws SQLException{
    return rs.getLong(TestUserMetaData.DeptId);
  }

  public Long getRoleId()throws SQLException{
    return rs.getLong(TestUserMetaData.RoleId);
  }

  public String getRoleIds()throws SQLException{
    return rs.getString(TestUserMetaData.RoleIds);
  }

  public String getRoleNames()throws SQLException{
    return rs.getString(TestUserMetaData.RoleNames);
  }

  public String getRank()throws SQLException{
    return rs.getString(TestUserMetaData.Rank);
  }

  public Boolean getFrozen()throws SQLException{
    return rs.getBoolean(TestUserMetaData.Frozen);
  }

  public Boolean getValid()throws SQLException{
    return rs.getBoolean(TestUserMetaData.Valid);
  }

  public Date getCreateTime()throws SQLException{
    return rs.getDate(TestUserMetaData.CreateTime);
  }

  public Long getCreateUserId()throws SQLException{
    return rs.getLong(TestUserMetaData.CreateUserId);
  }

  public String getCreateUserName()throws SQLException{
    return rs.getString(TestUserMetaData.CreateUserName);
  }

  public Date getModifyTime()throws SQLException{
    return rs.getDate(TestUserMetaData.ModifyTime);
  }

  public Long getModifyUserId()throws SQLException{
    return rs.getLong(TestUserMetaData.ModifyUserId);
  }

  public String getModifyUserName()throws SQLException{
    return rs.getString(TestUserMetaData.ModifyUserName);
  }

  public Date getDeleteTime()throws SQLException{
    return rs.getDate(TestUserMetaData.DeleteTime);
  }

  public Long getDeleteUserId()throws SQLException{
    return rs.getLong(TestUserMetaData.DeleteUserId);
  }

  public String getDeleteUserName()throws SQLException{
    return rs.getString(TestUserMetaData.DeleteUserName);
  }

  public Integer getVersion()throws SQLException{
    return rs.getInt(TestUserMetaData.Version);
  }

}
