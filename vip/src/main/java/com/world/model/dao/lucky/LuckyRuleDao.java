package com.world.model.dao.lucky;

import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.lucky.LuckyRule;
import com.world.model.entity.user.User;
import com.world.model.entity.user.authen.Authentication;
import com.world.util.CommonUtil;
import com.world.util.date.TimeUtil;
import com.world.util.string.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @Title: 抽奖规则信息表
 * @Description: 主要处理抽奖规则信息增删改查功能
 * @Company: atlas
 * @author: xzhang
 */
public class LuckyRuleDao extends DataDaoSupport {

    /**
     * @describe 查询当前活动奖池是否已满
     * @param luckyId
     * @return
     */
    public boolean getJackpot(String luckyId){
        List<LuckyRule>  luckyRuleList = null;
        try {
            String sql = "select t.ruleType,t.ruleId from luckyrule t where  t.luckyId = ? and ((t.ruleType = '01' and t.jackpotSize>t.occurAmount) or (t.ruleType = '02' and t.occurCount<t.jackpotSize))";
            luckyRuleList = find(sql, new Object[]{luckyId}, LuckyRule.class);
            if (!CollectionUtils.isEmpty(luckyRuleList)) {
                return true;
            }
        }catch (Exception e){
            log.error("【抽奖】根据抽奖ID："+luckyId+"查询当前活动奖池是否已满异常，异常信息为：",e);
            return false;
        }
        return false;
    }
    /**
     * @describe 查询当前活动奖池未满奖池明细
     * @param luckyId
     * @return
     */
    public List<LuckyRule> getRuleList(String luckyId){
        List<LuckyRule>  luckyRuleList = null;
        try {
            String sql = "select t.ruleId,t.luckyId,t.ruleType,t.radixPoint,t.jackpotSize,t.startSize,t.endSize,t.hitProbability,t.occurAmount,t.occurCount,t.isUse from luckyrule t where t.luckyId = ? and ((t.ruleType = '01' and t.jackpotSize>t.occurAmount) or (t.ruleType = '02' and t.occurCount<t.jackpotSize)) ";
            luckyRuleList = find(sql, new Object[]{luckyId}, LuckyRule.class);
        }catch (Exception e){
            log.error("【抽奖】根据抽奖ID："+luckyId+"查询当前活动奖池未满奖池明细异常，异常信息为：",e);
            return luckyRuleList;
        }
        return luckyRuleList;
    }

    /**
     * @describe 规则抽奖
     * @param user
     * @param luckyId
     * @param ruleList
     * @param limitCount
     * @param eventTitle
     * 抽奖逻辑：
     *      根据规则类型抽奖：
     *          1.1根据规则限制小数点位数，和随机数最大和最小值。随机产生符合条件的数字，
     *          1.2根据当前时间和领取状态，用户ID和活动id更新用户的领抽奖资格表
     *          1.3根据发生额和设定奖池大小大于等于当前发生额为条件更新奖池
     *      注意：
     *          1.一个事物执行该方法，执行失败。返回用户为未抽中。
     *          2.当随机数加已发生额大于奖池最大数。则发生额改为奖池大小减去已发生额。如果小于0，则改为0.提示用户未抽中
     *
     */
    public Map<String,Object> getLucky(User user, String luckyId, List<LuckyRule> ruleList,int limitCount,String eventTitle){
        Map<String,Object>  reMap = new HashMap<String,Object>();
        reMap.put("occurAmout",new BigDecimal(0));
        reMap.put("userAmout",new BigDecimal(0));
        LuckyRule rule = ruleList.get(0);
        try{
            if(Const.LUCKY_RULE_TYPE_MAX.equals(rule.getRuleType())){
                String curr = TimeUtil.getFormatCurrentDateTime20();
                List<OneSql> batchSQL = new ArrayList<>();
                //更新用户资格信息
                BigDecimal occurAmount = getOccurAmount(ruleList);
                String updateQualify = "update luckyqualify y set y.isReceive = '02',y.occurAmount = ? ,y.ruleId = ? ,y.updateTime = ? where y.qId = (" +
                        "select v1.qId from (select t.qId from luckyqualify t where t.luckyId = ? and t.userId = ? and isReceive = '01' and ? BETWEEN t.startTime AND t.endTime order by t.createTime asc limit 1) v1 ) "+
                        " and ? >= (select * from (select count(1) from luckyqualify t where t.luckyId = ? and t.userId = ? and isReceive = '02') v2 )";
                batchSQL.add(new OneSql(updateQualify, 1, new Object[]{occurAmount,rule.getRuleId(),curr,luckyId,user.get_Id(),curr,limitCount,luckyId,user.get_Id()}));
                //更新规则
                BigDecimal occurAmountRule =  rule.getOccurAmount().add(occurAmount);
                reMap.put("occurAmout",rule.getOccurAmount());
                String updateRule = "update luckyrule t set t.occurAmount = ? ,t.occurCount = t.occurCount+1,t.updateTime = ? where t.ruleId = ? and t.jackpotSize >= t.occurAmount";
                batchSQL.add(new OneSql(updateRule, 1, new Object[]{occurAmountRule,curr,rule.getRuleId()}));
                //更新用户金额
                if(occurAmount.compareTo(new BigDecimal(0))==1){
                    int fundsType = DatabasesUtil.coinProps("GBC").getFundsType();
                    int type = BillType.luckyIn.getKey();
                    String nowSql = "INSERT INTO bill (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType) " +
                            "SELECT '"+user.get_Id()+"','"+user.getUserName()+"',"+type+","+2+","+occurAmount+",'"+now()+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+" from pay_user where userId='" +user.get_Id() + "' AND fundsType = "+fundsType;
                    String newBillSql = "INSERT INTO billdistribution (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType, sourceRemark) " +
                            "SELECT '"+user.get_Id()+"','"+user.getUserName()+"',"+type+","+2+","+occurAmount+",'"+now()+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+", '"+ eventTitle+"' from pay_user where userId='" +user.get_Id() + "' AND fundsType = "+fundsType;
                    batchSQL.add(new OneSql("update pay_user set balance=balance+" + occurAmount + " where userId=? AND fundsType = ?", 1, new Object[] {user.get_Id(), fundsType}));
                    batchSQL.add(new OneSql(nowSql, 1, new Object[]{}));
                    batchSQL.add(new OneSql(newBillSql, 1, new Object[]{}));
                }
                Boolean flag = Data.doTrans(batchSQL);
                if(flag){
                    UserCache.resetUserFunds(user.get_Id());
                    reMap.put("occurAmout",occurAmountRule);
                    reMap.put("userAmout",occurAmount);
                }else{
                    log.error("【抽奖】根据userId："+user.get_Id()+"及抽奖ID:"+luckyId+"更新抽奖金额："+occurAmount+"发生异常,回滚抽奖更新信息");
                }
                return reMap;
            }else if (Const.LUCKY_RULE_TYPE_ZH.equals(rule.getRuleType())){
                //预留
                return reMap;
            }
        }catch (Exception e){
            log.error("【抽奖】根据userId："+user.get_Id()+"及抽奖ID:"+luckyId+"进行抽奖发生异常，异常信息为：",e);
        }
        return reMap;
    }

    /**
     * @describe 根据规则随机中奖金额
     * 1.1根据规则限制小数点位数，和随机数最大和最小值。随机产生符合条件的数字
     * @param ruleList
     * @return
     */
    public BigDecimal getOccurAmount(List<LuckyRule> ruleList){
        LuckyRule rule = ruleList.get(0);
        if(Const.LUCKY_RULE_TYPE_MAX.equals(rule.getRuleType())){
            BigDecimal jackpotSize = rule.getJackpotSize();
            BigDecimal occurAmount = rule.getOccurAmount();
            int radixPoint = rule.getRadixPoint();
            double start = CommonUtil.roundDOWN(rule.getStartSize().doubleValue(),radixPoint);
            double end = CommonUtil.roundDOWN(rule.getEndSize().doubleValue(),radixPoint);
            String nextDouble = nextDouble(start,end,radixPoint);
            if ((new BigDecimal(nextDouble).add(occurAmount)).compareTo(jackpotSize)== 1){
                return jackpotSize.subtract(occurAmount).compareTo(new BigDecimal(0))==1?jackpotSize.subtract(occurAmount):new BigDecimal(0);
            }
            return new BigDecimal(nextDouble);
        }else if (Const.LUCKY_RULE_TYPE_ZH.equals(rule.getRuleType())){
            //预留
        }
        return new BigDecimal(0);
    }

    public static String nextDouble(final double min, final double max,int lang) {
        double reurn = min + ((max - min) * new Random().nextDouble());
        reurn = CommonUtil.roundDOWN(reurn,lang);
        if(reurn == max){
            reurn = min;
        }
        return CommonUtil.getAmountAddZERO(new BigDecimal(reurn),lang);
    }

    /**
     * @describe 查询当前活动规则
     * @param luckyId
     * @return
     */
    public LuckyRule getRuleInfo(String luckyId){
        LuckyRule  luckyRule = null;
        try {
            luckyRule =  (LuckyRule)Data.GetOne("select t.ruleId,t.luckyId,t.ruleType,t.radixPoint,t.jackpotSize,t.startSize,t.endSize,t.hitProbability,t.occurAmount,t.occurCount,t.isUse from luckyrule t where  t.luckyId = ? ", new Object[] { luckyId }, LuckyRule.class);
        }catch (Exception e){
            log.error("【抽奖】根据抽奖ID："+luckyId+"查询当前活动规则异常，异常信息为：",e);
            return luckyRule;
        }
        return luckyRule;
    }


    public Map<String,Object> getLuckyTmp(User user, String luckyId, List<LuckyRule> ruleList,int limitCount,String eventTitle,String ip){
        Map<String,Object>  reMap = new HashMap<String,Object>();
        reMap.put("occurAmout",new BigDecimal(0));
        reMap.put("userAmout",new BigDecimal(0));
        LuckyRule rule = ruleList.get(0);
        TransactionObject txObj = new TransactionObject();
        List<OneSql> batchSQL = new ArrayList<>();
        try{
            if(Const.LUCKY_RULE_TYPE_MAX.equals(rule.getRuleType())){
                String curr = TimeUtil.getFormatCurrentDateTime20();
                //更新用户资格信息
                BigDecimal occurAmount = getOccurAmount(ruleList);
                String updateQualify = "update luckyqualify y set y.isReceive = '02',y.occurAmount = ? ,y.ruleId = ? ,y.updateTime = ?,y.ip = ? where y.qId = (" +
                        "select v1.qId from (select t.qId from luckyqualify t where t.luckyId = ? and t.userId = ? and isReceive = '01' and ? BETWEEN t.startTime AND t.endTime order by t.createTime asc limit 1) v1 ) "+
                        " and ? >= (select * from (select count(1) from luckyqualify t where t.luckyId = ? and t.userId = ? and isReceive = '02') v2 )";
                batchSQL.add(new OneSql(updateQualify, 1, new Object[]{occurAmount,rule.getRuleId(),curr,ip,luckyId,user.get_Id(),curr,limitCount,luckyId,user.get_Id()}));
                //更新规则
                BigDecimal occurAmountRule =  rule.getOccurAmount().add(occurAmount);
                reMap.put("occurAmout",rule.getOccurAmount());
                String updateRule = "update luckyrule t set t.occurAmount = ? ,t.occurCount = t.occurCount+1,t.updateTime = ? where t.ruleId = ? and t.jackpotSize >= t.occurAmount";
                batchSQL.add(new OneSql(updateRule, 1, new Object[]{occurAmountRule,curr,rule.getRuleId()}));
                //更新用户金额
                if(occurAmount.compareTo(new BigDecimal(0))==1){
                    int fundsType = DatabasesUtil.coinProps("GBC").getFundsType();
                    int type = BillType.luckyIn.getKey();
                    Authentication au = new AuthenticationDao().getByUserId(user.getId());
                    if(au != null&& AuditStatus.a1Pass.getKey() == au.getStatus()){
                        String nowSql = "INSERT INTO bill (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType) " +
                                "SELECT '"+user.get_Id()+"','"+user.getUserName()+"',"+type+","+2+","+occurAmount+",'"+now()+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+" from pay_user where userId='" +user.get_Id() + "' AND fundsType = "+fundsType;
                        String newBillSql = "INSERT INTO billdistribution (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType, sourceRemark) " +
                                "SELECT '"+user.get_Id()+"','"+user.getUserName()+"',"+type+","+2+","+occurAmount+",'"+now()+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+", '"+ eventTitle+"' from pay_user where userId='" +user.get_Id() + "' AND fundsType = "+fundsType;
                        batchSQL.add(new OneSql("update pay_user set balance=balance+" + occurAmount + " where userId=? AND fundsType = ?", 1, new Object[] {user.get_Id(), fundsType}));
                        batchSQL.add(new OneSql(nowSql, 1, new Object[]{}));
                        batchSQL.add(new OneSql(newBillSql, 1, new Object[]{}));
                    }else{
                        String nowSql = "INSERT INTO bill (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType) " +
                                "SELECT '"+user.get_Id()+"','"+user.getUserName()+"',"+type+","+2+","+occurAmount+",'"+now()+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+" from pay_user where userId='" +user.get_Id() + "' AND fundsType = "+fundsType;
                        String newBillSql = "INSERT INTO billdistribution (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType, sourceRemark) " +
                                "SELECT '"+user.get_Id()+"','"+user.getUserName()+"',"+type+","+0+","+occurAmount+",'"+now()+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+", '"+ eventTitle+"' from pay_user where userId='" +user.get_Id() + "' AND fundsType = "+fundsType;
                        batchSQL.add(new OneSql("update pay_user set freez=freez+" + occurAmount + ", eventFreez = eventFreez + " +occurAmount +" where userId=? AND fundsType = ?", 1, new Object[] {user.get_Id(), fundsType}));
                        batchSQL.add(new OneSql(nowSql, 1, new Object[]{}));
                        batchSQL.add(new OneSql(newBillSql, 1, new Object[]{}));
                    }
                }
                txObj.excuteUpdateList(batchSQL);
                if(txObj.commit()){
                    UserCache.resetUserFunds(user.get_Id());
                    reMap.put("occurAmout",occurAmountRule);
                    reMap.put("userAmout",occurAmount);
                }else{
                    log.error("【抽奖】根据userId："+user.get_Id()+"及抽奖ID:"+luckyId+"更新抽奖金额："+occurAmount+"发生异常,回滚抽奖更新信息");
                }
                return reMap;
            }else if (Const.LUCKY_RULE_TYPE_ZH.equals(rule.getRuleType())){
                //预留
                return reMap;
            }
        }catch (Exception e){
            txObj.rollback("抽奖批次保存失败");
            log.error("【抽奖】根据userId："+user.get_Id()+"及抽奖ID:"+luckyId+"进行抽奖发生异常，异常信息为：",e);
        }
        return reMap;
    }

    /**
     * 实名认证成功后，解冻用户的抽奖活动奖金。
     */
    public void unEventFreez(String userId){
        if(!StringUtil.exist(userId)){
            log.error("【奖金解冻】实名认证通过，调用抽奖解冻参数为空");
            return;
        }
        List<OneSql> batchSQL = new ArrayList<>();
        List<BillDetails> billDistributions =  null;
        String sql = "select userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType from billdistribution t where  t.userId = ?  and status = 0 ";
        billDistributions = find(sql, new Object[]{userId}, BillDetails.class);
        if (!CollectionUtils.isEmpty(billDistributions)) {
            StringBuffer billSql = new StringBuffer();
            for(BillDetails vo:billDistributions){
                billSql.append("("+vo.getUserId() +",'"+vo.getUserName()+"',"+vo.getType()+","+vo.getStatus()+","+vo.getAmount()+",'"+vo.getSendTime()+"','"+vo.getRemark()+"',"+vo.getFees()+","+vo.getBalance()+",0,"+vo.getFundsType()+"),");
            }
            String nowSql = "INSERT INTO bill (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType) " +
                    "values "+billSql.substring(0,billSql.length() -1)+";";
//            batchSQL.add(new OneSql(nowSql, -2,new Object[]{}));
            int fundsType = DatabasesUtil.coinProps("GBC").getFundsType();
            String updateDistributions = "update billdistribution t set t.status = 2 where t.status = 0 and t.userId = "+userId;
            batchSQL.add(new OneSql(updateDistributions, -2, new Object[]{}));
            batchSQL.add(new OneSql("update pay_user set balance = balance +eventFreez , freez=freez-eventFreez ,eventFreez = 0 where userId=? AND fundsType = ?", 1, new Object[] {userId, fundsType}));
            if(Data.doTrans(batchSQL)){
                try {
                    UserCache.resetUserFunds(userId);
                }catch (Exception e){
                    log.info("【抽奖】刷新用户资金失败，异常信息为：",e);
                }
            }else{
                log.error("【抽奖】根据userId："+userId+"解冻用户抽奖资金发生异常，回滚解冻操作");
            }
        }
    }
}

