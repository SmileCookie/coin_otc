package com.world.model.balaccount.job.autodownload;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.cache.Cache;
import com.world.controller.admin.btc.download.HomeConfirm;
import com.world.data.database.DatabasesUtil;
import com.world.model.balaccount.job.util.HttpRequestUtil;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.SmallPayManagementDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.SmallPayManagementBean;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.entity.AddressType;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.job.dao.JobDefinDao;
import com.world.model.job.entity.JobDefinBean;
import com.world.util.date.TimeUtil;
import com.yc.entity.msg.Msg;
import com.yc.util.MsgUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by xie on 2017/10/16.
 */
public class AutoDownloadWorker extends Worker {
    private static final long serialVersionUID = 1L;
    private final static Logger logAlarm = Logger.getLogger("alarmAll");
    /*用户日免审次数*/
    private static final int AUTO_DOWNLOAD_COUNT = 3;
    /*打币条数限制*/
    private static final int AUTO_DOWNLOAD_SIZE = 200;
    /*小额打币状态*/
    public static final String IS_RUNNING_PRE = "auto_download_running_";
    /*自动打币确认*/
    public static final String AUTO_DOWNLOAD_CONFIRMED_PRE = "auto_download_confirmed_";
    private JobDefinDao jobDefinDao = new JobDefinDao();
    private FinanAccountDao finanAccountDao = new FinanAccountDao();
    private DownloadDao downloadDao = new DownloadDao();
    private CommAttrDao commAttrDao = new CommAttrDao();

    /*用户打币金额限制*/
    private Map<Integer, String> autoDownloadLimitAmountMap = new HashMap<>();
    /*钱包剩余金额限制*/
    private Map<Integer, String> balanceLimitAmountMap = new HashMap<>();
    /*热提钱包余额预警值*/
    private Map<Integer, String> accountWarnLimitMap = new HashMap<>();
    /*热钱包余额*/
    private Map<Integer, BigDecimal> hotWithdrawWallBalanceMap = new HashMap<>();
    /*小额打币提醒手机号*/
    private List<String> noticePhoneList = null;

    public AutoDownloadWorker(String name, String des) {
        super(name, des);
    }

    public AutoDownloadWorker() {
    }

    public static void main(String[] args) {
        new AutoDownloadWorker().run();
    }

    /**
     * 初始化配置信息
     */
    public void initConfig() {
        Map<Integer, String> autoDownloadLimitAmountMapTmp = new HashMap<>();
        try {
            hotWithdrawWallBalanceMap = finanAccountDao.getBalanceMap(3);
            balanceLimitAmountMap = commAttrDao.queryUserTypeMap(AddressType.AUTO_DOWNLOAD_ACCOUNT_MIN.getKey());
            autoDownloadLimitAmountMapTmp = commAttrDao.queryUserTypeMap(AddressType.AUTO_DOWNLOAD_AMOUNT_LIMIT.getKey());
            accountWarnLimitMap = commAttrDao.queryUserTypeMap(AddressType.AUTO_DOWNLOAD_ACCOUNT_WARN_LIMIT.getKey());
            //获取手机号列表
            List<CommAttrBean> commAttrBeanList = commAttrDao.queryListByAttrTypeAndParaCode(AddressType.AUTO_DOWNLOAD_ACCOUNT_NOTICE.getKey(), "01");
            noticePhoneList = new ArrayList<>();
            if(commAttrBeanList != null && commAttrBeanList.size()>0){
                for(CommAttrBean commAttrBean : commAttrBeanList){
                    noticePhoneList.add(commAttrBean.getParaValue());
                }
            }

        } catch (Exception e) {
            log.error("小额打币初始化配置信息出错，错误信息：", e);
        }


        for (Map.Entry<Integer, String> entry : autoDownloadLimitAmountMapTmp.entrySet()) {
            Integer key = entry.getKey();
            if (hotWithdrawWallBalanceMap.containsKey(key) && balanceLimitAmountMap.containsKey(key)) {
                try {
                    BigDecimal balance = hotWithdrawWallBalanceMap.get(key);
                    BigDecimal balanceLimitAmount = new BigDecimal(balanceLimitAmountMap.get(key));
                    BigDecimal autoDownloadLimitAmount = new BigDecimal(autoDownloadLimitAmountMapTmp.get(key));
                    if (balance.compareTo(balanceLimitAmount) > 0 && balanceLimitAmount.compareTo(BigDecimal.ZERO) > 0 && autoDownloadLimitAmount.compareTo(BigDecimal.ZERO) > 0) {
                        autoDownloadLimitAmountMap.put(key, entry.getValue());
                    }
                } catch (Exception e) {
                    log.error("小额打币初始化信息异常，币种代码：" + key + "，错误信息：", e);
                }
            }
        }
    }


    @Override
    public void run() {
        Date currentDate = new Date();
        //判断是否可以进行小额打币
        if (canAutoDownload(currentDate)) {
            String isRunningKey = IS_RUNNING_PRE;
            String isRunning = Cache.Get(isRunningKey);
            if (StringUtils.isEmpty(isRunning)) {
                //更新缓存中自动打币状态,保存30min
                Cache.Set(isRunningKey, "1", 0);
            }
            while(true){
              if("1".equals(Cache.Get(isRunningKey)))  {
                  break;
              }else{
                  try {
                      Thread.sleep(30000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
              }
            }
            Cache.Set(isRunningKey, "2", 0);//进行中
            log.info("小额打币开始...");
            //初始物配置信息
            initConfig();
            checkWarnBalanceMsg(autoDownloadLimitAmountMap, hotWithdrawWallBalanceMap, accountWarnLimitMap);
            //当前时间
            long time = System.currentTimeMillis();
            //是否可以发送短信
            boolean isSendSmsForCanNotDownload = false;
            //循环执行配置的币种
            for (Map.Entry<Integer, String> entry : autoDownloadLimitAmountMap.entrySet()) {
                int fundstype = entry.getKey();
                //获取对应币种
                CoinProps coin = DatabasesUtil.coinProps(fundstype);
                //币种名称
                String coinName = coin.getDatabaseKey();
                List<SmallPayManagementBean> smallPayManagementBeanList = new SmallPayManagementDao().findList();
                Boolean download = false;
                for(SmallPayManagementBean SmallPayManagement : smallPayManagementBeanList){
                    if(SmallPayManagement.getFundstype() == fundstype){
                        download = SmallPayManagement.getDownload() == 1 ? true : false;
                    }
                }
                if(!download){
                    continue;
                }
                log.info("小额打币" + coinName + "开始");
                //提现热钱包ID
                String hotWithdrawWalletId = "";
                //获取提现热钱包余额
                BigDecimal balance = BigDecimal.ZERO;

                try {
                    //获取热提钱包余额
                    try {
                        FinanAccount finanAccount = finanAccountDao.findBeanByFundTypeAndType(fundstype, 3);
                        hotWithdrawWalletId = finanAccount.getId() + "";
                        balance = finanAccount.getAmount();
                    } catch (Exception e) {
                        log.error("获取" + coinName + "热提钱包余额或者当前币种限额失败,错误信息：", e);
                        continue;
                    }
                    //当前币种金额限制
                    BigDecimal balanceLimitAmount = new BigDecimal(balanceLimitAmountMap.get(fundstype));
                    //当前币种自动打币金额限制
                    BigDecimal autoDownloadLimit = new BigDecimal(autoDownloadLimitAmountMap.get(fundstype));
                    downloadDao.setCoint(coin);
                    //获取全部未审核的记录
                    List<DownloadBean> list = downloadDao.getAutoDownloadlist();
                    BigDecimal netBalance = balance.subtract(balanceLimitAmount);
                    List<DownloadBean> canAutoDownloadRecords = new ArrayList<>();
                    //获取符合打币条件记录
                    if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
                        canAutoDownloadRecords = getCanAutoDownloadRecords(netBalance, autoDownloadLimit, list, time, fundstype);
                    } else {//余额不足下一个币种
                        log.info("BBFKTSCWCL0034热提钱包余额不足【" + coinName + "热提钱包余额不足，余额：" + balance + ",最低限额：" + balanceLimitAmount + "】");
                        continue;
                    }
                    //发送短信
                    if (!isSendSmsForCanNotDownload && list.size() > canAutoDownloadRecords.size()) {
                        isSendSmsForCanNotDownload = true;
                    }
                    if (canAutoDownloadRecords.size() > 0) {
                        //判断钱包余额
                        try {
                            int downloadCount = 1 + canAutoDownloadRecords.size() / AUTO_DOWNLOAD_SIZE;
                            for (int i = 0; i < downloadCount; i++) {
                                int start = i * AUTO_DOWNLOAD_SIZE;
                                int end = (i + 1) * AUTO_DOWNLOAD_SIZE;
                                if (canAutoDownloadRecords.size() < end) {
                                    end = canAutoDownloadRecords.size();
                                }
                                // 取200条
                                List<DownloadBean> tmpcanAutoDownloadRecords = canAutoDownloadRecords.subList(start, end);

                                //打币
                                doAutoConfirmDownload(Integer.valueOf(hotWithdrawWalletId), coin, tmpcanAutoDownloadRecords, time);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            log.error(coin.getDatabaseKey() + "自动打币任务出错，错误信息：", e);
                        }
                    }
                    log.info("小额打币" + coinName + "结束");

                } catch (Exception e) {
                    log.error("小额打币出错，", e);
                } finally {
                    //打币结束，更新缓存中自动打币信息,状态设为开启
                    Cache.Set(isRunningKey, "1", 30*60);
                }

            }
            //发送
            if (isSendSmsForCanNotDownload) {
                try {
                    String cont = "存在需审核的打币记录,请尽快审核";
                    //钉钉通知
                    logAlarm.info("10400001TASKSHDB【提现审核】：" + cont);
                    if(noticePhoneList != null && noticePhoneList.size() > 0){
                        for(int i = 0 ; i < noticePhoneList.size(); i++){
                            sendPhone(noticePhoneList.get(i), cont);
                        }
                    }
                } catch (Exception e) {
                    log.error("发送小额打币提醒失败，错误信息：", e);
                }


            }
            log.info("小额打币结束...");
        }
    }

    /**
     * 打币
     *
     * @param hotWithdrawWalletId
     * @param coin
     * @param canAutoDownloadRecords
     */

    public void doAutoConfirmDownload(int hotWithdrawWalletId, CoinProps coin, List<DownloadBean> canAutoDownloadRecords, long time) {
        FinanAccount fa = new FinanAccountDao().get(hotWithdrawWalletId);
        //打币
        HomeConfirm homeConfirm = new HomeConfirm();
        homeConfirm.doConfirmDownload(fa, coin, canAutoDownloadRecords, true, time);
    }

    /**
     * 获取符合条件的待打币记录
     *
     * @param netBalance
     * @param autoDownloadAmountLimit
     * @return
     */
    public List<DownloadBean> getCanAutoDownloadRecords(BigDecimal netBalance, BigDecimal autoDownloadAmountLimit, List<DownloadBean> list, long time, int fundstype) {
        //待打币记录
        List<DownloadBean> waitDownloadList = new ArrayList<>();
        //待打币总金额
        BigDecimal downloadAmount = BigDecimal.ZERO;

        Map<String, Integer> countMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            int count = 0;//打币次数
            DownloadBean downloadBean = list.get(i);
            if (downloadBean.getAmount().compareTo(autoDownloadAmountLimit) > 0) {//如果大于自动打币限制
                continue;
            }
            if(fundstype == 27){
                //接口地址
                String url = "https://api.eospark.com/api";
                //接口参数
                Map params = new HashMap();
                params.put("module","account");
                params.put("action","get_account_info");
                params.put("apikey",ApiConfig.getInstance().getValue("EOS_API_KEY"));
                params.put("account",downloadBean.getToAddress());
                String result = HttpRequestUtil.httpRequest(url, params);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if("0".equals(jsonObject.getString("errno"))){
                    if(jsonObject.getJSONObject("data").getString("create_timestamp") == null || "".equals(jsonObject.getJSONObject("data").getString("create_timestamp"))){
                        log.info("EOS提现地址错误");
                        continue;
                    }
                }else{
                    log.info("发送短信提醒失败，失败信息：" + jsonObject.getString("errmsg"));
                    continue;
                }
            }
            String currentDate = TimeUtil.getDateStr(new Timestamp(time), "yyyyMMdd");
            String userId = downloadBean.getUserId();
            String key = AUTO_DOWNLOAD_CONFIRMED_PRE + userId + "_" + currentDate;

            //加上已打币次数
            if (StringUtils.isNotBlank(Cache.Get(key))) {
                count = Integer.valueOf(Cache.Get(key));
            }
            //加上待打币次数
            if (countMap.containsKey(key)) {
                count += countMap.get(key);
            }
            if (count >= AUTO_DOWNLOAD_COUNT) {
                continue;
            }
            downloadAmount = downloadAmount.add(downloadBean.getAmount());

            //钱包余额-最小剩余额>待打币总额
            if (netBalance.compareTo(downloadAmount) > 0) {
                waitDownloadList.add(downloadBean);
                int tmpCount = 0;
                if (countMap.containsKey(key)) {
                    tmpCount += countMap.get(key);
                }
                countMap.put(key, ++tmpCount);
            } else {
                break;
            }
        }
        return waitDownloadList;
    }

    /**
     * 是否可以进行小额打币
     * 可以进行条件:1.任务为启用状态，2在时间范围内
     *
     * @return
     */
    public boolean canAutoDownload(Date date) {
        String className = AutoDownloadWorker.class.getSimpleName();//任务名称
        try {
            JobDefinBean jobDefinBean = jobDefinDao.getJobDefinBeanByJobClass(className, 1);
            if (null != jobDefinBean) {
                //添加当天时间
                String dateStr = TimeUtil.getDateStr(new Timestamp(System.currentTimeMillis()), "yyyy-MM-dd");

                //任务开始时间
                Date startTimeDate = TimeUtil.getStringToDate(dateStr + " " + jobDefinBean.getJobStartTime());
                //任务结束时间
                Date endTimeDate = TimeUtil.getStringToDate(dateStr + " " + jobDefinBean.getJobEndTime());

                if (endTimeDate.compareTo(startTimeDate) < 0) {
                    Date startTimeDate1 = TimeUtil.getAfterDayTime(startTimeDate, -1);
                    Date endTimeDate1 = endTimeDate;
                    if (date.compareTo(startTimeDate1) >= 0 && date.compareTo(endTimeDate1) <= 0) {
                        return true;
                    }
                    endTimeDate = TimeUtil.getAfterDayTime(endTimeDate, 1);
                }

                if (date.compareTo(startTimeDate) >= 0 && date.compareTo(endTimeDate) <= 0) {
                    return true;
                }

            }
        } catch (Exception e) {
            log.error("判断是否可以进行小额打币失败，错误信息：", e);
        }
        return false;
    }

    /**
     * 检查热提钱包余额，如果低于设定的最小值发送短信
     *
     * @param autoDownloadLimitAmountMap
     * @param hotWithdrawWallBalanceMap
     * @param accountWarnLimitMap
     */
    public void checkWarnBalanceMsg(Map<Integer, String> autoDownloadLimitAmountMap, Map<Integer, BigDecimal> hotWithdrawWallBalanceMap, Map<Integer, String> accountWarnLimitMap) {

        String coinNames = "";
        for (Map.Entry<Integer, String> entry : autoDownloadLimitAmountMap.entrySet()) {
            Integer key = entry.getKey();
            CoinProps coin = DatabasesUtil.coinProps(key);
            if (hotWithdrawWallBalanceMap.containsKey(key) && accountWarnLimitMap.containsKey(key)) {
                if (hotWithdrawWallBalanceMap.get(key).compareTo(new BigDecimal(accountWarnLimitMap.get(key))) > 0) {
                    continue;
                } else {
                    coinNames += coin.getDatabaseKey() + ",";
                }
            } else {
                coinNames += coin.getDatabaseKey() + ",";
            }
        }

        //判断热提钱包余额是否低于预警值
        if (coinNames.length() > 1) {
            try {
                coinNames = coinNames.substring(0, coinNames.length() - 1);
                String cont = coinNames + "热提钱包余额低于预警值,请及时充值。";
                //钉钉通知
                logAlarm.info("auto_download_notice:" + cont);
                if(noticePhoneList != null && noticePhoneList.size() > 0){
                    for(int i = 0 ; i < noticePhoneList.size(); i++){
                        sendPhone(noticePhoneList.get(i), cont);
                    }

                }
            } catch (Exception e) {
                log.error("发送短信提醒失败，失败信息：", e);
            }
        }
    }

    public void sendPhone(String sendPhoneNum, String cont) {
        Msg msg = new Msg();
        msg.setSysId(1);
        msg.setSendIp("127.0.0.1");
        msg.setUserId("0");
        msg.setUserName("0");
        msg.setTitle("小额打币提醒");
        msg.setCont(cont);
        msg.setReceivePhoneNumber("+86 " + sendPhoneNum);
        msg.setSendUserName("VIP");
        msg.setCodec(8);
        MsgUtil.sendSms(msg);
    }

}
