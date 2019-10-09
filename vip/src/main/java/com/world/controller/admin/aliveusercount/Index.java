package com.world.controller.admin.aliveusercount;

import com.world.data.mysql.Data;
import com.world.model.entity.statisticalReport.AliveUserCount;
import com.world.model.entity.statisticalReport.AliveUserCountVo;
import com.world.model.statisticalreport.dao.AliveUserCountDao;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@FunctionAction(jspPath = "/admins/aliveusercount/", des = "用户活跃统计")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    AliveUserCountDao aliveUserCountDao = new AliveUserCountDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        try {
            Timestamp searchBeginTime = dateParam("startTime");
            Timestamp searchEndTime = dateParam("endTime");
            Date beginTime = null;
            Date endTime = null;
            if (searchBeginTime == null) {
                beginTime = TimeUtil.dayBegin(TimeUtil.dayBegin(new Date()));;
            } else {
                beginTime = TimeUtil.getSpecifiedDayAfter(new Date(searchBeginTime.getTime()));
            }
            if (searchEndTime == null) {
                endTime = TimeUtil.dayEnd(new Date());
            } else {
                endTime = TimeUtil.getSpecifiedDayAfter(new Date(searchEndTime.getTime()));
            }
            List<AliveUserCountVo> aliveUserDataCountList = aliveUserCountDao.getList(beginTime,endTime);
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
