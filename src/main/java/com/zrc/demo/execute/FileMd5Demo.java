package com.zrc.demo.execute;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

public class FileMd5Demo {
	public static void main(String[] args) throws Exception {
		boolean delete = new File("H:\\mnt").delete();
		System.out.println(delete);
	}
}
class DealMd5{
	private List<String> list = new ArrayList<>();
	public String getMd5(File file) throws Exception{
		InputStream inputStream = new FileInputStream(file);
		String md5 = DigestUtils.md5Hex(inputStream);
		inputStream.close();
		return md5;
	}
	
	public void search(String keyWords,String path){
		File dirFile = new File(path);
		String fileName = dirFile.getName();
		if(isContainsKey(fileName, keyWords)){
			System.out.println(dirFile.getPath());
		}
		if(dirFile.isDirectory()){
			File[] files = dirFile.listFiles();
			for (File file : files) {
				search(keyWords,file.getPath());
			}
		}
		
	}
	public boolean isContainsKey(String source,String key){
		if(source.contains(key)){
			return true;
		}
		return false;
	}
	public void delRepeatFile(File file) throws Exception{
		if(file.isDirectory()){
			System.out.println("进入文件夹--->"+file.getName());
			File[] files = file.listFiles();
			if(files.length==0){
				file.delete();
				System.out.println("删除文件夹--->"+file.getName());
			}else{
				for (File f : files) {
					delRepeatFile(f);
				}
			}
		}else{
			String md5 = getMd5(file);
			if(list.contains(md5)){
				file.delete();
				System.out.println("删除文件--->"+file.getName());
			}else{
				list.add(md5);
				System.out.println("添加索引--->"+file.getName());
			}
//			String name = file.getName();
//			if(name.endsWith("txt")){
//				String content = FileUtils.readFileToString(file, "GBK");
//				if(content.contains("kuai.xunlei.com")){
//					System.out.println("删除文件--->"+file.getPath());
//					file.delete();
//				}
//				
//			}
		}
	}
}