package com.zrc.demo.execute;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
/**
 * 用于下载小说天堂的小说
 * 同时存入本地D：\\novel
 * @author ZRC
 *
 */
class NovelDeal extends Thread{
	private String cata;
	public NovelDeal() {
	}
	public NovelDeal(String cata) {
		this.cata = cata;
	}
	@Override
	public void run(){
		beginWrite(cata);	//开启线程执行的方法
	}
	public static void main(String[] args) {
		NovelDeal a1 = new NovelDeal("mingzhu");	//每个线程用于下载一类图书
		NovelDeal a2 = new NovelDeal("ertong");
		NovelDeal a3 = new NovelDeal("qingchun");
		NovelDeal a4 = new NovelDeal("dangdai");
		NovelDeal a5 = new NovelDeal("gudian");
		NovelDeal a6 = new NovelDeal("pinglun");
		NovelDeal a7 = new NovelDeal("waiwen");
		NovelDeal a8 = new NovelDeal("sanwen");
		NovelDeal a9 = new NovelDeal("xuanhuan");
		NovelDeal a10 = new NovelDeal("chuanyue");
		NovelDeal a11 = new NovelDeal("wangyou");
		NovelDeal a12 = new NovelDeal("yanqing");
		NovelDeal a13 = new NovelDeal("xuanyi");
		NovelDeal a14 = new NovelDeal("wuxia");
		NovelDeal a15 = new NovelDeal("kehuan");
		NovelDeal a16 = new NovelDeal("renwen");
		NovelDeal a17 = new NovelDeal("junshi");
		NovelDeal a18 = new NovelDeal("zhuanji");
		NovelDeal a19 = new NovelDeal("lizhi");
		NovelDeal a20 = new NovelDeal("lishi");
		NovelDeal a21 = new NovelDeal("shenghuo");
		a1.start();	
	    a2.start();
        a3.start();
        a4.start();
        a5.start();
        a6.start();
        a7.start();
        a8.start();
        a9.start();
        a10.start();
        a11.start();
        a12.start();
        a13.start();
        a14.start();
        a15.start();
        a16.start();
        a17.start();
        a18.start();
        a19.start();
        a20.start();
        a21.start();
	}
	/**
	 * 传入小说地址
	 * @param address 小说地址
	 */
	public static void writeNovel(String address){
		Connection connect = Jsoup.connect(address).timeout(45000000).header("User-Agent", "chrome");
		Response execute;
		Document document=null;
		try {
			execute= connect.execute();
			document= execute.parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String articleName = document.getElementsByTag("h1").text().replaceAll("txt下载", "");
		String author = document.getElementsByClass("zhaungtai").get(0).getElementsByTag("a").get(0).text();
		String textName = articleName+"--"+author+".txt";
		String catalog = document.getElementsByClass("zhaungtai").get(0).getElementsByTag("a").get(1).text();
		String path = "D:\\novel\\"+catalog+"\\";
		File pathFile = new File(path);
		if(!pathFile.exists()){
			pathFile.mkdirs();
		}
		File textFile = new File(path+textName);
		OutputStream textOs = null;
		try {
			textOs=new FileOutputStream(textFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Elements tds = document.getElementsByTag("td");
		Set<String> set = new TreeSet<>();
		for (Element ele : tds) {
			Elements as = ele.getElementsByTag("a");
			for (Element aEle : as) {
				set.add(aEle.attr("href"));
			}
		}
		StringBuilder content = new StringBuilder();
		for (String url : set) {
			Response exe = null;
			Document doc = null;
			Connection con = Jsoup.connect("http://www.xiaoshuotxt.net"+url).timeout(45000000).header("User-Agent", "chrome");
			 try {
				exe = con.execute();
				 doc = exe.parse();
				 content.append(doc.getElementsByTag("h1").get(0).text());
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			Elements pEles = doc.getElementsByClass("zw");
				for (Element pEle : pEles) {
					content.append(pEle.toString().replaceAll("<div>", "").replaceAll("<a.*[堂]", "").replaceAll("</div>", "").replaceAll("<div class=\"zw\">", "").replaceAll("</p>", "").replaceAll("<p>", "").replaceAll("<br>", "").replaceAll("&nbsp;", " ").replaceAll("[w|W|Ｗ].*[堂]", "").replaceAll("<a.*a>", "").replaceAll("</a>", "").replaceAll("小说天.*[t|T]", "").replaceAll("[w|W|Ｗ].*[o|O][m|M]", ""));
				}
				content.append("\r\n");
		}
		try {
			IOUtils.write(content.toString(), textOs,"GBK");
			textOs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(catalog+"--->"+textName+"---->完成");
	}
	/**
	 * 通过分类取得每个小说的地址
	 * @param catalog
	 */
	public static void beginWrite(String catalog){
		int index = 1;
		while(true){
			String url = "http://www.xiaoshuotxt.net/"+catalog;
			String relUrl = "";
			if(index!=1){
				relUrl=url+"/index_"+index+".html";
			}else{
				relUrl=url;
			}
			Connection connect = Jsoup.connect(relUrl).timeout(45000000).header("User-Agent", "chrome");
			Elements elements;
			Document document;
			try {
				Response execute = connect.execute();
				document = execute.parse();
			}catch(SocketTimeoutException e){
				System.err.println("SocketTimeoutException-------------continue"+relUrl);
				e.printStackTrace();
				continue ;
			}catch(HttpStatusException e){
				System.err.println("HttpStatusException-------------break"+relUrl+"\n\n\n");
				e.printStackTrace();
				break;
			}catch (IOException e) {
				System.err.println("IOException-------------break"+relUrl+"\n\n\n");
				e.printStackTrace();
				break;
			} 
				elements = document.getElementsByClass("bbox");
				for (Element element : elements) {
					String base = element.getElementsByTag("a").get(0).attr("href");
					writeNovel("http://www.xiaoshuotxt.net"+base);
				}
				index++;
		}
	}
}

