package com.world.controller.api.notice;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.config.json.JsonConfig;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.FreezDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.FreezType;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.entity.pay.KeyBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.enums.CoinDownloadStatus;
import com.world.model.financial.dao.FinanEntryDao;
import com.world.timer.DateUtilsEx;
import com.world.util.DigitalUtil;
import com.world.util.MerchantsUtil;
import com.world.util.date.TimeUtil;
import com.world.util.request.MapSort;
import com.world.util.sign.DesUtil;
import com.world.util.sign.EncryDigestUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class Index extends BaseAction {
	
	private CoinProps coint;

	private DetailsDao detailsDao = new DetailsDao();
	private FreezDao freezDao = new FreezDao();
	private PayUserDao payUserDao = new PayUserDao();
	private FundsDao fundDao = new FundsDao();
	
	
	@Page(Viewer = JSON)
	public void index() {
		try {
			JSONObject bitbankConfig = (JSONObject)JsonConfig.getValue("bitbank");
		    JSONObject bitbankApiConfig = bitbankConfig.getJSONObject("api");
			
			String apiSecrect = bitbankApiConfig.getString("secret");
			String ssign = bitbankApiConfig.getString("sign");
			
			String method = request.getParameter("method");
			
			if(!MerchantsUtil.USE_MERCHANTS){
				response.getWriter().print("USE_MERCHANTS not open");
				return;
			}
				
			if(method!=null  && "download".equals(method)){ //提币回调
				handleDownload(apiSecrect, ssign);
			} else if(method!=null  && "downloadSys".equals(method)){ //系统提币回调
				handleDownload(apiSecrect, ssign);
			} else if(method!=null  && "charge".equals(method)){ //1.充币回调，保存充值记录
				handleCharge(apiSecrect, ssign);
			} else if(method!=null  && "chargeConfirm".equals(method)){ //3. 充币确认成功回调
				handleChargeConfirm(apiSecrect, ssign);
			} else if(method!=null  && "checkCoinAddr".equals(method)){ //地址核对
				handleCheckCoinAddr(apiSecrect, ssign);
			} else if(method!=null  && "syncConfirmTimes".equals(method)){ //2.通知充值次数
				String currency = param("currency");
				log.info("通知" + currency + "充值次数" + param("addHash") + " " + param("confirmTimes"));
				String addHash = param("addHash");
				int confirmTimes = intParam("confirmTimes");
				
				CoinProps coint = DatabasesUtil.coinProps(currency);

				int res = 0;
				String sql = "select * from "+currency+"details where addHash=?";
				DetailsBean edBean = (DetailsBean) detailsDao.getT(sql, new Object[]{addHash}, DetailsBean.class);
				if (null != edBean) {
					res = Data.Update("update "+currency+"details set confirmTimes=? where detailsId=? and confirmTimes<=?", new Object[]{confirmTimes, edBean.getDetailsId(), confirmTimes});
				}
				if (confirmTimes < coint.getInConfirmTimes()) {
					response.getWriter().print("failure");
					return;
				}
				
				if (res > 0) {
					response.getWriter().print("success");
				} else {
					response.getWriter().print("failure");
				}
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			try {
				response.getWriter().print("error");
			} catch (IOException e1) {
				log.error("内部异常", e1);
			}
		}
	}
	
	private void handleDownload(String apiSecrect, String key) {
		try {
			String sign = request.getParameter("sign");
			log.info("download收到的sign:" + sign);

			String[] jiamiArr = new String[]{"accesskey", "amount", "btcDownloadId", "commandId", "currency",
					"confirm", "fees", "freezeId", "isDel", "managerName", "managerId", 
					"manageTime", "method", "orderNo", "partner_id", "payFee", "realFee", "remark", 
					"status", "submitTime", "toAddress", "userName"};
			Map<String,String> maps = new HashMap<String, String>();
			for(int i=0;i<jiamiArr.length;i++){
				log.info("download获取参数名：" + jiamiArr[i] + "\t内容：" + request.getParameter(jiamiArr[i]));
				maps.put( jiamiArr[i], request.getParameter(jiamiArr[i]) );
			}
			
			String params = toStringMap(maps);
			log.info("download获取的params：" + params);
			
			String desSecrect = DesUtil.encrypt(MerchantsUtil.MERCHANTS_API_SECRET, MerchantsUtil.MERCHANTS_DES_KEY);
			log.info("download的desSecrect:" + desSecrect);
			String screct = EncryDigestUtil.digest(desSecrect);
			String signMyself = EncryDigestUtil.hmacSign(params, screct);
			log.info("download提现操作，接收到的参数加密的sign:" + signMyself);
			Timestamp manageTime = new Timestamp(DateUtilsEx.parseDate(param("manageTime"), TimeUtil.getNow()).getTime());
			
			if(sign!=null && sign.equals(signMyself)){ //签名验证通过
				String orderNo = param("orderNo");
				String fees = param("fees");
				BigDecimal realFee = DigitalUtil.getBigDecimal(fees);
				int status = intParam("status");
//				
				int fromAddress = intParam("fromAddress");
				String currency = param("currency");
				String addHash = param("addHash");
				CoinProps coint = DatabasesUtil.coinProps(currency);
				
				DownloadBean bdlb = (DownloadBean)Data.GetOne("select * from "+currency+"Download where merchantOrderNo=?",
						new Object[] {orderNo}, DownloadBean.class);
				if(bdlb != null){
					List<OneSql> sqls= new ArrayList<OneSql>();
					String logInfo = "商户版"+currency.toUpperCase()+"打币成功";
					if (status == CoinDownloadStatus.SUCCESS.getKey()) { //提现成功
						if (bdlb.getStatus() == status) {
							response.getWriter().print("success");
							return;
						}
						//解冻并扣除的语句
						freezDao.setCoint(coint);
						FreezeBean fbean = new FreezeBean(bdlb.getUserId(), bdlb.getUserName(), "提现成功", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), 0, 0);
						freezDao.unFreezSqls(sqls, fbean, BillType.download, true);
						
						sqls.add(new OneSql("update "+currency+"Download set status=?,manageTime=?,addHash=?,fromAddress=?,realFee=?,fees=? where id= ? AND status = ?", 1, new Object[]{
								status, manageTime, addHash, fromAddress, realFee, realFee, bdlb.getId(), CoinDownloadStatus.COMMITED.getKey()
						}));//状态提现记录改为成功
					} else if (status== CoinDownloadStatus.FAIL.getKey() || status== CoinDownloadStatus.CANCEL.getKey()){//提现失败
						if (bdlb.getStatus() == status) {
							response.getWriter().print("success");
							return;
						}
						freezDao.setCoint(coint);
						FreezeBean fbean = new FreezeBean(bdlb.getUserId(), bdlb.getUserName(), "取消下载", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), 0, 0);
						freezDao.unFreezSqls(sqls, fbean, null, false);
						
						String updateSql = "update "+currency+"Download set status=?,manageTime=? where id=? and status=?";
						sqls.add(new OneSql(updateSql, 1, new Object[]{status, TimeUtil.getNow(), bdlb.getId(), CoinDownloadStatus.COMMITED.getKey()}));
						logInfo = "商户版"+currency.toUpperCase()+"打币失败";
					}

					if (Data.doTrans(sqls)) {
						String userName = bdlb.getUserName();
						UserCache.resetUserFundsFromDatabase(bdlb.getUserId());
						try {
							if(status == CoinDownloadStatus.SUCCESS.getKey()){
								sqls.clear();
								new FinanEntryDao().syncDownloadFinanAccount(sqls, coint, bdlb);
								Data.doTrans(sqls);
							}
						} catch (Exception e1) {
							log.error("财务录入失败", e1);
						}
						
						try {
							//插入一条管理员日志信息
							DailyType type = DailyType.btcDownload;
							new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, logInfo, userName, bdlb.getId(), "", bdlb.getAmount(), bdlb.getToAddress(), coint.getPropCnName()), "0", "", TimeUtil.getNow());
						} catch (Exception e) {
							log.error("添加日志失败", e);
						}
						response.getWriter().print("success");
						return;
					}
				}else{
					response.getWriter().print("failure");
					return;
				}
			}else{
				response.getWriter().print("wrong sign");
				return;
			}
			response.getWriter().print("failure");
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	/**
	 * 保存充值记录
	 * @param apiSecrect
	 * @param ssign
     */
	private void handleCharge(String apiSecrect, String ssign) {
		try {
			String sign = request.getParameter("sign");
			String[] jiamiArr = new String[]{"accesskey", "method", "parameters"};
			Map<String,String> maps = new HashMap<String, String>();
			for(int i=0;i<jiamiArr.length;i++){
				log.info("charge获取参数名：" + jiamiArr[i] + "\t内容：" + request.getParameter(jiamiArr[i]));
				maps.put( jiamiArr[i], request.getParameter(jiamiArr[i]) );
			}
			
			String params = toStringMap(maps);
			log.info("charge获取的params：" + params);
			
			String desSecrect = DesUtil.encrypt(MerchantsUtil.MERCHANTS_API_SECRET, MerchantsUtil.MERCHANTS_DES_KEY);
			log.info("charge的desSecrect:" + desSecrect);
			String screct = EncryDigestUtil.digest(desSecrect);
			String signMyself = EncryDigestUtil.hmacSign(params, screct);
			log.info("charge操作，接收到的参数加密的sign:" + signMyself);
			
			log.info("charge收到的sign:" + sign);
			
			boolean isSuc = false;
			int size = 0;
			String successIds = "";
			if(sign!=null && sign.equals(signMyself)){
				if (null != maps.get("parameters")) {
					JSONArray jsonArr = JSONArray.parseArray(maps.get("parameters"));
					size = jsonArr.size();
					if (null != jsonArr && jsonArr.size() > 0) {
						List<OneSql> sqls = new ArrayList<OneSql>();
						for (int i=0;i<jsonArr.size();i++) {
							try {
								JSONObject json = jsonArr.getJSONObject(i);
								String currency = json.getString("currency");
								Long syncId = json.getLong("id");
								String addhash = json.getString("addHash");
								String sql = "";
								
								if( "".equals(json.getString("btcTo") ) ){
									successIds = successIds + "," + json.getLongValue("id");
									continue;
								}
								
								CoinProps coint = DatabasesUtil.coinProps(currency);
								Long userId = 0L;
								String userName = "";
								
								KeyBean lkb = (KeyBean) Data.GetOne("select * from "+currency+"Key where keyPre=?",
										new Object[] {json.getString("btcTo")}, KeyBean.class);
								if(lkb != null){
									userId=lkb.getUserId();
									userName=lkb.getUserName();
								}
								
								DetailsBean detail = (DetailsBean) Data.GetOne("select * from "+currency+"details where addhash=?", new Object[] {addhash}, DetailsBean.class);
								if (null != detail) {
									successIds = successIds + "," + json.getLongValue("id");
									continue;
								}
								
								Timestamp sendimeTime = null;
								try {
									sendimeTime = json.getTimestamp("sendimeTime");
								} catch (Exception e1) {
									sendimeTime = new Timestamp( TimeUtil.getZero().getTime() );
								}
								java.sql.Date configTime = null;
								try {
									configTime = json.getSqlDate("configTime");
								} catch (Exception e1) {
									configTime = new java.sql.Date(TimeUtil.getZero().getTime());
								}
								
								BigDecimal balance = BigDecimal.ZERO;
								PayUserBean payUser = payUserDao.getById(Integer.parseInt(userId+""), coint.getFundsType());
								BigDecimal amount = json.getBigDecimal("number");
								if (payUser != null){
									balance = amount.add(payUser.getBalance().add(payUser.getFreez()));
								}
								
								log.info("插入details");
								sql = "insert into "+currency+"details (type,fromAddr,toAddr,addHash,amount,sendTime,configTime,banlance,entrustId,price,fees,wallet,confirmTimes,sucConfirm,status,userId, userName, remark, merchantsSyncId, opUnique) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
								sqls.add(new OneSql(sql, 1, new Object[]{json.getIntValue("isIn"),json.getString("btcFrom"),json.getString("btcTo"),
										json.getString("addHash"),amount,sendimeTime,configTime,
										balance,json.getLongValue("entrustId"),json.getBigDecimal("price"),json.getBigDecimal("fees"),
										json.getString("wallet"),json.getIntValue("confirmTimes"),json.getIntValue("sucConfirm"),0,
										userId, userName, json.getString("remark"), syncId, syncId}));
								
								isSuc = Data.doTrans(sqls);
								if(isSuc){
									successIds = successIds + "," + json.getLongValue("id");
								}
							} catch (Exception e) {
								log.error("内部异常", e);
							}
						}
					}
				}

				if(!"".equals(successIds)){
					successIds = successIds.substring(1, successIds.length());
				}
				log.info("成功执行充值操作的id:" + successIds);
				if (!"".equals(successIds)) { 
					response.getWriter().print("success:" + successIds);
					return;
				} else {
					response.getWriter().print("failure");
				}
				
			}else{
				response.getWriter().print("wrong sign");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	/**
	 * 充值确认的通知
	 * @param apiSecrect
	 * @param ssign
     */
	private void handleChargeConfirm(String apiSecrect, String ssign){
		try {
			String sign = request.getParameter("sign");
			String[] jiamiArr = new String[]{"accesskey", "method", "parameters"};
			Map<String,String> maps = new HashMap<String, String>();
			for(int i=0;i<jiamiArr.length;i++){
				log.info("chargeConfirm获取参数名：" + jiamiArr[i] + "\t内容：" + request.getParameter(jiamiArr[i]));
				maps.put( jiamiArr[i], request.getParameter(jiamiArr[i]) );
			}
			
			String params = toStringMap(maps);
			log.info("chargeConfirm获取的params：" + params);
			
			String desSecrect = DesUtil.encrypt(MerchantsUtil.MERCHANTS_API_SECRET, MerchantsUtil.MERCHANTS_DES_KEY);
			log.info("chargeConfirm的desSecrect:" + desSecrect);
			String screct = EncryDigestUtil.digest(desSecrect);
			String signMyself = EncryDigestUtil.hmacSign(params, screct);
			log.info("chargeConfirm操作，接收到的参数加密的sign:" + signMyself);
			
			log.info("chargeConfirm收到的sign:" + sign);
			
			boolean isSuc = false;
			int size = 0;
			String successIds = "";
			if(sign!=null && sign.equals(signMyself)){
				if (null != maps.get("parameters")) {
					JSONArray jsonArr = JSONArray.parseArray(maps.get("parameters"));
					size = jsonArr.size();

					if (null != jsonArr && jsonArr.size() > 0) {
						List<OneSql> sqls = new ArrayList<OneSql>();
						for (int i=0;i<jsonArr.size();i++) {
							try {
								JSONObject json = jsonArr.getJSONObject(i);
								Long syncId = json.getLong("id");
								String addHash = json.getString("addHash");
								if( "".equals( json.getString("btcTo") ) ){
									successIds = successIds + "," + json.getLongValue("id");
									continue;
								}
								
								String currency = json.getString("currency");
								Timestamp configTime = getTime(json, "configTime");
								int confirmTimes = json.getIntValue("confirmTimes");
								int isIn = json.getIntValue("isIn");
								CoinProps coint = DatabasesUtil.coinProps(currency);
								if (isIn == 93) { // 平台手动充值
									confirmTimes = coint.getInConfirmTimes();
								}
								
								KeyBean lkb = (KeyBean) Data.GetOne("select * from "+currency+"Key where keyPre=?",
										new Object[] {json.getString("btcTo")}, KeyBean.class);
								
								DetailsBean detail = (DetailsBean) Data.GetOne("select * from "+currency+"details where addHash=?",
										new Object[] {addHash}, DetailsBean.class);
								if (null == detail) {
									response.getWriter().print("failure");
									return;
								}
								if (null != detail && detail.getStatus() == 2) {
									successIds = successIds + "," + json.getLongValue("id");
									continue;
								}
								sqls = new ArrayList<OneSql>();
								if(json.getLong("status")==0){
									log.info("只update key");
								} else if(json.getLong("status")==1){
									int iResult = Data.Update("update "+currency+"details set status=? where detailsId=? and status=?",
											new Object[]{1, detail.getDetailsId(), 0});
									if (iResult > 0) {
										successIds = successIds + "," + json.getLongValue("id");
										continue;
									}
								} else{
									BigDecimal balance = BigDecimal.ZERO;
									PayUserBean payUser = payUserDao.getById(Integer.parseInt(detail.getUserId()), coint.getFundsType());
									BigDecimal amount = json.getBigDecimal("number");
									if (payUser != null){
										balance = amount.add(payUser.getBalance().add(payUser.getFreez()));
									}
									sqls.add(new OneSql("update "+currency+"Key set usedTimes=usedTimes+? where keyPre=?" , 1 , new Object[] {1, json.getString("btcTo")}));
									sqls.add(new OneSql("update "+currency+"details set status=?,confirmTimes=?,configTime=?,banlance=? where detailsId=? and status=?" , 1 , new Object[]{2 , confirmTimes, configTime , balance ,detail.getDetailsId() , 0}));
									sqls.addAll(fundDao.addMoney(amount, detail.getUserId(), detail.getUserName(), currency.toUpperCase()+BillType.recharge.getValue(), BillType.recharge.getKey(), coint.getFundsType(), BigDecimal.ZERO, "0", true));
									isSuc = Data.doTrans(sqls);
									if(isSuc){
										UserCache.resetUserFundsFromDatabase(detail.getUserId());
										
										DailyType type = DailyType.btcCharge;
										new MainDailyRecordDao().insertOneRecord(type, "用户"+detail.getUserName()+"成功充值"+amount+currency.toUpperCase()+"，充值编号："+detail.getDetailsId(), String.valueOf(0), "", TimeUtil.getNow(), Integer.parseInt(detail.getUserId()) , amount);
										log.info("充值成功：" + detail.getMerchantsSyncId());
										successIds = successIds + "," + json.getLongValue("id");
										
										try {
											sqls.clear();
											new FinanEntryDao().syncFinanAccount(sqls, coint, detail);
											Data.doTrans(sqls);
											log.info("充值成功，财务账户录入成功。");
										} catch (Exception e) {
											log.error("财务账户录入失败", e);
										}
									}else{
										log.info("充值失败：" + detail.getMerchantsSyncId());
									}
								}
							} catch (Exception e) {
								log.error("内部异常", e);
							}
						}
					}
				}
				if(!"".equals(successIds)){
					successIds = successIds.substring(1, successIds.length());
				}
				if (!"".equals(successIds)) { 
					log.info("成功执行充值操作的id:" + successIds);
					response.getWriter().print("success:" + successIds);
					return;
				} else {
					response.getWriter().print("failure");
					return;
				}
			}else{
				response.getWriter().print("wrong sign");
			}
		
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	

	private Timestamp getTime(JSONObject json, String key){
		Timestamp date = null;
		try {
			date = json.getTimestamp(key);
		} catch (Exception e1) {
			date = TimeUtil.getNow();
		}

		return date;
	}
	
	/**
	 * 获取地址检查
	 * @param apiSecrect
	 * @param ssign
     */
	private void handleCheckCoinAddr(String apiSecrect, String ssign){
		try {
			String[] jiamiArr = new String[]{"accesskey", "method", "orderNo", "parameters"};
			Map<String,String> maps = new HashMap<String, String>();
			for(int i=0;i<jiamiArr.length;i++){
				log.info("checkCoinAddr获取参数名：" + jiamiArr[i] + "\t内容：" + request.getParameter(jiamiArr[i]));
				maps.put( jiamiArr[i], request.getParameter(jiamiArr[i]) );
			}
			
			String params = toStringMap(maps);

			
			log.info("checkCoinAddr获取的params：" + params);
			
			String desSecrect = DesUtil.encrypt(MerchantsUtil.MERCHANTS_API_SECRET, MerchantsUtil.MERCHANTS_DES_KEY);
			log.info("checkCoinAddr的desSecrect:" + desSecrect);
			String screct = EncryDigestUtil.digest(desSecrect);
			String signMyself = EncryDigestUtil.hmacSign(params, screct);
			log.info("checkCoinAddr操作，接收到的参数加密的sign:" + signMyself);
			
			String sign = request.getParameter("sign");
			log.info("checkCoinAddr收到的sign:" + sign);
			
			if(sign!=null && sign.equals(signMyself)){
				String parameters = maps.get("parameters");
				String merchantOrderNo = param("orderNo");
				if (null != parameters) {
					JSONArray jarry = JSONArray.parseArray(parameters);
					String currency = "";
					if (null != jarry && jarry.size() > 0) {
						JSONObject json = jarry.getJSONObject(0);
						if (json.containsKey("currency")) {
							currency = json.getString("currency");
						}
					}
					if (null == currency || "".equals(currency)) {
						response.getWriter().print("failure");
						return;
					}
					KeyDao keyDao = new KeyDao();
					String tableName = currency + "Key";
					String sql = "select * from "+tableName+" where merchantOrderNo=?";
					List<KeyBean> ekList = keyDao.find(sql, new Object[]{merchantOrderNo}, KeyBean.class);
					Map<String, String> ekMap = new HashMap<String, String>();
					if (null != ekList && ekList.size() > 0) {
						for (KeyBean ekb : ekList) {
							ekMap.put(ekb.getKeyPre(), ekb.getWallet());
						}
					}
					
					if (null != jarry && jarry.size() > 0) {
						
						for (int i=0;i<jarry.size();i++) {
							JSONObject json = jarry.getJSONObject(i);
							if (json.containsKey("address") && json.containsKey("wallet")) {
								String address = json.getString("address");
								String wallet = json.getString("wallet");

								if (!ekMap.containsKey(address)) {
									String countSql = "select count(*) from " + tableName + " where keyPre=?";
									List<Long> one = (List<Long>)Data.GetOne(countSql, new Object[]{address});
									if(one !=null && one.size() > 0 && one.get(0) > 0){
										continue;
									}

									int iResult = Data.Update("insert into "+tableName+"(keyPre,wallet,createTime,merchantOrderNo) values (?,?,now(),?)", 
											new Object[]{address, wallet, merchantOrderNo});
									if (iResult < 0) {
										response.getWriter().print("failure");
										return;
									}
								}
							}
						}
						response.getWriter().print("success");
						return;
					}
				}
				response.getWriter().print("failure");
			}else{
				response.getWriter().print("wrong sign");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public String toStringMap(Map m){
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
			if(!e.getKey().equals("sign") && !e.getKey().equals("reqTime") && !e.getKey().equals("tx"))
//				try {
//					sbl.append("&").append(e.getKey()).append("=").append(URLEncoder.encode(v, "utf-8"));
//				} catch (UnsupportedEncodingException e1) {
//					log.error(e1.toString(), e1);
					sbl.append("&").append(e.getKey()).append("=").append(v);
//				}
		}
		String s = sbl.toString();
		if(s.length()>0){
			return s.substring(1);
		}
		return "";
	}
	
	/**
	 * 打币成功之后的实际余额
	 * @return
	 */
	public double getAfterBtcs(double realFee, double balance, double amount){
		if(realFee > 0){
			return balance - realFee - amount;
		}
		return balance;
	}

}
