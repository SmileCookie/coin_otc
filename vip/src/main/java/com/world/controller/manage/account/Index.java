package com.world.controller.manage.account;


import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.PayUserApiService;
import com.world.data.big.DownTable;
import com.world.data.big.MysqlDownTable;
import com.world.data.big.table.DownTableManager;
import com.world.data.mysql.Data;
import com.world.model.dao.pay.DownloadSummaryDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.bill.BillType;
import com.world.model.enums.CoinChargeStatus;
import com.world.web.Page;
import com.world.web.action.LoanUserAction;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class Index extends LoanUserAction {
	PayUserDao payDao = new PayUserDao();
    DownloadSummaryDao downloadSummaryDao = new DownloadSummaryDao();

	@Page
    public void index() {

        //TODO jsp页面跳转，之后下掉jsp
        try {
            response.sendRedirect(VIP_DOMAIN);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

	/**
	 * 配合前端改造
	 */
	@Page(Viewer = JSON)
	public void indexJson() {
		Map<String, Object> result = new HashMap<>();
		try {
		    initLoginUser();

			//封装提现额度
			//获取用户是否通过身份认证
			Map<String, Object> userDownloadLimit = downloadSummaryDao.getDownloadLimit(loginUser.getId());
			BigDecimal downloadLimit = (BigDecimal) userDownloadLimit.get("downloadLimit");
			int authResult = (int) userDownloadLimit.get("authResult");
			result.put("authResult", authResult);
			result.put("downloadLimit", downloadLimit);

			//获取今日已提现btc金额
			BigDecimal alreadyDownload = downloadSummaryDao.getTodayBtcAmount(loginUser.getId());
			BigDecimal available = downloadLimit.subtract(alreadyDownload);
			result.put("availableDownload", available.compareTo(BigDecimal.ZERO) > 0 ? available : BigDecimal.ZERO);
			json("success", true, com.alibaba.fastjson.JSONObject.toJSONString(result), true);
		}catch (Exception e){
			log.error("indexJson error！", e);
			json("success", false, com.alibaba.fastjson.JSONObject.toJSONString(result), true);
		}

	}

	private BigDecimal trimZeroAfterPoint(BigDecimal val){
		DecimalFormat df = new DecimalFormat("###.######");
		return new BigDecimal(df.format(val));
	}
	
//	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}	
	
	
	
//	@Page(Viewer = JSON)
//	public void billDetail() {
//		try {
//			initLoginUser();
//			coinProps();`
//			//查询条件
//			int pageIndex = intParam("pageIndex");
//			int pageSize = intParam("pageSize");
//			String type = param("type");//all/payin/payout/trade
//
//			BillDetailDao bwDao = new BillDetailDao();
//			BeanProxy bp = MysqlDownTable.getProxy("bill");
//
//			Query<BillDetails> query = bwDao.getQuery();
//			query.setSql("select id,userId,type,status,amount,sendTime,balance,fundsType,fees  from bill ");
//			query.setCls(BillDetails.class);
//
//
//			if ("payin".equals(type)) {
//				query.append(" and type = "+BillType.recharge.getKey());
//			} else if("payout".equals(type)) {
//				query.append(" and type = "+BillType.download.getKey());
//			}else if("trade".equals(type)){
//				query.append(" and type in( "+BillType.buy.getKey()+", "+BillType.sell.getKey()+", "+BillType.exchangeIn.getKey()+", "+BillType.exchangeOut.getKey()+")" );
//			}else{
//				query.append(" and type in( "+BillType.recharge.getKey()+","+BillType.download.getKey()+","+BillType.buy.getKey()+", "+BillType.sell.getKey()+", "+BillType.exchangeIn.getKey()+", "+BillType.exchangeOut.getKey()+")" );
//			}
//
//
//			query.append(" and userId = "+userId());
//			List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
//			int total = query.count();
//			List<BillDetails> weight = null;
//			if(total > 0){
//				query.append("order by id desc");
//				//分页查询
//				weight = bwDao.findPage(pageIndex, pageSize);
//				SysEnum sysEnum;
//				for(BillDetails bill:weight){
//					Map<String,Object> billMap = new HashMap<String,Object>();
//					billMap.put("id", bill.getId());
//					billMap.put("showType", bill.getShowType());//账单显示类型
//					billMap.put("sendTime", bill.getSendTime());
//					billMap.put("amount", bill.getAmount());
//					billMap.put("balance", bill.getBalance());
//					billMap.put("coinName", bill.getCoinName());
//					billMap.put("fees", bill.getFees());//手续费
//					billMap.put("bt", bill.getBt());//账单类型
//
//					sysEnum = (CoinChargeStatus) EnumUtils.getEnumByKey(bill.getStatus(), CoinChargeStatus.class);
//					if (sysEnum != null) {
//						billMap.put("status", sysEnum.getValue());
//					}
//
//					list.add(billMap);
//				}
//				weight.clear();
//			}
//
//			Map<String, Object> page = new HashMap<String, Object>();
//			page.put("pageIndex", pageIndex);
//			page.put("totalCount", total);
//			page.put("list", list);
//			json("", true, JSONObject.fromObject(page).toString());
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//	}

	@Page(Viewer = JSON)
	public void billDetail() {
		try {
			initLoginUser();
			coinProps();
			//查询条件
//			int pageIndex = intParam("pageIndex");
//			int pageSize = intParam("pageSize");
			String type = param("type");

			StringBuilder sb = new StringBuilder();
			sb.append("select id,userId,type,status,amount,sendTime,balance,fundsType,fees  from bill ");
			sb.append(" where type in( ");

			if ("payin".equals(type)) {
				sb.append(BillType.recharge.getKey());
			} else if ("payout".equals(type)) {
				sb.append(BillType.download.getKey());
			} else if ("inAndOut".equals(type)) {
				sb.append(BillType.recharge.getKey())
						.append(",").append(BillType.download.getKey());
			}else if ("trade".equals(type)) {
				sb.append(BillType.buy.getKey()).append(",").append(BillType.sell.getKey());
			} else {
				sb.append(BillType.recharge.getKey()).append(",")
						.append(BillType.download.getKey()).append(",")
						.append(BillType.buy.getKey()).append(",")
						.append(BillType.sell.getKey()).append(",")
						.append(BillType.exchangeIn.getKey()).append(",")
						.append(BillType.exchangeOut.getKey());
			}

			sb.append(") ");
			sb.append(" and userId = ").append(userId());
			sb.append(" and status = ").append(2);
			sb.append(" order by id desc limit 0,10 ");

			StringBuilder union = new StringBuilder();
			MysqlDownTable.DownSql downSql = DownTableManager.getProxyDownSql(sb.toString(), null, null);
			downSql.tables = DownTable.tablesName(downSql.tableInfo);
			for (String table : downSql.tables) {
				union.append(" union all ");
				union.append(sb.toString().replace("from bill", "from " + table));

			}

			StringBuilder sql = new StringBuilder();
			sql.append("select * from ( ").append(sb.toString()).append(" ) as t ");
			sql.append(union.toString());

			List<BillDetails> result = Data.QueryT(sql.toString(), null, BillDetails.class);


			if (result.size() > 10) {
				result = result.subList(0, 10);
			}

			SysEnum sysEnum;
			String status = "";
			List<Map<String,Object>>  list = new ArrayList<>();
			for(BillDetails bill : result){
				Map<String,Object> billMap = new HashMap<>();
				billMap.put("id", bill.getId());
				billMap.put("showType", L(bill.getShowType()));//账单显示类型
				billMap.put("sendTime", bill.getSendTime());
				billMap.put("amount", bill.getAmount());
				billMap.put("balance", bill.getBalance());
				billMap.put("coinName", bill.getCoinName());
				billMap.put("fees", bill.getFees());//手续费
				billMap.put("bt", bill.getBt());//账单类型

				sysEnum = EnumUtils.getEnumByKey(bill.getStatus(), CoinChargeStatus.class);
				if (sysEnum != null) {
					status = L(sysEnum.getValue());
				}
				billMap.put("status", L(status));


				list.add(billMap);
			}

			Map<String, Object> page = new HashMap<String, Object>();
			page.put("list", list);

			json("", true, JSONObject.fromObject(page).toString());


//			BillDetailDao bwDao = new BillDetailDao();
//			BeanProxy bp = MysqlDownTable.getProxy("bill");
//
//			Query<BillDetails> query = bwDao.getQuery();
//			query.setSql("select id,userId,type,status,amount,sendTime,balance,fundsType,fees  from bill ");
//			query.setCls(BillDetails.class);


//			if ("payin".equals(type)) {
//				query.append(" and type = "+BillType.recharge.getKey());
//			} else if("payout".equals(type)) {
//				query.append(" and type = "+BillType.download.getKey());
//			}else if("trade".equals(type)){
//				query.append(" and type in( "+BillType.buy.getKey()+", "+BillType.sell.getKey()+", "+BillType.exchangeIn.getKey()+", "+BillType.exchangeOut.getKey()+")" );
//			}else{
//				query.append(" and type in( "+BillType.recharge.getKey()+","+BillType.download.getKey()+","+BillType.buy.getKey()+", "+BillType.sell.getKey()+", "+BillType.exchangeIn.getKey()+", "+BillType.exchangeOut.getKey()+")" );
//			}

//

//			query.append(" and userId = "+userId());
//			List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
//			int total = query.count();
//			List<BillDetails> weight = null;
//			if(total > 0){
//				query.append("order by id desc");
//				//分页查询
//				weight = bwDao.findPage(pageIndex, pageSize);
//				SysEnum sysEnum;
//				String status = "";
//				for(BillDetails bill:weight){
//					Map<String,Object> billMap = new HashMap<String,Object>();
//					billMap.put("id", bill.getId());
//					billMap.put("showType", bill.getShowType());//账单显示类型
//					billMap.put("sendTime", bill.getSendTime());
//					billMap.put("amount", bill.getAmount());
//					billMap.put("balance", bill.getBalance());
//					billMap.put("coinName", bill.getCoinName());
//					billMap.put("fees", bill.getFees());//手续费
//					billMap.put("bt", bill.getBt());//账单类型
//
//					sysEnum = EnumUtils.getEnumByKey(bill.getStatus(), CoinChargeStatus.class);
//					if (sysEnum != null) {
//						status = sysEnum.getValue();
//					}
//					billMap.put("status", status);
//
//
//					list.add(billMap);
//				}
//				weight.clear();
//			}
//
//			Map<String, Object> page = new HashMap<String, Object>();
//			page.put("pageIndex", pageIndex);
//			page.put("totalCount", total);
//			page.put("list", list);
//			json("", true, JSONObject.fromObject(page).toString());
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}


	/**
	 * 获取用户总资产(btc)
	 */
	@Page(Viewer = JSON)
	public void getUserTotalAssest() {
        try {
            String legalTender = param("legal_tender");
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
            PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
            String userId = userIdStr();
            String response = payUserApiService.getUserTotalAssest(userId,legalTender);
            json("", true,response);
        } catch (Exception e) {
            log.error("获取用户总资产异常！", e);
            json("fail", false, "");
        }
//
//
//
//		initLoanUser();
//
//
////		String btcUsdStr = Cache.Get("btc_usd");
////		String btcCnyStr = Cache.Get("btc_cny");
//		String usdCnyStr = Cache.Get("usd_cny");//美元对人民币
//
//		//{"total_btc":100,"total_legal_tender":100,"legal_tender_unit":"CNY"}
//		BigDecimal assets = p2pUser.getTotalAssets();//美元数量
//		BigDecimal assetsBTC = p2pUser.getTotalAssetsBtc();//btc数量
//		log.info(p2pUser.getUserId() + "换算汇率："  +usdCnyStr + "，折合美元资产：" + assets + "，折合比特币资产");
//		if ("usd".equalsIgnoreCase(legalTender)) {
////			json("", true, "{\"total_btc\":" + assets + ",\"total_legal_tender\":" + DigitalUtil.roundDown(assets.multiply(new BigDecimal(btcUsdStr)).doubleValue(), 2) + ",\"legal_tender_unit\":\"USD\"}");
//			json("", true, "{\"total_btc\":" + assetsBTC + ",\"total_usdt\":" + DigitalUtil.roundDown(assets.doubleValue(), 2) + ",\"total_legal_tender\":" + DigitalUtil.roundDown(assets.doubleValue(), 2) + ",\"legal_tender_unit\":\"USD\"}");
//			return;
//		} else if ("cny".equalsIgnoreCase(legalTender)) {
////			json("", true, "{\"total_btc\":" + assets + ",\"total_legal_tender\":" + DigitalUtil.roundDown(assets.multiply(new BigDecimal(btcCnyStr)), 2) + ",\"legal_tender_unit\":\"CNY\"}");
//			json("", true, "{\"total_btc\":" + assetsBTC + ",\"total_usdt\":" + DigitalUtil.roundDown(assets.doubleValue(), 2) + ",\"total_legal_tender\":" + DigitalUtil.roundDown(assets.multiply(new BigDecimal(usdCnyStr)), 2) + ",\"legal_tender_unit\":\"CNY\"}");
//			return;
//		} else {
//			json("", true, "{\"total_btc\":0,\"total_usdt\":0,\"total_legal_tender\":0,\"legal_tender_unit\":\"\"}");
//		}
	}


	/**
	 * 获取用户钱包总资产(btc)
	 */
	@Page(Viewer = JSON)
	public void getUserWalletTotalAssest() {
        try {
            String legalTender = param("legal_tender");
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
            PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
            String response = payUserApiService.getUserWalletTotalAssest(userIdStr(), legalTender);
            json("", true, response);
        } catch (Exception e) {
            log.error("获取用户钱包总资产异常！", e);
            json("fail", false, "");
        }
    }


	/**
	 * 获取用户钱包总资产(btc)
	 */
	@Page(Viewer = JSON)
	public void getUserOtcTotalAssest() {
        try {
            String legalTender = param("legal_tender");
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
            PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
            String response = payUserApiService.getUserOtcTotalAssest(userIdStr(), legalTender);
            json("", true, response);
        } catch (Exception e) {
            log.error("获取用户钱包总资产异常！", e);
            json("fail", false, "");
        }
    }

    /**
     * 获取用户理财资产
     */
    @Page(Viewer = JSON)
    public void getUserFinancialTotalAssest() {
        try {
            String legalTender = param("legal_tender");
            FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/payUser");
            PayUserApiService payUserApiService = container.getFeignClient(PayUserApiService.class);
            String response = payUserApiService.getUserFinancialTotalAssest(userIdStr(), legalTender);
            json("", true, response);
        } catch (Exception e) {
            log.error("获取用户钱包总资产异常！", e);
            json("fail", false, "");
        }
    }


}