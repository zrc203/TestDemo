package com.zrc.demo.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONArray;

public class JsonTest {
	public static void main(String[] args) throws IOException {
		List<String> urls = FileUtils.readLines(new File("D:\\aa.json"),"UTF-8");
		StringBuilder builder = new StringBuilder();
		for (String s : urls) {
			builder.append(s);
		}
		String json = builder.toString();
		List<TestBean> list = JSONArray.parseArray(json, TestBean.class);
		for (TestBean testBean : list) {
			System.out.println(testBean.getId());
		}
	}
}
