package com.esdk.test;

import java.util.ArrayList;
import java.util.Arrays;

import com.esdk.esdk;
import com.esdk.test.bean.Contact;
import com.esdk.utils.EasyArray;
import com.esdk.utils.EasyQuery;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyQuery.FieldExpression;
import com.esdk.utils.EasyQuery.FieldExpressions;

public class TestEasyQuery{
	public static void main(String[] args){
		FieldExpression exp=new FieldExpression("name=ffy");
		esdk.tool.assertEquals(exp.fieldName,"name");
		esdk.tool.assertEquals(exp.expression,"=");
		esdk.tool.assertEquals(exp.rightValue,"ffy");
		
		Contact contact1=new Contact();
		contact1.setId(100); 
		contact1.setMemberId("111"); 
		contact1.setName("ffy");
		contact1.setEmail("ffy@163.com");
		Contact contact2=new Contact();
		contact2.setId(200); 
		contact2.setMemberId("222");
		contact2.setName("ffy");
		contact2.setEmail("ffy@163.com");
		EasyQuery<Contact> easyQuery=new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("memberId=111");
		esdk.tool.assertEquals(easyQuery.size(),1);
		esdk.tool.assertEquals(easyQuery.first().getMemberId(),"111");
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("memberId=[111,222]").size(),2);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("memberId=111 || memberId=[111,222]").size(),2);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter(new FieldExpressions("memberId=111 && name^=ffy")).size(),1);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("name=[ffy,nina]").size(),2);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("id=[100,200]").size(),2);
		esdk.tool.assertEquals(new EasyQuery(new Contact[] {contact1,contact2}).distinct("name","email").size(),1);
		esdk.tool.assertEquals(new EasyQuery(new Contact[] {contact1,contact2}).distinct("name","memberId").size(),2);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).sort("name,memberId",true).first().getMemberId(),"222");
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).sort("name,memberId").first().getMemberId(),"111");
		Contact contact3=new Contact();
		contact3.setId(300);
		contact3.setMemberId("333");
		contact3.setName("nina");
		contact3.setEmail("nina@163.com");
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).add(contact3).sort("name,memberId").last().getMemberId(),"333");
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).sum("memberId"),333d);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2,contact3}).filter(new FieldExpression("id",FieldExpression.NOTEQ,100)).size(),2);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2,contact3}).filter(new FieldExpressions("id>100 && id<=300")).size(),2);
		EasyQuery<Contact> easyQuery1=EasyQuery.instanceOfShare(new ArrayList(Arrays.asList(new Contact[]{contact1,contact2,contact3}))).filter(new FieldExpressions("id=100 or (name=ffy and email$=@163.com)"));
		esdk.tool.assertEquals(easyQuery1.size(),2);
		esdk.tool.assertEquals(easyQuery1.first().getId().intValue(),100);
		esdk.tool.assertEquals(easyQuery1.last().getName(),"ffy");
		EasyQuery<Contact> easyQuery2=new EasyQuery<Contact>(new Contact[] {contact1,contact2,contact3}).filter(new FieldExpressions("id=100 || (name^=nina && email$=@163.com)"));
		esdk.tool.assertEquals(easyQuery2.size(),2);
		esdk.tool.assertEquals(easyQuery2.last().getName(),"nina");
		EasyQuery<Contact> easyQuery3=new EasyQuery<Contact>(new Contact[] {contact1,contact2,contact3});
		esdk.tool.assertEquals(easyQuery3.sub(0,1).size(),1);
		esdk.tool.assertEquals(easyQuery3.subsafe(10,11).size(),0);
		esdk.tool.assertEquals(easyQuery3.subsafe(-1,-2).size(),0);
		esdk.tool.assertEquals(easyQuery3.subsafe(2,4).size(),1);
		try{
			esdk.tool.assertEquals(easyQuery3.sub(0,5).size(),3);
		}catch(IndexOutOfBoundsException e){
			esdk.tool.assertEquals(e.getMessage(),"toIndex = 5");
		}
		EasyQuery easyQuery4=easyQuery3.sub(0,3);
		esdk.tool.assertEquals(EasyArray.toStr(easyQuery4.update("name","张三").getValues("name")),"张三,张三,张三");
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("memberId=111 || memberId=222").size(),2);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("mobile= && remark=").size(),0);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("mobile=null && remark=null").size(),2);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("mobile=null || remark=null",1).size(),1);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("memberId=111 || memberId=222",1).size(),1);
		contact2.setEmail(null);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).filter("email",null).size(),1);
		contact1.setMarried(true);
		contact2.setMarried(false);
		esdk.tool.assertEquals(new EasyQuery<Contact>(new Contact[] {contact1,contact2}).sort("married",true).get(0),contact1);

		contact1.setName("李一");
		contact2.setName("李二");
		contact3.setName("李三");
		esdk.tool.assertEquals(esdk.str.valueOf(new EasyQuery<Contact>(new Contact[] {contact2,contact1,contact3}).sort("name").getValues("name")),"李一,李二,李三");
		esdk.tool.assertEquals(esdk.str.valueOf(new EasyQuery<Contact>(new Contact[] {contact1,contact3,contact2}).sort("name",true).getValues("name")),"李三,李二,李一");
		
		esdk.tool.printAssertInfo();
	}
	
	
}