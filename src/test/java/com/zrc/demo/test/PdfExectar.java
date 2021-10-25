package com.zrc.demo.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfExectar {
	public static void main(String[] args) throws Exception {
		for(File f: Objects.requireNonNull(new File("E:\\0\\").listFiles())) {
			if(f.getName().endsWith("pdf")) {
				continue;
			}
			InputStream is = new FileInputStream(f);
			byte[] b = new byte[(int)f.length()];
			int read = is.read(b);
			StringBuilder source = new StringBuilder();
			for (byte c : b) {
				source.append(c);
				source.append(";");
			}
			Pattern pattern = Pattern.compile("37;80;68;70;45;49;46;.*;37;37;69;79;70");
			Matcher matcher = pattern.matcher(source);
			while(matcher.find()) {
				String pdfStr =matcher.group();
				String[] byteArray = pdfStr.split(";");
				byte[] pdfByte = new byte[byteArray.length];
				for(int i=0;i<byteArray.length;i++) {
					pdfByte[i] = Byte.parseByte(byteArray[i]);
				}
				OutputStream os = new FileOutputStream("E:\\0\\"+f.getName().replace(".msg", "")+".pdf");
				os.write(pdfByte);
				os.close();
			}
			is.close();
		}
		
	}
}
