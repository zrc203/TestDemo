package com.zrc.demo.execute;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Kugou {
	public static void main(String[] args) throws Exception {
		byte[] key={(byte) 0xAC,(byte) 0xEC,(byte) 0xDF,0x57};
        InputStream input = new FileInputStream("F:\\KuGou\\Temp\\b836fa736c7f18aff240a8e22e9ab7d8.kgtemp");
        {
            OutputStream output = new FileOutputStream("d:\\test.mp3");//输出文件
            input.skip(1024);//跳过1024字节的包头
            byte[] buffer = new byte[key.length];
            int length;
            while((length=input.read(buffer,0,buffer.length))>0)
            {
                for(int i=0;i<length;i++)
                {
                    byte k = key[i];
                    byte kh = (byte) (k >> 4);
	                byte kl = (byte) (k & 0xf);
	                byte b = buffer[i];
	                byte low = (byte) (b & 0xf ^ kl);//解密后的低4位
	                byte high = (byte) ((b >> 4) ^ kh ^ low & 0xf);//解密后的高4位
                    buffer[i] = (byte)(high << 4 | low);
                }
                output.write(buffer, 0, length);
            }
            output.close();
            input.close();
        }
        
	}
}
