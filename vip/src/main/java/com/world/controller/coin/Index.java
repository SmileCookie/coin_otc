package com.world.controller.coin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.model.coin.service.CoinInfoService;
import com.world.model.entity.coin.CoinInfo;
import com.world.model.entity.coin.CoinProps;
import com.world.util.sign.RSACoder;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by buxianguan on 2018/2/2.
 */
public class Index extends BaseAction {
    private static Logger logger = Logger.getLogger(Index.class.getName());

    private CoinInfoService coinInfoService = new CoinInfoService();

    /**
     * 获取币种配置信息，运营后台重构使用，有校验
     */
    @Page(Viewer = JSON)
    public void getAllCoin() {
        JSONArray result = new JSONArray();
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

            Map<String, CoinProps> coinMap = DatabasesUtil.getNewCoinPropMaps();
            for (Map.Entry<String, CoinProps> entry : coinMap.entrySet()) {
                JSONObject coinJson = (JSONObject) JSONObject.toJSON(entry.getValue());
                result.add(coinJson);
            }
        } catch (Exception e) {
            logger.error("获取币种配置信息异常", e);
        }
        json("success", true, result.toString(), true);
    }


    /**
     * 获取币种配置信息.web
     */
    @Page(Viewer = JSON)
    public void getCoin() {
        JSONArray result = new JSONArray();
        try {
            Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
            for (Map.Entry<String, CoinProps> entry : coinMap.entrySet()) {
                JSONObject coinJson = new JSONObject();
                coinJson.put("name", entry.getValue().getPropTag());
                coinJson.put("code", entry.getValue().getFundsType());
                result.add(coinJson);
            }
        } catch (Exception e) {
            logger.error("获取币种配置信息异常", e);
        }

        json("success", true, result.toString(), true);
    }

    /**
     * 获取币种介绍.web
     * kinghao 20180815
     */
    @Page(Viewer = JSON)
    public void getCoinInfo() {
        JSONObject result = new JSONObject();
        String coinName = param("coinName");
        try {
            CoinInfo coinInfo = new CoinInfo();
            coinInfo.setCoinNameJson(coinName);
            coinInfo.setInternationalization(param("internationalization"));
            if (StringUtils.isBlank(coinInfo.getCoinNameJson()) ||
                    StringUtils.isBlank(coinInfo.getInternationalization())) {
                json(L("参数非法！"), false, "", true);
                return;
            }
            log.info("币种名："+coinInfo.getCoinNameJson()+",国际化："+coinInfo.getInternationalization());
            CoinInfo coin = coinInfoService.getInfoCoin(coinInfo);
            if (coin == null) {
                json(L("未获取到对应币种信息！"), false, "", true);
                return;
            }
            result = (JSONObject) JSONObject.toJSON(coin);
        } catch (Exception e) {
            logger.error("获取币种介绍信息异常！coin：" + coinName, e);
        }

        json("success", true, result.toString(), true);
    }


    public static void main(String[] args) throws Exception {
        String sige = "F0mDE3t5LQaRt84xEgARMe5NcB4HmDpIyfZ7WZDZC8AuKRkurQK5iHnDVtAe2wzXvM8yBUtn3c8Z\n6Db0e0idYd3QKmMOhT9bTpZRFp2RLyg0ONHxqVu3hetop9kGV7TPFZRVxU1WYZvYpJdq5/tM/c4d\nYpvXYrMqk8JTF5qyGJk=";
        String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
        byte[] decodeSign = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(sige), priKey);
        System.out.println(new String(decodeSign));
        System.out.println(RSACoder.encryptBASE64(RSACoder.encryptByPublicKey("snowman_web_boss".getBytes(), "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNvhXdl2tYI1ld+0FsvqtdXKnQOQG3ecBOwgvpbVXqV0UVRri6AHRL5a+FVQ557ArEqLl67414Fhjvh2e+gsLlecXvMJcuh/q8EEeVa5r9PmSHxTTk3iRmVIBUV5mnS8Frd1Vjdq3Opk2PZ8fzGU/R9qv1m9Enl1BdYrZnfLrmhwIDAQAB")));
    }
}
