package com.zrc.demo.execute;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;

public class YouDaoDict {

	public static void main(String[] args) throws IOException {
		File docFiles = new File("D:\\0");
		File[] files = docFiles.listFiles();
		for (File file : files) {
			List<String> list = FileUtils.readLines(file, "UTF-8");
			StringBuilder sb = new StringBuilder();
			sb.append("<wordbook>");
			for (String wordStr : list) {
//				if(wordStr.contains("/")) {
//					String[] wordArr = wordStr.split("/");
//					String word = wordArr[0];
//					String phonetic = wordArr[1];
//					String trans="";
//					for(int i=2;i<wordArr.length;i++) {
//						trans+=wordArr[i];
//					}
				if(wordStr.trim().equals("")) {
					continue;
				}
					sb.append("<item>");
					sb.append("<word>"+wordStr+"</word>");
//					sb.append("<trans></trans>");
//					sb.append("<phonetic></phonetic>");
//					sb.append("<tags></tags>");
//					sb.append("<progress>0</progress>");
					sb.append("</item>");
//				}
			}
			sb.append("</wordbook>");
			FileUtils.write(new File("D:\\1\\"+file.getName()+".xml"), sb, "UTF-8");
		}
		
	}

}
