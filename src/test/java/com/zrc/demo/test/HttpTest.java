package com.zrc.demo.test;

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
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
//@SuppressWarnings("all")
public class HttpTest {
	public static void main(String[] args) throws Exception {
		HttpTest t = new HttpTest();
		List<String> urls = FileUtils.readLines(new File("D:\\aa.json"),"UTF-8");
		Pattern pattern = Pattern.compile("http.*-\\d\\d/(.*)\\?op=OPEN");
		for (String url : urls) {
			url=url.replace(" ", "%20");
			try {
				InputStream is = t.getInputStreamByUrl(url);
				if(is == null)
					continue;
				url = URLDecoder.decode(url, "UTF-8");
				Matcher matcher = pattern.matcher(url);
				String fileName ="";
				while(matcher.find()) {
					fileName = matcher.group(1);
				}
				OutputStream os = new FileOutputStream("D:\\0\\"+fileName);
				IOUtils.copy(is, os);
				is.close();
				os.close();
			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
			
		}
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
		CloseableHttpClient httpClient = builder.setUserAgent("chrome").setProxy(proxy).build();
		CloseableHttpResponse response = httpClient.execute(request);
		int code = response.getStatusLine().getStatusCode();
		System.out.println(code);
		if (code!= 200)
			return null;	
		InputStream inputStream = response.getEntity().getContent();
		return inputStream;
	}
	
}
