package com.zrc.demo.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {
	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("[abc]+");
		Matcher matcher = pattern.matcher("aadba");
		while(matcher.find()) {
			System.out.println(matcher.group());
		};
		
	}
}
