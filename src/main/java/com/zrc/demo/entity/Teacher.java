package com.zrc.demo.entity;

import java.io.Serializable;

import org.springframework.stereotype.Component;

@Component
public class Teacher implements Serializable,Comparable<Teacher> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer age;
	private String name;
	public Teacher(){};
	public Teacher(String name,Integer age){
		this.name=name;
		this.age=age;
	};
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int compareTo(Teacher t){
		if(t.getAge()>this.getAge()){
			return -1;
		}else if (t.getAge()<this.getAge()) {
			return 1;
		}else{
			return 0;
		}
	}
//	@Override
//	public String toString() {
//		return "Teacher [age=" + age + ", name=" + name + "]";
//	}
//	
	
}
