package com.uestc.javase.ORM.Reflect;

import java.sql.Connection;
import java.sql.DriverManager;
 
public class Connect2DBFactory {
	
	private final static String url = "jdbc:mysql://localhost:3306/webTest";
	
	private final static String user = "root";
	
	private final static String password = "xiyongfeng";
	
	private static Connection conn = null;
	
	//防止new一个Connect2DBFactory实例
	private Connect2DBFactory() {}
	
    public static Connection getDBConnection() {
        if(conn == null) {
            try {
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("----------------------------------数据库连接获取成功!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
        return conn;
    }
    
}
