package cn.edu.bupt.springmvc.core.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by limingkun on 2017/5/6.
 */
public class HiveConnection {

    private static String driverName = "org.apache.hive.jdbc.HiveDriver";

    private static Connection con = null;

    private HiveConnection(){
    }

    public static Connection getHiveConnection() throws SQLException, ClassNotFoundException {

        if (con == null){
            Class.forName(driverName);
            con = DriverManager.getConnection("jdbc:hive2://slave2.hadoop:10000/default", "root", "p@ssw0rd#CADhadoop");
        }
        return con;
    }
}
