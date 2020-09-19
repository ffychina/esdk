package com.esdk.test.orm;
import java.sql.Connection;

import com.esdk.sql.orm.AbstractSelect;

import java.sql.SQLException;

import com.esdk.sql.ISQL;

import com.esdk.sql.Field;

import com.esdk.sql.SmartBetween;

import java.util.Date;

public class TestUniqueCodeSelect extends AbstractSelect<TestUniqueCodeSelect,TestUniqueCodeRow> implements ISQL{
  public static final TestUniqueCodeMetaData metaData=new TestUniqueCodeMetaData();
  public static final TestUniqueCodeMetaData md=metaData;
  public TestUniqueCodeSelect(){
    super(TestUniqueCodeMetaData.TABLENAME,false);
  }

  public TestUniqueCodeSelect(Connection conn,boolean isTop){
    super(TestUniqueCodeMetaData.TABLENAME,isTop,conn);
  }

  public TestUniqueCodeSelect(Connection conn){
    super(TestUniqueCodeMetaData.TABLENAME,conn);
  }

  public TestUniqueCodeSelect(boolean isJoin){
    super(TestUniqueCodeMetaData.TABLENAME,isJoin);
  }

  public TestUniqueCodeSelect(String joinType){
    super(TestUniqueCodeMetaData.TABLENAME,joinType);
  }

  public TestUniqueCodeSelect(com.esdk.sql.orm.ORMSession ormsession,boolean isTop){
    super(TestUniqueCodeMetaData.TABLENAME,isTop,ormsession);
  }

  public TestUniqueCodeSelect(com.esdk.sql.orm.ORMSession ormsession){
    super(TestUniqueCodeMetaData.TABLENAME,ormsession);
  }

  public TestUniqueCodeMetaData getMetaData(){
    return metaData;
  }

  public TestUniqueCodeRow[] toTestUniqueCodeRowArray()throws Exception{
    return (TestUniqueCodeRow[])list().toArray(new TestUniqueCodeRow[0]);
  }

  public TestUniqueCodeRow getFirstTestUniqueCodeRow(){
    return (TestUniqueCodeRow)getFirstRow();
  }

  public TestUniqueCodeRow getFirstTestUniqueCodeRow(boolean isCreateInstance){
    return (TestUniqueCodeRow)getFirstRow(isCreateInstance);
  }

  public TestUniqueCodeResultSet toTestUniqueCodeResultSet()throws SQLException{
    return (TestUniqueCodeResultSet)toParentResultSet();
  }

  public void setPrimaryKey(Object value){
    fieldMap.put(TestUniqueCodeMetaData.PrimaryKey,value);
  }

  public Long getUid(){
    return (Long)fieldMap.get(TestUniqueCodeMetaData.Uid);
  }

  public TestUniqueCodeSelect setUid(Long value){
    fieldMap.put(TestUniqueCodeMetaData.Uid,value);
    return this;
  }

  public TestUniqueCodeSelect setUid(Long[] values){
    super.addIn(TestUniqueCodeMetaData.Uid,values);
    return this;
  }

  public TestUniqueCodeSelect setUid(AbstractSelect select){
    super.addIn(TestUniqueCodeMetaData.Uid,select);
    return this;
  }

  public TestUniqueCodeSelect setUid(Long start,Long end){
    fieldMap.put(TestUniqueCodeMetaData.Uid,new SmartBetween(this.createField(TestUniqueCodeMetaData.Uid),start,end));
    return this;
  }

  public Field createUidField(){
    return this.createField(metaData.Uid);
  }

  public Long getMachineId(){
    return (Long)fieldMap.get(TestUniqueCodeMetaData.MachineId);
  }

  public TestUniqueCodeSelect setMachineId(Long value){
    fieldMap.put(TestUniqueCodeMetaData.MachineId,value);
    return this;
  }

  public TestUniqueCodeSelect setMachineId(Long[] values){
    super.addIn(TestUniqueCodeMetaData.MachineId,values);
    return this;
  }

  public TestUniqueCodeSelect setMachineId(AbstractSelect select){
    super.addIn(TestUniqueCodeMetaData.MachineId,select);
    return this;
  }

  public Field createMachineIdField(){
    return this.createField(metaData.MachineId);
  }

  public Long getProductId(){
    return (Long)fieldMap.get(TestUniqueCodeMetaData.ProductId);
  }

  public TestUniqueCodeSelect setProductId(Long value){
    fieldMap.put(TestUniqueCodeMetaData.ProductId,value);
    return this;
  }

  public TestUniqueCodeSelect setProductId(Long[] values){
    super.addIn(TestUniqueCodeMetaData.ProductId,values);
    return this;
  }

  public TestUniqueCodeSelect setProductId(AbstractSelect select){
    super.addIn(TestUniqueCodeMetaData.ProductId,select);
    return this;
  }

  public Field createProductIdField(){
    return this.createField(metaData.ProductId);
  }

  public Boolean getValid(){
    return (Boolean)fieldMap.get(TestUniqueCodeMetaData.Valid);
  }

  public TestUniqueCodeSelect setValid(Boolean value){
    fieldMap.put(TestUniqueCodeMetaData.Valid,value);
    return this;
  }

  public Field createValidField(){
    return this.createField(metaData.Valid);
  }

  public Date getCreateTime(){
    return (Date)fieldMap.get(TestUniqueCodeMetaData.CreateTime);
  }

  public TestUniqueCodeSelect setCreateTime(Date value){
    fieldMap.put(TestUniqueCodeMetaData.CreateTime,value);
    return this;
  }

  public TestUniqueCodeSelect setCreateTime(Date start,Date end){
    fieldMap.put(TestUniqueCodeMetaData.CreateTime,new SmartBetween(this.createField(TestUniqueCodeMetaData.CreateTime),start,end));
    return this;
  }

  public Field createCreateTimeField(){
    return this.createField(metaData.CreateTime);
  }

}
