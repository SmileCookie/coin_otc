package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.world.cache.Cache;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinUserInsureInvest;
import com.world.model.financialproift.userfininfo.thread.InsureInvestTriggerThread;

import me.chanjar.weixin.common.util.StringUtils;

public class InsureInvestTriggerWork extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*查询SQL*/
	private String sql = "";
	/*此轮定时任务结束标识*/
    private static boolean workFlag = true;
    /*展示批量耗时的标识*/
    private boolean showInfoFlag = false;
    /*日志打印计数器执行100次打印一次*/
    private static int intLogInfoFlag = 1;
    
    public InsureInvestTriggerWork(String name, String des) {
		super(name, des);
	}
    
    @Override
	public void run() {
    	/*记录核算开始时间*/
        long startTime = System.currentTimeMillis();
        /*重置展示标志*/
        showInfoFlag = false;
        if (intLogInfoFlag == 100) {
        	log.info("理财报警REWARDINFO:【保险投资触发监控扫描-定时任务】开始");
        }
    	String vdsUsdt = Cache.Get("vds_usdt_l_price");
		BigDecimal bdVdsUsdt = BigDecimal.ZERO;
		if (StringUtils.isEmpty(vdsUsdt)) {
			log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】Cache获取vdsUsdt价格异常 = " + vdsUsdt);
			return;
		} else {
			try {
				bdVdsUsdt = new BigDecimal(vdsUsdt);
			} catch (Exception e) {
				log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】Cache获取vdsUsdt价格异常 = " + vdsUsdt);
				return;
			}
			if (bdVdsUsdt.compareTo(BigDecimal.ZERO) <= 0) {
				log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】Cache获取vdsUsdt价格异常小于0 = " + vdsUsdt);
				return;
			}
		}
		
    	if (workFlag) {
        	workFlag = false;
        	try {
        		/*查询满足条件的待触发记录 */
        		sql = "select * from fin_userinsureinvest where triggerFlag = 0 and investState = 0 and triggerPrice >= " + bdVdsUsdt + " ";
        		log.info("InsureInvestTriggerWork...sql = " + sql);
        		List<Bean> listFinUserInsureInvest = (List<Bean>) Data.Query("vip_financial", sql, null, FinUserInsureInvest.class);
        		log.info("listFinUserInsureInvest.size() = " + listFinUserInsureInvest.size());
        		/*循环变量*/
        		FinUserInsureInvest finUserInsureInvest;
        		/*线程池定义*/
        		ExecutorService executorSuperNode = Executors.newFixedThreadPool(1);
        		if (null != listFinUserInsureInvest && listFinUserInsureInvest.size() > 0) {
            		CountDownLatch countDownLatch = new CountDownLatch(listFinUserInsureInvest.size());
        			for (int i = 0; i < listFinUserInsureInvest.size(); i++) {
        				finUserInsureInvest = (FinUserInsureInvest) listFinUserInsureInvest.get(i);
        				InsureInvestTriggerThread insureInvestTriggerThread = new InsureInvestTriggerThread(finUserInsureInvest, countDownLatch);
        				executorSuperNode.execute(insureInvestTriggerThread);
        			}
        			countDownLatch.await();
        			showInfoFlag = true;
        		} else {
        			if (intLogInfoFlag == 10) {
    					log.info("理财报警REWARDTASK:【保险投资触发监控扫描-定时任务】本次没有需要分配的人员 sql = " + sql);
    				}
        		}
        		/*关闭线程池*/
    			executorSuperNode.shutdown();
    			if (showInfoFlag) {
    				long endTime = System.currentTimeMillis();
    				log.info("理财报警REWARDTASK:【保险投资触发监控扫描-定时任务】结束!!!【核算耗时：{" + (endTime - startTime) + "}】");
    			}
        	} catch (Exception e) {
    			log.info("理财报警REWARDERROR:【保险投资触发监控扫描-定时任务】", e);
    		} finally {
            	workFlag = true;
            	if (intLogInfoFlag == 100) {
            		intLogInfoFlag = 0;
            	}
            	intLogInfoFlag++;
            }
    	}
    }
    
    public static void main(String[] args) {
    	InsureInvestTriggerWork insureInvestTriggerWork = new InsureInvestTriggerWork("", "");
    	insureInvestTriggerWork.run();
    }
    
}
