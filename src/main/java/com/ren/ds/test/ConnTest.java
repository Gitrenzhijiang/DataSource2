package com.ren.ds.test;

import java.sql.Connection;

import com.ren.ds.pool.DataSource;

public class ConnTest {

    public static void main(String[] args) throws Exception {
        DataSource datasource = new DataSource();
        
        for (int i = 0;i < 3;i++) {
            Connection conn = datasource.getConnection();
            System.out.println(conn);
            conn.close();
        }
        Thread.sleep(6000);
        Connection conn = datasource.getConnection();
        System.out.println(conn);
        conn.close();
    }

}
