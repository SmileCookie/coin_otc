package com.world.controller.api;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;
import com.world.util.sign.EncryDigestUtil;

public class RestTest {
	
	private static Logger log = Logger.getLogger(RestTest.class);
	
	
	//本地测试 
	private String ACCESS_KEY = "8ea5f0d7-dd5f-490e-b33f-76105a2ebcdb";
	private String SECRET_KEY = "be9021f1930f1e60c19f47b0fcabf4aea9c6605d";
	public static String URL_PREFIX = "http://api.vip.com/api/";
	public static String PAY_PASS = "feng412708";

	String[] currencyArr = new String[]{ "eth_btc", "etc_btc"};
	
	/**
	 * 提供下单方法
	 * @return
	 */
	public JSONObject order(){
		JSONObject json  = new JSONObject();
		String params = "method=order&accesskey="+ACCESS_KEY+"&price=0.0001&amount=1&tradeType=1&currency=eth_btc";
		System.out.println("params=" + params);
		//参数执行加密
		String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
		//请求地址
		String url = URL_PREFIX+"order?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
		System.out.println("testOrder url: " + url);
		//请求测试
		String callback = get(url, "UTF-8");
		if(callback!=null && callback.startsWith("{")){
			json = JSONObject.parseObject(callback);
		}
		
		return json;
	}
	
	/**
	 * 委托下单
	 * tradeType 1买，0卖
	 */
	@Test
	public void testOrder(){
		try{
			//需加密的请求参数， tradeType=0卖单
//			String params = "method=order&accesskey="+ACCESS_KEY+"&customerOrderId=1&price=0.02&amount=0.1&tradeType=0&currency=eth_btc";
//			String params = "method=order&accesskey="+ACCESS_KEY+"&price=100&amount=5&tradeType=1&currency=ltc_cny";
			/*String params = "method=order&accesskey="+ACCESS_KEY+"&price=0.0001&amount=1&tradeType=1&currency=eth_btc";
			System.out.println("params=" + params);
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"order?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("testOrder url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");*/
			JSONObject json = order();
			System.out.println("testOrder 结果: " + json.toJSONString());
			Assert.assertNotSame(json, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	
	/**
	 * 取消下单
	 */
	@Test
	public void testCancelOrder(){
		
		try{
			String orderId="";
			JSONObject json = order();
			if(json.containsKey("code") && json.getInteger("code")==1000){
				orderId = json.getString("id");
			}else{
				System.out.println("order 下单 结果: 失败！" );
				return ;
			}
			Thread.sleep(1000);//延迟一秒可以在页面看到挂单出现，然后取消消失
			
			//需加密的请求参数
			String params = "method=cancelOrder&accesskey="+ACCESS_KEY + "&id=" + orderId + "&currency=eth_btc";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"cancelOrder?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("testGetOrder url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("testGetOrder 结果: " + callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取订单信息
	 */
	@Test
	public void testGetOrder(){
		try{
			String orderId="";
			JSONObject json = order();
			if(json.containsKey("code") && json.getInteger("code")==1000){
				orderId = json.getString("id");
				System.out.println("order 下单 结果: "+orderId+"" );
			}else{
				System.out.println("order 下单 结果: 失败！" );
				return ;
			}
			Thread.sleep(1000);//延迟一秒可以在页面看到挂单出现，然后取消消失
			
			//需加密的请求参数
			String params = "method=getOrder&accesskey="+ACCESS_KEY + "&id=" + orderId + "&currency=eth_btc";
			//String params = "method=getOrder&accesskey="+ACCESS_KEY + "&id=20131219686&currency=btc";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"getOrder?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("testGetOrder url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("testGetOrder 结果: " + callback);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取多个委托买单或卖单，每次请求返回10条记录
	 */
	@Test
	public void testGetOrders(){
		try{
			for(String currency : currencyArr){
				//需加密的请求参数
				String params = "method=getOrders&accesskey="+ACCESS_KEY + "&tradeType=1&currency=" + currency + "&pageIndex=1";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = URL_PREFIX+"getOrders?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				log.info("testGetOrders url: " + url);
				//请求测试
				String callback = get(url, "UTF-8");
				log.info("testGetOrders 结果: " + callback);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * (新)获取多个委托买单或卖单，每次请求返回pageSize<=100条记录
	 */
	@Test
	public void testGetOrdersNew(){
		try{
			
			for(String currency : currencyArr){
				//需加密的请求参数
				String params = "method=getOrdersNew&accesskey="+ACCESS_KEY + "&tradeType=1&currency=" + currency + "&pageIndex=1&pageSize=1";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = URL_PREFIX+"getOrdersNew?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				log.info("testGetOrdersNew url: " + url);
				//请求测试
				String callback = get(url, "UTF-8");
				log.info("testGetOrdersNew 结果: " + callback);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * 与getOrdersNew的区别是取消tradeType字段过滤，可同时获取买单和卖单，每次请求返回pageSize<=100条记录
	 */
	@Test
	public void getOrdersIgnoreTradeType(){
		try{
			for(String currency : currencyArr){
				//需加密的请求参数
				String params = "method=getOrdersIgnoreTradeType&accesskey="+ACCESS_KEY + "&currency=" + currency + "&pageIndex=1&pageSize=1";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = URL_PREFIX+"getOrdersIgnoreTradeType?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				log.info("getOrdersIgnoreTradeType url: " + url);
				//请求测试
				String callback = get(url, "UTF-8");
				log.info("getOrdersIgnoreTradeType 结果: " + callback);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * 获取未成交或部份成交的买单和卖单，每次请求返回pageSize<=100条记录
	 */
	@Test
	public void getUnfinishedOrdersIgnoreTradeType(){
		try{
			for(String currency : currencyArr){
				//需加密的请求参数
				String params = "method=getUnfinishedOrdersIgnoreTradeType&accesskey="+ACCESS_KEY + "&currency=" + currency + "&pageIndex=1&pageSize=20";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = URL_PREFIX+"getUnfinishedOrdersIgnoreTradeType?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				log.info("getUnfinishedOrdersIgnoreTradeType url: " + url);
				//请求测试
				String callback = get(url, "UTF-8");
				log.info("getUnfinishedOrdersIgnoreTradeType 结果: " + callback);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取个人资产信息
	 * 测试 SECRET_KEY 不需要再处理
	 * {
		    "result": [
		        {
		            "balance": 178256.31,
		            "freeze": 21743.695,
		            "fundsType": 1,
		            "propTag": "CNY",
		            "total": 200000,
		            "unitTag": "￥"
		        },
		        {
		            "balance": 10000158,
		            "freeze": 2081.4492,
		            "fundsType": 2,
		            "propTag": "BTC",
		            "total": 10002240,
		            "unitTag": "฿"
		        },
		        {
		            "balance": 9999429,
		            "freeze": 433.648,
		            "fundsType": 5,
		            "propTag": "ETH",
		            "total": 9999863,
		            "unitTag": "E"
		        },
		        {
		            "balance": 9997366,
		            "freeze": 2605.172,
		            "fundsType": 6,
		            "propTag": "ETC",
		            "total": 9999972,
		            "unitTag": "EC"
		        },
		        {
		            "balance": 0,
		            "freeze": 0,
		            "fundsType": 7,
		            "propTag": "LTC",
		            "total": 0,
		            "unitTag": "C"
		        }
		    ]
		}
	 */
	@Test
	public void testGetAccountInfo(){
		//while (true) {
            try{
    			//String SECRET_KEY = EncryDigestUtil.digest(this.SECRET_KEY);	
    			//需加密的请求参数
    			String params = "method=getAccountInfo&accesskey="+ACCESS_KEY;
    			//参数执行加密
    			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
    			//请求地址
    			String url = URL_PREFIX+"getAccountInfo?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
    			log.info("testGetAccountInfo url: " + url);
    			//请求测试
    			String callback = get(url, "UTF-8");
    			log.info("testGetAccountInfo 结果: " + callback);
    			Assert.assertNotSame(callback, "{}");
    		}catch(Exception ex){
    			ex.printStackTrace();
    		}
            
           
        //}
	}
	
	/**
	 * 获取个人的充值地址
	 * {
		    "code": 1000,
		    "message": {
		        "des": "success",
		        "isSuc": true,
		        "datas": {
		            "key": "0xbe9d2dad98705b7110db5ecc805c20732f69fcef"
		        }
			 }
		}
	 */
	@Test
	public void testGetUserAddress(){
		try{
			//需加密的请求参数
			String params = "method=getUserAddress&accesskey="+ACCESS_KEY+"&currency=etc";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"getUserAddress?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("getUserAddress url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("getUserAddress 结果: " + callback);
			Assert.assertNotSame(callback, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取认证的提现地址
	 * {
		    "code": 1000,
		    "message": {
		        "des": "success",
		        "isSuc": true,
		        "datas": [
		            {
		                "address": "1TMCuhtiCxz6622L8NK5dwtEPwopajixe",
		                "memo": "BTC"
		            }
		        ]
		    }
		}
	 */
	@Test
	public void testGetWithdrawAddress(){
		try{
			//需加密的请求参数
			String params = "method=getWithdrawAddress&accesskey="+ACCESS_KEY+"&currency=btc";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"getWithdrawAddress?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("getWithdrawAddress url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("getWithdrawAddress 结果: " + callback);
			Assert.assertNotSame(callback, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取提现记录
	 * {
		    "code": 1000,
		    "message": {
		        "des": "success",
		        "isSuc": true,
		        "datas": {
		            "list": [
		                {
		                    "amount": 1,
		                    "fees": 0.0001,
		                    "id": 201609307,
		                    "manageTime": -28800000,
		                    "status": 0,
		                    "submitTime": 1475200120000,
		                    "toAddress": "1TMCuhtiCxz6622L8NK5dwtEPwopajixe"
		                },
		                {
		                    "amount": 1,
		                    "fees": 0.0001,
		                    "id": 201609306,
		                    "manageTime": -28800000,
		                    "status": 0,
		                    "submitTime": 1475216194000,
		                    "toAddress": "1Eocmuhx6sy6d2LKtDEYQ21e9ng2wq8wsj"
		                },
		                {
		                    "amount": 1,
		                    "fees": 0.01,
		                    "id": 201609303,
		                    "manageTime": 1474708960000,
		                    "status": 3,
		                    "submitTime": 1475218172000,
		                    "toAddress": "0x05737c74756790643132a5b7f140a2880e769342"
		                },
		                {
		                    "amount": 1,
		                    "fees": 0.0001,
		                    "id": 201609302,
		                    "manageTime": -28800000,
		                    "status": 0,
		                    "submitTime": 1475207636000,
		                    "toAddress": "1Eocmuhx6sy6d2LKtDEYQ21e9ng2wq8wsj"
		                }
		            ],
		            "pageIndex": 1,
		            "pageSize": 10,
		            "totalCount": 4,
		            "totalPage": 1
		        }
		    }
		}
	 * 
	 */
	@Test
	public void testGetWithdrawRecord(){
		try{
			//需加密的请求参数
			String params = "method=getWithdrawRecord&accesskey="+ACCESS_KEY+"&currency=btc&pageIndex=1&pageSize=10";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"getWithdrawRecord?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("getWithdrawRecord url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("getWithdrawRecord 结果: " + callback);
			Assert.assertNotSame(callback, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取虚拟货币充值记录
	 *  {
			"code"        : 1000,
			"message"     : {
				"des"         : "success",
				"isSuc"       : true,
				"datas"       : {
					"list"        : [
						{
							"amount"      : 0.001,
							"hash"        : "a516ffeed83eba01e1537ed1be2508ade4f5b2b91c0de2fb2868cff7ee3196b4",
							"status"      : "确认成功",
							"submit_time" : "2016-09-23 18:50:16",
							"type"        : "充值"
						}
					],
					"pageIndex"   : 1,
					"pageSize"    : 10,
					"total"       : 1
				}
			}
		}

	 */
	@Test
	public void testGetChargeRecord(){
		try{
			//需加密的请求参数
			String params = "method=getChargeRecord&accesskey="+ACCESS_KEY+"&currency=btc&pageIndex=1&pageSize=10";
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"getChargeRecord?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("getChargeRecord url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("getChargeRecord 结果: " + JsonFormatTool.formatJson(callback, "\t") );
			Assert.assertNotSame(callback, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * 取消提现操作
	 */
	@Test
	public void testCancelWithdraw(){
		try{
			//String SECRET_KEY = EncryDigestUtil.digest(this.SECRET_KEY);	
			//需加密的请求参数
			String params = "method=cancelWithdraw&accesskey="+ACCESS_KEY+"&currency=etc&downloadId=2016090245&safePwd="+PAY_PASS;
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"cancelWithdraw?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("cancelWithdraw url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("cancelWithdraw 结果: " + callback);
			Assert.assertNotSame(callback, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 提现操作
	 */
	@Test
	public void withdraw(){
		try{
			//String SECRET_KEY = EncryDigestUtil.digest(this.SECRET_KEY);	
			//需加密的请求参数
			/*
			//# Request
			GET https://api.bitglobal.com/api/withdraw?accesskey=your_access_key
				&amount=0.01&currency=btc_cny&fees=0.001&method=withdraw&itransfer=0
				&receiveAddr=14fxEPirL9fyfw1i9EF439Pq6gQ5xijUmp&safePwd=资金安全密码
				&sign=请求加密签名串&reqTime=当前时间毫秒数
				*/
			
			String addr = "1TMCuhtiCxz6622L8NK5dwtEPwopajixe";
			String fees = "0.0003";
			String currency = "btc";
			String amount = "0.0004";
			String itransfer = "0";	 
//			String params = "method=withdraw"+ "&itransfer=" + itransfer +"&accesskey=" + ACCESS_KEY + "&amount=" + amount + "&currency=" + currency
//					+ "&fees=" + fees + "&receiveAddr=" + addr + "&safePwd=" + PAY_PASS;
			String params = "accesskey=" + ACCESS_KEY + "&amount=" + amount + "&currency=" + currency + "&fees=" + fees
					+ "&itransfer=" + itransfer + "&method=withdraw&receiveAddr=" + addr + "&safePwd=" + PAY_PASS;
			System.out.println(params);
			//参数执行加密
			String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
			//请求地址
			String url = URL_PREFIX+"withdraw?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
			System.out.println("withdraw url: " + url);
			//请求测试
			String callback = get(url, "UTF-8");
			System.out.println("withdraw 结果: " + callback);
			Assert.assertNotSame(callback, "{}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取交易记录
	 */
	@Test
	public void testGetTransRecord(){
		try{
			String[] currencyArr = new String[]{"eth_btc", "eth_btc"};
			for(String currency : currencyArr){
				//需加密的请求参数
				String params = "method=getTransRecord&accesskey="+ACCESS_KEY + "&currency=" + currency + "&pageIndex=1&pageSize=100&sinceId=0";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = URL_PREFIX+"getTransRecord?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				log.info("getTransRecord url: " + url);
				//请求测试
				String callback = get(url, "UTF-8");
				log.info("getTransRecord 结果: " + callback);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取交易记录
	 * [
		    {
		        "entrustIdBuy": 201609233,
		        "entrustIdSell": 201609232,
		        "numbers": 0.253,
		        "status": 0,
		        "submitTime": 1474600869087,
		        "totalMoney": 0.00649451,
		        "transRecordId": 1,
		        "types": 1,
		        "unitPrice": 0.02567
		    },
		    {
		        "entrustIdBuy": 201609233,
		        "entrustIdSell": 20160923403,
		        "numbers": 1.7,
		        "status": 0,
		        "submitTime": 1474611331797,
		        "totalMoney": 0.043639,
		        "transRecordId": 2,
		        "types": 0,
		        "unitPrice": 0.02567
		    },
		    {
		        "entrustIdBuy": 201609233,
		        "entrustIdSell": 20160923408,
		        "numbers": 2,
		        "status": 0,
		        "submitTime": 1474611406275,
		        "totalMoney": 0.05134,
		        "transRecordId": 3,
		        "types": 0,
		        "unitPrice": 0.02567
		    }
		]
	 */
	@Test
	public void testGetOrderTransRecord(){
		try{
			String[] currencyArr = new String[]{"eth_btc", "etc_btc"};
			for(String currency : currencyArr){
				//需加密的请求参数
				String params = "method=getOrderTransRecord&accesskey="+ACCESS_KEY + "&currency=" + currency + "&orderId=201609233&pageIndex=1&pageSize=100";
				//参数执行加密
				String hash = EncryDigestUtil.hmacSign(params, SECRET_KEY);
				//请求地址
				String url = URL_PREFIX+"getOrderTransRecord?" + params + "&sign=" + hash + "&reqTime=" + System.currentTimeMillis();
				log.info("getOrderTransRecord url: " + url);
				//请求测试
				String callback = get(url, "UTF-8");
				log.info("getOrderTransRecord 结果: " + callback);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	

	public static String API_DOMAIN = "http://t.vip.com";
	
	/**
	 * 测试获取行情
	 * {
		    "date": "1482738837197",
		    "ticker": {
		        "buy": "0.030516",
		        "high": "0.032489",
		        "last": "0.030516000",
		        "low": "0.030516",
		        "riseRate": -6.07,
		        "sell": "0.033227",
		        "vol": "4.0"
		    }
		}
	 */
	@Test
	public void testTicker() {
		try {
			//do{
				for(String currency : currencyArr){
					// 请求地址
					String url = API_DOMAIN+"/data/v1/ticker?currency="+currency;
//					String currency = "eth_cny";
//					String url = "https://www.okcoin.cn/api/v1/ticker.do?symbol="+currency;
					log.info(currency + "-testTicker url: " + url);
					// 请求测试
					String callback = get(url, "UTF-8");
					log.info(currency + "-testTicker 结果: " + callback);
				}
				Thread.sleep(3000);
			//}while(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 测试获取深度
	 * {
    "asks": [
        [
            0.003215,
            1.03
        ],
        [
            0.003214,
            1.285
        ],
        [
            0.003213,
            1.697
        ],
        [
            0.003212,
            0.982
        ],
        [
            0.003211,
            1.736
        ]
    ],
    "bids": [
        [
            0.002253,
            0.177
        ],
        [
            0.002248,
            0.333
        ],
        [
            0.002238,
            0.276
        ],
        [
            0.002228,
            0.276
        ],
        [
            0.002226,
            0.323
        ]
    ]
}
	 */
	@Test
	public void testDepth() {
		try {
//			do{
				for(int i=0;i<currencyArr.length;i++){
					String currency = currencyArr[i];
					// 请求地址
					String url = API_DOMAIN+"/data/v1/depth?currency=" + currency;
//					String url = API_DOMAIN+"/data/v1/depth?currency=" + currency + "&size=3&merge=" + merge;
//					String url = API_DOMAIN+"/data/v1/depth?currency=" + currency + "&size=3";
					log.info(currency + "-testDepth url: " + url);
					// 请求测试
					String callback = get(url, "UTF-8");
					log.info(currency + "-testDepth 结果: " + callback);
				}
//			}while(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 测试获取最近交易记录
	 * [
	    {
	        "amount": 0.034,
	        "date": 1482487187,
	        "price": 0.00228,
	        "tid": 19852,
	        "trade_type": "ask",
	        "type": "sell"
	    },
	    {
	        "amount": 0.259,
	        "date": 1482487187,
	        "price": 0.002274,
	        "tid": 19853,
	        "trade_type": "ask",
	        "type": "sell"
	    }
	    ]
	 */
	@Test
	public void testTrades() {
		try {
			//do{
				for(String currency : currencyArr){
					// 请求地址
					String url = API_DOMAIN+"/data/v1/trades?currency="+currency;
					log.info(currency + "-testTrades url: " + url);
					// 请求测试
					String callback = get(url, "UTF-8");
					log.info(currency + "-testTrades 结果: " + callback);
				}
				Thread.sleep(3000);
			//}while(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 测试获取K线数据
	 * 
	 * {
		    "data": [
		        [
		            1482143460000,
		            0.030222,
		            0.03422,
		            0.027371,
		            0.031042,
		            981.47
		        ],
		        [
		            1482144900000,
		            0.030526,
		            0.034229,
		            0.027766,
		            0.029601,
		            50.254
		        ],
		        [
		            1482198120000,
		            0.03283,
		            0.03334,
		            0.02807,
		            0.03334,
		            115.472
		        ],
		        [
		            1482199920000,
		            0.031181,
		            0.034348,
		            0.028172,
		            0.028879,
		            475.938
		        ],
		        [
		            1482487200000,
		            0.030918,
		            0.033227,
		            0.030918,
		            0.033227,
		            23.248
		        ],
		        [
		            1482720540000,
		            0.032489,
		            0.032489,
		            0.030516,
		            0.030516,
		            4
		        ]
		    ],
		    "moneyType": "BTC",
		    "symbol": "ETH"
		}
	 */
	@Test
	public void testKline() {
		//do{
			try {
				log.info("开始testKline");
				for(String currency : currencyArr){
					// 请求地址
					String url = API_DOMAIN+"/data/v1/kline?currency="+currency+"&times=1min";
					log.info(currency + "-testKline url: " + url);
					// 请求测试
					String callback = get(url, "UTF-8");
					JSONObject json = JSONObject.parseObject(callback);
					log.info(currency + "-testKline 结果: " + json.toJSONString());
				}
				Thread.sleep(1000);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		//}while(true);
	}
	
	/**
	 * 测试获取行情
	 */
	@Test
	public void testBtcTicker() {
		try {
			// 请求地址
			String url = API_DOMAIN+"/data/ticker";
			System.out.println("testBtcTicker url: " + url);
			// 请求测试
			String callback = get(url, "UTF-8");
			System.out.println("testBtcTicker 结果: " + callback);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 测试获取深度
	 */
	@Test
	public void testBtcDepth() {
		try {
			// 请求地址
			String url = API_DOMAIN+"/data/depth";
			System.out.println("testBtcDepth url: " + url);
			// 请求测试
			String callback = get(url, "UTF-8");
			System.out.println("testBtcDepth 结果: " + callback);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 测试获取最近交易记录
	 */
	@Test
	public void testBtcTrades(){
		int success = 0;
		try {
			//for(int i=0;i<100000;i++){
				//请求地址
				String url =API_DOMAIN+"/data/trades";
				System.out.println("testTrades url: " + url);
				//请求测试
				String callback = get(url, "UTF-8");
				System.out.println("testTrades 结果: " + callback);
				
				System.out.println("等待0.01秒");
				System.out.println("");
				
				success = success+1;
				Thread.sleep(10);
			//}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("成功次数=" + success);
	}
	
	
	
	/**
	 * 测试获取深度
	 */
	@Test
	public void testHangqing() {
		
		int success = 0;
		try {
			for(int i=0;i<10;i++){
				for(int j=0;j<currencyArr.length;j++){
					String currency = currencyArr[j];
					
					// 请求地址
					String url = API_DOMAIN + "/data/v1/depth?currency="+currency;
					System.out.println("testDepth url: " + url);
					// 请求测试
					String callback = get(url, "UTF-8");
					System.out.println("testBtcDepth 结果: " + callback);
					
					//请求地址
					url = API_DOMAIN + "/data/v1/trades?currency="+currency;
					System.out.println("testTrades url: " + url);
					//请求测试
					callback = get(url, "UTF-8");
					System.out.println("testTrades 结果: " + callback);
					
					// 请求地址
					url = API_DOMAIN + "/data/v1/ticker?currency="+currency;
					System.out.println("testTicker url: " + url);
					// 请求测试
					callback = get(url, "UTF-8");
					System.out.println("testTicker 结果: " + callback);
					
					System.out.println("等待0.01秒");
					System.out.println("");
				}
				success = success+1;
				Thread.sleep(10);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("成功次数=" + success);
	}
	
	/**
	 * 
	 * @param urlAll
	 *            :请求接口
	 * @param charset
	 *            :字符编码
	 * @return 返回json结果
	 */
	public String get(String urlAll, String charset) {
	/*	BufferedReader reader = null;
		String result = null;
		StringBuffer sbf = new StringBuffer();
		String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";// 模拟浏览器
		try {
			URL url = new URL(urlAll);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(30000);
			connection.setConnectTimeout(30000);
			connection.setRequestProperty("User-agent", userAgent);
			connection.connect();
			InputStream is = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, charset));
			String strRead = null;
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		String result = null;
		try {
			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod(urlAll);
			client.executeMethod(method);
			result = method.getResponseBodyAsString();
			
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
		return result;
	}
	
}
