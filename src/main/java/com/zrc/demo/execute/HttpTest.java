package com.zrc.demo.execute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//@SuppressWarnings("all")
public class HttpTest {
	public static void main(String[] args) throws Exception {
		HttpTest t = new HttpTest();
		Document document = t.getDocumentByUrl("https://m.ggkdm.top/newlg_5/p98017z.html");
		System.out.println(document);
			
		
	}//update fireeye template and add element del_row_by_reg for pdf template
	public Document getDocumentByUrl(String url) throws Exception {
		Document document = Jsoup.parse(getInputStreamByUrl(url),"UTF-8","");
		return document;
	}
	
	public String getStringByUrl(String url) throws Exception {
		InputStream inputStream = getInputStreamByUrl(url);
		StringBuilder builder = new StringBuilder();
		List<String> lines = IOUtils.readLines(inputStream, "UTF-8");
		for (String str : lines) {
			builder.append(str);
		}
		return builder.toString();
	}
	
	public InputStream getInputStreamByUrl(String url) throws Exception {
		HttpClientBuilder builder = HttpClientBuilder.create();
		HttpHost proxy = new HttpHost("BJC-S-TMG.synnex.org",8080);
		HttpUriRequest request= new HttpGet(url);
		CookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(new BasicClientCookie2("iparr3333","1"));
		cookieStore.addCookie(new BasicClientCookie2("__tins__19244622","%7B%22sid%22%3A%201533722786683%2C%20%22vd%22%3A%209%2C%20%22expires%22%3A%201533726199659%7D"));
		cookieStore.addCookie(new BasicClientCookie2("__tins__19196100","%7B%22sid%22%3A%201533722786696%2C%20%22vd%22%3A%209%2C%20%22expires%22%3A%201533726199668%7D"));
		cookieStore.addCookie(new BasicClientCookie2("__tins__19465217","%7B%22sid%22%3A%201533722786705%2C%20%22vd%22%3A%209%2C%20%22expires%22%3A%201533726199674%7D"));
		cookieStore.addCookie(new BasicClientCookie2("__51cke__",""));
		cookieStore.addCookie(new BasicClientCookie2("__51laig__","27"));
		CloseableHttpClient httpClient = builder.setUserAgent("XiaoMi").setProxy(proxy).setDefaultCookieStore(cookieStore).build();
		CloseableHttpResponse response = httpClient.execute(request);
		int code = response.getStatusLine().getStatusCode();
		System.out.println(code);
		InputStream inputStream = response.getEntity().getContent();
		return inputStream;
	}
	
}
