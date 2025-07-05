package com.fitai.service;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestConn {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/fitai_coach?useSSL=false&serverTimezone=Asia/Shanghai";
        String user = "root";
        String password = "0530";

        Connection conn = DriverManager.getConnection(url, user, password);
        System.out.println("连接成功！");
        conn.close();
    }
}
