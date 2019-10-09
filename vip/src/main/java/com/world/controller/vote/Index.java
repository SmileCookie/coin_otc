package com.world.controller.vote;

import cn.jpush.api.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.dao.lucky.LuckyQualifyDao;
import com.world.model.dao.vote.ActivityLogDao;
import com.world.model.dao.vote.CoinDao;
import com.world.model.dao.vote.CountDao;
import com.world.model.dao.vote.VoteDao;
import com.world.model.entity.vote.Activity;
import com.world.model.entity.vote.ActivityLog;
import com.world.model.entity.vote.Coin;
import com.world.model.entity.vote.Count;
import com.world.model.service.VoteService;
import com.world.model.vote.service.ActivityInitService;
import com.world.util.ip.IpUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;


import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 *
 */
public class Index extends BaseAction {

    static Logger logger = Logger.getLogger(Index.class.getName());
    VoteDao voteDao = new VoteDao();
    CoinDao coinDao = new CoinDao();
    CountDao countDao = new CountDao();
    ActivityLogDao activityLogDao = new ActivityLogDao();
    VoteService voteService = new VoteService();
    ActivityInitService activityInitService = new ActivityInitService();
    LuckyQualifyDao luckyQualifyDao = new LuckyQualifyDao();

    /**
     * 获取活动地址，默认取最新的一个活动给前端
     * 刷新活动列表,根据时间更改当前活动状态，入库并入缓存
     */
    //@Page(Viewer = JSON)
    public void activity() {
        List<Activity> activityList = voteDao.getList(null, "cn", "");

        if (!CollectionUtils.isEmpty(activityList)) {
            for (Activity activity : activityList) {
                activityInitService.intoCache(activity);  //更改状态
            }
            Activity activity = filter();
            if (null != activity) {
                JSONObject js = new JSONObject();
                js.put("activityId", activity.getActivityId());
                js.put("state", activity.getState());
                js.put("url", activity.getUrl());
                json("activityList.get(0)", true, js.toJSONString());
            }
        } else {
            json("", false, "");
        }
    }

    /**
     * 进入主页时，判断当前活动id和活动状态是否存在，如果活动id不为空的话
     * 说明这是从后台发出的预览请求，不做任何判断，如果为空，则根据默认给用户的第一个活动的状态，
     * 如果是未开始的话，则判断如果活动状态时未开始，则重定向到主页。
     */
    //@Page(Viewer = "/cn/msg/vote.jsp")
    public void index() {
        try {
            String preview_state = param("preview_state");
            if (StringUtils.isEmpty(preview_state)) {
                Activity activity = filter();
                if (activity != null) {
                    if (activity.getState() == 0) {
                        response.sendRedirect(VIP_DOMAIN);
                        return;
                    }
                } else {
                    response.sendRedirect(VIP_DOMAIN);
                    return;
                }

            }
        } catch (Exception e) {
            logger.error(e);
        }


    }


    /***
     * 初始化投票页面，返回三种数据
     * 1.活动信息 activity
     * 2.币库信息
     * 3.用户投票记录，如果为登陆则不返回
     */
    //@Page(Viewer = JSON)
    public void init() {
        Boolean canVoteFlag = true;
        int canVoteCount = 0;
        String orderby = "";
        String activityId = param("activityId");
        String lan = param("lan");
        String userId = userIdStr();
        JSONObject js = new JSONObject();
        String state = param("state");
        if ("".equals(lan) || null == lan) {
            lan = "en";
        }

        Activity activity = voteDao.getOne(activityId, "", lan);
        if (activity != null) {
            js.put("activity", activity);
        }

        int countPeople = activityLogDao.countTicket(activityId);
        if (countPeople == 0) {
            orderby = "activityId";
        }
        List<Coin> coinList = coinDao.getList(activityId, lan, orderby);
        for (Coin coin : coinList) {
            coin.setRealCount(coin.getVoteCount());
        }
        if (coinList != null) {
            js.put("coin", coinList);
        }
        List<ActivityLog> activityLogList = new ArrayList<>();
        if (!"".equals(userId) && null != userId && !"0".equals(userId)) {
            activityLogList = activityLogDao.getList(activityId, userId, activity.getActivityLimit());
            if (activityLogList != null) {
                js.put("activityLog", activityLogList);
            }
            if (activityLogList.size() >= activity.getSelectCount() && activity.getSelectCount() != 0) { //如果投票记录中已经有了投票记录，则用户不能再投票
                canVoteFlag = false;
            }
            if (activity.getSelectCount() != 0) {
                canVoteCount = activity.getSelectCount() - activityLogList.size();
            }
            js.put("canVoteFlag", canVoteFlag);
            if (canVoteFlag) {
                js.put("canVoteCount", canVoteCount);
            } else {
                js.put("canVoteCount", "0");
            }

        } else {
            js.put("activityLog", activityLogList);
        }
        json("init", true, js.toJSONString());
    }


    /****
     * 提交投票信息，并做刷票验证：验证用户，投票限制，投票状态
     * 插入投票时，对记录表和统计表各插入一条记录，采取事物的形式
     */
    //@Page(Viewer = JSON)
    public void postVote() {
        String coinIds = param("coinIds");
        String activityId = param("activityId");
        String userId = userIdStr();
        String voteIp = IpUtil.getIp(request);
        String[] coinArray = coinIds.split(",");
        Boolean flag = false;
        if (null == coinIds) {
            json(L("投票失败，请联系管理员"), flag, "");
            return;
        }
        if (coinArray.length > 1) {
            json(L("投票失败，请联系管理员"), flag, "");
            return;
        }
        String redirectUrl = "";
        Activity activity = null;
        //   activity = (Activity) Cache.GetObj(activityId);
        JSONObject obj = (JSONObject) Cache.GetObj(activityId);
        String activityJson = obj.toJSONString();
        activity = com.alibaba.fastjson.JSON.parseObject(activityJson, Activity.class);
        if(StringUtils.isNotEmpty(userId) && !userId.equals("0")){
            if(CollectionUtils.isEmpty(countDao.getList(activityId,userId,activity.getActivityLimit()))){
                countDao.insert(activity.getActivityId(),userId,activity.getActivityLimit());
            }
        }
        if (activity == null) {
            json(L("投票失败，请联系管理员"), flag, "");
            return;
        }
       /* if ((coinArray.length > activity.getSelectCount()) && activity.getSelectCount() != 0) {
            json(L("您已投过票或超出最大投票限制"), flag, "");
            return;
        }*/
        if (userId == null || "".equals(userId) || userId.equals("0")) {
            json(L("请登陆后再操作"), flag, "");
            return;
        }

        if (coinIds == null || "".equals(coinIds)) {
            json(L("请选择投票选项后进行提交"), flag, "");
            return;
        }
        String cacheSyncKey = "post_vote_" + userId;
        synchronized ("post_vote_" + userId) {
            String lock = Cache.Get(cacheSyncKey);
            if (null != lock) {
                json(L("您的投票操作太频繁了，请稍后重试或刷新查看数据。"), false, "", true);
                return;
            }
            try {
                Cache.Set(cacheSyncKey, userId + "", 120);
                List<Integer> coinArrayList = new ArrayList<>();
                List<Integer> coinsList = (List<Integer>) Cache.GetObj("coins");
                if (CollectionUtils.isEmpty(coinsList)) {
                    List<Coin> coinList = coinDao.getList(activityId, lan, "");
                    if (!CollectionUtils.isEmpty(coinList)) {
                        for (Coin coin : coinList) {
                            coinArrayList.add(coin.getCoinId());
                        }
                        Cache.SetObj("coins", coinArrayList);
                    }
                }
                for (String vote : coinArray) {
                    if (!coinsList.contains(Integer.parseInt(vote))) {
                        json(L("投票失败，请联系管理员"), flag, "");
                        return;
                    }
                }
                activity = voteDao.getOne(activityId, "", lan);
                if (activity.getState() != 1) {
                    json(L("投票已结束"), flag, "");
                    return;
                }
                List<ActivityLog> activityLogList = activityLogDao.getList(activityId, userId, activity.getActivityLimit());
                for (ActivityLog activityLog : activityLogList) {
                    if (activityLog.getVoteId() == Integer.valueOf(coinIds)) {
                        json(L("投票失败，请联系管理员"), flag, "");
                        return;
                    }
                }
                if (activityLogList.size() >= activity.getSelectCount() && activity.getSelectCount() != 0) {
                    json(L("您已投过票或超出最大投票限制"), flag, "");
                    return;
                } else {

                    flag = voteService.insertVote(activityId, userId, coinArray, voteIp,activity.getSelectCount(),activity.getActivityLimit()); //进行事务投票
                    JSONObject jsonObject = new JSONObject();
                    if (flag) {
                        List<ActivityLog> activityLog = activityLogDao.getList(activityId, userId, activity.getActivityLimit());
                        if (activityLog.size() == 1) {
                            Map<String, String> map = luckyQualifyDao.insertQualify(userId, activityId,voteIp);
                            if (map.get("isShow").equals("1")) {
                                redirectUrl = map.get("url");
                                jsonObject.put("flag", true);
                                jsonObject.put("url", redirectUrl);
                                json(L("投票成功，恭喜您获得一次抽奖机会。"), flag, jsonObject.toJSONString());

                            } else {
                                jsonObject.put("flag", false);
                                jsonObject.put("url", redirectUrl);
                                json(L("投票成功,感谢您的参与。"), flag, jsonObject.toJSONString());
                            }
                        } else {
                            jsonObject.put("flag", false);
                            jsonObject.put("url", redirectUrl);
                            json(L("投票成功,感谢您的参与。"), flag, jsonObject.toJSONString());
                        }
                    } else {
                        json(L("网络异常，请您再次尝试。"), flag, "");
                        return;
                    }

                }
            } catch (Exception e) {
                log.error("内部错误", e);
            } finally {
                try {
                    Cache.Delete(cacheSyncKey);
                } catch (Exception e) {
                }
            }


        }
    }

    /**
     * 定时获取任务时间及服务器时间，用作前台展示活动倒计时
     */
    //@Page(Viewer = JSON)
    public void time() {
        Activity activity = null;
        String activityId = param("activityId");
        if (activityId == null || "".equals(activityId)) {
            return;
        }
        JSONObject js = new JSONObject();
        //   activity = (Activity) Cache.GetObj(activityId);
        JSONObject obj = (JSONObject) Cache.GetObj(activityId);
        if(null != obj){
            String activityJson = obj.toJSONString();
            activity = com.alibaba.fastjson.JSON.parseObject(activityJson, Activity.class);
        }
        if (activity != null) {
            js.put("startTime", activity.getStartTime());
            js.put("endTime", activity.getEndTime());
        } else {
            activity = voteDao.getOne(activityId, "", "");
            if (activity == null) {
                return;
            }
            Cache.SetObj(activityId, com.alibaba.fastjson.JSON.toJSON(activity));  //后台编辑以后会及时更新缓存中信息
            js.put("startTime", activity.getStartTime());
            js.put("endTime", activity.getEndTime());
        }
        js.put("currentTime", System.currentTimeMillis());
        json("活动时间", true, js.toJSONString());
    }


    public Activity filter() {
        try {
            List<Activity> startingActivityList = voteDao.getList("1", "cn", "");
            if (!CollectionUtils.isEmpty(startingActivityList)) {
                return startingActivityList.get(0);
            }
            List<Activity> pauseActivityList = voteDao.getList("2", "cn", "");
            if (!CollectionUtils.isEmpty(pauseActivityList)) {
                return pauseActivityList.get(0);
            }
            List<Activity> endActivityList = voteDao.getList("3", "cn", "");
            if (!CollectionUtils.isEmpty(endActivityList)) {
                return endActivityList.get(0);
            }
            List<Activity> notStartedActivityList = voteDao.getList("0", "cn", "");
            if (!CollectionUtils.isEmpty(notStartedActivityList)) {
                return notStartedActivityList.get(0);
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return null;

    }


}
