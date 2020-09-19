package com.esdk.utils;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.ISelect;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.sql.orm.ABRowSet;
import com.esdk.sql.orm.IResultSet;
import com.esdk.sql.orm.IRow;
import com.esdk.sql.orm.IRowSet;

public class POIExcelBuilder extends Parser{
	private HashMap<Integer,String> indexToNameMap = null;//sheet下标与名称的Map
	private LinkedHashMap<String,Object> pMap=null;// Parameters
	private LinkedHashMap<String,ABRowSet> fMap=null;
	private LinkedHashMap<String,ABRowSet> SMap=null;
	private LinkedHashMap<String,List> lMap=null;// List
	private ArrayList<CellInfo> pCellList=new ArrayList(),fieldCellList=new ArrayList(),
			formFieldCellList=new ArrayList(),vCellList=new ArrayList(),
			lCellList=new ArrayList<CellInfo>(),aCellList=new ArrayList<CellInfo>(),iCellList=new  ArrayList<CellInfo>();
	private ArrayList<Cell> formulaCellList=new ArrayList();
	private LinkedHashMap<Integer,RowInfo> rowInfoMap=new LinkedHashMap<Integer,RowInfo>();
	private HSSFWorkbook wb=null;
	private String _defaultDateFormat="yyyy-MM-dd",_defaultTimeFormat="HH:mm:ss",
			_defaultDateTimeFormat=_defaultDateFormat+" "+_defaultTimeFormat;
	private boolean isAutoFitColumns=false;

	public POIExcelBuilder(File excelFile) throws IOException{
		this(new FileInputStream(excelFile));
	}

	public POIExcelBuilder(InputStream inputStream) throws IOException{
		pMap=new LinkedHashMap<String,Object>();
		fMap=new LinkedHashMap<String,ABRowSet>();
		lMap=new LinkedHashMap<String,List>();
		SMap=new LinkedHashMap<String,ABRowSet>();
		try{
			wb=new HSSFWorkbook(inputStream);
		}catch(Exception e){
			throw new IOException("模板出错，请确认EXCEL文件能被正确打开");
		}
	}

	public void setExcelTemplate(File file) throws IOException{
		wb=new HSSFWorkbook(new FileInputStream((File)file));
	}

	public void setExcelTemplate(InputStream inputStream) throws IOException{
		wb=new HSSFWorkbook(inputStream);
	}

	@Override public void setSource(Object source){
		try{
			if(source instanceof File)
				setExcelTemplate((File)source);
			else if(source instanceof InputStream)
				setExcelTemplate((InputStream)source);
			else
				throw new ParseRuntimeException("无法识别的参数:"+source.toString());
		}catch(IOException e){
			throw new ParseRuntimeException("模板出错，请确认EXCEL文件能被正确打开");
		}
	}

	public POIExcelBuilder setDefaultDateFormat(String pattern){
		this._defaultDateFormat=pattern;
		setDefaultDateTimeFormat(_defaultDateFormat+" "+_defaultTimeFormat);
		return this;
	}

	public POIExcelBuilder setDefaultTimeFormat(String pattern){
		this._defaultTimeFormat=pattern;
		setDefaultDateTimeFormat(_defaultDateFormat+" "+_defaultTimeFormat);
		return this;
	}

	public POIExcelBuilder setDefaultDateTimeFormat(String pattern){
		this._defaultDateTimeFormat=pattern;
		return this;
	}

	public void put(String key,Object value) throws Exception{
		if(value instanceof ABResultSet||value instanceof ABRowSet){
			putFMap(key,value);
		}else if(value instanceof List){
			putListMap(key,(List)value);
		}else if(value instanceof Object[]){
			putListMap(key,Arrays.asList((Object[])value));
		}else{
			putPMap(key,value);
		}
	}
	
	public void putSheetMap(String key,Object value) throws Exception{
		if(value instanceof ABResultSet){
			SMap.put(key,new ABRowSet((ABResultSet)value));
		}else if(value instanceof ABRowSet){
			SMap.put(key,(ABRowSet)value);
		}else{
			throw new IOException("不能接收ABResultSet,ABRowSet以外的类型");
		}
	}
	public  Object get(Object key){
		Object value=null;
		if(fMap.get(key)!=null){
			 value= fMap.get(key);			
		}else if(lMap.get(key)!=null){
			value=lMap.get(key);			
		}else if(pMap.get(key)!=null){
			value=pMap.get(key);			
		}
		return value;
	}

	public void putPMap(String key,Object value){
		pMap.put(key,value);
	}

	public void putListMap(String key,List value){
		lMap.put(key,value);
	}
	public void putFMap(String key,Object value) throws Exception{
		if(value instanceof ABResultSet){
			fMap.put(key,new ABRowSet((ABResultSet)value));
		}else if(value instanceof ABRowSet){
			fMap.put(key,(ABRowSet)value);
		}else{
			throw new IOException("不能接收ABResultSet,ABRowSet以外的类型");
		}
	}

	public void parse(){
		for(int i=0,n=wb.getNumberOfSheets();i<n;i++){
			parse(i);
			if(isAutoFitColumns)
				autoFitColumn(i);
		}
		parseSheet();
	}
	
	public void autoFitColumn(int sheetIndex) {
		HSSFSheet sheet=wb.getSheetAt(sheetIndex);
		int columnSize=sheet.getRow(0).getLastCellNum();
		for(int i=0;i<columnSize;i++) {
			sheet.autoSizeColumn(i,false);
		}
	}
	
	private void parseSheet() {
		if(SMap.size()==0)return;
		int parseSheetIndex=wb.getNumberOfSheets();
		for(int i=wb.getNumberOfSheets()-1;i>=0;i--){
			HSSFSheet sheet=wb.getSheetAt(i);
			if(sheet.getLastRowNum()==0)parseSheetIndex=i;
			else break;
		}
		 for (Entry<String, ABRowSet> entry : SMap.entrySet()) {
			String sheetName = entry.getKey();
			ABRowSet<IRow> rowSet = entry.getValue();
			
			if(wb.getNumberOfSheets()<=parseSheetIndex)wb.createSheet(sheetName);
			else wb.setSheetName(parseSheetIndex, sheetName);
			
			HSSFSheet sheet=wb.getSheetAt(parseSheetIndex);
			
			HSSFRow headRow = sheet.createRow(0);
			
			String[] columnName = rowSet.getColumnNames();
			for (int i = 0; i < columnName.length; i++) {
				HSSFCell headCell = headRow.createCell(i);
				POIUtil.setCellValue(columnName[i], headCell);
			}
			int rowNum = 1;
			for (IRow irow: rowSet) {
				HSSFRow row = sheet.createRow(rowNum);
				for (int i = 0; i < columnName.length; i++) {
					HSSFCell cell = row.createCell(i);
					POIUtil.setCellValue(irow.get(columnName[i]), cell);
				}
				rowNum++;
			}
			parseSheetIndex++;
		}
	}

	public HSSFWorkbook getHSSFWorkbook() {
		 return wb;
	}

  
  
	public boolean parse(String...sheetNames) throws Exception{
		boolean result=true;
		for(int i=0;i<sheetNames.length;i++){
			result=result&&parse(wb.getNameIndex(sheetNames[i]));
		}
		return result;
	}

	public boolean parse(int...sheetIndex) throws Exception{
		boolean result=true;
		for(int i=0;i<sheetIndex.length;i++){
			result=result&&parse(i);
		}
		return result;
	}

	public void clear(){
		this.formulaCellList.clear();
		this.fieldCellList.clear();
		this.formFieldCellList.clear();
		this.lCellList.clear();
	  this.iCellList.clear();
		this.vCellList.clear();
		this.pCellList.clear();
		this.aCellList.clear();
		this.rowInfoMap.clear();
	}

	public boolean parse(int sheetIndex){
		clear(); 
		try{
			if(!EasyObj.isBlank(indexToNameMap)&&indexToNameMap.containsKey(sheetIndex))
					wb.setSheetName(sheetIndex,indexToNameMap.get(sheetIndex));
			HSSFSheet sheet=wb.getSheetAt(sheetIndex);
			collectPVFLFs(sheet);
			replaceParameters(); //参数
			replaceVariables();  //变量
			replaceFormFields(sheet);//表单数据集，用于打印多个表单
			replaceFields(sheet);  //数据集
			replaceListList(sheet);   //List<List>
			replaceIists(sheet);   //List
			replaceArray();   //一维数组
		  replaceFormulas();//公式
			sheet.setForceFormulaRecalculation(true);
			return true;
		}catch(Exception e){
			appendErr(e);
			return false;
		}
	}

	private void collectPVFLFs(HSSFSheet sheet){
		for(Row row:sheet){
			for(Cell cell:row){
				if(cell.getCellType()==cell.CELL_TYPE_BLANK){
					continue;
				}else if(cell.getCellType()==cell.CELL_TYPE_FORMULA){
					formulaCellList.add(cell);
				}else if(cell.getCellType()==cell.CELL_TYPE_STRING&&cell.getStringCellValue().length()>0){
					Pattern p=Pattern.compile("(.*?)(\\$)([P,F,C,L,V,A,I][N]{0,1})(\\{)(.+?)(\\})(.*)",Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
					Matcher matcher=p.matcher(cell.getStringCellValue());
					if(matcher.find()){
						if(matcher.group(3).equalsIgnoreCase("P")){
							pCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,this.findValue(pMap,matcher.group(5))));
						}else if((matcher.group(3).equalsIgnoreCase("F")||matcher.group(3).equalsIgnoreCase("FN"))&&POIUtil.isVerticalMerged(sheet,row.getRowNum(),0)){
							formFieldCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,this.findValue(fMap,matcher.group(5))));
						}else if(matcher.group(3).equalsIgnoreCase("F")||matcher.group(3).equalsIgnoreCase("FN")){
							fieldCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,this.findValue(fMap,matcher.group(5))));
						}else if(matcher.group(3).equalsIgnoreCase("L")){
							lCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,this.findValue(lMap,matcher.group(5))));
						}else if(matcher.group(3).equalsIgnoreCase("V")&&matcher.group(5).matches("(COUNT|count|SUM|sum)(\\().*")){
							vCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,null));
						}else if(matcher.group(3).equalsIgnoreCase("A")){
							aCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,this.findValue(lMap,matcher.group(5))));
						}
						else if(matcher.group(3).equalsIgnoreCase("I")){
							iCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,this.findValue(lMap,matcher.group(5))));
						}else if(matcher.group(3).equalsIgnoreCase("PN")){ //強制把參數值轉換為數字型
							String value = String.valueOf(this.findValue(pMap,matcher.group(5)));
							if(EasyObj.isBlank(value))
						    pCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,value));
							else{
						  	pCellList.add(new CellInfo(matcher.group(1),matcher.group(5),matcher.group(7),cell,("null".equals(value)?BigDecimal.ZERO:new BigDecimal(value))));
							}
						}
					}
				}
			}
		}
	}

	private Object findValue(Object container,String path){
		Object result=null;
		if(EasyStr.isBlank(path)){
			this.appendErr("参数不能为空:".concat(path));
		}else{
			int i=path.indexOf('.');
			if(i>=0){
				String key=path.substring(0,i);
				String nextPath=path.substring(i+1);
				Object obj=getValue(container,key);
				result=findValue(obj,nextPath);
			}else{
				String[] complex=splitKeyAndArguments(path);
				result=getValue(container,complex[0]);
				if(complex[1]!=null)
					result=EasyStr.valueOf(result,getDateFormat(complex[1]));
				else if(result==null&&container instanceof Map){
					if(((Map)container).containsKey(complex[0]))
						result="";
				}
			}
		}
		return result;
	}

	private String getDateFormat(String args){
		if(args==null)
			return _defaultDateFormat;
		else{
			String[] result=args.split(",|;"); // 不可以用空格，因为日期格式有空格符号
			for(int i=0;i<result.length;i++){
				if(result[i].startsWith("dateFormat")){
					return result[i].substring(result[i].indexOf("=")+1);
				}
			}
			return _defaultDateFormat;
		}
	}

	/**eg. ${rows.createTime(dateFormat,yyyy-MM-dd HH:mm:ss)} */
	private String[] splitKeyAndArguments(String path){
		String[] result=new String[2];
		if(path.indexOf("(")<0){
			result[0]=path;
		}else{
			result[0]=path.substring(0,path.indexOf("("));
			result[1]=path.substring(path.indexOf('(')+1,path.lastIndexOf(')'));
		}
		return result;
	}

	private Object getValue(Object obj,String key){
		if(obj==null)
			return null;
		Object result=null;
		if(obj instanceof Map)
			result=((Map)obj).get(key);
		else if(obj instanceof IRowSet){
			if(!((IRowSet)obj).isEmpty()){
				IRow row=((IRowSet)obj).getRow(0);
				result=row.get(key);
			}
		}else if(obj instanceof IRow)
			result=((IRow)obj).get(key);
		else if(obj instanceof ISelect)
			throw new SdkRuntimeException("can't support iselect type, please use iselect.toRowSet().");
		else{
			try{
				result=EasyReflect.getGetMethodValue(obj,key);
			}catch(Exception e){
				appendErr(e);
				return result;
			}
		}
		return result;
	}

	private void replaceParameters() throws Exception{
		for(CellInfo cellInfo:pCellList){
			cellInfo.changeCellValue();
			if(cellInfo.isComplexCell()){
				String cellValue=new RegexReplace(cellInfo.cell.getStringCellValue(),"(.*?)(\\$P\\{)(.+?)(\\})(.*?)",
						Pattern.DOTALL|Pattern.CASE_INSENSITIVE){
					@Override public String getReplacement(Matcher matcher){
						Object findValue=findValue(pMap,matcher.group(3));
						if(findValue!=null) {
							String replaceValue=matcher.group(1)+findValue+matcher.group(5);
							return replaceValue;
						}
						return matcher.group();
					}
				}.replaceAll();
				cellInfo.cell.setCellValue(cellValue);
			}
		}
	}

	private void replaceVariables() throws Exception{
		for(CellInfo cellInfo:vCellList){
			String value=new RegexReplace(cellInfo.key,"(count|sum)(\\()(\\w*)(\\.?)(\\w*)(\\))"){
				@Override public String getReplacement(Matcher matcher){
					if(matcher.group(1).equalsIgnoreCase("count")){
						Object obj=EasyObj.or(fMap.get(matcher.group(3)),lMap.get(matcher.group(3)));
						if(obj instanceof IResultSet)
							return String.valueOf(((IResultSet)obj).size());
						else if(obj instanceof Collection)
							return String.valueOf(((Collection)obj).size());
					}else if(matcher.group(1).equalsIgnoreCase("sum")){
						String alias=matcher.group(3),fieldname=matcher.group(5);
						Object obj=fMap.get(alias);
						if(obj instanceof IResultSet)
							return String.valueOf(((IResultSet)obj).sum(fieldname.split(",")));
					}
					return matcher.group();
				}
			}.replaceAll();
			cellInfo.value=value;
			cellInfo.changeCellValue();
		}
	}


	private void replaceFormFields(HSSFSheet sheet){
		if(formFieldCellList.size()==0)
			return;
		for(CellInfo cellInfo:formFieldCellList){
			getFormInfo(cellInfo).add(cellInfo);
		}
		for(RowInfo rowInfo:rowInfoMap.values()){
			FormInfo formInfo=(FormInfo)rowInfo;
			POIUtil.insertMergeRow(sheet,formInfo.getFirstRowNum(),formInfo.getLastRowNum(),formInfo.getRowSize()-1);
			
			int index=0;
			for(IRow irow:formInfo.rowSet){
				for(CellInfo cellinfo:formInfo.cells){
					int irowFirstRowNum=formInfo.getFirstRowNum()+formInfo.getMergeSize()*index;
					int shift=cellinfo.cell.getRowIndex()-formInfo.getFirstRowNum();
					int rowIndex=irowFirstRowNum+shift;
					Row row=sheet.getRow(rowIndex);
					CellInfo targetCellInfo=new CellInfo(cellinfo.prefix,cellinfo.key,cellinfo.suffix,row.getCell(cellinfo.cell.getColumnIndex()),irow.get(cellinfo.key));
					targetCellInfo.changeCellValue();
					Cell autoNoCell=findAutoNoCell(row);
					if(autoNoCell!=null)
						autoNoCell.setCellValue(index+1);
				}
				index++;
			}
		}
	}
	
	private void replaceFields(HSSFSheet sheet){
		if(fieldCellList.size()==0)
			return ;
		for(CellInfo cellInfo:fieldCellList){
			getRowInfo(cellInfo).add(cellInfo);
		}
		for(RowInfo rowInfo:rowInfoMap.values()){
			int rowIndex=rowInfo.getRow().getRowNum();
			List<Integer[]> mergerColumnList = findMergerCell(sheet,rowInfo.getRow());
			POIUtil.insertRow(sheet,rowIndex,rowInfo.getRowSize()-1,true);
			int index=0;
			for(IRow irow:rowInfo.rowSet){
				HSSFRow row=sheet.getRow(rowIndex+index);
				Cell autoNoCell=findAutoNoCell(row);
				for(CellInfo cellinfo:rowInfo.cells){
					cellinfo.cell=row.getCell(cellinfo.cell.getColumnIndex());
					cellinfo.value=irow.get(cellinfo.key);
					cellinfo.changeCellValue();
				}
				if(autoNoCell!=null)
					autoNoCell.setCellValue(index+1);
				index++;
			}
			if(rowInfo.getRowSize()>0){
				for (Integer column[] : mergerColumnList) {
					 sheet.addMergedRegion(new CellRangeAddress(
							rowIndex, //first row (0-based)
							rowIndex+rowInfo.getRowSize()-1, //last row  (0-based)
							column[0], //first column (0-based)
							column[1]  //last column  (0-based)
					 ));
				}
			}
		}
	}
	
	private void replaceArray(){
		for(CellInfo cellInfo:aCellList){
			List list=(List)cellInfo.value;
			HSSFRow row=(HSSFRow)cellInfo.cell.getRow();
			Cell autoNoCell=findAutoNoCell(row);
			int rowIndex=0;
			int columnIndex=cellInfo.cell.getColumnIndex();
			if(list!=null) {
				for(Object value:list){
					Cell cloneCell=null;
					if(columnIndex+1<=row.getLastCellNum()){
						columnIndex=POIUtil.getMergedRegionLastColumn(row.getSheet(),cellInfo.cell.getRowIndex()+rowIndex,columnIndex,value,row);
						cloneCell=row.getCell(columnIndex);
					}else{
						cloneCell=row.createCell(columnIndex);
						cloneCell.setCellStyle(cellInfo.cell.getCellStyle());
						cloneCell.setCellType(cellInfo.cell.getCellType());
					}
					POIUtil.setCellValue(value,cloneCell);
					columnIndex++;
				}
			}
			if(autoNoCell!=null){
				autoNoCell.setCellValue(rowIndex+1);
			}
			rowIndex++;
		}
	}

	 private void replaceListList(HSSFSheet sheet){
	    for(CellInfo cellInfo:lCellList){
	      if(cellInfo.value instanceof List<?>){
	        List<List> listList=(List<List>)cellInfo.value;
	        POIUtil.insertRow(sheet,cellInfo.cell.getRowIndex(),listList.size()-1,true);
	        int rowIndex=0;
	        for(int i=0,n=listList.size();i<n;i++){
	          List list1=listList.get(i);
	          HSSFRow row=sheet.getRow(cellInfo.cell.getRowIndex()+i);
	          Cell autoNoCell=findAutoNoCell(row);
	          int columnIndex=cellInfo.cell.getColumnIndex();
	          for(Object value:list1){
	            Cell cloneCell=null;int k=0;k=columnIndex;
	            if(columnIndex+1<=row.getLastCellNum()){
	              columnIndex=POIUtil.getMergedRegionLastColumn(sheet,cellInfo.cell.getRowIndex()+i,columnIndex,value,row);
	              cloneCell=row.getCell(columnIndex);
	              //cloneCell.setCellStyle(cellInfo.cell.getCellStyle()); //TODO 需要多测试才确定是否需要复制格式
	            }else{
	              cloneCell=row.createCell(columnIndex);
	              cloneCell.setCellStyle(cellInfo.cell.getCellStyle());
	              //sheet.setColumnWidth(cloneCell.getColumnIndex(), sheet.getColumnWidth(firstCellCol));
	            }
	            if(k==columnIndex) {
	            	POIUtil.setCellValue(value,cloneCell);
	            	if(value instanceof Date &&cloneCell.getCellStyle().getDataFormat()==0) {
          				cloneCell.setCellStyle(cloneCell.getSheet().getWorkbook().createCellStyle());
          				/*HSSFDataFormat format=(HSSFDataFormat)cloneCell.getSheet().getWorkbook().createDataFormat(); //debug
          				System.out.println(format.getBuiltinFormats());*/
          				cloneCell.getCellStyle().setDataFormat((short)14); //14 is default date style[d/m/y]
	            	}
	            }
	            columnIndex++;
	          }
	          if(autoNoCell!=null){
	            autoNoCell.setCellValue(rowIndex+1);
	          }
	          rowIndex++;
	        }
	      }
	    }
	  }
	
	private void replaceIists(HSSFSheet sheet) throws Exception{
    for(CellInfo cellInfo:iCellList){
      if(cellInfo.value instanceof List<?>){
        List<List> listList=(List<List>)cellInfo.value;
        int rowIndex=cellInfo.cell.getRowIndex();
        for(int i=0,n=listList.size();i<n;i++){
          List list1=listList.get(i);
          HSSFRow row=sheet.getRow(rowIndex+i);
         if(row!=null) {
          int columnIndex=cellInfo.cell.getColumnIndex();
          int firstCellCol=columnIndex;
          for(Object value:list1){
            Cell cloneCell=null;int k=columnIndex;
            if(columnIndex+1<=row.getLastCellNum()){
              columnIndex=POIUtil.getMergedRegionLastColumn(sheet,rowIndex+i,columnIndex,value,row);
              cloneCell=row.getCell(columnIndex);
              copyCellStyle(sheet,row.getCell(firstCellCol),cloneCell);
            }
            if(cloneCell==null||columnIndex+1>row.getLastCellNum()){ 
              cloneCell=row.createCell(columnIndex);
              copyCellStyle(sheet,row.getCell(firstCellCol),cloneCell);
            }
            if(k==columnIndex&&value!= null) {
              POIUtil.setCellValue(value,cloneCell);
              }
            columnIndex++;
          }
          rowIndex+=POIUtil.getRowMergedRegionLength(sheet,rowIndex+i,cellInfo.cell.getColumnIndex());
        }
        
        }
      } 
    }
 }   
	
	
	 private  void copyCellStyle(HSSFSheet sheet,Cell sourceCell,Cell targetCell) throws Exception{
	   if(POIUtil.isDefaultStyle(targetCell)){
	     targetCell.setCellStyle(sourceCell.getCellStyle());
       sheet.setColumnWidth(targetCell.getColumnIndex(), sheet.getColumnWidth(sourceCell.getColumnIndex()));
	   }
	 }
	
	private int getLastRowNumber(int start,int end){
		int lastMunber=0,totalMun=0,newEnd=end;
		for(CellInfo cellInfo:lCellList){
			List<List> listList=(List<List>)cellInfo.value;
			int cellIndex=cellInfo.cell.getRowIndex()+1;
			int listIndex=listList.size()-1;
			if(start==end){
				if(start<=cellIndex&&cellIndex<=(newEnd+listIndex)){
					start=+listIndex;
					totalMun+=listIndex;
					newEnd+=listIndex;
				}
			}
		}
//		for(CellInfo cellInfo:fieldCellList){
			for(final RowInfo rowInfo:rowInfoMap.values()){
//				int cellIndex=cellInfo.cell.getRowIndex()+1;
				int cellIndex=rowInfo.getRow().getRowNum();
				int fieldIndex=rowInfo.getRowSize()-1;
				if(start==end){
					if((start<=cellIndex&&cellIndex<=newEnd+fieldIndex)){
						start=+fieldIndex;
						totalMun+=fieldIndex;
						newEnd+=fieldIndex;
					}
				}
			}
//		}
		lastMunber=totalMun+end;
		return lastMunber;
	}

	private void replaceFormulas() throws Exception{
		for(final Cell cell:formulaCellList){
			String newFormula=new RegexReplace(cell.getCellFormula(),"([a-zA-Z]+)([0-9]+):?([a-zA-Z]+)?([0-9]+)?"){
				@Override public String getReplacement(Matcher matcher){
					int group2=Integer.parseInt(matcher.group(2));
					int group4=(matcher.group(4)!=null)?Integer.parseInt(matcher.group(4)):group2;
					String group3=(matcher.group(3)==null||matcher.group(4)==null)?matcher.group(1):matcher.group(3);
					int lastRowNumber=getLastRowNumber(group2,group4);
					String formular=matcher.group();
					if(lastRowNumber!=group4)
						formular=matcher.group(1)+matcher.group(2)+":"+group3+lastRowNumber;
					if(cell.getRowIndex()+1==group2)
						formular=null;
					return formular;
				}
			}.replaceAll();
			if(!newFormula.equals(cell.getCellFormula()))
					cell.setCellFormula(newFormula);
			}
	}

	private List<Integer[]> findMergerCell(HSSFSheet sheet, Row row) {
		List<Integer[]> list = new ArrayList<Integer[]>();
		for (Cell cell : row) {
			if (cell.getCellType() == cell.CELL_TYPE_STRING && cell.getStringCellValue().length() > 0) {
				Pattern p = Pattern.compile("(.*)(\\$)([M])(\\{)(.+?)(\\})", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
				Matcher matcher = p.matcher(cell.getStringCellValue());
				if (matcher.find()) {
					cell.setCellValue(matcher.group(5));
					int startColumn = cell.getColumnIndex();
					int endColumn = cell.getColumnIndex();
					for (int i = 0,n=sheet.getNumMergedRegions(); i < n; i++) {
						CellRangeAddress ca = sheet.getMergedRegion(i);
						int firstColumn = ca.getFirstColumn();
						int lastColumn = ca.getLastColumn();
						int firstRow = ca.getFirstRow();
						int lastRow = ca.getLastRow();
						if (cell.getRowIndex() >= firstRow && cell.getRowIndex() <= lastRow) {
							if (cell.getColumnIndex() >= firstColumn && cell.getColumnIndex() <= lastColumn) {
								startColumn = firstColumn;
								endColumn = lastColumn;
								sheet.removeMergedRegion(i);
								break;
							}
						}
					}
					list.add(new Integer[] { startColumn, endColumn });
				}
			}
		}
		return list;
	}
	
	public Cell findAutoNoCell(Row row){
		for(Cell cell:row){
			if(cell.getCellType()==cell.CELL_TYPE_STRING&&cell.getStringCellValue().equalsIgnoreCase("$V{NO}"))
				return cell;
		}
		return null;
	}

	public void write(OutputStream os) throws IOException{
		wb.write(os);
		os.flush();
		os.close();
	}

	public void write(File saveTo) throws IOException{
		// wb.writeProtectWorkbook("password","username");
		EasyFile.createFolder(saveTo.getParent(),false);
		FileOutputStream fos=new FileOutputStream(saveTo);
		wb.write(fos);
		fos.flush();
		fos.close();
	}
	
	class CellInfo{
		public CellInfo(String prefix,String key,String suffix,Cell cel,Object value){
			this.prefix=prefix;
			this.suffix=suffix;
			this.key=key;
			this.cell=cel;
			this.value=value;
		}
		String prefix,suffix,key;
		Cell cell;
		int columnIndex;
		Object value;

		boolean isComplexCell(){
			return(EasyObj.isValid(prefix)||EasyObj.isValid(suffix));
		}

		Object getCellValue(){
			if(isComplexCell()){
				if(value instanceof Date){
					if(EasyTime.isEmptyTime((Date)value))
						value=EasyTime.formatDate((Date)value,_defaultDateFormat);
					else
						value=EasyTime.formatDate((Date)value,_defaultDateTimeFormat);
				}
				return prefix+value+suffix;
			}else
				return value;
		}

		void changeCellValue(){
			// if(this.getCellValue()!=null)
			POIUtil.setCellValue(this.getCellValue(),this.cell);
		}
	}
	class RowInfo{
		String tableAliasName;
		Iterable<IRow> rowSet;
		List<CellInfo> cells=new ArrayList<CellInfo>();

		public RowInfo(String tableAliasName,ABRowSet abRowSet){
			this.tableAliasName=tableAliasName;
			this.rowSet=abRowSet==null?new ABRowSet():abRowSet;
		}

		public HSSFRow getRow(){
			if(this.cells.size()>0){
				Cell cell=this.cells.get(0).cell;
				return (HSSFRow)cell.getSheet().getRow(cell.getRowIndex());
			}else
				return null;
		}

		public void add(CellInfo cellInfo){
			cells.add(cellInfo);
		}

		public int getRowSize(){
			return ((IRowSet)rowSet).size();
		}
	}

	class FormInfo extends RowInfo{
		int _firstRowNum,_lastRowNum;
		public FormInfo(String tableAliasName,ABRowSet abRowSet,int firstRowNum,int lastRowNum){
			super(tableAliasName,abRowSet);
			_firstRowNum=firstRowNum;
			_lastRowNum=lastRowNum;
		}
		public void add(CellInfo cellInfo){
			cells.add(cellInfo);
		}
		public int getFirstRowNum() {
			return _firstRowNum;
		}
		public int getLastRowNum() {
			return _lastRowNum;
		}
		public int getMergeSize() {
			return _lastRowNum-_firstRowNum+1;
		}
	}
	
	public RowInfo getRowInfo(CellInfo cellInfo){
		String tableAliasName=null,fieldName=null;
		int index=cellInfo.key.lastIndexOf('.');
		if(index>=0){
			tableAliasName=cellInfo.key.substring(0,index);
			fieldName=cellInfo.key.substring(index+1);
			cellInfo.key=fieldName;
		}
		Row row=cellInfo.cell.getRow();
		RowInfo result=rowInfoMap.get(row.getRowNum());
		if(result==null){
			result=new RowInfo(tableAliasName,fMap.get(tableAliasName));
			rowInfoMap.put(row.getRowNum(),result);
		}
		return result;
	}
	
	public RowInfo getFormInfo(CellInfo cellInfo){
		String tableAliasName=null,fieldName=null;
		int index=cellInfo.key.lastIndexOf('.');
		if(index>=0){
			tableAliasName=cellInfo.key.substring(0,index);
			fieldName=cellInfo.key.substring(index+1);
			cellInfo.key=fieldName;
		}
		Row row=cellInfo.cell.getRow();
		CellRangeAddress cellRangeAddress=POIUtil.getMergedRegion(row.getSheet(),row.getRowNum(),0);
		int firstRowNum=cellRangeAddress.getFirstRow();
		int lastRowNum=cellRangeAddress.getLastRow();
		FormInfo result=(FormInfo)rowInfoMap.get(firstRowNum);
		if(result==null){
				result=new FormInfo(tableAliasName,fMap.get(tableAliasName),firstRowNum,lastRowNum);
				rowInfoMap.put(firstRowNum,result);
		}
		return result;
	}
	
	public HashMap<Integer,String> getIndexToNameMap(){
		return indexToNameMap;
	}

	public void setIndexToNameMap(HashMap<Integer,String> indexToNameMap){
		this.indexToNameMap=indexToNameMap;
	}
	
	public boolean isAutoFitColumns(){
		return isAutoFitColumns;
	}

	public void setAutoFitColumns(boolean isAutoFitColumns){
		this.isAutoFitColumns = isAutoFitColumns;
	}
	

	@Override public Object getResult(){
		return wb;
	}

	@Override public String toString(){
		return wb.toString();
	}

	private static void test() throws Exception{
		TimeMeter tm=new TimeMeter();
		POIExcelBuilder builder=new POIExcelBuilder(new File("./testfiles/act_presence.xls"));
		ABRowSet apRs=new ABRowSet();
		IRow aprow1=apRs.append();
		aprow1.set("sex","女");
		aprow1.set("memberName","张洁");
		aprow1.set("group","欢乐组");
		aprow1.set("fee1",11);
		aprow1.set("fee2",12);
		aprow1.set("fee3",13);

		IRow apRow1=apRs.append();
		apRow1.set("sex","男");
		apRow1.set("memberName","陈胜");
		apRow1.set("group","雄鹰组");
		apRow1.set("fee1",21);
		apRow1.set("fee2",22);
		apRow1.set("fee3",23);

		IRow p1=apRs.append();
		p1.set("sex","男");
		p1.set("memberName","何林");
		p1.set("group","石头组");
		p1.set("fee1",31);
		p1.set("fee2",32);
		p1.set("fee3",33);

		IRow p2=apRs.append();
		p2.set("sex","男");
		p2.set("memberName","林锋");
		p2.set("group","石头组");
		p2.set("fee1",41);
		p2.set("fee2",42);
		p2.set("fee3",43);

		IRow aprowWW1=apRs.append();
		aprowWW1.set("sex","女");
		aprowWW1.set("memberName","何晓燕");
		aprowWW1.set("group","");
		aprowWW1.set("fee1",51);
		aprowWW1.set("fee2",52);
		aprowWW1.set("fee3",53);

		IRow apRoWw1=apRs.append();
		apRoWw1.set("sex","男");
		apRoWw1.set("memberName","范思能");
		apRoWw1.set("group","石头组");
		apRoWw1.set("fee1",61);
		apRoWw1.set("fee2",62);
		apRoWw1.set("fee3",63);

		IRow p1W=apRs.append();
		p1W.set("sex","男");
		p1W.set("memberName","郑凯");
		p1W.set("group","石头组");
		p1W.set("fee1",71);
		p1W.set("fee2",72);
		p1W.set("fee3",73);

		IRow pWQ2=apRs.append();
		pWQ2.set("sex","男");
		pWQ2.set("memberName","林震杰");
		pWQ2.set("group","雄鹰组");
		pWQ2.set("fee1",81);
		pWQ2.set("fee2",82);
		pWQ2.set("fee3",83);
		
		List attend=new ArrayList();
		attend.add(Arrays.asList(new String[]{"何小妹","女","黄昏组","Y","N","1"}));
		attend.add(Arrays.asList(new String[]{"何妹","女","黄昏组","Y","N","1"}));
		attend.add(Arrays.asList(new String[]{"wwwww","女","黄昏组","Y","N","1"}));
		attend.add(Arrays.asList(new Object[]{"李想1","男","琥珀组",true,null,"2"}));
		attend.add(Arrays.asList(new String[]{"22妹","女","黄昏组","Y","N","1"}));
		attend.add(Arrays.asList(new String[]{"33妹","女","黄昏组","Y","N","1"}));
		attend.add(Arrays.asList(new String[]{"44","女","黄昏组","Y","N","1"}));
		attend.add(Arrays.asList(new Object[]{"55","男","琥珀组",true,null,"2"}));

		attend.add(Arrays.asList(new Integer[]{1,3,45,56,4,54}));
		attend.add(Arrays.asList(new Integer[]{1,3,45,56,4,54}));
		attend.add(Arrays.asList(new Integer[]{1,3,45,56,4,54}));
		attend.add(Arrays.asList(new Integer[]{1,3,45,56,4,54}));
		
		builder.put("ap",apRs);
		builder.put("attend",attend);
		builder.put("act_time",new String[] {"23/6","24/6","25/6","26/6","27/6","28/6"});
		builder.put("centerName","荃湾长者中心");
		builder.put("actTimeDateStart",esdk.time.getDate("2015-06-24"));
		builder.put("actTimeDateEnd",esdk.time.getDate("2015-07-23"));
		Map reportMap=esdk.map.strToMap("{picName=李想,pic1Name=陈虹,presenceCount=10,absenceCount=7,activityName=登山,code=A0001,tutorName=郭嘉,time=08:00am-04:00pm}");
		builder.put("report",reportMap);
		ByteArrayOutputStream byteArrayOut=new ByteArrayOutputStream();
		BufferedImage bufferImg=ImageIO.read(new File("./testfiles/title.gif"));
		ImageIO.write(bufferImg,"gif",byteArrayOut);
		HSSFSheet sheet1=builder.getHSSFWorkbook().getSheetAt(0);
		HSSFPatriarch patriarch=sheet1.createDrawingPatriarch();
		HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,255,255,(short)2,0,(short)8,0);
		anchor.setAnchorType(3);
		patriarch.createPicture(anchor,
				builder.getHSSFWorkbook().addPicture(byteArrayOut.toByteArray(),HSSFWorkbook.PICTURE_TYPE_JPEG)).resize(1);

		builder.parse();
		tm.printElapse();
		builder.write(new File("./testfiles/PoiExport.xls"));

		// builder.write(new File("./testfiles/service_roster.xls"));
		// new File("./testfiles/PoiExport.xls").delete();
		tm.printElapse();
	}

	public static void main(String[] args) throws Exception{
		test();
	}
}
