package com.esdk.sql;

import com.esdk.sql.orm.AbstractSelect;
import com.esdk.utils.EasyReflect;
import com.esdk.utils.EasyStr;

public class JoinConfig {
	public static final String INNERJOIN=Table.INNERJOIN,LEFTJOIN=Table.LEFTJOIN,RIGHTJOIN=Table.RIGHTJOIN,FULLJOIN=Table.FULLJOIN;
	
	public JoinConfig on(AbstractSelect primaryKeySelect,AbstractSelect foreignKeySelect) {
		String primaryKeyName=(String)EasyReflect.getFieldValue(primaryKeySelect.getMetaData(),"PrimaryKey");
		String foreignKeyName=(String)EasyReflect.getFieldValue(foreignKeySelect.getMetaData(),EasyStr.toCamelCase(primaryKeyName,true));
		primaryKeySelect.addOnCondition(primaryKeySelect.createField(primaryKeyName),foreignKeySelect.createField(foreignKeyName));
		return this;
	}
	
	public JoinConfig on(AbstractSelect primaryKeySelect,AbstractSelect foreignKeySelect,String joinType) {
		on(primaryKeySelect,foreignKeySelect);
		primaryKeySelect.getTable().setRelationShip(joinType);
		return this;
	}
	
	public JoinConfig onPrimaryKey(AbstractSelect as1,AbstractSelect as2) {
		try {
			String pkn2=(String)EasyReflect.getFieldValue(as2.getMetaData(),"PrimaryKey");
			java.lang.reflect.Field f1=EasyReflect.findField(as2.getMetaData().getClass(),EasyStr.toCamelCase(pkn2,true),false);
			if(f1!=null) {
				as1.addOnCondition(as1.createField((String)f1.get(as1.getMetaData())),as2.createField(pkn2));	
			}
			else {
				String pkn1=(String)EasyReflect.getFieldValue(as1.getMetaData(),"PrimaryKey");
				java.lang.reflect.Field f2=EasyReflect.findField(as2.getMetaData().getClass(),EasyStr.toCamelCase(pkn1,true),false);
				if(f2!=null) {
					as2.addOnCondition(as2.createField((String)f2.get(as2.getMetaData())),as1.createField(pkn1));	
				}
			}
			return this;
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JoinConfig on(Select joinSelect,ILogic logic) {
		joinSelect.addOnCondition(logic);
		return this;
	}
	
	public JoinConfig on(Select joinSelect,ILogic logic,String joinType) {
		joinSelect.addOnCondition(logic);
		joinSelect.getTable().setRelationShip(joinType);
		return this;
	}
	
	public JoinConfig on(Select joinSelect,Field f1,Field f2) {
		joinSelect.addOnCondition(f1,f2);
		return this;
	}

	public JoinConfig on(Select joinSelect,Field f1,Field f2,String joinType) {
		joinSelect.addOnCondition(f1,f2);
		joinSelect.getTable().setRelationShip(joinType);
		return this;
	}
	
	public JoinConfig on(AbstractSelect joinSelect,Field f1,Field f2) {
		joinSelect.addOnCondition(f1,f2);
		return this;
	}
	
	public JoinConfig on(AbstractSelect joinSelect,Field f1,Field f2,String joinType) {
		joinSelect.addOnCondition(f1,f2);
		joinSelect.getTable().setRelationShip(joinType);
		return this;
	}
}
