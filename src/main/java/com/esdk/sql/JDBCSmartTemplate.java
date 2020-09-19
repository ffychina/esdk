package com.esdk.sql;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.esdk.esdk;
import com.esdk.utils.ParseException;
import com.esdk.utils.EasyObj;

/**用更强大的模板代替?号的普通sql模板*/
public class JDBCSmartTemplate extends JDBCTemplate{

	/**
	 * 不用加单引号，参数命名方式：fieldname={key} and create_time={date} and center_id
	 * in({ids})
	 */
	public JDBCSmartTemplate(Connection conn,String sql,Map params){
		super(esdk.str.format(sql,SQLAssistant.getJDBCTemplateParams(params)),conn);
	}

	public JDBCSmartTemplate(Connection conn,String sql,Object...params){
		super(conn,sql);
		if(sql.matches(".*?\\{\\d\\}.*?")){
			for(int i=0;i<params.length;i++){
				params[i]=SQLAssistant.getJDBCTemplateParams(params[i]);
			}
			setSql(esdk.str.format(sql,params));
		}
	}

	public static void main(String[] args){
		HashMap params=new HashMap();
		params.put("id",1);
		params.put("name","陈大民");
		params.put("codes",new String[]{"C003","A001","B002"});
		params.put("createTime",esdk.time.toDate("2015-08-01 23:59:59"));
		params.put("tablename","sys_user");
		JDBCSmartTemplate jt1=new JDBCSmartTemplate(null,
				"select * from sys_user where name=${name} and id=${id} and create_time=${createTime} and value=1 and code in(${codes}) order by id desc"
				,params);
		esdk.tool.assertEquals(jt1.getSQL(),"select * from sys_user where name='陈大民' and id=1 and create_time='2015-08-01 23:59:59' and value=1 and code in('C003','A001','B002') order by id desc");
		JDBCSmartTemplate jt2=new JDBCSmartTemplate(null,
				"select * from sys_user where name={0} and id={1} and create_time={2} and value=1 and code in({3}) order by id desc"
				,"陈大民",1,esdk.time.toDate("2015-08-01 23:59:59"),new String[]{"C003","A001","B002"});
		esdk.tool.assertEquals(jt2.getSQL(),"select * from sys_user where name='陈大民' and id=1 and create_time='2015-08-01 23:59:59' and value=1 and code in('C003','A001','B002') order by id desc");
	}
}
