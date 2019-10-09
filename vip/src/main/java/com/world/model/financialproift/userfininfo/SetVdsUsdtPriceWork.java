package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.cache.Cache;
import com.world.model.dao.task.Worker;

import me.chanjar.weixin.common.util.StringUtils;

public class SetVdsUsdtPriceWork extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*此轮定时任务结束标识*/
    private static boolean workFlag = true;
	
	public SetVdsUsdtPriceWork(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		if (workFlag) {
			/*任务是否可执行*/
	      	workFlag = false;
			try {
				Map<String, Object> objectMap = new HashMap<String, Object>();
				/*设置VDSUSDT价格*/
				String vdsUsdt = Cache.Get("vds_usdt_l_price");
				BigDecimal bdVdsUsdt = BigDecimal.ZERO;
				if (StringUtils.isEmpty(vdsUsdt)) {
					log.info("理财报警SETVUPERROR:Cache获取vdsUsdt价格异常 = " + vdsUsdt);
					return;
				} else {
					try {
						bdVdsUsdt = new BigDecimal(vdsUsdt);
					} catch (Exception e) {
						log.info("理财报警SETVUPERROR:Cache获取vdsUsdt价格异常 = " + vdsUsdt);
						return;
					}
					if (bdVdsUsdt.compareTo(BigDecimal.ZERO) <= 0) {
						log.info("理财报警SETVUPERROR:Cache获取vdsUsdt价格异常小于0 = " + vdsUsdt);
						return;
					}
				}
				objectMap.put("vdsUsdt", vdsUsdt);
				log.info("objectMap = " + objectMap);
				String urlFinancial = ApiConfig.getValue("urlfinancial.url");
				urlFinancial += "/vdsapollo/op/setVdsUsdtPrice";
				log.info("urlFinancial = " + urlFinancial);
				String resultInterface = "";
				/*接口返回码和返回消息*/
				String resultInterfaceCode = "";
//				String resultInterfaceMsg = "";
				resultInterface = doPostData(urlFinancial, objectMap);
				JSONObject jsonResultInterface = com.alibaba.fastjson.JSONObject.parseObject(resultInterface);
				log.info("jsonResultInterface = " + jsonResultInterface);
				/*返回标志*/
				boolean setFlag = true;
				if (null != jsonResultInterface) {
					if (null != jsonResultInterface.getString("code")) {
						resultInterfaceCode = jsonResultInterface.getString("code");
					}
//					if(null != jsonResultInterface.getString("message")) {
//						resultInterfaceMsg = jsonResultInterface.getString("message");
//					}
					log.info("resultInterfaceCode = " + resultInterfaceCode);
					if (!"200".equals(resultInterfaceCode)) {
						setFlag = false;
					}
				} else {
					setFlag = false;
				}
				if (!setFlag) {
					log.info("理财报警INTERFACE:设置VDSUSDT价格失败 = " + vdsUsdt);
				}
			} catch (Exception e) {
				log.info("理财报警INTERFACE:SetVdsUsdtPriceWork", e);
			} finally {
				workFlag = true;
			}
		}
	}
	
	public static void main (String[] args) {
		SetVdsUsdtPriceWork setVdsUsdtPriceWork = new SetVdsUsdtPriceWork("", "");
		setVdsUsdtPriceWork.run();
	}
	
	public String doPostData(String url, Map<String,Object> objectMap) throws Exception {
		String result = "";
		try {
			String dataJson = com.alibaba.fastjson.JSON.toJSONString(objectMap);
	        jodd.http.HttpRequest request = jodd.http.HttpRequest.post(url);
	        request.query("data",dataJson);
	        jodd.http.HttpResponse response = request.send();
	        result = response.bodyText();
		} catch (Exception e) {
			throw new Exception(e);
		}
		return result;
	}
}
