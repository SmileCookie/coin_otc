package com.world.controller.admin.vote.voteLog;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Query;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.vote.ActivityLogDao;
import com.world.model.entity.user.User;
import com.world.model.entity.vote.ActivityLogVo;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@FunctionAction(jspPath = "/admins/vote/voteLog/", des = "投票记录")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    UserDao userDao = new UserDao();
    ActivityLogDao activityLogDao = new ActivityLogDao();


    @Page(Viewer = "/admins/vote/voteLog/voteLog.jsp")
    public void index() {
        try {
            String userId = param("userId");
            String activityId = param("activityId");
            String ip = param("ip");
            int voteId = intParam("voteId");
            Timestamp startTime = dateParam("startDate");
            Timestamp endTime = dateParam("endDate");
            int currentPage = intParam("page");
            int pageSize = 10;
            List<ActivityLogVo> activityLogList = new ArrayList<ActivityLogVo>();
            Query logQuery = activityLogDao.getQuery();
            logQuery.setCls(ActivityLogVo.class);
            logQuery.setSql("select a.*,b.coinNameJson as voteName from activityLog a left Join coin b on a.voteId=b.coinId where activityId='" + activityId + "'");
            if (!"".equals(userId) && null != userId) {
                logQuery.append(" AND userId = " + userId);
            }
            if (!"".equals(ip) && null != ip) {
                logQuery.append(" AND voteIp like  %" + ip + "%");
            }
            if (0 != voteId) {
                logQuery.append(" AND voteId = " + voteId);
            }
            if (startTime != null) {
                logQuery.append(" and voteTime>=cast('" + startTime + "' as datetime)");
            }

            if (endTime != null) {
                logQuery.append(" and voteTime<=cast('" + endTime + "' as datetime)");
            }
            logQuery.append(" order by a.voteTime desc");
            int total = activityLogDao.count();
            activityLogList = logQuery.getPageList(currentPage, pageSize);
            for (ActivityLogVo activityLogVo : activityLogList) {
                JSONObject js = JSONObject.parseObject(activityLogVo.getVoteName());
                activityLogVo.setVoteName(js.get("cn").toString());
                User user = new User();
                user = userDao.get(activityLogVo.getUserId());
                if (user != null) {
                    activityLogVo.setUserName(user.getUserName());
                }
            }
            setAttr("activityLogVoList", activityLogList);
            setPaging(total, currentPage, pageSize);
        } catch (Exception e) {
            logger.error("进入投票结果页面失败", e);
        }

    }

    @Page(Viewer = "/admins/vote/voteLog/logAjax.jsp")
    public void ajax() {
        index();
    }


}
