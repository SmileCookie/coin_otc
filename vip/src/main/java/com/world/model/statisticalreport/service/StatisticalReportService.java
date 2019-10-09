package com.world.model.statisticalreport.service;

import com.world.data.mysql.Query;
import com.world.model.dao.bill.BillDetailDao;
import com.world.model.entity.bill.BillDetails;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.List;

/**
 * bill计算方法
 * @author:chendi
 */
public class StatisticalReportService {
    static Logger logger = Logger.getLogger(StatisticalReportService.class.getName());
    private Timestamp lastUpdateTime = TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));



    public void FundStatisticsDay(){
        BillDetailDao bwDao = new BillDetailDao();
        Query<BillDetails> query = bwDao.getQuery();
        query.setSql("select * from bill");
        query.setCls(BillDetails.class);
        Timestamp tsTodayTime = TimeUtil.getTodayFirst();
        if(lastUpdateTime != null){
            query.append(" and sendTime>=cast('"+lastUpdateTime+"' as datetime)");
        }
        if(tsTodayTime != null){
            query.append(" and sendTime<=cast('"+tsTodayTime+"' as datetime)");
        }
        int total = query.count();
        List<BillDetails> billDetails = query.getList();

    }
}
