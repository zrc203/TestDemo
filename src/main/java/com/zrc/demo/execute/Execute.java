package com.zrc.demo.execute;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zrc.demo.service.AreaService;

public class Execute {
	public static void main(String[] args)  {
		new Thread(new Music("11440785","【扫图】PDF")).start();
		new Thread(new Music("14589573","【扫图】无损音乐CD封面封底")).start();
		new Thread(new Music("4252130","【程序】酷软分享")).start();
		new Thread(new Music("4321919","【综合】我的收藏")).start();
		new Thread(new Music("4252141","【综合】教育资源")).start();
		new Thread(new Music("7106123","【视频】4K超高清")).start();
		new Thread(new Music("14201220","【视频】8K超高清")).start();
		new Thread(new Music("14201349","【视频】DJ")).start();
		new Thread(new Music("4251884","【视频】HD-MV")).start();
		new Thread(new Music("4538322","【视频】KTV-MKV")).start();
		new Thread(new Music("6511581","【视频】KTV-MPG")).start();
		new Thread(new Music("11690505","【视频】标清MV")).start();
		new Thread(new Music("4252180","【视频】演唱会视频")).start();
		new Thread(new Music("7285013","【视频】高清综艺视频")).start();
		new Thread(new Music("7762210","【音频】DJ串烧+车载串烧")).start();
		new Thread(new Music("4385746","【音频】DJ单曲")).start();
		new Thread(new Music("4252161","【音频】MP3音乐专辑")).start();
		new Thread(new Music("6517344","【音频】无损戏曲")).start();
		new Thread(new Music("8316386","【音频】无损音乐【亲压亲传-2014年快压打包】")).start();
		new Thread(new Music("8316213","【音频】无损音乐【亲压亲传-2015年RAR打包+添加恢复记")).start();
		new Thread(new Music("14082435","【音频】无损音乐【亲压亲传-2016年RAR打包+添加恢复记")).start();
		new Thread(new Music("19158257","【音频】无损音乐【亲压亲传-2017年RAR打包+添加恢复记")).start();
		new Thread(new Music("26500697","【音频】无损音乐【亲压亲传-2019年RAR打包+添加恢复记")).start();
		new Thread(new Music("260582","【音频】无损音乐【转存2015】")).start();
		new Thread(new Music("6233098","【音频】无损音乐【转存2016】")).start();
		new Thread(new Music("19436128","【音频】无损音乐【转存2017】")).start();
		new Thread(new Music("6509234","【音频】有声读物")).start();
		new Thread(new Music("22070591","【音频】无损音乐【亲压亲传-2018年RAR打包+添加恢复记")).start();

	}
}

class Music implements Runnable {
	private String cId;
	private String cName;
	public String getcId() {
		return cId;
	}

	public void setcId(String cId) {
		this.cId = cId;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}
	
	Music(String cId, String cName){
		this.cName = cName;
		this.cId = cId;
	}
	private AreaService areaService = (AreaService) new ClassPathXmlApplicationContext("classpath:app-db-context.xml")
			.getBean("areaService");

	public void getMusicDownById(String cId, String cName) throws Exception {
		System.out.println("处理文件夹--->" + cName);
		String ajaxUrlByBrowserUrl = getAjaxUrlByBrowserUrl("https://qiannianhupo.ctfile.com/u/167219/" + cId);

		String jsoup = getHttpContent(ajaxUrlByBrowserUrl);
		JSONObject object = JSONObject.parseObject(jsoup);
		JSONArray aaDataArray = (JSONArray) object.get("aaData");
		List<Map<String, Object>> list = new ArrayList<>();
		for (Object obj : aaDataArray) {
			String str = obj.toString();
			String[] split = str.split(",");
			Document classDoc = jsoup(split[0].toString().replace("\\t", "").replace("\\", ""));
			String type = classDoc.getElementsByTag("input").get(0).attr("id");
			Document classNameDoc = jsoup(split[1].toString().replace("\\t", "").replace("\\", ""));
			String className = classNameDoc.getElementsByTag("a").get(0).text();
			String url = "https://qiannianhupo.ctfile.com/fs/167219-";
			Map<String, Object> map = new HashMap<>();
			map.put("pid", cId);
			map.put("pname", cName);
			map.put("name", className);
			if ("folder_ids".equals(type)) {
				String classId = classDoc.getElementById("folder_ids").attr("value");
				map.put("id", classId);
				map.put("type", 0);
				System.out.println(classId + "          " + className);
				getMusicDownById(classId, className);
			} else {
				String classId = classDoc.getElementsByTag("input").get(0).attr("value");
				map.put("type", 1);
				map.put("id", classId);
				map.put("url", url+classId);
				map.put("size", split[2]);
				System.out.println(classId + "--->" + cName + "--->" + className);
			}
			list.add(map);
		}
		if (list.size() != 0) {
			areaService.insertMusic(list);
		}
	}

	public String getAjaxUrlByBrowserUrl(String url) throws Exception {
		String httpContent = getHttpContent(url);
		Pattern p = Pattern.compile("/iajax_guest.*,$", Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = p.matcher(httpContent);
		String resUrl = "";
		while (matcher.find()) {
			resUrl = matcher.group();
		}
		return "https://qiannianhupo.ctfile.com" + resUrl.replace("\",", "");
	}

	public String get(int n) {
		if (n == 1) {
			return "1";
		}
		BigInteger i = new BigInteger(get(n - 1));
		BigInteger integer = i.multiply(BigInteger.valueOf(n));
		return integer.toString();
	}


	public void getDownUrl() {
		try {
			InputStream is = new FileInputStream("D:\\0.txt");
			List<String> list = IOUtils.readLines(is, "GBK");
			is.close();
			String thUrl = list.get(0);
			thUrl = thUrl.replaceAll("thunder://", "").replaceAll("/", "");
			byte[] bs = Base64Utils.decodeFromString(thUrl);
			IOUtils.write(bs, new FileOutputStream("D:\\de.txt"));
			String str = new String(bs, "GBK");
			String string = URLDecoder.decode(str, "GBK");
			// string = string.substring(2, string.length() - 2);
			System.out.print(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getURLInputStream(String url, String fileName) {
		try {
			URL r = new URL(url);
			InputStream is = r.openStream();
			OutputStream os = new FileOutputStream("D:\\" + fileName);
			IOUtils.copy(is, os);
			is.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document jsoup(String content) throws Exception {
		Document document = Jsoup.parse(content);
		return document;
	}

	public String getHttpContent(String url) throws Exception {
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("User-Agent", "chrome"));
		CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultHeaders(headers).build();
		HttpUriRequest httpUriRequest = new HttpGet(url);
		CloseableHttpResponse response = httpClient.execute(httpUriRequest);
		HttpEntity entity = response.getEntity();
		InputStream inputStream = entity.getContent();
		String content = IOUtils.toString(inputStream, "UTF-8");

		return content;
	}

	@Override
	public void run() {
			try {
				getMusicDownById(cId, cName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
}