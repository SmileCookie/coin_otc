package com.world.model.service;

import com.world.data.mysql.Data;
import com.world.model.dao.vote.ActivityLogDao;
import com.world.model.dao.vote.CoinDao;
import com.world.data.mysql.OneSql;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VoteService {
    static Logger logger = Logger.getLogger(VoteService.class.getName());
    CoinDao coinDao = new CoinDao();
    ActivityLogDao activityLogDao = new ActivityLogDao();

    /**
     * 事务型投票,如果失败则回滚,limitCount 2每天一次，1整体一次
     *
     * @param activityId
     * @param userId
     * @param voteId
     * @param voteIp
     * @return
     */
    public Boolean insertVote(String activityId, String userId, String[] voteId, String voteIp,int selectCount,int limitCount) {
        Boolean flag = false;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(new Date());
            List<OneSql> paySqls = new ArrayList<>();
            for (String vote : voteId) {
                if(limitCount== 2){
                    String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
                    paySqls.add(new OneSql("update count set voteTimes = voteTimes + 1 where activityId =? and userId=? and date =? and voteTimes <?", 1,
                            new Object[]{activityId, userId, date,selectCount}));
                }else{
                    String date = "0";
                    paySqls.add(new OneSql("update count set voteTimes = voteTimes + 1 where activityId =? and userId=? and date =? and voteTimes <?", 1,
                            new Object[]{activityId, userId, date,selectCount}));
                }
                paySqls.add(new OneSql("insert into activityLog (activityId,userId,voteId,voteTime,voteIp) values(?,?,?,?,?)", 1,
                        new Object[]{activityId, userId, vote, dateString, voteIp}));
                paySqls.add(new OneSql("update activityCoin SET voteCount=voteCount+1,realCount=realCount+1 where activityId =? and coinId=?", 1,
                        new Object[]{activityId, Integer.parseInt(vote)}));
            }
            flag = Data.doTrans(paySqls);
        } catch (Exception e) {
            logger.error("投票失败", e);
        }
        return flag;
    }


    public static void main(String[] args) {
        VoteService voteService = new VoteService();
        String[] a = {"1"};
//        Boolean flag = voteService.insertVote("12", "2", a, "127.0.0.2");
//        System.out.println("0000000" + flag);

    }

}