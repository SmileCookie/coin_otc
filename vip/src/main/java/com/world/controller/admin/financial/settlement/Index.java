package com.world.controller.admin.financial.settlement;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.Key;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.enums.CoinChargeStatus;
import com.world.model.enums.CoinDownloadStatus;
import com.world.model.financial.dao.SettlementInfoDao;
import com.world.model.financial.entity.SettlementInfo;
import com.world.timer.DateUtilsEx;
import com.world.util.CommonUtil;
import com.world.util.MerchantsUtil;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

/**
 *
 * FIXME: 2017/7/31 suxinjie "下班结算"功能废弃,用"商户平台对账明细"代替
 */
@Deprecated
@FunctionAction(jspPath = "/admins/financial/settlement/", des = "下班结算")
public class Index extends AdminAction {

	SettlementInfoDao dao = new SettlementInfoDao();

	private static String CACHE_KEY = "settlementList";

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		int type = intParam("coinType");

		Query<SettlementInfo> q = dao.getQuery();
		int pageSize = PAGE_SIZE;

		Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
		// 将参数保存为attribute
		try {

			if(type > 0){
				q.filter("coinType =", type);
			}

			long total = q.countAll();
			if (total > 0) {
				q.order("-_id");
				List<SettlementInfo> dataList = dao.findPage(q, pageNo, pageSize);

				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);

			final List<SettlementInfo> list = new ArrayList<>(coinMap.size());

			final Timestamp endTime = TimeUtil.getMinuteFirst(now().getTime());
			long startms = System.currentTimeMillis();
			final List<Object> sumPayUserList = null;//getSumPayUser();
			log.info("统计RMB/LTC/ETH耗时：" + (System.currentTimeMillis() - startms) + "秒");
			final CountDownLatch countDownLatch = new CountDownLatch(coinMap.size());
			for (final Entry<String, CoinProps> entry : coinMap.entrySet()){
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							sum(entry.getValue(), endTime, sumPayUserList, list);
						}catch (Exception ex){
							log.error(ex.toString(), ex);
						}finally {
							countDownLatch.countDown();
						}
					}
				}).start();
			}
			countDownLatch.await();


            Collections.sort(list, new Comparator<SettlementInfo>() {
                @Override
                public int compare(SettlementInfo o1, SettlementInfo o2) {
                    return o1.getCoinType() < o2.getCoinType() ? -1 : 0;
                }
            });
			setAttr("list", list);
			Cache.SetObj(CACHE_KEY, list, 60 * 1000);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}

		setAttr("coinMap", coinMap);
	}

	private void sum(final CoinProps coint, final Timestamp endTime, List<Object> sumPayUserList, List<SettlementInfo> list) throws Exception{
		final long[] startms = {System.currentTimeMillis()};
		Timestamp startTime = null;
		double prevFtotalBalance = 0;
		int coin = coint.getFundsType();
		//查询最近一次结算记录
		SettlementInfo prevSettlementInfo = dao.getLast(coin);
		if (prevSettlementInfo != null) {
			startTime = prevSettlementInfo.getEndTime();
			startTime = CommonUtil.addSecond(startTime, 1);
			prevFtotalBalance = prevSettlementInfo.getFtotalBalance();
		} else {
			startTime = Timestamp.valueOf("2010-01-01 00:00:00");
		}

		SettlementInfo settlementInfo = new SettlementInfo();
		settlementInfo.setCoinType(coin);
		settlementInfo.setPrevFtotalBalance(prevFtotalBalance);
		BigDecimal zero = BigDecimal.ZERO;
		final BigDecimal[] totalBalance = {zero};
		final BigDecimal[] totalCharge = {zero};
		final BigDecimal[] totalChargeMer = {zero};
		final BigDecimal[] totalWithdraw = {zero};
		final BigDecimal[] totalWithdrawMer = {zero};
		final BigDecimal[] totalFees = {zero};
		//double totalReduce = sumReduce(coin, startTime, endTime);

		final String startTimeStr = DateUtilsEx.dateToString(startTime, "yyyy-MM-dd HH:mm:ss");
		final String endTimeStr = DateUtilsEx.dateToString(endTime, "yyyy-MM-dd HH:mm:ss");

		/*if (CoinType.CNY.getKey() == coin) {
//			totalBalance[0] = DigitalUtil.round(
//					DigitalUtil.add((double) sumPayUserList.get(0), (double) sumPayUserList.get(1)), 2);//sumPayUser.getTotalBalance();

			final BankTradeDao bankTradeDao = new BankTradeDao();

			final CountDownLatch countDownLatch = new CountDownLatch(4);

			final Timestamp finalStartTime = startTime;
			new Thread(new Runnable() {
				@Override
				public void run() {
                    long startms = System.currentTimeMillis();
					try {
						totalCharge[0] = bankTradeDao.sumChargeRMB(finalStartTime, endTime);
					}finally {
                        log.info("统计人民币充值总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
						countDownLatch.countDown();
					}
				}
			}).start();


			new Thread(new Runnable() {
				@Override
				public void run() {
                    long startms = System.currentTimeMillis();
					try {
						totalChargeMer[0] = getMerchantsResult(CoinType.CNY, true, startTimeStr, endTimeStr);
					}finally {
                        log.info("统计商户平台人民币充值总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
						countDownLatch.countDown();
					}
				}
			}).start();

			new Thread(new Runnable() {
				@Override
				public void run() {
                    long startms = System.currentTimeMillis();
					try {
						double[] totals = bankTradeDao.sumWithDrawRMB(finalStartTime, endTime);
						totalWithdraw[0] = totals[0];
						totalFees[0] = totals[1];
					}finally {
                        log.info("统计人民币提现总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
						countDownLatch.countDown();
					}
				}
			}).start();


			new Thread(new Runnable() {
				@Override
				public void run() {
                    long startms = System.currentTimeMillis();
					try {
						totalWithdrawMer[0] = getMerchantsResult(CoinType.CNY, false, startTimeStr, endTimeStr);
					}finally {
                        log.info("统计商户平台人民币提现总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
						countDownLatch.countDown();
					}
				}
			}).start();

			countDownLatch.await();

		} else {*/
			/*CountDownLatch countDownLatch = null;
			if (CoinType.LTC.getKey() == coin) {
				countDownLatch = new CountDownLatch(4);
//				totalBalance[0] = add((BigDecimal) sumPayUserList.get(2), (BigDecimal) sumPayUserList.get(3));
				//totalReduce = sumReduce.getLtcChange();
			} else if (CoinType.ETH.getKey() == coin) {
				countDownLatch = new CountDownLatch(4);
//				totalBalance[0] = add((BigDecimal) sumPayUserList.get(4), (BigDecimal) sumPayUserList.get(5));

				//totalReduce = sumReduce.getEthChange();
			} else if (CoinType.BTC.getKey() == coin) {
				countDownLatch = new CountDownLatch(5);
				final CountDownLatch finalCountDownLatch = countDownLatch;
				new Thread(new Runnable() {
					@Override
					public void run() {
                        long startms = System.currentTimeMillis();
						try{
							List<Object> sumBtcUserList = getSumBtcUser();
							log.info("统计BTC总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
							totalBalance[0] = add((BigDecimal) sumBtcUserList.get(0), (BigDecimal) sumBtcUserList.get(1)); // 可用 + 冻结
							totalBalance[0] = 0;//new BtcUserDao().getSumBtcBalance();
						}finally {
                            log.info("统计BTC总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
							finalCountDownLatch.countDown();
						}
					}
				}).start();

				//totalReduce = sumReduce.getBtcChange();
			} else if (CoinType.ETC.getKey() == coin) {
				countDownLatch = new CountDownLatch(5);
				final CountDownLatch finalCountDownLatch = countDownLatch;
				new Thread(new Runnable() {
					@Override
					public void run() {
                        long startms = System.currentTimeMillis();
						try{
							List<Object> sumCurrencyUserList = getSumCurrencyUser();
							log.info("统计ETC总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
							totalBalance[0] = add((BigDecimal) sumCurrencyUserList.get(0), (BigDecimal) sumCurrencyUserList.get(1)); // 可用 + 冻结
                            totalBalance[0] = 0;//new CurrencyUserDao().getSumEtcBalance();
                        }finally {
                            log.info("统计ETC总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
							finalCountDownLatch.countDown();
						}
					}
				}).start();

				//totalReduce = sumReduce.getEtcChange();
			}*/

			final String currency = coint.getStag();
			CountDownLatch countDownLatch = new CountDownLatch(4);
			final Timestamp finalStartTime1 = startTime;
			final CountDownLatch finalCountDownLatch = countDownLatch;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						long startms = System.currentTimeMillis();
						totalCharge[0] = sumChargeVcoin(currency, finalStartTime1, endTime);
						log.info("统计" + currency + "充值总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
					}finally {
						finalCountDownLatch.countDown();
					}
				}
			}).start();


			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						long startms = System.currentTimeMillis();
						totalChargeMer[0] = getMerchantsResult(coint, true, startTimeStr, endTimeStr);
						log.info("统计商户平台" + coint.getStag() + "充值总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
					}finally {
						finalCountDownLatch.countDown();
					}
				}
			}).start();

			final Timestamp finalStartTime3 = startTime;
			new Thread(new Runnable() {
				@Override
				public void run() {
					try{
						long startms = System.currentTimeMillis();
						BigDecimal[] arr = sumWidrawVcoin(currency, finalStartTime3, endTime);
						log.info("统计" + currency + "提现总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
						totalWithdraw[0] = arr[0];
						totalFees[0] = arr[1];
					}finally {
						finalCountDownLatch.countDown();
					}
				}
			}).start();

			new Thread(new Runnable() {
				@Override
				public void run() {
                    long startms = System.currentTimeMillis();
					try{
						totalWithdrawMer[0] = getMerchantsResult(coint, false, startTimeStr, endTimeStr);
					}finally {
                        log.info("统计商户平台" + currency + "提现总额耗时：" + (System.currentTimeMillis() - startms) / 1000.0 + "秒");
						finalCountDownLatch.countDown();
					}
				}
			}).start();

			finalCountDownLatch.await();
//		}

		settlementInfo.setFtotalBalance(totalBalance[0].doubleValue());
		settlementInfo.setTotalCharge(totalCharge[0].doubleValue());
		settlementInfo.setTotalChargeMer(totalChargeMer[0].doubleValue());
		settlementInfo.setTotalWithdraw(totalWithdraw[0].doubleValue());
		settlementInfo.setTotalWithdrawMer(totalWithdrawMer[0].doubleValue());
		settlementInfo.setTotalFees(totalFees[0].doubleValue());
		//settlementInfo.setSysReduce(totalReduce);
		settlementInfo.setStartTime(startTime);
		settlementInfo.setEndTime(endTime);
		settlementInfo.setUserName(adminName());

		list.add(settlementInfo);
	}

	private BigDecimal getMerchantsResult(CoinProps coint, boolean isCharge, String startTimeStr, String endTimeStr){
		JSONObject result = MerchantsUtil.getBillTotal(coint.getStag(), isCharge ? "1" : "0", startTimeStr, endTimeStr, "0");

		JSONObject data = null;
		if(result != null &&
				result.getIntValue("code") == 1000 &&
				(data = result.getJSONObject("data") ) != null){
			try {
				return data.getBigDecimal("money");
			}catch (Exception ex){
				log.error("内部异常", ex);
			}
		}

		return BigDecimal.ZERO;
	}

	/*private List<Object> getSumPayUser(){
		String sql = "select sum(p.Balance_Money) balance_Money, sum(p.Freez_Money) freez_Money, " +
				"sum(p.ltcs) ltcs, sum(p.freezLtcs) freezLtcs, sum(p.eths) eths, sum(p.freezEths) freezEths from pay_user p";
		return (List<Object>)Data.GetOne(sql, null);

       *//* String sql = "select balance_Money, freez_Money, ltcs, freezLtcs, eths, freezEths from pay_user";
        List<PayUserBean>  payUserList = Data.Query(sql, null);
        for()*//*
	}*/

	/*private double sumChargeVcoin(String currency, Timestamp startTime, Timestamp endTime){
		StringBuilder querySql = new StringBuilder();
		querySql.append("select sum(number) number from ")
				.append(currency).append("details where status=?")
				.append(" and ").append("btc".equals(currency) ? "isIn" : "type").append("=1")
				.append(" and configTime<=?");

		Object[] params = null;
		if(startTime == null){
			params = new Object[]{CoinChargeStatus.SUCCESS.getKey(), endTime};
		}else{
			querySql.append(" and configTime>=?");
			params = new Object[]{CoinChargeStatus.SUCCESS.getKey(), endTime, startTime};
		}

		List<Object> list = (List<Object>)Data.GetOne(querySql.toString(), params);

		return formateBigDecimal((BigDecimal)list.get(0));
	}*/

	private BigDecimal sumChargeVcoin(String currency, Timestamp startTime, Timestamp endTime){
		StringBuilder querySql = new StringBuilder();
		querySql.append("select amount from ")
				.append(currency).append("details where status=?")
				.append(" and ").append("type").append("=1")
				.append(" and configTime<=?");

		Object[] params = null;
		if(startTime == null){
			params = new Object[]{CoinChargeStatus.SUCCESS.getKey(), endTime};
		}else{
			querySql.append(" and configTime>=?");
			params = new Object[]{CoinChargeStatus.SUCCESS.getKey(), endTime, startTime};
		}

		List<Bean> list = Data.Query(querySql.toString(), params, DetailsBean.class);

		if(CommonUtil.isEmptyCollection(list)){
			return BigDecimal.ZERO;
		}

		BigDecimal total = BigDecimal.ZERO;
		for(Bean b :  list){
			DetailsBean details = (DetailsBean)b;
			total = total.add(details.getAmount());
		}
		return total;
	}

	/*private double[] sumWidrawVcoin(String currency, Timestamp startTime, Timestamp endTime){
		StringBuilder querySql = new StringBuilder();
		querySql.append("select sum(amount), sum(fees) from ")
				.append(currency).append("download where status=?")
				.append(" and manageTime<=?");

		Object[] params = null;
		if(startTime == null){
			params = new Object[]{CoinDownloadStatus.SUCCESS.getKey(), endTime};
		}else{
			querySql.append(" and manageTime>=?");
			params = new Object[]{CoinDownloadStatus.SUCCESS.getKey(), endTime, startTime};
		}

		List<BigDecimal> list = (List<BigDecimal>)Data.GetOne(querySql.toString(), params);

		return new double[]{
				formateBigDecimal(list.get(0)),
				formateBigDecimal(list.get(1))
		};
	}*/

	private BigDecimal[] sumWidrawVcoin(String currency, Timestamp startTime, Timestamp endTime){
		StringBuilder querySql = new StringBuilder();
		querySql.append("select amount, fees from ")
				.append(currency).append("download where status=?")
				.append(" and manageTime<=?");

		Object[] params = null;
		if(startTime == null){
			params = new Object[]{CoinDownloadStatus.SUCCESS.getKey(), endTime};
		}else{
			querySql.append(" and manageTime>=?");
			params = new Object[]{CoinDownloadStatus.SUCCESS.getKey(), endTime, startTime};
		}

		List<Bean> list = Data.Query(querySql.toString(), params, DownloadBean.class);

		if(CommonUtil.isEmptyCollection(list)){
			return new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO};
		}

		BigDecimal total = BigDecimal.ZERO;
        BigDecimal fees = BigDecimal.ZERO;
		for(Bean b : list){
			DownloadBean download = (DownloadBean)b;
			total = total.add(download.getAmount());
			fees = fees.add(download.getFees());
		}

		return new BigDecimal[]{total,fees};
	}

	private BigDecimal sumReduce(int coin, Timestamp startTime, Timestamp endTime){
		String column = "amount";
		int type = BillType.sysDeduct.getKey();

		StringBuilder querySql = new StringBuilder();
		querySql.append("select sum(")
				.append(column)
				.append(") from bill where type=?")
				.append(" and date<=?");

		Object[] params = null;
		if(startTime == null){
			params = new Object[]{type, endTime};
		}else{
			querySql.append(" and date>=?");
			params = new Object[]{type, endTime, startTime};
		}

		List<Object> list = ((List<Object>) Data.GetOne(querySql.toString(), params));
		if(CommonUtil.isEmptyCollection(list)){
			return BigDecimal.ZERO;
		}

		return (BigDecimal)list.get(0);
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	private SettlementInfo getByCoinType(int coinType){
		List<SettlementInfo> list = (List<SettlementInfo>)Cache.GetObj(CACHE_KEY);
		if(list == null || list.size() < 1){
			return null;
		}

		for(SettlementInfo settlementInfo: list){
			if(settlementInfo.getCoinType() == coinType){
				return  settlementInfo;
			}
		}

		return null;
	}

	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try{
			int coinType = intParam("coinType");
			SettlementInfo settlementInfo = getByCoinType(coinType);

			setAttr("coinType", coinType);
			setAttr("entity", settlementInfo);
			setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
		}catch (Exception ex){
			log.error(ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doAoru() {
		try{
			if(!codeCorrect(XML)){
				return;
			}

			String memo = param("memo");
			String status = param("status");
			int coinType = intParam("coinType");
			SettlementInfo settlementInfo = getByCoinType(coinType);
			settlementInfo.setMemo(memo);
			settlementInfo.setStatus(status);

			Key<SettlementInfo> key = dao.save(settlementInfo);

			if (key != null) {
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		}catch (Exception ex){
			log.error("内部异常", ex);
			WriteError("操作失败");
		}
	}


	@Page(Viewer = "/admins/financial/settlement/memo.jsp")
	public void memo() {
		try {
			long id = longParam("id");
			if(id > 0){
				SettlementInfo info = dao.findOne(dao.getQuery().filter("_id =", id));
				setAttr("entity", info);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doMemo() {
		try {
			long id = longParam("id");
			String memo = param("memo");

			memo = adminName() + ":" + memo;
			UpdateResults<SettlementInfo> ur =	dao.update(dao.createQuery().filter("_id =", id), dao.getUpdateOperations().set("memo", memo));

			if (ur.getError() == null) {
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception ex) {
			WriteError("操作失败");
			log.error("内部异常", ex);
		}
	}
}

