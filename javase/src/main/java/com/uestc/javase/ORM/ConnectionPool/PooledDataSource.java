package com.uestc.javase.ORM.ConnectionPool;

import java.sql.Connection;

public interface PooledDataSource {
    /**
     * 获取链接
     * 
     */
    Connection getConnection() throws Exception;

    /**
     * 销毁
     * 
     */
    void destroy() throws Exception;

    /**
     * 释放
     * 
     */
    void release(Connection connection) throws Exception;

    /**
     * 数据源是否可用
     * 
     */
    boolean isAvailable();


}
