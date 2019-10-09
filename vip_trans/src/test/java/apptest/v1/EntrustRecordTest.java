package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;

import apptest.BaseTest;





/**
 * 接口名称：当前委托
 * 测试接口：entrustRecord
 * 接口URL：http://trans.vip.com/api/m/V?_?/entrustRecord
 * - **请求参数**

  | 参数名        | 类型     | 是否必须 | 描述                                       |
  | :--------- | :----- | :--- | :--------------------------------------- |
  | userId     | String | 是    | 用户id                                     |
  | token      | String | 是    | 登录token                                  |
  | type       | String | 否    | 类型<br/>0：卖出 1：买入  -1：不限制          默认 -1       |
  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic |
  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic|
  | dayIn3     | String | 否    |    3天内数据  0：否    1：是  默认1    |
  | timeFrom   | String | 否    | 委托时间-开始时间 时间戳                            |
  | timeTo     | String | 否    | 委托时间-结束时间 时间戳                            |
  | numberFrom | String | 否    | 委托数量-最低                                  |
  | numberTo   | String | 否    | 委托数量-最高                                  |
  | priceFrom  | String | 否    | 委托单价-最低                                  |
  | priceTo    | String | 否    | 委托单价-最高                                  |
  | status     | String | 否    | 状态<br/>0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）-1计划中 默认：0 |
  | pageIndex  | String | 否   | 页码 从1开始      默认：1                             |
  | pageSize   | String | 否   | 每页显示数量 默认10， 最大200                             |

- **返回结果**

| 参数名           | 类型             | 是否必须 | 示例      | 描述           |
| :------------ | :------------- | :--- | :------ | :----------- |
| entrustTrades | EntrustTrade[] | 是    |         | 委托交易数组       |
| pageIndex     | String         | 是    |         | 页码 从1开始      |
| pageSize      | String         | 是    |         | 每页显示数量 最大200 |
| totalPage    | String         | 是    |         | 总页数 |
 * @author jiahua
 */
public class EntrustRecordTest extends BaseTest {


	/*
	 * ETC_BTC
	 */
	@Test
	public void testEntrustRecord_ETC_BTC_sell_V1_1() throws Exception {
		printTitle("testEntrustRecord_ETC_BTC_sell_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "0", "ETC", "BTC", "0", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()>0 && jsonArray.size()<=5);
		printSpendTime();
	}

	@Test
	public void testEntrustRecord_ETC_BTC_buy_V1_1() throws Exception {
		printTitle("testEntrustRecord_BTC_buy_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "1", "ETC", "BTC", "0", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()>0 && jsonArray.size()<=5);
		printSpendTime();
	}
	
	@Test
	public void testEntrustRecord_ETC_BTC_sell_3day_V1_1() throws Exception {
		printTitle("testEntrustRecord_ETC_BTC_sell_3day_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "0", "ETC", "BTC", "1", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()<=5);
		printSubTitle("数量：" + jsonArray.size());
		printSpendTime();
	}
	
	@Test
	public void testEntrustRecord_ETC_BTC_buy_3day_V1_1() throws Exception {
		printTitle("testEntrustRecord_ETC_BTC_buy_3day_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "1", "ETC", "BTC", "1", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()<=5);
		printSubTitle("数量：" + jsonArray.size());
		printSpendTime();
	}
	
	
	
	
	/*
	 * ETH_BTC
	 */
	@Test(timeout=10000)
	public void testEntrustRecord_ETH_BTC_sell_V1_1() throws Exception {
		printTitle("testEntrustRecord_ETH_BTC_sell_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "0", "ETH", "BTC", "0", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()>0 && jsonArray.size()<=5);
		printSpendTime();
	}
	
	@Test(timeout=10000)
	public void testEntrustRecord_ETH_BTC_buy_V1_1() throws Exception {
		printTitle("testEntrustRecord_ETH_BTC_buy_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "1", "ETH", "BTC", "0", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()>0 && jsonArray.size()<=5);
		printSpendTime();
	}
	@Test
	public void testEntrustRecord_ETH_BTC_sell_3day_V1_1() throws Exception {
		printTitle("testEntrustRecord_ETH_BTC_sell_3day_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "0", "ETH", "BTC", "1", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()<=5);
		printSubTitle("数量：" + jsonArray.size());
		printSpendTime();
	}
	
	@Test
	public void testEntrustRecord_ETH_BTC_buy_3day_V1_1() throws Exception {
		printTitle("testEntrustRecord_ETH_BTC_buy_3day_V1_1");
		String json = entrustRecordV1_1("resMsg.message*=成功", "1", "ETH", "BTC", "1", "0", "1", "5", "", "", "", "", "", "");
		JSONArray jsonArray = getArrayFromJson(json, "datas.entrustTrades");
		assertTrue("数据返回有误，数量：" + jsonArray.size(), jsonArray.size()<=5);
		printSubTitle("数量：" + jsonArray.size());
		printSpendTime();
	}
	
	
	
	/**
	 * 接口方法
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
	 * 
	 * timeFrom		String	否	委托时间-开始时间 时间戳
	 * timeTo		String	否	委托时间-结束时间 时间戳
	 * numberFrom	String	否	委托数量-最低
	 * numberTo		String	否	委托数量-最高
	 * priceFrom	String	否	委托单价-最低
	 * priceTo		String	否	委托单价-最高
	 * </pre>
	 */
	public String entrustRecordV1_1(
			String test, String type, String currencyType, String exchangeType, String dayIn3,String status, String pageIndex, String pageSize, 
			String timeFrom, String timeTo, String numberFrom,String numberTo, String priceFrom, String priceTo) throws Exception {

		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(uri + "V1_1/entrustRecord");
		log.info(method.getURI());
		
		addTokenUserId(method);
		method.addParameter("type",type);		
		method.addParameter("currencyType",currencyType);
		method.addParameter("exchangeType",exchangeType);
		method.addParameter("dayIn3",dayIn3);		
		method.addParameter("timeFrom",timeFrom);	
		method.addParameter("timeTo",timeTo);		
		method.addParameter("numberFrom",numberFrom);	
		method.addParameter("numberTo",numberTo);	
		method.addParameter("priceFrom",priceFrom);	
		method.addParameter("priceTo",priceTo);		
		method.addParameter("status",status);		
		method.addParameter("pageIndex",pageIndex);	
		method.addParameter("pageSize",pageSize);	
		
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
	
}
