package com.esdk.test;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import com.esdk.esdk;
import com.esdk.sql.ConnectionBuilder;
import com.esdk.sql.Select;
import com.esdk.sql.orm.ABResultSet;
import com.esdk.test.orm.CheckStopRow;
import com.esdk.utils.EasySql;
import com.esdk.utils.LoginAccount;
import com.esdk.utils.TestHelper;
import com.esdk.utils.EasyObj;

public class TestConnectionBuilder {

  private static void testsql(Connection conn) throws SQLException {
    /*
     * System.out.println(conn.getMetaData().getDatabaseProductName());
     * System.out.println(conn.getMetaData().supportsMixedCaseQuotedIdentifiers(
     * ));
     * System.out.println(conn.getMetaData().supportsMixedCaseIdentifiers());
     * System.out.println(conn.getMetaData().getSearchStringEscape());
     */
    CheckStopRow csr=new CheckStopRow();
    csr.refresh("AVASH",conn);
    TestHelper.assertEquals(csr.toXml(),
            "<CheckStop><pName>AVASH</pName><lastCheckTime>*</lastCheckTime><interTime></interTime><alertList>ffy,someone</alertList><mailList>project.ffy@pgl-world.com</mailList><Valid>true</Valid></CheckStop>");
    csr.setLastCheckTime(new BigDecimal("20090410130020"));
    csr.update();
    csr.flush();
  }

  private static void testmssql(Connection conn) throws SQLException{
    Select s=new Select("sysfiles",conn);
    TestHelper.assertEquals(s.toABResultSet().size(),2);
  }

  private static void testTableOperation(Connection conn) throws Exception{
    java.sql.Statement stmt=conn.createStatement();
    String createTableScript="CREATE TABLE \"AREA\"\r\n"+"(AreaID NUMERIC(18) PRIMARY KEY,\r\n"+" Code VARCHAR(40),\r\n"
            +" Name VARCHAR(100),\r\n"+" VALID BOOLEAN,\r\n"+" Remark VARCHAR(40)\r\n"+");";
    stmt.execute(createTableScript);
    String insert="INSERT INTO Area (AreaID,Code,Name,Valid,Remark) VALUES (1,\'gz\',\'广州\',\'true\',null)";
		/* stmt.execute(insert);
		TestHelper.assertEquals(new ABResultSet(stmt.executeQuery("select * from Area")).toXml(),
		        "<list><record><AREAID>1</AREAID><CODE>gz</CODE><NAME>广州</NAME><VALID>true</VALID><REMARK></REMARK></record></list>");*/
    stmt.execute("DROP TABLE Area");
  }

  private static void test() throws Exception{
    LoginAccount account=new LoginAccount("127.0.0.1","sa","sa");
    String dbname="sage";
    Connection mssqlconn= ConnectionBuilder.createConnection("com.microsoft.sqlserver.jdbc.SQLServerDriver",
            "jdbc:microsoft:sqlserver://"+account.getServerName()+":1433",account.getLoginName(),account.getPassword(),dbname);
    testmssql(mssqlconn);
    mssqlconn=ConnectionBuilder.createConnection(new File("./config/db.txt"));
    testsql(mssqlconn);
    Connection jtdsconn=ConnectionBuilder.createConnection("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://"+account.getServerName()+":1433",
            account.getLoginName(),account.getPassword(),dbname);
    testmssql(jtdsconn);
    jtdsconn=ConnectionBuilder.createConnection("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://"+account.getServerName()+":1433/"+dbname,
            account.getLoginName(),account.getPassword(),dbname);
    testmssql(jtdsconn);
    /*
     * Connection mysqlconn=createConnection("com.mysql.jdbc.Driver",
     * "jdbc:mysql://10.1.0.33:3306/edi?useUnicode=true&amp;characterEncoding=gb2312"
     * ,"root","",""); Tools.assertEquals(new
     * ABResultSet(mysqlconn.createStatement().executeQuery("select * from User"
     * )).toXml(),
     * "<list><record><userName>admin</userName><password>888888</password><purview>1</purview></record><record><userName>kama</userName><password>888888</password><purview>1</purview></record><record><userName>kama1</userName><password>888888</password><purview>1</purview></record><record><userName>kama2</userName><password>888888</password><purview>1</purview></record><record><userName>1</userName><password>1</password><purview>1</purview></record></list>"
     * );
     */
    Connection accessconn=ConnectionBuilder.createConnection("sun.jdbc.odbc.JdbcOdbcDriver",
            "jdbc:odbc:driver={Microsoft Access Driver (*.mdb)};DBQ=./config/config.mdb","admin","tomedi88","");
    esdk.tool.assertEquals(accessconn.getCatalog(),"./config/config");
    Connection hsqlconn=ConnectionBuilder.createConnection("org.hsqldb.jdbcDriver","jdbc:hsqldb:mem:./testfiles/hsqldb/testdb","sa","","");
    testTableOperation(hsqlconn);
    EasySql.close(hsqlconn);
  }

  public static void main(String[] args) throws Exception{
    test();
  }
}
