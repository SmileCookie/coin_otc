package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import apptest.BaseTest;





/**
 * 接口名称：4.85 获取各个平台配置信息-首页行情获取(暂无配置信息)
 * 测试接口：getPlatformSet
 * 接口URL：http://trans.vip.com/api/m/V?_?/getPlatformSet
 * 
 * @author jiahua
 */
public class GetPlatformSetTest extends BaseTest {

	
	
	/*
	 * 当前版本，不返回数据
	 */
	@Test
	public void testGetPlatformSet_version1_V1_1() throws Exception {
		printTitle("testGetPlatformSet_version1_V1_1");
		this.getPlatformSetV1_1("resMsg.message*=成功|datas.platformSets[].size==0", "1");
	}
	
	/*
	 * 非当前版本:获取新的数据
	 */
	@Test
	public void testGetPlatformSet_version0_V1_1() throws Exception {
		printTitle("testGetPlatformSet_version0_V1_1");
		this.getPlatformSetV1_1("resMsg.message*=成功|datas.platformSets[].size==0", "0");
	}

	
	
	
	
	
	/**
	 * 接口方法
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * version	Integer	是	版本号
	 * </pre>
	 */
	public String getPlatformSetV1_1(String test, String version) throws Exception {
		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(uri + "V1_1/getPlatformSet");
		log.info(method.getURI());
		
		addTokenUserId(method);

		method.addParameter("version", version);
	
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
	
	
}
