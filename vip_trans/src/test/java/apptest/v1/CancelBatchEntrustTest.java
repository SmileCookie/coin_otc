package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;

import apptest.BaseTest;




/**
 * 接口名称： 5.3.10 批量取消交易
 * 测试接口：cancelBatchEntrust
 * 接口URL：http://trans.bitglobal.com/api/m/V?_?/cancelBatchEntrust
 * - **请求参数**
	
	  | 参数名         | 类型     | 是否必须 | 描述      |
	  | :---------- | :----- | :--- | :------ |
	  | userId      | String | 是    | 用户id    |
	  | token       | String | 是    | 登录token |
	  | type | String | 是    | 取消类型  0 卖单  1 买单  -1 全部     |
	  | currencyType| String | 是    | 货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic     |
	  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |
	
	- **返回结果**
	
	| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
	| :------ | :----- | :--- | :------ | :--- |
 * @author jiahua
 */
public class CancelBatchEntrustTest extends BaseTest {


	/*
	 * ETC_BTC
	 */
	@Test
	public void testCancelBatchEntrust_ETC_BTC_Success_V1_1() throws Exception {
		printTitle("testCancelBatchEntrust_ETC_BTC_Success_V1_1");
		String isBuy = "0";
		doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETC", "BTC", "0", "0.6000", "0.001");
		cancelBatchEntrustV1_1("resMsg.message*=成功", isBuy, "ETC", "BTC");
		printSpendTime();

		Thread.sleep(100);	//取消委托后立即查记录，有可能取消委托还没执行完
		String json = entrustRecordV1_1("resMsg.message*=成功", isBuy , "ETC", "BTC", "1", "1", "1", "1");	
		JSONArray records = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("批量取消后，第一条记录状态应该为【已取消】状态", records.size()==1);	

		json = entrustRecordV1_1("resMsg.message*=成功", isBuy , "ETC", "BTC", "1", "3", "1", "1");
		records = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("批量取消后，交易中状态的记录数应该为0", records.size()==0);	
		
	}
	
	/*
	 * ETH_BTC
	 */
	@Test
	public void testCancelBatchEntrust_ETH_BTC_Success_V1_1() throws Exception {
		printTitle("testCancelBatchEntrust_ETH_BTC_Success_V1_1");
		String isBuy = "0";
		doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETH", "BTC", "0", "0.6000", "0.001");
		cancelBatchEntrustV1_1("resMsg.message*=成功", isBuy, "ETH", "BTC");
		printSpendTime();

		Thread.sleep(100);	//取消委托后立即查记录，有可能取消委托还没执行完
		String json = entrustRecordV1_1("resMsg.message*=成功", isBuy , "ETH", "BTC", "1", "1", "1", "1");	
		JSONArray records = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("批量取消后，第一条记录状态应该为【已取消】状态", records.size()==1);	
		
		json = entrustRecordV1_1("resMsg.message*=成功", isBuy , "ETH", "BTC", "1", "3", "1", "1");
		records = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("批量取消后，交易中状态的记录数应该为0", records.size()==0);	
		
	}
	
	
	/**
	 * 接口方法
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * type		String	是	取消类型 0 卖单 1 买单 -1 全部
	 * currencyType	String	是	货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic
	 * </pre>
	 */
	public String cancelBatchEntrustV1_1(String test, String type, String currencyType, String exchangeType) throws Exception {
		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(uri + "V1_1/cancelBatchEntrust");
		log.info(method.getURI());
		
		addTokenUserId(method);

		method.addParameter("type", type);
		method.addParameter("currencyType", currencyType);
		method.addParameter("exchangeType", exchangeType);
	
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
	/**
	 * entrustRecordV1_1接口适配器
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * type			String	是	类型 0：卖出 1：买入 -1：不限制
	 * currencyType	String	是	货币类型： BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic
	 * dayIn3		String	是	3天内数据 0：否 1：是 默认1
	 * status		String	是	状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）-1计划中
	 * pageIndex	String	是	页码 从1开始
	 * pageSize		String	是	每页显示数量 最大200
	 * </pre>
	 */
	public String entrustRecordV1_1(String test, String type, String currencyType, String exchangeType, String dayIn3,
			String status, String pageIndex, String pageSize) throws Exception {
	
		EntrustRecordTest entrustRecordTest = new EntrustRecordTest();
		entrustRecordTest.setUp();
		return entrustRecordTest.entrustRecordV1_1(test, type, currencyType, exchangeType, dayIn3, status, pageIndex,
				pageSize, "", "", "", "", "", "");

	}
	
	/**
	 * doEntrust接口适配器
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * timeStamp	String	是	时间戳
	 * type			String	是	类型  1：买入 0：卖出
	 * currencyType	String	是	货币类型：BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic
	 * isPlan		String	是	类型  1：计划/委托交易 0：立即交易
	 * unitPrice	String	是	买入/卖出单价
	 * number		String	是	数量
	 * </pre>
	 */
	public String doEntrustV1_1(String test, String timeStamp, String type, String currencyType, String exchangeType,
			String isPlan, String unitPrice, String number) throws Exception {
		
		DoEntrustTest doEntrustTest = new DoEntrustTest();
		doEntrustTest.setUp();
		return doEntrustTest.doEntrustV1_1(test, timeStamp, type, currencyType, exchangeType, isPlan, unitPrice,
				number, "", "", "");
		
	}
	
}
