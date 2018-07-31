package com.zrc.demo.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;

public class DrognDown {
	static String baseUrl = "http://ac.qq.com";
	public static void main(String[] args) throws Exception{
		getAcPic("http://ac.qq.com/Comic/comicInfo/id/505436");
	}
	public static void getAcPic(String url) throws Exception{
		Document listDoc = getUrlDocument(url);
		Elements listEachChapter = getlistEachChapter(url);
		String acName = listDoc.getElementsByTag("strong").get(0).text().replace(".", "").replace("?", "").replace("\"", "");
		String rootPath = "D:\\"+acName+"\\";
		File acPath = new File(rootPath);
		if(!acPath.exists()){
			acPath.mkdirs();
		}
//		Element chapter = listEachChapter.get(27);
//		writePic(chapter,rootPath);
		for (Element chapter : listEachChapter) {
			writePic(chapter,rootPath);
		}
	}
	public static Elements getlistEachChapter(String acUrl){
		Document listDoc = getUrlDocument(acUrl);
		Elements listEachChapter = listDoc.getElementsByClass("works-chapter-item");
		return listEachChapter;
	}
	public static void writePic(Element chapter,String rootPath) throws Exception{
		Element chapTagA = chapter.getElementsByTag("a").get(0);
		String href = baseUrl+chapTagA.attr("href");
		String chapName = chapTagA.text().replace(".", "").replace("?", "").replace("\"", "");
		
		Document aChapDoc = getUrlDocument(href);
		Pattern p = Pattern.compile("var DATA += '(.*=)'");
		String doc= aChapDoc.toString();
		Matcher m = p.matcher(doc);
		String data = "";
		if(m.find()){
			data =m.group(1);
		}
		data=data.substring(1);
		String decode = decode(data).replaceAll("@", "");
		JSONObject dataObj = (JSONObject) JSONObject.parse(decode);
		List<JSONObject> picList=(List<JSONObject>) dataObj.get("picture");
		DecimalFormat df = new DecimalFormat("00");
		int i = 0;
		for (JSONObject pic : picList) {
			String picName = df.format(i);
			File chapDir = new File(rootPath+"\\"+chapName);
			chapDir.mkdirs();
			OutputStream os = new FileOutputStream(rootPath+"\\"+chapName+"\\"+picName+".png");
			String picurl = pic.get("url").toString();
			InputStream is = getInputStreamByUrl(picurl);
			IOUtils.copy(is, os);
			is.close();
			os.close();
			i++;
		}
		System.err.println(chapName+"----->完成");
	}
	public static InputStream getInputStreamByUrl(String url) throws Exception {
		HttpClientBuilder builder = HttpClientBuilder.create();
		HttpHost proxy = new HttpHost("BJC-S-TMG.synnex.org",8080);
		HttpUriRequest request= new HttpGet(url);
		CloseableHttpClient httpClient = builder.setUserAgent("chrome").setProxy(proxy).build();
		CloseableHttpResponse response = httpClient.execute(request);
		int code = response.getStatusLine().getStatusCode();
		System.out.println(code);
		InputStream inputStream = response.getEntity().getContent();
		return inputStream;
	}
	public static Document getUrlDocument(String url){
		try {
			Connection conList = Jsoup.connect(url).header("User-Agent", "chrome").timeout(4500000).proxy("BJC-S-TMG.synnex.org",8080);
			Response listResp = conList.execute();
			Document listDoc = listResp.parse();
			return listDoc;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String decode(String c) {
		String _keyStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		String a = "";
		int b;
		int d, h, f, g;
		int e = 0;
		for (c = c.replaceAll("/[^A-Za-z0-9\\+\\/\\=]/g", ""); e < c.length();) {
			b = _keyStr.indexOf(c.charAt(e++));
			d = _keyStr.indexOf(c.charAt(e++));
			f = _keyStr.indexOf(c.charAt(e++));
			g = _keyStr.indexOf(c.charAt(e++));
			b = b << 2 | d >> 4;
			d = (d & 15) << 4 | f >> 2;
			h = (f & 3) << 6 | g;
			a += fromCharCode(b);
			a += fromCharCode(d);
			a += fromCharCode(h);
			if (f == 64 && g == 64) {
				break;
			}
		}

		return a;
	}

	public static String fromCharCode(int b) {
		Character a = Character.valueOf((char) b);
		return a.toString();
	}
}
