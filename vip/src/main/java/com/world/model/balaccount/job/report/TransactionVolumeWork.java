package com.world.model.balaccount.job.report;

import com.world.cache.Cache;
import com.world.model.chart.ChartConstant;
import com.world.model.dao.report.ReportTypeDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.Market;
import com.world.model.entity.report.ReportType;
import com.world.model.entity.report.TransactionVolume;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @ClassName TransactionVolumeWork
 * @Description
 * @Author kinghao
 * @Date 2018/8/11   11:59
 * @Version 1.0
 * @Description
 */
public class TransactionVolumeWork extends Worker {


    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(TransactionVolumeWork.class);


    ReportTypeDao reportTypeDao = new ReportTypeDao();

    public TransactionVolumeWork(String name, String des) {
        super(name, des);
    }

    public TransactionVolumeWork() {
    }


    public static void main(String[] args) {
        TransactionVolumeWork transactionVolumeWork = new TransactionVolumeWork("TransactionVolumeWork", "委托分布");
        transactionVolumeWork.run();
        String s = Cache.Get("TRANSACTION_VOLUM_WORK_KEY");
        System.out.println("====>>" + s);
    }

    @Override
    public void run() {
        try {
            boolean trueOrFalse = true;
            List<TransactionVolume> transactionVolumes = new ArrayList<>();
            //委托分布报表类型
            int dealType = 2;
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
                //获取所有市场
                Set<String> marketNames = Market.getAllMarketName();
                for (String marketName : marketNames) {
                    TransactionVolume transactionVolume = new TransactionVolume();
                    //从缓存获取市场24小时成交量
                    String volumeCache = Cache.Get(String.format(ChartConstant.MARKET_24HOUR_VOLUME_CACHE_KEY, marketName));
                    if (StringUtils.isNotBlank(volumeCache)) {
                        BigDecimal volume = new BigDecimal(volumeCache);
                        transactionVolume.setMarketName(marketName);
                        transactionVolume.setVolumeCache(volume);
                        transactionVolumes.add(transactionVolume);
                    }
                }
            } else {
                //自增假数据处理
                List<ReportType> reportTypeList = reportTypeDao.forFalse(dealType);
                for (ReportType reportType : reportTypeList) {
                    TransactionVolume volume = new TransactionVolume();
                    volume.setMarketName(reportType.getAttribute());
                    volume.setVolumeCache(reportType.getInitial());
                    transactionVolumes.add(volume);
                }
            }
            JSONArray json = JSONArray.fromObject(transactionVolumes);
            //插入memcacher
            Cache.Set("TRANSACTION_VOLUM_WORK_KEY", json.toString(), 60 * 60);
        } catch (Exception e) {
            logger.error("[交易量分布报表定时任务处理数据异常] ：" + e);
        }

    }
}
