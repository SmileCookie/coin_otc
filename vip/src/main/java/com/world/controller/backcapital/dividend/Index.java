package com.world.controller.backcapital.dividend;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.backcapital.constant.BackCapitalConst;
import com.world.model.backcapital.service.BackCapitalService;
import com.world.model.entity.backcapital.BackCapitalConfig;
import com.world.model.entity.backcapital.Dividend;
import com.world.model.entity.backcapital.DividendHistory;
import com.world.model.entity.backcapital.CountDownInfo;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * Created by buxianguan on 18/3/10.
 */
public class Index extends BaseAction {
    private final static Logger log = Logger.getLogger(Index.class);

    private BackCapitalService backCapitalService = new BackCapitalService();

    /**
     * 获取回购倒计时
     */
    @Page(Viewer = JSON)
    public void bcCountDown() {
        try {
            JSONObject result = new JSONObject();

            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                json("出现异常", false, "", true);
                return;
            }

            //回购频率
            int frequency = config.getBcFrequency();
            result.put("frequency", frequency);

            //倒计时
            long countDown = backCapitalService.getCountDown(frequency, BackCapitalConst.BACK_CAPITAL_TIME_CACHE_KEY);
            result.put("countDown", countDown);

            //最近一次回购量
            BigDecimal lastBackCapitalNumer = backCapitalService.getLastBackCapitalNumbers();
            result.put("lastBackCapitalNumber", lastBackCapitalNumer);

            //回购平均速度，单位 GBC/M
            BigDecimal avgSpeed = (BigDecimal) Cache.GetObj(BackCapitalConst.BACK_CAPITAL_AVG_SPEED_CACHE_KEY);
            if (null == avgSpeed) {
                avgSpeed = BigDecimal.ZERO;
            }
            result.put("avgSpeed", avgSpeed);

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    /**
     * 获取回购提现倒计时
     */
    @Page(Viewer = JSON)
    public void withdrawCountDown() {
        try {
            JSONObject result = new JSONObject();

            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                json("出现异常", false, "", true);
                return;
            }

            //倒计时
            CountDownInfo countDownInfo = backCapitalService.getWithdrawCountDown(config);
            long frequency = countDownInfo.getFrequency();
            //倒计时时间加1秒，防止倒计时结束定时任务还未执行
            long countDown = countDownInfo.getCountDown() + 1;
            if (countDown < 0) {
                countDown = 0;
            }
            if (countDown > frequency) {
                countDown = frequency;
            }
            result.put("countDown", countDown);

            //提现频率
            result.put("frequency", frequency);

            //最近一次提现数量
            BigDecimal lastWithdrawAmount = backCapitalService.getLastWithdrawAmount(config);
            result.put("lastWithdraw", lastWithdrawAmount);

            //平台GBC余额，可用于提现的数量
            BigDecimal withdrawAmount = backCapitalService.getWithdrawAmount(config);
            result.put("balance", withdrawAmount);

            //分红地址余额
            BigDecimal addressBalance = BigDecimal.ZERO;
            Dividend dividend = backCapitalService.getDividend();
            if (null != dividend) {
                addressBalance = dividend.getBalance();
            }

            //月累计回购GBC量:可用提现数量+分红地址余额，为了保证页面显示小数位正确，把两个金额按照两位截取后相加
            result.put("totalBalance", withdrawAmount.setScale(2, BigDecimal.ROUND_DOWN).add(addressBalance.setScale(2, BigDecimal.ROUND_DOWN)));

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    @Page(Viewer = JSON)
    public void getDividendInfo() {
        try {
            JSONObject result = new JSONObject();

            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                json("出现异常", false, "", true);
                return;
            }

            //分红地址
            String address = config.getWithdrawAddress();
            result.put("address", address);

            //分红地址区块链查看链接
            String webUrl = config.getWebUrl();
            result.put("webUrl", webUrl);

            //分红地址余额
            Dividend dividend = backCapitalService.getDividend();
            if (null != dividend) {
                result.put("balance", dividend.getBalance());
            } else {
                result.put("balance", 0);
            }

            //分红次数
            int number = 1;
            DividendHistory dividendHistory = backCapitalService.getLastDividendHistory();
            if (dividendHistory.getId() > 0) {
                number = dividendHistory.getNumber() + 1;
            }
            result.put("number", number);

            //分红倒计时
            CountDownInfo countDownInfo = backCapitalService.dividendCountDown(config.getWithdrawFrequency());
            long countDown = countDownInfo.getCountDown();
            if (countDown < 0) {
                countDown = 0;
            }
            result.put("countDown", countDown);

            //分红频率
            result.put("frequency", countDownInfo.getFrequency());

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    @Page(Viewer = JSON)
    public void getDividendHistory() {
        try {
            JSONObject result = new JSONObject();

            BackCapitalConfig config = backCapitalService.getConfig();
            if (null == config) {
                json("出现异常", false, "", true);
                return;
            }

            //已转化股份
            int shareCount = 0;
            Dividend dividend = backCapitalService.getDividend();
            if (null != dividend) {
                shareCount = dividend.getTotalShareCount();
            }
            result.put("shareCount", shareCount);

            //全球已燃烧
            result.put("burnedAmount", shareCount * 1000);

            BigDecimal lastPerShareAmount = BigDecimal.ZERO;
            BigDecimal lastDividendAmount = BigDecimal.ZERO;
            DividendHistory dividendHistory = backCapitalService.getLastDividendHistory();
            if (dividendHistory.getId() > 0) {
                lastDividendAmount = dividendHistory.getAmount();
                lastPerShareAmount = lastDividendAmount.divide(new BigDecimal(dividendHistory.getShareCount()), 9, BigDecimal.ROUND_DOWN);
            }
            //最近一次分红每股获得GBC数量
            result.put("lastPerShareAmount", lastPerShareAmount);
            //最近一次分红量
            result.put("lastDividendAmount", lastDividendAmount);
            //累计分红量
            BigDecimal totalAmount = backCapitalService.getTotalDividendAmout();
            result.put("totalDividendAmount", totalAmount);

            //私钥分布坐标
            String strCoords = backCapitalService.getCoordsList();
            result.put("privateKeyCoords", JSONObject.parseArray(strCoords));

            json("success", true, result.toJSONString(), true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    public static void main(String[] args) {
//        long dividendCountDown = -11100000;
//        System.out.println(dividendCountDown / 1000 / 60);

        Calendar cal = Calendar.getInstance();
        int frequency = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println(frequency);
    }
}