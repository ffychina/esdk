package com.esdk.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import com.esdk.exception.SdkRuntimeException;

public class POIUtil{

	public static void copyRow(HSSFSheet sheet,HSSFRow sourceRow,HSSFRow targetRow,boolean isCopyContent){
		Set<CellRangeAddress> mergedRegions=new HashSet<CellRangeAddress>();
		if(targetRow!=null) {
		if(sourceRow.getHeight()>=0){
			targetRow.setHeight(sourceRow.getHeight());
		}
		int startCellNum=sourceRow.getFirstCellNum();
		int endCellNum=sourceRow.getLastCellNum();
		
		for(int j=startCellNum;j<=endCellNum;j++){
			HSSFCell sourceCell=sourceRow.getCell(j);
			HSSFCell targetCell=targetRow.getCell(j);
			if(sourceCell!=null){
				if(targetCell==null){
					targetCell=targetRow.createCell(j);
				}
				CellRangeAddress mergedRegion=getMergedRegion(sheet,sourceRow.getRowNum(),sourceCell.getColumnIndex());
				if(mergedRegion!=null&&mergedRegion.getFirstRow()==sourceRow.getRowNum()
						&&mergedRegion.getFirstColumn()==sourceCell.getColumnIndex()){
					CellRangeAddress newMergedRegion=new CellRangeAddress(targetRow.getRowNum(),
							targetRow.getRowNum()+mergedRegion.getLastRow()-mergedRegion.getFirstRow(),
							mergedRegion.getFirstColumn(),mergedRegion.getLastColumn());
					if(isNewMergedRegion(newMergedRegion,mergedRegions)){
						mergedRegions.add(newMergedRegion);
						sheet.addMergedRegion(newMergedRegion);
					}
				}
				if(isCopyContent){
					copyCell(sourceCell,targetCell,true);
					if(targetCell.getCellType()==targetCell.CELL_TYPE_FORMULA) {
						replaceFormula(targetCell,targetCell.getRowIndex()-sourceCell.getRowIndex());
					}
				}else{
					targetCell.setCellStyle(sourceCell.getCellStyle());
				}
			}
		}
		}
	}
	
	public static void replaceFormula(final Cell cell,final int shiftNum) {
		String newFormula;
		try{
			newFormula=new RegexReplace(cell.getCellFormula(),"([a-zA-Z]+)([0-9]+)"){
				@Override public String getReplacement(Matcher matcher){
					int rowNum=Integer.parseInt(matcher.group(2));
					String formular=matcher.group();
					if(shiftNum>0) {
						formular=matcher.group(1)+(rowNum+shiftNum);
					}
					return formular;
				}
			}.replaceAll();
		}catch(Exception e){
			throw new SdkRuntimeException(e);
		}
		if(!newFormula.equals(cell.getCellFormula()))
				cell.setCellFormula(newFormula);
		}

	public static void copyCell(Cell oldCell,Cell newCell,boolean copyStyle){
		if(copyStyle){
			newCell.setCellStyle(oldCell.getCellStyle());
		}
		switch(oldCell.getCellType()){
		case HSSFCell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getRichStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			int oldRowNum=oldCell.getRowIndex()+1;
			int newRowNum=newCell.getRowIndex()+1;
			String oldFormula=oldCell.getCellFormula();
			if(oldFormula.indexOf("SUM")!=-1){
				oldFormula=oldFormula.replace(oldRowNum+"",newRowNum+"");
			}
			newCell.setCellFormula(oldFormula);
			break;
		default:
			break;
		}
	}

	public static Object getCellValue(Cell cell){
		Object value=null;
		switch(cell.getCellType()){
		case HSSFCell.CELL_TYPE_STRING:
			value=cell.getStringCellValue();
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			value=cell.getNumericCellValue();
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			value="";
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			value=cell.getBooleanCellValue();
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			value=cell.getErrorCellValue();
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			value=cell.getCellFormula();
			break;
		default:
			break;
		}
		return value;
	}

	public static void setCellValue(Object value,Cell cell){
		Pattern p=Pattern.compile("(.*)(\\$)([P,F,C,L,V,A,I][N]{0,1})(\\{)(.+?)(\\})(.*)",Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
		Matcher matcher=p.matcher(cell.getStringCellValue());
		if(matcher.find()){
			if(matcher.group(3).equalsIgnoreCase("FN")&&!EasyObj.isBlank(value)){
				   if(!"null".equals(value.toString()))
				  	 value = new BigDecimal(String.valueOf(value));
				   else
				  	 value = BigDecimal.ZERO;
			}
		}
		if(value==null)
			cell.setCellValue("");
		else if(value instanceof Integer){
			cell.setCellValue((Integer)value);
		}else if(value instanceof Long){
			cell.setCellValue((Long)value);
		}else if(value instanceof Boolean){
			cell.setCellValue((Boolean)value);
		}else if(value instanceof Double){ 
			cell.setCellValue((Double)value);
		}else if(value instanceof Float){ 
			cell.setCellValue((Float)value);
		}else if(value instanceof Date){
			cell.setCellValue((Date)value);
		}else if(value instanceof CharSequence){
			String strValue=value.toString().trim();
			if(!strValue.equals("") && strValue.matches("=[IF|SUM|COUNT|COUNTIF]\\(*.*(\\)$)")) {
				 cell.setCellFormula(cell.getCellType()==HSSFCell.CELL_TYPE_FORMULA?(cell.getCellFormula()):(strValue.substring(1,strValue.length())));
			}else {
			  cell.setCellValue((String)value);
			}
		}else if(value instanceof BigDecimal){
			cell.setCellValue(((BigDecimal)value).doubleValue());
		}else{
			throw new RuntimeException("unknown data type:value["+value+"],class["+value.getClass().getName()+"]");
		}
	}
	
	public static boolean isVerticalMerged(Sheet sheet,int rowNum,int columnNum) {
		CellRangeAddress cellRangeAddress=getMergedRegion(sheet,rowNum,columnNum);
		if(cellRangeAddress!=null)
			return cellRangeAddress.getFirstRow()!=cellRangeAddress.getLastRow();
		return false;
	}
	
	public static boolean isHorizontalMerged(HSSFSheet sheet,int rowNum,int columnNum) {
		CellRangeAddress cellRangeAddress=getMergedRegion(sheet,rowNum,columnNum);
		return cellRangeAddress.getLastColumn()!=cellRangeAddress.getFirstColumn();
	}
	
	public static int getMergedRegionLastColumn(HSSFSheet sheet,int rowIndex1,int columnIndex,Object value,HSSFRow row){
		CellRangeAddress region=POIUtil.getMergedRegion(sheet,rowIndex1,columnIndex);
		if(EasyObj.isValid(region)){
		  POIUtil.setCellValue(value,row.getCell(columnIndex));
			columnIndex=columnIndex+region.getLastColumn()-region.getFirstColumn();
		 
		}
		return columnIndex;
	}
	public static int getRowMergedRegionLength(HSSFSheet sheet,int rowIndex,int columnIndex){
	  int length=0;
		CellRangeAddress region=POIUtil.getMergedRegion(sheet,rowIndex,columnIndex);
		if(EasyObj.isValid(region)){
		  length=region.getLastRow()-region.getFirstRow();
		}
		return length;
	}
	public static CellRangeAddress getMergedRegion(Sheet sheet,int rowNum,int cellNum){
		for(int i=0;i<sheet.getNumMergedRegions();i++){
			CellRangeAddress merged=getMergedRegion(sheet,i);
			if(isRangeContainsCell(merged,rowNum,cellNum)){
				return merged;
			}
		}
		return null;
	}

	public static boolean isRangeContainsCell(CellRangeAddress range,int row,int col){
		if((range.getFirstRow()<=row)&&(range.getLastRow()>=row)&&(range.getFirstColumn()<=col)&&(range.getLastColumn()>=col)){
			return true;
		}
		return false;
	}
	private static CellRangeAddress getMergedRegion(Sheet sheet,int i){
		CellRangeAddress region=sheet.getMergedRegion(i);
		return region;
	}

	private static boolean isNewMergedRegion(CellRangeAddress region,Collection mergedRegions){
		for(Iterator iterator=mergedRegions.iterator();iterator.hasNext();){
			CellRangeAddress cellRangeAddress=(CellRangeAddress)iterator.next();
			if(areRegionsEqual(cellRangeAddress,region)){
				return false;
			}
		}
		return true;
	}

	private static boolean areRegionsEqual(CellRangeAddress region1,CellRangeAddress region2){
		if(region1==region2)
			return true;
		else if((region1==null&&region2!=null)||(region1!=null&&region2==null)){
			return false;
		}
		else
			return(region1.getFirstColumn()==region2.getFirstColumn()
				&&region1.getLastColumn()==region2.getLastColumn()
				&&region1.getFirstRow()==region2.getFirstRow()
				&&region1.getLastRow()==region2.getLastRow());
	}
	
	/**startRowNum应为excel看到的行数-1，endRowNum应为excel看到的列数-1*/
	public static void insertMergeRow(HSSFSheet sheet,int startRowNum,int endRowNum,int copyNum){
		int mergedSize=endRowNum-startRowNum+1;
		if(copyNum<0) {
			deleteRow(sheet,startRowNum,mergedSize);
			return ;
		}
		int oldLastRowNum=sheet.getLastRowNum();
		int newLastRowNum=sheet.getLastRowNum()+mergedSize*copyNum;
		for(int i=sheet.getLastRowNum()+1,n=newLastRowNum+1;i<n;i++) {
			sheet.createRow(i);
		}
		sheet.shiftRows(endRowNum+1,oldLastRowNum,newLastRowNum-oldLastRowNum,true,false,false);
		for(int i=0;i<copyNum;i++){
			for(int j=0;j<mergedSize;j++) {
				HSSFRow sourceRow=null;
				HSSFRow targetRow=null;
				sourceRow=sheet.getRow(startRowNum+j);
				int targetRowNum=startRowNum+j+mergedSize*(i+1);
				targetRow=sheet.getRow(targetRowNum);
				POIUtil.copyRow(sheet,sourceRow,targetRow,true);
			}
		}
	}
	
	public static void insertRow(HSSFSheet sheet,int startRowIndex,int copyNum,boolean isCopyContent){
		if(copyNum<0) {
			deleteRow(sheet,startRowIndex,-copyNum);
			return ;
		}
		int shiftSize=0;
		if(startRowIndex>=sheet.getLastRowNum()){
			Row row=null;
			for(int i=1;i<=copyNum;i++,shiftSize++){
				row=sheet.getRow(startRowIndex+i);
				if(row==null){
					row=sheet.createRow(startRowIndex+i);
				}
			}
		}
		if(copyNum>0&&copyNum-shiftSize>0)
			sheet.shiftRows(startRowIndex+1,sheet.getLastRowNum(),copyNum-shiftSize,true,false,false);
		for(int i=0;i<copyNum;i++){
			HSSFRow sourceRow=null;
			HSSFRow targetRow=null;
			sourceRow=sheet.getRow(startRowIndex);
			targetRow=sheet.getRow(++startRowIndex);
			if(targetRow==null){
				targetRow=sheet.createRow(startRowIndex);
			}
			POIUtil.copyRow(sheet,sourceRow,targetRow,isCopyContent);
		}
	}

	public static void deleteRow(HSSFSheet sheet,int startRow,int rows){
		int lastIndex=sheet.getLastRowNum();
		for(int i=startRow+rows,n=startRow;i>n;i--){
			sheet.shiftRows(i,EasyMath.max(sheet.getLastRowNum(),lastIndex)+1,-1,true,true);
		}
	}
	
	public static void deleteRow(HSSFSheet sheet,int startRow){
		 for(int i=sheet.getLastRowNum(),n=startRow;i>=n;i--) {
			sheet.shiftRows(i,sheet.getLastRowNum()+1,-1);
		 }
	}

	public static void deleteRow(Row row){
		row.getSheet().shiftRows(row.getRowNum()+1,row.getSheet().getLastRowNum(),-1,true,false);
	}

	public static String getColLetter(int colIndex){
		return colIndex<26?(""+(char)((colIndex)+65)):(""+(char)((colIndex)/26+65-1)+(char)((colIndex)%26+65));
	}

	public static HSSFCell getCell(Sheet sheet,String poisition,boolean autoCreateCell){
		if(poisition==null)
			return null;
		else{
			HSSFCell result=null;
			poisition=poisition.toUpperCase();
			int rowIndex=EasyMath.toInt(poisition.replaceAll("[A-Z]*",""))-1;
			int columnIndex=0;
			if(poisition.charAt(1)<'A'||poisition.charAt(1)>'Z'){
				columnIndex=poisition.charAt(0)-65;
			}
			else{
				columnIndex=(poisition.charAt(0)-64)*26+poisition.charAt(1)-65;
			}
			Row rowObj=sheet.getRow(rowIndex);
			if(rowObj==null&&autoCreateCell)
				rowObj=sheet.createRow(rowIndex);
			if(rowObj!=null) {
				result=(HSSFCell)rowObj.getCell(columnIndex);
				if(result==null&&autoCreateCell)
					result=(HSSFCell)rowObj.createCell(columnIndex);
			}
			return result;
		}
	}
	
	public static HSSFCell getCell(Sheet sheet,String poisition){
		return getCell(sheet,poisition,false);
	}
	
	public static boolean isDateStyle(Cell cell) {
		return isDateStyle(cell.getCellType(),cell.getCellStyle());
	}
	
	@SuppressWarnings("deprecation")
	public static boolean isDateStyle(int cellType,CellStyle cellstyle) {
		if(cellType==Cell.CELL_TYPE_NUMERIC||cellType==Cell.CELL_TYPE_FORMULA) {
			String dataFormatString=cellstyle.getDataFormatString();
			if(dataFormatString==null||cellstyle.getDataFormat()==0||dataFormatString.startsWith("0")||dataFormatString.equals("@"))
				return false;
			else if(dataFormatString.contains("m:s") || dataFormatString.contains("h:m"))
				return true;
		}
		return false;
 }
	
	public static boolean isDefaultStyle(Cell cell) {
    boolean isDefaultStyle=false;
   if(cell!=null) { 
     CellStyle nextStyle=cell.getCellStyle();
    if(nextStyle.getBorderLeft()==0&&nextStyle.getBorderRight()==0&&nextStyle.getBorderTop()==0&&nextStyle.getBorderBottom()==0) {
     isDefaultStyle=true; 
     }
   }
   return isDefaultStyle;
 }
	
	/** 
   * 复制原有sheet的合并单元格到新创建的sheet 
   *  
   * @param sheetCreat 新创建sheet 
   * @param sheet      原有的sheet 
   */  
   private static void copyMergedRegion(HSSFSheet fromSheet, HSSFSheet toSheet) {  
      int sheetMergerCount = fromSheet.getNumMergedRegions();  
      for (int i = 0; i < sheetMergerCount; i++) {  
       CellRangeAddress mergedRegionAt = fromSheet.getMergedRegion(i);  
       toSheet.addMergedRegion(mergedRegionAt);  
      }  
   } 
	
   /** 
    * 行复制功能 
    * @param fromRow 
    * @param toRow 
    */  
   private static void copyRow(HSSFWorkbook wb,HSSFRow fromRow,HSSFRow toRow,boolean copyValueFlag){  
       for (Iterator cellIt = fromRow.cellIterator(); cellIt.hasNext();) {  
           HSSFCell tmpCell = (HSSFCell) cellIt.next();  
           HSSFCell newCell = toRow.createCell(tmpCell.getColumnIndex());  
           copyCell(wb,tmpCell, newCell, copyValueFlag);  
       }  
   } 
   
   /** 
    * 复制单元格 
    * @param srcCell 
    * @param distCell 
    * @param copyValueFlag true则连同cell的内容一起复制 
    */  
   private static void copyCell(HSSFWorkbook wb,HSSFCell srcCell, HSSFCell distCell, boolean copyValueFlag) {  
       HSSFCellStyle newstyle=wb.createCellStyle();  
       copyCellStyle(srcCell.getCellStyle(), newstyle);  
       distCell.setCellStyle(newstyle);  
       if (srcCell.getCellComment() != null) {  
         distCell.setCellComment(srcCell.getCellComment());  
       }
       // 不同数据类型处理  
       int srcCellType = srcCell.getCellType();  
       distCell.setCellType(srcCellType);  
       if (copyValueFlag) {  
           if (srcCellType == HSSFCell.CELL_TYPE_NUMERIC) {  
               if (HSSFDateUtil.isCellDateFormatted(srcCell)) {  
                   distCell.setCellValue(srcCell.getDateCellValue());  
               } else {  
                   distCell.setCellValue(srcCell.getNumericCellValue());  
               }  
           } else if (srcCellType == HSSFCell.CELL_TYPE_STRING) {  
               distCell.setCellValue(srcCell.getRichStringCellValue());  
           } else if (srcCellType == HSSFCell.CELL_TYPE_BLANK) {  
               // nothing21  
           } else if (srcCellType == HSSFCell.CELL_TYPE_BOOLEAN) {  
               distCell.setCellValue(srcCell.getBooleanCellValue());  
           } else if (srcCellType == HSSFCell.CELL_TYPE_ERROR) {  
               distCell.setCellErrorValue(srcCell.getErrorCellValue());  
           } else if (srcCellType == HSSFCell.CELL_TYPE_FORMULA) {  
               distCell.setCellFormula(srcCell.getCellFormula());  
           } else { // nothing29  
           }  
       }  
   } 
   
   /** 
    * 复制一个单元格样式到目的单元格样式 
    * @param fromStyle 
    * @param toStyle 
    */  
   public static void copyCellStyle(HSSFCellStyle fromStyle,  
           HSSFCellStyle toStyle) {  
       toStyle.setAlignment(fromStyle.getAlignment());  
       //边框和边框颜色  
       toStyle.setBorderBottom(fromStyle.getBorderBottom());  
       toStyle.setBorderLeft(fromStyle.getBorderLeft());  
       toStyle.setBorderRight(fromStyle.getBorderRight());  
       toStyle.setBorderTop(fromStyle.getBorderTop());  
       toStyle.setTopBorderColor(fromStyle.getTopBorderColor());  
       toStyle.setBottomBorderColor(fromStyle.getBottomBorderColor());  
       toStyle.setRightBorderColor(fromStyle.getRightBorderColor());  
       toStyle.setLeftBorderColor(fromStyle.getLeftBorderColor());  
         
       //背景和前景  
       toStyle.setFillBackgroundColor(fromStyle.getFillBackgroundColor());  
       toStyle.setFillForegroundColor(fromStyle.getFillForegroundColor());  
         
       toStyle.setDataFormat(fromStyle.getDataFormat());  
       toStyle.setFillPattern(fromStyle.getFillPattern());  
//     toStyle.setFont(fromStyle.getFont(null));  
       toStyle.setHidden(fromStyle.getHidden());  
       toStyle.setIndention(fromStyle.getIndention());//首行缩进  
       toStyle.setLocked(fromStyle.getLocked());  
       toStyle.setRotation(fromStyle.getRotation());//旋转  
       toStyle.setVerticalAlignment(fromStyle.getVerticalAlignment());  
       toStyle.setWrapText(fromStyle.getWrapText());  
         
   }  
   
	/** 
   * Sheet复制 
   * @param fromSheet 
   * @param toSheet 
   * @param needCopyContent 
   */  
  public static void copySheet(HSSFWorkbook wb,HSSFSheet fromSheet, HSSFSheet toSheet,boolean needCopyContent) {
      //合并区域处理  
      copyMergedRegion(fromSheet, toSheet);  
      for (Iterator rowIt = fromSheet.rowIterator(); rowIt.hasNext();) {  
          HSSFRow tmpRow = (HSSFRow) rowIt.next();  
          HSSFRow newRow = toSheet.createRow(tmpRow.getRowNum());  
          //行复制  
          copyRow(wb,tmpRow,newRow,needCopyContent);  
      }  
  } 
  
  public static void copySheet(HSSFWorkbook wb,int sourceSheetIndex,int targetSheetIndex){
  	HSSFSheet sourceSheet = wb.getSheetAt(sourceSheetIndex);
  	HSSFSheet targetSheet = wb.getSheetAt(targetSheetIndex);
  	copySheet(wb, sourceSheet, targetSheet, true);
  }
	
	public static void main(String[] args) throws Exception{
		HSSFWorkbook wb=new HSSFWorkbook(new FileInputStream(new File("./testfiles/member.xls")));
		HSSFSheet sheet=wb.getSheetAt(0);
		short type=sheet.getRow(0).getCell(0).getCellStyle().getDataFormat();
		sheet.getRow(1).getCell(0).getCellStyle().setDataFormat(type);
		getCell(sheet,"AS3").setCellValue("大家好\nhere");
		getCell(sheet,"BB12",true).setCellValue("创建单元格并写入值");
		wb.write(new FileOutputStream(new File("./testfiles/member_outuput.xls")));
	}
}
