package com.zrc.demo.execute;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import com.alibaba.fastjson.JSON;

public class CallPdfParser {

	public static void main(String[] args) throws Exception{
		parsePdf();
//		getToken();

	}
	public static void parsePdf()  throws Exception{
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODQ0MTAzMjQsIm5iZiI6MTU4NDQxMDMyNCwianRpIjoiMWNlYTVmNzQtNDlkOS00ZjVjLWJlYmUtNzJhZTg5NDExYjI1IiwiZXhwIjoxNTg0NDEyMTI0LCJpZGVudGl0eSI6eyJpZCI6MSwidXNlcm5hbWUiOiJ0aW5hdCJ9LCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.M7H5GuIveeSLT8VC-TlT2LXrOodMaW8QM2ZkNiX-9Ao"));
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultHeaders(headers).build();
		HttpPost post = new HttpPost("http://127.0.0.1:5000/pdfparser/parsePdf/366");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		File file = new File("D:\\T20022800446.pdf");
		builder.addBinaryBody("file", file, ContentType.create("multipart/form-data"), "T20022800446.pdf");
		HttpEntity reqEntity = builder.build();
		post.setEntity(reqEntity);
		CloseableHttpResponse response = httpClient.execute(post);
		HttpEntity respEntity = response.getEntity();
		String s = IOUtils.toString(respEntity.getContent(), "UTF-8");
		System.out.println(s);
	}
	
	public static void getToken()  throws Exception{
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Content-Type", "application/json"));
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultHeaders(headers).build();
		HttpPost post = new HttpPost("http://127.0.0.1:5000/pdfparser/usr/access_token");
		Map<String,String> param = new HashMap<>();
		param.put("username", "tinat");
		param.put("password", "12345");
		String json = JSON.toJSONString(param);
		StringEntity requestEntity = new StringEntity(json,"utf-8");
		post.setEntity(requestEntity);
		CloseableHttpResponse response = httpClient.execute(post);
		HttpEntity respEntity = response.getEntity();
		String s = IOUtils.toString(respEntity.getContent(), "UTF-8");
		System.out.println(s);
	}

}
