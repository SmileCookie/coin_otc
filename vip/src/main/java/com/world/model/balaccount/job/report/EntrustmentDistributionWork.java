package com.world.model.balaccount.job.report;

import com.world.cache.Cache;
import com.world.model.dao.report.ReportTypeDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.report.EntrustmentDis;
import com.world.model.entity.report.ReportType;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 * @ClassName EntrustmentDistributionWork
 * @Description
 * @Author kinghao
 * @Date 2018/8/9   11:31
 * @Version 1.0
 * @Description
 */
public class EntrustmentDistributionWork extends Worker {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(PlatformFundsWork.class);


    ReportTypeDao reportTypeDao = new ReportTypeDao();

    public EntrustmentDistributionWork(String name, String des) {
        super(name, des);
    }

    public EntrustmentDistributionWork() {
    }

    public static void main(String[] args) {
        EntrustmentDistributionWork entrustmentDistributionWork = new EntrustmentDistributionWork("EntrustmentDistributionWork", "委托分布");
        entrustmentDistributionWork.run();
//        String s = Cache.Get("ENTRUSTMENT_DIS_WORK_KEY");
//        System.out.println("====>>" + s);
    }

    @Override
    public void run() {
        try {
            boolean trueOrFalse = true;
            List<EntrustmentDis> entrustmentDisList = new ArrayList<>();
            //委托分布报表类型
            int dealType = 4;
            //获取所有待处理数据
            List<ReportType> reportTypes = reportTypeDao.getAllReportType(dealType);
            for (ReportType reportType : reportTypes) {
                //验证当前数据时候为真
                if (reportType.getTrueOrFalse() == 1) {
                    trueOrFalse = false;
                    break;
                }
            }
            if (trueOrFalse) {
                //真数据处理逻辑
            } else {
                //自增假数据处理
                reportTypeDao.forFalse(dealType);
                //买入卖出总值
                List<ReportType> reportTypeList = reportTypeDao.getSumByCoinType(dealType);
                //计算百分比
                for (ReportType reportType : reportTypes) {
                    for (ReportType report : reportTypeList) {
                        EntrustmentDis entrustmentDis = new EntrustmentDis();
                        if (reportType.getAttribute().equals(report.getAttribute())) {
                            if (reportType.getType() == 0) {
                                entrustmentDis.setBuyPercentage((reportType.getInitial().divide(report.getInitial(), 2, ROUND_HALF_DOWN)));
                            } else {
                                entrustmentDis.setSalePercentage((reportType.getInitial().divide(report.getInitial(), 2, ROUND_HALF_DOWN)));
                            }
                            entrustmentDis.setType(reportType.getType());
                            entrustmentDis.setCoinTypeName(StringUtils.isNotEmpty(reportType.getAttribute()) ? reportType.getAttribute() : "");
                            entrustmentDisList.add(entrustmentDis);
                        }
                    }
                }
                JSONArray json = JSONArray.fromObject(entrustmentDisList);
                //插入memcacher
                Cache.Set("ENTRUSTMENT_DIS_WORK_KEY", json.toString(), 60 * 60);
            }
        } catch (Exception e) {
            logger.error("委托分布报表定时任务处理数据异常：" + e);
        }
    }

}
