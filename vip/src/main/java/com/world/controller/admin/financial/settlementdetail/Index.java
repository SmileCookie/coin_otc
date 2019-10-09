package com.world.controller.admin.financial.settlementdetail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.query.Criteria;
import com.google.code.morphia.query.Query;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.enums.CoinChargeStatus;
import com.world.model.enums.CoinDownloadStatus;
import com.world.model.financial.dao.SettlementDetailDao;
import com.world.model.financial.entity.SettlementDetail;
import com.world.model.service.CoinChangeDetails;
import com.world.model.service.CoinService;
import com.world.util.CommonUtil;
import com.world.util.MerchantsUtil;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/settlementdetail/", des = "商户平台明细对账")
public class Index extends AdminAction {
	SettlementDetailDao dao = new SettlementDetailDao();
	CoinService coinService = new CoinService();

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		String tab = param("tab");
		int pageNo = intParam("page");
		Timestamp startTime = dateParam("startTime");
		Timestamp endTime = dateParam("endTime");
		String currency = param("currency");
		
		Map<String,CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
		if(currency==null ||currency.equals("")){
			Iterator<String> it = coinMap.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if(coinMap.get(key).isCoin()){
					currency = key;
					break;
				}
			}
		}
		
		Query query = dao.getQuery();

		int isIn = -1;
		if("charge".equals(tab)){
			isIn = 1;
		}else if("withdraw".equals(tab)){
			isIn = 0;
		}

		//String currency = coint;
		if(isIn >= 0 ){
			query.filter("isIn", isIn);
		}
		
		if(currency!=null && !currency.equals("")){
			query.filter("currency", currency);
		}
		//log.error(isIn+"  "+currency);
		

		if(startTime != null) {
			query.or(
					(Criteria)query.criteria("date").greaterThanOrEq(startTime),
					(Criteria)query.criteria("mdate").greaterThanOrEq(startTime)
			);
		}
		if(endTime != null) {
			query.or(
					(Criteria)query.criteria("date").lessThanOrEq(endTime),
					(Criteria)query.criteria("mdate").lessThanOrEq(endTime)
			);
		}

		int unusually = CommonUtil.stringToInt(param("unusually"), -1);
		if(unusually >= 0){
			query.filter("unusually", unusually);
		}

		String orderNo = param("orderNo");
		if(StringUtils.isNotBlank(orderNo)){
			query.filter("merchantOrderNo", orderNo);
		}

		long tradId = CommonUtil.stringToLong(param("tradId"));
		if(tradId > 0){
			query.filter("tradId", tradId);
		}

		query.order("-date");

		int pageSize = 20;
		long total = dao.count(query);
		List<SettlementDetail> dataList = dao.findPage(query, pageNo, pageSize);
		setAttr("dataList", dataList);
		setPaging((int) total, pageNo, pageSize);

		setAttr("currency", currency);
		setAttr("coinMap",coinMap);
		if(isIn >=0 && total > 0){
			double[] arr = dao.stats(currency, isIn, unusually, startTime, endTime);
			setAttr("total", arr[0]);
			setAttr("mtotal", arr[1]);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		setAttr("coinType", param("coinType"));
		setAttr("isIn", param("isIn"));
		setAttr("startTime", param("startTime"));
		Timestamp endTime = dateParam("endTime");
		setAttr("endTime", endTime == null ? TimeUtil.getNow() : endTime);
		setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
	}
	
	@Page(Viewer = ".xml")
	public void doAoru() {
		try {
			String currency = param("currency");
			int isIn = intParam("isIn");
			boolean isCharge = isIn == 1;
			Timestamp startTime = dateParam("startTime");
			Timestamp endTime = dateParam("endTime");
			Query<SettlementDetail> query = dao.getQuery();
			query.filter("currency", currency)
					.filter("isIn", isIn)
					/*.filter("date >=", startTime)
					.filter("date <", endTime)*/;

			dao.deleteByQuery(query);

			if(startTime==null){
				WriteError("请选择开始时间。");
				return;
			}
			// 1. 获取商户平台的数据
			JSONObject json = MerchantsUtil.getBill(currency, String.valueOf(isIn),
					CommonUtil.formatDate(startTime, "yyyy-MM-dd HH:mm:ss"),
					CommonUtil.formatDate(endTime, "yyyy-MM-dd HH:mm:ss"), "0");
			if (null == json || json.size() <= 0) {
				WriteError("访问商户平台超时。");
				return;
			}

			// 2. 以商户平台的数据为基础对比
			List<SettlementDetail> settlementDetailList = null;
			JSONArray jarry = null;
			if (json.containsKey("data")) {
				JSONObject data = json.getJSONObject("data");
				if (data.containsKey("bills")) {
					jarry = data.getJSONArray("bills");
					if (null != jarry && jarry.size() > 0) {
						settlementDetailList = new ArrayList<>(jarry.size());
						for (int i = 0;i<jarry.size();i++) {
							try {
								JSONObject bill = jarry.getJSONObject(i);
								SettlementDetail entity = new SettlementDetail();
								entity.setCurrency(currency);
								entity.setMerchantOrderNo(bill.getString("orderNo"));
								entity.setTradId(bill.getLong("tradId"));// 支付宝、人民币线下汇款tradeNo，在线充值对应billNo=id_用户id
								entity.setIsIn(isIn);
								entity.setmMoney(bill.getDoubleValue("money"));
								entity.setMdate(Timestamp.valueOf(bill.getString("date")));
								entity.setNoticeStatus(bill.getIntValue("noticeStatus"));
								entity.setmFees(bill.getDoubleValue("fees"));

								if (isCharge) { // 充值
									updateVcoinChargeByMid(entity);
								} else { // 虚拟币提现
									updateVcoinWithdrawByMid(entity);
								}

								dao.save(entity);
								settlementDetailList.add(entity);
							}catch (Exception ex){
								log.error(ex.toString(), ex);
							}
						}
					} /*else {
						WriteError("该时间段商户平台没有数据。");
						return;
					}*/
				}
			}

			// 3. 以bitglobal的数据为基础对比
			compareBaseOnBITGLOBAL(currency, isIn, isCharge, startTime, endTime, settlementDetailList);

			WriteRight("操作成功");
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteError("操作失败");
		}
	}

	private void compareBaseOnBITGLOBAL(String currency, int isIn, boolean isCharge, Timestamp startTime, Timestamp endTime, List<SettlementDetail> settlementDetailList) {
        List<CoinChangeDetails> coinChangeDetailsList = null;
		CoinProps coint = DatabasesUtil.coinProps(currency);
        if(isCharge) {
            coinChangeDetailsList = coinService.findChargeSucc(coint, startTime, endTime);
        }else {
            coinChangeDetailsList = coinService.findWithdrawSucc(coint, startTime, endTime);
        }

        if(CommonUtil.isNotEmptyCollection(coinChangeDetailsList)){
            for (CoinChangeDetails coinChangeDetails : coinChangeDetailsList){
                boolean isExist = false;
				if(CommonUtil.isNotEmptyCollection(settlementDetailList)) {
					for (SettlementDetail settlementDetail : settlementDetailList) {
						if ((isCharge && coinChangeDetails.getMerchantsSyncId() == settlementDetail.getTradId()) ||
								(!isCharge && coinChangeDetails.getMerchantOrderNo() != null && StringUtils.equals(coinChangeDetails.getMerchantOrderNo(), settlementDetail.getMerchantOrderNo()))) {
							isExist = true;
							break;
						}
					}
				}

                if( !isExist ){
                    SettlementDetail entity = new SettlementDetail();
					entity.setRecordId(coinChangeDetails.getRecordId());
                    entity.setCurrency(currency);
                    entity.setMerchantOrderNo(coinChangeDetails.getMerchantOrderNo());
                    entity.setTradId(coinChangeDetails.getMerchantsSyncId());
                    entity.setIsIn(isIn);
                    entity.setMoney(coinChangeDetails.getAmount().doubleValue());
                    entity.setDate(coinChangeDetails.getChangeTime());
                    entity.setNoticeStatus(-1);
                    entity.setFees(coinChangeDetails.getFees().doubleValue());
                    entity.setUnusually(1);
                    dao.save(entity);
                }
            }
        }
	}

	private void updateVcoinChargeByMid(SettlementDetail settlementDetail){
		String currency = settlementDetail.getCurrency();
		StringBuilder sql = new StringBuilder("select ");
		sql.append("detailsid, amount, fees, status, configTime from ")
				.append(currency).append("details where opUnique=?")
				.append(" and  status=?")
				/*.append(CoinType.LTC.getValue().equals(currency) ? "opUnique" : "merchantsSyncId")
				.append("=?")*/;
		DetailsBean coinDetails = (DetailsBean)Data.GetOne(sql.toString(),
				new Object[]{settlementDetail.getTradId(), CoinChargeStatus.SUCCESS.getKey()}, DetailsBean.class);

		if(coinDetails == null){
			settlementDetail.setMoney(0);
			settlementDetail.setFees(0);
			settlementDetail.setUnusually(1);
		}else{
			settlementDetail.setRecordId(coinDetails.getDetailsId());
			settlementDetail.setMoney(coinDetails.getAmount().doubleValue());
			settlementDetail.setFees(coinDetails.getFees().doubleValue());
			settlementDetail.setStatus(coinDetails.getStatus());
			settlementDetail.setDate(coinDetails.getConfigTime());
			settlementDetail.compareMoneyIsUnusually();
		}
	}

	private void updateVcoinWithdrawByMid(SettlementDetail settlementDetail){
		String currency = settlementDetail.getCurrency();
		StringBuilder sql = new StringBuilder("select ");
		sql.append("id, amount, fees, status, manageTime from ")
				.append(currency).append("download where ")
				.append("merchantOrderNo=?")
				.append(" and  status=?");
		DownloadBean coinDownload = (DownloadBean)Data.GetOne(sql.toString(),
				new Object[]{settlementDetail.getMerchantOrderNo(), CoinDownloadStatus.SUCCESS.getKey()}, DownloadBean.class);

		if(coinDownload == null){
			settlementDetail.setMoney(0);
			settlementDetail.setFees(0);
			settlementDetail.setUnusually(1);
		}else{
			settlementDetail.setRecordId(coinDownload.getId());
			settlementDetail.setMoney(coinDownload.getAmount().doubleValue());
			settlementDetail.setFees(coinDownload.getFees().doubleValue());
			settlementDetail.setStatus(coinDownload.getStatus());
			settlementDetail.setDate(coinDownload.getManageTime());
			settlementDetail.compareMoneyIsUnusually();
		}
	}
}

