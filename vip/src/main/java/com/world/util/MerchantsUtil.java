package com.world.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import com.alibaba.fastjson.JSONObject;
import com.api.util.MapSort;
import com.world.config.json.JsonConfig;
import com.world.model.entity.pay.DownloadBean;
import com.world.util.request.HttpUtil;
import com.world.util.sign.EncryDigestUtil;
import com.world.util.sign.RSACoder;
import org.apache.log4j.Logger;

public class MerchantsUtil {
	protected static Logger log = Logger.getLogger(MerchantsUtil.class.getName());
	
	public static Integer MERCHANTS_KEY_NUMBER_ALERT = 0;
	public static boolean USE_MERCHANTS = false;
	public static String MERCHANTS_DOMAIN = null;
	public static String MERCHANTS_API_KEY = null;
	public static String MERCHANTS_API_SECRET = null;
	public static String MERCHANTS_DES_KEY = null;
	
	static{
		if(MERCHANTS_DOMAIN==null){
			JSONObject jsonConfig = (JSONObject)JsonConfig.getValue("bitbank");
			JSONObject jsonApiConfig = jsonConfig.getJSONObject("api");
			    
			MERCHANTS_DOMAIN = jsonApiConfig.getString("domain").trim();
			MERCHANTS_API_KEY = jsonApiConfig.getString("key").trim();
			MERCHANTS_API_SECRET = jsonApiConfig.getString("secret").trim();
			MERCHANTS_KEY_NUMBER_ALERT = jsonApiConfig.getInteger("alert");
			MERCHANTS_DES_KEY = jsonApiConfig.getString("sign");
			if(jsonApiConfig.getString("use")!=null && "true".equals(jsonApiConfig.getString("use")) ){ //是否使用商户版接口
				USE_MERCHANTS = true;
			}
		}
	}
	
	/**
	 * 测试在商户平台下线下rmb充值
	 * @param number 获取个数
	 * @return
	 */
	public static JSONObject offChargeRMB(double cashAmount, String fromAccount, String fromPayee, int isAliPay, int rechargeBankId, int userId, String userName, long customerOrderId){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("cashAmount", cashAmount+"");	//充值金额，包括小数
			params.put("fromAccount", fromAccount);		//卡号或支付宝账号
			params.put("fromPayee", fromPayee);			//充值人姓名
			params.put("isAlipay", isAliPay+"");		//是否支付宝，0否1是
			params.put("rechargeBankId", rechargeBankId+"");	//充值银行id
			params.put("sUserId", userId+"");		//充值银行id
			params.put("sUserName", userName);		//登录名
			params.put("customerOrderId", customerOrderId + "");		//自定义同步ID
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("offChargeRMB设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("offChargeRMB的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("offChargeRMB的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/offChargeRMB";
			String result = HttpUtil.doGet(url, params);
			log.info("offChargeRMB的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			
			//返回数据
//			json.put("realName", param("fromPayee")); //汇款人姓名
//			json.put("fromAccount", param("fromAccount")); //汇款人账号
//			json.put("money", decimalParam("cashAmount")); //汇款金额
//			json.put("receiveAccount", certify); //汇入银行账户
//			json.put("receiveBank", bankName); //汇入银行
//			json.put("receiver", ba.getStartMan()); //收款人
//			json.put("qrCode", ba.getQrCode()); //二维码
//			json.put("memo", rtn.substring(4, rtn.length())); //汇款时备注
//			json.put("transNo", rtn); //汇款流水号
			
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	/**
	 * 测试在商户平台下取消线下充值
	 * @param number 获取个数
	 * @return
	 */
	public static JSONObject offChargeRMBCancel(String tradeNo){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("tradeNo", tradeNo);
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("offChargeRMBCancel设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("offChargeRMBCancel的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("offChargeRMBCancel的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/offChargeRMBCancel";
			String result = HttpUtil.doGet(url, params);
			log.info("offChargeRMBCancel的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	/**
	 * 测试在商户平台下委托单
	 * @param number 获取个数
	 * @return
	 */
	public static JSONObject newExchangeTest(){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("amount", "0.01");
			params.put("completeAmount", "0.02");
			params.put("completeMoney", "0.03");
			params.put("entrustPrice", "0.04");
			params.put("marketPrice", "0.05");
			params.put("money", "0.06");
			params.put("realEntrustAmount", "0.07");
			params.put("symbol", "btc");
			params.put("type", "buy");
			params.put("website", "vip");
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("newExchange设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("newExchange的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("newExchange的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/newExchange";
			String result = HttpUtil.doGet(url, params);
			log.info("newExchange的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	/**
	 * 从商户平台处获取新的地址
	 * @param number 获取个数
	 * @return
	 */
	public static JSONObject getNewAddress(String currency, int number){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("currency", currency);
			params.put("number", number+"");
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("getNewAddress设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("getNewAddress的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("getNewAddress的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/getNewAddress";
			String result = HttpUtil.doGet(url, params);
			log.info("getNewAddress的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
//	/**
//	 * 通知商户平台，进行提现操作
//	 * @param download
//	 * @return
//	 */
//	public static JSONObject doBtcDownload(btcDownloadBean download){
//		String url = "";
//		try {
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("accesskey", MERCHANTS_API_KEY);
//			params.put("cashAmount", download.getAmount()+"");
//			params.put("payFee", "0.00000000");
//			params.put("receiveAddress", download.getToAddress());
//			params.put("remark", download.getRemark());
//			
////			params = MapSort.sortMapByKey(params);
////			for (String key : params.keySet()) {  
////				log.info("doBtcDownload设置参数名：" + key + "\t内容：" + params.get(key));
////			}
//			
//			String info = toStringMap(params);
//			log.info("doBtcDownload的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
//			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
//			String sign = EncryDigestUtil.hmacSign(info, screct);
//			log.info("doBtcDownload的sign:" + sign);
//			
//			params.put("sign", sign);
//			params.put("reqTime", System.currentTimeMillis()+"");
//			params.put("des_key", MERCHANTS_DES_KEY);
//			
//			url = MERCHANTS_DOMAIN+"/api/btcDownload";
//			String result = HttpUtil.doPost(url, params, 60000, 60000, false);
//			log.info("doBtcDownload的返回数据:" + result);
//			
//			JSONObject json = JSONObject.parseObject(result);
//			return json;
//		} catch (Exception e) {
//			log.info("url地址：" + url);
//			log.error(e.toString(), e);
//		}
//		return null;
//	}
	
	/**
	 * 从商户平台处，根据地址获取地址的余额
	 * @param number 获取个数
	 * @return
	 */
	public static JSONObject keyAmount(String btckey){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("key", btckey);
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("keyAmount设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("keyAmount的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("keyAmount的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/keyAmount";
			String result = HttpUtil.doGet(url, params);
			log.info("keyAmount的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	/**
	 * 从商户平台处，根据地址获取地址的余额
	 * @param number 获取个数
	 * @return
	 */
	public static JSONObject getMyAddressList(){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("getMyAddressList设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("getMyAddressList的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("getMyAddressList的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/getMyAddressList";
			String result = HttpUtil.doGet(url, params);
			log.info("getMyAddressList的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	/**
	 * 从商户平台处，根据地址获取地址的余额
	 * @param number 获取个数
	 * @return
	 */
	public static JSONObject cnyChargeApp(String serilNumber, int userId){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("billNo", serilNumber+"_"+userId);
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("cnyChargeApp设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("cnyChargeApp的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("cnyChargeApp的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/cnyChargeApp";
			String result = HttpUtil.doGet(url, params);
			log.info("cnyChargeApp的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	public static String toStringMap(Map m){
		//按map键首字母顺序进行排序
		m = MapSort.sortMapByKey(m);
		
		StringBuilder sbl = new StringBuilder();
		for(Iterator<Entry> i = m.entrySet().iterator(); i.hasNext();){
			Entry e = i.next();
			Object o = e.getValue();
			String v = "";
			if(o == null){
				v = "";
			}else if(o instanceof String[]) {
				String[] s = (String[]) o;
				if(s.length > 0){
					v = s[0];
				}
			}else{
				v=o.toString();
			}
			if(!e.getKey().equals("sign") && !e.getKey().equals("reqTime") && !e.getKey().equals("tx")){
//				try {
//					sbl.append("&").append(e.getKey()).append("=").append(URLEncoder.encode(v, "utf-8"));
//				} catch (UnsupportedEncodingException e1) {
//					log.error(e1.toString(), e1);
					sbl.append("&").append(e.getKey()).append("=").append(v);
//				}
			}
		}
		String s = sbl.toString();
		if(s.length()>0){
			return s.substring(1);
		}
		return "";
	}
	
	/**
	 * 通知商户平台，进行提现操作
	 * @param download
	 * @return
	 */
	public static JSONObject doBtcDownload(DownloadBean download, BigDecimal amount, BigDecimal fees, String currency){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("cashAmount", amount+"");
			params.put("currency", currency);
			params.put("customerOrderId", download.getId()+"");
			params.put("payFee", fees + "");
			params.put("receiveAddress", download.getToAddress());
			String remark = download.getRemark();
			if (null == remark || "".equals(remark)) {
				remark = " ";
			}
			params.put("remark", download.getRemark().replaceAll("，（<font color=\"red\">提币地址是新地址</font>）", ""));
			String userName = download.getUserName();
			if (null == userName || "".equals(userName)) {
				userName = " ";
			}
			params.put("sUserName", userName);
			params.put("itransfer", "false");
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("doBtcDownload设置参数名：" + key + "\t内容：" + params.get(key));
			}
			
			String info = toStringMap(params);
			log.info("doBtcDownload的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("doBtcDownload的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/btcDownload";
			String result = HttpUtil.doPost(url, params, 10000, 10000, false);
			log.info("doBtcDownload的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	public static void main(String[] args){
		log.info("启动main方法");
		
		try {
			ResourceBundle rb = ResourceBundle.getBundle("main");
			String MERCHANTS_API_SECRET = rb.getString("MERCHANTS_API_SECRET").trim();
			String MERCHANTS_RSA_PUBLIC_KEY = rb.getString("MERCHANTS_RSA_PUBLIC_KEY").trim();
			
			String rsaSecrect = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(MERCHANTS_API_SECRET.getBytes(), MERCHANTS_RSA_PUBLIC_KEY));
			log.info("rsaSecrect:" + rsaSecrect);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		
	}
	
	/**
	 * 获取商户版指定币种的交易明细
	 * @param currency 币种
	 * @param startTime 搜索开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime 搜索结束时间  yyyy-MM-dd HH:mm:ss
	 * @param isIn 0提现1充值2全部
	 * @param tradeType 8在线充值 1线下汇款 0不区分
	 * @return
	 */
	public static JSONObject getBill(String currency, String isIn, String startTime, String endTime, String tradeType){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("currency", currency);
			params.put("startTime", startTime);
			params.put("endTime", endTime);
			params.put("isIn", isIn);
			params.put("tradeType", tradeType);
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("getBill设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("getBill的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("getBill的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/getBill";
			String result = HttpUtil.doGet(url, params);
			log.info("getBill的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	/**
	 * 获取商户版指定币种的交易总额
	 * @param currency 币种
	 * @param startTime 搜索开始时间 yyyy-MM-dd HH:mm:ss
	 * @param endTime 搜索结束时间  yyyy-MM-dd HH:mm:ss
	 * @param isIn 0提现1充值
	 * @param tradeType 8在线充值 1线下汇款 0不区分
	 * @return
	 */
	public static JSONObject getBillTotal(String currency, String isIn, String startTime, String endTime, String tradeType){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("currency", currency.toLowerCase());
			params.put("startTime", startTime);
			params.put("endTime", endTime);
			params.put("isIn", isIn);
			params.put("tradeType", tradeType);
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("getBillTotal设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("getBillTotal的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("getBillTotal的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/getBillTotal";
			String result = HttpUtil.doGet(url, params);
			log.info("getBillTotal的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}
	
	/**
	 * 获取充值确认次数
	 * @param currency 币种,btc/ltc/eth
	 * @param addHash 交易hash
	 * @param btcTo 充值地址
	 * @return
	 */
	public static JSONObject getConfirmTimes(String currency, String addHash, String btcTo){
		String url = "";
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accesskey", MERCHANTS_API_KEY);
			params.put("addHash", addHash);
			params.put("btcTo", btcTo);
			params.put("currency", currency);
			
			params = MapSort.sortMapByKey(params);
			for (String key : params.keySet()) {  
				log.info("getConfirmTimes设置参数名：" + key + "\t内容：" + params.get(key));
			}
					
			String info = toStringMap(params);
			log.info("getConfirmTimes的加密前字符串:" + info + "\tsecret:"+MERCHANTS_API_SECRET);
			String screct = EncryDigestUtil.digest(MERCHANTS_API_SECRET);
			String sign = EncryDigestUtil.hmacSign(info, screct);
			log.info("getConfirmTimes的sign:" + sign);
			
			params.put("sign", sign);
			params.put("reqTime", System.currentTimeMillis()+"");
			params.put("des_key", MERCHANTS_DES_KEY);
			
			url = MERCHANTS_DOMAIN+"/api/getConfirmTimes";
			String result = HttpUtil.doGet(url, params);
			log.info("getConfirmTimes的返回数据:" + result);
			
			JSONObject json = JSONObject.parseObject(result);
			return json;
		} catch (Exception e) {
			log.error("url地址：" + url, e);
		}
		return null;
	}

}
