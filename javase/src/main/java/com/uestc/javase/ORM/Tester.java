package com.uestc.javase.ORM;

import java.util.List;

public class Tester {
	
	private static Class<?> name;
	
    public static void main(String args[]) {
    	
		getCaller();
    	//UserInfo user = new UserInfo();
    	//user.setId(6991);
        //user.setAge(66);
        //user.setPwd("pwd");
        //user.setName("champion");
    	//System.out.println("获取到的信息：" + NetJavaSession.getDeleteObjectSql(user));
    	//System.out.println(NetJavaSession.getObject(UserInfo.class,"cha"));
    	//List<Object> list = NetJavaSession.getAllObject(UserInfo.class);
        
        //for(Object u : list) {
        //	UserInfo ui = (UserInfo)u;
        //	System.out.println("---" + ui.toString());
        //}
        //创建一个UserInfo对象
        /*UserInfo user = new UserInfo();
        //设置对象的属性
        user.setId(6992);
        user.setAge(46);
        user.setPwd("pwd");
        user.setName("champion");
        //将对象保存到数据库中
        NetJavaSession.saveObject(user);
        //查找对象
        UserInfo userInfo = (UserInfo) NetJavaSession.getObject("reflect.UserInfo", 6992);
        System.out.println("获取到的信息：" + userInfo);
        
        List<?> list = NetJavaSession.getDatasFromDB("reflect.UserInfo");
        
        for(Object u : list) {
        	UserInfo ui = (UserInfo)u;
        	System.out.println("---" + ui.toString());
        }*/
    }
    
    private static void getCaller() {
    	//拿到调用此方法的类名
    	new SecurityManager() {
            {
                name = getClassContext()[1];
            }
        };
        System.err.println(name.getName());
    }
    
}
