package com.esdk.utils;


public class TestHelper{
  public static int errCount=0,successCount=0;
  public static boolean isEqual(Object o1,Object o2) {
    return EasyObj.equal(o1,o2);
  }
  
  public static void printAssertInfo() {
    if(errCount==0)
      System.out.println("全部测试通过，成功"+successCount+"次。");
    else
      System.err.println("测试出错次数："+errCount+"。");
  }
  
  public static int getErrCount() {
    return errCount;
  }
  
  public static boolean assertEquals(boolean v) {
    return assertEquals(Boolean.valueOf(v),Boolean.TRUE);
  }
  
  public static boolean assertEquals(double actualValue,double expectValue) {
    return assertEquals(new Double(actualValue),new Double(expectValue));
  }

  public static boolean assertEquals(int actualValue,int expectValue) {
    return assertEquals(new Integer(actualValue),new Integer(expectValue));
  }
  
  public static boolean assertEquals(String actualValue,String expectValue) {
    if(actualValue==null)actualValue="";
    if(expectValue==null)expectValue="";
    return assertEquals(new TString(actualValue),new TString(expectValue));
  }
  
  public static boolean assertEqualsIgnoreCase(String actualValue,String expectValue) {
    return assertEquals(new VString(new TString(actualValue)),new VString(new TString(expectValue)));
  }
  
  public static boolean assertEquals(Object actualValue,Object expectValue){
    boolean result=false;
    try{
      if(actualValue==expectValue)
        result=true;
      else if(actualValue!=null&&expectValue!=null){
        if(actualValue.getClass()==expectValue.getClass()){
          if(expectValue instanceof TString){
            expectValue=expectValue.toString();
            actualValue=actualValue.toString();
          }
          if(expectValue instanceof String)
            expectValue=replaceWildcard((String)actualValue,(String)expectValue);
          if(expectValue instanceof VString)
            expectValue=replaceWildcard((VString)actualValue,(VString)expectValue);
          if(expectValue instanceof CharSequence[]){
            expectValue=new CharAppender().append((CharSequence[])expectValue);
            actualValue=new CharAppender().append((CharSequence[])actualValue);
          }
          result=actualValue.equals(expectValue);
        }
        else{
          throw new AssertException("ClassCastException: ActualClass:"+actualValue.getClass().getName()+"<------>"+"ExpectClass:"+expectValue.getClass().getName());
        }
      }
      if(result) {
        System.out.println(result);
        successCount+=1;
      }
      else{
        throw new AssertException("ActualValue:"+actualValue+"<------>"+"ExpectValue:"+expectValue);
      }
      return result;
    }
    catch(AssertException e){
      errCount+=1;
      e.printStackTrace();
      result=false;
      return result;
    }
  }

  public static boolean wildCardMatch(VString actualValue,VString expectValue) {
    return wildCardMatch(actualValue.toUpperCase(),expectValue.toUpperCase());
  }

  public static boolean wildCardMatch(String actualValue,String expectValue) {
    if(expectValue.indexOf('*')<0&&expectValue.indexOf('?')<0)
      return actualValue.compareTo(expectValue)==0;
    int start=0;
    expectValue=EasyStr.ReplaceAll(expectValue,"*?","*",false,true);
    StringBuffer segment=new StringBuffer();
    for(int i=0,offset=0;i<expectValue.length();i++,offset++){
      char c=expectValue.charAt(i);
      if(c!='*') {
        if(c=='?')
          c=actualValue.charAt(start+offset);    
      segment.append(c);
      }
      if((c=='*'&&segment.length()>0)||i==expectValue.length()-1) {
        int end=actualValue.indexOf(segment.toString(),start);
        if(end>=0) {
          start=end+segment.length();
        }
        else {
          return false;          
        }
        segment.delete(0,segment.length());
        offset=-1;
      }
    }
    return true;
  }
  
  public static VString replaceWildcard(VString actualValue,VString expectValue) {
    if(wildCardMatch(actualValue,expectValue))
      return actualValue;
    else
      return expectValue;
  }

  public static String replaceWildcard(String actualValue,String expectValue) {
    if(wildCardMatch(actualValue,expectValue))
      return actualValue;
    else
      return expectValue;
  }

  public static void main(String[] args){
    assertEquals(wildCardMatch("abcd","a?cd"));
    assertEquals(wildCardMatch("!can not sign because task[id=14] has not completed.","!can not sign because task[id=??] has not completed."));
    assertEquals(wildCardMatch("aabbccdd","*a*bb*cd*d*"));
    assertEquals(!wildCardMatch("aabbccdd","a*a*bb*cd*d*d"));
    assertEquals(wildCardMatch("aabbccddee","a*a*b?*?d*d??"));
    assertEquals(!replaceWildcard("1234567890","*1*0*0").equals("1234567890"));
    assertEquals(replaceWildcard("12344","1*44").equals("12344"));
    assertEquals(replaceWildcard("12344","1*4").equals("12344"));
    assertEquals(replaceWildcard("1234567890","1*0").equals("1234567890"));
    assertEquals(replaceWildcard("1234567890","1*7*9*").equals("1234567890"));
    assertEquals(replaceWildcard("1234567890","*1*0**").equals("1234567890"));
    assertEquals(replaceWildcard("1234567890","*1*0").equals("1234567890"));
    assertEquals(replaceWildcard("1234567890","1*0").equals("1234567890"));
    assertEquals(replaceWildcard("1234567890","1*").equals("1234567890"));
    assertEquals(replaceWildcard("1234567890","*").equals("1234567890"));
   printAssertInfo();
  }
}
