package com.esdk.interfaces;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.CellStyle;

public interface POIHandler {
	public static int CELL_TYPE_NUMERIC = HSSFCell.CELL_TYPE_NUMERIC; //0
	public static int CELL_TYPE_STRING = HSSFCell.CELL_TYPE_STRING;  //1
	public static int CELL_TYPE_FORMULA = HSSFCell.CELL_TYPE_FORMULA;  //2
	public static int CELL_TYPE_BLANK = HSSFCell.CELL_TYPE_BLANK;  //3
	public static int CELL_TYPE_BOOLEAN = HSSFCell.CELL_TYPE_BOOLEAN;  //4
	public static int CELL_TYPE_ERROR = HSSFCell.CELL_TYPE_ERROR;  //5
	public static int CELL_TYPE_DATE = 6;
	
	int getCellType(int row,int col,int cellType,CellStyle cellstyle);

}
