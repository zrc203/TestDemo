package com.zrc.demo.execute;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class DealAspFile {
	public static void main(String[] args) {
		new DealAspFile().dealFile(new File("D:\\1"));
	}
	
	
	public void dealFile(File pathDir){
		if(pathDir.isDirectory()){
			System.out.println("dealDir--->"+pathDir.getName());
			File[] files = pathDir.listFiles();
			for (File file : files) {
				dealFile(file);
			}
		}else if(pathDir.getName().toLowerCase().endsWith("asp")){
			try {
				String aspContent = FileUtils.readFileToString(pathDir, "GB2312").replace("<%", "<!--%").replace("%>", "%-->");
				FileUtils.write(pathDir, aspContent, "GB2312",false);
				System.out.println("dealFile--->"+pathDir.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
