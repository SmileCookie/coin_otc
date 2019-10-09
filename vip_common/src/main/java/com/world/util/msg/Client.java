package com.world.util.msg;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
public class Client {

	private final static Logger log = Logger.getLogger(Client.class.getName());

	/*
	 * webservice服务器定义
	 */
	private String serviceURL1 = "http://sdk2.zucp.net:8060/webservice.asmx"; //主地址
	private String serviceURL2 = "http://sdk2.entinfo.cn:8060/webservice.asmx";//备用地址
	private String serviceURL = serviceURL1;
	private String gj_serviceURL = "http://sdk2.entinfo.cn:8060/gjWebService.asmx";
	private String audio_serviceURL = "http://sdk3.entinfo.cn:8060/webservice.asmx";
	private String sn = "";// 序列号
	private String pwd = "";// 密码
	private String password = "";
	private int connectCount = 0; //地址连接次数
	private int connectCountLimit = 3;//地址连接限制次数
	private int urlFlag = 1; //当前连接地址标识，1表示主地址，2表示备用地址
	private Document document = null; 
	private NodeList allNode = null; 
	
//	private URLConnection connection;
//	private HttpURLConnection httpconn;
//	private static Map<String,HttpURLConnection> map = new HashMap<String, HttpURLConnection>();
	
	private static Client client;
	
	public static Client getInstance() throws Exception{
		if(client == null){
			String sn = "SDK-BBX-010-19394";
			String pwd = "59@6d0f@";
			client = new Client(sn, pwd);
		}
		return client;
	}
	
	/*
	 * 构造函数
	 */
	public Client(String sn, String password)
			throws UnsupportedEncodingException {
		this.sn = sn;
		this.password = password;
		this.pwd = this.getMD5(sn + password);
		//地址检测切换线程
		//ChangeUrlThread changeUrlThread = new ChangeUrlThread();
		//changeUrlThread.start();
	}
	
	/*
	 * 方法名称：getMD5 功 能：字符串MD5加密 参 数：待转换字符串 返 回 值：加密之后字符串
	 */
	public String getMD5(String sourceStr) throws UnsupportedEncodingException {
		String resultStr = "";
		try {
			byte[] temp = sourceStr.getBytes();
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(temp);
			// resultStr = new String(md5.digest());
			byte[] b = md5.digest();
			for (int i = 0; i < b.length; i++) {
				char[] digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
						'9', 'A', 'B', 'C', 'D', 'E', 'F' };
				char[] ob = new char[2];
				ob[0] = digit[(b[i] >>> 4) & 0X0F];
				ob[1] = digit[b[i] & 0X0F];
				resultStr += new String(ob);
			}
			return resultStr;
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString(), e);
			return null;
		}
	}


	/*
	 * 方法名称：getBalance 功 能：获取余额 参 数：无 返 回 值：余额（String）
	 */
	public String getBalance() {
		String result = "";
		String soapAction = "http://tempuri.org/balance";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
		xml += "<soap:Body>";
		xml += "<balance xmlns=\"http://tempuri.org/\">";
		xml += "<sn>" + sn + "</sn>";
		xml += "<pwd>" + pwd + "</pwd>";
		xml += "</balance>";
		xml += "</soap:Body>";
		xml += "</soap:Envelope>";

		try {
//			httpconn = map.get("getBalance");
//			if(httpconn == null){
//				URL url = new URL(serviceURL);
//				connection = url.openConnection();
//				httpconn = (HttpURLConnection) connection;
//				map.put("getBalance", httpconn);
//			}
			URL url = new URL(serviceURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) connection;
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes("GBK"));
			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String
					.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type",
					"text/xml; charset=gb2312");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			
			InputStreamReader isr = new InputStreamReader(httpconn
					.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern
						.compile("<balanceResult>(.*)</balanceResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			out.close();
			return new String(result.getBytes());
		} catch (Exception e) {
			log.error(e.toString(), e);
			return "";
		}
	}

	/*
	 * 方法名称：mt 功 能：发送短信 参 数：mobile,content,ext,stime,rrid(手机号，内容，扩展码，定时时间，唯一标识)
	 * 返 回 值：唯一标识，如果不填写rrid将返回系统生成的
	 */
	public String mt(String mobile, String content, String ext, String stime,
			String rrid) {
		String result = "";
		String soapAction = "http://tempuri.org/mt";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
		xml += "<soap:Body>";
		xml += "<mt xmlns=\"http://tempuri.org/\">";
		xml += "<sn>" + sn + "</sn>";
		xml += "<pwd>" + pwd + "</pwd>";
		xml += "<mobile>" + mobile + "</mobile>";
		xml += "<content>" + content + "</content>";
		xml += "<ext>" + ext + "</ext>";
		xml += "<stime>" + stime + "</stime>";
		xml += "<rrid>" + rrid + "</rrid>";
		xml += "</mt>";
		xml += "</soap:Body>";
		xml += "</soap:Envelope>";
		
		InputStreamReader isr = null;
		BufferedReader in = null;
		HttpURLConnection httpconn = null;

		try {
//			httpconn = map.get("mt");
//			if(httpconn == null){
//				URL url = new URL(serviceURL);
//				connection = url.openConnection();
//				httpconn = (HttpURLConnection) connection;
//				map.put("mt", httpconn);
//			}
			
			URL url = new URL(serviceURL);
			URLConnection connection = url.openConnection();
			httpconn = (HttpURLConnection) connection;
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes("GBK"));
			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String
					.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type",
					"text/xml; charset=gb2312");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			out.close();

			isr = new InputStreamReader(httpconn
					.getInputStream());
			in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern.compile("<mtResult>(.*)</mtResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			return result;
		} catch (Exception e) {
			connectCount ++ ;
			//当达到连接次数限制的时候 切换连接地址
			if(connectCount >= connectCountLimit){
				if(serviceURL.equals(serviceURL1)){
					serviceURL = serviceURL2;
					urlFlag = 2 ;
				}
				connectCount = 0;
			}
			log.error(e.toString(), e);
			return "";
		}finally{
			try {
				if(isr != null){
					isr.close();
				}
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				log.error(e.toString(), e);
			}
		}
	}

	/*
	 * 方法名称：gxmt 功 能：发送短信 参
	 * 数：mobile,content,ext,stime,rrid(手机号，内容，扩展码，定时时间，唯一标识) 返 回
	 * 值：唯一标识，如果不填写rrid将返回系统生成的
	 */
	public String gxmt(String mobile, String content, String ext, String stime,
			String rrid) {
		String result = "";
		String soapAction = "http://tempuri.org/gxmt";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
		xml += "<soap:Body>";
		xml += "<gxmt xmlns=\"http://tempuri.org/\">";
		xml += "<sn>" + sn + "</sn>";
		xml += "<pwd>" + pwd + "</pwd>";
		xml += "<mobile>" + mobile + "</mobile>";
		xml += "<content>" + content + "</content>";
		xml += "<ext>" + ext + "</ext>";
		xml += "<stime>" + stime + "</stime>";
		xml += "<rrid>" + rrid + "</rrid>";
		xml += "</gxmt>";
		xml += "</soap:Body>";
		xml += "</soap:Envelope>";

		try {
//			httpconn = map.get("gxmt");
//			if(httpconn == null){
//				URL url = new URL(serviceURL);
//				connection = url.openConnection();
//				httpconn = (HttpURLConnection) connection;
//				map.put("gxmt", httpconn);
//			}
			URL url = new URL(serviceURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpconn = (HttpURLConnection) connection;
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes("GBK"));
			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String
					.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type",
					"text/xml; charset=gb2312");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			out.close();

			InputStreamReader isr = new InputStreamReader(httpconn
					.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern
						.compile("<gxmtResult>(.*)</gxmtResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			return result;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return "";
		}
	}

	/*
	 * 方法名称：mdSmsSend_g 功 能：发送短信 
	 * 参数：mobile,content,ext,stime,rrid(手机号，内容，扩展码，定时时间，唯一标识) 返 回
	 * 值：唯一标识，如果不填写rrid将返回系统生成的
	 */
	public String mdSmsSend_g(String mobile, String content, String ext, String stime,
			String rrid) {
		String result = "";
		String soapAction = "http://tempuri.org/mdSmsSend_g";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
		xml += "<soap:Body>";
		xml += "<mdSmsSend_g xmlns=\"http://tempuri.org/\">";
		xml += "<sn>" + sn + "</sn>";
		xml += "<pwd>" + pwd + "</pwd>";
		xml += "<mobile>" + mobile + "</mobile>";
		xml += "<content>" + content + "</content>";
		xml += "<ext>" + ext + "</ext>";
		xml += "<stime>" + stime + "</stime>";
		xml += "<rrid>" + rrid + "</rrid>";
		xml += "</mdSmsSend_g>";
		xml += "</soap:Body>";
		xml += "</soap:Envelope>";
		InputStreamReader isr = null;
		BufferedReader in = null;
		HttpURLConnection httpconn = null;
		try {
//			httpconn = map.get("mdSmsSend_g");
//			if(httpconn == null){
//				URL url = new URL(serviceURL);
//				connection = url.openConnection();
//				httpconn = (HttpURLConnection) connection;
//				map.put("mdSmsSend_g", httpconn);
//			}
			URL url = new URL(serviceURL);
			URLConnection connection = url.openConnection();
			httpconn = (HttpURLConnection) connection;
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes("GBK"));
			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String
					.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type",
					"text/xml; charset=gb2312");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			out.close();

			isr = new InputStreamReader(httpconn
					.getInputStream());
			in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern
						.compile("<mdSmsSend_gResult>(.*)</mdSmsSend_gResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			return result;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return "";
		}finally{
			try {
				if(isr != null){
					isr.close();
				}
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				log.error(e.toString(), e);
			}
		}
	}
	
	/*
	 * 方法名称：mdAudioSend
	 * 功    能：提交彩信基本信息
	 * 参    数：title 传真标题，mobile 手机号，txt 文本内容, content 传真base64内容，schTime 定时时间，如果不需要置为空间
	 * 返 回 值：返回一个唯一值rrid
	 */
	public String mdAudioSend (String title,String mobile,String txt,String content,String srcnumber,String stime)
	{
		String result = "";
		String soapAction = "http://tempuri.org/mdAudioSend";
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">";
		xml += "<soap12:Body>";
		xml += "<mdAudioSend xmlns=\"http://tempuri.org/\">";
		xml += "<sn>" + sn + "</sn>";
		xml += "<pwd>" + pwd + "</pwd>";
		xml += "<title>" + title + "</title>";
		xml += "<mobile>" + mobile + "</mobile>";
		xml += "<txt>" + txt + "</txt>";
		xml += "<content>" + content + "</content>";
		xml += "<srcnumber>" + srcnumber + "</srcnumber>";
		xml += "<stime>" + stime + "</stime>";
		xml += "</mdAudioSend>";
		xml += "</soap12:Body>";
		xml += "</soap12:Envelope>";
		
		InputStreamReader isr = null;
		BufferedReader in = null;
		HttpURLConnection httpconn = null;
		
		try {
//			httpconn = map.get("mdAudioSend");
//			if(httpconn == null){
//				URL url = new URL(serviceURL);
//				connection = url.openConnection();
//				httpconn = (HttpURLConnection) connection;
//				map.put("mdAudioSend", httpconn);
//			}
			URL url = new URL(audio_serviceURL);
			URLConnection connection = url.openConnection();
			httpconn = (HttpURLConnection) connection;
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xml.getBytes("GBK"));
			byte[] b = bout.toByteArray();
			httpconn.setRequestProperty("Content-Length", String
					.valueOf(b.length));
			httpconn.setRequestProperty("Content-Type",
					"text/xml; charset=gb2312");
			httpconn.setRequestProperty("SOAPAction", soapAction);
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);

			OutputStream out = httpconn.getOutputStream();
			out.write(b);
			out.close();

			isr = new InputStreamReader(httpconn
					.getInputStream());
			in = new BufferedReader(isr);
			String inputLine;
			while (null != (inputLine = in.readLine())) {
				Pattern pattern = Pattern.compile("<mdAudioSendResult>(.*)</mdAudioSendResult>");
				Matcher matcher = pattern.matcher(inputLine);
				while (matcher.find()) {
					result = matcher.group(1);
				}
			}
			return result;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return "";
		}finally{
			try {
				if(isr != null){
					isr.close();
				}
				if(in != null){
					in.close();
				}
			} catch (IOException e) {
				log.error(e.toString(), e);
			}
		}
	}
	
	
	/**
	 * 地址切换线内部类
	 * 检测主地址是否连接异常，异常则切换到备用地址，休眠5分钟，没有异常则休眠30分钟
	 * @author CHENJUN
	 *
	 */
	class ChangeUrlThread extends Thread{
		private int count = 0;
		public void run() {
			//log.info("--地址检测切换线程启动--");
			while(true){
				try {
//					httpconn = map.get("ChangeUrlThread");
//					if(httpconn == null){
//						URL url = new URL(serviceURL);
//						connection = url.openConnection();
//						httpconn = (HttpURLConnection) connection;
//						map.put("ChangeUrlThread", httpconn);
//					}
					URL url = new URL(serviceURL);
					URLConnection connection = url.openConnection();
					HttpURLConnection httpconn = (HttpURLConnection) connection;
					
					//设置连接超时时间10秒
					httpconn.setConnectTimeout(10000);
					httpconn.connect();
					//连接成功，切换为主地址
					serviceURL = serviceURL1;
					urlFlag = 1;
					count = 0 ;
					//线程沉睡
					sleep30m();
					
				} catch (IOException e) {
					count ++;
					//线程主地址三次连接失败连接 切换为备用地址
					if(count > connectCountLimit){
						serviceURL = serviceURL2;
						urlFlag = 2;
						count = 0;
						sleep5m();
					}
					sleep10s();
//					log.error(e.toString(), e);
				} 
					
			}
		}
		//30分钟沉睡线程
		private void sleep30m(){
			try {
				Thread.sleep(1000 * 30 * 60);
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
			}
		}
		//5分钟沉睡线程
		private void sleep5m(){
			try {
				Thread.sleep(1000 * 5 * 60);
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
			}
		}
		//10s沉睡线程
		private void sleep10s(){
			try {
				Thread.sleep(1000*10);
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
			}
		}
		
	}
	

	public static void testBalance(){
		try{
			//输入软件序列号和密码
//			String sn = "SDK-BBX-010-19394";
//			String pwd = "59@6d0f@";
//			
			String result_mt = Client.getInstance().getBalance();
			//输出可用的信息数量(条)
			log.info(result_mt);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	public static void main(String[] args) {
		try{
//			testBalance();	
//			testSendSms();
//			testAudioSms();
//			testAudioSms1();
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	
}
