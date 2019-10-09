package apptest.v1;

import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;

import apptest.BaseTest;






/**
 * 接口名称：4.61 获取交易货币配置
 * 测试接口：getCurrencySet
 * 接口URL：http://trans.bitglobal.comapi/m/V?_?/getCurrencySet
 * 
 * @author jiahua
 */
public class GetCurrencySetTest extends BaseTest {

	
	/*
	 * 非当前版本
	 * {
    "resMsg": {
        "code": 1000,
        "message": "操作成功",
        "method": "getCurrencySet"
    },
    "datas": {
        "currencySets": [
            {
                "marketDepth": [],
                "englishName": "Ethereum",
                "marketLength": [
                    {
                        "optional": [
                            5,
                            10,
                            20,
                            50
                        ],
                        "currency": "BTC"
                    }
                ],
                "symbol": "E",
                "name": "以太币",
                "coinUrl": "http://ts.vip.com/statics/img/v2/mobile/ico/ico_eth_type_sm@3x.png",
                "prizeRange": "0.05",
                "financeCoinUrl": "http://ts.vip.com/statics/img/v2/mobile/ico/ico_eth_finance.png",
                "currency": "ETH"
            },
            {
                "marketDepth": [],
                "englishName": "Ethereum Classic",
                "marketLength": [
                    {
                        "optional": [
                            5,
                            10,
                            20,
                            50
                        ],
                        "currency": "BTC"
                    }
                ],
                "symbol": "E",
                "name": "经典以太",
                "coinUrl": "http://ts.vip.com/statics/img/v2/mobile/ico/ico_etc_type_sm@3x.png",
                "prizeRange": "0.05",
                "financeCoinUrl": "http://ts.vip.com/statics/img/v2/mobile/ico/ico_etc_finance.png",
                "currency": "ETC"
            }
        ],
        "version": 1
    }
}
	 */
	@Test
	public void testGetCurrencySet_v1_V1_1() throws Exception {
		printTitle("testGetCurrencySet_v1_V1_1");
		//检验：btc深度列表：{0.01, 0.1, 1}, LTC:{0.01, 0.1, 0.3, 0.5}, ETH:{0.01, 0.1, 0.3, 0.5}, ETC:{0.01, 0.1, 0.3}
		this.getCurrencySetV1_1("resMsg.message*=成功|datas.currencySets[].size>0", "1");	
	}
	
	/*
	 * 当前版本，不返回数据
	 */
	@Test
	public void testGetCurrencySet_v2_V1_1() throws Exception {
		printTitle("testGetCurrencySet_v2_V1_1");
		this.getCurrencySetV1_1("resMsg.message*=成功|datas.currencySets[].size>0", "2");	
	}
	
	
	
	
	
	/**
	 * 接口方法
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * version	Integer	是	版本号
	 * </pre>
	 */
	public String getCurrencySetV1_1(String test, String version) throws Exception {
		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(uri + "V1_1/getCurrencySet");
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
