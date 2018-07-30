package com.zrc.demo.execute;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class AAA {

	public static void main(String[] args) throws Exception {
		
	}
	
	public static int fun(int n) {
		return n==1?1:n*fun(n-1);
	}
	
	public Document jsoup(String content) throws Exception {
		Document document = Jsoup.parse(content);
		return document;
	}

	public static InputStream getHttpContent(String url,String method) throws Exception {
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.104 Safari/537.36 Core/1.53.3427.400 QQBrowser/9.6.12513.400"));
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultHeaders(headers).build();
		HttpUriRequest httpUriRequest =null;
		if("get".equals(method)){
			httpUriRequest=new HttpGet(url);
		}else if("post".equals(method)){
			httpUriRequest=new HttpPost(url);
		}else if("options".equals(method)){
			httpUriRequest=new HttpOptions(url);
		}else if("delete".equals(method)){
			httpUriRequest=new HttpDelete(url);
		}
		CloseableHttpResponse response = httpClient.execute(httpUriRequest);
		HttpEntity entity = response.getEntity();
		StatusLine line = response.getStatusLine();
		System.out.println("状态码：  -->  "+line.getStatusCode());
		InputStream inputStream = entity.getContent();
		return inputStream;
	}

}

