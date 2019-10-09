package com.world.controller.admin.position;


import com.world.data.database.DatabasesUtil;
import com.world.model.entity.statisticalReport.BillAllCount;
import com.world.model.entity.statisticalReport.BillAllCountVo;
import com.world.model.statisticalreport.dao.BillAllCountDao;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


@FunctionAction(jspPath = "/admins/position/", des = "持仓统计")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    BillAllCountDao billAllCountDao = new BillAllCountDao();

    /**
     * 统计用户资金
     */
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {

        try {
            String fundsType = param("fundsType");
            Timestamp searchBeginTime = dateParam("startTime");
            Timestamp searchEndTime = dateParam("endTime");
            Date beginTime=null;
            Date endTime=null;
            if (searchBeginTime == null){
                beginTime = TimeUtil.dayBegin(new Date());
            }else{
                beginTime = TimeUtil.getSpecifiedDayAfter(new Date(searchBeginTime.getTime()));
            }
            if (searchEndTime == null){
                endTime = TimeUtil.dayEnd(new Date());
            }else{
                endTime = TimeUtil.getSpecifiedDayAfter(new Date(searchEndTime.getTime()));
            }
            List<BillAllCountVo> billAllCountList = billAllCountDao.getList(fundsType,beginTime,endTime);
            setAttr("ft", DatabasesUtil.getCoinPropMaps());
            setAttr("dataList", billAllCountList);
            setAttr("beginTime", TimeUtil.getSpecifiedDayBefore(beginTime));
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
