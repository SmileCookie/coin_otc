package com.world.model.balaccount.job.feeaccount.thread;

import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.fee.FeeDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xie on 2017/7/5.
 */
public class FeeAccountDealThread extends Thread{


    private String conitName;//币种名称
    private static Logger log = Logger.getLogger(FeeAccountDealThread.class);

    public FeeAccountDealThread(String conitName){
        this.conitName = conitName;
    }
    private DownloadDao downloadDao = new DownloadDao();
    private FeeDao feeDao = new FeeDao();

    @Override
    public void run() {
        try{
            String sql = "select batchId, fee, realFee, count from ("
                    + " SELECT fa.batchId, fa.fee, fa.realFee, fb.totalCnt, fc.dealCnt,count from "
                    + " (select batchId, sum(fees) fee, sum(realFee) realFee,count(*) count from " + this.conitName + "download where feeAccountFlag = 0 and status = 2 GROUP BY batchId) fa"
                    + " left join "
                    + " (select batchId, count(*) totalCnt from " + this.conitName + "download where feeAccountFlag = 0 group by batchId) fb on fa.batchId = fb.batchId"
                    + " left join "
                    + " (select batchId, count(*) dealCnt from " + this.conitName + "download where feeAccountFlag = 0 and status = 2  group by batchId) fc on fa.batchId = fc.batchId "
                    + ") ffa where ffa.dealCnt = ffa.totalCnt";

            log.info("处理提现手续费执行sql:" + sql);
            //获取未处理记录的batchId
            List<List<Object>> batchIdList = (List<List<Object>>)Data.Query(sql, new Object[]{});
            log.info("处理" + conitName + "手续费开始...");
            for(List<Object> download : batchIdList) {
                Long batchId = (Long)download.get(0);//批次ID
                BigDecimal fees = (BigDecimal) download.get(1);//总手续费
                BigDecimal realFees = ((BigDecimal) download.get(2)).divide(new BigDecimal(download.get(3).toString()));//实际手续费
                BigDecimal fee = fees.subtract(realFees);//总手续费-实际手续费

                //插入fee表
                OneSql insertFeeSql = feeDao.addFee(0, 3, fee,conitName.toUpperCase(), batchId, "vip_main", 0);//参数1：admin的UserId暂定为0；参数2：3为提现手续费
                //更新download表
                OneSql updateDownloadSql = downloadDao.getUpdateDownloadSql(conitName, 1, batchId);

                List<OneSql> list = new ArrayList<>();
                list.add(insertFeeSql);
                list.add(updateDownloadSql);
                if(Data.doTrans(list)){
                    log.info("处理" + conitName + "手续费成功，batchId:" + batchId);
                }else{
                    log.error("处理" + conitName + "手续费出错，batchId:" + batchId);
                }
            }
            log.info("处理" + conitName + "手续费结束...");
        }catch (Exception e){
            log.error("处理" + conitName + "手续费出错,出错原因：" + e.toString());
        }

    }

}
