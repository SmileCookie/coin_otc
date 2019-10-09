package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import apptest.BaseTest;





/**
 * 接口名称： 5.3.11 取消单笔交易
 * 测试接口：cancelEntrust
 * 接口URL：http://trans.bitglobal.com/api/m/V?_?/cancelEntrust
 * - **请求参数**

  | 参数名         | 类型     | 是否必须 | 描述      |
  | :---------- | :----- | :--- | :------ |
  | userId      | String | 是    | 用户id    |
  | token       | String | 是    | 登录token |
  | currencyType| String | 是    | 货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic     |
  | exchangeType| String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |
  | entrustId   | String | 是    | 交易id     |

- **返回结果**

| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
| :------ | :----- | :--- | :------ | :--- |
 * @author jiahua
 */
public class CancelEntrustTest extends BaseTest {


	/*
	 * ETC_BTC
	 * 1.先委托一笔暂不会被成交的单，
	 * 2.查询刚才那笔委托
	 * 3.取消委托
	 * 4.检验委托状态为：“已取消”
	 */
	@Test
	public void testCancelEntrust_ETC_BTC_Success_V1_1() throws Exception {
		printTitle("testCancelEntrust_ETC_BTC_Success_V1_1");
		String unitPrice = "0.6"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "0";
		
		this.doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETC", "BTC", "0", unitPrice, number);
		
		Thread.sleep(200);	//如果取消委托后立即查记录，有可能取消委托还没执行完
		String json = this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" +
				"|datas.entrustTrades[0].entrustId!=null"
				, isBuy, "ETC", "BTC", "1", "3", "1", "1");
		String entrustId = getStringFromJson(json, "datas.entrustTrades[0].entrustId");
		
		this.cancelEntrustV1_1("resMsg.message*=成功", "ETC", "BTC", entrustId);
		
		Thread.sleep(300);	
		this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[].size==1|datas.entrustTrades[0].entrustId=="+ entrustId, isBuy, "ETC", "BTC", "1", "1", "1", "1");
	}
	
	
	/*
	 * ETC_BTC
	 * 取消一笔不属于自己的挂单
	 */
	@Test
	public void testCancelEntrust_ETC_BTC_cancelNotMe_V1_1() throws Exception {
		printTitle("testCancelEntrust_ETC_BTC_cancelNotMe_V1_1");
		this.cancelEntrustV1_1("resMsg.message*=失败", "ETC", "BTC", "20161216122151734");
	}
	
	/*
	 * ETC_BTC
	 * 取消一笔不属于自己的挂单
	 */
	@Test
	public void testCancelEntrust_ETC_BTC_cancelFinishedRecord_V1_1() throws Exception {
		printTitle("testCancelEntrust_ETC_BTC_cancelNotMe_V1_1");
		this.cancelEntrustV1_1("resMsg.message*=失败", "ETC", "BTC", "20161216122151466");	//已取消
		this.cancelEntrustV1_1("resMsg.message*=失败", "ETC", "BTC", "20161109120522958");	//已完成
	}
	
	
	/*
	 * ETH_BTC
	 */
	@Test
	public void testCancelEntrust_ETH_BTC_Success_V1_1() throws Exception {
		printTitle("testCancelEntrust_ETH_BTC_Success_V1_1");
		String unitPrice = "0.60"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "0";
		
		this.doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETH", "BTC", "0", unitPrice, number);
		
		Thread.sleep(200);	//如果取消委托后立即查记录，有可能取消委托还没执行完
		String json = this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" 
				, isBuy, "ETH", "BTC", "1", "3", "1", "1");
		String entrustId = getStringFromJson(json, "datas.entrustTrades[0].entrustId");
		Assert.assertNotNull("entrustId不应该为空", entrustId);
		
		this.cancelEntrustV1_1("resMsg.message*=成功", "ETH", "BTC", entrustId);
		
		Thread.sleep(200);	
		this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[].size==1|datas.entrustTrades[0].entrustId=="+ entrustId, isBuy, "ETH", "BTC", "1", "1", "1", "1");
	}
	
	
	
	
	
	/**
	 * 接口方法
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * currencyType	String	是	货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic
	 * entrustId	String	是	交易id
	 * </pre>
	 */
	public String cancelEntrustV1_1(String test, String currencyType, String exchangeType, String entrustId) throws Exception {
		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(uri + "V1_1/cancelEntrust");
		log.info(method.getURI());
		
		addTokenUserId(method);

		method.addParameter("currencyType", currencyType);
		method.addParameter("exchangeType", exchangeType);
		method.addParameter("entrustId", entrustId);
	
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
}
