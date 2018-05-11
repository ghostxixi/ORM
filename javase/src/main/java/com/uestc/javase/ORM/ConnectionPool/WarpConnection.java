package com.uestc.javase.ORM.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class WarpConnection {
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private String name;
    private long connectionTime;
    private long lastWorkTime;
    private Connection connection;

    public long getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(long connectionTime) {
        this.connectionTime = connectionTime;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static WarpConnection warp(Connection connection) {
        WarpConnection warpConnection = new WarpConnection();
        warpConnection.setConnection(connection);
        warpConnection.setConnectionTime(System.currentTimeMillis());
        warpConnection.setName("name" + atomicInteger.getAndAdd(1));
        return warpConnection;
    }

    public boolean isTimeOut(long time) {
        boolean flag = System.currentTimeMillis() - this.connectionTime >= time;
        return flag;
    }

    public long getLastWorkTime() {
        return lastWorkTime;
    }

    public void setLastWorkTime(long lastWorkTime) {
        this.lastWorkTime = lastWorkTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((connection == null) ? 0 : connection.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WarpConnection other = (WarpConnection) obj;
        if (connection == null) {
            if (other.connection != null)
                return false;
        } else if (!connection.equals(other.connection))
            return false;
        return true;
    }

    /**
     * 查看链接是否有效
     * 
     * @param connectionLonger
     *            连接最大时间
     * @return
     * 
     */
    public boolean veryfiConnection(int connectionLonger) {
        try {
            if (this.connection == null || this.connection.isClosed()
                    || isTimeOut(connectionLonger)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
