package com.zrc.demo.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.http.ParseException;

import com.alibaba.fastjson.JSONObject;

public class HttpClientUtils {
	/**
	 * 发送短信
	 * @param postMessage
	 * @param userAgent 
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject sendSmspost(String	url ,String postMessage, String userAgent) throws ParseException, IOException{
		JSONObject jsonObject = null;
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(url);
		HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
		httpConnectionManager.setParams(connectionManagerParams);
		client.setHttpConnectionManager(httpConnectionManager);
		try {
			postMethod.addRequestHeader("Content-Type", "application/json;charset=UTF-8");
			postMethod.setRequestEntity(new StringRequestEntity(postMessage, null, "UTF-8"));//
			postMethod.addRequestHeader("User-Agent", userAgent);
			int resCode = client.executeMethod(postMethod);
			String response = new String(postMethod.getResponseBody(), "UTF-8");
			jsonObject = JSONObject.parseObject(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	

	
	public static JSONObject get(String url) throws ParseException, IOException{
		JSONObject jsonObject = null;
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(url);

		HttpConnectionManager httpConnectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams connectionManagerParams = new HttpConnectionManagerParams();
		httpConnectionManager.setParams(connectionManagerParams);
		client.setHttpConnectionManager(httpConnectionManager);
		try {
			int resCode = client.executeMethod(getMethod);
			String response = new String(getMethod.getResponseBody(), "UTF-8");
			jsonObject = JSONObject.parseObject(response);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
}
