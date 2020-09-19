package com.esdk.convert;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

public class Html2Text extends HTMLEditorKit.ParserCallback {
	StringBuilder sb;

 public Html2Text() {}

 public void parse(Reader in) throws IOException {
   sb = new StringBuilder();
   ParserDelegator delegator = new ParserDelegator();
   // the third parameter is TRUE to ignore charset directive
   delegator.parse(in, this, Boolean.TRUE);
 }

 @Override
	public void handleStartTag(Tag t,MutableAttributeSet a,int pos){
		super.handleStartTag(t,a,pos);
	}
 
 @Override
	public void handleSimpleTag(Tag t,MutableAttributeSet a,int pos){
	 if(t.toString().equalsIgnoreCase("br"))
		 sb.append("\n");
	 super.handleSimpleTag(t,a,pos);
	}
 
 @Override
	public void handleEndTag(Tag t,int pos){
	 if(t.toString().equalsIgnoreCase("p"))
		 sb.append("\n");
		super.handleEndTag(t,pos);
	}
 
 public void handleText(char[] text, int pos) {
   sb.append(text);
 }

 public String getText() {
   return sb.toString();
 }

 public static String htm2txt(String html) {
	 if(html==null)
		 return null;
	 try {
     // the HTML to convert
     StringReader sr=new StringReader(html);
     Html2Text parser = new Html2Text();
     parser.parse(sr);
     sr.close();
     return parser.getText();
   }
   catch (Exception e) {
     throw new RuntimeException(e);
   }
 }
 
 public static void main (String[] args) {
	 System.out.println(htm2txt("<p>世界卫生组织和联合国环境组织发表的一份报告说：&ldquo;空气污染已成为全世界城市居民生活中一个无法逃避的现实。&rdquo;学习大气污染控制从而赢得一片自己洁净的生存空间。.</p>"));
	 System.out.println(htm2txt("<p>第一行</p><p>第二行</p><br>第四行<br>第五行<br/><p>第六行</p><br>").trim());
 }
}