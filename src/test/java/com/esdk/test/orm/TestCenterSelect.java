package com.esdk.test.orm;
import java.sql.Connection;

import com.esdk.sql.orm.AbstractSelect;

import java.sql.SQLException;

import com.esdk.sql.ISQL;

import com.esdk.sql.Field;

import java.util.Date;

import com.esdk.sql.SmartBetween;

public class TestCenterSelect extends AbstractSelect<TestCenterSelect,TestCenterRow> implements ISQL{
  public static final TestCenterMetaData metaData=new TestCenterMetaData();
  public static final TestCenterMetaData md=metaData;
  public TestCenterSelect(){
    super(TestCenterMetaData.TABLENAME,false);
  }

  public TestCenterSelect(Connection conn,boolean isTop){
    super(TestCenterMetaData.TABLENAME,isTop,conn);
  }

  public TestCenterSelect(Connection conn){
    super(TestCenterMetaData.TABLENAME,conn);
  }

  public TestCenterSelect(boolean isJoin){
    super(TestCenterMetaData.TABLENAME,isJoin);
  }

  public TestCenterSelect(String joinType){
    super(TestCenterMetaData.TABLENAME,joinType);
  }

  public TestCenterSelect(com.esdk.sql.orm.ORMSession ormsession,boolean isTop){
    super(TestCenterMetaData.TABLENAME,isTop,ormsession);
  }

  public TestCenterSelect(com.esdk.sql.orm.ORMSession ormsession){
    super(TestCenterMetaData.TABLENAME,ormsession);
  }

  public TestCenterMetaData getMetaData(){
    return metaData;
  }

  public TestCenterRow[] toTestCenterRowArray()throws Exception{
    return (TestCenterRow[])list().toArray(new TestCenterRow[0]);
  }

  public TestCenterRow getFirstTestCenterRow(){
    return (TestCenterRow)getFirstRow();
  }

  public TestCenterRow getFirstTestCenterRow(boolean isCreateInstance){
    return (TestCenterRow)getFirstRow(isCreateInstance);
  }

  public TestCenterResultSet toTestCenterResultSet()throws SQLException{
    return (TestCenterResultSet)toParentResultSet();
  }

  public void setPrimaryKey(Object value){
    fieldMap.put(TestCenterMetaData.PrimaryKey,value);
  }

  public Integer getCenterId(){
    return (Integer)fieldMap.get(TestCenterMetaData.CenterId);
  }

  public TestCenterSelect setCenterId(Integer value){
    fieldMap.put(TestCenterMetaData.CenterId,value);
    return this;
  }

  public TestCenterSelect setCenterId(Integer[] values){
    super.addIn(TestCenterMetaData.CenterId,values);
    return this;
  }

  public TestCenterSelect setCenterId(AbstractSelect select){
    super.addIn(TestCenterMetaData.CenterId,select);
    return this;
  }

  public Field createCenterIdField(){
    return this.createField(metaData.CenterId);
  }

  public String getCode(){
    return (String)fieldMap.get(TestCenterMetaData.Code);
  }

  public TestCenterSelect setCode(String value){
    fieldMap.put(TestCenterMetaData.Code,value);
    return this;
  }

  public TestCenterSelect setCode(String[] values){
    super.addIn(TestCenterMetaData.Code,values);
    return this;
  }

  public TestCenterSelect setCode(AbstractSelect select){
    super.addIn(TestCenterMetaData.Code,select);
    return this;
  }

  public TestCenterSelect addLikeCode(String value){
    addLikeCondition(metaData.Code,value);
    return this;
  }

  public Field createCodeField(){
    return this.createField(metaData.Code);
  }

  public String getName(){
    return (String)fieldMap.get(TestCenterMetaData.Name);
  }

  public TestCenterSelect setName(String value){
    fieldMap.put(TestCenterMetaData.Name,value);
    return this;
  }

  public TestCenterSelect setName(String[] values){
    super.addIn(TestCenterMetaData.Name,values);
    return this;
  }

  public TestCenterSelect setName(AbstractSelect select){
    super.addIn(TestCenterMetaData.Name,select);
    return this;
  }

  public TestCenterSelect addLikeName(String value){
    addLikeCondition(metaData.Name,value);
    return this;
  }

  public Field createNameField(){
    return this.createField(metaData.Name);
  }

  public String getEnglishName(){
    return (String)fieldMap.get(TestCenterMetaData.EnglishName);
  }

  public TestCenterSelect setEnglishName(String value){
    fieldMap.put(TestCenterMetaData.EnglishName,value);
    return this;
  }

  public TestCenterSelect setEnglishName(String[] values){
    super.addIn(TestCenterMetaData.EnglishName,values);
    return this;
  }

  public TestCenterSelect setEnglishName(AbstractSelect select){
    super.addIn(TestCenterMetaData.EnglishName,select);
    return this;
  }

  public TestCenterSelect addLikeEnglishName(String value){
    addLikeCondition(metaData.EnglishName,value);
    return this;
  }

  public Field createEnglishNameField(){
    return this.createField(metaData.EnglishName);
  }

  public String getAbbr(){
    return (String)fieldMap.get(TestCenterMetaData.Abbr);
  }

  public TestCenterSelect setAbbr(String value){
    fieldMap.put(TestCenterMetaData.Abbr,value);
    return this;
  }

  public TestCenterSelect setAbbr(String[] values){
    super.addIn(TestCenterMetaData.Abbr,values);
    return this;
  }

  public TestCenterSelect setAbbr(AbstractSelect select){
    super.addIn(TestCenterMetaData.Abbr,select);
    return this;
  }

  public TestCenterSelect addLikeAbbr(String value){
    addLikeCondition(metaData.Abbr,value);
    return this;
  }

  public Field createAbbrField(){
    return this.createField(metaData.Abbr);
  }

  public String getTel(){
    return (String)fieldMap.get(TestCenterMetaData.Tel);
  }

  public TestCenterSelect setTel(String value){
    fieldMap.put(TestCenterMetaData.Tel,value);
    return this;
  }

  public TestCenterSelect setTel(String[] values){
    super.addIn(TestCenterMetaData.Tel,values);
    return this;
  }

  public TestCenterSelect setTel(AbstractSelect select){
    super.addIn(TestCenterMetaData.Tel,select);
    return this;
  }

  public TestCenterSelect addLikeTel(String value){
    addLikeCondition(metaData.Tel,value);
    return this;
  }

  public Field createTelField(){
    return this.createField(metaData.Tel);
  }

  public String getFax(){
    return (String)fieldMap.get(TestCenterMetaData.Fax);
  }

  public TestCenterSelect setFax(String value){
    fieldMap.put(TestCenterMetaData.Fax,value);
    return this;
  }

  public TestCenterSelect setFax(String[] values){
    super.addIn(TestCenterMetaData.Fax,values);
    return this;
  }

  public TestCenterSelect setFax(AbstractSelect select){
    super.addIn(TestCenterMetaData.Fax,select);
    return this;
  }

  public TestCenterSelect addLikeFax(String value){
    addLikeCondition(metaData.Fax,value);
    return this;
  }

  public Field createFaxField(){
    return this.createField(metaData.Fax);
  }

  public String getAddress(){
    return (String)fieldMap.get(TestCenterMetaData.Address);
  }

  public TestCenterSelect setAddress(String value){
    fieldMap.put(TestCenterMetaData.Address,value);
    return this;
  }

  public TestCenterSelect setAddress(String[] values){
    super.addIn(TestCenterMetaData.Address,values);
    return this;
  }

  public TestCenterSelect setAddress(AbstractSelect select){
    super.addIn(TestCenterMetaData.Address,select);
    return this;
  }

  public TestCenterSelect addLikeAddress(String value){
    addLikeCondition(metaData.Address,value);
    return this;
  }

  public Field createAddressField(){
    return this.createField(metaData.Address);
  }

  public String getRegion(){
    return (String)fieldMap.get(TestCenterMetaData.Region);
  }

  public TestCenterSelect setRegion(String value){
    fieldMap.put(TestCenterMetaData.Region,value);
    return this;
  }

  public TestCenterSelect setRegion(String[] values){
    super.addIn(TestCenterMetaData.Region,values);
    return this;
  }

  public TestCenterSelect setRegion(AbstractSelect select){
    super.addIn(TestCenterMetaData.Region,select);
    return this;
  }

  public TestCenterSelect addLikeRegion(String value){
    addLikeCondition(metaData.Region,value);
    return this;
  }

  public Field createRegionField(){
    return this.createField(metaData.Region);
  }

  public String getEmail(){
    return (String)fieldMap.get(TestCenterMetaData.Email);
  }

  public TestCenterSelect setEmail(String value){
    fieldMap.put(TestCenterMetaData.Email,value);
    return this;
  }

  public TestCenterSelect setEmail(String[] values){
    super.addIn(TestCenterMetaData.Email,values);
    return this;
  }

  public TestCenterSelect setEmail(AbstractSelect select){
    super.addIn(TestCenterMetaData.Email,select);
    return this;
  }

  public TestCenterSelect addLikeEmail(String value){
    addLikeCondition(metaData.Email,value);
    return this;
  }

  public Field createEmailField(){
    return this.createField(metaData.Email);
  }

  public String getContact(){
    return (String)fieldMap.get(TestCenterMetaData.Contact);
  }

  public TestCenterSelect setContact(String value){
    fieldMap.put(TestCenterMetaData.Contact,value);
    return this;
  }

  public TestCenterSelect setContact(String[] values){
    super.addIn(TestCenterMetaData.Contact,values);
    return this;
  }

  public TestCenterSelect setContact(AbstractSelect select){
    super.addIn(TestCenterMetaData.Contact,select);
    return this;
  }

  public TestCenterSelect addLikeContact(String value){
    addLikeCondition(metaData.Contact,value);
    return this;
  }

  public Field createContactField(){
    return this.createField(metaData.Contact);
  }

  public String getType(){
    return (String)fieldMap.get(TestCenterMetaData.Type);
  }

  public TestCenterSelect setType(String value){
    fieldMap.put(TestCenterMetaData.Type,value);
    return this;
  }

  public TestCenterSelect setType(String[] values){
    super.addIn(TestCenterMetaData.Type,values);
    return this;
  }

  public TestCenterSelect setType(AbstractSelect select){
    super.addIn(TestCenterMetaData.Type,select);
    return this;
  }

  public TestCenterSelect addLikeType(String value){
    addLikeCondition(metaData.Type,value);
    return this;
  }

  public Field createTypeField(){
    return this.createField(metaData.Type);
  }

  public String getRemark(){
    return (String)fieldMap.get(TestCenterMetaData.Remark);
  }

  public TestCenterSelect setRemark(String value){
    fieldMap.put(TestCenterMetaData.Remark,value);
    return this;
  }

  public TestCenterSelect setRemark(String[] values){
    super.addIn(TestCenterMetaData.Remark,values);
    return this;
  }

  public TestCenterSelect setRemark(AbstractSelect select){
    super.addIn(TestCenterMetaData.Remark,select);
    return this;
  }

  public TestCenterSelect addLikeRemark(String value){
    addLikeCondition(metaData.Remark,value);
    return this;
  }

  public Field createRemarkField(){
    return this.createField(metaData.Remark);
  }

  public String getLogoUrl(){
    return (String)fieldMap.get(TestCenterMetaData.LogoUrl);
  }

  public TestCenterSelect setLogoUrl(String value){
    fieldMap.put(TestCenterMetaData.LogoUrl,value);
    return this;
  }

  public TestCenterSelect setLogoUrl(String[] values){
    super.addIn(TestCenterMetaData.LogoUrl,values);
    return this;
  }

  public TestCenterSelect setLogoUrl(AbstractSelect select){
    super.addIn(TestCenterMetaData.LogoUrl,select);
    return this;
  }

  public TestCenterSelect addLikeLogoUrl(String value){
    addLikeCondition(metaData.LogoUrl,value);
    return this;
  }

  public Field createLogoUrlField(){
    return this.createField(metaData.LogoUrl);
  }

  public String getAppLogoUrl(){
    return (String)fieldMap.get(TestCenterMetaData.AppLogoUrl);
  }

  public TestCenterSelect setAppLogoUrl(String value){
    fieldMap.put(TestCenterMetaData.AppLogoUrl,value);
    return this;
  }

  public TestCenterSelect setAppLogoUrl(String[] values){
    super.addIn(TestCenterMetaData.AppLogoUrl,values);
    return this;
  }

  public TestCenterSelect setAppLogoUrl(AbstractSelect select){
    super.addIn(TestCenterMetaData.AppLogoUrl,select);
    return this;
  }

  public TestCenterSelect addLikeAppLogoUrl(String value){
    addLikeCondition(metaData.AppLogoUrl,value);
    return this;
  }

  public Field createAppLogoUrlField(){
    return this.createField(metaData.AppLogoUrl);
  }

  public Boolean getValid(){
    return (Boolean)fieldMap.get(TestCenterMetaData.Valid);
  }

  public TestCenterSelect setValid(Boolean value){
    fieldMap.put(TestCenterMetaData.Valid,value);
    return this;
  }

  public Field createValidField(){
    return this.createField(metaData.Valid);
  }

  public Date getCreateTime(){
    return (Date)fieldMap.get(TestCenterMetaData.CreateTime);
  }

  public TestCenterSelect setCreateTime(Date value){
    fieldMap.put(TestCenterMetaData.CreateTime,value);
    return this;
  }

  public TestCenterSelect setCreateTime(Date start,Date end){
    fieldMap.put(TestCenterMetaData.CreateTime,new SmartBetween(this.createField(TestCenterMetaData.CreateTime),start,end));
    return this;
  }

  public Field createCreateTimeField(){
    return this.createField(metaData.CreateTime);
  }

  public Integer getCreateUserId(){
    return (Integer)fieldMap.get(TestCenterMetaData.CreateUserId);
  }

  public TestCenterSelect setCreateUserId(Integer value){
    fieldMap.put(TestCenterMetaData.CreateUserId,value);
    return this;
  }

  public TestCenterSelect setCreateUserId(Integer[] values){
    super.addIn(TestCenterMetaData.CreateUserId,values);
    return this;
  }

  public TestCenterSelect setCreateUserId(AbstractSelect select){
    super.addIn(TestCenterMetaData.CreateUserId,select);
    return this;
  }

  public Field createCreateUserIdField(){
    return this.createField(metaData.CreateUserId);
  }

  public String getCreateUserName(){
    return (String)fieldMap.get(TestCenterMetaData.CreateUserName);
  }

  public TestCenterSelect setCreateUserName(String value){
    fieldMap.put(TestCenterMetaData.CreateUserName,value);
    return this;
  }

  public TestCenterSelect setCreateUserName(String[] values){
    super.addIn(TestCenterMetaData.CreateUserName,values);
    return this;
  }

  public TestCenterSelect setCreateUserName(AbstractSelect select){
    super.addIn(TestCenterMetaData.CreateUserName,select);
    return this;
  }

  public TestCenterSelect addLikeCreateUserName(String value){
    addLikeCondition(metaData.CreateUserName,value);
    return this;
  }

  public Field createCreateUserNameField(){
    return this.createField(metaData.CreateUserName);
  }

  public Date getModifyTime(){
    return (Date)fieldMap.get(TestCenterMetaData.ModifyTime);
  }

  public TestCenterSelect setModifyTime(Date value){
    fieldMap.put(TestCenterMetaData.ModifyTime,value);
    return this;
  }

  public TestCenterSelect setModifyTime(Date start,Date end){
    fieldMap.put(TestCenterMetaData.ModifyTime,new SmartBetween(this.createField(TestCenterMetaData.ModifyTime),start,end));
    return this;
  }

  public Field createModifyTimeField(){
    return this.createField(metaData.ModifyTime);
  }

  public Integer getModifyUserId(){
    return (Integer)fieldMap.get(TestCenterMetaData.ModifyUserId);
  }

  public TestCenterSelect setModifyUserId(Integer value){
    fieldMap.put(TestCenterMetaData.ModifyUserId,value);
    return this;
  }

  public TestCenterSelect setModifyUserId(Integer[] values){
    super.addIn(TestCenterMetaData.ModifyUserId,values);
    return this;
  }

  public TestCenterSelect setModifyUserId(AbstractSelect select){
    super.addIn(TestCenterMetaData.ModifyUserId,select);
    return this;
  }

  public Field createModifyUserIdField(){
    return this.createField(metaData.ModifyUserId);
  }

  public String getModifyUserName(){
    return (String)fieldMap.get(TestCenterMetaData.ModifyUserName);
  }

  public TestCenterSelect setModifyUserName(String value){
    fieldMap.put(TestCenterMetaData.ModifyUserName,value);
    return this;
  }

  public TestCenterSelect setModifyUserName(String[] values){
    super.addIn(TestCenterMetaData.ModifyUserName,values);
    return this;
  }

  public TestCenterSelect setModifyUserName(AbstractSelect select){
    super.addIn(TestCenterMetaData.ModifyUserName,select);
    return this;
  }

  public TestCenterSelect addLikeModifyUserName(String value){
    addLikeCondition(metaData.ModifyUserName,value);
    return this;
  }

  public Field createModifyUserNameField(){
    return this.createField(metaData.ModifyUserName);
  }

  public Date getDeleteTime(){
    return (Date)fieldMap.get(TestCenterMetaData.DeleteTime);
  }

  public TestCenterSelect setDeleteTime(Date value){
    fieldMap.put(TestCenterMetaData.DeleteTime,value);
    return this;
  }

  public TestCenterSelect setDeleteTime(Date start,Date end){
    fieldMap.put(TestCenterMetaData.DeleteTime,new SmartBetween(this.createField(TestCenterMetaData.DeleteTime),start,end));
    return this;
  }

  public Field createDeleteTimeField(){
    return this.createField(metaData.DeleteTime);
  }

  public Integer getDeleteUserId(){
    return (Integer)fieldMap.get(TestCenterMetaData.DeleteUserId);
  }

  public TestCenterSelect setDeleteUserId(Integer value){
    fieldMap.put(TestCenterMetaData.DeleteUserId,value);
    return this;
  }

  public TestCenterSelect setDeleteUserId(Integer[] values){
    super.addIn(TestCenterMetaData.DeleteUserId,values);
    return this;
  }

  public TestCenterSelect setDeleteUserId(AbstractSelect select){
    super.addIn(TestCenterMetaData.DeleteUserId,select);
    return this;
  }

  public Field createDeleteUserIdField(){
    return this.createField(metaData.DeleteUserId);
  }

  public String getDeleteUserName(){
    return (String)fieldMap.get(TestCenterMetaData.DeleteUserName);
  }

  public TestCenterSelect setDeleteUserName(String value){
    fieldMap.put(TestCenterMetaData.DeleteUserName,value);
    return this;
  }

  public TestCenterSelect setDeleteUserName(String[] values){
    super.addIn(TestCenterMetaData.DeleteUserName,values);
    return this;
  }

  public TestCenterSelect setDeleteUserName(AbstractSelect select){
    super.addIn(TestCenterMetaData.DeleteUserName,select);
    return this;
  }

  public TestCenterSelect addLikeDeleteUserName(String value){
    addLikeCondition(metaData.DeleteUserName,value);
    return this;
  }

  public Field createDeleteUserNameField(){
    return this.createField(metaData.DeleteUserName);
  }

  public Integer getVersion(){
    return (Integer)fieldMap.get(TestCenterMetaData.Version);
  }

  public TestCenterSelect setVersion(Integer value){
    fieldMap.put(TestCenterMetaData.Version,value);
    return this;
  }

  public TestCenterSelect setVersion(Integer[] values){
    super.addIn(TestCenterMetaData.Version,values);
    return this;
  }

  public TestCenterSelect setVersion(AbstractSelect select){
    super.addIn(TestCenterMetaData.Version,select);
    return this;
  }

  public TestCenterSelect setVersion(Integer start,Integer end){
    fieldMap.put(TestCenterMetaData.Version,new SmartBetween(this.createField(TestCenterMetaData.Version),start,end));
    return this;
  }

  public Field createVersionField(){
    return this.createField(metaData.Version);
  }

}
