package com.world.model.dao.vote;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.Query;
import com.world.model.entity.lucky.LuckyEvent;
import com.world.model.entity.vote.ActivityLog;
import com.world.model.entity.vote.ActivityLogVo;
import com.world.model.entity.vote.Count;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityLogDao extends DataDaoSupport<ActivityLog> {
    Logger logger = Logger.getLogger(ActivityLogDao.class);


    /**
     * 根据活动id，用户id获取活动记录列表
     *
     * @param activityId
     * @param userId
     * @return
     */
    public List<ActivityLog> getList(String activityId, String userId, int activityLimit) {
        List<ActivityLog> activityLogList = new ArrayList<>();
        String sql = "";
        try {
            Timestamp toodayFirst= TimeUtil.getTodayFirst();
            Timestamp toodayLast= TimeUtil.getTodayLast();
            if (activityLimit == 2) {  //用户活动期间每天能投一次
                sql = "select id,voteId,userId,activityId,voteTime,voteIp from activityLog where  activityId= ? and userId = ? and voteTime>=? and voteTime<? for update";
                activityLogList = find(sql, new Object[]{activityId, userId,TimeUtil.parseDate(toodayFirst.getTime()),TimeUtil.parseDate(toodayLast.getTime())}, ActivityLog.class);
            } else  {   //用户活动期间只能投一次
                sql = "select id,voteId,userId,activityId,voteTime,voteIp from activityLog where  activityId= ? and userId = ? for update ";
                activityLogList = find(sql, new Object[]{activityId, userId}, ActivityLog.class);
            }
        } catch (Exception e) {
            logger.error("获取投票记录失败", e);
        }
        return activityLogList;
    }





    /**
     * 插入投票记录
     *
     * @param activityId
     * @param userId
     * @param voteId
     * @param voteIp
     * @return
     */
    public Boolean insert(String activityId, String userId, String voteId, String voteIp) {
        Boolean flag = false;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(new Date());
            String sql = "insert into activityLog (activityId,userId,voteId,voteTime,voteIp) values(?,?,?,?,?)";
            int row = save(sql, new Object[]{activityId, userId, Integer.parseInt(voteId), dateString, voteIp});
            if (row != -1) {
                flag = true;
            }
            return flag;
        } catch (Exception e) {
            logger.error("活动创建失败", e);
            return false;
        }
    }

    /**
     * 统计参与投票人数
     *
     * @param activityId
     * @return
     */
    public int countPeople(String activityId) {
        int row = 0;
        try {
            Query logQuery = getQuery();
            logQuery.setCls(ActivityLog.class);
            String sql = "select count(*) from activityLog where activityId=? group by userId ";
            row = count(sql, new Object[]{activityId});
        } catch (Exception e) {
            logger.error("统计活动人数失败", e);
        }
        return row;
    }


    public List<String> getPeople(String activityId,int coinId){
        List<String> userList = new ArrayList<>();
        try{
            String sql="select distinct userid from activitylog where activityId=? and voteId=?";
            userList = (List<String>)Data.Query(sql,new Object[]{activityId,coinId});
        }catch (Exception e){
            logger.error("统计活动人数失败", e);
        }
        return  userList;

    }

    public List<String> getPeopleList(String activityId,int coinId){
        List<ActivityLog> userList = new ArrayList<>();
        List<String> retList = new ArrayList<String>();
        try{
            String sql="select distinct userid from activitylog where activityId=? and voteId=?";
            userList = find(sql, new Object[]{activityId,coinId}, ActivityLog.class);
            if(!CollectionUtils.isEmpty(userList)) {
                for(ActivityLog vo:userList){
                    retList.add(vo.getUserId());
                }
            }
        }catch (Exception e){
            logger.error("统计活动人数失败", e);
        }
        return  retList;

    }


    /**
     * 查询是否有票数
     *
     * @param activityId
     * @return
     */
    public int countTicket(String activityId) {
        int row = 0;
        try {
            Query logQuery = getQuery();
            logQuery.setCls(ActivityLog.class);
            String sql = "select count(*) from activityCoin where voteCount>0 and activityId=? ";
            row = count(sql, new Object[]{activityId});
        } catch (Exception e) {
            logger.error("统计活动人数失败", e);
        }
        return row;
    }


    public static void main(String[] args) {
        ActivityLogDao activityLogDao = new ActivityLogDao();
        activityLogDao.count();


    }


}
