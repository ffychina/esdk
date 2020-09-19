package com.esdk.sql.orm.tools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import com.esdk.esdk;
import com.esdk.utils.EasyObj;

public class JavaCreater{
  public static final String _public="public",_protected="protected",_private="private";
  public static final String TVoid="void",TString="String",Tint="int",TBigDecimal="BigDecimal",TArrayList="ArrayList",TList="List";
  public static final String _RuntimeThrows="";
  private String packageName;
  private LinkedHashSet importSet;
  private LinkedHashSet fieldSet;
  private List<String> constructorList;
  private LinkedHashSet<String> methodSet;
  private String className;
  private StringBuffer javaContent;
  private String implementClassName;
  private String extentClassName;
  private List<String> classAnnotationList=new ArrayList();
  
  public JavaCreater(String clsname,String packagename) {
    className=clsname;
    packageName=packagename;
    importSet=new LinkedHashSet();
    constructorList=new ArrayList();
    fieldSet=new LinkedHashSet();
    methodSet=new LinkedHashSet();
  }
  
  public void addConstructor(String parameters,String content){
    addConstructor(parameters,"",content);
  }
   
  public void addConstructor(String parameters, String throwString,String content){
    StringBuffer sb=new StringBuffer();
    sb.append("  public").append(" ").append(className).append("(").append(parameters).append(")").append(throwString).append("{\n");
    sb.append(content).append("\n  }");
    methodSet.add(sb.toString());
  }
  
  public void addField(String type,boolean isStatic,boolean isFinal,String clssName,String variantName) {
    String s="  "+type.concat(isStatic?" static":"").concat(isFinal?" final ":" ")+organizeImport(clssName)+" "+variantName+";";
    fieldSet.add(s);
  }
  
  public void addField(String type,boolean isStatic,boolean isFinal,boolean isTransient,String clssName,String variantName) {
    String s="  "+type.concat(isStatic?" static":"").concat(isFinal?" final ":"").concat(isTransient?" transient ":" ")+organizeImport(clssName)+" "+variantName+";";
    fieldSet.add(s);
  }

  public void addFieldAnnotation(String content){
  	fieldSet.add(content);
  }
  
  
  public void addField(String type,boolean isStatic,boolean isFinal,String clssName,String variantName,String value) {
    String s="  "+type.concat(isStatic?" static":" ").concat(isFinal?" final ":" ")+organizeImport(clssName)+" "
        +variantName.concat(value!=null?"="+value:"")+";";
    fieldSet.add(s);
  }

  private String organizeImport(String clssName){
    if(esdk.obj.getClassSimpleName(clssName).equals(clssName)) {
      if(clssName.equals("BigDecimal"))
        addImport(BigDecimal.class.getName());
      if(clssName.equals("ArrayList"))
        addImport(ArrayList.class.getName());
      if(clssName.equals("List"))
        addImport(List.class.getName());
      if(clssName.equals("Date"))
        addImport(Date.class.getName());
      return clssName;
    }
    else {
      addImport(clssName);
      return EasyObj.getClassSimpleName(clssName);
    }
  }

  public void addConstructor(String value) {
    constructorList.add(value);
  }
  
  public void addClassAnnotation(String value) {
  	classAnnotationList.add(value);
  }
  
  public void addMethod(String type,boolean isStatic,String returnclassName,String methodName,String parameters,
      String throwString,String content){
    StringBuffer sb=new StringBuffer();
    organizeImport(returnclassName);
    sb.append("  ").append(type).append(isStatic?"static ":" ").append(returnclassName).append(" ").append(
        methodName).append("(").append(parameters).append(")").append(throwString).append("{\n");
    sb.append(content).append("\n  }");
    methodSet.add(sb.toString());
  }
  
  public void addMethod(String type,String returnclassName,String methodName,String parameters,String throwString,String content){
    addMethod(type,false,returnclassName,methodName,parameters,throwString,content);
  }
  
  public void addMethodAnnotation(String content){
    methodSet.add(content);
  }
  
  public void addImport(String clspath) {
    importSet.add(clspath);
  }
  public void addImport(Class cls) {
  	if(cls!=null) {
			if(!(cls.equals(Integer.class)||cls.equals(String.class)||cls.equals(Double.class)||cls.equals(Short.class)
					||cls.equals(Float.class)||cls.equals(Long.class)||cls.equals(Object.class)))
				importSet.add(cls.getName());
  	}
  }
  
  public void parse() {
    javaContent=new StringBuffer();
    javaContent.append("package ").append(packageName).append(";\n");
    for(Iterator iter=importSet.iterator();iter.hasNext();){
      javaContent.append("import ").append(iter.next()).append(";\n\n");
    }
    for(int i=0;i<classAnnotationList.size();i++){
      javaContent.append(classAnnotationList.get(i)).append("\n");
    }
    javaContent.append("public class ").append(className);
    if(extentClassName!=null)
      javaContent.append(" extends ").append(extentClassName);
    if(implementClassName!=null)
      javaContent.append(" implements ").append(implementClassName);
     javaContent.append("{\n");
     List fieldList=new ArrayList(fieldSet);
     for(int i=0;i<fieldSet.size();i++){
       javaContent.append(fieldList.get(i)).append("\n");
     }
     for(int i=0;i<constructorList.size();i++){
       javaContent.append(constructorList.get(i)).append("\n");
     }
     for(String mthcontent:methodSet){
       javaContent.append(mthcontent).append("\n");
       if(!mthcontent.trim().startsWith("@"))
      	 javaContent.append("\n");
     }
     
    javaContent.append("}\n");
  }
  
  public String getJavaContent() {
    return javaContent.toString();
  }

  public String getExtentClassName(){
    return this.extentClassName;
  }

  public void setExtentClassName(String extentclassName){
    this.extentClassName=extentclassName;
  }

  public String getImplementClassName(){
    return this.implementClassName;
  }

  public void setImplementClassName(String implementclassName){
    this.implementClassName=implementclassName;
  }
  
}
