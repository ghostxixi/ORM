package com.uestc.javase.ORM.Reflect;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NetJavaSession {
	
	
	/**
	 * 解析出保存对象的sql语句
	 *
	 * @param object
	 *            ：需要保存的对象
	 * @return：保存对象的sql语句
	 */
	public static String getSaveObjectSql(Object object) {
		// 定义一个sql字符串
		String sql = "insert into ";
		// 得到对象的类
		Class<?> c = object.getClass();
		String tableName = c.getSimpleName();
		sql += tableName + "(";
		List<String> mList = new ArrayList<String>();
		List<Object> vList = new ArrayList<Object>();
		resolveValues(object,mList,vList);
		for (int i = 0; i < mList.size(); i++) {
			if (i < mList.size() - 1) {
				sql += mList.get(i) + ",";
			} else {
				sql += mList.get(i) + ") values(";
			}
		}
		for (int i = 0; i < vList.size(); i++) {
			if (i < vList.size() - 1) {
				sql += vList.get(i) + ",";
			} else {
				sql += vList.get(i) + ")";
			}
		}
		System.out.println("保存对象的sql语句：" + sql);
		return sql;
	}
	
	/**
	 * 解析出更新对象的sql语句
	 *
	 * @param object
	 *            ：需要更新的对象
	 * @return：更新对象的sql语句
	 */
	public static String getUpdateObjectSql(Object object) {
		// 定义一个sql字符串
		String sql = "update ";
		// 得到对象的类
		Class<?> c = object.getClass();
		String tableName = c.getSimpleName();
		sql +=tableName + " set ";
		List<String> nList = new ArrayList<String>();
		List<Object> vList = new ArrayList<Object>();
		String id = resolveValues(object,nList,vList);
		Object idValue = null;
		for(int i = 0; i < nList.size(); i++) {
			if(!nList.get(i).equals(id)) {
				if(vList.get(i) != null) {
					sql += nList.get(i) + " = " + vList.get(i) + ",";
				}
			} else {
				idValue = vList.get(i);
			}
		}
		if(idValue == null) {
			return null;
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += " where " + id + " = " + idValue;
		return sql;
	}
	
	/**
	 * 解析出更新对象的sql语句
	 *
	 * @param object
	 *            ：需要更新的对象
	 * @return：更新对象的sql语句
	 */
	public static String getDeleteObjectSql(Object object) {
		// 定义一个sql字符串
		String sql = "delete from ";
		// 得到对象的类
		Class<?> c = object.getClass();
		String tableName = c.getSimpleName();
		sql += tableName + " where ";
		sql += resolveIdAndValue(object);
		return sql;
	}
	
	/**
	 * 从Object中解析出带Id注解的成员名字及对应的值
	 *
	 * @return:主键的名字及对应的值
	 */
	private static String resolveIdAndValue(Object object) {
		Class<?> c = object.getClass();
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			if(method.isAnnotationPresent(Id.class)) {
				try {
					String fieldName = method.getName().substring(3, method.getName().length());
					Object value = method.invoke(object);
					if(value.getClass() == String.class) {
						return fieldName + " = \"" + value + "\"";
					}
					return fieldName + " = " + value;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 从Class中解析出带Id注解的成员名字
	 *
	 * @return:主键的名字
	 */
	private static String resolveId(Class<?> c) {
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			if(method.isAnnotationPresent(Id.class)) {
				try {
					String fieldName = method.getName().substring(3, method.getName().length());
					return fieldName;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * 从Object中解析出成员名字及对应的值
	 *
	 * @return:主键的名字      
	 */
	private static String resolveValues(Object object,List<String> nList,List<Object> vList) {
		String id = null;
		Class<?> c = object.getClass();
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			String mName = method.getName();
			if (mName.startsWith("get") && !mName.startsWith("getClass")) {
				String fieldName = mName.substring(3, mName.length());
				nList.add(fieldName);
				System.out.println("字段名字----->" + fieldName);
				if(method.isAnnotationPresent(Id.class)) {
					id = fieldName;
				}
				try {
					Object value = method.invoke(object);
					if(value instanceof String){
						vList.add("\"" + value + "\"");
					} else {
						vList.add(value);
					}
					System.out.println("字段值------>" + value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return id;
	}

	/**
	 * 将数据库数据封装成对象
	 *
	 * @param c: 封装成的类
	 * @param methods ： 方法数组
	 * @param set ： 数据库返回的结果集
	 * 
	 * @return:返回的对象
	 */
	public static Object packageObject(Class<?> c, ResultSet set) {
		
		Object obj = null;
		// 得到对象的方法数组
		Method[] methods = c.getMethods();
		try {
			obj = c.newInstance();
			// 遍历对象的方法
			for (Method method : methods) {
				String methodName = method.getName();
				// 如果对象的方法以set开头
				if (methodName.startsWith("set")) {
					// 根据方法名字得到数据表格中字段的名字
					String columnName = methodName.substring(3, methodName.length());
					// 得到方法的参数类型
					Class<?>[] parmts = method.getParameterTypes();
					if (parmts[0] == String.class) {
						// 如果参数为String类型，则从结果集中按照列名取得对应的值，并且执行set方法
						method.invoke(obj, set.getString(columnName));
					}
					if (parmts[0] == int.class) {
						method.invoke(obj, set.getInt(columnName));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return obj;
	}

	/**
	 * 将对象保存到数据库中
	 *
	 * @param object
	 *            ：需要保存的对象 @return：方法执行的结果;1:表示成功，0:表示失败
	 */
	public static int saveObject(Object object) {
		String sql = getSaveObjectSql(object);
		return executeSql(sql);
	}
	
	/**
	 * 将对象更新到数据库中
	 *
	 * @param object
	 *            ：需要更新的对象 @return：方法执行的结果;1:表示成功，0:表示失败
	 */
	public static int updateObject(Object object) {
		String sql = getUpdateObjectSql(object);
		return executeSql(sql);
	}
	
	/**
	 * 删除指定对象
	 *
	 * @param object
	 *            ：需要更新的对象 @return：方法执行的结果;1:表示成功，0:表示失败
	 */
	public static int deleteObject(Object object) {
		String sql = getDeleteObjectSql(object);
		return executeSql(sql);
	}
	
	/**
	 * 执行sql语句
	 *
	 * @param String
	 *            ：sql
	 * @return：方法执行的结果;1:表示成功，0:表示失败
	 */
	public static int executeSql(String sql) {
		Connection con = Connect2DBFactory.getDBConnection();
		try {
			PreparedStatement psmt = con.prepareStatement(sql);
			psmt.executeUpdate();
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * 执行sql语句
	 *
	 * @param String ：sql
	 * @return：ResultSet set
	 */
	public static ResultSet executeSql2(String sql) {
		Connection con = Connect2DBFactory.getDBConnection();
		ResultSet set = null;
		try {
			PreparedStatement psmt = con.prepareStatement(sql);
			// 得到执行查寻语句返回的结果集
			set = psmt.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}

	/**
	 * 从数据库中取得对象
	 *
	 * @param arg0
	 *            ：对象所属的类
	 * @param id
	 *            ：Id
	 * @return:需要查找的对象
	 */
	public static Object getObject(Class<?> c, Object Id) {

		// 拼凑查询sql语句
		String sql;
		if(Id.getClass() == String.class) {
			sql = "select * from " + c.getSimpleName() + " where " + resolveId(c) + " = \"" + Id + "\"";
		}else {
			sql = "select * from " + c.getSimpleName() + " where " + resolveId(c) + " = " + Id;
		}
		System.out.println("查找sql语句：" + sql);
		ResultSet set = executeSql2(sql);
		Object obj = null;
		if(set != null) {
			try {				
				set.next();
				obj = packageObject(c,set);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return obj;
	}
	
	/**
	 * 从数据库中取得对象集合
	 *
	 * @param arg0
	 *            ：对象所属的类
	 * @return:需要查找的对象集合
	 */
	public static List<Object> getAllObject(Class<?> c) {
		// 拼凑查询sql语句
		String sql = "select * from " + c.getSimpleName();
		System.out.println("查找sql语句：" + sql);
		// 得到执行查寻语句返回的结果集
		ResultSet set = executeSql2(sql);
		// 返回的结果集
		List<Object> list = null;
		if(set != null) {
			list = new ArrayList<Object>();
			try {			
				// 遍历结果集
				while (set.next()) {
					list.add(packageObject(c,set));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
}
