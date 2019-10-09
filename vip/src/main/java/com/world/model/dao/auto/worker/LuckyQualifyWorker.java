package com.world.model.dao.auto.worker;

import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.lucky.LuckyEventDao;
import com.world.model.dao.lucky.LuckyQualifyDao;
import com.world.model.dao.task.Worker;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.lucky.LuckyEvent;
import com.world.model.entity.user.User;
import com.world.model.entity.user.authen.Authentication;
import com.world.model.entity.vote.ActivityTicketVo;
import com.world.model.vote.service.ActivityInitService;
import com.world.util.date.TimeUtil;
import com.world.util.string.StringUtil;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LuckyQualifyWorker extends Worker {

    LuckyEventDao luckyEventDao = new LuckyEventDao();
    ActivityInitService activityInitService = new ActivityInitService();
    LuckyQualifyDao luckyQualifyDao = new LuckyQualifyDao();
    UserDao userDao = new UserDao();

    public LuckyQualifyWorker(String name, String des) {
        super(name, des);
    }
    @Override
    public void run() {
        super.run();
        try {
            //再赋予一次抽奖机会
            synUserQualify();
        }catch (Exception e){
            //捕获异常防止第一个方法执行失败，导致后续无法继续执行
            log.error("【再赋予一次抽奖机会】发生非受控异常信息，异常信息为：",e);
        }
        try {
            //奖金翻倍
            luckyDouble();
        }catch (Exception e){
            //捕获异常防止第一个方法执行失败，导致后续无法继续执行
            log.error("【再赋予一次抽奖机会】发生非受控异常信息，异常信息为：",e);
        }
    }

    //再赋予一次抽奖机会
    public void synUserQualify(){
        log.info("再赋予一次抽奖机会开始");
        LuckyEvent luckyEvent = luckyEventDao.getEventAndActivity();
        if(luckyEvent != null){
            log.info("再赋予一次抽奖机会开始,获取订单:"+luckyEvent.getLuckyId());
            String batchSql = "";
            ActivityTicketVo activityTicketVo = null;
            activityTicketVo = activityInitService.get1(luckyEvent.getRelateEventId());
            if(activityTicketVo!= null&&3==activityTicketVo.getState()&&!CollectionUtils.isEmpty(activityTicketVo.getUserIdList())&&activityTicketVo.getUserIdList().size()>0){
                log.info("再赋予一次抽奖机会开始,获取投票活动信息");
                String curr = TimeUtil.getFormatCurrentDateTime20();
                List<String> list = activityTicketVo.getUserIdList();
                for(String str:list){
                    batchSql += "('" + luckyEvent.getLuckyId() + "', '" + "" + "', '" + str + "', '" + TimeUtil.parseDate(luckyEvent.getStartTime().getTime()) + "', '" + TimeUtil.parseDate(luckyEvent.getEndTime().getTime()) + "', '" + new BigDecimal(0)
                            + "','01','01' " + ", '" + curr + "', '" + curr + "'),";
                }
                if(StringUtil.exist(batchSql)) {
                    batchSql = batchSql.substring(0,batchSql.length()-1)+";";
                    batchSql = "insert into luckyqualify (luckyId,ruleId,userId,startTime,endTime,occurAmount,isReceive,source,updateTime,createTime) values " + batchSql;
                }
                List<OneSql> batchSQL = new ArrayList<>();
                batchSQL.add(new OneSql(batchSql, activityTicketVo.getUserIdList().size(), new Object[]{}));

                String updateRule = "update luckyevent t set t.isHighSyn = '02',t.updateTime = '"+curr+"' where t.luckyId = ? ";
                batchSQL.add(new OneSql(updateRule, 1, new Object[]{luckyEvent.getLuckyId()}));
                Data.doTrans(batchSQL);
                log.info("再赋予一次抽奖机会执行结束");
            }else{
                log.info("再赋予一次抽奖机会,投票尚未结束");
            }
        }
    }
    //奖金翻倍
    public void luckyDouble(){
        log.info("【奖金翻倍】开始");
        LuckyEvent luckyEvent = luckyEventDao.getEventEnd();
        if(luckyEvent != null){

            ActivityTicketVo activityTicketVo = null;
            activityTicketVo = activityInitService.get1(luckyEvent.getRelateEventId());
            if(activityTicketVo!= null&&3==activityTicketVo.getState()&&!CollectionUtils.isEmpty(activityTicketVo.getUserIdList())&&activityTicketVo.getUserIdList().size()>0){
                List<OneSql> userBatchSQL = new ArrayList<>();
                log.info("【奖金翻倍】开始,获取投票活动信息");
                String curr = TimeUtil.getFormatCurrentDateTime20();
                List<String> list = activityTicketVo.getUserIdList();
                for(String userId:list){
                    String batchSql = "";
                    BigDecimal amount = luckyQualifyDao.getReceived(luckyEvent.getLuckyId(),userId);
                    if(amount.compareTo(BigDecimal.ZERO) == 1){
                       User user =  userDao.getUserById(userId);
                       if(user == null){
                           log.error("【奖金翻倍】根据用户ID："+userId+"查询用户信息为空！！！");
                           continue;
                       }
                        batchSql += "('" + luckyEvent.getLuckyId() + "', '" + "" + "', '" + userId + "', '" + TimeUtil.parseDate(luckyEvent.getStartTime().getTime()) + "', '" + TimeUtil.parseDate(luckyEvent.getEndTime().getTime()) + "', '" + amount
                                + "','02','03' " + ", '" + curr + "', '" + curr + "','02'),";
                        if(StringUtil.exist(batchSql)) {
                            batchSql = batchSql.substring(0,batchSql.length()-1)+";";
                            batchSql = "insert into luckyqualify (luckyId,ruleId,userId,startTime,endTime,occurAmount,isReceive,source,updateTime,createTime,isShow) values " + batchSql;
                        }
                        userBatchSQL.add(new OneSql(batchSql, -2, new Object[]{}));

                        int fundsType = DatabasesUtil.coinProps("GBC").getFundsType();
                        int type = BillType.luckyDouble.getKey();
                        Authentication au = new AuthenticationDao().getByUserId(userId);
                        if(au != null&& AuditStatus.a1Pass.getKey() == au.getStatus()){
                            String nowSql = "INSERT INTO bill (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType) " +
                                    "SELECT '"+userId+"','"+user.getUserName()+"',"+type+","+2+","+amount+",'"+curr+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+" from pay_user where userId='" +userId + "' AND fundsType = "+fundsType;
                            String newBillSql = "INSERT INTO billdistribution (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType, sourceRemark) " +
                                    "SELECT '"+userId+"','"+user.getUserName()+"',"+type+","+2+","+amount+",'"+curr+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+", '"+ luckyEvent.getEventTitleJson()+"' from pay_user where userId='" +userId + "' AND fundsType = "+fundsType;
                            userBatchSQL.add(new OneSql("update pay_user set balance=balance+" + amount + " where userId=? AND fundsType = ?", 1, new Object[] {userId, fundsType}));
                            userBatchSQL.add(new OneSql(nowSql, 1, new Object[]{}));
                            userBatchSQL.add(new OneSql(newBillSql, 1, new Object[]{}));
                        }else{
                            String nowSql = "INSERT INTO bill (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType) " +
                                    "SELECT '"+userId+"','"+user.getUserName()+"',"+type+","+2+","+amount+",'"+curr+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+" from pay_user where userId='" +userId + "' AND fundsType = "+fundsType;
                            String newBillSql = "INSERT INTO billdistribution (userId, userName, type, status, amount, sendTime, remark, fees, balance, adminId, fundsType, sourceRemark) " +
                                    "SELECT '"+userId+"','"+user.getUserName()+"',"+type+","+0+","+amount+",'"+curr+"','"+"抽奖赠送"+"',"+new BigDecimal(0)+",balance+freez as balance,0"+ ", "+ fundsType+", '"+ luckyEvent.getEventTitleJson()+"' from pay_user where userId='" +userId + "' AND fundsType = "+fundsType;
                            userBatchSQL.add(new OneSql("update pay_user set freez=freez+" + amount + ", eventFreez = eventFreez + " +amount +" where userId=? AND fundsType = ?", 1, new Object[] {userId, fundsType}));
                            userBatchSQL.add(new OneSql(newBillSql, 1, new Object[]{}));
                            userBatchSQL.add(new OneSql(nowSql, 1, new Object[]{}));
                        }
                        Cache.Set("luckyDouble_"+userId,"02");
                        try {
                            UserCache.resetUserFunds(userId);
                        }catch (Exception e){
                            log.info("【奖金翻倍】,刷新用户资金失败，异常信息为：",e);
                        }
                    }
                }
                String updateRule = "update luckyevent t set t.isDoubleSyn = '02',t.updateTime = '"+curr+"' where t.luckyId = ? ";
                userBatchSQL.add(new OneSql(updateRule, 1, new Object[]{luckyEvent.getLuckyId()}));
                Data.doTrans(userBatchSQL);
            }else{
                log.info("【奖金翻倍】,投票尚未结束");
            }
        }
    }

}
