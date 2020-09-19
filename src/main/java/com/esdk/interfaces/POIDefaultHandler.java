package com.esdk.interfaces;

import org.apache.poi.ss.usermodel.CellStyle;

import com.esdk.utils.POIUtil;

public class POIDefaultHandler implements POIHandler{
	@Override public int getCellType(int row,int col,int cellType,CellStyle cellstyle){
		if(POIUtil.isDateStyle(cellType,cellstyle))
			cellType = POIHandler.CELL_TYPE_DATE;
		return cellType;
	}
}
