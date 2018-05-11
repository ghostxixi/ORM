package com.uestc.javase.ORM.ConnectionPool;

import java.util.ResourceBundle;

public class Configuration {
    private ResourceBundle resource;
    private int initialPoolSize = 3;
    private int minPoolSize = 3;
    private int maxPoolSize = 10;
    private int maxStatements = 30;
    private int maxIdleTime = 25000;
    private int idleConnectionTestPeriod = 18000;
    private String url = "jdbc:mysql://localhost:3306/webTest";
    private String user = "root";
    private String password = "xiyongfeng";

    public Configuration() {
    }

    public Configuration(String properties) {
        init(properties);
    }

    /**
     * 
     * @param properties
     * 
     */
    private void init(String properties) {
        resource = ResourceBundle.getBundle(properties);
        try {
            String tmp;
            tmp = resource.getString("initialPoolSize");
            if (tmp != null) {
                setInitialPoolSize(Integer.parseInt(tmp));
            }
            tmp = resource.getString("minPoolSize");
            if (tmp != null) {
                setMinPoolSize(Integer.parseInt(tmp));
            }
            tmp = resource.getString("maxPoolSize");
            if (tmp != null) {
                setMaxPoolSize(Integer.parseInt(tmp));
            }
            tmp = resource.getString("maxStatements");
            if (tmp != null) {
                setMaxStatements(Integer.parseInt(tmp));
            }
            tmp = resource.getString("maxIdleTime");
            if (tmp != null) {
                setMaxIdleTime(Integer.parseInt(tmp));
            }
            tmp = resource.getString("idleConnectionTestPeriod");
            if (tmp != null) {
                setIdleConnectionTestPeriod(Integer.parseInt(tmp));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ResourceBundle getResource() {
        return resource;
    }

    public void setResource(ResourceBundle resource) {
        this.resource = resource;
    }

    public int getInitialPoolSize() {
        return initialPoolSize;
    }

    public void setInitialPoolSize(int initialPoolSize) {
        this.initialPoolSize = initialPoolSize;
    }

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public void setMinPoolSize(int minPoolSize) {
        this.minPoolSize = minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getMaxStatements() {
        return maxStatements;
    }

    public void setMaxStatements(int maxStatements) {
        this.maxStatements = maxStatements;
    }

    public int getMaxIdleTime() {
        return maxIdleTime;
    }

    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
    }

    public int getIdleConnectionTestPeriod() {
        return idleConnectionTestPeriod;
    }

    public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
        this.idleConnectionTestPeriod = idleConnectionTestPeriod;
    }
    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
