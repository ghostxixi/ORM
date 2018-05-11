package com.uestc.javase.ORM.ConnectionPool;

import java.sql.Connection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

	public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        final PooledDataSourceImpl dataSource = new PooledDataSourceImpl(conf);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        Runnable r = new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 4; i++) {
                        Connection connection = dataSource.getConnection();
                        System.out.println(Thread.currentThread().getName()
                                + " : " + connection);
                        Thread.sleep(300);
                        dataSource.release(connection);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        for (int i = 0; i < 5; i++) {
            executor.execute(r);
        }
        executor.shutdown();

    }
}
