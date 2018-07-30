package com.zrc.demo.execute;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZasvMusic {
	public static final Logger logger = LoggerFactory.getLogger(ZasvMusic.class);
	public static void main(String[] args) throws IOException {
		new ZasvMusic().del(new File("D:\\0"));
	}
	
	public void del(File dir) throws IOException{
		if(dir.isDirectory()){
			File[] files = dir.listFiles();
			if(files.length==0){
				dir.delete();
				logger.info("删除空文件夹-->"+dir.getName());
			}else{
				logger.info("进入文件夹-->"+dir.getName());
				for (File file : files) {
					del(file);
				}
			}
		}else{
			String name = dir.getName();
			if(name.toLowerCase().endsWith("cue")||name.toLowerCase().endsWith("wav")){
				if(name.toLowerCase().endsWith("cue")){
					List<String> list = FileUtils.readLines(dir,"gb2312");
					for (String str : list) {
						if(str.toLowerCase().startsWith("title")){
							if(str.endsWith("未知标题\"")){
								logger.info("删除文件-->"+dir.getName());
								dir.delete();
							}
							break;
						}
					}
				}
			}else{
				logger.info("删除文件-->"+name);
				dir.delete();
			}
		}
	}
}
