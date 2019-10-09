package com.world.controller.admin.reconciliation;

import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.reconciliation.ReconciliationDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.reconciliation.Reconciliation;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/reconciliation/", des = "对账表")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    ReconciliationDao reconciliationDao = new ReconciliationDao();



    /**
     * 统计对账表
     */
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        List<Reconciliation> reconciliationList = new ArrayList<>();
        Timestamp endTime = dateParam("endTime");
        Timestamp beginTime = dateParam("beginTime");
        int fundsType = intParam("fundsType");
        if (endTime == null) {
            endTime = TimeUtil.getTodayLast();
        } else {
            endTime = TimeUtil.getTodayLast(endTime);
        }
        try {
            reconciliationList=reconciliationDao.getBatchThread(beginTime,endTime);
            List<Reconciliation> gbcIco = reconciliationDao.getGbcIcoExchange(beginTime,endTime);
            for(Reconciliation reconciliation:reconciliationList){
                if (reconciliation.getFundsType() == 9) {
                    reconciliation.setIcoExchange(BigDecimal.valueOf(gbcIco.get(0).getIcoExchange().multiply(new BigDecimal("-1")).doubleValue()));
                }
            }
        } catch (Exception e) {
            logger.error("进入统计对账表页面失败", e);
        }
        Date startTime = null;
        if (beginTime != null) {
            startTime = new Date(beginTime.getTime());
        }
        if (fundsType != 0) {
            List<Reconciliation> fundsTypeList = new ArrayList<>();
            for (Reconciliation reconciliation : reconciliationList) {
                if (reconciliation.getFundsType() == fundsType) {
                    fundsTypeList.add(reconciliation);
                }
            }
            setAttr("ft", DatabasesUtil.getCoinPropMaps());
            setAttr("dataList", fundsTypeList);
            setAttr("beginTime", startTime);
            setAttr("endTime", new Date(endTime.getTime()));
        } else {
            setAttr("ft", DatabasesUtil.getCoinPropMaps());
            setAttr("dataList", reconciliationList);
            setAttr("beginTime", startTime);
            setAttr("endTime", new Date(endTime.getTime()));
        }
    }





    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }
}
