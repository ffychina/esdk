package com.esdk.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.esdk.esdk;

public class EasyDoc{
	private File _templateFile; //后缀名为doc，但内容为xml格式
	private JSONObject params=new JSONObject();
	public EasyDoc(File docTemplateFile)throws FileNotFoundException{
		if(!docTemplateFile.exists())
			throw new FileNotFoundException(docTemplateFile.toString()+" is not found");
		this._templateFile=docTemplateFile;
	}
	
	public EasyDoc set(String key,String value) {
		params.put(key,value);
		return this;
	}
	
	public EasyDoc set(String key,Number value) {
		params.put(key,value+"");
		return this;
	}

	public EasyDoc set(String key,Map map) {
		params.put(key,map);
		return this;
	}

	public String convertCheckBoxValue(Boolean value){
		return value!=null&&value==true?"■":"□";
	}
	
	public String convertRadioValue(Boolean value){
		return value!=null&&value==true?"●":"○";
	}
	
	private String parse() throws IOException {
		String docXmlStr=EasyFile.loadFromFile(_templateFile,"utf-8");
		String regex="(\\$\\{)(.+?)(\\})";
		String content=(new RegexReplace(docXmlStr,regex,Pattern.CASE_INSENSITIVE|Pattern.DOTALL){
			@Override
			public String getReplacement(Matcher matcher){
				String key=matcher.group(2).replaceAll("<.*?>| ","");
				String value=params.getString(key);
				if(value==null)
					value=(String)JSONPath.eval(params,"$."+key);
				return esdk.str.or(value,"").replaceAll("\r?\n","<w:br w:type=\"textWrapping\"/>");
			}
		}).replaceAll();
		return content;
	}
	
	public boolean toDoc(File outputFile)throws Exception{
		String content=parse();
		return esdk.file.saveToFile(content,outputFile,true,EasyFile.UTF8);
	}
	
	public void toPDF(File pdfFile)throws Exception{
		File docFile=new File(pdfFile.getAbsolutePath().replaceFirst("\\.pdf",".doc"));
		toDoc(docFile);
		esdk.tool.exec("wps2pdf.exe "+docFile.getAbsolutePath()+" "+pdfFile.getAbsolutePath());//要先安装wps
		//TODO 如何删除临时文件.doc?
	}
	
	public File doc2pdf(File docFile,File pdfFile) throws Exception {
		if(pdfFile==null)
			pdfFile=new File(docFile.getAbsolutePath().replaceFirst("\\.docx?",".pdf"));
		esdk.tool.exec("wps2pdf.exe "+docFile.getAbsolutePath()+" "+pdfFile.getAbsolutePath());//要先安装wps
		return pdfFile;
	}
	
	public void writeTo(OutputStream os) throws Exception {
		String content=parse();
    os.write(content.getBytes());
    os.flush();
	}
	
	public JSONObject getParams() {
		return params;
	}
	public static void main(String[] args) throws Exception{
		TimeMeter tm=new TimeMeter();
		String filename="./testfiles/"+"${name}${year}年${type}${hours}学时证明.doc";
		EasyDoc ed=new EasyDoc(new File(filename));
		ed.set("name","苏穗华");
		ed.set("hours",760);
		ed.set("year",2016);
		ed.set("type","公需课");
		ed.set("courseInfo","《植物生理学---生长生理（上）》（5学时）、《植物生理学---生长生理（下）》（5学时）、《植物生理学---植物的能量代谢（上）》（5学时）、《植物生理学---植物的能量代谢（下）》（10学时）、《岭南园林植物的特点与文化内涵》（1学时）、《生物防治我国重大林木害虫研究进展》（2学时）、《林木枝干病害》（4学时）、《林业生态工程建设》（10学时）、《发展珍贵树种,促进林业产业升级》（2.5学时）、《林业资源环境统计分析方法》（3学时）");
		ed.set("nowDate",esdk.time.formatDate(new Date(),"yyyy年M月d日"));
		ed.set("course",esdk.map.valueOf("timeFrom=2015年04月22日&timeTo=2016年8月13日&type=公需课&year=2016&studyHours=760"));
		File outputFile=new File(esdk.str.format(filename,ed.params));
		ed.toDoc(outputFile);
		ed.toPDF(outputFile);
		tm.printElapse();
	}
}
