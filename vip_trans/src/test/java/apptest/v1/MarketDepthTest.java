package apptest.v1;

import java.math.BigDecimal;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Ignore;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;

import apptest.BaseTest;




/**
 * 接口名称：获取行情盘口深度
 * 测试接口：marketDepth
 * 接口URL：http://api.bitglobal.com/api/m/V?_?/marketDepth
 * 
 * @author jiahua
 */
public class MarketDepthTest extends BaseTest {



	@Test
	public void testMarketDepth_Success_V1_1() throws Exception {
		
		/*
		 * ETC_BTC
		 */
		commonMethod("ETC", "BTC", "5", "", 	5, 0.000001);
		commonMethod("ETC", "BTC", "10", "", 	10, 0.000001);
		commonMethod("ETC", "BTC", "20", "", 	20, 0.000001);
		commonMethod("ETC", "BTC", "50", "", 	50, 0.000001);
		/**
		 * ETH_BTC
		 */
		commonMethod("ETH", "BTC", "5", "", 	5, 0.000001);
		commonMethod("ETH", "BTC", "10", "", 	10, 0.000001);
		commonMethod("ETH", "BTC", "20", "", 	20, 0.000001);
		commonMethod("ETH", "BTC", "50", "", 	50, 0.000001);
		
		
		
	}

	@Ignore
	@Test
	public void testMarketDepth_NoMarket_V1_1() throws Exception {
		printTitle("testMarketDepth_NoMarket_V1_1");
		marketDepthV1_1("resMsg.code==1001", "BTC", "CNY", "5", "0.3");	//无此合并深度
	}
	
	
	
	/**
	 * 通用封装
	 * @param currencyType
	 * @param exchangeType
	 * @param length
	 * @param depth
	 * @param expectedSize	期望返回数组长度
	 * @param expectedScale	期望精度
	 * @throws Exception
	 */
	private void commonMethod( String currencyType ,String exchangeType, String length, String depth, int expectedSize, double expectedScale) throws Exception {
		printTitle("testMarketDepth_Success_" + currencyType + "_" + length + "_" + depth.replace(".", "") + "_V1_1");
		String json = marketDepthV1_1("resMsg.code==1000", currencyType, exchangeType, length, depth);	 
		JSONArray asks = getArrayFromJson(json, "datas.asks");
		JSONArray bids = getArrayFromJson(json, "datas.bids");
		check(asks, expectedSize, expectedScale);
		check(bids, expectedSize, expectedScale);
	}
	
	/**
	 * 
	 * @param array	待检查数组
	 * @param length	档数
	 * @param scale		合并深度
	 */
	private void check(JSONArray array,int length, double scale) {

		assertTrue(array.size()<=length);
		printSubTitle("size::" + array.size());
		
		BigDecimal prices1 = array.getJSONArray(0).getBigDecimal(0);
		BigDecimal prices2 = array.getJSONArray(1).getBigDecimal(0);
		BigDecimal prices3 = array.getJSONArray(2).getBigDecimal(0);
		//检查至少有一个的精度为两位小数
		assertTrue(
				"精度错误，【期望】" + scale + ", 【实际】前5个价格都不是：【" + prices1 + "，" + prices2 + "，" + prices3 + "】",
				   prices1.ulp().doubleValue() == scale 
				|| prices2.ulp().doubleValue() == scale 
				|| prices3.ulp().doubleValue() == scale);
	}
	
	
	
	/**
	 * 接口方法
	 */
	public String marketDepthV1_1(String test, String currencyType ,String exchangeType, String length, String depth) throws Exception {

		/*
			参数名			类型		必须	描述
			currencyType	String	是	货币类型： BTC：比特币，LTC：莱特币，，ETH：Ethereum，ETC：Ethereum Classic
			exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classi
			length			String	是	数据长度，可传入 5，10，20，50
			depth			String	是	深度间距，0.1 0.3 0.5 1
		 */
		
		PostMethod method = new PostMethod(uri + "V1_1/marketDepth");
		log.info(method.getURI());

//		doLoginIfNot();
//		addTokenUserId(method);
		method.addParameter("currencyType",currencyType);		
		method.addParameter("exchangeType",exchangeType);	
		method.addParameter("length",length);	
		method.addParameter("depth",depth);	
				
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
	
}
