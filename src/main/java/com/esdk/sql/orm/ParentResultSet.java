package com.esdk.sql.orm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esdk.esdk;
import com.esdk.exception.SdkRuntimeException;
import com.esdk.sql.ISelect;
import com.esdk.sql.OrderBy;
import com.esdk.sql.RowExpression;
import com.esdk.sql.RowExpressions;
import com.esdk.sql.SQLRuntimeException;
import com.esdk.utils.Constant;
import com.esdk.utils.EasyStr;
import com.esdk.utils.JsonUtils;
import com.esdk.utils.EasyObj;

abstract public class ParentResultSet<T extends ParentRow> extends ABResultSet<T> {
	abstract public ParentRow createRowInstance();
  private HashMap<Object,ParentRow> primaryIdMap=new HashMap<Object,ParentRow>();
	protected final String _rowClsName=this.getClass().getName().replace("ResultSet","Row"); 

	public ParentResultSet(ResultSet rset) {
		super(rset);
	}

	@Override
	protected IRow getCurrent() throws SQLException {
		ParentRow result = createRowInstance();
		result.setConnection(rs.getStatement().getConnection());
		result.record=super.getCurrentMap();
		result.isExistRecord = true;
		return result;
	}

	public int indexOf(T row) {
		int index = rowList.indexOf(row);
		if (index >= 0)
			return index;
		else {
			for (int i = 0, n = this.size(); i < n; i++) {
				if (this.getRow(i).getPrimaryKey().equals(row.getPrimaryKey()))
					return i;
			}
			return -1;
		}
	}

	@Override
	public T getRow(int index) {
		int curPos = this.cursorPos;
		try {
			if(absolute(index)) {
				IRow result = this.getCurrentRow();
				this.cursorPos = curPos;
				return (T) result;
			}else {
				return null;
			}
		} catch (SQLException e) {
			this.cursorPos = curPos;
			throw new SQLRuntimeException(e);
		}
	}
	
  public T getFirstRow(boolean isCreateInstance) {
  	T result=(T)getRow(0);
  	if(result==null&&isCreateInstance) {
  		result=(T)createRowInstance();
  	}
  	return result;
  }
  
	protected Object gainCurrentRow(ResultSet rset) throws SQLException {
		esdk.tool.read(rset);
		return rowList.get(cursorPos);
	}

	@Override
	public String[] getColumns() {
		if (columns == null) {
			String classname = this.getClass().getName();
			classname = classname.substring(0, classname.length() - 9).concat("MetaData");
			try {
				columns = (String[]) Class.forName(classname).getField("FieldNames").get(this);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return columns;
	}

	@Override public JSONArray toJsonArray(boolean isFormatJavaBeanName){
  	try {
  		return JsonUtils.toJsonArray(this.getAllRows(),JsonUtils.IResultSetPropertyPreFilter);
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
  }
  
  public JSONArray toJsonArrayWithExtendFields(){
  	try {
  		List<T> list=this.getAllRows();
  		JSONArray result=new JSONArray();
  		for(T item:list) {
  			JSONObject jo=item.toJsonObject(true,true);
  			result.add(jo);
  		}
  		return result;
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
  }

	@Override public String toString(){
		return toJsonArray().toString();
	}
  
	public JSONArray toJsonArray(final String... columns) {
		String[] excludeColumns = EasyStr.remove(getColumns(), columns);
		for (int i = 0; i < excludeColumns.length; i++) {
			excludeColumns[i] = EasyStr.toCamelCase(excludeColumns[i]);
		}
		excludeColumns=esdk.str.toArray(excludeColumns,RowUtils.Keywords);
		try {
			return JsonUtils.toJsonArray(this.getAllRows(),JsonUtils.getPropertyPreFilter(excludeColumns));
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}

	@Override public JSONArray toJsonArray(int start, int limit) {
		try {
			return JsonUtils.toJsonArray(this.getSubList(start, limit),JsonUtils.IResultSetPropertyPreFilter);
		} catch (SQLException e) {
			throw new SQLRuntimeException(e);
		}
	}

	@Override
	public ParentResultSet sort(boolean isDesc, String... cols) {
		return (ParentResultSet) super.sort(isDesc, cols);
	}

	@Override
	public ParentResultSet sort(String... columnKeys) {
		return (ParentResultSet) super.sort(columnKeys);
	}

	@Override
	public ParentResultSet sort(OrderBy... cols) {
		return (ParentResultSet) super.sort(cols);
	}

	public List<T> toBeanList(Class<T> beanCls) {
		return toBeanList(beanCls, 0, size());
	}

	public List<T> toBeanList(Class<T> beanCls, int start, int limit) {
		try {
			String beanClassName = beanCls.getName();
			beanClassName = beanClassName.substring(0, beanClassName.length() - 9).concat("Bean");
			Class cls = Thread.currentThread().getContextClassLoader().loadClass(beanClassName);
			List result = new ArrayList<List>();
			List rowList = getSubList(start, limit);
			for (Iterator iter = rowList.iterator(); iter.hasNext();) {
				IRow row = (IRow) iter.next();
				Object pojo = cls.newInstance();
				result.add(RowUtils.loadFrom(pojo, row, false));
			}
			return result;
		} catch (Exception e) {
			throw new SdkRuntimeException(e);
		}
	}
	
	@Override protected ParentResultSet clone() throws CloneNotSupportedException{
		try{
			java.lang.reflect.Constructor constructor=this.getClass().getConstructor(new Class[]{ResultSet.class});
			ParentResultSet result=(ParentResultSet)constructor.newInstance(new Object[]{rs});
			result.rowList=this.rowList;
			result.rsmd=this.rsmd;
			result.mapColumnClass=this.mapColumnClass;
			result.columns=this.columns;
			return result;
		}catch(Exception e){
				throw new SdkRuntimeException(e);
		}
	}
	
	@Override void addRowList(IRow currentRow) throws SQLException{
		super.addRowList(currentRow);
		ParentRow prow=(ParentRow)currentRow;
		primaryIdMap.put(prow.getPrimaryKey(),(T)prow);
	}
	
	/**直接通过主键ID获取记录，比使用filter(id,1111)的方式的效率更高，使用更简单*/
	public T findById(Integer primaryId){
		size();
		return (T)primaryIdMap.get(primaryId);
	}
  
	/**left join操作，类似于select的left join处理逻辑，适合处理1对1或1对N
	 * appendColumns：增加字段如果为空则获取记录集的所有字段（注意会排除重复字段）
	 * onFieldsMap：字段匹配条件，只能支持字段值匹配，不能指定常量值匹配，如有常量值条件应先执行过滤条件处理。
	 * 							key为原记录集的字段名，value为join记录集的字段名。
	 *              为null时会以两个记录集的重叠字段（去掉公用字段和主键）做为匹配条件。
	 * */
	public <PRS extends ParentResultSet> PRS join(IRowSet rs,String[] appendColumns,Map<String,String> onFieldsMap){
		if(rs==null || rs.size()==0)
			return (PRS)this;
		if(esdk.obj.isBlank(appendColumns)) {
			appendColumns=rs.getColumnNames();
		}
		appendColumns=esdk.str.remove(appendColumns,this.getColumnNames());
		columns=esdk.str.toArray(columns,appendColumns);
		if(onFieldsMap==null || onFieldsMap.keySet().size()==0) {
			String[] joinFields=esdk.array.overlap(this.getColumnNames(),rs.getColumnNames());
			joinFields=esdk.str.remove(esdk.str.remove(joinFields,Constant.SystemFields),"id");
			onFieldsMap=new LinkedHashMap();
			for(String field:joinFields) {
				onFieldsMap.put(field,field);
			}
		}
		for(IRow row:this) {
			RowExpressions exps=new RowExpressions();
			for(Entry<String,String> entry:onFieldsMap.entrySet()) {
				exps.add(RowExpression.create(null,entry.getValue(),row.get(entry.getKey())));
			}
			IRow findRow=rs.filter(1,exps).getFirstRow();
			if(findRow!=null)
				((ABRow)row).load(findRow.toMap(false,appendColumns));
		}
		return (PRS)this;
	}
	
	@Override
	public ParentResultSet update(Consumer<T> fn) throws Exception{
		return (ParentResultSet)super.update(fn);
	}
}
