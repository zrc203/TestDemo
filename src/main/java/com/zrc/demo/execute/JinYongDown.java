package com.zrc.demo.execute;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JinYongDown {
	public static void main(String[] args) {
		String rootUrl= "http://www.jinyongwang.com";
		Document listBookDoc = getJsoupDoc(rootUrl);
		Elements bookEles = listBookDoc.getElementsByClass("book_li");
		for (Element bookInfo : bookEles) {
			String bookName = bookInfo.getElementsByTag("img").attr("alt").replaceAll("小说", "");
			Elements bookElems = bookInfo.getElementsByClass("book_li_other").get(0).getElementsByTag("a");
			for (Element ele : bookElems) {
				String type = ele.text();
				String bookUrl = ele.attr("href");
				String realBookUrl = rootUrl+bookUrl;
				Document bookDoc = getJsoupDoc(realBookUrl);
				Elements chapEles = bookDoc.getElementsByClass("mlist").get(0).getElementsByTag("a");
				StringBuilder content = new StringBuilder();
				if(("射雕英雄传".equals(bookName)||"雪山飞狐".equals(bookName))&&"修订版".equals(type)){
					Element chapEle;
					for(int i = chapEles.size()-1;i>=0;i--){
						chapEle = chapEles.get(i);
						String chapUrl = rootUrl + chapEle.attr("href");
						Document chapDoc = getJsoupDoc(chapUrl);
						String chapName = chapDoc.getElementsByClass("mbtitle").get(0).text();
						
						content.append(chapName);
						content.append("\r\n");
						Elements chapTextEles = chapDoc.getElementById("vcon").getElementsByTag("p");
						for (Element text : chapTextEles) {
							content.append("\t");
							content.append(text.text());
							content.append("\r\n");
						}
						System.out.println(chapName);
					}
				}else{
					for (Element chapEle : chapEles) {
						String chapUrl = rootUrl + chapEle.attr("href");
						Document chapDoc = getJsoupDoc(chapUrl);
						String chapName = chapDoc.getElementsByClass("mbtitle").get(0).text();
						
						content.append(chapName);
						content.append("\r\n");
						Elements chapTextEles = chapDoc.getElementById("vcon").getElementsByTag("p");
						for (Element text : chapTextEles) {
							content.append("\t");
							content.append(text.text());
							content.append("\r\n");
						}
						System.out.println(chapName);
					}
				}
				
				String filePath = "D:\\jin\\"+type+"\\";
				File dir = new File(filePath);
				if(!dir.exists()){
					dir.mkdirs();
				}
				File bookFile = new File(filePath+bookName+".txt");
				try {
					FileUtils.write(bookFile, content,"UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println(bookName+type+"---->"+"完成");
			}
			
			
			
		}
		

	}
	
	public static Document getJsoupDoc(String url){
		Connection con = Jsoup.connect(url).header("User-Agent", "chrome").proxy("BJC-S-TMG.synnex.org", 8080).timeout(45000000);
		Document parse;
		try {
			Response execute = con.execute();
			parse = execute.parse();
			return parse;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
