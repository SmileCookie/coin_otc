package com.world.controller.admin.capitalaccount;

import com.world.data.database.DatabasesUtil;
import com.world.model.dao.reconciliation.ReconciliationDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.reconciliation.Reconciliation;
import com.world.timer.DateUtilsEx;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/capitalaccount/", des = "交易平台资金总账")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    ReconciliationDao reconciliationDao = new ReconciliationDao();


    /**
     * 交易平台资金总账
     */
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        Timestamp endTime = dateParam("endTime");
        Timestamp beginTime = dateParam("beginTime");
        Date begin = null;
        Date end = null;
        int fundsType = intParam("fundsType");
        if(null == beginTime && null == endTime) {
        	setAttr("queryBeginTime", DateUtilsEx.getBeforeNowDate(new Date(), 1));
        	setAttr("queryEndTime", DateUtilsEx.getBeforeNowDate(new Date(), 1));
        }
        if (beginTime == null) {
            begin = TimeUtil.getSpecifiedDayBefore(TimeUtil.dayBegin(new Date()));
        } else {
            begin = TimeUtil.dayBegin(new Date(beginTime.getTime()));
        }
        if (endTime == null) {
            end = TimeUtil.dayEnd(new Date());
        } else {
            end = TimeUtil.dayEnd(new Date(endTime.getTime()));
        }
        
        try {
            reconciliationList = reconciliationDao.getList(begin, end, fundsType);
            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            for (Reconciliation reconciliation : reconciliationList) {
                if (reconciliation.getFundsType() == 9) {
                    reconciliation.setIcoExchange(BigDecimal.valueOf(reconciliation.getIcoExchange().multiply(new BigDecimal("-1")).doubleValue()));
                }
                for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                    CoinProps coint = entry.getValue();
                    if (reconciliation.getFundsType() == coint.getFundsType()) {
                        reconciliation.setFundsTypeName(coint.getPropTag());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("进入统计对账表页面失败", e);
        }
        Date startTime = null;
        if (beginTime != null) {
            startTime = new Date(beginTime.getTime());
        }
        setAttr("ft", DatabasesUtil.getCoinPropMaps());
        setAttr("dataList", reconciliationList);
        setAttr("beginTime", startTime);
        setAttr("endTime", new Date(end.getTime()));
    }


    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }
}
