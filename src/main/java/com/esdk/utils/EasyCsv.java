package com.esdk.utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esdk.esdk;

public class EasyCsv {
	public static final char COMMA = ',';  //字段分隔符
	public static final char QUOTA = '"';  //同一字段的字符串限定符
	public static final char CR = '\n';// 0a
	public static final char FL = '\r';// 0d
	public static final char TextFlag= '\'';	//限定字段是字符串型的限定标记
	public static final String CRLF = "\r\n"; //换行符

	public static String[][] fromCsv(String src) {
		return fromCsv(src,true);
	}
	
	public static boolean toCsvFile(String[][] data,File file) throws IOException {
		return EasyFile.saveToFile(toCsv(data),file,true,Constant.UTF8);
	}
	
	public static String[][] fromCsvFile(File csvfile) throws IOException{
		return fromCsv(EasyFile.loadFromFile(csvfile));
	}
	public static String[][] fromCsv(String src,String delimit){
		return fromCsv(src,delimit,String.valueOf(QUOTA),CRLF,false);
	}

	public static String[][] fromCsv(String src,boolean trim) {
		if(!src.endsWith(CRLF))
			src +=CRLF;
		ArrayList<String[]> result = new ArrayList<String[]>();
		ArrayList<String> row = new ArrayList<String>();
		StringBuffer cell = new StringBuffer();// 記錄每個單元格的內容
		StringBuffer bufferLine = new StringBuffer();// 記錄每行內容,用于報錯信息
		boolean parseQuotaField = false;
		for (int i = 0, line = 1; i < src.length(); ++i) {
			bufferLine.append(src.charAt(i));
			if (src.charAt(i) == CR) {
				line++;
				bufferLine.setLength(0);
			}
			if (parseQuotaField) {
				if (src.charAt(i) == QUOTA) {
					i++;
					if (i == src.length()) {
						// 完成全部处理
						row.add(getCell(cell,trim));
						result.add(listToArray(row));
						continue;
					}
					switch (src.charAt(i)) {
					case QUOTA:
						cell.append(QUOTA);
						continue;
					case COMMA:
						// 完成1个field
						row.add(getCell(cell,trim));
						cell.setLength(0);
						parseQuotaField = false;
						continue;
					case CR:
					case FL:
						// 有(")符号包围,完成一行读取
						row.add(getCell(cell,trim));
						result.add(listToArray(row));
						row.clear();
						cell.setLength(0);
						parseQuotaField = false;
						i++;
						continue;
					default:
						throw new RuntimeException(String.format("无法辨识第%d行:[%s]", line, bufferLine.toString()));
					}
				} else {
					cell.append(src.charAt(i));
				}
			} else {
				switch (src.charAt(i)) {
					case COMMA:
						// 完成1个field的读取
						row.add(getCell(cell,trim));
						cell.setLength(0);
						continue;
					case CR:
						// 无(\")完成一行的读取
						row.add(getCell(cell,trim));
						result.add(listToArray(row));
						row.clear();
						cell.setLength(0);
					case FL:
						continue;
					case QUOTA:
						if (cell.length() != 0)
							throw new RuntimeException(String.format("无法辨识第%d行:[%s]", line, bufferLine.toString()));
						else
							parseQuotaField = true;
						continue;
					default: {
						cell.append(src.charAt(i));
						if (i == src.length()-1) {
							// 完成全部处理
							row.add(getCell(cell,trim));
							result.add(listToArray(row));
							continue;
						}
					}
				}
			}
		}
//		return EasyStr.ListToArr(result);
		return (String[][])result.toArray(new String[0][]);
	}

	public static String[][] fromCsv(String src, String breaksign, String quotaStr,
										String lineEndStr, boolean forceText) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		ArrayList<String> row = new ArrayList<String>();
		StringBuffer source = new StringBuffer(src);
		int fromIndex = 0;
		int lineEndPos = indexOfEnd(source, lineEndStr, quotaStr, fromIndex,-1);
		if (lineEndPos < 0 )
			lineEndPos = source.length();
		while(lineEndPos > 0) {
			getRow(row,source.substring(fromIndex, lineEndPos),breaksign,quotaStr,forceText);
			result.add(listToArray(row));
			row.clear();
			fromIndex = lineEndPos + lineEndStr.length();
			if (fromIndex > source.length())
				break;
			lineEndPos = indexOfEnd(source,lineEndStr,quotaStr,fromIndex,-1);
			if (lineEndPos < 0)
				lineEndPos = source.length();
		}
		return (String[][])result.toArray(new String[0][]);
	}
	
	private static void getRow(List<String> row, String src, String breaksign, String quotaStr,
								boolean forceText) {
		StringBuffer source =  new StringBuffer(src);
		int fromIndex = 0;
		int cellEndPos = indexOfEnd(source,breaksign,quotaStr,fromIndex,-1);
		if (cellEndPos < 0)
			cellEndPos = source.length();
		while(cellEndPos >0) {
			String cell = getCell(source.substring(fromIndex, cellEndPos),quotaStr,forceText);
			row.add(cell);
			fromIndex = cellEndPos + breaksign.length();
			if (fromIndex >= source.length())
				break;
			cellEndPos = indexOfEnd(source,breaksign,quotaStr,fromIndex,-1);
			if (cellEndPos < 0)
				cellEndPos = source.length();
		}
		source.delete(0, source.length());
	}

	/**
	 * 定位从fromIndex位置开始,不在quotaStr包围中的分隔符位置
	 */
	private static int indexOfEnd(StringBuffer src, String findStr,String quotaStr, int fromIndex,int nextFromIndex) {
		int result = -1;
		int lineEndPos = -1;
		if (nextFromIndex <= 0)
			lineEndPos = src.indexOf(findStr, fromIndex);
		else 
			lineEndPos = src.indexOf(findStr,nextFromIndex);
		
		if (lineEndPos < 0)
			return result;
		int quotaPos = src.indexOf(quotaStr, fromIndex);
		boolean isGeminate = true;
		if (quotaPos >= 0)
			isGeminate = isGeminate(src,quotaStr,fromIndex,lineEndPos);
		if (!isGeminate && fromIndex <= quotaPos && quotaPos<lineEndPos) {
			result = indexOfEnd(src,findStr,quotaStr,fromIndex,lineEndPos+findStr.length());
		}else {
			result = lineEndPos;
		}
		return result;
	}
	
	/**
	 * 判断要查找的字符串是否成对出现,例如有双数个双引号""
	 */
	private static boolean isGeminate(StringBuffer src,String findStr,int fromIndex,int endIndex) {
		int count =0; 
		int ipos = src.indexOf(findStr,fromIndex);
		while(ipos>=0 && ipos>=fromIndex && ipos<=endIndex) {
			count++;
			ipos = src.indexOf(findStr,ipos + findStr.length());
		}
		return count % 2 == 0;
	}
	
	private static String getCell(String cell,String quotaStr, boolean forceText) {
		cell = cell.replaceAll(quotaStr+quotaStr, quotaStr);
		if (cell.startsWith(quotaStr) && cell.endsWith(quotaStr))
			return cell.substring(quotaStr.length(), cell.length()-quotaStr.length());
		return forceText&&cell.indexOf("'")==0?cell.substring(1):cell;
	}

	private static String getCell(StringBuffer cell,boolean trim) {
		String result=cell.indexOf("'")==0?cell.substring(1):cell.toString();
		if(trim)
			result=result.trim();
		return result;
	}
	
	private static String[] listToArray(ArrayList<String> row) {
		return (String[]) row.toArray(new String[0]);
	}

	public static String toCsv(String[][] csv) {
		return toCsv(csv, ",", CRLF,false);
	}
	public static String toCsv(String[][] csv,boolean forceText,boolean isTrim) {
		return toCsv(csv, ",", CRLF,forceText,isTrim);
	}
	public static String toCsv(String[][] csv, String delimit, String endlinesign) {
		return toCsv(csv,delimit,endlinesign,false);
	}
	public static String toCsv(String[][] csv, String delimit, String endlinesign,boolean forceText) {
		return toCsv(csv,delimit,endlinesign,forceText,false);
	}
	public static String toCsv(String[][] csv, String delimit, String endlinesign,boolean forceText,boolean isTrim) {
		StringBuffer result = new StringBuffer();
		int i, j;
		for (i = 0; i < csv.length; i++) {
			for (j = 0; j < csv[i].length; j++) {
				if (csv[i][j] == null) {
					csv[i][j] = "";
				} else
					result.append(csvEncode(isTrim?csv[i][j].trim():csv[i][j],forceText));
				if (j < csv[i].length - 1)
					result.append(delimit);
			}
			if (i < csv.length - 1) {
				result.append(endlinesign);
			}
		}
		return result.toString();
	}

	public static String csvEncode(String cell) {
		return csvEncode(cell,false);
	}
	public static String csvEncode(String cell,boolean forceText) {
		return csvEncode(cell,forceText,String.valueOf(QUOTA));
	}
	public static String csvEncode(String cell,boolean forceText,String quotaStr) {
		String result = null;
		if(forceText)
			cell=TextFlag+cell;
		if (cell.indexOf(quotaStr) >= 0)
			result = quotaStr + cell.replaceAll(quotaStr, quotaStr+quotaStr) + quotaStr;
		else if (cell.matches(".*\\r?[\\n,].*")) {
			result = quotaStr + cell + quotaStr;
		} else
			result = cell;
		return result;
	}

	public static String csvDecode(String cell) {
		return csvDecode(cell,String.valueOf(QUOTA));
	}
	public static String csvDecode(String cell,String quotaStr) {
		String result = cell.replaceAll(quotaStr+quotaStr, quotaStr);
		Pattern p = Pattern.compile(quotaStr+"(.*)"+quotaStr, Pattern.DOTALL);
		Matcher m = p.matcher(result);
		if (m.matches())
			result = m.group(1);
		return result;
	}

	private static void test() {
		try {
			esdk.tool.assertEquals(csvEncode("a,b"), "\"a,b\"");
			esdk.tool.assertEquals(csvEncode("a\nb"), "\"a\nb\"");
			esdk.tool.assertEquals(csvEncode("a\"b"), "\"a\"\"b\"");
			esdk.tool.assertEquals(csvDecode("\"a,b\""), "a,b");
			esdk.tool.assertEquals(csvDecode("\"a\nb\""), "a\nb");
			esdk.tool.assertEquals(csvDecode("\"a\"\"b\""), "a\"b");
			String csv = "\"a\"\"B\",ts\r\n\"d\r\ne\",\r\n\"f,g\",\r\n\r\nlast";
			esdk.tool.assertEquals(toCsv(fromCsv(csv)), csv);
			esdk.tool.assertEquals(toCsv(fromCsv(csv),true,false).replaceAll("'",""),csv);
			String csv1=EasyFile.loadFromFile("./testfiles/test.csv");
			String[][] array = fromCsv(csv1);
			EasyStr.equals(toCsv(array),csv1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		test();
	}
}
