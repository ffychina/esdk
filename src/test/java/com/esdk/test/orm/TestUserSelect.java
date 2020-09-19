package com.esdk.test.orm;
import java.sql.Connection;

import com.esdk.sql.orm.AbstractSelect;

import java.sql.SQLException;

import com.esdk.sql.ISQL;

import com.esdk.sql.Field;

import java.util.Date;

import com.esdk.sql.SmartBetween;

public class TestUserSelect extends AbstractSelect<TestUserSelect,TestUserRow> implements ISQL{
  public static final TestUserMetaData metaData=new TestUserMetaData();
  public static final TestUserMetaData md=metaData;
  public TestUserSelect(){
    super(TestUserMetaData.TABLENAME,false);
  }

  public TestUserSelect(Connection conn,boolean isTop){
    super(TestUserMetaData.TABLENAME,isTop,conn);
  }

  public TestUserSelect(Connection conn){
    super(TestUserMetaData.TABLENAME,conn);
  }

  public TestUserSelect(boolean isJoin){
    super(TestUserMetaData.TABLENAME,isJoin);
  }

  public TestUserSelect(String joinType){
    super(TestUserMetaData.TABLENAME,joinType);
  }

  public TestUserSelect(com.esdk.sql.orm.ORMSession ormsession,boolean isTop){
    super(TestUserMetaData.TABLENAME,isTop,ormsession);
  }

  public TestUserSelect(com.esdk.sql.orm.ORMSession ormsession){
    super(TestUserMetaData.TABLENAME,ormsession);
  }

  public TestUserMetaData getMetaData(){
    return metaData;
  }

  public TestUserRow[] toTestUserRowArray()throws Exception{
    return (TestUserRow[])list().toArray(new TestUserRow[0]);
  }

  public TestUserRow getFirstTestUserRow(){
    return (TestUserRow)getFirstRow();
  }

  public TestUserRow getFirstTestUserRow(boolean isCreateInstance){
    return (TestUserRow)getFirstRow(isCreateInstance);
  }

  public TestUserResultSet toTestUserResultSet()throws SQLException{
    return (TestUserResultSet)toParentResultSet();
  }

  public void setPrimaryKey(Object value){
    fieldMap.put(TestUserMetaData.PrimaryKey,value);
  }

  public Long getUserId(){
    return (Long)fieldMap.get(TestUserMetaData.UserId);
  }

  public TestUserSelect setUserId(Long value){
    fieldMap.put(TestUserMetaData.UserId,value);
    return this;
  }

  public TestUserSelect setUserId(Long[] values){
    super.addIn(TestUserMetaData.UserId,values);
    return this;
  }

  public TestUserSelect setUserId(AbstractSelect select){
    super.addIn(TestUserMetaData.UserId,select);
    return this;
  }

  public Field createUserIdField(){
    return this.createField(metaData.UserId);
  }

  public String getUserCode(){
    return (String)fieldMap.get(TestUserMetaData.UserCode);
  }

  public TestUserSelect setUserCode(String value){
    fieldMap.put(TestUserMetaData.UserCode,value);
    return this;
  }

  public TestUserSelect setUserCode(String[] values){
    super.addIn(TestUserMetaData.UserCode,values);
    return this;
  }

  public TestUserSelect setUserCode(AbstractSelect select){
    super.addIn(TestUserMetaData.UserCode,select);
    return this;
  }

  public TestUserSelect addLikeUserCode(String value){
    addLikeCondition(metaData.UserCode,value);
    return this;
  }

  public Field createUserCodeField(){
    return this.createField(metaData.UserCode);
  }

  public String getUserName(){
    return (String)fieldMap.get(TestUserMetaData.UserName);
  }

  public TestUserSelect setUserName(String value){
    fieldMap.put(TestUserMetaData.UserName,value);
    return this;
  }

  public TestUserSelect setUserName(String[] values){
    super.addIn(TestUserMetaData.UserName,values);
    return this;
  }

  public TestUserSelect setUserName(AbstractSelect select){
    super.addIn(TestUserMetaData.UserName,select);
    return this;
  }

  public TestUserSelect addLikeUserName(String value){
    addLikeCondition(metaData.UserName,value);
    return this;
  }

  public Field createUserNameField(){
    return this.createField(metaData.UserName);
  }

  public String getUserAccount(){
    return (String)fieldMap.get(TestUserMetaData.UserAccount);
  }

  public TestUserSelect setUserAccount(String value){
    fieldMap.put(TestUserMetaData.UserAccount,value);
    return this;
  }

  public TestUserSelect setUserAccount(String[] values){
    super.addIn(TestUserMetaData.UserAccount,values);
    return this;
  }

  public TestUserSelect setUserAccount(AbstractSelect select){
    super.addIn(TestUserMetaData.UserAccount,select);
    return this;
  }

  public TestUserSelect addLikeUserAccount(String value){
    addLikeCondition(metaData.UserAccount,value);
    return this;
  }

  public Field createUserAccountField(){
    return this.createField(metaData.UserAccount);
  }

  public String getUserAlias(){
    return (String)fieldMap.get(TestUserMetaData.UserAlias);
  }

  public TestUserSelect setUserAlias(String value){
    fieldMap.put(TestUserMetaData.UserAlias,value);
    return this;
  }

  public TestUserSelect setUserAlias(String[] values){
    super.addIn(TestUserMetaData.UserAlias,values);
    return this;
  }

  public TestUserSelect setUserAlias(AbstractSelect select){
    super.addIn(TestUserMetaData.UserAlias,select);
    return this;
  }

  public TestUserSelect addLikeUserAlias(String value){
    addLikeCondition(metaData.UserAlias,value);
    return this;
  }

  public Field createUserAliasField(){
    return this.createField(metaData.UserAlias);
  }

  public String getTel(){
    return (String)fieldMap.get(TestUserMetaData.Tel);
  }

  public TestUserSelect setTel(String value){
    fieldMap.put(TestUserMetaData.Tel,value);
    return this;
  }

  public TestUserSelect setTel(String[] values){
    super.addIn(TestUserMetaData.Tel,values);
    return this;
  }

  public TestUserSelect setTel(AbstractSelect select){
    super.addIn(TestUserMetaData.Tel,select);
    return this;
  }

  public TestUserSelect addLikeTel(String value){
    addLikeCondition(metaData.Tel,value);
    return this;
  }

  public Field createTelField(){
    return this.createField(metaData.Tel);
  }

  public String getMobile(){
    return (String)fieldMap.get(TestUserMetaData.Mobile);
  }

  public TestUserSelect setMobile(String value){
    fieldMap.put(TestUserMetaData.Mobile,value);
    return this;
  }

  public TestUserSelect setMobile(String[] values){
    super.addIn(TestUserMetaData.Mobile,values);
    return this;
  }

  public TestUserSelect setMobile(AbstractSelect select){
    super.addIn(TestUserMetaData.Mobile,select);
    return this;
  }

  public TestUserSelect addLikeMobile(String value){
    addLikeCondition(metaData.Mobile,value);
    return this;
  }

  public Field createMobileField(){
    return this.createField(metaData.Mobile);
  }

  public String getEmail(){
    return (String)fieldMap.get(TestUserMetaData.Email);
  }

  public TestUserSelect setEmail(String value){
    fieldMap.put(TestUserMetaData.Email,value);
    return this;
  }

  public TestUserSelect setEmail(String[] values){
    super.addIn(TestUserMetaData.Email,values);
    return this;
  }

  public TestUserSelect setEmail(AbstractSelect select){
    super.addIn(TestUserMetaData.Email,select);
    return this;
  }

  public TestUserSelect addLikeEmail(String value){
    addLikeCondition(metaData.Email,value);
    return this;
  }

  public Field createEmailField(){
    return this.createField(metaData.Email);
  }

  public String getPassword(){
    return (String)fieldMap.get(TestUserMetaData.Password);
  }

  public TestUserSelect setPassword(String value){
    fieldMap.put(TestUserMetaData.Password,value);
    return this;
  }

  public TestUserSelect setPassword(String[] values){
    super.addIn(TestUserMetaData.Password,values);
    return this;
  }

  public TestUserSelect setPassword(AbstractSelect select){
    super.addIn(TestUserMetaData.Password,select);
    return this;
  }

  public TestUserSelect addLikePassword(String value){
    addLikeCondition(metaData.Password,value);
    return this;
  }

  public Field createPasswordField(){
    return this.createField(metaData.Password);
  }

  public String getRemark(){
    return (String)fieldMap.get(TestUserMetaData.Remark);
  }

  public TestUserSelect setRemark(String value){
    fieldMap.put(TestUserMetaData.Remark,value);
    return this;
  }

  public TestUserSelect setRemark(String[] values){
    super.addIn(TestUserMetaData.Remark,values);
    return this;
  }

  public TestUserSelect setRemark(AbstractSelect select){
    super.addIn(TestUserMetaData.Remark,select);
    return this;
  }

  public TestUserSelect addLikeRemark(String value){
    addLikeCondition(metaData.Remark,value);
    return this;
  }

  public Field createRemarkField(){
    return this.createField(metaData.Remark);
  }

  public String getPhotoUrl(){
    return (String)fieldMap.get(TestUserMetaData.PhotoUrl);
  }

  public TestUserSelect setPhotoUrl(String value){
    fieldMap.put(TestUserMetaData.PhotoUrl,value);
    return this;
  }

  public TestUserSelect setPhotoUrl(String[] values){
    super.addIn(TestUserMetaData.PhotoUrl,values);
    return this;
  }

  public TestUserSelect setPhotoUrl(AbstractSelect select){
    super.addIn(TestUserMetaData.PhotoUrl,select);
    return this;
  }

  public TestUserSelect addLikePhotoUrl(String value){
    addLikeCondition(metaData.PhotoUrl,value);
    return this;
  }

  public Field createPhotoUrlField(){
    return this.createField(metaData.PhotoUrl);
  }

  public Long getCenterId(){
    return (Long)fieldMap.get(TestUserMetaData.CenterId);
  }

  public TestUserSelect setCenterId(Long value){
    fieldMap.put(TestUserMetaData.CenterId,value);
    return this;
  }

  public TestUserSelect setCenterId(Long[] values){
    super.addIn(TestUserMetaData.CenterId,values);
    return this;
  }

  public TestUserSelect setCenterId(AbstractSelect select){
    super.addIn(TestUserMetaData.CenterId,select);
    return this;
  }

  public Field createCenterIdField(){
    return this.createField(metaData.CenterId);
  }

  public Long getLoginCenterId(){
    return (Long)fieldMap.get(TestUserMetaData.LoginCenterId);
  }

  public TestUserSelect setLoginCenterId(Long value){
    fieldMap.put(TestUserMetaData.LoginCenterId,value);
    return this;
  }

  public TestUserSelect setLoginCenterId(Long[] values){
    super.addIn(TestUserMetaData.LoginCenterId,values);
    return this;
  }

  public TestUserSelect setLoginCenterId(AbstractSelect select){
    super.addIn(TestUserMetaData.LoginCenterId,select);
    return this;
  }

  public Field createLoginCenterIdField(){
    return this.createField(metaData.LoginCenterId);
  }

  public String getCenterIds(){
    return (String)fieldMap.get(TestUserMetaData.CenterIds);
  }

  public TestUserSelect setCenterIds(String value){
    fieldMap.put(TestUserMetaData.CenterIds,value);
    return this;
  }

  public TestUserSelect setCenterIds(String[] values){
    super.addIn(TestUserMetaData.CenterIds,values);
    return this;
  }

  public TestUserSelect setCenterIds(AbstractSelect select){
    super.addIn(TestUserMetaData.CenterIds,select);
    return this;
  }

  public TestUserSelect addLikeCenterIds(String value){
    addLikeCondition(metaData.CenterIds,value);
    return this;
  }

  public Field createCenterIdsField(){
    return this.createField(metaData.CenterIds);
  }

  public String getCenterNames(){
    return (String)fieldMap.get(TestUserMetaData.CenterNames);
  }

  public TestUserSelect setCenterNames(String value){
    fieldMap.put(TestUserMetaData.CenterNames,value);
    return this;
  }

  public TestUserSelect setCenterNames(String[] values){
    super.addIn(TestUserMetaData.CenterNames,values);
    return this;
  }

  public TestUserSelect setCenterNames(AbstractSelect select){
    super.addIn(TestUserMetaData.CenterNames,select);
    return this;
  }

  public TestUserSelect addLikeCenterNames(String value){
    addLikeCondition(metaData.CenterNames,value);
    return this;
  }

  public Field createCenterNamesField(){
    return this.createField(metaData.CenterNames);
  }

  public Long getOrgId(){
    return (Long)fieldMap.get(TestUserMetaData.OrgId);
  }

  public TestUserSelect setOrgId(Long value){
    fieldMap.put(TestUserMetaData.OrgId,value);
    return this;
  }

  public TestUserSelect setOrgId(Long[] values){
    super.addIn(TestUserMetaData.OrgId,values);
    return this;
  }

  public TestUserSelect setOrgId(AbstractSelect select){
    super.addIn(TestUserMetaData.OrgId,select);
    return this;
  }

  public Field createOrgIdField(){
    return this.createField(metaData.OrgId);
  }

  public Long getDeptId(){
    return (Long)fieldMap.get(TestUserMetaData.DeptId);
  }

  public TestUserSelect setDeptId(Long value){
    fieldMap.put(TestUserMetaData.DeptId,value);
    return this;
  }

  public TestUserSelect setDeptId(Long[] values){
    super.addIn(TestUserMetaData.DeptId,values);
    return this;
  }

  public TestUserSelect setDeptId(AbstractSelect select){
    super.addIn(TestUserMetaData.DeptId,select);
    return this;
  }

  public Field createDeptIdField(){
    return this.createField(metaData.DeptId);
  }

  public Long getRoleId(){
    return (Long)fieldMap.get(TestUserMetaData.RoleId);
  }

  public TestUserSelect setRoleId(Long value){
    fieldMap.put(TestUserMetaData.RoleId,value);
    return this;
  }

  public TestUserSelect setRoleId(Long[] values){
    super.addIn(TestUserMetaData.RoleId,values);
    return this;
  }

  public TestUserSelect setRoleId(AbstractSelect select){
    super.addIn(TestUserMetaData.RoleId,select);
    return this;
  }

  public Field createRoleIdField(){
    return this.createField(metaData.RoleId);
  }

  public String getRoleIds(){
    return (String)fieldMap.get(TestUserMetaData.RoleIds);
  }

  public TestUserSelect setRoleIds(String value){
    fieldMap.put(TestUserMetaData.RoleIds,value);
    return this;
  }

  public TestUserSelect setRoleIds(String[] values){
    super.addIn(TestUserMetaData.RoleIds,values);
    return this;
  }

  public TestUserSelect setRoleIds(AbstractSelect select){
    super.addIn(TestUserMetaData.RoleIds,select);
    return this;
  }

  public TestUserSelect addLikeRoleIds(String value){
    addLikeCondition(metaData.RoleIds,value);
    return this;
  }

  public Field createRoleIdsField(){
    return this.createField(metaData.RoleIds);
  }

  public String getRoleNames(){
    return (String)fieldMap.get(TestUserMetaData.RoleNames);
  }

  public TestUserSelect setRoleNames(String value){
    fieldMap.put(TestUserMetaData.RoleNames,value);
    return this;
  }

  public TestUserSelect setRoleNames(String[] values){
    super.addIn(TestUserMetaData.RoleNames,values);
    return this;
  }

  public TestUserSelect setRoleNames(AbstractSelect select){
    super.addIn(TestUserMetaData.RoleNames,select);
    return this;
  }

  public TestUserSelect addLikeRoleNames(String value){
    addLikeCondition(metaData.RoleNames,value);
    return this;
  }

  public Field createRoleNamesField(){
    return this.createField(metaData.RoleNames);
  }

  public String getRank(){
    return (String)fieldMap.get(TestUserMetaData.Rank);
  }

  public TestUserSelect setRank(String value){
    fieldMap.put(TestUserMetaData.Rank,value);
    return this;
  }

  public TestUserSelect setRank(String[] values){
    super.addIn(TestUserMetaData.Rank,values);
    return this;
  }

  public TestUserSelect setRank(AbstractSelect select){
    super.addIn(TestUserMetaData.Rank,select);
    return this;
  }

  public TestUserSelect addLikeRank(String value){
    addLikeCondition(metaData.Rank,value);
    return this;
  }

  public Field createRankField(){
    return this.createField(metaData.Rank);
  }

  public Boolean getFrozen(){
    return (Boolean)fieldMap.get(TestUserMetaData.Frozen);
  }

  public TestUserSelect setFrozen(Boolean value){
    fieldMap.put(TestUserMetaData.Frozen,value);
    return this;
  }

  public Field createFrozenField(){
    return this.createField(metaData.Frozen);
  }

  public Boolean getValid(){
    return (Boolean)fieldMap.get(TestUserMetaData.Valid);
  }

  public TestUserSelect setValid(Boolean value){
    fieldMap.put(TestUserMetaData.Valid,value);
    return this;
  }

  public Field createValidField(){
    return this.createField(metaData.Valid);
  }

  public Date getCreateTime(){
    return (Date)fieldMap.get(TestUserMetaData.CreateTime);
  }

  public TestUserSelect setCreateTime(Date value){
    fieldMap.put(TestUserMetaData.CreateTime,value);
    return this;
  }

  public TestUserSelect setCreateTime(Date start,Date end){
    fieldMap.put(TestUserMetaData.CreateTime,new SmartBetween(this.createField(TestUserMetaData.CreateTime),start,end));
    return this;
  }

  public Field createCreateTimeField(){
    return this.createField(metaData.CreateTime);
  }

  public Long getCreateUserId(){
    return (Long)fieldMap.get(TestUserMetaData.CreateUserId);
  }

  public TestUserSelect setCreateUserId(Long value){
    fieldMap.put(TestUserMetaData.CreateUserId,value);
    return this;
  }

  public TestUserSelect setCreateUserId(Long[] values){
    super.addIn(TestUserMetaData.CreateUserId,values);
    return this;
  }

  public TestUserSelect setCreateUserId(AbstractSelect select){
    super.addIn(TestUserMetaData.CreateUserId,select);
    return this;
  }

  public Field createCreateUserIdField(){
    return this.createField(metaData.CreateUserId);
  }

  public String getCreateUserName(){
    return (String)fieldMap.get(TestUserMetaData.CreateUserName);
  }

  public TestUserSelect setCreateUserName(String value){
    fieldMap.put(TestUserMetaData.CreateUserName,value);
    return this;
  }

  public TestUserSelect setCreateUserName(String[] values){
    super.addIn(TestUserMetaData.CreateUserName,values);
    return this;
  }

  public TestUserSelect setCreateUserName(AbstractSelect select){
    super.addIn(TestUserMetaData.CreateUserName,select);
    return this;
  }

  public TestUserSelect addLikeCreateUserName(String value){
    addLikeCondition(metaData.CreateUserName,value);
    return this;
  }

  public Field createCreateUserNameField(){
    return this.createField(metaData.CreateUserName);
  }

  public Date getModifyTime(){
    return (Date)fieldMap.get(TestUserMetaData.ModifyTime);
  }

  public TestUserSelect setModifyTime(Date value){
    fieldMap.put(TestUserMetaData.ModifyTime,value);
    return this;
  }

  public TestUserSelect setModifyTime(Date start,Date end){
    fieldMap.put(TestUserMetaData.ModifyTime,new SmartBetween(this.createField(TestUserMetaData.ModifyTime),start,end));
    return this;
  }

  public Field createModifyTimeField(){
    return this.createField(metaData.ModifyTime);
  }

  public Long getModifyUserId(){
    return (Long)fieldMap.get(TestUserMetaData.ModifyUserId);
  }

  public TestUserSelect setModifyUserId(Long value){
    fieldMap.put(TestUserMetaData.ModifyUserId,value);
    return this;
  }

  public TestUserSelect setModifyUserId(Long[] values){
    super.addIn(TestUserMetaData.ModifyUserId,values);
    return this;
  }

  public TestUserSelect setModifyUserId(AbstractSelect select){
    super.addIn(TestUserMetaData.ModifyUserId,select);
    return this;
  }

  public Field createModifyUserIdField(){
    return this.createField(metaData.ModifyUserId);
  }

  public String getModifyUserName(){
    return (String)fieldMap.get(TestUserMetaData.ModifyUserName);
  }

  public TestUserSelect setModifyUserName(String value){
    fieldMap.put(TestUserMetaData.ModifyUserName,value);
    return this;
  }

  public TestUserSelect setModifyUserName(String[] values){
    super.addIn(TestUserMetaData.ModifyUserName,values);
    return this;
  }

  public TestUserSelect setModifyUserName(AbstractSelect select){
    super.addIn(TestUserMetaData.ModifyUserName,select);
    return this;
  }

  public TestUserSelect addLikeModifyUserName(String value){
    addLikeCondition(metaData.ModifyUserName,value);
    return this;
  }

  public Field createModifyUserNameField(){
    return this.createField(metaData.ModifyUserName);
  }

  public Date getDeleteTime(){
    return (Date)fieldMap.get(TestUserMetaData.DeleteTime);
  }

  public TestUserSelect setDeleteTime(Date value){
    fieldMap.put(TestUserMetaData.DeleteTime,value);
    return this;
  }

  public TestUserSelect setDeleteTime(Date start,Date end){
    fieldMap.put(TestUserMetaData.DeleteTime,new SmartBetween(this.createField(TestUserMetaData.DeleteTime),start,end));
    return this;
  }

  public Field createDeleteTimeField(){
    return this.createField(metaData.DeleteTime);
  }

  public Long getDeleteUserId(){
    return (Long)fieldMap.get(TestUserMetaData.DeleteUserId);
  }

  public TestUserSelect setDeleteUserId(Long value){
    fieldMap.put(TestUserMetaData.DeleteUserId,value);
    return this;
  }

  public TestUserSelect setDeleteUserId(Long[] values){
    super.addIn(TestUserMetaData.DeleteUserId,values);
    return this;
  }

  public TestUserSelect setDeleteUserId(AbstractSelect select){
    super.addIn(TestUserMetaData.DeleteUserId,select);
    return this;
  }

  public Field createDeleteUserIdField(){
    return this.createField(metaData.DeleteUserId);
  }

  public String getDeleteUserName(){
    return (String)fieldMap.get(TestUserMetaData.DeleteUserName);
  }

  public TestUserSelect setDeleteUserName(String value){
    fieldMap.put(TestUserMetaData.DeleteUserName,value);
    return this;
  }

  public TestUserSelect setDeleteUserName(String[] values){
    super.addIn(TestUserMetaData.DeleteUserName,values);
    return this;
  }

  public TestUserSelect setDeleteUserName(AbstractSelect select){
    super.addIn(TestUserMetaData.DeleteUserName,select);
    return this;
  }

  public TestUserSelect addLikeDeleteUserName(String value){
    addLikeCondition(metaData.DeleteUserName,value);
    return this;
  }

  public Field createDeleteUserNameField(){
    return this.createField(metaData.DeleteUserName);
  }

  public Integer getVersion(){
    return (Integer)fieldMap.get(TestUserMetaData.Version);
  }

  public TestUserSelect setVersion(Integer value){
    fieldMap.put(TestUserMetaData.Version,value);
    return this;
  }

  public TestUserSelect setVersion(Integer[] values){
    super.addIn(TestUserMetaData.Version,values);
    return this;
  }

  public TestUserSelect setVersion(AbstractSelect select){
    super.addIn(TestUserMetaData.Version,select);
    return this;
  }

  public TestUserSelect setVersion(Integer start,Integer end){
    fieldMap.put(TestUserMetaData.Version,new SmartBetween(this.createField(TestUserMetaData.Version),start,end));
    return this;
  }

  public Field createVersionField(){
    return this.createField(metaData.Version);
  }

}
