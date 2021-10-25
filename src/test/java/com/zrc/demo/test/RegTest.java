package com.zrc.demo.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.Math;

public class RegTest {
	public static void main(String[] args) {
		RegTest test = new RegTest();
		int n = 5;
		List<String> posList = test.getPosList(n);
		String[][] asList = test.genArray(posList);
		for(String[] a:asList) {
			List<String> b = Arrays.asList(a);
			String line = String.join(",", b);
			System.out.println(line);
			
		}

	}

	public String[][] genArray(List<String> posList) {
		int size = posList.size();
		int len = (int) Math.sqrt(size);
		String[][] ar = new String[len][len];
		int i = 1;
		for (String posStr:posList) {
			String[] posArr = posStr.split(",");
			int x = Integer.parseInt(posArr[0]);
			int y = Integer.parseInt(posArr[1]);
			ar[y][x] = i+"";
			i++;
		}
		return ar;
	}

	public List<String> getPosList(int n) {
		int i = 0;
		int j = 0;
		String direct = "d";
		List<String> posList = new ArrayList<>();
		boolean isBreak = false;
		while (true) {
			if (isBreak) {
				break;
			}
			posList.add(i + "," + j);
			switch (direct) {
			case "w": {
				j--;
				if ((j == 0) || posList.contains(i + "," + (j - 1))) {
					direct = "d";
				}

			}
				break;
			case "s": {
				j++;
				if ((j + 1 == n) || posList.contains(i + "," + (j + 1))) {
					direct = "a";
				}

			}
				break;
			case "a": {
				i--;
				if ((i == 0) || posList.contains((i - 1) + "," + j)) {
					direct = "w";
				}

			}
				break;
			case "d": {
				i++;
				if ((i + 1 == n) || posList.contains((i + 1) + "," + j)) {
					direct = "s";
				}

			}
				break;
			}
			if (posList.contains(i + "," + j)) {
				isBreak = true;
			}

		}
		return posList;
	}
}
