
package com.esdk.utils;

import java.util.Map;

import com.esdk.esdk;
import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class EasyPinyin{
		private static Map<String,String> MultiPinyinSurnameMap=esdk.map.strToMap("{盖=ge,曾=zeng,区=ou,单=chan,重=chong,行=hang,芃=peng,仇=qiu,解=xie,朴=piao,翟=zhai,查=zha,朴=piao}");
	/**
   * 转换为有声调的拼音字符串
   * @param pinYinStr 汉字
   * @return 有声调的拼音字符串
   */
	public static String getMarkPinYin(String pinYinStr){
		String tempStr=null;
		try{
			tempStr=PinyinHelper.convertToPinyinString(pinYinStr," ",PinyinFormat.WITH_TONE_MARK);
		}catch(Exception e){
			e.printStackTrace();
		}
		return tempStr;
	}

  /**
   * 转换为数字声调字符串
   * @param pinYinStr 需转换的汉字
   * @return 转换完成的拼音字符串
   */
	public static String getNumberPinYin(String pinYinStr){
		String tempStr=null;
		try{
			tempStr=PinyinHelper.convertToPinyinString(pinYinStr," ",PinyinFormat.WITH_TONE_NUMBER);
		}catch(Exception e){
			e.printStackTrace();
		}
		return tempStr;
	}

  /**
   * 转换为不带音调的拼音字符串
   * @param pinYinStr 需转换的汉字
   * @return 拼音字符串
   */
	public static String getTonePinYin(String pinYinStr,String delimter){
		String tempStr=null;
		try{
			tempStr=PinyinHelper.convertToPinyinString(pinYinStr,delimter,PinyinFormat.WITHOUT_TONE);
		}catch(Exception e){
			e.printStackTrace();
		}
		return tempStr;
	}

  /**
   * 转换为不带音调的拼音姓名，能正常识别多音字姓，如重曾区单芃重等等。
   * @param chinese 需转换的汉字
   * @return 拼音字符串
   */
	public static String getNamePinYin(String chinese,String delimter){
		StringBuilder result=new StringBuilder("");
		if(esdk.str.isValid(chinese)){
			String first=chinese.charAt(0)+"";
			String name=chinese.substring(1);
			try{
				if(!MultiPinyinSurnameMap.containsKey(first))
					result.append(PinyinHelper.convertToPinyinString(chinese,String.valueOf(delimter),PinyinFormat.WITHOUT_TONE));
				else{
					result.append(MultiPinyinSurnameMap.get(first)).append(delimter);
					result.append(PinyinHelper.convertToPinyinString(name,String.valueOf(delimter),PinyinFormat.WITHOUT_TONE));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result.toString();
	}
	
  /**
   * 转换为每个汉字对应拼音首字母字符串
   * @param pinYinStr 需转换的汉字
   * @return 拼音字符串
   */
	public static String getShortPinyin(String pinYinStr){
		String tempStr=null;
		try{
			tempStr=PinyinHelper.getShortPinyin(pinYinStr);
		}catch(Exception e){
			e.printStackTrace();
		}
		return tempStr;
	}

  /**
   * 检查汉字是否为多音字
   * @param chineseChar 需检查的汉字
   * @return true 多音字，false 不是多音字
   */
	public static boolean hasMultiPinyin(char chineseChar){
		boolean check=false;
		try{
			check=PinyinHelper.hasMultiPinyin(chineseChar);
		}catch(Exception e){
			e.printStackTrace();
		}
		return check;
	}
  /**
   * 检查汉字是否为多音字
   * @param pinYinStr 需检查的汉字
   * @return true 多音字，false 不是多音字
   */
	public static boolean hasMultiPinyin(String pinYinStr){
		boolean check=false;
		try{
			for(char item:pinYinStr.toCharArray()) {
				check=check||PinyinHelper.hasMultiPinyin(item);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return check;
	}

  /**
   * 简体转换为繁体
   * @param pinYinStr
   * @return
   */
	public static String getTraditional(String pinYinStr){
		String tempStr=null;
		try{
			tempStr=ChineseHelper.convertToTraditionalChinese(pinYinStr);
		}catch(Exception e){
			e.printStackTrace();
		}
		return tempStr;
	}

  /**
   * 繁体转换为简体
   * @param pinYinSt
   * @return
   */
	public static String getSimplified(String pinYinSt){
		String tempStr=null;
		try{
			tempStr=ChineseHelper.convertToSimplifiedChinese(pinYinSt);
		}catch(Exception e){
			e.printStackTrace();
		}
		return tempStr;
	}

public static void main(String[] args) {
  String str = "盖曾区单重行芃仇解朴翟查朴";//多音字
  String str1 = "曾雪";//多音字
  String str2 = "盖章";//多音字
  String str3 = "芃查朴";//多音字
  esdk.tool.assertEquals(getNamePinYin(str1," "),"zeng xue");
  esdk.tool.assertEquals(getNamePinYin(str2," "),"ge zhang");
  esdk.tool.assertEquals(getNamePinYin(str3," "),"peng cha pu");
  esdk.tool.assertEquals(getTonePinYin(str," "),"gai ceng qu dan zhong xing peng chou jie pu di cha pu");
  esdk.tool.assertEquals(getNumberPinYin(str),"gai4 ceng2 qu1 dan1 zhong4 xing2 peng2 chou2 jie3 pu3 di2 cha2 pu3");
  esdk.tool.assertEquals(getMarkPinYin(str),"gài céng qū dān zhòng xíng péng chóu jiě pǔ dí chá pǔ");
  esdk.tool.assertEquals(getShortPinyin(str),"gcqdzxpcjpdcp");
  esdk.tool.assertEquals(hasMultiPinyin('重'));
  esdk.tool.assertEquals(hasMultiPinyin(str));
  esdk.tool.assertEquals(getNamePinYin("药理学（张岫美）","_"),"yao_li_xue_（_zhang_xiu_mei_）");
}
}
