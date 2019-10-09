package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;

import apptest.BaseTest;

/**
 * 接口名称：获取首页行情图表（历史时间） 
 * 测试接口：indexMarketChart
 * 接口URL：http://trans.vip.com/api/m/V?_?/indexMarketChart
 * 
 * @author jiahua
 */
public class IndexMarketChartTest extends BaseTest {
	
	@Test
	public void testAll() throws Exception{
		testIndexMarketChartSuccess_60_5_V1_1("ETH","BTC");
		testIndexMarketChartSuccess_60_5_V1_1("ETC","BTC");
		
		testIndexMarketChartSuccess_300_5_V1_1("ETH","BTC");
		testIndexMarketChartSuccess_300_5_V1_1("ETC","BTC");
		
		testIndexMarketChartSuccess_900_5_V1_1("ETH","BTC");
		testIndexMarketChartSuccess_900_5_V1_1("ETC","BTC");
		
		testIndexMarketChartSuccess_1800_5_V1_1("ETH","BTC");
		testIndexMarketChartSuccess_1800_5_V1_1("ETC","BTC");
		
		testIndexMarketChartSuccess_3600_5_V1_1("ETH","BTC");
		testIndexMarketChartSuccess_3600_5_V1_1("ETC","BTC");
		
		testIndexMarketChartSuccess_86400_5_V1_1("ETH","BTC");
		testIndexMarketChartSuccess_86400_5_V1_1("ETC","BTC");
		
		testIndexMarketChartWrong_123_5_V1_1("ETH","BTC");
		testIndexMarketChartWrong_123_5_V1_1("ETC","BTC");
	}

	@Test
	public void testIndexMarketChartSuccess_60_5_V1_1(String currencyType,String exchangeType) throws Exception {
		printTitle("testIndexMarketChartSuccess_60_5_V1_1");
		String json = indexMarketChartV1_1("resMsg.code==1000", currencyType, exchangeType, "60", "5");
		JSONArray jsonArray = getArrayFromJson(json, "datas.chartData");
		assertEquals(5, jsonArray.size());
	
	}

	@Test
	public void testIndexMarketChartSuccess_300_5_V1_1(String currencyType,String exchangeType) throws Exception {
		printTitle("testIndexMarketChartSuccess_300_5_V1_1");
		String json = indexMarketChartV1_1("resMsg.code==1000", currencyType, exchangeType, "300", "5");
		JSONArray jsonArray = getArrayFromJson(json, "datas.chartData");
		assertEquals(5, jsonArray.size());
	
	}

	@Test
	public void testIndexMarketChartSuccess_900_5_V1_1(String currencyType,String exchangeType) throws Exception {
		printTitle("testIndexMarketChartSuccess_900_5_V1_1");
		String json = indexMarketChartV1_1("resMsg.code==1000", currencyType, exchangeType, "900", "5");
		JSONArray jsonArray = getArrayFromJson(json, "datas.chartData");
		assertEquals(5, jsonArray.size());
	
	}

	@Test
	public void testIndexMarketChartSuccess_1800_5_V1_1(String currencyType,String exchangeType) throws Exception {
		printTitle("testIndexMarketChartSuccess_1800_5_V1_1");
		String json = indexMarketChartV1_1("resMsg.code==1000", currencyType, exchangeType, "1800", "5");
		JSONArray jsonArray = getArrayFromJson(json, "datas.chartData");
		assertEquals(5, jsonArray.size());
	
	}

	@Test
	public void testIndexMarketChartSuccess_3600_5_V1_1(String currencyType,String exchangeType) throws Exception {
		printTitle("testIndexMarketChartSuccess_3600_5_V1_1");
		String json = indexMarketChartV1_1("resMsg.code==1000", currencyType, exchangeType, "3600", "5");
		JSONArray jsonArray = getArrayFromJson(json, "datas.chartData");
		assertEquals(5, jsonArray.size());
	
	}

	@Test
	public void testIndexMarketChartSuccess_86400_5_V1_1(String currencyType,String exchangeType) throws Exception {
		printTitle("testIndexMarketChartSuccess_86400_5_V1_1");
		String json = indexMarketChartV1_1("resMsg.code==1000", currencyType, exchangeType, "86400", "5");
		JSONArray jsonArray = getArrayFromJson(json, "datas.chartData");
		assertEquals(5, jsonArray.size());
		
	}

	@Test
	public void testIndexMarketChartWrong_123_5_V1_1(String currencyType,String exchangeType) throws Exception {
		printTitle("testIndexMarketChartWrong_123_5_V1_1");
		String json = indexMarketChartV1_1("resMsg.code==1000", currencyType, exchangeType, "123", "5");
		JSONArray jsonArray = getArrayFromJson(json, "datas.chartData");
		assertEquals(0, jsonArray.size());
	}
	
	
	

	/**
	 * 接口方法
	 * 
	 * <pre>
	 * 参数名 		类型 		必须 	描述
	 * currencyType String 是 	货币类型： BTC：比特币，LTC：莱特币，，ETH：Ethereum，ETC：Ethereum Classic 
	 * exchangeType String 是 	兑换货币类型： BTC：人民币 BTC：比特币，LTC：莱特币，，ETH：Ethereum，ETC：Ethereum Classic 
	 * step 		String 是 	步长时间（秒），如30分钟，则传入1800 size String 是 记录数
	 * </pre>
	 */
	public String indexMarketChartV1_1(String test, String currencyType, String exchangeType, String step, String size)
			throws Exception {

		PostMethod method = new PostMethod(uri + "V1_1/indexMarketChart");
		log.info(method.getURI());

		// doLoginIfNot();
		// addTokenUserId(method);
		method.addParameter("currencyType", currencyType);
		method.addParameter("exchangeType", exchangeType);
		method.addParameter("step", step);
		method.addParameter("size", size);

		addConfig(method);
		method.addParameter("sign", getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");

		return doCheck(test, method);
	}

}
