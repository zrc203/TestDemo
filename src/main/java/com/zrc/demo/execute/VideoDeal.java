package com.zrc.demo.execute;

import java.io.File;

public class VideoDeal {
	public static void main(String[] args) {
		File listDir = new File("D:\\0");
		File[] listFiles = listDir.listFiles();
		for (File file : listFiles) {
			System.out.println(file.getName());
		}
		
	}
}
interface A{
	
}