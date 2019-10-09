package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import com.alibaba.fastjson.JSONArray;

import apptest.BaseTest;

/**
 * 接口名称：4.21 获取首页显示行情模块 
 * 测试接口：getIndexMarketSet
 * 接口URL：http://api.bitglobal.com/api/m/V?_?/getIndexMarketSet
 * 
 * @author jiahua
 * 
 * @deprecated
 */
public class GetSetIndexMarketSetTest extends BaseTest {

	
	/*
	 * 设置22
	 */
	@Test
	public void testIndexMarketSetSuccess_V1_1() throws Exception {
		printTitle("testIndexMarketSetSuccess_V1_1");
		String[][] marketSets = new String[][]{new String[]{"1","abc"},new String[]{"2","qwe"}};	
		indexMarketSetV1_1("", marketSets);	//【fail】引号被转义
		
	}
	/*
	 * 获取
	 */
	@Test
	public void testGetIndexMarketSetSuccess_V1_1() throws Exception {
		printTitle("testGetIndexMarketSetSuccess_V1_1");
		getIndexMarketSetV1_1("resMsg.message*=成功");	//【fail】
	}

	
	/**
	 * <pre>
	 * 接口名称：4.21 获取首页显示行情模块 
	 * 测试接口：getIndexMarketSet
	 * 接口URL：http://api.bitglobal.com/api/m/V?_?/getIndexMarketSet
	 * 
	 * 参数名 		类型 		必须 	描述
	 * userId		String		是	用户id
	 * token		String		是	登录token
	 * </pre>
	 */
	public String getIndexMarketSetV1_1(String test)
			throws Exception {
		doLoginIfNot();

		PostMethod method = new PostMethod(uri + "V1_1/getIndexMarketSet");
		log.info(method.getURI());

		 addTokenUserId(method);

		addConfig(method);
		method.addParameter("sign", getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");

		return doCheck(test, method);
	}

	/**
	 * <pre>
	 * 接口名称：4.20 设置首页显示行情模块
	 * 测试接口：indexMarketSet
	 * 接口URL：http://api.bitglobal.com/api/m/V?_?/indexMarketSet
	 * 
	 * 参数名 		类型 		必须 	描述
	 * userId		String		是	用户id
	 * token		String		是	登录token
	 * marketSets	String[][]	是	行情模块数组 下标：0，行情标识（btc123 api的symbol值） 1，位置（Integer）
	 * </pre>
	 */
	public String indexMarketSetV1_1(String test, String[][] marketSets)
			throws Exception {
		doLoginIfNot();

		PostMethod method = new PostMethod(uri + "V1_1/indexMarketSet");
		log.info(method.getURI());

		 addTokenUserId(method);
		method.addParameter("marketSets", JSONArray.toJSONString(marketSets));

		addConfig(method);
		method.addParameter("sign", getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");

		return doCheck(test, method);
	}
	
}
