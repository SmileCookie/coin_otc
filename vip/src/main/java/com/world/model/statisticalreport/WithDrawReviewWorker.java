package com.world.model.statisticalreport;

import com.world.data.database.DatabasesUtil;
import com.world.model.dao.pay.DownloadSummaryDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadSummaryBean;
import com.world.system.Sys;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Elysion
 * @Description:定时统计当前已审核但未到账且时间超过40分钟的提现记录
 * @date 2018/7/2下午5:25
 */
public class WithDrawReviewWorker extends Worker {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    DownloadSummaryDao downloadSummaryDao = new DownloadSummaryDao();

    /*sql语句*/
    private String sql = "";

    private static Logger log = Logger.getLogger(WithDrawReviewWorker.class.getName());

    public WithDrawReviewWorker(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        try {
            sql = "select * from downloadsummary where status=0 and commandid > 0";
            List<DownloadSummaryBean> downloadSummaryBeanList = downloadSummaryDao.find(sql, new Object[]{}, DownloadSummaryBean.class);
            Map<Integer, BigDecimal> amountMap = new HashMap<>();
            Map<Integer, Integer> countMap = new HashMap<>();
            Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
            for (DownloadSummaryBean downloadSummaryBean : downloadSummaryBeanList) {
                long now = System.currentTimeMillis();
                if (now - downloadSummaryBean.getManageTime().getTime() >= 40 * 60 * 1000) {
                    if (amountMap.containsKey(new Integer(downloadSummaryBean.getFundsType()))) {
                        BigDecimal amount = amountMap.get(new Integer(downloadSummaryBean.getFundsType()));
                        amount = amount.add(downloadSummaryBean.getAmount());
                        amountMap.put(new Integer(downloadSummaryBean.getFundsType()), amount);
                    } else {
                        amountMap.put(new Integer(downloadSummaryBean.getFundsType()), downloadSummaryBean.getAmount());
                    }
                    if (countMap.containsKey(new Integer(downloadSummaryBean.getFundsType()))) {
                        int count = countMap.get(new Integer(downloadSummaryBean.getFundsType()).intValue());
                        count = count+1;
                        countMap.put(new Integer(downloadSummaryBean.getFundsType()), count);
                    } else {
                        countMap.put(new Integer(downloadSummaryBean.getFundsType()), 1);
                    }
                }
            }
            String content = "提现延迟到账预警:";
            for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                CoinProps coint = entry.getValue();
                if (countMap.containsKey(new Integer(coint.getFundsType()))) {
                    content = content + "[币种:" + coint.getPropTag() + "笔数:" + countMap.get(new Integer(coint.getFundsType())) + ",金额:" + amountMap.get(new Integer(coint.getFundsType())) + "];";
                }
            }
            if (!content.equals("提现延迟到账预警:")) {
                log.info("10400001TASKYCDZYJ【提现延迟到账】：" + content);
            }
        } catch (Exception e) {
            log.error("定时统计延迟到账提现记录失败", e);
        }
    }



    public static void main(String[] args) {
        WithDrawReviewWorker withDrawReviewWorker = new WithDrawReviewWorker("","");
        withDrawReviewWorker.run();
    }



}
