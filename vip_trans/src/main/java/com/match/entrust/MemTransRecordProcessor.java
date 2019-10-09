package com.match.entrust;

import com.match.domain.TransRecordMem;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.model.entitys.record.TransRecord;
import org.apache.log4j.Logger;

import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <p>@Description: 更新内存最新成交记录队列</p>
 *
 * @author buxianguan
 * @date 2018/6/19下午8:29
 */
public class MemTransRecordProcessor extends TimerTask {
    public static Logger logger = Logger.getLogger(MemTransRecordProcessor.class);

    private static LinkedBlockingQueue<TransRecordMem> transRecordList = new LinkedBlockingQueue<>();

    public MemTransRecordProcessor() {
    }

    public static int memTransSize() {
        return transRecordList.size();
    }

    @Override
    public void run() {
        try {
            TransRecordMem transRecordMem = null;
            do {
                transRecordMem = transRecordList.poll(1, TimeUnit.SECONDS);
                if (null != transRecordMem) {
                    TransRecord transRecord = transRecordMem.getTransRecord();
                    Market market = transRecordMem.getMarket();

                    ChartManager.addNewTransRecord(transRecord, market);
                }
            } while (null != transRecordMem);
        } catch (Exception e) {
            logger.error(" [撮合交易] 更新内存成交记录错误！", e);
        }
    }

    public static void add(TransRecordMem transRecordMem) {
        transRecordList.offer(transRecordMem);
    }
}