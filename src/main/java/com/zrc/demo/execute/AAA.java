package com.zrc.demo.execute;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.util.Base64Utils;

import com.zrc.demo.util.GZIPUtils;

public class AAA {

	public static void main(String[] args) throws Exception {
		String encodeToString = Base64Utils.encodeToString("thunder://QUFmdHA6Ly9keW06ZHltQGR5bS5keS5uZXQ6NjA0Ny8xNTU4NTE4MjI1L+azouilv+exs+S6mueLguaDs+absi7kv67lpI3niYguQm9oZW1pYW4uUmhhcHNvZHkuMjAxOC5CRDEwODBQLlgyNjQuQUFDLkVuZ2xpc2guQ0hTLUVORy5NcDRCYS5tcDRaWg==".getBytes());
		System.out.println(encodeToString);
	}
	public void getDownUrl() {
		try {
			InputStream is = new FileInputStream("D:\\0.txt");
			List<String> list = IOUtils.readLines(is, "GBK");
			is.close();
			String thUrl = list.get(0);
			thUrl = thUrl.replaceAll("thunder://", "").replaceAll("/", "");
			byte[] bs = Base64Utils.decodeFromString(thUrl);
			FileOutputStream fileOutputStream = new FileOutputStream("D:\\de.txt");
			IOUtils.write(bs, fileOutputStream);
			fileOutputStream.close();
			String str = new String(bs, "GBK");
			String string = URLDecoder.decode(str, "GBK");
			// string = string.substring(2, string.length() - 2);
			System.out.print(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void invoke() throws Exception {
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("User-Agent", "QQBrowser/9.6.12513.400"));
		headers.add(new BasicHeader("Authorization", "Bearer 9kh6P8D2oq9AjHvbRreHTS9QMxjU"));
		headers.add(new BasicHeader("AttachmentSize", "200"));
		headers.add(new BasicHeader("SenderDUNS", "055991053"));
		headers.add(new BasicHeader("POFormatType", "3A4"));
		headers.add(new BasicHeader("Content-Type", "multipart/related;"));
		HttpHost proxy = new HttpHost("BJC-S-TMG.synnex.org", 8080);
		CloseableHttpClient httpClient = HttpClientBuilder.create().setProxy(proxy).setDefaultHeaders(headers).build();
		HttpPost post = new HttpPost("https://api-test.cisco.com/commerce/ORDER/v2/POE/sync/validateOrder");
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		ContentBody strBody = new StringBody(
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Header><eb:Messaging xmlns:eb=\"http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\" xsi:schemaLocation=\"http://www.w3.org/2001/XMLSchema-instance http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/core/ebms-header-3_0-200704.xsd\" soapenv:mustUnderstand=\"true\"><eb:UserMessage><eb:MessageInfo><eb:Timestamp>2019-03-06T16:13:17</eb:Timestamp><eb:MessageId>kaiy@synnex.com</eb:MessageId></eb:MessageInfo><eb:PartyInfo><eb:From><eb:PartyId>preorder.synnex.com</eb:PartyId><eb:Role>http://example.org/roles/Buyer</eb:Role></eb:From><eb:To><eb:PartyId>PreOrderValidationService.cisco.com</eb:PartyId><eb:Role>http://example.org/roles/Seller</eb:Role></eb:To></eb:PartyInfo><eb:CollaborationInfo/><eb:MessageProperties/><eb:PayloadInfo><eb:PartInfo href=\"Validate_2100_Lines_Payload.xml.gz\"><eb:Schema location=\"http://www.cisco.com/assets/wsx_xsd/B2BNGC/ngcBOD.xsd\" version=\"2.0\"/><eb:PartProperties><eb:Property name=\"Description\">Order Validation WS Service Call</eb:Property><eb:Property name=\"MimeType\">application/xml</eb:Property><eb:Property name=\"CharacterSet\">utf-8</eb:Property><eb:Property name=\"Compressed\"/></eb:PartProperties></eb:PartInfo></eb:PayloadInfo></eb:UserMessage></eb:Messaging><ns:Messaging xmlns:ns=\"http://docs.oasis-open.org/ebxgml-msg/ebms/v3.0/ns/core/200704/\"/></soapenv:Header><soapenv:Body><ProcessPurchaseOrder releaseID=\"2014\" versionID=\"1.0\" systemEnvironmentCode=\"Test/Production\" languageCode=\"en-US\" xmlns=\"http://www.openapplications.org/oagis/10 ProcessPurchaseOrder.xsd\"><ApplicationArea><Sender><LogicalID schemeAgencyID=\"Cisco\">055991053</LogicalID><ComponentID>B2B-3.0</ComponentID><ReferenceID>SYNNEX CORPORATION.</ReferenceID></Sender><Receiver><LogicalID schemeAgencyID=\"Cisco\">364132837</LogicalID><ID>Cisco</ID></Receiver><CreationDateTime>2019-03-07</CreationDateTime><BODID>1111111</BODID><Extension><Code typeCode=\"PreOrderValidation\">PreOrderValidation</Code></Extension></ApplicationArea><DataArea><Process></Process><PurchaseOrder>25588166</PurchaseOrder></DataArea></ProcessPurchaseOrder></soapenv:Body></soapenv:Envelope>",
				ContentType.TEXT_XML);
		entityBuilder.addPart("s", strBody);
		ContentBody fileBody = new InputStreamBody(new FileInputStream(new File("D:\\0\\a.txt")),
				ContentType.TEXT_PLAIN);
		entityBuilder.addPart("f", fileBody);
		post.setEntity(entityBuilder.build());
		CloseableHttpResponse response = httpClient.execute(post);
		HttpEntity entity = response.getEntity();
		String s = IOUtils.toString(entity.getContent(), "UTF-8");
		System.out.println(s);
		
	}
}
