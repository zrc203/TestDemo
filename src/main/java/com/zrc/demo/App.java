package com.zrc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages="com.zrc.demo")
public class App {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(App.class, args);
	}

}
