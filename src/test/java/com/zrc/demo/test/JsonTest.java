package com.zrc.demo.test;

import java.io.IOException;

public class JsonTest {
	public static void main(String[] args) throws IOException {
		byte[] b = "[33m".getBytes();
		System.out.println(new String(b).getBytes());
	}
}
