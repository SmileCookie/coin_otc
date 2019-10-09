package com.world.model.dao.vote;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.vote.Activity;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class VoteDao extends DataDaoSupport<Activity> {
    Logger logger = Logger.getLogger(VoteDao.class);

    /**
     * 根据条件获取活动内容
     * @param activityId
     * @param state
     * @param lan
     * @return
     */
    public Activity getOne(String activityId, String state, String lan) {
        Activity activity = null;
        StringBuilder where = new StringBuilder();
        try {
            if (!"".equals(activityId) && null != activityId) {
                where.append(" AND activityId =  '" + activityId + "'");
            }
            if (!"".equals(state) && null != state) {
                where.append(" AND state = " + Integer.parseInt(state));
            }
            String w = where.toString();
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select * from activity " + w + " ";
            activity = getT(sql, null, Activity.class);
            if(activity != null){
                JSONObject jsonName = JSONObject.parseObject(activity.getActivityNameJson());
                JSONObject jsonRule = JSONObject.parseObject(activity.getActivityRuleJson());
                JSONObject jsonContent = JSONObject.parseObject(activity.getActivityContentJson());
                if (!lan.equals("") && null != lan) {
                    activity.setActivityNameJson(jsonName.getString(lan));
                    activity.setActivityRuleJson(jsonRule.getString(lan));
                    activity.setActivityContentJson(jsonContent.getString(lan));
                }
            }
        } catch (Exception e) {
            logger.error("查询活动信息失败!", e);
        }
        return activity;
    }


    /**
     * 根据条件获取活动列表
     *
     * @param state:状态
     * @param lan：语言
     * @param activityName
     * @return
     */
    public List<Activity> getList(String state, String lan, String activityName) {
        List<Activity> activityList = new ArrayList<>();
        StringBuilder where = new StringBuilder();
        String orderType="";
        try {
            if (!"".equals(state) && null != state) {
                where.append(" AND state in ( " + state+ ")");
            } else {
                where.append(" AND state <> 4");
            }
            if (!"".equals(activityName) && null != activityName) {
                where.append(" AND activityNameJson like '%" + activityName + "%'");
            }
            String w = where.toString();
            if(("0").equals(state)){  //未开始的活动选择开始时间最近的
                orderType=" order by startTime ASC";
            }else if(("3").equals(state)){ //已结束的活动选择结束时间最新的
                orderType=" order by endTime DESC";
            }else{
                orderType=" order by createTime DESC";
            }
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select * from activity " + w + orderType;
            logger.info(sql);
            activityList = find(sql, null, Activity.class);
            for (Activity activity : activityList) {
                JSONObject jsonName = JSONObject.parseObject(activity.getActivityNameJson());
                JSONObject jsonRule = JSONObject.parseObject(activity.getActivityRuleJson());
                JSONObject jsonContent = JSONObject.parseObject(activity.getActivityContentJson());
                if (!lan.equals("") && null != lan) {
                    activity.setActivityNameJson(jsonName.getString(lan));
                    activity.setActivityRuleJson(jsonRule.getString(lan));
                    activity.setActivityContentJson(jsonContent.getString(lan));
                }
            }
        } catch (Exception e) {
            logger.error("查询活动列表失败", e);
        }
        return activityList;

    }







    public static void main(String[] args) {
        VoteDao voteDao = new VoteDao();
        List<Activity>  list=voteDao.getList("1,2,3,0","cn",null);
        System.out.println(list.size());
    }


}
