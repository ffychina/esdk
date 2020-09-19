package com.esdk.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.interfaces.POIDefaultHandler;
import com.esdk.interfaces.POIHandler;
import com.esdk.sql.orm.ABRow;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.ARowSet;
import com.esdk.sql.orm.IRow;

public class EasyPOI{
	private File xlsFileName;
	private int sheetIndex=0,fromRow=0,fromCol=0,toRow=65535,toCol=255;
	private Workbook wb;
	private Sheet sheet;
	private POIHandler handler=null;
	boolean hasTitle=true;
	
	public EasyPOI() {}
	
	public EasyPOI(File xlsFileName,int sheetIndex) throws IOException{
		this.xlsFileName=xlsFileName;
		this.sheetIndex=sheetIndex;
		FileInputStream is=new FileInputStream(this.xlsFileName);
		String path=xlsFileName.getPath();
		if(path.endsWith(".xls"))
			this.wb=new HSSFWorkbook(new POIFSFileSystem(is));
		else if(path.endsWith(".xlsx"))
			this.wb=new XSSFWorkbook(is);
		else
			this.wb=new HSSFWorkbook(is);
		this.sheet=(this.sheetIndex<0)?wb.getSheetAt(0):wb.getSheetAt(sheetIndex);
	}

	public EasyPOI(File xlsFileName,String sheetName) throws IOException{
		this.xlsFileName=xlsFileName;
		FileInputStream is=new FileInputStream(this.xlsFileName);
		String path=xlsFileName.getPath();
		if(path.endsWith(".xls"))
			this.wb=new HSSFWorkbook(new POIFSFileSystem(is));
		else if(path.endsWith(".xlsx"))
			this.wb=new XSSFWorkbook(is);
		else
			this.wb=new HSSFWorkbook(is);
		if(sheetName==null)
			this.sheet=wb.getSheetAt(0);
		else{
			this.sheet=wb.getSheet(sheetName);
			if(this.sheet==null)
				throw new RuntimeException("Sheet["+sheetName+"] no found.");
		}
	}

	public static int[] getCellPosition(String cellPoint){
		int[] result=new int[2];
		cellPoint=cellPoint.toUpperCase();
		for(int i=1;i<cellPoint.length();i++){
			if(cellPoint.charAt(i)>='0'&&cellPoint.charAt(i)<='9'){
				result[0]=(cellPoint.charAt(0)-64)*26*(i-1)+cellPoint.charAt(i-1)-65;
				result[1]=Integer.valueOf(cellPoint.substring(i))-1;
			}
		}
		return result;
	}

	public EasyPOI setRectangle(String fromPoint,String toPoint){
		int[] from=getCellPosition(fromPoint);
		this.fromCol=from[0];
		this.fromRow=from[1];
		int[] to=getCellPosition(toPoint);
		this.toCol=to[0];
		this.toRow=to[1];
		return this;
	}

	public EasyPOI setRectangle(int fromRow,int fromCol,int toRow,int toCol){
		this.fromRow=fromRow;
		this.fromCol=fromCol;
		if(toRow>0)
			this.toRow=toRow;
		if(toCol>0)
			this.toCol=toCol;
		return this;
	}

	public EasyPOI setHasTitle(boolean value){
		this.hasTitle=value;
		return this;
	}

	public EasyPOI setHandler(POIHandler handler){
		this.handler=handler;
		return this;
	}

	public List<List> toList(){
		List list=new ArrayList();
		int rowNum=toRow-fromRow+1;// 有几行
		Row hssfRow=null;// 有几列
		if(EasyObj.isValid(sheet.getRow(fromRow)))
			hssfRow=sheet.getRow(fromRow);
		else
			throw new SdkRuntimeException("EXCEL文件内容错误！");

		int cellNum=EasyMath.min(toCol-fromCol+1,hssfRow.getLastCellNum());
		for(int i=0;i<rowNum;i++){
			Row row=sheet.getRow(fromRow+i);
			List tempList=new ArrayList();
			if(row!=null){
				for(int j=0;j<cellNum;j++){
					try{
						Cell cell=row.getCell((short)(fromCol + j));
						// System.out.println("i="+i+",j="+j);
						if(cell != null){
							int originCellType=cell.getCellType();
							CellStyle cellstyle=cell.getCellStyle();
							int cellType;
							if(handler != null){
								cellType=handler.getCellType(i,j,originCellType,cellstyle);
							}else {
								cellType=new POIDefaultHandler().getCellType(i,j,originCellType,cellstyle);
							}
							if(cellType == POIHandler.CELL_TYPE_DATE) //日期类型要改为数字类型，才能获得日期值
								cell.setCellType(Cell.CELL_TYPE_NUMERIC);
							else if(cellType != originCellType && cellType == Cell.CELL_TYPE_STRING && Cell.CELL_TYPE_FORMULA != originCellType)
								cell.setCellType(cellType);
							if(cellType == Cell.CELL_TYPE_STRING){
								tempList.add(cell.getStringCellValue());
							}
							if(cellType == Cell.CELL_TYPE_NUMERIC){
								tempList.add(esdk.math.toNumber(cell.getNumericCellValue()));
							}else if(cellType == Cell.CELL_TYPE_BLANK){
								tempList.add(null);
							}else if(cellType == Cell.CELL_TYPE_BOOLEAN){
								tempList.add(cell.getBooleanCellValue());
							}else if(cellType == Cell.CELL_TYPE_ERROR){
								tempList.add(cell.toString());
							}else if(cellType == POIHandler.CELL_TYPE_DATE){
								tempList.add(cell.getDateCellValue());
							}else if(cellType == POIHandler.CELL_TYPE_FORMULA){
								Object obj=getCellValue(cell);
								tempList.add(obj);
							}
						}else{
							tempList.add(null);
						}
					}catch(Exception e){
						e.printStackTrace();
						SdkRuntimeException e1=new SdkRuntimeException(esdk.str.format("第{}行第{}列转换内容出错：{}",i+1,j+1,e.getMessage()));
						e1.setStackTrace(e.getStackTrace());
						throw e1;
					}
				}
				list.add(tempList);
			}else{
				break;
			}
		}
		return list;
	}

	private Object getCellValue(Cell cell){
		Object result=null;
		try {
		result=cell.getStringCellValue();
		}catch(IllegalStateException e) {
			try {
			result=esdk.math.toNumber(cell.getNumericCellValue());
			}catch(IllegalStateException e1) {
				result=cell.getBooleanCellValue();
			}
		}
		return result;
	}

	public static List<List> toList(File xlsFileName,int sheetIndex,int fromRow,int fromCol) throws IOException{
		EasyPOI ep=new EasyPOI(xlsFileName,sheetIndex);
		return ep.setRectangle(fromRow,fromCol,ep.toRow,ep.toCol).toList();
	}

	public static List<List> toList(File xlsFileName,int sheetIndex,int fromRow,int fromCol,POIHandler handler)
			throws IOException{
		EasyPOI ep=new EasyPOI(xlsFileName,sheetIndex);
		return ep.setHandler(handler).setRectangle(fromRow,fromCol,ep.toRow,ep.toCol).toList();
	}

	public static List<List> toList(File xlsFileName,int sheetIndex,int fromRow,int fromCol,int toRow,int toCol)
			throws IOException{
		return new EasyPOI(xlsFileName,sheetIndex).setRectangle(fromRow,fromCol,toRow,toCol).toList();
	}

	public static List<List> toList(File xlsFileName,String sheetName,int fromRow,int fromCol,int toRow,int toCol)
			throws IOException{
		return new EasyPOI(xlsFileName,sheetName).setRectangle(fromRow,fromCol,toRow,toCol).toList();
	}

	public static List<List> toList(File xlsFileName,String sheetName,int fromRow,int fromCol) throws IOException{
		EasyPOI ep=new EasyPOI(xlsFileName,sheetName);
		return ep.setRectangle(fromRow,fromCol,ep.toRow,ep.toCol).toList();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName) throws IOException{
		return new EasyPOI(xlsFileName,0).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,String sheetName) throws IOException{
		return new EasyPOI(xlsFileName,sheetName).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,String sheetName,int fromRow,int fromCol,int toRow,int toCol)
			throws IOException{
		return new EasyPOI(xlsFileName,sheetName).setRectangle(fromRow,fromCol,toRow,toCol).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,int sheetIndex) throws IOException{
		return new EasyPOI(xlsFileName,sheetIndex).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,int sheetIndex,String startPoint,String endPoint) throws IOException{
		return new EasyPOI(xlsFileName,sheetIndex).setRectangle(startPoint,endPoint).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,int sheetIndex,int fromRow,int fromCol,int toRow,int toCol)
			throws IOException{
		return new EasyPOI(xlsFileName,sheetIndex).setRectangle(fromRow,fromCol,toRow,toCol).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,int sheetIndex,int fromRow,int fromCol,POIHandler handler)
			throws IOException{
		EasyPOI ep=new EasyPOI(xlsFileName,sheetIndex);
		return ep.setHandler(handler).setRectangle(fromRow,fromCol,ep.toRow,ep.toCol).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,String sheetname,int fromRow,int fromCol,POIHandler handler)
			throws IOException{
		EasyPOI ep=new EasyPOI(xlsFileName,sheetname);
		return ep.setHandler(handler).setRectangle(fromRow,fromCol,ep.toRow,ep.toCol).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,int sheetIndex,int fromRow,int fromCol) throws IOException{
		EasyPOI ep=new EasyPOI(xlsFileName,sheetIndex);
		return ep.setRectangle(fromRow,fromCol,ep.toRow,ep.toCol).toRowSet();
	}

	public static ABRowSet<IRow> toRowSet(File xlsFileName,String sheetName,String startPoint,String endPoint)
			throws IOException{
		return new EasyPOI(xlsFileName,sheetName).setRectangle(startPoint,endPoint).toRowSet();
	}

	public ABRowSet<IRow> toRowSet(){
		List<List> list=toList();
		String[] cols=(String[])list.get(0).toArray(new String[0]);
		ABRowSet result=new ABRowSet(cols);
		for(int i=1;i<list.size();i++){
			result.add(new ABRow(cols,list.get(i).toArray()));
		}
		return result;
	}

	public static Cell getCell(Sheet sheet,String poisition){
		return POIUtil.getCell(sheet,poisition);
	}
	
	public static void toExcel(ARowSet rs,File outfile) throws Exception{
		List list2=rs.toList();
		list2.add(0,Arrays.asList(rs.getColumnNames()));
		toExcel(list2,outfile);
	}

	public static void toExcel(List<List> list2,File outfile) throws Exception{
		InputStream is=EasyPOI.class.getResourceAsStream("export.xls");
		POIExcelBuilder peb=new POIExcelBuilder(is);
		is.close();
		peb.put("content",list2);
		peb.setAutoFitColumns(true);
		peb.parse();
		peb.write(outfile);
	}

	public static void toExcel(List<List> list2,OutputStream os) throws Exception{
		InputStream is=EasyPOI.class.getResourceAsStream("export.xls");
		POIExcelBuilder peb=new POIExcelBuilder(is);
		is.close();
		peb.put("content",list2);
		peb.setAutoFitColumns(true);
		peb.parse();
		peb.write(os);
	}

	public static void toExcel(Object[][] arr2,File outfile) throws Exception{
		InputStream is=EasyFile.getInputStreamFromResources("com/esdk/utils/export.xls");
		POIExcelBuilder peb=new POIExcelBuilder(is);
		is.close();
		peb.put("content",EasyObj.arr2ToList(arr2));
		peb.setAutoFitColumns(true);
		peb.parse();
		peb.write(outfile);
	}

	public static void toExcel(Object[][] arr2,OutputStream os) throws Exception{
		InputStream is=EasyPOI.class.getResourceAsStream("export.xls");
		POIExcelBuilder peb=new POIExcelBuilder(is);
		is.close();
		peb.put("content",EasyObj.arr2ToList(arr2));
		peb.setAutoFitColumns(true);
		peb.parse();
		peb.write(os);
	}
	
	public static void main(String[] args) throws Exception{
		Object[][] data={{"NO","Status","标志","数值","日期"},{1,"成功",true,1333.222,EasyTime.valueOf("2014-04-01")},
				{2,"失败",false,222.222,EasyTime.valueOf("2015-04-01")},
				{3,"失败",false,33333333.3999,EasyTime.valueOf("2015-05-32")}};
		EasyPOI.toExcel(data,new File("testfiles/TestToExcel.xls"));
		esdk.tool.assertEquals(EasyPOI.toRowSet(new File("testfiles/TestToExcel.xls"),0,0,0).toCsv(),"NO,Status,标志,数值,日期\r\n"
				+"1.0,成功,true,1333.222,2014-04-01\r\n"+"2.0,失败,false,222.222,2015-04-01\r\n"
				+"3.0,失败,false,3.33333333999E7,2015-06-01");
		new File("testfiles/TestToExcel.xls").delete();
		esdk.tool.assertEquals(
						EasyPOI.toRowSet(new File("testfiles/member.xls"),"member_datamigration","a1","AS3").toCsv(),
						"会员编号,会籍类别,姓(中文),名(中文),姓(英文),名(英文),身份証号码,籍贯(省/县),住宅电话,手提电话,出生日期(西曆),出生月(农曆),出生日(农曆),备注,性别,婚姻状况,居住状况,现职,敎育程度,经济状况,惯用方言,地区,街道名称,号数,屋村/乡村名称,座/大厦名称,楼,室,心脏,血压,糖尿病,哮喘,眼部(左),眼部(右),耳部(左),耳部(右),活动能力,曾经中风,会籍组别,入会日期,会籍到期日,通知会员方式,(紧急联络人1)中文姓名,(紧急联络人1)英文姓名,(紧急联络人1)关係\r\n"
								+"1303,翡翠会员,张,三,Mr,zhong,1111111,,,,1948-02-03,,,,男,独身,独居,就业,大学,有领取综援金,\"粤语,闽南话,广州话\",新界,1,2,3,4,5,6,\"需长期吃心脏药,安装了心脏起伏器\",轻微<不需吃药>,严重<需吃药控制>,轻微<不需吃药>,\"白内障,青光眼\",青光眼,\"失聪,撞聋\",撞聋,\"行动不便(需要轮椅),拐杖,AA\",有,鑽石,1999-02-25,,邮件,12,ma,父子\r\n"
								+"1302,非会员,李,四,Mr,zhong,x1234566,,,,1948-05-06,,,,男,,独居,,大学,,\"粤语,闽南话,广州话\",新界,1,2,3,4,5,6,\"需长期吃心脏药,安装了心脏起伏器\",轻微<不需吃药>,严重<需吃药控制>,轻微<不需吃药>,\"白内障,青光眼\",青光眼,\"失聪,撞聋\",,\"行动不便(需要轮椅),拐杖,AA\",有,鑽石,1999-02-25,,邮件,12,ma,父子");
	}
}
