package com.zrc.demo.execute;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.zrc.demo.entity.City;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class DealCity {
	public static void main(String[] args) throws IOException {
		List<String> lines = FileUtils.readLines(new File("D:\\0.txt"),"GBK");
		Map<String,HashSet<City>> map = new HashMap<>();
		for (String city : lines) {
			String first = getPinYin(city).substring(0,1);
			if(map.containsKey(first)) {
				HashSet<City> set = map.get(first);
				set.add(new City(city));
			}else {
				HashSet<City> set = new HashSet<>();
				set.add(new City(city));
				map.put(first, set);
			}
		}
		List<Map<String,Object>> jsonList = new ArrayList<Map<String,Object>>();
		Set<String> set = map.keySet();
		for (String first : set) {
			Map<String,Object> cityMap = new HashMap<>();
			cityMap.put("first", first);
			Set<HashMap<String,String>> citySet = new HashSet<>();
			HashSet<City> cityName = map.get(first);
			for (City city : cityName) {
				HashMap<String,String> end = new HashMap<>();
				end.put("code", "");
				end.put("name", city.toString());
				end.put("py", "");
				end.put("sx", "");
				citySet.add(end);
			}
			cityMap.put("city", citySet);
			jsonList.add(cityMap);
		}
		String jsonString = JSON.toJSONString(jsonList,true);
		System.out.println(jsonString);
		
	}
	 public static String getPinYin(String inputString) {  
         
	        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();  
	        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);  
	        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
	        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);  
	  
	        char[] input = inputString.trim().toCharArray();  
	        StringBuffer output = new StringBuffer("");  
	  
	        try {  
	            for (int i = 0; i < input.length; i++) {  
	                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);  
	                    output.append(temp[0]);  
	                    output.append(" ");  
	            }  
	        } catch (BadHanyuPinyinOutputFormatCombination e) {  
	            e.printStackTrace();  
	        }  
	        return output.toString();  
	    }  
}
