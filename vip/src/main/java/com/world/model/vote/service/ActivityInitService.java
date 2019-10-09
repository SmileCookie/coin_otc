package com.world.model.vote.service;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.dao.vote.ActivityLogDao;
import com.world.model.dao.vote.CoinDao;
import com.world.model.dao.vote.VoteDao;
import com.world.model.entity.vote.Activity;
import com.world.model.entity.vote.ActivityTicketVo;
import com.world.model.entity.vote.Coin;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityInitService {
    VoteDao voteDao = new VoteDao();
    CoinDao coinDao = new CoinDao();
    ActivityLogDao activityLogDao = new ActivityLogDao();

    public List<Activity> init() {
        List<Activity> activityList = new ArrayList<Activity>();
        try {
            activityList = voteDao.getList("", "", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activityList;
    }


    public int update(int state, String activityId) {
        int a = -1;
        try {
            a = Data.Update("UPDATE activity SET state=? WHERE activityId=?", new Object[]{state, activityId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return a;

    }

    /**
     * 根据时间范围更改活动状态
     * 状态：0：未开始，1进行中 ，2暂停，3结束，4删除
     *
     * @param activity
     * @return
     */
    public Activity intoCache(Activity activity) {
        Long startTime = activity.getStartTime().getTime();
        Long endTime = activity.getEndTime().getTime();
        String key = activity.getActivityId();
        Long now = (new Date()).getTime();
        if ((startTime <= now) && (now <= endTime)) {
            if (activity.getState() != 1 && activity.getState() != 2) {
                activity.setState(1);
                int row = update(1, key);
                if (row != -1) {
                    Cache.SetObj(key, com.alibaba.fastjson.JSON.toJSON(activity));
                }
            }
        } else if (now >= endTime) {
            if (activity.getState() != 3) {
                activity.setState(3);
                int row = update(3, key);
                if (row != -1) {
                    Cache.SetObj(key, com.alibaba.fastjson.JSON.toJSON(activity));
                }
            }
        } else if (now < startTime) {
            if (activity.getState() != 0) {
                activity.setState(0);
                int row = update(0, key);
                if (row != -1) {
                    Cache.SetObj(key, com.alibaba.fastjson.JSON.toJSON(activity));
                }
            }
        }
        return activity;
    }


    /**
     * 根据活动id，获取活动的状态及投票冠军结果
     *
     * @param activityId
     * @return
     */
    public ActivityTicketVo get(String activityId) {
        ActivityTicketVo activityTicketVo = new ActivityTicketVo();
        List<Activity> activityList = voteDao.getList(null, "cn", "");
        if (!CollectionUtils.isEmpty(activityList)) {
            for (Activity activity : activityList) {
                intoCache(activity);  //更改状态
            }
        }


        JSONObject obj = (JSONObject) Cache.GetObj(activityId);
        String activityJson = obj.toJSONString();
        Activity activityCache = com.alibaba.fastjson.JSON.parseObject(activityJson, Activity.class);
        activityTicketVo.setState(activityCache.getState());
        if (activityTicketVo.getState() == 3) {
            List<Coin> coinList = coinDao.getList(activityId, "cn", "");
            if (!CollectionUtils.isEmpty(coinList)) {
                int coinId = coinList.get(0).getCoinId();
                activityTicketVo.setUserIdList(activityLogDao.getPeople(activityId, coinId));
            }
        }
        return activityTicketVo;
    }

    /**
     * 根据活动id，获取活动的状态及投票冠军结果
     *
     * @param activityId
     * @return
     */
    public ActivityTicketVo get1(String activityId) {
        ActivityTicketVo activityTicketVo = new ActivityTicketVo();
        List<Activity> activityList = voteDao.getList(null, "cn", "");
        Activity activityCache = new Activity();
        if (!CollectionUtils.isEmpty(activityList)) {
            for (Activity activity : activityList) {
                intoCache(activity);  //更改状态
                if(activity.getActivityId().equals(activityId)){
                    activityCache = activity;
                }
            }
        }
        JSONObject obj = (JSONObject) Cache.GetObj(activityId);
        if (null == obj) {
            return null;
        }
        activityTicketVo.setState(activityCache.getState());
        if (activityTicketVo.getState() == 3) {
            List<Coin> coinList = coinDao.getList(activityId, "cn", "");
            if (!CollectionUtils.isEmpty(coinList)) {
                int coinId = coinList.get(0).getCoinId();
                activityTicketVo.setUserIdList(activityLogDao.getPeopleList(activityId, coinId));
            }
        }
        return activityTicketVo;
    }



    public static void main(String args[]){
    ActivityInitService activityInitService = new ActivityInitService();
        ActivityTicketVo activityTicketVo = activityInitService.get1("5a3dc6c819484ec1aba4d7d2489b93b3");
        int a = 0;
        System.out.print("000");

    }


}
