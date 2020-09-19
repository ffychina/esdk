package com.esdk.sql.orm;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.IConnectionable;
import com.esdk.sql.ISelect;
import com.esdk.utils.EasySql;

public class ORMSession implements IConnectionable{
  HashMap<String,ABRowSet> selectMap=new HashMap();
  Connection conn;
  public ORMSession(Connection con){
    setConnection(con);
    ORMSessionFactory.addORMSession(this);
  }
  
  public <T extends AbstractSelect> T createSelect(Class<T> cls)throws Exception{
      java.lang.reflect.Constructor constructor=cls.getConstructor(new Class[] {Connection.class});
      AbstractSelect result=(AbstractSelect)constructor.newInstance(new Object[]{conn});
      result.setSession(this);
      return (T)result;
  }
  
  public void setAutoCommit(boolean b) throws SQLException {
    conn.setAutoCommit(b);
  }
  
  public void close(){
  	clear();
    ORMSessionFactory.removeORMSession(this);
    EasySql.close(conn);
  }
  
  public void clear() {
    selectMap.clear();
  }
  
  public ParentRow getRow(Class cls,BigDecimal pkid) throws Exception {
    ParentRow row=(ParentRow)cls.newInstance();
    row.refresh(pkid,this);
    return row;
  }
  
  public void flush() throws SQLException {
    for(Iterator iter=selectMap.values().iterator();iter.hasNext();) {
      ((ParentRow)iter.next()).save();
      ((ParentRow)iter.next()).flush();
    }
    if(!conn.getAutoCommit())
      conn.commit();
  }
  
  private boolean contain(ISelect s) {
    if(s==null)
      return false;
    return selectMap.containsKey(s.getSQL());
  }
  
  public void put(ISelect s,ABRowSet rs) {
    selectMap.put(s.getSQL(),rs);
  }

  public void remove(ISelect s) {
    selectMap.remove(s.getSQL());
  }
  
  public void put(ISelect s,ParentRow row) {
  	ABRowSet rs=new ABRowSet();
  	rs.add(row);
    put(s,rs);
  }
  
  public Object get(ISelect s) {
    return selectMap.get(s.getSQL());
  }

  public List<IRow> getList(ISelect s) {
    return selectMap.get(s.getSQL()).toList();
  }

  public List list(ISelect s) throws SQLException, Exception {
    List result=null;
    if(contain(s))
      result=getList(s);
    else {
    	ABRowSet rs=((AbstractSelect)s).toRowSet();
      put(s,rs);
      result=rs.toList();
    }
    return result;
  }

  public ABRowSet getRowSet(ISelect s) {
    return selectMap.get(s.getSQL());
  } 
  public IRowSet toRowSet(ISelect s) throws SQLException {
    ABRowSet result=null;
    if(contain(s))
      result=getRowSet(s);
    else {
    	if(s instanceof AbstractSelect)
				try{
					result=new ABRowSet(((AbstractSelect)s).toParentResultSet());
				}catch(Exception e){
					throw new SdkRuntimeException(e);
				}
			else 
    		result=new ABRowSet(s.toABResultSet());//notice: can't invoke s.toRowSet(), it will cause StackOverflowError. 
      put(s,result);
    }
    return result;
  }
  
  public void setConnection(Connection con){conn=con;}
  
  public Connection getConnection() {
    return conn;
  }
  
  public ParentRow getFirstRow(ISelect s) {
    List list=getList(s);
    if(list==null||list.size()==0)
      return null;
    return (ParentRow)list.get(0);
  }
}
