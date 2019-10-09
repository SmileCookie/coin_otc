package com.world.controller.admin.aliveuserdatacount;

import com.world.model.entity.statisticalReport.AliveUserDataCount;
import com.world.model.entity.statisticalReport.AliveUserDataCountVo;
import com.world.model.statisticalreport.dao.AliveUserDataCountDao;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@FunctionAction(jspPath = "/admins/aliveuserdatacount/", des = "用户活跃数据统计")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    AliveUserDataCountDao aliveUserDataCountDao = new AliveUserDataCountDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        try {
            Timestamp searchBeginTime = dateParam("startTime");
            Timestamp searchEndTime = dateParam("endTime");
            Date beginTime = null;
            Date endTime = null;
            if (searchBeginTime == null) {
                beginTime = TimeUtil.dayBegin(new Date());
            } else {
                beginTime =  TimeUtil.getSpecifiedDayAfter(new Date(searchBeginTime.getTime()));
            }
            if (searchEndTime == null) {
                endTime = TimeUtil.dayEnd(new Date());
            } else {
                endTime =  TimeUtil.getSpecifiedDayAfter(new Date(searchEndTime.getTime()));
            }
            List<AliveUserDataCountVo> aliveUserDataCountList = aliveUserDataCountDao.getList(beginTime,endTime);
            setAttr("dataList", aliveUserDataCountList);
            setAttr("beginTime",  TimeUtil.getSpecifiedDayBefore(beginTime));
            setAttr("endTime", TimeUtil.getSpecifiedDayBefore(endTime));
        } catch (Exception e) {
            logger.error("进入资金统计页面失败", e);
        }

    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }
}
