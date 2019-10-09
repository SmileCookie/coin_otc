package com.world.model.dao.pay.charge;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.google.common.collect.Lists;
import com.kafka.ProducerSend;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.CapitalTransferApiService;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.pay.PayUserWalletDao;
import com.world.model.dao.user.EmailDao;
import com.world.model.dao.user.MobileDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.Price;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.KeyBean;
import com.world.model.entity.pay.PayUserWalletBean;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.entity.AddressType;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.model.financial.dao.FinanEntryDao;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.singleton.SingletonThreadPool;
import com.world.util.date.TimeUtil;
import com.yc.entity.SysGroups;
import com.yc.util.MsgUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * FIXME 代码太垃圾了实在无法忍受 renfei
 *
 * @author
 */
public class ChargeFacotry {
    static Logger log = Logger.getLogger(ChargeFacotry.class.getName());

    private DailyType dailyType;
    private CoinProps coin;
    PayUserWalletDao payDao = new PayUserWalletDao();
    FundsDao fundDao = new FundsDao();
    DetailsDao detailsDao = new DetailsDao();
    private CommAttrDao commAttrDao = new CommAttrDao();
    /*充值到账提醒手机号*/
    private List<String> noticePhoneList = null;
    private Map<String, Object> blockBrowserMap = null;

    public ChargeFacotry(DailyType dailyType, CoinProps coint) {
        super();
        this.dailyType = dailyType;
        this.coin = coint;
        this.detailsDao.setCoint(coint);
    }

    /**
     * 初始化配置信息
     */
    public void initConfig() {
        try {
            //获取手机号列表
            List<CommAttrBean> commAttrBeanList = commAttrDao.queryListByAttrTypeAndParaCode(AddressType.AUTO_RECHARGE_ACCOUNT_NOTICE.getKey(), "01");
            noticePhoneList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(commAttrBeanList)) {
                for (CommAttrBean commAttrBean : commAttrBeanList) {
                    noticePhoneList.add(commAttrBean.getParaValue());
                }
            }

            //获取各币种区块浏览器交易查询列表
            List<CommAttrBean> blockBlowserList = commAttrDao.queryListByAttrType(AddressType.BLOCKBROWSER_TX_QUERY.getKey());
            blockBrowserMap = new HashMap<String, Object>();
            if (CollectionUtils.isNotEmpty(blockBlowserList)) {
                for (CommAttrBean blockBlowser : blockBlowserList) {
                    blockBrowserMap.put(blockBlowser.getParaCode(), blockBlowser.getParaValue());
                }
            }

        } catch (Exception e) {
            log.error("获取充值到帐接受人员手机号初始化配置信息出错，错误信息：", e);
        }

    }

    public void refreshChargeTables() {
        try {
            //初始化信息
            initConfig();
            dealSync();
        } catch (Exception ex) {
            log.error(coin.getPropCnName() + "充值定时器执行出错", ex);
        }
    }

    public void dealSync() {
        List<DetailsBean> charges = detailsDao.find("select * from " + detailsDao.getTableName() + " where status = ? limit 0,30", new Object[]{Const.RechargeStatus.CONFIRMING}, DetailsBean.class);
        if (charges.size() > 0) {
            for (DetailsBean db : charges) {
                log.info(coin.getTag() + "开始处理 " + db.getDetailsId() + "的充值到账业务,当前确认数:" + db.getConfirmTimes() + ",交易流水:" + db.getAddHash());
                try {
                    syncTable(db);
                } catch (Exception ex) {
                    log.error(coin.getTag() + "充值定时器执行dealSync()出错，BtcCharge ID: " + db.getDetailsId(), ex);
                }
            }
        }
    }

    public void syncTable(DetailsBean lc) {
        List<OneSql> sqls = Lists.newArrayList();
        String userId = lc.getUserId();
        String userName = lc.getUserName();
        int fundsType = coin.getFundsType();
        if(coin.getPropTag().toLowerCase().equals("usdte")){
            fundsType = coin.getAgreement();
        }
        /*更新充值记录的用户信息*/
        if (StringUtils.isEmpty(userId) || "0".equals(userId)) {
            KeyBean lkb = new KeyBean();
            //验证当前处理币种时候是ERC20币种
            //true 校验地址为ETH地址库
            //false 校验地址为当前币种地址库

            Map<String, CoinProps> coinMaps = DatabasesUtil.getNewCoinPropMaps();//币种Map
            CoinProps coinProps = coinMaps.get(coin.getStag());
            if (coinProps.isERC()) {
                lkb = (KeyBean) Data.GetOne("select * from  ethKey where keyPre=? and wallet=?", new Object[]{lc.getToAddr(), lc.getWallet()}, KeyBean.class);
                /** start by kinghao 20190111 **/
            } else if ("eos".equals(coinProps.getDatabaseKey())) {
                lkb = (KeyBean) Data.GetOne("select * from " + coin.getStag() + "Key where keyPre=? and wallet=? and addressTag=?", new Object[]{lc.getToAddr(), lc.getWallet(), lc.getAddressTag()}, KeyBean.class);
                /**end*/
            } else {
                lkb = (KeyBean) Data.GetOne("select * from " + coin.getStag() + "Key where keyPre=? and wallet=?", new Object[]{lc.getToAddr(), lc.getWallet()}, KeyBean.class);
            }
            if (lkb != null) {
                userId = lkb.getUserId() + "";
                userName = lkb.getUserName();
            } else {
                //在我们的地址中没有找到接受地址   说明充值无效直接更新为已经同步 设置已失败
                log.info(coin.getTag() + "地址中没有找到接受地址,交易流水:" + lc.getAddHash() + ",detail id:" + lc.getDetailsId());
                Data.Update("update " + detailsDao.getTableName() + " set status=? where detailsId=?", new Object[]{Const.RechargeStatus.FAIL, lc.getDetailsId()});
                //更新充值记录汇总表
                Data.Update("update detailssummary set status=? where fundsType=? and detailsId=? ", new Object[]{Const.RechargeStatus.FAIL, fundsType, lc.getDetailsId()});
                return;
            }

            log.info(coin.getTag() + "更新用户userid:" + userId + ";用户名称:" + userName + ",detail id:" + lc.getDetailsId());
            sqls.add(new OneSql("update " + detailsDao.getTableName() + "  set userId=?, userName=? where detailsId=?", 1, new Object[]{userId, userName, lc.getDetailsId()}));
            //更新充值记录汇总表
            sqls.add(new OneSql("update detailssummary set userId=?, userName=? where fundsType=? and detailsId=? ", 1, new Object[]{userId, userName, fundsType, lc.getDetailsId()}));
            //by kinghao   ERC20币种使用ETH地址
            if (coinProps.isERC()) {
                sqls.add(new OneSql("update ethKey set usedTimes=usedTimes+? where keyId=?", 1, new Object[]{1, lkb.getKeyId()}));
            } else {
                sqls.add(new OneSql("update " + coin.getStag() + "Key set usedTimes=usedTimes+? where keyId=?", 1, new Object[]{1, lkb.getKeyId()}));
            }
        }

        //确认数未达到目标确认次数 则跳出任务  modify by renfei
        if (lc.getConfirmTimes() < coin.getInConfirmTimes()) {
            //fixme 如果跳出只更新上面修改用户id和用户名的代码,其实可以删掉,为了兼容错误用,代码实在太烂需要重构 renfei
            try {
                Data.doTrans(sqls);
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
            return;
        }

        /*如果为已确认状态，下面完成充值*/
        PayUserWalletBean payUser = payDao.getById(Integer.parseInt(userId), coin.getFundsType());
        if (payUser == null) {
            log.info("用户资金表不存在，充值失败。");
            return;
        }
        BigDecimal balance = payUser.getTotal().add(lc.getAmount());
        BigDecimal convertBtcAmount = Price.getCoinBtcPrice(coin.getStag()).multiply(lc.getAmount()).setScale(8, BigDecimal.ROUND_DOWN);
        Timestamp configTime = TimeUtil.getNow();
        //修改充值记录表 btcdetails
        sqls.add(new OneSql("update " + detailsDao.getTableName() + " set status=?,confirmTimes=?,configTime=?,banlance=? where DetailsId=? ", 1,
                new Object[]{Const.RechargeStatus.SUCCESS, lc.getConfirmTimes(), configTime, balance, lc.getDetailsId()}));

        //钱包账单流水结算记账时间
        Timestamp billTime = TimeUtil.getNow();
        //更新充值记录汇总表 detailssummary
        sqls.add(new OneSql("update detailssummary set billTime = ? , status=?, convertBtcAmount=?, confirmTimes=?,configTime=?,banlance=? where fundsType=? and detailsId=? ", 1,
                new Object[]{billTime,Const.RechargeStatus.SUCCESS, convertBtcAmount, lc.getConfirmTimes(), configTime, balance, fundsType, lc.getDetailsId()}));

        //用户增加资金的方法 pay_user  →  bill
        sqls.addAll(fundDao.addMoney(billTime,lc.getAmount(), lc.getSendTime(), lc.getAddHash(), userId, userName, coin.getTag() + "充值", BillType.recharge.getKey(), coin.getFundsType(), lc.getDetailsId(), BigDecimal.ZERO, "0", true));

        try {
            /*同步财务录入记录*/
            new FinanEntryDao().syncFinanAccount(sqls, coin, lc);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }


        if (Data.doTrans(sqls)) {
            /*Start by guankaili 20190107 我的钱包划转到币币 */
            UserCache.resetUserWalletFundsFromDatabase(userId);
/*            String url = ApiConfig.getValue("usecenter.url");
            FeignContainer container = new FeignContainer(url + "/capitalTransfer");
            CapitalTransferApiService capitalTransferApiService = container.getFeignClient(CapitalTransferApiService.class);
            Boolean flag = capitalTransferApiService.transfer(Const.pay_user_wallet, Const.pay_user_bg, lc.getAmount(), coin.getFundsType(), userId);*/
            if (true) {
                UserCache.resetUserFundsFromDatabase(userId);
                log.info("用户" + userName + "(" + userId + ")划转成功");
                new MainDailyRecordDao().insertOneRecord(dailyType, "用户" + userName + "成功充值" + lc.getAmount() + coin.getTag() + "，充值编号：" + lc.getDetailsId(), String.valueOf(0), "", TimeUtil.getNow(), Integer.parseInt(userId), lc.getAmount());
                /*start by xwz 20170626 充值加积分*/
                JifenManage jifenManager = new JifenManage(userId, 6, null, null, "VIP");//6：首次充值
                SingletonThreadPool.addJiFenThread(jifenManager);
                jifenManager = new JifenManage(userId, 7, lc.getAmount(), coin.getDatabaseKey(), "VIP");//7：充值
                SingletonThreadPool.addJiFenThread(jifenManager);
                /*end*/

                //start 2017.08.16 xzhang  充值成功发短信提醒
                try {
                    double amountdouble = round(lc.getAmount().doubleValue(), 6);
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
                    String key = "充值到账提醒内容";
                    String content = Lan.LanguageFormat(lan, key, userName, time, amountStr + "", coin.getTag());
                    if ("en".equals(lan) || (2 == mobileStatu && !MsgUtil.isContain(user.getUserContact().getSafeMobile()))) {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
                        time = sdf.format(new Date());
                        content = Lan.LanguageFormat("en", key, amountStr + "", coin.getTag(), time);
                    }
                    String ip = "";
                    if (2 == mobileStatu) {
                        //发短信
                        MobileDao mDao = new MobileDao();
                        mDao.sendSms(user, ip, Lan.Language(lan, "充值到账提醒"), content, user.getUserContact().getSafeMobile());
                    } else {
                        //发邮箱
                        EmailDao eDao = new EmailDao();
                        String title = Lan.Language(lan, SysGroups.vip.getValue()) + " " + Lan.Language(lan, "充值到账提醒");
                        content = eDao.getAutoSendEmailHtml(lan, user.getUserName(), content);
                        eDao.sendEmail(ip, userId, user.getUserName(), title, content, user.getUserContact().getSafeEmail());
                    }
                    /*Start by gkl 充值到帐后发送短信到手机 后期会删除*/
                    //发短信
                    try {
                        MobileDao mDao = new MobileDao();
                        //钉钉通知
                        String txQueryAdd = "";
                        if (blockBrowserMap != null && blockBrowserMap.get("" + coin.getFundsType()) != null) {
                            txQueryAdd = (String) blockBrowserMap.get("" + coin.getFundsType());
                            txQueryAdd = txQueryAdd == null ? "" : txQueryAdd.replace("blockHeight", "" + lc.getBlockHeight());
                        }
//                        String txId = lc.getAddHash() == null ? "" : lc.getAddHash().split("_")[0];
                        String czdzInfoDingDing = "【用户 " + user.getUserName()
                                + " ,用户编号 " + userId
                                + " ,于 " + TimeUtil.getDateToString(new Date())
                                + " 充值到账 " + coin.getTag()
                                + " 币 " + lc.getAmount()
                                + " 个,热充地址为 " + lc.getToAddr()
                                + " ,区块地址直达链接: " + txQueryAdd + lc.getToAddr() + " 】";

                        log.info("20190409CZDZ【充值到账】 " + czdzInfoDingDing);
//                        noticePhoneList.forEach(noticePhone -> {
//                            mDao.sendSms(user, ip, Lan.Language(lan, "充值到账提醒"), finalContent, "+86 ".concat(noticePhone));
//                        });

                        String finalContent = "用户：" + user.getUserName()
                                + ",用户ID【" + userId + "】，于"
                                + TimeUtil.getDateToString(new Date())
                                + "充值到账【" + lc.getAmount()
                                + "】【" + coin.getTag() + "】";
                        for (String noticePhone : noticePhoneList) {
                            mDao.sendSms(user, ip, Lan.Language(lan, "充值到账提醒"), finalContent, "+86 ".concat(noticePhone));
                        }

                    } catch (Exception e) {
                        log.error("发送小额打币提醒失败，错误信息：", e);
                    }
                    /*End*/
                } catch (Exception e) {
                    log.error("【充值到账提醒】用户" + userId + "充值到账提醒异常，异常信息为：", e);
                }
                //end
                log.info("充值成功：" + lc.getDetailsId());

            } else {
                log.info("用户" + userName + "(" + userId + ")划转失败");
            }
            /*End*/

            /*Start by guankaili 20190516 用户充值到账埋点 */
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("recharge", lc.getAmount().toPlainString());
            jsonObject.put("sendTime", configTime);
            jsonObject.put("fundsType", coin.getFundsType());
            ProducerSend producerSend = new ProducerSend();
            producerSend.sendMessage("trading", jsonObject.toString());
            log.info("推送驾驶舱用户充值到账埋点成功：" + jsonObject.toString());
            /*end*/

        } else {
            log.info("充值失败：" + lc.getDetailsId());
        }
    }

    /**
     * 四舍五入操作
     *
     * @param d   要操作的数
     * @param len 保留几位有效数字
     * @return
     */
    public static double round(double d, int len) {
        BigDecimal b1 = new BigDecimal(d);
        BigDecimal b2 = new BigDecimal("1");
        // 任何一个数字除以1都是原数字
        // ROUND_HALF_UP是BigDecimal的一个常量,表示进行四舍五入的操作
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static void main(String[] args) {
//        Map<String, CoinProps> coinMaps = DatabasesUtil.getCoinPropMaps();//币种Map
//        KeyBean lkb = new KeyBean();
//
//        CoinProps coinProps = coinMaps.get("gbc");
//        if (coinProps.isERC()) {
//            String currency = "eth";
//            System.out.println(currency);
//
//        }

        List<String> receivePhones = Arrays.asList("+86 13615325522", "+86 13589220047", "+86 15165328416");
        receivePhones.forEach(receivePhone -> {
            System.out.println(receivePhone);
        });


    }
}
