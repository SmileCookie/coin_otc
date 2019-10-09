package com.world.controller.backcapital.api;

import com.world.config.GlobalConfig;
import com.world.model.backcapital.dao.BackCapitalConfigDao;
import com.world.model.backcapital.service.BackCapitalService;
import com.world.model.entity.backcapital.BackCapitalConfig;
import com.world.util.sign.RSACoder;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * Created by buxianguan on 18/3/09.
 */
public class Index extends BaseAction {
    private final static Logger log = Logger.getLogger(Index.class);

    private BackCapitalService backCapitalService = new BackCapitalService();
    private BackCapitalConfigDao configDao = new BackCapitalConfigDao();

    /**
     * 切换回购手续费比例
     */
    @Page(Viewer = JSON)
    public void switchFeeRatio() {
        try {
            // 加点防御措施
            String sign = param("sign");
            if (StringUtils.isBlank(sign)) {
                json(L("参数非法！"), false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeSign = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(sign), priKey);//解密
            if (!"snowman_web_boss".equals(new String(decodeSign))) {
                json(L("参数非法！"), false, "", true);
                return;
            }

            int feeRatio = intParam("feeRatio");
            BackCapitalConfig config = configDao.getConfig();
            if (feeRatio == config.getFeeRatio()) {
                json(L("参数非法！"), false, "", true);
                return;
            }

            //转移资金
            boolean transferResult = backCapitalService.userFundsTransfer(config);
            if (transferResult) {
                backCapitalService.updateFeeRatio(config, feeRatio);
                json("success", true, "", true);
            } else {
                json("切换手续费失败！", false, "", true);
            }
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    @Page(Viewer = JSON)
    public void dividendAddress() {
        try {
            //验证ip是否合法
            if (!validateIpAccess()) {
                json("请求IP不合法，拒绝请求！", false, "", true);
                return;
            }

            String strBalance = GetPrama(0);
            log.info("balance:" + strBalance);
            if (StringUtils.isBlank(strBalance)) {
                log.error("【GBC分红】同步区块链分红地址余额，余额参数为空！");
                json("账户余额参数错误！", false, "", true);
                return;
            }

            BigDecimal balance = new BigDecimal(strBalance);
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                log.error("【GBC分红】同步区块链分红地址余额，余额参数小于0！");
                json("账户余额参数错误！", false, "", true);
                return;
            }

            balance = balance.divide(new BigDecimal(10).pow(8));
            backCapitalService.saveDividend(balance, -1);
            json("success", true, "", true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    @Page(Viewer = JSON)
    public void gbcShares() {
        try {
            //验证ip是否合法
            if (!validateIpAccess()) {
                json("请求IP不合法，拒绝请求！", false, "", true);
                return;
            }

            String strShareCount = GetPrama(0);
            log.info("totalShareCount:" + strShareCount);

            if (StringUtils.isBlank(strShareCount)) {
                log.error("【GBC分红】同步区块链已转化股份，股份参数为空！");
                json("已转化股份参数错误！", false, "", true);
                return;
            }
            int totalShareCount = Integer.parseInt(strShareCount);
            if (totalShareCount < 0) {
                log.error("【GBC分红】同步区块链已转化股份，股份参数小于0！");
                json("已转化股份参数错误！", false, "", true);
                return;
            }

            backCapitalService.saveDividend(null, totalShareCount);
            json("success", true, "", true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    @Page(Viewer = JSON)
    public void recentlyDividend() {
        try {
            //验证ip是否合法
            if (!validateIpAccess()) {
                json("请求IP不合法，拒绝请求！", false, "", true);
                return;
            }

            String uniqueKey = param("txid");
            String strAmount = param("totalDividends");
            int shareCount = intParam("totalShares");
            String strTime = param("dividendsTime");

            log.info("txid:" + uniqueKey + ",totalDividends:" + strAmount + ",totalShares:" + shareCount + ",dividendsTime:" + strTime);

            //校验txid
            if (StringUtils.isBlank(uniqueKey)) {
                log.error("【GBC分红】分红txid为空！");
                json("分红txid为空！", false, "", true);
                return;
            }
            //刚上线未分红的时候传递的值是0，不处理
            if ("0".equals(uniqueKey)) {
                json("success", true, "", true);
                return;
            }
            //上线后测试的分红数据，重启区块链服务时会推送，过滤掉
            if ("0x7cda71373ec459bc56f312757e24bf32d33be16b5a57907a7cf01a43ce0bfe34".equals(uniqueKey)) {
                json("success", true, "", true);
                return;
            }

            //校验分红GBC总量
            if (StringUtils.isBlank(strAmount)) {
                log.error("【GBC分红】同步区块链分红历史，分红GBC总量参数为空！");
                json("分红GBC总量为空！", false, "", true);
                return;
            }
            BigDecimal amount = new BigDecimal(strAmount);
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                log.error("【GBC分红】同步区块链分红历史，分红GBC总量参数小于0！");
                json("分红GBC总量小于0！", false, "", true);
                return;
            }
            amount = amount.divide(new BigDecimal(10).pow(8));

            //校验分红股数
            if (shareCount < 0) {
                log.error("【GBC分红】同步区块链分红历史，分红股数参数小于0！");
                json("分红股数小于0！", false, "", true);
                return;
            }

            //校验分红时间
            if (StringUtils.isBlank(strTime)) {
                log.error("【GBC分红】同步区块链分红历史，分红时间参数为空！");
                json("分红时间为空错误！", false, "", true);
                return;
            }
            long time = Long.parseLong(strTime);
            if (time <= 0) {
                log.error("【GBC分红】同步区块链分红历史，分红时间小于等于0！");
                json("分红时间小于等于0！", false, "", true);
                return;
            }

            backCapitalService.saveDividendHistory(uniqueKey, amount, shareCount, time * 1000);
            json("success", true, "", true);
        } catch (Exception ex) {
            log.error(ex, ex);
            json("出现异常", false, "", true);
        }
    }

    private boolean validateIpAccess() {
        String strCanIps = GlobalConfig.getValue("gbcDividendIps");
        if (StringUtils.isBlank(strCanIps)) {
            return false;
        }
        String[] canIps = strCanIps.split(",");
        String ip = ip();
        for (String canIp : canIps) {
            if (canIp.equals(ip)) {
                return true;
            }
        }
        log.info("【GBC分红】请求IP不合法，拒绝请求！ip:" + ip);
        return false;
    }
}