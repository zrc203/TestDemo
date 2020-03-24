package com.zrc.demo.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

//How to use BASE64Encoder() and BASE64Decoder() in Eclipse
//https://blog.csdn.net/erlian1992/article/details/79518416

public class GZIPUtils {

	public static void main(String[] args) throws Exception{
//		String uncompress = "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";
		
		String uncompress = "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";
		System.out.println(uncompress(uncompress));
		
//		System.out.println(getXmlString("C:\\SynnexTest.xml"));
//		System.out.println(uncompress(compress(getXmlString("C:\\SynnexTest.xml"))));
//		System.out.println(compress(getXmlString("C:\\Sample3A4ConfigFile.xml")));
		
//		String xml = "<XML>aaa</XML>";
//		System.out.println(compress(xml));

//		System.out.println(uncompress(compress(xml)));
	}
	
	public static String compress(String primStr) {
        if (primStr == null || primStr.length() == 0) {
            return primStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(primStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (gzip != null) {
                try {
                    gzip.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new sun.misc.BASE64Encoder().encode(out.toByteArray());
    }

    /**
     * 使用gzip进行解压缩
     */
    public static String uncompress(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = null;
        GZIPInputStream ginzip = null;
        byte[] compressed = null;
        String decompressed = null;
        try {
            compressed = new sun.misc.BASE64Decoder().decodeBuffer(compressedStr);
            in = new ByteArrayInputStream(compressed);
            ginzip = new GZIPInputStream(in);

            byte[] buffer = new byte[1024];
            int offset = -1;
            while ((offset = ginzip.read(buffer)) != -1) {
                out.write(buffer, 0, offset);
            }
            decompressed = out.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ginzip != null) {
                try {
                    ginzip.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            try {
                out.close();
            } catch (IOException e) {
            }
        }
        return decompressed;
    }
    
    public static String getXmlString(String path) {
    	 String xmlString;
    	 byte[] strBuffer = null;
    	 int flen = 0;
    	 File xmlfile = new File(path);
    	 try {
    	 InputStream in = new FileInputStream(xmlfile);
    	 flen = (int)xmlfile.length();
    	 strBuffer = new byte[flen];
    	 in.read(strBuffer, 0, flen);
    	 } catch (FileNotFoundException e) {
    	 // TODO Auto-generated catch block
    	 e.printStackTrace();
    	 } catch (IOException e) {
    	 // TODO Auto-generated catch block
    	 e.printStackTrace();
    	 }
    	 xmlString = new String(strBuffer);
    	return xmlString;

    }




}
