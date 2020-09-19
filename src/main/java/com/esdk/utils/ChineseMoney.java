package com.esdk.utils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
class ChineseMoney{
  private String number[]={"","壹","贰","叁","肆","伍","陆","柒","捌","玖"};
  private String unit[]={"元","拾","佰","仟","万","拾","佰","仟","亿","拾","佰"};
  private String small[]={"角","分"};
  private String strNumber,strUnit,strAll;

  private String onlyInt(int intInt){
    String strInt;
    strInt=String.valueOf(intInt);
    strNumber="";
    strUnit="";
    strAll="";
    int l=strInt.length();
    int j,k,zeorCount;
    zeorCount=0;
    for(k=0;k<l;k++){
      String strTemp=strInt.substring(k,k+1);
      int intTemp=Integer.parseInt(strTemp);
      strNumber=number[intTemp];
      j=l-1-k;
      strUnit=unit[j];
      if(intTemp==0){
        if(zeorCount==0){
          strUnit=strUnit.replace('拾','零');
          strUnit=strUnit.replace('佰','零');
          strUnit=strUnit.replace('仟','零');
          strUnit=strUnit.replace('万','零');
        }
        else{
          strUnit=strUnit.replaceAll("拾","");
          strUnit=strUnit.replaceAll("佰","");
          strUnit=strUnit.replaceAll("仟","");
          strUnit=strUnit.replaceAll("万","");
        }
        zeorCount++;
      }
      strAll+=strNumber+strUnit;
    }
    return strAll;

  }

  private String onlySmall(int intSmall){

    strNumber="";
    strUnit="";
    strAll="";
    String strSmall,strTemp;
    strSmall=String.valueOf(intSmall);
    int i;
    if(intSmall>=10){
      for(i=0;i<strSmall.length();i++){
        strTemp=String.valueOf(intSmall).substring(i,i+1);
        if(Integer.parseInt(strTemp)!=0){
          strNumber=number[Integer.parseInt(strTemp)];
          strUnit=small[i];
          strAll+=strNumber+strUnit;
        }
      }
    }
    else{
      if(intSmall!=0){
        strNumber=number[intSmall];
        strUnit=small[1];
        strAll+=strNumber+strUnit;
      }
    }

    return strAll;
  }

  public String getChineseMoney(double number){
    //四舍五入
    number=(number*100+0.5)/100;

    String strAll,strChineseInt,strChineseSmall,strZheng;
    ;
    int intInt,intSmall;
    strChineseInt="";
    strChineseSmall="";
    strZheng="";

    //整数部分
    intInt=(int)(number*100/100);
    if(intInt!=0){
      strChineseInt=onlyInt(intInt);
    }
    //小数部分
    double temp=(number-intInt)*100*100/100;
    //对小数部分四舍五入
    intSmall=(int)(temp*100+0.5)/100;
    if(intSmall!=0){
      strChineseSmall=onlySmall(intSmall);
    }
    else{
      strZheng="整";
    }
    strAll=strChineseInt+strChineseSmall+strZheng;
    return strAll;
  }
  
  public static void main(String args[]) throws IOException{
    ChineseMoney cm=new ChineseMoney();
    double money;
    String strMoney="",strChineseMoney;
    //读取
    System.out.println("输入货币(四舍五入):");
    BufferedReader cin=new BufferedReader(new InputStreamReader(System.in));
    strMoney=cin.readLine();
    money=Double.parseDouble(strMoney);
    strChineseMoney=cm.getChineseMoney(money);
    System.out.println(strChineseMoney);
  }
}
