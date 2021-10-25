package com.zrc.demo.test;

import java.io.IOException;
import java.util.Arrays;

public class JsonTest {
	public static void main(String[] args) throws IOException {
		byte[] b = "[33m".getBytes();
		System.out.println(Arrays.toString(new String(b).getBytes()));
	}
}
