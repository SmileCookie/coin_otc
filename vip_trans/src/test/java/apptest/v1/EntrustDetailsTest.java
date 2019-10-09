package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import apptest.BaseTest;






/**
 * 接口名称：4.66 获取交易明细	（当前app好像没有调用）
 * 测试接口：entrustDetails
 * 接口URL：http://trans.vip.com/api/m/V?_?/entrustDetails
 * - **请求参数**

  | 参数名         | 类型     | 是否必须 | 描述      |
  | :---------- | :----- | :--- | :------ |
  | userId      | String | 是    | 用户id    |
  | token       | String | 是    | 登录token |
  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |
  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic |
  | entrustId | Integer | 是    |   委托id  |
- **返回结果**

| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
| :------ | :----- | :--- | :------ | :--- |
| entrustOrders | 委托详情数组[EntrustOrder] | 是    |  | 委托详情数组   |
 * @author jiahua
 */
public class EntrustDetailsTest extends BaseTest {

	/*
	 * ETC_BTC
	 * 
	 * 检查 获取已成交记录的明细(一定存在明细记录)
	 */
	@Test
	public void testEntrustDetails_ETC_BTC_Success_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETC_BTC_Success_V1_1");
		
		String unitPrice = "0.060"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "1";
		
		String json = this.doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETC", "BTC", "0", unitPrice, number);
		Thread.sleep(1000);
		/*
		Thread.sleep(1000);	//如果取消委托后立即查记录，有可能取消委托还没执行完
		String json = this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" +
				"|datas.entrustTrades[0].entrustId!=null" , isBuy, "BTC", "CNY", "1", "2", "1", "1");
		*/
		
		String entrustId = getStringFromJson(json, "datas.entrustId");
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size>=1", "ETC", "BTC", entrustId);	//已成交
		
		//entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETH", "BTC", entrustId);	//其它盘不存在此ID
	}
	
	/*
	 * ETC_BTC
	 */
	@Test
	public void testEntrustDetails_ETC_BTC_NotExist_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETC_BTC_NotExist_V1_1");
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETC", "BTC", "123123123123");	
	}
	
	/*
	 * ETC_BTC
	 */
	@Test
	public void testEntrustDetails_ETC_BTC_WrongInput_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETC_BTC_WrongInput_V1_1");
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETC", "BTC", "qweqweqweqwe");	
		/*
		 * TODO [bug] 内部异常
		 *	【结果】{委托取消区间订单失败-区间价格可能颠倒导致错误:java.lang.NumberFormatException: For input string: "qweqweqweqwe"}
		 *	【预期】委托id格式不正确 ，或返回空结果
		 *	【注】不要简单返回内部错误
		 */
	}
	
	/*
	 * ETC_BTC
	 * 获取不属于自己的交易明细
	 * 【预期】返回空结果
	 */
	@Test
	public void testEntrustDetails_ETC_BTC_NotMe_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETC_BTC_NotMe_V1_1");
		
//		//另一帐号登录，提交委托
		this.userId = "14";
		this.userName = "13760790454";
		this.password = "feng412708";
		doLoginV1_1("resMsg.message*=成功", userName, password, "", "", countryCode);
		
		String unitPrice = "0.006"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "1";
		String json = this.doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETC", "BTC", "0", unitPrice, number,userId,userName,password);
		
		Thread.sleep(1000);	
		/*String json = this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades[].size==1|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" +
				"|datas.entrustTrades[0].entrustId!=null" , isBuy, "BTC", "CNY", "1", "2", "1", "1",userId,userName,password);
		*/
		String entrustId = getStringFromJson(json, "datas.entrustId");
		
		//我的帐号登录，获取交易明细
		this.userId = "11";
		this.userName = "13760790453";
		this.password = "feng412708";
		doLoginV1_1("resMsg.message*=成功", userName, password, "", "", countryCode);
		
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETC", "BTC", entrustId);
	}
	
	
	
	
	/*
	 * ETH_BTC
	 * 检查 获取已成交记录的明细(一定存在明细记录)
	 */
	@Test
	public void testEntrustDetails_ETH_BTC_Success_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETH_BTC_Success_V1_1");
		
		String unitPrice = "0.6"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "1";
		
		String json = this.doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETH", "BTC", "0", unitPrice, number);
		
		Thread.sleep(1000);	//如果取消委托后立即查记录，有可能取消委托还没执行完
		/*String json = this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" +
				"|datas.entrustTrades[0].entrustId!=null" , isBuy, "LTC", "CNY", "1", "2", "1", "1");*/
		String entrustId = getStringFromJson(json, "datas.entrustId");
		
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size>=1", "ETH", "BTC", entrustId);	//已成交
		
		//entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETC", "BTC", entrustId);	//其它盘不存在此ID
	}
	
	/*
	 * ETH_BTC
	 */
	@Test
	public void testEntrustDetails_ETH_BTC_NotExist_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETH_BTC_cancelNotMe_V1_1");
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETH", "BTC", "123123123123");	
	}
	
	/*
	 * ETH_BTC
	 */
	@Test
	public void testEntrustDetails_ETH_BTC_WrongInput_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETH_BTC_cancelNotMe_V1_1");
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETH", "BTC", "qweqweqweqwe");	
		/*
		 * TODO [bug] 内部异常
		 *	【结果】{委托取消区间订单失败-区间价格可能颠倒导致错误:java.lang.NumberFormatException: For input string: "qweqweqweqwe"}
		 *	【预期】委托id格式不正确 ，或返回空结果
		 *	【注】不要简单返回内部错误
		 */
	}
	
	/*
	 * ETH_BTC
	 *  获取不属于自己的交易明细
	 * 【预期】返回空结果
	 */
	@Test
	public void testEntrustDetails_ETH_BTC_NotMe_V1_1() throws Exception {
		printTitle("testEntrustDetails_ETH_BTC_NotMe_V1_1");
		
		//另一帐号登录，提交委托
		this.userId = "14";
		this.userName = "13760790454";
		this.password = "feng412708";
		doLoginV1_1("resMsg.message*=成功", userName, password, "", "", countryCode);
		
		String unitPrice = "0.60"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "1";
		String json = this.doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETH", "BTC", "0", unitPrice, number,userId,userName,password);
		
		Thread.sleep(1000);	
		/*String json = this.entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades[].size==1|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" +
				"|datas.entrustTrades[0].entrustId!=null" , isBuy, "LTC", "CNY", "1", "2", "1", "1",userId,userName,password);*/
		String entrustId = getStringFromJson(json, "datas.entrustId");
		
		//我的帐号登录，获取交易明细
		this.userId = "11";
		this.userName = "13760790453";
		this.password = "feng412708";
		doLoginV1_1("resMsg.message*=成功", userName, password, "", "", countryCode);
		
		entrustDetailsV1_1("resMsg.message*=成功|datas.entrustOrders[].size==0", "ETH", "BTC", entrustId);
	}
	
	
	
	
	/**
	 * 接口方法
	 * 
	 * <pre>
	 * 参数名		类型	必须	描述
	 * userId		String	是	用户id
	 * token		String	是	登录token
	 * currencyType	String	是	货币类型： BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic
	 * entrustId	Integer	是	委托id
	 * </pre>
	 */
	public String entrustDetailsV1_1(String test, String currencyType, String exchangeType, String entrustId) throws Exception {
		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(uri + "V1_1/entrustDetails");
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
	
	public String doEntrustV1_1(String test, String timeStamp, String type, String currencyType, String exchangeType,
			String isPlan, String unitPrice, String number,String userId, String userName, String password) throws Exception {
		
		DoEntrustTest doEntrustTest = new DoEntrustTest();
		doEntrustTest.setUp();
		doEntrustTest.setUserId(userId);
		doEntrustTest.setUserName(userName);
		doEntrustTest.setPassword(password);
		return doEntrustTest.doEntrustV1_1(test, timeStamp, type, currencyType, exchangeType, isPlan, unitPrice, number, "", "", "");
		
	}
	
	public String entrustRecordV1_1(String test, String type, String currencyType, String exchangeType, String dayIn3,
			String status, String pageIndex, String pageSize,String userId, String userName, String password) throws Exception {
	
		EntrustRecordTest entrustRecordTest = new EntrustRecordTest();
		entrustRecordTest.setUp();
		entrustRecordTest.setUserId(userId);
		entrustRecordTest.setUserName(userName);
		entrustRecordTest.setPassword(password);
		return entrustRecordTest.entrustRecordV1_1(test, type, currencyType, exchangeType, dayIn3, status, pageIndex, pageSize, "", "", "", "", "", "");

	}
	
}
