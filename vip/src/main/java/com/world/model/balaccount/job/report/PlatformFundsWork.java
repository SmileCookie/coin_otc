package com.world.model.balaccount.job.report;

import com.world.cache.Cache;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.report.PlatformFundsDao;
import com.world.model.dao.report.ReportTypeDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.report.PlatformFunds;
import com.world.model.entity.report.ReportType;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PlatformFundsWork
 * @Description
 * @Author kinghao
 * @Date 2018/8/7   15:49
 * @Version 1.0
 * @Description
 */
public class PlatformFundsWork extends Worker {

    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(PlatformFundsWork.class);

    PlatformFundsDao platformFundsDao = new PlatformFundsDao();

    ReportTypeDao reportTypeDao = new ReportTypeDao();

    public PlatformFundsWork(String name, String des) {
        super(name, des);
    }

    public PlatformFundsWork() {
    }

    public static void main(String[] args) {
        PlatformFundsWork platformFundsWork = new PlatformFundsWork("PlatformFundsWork", "平台资金");
        platformFundsWork.run();
//         String s=   Cache.Get("PLATFORM_FUNDS_WORK_KEY");
//        System.out.println("===="+s);

//        Boolean b = Cache.Delete("user_funds_1000340");
//        JSONArray s = Cache.T("user_funds_1000340");
//        System.out.println("======》》》" + s);
//        System.out.println("======>>>>>" + b);


    }


    @Override
    public void run() {
        try {
            boolean trueOrFalse = true;
            List<PlatformFunds> platformFunds = new ArrayList<>();
            //获取资金报表控制
            List<ReportType> reportTypes = reportTypeDao.getAllReportType(3);
            if (reportTypes.size() < 0) {
                logger.info("未获取到当前要处理的数据！");
            }
            for (ReportType reportType : reportTypes) {
                //验证当前数据时候为真
                if (reportType.getTrueOrFalse() == 1) {
                    trueOrFalse = false;
                    break;
                }
            }
            if (trueOrFalse) {
                platformFunds = isTrue();
            } else {
                platformFunds = isFalse(reportTypes);
            }
            JSONArray json = JSONArray.fromObject(platformFunds);
            //插入memcacher
            Cache.Set("PLATFORM_FUNDS_WORK_KEY", json.toString(), 60 * 60);
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


    /**
     * 真数据
     **/
    public List<PlatformFunds> isTrue() {
        List<PlatformFunds> lists = platformFundsDao.getPlatformFunds();
        try {
            //币种配置
            Map<String, CoinProps> map = DatabasesUtil.getCoinPropMaps();
            // 查询对账表数据 dealtype in（1，2）
            for (PlatformFunds platformFunds : lists) {
                for (Map.Entry<String, CoinProps> entry : map.entrySet()) {
                    CoinProps coinProps = entry.getValue();
                    if (platformFunds.getFundsType() == coinProps.getFundsType()) {
                        platformFunds.setFundsName(coinProps.getPropTag());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("平台资金报表真数据操作异常：" + e);
        }
        return lists;
    }


    /**
     * 假数据
     **/
    public List<PlatformFunds> isFalse(List<ReportType> reportTypes) {
        List<PlatformFunds> platformFunds = new ArrayList<>();
        try {
            for (ReportType reportType : reportTypes) {
                PlatformFunds funds = new PlatformFunds();
                funds.setFundsName(reportType.getAttribute());
                //
                if (reportType.getType() == 0) {
                    funds.setDealType(1);
                } else if (reportType.getType() == 1) {
                    funds.setDealType(2);
                }
                //自增量
                int resultInt = (int) (Math.random() * ((Integer.valueOf(reportType.getEnd()) - Integer.valueOf(reportType.getStart())) + 1) + Integer.valueOf(reportType.getStart()));
                BigDecimal result = new BigDecimal(resultInt).add(reportType.getInitial());
                funds.setTxAmount(result);
                platformFunds.add(funds);
                //修改mysql数据库对应的变化值
                reportTypeDao.updateReportType(result.intValue(), "admin", reportType.getId());
            }

        } catch (Exception e) {
            logger.error("平台资金报表假数据操作异常：" + e);
        }
        return platformFunds;

    }


}
