package com.zrc.demo.execute;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
public class SybaseJDBC {
 public static void main(String[] args) throws Exception{
	 Class.forName("com.sybase.jdbc4.jdbc.SybDataSource");
	 Connection conn = DriverManager.getConnection("jdbc:sybase:Tds:HYUAT.hyvesolutions.org:5100/CIS", "cron_uat", "synnex");
	 CallableStatement cs = conn.prepareCall("{? = call rio_auto_release_to_inv(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)}");
     cs.registerOutParameter(1, Types.INTEGER);
     cs.setObject(2, 303703);
     cs.setObject(3, 4);
     cs.setObject(4, -1);
     cs.setObject(5, 4157519);
     cs.setObject(6, 197);
     cs.setObject(7, 1);
     cs.setObject(8, 698615);
     cs.setNull(9, Types.INTEGER);
     cs.registerOutParameter(10, java.sql.Types.VARCHAR);
     cs.setObject(11, "N");
     long start = System.currentTimeMillis();
     cs.execute();
     long end = System.currentTimeMillis();
     System.err.println("Spend-------->"+(end-start)+"ms!");
     Object objRtn = cs.getObject(1);      //得到返回值
     System.out.println("RETURN_STATUS:"+objRtn);

     cs.close();
     conn.close();
     Map<String,Object> spResult = new HashMap<>();
     spResult.put("RETURN_VALUE",objRtn);
     System.out.println(spResult);
}
}
