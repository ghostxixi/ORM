package com.uestc.javase.ORM;

import com.uestc.javase.ORM.Reflect.Id;

public class UserInfo {

	private int id;
	
	private String name;
	
	private String pwd;
	
	public int age;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Id
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
	    return "UserInfo [id=" + id + ", name=" + name + ", pwd=" + pwd + ", age="
	            + age + "]";
	}	

}
