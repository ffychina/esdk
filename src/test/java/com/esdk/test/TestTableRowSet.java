package com.esdk.test;

import com.esdk.sql.Select;
import com.esdk.sql.datasource.FileConnectionPool;
import com.esdk.sql.orm.IRow;
import com.esdk.sql.orm.ORMSessionBuilder;
import com.esdk.sql.orm.TableResultSet;
import com.esdk.sql.orm.TableRow;
import com.esdk.test.orm.CheckStopMetaData;
import com.esdk.utils.TestHelper;

import java.util.Date;
import java.util.List;

public class TestTableRowSet {
  private static void test() {
    try{
      ORMSessionBuilder sb=new ORMSessionBuilder(FileConnectionPool.getConnection());
      Select s=sb.createSelect(CheckStopMetaData.TABLENAME);
      s.setTop(0);
      TableResultSet trs=new TableResultSet(s);
      int size=trs.size();
      TestHelper.assertEquals(size,5);
      TestHelper.assertEquals(trs.first(),true);
      TestHelper.assertEquals(trs.last(),true);
      TestHelper.assertEquals(trs.relative(-1),true);
      TestHelper.assertEquals(!trs.isChanged());
      TableRow updaterow=trs.updateRow();
      updaterow.set(CheckStopMetaData.alertList,"ffyproject");
      TestHelper.assertEquals(updaterow,trs.getCurrentRow());
      List<IRow> tableRowList=trs.getAllRows();
      for(int i=0;i<tableRowList.size();i++){
        if(tableRowList.get(i).get(CheckStopMetaData.pName).toString().equals("testbyffy")) {
          ((TableRow)tableRowList.get(i)).delete();
          trs.save((TableRow)tableRowList.get(i));
          TestHelper.assertEquals(trs.isChanged());
        }
      }
      TableRow insertRow=trs.insertRow();
      TestHelper.assertEquals(trs.size(),size+1);
      insertRow.set(CheckStopMetaData.pName,"testbyffy");
      insertRow.set(CheckStopMetaData.lastCheckTime,new Date());
      insertRow.set(CheckStopMetaData.mailList,"project.ffy@pgl-world.com");
      insertRow.set(CheckStopMetaData.alertList,"ffyproject");
      TestHelper.assertEquals(trs.flush());
      TestHelper.assertEquals(insertRow,trs.deleteRow());
      TestHelper.assertEquals(trs.size(),size);
      updaterow.set(CheckStopMetaData.alertList,"ffy,someone");
      TestHelper.assertEquals(trs.flush());
      TestHelper.assertEquals(!trs.isChanged());
      insertRow.insert();
      TestHelper.assertEquals(!insertRow.isChanged());
      insertRow.set(CheckStopMetaData.alertList,"ffy");
      TestHelper.assertEquals(insertRow.isChanged());
      TestHelper.assertEquals(insertRow.flush());
      TestHelper.assertEquals(trs.flush());
      insertRow.refresh();
      TestHelper.assertEquals(!trs.contains(insertRow));
      insertRow.delete();
      insertRow.flush();
      TestHelper.assertEquals(!trs.contains(insertRow));
      TestHelper.assertEquals(trs.size(),size);
      trs.first();
      System.out.println("排序之前的位置:"+trs.getCursor());
      trs.sort(true,new String[] {CheckStopMetaData.pName,CheckStopMetaData.lastCheckTime,CheckStopMetaData.Valid});
      TestHelper.assertEquals(trs.getCursor(),4);
      System.out.println("排序之后的位置:"+trs.getCursor());
      trs.sort(true,new String[] {CheckStopMetaData.lastCheckTime});
      IRow currentRow=trs.getCurrentRow();
      trs.moveTo(currentRow,trs.getCursor()+2);
      TestHelper.assertEquals(trs.indexOf(currentRow),trs.getCursor()+1);//移动位置在当前位置之后,因此移动之后比新位置减1
      TestHelper.assertEquals(trs.size(),size);
      trs.moveTo(currentRow,1);
      TestHelper.assertEquals(trs.indexOf(currentRow),1);
      TestHelper.assertEquals(trs.size(),size);
      sb.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  public static void main(String[] args){
    test();
  }

}
