package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import apptest.BaseTest;




/**
 * 接口名称：获取汇率信息
 * 测试接口：exchangeRate
 * 接口URL：http://trans.bitglobal.com/api/m/V?_?/exchangeRate
 * 
 * @author jiahua
 */
public class ExchangeRateTest extends BaseTest {

	
	@Test
	public void testExchangeRateSuccess_V1_1() throws Exception {
		printTitle("testExchangeRateSuccess_V1_1");
		exchangeRateV1_1("resMsg.code==1000", "CNY", "USD");
		exchangeRateV1_1("resMsg.code==1000", "USD", "CNY");	//两个方法返回一样的结果
	}
	
//	@Test
	public void testExchangeRateWrong_V1_1() throws Exception {
		printTitle("testExchangeRateWrong_V1_1");
		exchangeRateV1_1("resMsg.code!=1000", "CNY", "GBP");	//暂不支持其它币汇率
	}
	
	
	
	/**
	 * 接口方法
	 */
	public String exchangeRateV1_1(String test, String currencyA ,String currencyB) throws Exception {

		/*
			参数名		类型	是否必须	描述
			currencyA	String	是	类型：CNY，USD等
			currencyB	String	是	类型：CNY，USD等
		 */
		
		PostMethod method = new PostMethod(uri + "V1_1/exchangeRate");
		log.info(method.getURI());

//		doLoginIfNot();
//		addTokenUserId(method);
		method.addParameter("currencyA",currencyA);		
		method.addParameter("currencyB",currencyB);	
				
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
	
}
