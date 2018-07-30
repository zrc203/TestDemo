package com.zrc.demo.execute;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WeixinTest {
	public static void main(String[] args) throws Exception {
//		System.out.println(getToken());
//		System.out.println(getImageList());
		System.out.println(addImage());
	}
	//GLm7c5Wz-SWwAsPZyjPtINbloz-rU9Wnld-VfCgJF64-SGL1uZjQ15BVXzb9P_5cXWIw8ucJFUt59Jm3K_VFahwyqzsNvmz2o9uLTgXTLvcHTwTUwc06CJooT665tudZWFBaAGAHQM
	//tEp1G7i8Di1_XQIoM4sbctiepxgH6yB6kgDVVFFJbr4baMDYF8kF-IT7-UeFqhpUAQkSszu37CScUijekyezU4lSbfr847-5ynQ4oNBGLc3rPHB2yuPpaCysslot5Hd1QAQjAEAEOE
	public static String getToken() throws IOException{
		Document document = Jsoup.connect("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wx5c11643396d60075&secret=cdee64519a577f83b3e2c5af2a08b88e").ignoreContentType(true).get();
		return document.body().html();
	}
	//CnFwx8jrMabI0I-5rpjPQc7RuAwHs7CZavQaHNJF6Pg
	public static String getImageList() throws Exception{
		String token = "GLm7c5Wz-SWwAsPZyjPtINbloz-rU9Wnld-VfCgJF64-SGL1uZjQ15BVXzb9P_5cXWIw8ucJFUt59Jm3K_VFahwyqzsNvmz2o9uLTgXTLvcHTwTUwc06CJooT665tudZWFBaAGAHQM";
		Connection connection = Jsoup.connect("https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token="+token);
		connection.ignoreContentType(true);
		String body= "{\"type\":\"image\",\"offset\":0,\"count\":20}";
		connection.requestBody(body);
		Document document = connection.post();
		return document.body().html();
	}
	
	public static String addImage() throws Exception{
		String token = "tEp1G7i8Di1_XQIoM4sbctiepxgH6yB6kgDVVFFJbr4baMDYF8kF-IT7-UeFqhpUAQkSszu37CScUijekyezU4lSbfr847-5ynQ4oNBGLc3rPHB2yuPpaCysslot5Hd1QAQjAEAEOE";
		Connection connection = Jsoup.connect("https://api.weixin.qq.com/cgi-bin/material/add_material?access_token="+token+"&type=image");
		connection.ignoreContentType(true);
		File file = new File("D:\\0.jpg");
		InputStream inputStream = new FileInputStream(file);
		connection.data("media", "客服提示.jpg", inputStream);
		connection.header("Content-Type", "multipart/form-data");
		connection.header("Content-Length", String.valueOf(file.length()));
		Document document = connection.post();
		inputStream.close();
		return document.body().html();
	}
	
}
