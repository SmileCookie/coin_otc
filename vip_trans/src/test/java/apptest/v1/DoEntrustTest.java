package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.world.util.sign.RSACoder;

import apptest.BaseTest;




/**
 * 接口名称：委托下单
 * 测试接口：doEntrust
 * 接口URL：http://trans.vip.com/api/m/V?_?/doEntrust
 * - **请求参数**

  | 参数名          | 类型     | 是否必须 | 描述                         |
  | :----------- | :----- | :--- | :------------------------- |
  | userId       | String | 是    | 用户id                       |
  | token        | String | 是    | 登录token                    |
  | timeStamp | String | 是    | 时间戳                       |
  | sign| String | 是    | 签名  RSA(user_id,token,timestamp)                       |
  | type         | String | 是    | 类型 <br/> 1：买入 0：卖出         |
  | currencyType | String | 是    | 货币类型：<br>BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic|
  | exchangeType | String | 是    | 兑换货币类型：<br>CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic |
  | isPlan | String | 是    | 类型 <br/> 1：计划/委托交易  0：立即交易 |
  | unitPrice    | String | 是    | 买入/卖出单价                    |
  | number       | String | 是    | 数量                         |
  | safePwd | String | 否    | 资金密码   （RSA加密）                    |
  | fingerprint | String | 否    |  指纹识别码（RSA加密）  当有指纹密码传入时，优先判断指纹密码，通过则不验证其它密码，不通过，再验证其它密码。|

- **返回结果**

| 参数名     | 类型     | 是否必须 | 示例      | 描述   |
| :------ | :----- | :--- | :------ | :--- |

 * @author jiahua
 */
public class DoEntrustTest extends BaseTest {


	/*
	 * ETC_BTC
	 */
	@Test
	public void testDoEntrust_ETC_BTC_sell_V1_1() throws Exception {
		printTitle("testDoEntrust_ETC_BTC_sell_V1_1");
		String unitPrice = "0.002"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "0";
		doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETC", "BTC", "0", unitPrice, number,safePwd, "", "");
		printSpendTime();
		entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" 
				+ "|datas.entrustTrades[0].number==[" + number + "]", isBuy, "ETC", "BTC", "1", "0", "1", "1");
		printSpendTime();
	}
	@Test
	public void testDoEntrust_ETC_BTC_buy_V1_1() throws Exception {
		printTitle("testDoEntrust_ETC_BTC_buy_V1_1");
		String unitPrice = "0.004"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "1";
		doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETC", "BTC", "0", unitPrice, number,safePwd, "", "");
		printSpendTime();
		entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" 
				+ "|datas.entrustTrades[0].number==[" + number + "]", isBuy, "ETC", "BTC", "1", "0", "1", "1");
		printSpendTime();
	}
	
	
	/*
	 * ETH_BTC
	 */
	@Test
	public void testDoEntrust_ETH_BTC_sell_V1_1() throws Exception {
		printTitle("testDoEntrust_ETH_BTC_sell_V1_1");
		String unitPrice = "0.02"+RandomStringUtils.randomNumeric(1);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "0";
		doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETH", "BTC", "0", unitPrice, number,safePwd, "", "");
		printSpendTime();
		entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" 
				+ "|datas.entrustTrades[0].number==[" + number + "]", isBuy, "ETH", "BTC", "1", "0", "1", "1");
		printSpendTime();
	}
	@Test
	public void testDoEntrust_ETH_BTC_buy_V1_1() throws Exception {
		printTitle("testDoEntrust_ETH_BTC_buy_V1_1");
		String unitPrice = "0.04"+RandomStringUtils.randomNumeric(2);
		String number = "0.00"+RandomStringUtils.random(1, "123456789");
		String isBuy = "1";
		doEntrustV1_1("resMsg.message*=成功", "", isBuy, "ETH", "BTC", "0", unitPrice, number,safePwd, "", "");
		printSpendTime();
		entrustRecordV1_1("resMsg.message*=成功|datas.entrustTrades!=null|datas.entrustTrades[0].unitPrice==[" + unitPrice + "]" 
				+ "|datas.entrustTrades[0].number==[" + number + "]", isBuy, "ETH", "BTC", "1", "0", "1", "1");
		printSpendTime();
	}
	
	
	
	
	/*
	 * 其它验证
	 */
	@Test
	public void testDoEntrust_ETC_sell_CheckSavePwd_V1_1() throws Exception {
		printTitle("testDoEntrust_ETC_sell_CheckSavePwd_V1_1");
		doLoginIfNot();
	
		try {
			printSubTitle("修改交易资金密码策略 >>>> 2，2 ");
			doChangeAuthV1_1("", "2", "2", safePwd, getMobileCode(9), getGoogleCode());
			doEntrustV1_1("resMsg.message*=成功", "", "0", "ETC", "BTC", "0", "0.0020", "0.001","", "", "");

			printSubTitle("修改交易资金密码策略 >>>> 2，3 ");
			doChangeAuthV1_1("", "2", "3", safePwd, getMobileCode(9), getGoogleCode());
			doEntrustV1_1("resMsg.code!=1000|resMsg.message*=资金*密码*误", "", "0", "ETC", "BTC", "0", "0.0020", "0.001","123123", "", "");
			doEntrustV1_1("resMsg.code!=1000|resMsg.message*=输入*资金*密码", "", "0", "ETC", "BTC", "0", "0.0020", "0.001","", "", "");
			doEntrustV1_1("resMsg.message*=成功", "", "0", "ETC", "BTC", "0", "0.0020", "0.001",safePwd, "", "");
			
			printSubTitle("修改交易资金密码策略 >>>> 2，1 "); 
			doChangeAuthV1_1("", "2", "1", safePwd, getMobileCode(9), getGoogleCode());
			doEntrustV1_1("resMsg.message*=成功", "", "0", "ETC", "BTC", "0", "0.0020", "0.001","", "", "");	//卖，20元0.001ETC
		} catch (Exception e) {
			log.error(e.toString(), e);
			fail(e.getMessage());
		} finally{
			printSubTitle("恢复交易资金密码策略 >>>> 2，1 "); 
			doChangeAuthV1_1("", "2", "1", safePwd, getMobileCode(9), getGoogleCode());
		}
	
		
	}	
	
	
	/**
	 * 接口方法
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
	 * safePwd		String	否	资金密码 （RSA加密）
	 * fingerprint	String	否	指纹识别码（指纹签名：RSA(MD5(appsecret+用户id+用户token+128位随机验证码+ appKey).toLowerCase()).toLowerCase()） 当有指纹密码传入时，优先判断指纹密码，通过则不验证其它密码，不通过，再验证其它密码。
	 * fingerCode	String	否	指纹校验码（128位随机验证码）
	 * </pre>
	 */
	public String doEntrustV1_1(String test, String timeStamp, String type, String currencyType, String exchangeType,
			String isPlan, String unitPrice, String number, String safePwd, String fingerprint, String fingerCode) throws Exception {
		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(uri + "V1_1/doEntrust");
		log.info(method.getURI());
		
		String _safePwd = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(safePwd.getBytes(), pubKey));

		addTokenUserId(method);

		method.addParameter("timeStamp", timeStamp);
		method.addParameter("type", type);
		method.addParameter("currencyType", currencyType);
		method.addParameter("exchangeType", exchangeType);
		method.addParameter("isPlan", isPlan);
		method.addParameter("unitPrice", unitPrice);
		method.addParameter("number", number);
		method.addParameter("safePwd", _safePwd);
		method.addParameter("fingerprint", fingerprint);
		method.addParameter("fingerCode", fingerCode);
	
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
	
}
