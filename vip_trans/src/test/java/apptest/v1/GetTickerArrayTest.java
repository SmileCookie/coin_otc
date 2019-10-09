package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import apptest.BaseTest;






/**
 * 接口名称：获取盘口行情ticker
 * 测试接口：getTickerArray
 * 接口URL：http://trans.vip.com/api/m/V?_?/getTickerArray
 * 
 * @author jiahua
 */
public class GetTickerArrayTest extends BaseTest {

	
	@Test
	public void testGetTickerArraySuccess_V1_1() throws Exception {
		printTitle("testGetTickerArraySuccess_V1_1");
		getTickerArrayV1_1("resMsg.code==1000|resMsg.message*=成功|datas.marketDatas[0].symbol==etc_btc|datas.marketDatas[0].ticker.last>[0]", "BTC", "ETC");
		getTickerArrayV1_1("resMsg.code==1000|resMsg.message*=成功|datas.marketDatas[0].symbol==eth_btc|datas.marketDatas[0].ticker.last>[0]", "BTC", "ETH");
	
	}
	
	/*
	 * 无此交易盘  
	 */
	@Test
	public void testGetTickerArrayWrong1_V1_1() throws Exception {
		printTitle("testGetTickerArrayWrong1_V1_1");
		getTickerArrayV1_1("resMsg.code==1000|resMsg.message*=成功|datas.marketDatas[].size==[0]", "BTC", "BTC");
	}
	
	@Test
	public void testGetTickerArrayWrong2_V1_1() throws Exception {
		printTitle("testGetTickerArrayWrong2_V1_1");
		getTickerArrayV1_1("resMsg.code==1000|resMsg.message*=成功|datas.marketDatas[].size==[0]", "ETH", "ETC");
	}
	
	@Test
	public void testGetTickerArrayWrong3_V1_1() throws Exception {
		printTitle("testGetTickerArrayWrong3_V1_1");
		getTickerArrayV1_1("resMsg.code==1000|resMsg.message*=成功|datas.marketDatas[].size==[0]", "BTC", "LTC");
	}
	
	@Test
	public void testGetTickerArrayWrong4_V1_1() throws Exception {
		printTitle("testGetTickerArrayWrong4_V1_1");
		getTickerArrayV1_1("resMsg.code==1000|resMsg.message*=成功|datas.marketDatas[].size==0", "CNY", "AAA");
	}
	
	@Test
	public void testGetTickerArrayWrong5_V1_1() throws Exception {
		printTitle("testGetTickerArrayWrong5_V1_1");
		getTickerArrayV1_1("resMsg.code==1000|resMsg.message*=成功|datas.marketDatas[].size==0", "BTC", "CNY");
	}
	
	
	/**
	 * 接口方法
	 */
	public String getTickerArrayV1_1(String test, String exchangeType ,String currencyType) throws Exception {

		/*
			参数名			类型	是否必须	描述
			exchangeType	String	是	市场类型（兑换货币类型）： CNY：人民币 BTC：比特币，LTC：莱特币
			currencyType	String	否	货币类型： CNY：人民币 BTC：比特币，LTC：莱特币,ETH,ETC
		 */
		
		PostMethod method = new PostMethod(uri + "V1_1/getTickerArray");
		log.info(method.getURI());

//		doLoginIfNot();
//		addTokenUserId(method);
		method.addParameter("exchangeType",exchangeType);		
		method.addParameter("currencyTypes","['"+currencyType+"']");	
				
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
	
}
