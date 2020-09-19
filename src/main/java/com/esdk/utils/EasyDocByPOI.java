package com.esdk.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.esdk.esdk;

public class EasyDocByPOI{
	private File _templateFile; // 后缀名为doc，但内容为xml格式
	private JSONObject params=new JSONObject();

	public EasyDocByPOI(File docTemplateFile) throws FileNotFoundException{
		if(!docTemplateFile.exists())
			throw new FileNotFoundException(docTemplateFile.toString() + " is not found");
		this._templateFile=docTemplateFile;
	}

	public EasyDocByPOI set(String key,String value){
		params.put(key,value);
		return this;
	}

	public EasyDocByPOI set(String key,Number value){
		params.put(key,value + "");
		return this;
	}

	public EasyDocByPOI set(String key,Map map){
		params.put(key,map);
		return this;
	}

	public String convertCheckBoxValue(Boolean value){
		return value != null && value == true?"■":"□";
	}

	public String convertRadioValue(Boolean value){
		return value != null && value == true?"●":"○";
	}

	private XWPFDocument parse() throws Exception{
		XWPFDocument doc=new XWPFDocument(POIXMLDocument.openPackage(_templateFile.getAbsolutePath()));
//		doc.getParagraphs().get(0).getRuns().get(0).getText(0);
		setParams(doc.getParagraphs());
		for(Iterator<XWPFHeader> iter=doc.getHeaderList().iterator();iter.hasNext();){
			XWPFHeader header=(XWPFHeader)iter.next();
			setParams(header.getParagraphs());
		}
		for(Iterator<XWPFFooter> iter=doc.getFooterList().iterator();iter.hasNext();){
			XWPFFooter footer=(XWPFFooter)iter.next();
			setParams(footer.getParagraphs());
		}
		return doc;
	}

	private void setParams(List<XWPFParagraph> paragraphs) throws Exception{
		for(Iterator<XWPFParagraph> iter=paragraphs.iterator();iter.hasNext();){
			XWPFParagraph paragraph=(XWPFParagraph)iter.next();
			List<XWPFRun> runs=paragraph.getRuns();
			for(int i=0;i < runs.size();i++){
				XWPFRun run=runs.get(i);
				String source=run.getText(0);
//				System.out.println(source);
				if(esdk.str.isValid(source)){
					String content=replaceParams(source);
					run.setText(content,0);
				}
			}
		}
	}

	private String replaceParams(String source) throws Exception{
		String regex="(\\$\\{)(.+?)(\\})";
		String result=(new RegexReplace(source,regex,Pattern.CASE_INSENSITIVE | Pattern.DOTALL){
			@Override
			public String getReplacement(Matcher matcher){
				String key=matcher.group(2).replaceAll("<.*?>| ","");
				String value=params.getString(key);
				if(value == null)
					value=(String)JSONPath.eval(params,"$." + key);
				value=esdk.str.or(value,"").replaceAll("\r?\n","<w:br w:type=\"textWrapping\"/>");
				return value;
			}
		}).replaceAll();
		return result;
	}

	public void toDoc(File outputFile) throws Exception{
		XWPFDocument doc=parse();
		if(doc != null){
			OutputStream os=new FileOutputStream(outputFile);
			doc.write(os);
			os.close();
		}
	}

	public static void toPDF(File docPath,File pdfPath) throws Exception{ //TODO 效果不好, 有乱码
		esdk.tool.exec("wps2pdf.exe "+docPath.getAbsolutePath()+" "+pdfPath.getAbsolutePath());
	}

	public void writeTo(OutputStream os) throws Exception{
		XWPFDocument doc=parse();
		if(doc != null){
			doc.write(os);
			os.close();
		}
	}

	public JSONObject getParams(){
		return params;
	}

	public static void main(String[] args) throws Exception{
		TimeMeter tm=new TimeMeter();
		String filename="./testfiles/" + "${name}_${year}年_${type}_${hours}_学习证明.docx";
		EasyDocByPOI ed=new EasyDocByPOI(new File(filename));
		ed.set("name","苏穗华");
		ed.set("hours",760);
		ed.set("printTimes",1);
		ed.set("year",2016);
		ed.set("type","公需课");
		ed.set("courseInfo","《植物生理学---生长生理（上）》（5学时）、《植物生理学---生长生理（下）》（5学时）、《植物生理学---植物的能量代谢（上）》（5学时）、《植物生理学---植物的能量代谢（下）》（10学时）、《岭南园林植物的特点与文化内涵》（1学时）、《生物防治我国重大林木害虫研究进展》（2学时）、《林木枝干病害》（4学时）、《林业生态工程建设》（10学时）、《发展珍贵树种,促进林业产业升级》（2.5学时）、《林业资源环境统计分析方法》（3学时）");
		ed.set("nowDate",esdk.time.formatDate(new Date(),"yyyy年M月d日"));
		ed.set("course",esdk.map.urlParamsToMap("timeFrom=2015年04月22日&timeTo=2016年8月13日&type=公需课&year=2016&studyHours=760"));
		File outputFile=new File(esdk.str.format(filename,ed.params));
		ed.toDoc(outputFile);
		System.out.println(outputFile);
		tm.printElapse();
		ed.toPDF(outputFile,new File(outputFile.getAbsolutePath().replaceFirst(".docx","pdf")));
		System.out.println(outputFile);
		tm.printElapse();
		ed.toPDF(outputFile,new File(outputFile.getAbsolutePath().replaceFirst(".docx","pdf")));
		System.out.println(outputFile);
		tm.printElapse();
	}
}
