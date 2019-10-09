package com.world.controller.admin.btc.download;

import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.world.cache.Cache;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.balaccount.job.autodownload.AutoDownloadWorker;
import com.world.model.dao.autofactory.AutoDownloadRecordDao;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.WalletBean;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.entity.AccountType;
import com.world.model.financial.entity.FinanAccount;
import com.world.util.UserUtil;
import com.world.util.date.TimeUtil;
import com.world.util.poi.ExcelManager;
import com.world.util.request.HttpUtil;
import com.world.web.Page;
import com.world.web.Pages;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.response.DataResponse;
import com.yc.entity.SysGroups;
import com.yc.util.MsgUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@FunctionAction(jspPath = "/admins/btc/download/", des = "本站确认")
public class HomeConfirm extends FinanAction {
    private static final long serialVersionUID = 1L;
    private DownloadDao bdDao = new DownloadDao();
    private AutoDownloadRecordDao autoDownloadRecordDao = new AutoDownloadRecordDao();

    // /选择钱包
    @Page(Viewer = "/admins/btc/download/wallet.jsp")
    public void confirmWallet() {

        int fundsType = coint.getFundsType();
        List<Bean> accounts = (List<Bean>) Data.Query("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ? ORDER BY createTime", new Object[]{fundsType, AccountType.withdraw.getKey()}, FinanAccount.class);
        setAttr("accounts", accounts);

        long did = longParam("id");

        Query<DownloadBean> query = bdDao.getQuery();
        query.setSql("select * from " + bdDao.getTableName() + " where id = " + did + " AND commandId=0 and status=0");// 没有确认的
        query.setCls(DownloadBean.class);

        DownloadBean bdb = (DownloadBean) query.getOne();
        setAttr("curData", bdb);
    }

    /**
     * 选择打币的账户之后
     */
    @Page(Viewer = JSON)
    public void getWalletBalance() {
        int accountId = intParam("accountId");
        if (accountId == 0) {
            json("notexsit", true, "");
            return;
        }
        FinanAccount fa = new FinanAccountDao().get(accountId);
        int walletId = fa.getBankAccountId();

        WalletBean bwb = (WalletBean) Data.GetOne("select * from " + coint.getStag() + "Wallet where walletId=?", new Object[]{walletId}, WalletBean.class);
        if (bwb == null) {
            json("notexsit", true, "");
            return;
        }
        JSONObject object = new JSONObject();

        BigDecimal balance = bwb.getBalance();
        object.put("wallet", balance);

        JSONObject hasSync = new JSONObject();
        JSONObject noSync = new JSONObject();
        List<Bean> downloads = (List<Bean>) Data.Query("SELECT * FROM " + bdDao.getTableName() + " WHERE commandId > 0 AND confirm = 0", new Object[]{}, DownloadBean.class);
        if (downloads != null && downloads.size() > 0) {
            BigDecimal fees = BigDecimal.ZERO;
            int count = 0;
            for (Bean b : downloads) {
                DownloadBean download = (DownloadBean) b;
                if (download.getRealFee().compareTo(BigDecimal.ZERO) > 0) {
                    count++;
                    fees = download.getRealFee().add(fees);
                }
            }
            if (count > 0) {
                hasSync.put("count", count);
                hasSync.put("fees", fees);
            }
            count = 0;
            fees = BigDecimal.ZERO;
            for (Bean b : downloads) {
                DownloadBean download = (DownloadBean) b;
                if (download.getRealFee().compareTo(BigDecimal.ZERO) == 0) {
                    count++;
                    fees = download.getAfterAmount().add(fees);
                }
            }
            if (count > 0) {
                noSync.put("count", count);
                noSync.put("fees", fees);
            }
        }
        if (!hasSync.isEmpty()) {
            object.put("hasSync", hasSync);
        }
        if (!noSync.isEmpty()) {
            object.put("noSync", noSync);
        }
        json("", true, object.toString());
    }

    /*****
     * 确认可自动提现
     */
    @Page(Viewer = ".xml")
    public void confirm() {
        if (!codeCorrect(XML)) {
            return;
        }
        //小额自动打币是否运行
        String autoDownloadRunning = "0";

        String autoDownloadRunningKey = AutoDownloadWorker.IS_RUNNING_PRE;
        if (StringUtils.isNotBlank(Cache.Get(autoDownloadRunningKey))) {
            autoDownloadRunning = Cache.Get(autoDownloadRunningKey);
            if (autoDownloadRunning.equals("1")) {
                WriteError(coint.getDatabaseKey() + "正在进行自动打币，请稍后");
                return;
            }
        }

        long did = longParam("did");
        int accountId = intParam("accountId");
        if (did <= 0 || accountId <= 0) {
            WriteError("该记录不存在或状态已变更，请刷新页面");
            return;
        }
        FinanAccount fa = new FinanAccountDao().get(accountId);

        Query<DownloadBean> query = bdDao.getQuery();

        query.setSql("select * from " + bdDao.getTableName() + " where id = " + did + " AND commandId=0 and status=0");// 没有确认的
        query.setCls(DownloadBean.class);

        DownloadBean bdb = (DownloadBean) query.getOne();
        if (bdb == null) {
            WriteError("该记录已确认或者状态已发生改变");
            return;
        }
        CoinProps coin = coinProps();
        //调用远程接口
        List<DownloadBean> list = Lists.newArrayList();
        list.add(bdb);

        ImmutablePair<Integer, List<String>> successUuids = null;
        if (updateStatusWhenSendBefore(coin, list)) {
            successUuids = callRemoteConfirmWallet(coin, list);
        } else {
            WriteError("该记录已经被审核，不可重新审核.");
            return;
        }
        if (successUuids != null) {

            // modify by renfei  增加服务可用性
            Integer code = successUuids.getLeft();
            List<String> data = successUuids.getRight();
            if (code.intValue() == -10010) {
                WriteError("钱包余额不足,暂时不能提现,请联系运营人员.");
                return;
            } else if (code.intValue() == 0) {

                if (CollectionUtils.isNotEmpty(data)) {

                    //u为空待处理
                    User u = getUserByUserId(Integer.parseInt(bdb.getUserId()));

                    BigDecimal fees = bdb.getFees();// FeeFactory.getFee(coin);
                    BigDecimal account = bdb.getAmount();
                    if (fees.compareTo(BigDecimal.ZERO) > 0) {// 网站补贴费率
                        account = account.subtract(fees);
                    }
                    long time = System.currentTimeMillis();
                    DataResponse dr = bdDao.autoCashToUser(coint, u.getId(), u.getUserName(), 3, "管理员操作", bdb.getToAddress(), did, bdb.getAmount(), fees, bdb.getPayFee(), fa.getId(), adminId(), ip(), time);

                    if (dr.isSuc()) {
                        //2017.08.16  xzhang 提现成功发短信提醒
                        autoCashNotice(bdb.getUserId(), bdb.getAmount());
                        WriteRight("记录已确认，正在打币中。");
                        try {
                            // 插入一条管理员日志信息
                            DailyType type = DailyType.btcDownload;
                            new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "下载确认", u.getUserName(), account, did, coint.getPropTag()), String.valueOf(adminId()), ip(), now(), Integer.parseInt(bdb.getUserId()), bdb.getAmount());
                        } catch (Exception e) {
                            log.error("添加管理员日志失败", e);
                        }
                    }
                } else {//兼容老接口
                    WriteError("钱包余额不足,暂时不能提现,请联系运营人员.");
                    return;
                }
            } else {
                WriteError("确认失败,错误码:" + code.intValue());
                return;
            }

        } else {
            WriteError("确认失败,远程服务异常.");
            return;
        }
    }

    /**
     * 调用确认前更新
     *
     * @param coin
     * @param list
     * @return
     */
    private boolean updateStatusWhenSendBefore(CoinProps coin, List<DownloadBean> list) {
        /*确认打币增加中间状态开始start*/
        try {
            String uuids = "";
            for (DownloadBean bean : list) {
                uuids += "'" + bean.getUuid() + "',";
            }
            if (uuids.length() > 1) {
                uuids = uuids.substring(0, uuids.length() - 1);
            }
            //int count = Data.Update("update " + coin.getDatabaseKey() + "download set status = ? where status = 0 and commandId = 0 and uuid in (" + uuids + ")", new Object[]{7});
            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(new OneSql("update " + coin.getDatabaseKey() + "download set status = ? where status = 0 and commandId = 0 and uuid in (" + uuids + ")" , list.size() ,
                    new Object[]{7}));
            sqls.add(new OneSql("update downloadsummary set status = ? where status = 0 and commandId = 0 and uuid in (" + uuids + ")" , list.size() ,
                    new Object[]{7}));
            if (!Data.doTrans(sqls)) {
                log.error("发送提币前更新提币记录状态失败...");
                return false;
            }
        } catch (Exception e) {
            log.error("发送提币前更新提币记录状态失败，", e);
            return false;
        }
        return true;
        /*end*/
    }

    // /批量确认选择钱包时提示总金额
    @Page(Viewer = "/admins/btc/download/walletAll.jsp")
    public void confirmAllWallet() {
        List<Bean> accounts = (List<Bean>) Data.Query("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ? ORDER BY createTime", new Object[]{coint.getFundsType(), AccountType.withdraw.getKey()}, FinanAccount.class);
        setAttr("accounts", accounts);

        String ids = param("eIds");
        boolean isAll = booleanParam("isAll");
        setAttr("isAll", isAll);
        setAttr("ids", ids);

        List<DownloadBean> list = getUserList();
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (list != null && list.size() > 0) {
            for (Bean b : list) {
                DownloadBean dbd = (DownloadBean) b;
                totalAmount = totalAmount.add(dbd.getAmount());
            }
        }
        setAttr("totalAmount", totalAmount);
    }
//	@Page(Viewer = JSON)
//	public void confirmAll() {
//		if (!codeCorrect(JSON)) {
//			return;
//		}
//
//		CoinProps coin = coinProps();
//		int accountId = intParam("accountId");
//
//		//提现记录列表
//		List<DownloadBean> list = getUserList();
//		if (accountId == 0) {
//			json("请选择打币钱包", false, "");
//			return;
//		}
//
//		if (list == null || list.size() == 0) {
//			json("没有需要打币的记录", false, "");
//			return;
//		}
//
//		//公司账户信息
//		FinanAccount fa = new FinanAccountDao().get(accountId);
//		int succCount = 0;
//		int failCount = 0;
//		BigDecimal succAmount = BigDecimal.ZERO;
//		for (Bean b : list) {
//			DownloadBean bdb = (DownloadBean) b;
//			//判断提现记录状态
//			if (bdb.getStatus() != 0 || bdb.getCommandId() > 0) {
//				continue;
//			}
//			long did = bdb.getId();
//			BigDecimal fees = bdb.getFees();// FeeFactory.getFee(coin);
//			BigDecimal account = bdb.getAmount();
//			if (fees.compareTo(BigDecimal.ZERO) > 0) {// 网站补贴费率
//				account = account.subtract(fees);// DigitalUtil.sub(account ,
//				// fees);
//			}
//
//			DataResponse dr = bdDao.autoCashToUser(coin, bdb.getUserId(), bdb.getUserName(), 3, "管理员操作", bdb.getToAddress(), did, bdb.getAmount(), fees, bdb.getPayFee(), fa.getId(), adminId(), ip());
//			if (dr.isSuc()) {
//				succCount++;
//				succAmount = succAmount.add(bdb.getAmount());
//			} else {
//				failCount++;
//			}
//		}
//		json("记录已同步到自动库中，成功" + succCount + "条，失败" + failCount + "条，打币总金额：" + succAmount + "。", true, "");
//		try {
//			// 插入一条管理员日志信息
//			DailyType type = DailyType.btcDownload;
//			new MainDailyRecordDao().insertOneRecord(type, coin.getPropTag() + "记录已同步到自动库中，成功" + succCount + "条，失败" + failCount + "条，打币总金额：" + succAmount + "。", String.valueOf(adminId()), ip(), now(), 0, succAmount);
//		} catch (Exception e) {
//			log.info("添加管理员日志失败");
//			log.error(e.toString(), e);
//		}
//	}

    /**
     * 批量打币方法，可以根据选中打币，可以把符合条件的全部打出
     * 增加调用远程接口（add by xiewenzheng）
     */
    @Page(Viewer = JSON)
    public void confirmAll() {
        if (!codeCorrect(JSON)) {
            return;
        }

        CoinProps coin = coinProps();
        int accountId = intParam("accountId");

        //提现记录列表
        List<DownloadBean> list = getUserList();

        if (null != list && list.size() > 200) {
            json("打币记录不能超过200条", false, "");
            return;
        }

        if (accountId == 0) {
            json("请选择打币钱包", false, "");
            return;
        }

        if (list == null || list.size() == 0) {
            json("没有需要打币的记录", false, "");
            return;
        }

        //公司账户信息
        FinanAccount fa = new FinanAccountDao().get(accountId);

        /*modify by xwz 自动打币修改此方法*/
        long time = System.currentTimeMillis();
        doConfirmDownload(fa, coin, list, false, time);
//		ImmutablePair<Integer,List<String>> successUuids = callRemoteConfirmWallet(coin,list);
//		//  modify by renfei  增加可用性
//		if(successUuids != null) {
//			int code = successUuids.getLeft();
//			List<String> result = successUuids.getRight();
//			if(code == 0){
//				if(CollectionUtils.isNotEmpty(result)){
//					long time = System.currentTimeMillis();
//					for (Bean b : list) {
//						DownloadBean bdb = (DownloadBean) b;
//						//判断提现记录状态,
//						if (!result.contains(bdb.getUuid()+"") || bdb.getStatus() != 0 || bdb.getCommandId() > 0) {
//							continue;
//						}
//						long did = bdb.getId();
//						BigDecimal fees = bdb.getFees();// FeeFactory.getFee(coin);
//						BigDecimal account = bdb.getAmount();
//						if (fees.compareTo(BigDecimal.ZERO) > 0) {// 网站补贴费率
//							account = account.subtract(fees);// DigitalUtil.sub(account ,
//							// fees);
//						}
//						DataResponse dr = bdDao.autoCashToUser(coin, bdb.getUserId(), bdb.getUserName(), 3, "管理员操作", bdb.getToAddress(), did, bdb.getAmount(), fees, bdb.getPayFee(), fa.getId(), adminId(), ip(),time);
//						if (dr.isSuc()) {
//							succCount++;
//							succAmount = succAmount.add(bdb.getAmount());
//							//2017.08.16  xzhang 提现成功发短信提醒
//							autoCashNotice(bdb.getUserId(),bdb.getAmount());
//						} else {
//							failCount++;
//						}
//					}
//				}else{//兼容老接口
//					json("钱包余额不足,暂时不能提现,请联系运营人员.", false, "");
//					return;
//				}
//
//			}else if(code == -10010){
//				json("钱包余额不足,暂时不能提现,请联系运营人员.", false, "");
//				return;
//			}else{
//				json("确认失败,远程服务异常.", false, "");
//				return;
//			}
//
//		}else{
//			json("确认失败,远程服务异常.", false, "");
//			return;
//		}
//		json("记录已同步到自动库中，成功" + succCount + "条，失败" + failCount + "条，打币总金额：" + succAmount + "。", true, "");
//		try {
//			// 插入一条管理员日志信息
//			DailyType type = DailyType.btcDownload;
//			new MainDailyRecordDao().insertOneRecord(type, coin.getPropTag() + "记录已同步到自动库中，成功" + succCount + "条，失败" + failCount + "条，打币总金额：" + succAmount + "。", String.valueOf(adminId()), ip(), now(), 0, succAmount);
//		} catch (Exception e) {
//			log.error("内部异常", e);
//		}
    }

    /**
     * 审核打币方法
     *
     * @param fa
     * @param coin
     * @param list
     * @param autoComfirm：true,自动打币；false,手动打币
     */
    public void doConfirmDownload(FinanAccount fa, CoinProps coin, List<DownloadBean> list, boolean autoComfirm, long time) {
        int adminId = 0;
        String ip = "127.0.0.1";
        if (!autoComfirm) {
            adminId = adminId();
            ip = ip();
        } else {
            bdDao.setCoint(coin);
        }

        int succCount = 0;
        int failCount = 0;
        BigDecimal succAmount = BigDecimal.ZERO;
        ImmutablePair<Integer, List<String>> successUuids = null;

        if (updateStatusWhenSendBefore(coin, list)) {
            successUuids = callRemoteConfirmWallet(coin, list);
        } else {
            json("存在记录已经被审核，不可重新审核.", false, "");
            return;
        }

        //  modify by renfei  增加可用性
        if (successUuids != null) {
            int code = successUuids.getLeft();
            List<String> result = successUuids.getRight();
            if (code == 0) {
                if (CollectionUtils.isNotEmpty(result)) {

                    for (Bean b : list) {
                        DownloadBean bdb = (DownloadBean) b;
                        //判断提现记录状态,
                        if (!result.contains(bdb.getUuid() + "") || bdb.getStatus() != 0 || bdb.getCommandId() > 0) {
                            continue;
                        }
                        long did = bdb.getId();
                        BigDecimal fees = bdb.getFees();// FeeFactory.getFee(coin);
                        BigDecimal account = bdb.getAmount();
                        if (fees.compareTo(BigDecimal.ZERO) > 0) {// 网站补贴费率
                            account = account.subtract(fees);
                            // DigitalUtil.sub(account, fees);
                        }

                        DataResponse dr = bdDao.autoCashToUser(coin, bdb.getUserId(), bdb.getUserName(), 3, "管理员操作", bdb.getToAddress(), did, bdb.getAmount(), fees, bdb.getPayFee(), fa.getId(), adminId, ip, time);
                        if (dr.isSuc()) {
                            //更新自动打币信息
                            succCount++;
                            succAmount = succAmount.add(bdb.getAmount());
                            if (autoComfirm) {
                                super.coint = coin;
                                bdb.setBatchId(coint.getDatabaseKey() + time);
                                updateAutoDownloadRecord(bdb, coin, time);
                            }
                            //2017.08.16  xzhang 提现成功发短信提醒
                            autoCashNotice(bdb.getUserId(), bdb.getAmount());
                        } else {
                            failCount++;
                        }
                    }
                } else {//兼容老接口
                    if (!autoComfirm) {
                        json("钱包余额不足,暂时不能提现,请联系运营人员.", false, "");
                    }
                    return;
                }

            } else if (code == -10010) {
                if (!autoComfirm) {
                    json("钱包余额不足,暂时不能提现,请联系运营人员.", false, "");
                }
                return;
            } else {
                if (!autoComfirm) {
                    json("确认失败,远程服务异常.", false, "");
                }
                return;
            }

        } else {
            if (!autoComfirm) {
                json("确认失败,远程服务异常.", false, "");
            }
            return;
        }
        if (!autoComfirm) {
            json("记录已同步到自动库中，成功" + succCount + "条，失败" + failCount + "条，打币总金额：" + succAmount + "。", true, "");
        }
        try {
            // 插入一条管理员日志信息
            DailyType type = DailyType.btcDownload;
            new MainDailyRecordDao().insertOneRecord(type, coin.getPropTag() + "记录已同步到自动库中，成功" + succCount + "条，失败" + failCount + "条，打币总金额：" + succAmount + "。", String.valueOf(adminId), ip, now(), 0, succAmount);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    /**
     * 自动打币更新缓存
     *
     * @param downloadBean
     */
    public void updateCacheWhenAutoConfirmCount(DownloadBean downloadBean, long time) {
        String submitTime = TimeUtil.getDateStr(new Timestamp(time), "yyyyMMdd");
        String userId = downloadBean.getUserId();

        int count = 0;//自动打币次数
        String key = AutoDownloadWorker.AUTO_DOWNLOAD_CONFIRMED_PRE + userId + "_" + submitTime;

        String countStr = Cache.Get(key);
        if (StringUtils.isNotBlank(countStr)) {
            count = Integer.valueOf(countStr);
        }
        //更新缓存
        Cache.Set(key, String.valueOf(++count), 25 * 60 * 60);
    }

    /**
     * @param download
     */
    public void updateAutoDownloadRecord(DownloadBean download, CoinProps coin, long time) {
        updateCacheWhenAutoConfirmCount(download, time);
        autoDownloadRecordDao.insertAutoDownloadRecordBean(download, coin.getFundsType());
    }


    //调用远程提币接口，返回提现ID字符串
    private ImmutablePair<Integer, List<String>> callRemoteConfirmWallet(CoinProps coin, List<DownloadBean> list) {
        try {
            String coinType = coin.getDatabaseKey().toLowerCase();
            String paramsStr = getParamOfComfirmWallet(coinType, list);
            log.info("调用提币接口参数：" + paramsStr);
            String ctype = "application/json;charset=UTF-8";    //类型
            coinType = coinType.toUpperCase();
            int confirm = coin.getOutConfirmTimes();
            String url = "/openapi/tradingcenter/withdrawal/";
            url = ApiConfig.getInstance().getValue("tradingcenter.url") + url + coinType + "?confirm=" + confirm;//请求地址,暂时
            String result = HttpUtil.doPostv2(url, ctype, paramsStr, 3000, 3000);
            log.info("调用提币获取提币结果:" + result);

            // 增加接口调用可用性  modify by renfei
            int resultCode = 0;
            List<String> resultMsg = Lists.newArrayList();
            if (StringUtils.isNotBlank(result)) {

                JSONObject json = com.alibaba.fastjson.JSON.parseObject(result);
                resultCode = json.getInteger("status");
                JSONObject data = json.getJSONObject("data");
                if (data.containsKey("success_ids")) {

                    JSONArray array = data.getJSONArray("success_ids");
                    resultMsg = array.toJavaList(String.class);
                }

            }


            return ImmutablePair.of(resultCode, resultMsg);
        } catch (Exception e) {
            log.error("内部异常", e);
        }
        return null;
    }

    //组装调用接口参数
    private String getParamOfComfirmWallet(String coinType, List<DownloadBean> list) {

        List<Map<String, Object>> result = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(list)) {
            for (DownloadBean bean : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", bean.getUuid());
                map.put("deal_time", new Date());
                map.put("amount", bean.getAmount());
                map.put("withdrawal_address", bean.getToAddress());
                map.put("service_charge", bean.getFees());
                /**start by kinghao 20190111 */
                if ("eos".equals(coinType)) {
                    map.put("memo", bean.getMemo());//插入地址标签
                }
                /**ends*/
                if (UserUtil.checkAddress(coinType, bean.getToAddress())) {
                    result.add(map);
                }

            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = null;
        try {
            jsonStr = objectMapper.writeValueAsString(result);
        } catch (IOException e) {
            log.error("format error.", e);
        }
        return jsonStr;//返回Json字符串
    }

    public List<DownloadBean> getUserList() {
        String currentTab = param("tab");
        Timestamp startTime = dateParam("startDate");
        Timestamp endTime = dateParam("endDate");
        Timestamp confirmStartime = dateParam("confirmStartDate");
        Timestamp confirmEndTime = dateParam("confirmEndDate");
        String toAddress = param("toAddress").trim();
        String userName = param("userName").trim();
        double moneyMin = doubleParam("moneyMin");
        double moneyMax = doubleParam("moneyMax");
        int commandId = intParam("commandId");
        String remark = param("remark");

        String ids = param("eIds");
        boolean isAll = booleanParam("isAll");

        Query<DownloadBean> query = bdDao.getQuery();
        query.setSql("select * from " + bdDao.getTableName());
        query.setCls(DownloadBean.class);

        if (isAll) {

            if (currentTab.length() == 0)
                currentTab = "needSend";

            if (currentTab.equals("needSend")) {
                query.append(" AND  (status=0 and commandId = 0)");
            } else if (currentTab.equals("confirm")) {
                query.append(" AND  (status=0 and commandId > 0)");
            } else if (currentTab.equals("success")) {
                query.append(" AND  (status=2)");
            } else if (currentTab.equals("fail")) {
                query.append(" AND  (status=1)");
            } else if (currentTab.equals("cancel")) {
                query.append(" AND  (status=3)");
            } else if (currentTab.equals("sendding")) {
                query.append(" AND  (status=7)");
            }
            if (confirmStartime != null) {
                query.append(" and manageTime >= cast('" + confirmStartime + "' as datetime)");
            }
            if (confirmEndTime != null) {
                query.append(" and manageTime <= cast('" + confirmEndTime + "' as datetime)");
            }

            if (startTime != null) {
                query.append(" and submitTime>=cast('" + startTime + "' as datetime)");
            }
            if (endTime != null) {
                query.append(" and submitTime<=cast('" + endTime + "' as datetime)");
            }

            if (toAddress.length() > 0) {
                query.append(" and toAddress LIKE '%" + toAddress + "%'");
            }
            if (userName.length() > 0) {
                query.append(" and userName LIKE '%" + userName + "%'");
            }

            if (StringUtils.isNotEmpty(remark)) {
                query.append(" and remark like '%" + remark + "%'");
            }

            if (moneyMin > 0) {
                query.append(" and amount >=" + moneyMin);
            }
            if (moneyMax > 0) {
                query.append(" and amount <=" + moneyMax);
            }
            if (commandId > 0) {
                query.append(" and commandId > 0");
            }
        } else {
            if (ids.endsWith(",")) {
                ids = ids.substring(0, ids.length() - 1);
            }
            query.append(" AND id IN (" + ids + ")");
        }

        query.append(" ORDER BY submitTime asc");

        int total = query.count();
        if (total > 0) {
            List<DownloadBean> btcDownloads = query.getList();

            return btcDownloads;
        }
        return null;
    }

    @Page(Viewer = "")
    public void exportUser() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            List<DownloadBean> needUser = getUserList();

            String[] column = {"userName", "toAddress", "amount", "status", "manageTime"};
            String[] tabHead = {"用户名", "提现地址", "数量", "状态代码", "确认时间"};//{"用户名","提交时间","提现地址","数量","状态"};
            HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
            OutputStream out = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("excel_download_info.xls", "UTF-8"));
            response.setContentType("application/msexcel;charset=UTF-8");
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    //start 2017.08.16  xzhang 提现成功发短信提醒
    public void autoCashNotice(String userId, BigDecimal amount) {
        try {
            double amountdouble = round(amount.doubleValue(), 6);
            DecimalFormat df = new DecimalFormat("######0.000000");
            String amountStr = df.format(amountdouble);
            if (amountStr.indexOf(".") > 0) {
                //正则表达
                amountStr = amountStr.replaceAll("0+?$", "");//去掉后面无用的零
                amountStr = amountStr.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
            }
            User user = new UserDao().getById(userId);
            UserContact userContact = user.getUserContact();
            int mobileStatu = userContact.getMobileStatu();
            String lan = user.getLanguage();
            String time = TimeUtil.getDateToString(new Date());
            String key = "申请提现到账提醒内容";
            String content = Lan.LanguageFormat(lan, key, user.getUserName(), time, amountStr, coint.getTag());
            if ("en".equals(lan)) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                time = sdf.format(new Date());
                content = Lan.LanguageFormat(lan, key, user.getUserName(), time,Double.parseDouble(amountStr) + "", coint.getTag());
            }
            if (2 == mobileStatu && !MsgUtil.isContain(user.getUserContact().getSafeMobile())) {
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                time = sdf.format(new Date());
                content = Lan.LanguageFormat("en", key, user.getUserName(), time,Double.parseDouble(amountStr) + "", coint.getTag());
            }
            String ip = "";
            if (2 == mobileStatu) {
                //发短信
                MobileDao mDao = new MobileDao();
                /*start by xzhang 20171031 短信服务临时解决方法，除+86外全发英文*/
                String title = Lan.Language(lan, "提现到账提醒");
                //去掉该逻辑，所有都按照用户选择语言发送 modify by buxianguan 20190805
//                if (!MsgUtil.isContain(user.getUserContact().getSafeMobile())) {
//                    title = Lan.Language("en", "提现到账提醒");
//                }
                /*end*/
                mDao.sendSms(user, ip, title, content, user.getUserContact().getSafeMobile());
            } else {
                //发邮箱
                EmailDao eDao = new EmailDao();
                SysGroups sg = SysGroups.vip;
                String title = Lan.Language(lan, SysGroups.vip.getValue()) + " " + Lan.Language(lan, "提现到账提醒");
                Pages pages = new Pages();
                content = eDao.getAutoSendEmailHtml(lan, user.getUserName(), content);
                eDao.sendEmail(ip, userId, user.getUserName(), title, content, user.getUserContact().getSafeEmail());
            }
        } catch (Exception e) {
            log.error("【提现到账提醒】用户" + userId + "提现到账提醒异常，异常信息为：", e);
        }
    }
    //end

    /**
     * 四舍五入操作
     *
     * @param d   要操作的数
     * @param len 保留几位有效数字
     * @return
     */
    public static double round(double d, int len) {
        BigDecimal b1 = new BigDecimal(d);
        BigDecimal b2 = new BigDecimal(1);
        // 任何一个数字除以1都是原数字
        // ROUND_HALF_UP是BigDecimal的一个常量,表示进行四舍五入的操作
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void main(String[] args) {
        String paramsStr = "[{\"amount\":\"560.0000\",\"service_charge\":\"0.2\",\"withdrawal_address\":\"sys\",\"memo\":\"123456\",\"deal_time\":154278625213855,\"id\":\"d0e53e2e-ac31-44af-90c4-6681f19543afb\"}]\n";
        log.info("调用提币接口参数：" + paramsStr);
        String ctype = "application/json;charset=UTF-8";    //类型
        String coinType = "EOS";
        int confirm = 3;
        String url = "http://192.168.2.35:8010/openapi/tradingcenter/withdrawal/";
        url = url + coinType + "?confirm=" + confirm;//请求地址,暂时
//        String result = HttpUtil.doPostv2(url, ctype, paramsStr, 3000, 3000);
//        log.info("调用提币获取提币结果:" + result);
    }


}
