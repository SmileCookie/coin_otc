package com.world.controller.manage.financial.insurance;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.CapitalTransferApiService;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.web.Page;
import com.world.web.action.UserAction;

public class Index extends UserAction {
    /**
    *
    */
   private static final long serialVersionUID = 1L;
   
   /**
    * 理财担保投资用户预投资金
    * insureInvestAmount 担保投资资金
    */
   @SuppressWarnings("unchecked")
   @Page(Viewer = JSON)
   public void userFinancialInsureInvest() {
       initLoginUser();
       try {
           /*调用用户信息*/
           String userId = loginUser.get_Id();
           String userName = loginUser.getUserName();
           /*资金来源*/
           String amountFromSys = param("amountFromSys");
           int intAmountFromSys = 0;
           /*保险投资资金*/
           String insureInvestAmount = param("insureInvestAmount");
           BigDecimal bdInsureInvestAmount = BigDecimal.ZERO;
           /*触发价格*/
           String triggerPrice = param("triggerPrice");
           BigDecimal bdTriggerPrice = BigDecimal.ZERO;
           /**
            * 参数校验,先分别设置校验不通过时的返回值
            */
           boolean crFlag = false;
           /*资金来源 校验标志*/
           String amountFromSysCR = "";
           /*担保投资资金 检验标志*/
           String insureInvestAmountCR = "";
           /*触发价格 校验标志*/
           String triggerPriceCR = "";
           
           /*查询SQL*/
           String sql = "";
           /*拼装返回值*/
           Map<String, Object> result = new HashMap<>();

           /*投资价格转换、触发价格，异常处理*/
           try {
        	   intAmountFromSys = Integer.parseInt(amountFromSys);
        	   bdInsureInvestAmount = new BigDecimal(insureInvestAmount).setScale(4, BigDecimal.ROUND_DOWN);
               bdTriggerPrice = new BigDecimal(triggerPrice);
           } catch (Exception e) {
        	   bdInsureInvestAmount = BigDecimal.ZERO;
               bdTriggerPrice = BigDecimal.ZERO;
               intAmountFromSys = 0;
           }
           
           /*基础校验*/
           if (bdInsureInvestAmount.compareTo(BigDecimal.ONE) <= 0) {
               insureInvestAmountCR = "投资数量错误";
               log.info("理财报警WARN:userFinancialInsureInvest 担保投资资金 异常 = " + bdInsureInvestAmount);
               crFlag = true;
           }
           if (bdTriggerPrice.compareTo(BigDecimal.ZERO) <= 0) {
               triggerPriceCR = "触发价格错误";
               log.info("理财报警WARN:userFinancialInsureInvest 触发价格 异常 = " + bdTriggerPrice);
               crFlag = true;
           }
           if (intAmountFromSys < 1) {
               amountFromSysCR = "账户类型错误";
               log.info("理财报警WARN:userFinancialInsureInvest 资金划转来源 异常 = " + intAmountFromSys);
               crFlag = true;
           }
           
           /**
            * 基础信息校验检查返回
            */
           if (crFlag) {
               if (!StringUtils.isEmpty(amountFromSysCR)) {
                   result.put("amountFromSysCR", L(amountFromSysCR));
               }
               if (!StringUtils.isEmpty(insureInvestAmountCR)) {
                   result.put("insureInvestAmountCR", L(insureInvestAmountCR));
               }
               if (!StringUtils.isEmpty(triggerPriceCR)) {
                   result.put("triggerPriceCR", L(triggerPriceCR));
               }
               log.info("result = " + result);
               json("ok", false, JSONObject.toJSONString(result), true);
               return;
           }

           /*获取vds_usdt实时价格*/
           String vdsUsdtPrice = Cache.Get("vds_usdt_l_price");
           log.info("vdsUsdtPrice = " + vdsUsdtPrice);
           if (StringUtils.isEmpty(vdsUsdtPrice)) {
               json(L("系统繁忙，请稍后再试"), false, null, true);
               log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
               return;
           }
           BigDecimal bdVdsUsdtPrice = BigDecimal.ZERO;
           try {
               bdVdsUsdtPrice = new BigDecimal(vdsUsdtPrice);
           } catch (Exception e) {
               bdVdsUsdtPrice = BigDecimal.ZERO;
           }
           if (bdVdsUsdtPrice.compareTo(BigDecimal.ZERO) <= 0) {
               json(L("系统繁忙，请稍后再试"), false, null, true);
               log.info("理财报警ERROR:获取VDSUSDT成交价格异常");
               return;
           }
           
           /**
            * 划转前资金提前判断
            */
           String checkPayTable = "";
           String fundsType = "fundsType";
           if (1 == intAmountFromSys) {
               checkPayTable = "pay_user_wallet";
           } else if (2 == intAmountFromSys) {
           	checkPayTable = "pay_user";
           } else if (3 == intAmountFromSys) {
           	checkPayTable = "pay_user_otc";
           	fundsType = "coinTypeId";
           } else if (5 == intAmountFromSys) {
           	checkPayTable = "pay_user_financial";
           } else if (4 == intAmountFromSys) {
           	json(L("账户类型错误"), false, null, true);
           	log.info("账户类型错误 = " + intAmountFromSys);
           	return;
           }
           /*先检查资金是否有足够的资金*/
           sql = "select balance from " + checkPayTable + " where userid = " + userId + " and " + fundsType + " = 51";
           log.info("sql = " + sql);
           List<BigDecimal> listPayUserFinancial = (List<BigDecimal>) Data.GetOne("vip_main", sql, null);
           BigDecimal userBalance = BigDecimal.ZERO;
           if (null == listPayUserFinancial || listPayUserFinancial.size() < 1) {
               json(L("账户初始化失败，请联系客服"), false, null, true);
               log.info("理财报警ERROR:保险投资，账户初始化失败，请联系客服" + userId + ", 账户表 = " + checkPayTable);
               return;
           } else {
               if (null != listPayUserFinancial.get(0)) {
                   userBalance = listPayUserFinancial.get(0);
               }
               log.info("userBalance = " + userBalance);
               if (userBalance.compareTo(bdInsureInvestAmount) < 0) {
                   json(L("账户资金不足，请先进行充值或者划转"), false, null, true);
                   log.info("理财报警WARN:保险投资，账户资金不足，请先进行充值或者划转" + userId + ", userBalance = " + userBalance + ", 账户表 = " + checkPayTable);
                   return;
               }
           }
           
           /**
            * 进行资金划转
            */
           String url = ApiConfig.getValue("usecenter.url");
           FeignContainer container = new FeignContainer(url + "/capitalTransfer");
           CapitalTransferApiService capitalTransferApiService = container.getFeignClient(CapitalTransferApiService.class);
           /**
            * 钱包账户 	1
            * 币币账户	2
            * 币法账户	3
            * 期货账户	4
            * 理财账户	5
            */
           if (intAmountFromSys != 5 && intAmountFromSys > 0) {
               Boolean flag = capitalTransferApiService.transfer(intAmountFromSys, 5, bdInsureInvestAmount, 51, userId + "");
               if (!flag) {
                   log.info("理财报警WARN:userFinancialInsureInvest 担保投资划转异常 = " + bdInsureInvestAmount);
                   json(L("保险投保失败"), false, null, true);
                   return;
               }
           }

           /**
            * 开启事务处理
            */
           List<OneSql> sqls = new ArrayList<>();
           TransactionObject txObj = new TransactionObject();
//           long currentTime = System.currentTimeMillis();
           /*担保投资资金扣减*/
           sql = "update pay_user_financial set balance = balance - " + bdInsureInvestAmount + ", "
               + "insureInvestFreezeAmount = insureInvestFreezeAmount + " + bdInsureInvestAmount + " "
               + "where userId = " + userId + " and fundsType = 51 and balance >= " + bdInsureInvestAmount + " ";
           log.info("sql = " + sql);
           sqls.add(new OneSql(sql, 1, null, "vip_main"));

           /**
            * 生成担保投资记录
            * investPrice					投资时价格
            * triggerPrice					触发价
            * investLevelAmount			投资矩阵金额
            * insureInvestAmount 			担保投资资金
            * insureInvestSurplusAmount 	投资剩余资金
            * insureInvestNum 				投资份数
            * insureInvestSurplusNum		投资剩余份数
            * pInvitationCode				推进人邀请码
            * userVID						用户VID
            */
           sql = "insert into fin_userinsureinvest (userId, userName, investPrice, triggerPrice, investLevel, investLevelAmount, "
                   + "insureInvestAmount, insureInvestSurplusAmount, insureInvestNum, insureInvestSurplusNum, "
                   + "pInvitationCode, userVID, createTime) "
                   + "values (" + userId + ", '" + userName + "', " + bdVdsUsdtPrice + ", " + bdTriggerPrice + ", "
                   + "6, 188, " + bdInsureInvestAmount + ", " + bdInsureInvestAmount + ", 0, 0, '', '', now() )";
           log.info("sql = " + sql);
           sqls.add(new OneSql(sql, 1, null, "vip_financial"));

           txObj.excuteUpdateList(sqls);
           if (txObj.commit()) {
               /*支付成功*/
               log.info("理财报警INFO:用户【 " + userId + "】保险投保成功，保险金额 = " + bdInsureInvestAmount + ", 触发价 = " + bdTriggerPrice + ", 当前价格 = " + bdVdsUsdtPrice);
               json(L("保险投保成功"), true, null, true);
           } else {
               log.info("理财报警ERROR:用户【 " + userId + "】保险投保失败，保险金额 = " + bdInsureInvestAmount + ", 触发价 = " + bdTriggerPrice + ", 当前价格 = " + bdVdsUsdtPrice);
               json(L("保险投保失败"), false, null, true);
           }
       } catch (Exception e) {
           json(L("保险投保失败"), false, null, true);
           log.info("理财报警ERROR:保险投保失败", e);
       }
   }
   
}
