package com.zrc.demo.execute;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringSourceDemo {
	public static void main(String[] args) {
		String configLocation="classpath:app-db-context.xml";
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(configLocation);
		System.out.println(applicationContext);
	}
}
