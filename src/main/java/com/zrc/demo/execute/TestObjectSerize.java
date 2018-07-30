package com.zrc.demo.execute;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zrc.demo.service.AreaService;

public class TestObjectSerize {
	private CloseableHttpClient httpClient = HttpClientBuilder.create().setUserAgent("chrome")
			.setDefaultRequestConfig(RequestConfig.DEFAULT).build();

	public static void main(String[] args) throws Exception {
		new Thread(new XiaomiArea("3510")).start();
	}

	public void writeToDisc(String url) throws Exception {
		CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(url));
		HttpEntity httpEntity = httpResponse.getEntity();
		InputStream inputStream = httpEntity.getContent();
		OutputStream os = new FileOutputStream("D:\\a.html");
		IOUtils.copy(inputStream, os);
		os.close();
		inputStream.close();
	}

	public void printJson(String url) throws Exception {
		CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(url));
		HttpEntity httpEntity = httpResponse.getEntity();
		InputStream inputStream = httpEntity.getContent();
		List<String> list = IOUtils.readLines(inputStream, "UTF-8");
		StringBuilder json = new StringBuilder();
		for (String string : list) {
			json.append(string);
		}
		String jsonStr = json.toString().replace("getAreaListCallback(", "").replace(")", "");
		Object parse = JSONObject.parse(jsonStr);
		System.out.println(parse);
		inputStream.close();
	}

	public void printDocument(String url) throws Exception {
		CloseableHttpResponse httpResponse = httpClient.execute(new HttpGet(url));
		HttpEntity httpEntity = httpResponse.getEntity();
		InputStream inputStream = httpEntity.getContent();
		Document document = Jsoup.parse(inputStream, "UTF-8", "");
		System.out.println(document);
		inputStream.close();
	}

	public void login() throws Exception {
		HttpPost post = new HttpPost("http://www.weike22.com/users/?action=login");
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("username", "hyxily"));
		parameters.add(new BasicNameValuePair("password", "123456"));
		post.setEntity(new UrlEncodedFormEntity(parameters, "GBK"));
		CloseableHttpResponse httpResponse = httpClient.execute(post);
		HttpEntity httpEntity = httpResponse.getEntity();
		InputStream inputStream = httpEntity.getContent();
		Document document = Jsoup.parse(inputStream, "GBK", "");
		System.out.println(document);
		inputStream.close();
	}
}

class XiaomiArea implements Runnable {
	Connection connection = null;
	ApplicationContext ac = new ClassPathXmlApplicationContext("app-db-context.xml");
	private AreaService areaService = (AreaService) ac.getBean("areaService");
	private String id;

	public XiaomiArea(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		writeArea(id);
	}

	public void writeArea(String id) {
		System.out.println("当前----" + id);
		try {
			Thread.sleep(1000);
			long time = new Date().getTime();
			connection = Jsoup
					.connect("https://addr.vmall.com/data/region/children/"+id+".json?callback=jsonp"+time)
					.header("User-Agent", "chrome").ignoreContentType(true).timeout(5000);
			Response response = connection.execute();
			String jsonStr = response.body();
			jsonStr = jsonStr.replace("jsonp"+time+"(", "").replace(")", "");
			//https://addr.vmall.com/data/region/children/3511.json?callback=jsonp1493257595877
			JSONObject parse = (JSONObject) JSONObject.parse(jsonStr);
			
			JSONArray regions = (JSONArray) parse.get("data");
			if (regions != null && regions.size() != 0) {
				for (Object idKey : regions) {
					JSONObject object = (JSONObject) idKey;
					Map map = object;
					map.put("parentId", id);
					areaService.insertArea(map);
					System.out.println(object);
					writeArea(map.get("id").toString());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			writeArea(id);
		}
	}
}