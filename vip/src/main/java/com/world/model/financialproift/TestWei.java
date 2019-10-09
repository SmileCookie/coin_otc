package com.world.model.financialproift;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.redis.RedisUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.data.mysql.QueryDataType;
import com.world.model.dao.financialproift.*;
import com.world.model.entity.financialproift.*;
import com.world.model.enums.BonusEnum;
import com.world.model.enums.StatusEnum;
import com.world.web.Page;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.logging.Logger;

public class TestWei {
    private static Logger log = Logger.getLogger(TestWei.class.getName());

    public static void main(String[] args) {
        TestWei testWei = new TestWei();
       testWei.superNodeProduceInfo();

    }



    @SuppressWarnings("unchecked")
    public void useInsureInvestSonInfos() {
        try {
           // initLoginUser();
            int pageNo = 1;
            if (pageNo < 1) {
              //  json(L("无效参数"), false, null, true);
                return;
            }
            String sonUserId="";
            String sonUserName="";
            String sql = "";
            String userId="";
            try{
                userId = "1004746";
            }catch (Exception e){
                //log.info("查询用户异常");
            }
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
                sonUserName = sonUserName;
            }
            sql = "select userName,invitationCode,userVID,'切换主账号' as switchAccount from fin_userfinancialinfo " +
                    "where parentUserId =" + userId + " and authPayFlag != 1 ";

            UserFinancialInfoDao dao = new UserFinancialInfoDao();
            Query<UserFinancialInfo> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(UserFinancialInfo.class);
            int total = queryTotal.count();
            if (total > 0) {
                List<UserFinancialInfo> list = queryTotal.getPageList(pageNo, 10);
                String invitationCode = list.get(0).getpInvitationCode();
                /**
                 * 直接邀请人数
                 */
                String directInvitationSucNum = "0";
                sql = "select count(*) cnt from fin_userfinancialinfo where pInvitationCode = '" + invitationCode + "' and authPayFlag = 2";
               // log.info("useInsureInvestSonInfos sql = " + sql);
                List<Integer> listDirectInvitationSucNum = (List<Integer>) Data.GetOne("vip_financial", sql, null);
                if (null != listDirectInvitationSucNum) {
                    directInvitationSucNum = listDirectInvitationSucNum.get(0) + "";
                }

                //循环插入团队邀请人数和直接邀请人数
                for (UserFinancialInfo userFinancialInfo : list) {
                    /**
                     * 团队邀请人数
                     */
                    String invitationTotalNum = RedisUtil.get("financial_invitationTotalNum_" + userFinancialInfo.getUserId());
                    if (StringUtils.isBlank(invitationTotalNum)) {
                        invitationTotalNum = "0";
                    }
                    userFinancialInfo.setSwitchAccount(userFinancialInfo.getSwitchAccount());
                    userFinancialInfo.setTeamsNumber(Integer.valueOf(invitationTotalNum));
                    userFinancialInfo.setDirectNumber(Integer.valueOf(directInvitationSucNum));
                }
                Map<String, Object> page = new HashMap<String, Object>();
                page.put("pageIndex", pageNo);
                page.put("totalCount", total);
                page.put("list", list);
               // json("ok", true, JSONObject.toJSONString(page));
               // log.info("查询结果："+JSONObject.toJSONString(page));
                System.out.println(JSONObject.toJSONString(page));

                return;
            }else{
                Map<String, Object> page = new HashMap<String, Object>();
                page.put("pageIndex", pageNo);
                page.put("totalCount", total);
                page.put("list", new ArrayList<>());
               // json("ok", true, JSONObject.toJSONString(page));
            }
        } catch (Exception e) {
           // json("error",false,e.getMessage());
            log.info("保险投资子账号列表查询异常" + e);
        }
    }

    public void queryUserInsureinvest() {

        try {
           // initLoginUser();
            int pageNo = 1;
            if (pageNo < 1) {
               // json(L("无效参数"), false, null, true);
               // return;
            }
            String sql = "select id,triggerPrice,investLevelAmount,insureInvestAmount,insureInvestNum from fin_userinsureinvest ";
            FinUserInsureInvestDao dao = new FinUserInsureInvestDao();
            Query<FinUserInsureInvest> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(FinUserInsureInvest.class);
            int total = queryTotal.count();
            if (total > 0) {
                List<FinUserInsureInvest> list = queryTotal.getPageList(pageNo, 10);
                for (FinUserInsureInvest inverst : list) {
                    if (inverst.getTriggerFlag() == 0) {
                        inverst.setTriggerFlagDesc("暂未投资");
                    } else if (inverst.getTriggerFlag() == 1) {
                        inverst.setTriggerFlagDesc("部分投资中");
                    } else if (inverst.getTriggerFlag() == 2) {
                        inverst.setTriggerFlagDesc("投资完成");
                    } else {
                        inverst.setTriggerFlagDesc("已撤销");
                    }
                    if (inverst.getInvestState() == 0 || inverst.getInvestState() == 3) {
                        inverst.setInvestStateDesc("-");
                    } else {
                        inverst.setInvestStateDesc("已设置" + "(" + inverst.getInsureInvestSurplusNum() + "/" + inverst.getInsureInvestNum() + ")");
                    }
                    if (inverst.getInvestState() == 1) {
                        inverst.setJumpFlag(true);
                    }
                }
                Map<String, Object> page = new HashMap<String, Object>();
                page.put("pageIndex", pageNo);
                page.put("totalCount", total);
                page.put("list", list);
               // json("ok", true, JSONObject.toJSONString(page));
                return;
            }
        } catch (Exception e) {
            log.info("用户担保投资查询异常" + e.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    public   boolean userSonRelationCheck(String userId, String userName, String sonUserId) {
        boolean userSonRelationFlag = false;

        /**
         * 支持子账号切换校验
         */
        /*查询SQL*/
        String sql = "";
        if (StringUtils.isEmpty(sonUserId)) {
            return userSonRelationFlag;
        }
        /*判断子账号是否归属此用户*/
        sql = "select count(*) cnt from fin_userfinancialinfo where parentUserId = '" + userId + "' and userId = '" + sonUserId + "' ";
        int sonUserCount = 0;
        List<Long> listSonUser = (List<Long>) Data.GetOne("vip_financial", sql, null);
        if (null != listSonUser) {
            /*个数检查*/
            sonUserCount = listSonUser.get(0).intValue();
        }
        //log.info("sonUserCount = " + sonUserCount);
        if (1 == sonUserCount) {
            userSonRelationFlag = true;
        }

        return userSonRelationFlag;
    }

    public void queryUserinSureinvestNum() {
        try {
            String id = "6";
            log.info("canshu"+id);
            String sql = "";
            sql = "select insureInvestNum-insureInvestSurplusNum as num from fin_userinsureinvest where id =" + id;
            List<String> num = (List<String>)Data.GetOne("vip_financial", sql, null);

            if(num.size()>0 && num != null){
               // json("ok", true, num.get(0));
            }else{
               // json("ok", true, "0");
            }
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:设置排位可用份数");
        }
        //json("ok", true, "0");
    }


    public void ascriptionOption() {
        try {
            String sql = "";
            sql = "select DISTINCT(transferName) as transferName  from fin_usersontransfer where transferName != ''  ";
            FinUsersontransferDao dao = new FinUsersontransferDao();
            Query<FinUsersontransfer> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(FinUsersontransfer.class);
            List<FinUsersontransfer> num =queryTotal.getList();
            List<String> list = new ArrayList<>();
            for (FinUsersontransfer n : num) {
                list.add(n.getTransferName());
            }
            list.add("全部");
            // json("ok", true, JSONObject.toJSONString(num));
            System.out.println(JSONObject.toJSONString(list));
            return;
        } catch (Exception e) {
            log.info("理财报警ERROR:设置排位可用份数"+ e.getMessage());
        }
       // json("ok", true, "0");
    }

    public void userTransferBill() {
        try {
            String sonUserId = "";
            String sonUserName = "";
            String transferName= "";
            int pageSize = 1;
            int currentPage = 1;
            String userId = "1004753";
            String sql = "";
            sql = "select id,createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                    "as AttributionNum from fin_usersontransfer where fundsType=51 and parentUserId=" + userId;
            if(StringUtils.isNotEmpty(transferName)){
                sql+= " and transferName="+transferName;
            }
            boolean userSonRelationFlag = userSonRelationCheck(userId, sonUserName, sonUserId);
            if (userSonRelationFlag) {
                /*是子账号，切换数据处理逻辑*/
                userId = sonUserId;
                sonUserName = sonUserName;
                sql = "select createTime,sonUserName,concat(avaTransferAmount,'VOLLAR') as avaTransferAmount,concat(douProfitAmount,'VOLLAR') as douProfitAmount,transferType,parentUserName,concat(avaTransferAmount-douProfitAmount,'VOLLAR') " +
                        "as AttributionNum from fin_usersontransfer where fundsType=51 and sonUserId=" + userId;
                if(StringUtils.isNotEmpty(transferName)){
                    sql+= " and transferName="+transferName;
                }
            }
            FinUsersontransferDao dao = new FinUsersontransferDao();
            Query<FinUsersontransfer> queryTotal = dao.getQuery();
            queryTotal.setSql(sql);
            queryTotal.setDatabase("vip_financial");
            queryTotal.setCls(FinUsersontransfer.class);
            int total = queryTotal.count();
            List<FinUsersontransfer> list = queryTotal.getPageList(currentPage, pageSize);
            Map<String, Object> page = new HashMap<String, Object>();
            page.put("pageIndex", currentPage);
            page.put("totalCount", total);
            page.put("list", list);
            //json("ok", true, JSONObject.toJSONString(page));
            return;
        } catch (Exception e) {
           // log.info("理财报警ERROR:账号划入划出列表异常", e);
        }
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("pageIndex", 1);
        page.put("totalCount", 0);
        page.put("list", new ArrayList<FinUsersontransfer>());
        //json("ok", true, JSONObject.toJSONString(page));
    }


    public void returnUserOrderWork() {
        String sql = " select  id, userId, investAmount , userName, profitTime, resetProfitTime, expectProfitUsdt, staticProfitSumUsdt, returnType, batchNo, seqNo, dealFlag,dealTime,authPayFlag,investAvergPrice  from fin_userreturnorderinfoall where 1=1 and dealFlag = 1 and userId = " + 1779367;

        FinUserReturnOrderInfoDao dao = new FinUserReturnOrderInfoDao();
        Query<FinUserReturnOrderInfo> queryTotal = dao.getQuery();
        queryTotal.setSql(sql);
        queryTotal.setDatabase("vip_financial");
        queryTotal.setCls(FinUserReturnOrderInfo.class);
       int total = queryTotal.count();
        List<FinUserReturnOrderInfo> list = new ArrayList<>();
        if (total > 0) {
            list = queryTotal.getPageList(1, 30);
            for (FinUserReturnOrderInfo inverst : list) {
                //投资基数
                BigDecimal investAmount = inverst.getInvestAmount().multiply(inverst.getInvestAvergPrice()).setScale(3, BigDecimal.ROUND_DOWN);
                if (inverst.getDealFlag() == 0) {
                    inverst.setDealFlagDESC("未回本");
                } else {
                    inverst.setDealFlagDESC("已回本");
                }
                if (inverst.getAuthPayFlag() == 2) {
                    inverst.setReturnTypeDESC("已复投");
                } else {
                    inverst.setReturnTypeDESC("未复投");
                }
                /*理论收益 3位小数*/
                String userNames = inverst.getUserName();
                String userNameBefor = userNames.substring(0, userNames.indexOf("@"));
                if (userNameBefor.length() > 2) {
                    userNameBefor = userNameBefor.substring(0, 2) + "****";
                    userNames = userNameBefor + userNames.substring(userNames.indexOf("@"));
                }

                inverst.setUserName(userNames);
                //回本数量截取3位小数
                BigDecimal recoveyUsdt = inverst.getExpectProfitUsdt().subtract(inverst.getStaticProfitSumUsdt())
                        .setScale(3, BigDecimal.ROUND_DOWN);

                if (recoveyUsdt.compareTo(BigDecimal.ZERO) < 0) {
                    inverst.setRecoveryUsdt(BigDecimal.ZERO);
                } else {
                    /*.setScale(4, BigDecimal.ROUND_DOWN);*/
                    inverst.setRecoveryUsdt(recoveyUsdt);
                }
                inverst.setInvestAmount(investAmount);
            }
        }
    }


    public void superNodeProduceInfo() {
        //初创节点数量
        String homeMadeNodeShowNum = RedisUtil.get("fin_homeMadeNodeShowNum");
        if (StringUtils.isBlank(homeMadeNodeShowNum) ) {
            homeMadeNodeShowNum = "0";
        }

        //初创节点累积收益
        String homeMadeNodeTotalProfit = RedisUtil.get("fin_homeMadeNodeTotalProfit");
        if (StringUtils.isBlank(homeMadeNodeTotalProfit) ) {
            homeMadeNodeTotalProfit = "0";
        }

        //初创节点已发放收益
        String homeMadeNodePayProfit = RedisUtil.get("fin_homeMadeNodePayProfit");
        if (StringUtils.isBlank(homeMadeNodePayProfit)  ) {
            homeMadeNodePayProfit = "0";
        }

        //固定节点数量
        String fixedMadeNodeShowNum = RedisUtil.get("fin_fixedMadeNodeShowNum");
        if (StringUtils.isBlank(fixedMadeNodeShowNum)  ) {
            fixedMadeNodeShowNum = "0";
        }

        //固定节点累积收益
        String fixedMadeNodeTotalProfit = RedisUtil.get("fin_fixedMadeNodeTotalProfit");
        if (StringUtils.isBlank(fixedMadeNodeTotalProfit) ) {
            fixedMadeNodeTotalProfit = "0";
        }

        //固定节点已发放收益
        String fixedMadeNodePayProfit = RedisUtil.get("fin_fixedMadeNodePayProfit");
        if (StringUtils.isBlank(fixedMadeNodePayProfit) ) {
            fixedMadeNodePayProfit = "0";
        }

        //动态节点数量
        String trendsMadeNodeShowNum = RedisUtil.get("fin_trendsMadeNodeShowNum");
        if (StringUtils.isBlank(trendsMadeNodeShowNum)  ) {
            trendsMadeNodeShowNum = "0";
        }

        //动态节点累积收益
        String trendsMadeNodeTotalProfit = RedisUtil.get("fin_trendsMadeNodeTotalProfit");
        if (StringUtils.isBlank(trendsMadeNodeTotalProfit) ) {
            trendsMadeNodeTotalProfit = "0";
        }

        //动态节点已发放收益
        String trendsMadeNodePayProfit = RedisUtil.get("fin_trendsMadeNodePayProfit");
        if (StringUtils.isBlank(trendsMadeNodePayProfit) ) {
            trendsMadeNodePayProfit = "0";
        }

        //节点用作静态和保险分红字段显示 初期-固定-动态
        Integer bonus = Integer.valueOf(homeMadeNodeShowNum) - Integer.valueOf(fixedMadeNodeShowNum) - Integer.valueOf(trendsMadeNodeShowNum);

        //固定超级主节点提示字段 固定+动态
        Integer accelerator = Integer.valueOf(fixedMadeNodeShowNum) + Integer.valueOf(trendsMadeNodeShowNum);
        String resultMsg = "初创吐司";

        resultMsg = resultMsg.replaceAll("xxx", "" + bonus);
        resultMsg = resultMsg.replaceAll("yyy", "" + accelerator);

        String fixedMsg = "固定吐司";
        fixedMsg = fixedMsg.replaceAll("xxx", "" + fixedMadeNodeShowNum);

        String trendsMsg = "动态吐司";
        trendsMsg = fixedMsg.replaceAll("xxx", "" + trendsMadeNodeShowNum);


        Map<String, String> map = new HashMap<>();
        map.put("homeMadeNodeShowNum", homeMadeNodeShowNum);
        map.put("homeMadeNodeTotalProfit", homeMadeNodeTotalProfit);
        map.put("homeMadeNodePayProfit", homeMadeNodePayProfit);
        map.put("fixedMadeNodeShowNum", fixedMadeNodeShowNum);
        map.put("fixedMadeNodeTotalProfit", fixedMadeNodeTotalProfit);
        map.put("trendsMadeNodeShowNum", trendsMadeNodeShowNum);
        map.put("trendsMadeNodeTotalProfit", trendsMadeNodeTotalProfit);
        map.put("trendsMadeNodePayProfit", trendsMadeNodePayProfit);
        map.put("fixedMadeNodePayProfit", fixedMadeNodePayProfit);
        map.put("homeMadeNodeTips", resultMsg);
        map.put("fixedMadeNodeTips", fixedMsg);
        map.put("trendsMadeNodeTips", trendsMsg);

       // json("ok", true, JSONObject.toJSONString(map));


    }




}
