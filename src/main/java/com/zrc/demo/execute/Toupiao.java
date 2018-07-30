package com.zrc.demo.execute;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ParseException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zrc.demo.util.HttpClientUtils;

public class Toupiao {
	public static void main(String[] args) throws ParseException, IOException {
		String url = "http://119032548.ax.nofollow.51wyfl.com/index.php/toupiao/h5/ndotoupiao";
		String userAgent = "Mozilla/5.0 (Linux; Android 6.0.1; MI 5s Plus Build/MXB48T) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/37.0.0.0 Mobile MQQBrowser/6.2 TBS/88888888 Safari/537.36 MicroMessenger/6.5.23.1180 NetType/WIFI Language/zh_CN";
		String id= "300792";
		String vid = "119032548";
		String token = "9295";
//		String id= "300792";
//		String vid = "119032548";
//		String token = "9294";
		Map<String,Object> param = new HashMap<>();
		param.put("id", id);
		param.put("vid", vid);
		param.put("token", token);
		String json = JSON.toJSONString(param, false);
		JSONObject object = HttpClientUtils.sendSmspost(url, json,userAgent);
		System.out.println(object);
				
	}
}
