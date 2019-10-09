package com.world.model.balaccount.job.report;

import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.model.dao.report.ReportTypeDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.report.ReportType;
import com.world.model.entity.report.UserDistribution;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserDistributionWork
 * @Description
 * @Author kinghao
 * @Date 2018/8/10   20:45
 * @Version 1.0
 * @Description
 */
public class UserDistributionWork extends Worker {


    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UserDistributionWork.class);


    ReportTypeDao reportTypeDao = new ReportTypeDao();

    public UserDistributionWork(String name, String des) {
        super(name, des);
    }

    public UserDistributionWork() {
    }


    public static void main(String[] args) {
        UserDistributionWork userDistributionWork = new UserDistributionWork("UserDistributionWork", "委托分布");
        userDistributionWork.run();
//        String s = Cache.Get("ENTRUSTMENT_DIS_WORK_KEY");
//        System.out.println("====>>" + s);
       String cn= Cache.Get("USER_DISTRIBUTION_WORK_CN_KEY");
       String en= Cache.Get("USER_DISTRIBUTION_WORK_EN_KEY");
       String hk= Cache.Get("USER_DISTRIBUTION_WORK_HK_KEY");

    }

    @Override
    public void run() {
        try {
            boolean trueOrFalse = true;
            List<UserDistribution> userDistributionsCN = new ArrayList<>();
            List<UserDistribution> userDistributionsEN = new ArrayList<>();
            List<UserDistribution> userDistributionsHK = new ArrayList<>();
            //委托分布报表类型
            int dealType = 1;
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

                //CN
                //自增假数据处理
                List<ReportType> reportTypeList = reportTypeDao.forFalse(dealType);
                for (ReportType reportType : reportTypeList) {
                    UserDistribution userDistribution = new UserDistribution();
                    userDistribution.setAttribute(org.apache.commons.lang3.StringUtils.isNotEmpty(reportType.getAttribute()) ? reportType.getAttribute() : "");
                    userDistribution.setInitial(reportType.getInitial());
                    userDistribution.setKey(reportType.getKey());
                    userDistributionsCN.add(userDistribution);
                }

                JSONArray jsonCN = JSONArray.fromObject(userDistributionsCN);
                //插入memcacher
                Cache.Set("USER_DISTRIBUTION_WORK_CN_KEY", jsonCN.toString(), 60 * 60);

                //EN
                //自增假数据处理
                for (ReportType reportType : reportTypeList) {
                    UserDistribution userDistribution = new UserDistribution();
                    String attribute = Const.UserDistributionENMap.get(reportType.getAttribute());
                    userDistribution.setAttribute(org.apache.commons.lang3.StringUtils.isNotEmpty(attribute) ? attribute : "");
                    userDistribution.setInitial(reportType.getInitial());
                    userDistribution.setKey(reportType.getKey());
                    userDistributionsEN.add(userDistribution);
                }
                JSONArray jsonEN = JSONArray.fromObject(userDistributionsEN);
                //插入memcacher
                Cache.Set("USER_DISTRIBUTION_WORK_EN_KEY", jsonEN.toString(), 60 * 60);

                //HK
                //自增假数据处理
                for (ReportType reportType : reportTypeList) {
                    UserDistribution userDistribution = new UserDistribution();
                    String attribute = Const.UserDistributionHKMap.get(reportType.getAttribute());
                    userDistribution.setAttribute(org.apache.commons.lang3.StringUtils.isNotEmpty(attribute) ? attribute : "");
                    userDistribution.setInitial(reportType.getInitial());
                    userDistribution.setKey(reportType.getKey());
                    userDistributionsHK.add(userDistribution);
                }
                JSONArray jsonHK = JSONArray.fromObject(userDistributionsHK);
                //插入memcacher
                Cache.Set("USER_DISTRIBUTION_WORK_HK_KEY", jsonHK.toString(), 60 * 60);

            }
        } catch (Exception e) {
            logger.error("[用户分布报表定时任务处理数据异常] ：" + e);
        }
    }
}
