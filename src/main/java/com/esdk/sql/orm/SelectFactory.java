package com.esdk.sql.orm;

import java.sql.Connection;

public class SelectFactory{
	private Connection conn;
	private ORMSession ormSession;
	
	public SelectFactory(Connection conn){
		this.conn=conn;
	}
	
	public SelectFactory(ORMSession session) {
		this.ormSession=session;
	}
	
	public ORMSession getORMSession() {
		return this.ormSession;
	}
	
	public void setORMSession(ORMSession ormSession2){
		this.ormSession=ormSession2;
	}

	
  public <T extends AbstractSelect> T createSelect(Class<T> selectClass) throws Exception{
    T result=null;
    if(ormSession!=null) {
      java.lang.reflect.Constructor constructor = selectClass.getConstructor(new Class[] {ORMSession.class});
    	result=(T)constructor.newInstance(new Object[] {ormSession});
    }
    else {
  	 java.lang.reflect.Constructor constructor = selectClass.getConstructor(new Class[] {Connection.class});
    	result=(T)constructor.newInstance(new Object[] {conn});
    }
    return result;
  }
  
  public <T extends ParentRow> T createRow(Class<T> parentRowClass) throws Exception{
    T row=(T)parentRowClass.newInstance();
    row.setConnection(conn);
    if(ormSession!=null)
    	row.setSession(ormSession);
    return row;
  }
  
  public <T extends ParentRow> T createRow(Class<T> parentRowClass,Object pkid) throws Exception{
    T row=(T)parentRowClass.newInstance();
    row.setConnection(conn);
    if(ormSession!=null)
    	row.setSession(ormSession);
    row.setPrimaryKey(pkid);
    row.refresh();
    return row;
  }
  
  public <T extends ParentRow> T createRow(Class<T> parentRowClass,Object pkid,int cacheSec) throws Exception{
    T row=(T)parentRowClass.newInstance();
    row.setConnection(conn);
    if(ormSession!=null)
    	row.setSession(ormSession);
    row.useCache(cacheSec);
    row.setPrimaryKey(pkid);
    row.refresh();
    return row;
  }
	
	@Override
	public void finalize() throws Throwable{
		if(ormSession!=null)
			ORMSessionFactory.removeORMSession(ormSession);
		super.finalize();
	}
}
