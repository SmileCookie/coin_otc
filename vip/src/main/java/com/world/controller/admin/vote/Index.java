package com.world.controller.admin.vote;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.vote.ActivityLogDao;
import com.world.model.dao.vote.CoinDao;
import com.world.model.dao.vote.VoteDao;
import com.world.model.entity.vote.Activity;
import com.world.model.entity.vote.Coin;
import com.world.model.vote.service.ActivityInitService;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@FunctionAction(jspPath = "/admins/vote/", des = "投票管理")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    VoteDao voteDao = new VoteDao();
    CoinDao coinDao = new CoinDao();
    ActivityLogDao activityLogDao = new ActivityLogDao();
    ActivityInitService activityInitService = new ActivityInitService();

    /**
     * 进入投票管理页面
     * 刷新缓存并更改任务状态
     */
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        try {
            String activityId = param("activityId");
            String activityName = param("activityName");
            String state = param("state");
            String lan = param("lan");
            List<Activity> activityList = new ArrayList<Activity>();
            activityList = voteDao.getList(state, "cn", activityName);
            if (!CollectionUtils.isEmpty(activityList)) {
                for (Activity activity : activityList) {
                    activityInitService.intoCache(activity);
                    activity.setCreateUser(adminName());
                }
            }
            setAttr("dataList", activityList);
        } catch (Exception e) {
            logger.error("进入投票管理页面失败", e);
        }


    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

    /**
     * 进入投票编辑页面
     * 根据活动id，查询活动信息并填充编辑页
     */
    @Page(Viewer = DEFAULT_AORU)
    public void aoru() {
        try {
            String activityId = param("id");
            if (null != activityId && !"".equals(activityId)) {
                Activity activity = new Activity();
                activity = voteDao.getOne(activityId, "", "");
                JSONObject jsonName = JSONObject.parseObject(activity.getActivityNameJson());
                JSONObject jsonRule = JSONObject.parseObject(activity.getActivityRuleJson());
                JSONObject jsonContent = JSONObject.parseObject(activity.getActivityContentJson());
                activity.setActivityNameSimple(jsonName.getString("cn"));
                activity.setActivityNameTraditional(jsonName.getString("hk"));
                activity.setActivityNameEnglish(jsonName.getString("en"));

                activity.setActivityRuleSimple(jsonRule.getString("cn"));
                activity.setActivityRuleTraditional(jsonRule.getString("hk"));
                activity.setActivityRuleEnglish(jsonRule.getString("en"));

                activity.setActivityContentSimple(jsonContent.getString("cn"));
                activity.setActivityContentTraditional(jsonContent.getString("hk"));
                activity.setActivityContentEnglish(jsonContent.getString("en"));

                setAttr("n", activity);
            }
        } catch (Exception ex) {
            logger.error("进入编辑投票页面失败", ex);
        }
    }

    /**
     * 活动创建
     */
    @Page(Viewer = ".xml")
    public void insert() {
        try {
            String activityNameSimple = param("activityNameSimple");
            String activityNameTraditional = param("activityNameTraditional");
            String activityNameEnglish = param("activityNameEnglish");
            String activityContentSimple = request.getParameter("activityContentSimple").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String activityContentTraditional = request.getParameter("activityContentTraditional").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String activityContentEnglish = request.getParameter("activityContentEnglish").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String activityRuleSimple = request.getParameter("activityRuleSimple").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String activityRuleTraditional = request.getParameter("activityRuleTraditional").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            String activityRuleEnglish = request.getParameter("activityRuleEnglish").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            int selectCount = intParam("selectCount");
            Timestamp startTime = dateParam("startTime");
            int activityLimit = intParam("activityLimit");    //本次活动默认投票限制
            String dataBaseName = "coin";  //本次币库默认值，该字段未启用
            int state = 0;                    //创建活动时默认状态为未开始
            Timestamp endTime = dateParam("endTime");
            List<Activity> activityList = voteDao.getList(null, "cn", "");
            if (!CollectionUtils.isEmpty(activityList)) {
                for (Activity activity : activityList) {
                    Boolean flag = TimeUtil.isOverlap(startTime, endTime, activity.getStartTime(), activity.getEndTime());
                    if (flag == true) {
                        Write("操作失败,活动进行时间冲突", false, "操作失败");
                        return;
                    }
                }

            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String begin = format.format(startTime);
            String end = format.format(endTime);
            String dateString = format.format(new Date());
            String url = "/vote";  //本次投票活动默认地址
            Date date1 = format.parse(begin);
            Date date2 = format.parse(end);
            JSONObject activityNameJson = new JSONObject();
            JSONObject activityContentJson = new JSONObject();
            JSONObject activityRuleJson = new JSONObject();
            activityNameJson.put("en", activityNameEnglish);
            activityNameJson.put("cn", activityNameSimple);
            activityNameJson.put("hk", activityNameTraditional);
            activityContentJson.put("en", activityContentEnglish);
            activityContentJson.put("cn", activityContentSimple);
            activityContentJson.put("hk", activityContentTraditional);
            activityRuleJson.put("en", activityRuleEnglish);
            activityRuleJson.put("cn", activityRuleSimple);
            activityRuleJson.put("hk", activityRuleTraditional);
            Activity activity = new Activity();
            activity.setActivityNameJson(activityNameJson.toString());
            activity.setActivityContentJson(activityContentJson.toString());
            activity.setActivityRuleJson(activityRuleJson.toString());
            activity.setSelectCount(selectCount);
            activity.setUrl(url);  //当前版本默认的投票页面，后续版本中根据前台活动地址配置
            activity.setStartTime(date1);
            activity.setEndTime(date2);
            activity.setCreateUser(adminName());
            activity.setCreateTime(new Date());
            activity.setState(0); //任务创建初始状态*/
            String activityId = UUID.randomUUID().toString().replaceAll("\\-", "");
            activity.setActivityId(activityId);
            String sql = "insert into activity(activityId, activityNameJson, activityRuleJson, activityContentJson, startTime, endTime, " + "activityLimit, dataBaseName, state, createUser, createTime, selectCount, url) " + "values ('" + activityId + "', '" + activityNameJson + "','" + activityRuleJson + "','" + activityContentJson + "','" + begin + "','" + end + "','" + activityLimit + "','" + dataBaseName + "','" + state + "','" + adminName() + "','" + dateString + "','" + selectCount + "','" + url + "')";
            List<OneSql> paySqls = new ArrayList<>();
            String sql2 = "INSERT INTO activitycoin(activityId,coinId) SELECT '" + activityId + " ',coinId FROM coin";
            int coinCount = coinDao.coinCount();
            paySqls.add(new OneSql(sql, 1, new Object[]{}));
            paySqls.add(new OneSql(sql2, coinCount, new Object[]{}));
            Boolean flag = Data.doTrans(paySqls);
            if (flag) {
                Write("操作成功", true, "操作成功");
                Cache.SetObj(activityId, com.alibaba.fastjson.JSON.toJSON(activity));
            } else {
                Write("操作失败", false, "操作失败");
            }
        } catch (Exception e) {
            logger.error("新建投票失败", e);
            Write("操作失败", false, "操作失败");
        }

    }

    /**
     * 活动编辑
     */
    @Page(Viewer = ".xml")
    public void update() {
        String activityNameSimple = param("activityNameSimple");
        String activityNameTraditional = param("activityNameTraditional");
        String activityNameEnglish = param("activityNameEnglish");
        String activityContentSimple = request.getParameter("activityContentSimple").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
        String activityContentTraditional = request.getParameter("activityContentTraditional").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
        String activityContentEnglish = request.getParameter("activityContentEnglish").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
        String activityRuleSimple = request.getParameter("activityRuleSimple").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
        String activityRuleTraditional = request.getParameter("activityRuleTraditional").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
        String activityRuleEnglish = request.getParameter("activityRuleEnglish").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
        String activityId = param("activityId");
        int selectCount = intParam("selectCount");
        int activityLimit = intParam("activityLimit");
        Timestamp startTime = dateParam("startTime");
        Timestamp endTime = dateParam("endTime");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String begin = format.format(startTime);
        String end = format.format(endTime);

        try {
            List<Activity> activityList = voteDao.getList(null, "cn", "");
            if (!CollectionUtils.isEmpty(activityList)) {
                for (Activity activity : activityList) {
                    if(!activity.getActivityId().equals(activityId)){
                        Boolean flag = TimeUtil.isOverlap(startTime, endTime, activity.getStartTime(), activity.getEndTime());
                        if (flag == true) {
                            Write("操作失败,活动进行时间冲突", false, "操作失败");
                            return;
                        }
                    }
                }

            }
            Date date1 = format.parse(begin);
            Date date2 = format.parse(end);
            JSONObject activityNameJson = new JSONObject();
            JSONObject activityContentJson = new JSONObject();
            JSONObject activityRuleJson = new JSONObject();
            activityNameJson.put("en", activityNameEnglish);
            activityNameJson.put("cn", activityNameSimple);
            activityNameJson.put("hk", activityNameTraditional);
            activityContentJson.put("en", activityContentEnglish);
            activityContentJson.put("cn", activityContentSimple);
            activityContentJson.put("hk", activityContentTraditional);
            activityRuleJson.put("en", activityRuleEnglish);
            activityRuleJson.put("cn", activityRuleSimple);
            activityRuleJson.put("hk", activityRuleTraditional);
            Activity activity = new Activity();
            activity.setActivityNameJson(activityNameJson.toString());
            activity.setActivityContentJson(activityContentJson.toString());
            activity.setActivityRuleJson(activityRuleJson.toString());
            activity.setSelectCount(selectCount);
            activity.setActivityLimit(activityLimit);
            activity.setStartTime(date1);
            activity.setEndTime(date2);
            int a = Data.Update("UPDATE activity SET activityLimit=?,activityNameJson=?, activityContentJson=?, activityRuleJson=?, startTime=?,endTime=?,selectCount=? WHERE activityId=?", new Object[]{activityLimit, activityNameJson, activityContentJson, activityRuleJson, begin, end, selectCount, activityId});
            if (a != -1) {
                Write("操作成功", true, "操作成功");
                Cache.SetObj(activityId, com.alibaba.fastjson.JSON.toJSON(activity));
            } else {
                Write("操作失败", false, "操作失败");
            }
        } catch (Exception e) {
            logger.error("更新投票活动失败", e);
            Write("操作失败", false, "操作失败");
        }
    }

    /**
     * 活动状态修改
     * 0：未开始，1：进行中，2:暂停中，3：已结束，4，已删除
     */
    @Page(Viewer = ".xml")
    public void changeState() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String activityId = param("activityId");
            JSONObject obj = (JSONObject)Cache.GetObj(activityId);
            String activityJson=  obj.toJSONString();
            Activity activity = com.alibaba.fastjson.JSON.parseObject(activityJson, Activity.class);
            int state = intParam("state");
            Date now = new Date();
            if (state == 1) {
                if (activity.getState() == 2) {
                    List<Activity> startingActivityList = voteDao.getList("1", "cn", "");
                    List<Activity> pauseActivityList = voteDao.getList("2", "cn", "");
                    if ((startingActivityList.size() + pauseActivityList.size()) > 1) {
                        Write("操作失败,活动进行时间冲突", false, "操作失败");
                        return;
                    }
                    Data.Update("UPDATE activity SET state=? WHERE activityId=?", new Object[]{state, activityId});
                    activity.setState(state);
                    Cache.SetObj(activityId, com.alibaba.fastjson.JSON.toJSON(activity));
                } else {
                    List<Activity> startingActivityList = voteDao.getList("1", "cn", "");
                    List<Activity> pauseActivityList = voteDao.getList("2", "cn", "");
                    if ((startingActivityList.size() + pauseActivityList.size()) > 0) {
                        Write("操作失败,活动进行时间冲突", false, "操作失败");
                        return;
                    }
                    String beginDateString = formatter.format(now);
                    Data.Update("UPDATE activity SET state=?,startTime=? WHERE activityId=?", new Object[]{state, beginDateString, activityId});
                    activity.setState(state);
                    activity.setStartTime(now);
                    Cache.SetObj(activityId, com.alibaba.fastjson.JSON.toJSON(activity));
                }
            } else if (state == 3) {
                String endDateString = formatter.format(new Date());
                Data.Update("UPDATE activity SET state=?,endTime=? WHERE activityId=?", new Object[]{state, endDateString, activityId});
                activity.setState(state);
                activity.setEndTime(now);
                Cache.SetObj(activityId, com.alibaba.fastjson.JSON.toJSON(activity));
            } else if (state == 4) {
                Data.Update("UPDATE activity SET state=? WHERE activityId=?", new Object[]{state, activityId});
                Cache.Delete(activityId);
            } else {
                Data.Update("UPDATE activity SET state=? WHERE activityId=?", new Object[]{state, activityId});
                activity.setState(state);
                Cache.SetObj(activityId, com.alibaba.fastjson.JSON.toJSON(activity));
            }
            Write("操作成功", true, "操作成功");


        } catch (Exception e) {
            logger.error("更新投票活动状态失败", e);
            Write("操作失败", false, "操作失败");
        }


    }


    /**
     * 投票结果查看，返回3种报文
     * 1，activity 活动内容
     * 2。币库投票结果
     * 3。参与投票人数
     */
    @Page(Viewer = "/admins/vote/voteResult.jsp")
    public void voteReult() {
        try {
            String activityId = param("id");
            Activity activity = new Activity();
            activity = voteDao.getOne(activityId, "", "cn");//默认为中文的查看结果
            setAttr("activity", activity);
            String orderBy = param("orderBy");
            List<Coin> coinList = coinDao.getList(activityId, lan, orderBy);
            for (Coin coin : coinList) {
                coin.setCount(coin.getVoteCount() - coin.getRealCount());
            }
            setAttr("coinList", coinList);
            int countPeople = activityLogDao.countPeople(activityId);
            setAttr("countPeople", countPeople);
        } catch (Exception e) {
            logger.error("进入投票结果页面失败", e);
        }

    }

    /**
     * 刷票，
     * param：brushVotes  刷票数
     */
    @Page(Viewer = ".xml")
    public void brushVote() {
        try {
            int brushCount = intParam("brushVotes");
            String activityId = param("activityId");
            int coinId = intParam("coinId");
            int a = coinDao.brashVote(activityId, coinId, brushCount);
            if (a != -1) {
                Write("操作成功,", true, "操作成功");
            } else {
                Write("操作失败,", false, "操作失败");
            }
        } catch (Exception e) {
            logger.error("刷票失败", e);
        }


    }





}
