package com.world.model.dao.pay.download;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.kafka.ProducerSend;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.pay.FreezDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.FreezType;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.financial.dao.FinanEntryDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanUseType;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadFacotry {
    FreezDao freezDao = new FreezDao();

    static Logger log = Logger.getLogger(DownloadFacotry.class.getName());
    private DailyType dailyType;//日志类型
    private CoinProps coint;

    public DownloadFacotry(DailyType dailyType, CoinProps coint) {
        this.dailyType = dailyType;
        this.coint = coint;
    }

    public void refreshDownloadTables() {
//		dealSync();
        List<Map<String, Object>> paramList = new ArrayList<>();
        String url = "/openapi/tradingcenter/withdrawal/trans/";
        url = ApiConfig.getInstance().getValue("tradingcenter.url") + url + coint.getPropTag().toUpperCase();
        try {
            String result = HttpUtil.doGetv2(url, null, 30000, 30000);
            // 报文格式
//			{"status":0,"data":[{"id":"87b97060-63ba-11e7-a74a-716192b9e861","amount":1.00000000,"coinType":"BTC","confirm":0,"status":2,"vn":0,"deal_time":null,"withdrawal_address":"1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa","service_charge":0.00500000,"target_confirm":3,"block_height":12,"real_fee":"12.2","tx_id":"_"}]}
            log.info("获取远程" + coint.getPropTag().toUpperCase() + "提币确认次成功:" + result);
            DownloadRemoteInfo info = com.alibaba.fastjson.JSONObject.parseObject(result, DownloadRemoteInfo.class);
            //调用处理
            if (null != info && info.getData() != null) {
                dealSuccessWallet(info);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }

//	public static void main(String [] args){
//		String str = "{\"status\":0,\"data\":[{\"id\":\"102988b2-6551-11e7-a74a-716192b9e861\",\"amount\":1.29000000,\"coinType\":\"LTC\",\"confirm\":3,\"status\":2,\"vn\":0,\"deal_time\":null,\"withdrawal_address\":\"mwTm2vRgYBmy4cfrPBuX1LziY8v6hdweky\",\"service_charge\":0.00100000,\"target_confirm\":3,\"block_height\":0,\"real_fee\":\"0.001\",\"tx_id\":\"350a7dd73eb7c5f56b2347c0eb529f25e6b582efe674828ded7e1f01d715a863\"}]}";
//		DownloadRemoteInfo info = com.alibaba.fastjson.JSONObject.parseObject(str,DownloadRemoteInfo.class);
//		CoinProps coint = DatabasesUtil.coinProps(3);
//		DownloadFacotry downloadFacotry = new DownloadFacotry(DailyType.btcDownload, coint);
//		downloadFacotry.dealSuccessWallet(info);
//	}

    /**
     * 返回特定状态的对象
     *
     * @param resultList
     * @param status
     * @return
     */
    private static List<DataInfo> getSingleStatusRecord(DownloadRemoteInfo resultList, int status) {
        List<DataInfo> dataList = new ArrayList<>();
        for (DataInfo data : resultList.getData()) {
            if (data.getStatus().intValue() == status) {
                dataList.add(data);
            }
        }
        return dataList;
    }

    /**
     * 获取失败的UUId
     *
     * @param list
     * @return
     */
    private String getFailedUuids(List<DataInfo> list) {
        String uuids = "";
        for (DataInfo data : list) {
            if (data.getId() != null && data.getId() != "") {
                uuids += "'" + data.getId() + "',";
            }
        }
        if (uuids == "") {
            return null;
        }
        return "(" + uuids.substring(0, uuids.length() - 1) + ")";
    }

    /**
     * 获取成功的UuId
     *
     * @param list
     * @return
     */
    private String getSuccessUuids(List<DataInfo> list) {
        String uuids = "";
        for (DataInfo data : list) {
            int outConfirmTimes = coint.getOutConfirmTimes();
            if (data.getId() != null && data.getId() != "" && data.getConfirm() >= outConfirmTimes) {
                uuids += "'" + data.getId() + "',";
            }
        }
        if (uuids == "") {
            return null;
        }
        return "(" + uuids.substring(0, uuids.length() - 1) + ")";
    }

//	/****
//	 * 获取待处理记录  status :5、打币中 4、失败  6、成功
//	 */
//	public void dealSync(){
//
//		List<Bean> syncs = Data.Query("select * from "+coint.getStag()+"download where status > 3 ORDER BY submitTime DESC limit 0,30", new Object[]{}, DownloadBean.class);
//		int size = syncs.size();
//		if(size > 0){
//			for(Bean b : syncs){
//				DownloadBean bd = (DownloadBean) b;
//				if(bd.getStatus() == 6){
//					sucess(bd);
//				}else if(bd.getStatus() == 4){
//					fail(bd);
//				}
//			}
//		}
//	}

    /**
     * 根据远程接口返回结果处理待处理信息
     *
     * @param
     */
    public void dealSuccessWallet(DownloadRemoteInfo infoObj) {

        log.info("dealSuccessWallet开始...");
        List<DataInfo> successObjList = getSingleStatusRecord(infoObj, 2);//获取成功的记录
        List<DataInfo> failedObjList = getSingleStatusRecord(infoObj, -1);//获取失败的记录

        String successUuids = getSuccessUuids(successObjList);
        String failedUuids = getFailedUuids(failedObjList);

        log.info("successUuids:" + successUuids);
        log.info("failedUuids:" + failedUuids);


        //处理成功的记录
        if (successUuids != null && successUuids != "") {
            List<Bean> successDownlaodList = Data.Query("select * from " + coint.getStag() + "download where status != 2 and uuid in " + successUuids, new Object[]{}, DownloadBean.class);
            Map<String, DataInfo> dataInfoMap = getDataInfoMap(successObjList);
            for (Bean b : successDownlaodList) {
                DownloadBean bd = (DownloadBean) b;
                log.info("正在处理提现成功记录-uuid:" + bd.getUuid());
                DataInfo info = dataInfoMap.get(bd.getUuid());
                sucess(bd, info);
            }
        }

        //处理失败的记录
        if (failedUuids != null && failedUuids != "") {
            List<Bean> failDownlaodList = Data.Query("select * from " + coint.getStag() + "download where status != 1 and uuid " + failedUuids, new Object[]{}, DownloadBean.class);
            Map<String, DataInfo> dataInfoMap = getDataInfoMap(failedObjList);
            for (Bean b : failDownlaodList) {
                DownloadBean bd = (DownloadBean) b;
                log.info("正在处理提现失败记录-uuid:" + bd.getUuid());
                DataInfo info = dataInfoMap.get(bd.getUuid());
                fail(bd, info);
            }
        }
        log.info("dealSuccessWallet结束...");
    }

    /**
     * 获取DataInfo键值对
     *
     * @param list
     * @return
     */
    private Map<String, DataInfo> getDataInfoMap(List<DataInfo> list) {
        Map<String, DataInfo> dataInfoMap = new HashMap<>();
        for (DataInfo info : list) {
            dataInfoMap.put(info.getId(), info);
            log.info("正在获取DataInfo键值对，键" + info.getId() + ",值：" + info);
        }
        return dataInfoMap;
    }

    //成功
    public void sucess(DownloadBean bdlb, DataInfo info) {
        try {
            List<OneSql> sqls = new ArrayList<>();
            //更新自动库中的状态为200表示已完成
            bdlb.setRemark(coint.getTag() + "提现成功");

            //modify by kinghao 20190121  预防时间戳非毫秒级
            long time = 9999999999L;
            Timestamp configTime = new java.sql.Timestamp(info.getTimestamp() * 1000);

            //钱包账单流水结算记账时间
            Timestamp billTime = TimeUtil.getNow();

            if (info.getTimestamp() > time) {
                configTime = new Timestamp(info.getTimestamp());
            }

            long freezId = freezDao.getId();
            //解冻并扣除的语句
            FreezeBean fbean = new FreezeBean(bdlb.getUserId(), bdlb.getUserName(), "提现成功", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), freezId, bdlb.getFreezeId());

            freezDao.coint = coint;
            //modify by xwz20171101为bill表增加手续费记录
            freezDao.unFreezPayUserSqls(billTime,sqls, fbean, BillType.download, false, bdlb.getFees(), configTime, info.getTxIdN(), bdlb.getId());
            int fundsType = coint.getFundsType();
            if(coint.getPropTag().toLowerCase().equals("usdte")){
                fundsType = coint.getAgreement();
            }
            /*modify by xwz 20170703*/

            //end
            /***Start by gkl 20190516 此处不能更新审核时间******/
//			sqls.add(new OneSql("update "+coint.getStag()+"Download set status=2,manageTime=?,confirm=?,txId=?,txIdN=?,blockHeight=?,realFee=?,configTime = ? where id="+bdlb.getId() , 1 ,
//					new Object[]{TimeUtil.getNow(), true, info.getTx_id(), info.getTxIdN(),info.getBlockHeight(), info.getReal_fee(),configTime}));//状态提现记录改为成功
            sqls.add(new OneSql("update " + coint.getStag() + "Download set status=2,confirm=?,txId=?,txIdN=?,blockHeight=?,realFee=?,configTime = ? where id=" + bdlb.getId(), 1,
                    new Object[]{true, info.getTx_id(), info.getTxIdN(), info.getBlockHeight(), info.getReal_fee(), configTime}));//状态提现记录改为成功
            /*end*/
            //更新提现汇总表中状态
//            sqls.add(new OneSql("update downloadsummary set status=2,manageTime=?,confirm=?,txId=?,txIdN=?,blockHeight=?,realFee=?,configTime = ? where fundsType=? and downloadId="+bdlb.getId(), 1 ,
//                    new Object[]{TimeUtil.getNow(), true, info.getTx_id(), info.getTxIdN(),info.getBlockHeight(), info.getReal_fee(),configTime, coint.getFundsType()}));//状态提现记录改为成功
            sqls.add(new OneSql("update downloadsummary set billTime = ? ,status=2,confirm=?,txId=?,txIdN=?,blockHeight=?,realFee=?,configTime = ? where fundsType=? and downloadId=" + bdlb.getId(), 1,
                    new Object[]{billTime,true, info.getTx_id(), info.getTxIdN(), info.getBlockHeight(), info.getReal_fee(), configTime, fundsType}));//状态提现记录改为成功
            /***End***/
            FinanAccount fa = (FinanAccount) Data.GetOne("SELECT * FROM finanaccount WHERE id = ? AND fundType = ?", new Object[]{bdlb.getCommandId(), coint.getFundsType()}, FinanAccount.class);
            FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
            FinanUseType usetype = useTypeDao.getByType(2);
            int useTypeId = usetype.getId();
            if (bdlb.getRealFee().compareTo(BigDecimal.ZERO) > 0) {
                sqls.addAll(new FinanEntryDao().saveOneEntry(fa.getId(), useTypeId, coint.getFundsType(), BigDecimal.ZERO, BigDecimal.ZERO.subtract(bdlb.getRealFee()), Integer.parseInt(bdlb.getUserId()), bdlb.getUserName(), "自动打币实际产生手续费", "127.0.0.1", bdlb.getId(), 0, false));
            }

            /*start by flym 20170628 打币成功后：更新到表finanaccount对应的资金类型字段curTotalAmount。*/
            String sql = "";
            sql = "update finanaccount set curTotalAmount = curTotalAmount + " + bdlb.getAmount() + "," + "amount = amount -" + bdlb.getAmount() + " "
                    + "where id = " + bdlb.getCommandId() + " and type = 3 ";
            log.info("更新finanaccount sql:" + sql);
            sqls.add(new OneSql(sql, 1, null));
            /*end*/
            log.info("提现成功执行sqls:" + sqls.toString());
            if (Data.doTrans(sqls)) {
                /*Start by guankaili 20190516 用户提现到账埋点 */
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("download", bdlb.getAmount().toPlainString());
                jsonObject.put("sendTime", configTime);
                jsonObject.put("fundsType", coint.getFundsType());
                ProducerSend producerSend = new ProducerSend();
                producerSend.sendMessage("trading", jsonObject.toString());
                log.info("推送驾驶舱用户提现到账埋点成功：" + jsonObject.toString());
                /*end*/
                log.info("更新提现状态成功，成功扣除用户资金。");
            } else {
                //TODO
                //出错打印报警日志，人工处理
                log.info("更新提现状态失败。");
                return;
            }
            String userName = bdlb.getUserName();
            UserCache.resetUserFundsFromDatabase(bdlb.getUserId());
            try {
                //20170328 modify by suxinjie 网页登录成功给app发送推送提醒
                //User downloadUser = new UserDao().getById(bdlb.getUserId());

                //插入一条管理员日志信息
                DailyType type = DailyType.btcDownload;
                new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "自动打币", userName, bdlb.getId(), fa.getName(), bdlb.getAmount(), bdlb.getToAddress()), "0", "", TimeUtil.getNow());

            } catch (Exception e) {
                log.error("添加日志失败:", e);
            }
        } catch (Exception e) {
            log.error("处理btc提现成功数据失败:", e);
        }
    }

    //地址非法失败解冻
    public void fail(DownloadBean bdlb, DataInfo info) {
        try {
            List<OneSql> sqls = new ArrayList<OneSql>();
            if (bdlb != null) {
                /***Start by gkl 20190516 此处不能更新审核时间******/
//				sqls.add(new OneSql("update "+coint.getStag()+"Download set status=1,manageTime=?,txId=?,txIdN=?,blockHeight=?,realFee=? where id=?", 1 ,
//                        new Object[]{ TimeUtil.getNow(),info.getTx_id(),info.getTxIdN(),info.getBlockHeight(),info.getReal_fee(),bdlb.getId()}));//状态提现记录改为失败
                sqls.add(new OneSql("update " + coint.getStag() + "Download set status=1,txId=?,txIdN=?,blockHeight=?,realFee=? where id=?", 1,
                        new Object[]{info.getTx_id(), info.getTxIdN(), info.getBlockHeight(), info.getReal_fee(), bdlb.getId()}));//状态提现记录改为失败
                //更新提现汇总表中状态
//                sqls.add(new OneSql("update downloadsummary set status=1,manageTime=?,txId=?,txIdN=?,blockHeight=?,realFee=? where fundsType=? and downloadId=? ", 1 ,
//                        new Object[]{ TimeUtil.getNow(),info.getTx_id(),info.getTxIdN(),info.getBlockHeight(),info.getReal_fee(), coint.getFundsType(), bdlb.getId()}));
                sqls.add(new OneSql("update downloadsummary set status=1,txId=?,txIdN=?,blockHeight=?,realFee=? where fundsType=? and downloadId=? ", 1,
                        new Object[]{info.getTx_id(), info.getTxIdN(), info.getBlockHeight(), info.getReal_fee(), coint.getFundsType(), bdlb.getId()}));
                /***End***/
                bdlb.setRemark(coint.getTag() + "提现失败");
                long freezId = freezDao.getId();
                //解冻不扣除
                FreezeBean fbean = new FreezeBean(bdlb.getUserId(), bdlb.getUserName(), "取消下载", FreezType.cashUnFreez.getKey(), bdlb.getAmount(), freezId, 0);
                freezDao.coint = coint;
                freezDao.unFreezSqls(sqls, fbean, null, false);
            }
            log.info("提现失败执行sqls:" + sqls.toString());
            if (Data.doTrans(sqls)) {///发送提现成功消息
                try {
                    UserCache.resetUserFundsFromDatabase(bdlb.getUserId());
                    //插入一条管理员日志信息
                    new MainDailyRecordDao().insertOneRecord(dailyType, DailyType.getMemoByType(dailyType, "确认失败", bdlb.getUserName(), bdlb.getId()), String.valueOf(0), "", TimeUtil.getNow());
                } catch (Exception e) {
                    log.error("添加日志失败", e);
                }
                return;
            }
        } catch (Exception e) {
            log.info("处理btc提现失败数据失败");
        }
    }


    public static void main(String[] args) {
		/*int fundsType = 9;
		String userId = "1003674";
		String userName = "l016@qq.com";
		String reMark = "提现成功";
		BigDecimal fees = new BigDecimal("0.5");

		Map<String, CoinProps> map = DatabasesUtil.getCoinPropMaps();
		for(Map.Entry<String, CoinProps> entry : map.entrySet()){
			CoinProps coint = entry.getValue();
			if (coint.getDatabaseKey().equals("gbc")){
				ExecutorService exec = Executors.newCachedThreadPool();
				// thread_num个线程可以同时访问
				final Semaphore semp = new Semaphore(5);
				// 模拟client_num个客户端访问
				for (int index = 0; index < 150; index++) {
					BigDecimal btcNumber = new BigDecimal(String.valueOf(index+1));
					final int NO = index;
					Runnable run = new Runnable() {
						@Override
						public void run() {
							try {
								// 获取许可
								semp.acquire();

								System.out.println("Thread并发事情>>>"+ NO);

								try {
									List<OneSql> sqls = new ArrayList<>();
									String otherSql = "withdrawFreeze=withdrawFreeze-"+btcNumber;
									sqls.add(new OneSql("update pay_user set freez=freez-? ,"+otherSql+" where userid=? and freez>=? AND fundsType = ?",1,
											new Object[] {btcNumber, userId, btcNumber, fundsType}));
									//Start by  gkl 20190214 提现解冻成功后添加提现流水(由于是从划转迁移过来的，对账需要类型，所以先按照币币账户划至我的钱包的类型处理，)
									String reMarkOut = "资金划转出";
									String outNowSql = "INSERT INTO bill (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
											"SELECT "+userId+",'"+userName+"','"+Integer.valueOf(BillType.bibiToWalletOut.getKey())+"',"+btcNumber+",'"+TimeUtil.getNow()+"','"+reMarkOut+"',"+BigDecimal.ZERO
											+",balance+freez as balance,"+fundsType+" from pay_user where userId=" +userId + " AND fundsType = "+fundsType+" for update";
									sqls.add(new OneSql(outNowSql, 1, new Object[]{}));

									String reMarkIn = "资金划转入";
									String inNowSql = "INSERT INTO bill_wallet (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
											"SELECT "+userId+",'"+userName+"','"+Integer.valueOf(BillType.bibiToWalletIn.getKey())+"',"+btcNumber+",'"+TimeUtil.getNow()+"','"+reMarkIn+"',"+BigDecimal.ZERO
											+",balance+freez+withdrawFreeze+"+btcNumber+" as balance,"+fundsType+" from pay_user_wallet where userId=" +userId + " AND fundsType = "+fundsType+" for update";
									sqls.add(new OneSql(inNowSql, 1, new Object[]{}));
									//end
									//去掉冻结明细
									//end
									String nowSql = "INSERT INTO bill_wallet (userId, userName, type, amount, sendTime, remark, fees, balance, fundsType) " +
											"SELECT "+userId+",'"+userName+"','"+BillType.download.getKey()+"',"+btcNumber+",'"+TimeUtil.getNow()+"','"+reMark+"',"+fees
											+",balance+freez+withdrawFreeze as balance,"+fundsType+" from pay_user_wallet where userId=" +userId + " AND fundsType = "+fundsType+" for update";
									sqls.add(new OneSql(nowSql, 1, new Object[]{}));
									//划转记录
									String fundTransferLogSql = "INSERT INTO fund_transfer_log (uid, amount, fundType, src, dst, time) " +
											"VALUES ("+userId+","+btcNumber+","+fundsType+","+2+","+1+",'"+TimeUtil.getNow()+"')";
									sqls.add(new OneSql(fundTransferLogSql, 1, new Object[]{}));
									if(Data.doTrans(sqls)){
										log.info("更新提现状态成功，成功扣除用户资金。");
									}else{
										//TODO
										//出错打印报警日志，人工处理
										log.info("更新提现状态失败。");
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								semp.release();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					exec.execute(run);
				}
				// 退出线程池
				exec.shutdown();
			}
		}*/
    }


}

