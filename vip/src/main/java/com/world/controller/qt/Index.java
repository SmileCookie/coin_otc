package com.world.controller.qt;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.world.model.dao.GuadanDao;
import com.world.model.dao.trace.EntrustDao;
import com.world.model.entity.Guadan;
import com.world.model.entity.Market;
import com.world.model.entity.record.QtTransRecord;
import com.world.model.entity.trace.Entrust;
import com.world.model.quanttrade.dao.QtTransRecordDao;
import com.world.util.sign.RSACoder;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by suxinjie on 2017/8/21.
 */
public class Index extends BaseAction {

    static Logger logger = Logger.getLogger(Index.class.getName());

    private QtTransRecordDao qtTransRecordDao = new QtTransRecordDao();
    private GuadanDao guadanDao = new GuadanDao();
    private EntrustDao entrustDao = new EntrustDao();

    /**
     * 获取量化用户和普通用户成交的订单信息
     */
    @Page(Viewer = JSON)
    public void getQtDealOrder() {

        try {
            // 加点防御措施
            String userId = param("userId");
            if (StringUtils.isBlank(userId)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(userId), priKey);//解密用户ID
            int userid = Integer.parseInt(new String(decodeUserId));

            List<QtTransRecord> list = qtTransRecordDao.find("select id,transRecordId,entrustId,entrustPrice,entrustNum,entrustType,entrustMarket,entrustQtUserId,entrustUserId,entrustStatus,addTime from qttransrecord where entrustStatus=0", null, QtTransRecord.class);
            if (CollectionUtils.isNotEmpty(list)) {
                String result = com.alibaba.fastjson.JSON.toJSONString(list);

                for (QtTransRecord tr : list) {
                    qtTransRecordDao.update("update qttransrecord set entrustStatus=1 where id=?", new Object[]{tr.getId()});
                }

                json("success", true, result, true);
                return;
            }

        } catch (Exception e) {
            logger.error("获取量化用户与普通用户成交单异常", e);
        }

        json("success", true, "[]", true);

    }

    /**
     * 获取用户的买一卖一信息
     */
    @Page(Viewer = JSON)
    public void getUserTicker() {

        try {
            // 加点防御措施
            String userId = param("userId");
            if (StringUtils.isBlank(userId)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(userId), priKey);//解密用户ID
            int userid = Integer.parseInt(new String(decodeUserId));

            String currentTab = param("market");
            JSONObject marketJson = Market.getMarketByName(currentTab);

            if (marketJson == null) {
                log.error("获取不到盘口信息，请检查参数 : market=" + currentTab);
                return;
            }

            String dbName = marketJson.getString("db");//数据库名称
            guadanDao.setDatabase(dbName);

            String buyOneSql = "SELECT unitPrice avgPrice, (numbers - completeNumber) numbers, userId, types isBuy FROM entrust WHERE userId = ? AND STATUS = 3 AND types = 1 ORDER BY unitPrice DESC LIMIT 1";
            Guadan buyOne = (Guadan) guadanDao.get(buyOneSql, new Object[]{userid}, Guadan.class);

            String sellOneSql = "SELECT unitPrice avgPrice, (numbers - completeNumber) numbers, userId, types isBuy FROM entrust WHERE userId = ? AND STATUS = 3 AND types = 0 ORDER BY unitPrice ASC LIMIT 1";
            Guadan sellOne = (Guadan) guadanDao.get(sellOneSql, new Object[]{userid}, Guadan.class);

            Map<String, Guadan> result = Maps.newHashMap();
            result.put("buyOne", buyOne);
            result.put("sellOne", sellOne);

            json("success", true, com.alibaba.fastjson.JSON.toJSONString(result), true);
            return;

        } catch (Exception e) {
            logger.error("获取用户的买一卖一信息异常", e);
        }

        json("success", true, "", true);

    }

    /**
     * 获取用户价格区间内的委托列表
     */
    @Page(Viewer = JSON)
    public void getUserOrdersByPrice() {

        try {
            // 加点防御措施
            String strUserId = param("userId");
            if (StringUtils.isBlank(strUserId)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(strUserId), priKey);//解密用户ID
            int userId = Integer.parseInt(new String(decodeUserId));

            String currentTab = param("market");
            BigDecimal lowPrice = decimalParam("lowPrice");
            BigDecimal highPrice = decimalParam("highPrice");
            boolean isBuy = booleanParam("isBuy"); // 1 true(买)

            JSONObject marketJson = Market.getMarketByName(currentTab);

            if (marketJson == null) {
                log.error("获取不到盘口信息，请检查参数 : market=" + currentTab);
                return;
            }

            String dbName = marketJson.getString("db");//数据库名称
            entrustDao.setDatabase(dbName);

            String sql;
            if (isBuy) {
                sql = "SELECT entrustId, unitPrice, (numbers - completeNumber) numbers, userId, types isBuy FROM entrust WHERE userId = ? AND STATUS = 3 AND types = 1 AND unitPrice >= ? AND unitPrice <= ? ORDER BY unitPrice DESC";
            } else {
                sql = "SELECT entrustId, unitPrice, (numbers - completeNumber) numbers, userId, types isBuy FROM entrust WHERE userId = ? AND STATUS = 3 AND types = 0 AND unitPrice >= ? AND unitPrice <= ? ORDER BY unitPrice ASC";
            }

            List<Entrust> results = entrustDao.find(sql, new Object[]{userId, lowPrice, highPrice}, Entrust.class);

            json("success", true, com.alibaba.fastjson.JSON.toJSONString(results), true);
            return;
        } catch (Exception e) {
            logger.error("获取用户价格区间内的委托列表异常", e);
        }

        json("success", true, "[]", true);

    }

    /**
     * 获取用户所有委托列表，包括买卖单
     */
    @Page(Viewer = JSON)
    public void getUserOrders() {

        try {
            // 加点防御措施
            String strUserId = param("userId");
            if (StringUtils.isBlank(strUserId)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(strUserId), priKey);//解密用户ID
            int userId = Integer.parseInt(new String(decodeUserId));

            String market = param("market");

            JSONObject marketJson = Market.getMarketByName(market);

            if (null == marketJson) {
                log.info("获取不到盘口信息，请检查参数 : market=" + market);
                return;
            }

            String dbName = marketJson.getString("db");//数据库名称
            entrustDao.setDatabase(dbName);

            String sql = "SELECT entrustId, unitPrice, (numbers - completeNumber) numbers, userId, types FROM entrust WHERE userId = ? AND STATUS = 3 ";

            List<Entrust> results = entrustDao.find(sql, new Object[]{userId}, Entrust.class);

            json("success", true, com.alibaba.fastjson.JSON.toJSONString(results), true);
            return;
        } catch (Exception e) {
            logger.error("获取用户委托列表异常", e);
        }

        json("success", true, "[]", true);

    }

    /**
     * 获取用户的买一卖一信息
     * <p>
     * 通过 市场, 买卖类型, 最高价, 最低价获取中价格之间(包含高低价格)的原始挂单(不合并,有的价格可能有多人挂单,需要分开返回)
     */
    @Page(Viewer = JSON)
    public void getOrdersByPrice() {

        try {
            // 加点防御措施
            String userId = param("userId");
            if (StringUtils.isBlank(userId)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(userId), priKey);//解密用户ID
            int userid = Integer.parseInt(new String(decodeUserId));

            String currentTab = param("market");
            BigDecimal lowPrice = decimalParam("lowPrice");
            BigDecimal highPrice = decimalParam("highPrice");
            boolean isBuy = booleanParam("isBuy"); // 1 true(买)


            JSONObject marketJson = Market.getMarketByName(currentTab);

            if (marketJson == null) {
                log.error("获取不到盘口信息，请检查参数 : market=" + currentTab);
                return;
            }

            String dbName = marketJson.getString("db");//数据库名称
            guadanDao.setDatabase(dbName);

            String sql;
            if (isBuy) {
                sql = "SELECT unitPrice avgPrice ,(numbers - completeNumber) numbers, userId, types isBuy FROM entrust WHERE unitPrice >= ? AND unitPrice <= ? AND STATUS = 3 AND types = 1 ORDER BY unitPrice DESC";
            } else {
                sql = "SELECT unitPrice avgPrice, (numbers - completeNumber) numbers, userId, types isBuy FROM entrust WHERE unitPrice >= ? AND unitPrice <= ? AND STATUS = 3 AND types = 0 ORDER BY unitPrice ASC";
            }

            List<Guadan> results = guadanDao.find(sql, new Object[]{lowPrice, highPrice}, Guadan.class);

            json("success", true, com.alibaba.fastjson.JSON.toJSONString(results), true);
            return;

        } catch (Exception e) {
            logger.error("获取用户的买一卖一信息异常", e);
        }

        json("success", true, "[]", true);

    }
}
