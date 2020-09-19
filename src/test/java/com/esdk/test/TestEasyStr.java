package com.esdk.test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.TreeMap;

import com.esdk.esdk;
import com.esdk.utils.CharAppender;
import com.esdk.utils.EasyObj;
import com.esdk.utils.EasyStr;

public class TestEasyStr{
	private static void test() throws Exception{
		esdk.tool.assertEquals(esdk.str.padLeft("1",5,'0'),"00001");
		esdk.tool.assertEquals(esdk.str.padRight("002",5,' '),"002  ");
		esdk.tool.assertEquals(EasyStr.valueOf(EasyStr.distinct("A,B,C".split(","),"B".split(","))),"A,B,C");
		esdk.tool.assertEquals(EasyStr.valueOf(EasyStr.unique("A,B,C".split(","),"B".split(","))),"A,B,C");
		esdk.tool.assertEquals(EasyStr.arrToStr(EasyStr.toArray(new String[]{"A","B","C"},"D".split(","),"E"),"|"),"A|B|C|D|E");
		esdk.tool.assertEquals(EasyStr.arrToStr(EasyStr.toArray(new String[]{})),"");
		esdk.tool.assertEquals(EasyStr.unistr(',',"A,B,C","B"),"A,B,C");
		esdk.tool.assertEquals(EasyStr.format("I am {}, I like {}","pgl","China"),"I am pgl, I like China");
		esdk.tool.assertEquals(EasyStr.format("I am {0}, I like {1}","pgl","China"),"I am pgl, I like China");
		esdk.tool.assertEquals(EasyStr.serial("6",3),"007");
		esdk.tool.assertEquals(EasyStr.serial("1001",3),"1002");
		esdk.tool.assertEquals(EasyStr.serial("filename",3),"filename001");
		esdk.tool.assertEquals(EasyStr.serial("filename001",3),"filename002");
		esdk.tool.assertEquals(EasyStr.compareTo("第1章 创造性思维理论（二十七）","第10章 创造性思维理论（十九）"),-1);
		esdk.tool.assertEquals(EasyStr.compareTo("第17章 创造性思维理论（十七）","第9章 创造性思维理论（九）"),1);
		esdk.tool.assertEquals(EasyStr.replaceChineseNum("创造性思维理论（二十七）"),"创造性思维理论（027）");
		esdk.tool.assertEquals(EasyStr.replaceChineseNum("创造性思维理论（二十）"),"创造性思维理论（020）");
		esdk.tool.assertEquals(EasyStr.replaceChineseNum("创造性思维理论（十）"),"创造性思维理论（010）");
		System.out.println("Aa".hashCode()+","+"BB".hashCode());
		System.out.println(3&3);
		TreeMap map=new TreeMap();
		map.put("Aa",111);
		map.put("BB",222);
		map.put("cC",333);
		esdk.tool.assertEquals(map.subMap("AA","cc").get("BB"),222);
		System.out.println(map.get("Aa"));
		System.out.println(map.get("BB"));
		System.out.println("abcdefghijklmn123456".hashCode());
		System.out.println(EasyObj.hashCode("abcdefghijklmn123456"));
		esdk.tool.assertEquals(EasyStr.ellipse("Len天下太平",10,"utf8"),"Len天...");
		esdk.tool.assertEquals(EasyStr.deleteArray(new String[]{"1","2","3","4","5"},1,3),new String[]{"1","5"});
		esdk.tool.assertEquals(EasyStr.getSubString("fanfeiyu",0,30),"fanfeiyu");
		esdk.tool.assertEquals(EasyStr.arrToCsv(EasyStr.bubbleSortStringArray(EasyStr.fromCsv("e,f,g\nc,d,e\nr,t,v"),new int[]{1,2,3}),","),
				"c,d,e\r\ne,f,g\r\nr,t,v");
		esdk.tool.assertEquals(EasyStr.getSubString("fanfeiyu",1,3),"an");
		esdk.tool.assertEquals(EasyStr.serial("123"),"124");
		esdk.tool.assertEquals(EasyStr.serial("ab1"),"ab2");
		esdk.tool.assertEquals(EasyStr.serial("ab"),"ab1");
		esdk.tool.assertEquals(EasyStr.serial("001"),"002");
		esdk.tool.assertEquals(EasyStr.serial("2001-001"),"2001-002");
		esdk.tool.assertEquals(EasyStr.serial(""),"1");
		esdk.tool.assertEquals(EasyStr.serial(null),"1");
		esdk.tool.assertEquals(EasyStr.quote("a\nb\""),"\"a\\nb\\\"\"");
		esdk.tool.assertEquals(EasyStr.getSubASCIILength("",0,4),"");
		String encoding=System.getProperty("file.encoding");
		if(encoding.equals("gbk")){ // TODO gbk encoding problem.
			esdk.tool.assertEquals(EasyStr.getSubASCIILength("A大家",0,4),"A大");
			esdk.tool.assertEquals(EasyStr.getSubASCIILength("A工人们",0,4),"A工");
			esdk.tool.assertEquals(EasyStr.getSubASCIILength("ABC大家好",0,7),"ABC大家");
			esdk.tool.assertEquals(EasyStr.getSubASCIILength("ABC大家好",0,8),"ABC大家");
			esdk.tool.assertEquals(new CharAppender('|').add(EasyStr.getStringsByLength("天空abcd爱MMA是B??",4)).toString(),
					"天空|abcd|爱MM|A是B|??");
			esdk.tool.assertEquals(EasyStr.ellipsis("中华人民共和国",8),"中华...");
		}
		esdk.tool.assertEquals(EasyStr.BigDecimalToString(new BigDecimal(125.555),7,6),"125.555");
		esdk.tool.assertEquals(EasyStr.DoubleToString(12.025,5),"12.025");
		esdk.tool.assertEquals(EasyStr.IntToString(12,50),"00000000000000000000000000000000000000000000000012");
		esdk.tool.assertEquals(EasyStr.getISN("运照".toCharArray()[0],"gb2312"),54475);
		esdk.tool.assertEquals(EasyStr.compareTo("照","运"),-7785);
		esdk.tool.assertEquals(EasyStr.StrInverse("为三星抛头驴,洒热雪"),"雪热洒,驴头抛星三为");
		esdk.tool.assertEquals(EasyStr.ConvertChinesePunctuation("MWO-GRILL(0.7CU.FT)\";SSEC,"),"MWO-GRILL(0.7CU.FT)＂;SSEC，");
		String text="fanfeiyu";
		esdk.tool.assertEquals(EasyStr.left(text,10),"fanfeiyu");
		esdk.tool.assertEquals(EasyStr.mid(text,-2,8),"fanfeiyu");
		esdk.tool.assertEquals(EasyStr.right(text,9),"fanfeiyu");
		esdk.tool.assertEquals(EasyStr.ConvertUTF8("a范! b"),"a%E8%8C%83! b");
		esdk.tool.assertEquals("a^a".replaceFirst("^^","a"),"aa^a");
		esdk.tool.assertEquals(EasyStr.arrToStr(EasyStr.SortArray(new String[]{"d","c","b","a"})),"a,b,c,d");
		esdk.tool.assertEquals(EasyStr.indexOf(new String[]{"ab","cd"},"CD")==1);
		String abc="abc";
		esdk.tool.assertEquals(abc.replace('d','e')==abc&&abc.trim()==abc);
		esdk.tool.assertEquals("\na\n\n".split(",").length,1);
		esdk.tool.assertEquals(EasyStr.increase("sms2base998",3),"sms2base1001");
		
		esdk.tool.assertEquals(esdk.str.isIncludeChinese("AB,.，范"));
		esdk.tool.assertEquals(EasyStr.isIncludeChinese("a卡"));
		esdk.tool.assertEquals(!EasyStr.isIncludeChinese(null));
		esdk.tool.assertEquals(!EasyStr.isIncludeChinese("abc"));
		esdk.tool.assertEquals(EasyStr.ellipsis("abcdefg",5),"ab...");
		esdk.tool.assertEquals(EasyStr.ellipsis("abcdefg",7),"abcdefg");
		esdk.tool.assertEquals(
				EasyStr.format("${java.io.tempdir}logs\\sql.log",
						esdk.map.strToMap("{java.io.tempdir=C:\\Users\\admin\\AppData\\Local\\Temp\\}")),
				"C:\\Users\\admin\\AppData\\Local\\Temp\\logs\\sql.log");
		esdk.tool.assertEquals(EasyStr.format("I am ${a.name}, I like ${a.country}",esdk.map.strToMap("{a.name=pgl,a.country=China}")),
				"I am pgl, I like China");
		esdk.tool.assertEquals(EasyStr.format("I am ${chinese.name}, I like ${country.name}",
				esdk.map.strToMap("{chinese.name=pgl,country.name=China}")),"I am pgl, I like China");
		esdk.tool.assertEquals(EasyStr.toCamelCase("brushTime",true).equals("BrushTime"));
		esdk.tool.assertEquals(EasyStr.upperFirst("brushTime").equals("BrushTime"));
		esdk.tool.assertEquals(EasyStr.lowerFirst("BrushTime").equals("brushTime"));
		esdk.tool.assertEquals(EasyStr.toCamelCase("BrushTime").equals("brushTime"));
		esdk.tool.assertEquals(EasyStr.remove(new String[]{"a","b","c"},"a","c"),new String[]{"b"});
		HashSet set1=new HashSet();
		set1.add(1);
		set1.add(2);
		set1.add(3);
		HashSet subSet=new HashSet();
		subSet.add(1);
		subSet.add(3);
		esdk.tool.assertEquals(set1.containsAll(subSet));
		esdk.tool.assertEquals(EasyStr.decode(EasyStr.encode("aha801801")),"aha801801");
		String decodestr="我们abc.";
		esdk.tool.assertEquals(EasyStr.encode(decodestr),"5oiR5LusYWJjLg==");
		esdk.tool.assertEquals(EasyStr.encodeUnicode(decodestr),"\\u6211\\u4eecabc.");
		esdk.tool.assertEquals(EasyStr.decode(EasyStr.encode(decodestr)),decodestr);
		esdk.tool.assertEquals(EasyStr.decodeUnicode(EasyStr.encodeUnicode(decodestr)),decodestr);
		esdk.tool.assertEquals(EasyStr.or("",null,"abc"),"abc");
		esdk.tool.assertEquals(EasyObj.or("",null,"abc"),"abc");
		esdk.tool.assertEquals(EasyStr.ellipse("A机票预定页面",10),"A机票预...");
		esdk.tool.assertEquals(EasyStr.ellipse("AB机票预定页面",10),"AB机票...");
		esdk.tool.assertEquals(EasyStr.ellipse("ABC机票预定页面",10),"ABC机票...");
		esdk.tool.assertEquals(EasyStr.ellipse("机票预定页面",10),"机票预定...");
		esdk.tool.assertEquals(EasyStr.ellipse("ABC机票",10),"ABC机票");
		esdk.tool.assertEquals(EasyStr.ellipse("机票预定页",10),"机票预定页");
		esdk.tool.assertEquals(EasyStr.split("学员登录名	学员姓名	性别	身份证号码").length,4);
		esdk.tool.assertEquals(EasyStr.existOr("abcd","a,b,e".split(",")));
		esdk.tool.assertEquals(EasyStr.existAnd("abcd","a,b,e".split(",")),false);
		esdk.tool.assertEquals(EasyStr.toUnderlineCase("createUserId"),"create_user_id");
		esdk.tool.assertEquals(EasyStr.toCamelCase("create_user_id"),"createUserId");
		esdk.tool.assertEquals(esdk.str.isEnglish('A'));
		esdk.tool.assertEquals(!esdk.str.isEnglish('9'));
		esdk.tool.assertEquals(!esdk.str.isEnglish('#'));
		esdk.tool.assertEquals(!esdk.str.isEnglish('范'));
		esdk.tool.assertEquals(esdk.str.isNumeric('9'));

		esdk.tool.printAssertInfo();
	}

	private static void testRegex(){
		String[] s=EasyStr.split("ab，。／/\\、d12/3456",new String[]{"\\.",",","/","\\\\","，","。","／"});
		esdk.tool.assertEquals(s[0],"ab，。／");
		esdk.tool.assertEquals(s[1],"\\、d12");
		esdk.tool.assertEquals(s[2],"3456");
		String[] s2=EasyStr.split("",new String[]{"\\.",",","/","\\\\","，","。","／"});
		esdk.tool.assertEquals(s2.length,0);
		String[] s3=EasyStr.split(null,new String[]{"\\.",",","/","\\\\","，","。","／"});
		esdk.tool.assertEquals(s3.length,0);
		esdk.tool.assertEquals(EasyStr.splitFirst("",new String[]{"\\.",",","/","\\\\","，","。","／"}),"");
		esdk.tool.assertEquals(EasyStr.splitFirst(null,new String[]{"\\.",",","/","\\\\","，","。","／"}),null);
		esdk.tool.assertEquals(EasyStr.splitFirst("a\\b\\c",new String[]{"\\.",",","/","\\\\","，","。","／"}),"a");
	}

	public static void main(String[] args) throws Exception{
		test();
		testRegex();
	}
}
