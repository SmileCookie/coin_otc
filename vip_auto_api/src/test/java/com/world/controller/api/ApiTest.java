package com.world.controller.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import com.world.util.sign.EncryDigestUtil;

public class ApiTest {
	private String ACCESS_KEY = "8ea5f0d7-dd5f-490e-b33f-76105a2ebcdb";
	private String SECRET_KEY = "be9021f1930f1e60c19f47b0fcabf4aea9c6605d";
	//api order 方法测试
	@Test
	public void testOrderSell(){
		try{
			ACCESS_KEY = "5ed5deef-59ac-4e49-8b27-548436f094c9";
			SECRET_KEY = "670d462b-2f69-4787-b9cb-a65b7fddd20b";
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			while(true){
				//需加密的请求参数
				//String params = "method=order&accesskey="+ACCESS_KEY + "&price=300&amount=9&tradeType=1&currency=ltc";
				String params = "method=order&accesskey="+ACCESS_KEY + "&price=6150&amount=1&tradeType=0&currency=btc";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = "http://api.bitglobal.com/api/order?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				//请求测试
				String callback = testRequest(url);
				System.out.println(callback);
				Thread.sleep(3*60*1000);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	@Test
	public void testOrderBuy(){
		try{
			ACCESS_KEY = "20095add-e615-4ee6-b7c3-89ae907bd8ac";
			SECRET_KEY = "b73b055e-bcc4-480f-af7e-0edc7729b985";
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			while(true){
				//需加密的请求参数
				//String params = "method=order&accesskey="+ACCESS_KEY + "&price=300&amount=9&tradeType=1&currency=ltc";
				String params = "method=order&accesskey="+ACCESS_KEY + "&price=6150&amount=1&tradeType=1&currency=btc";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = "http://api.bitglobal.com/api/order?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				//请求测试
				String callback = testRequest(url);
				System.out.println(callback);
				Thread.sleep(3*60*1000);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Test
	public void testOrder(){
		try{
			ACCESS_KEY = "20095add-e615-4ee6-b7c3-89ae907bd8ac";
			SECRET_KEY = "b73b055e-bcc4-480f-af7e-0edc7729b985";
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			//String params = "method=order&accesskey="+ACCESS_KEY + "&price=300&amount=9&tradeType=1&currency=ltc";
			String params = "method=order&accesskey="+ACCESS_KEY + "&price=3914.4555&amount=1.25&tradeType=0&currency=btc";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/order?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println(url);
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//api cancelOrder 方法测试
	@Test
	public void testCancelOrder(){
		try{
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			//String params = "method=cancelOrder&accesskey="+ACCESS_KEY + "&id=20131219686&currency=ltc";
			String params = "method=cancelOrder&accesskey="+ACCESS_KEY + "&id=201403187174423&currency=btc";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/cancelOrder?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//api getOrder 方法测试
	@Test
	public void testGetOrder(){
		try{
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			String params = "method=getOrder&accesskey="+ACCESS_KEY + "&id=201403187174423&currency=btc";
			//String params = "method=getOrder&accesskey="+ACCESS_KEY + "&id=20131219686&currency=btc";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/getOrder?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	//api getOrders 方法测试
	@Test
	public void testGetOrders(){
		try{
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			String params = "method=getOrders&accesskey="+ACCESS_KEY + "&tradeType=0&currency=btc&pageIndex=1";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/getOrders?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	//api getOrders 方法测试
	@Test
	public void testGetOrdersNew(){
		try{
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			String params = "method=getOrdersNew&accesskey="+ACCESS_KEY + "&tradeType=1&currency=btc&pageIndex=1&pageSize=1";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/getOrdersNew?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	
	//api getOrders 方法测试
	@Test
	public void getUnfinishedOrdersIgnoreTradeType(){
		try{
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			String params = "method=getUnfinishedOrdersIgnoreTradeType&accesskey="+ACCESS_KEY + "&currency=btc&pageIndex=1&pageSize=10";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/getUnfinishedOrdersIgnoreTradeType?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	//api getOrders 方法测试
	@Test
	public void getOrdersIgnoreTradeType(){
		try{
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			String params = "method=getOrdersIgnoreTradeType&accesskey="+ACCESS_KEY + "&currency=btc&pageIndex=1&pageSize=2";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/getOrdersIgnoreTradeType?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	//api getAccountInfo 方法测试
	@Test
	public void testGetAccountInfo(){
		
		try{
			SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);	
			//需加密的请求参数
			String params = "method=getAccountInfo&accesskey="+ACCESS_KEY;
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = "http://api.bitglobal.com/api/getAccountInfo?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println(url);
			//请求测试
			String callback = testRequest(url);
			System.out.println(callback);
			Assert.assertNotSame(callback, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	//api getAccountInfo 方法测试
	@Test
	public void testGetAccountInfo2(){
		String SECRET_KEY = "E72EF2D50922EC8434255FB8C00C5D21";
		SECRET_KEY = EncryDigestUtil.digest(SECRET_KEY);
		System.out.println("*** SECRET_KEY = " + SECRET_KEY);
		try{
			//需加密的请求参数
			String params = "type=1&phone=13590758448&email=jiahua_g@163.com";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			System.out.println("*** hash = " + hash);
			//请求地址
			String url = "https://api.bitglobal.com/api/getAccountInfo2?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println(url);
			//请求测试
			String callback = testRequest(url);
			System.out.println("testGetAccountInfo2:callback = " + callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param reqUrl
	 */
	public String testRequest(String reqUrl) throws Exception {  
	        URL url = new URL(reqUrl);  
	        
	        URLConnection connection = url.openConnection();  
	        connection.setDoOutput(true);  
	        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "iso-8859-1");
	        
	        out.flush();  
	        out.close();  
	        
	        String sCurrentLine;  
	        String sTotalString;  
	        sCurrentLine = "";  
	        sTotalString = "";  
	        InputStream l_urlStream;  
	        l_urlStream = connection.getInputStream();  
	        // 传说中的三层包装阿！  
	        BufferedReader l_reader = new BufferedReader(new InputStreamReader(  
	                l_urlStream));  
	        while ((sCurrentLine = l_reader.readLine()) != null) {  
	            sTotalString += sCurrentLine;  
	  
	        }  
	        
	        return sTotalString;
	}
	

	//////给用户生成密钥对
	@Test
	public void getKeyAndSecret(){
		String key = UUID.randomUUID().toString();//key
		String secret = UUID.randomUUID().toString();//密钥
		
		System.out.println("成功生成密钥对：key:" + key + ",secret:" + secret);
		
		long result = 0;
		for (int i = 0; i < 1000; i++) {
			long start = System.currentTimeMillis();
			EncryDigestUtil.digest(secret);
			long end =System.currentTimeMillis();
			result += (end - start);
		}
		System.out.println(result/1000);
	}
}
