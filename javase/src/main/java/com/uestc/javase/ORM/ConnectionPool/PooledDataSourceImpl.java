package com.uestc.javase.ORM.ConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PooledDataSourceImpl {

	protected ConcurrentLinkedQueue<WarpConnection> idleQueue;
    protected ConcurrentLinkedQueue<WarpConnection> busyQueue;
    protected ThreadLocal<Connection> threadLocal;
    protected AtomicInteger totalSize;
    //protected AtomicInteger currentSize;
    protected boolean available;
    protected Configuration configuration;
    final Lock lock = new ReentrantLock();// 锁
    final Condition notEmpty = lock.newCondition();

    public PooledDataSourceImpl(Configuration configuration)
            throws Exception {
        this.configuration = configuration;
        idleQueue = new ConcurrentLinkedQueue<WarpConnection>();
        busyQueue = new ConcurrentLinkedQueue<WarpConnection>();
        threadLocal = new ThreadLocal<Connection>();
        totalSize = new AtomicInteger(0);
        //currentSize = new AtomicInteger(0);
        init();
    }

    private void init() throws Exception {
        for (int i = 0; i < configuration.getInitialPoolSize(); i++) {
            idleQueue.add(WarpConnection.warp(openConnection()));
        }
        totalSize.set(configuration.getInitialPoolSize());
        available = true;
    }

    protected Connection openConnection() throws SQLException {
        return DriverManager.getConnection(configuration.getUrl(),configuration.getUser(),configuration.getPassword());
    }

    public Connection getConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if (connection != null) {
        	System.out.println(Thread.currentThread().getName() + ": get connection from threadLocal");
            return connection;
        }
        try {
            lock.lock();
            WarpConnection warpConnection = null;
            try {
                warpConnection = idleQueue.remove();
                System.out.println(Thread.currentThread().getName() + ": get connection from idleQueue --- idleQueue.size:" + idleQueue.size());
            } catch (NoSuchElementException e) {
                warpConnection = getWarpConnection();
                System.out.println(Thread.currentThread().getName() + ": get new connection");
            }
            veryfiConnection(warpConnection);
            warpConnection.setLastWorkTime(System.currentTimeMillis());
            busyQueue.add(warpConnection);
            //System.out.println("getConnection:" + busyQueue.size());
            threadLocal.set(warpConnection.getConnection());
            return warpConnection.getConnection();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查链接状态
     * 
     */
    private void veryfiConnection(WarpConnection warpConnection)
            throws SQLException {
        if (warpConnection.veryfiConnection(configuration.getIdleConnectionTestPeriod())) {
            warpConnection.setConnection(openConnection());
            warpConnection.setConnectionTime(System.currentTimeMillis());
        }
    }


    private WarpConnection getWarpConnection() throws SQLException {
        WarpConnection warpConnection = null;

        if (totalSize.get() < configuration.getMaxPoolSize()) {
            warpConnection = WarpConnection.warp(openConnection());
            totalSize.addAndGet(1);
            return warpConnection;
        }
        while (true) {
            try {
                warpConnection = idleQueue.remove();
                return warpConnection;
            } catch (NoSuchElementException e) {
                try {
                    notEmpty.wait();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }

    public void destroy() {
        available = false;
        ConcurrentLinkedQueue<WarpConnection> _idleQueue = idleQueue;
        //ConcurrentLinkedQueue<WarpConnection> _busyQueue = busyQueue;
        idleQueue = null;
        busyQueue = null;
        threadLocal = null;
        for (WarpConnection connection : _idleQueue) {
            closeQuiet(connection.getConnection());
        }
    }

    private void closeQuiet(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void release(Connection connection) throws Exception {
        try {
            lock.lock();
            if (available) {
                WarpConnection warpConnection = null;
                for (WarpConnection element : busyQueue) {
                    if (element.getConnection().equals(connection)) {
                        warpConnection = element;
                        break;
                    }
                }
                busyQueue.remove(warpConnection);
                idleQueue.add(warpConnection);
                //System.out.println("busyQueue = " + busyQueue.size());
                System.out.println(Thread.currentThread().getName() + " release " + connection +":idleQueue = " + idleQueue.size());
                threadLocal.set(null);
                notEmpty.signal();// 一旦插入就唤醒取数据线程
            } else {
                closeQuiet(connection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public boolean isAvailable() {
        return available;
    }

}
