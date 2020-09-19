package com.esdk;

import java.util.List;

import com.esdk.utils.Constant;
import com.esdk.utils.EasyArray;
import com.esdk.utils.EasyExcel;
import com.esdk.utils.EasyFile;
import com.esdk.utils.EasyHttp;
import com.esdk.utils.EasyJson;
import com.esdk.utils.EasyMap;
import com.esdk.utils.EasyMath;
import com.esdk.utils.EasyNet;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyPinyin;
import com.esdk.utils.EasyProp;
import com.esdk.utils.EasyQuery;
import com.esdk.utils.EasyReflect;
import com.esdk.utils.EasySql;
import com.esdk.utils.EasyStr;
import com.esdk.utils.EasyTime;
import com.esdk.utils.EasyTool;
import com.esdk.utils.EasyRegex;
import com.esdk.utils.EasyObj;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

public class esdk{
	public static final EasyProp prop=new EasyProp("sdk.properties","project.properties","application.properties");
	public static final EasyFile file=new EasyFile();
	public static final EasyStr str=new EasyStr();
	public static final EasyMath math=new EasyMath();
	public static final EasyTime time=new EasyTime();
	public static final EasyExcel excel=new EasyExcel();
	public static final EasyReflect reflect=new EasyReflect();
	public static final EasySql sql=new EasySql();
	public static final EasyTool tool=new EasyTool();
	public static final EasyObj obj=new EasyObj();
	public static final EasyArray array=new EasyArray();
	public static final EasyMap map=new EasyMap();
	public static final EasyRegex regex=new EasyRegex();
	public static final EasyJson json=new EasyJson();
	public static final Constant cnst=new Constant();
	public static final EasyHttp http=new EasyHttp();
	public static final EasyPinyin pinyin=new EasyPinyin();
	public static final EasyNet net=new EasyNet();
	public static final Log log=LogFactory.get();

	public static void println(Object v){
		System.out.println(v);
	}

	public static EasyQuery getEasyQuery(){
		return new EasyQuery();
	}

	public static <T> EasyQuery<T> getEasyQuery(List<T> list){
		return new EasyQuery<T>((List)list);
	}

	public static void main(String[] args){
		esdk.tool.assertEquals(prop.getBoolean("IsShowSql"),"true");
		esdk.tool.assertEquals(prop.getString("language"),"tw");
		esdk.tool.assertEquals(prop.getString("testempty"),null);
		esdk.println(esdk.math.max(1,3));
		esdk.println(esdk.obj.or(null,3));
		esdk.println(esdk.str.ifnull(null,"abc"));
		esdk.tool.assertEquals(esdk.map.createMap("{name=李元富,sex=男}").get("name"),"李元富");
		esdk.tool.assertEquals(esdk.map.createMap("name","张三","code","zhang","sex","男","age",18).toString(),"{name=张三, code=zhang, sex=男, age=18}");
	}

}

