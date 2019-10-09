package com.world.controller;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.tenstar.HTTPTcp;
import com.tenstar.Info;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.tenstar.RecordMessage;
import com.world.cache.Cache;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.entity.LegalTenderType;
import com.world.model.entitys.summary.TransactionSummary;
import com.world.util.DigitalUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import javax.crypto.BadPaddingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;

/**
 * 交易相关的
 *
 * @author pc
 */
public class Entrust extends UserAction {
    public final static String userOperation = "userOperation_";

    private IndexServer server = new IndexServer(lan);

    @Page(Viewer = ".xml")
    public void index() {
    }

    /**
     * 委托
     */
    @Page(Viewer = JSON)
    public void doEntrust() {
        if (!IsLogin()) {
            toLogin();
            return;
        }
        String userId = userId(true, true);
        String safePwd = param("safePassword");
        if (StringUtils.isNotBlank(safePwd)) {
            try {
                safePwd = URLDecoder.decode(safePwd, "UTF-8");
            } catch (Exception e) {
                log.info("资金密码转义出错" + safePwd);
            }
        }

        Market m = Market.getMarkeByName(GetPrama(0));
        if (m == null) {
            json(L("错误的市场"), false, "", true);
            return;
        }

        String isBuyStr = param("isBuy");
        if (StringUtils.isBlank(isBuyStr)) {
            json("参数错误", false, "");
            return;
        }
        if (!"0".equals(isBuyStr) && !"1".equals(isBuyStr)) {
            json("参数错误", false, "");
            return;
        }
        int isBuy = intParam("isBuy");


        if (StringUtils.isNotBlank(Cache.Get(userOperation + userId))) {
            json(L("您的账户存在问题，已禁止交易。如有问题请与客服联系"), false, "");
            return;
        }

        try {
            int userid = userId();
            /*Start by guankaili 20181120 B029优化买入卖出逻辑 */
            if (!safePwdCheck(safePwd, userId, m.market, isBuy)) {
//                json(L("资金密码输入有误。"), false, "");
                return;
            }
            /*end*/
            double unitPrice = DigitalUtil.roundDown(doubleParam("unitPrice"), m.exchangeBixDian);
            double number = DigitalUtil.roundDown(doubleParam("number"), m.numberBixDian);
            double triggerPrice = DigitalUtil.roundDown(doubleParam("triggerPrice"), m.exchangeBixDian);

            String isReal = GetPrama(1);
            boolean isPlan = isReal.equals("false");//true代表是计划委托
            Message myObj = new Message();
            myObj.setUserId(userid);

            String userAgent = request.getHeader("user-agent").toLowerCase();

            if(userAgent.indexOf("micromessenger")!= -1 || userAgent.indexOf("android") != -1 || userAgent.indexOf("iphone") != -1){
                myObj.setWebId(5);
            }else if( userAgent.indexOf("ipad") != -1){
                //苹果pad
                myObj.setWebId(9);
            }else{
                //pc端
                myObj.setWebId(8);
            }

            myObj.setNumbers(BigDecimal.valueOf(number));

            myObj.setTypes(isBuy);
            myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
            myObj.setStatus(isPlan ? 1 : 0);//0代表真实委托   1代表计划委托
            myObj.setTriggerPrice(BigDecimal.valueOf(triggerPrice));//计划委托触发价
            myObj.setMarket(m.market);//市场名称

			/*start by xwz 20170913 下单数量不能小于最小交易单位*/
            BigDecimal bixMinNum = BigDecimal.valueOf(m.bixMinNum);
            BigDecimal bixMaxNum = BigDecimal.valueOf(m.bixMaxNum);
            if (bixMinNum.compareTo(BigDecimal.ZERO) <= 0) {
                bixMinNum = BigDecimal.ONE;
            }
            if (bixMaxNum.compareTo(BigDecimal.ZERO) <= 0) {
                bixMaxNum = BigDecimal.ONE;
            }
            if (BigDecimal.valueOf(number).compareTo(bixMinNum) < 0) {
                json(Lan.LanguageFormat(lan, "委托失败-数量小于系统规定数量。", new String[]{m.bixMinNum + "", m.numberBi.toUpperCase()}), false, "", true);
                return;
            }
            if (BigDecimal.valueOf(number).compareTo(bixMaxNum) > 0) {
                json(Lan.LanguageFormat(lan, "委托失败-数量大于系统规定数量。", new String[]{m.bixMaxNum + "", m.numberBi.toUpperCase()}), false, "", true);
                return;
            }
            /*end*/

            try {
                Message rtn2 = null;
                if (m.listenerOpen) {
                    rtn2 = server.Entrust(myObj, m);
                } else {
                    String serverPath = "/server/entrust";
                    String param = HTTPTcp.ObjectToString(myObj);
                    String rtn = HTTPTcp.Post(m.ip, m.port, serverPath, param);
                    rtn2 = (Message) HTTPTcp.StringToObject(rtn);
                }

                json(L(rtn2.getMessage()), rtn2.getStatus() == Info.DoEntrustSuccess.getNum(), "{\"code\" :" + rtn2.getStatus() + "}", true);
            } catch (Exception ex2) {
                log.error("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                json(L("委托失败。"), false, "", true);
            }

        } catch (Exception ex) {
            log.error("委托失败。", ex);
        }
    }

    /**
     * 批量委托挂单-新运营后台
     * <p>
     * create by chendi
     */
    @Page(Viewer = JSON)
    public void doEntrustBatch() {
        String sign = param("sign");
        String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
        try {
            byte[] decodeSign = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(sign), priKey);//解密
            if (!"snowman_web_boss".equals(new String(decodeSign))) {
                json("参数非法！", false, "", true);
                return;
            }
            int userId = intParam("userId");
            String type = param("type"); //0:卖出 1:买入
            String market = param("market");
            Market m = Market.getMarkeByName(market);
            if (m == null) {
                json("错误的市场", false, "");
                return;
            }
            //总下单数量
            BigDecimal count = decimalParam("count");
            BigDecimal price = decimalParam("price");
            //每单的最低数量和最高数量
            Message myObj = new Message();
            myObj.setUserId(userId);
            myObj.setWebId(m.webId);
            myObj.setTypes(Integer.parseInt(type));
            myObj.setStatus(0);//0代表真实委托   1代表计划委托
            myObj.setMarket(m.market);//市场名称
            myObj.setNumbers(count);
            myObj.setUnitPrice(price);
            try {
                Message rtn2 = null;//(Message)HTTPTcp.StringToObject(rtn);
                if (m.listenerOpen) {
                    rtn2 = server.Entrust(myObj, m);
                } else {
                    String serverPath = "/server/entrust";//正常委托
                    String param = HTTPTcp.ObjectToString(myObj);
                    String rtn = HTTPTcp.Post(m.ip, m.port, serverPath, param);
                    rtn2 = (Message) HTTPTcp.StringToObject(rtn);
                }
                if (rtn2.getStatus() == Info.DoEntrustSuccess.getNum()) {
                    json("委托成功", true, "", true);
                } else {
                    json(rtn2.getMessage(), false, "", true);
                }
            } catch (Exception ex2) {
                log.error("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                json("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！", false, "", true);
            }
        } catch (Exception e) {
            log.error("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！", e);
            json("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！", false, "", true);
        }
        return;
    }


    /**
     * 随机两个bigDecimal区间中的值，闭区间
     *
     * @param start 开始
     * @param end   结束
     */
    public static BigDecimal getRandomBigDecimal(BigDecimal start, BigDecimal end) {
        //计算两个值的差值
        BigDecimal sub = end.subtract(start);
        int scale = sub.scale();
        //随机区间为左开右闭，为了能取到最右值，在差值后面加上相应小数位
        sub = sub.add(BigDecimal.valueOf(1 / Math.pow(10, scale)));

        //差值跟[0.0 - 1.0)随机数相乘，然后加上start，即为start和end之间的随机数，注意精度的设置
        double randomResult = RandomUtils.nextDouble() * sub.doubleValue();

        BigDecimal result = start.add(BigDecimal.valueOf(randomResult).setScale(scale, BigDecimal.ROUND_DOWN));
        return result;
    }


    /**
     * 委托
     * <p>
     * Close By suxinjie 一期屏蔽该功能
     */
    @Page(Viewer = JSON)
    public void doEntrustMore() {
        if (!IsLogin()) {
            toLogin();
            return;
        }
        Market m = Market.getMarkeByName(GetPrama(0));
        if (m == null) {
            json(L("错误的市场"), false, "", true);
            return;
        }

        int type = intParam("isbuy");//0 卖出  1 买入

        String safePwd = param("safePassword");
        if (StringUtils.isNotBlank(safePwd)) {
            try {
                safePwd = URLDecoder.decode(safePwd, "UTF-8");
            } catch (Exception e) {
                log.info("资金密码转义出错" + safePwd);
            }
        }
        String userId = userId(true, true);
        if (!safePwd(safePwd, userId, m.market, type)) {
            return;
        }

        try {
            int userid = userId();
            int webid = m.webId;

            double minPrice = DigitalUtil.roundDown(doubleParam("priceLow"), m.exchangeBixDian);
            double maxPrice = DigitalUtil.roundDown(doubleParam("priceHigh"), m.exchangeBixDian);
            double counts = DigitalUtil.roundDown(doubleParam("numbers"), m.numberBixDian);

            if (maxPrice - minPrice <= 0) {
                json(L("最高价必须大于最低价"), false, "", true);
                return;
            }

			/*long priceLow=DigitalUtil.longMultiply(minPrice, m.exchangeBixShow);
            long priceHigh=DigitalUtil.longMultiply(maxPrice, m.exchangeBixShow);
			long numbers=DigitalUtil.longMultiply(counts, m.numberBixShow);
			*/
//			long priceLow = Long.parseLong(param("priceLow").equals("") ? "0"
//					: param("priceLow"));// 如果有type可能会部分失效
//			long priceHigh = Long.parseLong(param("priceHigh").equals("") ? "0"
//					: param("priceHigh"));// 如果有type可能会部分失效

//			long numbers=Long.parseLong(param("numbers").equals("") ? "0"
//					: param("numbers"));// 如果有type可能会部分失效
            if (counts <= 0 || minPrice <= 0 || maxPrice <= 0) {
                json(L("输入有误，请重新输入!"), false, "", true);
                return;
            }

			/*start by xwz 20170913 下单数量不能小于最小交易单位  update by kinghao 添加最大交易量限制*/
            BigDecimal bixMinNum = BigDecimal.ONE;
            BigDecimal bixMaxNum = BigDecimal.ONE;
            try {
                bixMinNum = new BigDecimal(m.bixMinNum + "");
                if (bixMinNum == null || bixMinNum.compareTo(BigDecimal.ZERO) <= 0) {
                    bixMinNum = BigDecimal.ONE;
                }
                bixMaxNum = new BigDecimal(m.bixMaxNum + "");
                if (bixMaxNum == null || bixMaxNum.compareTo(BigDecimal.ZERO) <= 0) {
                    bixMaxNum = BigDecimal.ONE;
                }
            } catch (Exception e) {
            }
            if (BigDecimal.valueOf(counts).compareTo(bixMinNum) < 0) {
                /*start by gkl 20190408国际化修改*/
//                json(Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMinNum + "", m.numberBi.toUpperCase()}), false, "", true);
                json(Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.bixMinNum + "", m.numberBi.toUpperCase()}), false, "", true);
                /*end*/
                return;
            }
            if (BigDecimal.valueOf(counts).compareTo(bixMaxNum) > 0) {
                /*start by gkl 20190408国际化修改*/
//                json(Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMaxNum + "", m.numberBi.toUpperCase()}), false, "", true);
                json(Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.bixMaxNum + "", m.numberBi.toUpperCase()}), false, "", true);
                /*end*/
                return;
            }
            /*end*/


            //这里何用cancle这个
            MessageCancle myObj = new MessageCancle();
            myObj.setUserId(userid);
            myObj.setWebId(webid);
            myObj.setPriceLow(BigDecimal.valueOf(minPrice));
            myObj.setPriceHigh(BigDecimal.valueOf(maxPrice));
            myObj.setNumbers(BigDecimal.valueOf(counts));
            myObj.setType(type);
            myObj.setMarket(m.market);

            try {
                //String param = HTTPTcp.ObjectToString(myObj);
                //String rtn = HTTPTcp.Post(m.ip , m.port , "/entrustmore", param);
                //log.info(rtn);
                //MessageCancle rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                MessageCancle rtn2;
                if (m.listenerOpen) {
                    rtn2 = server.entrustmore(myObj, m);
                } else {
                    String param = HTTPTcp.ObjectToString(myObj);
                    String rtn = HTTPTcp.Post(m.ip, m.port, "/server/entrustmore", param);
                    log.info(rtn);
                    rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                }
                json(L(rtn2.getMessage()) + "", true, "", true);
            } catch (Exception ex2) {
                log.error("批量失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                json(L("批量失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
            }
        } catch (Exception ex) {
            log.error("批量失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex);
            json(L("批量失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
        }
    }

    /**
     * 取消
     */
    @Page(Viewer = JSON)
    public void cancle() {
        if (!IsLogin()) {
            toLogin();
            return;
        }
        Market m = Market.getMarkeByName(GetPrama(0));
        if (m == null) {
            json("", false, L("错误的市场"), true);
            return;
        }
        try {
            int userid = userId();
            int webid = m.webId;
            long entityId = Long.parseLong(GetPrama(1));

            String planType = GetPrama(2);

            log.info("取消订单，用户ID：" + userid + ",entityId: " + entityId + ",ip:" + ip() + ",planType:" + planType + ",resoureRequest:" + resoureRequest);

            MessageCancle myObj = new MessageCancle();
            myObj.setUserId(userid);
            myObj.setWebId(webid);

            myObj.setEntrustId(entityId);//
            myObj.setStatus(0);
            myObj.setMarket(m.market);
            try {

                MessageCancle rtn2 = new MessageCancle();

                if (m.listenerOpen) {
                    if (planType != null && planType.equals("true")) {
                        rtn2 = server.canclePlanEntrust(myObj, m);//计划委托取消
                    } else {
                        rtn2 = server.cancle(myObj, m);
                    }
                } else {
                    String serverPath = "/server/cancle";//正常委托取消
                    String param = HTTPTcp.ObjectToString(myObj);
                    if (planType != null && planType.equals("true")) {
                        serverPath = "/server/canclePlanEntrust";//计划委托取消
                    }
                    String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, serverPath, param);
                    rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                }

                json(L(rtn2.getMessage()), true, rtn2.getStatus() + "", true);


            } catch (Exception ex2) {
                log.error("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                json(L("撤销失败。"), false, "", true);
            }
        } catch (Exception ex) {
            log.error("撤销失败。", ex);
        }
    }

    /**
     * 取消
     */
    @Page(Viewer = JSON)
    public void canclemore() {
        int userid = userId();
        String cacheSyncKey = "cancelmore_entrust_" + userid;
        synchronized ("cancelmore_entrust_" + userid) {
            String lock = Cache.Get(cacheSyncKey);
            if (null != lock) {
                json(L("您的取消操作太频繁了，请稍后重试或刷新查看数据。"), false, "", true);
                return;
            }
            try {
                Cache.Set(cacheSyncKey, userid + "", 120);
                if (!IsLogin()) {
                    toLogin();
                    return;
                }
                Market m = Market.getMarkeByName(GetPrama(0));
                if (m == null) {
                    json("", false, L("错误的市场"), true);
                    return;
                }
                // String safePwd = param("payPass");
                // String userId = userId(true , true);
                // if(!safePwd(safePwd, userId)){
                // Write("",false,"请输入安全密码");
                // return;
                // }
                String planType = GetPrama(1);//是否计划委托

                int webid = m.webId;

                double minPrice = DigitalUtil.roundDown(doubleParam("minPrice"), m.exchangeBixDian);
                double maxPrice = DigitalUtil.roundDown(doubleParam("maxPrice"), m.exchangeBixDian);

                //long priceLow = DigitalUtil.longMultiply(minPrice, m.exchangeBixShow);
                //long priceHigh = DigitalUtil.longMultiply(maxPrice, m.exchangeBixShow);

                // 0
                // 按照区间设置
                // 1取消买入
                // 2取消卖出
                // 3
                // 取消所有
                int type = Integer.parseInt(param("types").equals("") ? "0" : param("types"));

                MessageCancle myObj = new MessageCancle();
                myObj.setUserId(userid);
                myObj.setWebId(webid);
                myObj.setPriceLow(BigDecimal.valueOf(minPrice));
                myObj.setPriceHigh(BigDecimal.valueOf(maxPrice));
                myObj.setType(type);
                myObj.setMessage(planType);
                myObj.setMarket(m.market);
                try {
                    MessageCancle rtn2 = new MessageCancle();

                    if (m.listenerOpen) {
                        rtn2 = server.cancelmore(myObj, m);
                    } else {
                        String param = HTTPTcp.ObjectToString(myObj);
                        String serverPath = "/server/cancelmore";
                        String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, serverPath, param);
                        rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                    }
                    json(rtn2.getStatus() + "", true, rtn2.getStatus() + "", true);

                } catch (Exception ex2) {
                    log.error("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                    json(L("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
                }
            } catch (Exception ex) {
                log.error("内部错误", ex);
            } finally {
                try {
                    Cache.Delete(cacheSyncKey);
                } catch (Exception e) {
                }
            }
        }
    }


    /**
     * 取消市场全部订单
     */
    @Page(Viewer = JSON)
    public void cancleALL() {
        String market = param("market");
        int userid = userId();
        if (userid != 1000351) {
            json(L("无权限"), false, "", true);
            return;
        }
        String cacheSyncKey = "cancelALl_entrust_" + market;
        synchronized ("cancelALl_entrust_" + market) {
            String lock = Cache.Get(cacheSyncKey);
            if (null != lock) {
                json(L("您的取消操作太频繁了，请稍后重试或刷新查看数据。"), false, "", true);
                return;
            }
            try {
                Cache.Set(cacheSyncKey, market + "", 120000);
                Market m = Market.getMarkeByName(market);
                if (m == null) {
                    json("", false, L("错误的市场"), true);
                    return;
                }
                try {
                    MessageCancle rtn2 = new MessageCancle();
                    if (m.listenerOpen) {
                        rtn2 = server.cancelAll(m);
                    } else {
                        String param = HTTPTcp.ObjectToString(m);
                        String serverPath = "/server/cancleAllServer";
                        String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, serverPath, param);
                        rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                    }
                    json(rtn2.getStatus() + "", true, rtn2.getStatus() + "", true);
                } catch (Exception ex2) {
                    log.error("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                    json(L("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
                }
            } catch (Exception ex) {
                log.error("内部错误", ex);
            } finally {
                try {
                    Cache.Delete(cacheSyncKey);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 取消全部计划委托
     *
     * @author zhanglinbo 20161024
     */
    @Page(Viewer = JSON)
    public void cancelmorePlanEntrust() {
        int userid = userId();
        String cacheSyncKey = "cancelmore_plan_entrust_" + userid;
        synchronized ("cancelmore_plan_entrust_" + userid) {
            String lock = Cache.Get(cacheSyncKey);
            if (null != lock) {
                //Write(L("您的取消操作太频繁了，请稍后重试或刷新查看数据。"), false, "");
                json(L("您的取消操作太频繁了，请稍后重试或刷新查看数据。"), false, "");
                return;
            }
            try {
                Cache.Set(cacheSyncKey, userid + "", 120);
                if (!IsLogin()) {
                    toLogin();
                    return;
                }
                Market m = Market.getMarkeByName(GetPrama(0));
                if (m == null) {
                    //Write("", false, L("错误的市场"));
                    json("", false, L("错误的市场"));
                    return;
                }
                String planType = "true";//是否计划委托

                int webid = m.webId;

                // 0
                // 按照区间设置
                // 1取消买入
                // 2取消卖出
                // 3
                // 取消所有
                int type = 0;

                MessageCancle myObj = new MessageCancle();
                myObj.setUserId(userid);
                myObj.setWebId(webid);
                myObj.setType(type);
                myObj.setMessage(planType);
                myObj.setMarket(m.market);
                try {
                    MessageCancle rtn2 = new MessageCancle();

                    if (m.listenerOpen) {
                        rtn2 = server.cancelmorePlanEntrust(myObj, m);
                    } else {
                        String param = HTTPTcp.ObjectToString(myObj);
                        String serverPath = "/server/cancelmorePlanEntrust";
                        String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, serverPath, param);
                        rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                    }
                    json(rtn2.getStatus() + "", true, rtn2.getStatus() + "", true);
                } catch (Exception ex2) {
                    log.error("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                    json(L("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
                }
            } catch (Exception ex) {
                log.error("内部错误", ex);
            } finally {
                try {
                    Cache.Delete(cacheSyncKey);
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 取消
     */
    @Page(Viewer = ".xml")
    public void dosyndata() {
        if (!IsLogin()) {
            toLogin();
            return;
        }
        Market m = Market.getMarkeByName(GetPrama(0));
        if (m == null) {
            Write("", false, L("错误的市场"));
            return;
        }
//		String safePwd = param("payPass");
//		String userId = userId(true , true);
//		if(!safePwd(safePwd, userId)){
//			Write("",false,"请输入安全密码");
//			return;
//		}

        try {
            int userid = userId();
            int webid = m.webId;

            RecordMessage myObj = new RecordMessage();


            try {
                RecordMessage rtn2 = new RecordMessage();
                if (m.listenerOpen) {
                    rtn2 = server.syndata(myObj);
                } else {
                    String param = HTTPTcp.ObjectToString(myObj);
                    String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, "/server/syndata", param);
                    rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
                }

                Write(rtn2.getStatus() + "", true, rtn2.getStatus() + "");

            } catch (Exception ex2) {
                log.error(ex2.toString(), ex2);
                Write(L("同步失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "");
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }


    /**
     * 内部测试委托
     */
    @Page(Viewer = JSON)
    public void doEntrustById() {

        Market m = Market.getMarkeByName(GetPrama(0));
        if (m == null) {
            json(L("错误的市场"), false, "", true);
            return;
        }

        int isBuy = intParam("isBuy");//Integer.parseInt(request.getParameter("isBuy"));
        String userId = request.getParameter("userId");
        if (StringUtils.isBlank(userId)) {
            json(L("参数非法，不能委托！"), false, "", true);
            return;
        }
        try {
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(userId), priKey);//解密用户ID
            int userid = Integer.parseInt(new String(decodeUserId));

            double unitPrice = DigitalUtil.roundDown(doubleParam("unitPrice"), m.exchangeBixDian);
            double number = DigitalUtil.roundDown(doubleParam("number"), m.numberBixDian);
            double triggerPrice = DigitalUtil.roundDown(doubleParam("triggerPrice"), m.exchangeBixDian);


            String isReal = GetPrama(1);
            boolean isPlan = isReal.equals("false");//true代表是计划委托
            Message myObj = new Message();
            myObj.setUserId(userid);
            myObj.setWebId(m.webId);
            myObj.setNumbers(BigDecimal.valueOf(number));

            myObj.setTypes(isBuy);
            myObj.setUnitPrice(BigDecimal.valueOf(unitPrice));
            myObj.setStatus(isPlan ? 1 : 0);//0代表真实委托   1代表计划委托
            myObj.setTriggerPrice(BigDecimal.valueOf(triggerPrice));//计划委托触发价
            myObj.setMarket(m.market);//市场名称

            try {
                Message rtn2 = null;//(Message)HTTPTcp.StringToObject(rtn);
                if (m.listenerOpen) {
                    rtn2 = server.Entrust(myObj, m);
                } else {
                    String serverPath = "/server/entrust";//正常委托

                    String param = HTTPTcp.ObjectToString(myObj);
                    String rtn = HTTPTcp.Post(m.ip, m.port, serverPath, param);
                    rtn2 = (Message) HTTPTcp.StringToObject(rtn);
                }

                json(L(rtn2.getMessage()), true, "{\"code\" :" + rtn2.getStatus() + ",\"id\" :" + rtn2.getNumbers() + "}", true);
            } catch (Exception ex2) {
                json(L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
            }

        } catch (Exception ex) {
            if (ex instanceof BadPaddingException) {
                json(L("参数非法，不能委托！"), false, "", true);
            } else {
                log.error(ex.toString(), ex);
            }
        }
    }


    /**
     * add by suxinjie 20170752 刷量需要
     */
    @Page(Viewer = JSON)
    public void cancleMoreById() {
        try {
            String userid = param("userId");
            if (StringUtils.isBlank(userid)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }

            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(userid), priKey);//解密用户ID
            userid = new String(decodeUserId);

            Market m = Market.getMarkeByName(param("market"));
            if (m == null) {
                json("", false, L("错误的市场"), true);
                return;
            }

            int webid = m.webId;

            double minPrice = DigitalUtil.roundDown(doubleParam("minPrice"), m.exchangeBixDian);
            double maxPrice = DigitalUtil.roundDown(doubleParam("maxPrice"), m.exchangeBixDian);

            // 0 按照区间设置
            // 1 取消买入
            // 2 取消卖出
            // 3 取消所有
            int type = Integer.parseInt(param("types").equals("") ? "0" : param("types"));

            MessageCancle myObj = new MessageCancle();
            myObj.setUserId(Integer.parseInt(userid));
            myObj.setWebId(webid);
            myObj.setPriceLow(BigDecimal.valueOf(minPrice));
            myObj.setPriceHigh(BigDecimal.valueOf(maxPrice));
            myObj.setType(type);
            myObj.setMessage("");
            myObj.setMarket(m.market);
            try {
                MessageCancle rtn2;

                if (m.listenerOpen) {
                    rtn2 = server.cancelmoreForBrush(myObj, m);
                } else {
                    String param = HTTPTcp.ObjectToString(myObj);
                    String serverPath = "/server/cancelmoreForBrush";
                    String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, serverPath, param);
                    rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                }
                json(rtn2.getStatus() + "", true, rtn2.getStatus() + "", true);

            } catch (Exception ex2) {
                log.error("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                json(L("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
            }
        } catch (Exception e) {
            log.error("内部错误", e);
        }
    }


    /**
     * 根据id列表撤单 刷量需要 add by buxianguan
     */
    @Page(Viewer = JSON)
    public void cancleByIds() {
        try {
            String userid = param("userId");
            if (StringUtils.isBlank(userid)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }

            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(userid), priKey);//解密用户ID
            userid = new String(decodeUserId);

            Market m = Market.getMarkeByName(param("market"));
            if (m == null) {
                json("", false, L("错误的市场"), true);
                return;
            }

            String ids = param("ids");

            Message myObj = new Message();
            myObj.setUserId(Integer.parseInt(userid));
            myObj.setMessage(ids);
            myObj.setMarket(m.market);
            try {
                Message rtn2;

                if (m.listenerOpen) {
                    rtn2 = server.cancelByIdsForBrush(myObj, m);
                } else {
                    String param = HTTPTcp.ObjectToString(myObj);
                    String serverPath = "/server/cancelByIdsForBrush";
                    String rtn = HTTPTcp.DoRequest2(true, m.ip, m.port, serverPath, param);
                    rtn2 = (Message) HTTPTcp.StringToObject(rtn);
                }
                json(rtn2.getStatus() + "", true, rtn2.getStatus() + "", true);

            } catch (Exception ex2) {
                log.info("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                json(L("取消失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
            }
        } catch (Exception e) {
            log.error("内部错误", e);
        }
    }

    /**
     * 批量委托
     * add by buxianguan 20170821 GBC刷量需要
     */
    @Page(Viewer = JSON)
    public void doEntrustMoreById() {
        try {
            String userId = param("userId");
            if (StringUtils.isBlank(userId)) {
                json(L("参数非法，不能委托！"), false, "", true);
                return;
            }

            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
            byte[] decodeUserId = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(userId), priKey);//解密用户ID
            int userid = Integer.parseInt(new String(decodeUserId));

            Market m = Market.getMarkeByName(GetPrama(0));
            if (m == null) {
                json("", false, L("错误的市场"), true);
                return;
            }

            int webid = m.webId;
            int type = intParam("isBuy");//0 卖出  1 买入

            double minPrice = DigitalUtil.roundDown(doubleParam("priceLow"), m.exchangeBixDian);
            double maxPrice = DigitalUtil.roundDown(doubleParam("priceHigh"), m.exchangeBixDian);
            double counts = DigitalUtil.roundDown(doubleParam("numbers"), m.numberBixDian);

            if (maxPrice - minPrice <= 0) {
                json(L("最高价必须大于最低价"), false, "", true);
                return;
            }

            if (counts <= 0 || minPrice <= 0 || maxPrice <= 0) {
                json(L("输入有误，请重新输入!"), false, "", true);
                return;
            }

            //这里何用cancle这个
            MessageCancle myObj = new MessageCancle();
            myObj.setUserId(userid);
            myObj.setWebId(webid);
            myObj.setPriceLow(BigDecimal.valueOf(minPrice));
            myObj.setPriceHigh(BigDecimal.valueOf(maxPrice));
            myObj.setNumbers(BigDecimal.valueOf(counts));
            myObj.setType(type);
            myObj.setMarket(m.market);

            try {
                MessageCancle rtn2;
                if (m.listenerOpen) {
                    rtn2 = server.entrustmore(myObj, m);
                } else {
                    String param = HTTPTcp.ObjectToString(myObj);
                    String rtn = HTTPTcp.Post(m.ip, m.port, "/server/entrustmore", param);
                    log.info(rtn);
                    rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
                }
                json(L(rtn2.getMessage()) + "", true, "", true);
            } catch (Exception ex2) {
                log.error("批量失败，交易大盘忙碌，请稍后再试，或者通知网站！", ex2);
                json(L("批量失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
            }
        } catch (Exception ex) {
            if (ex instanceof BadPaddingException) {
                json(L("参数非法，不能委托！"), false, "", true);
            } else {
                log.error(ex.toString(), ex);
            }
        }
    }


    /**
     * 进行计划委托 下单
     * 分离计划委托为独立方法 ，增加抄底追高买入 的计划委托和止盈止损的计划委托类型。
     *
     */
//	@Page(Viewer = JSON )
//	public void doPlanEntrust(){
//		if(!IsLogin()){
//		   toLogin();
//		   return;
//		}
//		log.info("here");
//
//		String safePwd = param("safePassword");
//		int adminId = adminId();
//
//		String currency = GetPrama(0);
//		Market m = Market.getMarkeByName(currency);
//		if (m == null) {
//			json(L("错误的市场"), false, "",true);
//			return;
//		}
//
//
//		//买卖类型 1：买 0：卖
//		int isBuy=intParam("isBuy");//Integer.parseInt(request.getParameter("isBuy"));
//
//		String userId = userId(true , true);
//
//		log.info("管理员ID：" + adminId);
//		if(adminId == 13 || adminId == 19){
//			log.info("管理员ID：" + adminId + "代用户委托");
//		}else{
//			if(!safePwd(safePwd, userId, m.market, isBuy)){
//				return;
//			}
//		}
//		try{
//		    int userid=userId();
//
//
//		    double buyOne = 0d;//当前盘口的买一价格
//		    double sellOne = 0d;//当前盘口的卖一价格
//		    String ticker =  Cache.Get(m.market + "_hotdata");//获取行情
//		    if(StringUtils.isNotEmpty(ticker)){
//		    	String[] arr = ticker.split(",");
//		    	buyOne = arr[1]==null?0:Double.parseDouble(arr[1]);//买一
//		    	sellOne = arr[2]==null?0:Double.parseDouble(arr[2]);//卖一
//		    }
//
//		    //价格参数获取
//		    double buyPlanMoney 		=0d;//计划委托金额
//		    double sellPlanNumber 		=0d; //计划委托数量（）
//
//			double buyPlanLowPrice 		=0d;  //抄底委托价
//			double buyPlanHighPrice 	=0d; //追高委托价
//			double buyTriggerLowPrice 	=0d;  //抄底触发价
//			double buyTriggerHighPrice 	=0d; //追高  触发价格
//
//			double sellTriggerHighPrice	=0d;//止盈触发价
//			double sellTriggerLowPrice 	=0d;//止损触发价
//			double sellPlanHighPrice 	=0d;//止盈委托价
//			double sellPlanLowPrice 	=0d;//止损委托价
//
//			if(isBuy==1){//计划买入
//				buyPlanMoney 			= DigitalUtil.roundDown(doubleParam("buyPlanMoney"), m.exchangeBixDian);//计划委托金额
//				buyTriggerHighPrice 	= DigitalUtil.roundDown(doubleParam("buyTriggerHighPrice"), m.exchangeBixDian);//追高  触发价格
//				buyTriggerLowPrice 		= DigitalUtil.roundDown(doubleParam("buyTriggerLowPrice"), m.exchangeBixDian);//抄底 触发价格
//				buyPlanHighPrice 		= DigitalUtil.roundDown(doubleParam("buyPlanHighPrice"), m.exchangeBixDian);//追高 委托价格
//				buyPlanLowPrice 		= DigitalUtil.roundDown(doubleParam("buyPlanLowPrice"), m.exchangeBixDian);//抄底委托价格
//				//参数校验判断 触发价和委托价不能为空
//				if((buyTriggerHighPrice>0 && buyPlanHighPrice<=0) || (buyTriggerHighPrice<=0 && buyPlanHighPrice>0)){
//					json(L("追高策略必须填写追高触发价和追高委托价，若不使用追高策略请留空。"),false,"",true);
//					return;
//				}
//				if((buyTriggerLowPrice>0 && buyPlanLowPrice<=0) || (buyTriggerLowPrice<=0 && buyPlanLowPrice>0)){
//					json(L("抄底策略必须填写抄底触发价和抄底委托价，若不使用抄底策略请留空。"),false,"",true);
//					return;
//				}
//				if(buyTriggerHighPrice<=0 && buyPlanHighPrice<=0 && buyTriggerLowPrice<=0 && buyPlanLowPrice<=0){
//					json(L("请至少填写一个追高触发价或抄底触发价。"),false,"",true);
//					return;
//				}else{
//					//触发价格与行情买卖一价格判断
//					if(buyTriggerHighPrice>0 && sellOne>0 && buyTriggerHighPrice<=sellOne){
//						json(L("追高触发价必须大于当前行情的卖一价"+sellOne ),false,"",true);
//						return;
//					}
//
//					if(buyTriggerLowPrice>0 && buyOne>0 && buyTriggerLowPrice>=buyOne){
//						json(L("抄底触发价必须小于当前行情的买一价"+buyOne ),false,"",true);
//						return;
//					}
//				}
//
//				if(buyPlanHighPrice >0 && BigDecimal.valueOf(buyPlanMoney).divide(BigDecimal.valueOf(buyPlanHighPrice), m.numberBixDian, BigDecimal.ROUND_DOWN).doubleValue()<=0){
//					json(L("委托失败，追高计划委托预计购买数量低于最小数量！"),false,"",true);
//					return;
//				}else if(buyPlanLowPrice>0 && BigDecimal.valueOf(buyPlanMoney).divide(BigDecimal.valueOf(buyPlanLowPrice), m.numberBixDian, BigDecimal.ROUND_DOWN).doubleValue()<=0){
//					json(L("委托失败，抄底计划委托预计购买数量低于最小数量！"),false,"",true);
//					return;
//				}
//
//			}else{//计划卖出
//				sellPlanNumber 			= DigitalUtil.roundDown(doubleParam("sellPlanNumber"), m.numberBixDian);//计划卖出数量
//				sellTriggerHighPrice 	= DigitalUtil.roundDown(doubleParam("sellTriggerHighPrice"), m.exchangeBixDian);//止盈触发价
//				sellTriggerLowPrice 	= DigitalUtil.roundDown(doubleParam("sellTriggerLowPrice"), m.exchangeBixDian);//止损触发价
//				sellPlanHighPrice 		= DigitalUtil.roundDown(doubleParam("sellPlanHighPrice"), m.exchangeBixDian);//止盈委托价
//				sellPlanLowPrice 		= DigitalUtil.roundDown(doubleParam("sellPlanLowPrice"), m.exchangeBixDian);//止损委托价格
//				//参数校验判断 触发价和委托价不能为空
//				if((sellTriggerHighPrice>0 && sellPlanHighPrice<=0) || (sellTriggerHighPrice<=0 && sellPlanHighPrice>0)){
//					json(L("止盈策略必须填写止盈触发价和止盈委托价。"),false,"",true);
//					return;
//				}
//				if((sellTriggerLowPrice>0 && sellPlanLowPrice<=0) || (sellTriggerLowPrice<=0 && sellPlanLowPrice>0)){
//					json(L("止损策略必须填写止损触发价和止损委托价。"),false,"",true);
//					return;
//				}
//
//				if(sellTriggerHighPrice<=0 && sellPlanHighPrice<=0 && sellTriggerLowPrice<=0 && sellPlanLowPrice<=0){
//					json(L("请至少填写一个止盈触发价或止损触发价。"),false,"",true);
//					return;
//				}else{
//					//触发价格与行情买卖一价格判断
//					if(sellTriggerHighPrice>0 && sellOne>0 && sellTriggerHighPrice<=sellOne){
//						json(L("止盈触发价必须大于当前行情的卖一价"+sellOne ),false,"",true);
//						return;
//					}
//
//					if(sellTriggerLowPrice>0 && buyOne>0 && sellTriggerLowPrice>=buyOne){
//						json(L("止损触发价必须小于当前行情的买一价"+buyOne ),false,"",true);
//						return;
//					}
//				}
//
//
//				if(BigDecimal.valueOf(sellPlanNumber).setScale(3, BigDecimal.ROUND_DOWN).doubleValue()<=0){
//					json(L("委托失败， 计划委托卖出数量低于最小数量！"),false,"",true);
//					return;
//				}
//
//			}
//
//			BigDecimal totalMoney			=BigDecimal.valueOf(buyPlanMoney);//计划委托总金额（买入）
//			BigDecimal number				=BigDecimal.valueOf(sellPlanNumber);//计划委托总数量（卖出）
//
//			BigDecimal unitPrice			= BigDecimal.ZERO;//计划委托 追高、止损委托价格
//			BigDecimal unitPriceProfit	=BigDecimal.ZERO;//计划委托 抄底、止盈委托价格
//
//			BigDecimal triggerPrice		=BigDecimal.ZERO;//计划委托 追高、止损触发价格
//			BigDecimal triggerPriceProfit	=BigDecimal.ZERO;//计划委托 抄底、止盈触发价格
//			if(isBuy==1){//计划买入
//				unitPriceProfit		= BigDecimal.valueOf(buyPlanLowPrice);
//				unitPrice			= BigDecimal.valueOf(buyPlanHighPrice);
//				triggerPriceProfit 	= BigDecimal.valueOf(buyTriggerLowPrice);
//				triggerPrice 		= BigDecimal.valueOf(buyTriggerHighPrice);
//			}else{
//				unitPrice 			= BigDecimal.valueOf(sellPlanLowPrice);
//				unitPriceProfit		= BigDecimal.valueOf(sellPlanHighPrice);
//				triggerPrice	 	= BigDecimal.valueOf(sellTriggerLowPrice);
//				triggerPriceProfit 	= BigDecimal.valueOf(sellTriggerHighPrice);
//			}
//
//
//			//String isReal=GetPrama(1);
//			//log.error("用户ID：" + userId + ",price: " + price + ",count:" + count + ",isBuy:" + isBuy + ",isPlan: true,ip:" + ip() + ",resoureRequest:" + resoureRequest);
//			Message myObj = new Message();
//	        myObj.setUserId(userid);
//	        myObj.setWebId(m.webId);
//	        myObj.setNumbers(number);//止盈止损总数量
//	        myObj.setMarket(m.market);
//	        myObj.setTypes(isBuy);//买卖方向
//	        myObj.setUnitPrice(unitPrice);//计划委托 追高、止损委托价格
//	        myObj.setStatus(1);//0代表真实委托   1代表计划委托
//	        myObj.setTriggerPrice(triggerPrice);//计划委托 追高、止损触发价格
//	        myObj.setUnitPriceProfit(unitPriceProfit);//计划委托  抄底、止盈委托价格
//	        myObj.setTriggerPriceProfit(triggerPriceProfit);//计划委托 抄底、止盈触发价格
//	        myObj.setTotalMoney(totalMoney);//追高抄底总金额
//
//
//	        try{
//
//	         Message rtn2 = null;//(Message)HTTPTcp.StringToObject(rtn);
//	   		  if(m.listenerOpen){
//
//	   				  rtn2 = server.planEntrust(myObj,m);
//	   		  }else{
//
//	   		   		String	  serverPath = "/server/planEntrust";
//
//
//	   	          String param=HTTPTcp.ObjectToString(myObj);
//	   		      String rtn=HTTPTcp.Post(m.ip,m.port,serverPath,param);
//	   		      rtn2 =(Message)HTTPTcp.StringToObject(rtn);
//	   		  }
//	   		  json(L(rtn2.getMessage()),true, "{\"code\" :" + rtn2.getStatus()+"}",true);
//	        }catch(Exception ex2){
//	        	json(L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"),false,"",true);
//	        }
//
//		}catch(Exception ex){
//			log.error(ex.toString(), ex);
//		}
//	}

    /**
     * 参数
     * isBuy=1
     * <p>
     * buyPlanMoney
     * buyTriggerPrice
     * buyPlanPrice
     * <p>
     * sellPlanNumber
     * sellTriggerPrice
     * sellPlanPrice
     * <p>
     * // TODO: 2017/5/23 suxinjie 需要删除的国际化信息
     * 1.
     * 2.
     * 3.
     * 4.
     * 5.
     */
    @Page(Viewer = JSON)
    public void doPlanEntrust() {
        if (!IsLogin()) {
            toLogin();
            return;
        }

        String currency = GetPrama(0);
        Market m = Market.getMarkeByName(currency);
        if (m == null) {
            json(L("错误的市场"), false, "", true);
            return;
        }

        //买卖类型 1：买 0：卖
        String isBuyStr = param("isBuy");
        if (StringUtils.isBlank(isBuyStr)) {
            json("参数错误", false, "");
            return;
        }
        if (!"0".equals(isBuyStr) && !"1".equals(isBuyStr)) {
            json("参数错误", false, "");
            return;
        }
        int isBuy = intParam("isBuy");

        String userId = userId(true, true);
        if (StringUtils.isNotBlank(Cache.Get(userOperation + userId))) {
            json(L("您的账户存在问题，已禁止交易。如有问题请与客服联系"), false, "");
            return;
        }

        try {
            int userid = userId();

            BigDecimal currencyPrice = BigDecimal.ZERO; //当前市场价格
            double buyOne = 0d; //当前盘口的买一价格
            double sellOne = 0d; //当前盘口的卖一价格
            String ticker = DishDataCacheService.getHotData(m.market);//获取行情
            if (StringUtils.isNotEmpty(ticker)) {
                String[] arr = ticker.split(",");
                currencyPrice = arr[0] == null ? BigDecimal.ZERO : new BigDecimal(arr[0]);//市场价格
//				buyOne                  = arr[1] == null ? 0 : Double.parseDouble(arr[1]);//买一
//				sellOne                 = arr[2] == null ? 0 : Double.parseDouble(arr[2]);//卖一
            }

            //价格参数获取
            double buyPlanMoney = 0d; //计划委托金额
            double sellPlanNumber = 0d; //计划委托数量（）

            double buyTriggerPrice = 0d; //买入触发价格
            double buyPlanPrice = 0d; //买入委托价格

            double sellTriggerPrice = 0d; //卖出触发价格
            double sellPlanPrice = 0d; //卖出委托价格


            if (isBuy == 1) {//计划买入
                buyPlanMoney = DigitalUtil.roundDown(doubleParam("buyPlanMoney"), m.exchangeBixDian);//计划委托金额
                buyTriggerPrice = DigitalUtil.roundDown(doubleParam("buyTriggerPrice"), m.exchangeBixDian);//触发价
                buyPlanPrice = DigitalUtil.roundDown(doubleParam("buyPlanPrice"), m.exchangeBixDian);//委托价

                if (buyTriggerPrice <= 0) {
                    json(L("请输入触发价格"), false, "", true);
                    return;
                }
                if (buyPlanPrice <= 0) {
                    json(L("请输入委托价格"), false, "", true);
                    return;
                }

				/*
                 * start by xwz 20170926 增加提示成交金额小于规定金额
				 * 后面的方法也有同样的判断
				 * 如果此处不加提示成交金额验证，提示信息不准确
				 */
                if (m.getMinAmount() > 0 && BigDecimal.valueOf(buyPlanMoney).compareTo(BigDecimal.valueOf(m.getMinAmount())) < 0) {
                    DecimalFormat df = new DecimalFormat("0.#########");
                    json(Lan.LanguageFormat(lan, "委托失败-成交金额小于系统规定金额", df.format(m.getMinAmount()) + m.exchangeBi.toUpperCase()), false, "", true);

                    return;
                }
				/*end*/

				/*start by xwz 20170913 下单数量不能小于最小交易单位*/
                BigDecimal buyNumber = BigDecimal.valueOf(buyPlanMoney).divide(BigDecimal.valueOf(buyPlanPrice), m.numberBixDian, BigDecimal.ROUND_DOWN);
                BigDecimal bixMinNum = BigDecimal.ONE;
                BigDecimal bixMaxNum = BigDecimal.ONE;
                try {
                    bixMinNum = new BigDecimal(m.bixMinNum + "");
                    if (bixMinNum == null || bixMinNum.compareTo(BigDecimal.ZERO) <= 0) {
                        bixMinNum = BigDecimal.ONE;
                    }
                    bixMaxNum = new BigDecimal(m.bixMaxNum + "");
                    if (bixMaxNum == null || bixMaxNum.compareTo(BigDecimal.ZERO) <= 0) {
                        bixMaxNum = BigDecimal.ONE;
                    }
                } catch (Exception e) {

                }

                if (buyNumber.compareTo(bixMinNum) < 0) {
                    /*start by gkl 20190408国际化修改*/
//                    json(Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMinNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    json(Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.bixMinNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    /*end*/
                    return;
                } if (buyNumber.compareTo(bixMaxNum) > 0) {
                    /*start by gkl 20190408国际化修改*/
//                    json(Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMaxNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    json(Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.bixMaxNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    /*end*/
                    return;
                }
				/*end*/


            } else {//计划卖出
                sellPlanNumber = DigitalUtil.roundDown(doubleParam("sellPlanNumber"), m.numberBixDian);//计划卖出数量
                sellTriggerPrice = DigitalUtil.roundDown(doubleParam("sellTriggerPrice"), m.exchangeBixDian);//触发价
                sellPlanPrice = DigitalUtil.roundDown(doubleParam("sellPlanPrice"), m.exchangeBixDian);//委托价

                if (sellTriggerPrice <= 0) {
                    json(L("请输入触发价格"), false, "", true);
                    return;
                }
                if (sellPlanPrice <= 0) {
                    json(L("请输入委托价格"), false, "", true);
                    return;
                }

				/* start by xwz 20170926 增加提示成交金额小于规定金额
				 * 后面的方法也有同样的判断
				 * 如果此处不加提示成交金额验证，提示信息不准确
				 */
                if (m.getMinAmount() > 0 && BigDecimal.valueOf(sellPlanNumber).multiply(BigDecimal.valueOf(sellPlanPrice)).compareTo(BigDecimal.valueOf(m.getMinAmount())) < 0) {
                    //防止double位数太多出现科学计数法  by Mark 2018/3/21
                    DecimalFormat df = new DecimalFormat("0.#########");
                    json(Lan.LanguageFormat(lan, "委托失败-成交金额小于系统规定金额", df.format(m.getMinAmount()) + m.exchangeBi.toUpperCase()), false, "", true);
                    return;
                }
				/*end*/

				/*start by xwz 20170913 下单数量不能小于最小交易单位
				update by kinghao 20181121 添加最大交易单位*/
                BigDecimal bixMinNum = BigDecimal.ONE;
                BigDecimal bixMaxNum = BigDecimal.ONE;
                try {
                    bixMinNum = new BigDecimal(m.bixMinNum + "");
                    if (bixMinNum == null || bixMinNum.compareTo(BigDecimal.ZERO) <= 0) {
                        bixMinNum = BigDecimal.ONE;
                    }
                    bixMaxNum = new BigDecimal(m.bixMaxNum + "");
                    if (bixMaxNum == null || bixMaxNum.compareTo(BigDecimal.ZERO) <= 0) {
                        bixMaxNum = BigDecimal.ONE;
                    }
                } catch (Exception e) {

                }

                if (BigDecimal.valueOf(sellPlanNumber).compareTo(bixMinNum) < 0) {
                    /*start by gkl 20190408国际化修改*/
//                    json(Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMinNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    json(Lan.LanguageFormat(lan, "委托失败-成交数量小于系统规定数量", new String[]{m.bixMinNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    /*end*/
                    return;
                }
                if (BigDecimal.valueOf(sellPlanNumber).compareTo(bixMaxNum) > 0) {
                    /*start by gkl 20190408国际化修改*/
//                    json(Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.numberBi.toUpperCase(), m.bixMaxNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    json(Lan.LanguageFormat(lan, "委托失败-成交数量大于系统规定数量", new String[]{m.bixMaxNum + "", m.numberBi.toUpperCase()}), false, "", true);
                    /*end*/
                    return;
                }
				/*end*/

            }

            BigDecimal totalMoney = BigDecimal.valueOf(buyPlanMoney);//计划委托总金额（买入）
            BigDecimal number = BigDecimal.valueOf(sellPlanNumber);//计划委托总数量（卖出）

            BigDecimal unitPrice = BigDecimal.ZERO;//计划委托 追高、止损委托价格
            BigDecimal unitPriceProfit = BigDecimal.ZERO;//计划委托 抄底、止盈委托价格

            BigDecimal triggerPrice = BigDecimal.ZERO;//计划委托 追高、止损触发价格
            BigDecimal triggerPriceProfit = BigDecimal.ZERO;//计划委托 抄底、止盈触发价格

            /**
             * 		   unitPrice    triggerPrice    unitPriceProfit    triggerPriceProfit
             * 	买      追高委托      追高触发         抄底委托             抄底触发
             * 	卖	    止损委托      止损触发         止盈委托             止盈触发
             *
             * 	通过触发价格和当前市场价格进行比较获取是追高还是抄底
             */
            if (isBuy == 1) {
                if (new BigDecimal(buyTriggerPrice).compareTo(currencyPrice) > 0) {
                    unitPrice = BigDecimal.valueOf(buyPlanPrice);
                    triggerPrice = BigDecimal.valueOf(buyTriggerPrice);
                } else if (new BigDecimal(buyTriggerPrice).compareTo(currencyPrice) < 0) {
                    unitPriceProfit = BigDecimal.valueOf(buyPlanPrice);
                    triggerPriceProfit = BigDecimal.valueOf(buyTriggerPrice);
                } else {
                    //返回信息告诉用户触发价不能和市场价相等
                    json(L("触发价不能与市场价相同"), true, "", true);
                    return;
                }
            } else if (isBuy == 0) {
                if (new BigDecimal(sellTriggerPrice).compareTo(currencyPrice) > 0) {
                    unitPriceProfit = BigDecimal.valueOf(sellPlanPrice);
                    triggerPriceProfit = BigDecimal.valueOf(sellTriggerPrice);
                } else if (new BigDecimal(sellTriggerPrice).compareTo(currencyPrice) < 0) {
                    unitPrice = BigDecimal.valueOf(sellPlanPrice);
                    triggerPrice = BigDecimal.valueOf(sellTriggerPrice);
                } else {
                    //返回信息告诉用户触发价不能和市场价相等
                    json(L("触发价不能与市场价相同"), true, "", true);
                    return;
                }
            } else {
                //不存在的交易类型
                json("", true, "", true);
                return;
            }

            //String isReal=GetPrama(1);
            Message myObj = new Message();
            myObj.setUserId(userid);
            myObj.setWebId(m.webId);
            myObj.setNumbers(number);//止盈止损总数量
            myObj.setMarket(m.market);
            myObj.setTypes(isBuy);//买卖方向
            myObj.setUnitPrice(unitPrice);//计划委托 追高、止损委托价格
            myObj.setStatus(1);//0代表真实委托   1代表计划委托
            myObj.setTriggerPrice(triggerPrice);//计划委托 追高、止损触发价格
            myObj.setUnitPriceProfit(unitPriceProfit);//计划委托  抄底、止盈委托价格
            myObj.setTriggerPriceProfit(triggerPriceProfit);//计划委托 抄底、止盈触发价格
            myObj.setTotalMoney(totalMoney);//追高抄底总金额

            try {
                Message rtn2 = null;//(Message)HTTPTcp.StringToObject(rtn);
                if (m.listenerOpen) {

                    rtn2 = server.planEntrust(myObj, m);
                } else {
                    String serverPath = "/server/planEntrust";
                    String param = HTTPTcp.ObjectToString(myObj);
                    String rtn = HTTPTcp.Post(m.ip, m.port, serverPath, param);
                    rtn2 = (Message) HTTPTcp.StringToObject(rtn);
                }
                json(L(rtn2.getMessage()), true, "{\"code\" :" + rtn2.getStatus() + "}", true);
            } catch (Exception ex2) {
                json(L("委托失败，交易大盘忙碌，请稍后再试，或者通知网站！"), false, "", true);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    /**
     * 交易摘要
     * <p>
     * currency 当前市场
     */
//    @Page(Viewer = JSON)
    public void transactionSummary() {
        if (!IsLogin()) {
            toLogin();
            return;
        }
		/*start by xzhang 20171215 交易页面三期PRD:摘要折算法币*/
        String result = "";
        String currency = param("currency").toLowerCase();
        String legalTender = param("legalTender").toLowerCase();//法币
        String summaryKey = "transaction_summary_" + currency + "_" + legalTender + "_" + userIdStr();
        String summaryJson = Cache.Get(summaryKey);

        // FIXME: 2017/7/22 suxinjie 经常取到0,trans服务多环境部署问题,需要从缓存取
//		Market m = Market.getMarkeByName(currency);
//		BigDecimal lastPrice = ChartManager.getPrice(m);

        BigDecimal lastPrice = BigDecimal.ZERO;
        String currMarket = DishDataCacheService.getDishDepthData(currency, 60);
        if (StringUtil.exist(currMarket)) {
            currMarket = currMarket.replace("([", "").replace("])", "");
            lastPrice = JSONObject.parseObject(currMarket).getBigDecimal("currentPrice");
        }

        String legal_convert = "0";
        if (currency.contains("_usdt")) {
            if (!"usd".equals(legalTender)) {
                legal_convert = StringUtil.exist(Cache.Get("usdt_" + legalTender)) ? Cache.Get("usdt_" + legalTender) : "1";
                lastPrice = lastPrice.multiply(new BigDecimal(legal_convert));
            }
        } else if (currency.contains("_btc")) {
            if ("usd".equals(legalTender)) {
                legal_convert = StringUtil.exist(Cache.Get("btc_usdt")) ? Cache.Get("btc_usdt") : "1";
                lastPrice = lastPrice.multiply(new BigDecimal(legal_convert));
            } else {
                legal_convert = StringUtil.exist(Cache.Get("btc_" + legalTender)) ? Cache.Get("btc_" + legalTender) : "1";
                lastPrice = lastPrice.multiply(new BigDecimal(legal_convert));
            }
        } else {
            log.error("【交易摘要】用户请求市场信息：" + currency + "尚未维护该市场信息");
        }

        //获取市场配置信息
        Market market = Market.getMarket(currency);
        if (null == market) {
            json("", true, result, true);
            return;
        }

        int isProfit = 1; //是否盈亏标识，1：盈利 0：亏损（netValue<0 标识亏损）
        if (!StringUtil.exist(summaryJson)) {
            result = "{\"netValue\":0,\"isProfit\":1,\"costPrice\":0,\"lastPrice\":\"" + Market.formatMoneyToBigDecimal(lastPrice, market).toPlainString() + "\",\"marketValue\":0,\"profitOrLoss\":0}";
            json("", true, result, true);
            return;
        }

        TransactionSummary ts = com.alibaba.fastjson.JSON.parseObject(summaryJson, TransactionSummary.class);

        BigDecimal profitLoss = BigDecimal.ZERO;
        if (ts.getNetAmount().compareTo(BigDecimal.ZERO) == 0) {
            profitLoss = ts.getCost();
            result = "{\"netValue\":0,\"isProfit\":1,\"costPrice\":0,\"lastPrice\":\"" + Market.formatMoneyToBigDecimal(lastPrice, market).toPlainString() + "\",\"marketValue\":0,\"profitOrLoss\":" + profitLoss.setScale(7, BigDecimal.ROUND_DOWN).toPlainString() + "}";
        } else {
//			profitLoss = lastPrice.abs().subtract(ts.getCostPrice().abs()).abs()
//							.multiply(ts.getNetAmount().abs());


            profitLoss = ((ts.getNetAmount().multiply(lastPrice)).abs()).subtract((ts.getCostPrice().multiply(ts.getNetAmount())).abs());
            if (ts.getNetAmount().compareTo(BigDecimal.ZERO) < 0) {
                profitLoss = profitLoss.negate();
                isProfit = 0;
            }
            result = "{\"netValue\":" + ts.getNetAmount().abs() + ",\"isProfit\":" + isProfit +
                    ",\"costPrice\":\"" + Market.formatMoneyToBigDecimal(ts.getCostPrice().abs(), market).toPlainString() +
                    "\",\"lastPrice\":\"" + Market.formatMoneyToBigDecimal(lastPrice, market).toPlainString() +
                    "\",\"marketValue\":" + ts.getNetAmount().multiply(lastPrice).abs().setScale(7, BigDecimal.ROUND_DOWN).toPlainString() +
                    ",\"profitOrLoss\":" + profitLoss.setScale(7, BigDecimal.ROUND_DOWN).toPlainString() + "}";
        }

        json("", true, result, true);
		/*end*/
    }


    /**
     * 重置交易摘要
     */
//    @Page(Viewer = JSON)
    public void transactionSummaryResum() {
        if (!IsLogin()) {
            toLogin();
            return;
        }
		/*start by xzhang 20171215 交易页面三期PRD:摘要折算法币*/
        String currency = param("currency").toLowerCase();
        String summaryKey = "";
        for (LegalTenderType tenderType : LegalTenderType.values()) {
            summaryKey = "transaction_summary_" + currency + "_" + tenderType.getKey().toLowerCase() + "_" + userIdStr();
            Cache.Set(summaryKey, "");
        }
		/*end*/
        json("Success", true, "", true);
    }

    /**
     * 修改撮合开关
     */
//    @Page(Viewer = JSON)
    public void switchMatchOpen() {
//        if (!IsLogin()) {
//            toLogin();
//            return;
//        }

//        try {
//            // 增加防御措施
//            String sign = param("sign");
//            if (StringUtils.isBlank(sign)) {
//                json(L("参数非法！"), false, "", true);
//                return;
//            }
//            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI2+Fd2Xa1gjWV37QWy+q11cqdA5Abd5wE7CC+ltVepXRRVGuLoAdEvlr4VVDnnsCsSouXrvjXgWGO+HZ76CwuV5xe8wly6H+rwQR5Vrmv0+ZIfFNOTeJGZUgFRXmadLwWt3VWN2rc6mTY9nx/MZT9H2q/Wb0SeXUF1itmd8uuaHAgMBAAECgYB/kDQTgmOsJdwW1boSySJmWq/FYpil7B/jgYXA5ZJt3W6h8EztsNz5NVQateraVVF3nbWX6yGxkomMgJsgfIQzLVAL2yoFyMPYe37+0NSNlBiKtBSGhfvDKwhZrKU+NAg4OjYX31/LR6ezbgIr/IRbrXwIYesy0XDdu02GRpnjSQJBAOrl+4UVQli8L5x4DiUK0BhzMYsqg0Gpk9KaE8fBdTw7Ubf+8dkgUu3Ta4cnLgvBywqpBZoXBe1H8yXpSlJ09WUCQQCaecYx4t+4SyBA/uk3kwJES2M4GFZb2I520yoa0ujc/uEU9X8nyD/LnSqpqKf+1cbrWl3MAO/xPhZfNwnVIJN7AkEAsc1JmI/h+5belxqM4l8P6yHuw393gRFiMkysYky+d8wS7CpPWGHOQ/T/dHsksIONNFGCSwPYWaZXlz/CIS4kvQJACWLIxhMw4LO/2/MhHH1UL+4cszXXWXFJBrNB5atW9saNyoY4GaSzK537D5/txTAcDATLmi+cZJ4PIe3oLQjzrQJAPxnkLo0fUR4DvnsDL+4nL2xtunNce7TtSxbXgfvKseaC4yr/+y47lXL5V6Xof+WTXvBz29OcjRRcqNdFkjrXkg==";
//            byte[] decodeSign = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(sign), priKey);//解密
//            if (!"snowman_web_boss".equals(new String(decodeSign))) {
//                json(L("参数非法！"), false, "", true);
//                return;
//            }
//        } catch (Exception e) {
//            log.error("解密失败！", e);
//            json(L("参数非法！"), false, "", true);
//            return;
//        }

        Market m = Market.getMarkeByName(GetPrama(0));
        if (m == null) {
            Write("", false, L("错误的市场"));
            return;
        }

        int type = intParam("isOpen");//0 关闭  1 开启

        Message myObj = new Message();
        myObj.setTypes(type);
        myObj.setMarket(m.market);
        try {
            boolean result = false;
            if (m.listenerOpen) {
                result = server.switchMatchOpen(myObj);
            } else {
                String serverPath = "/server/switchMatchOpen";
                String param = HTTPTcp.ObjectToString(myObj);
                String rtn = HTTPTcp.Post(m.ip, m.port, serverPath, param);
                result = (Boolean) HTTPTcp.StringToObject(rtn);
            }

            json("Success", true, String.valueOf(result), true);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            json("Error", true, "false", true);
        }
    }

//    @Page(Viewer = JSON)
//    public void testSql() {
//        Market m = Market.getMarkeByName(GetPrama(0));
//        if (m == null) {
//            Write("", false, L("错误的市场"));
//            return;
//        }
//        Data.doWithoutTrans(m.db);
//        json("Success", true, "", true);
//    }
}
