package com.world.model.balaccount.job.finaccdetailswork;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;
import com.world.data.database.DatabasesUtil;
import com.world.model.balaccount.job.finaccdetailswork.thread.FinAccDetailsThread;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.util.date.TimeUtil;

/**
 * <p>标题: 充值记录对账同步</p>
 * <p>描述: 充值记录对账查询从支付中心同步数据</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class FinAccDetaislWork extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*上次更新时间默认为当天,即明天凌晨执行数据同步; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));*/
	private Timestamp lastUpdateTime =TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));
	
//	/*sql语句*/
//	private String sql = "";
	/*记录日志*/
	private String logContent = "";
	
	private static Logger log = Logger.getLogger(FinAccDetaislWork.class);

	public FinAccDetaislWork(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		try {
			/*时间控制*/
			Timestamp tsTodayTime  = TimeUtil.getTodayFirst();
			Date nowDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			/*现在时间获取*/
			String strNowTime = sdf.format(nowDate);
			log.info("FinAccDetaislWork...strNowTime = " + strNowTime + ", tsTodayTime = " + tsTodayTime + ", lastUpdateTime = " + lastUpdateTime);
			/*00点10分之后执行 strNowTime.indexOf(" 00:1") >= 0 && */

			/*记录日志*/
            logContent = "开始进行充值记录对账查询";
			log.info("logContent");

			//记录核算开始时间
			long startTime = System.currentTimeMillis();

			//创建一个可重用固定线程数的线程池
			ExecutorService finAccDetaislWorkPool = Executors.newFixedThreadPool(1);

			//循环币种进行充值流水抽取，即从支付中心同步充值流水
			Map<String , CoinProps> mapCoinProps = DatabasesUtil.getNewCoinPropMaps();
			Iterator<Entry<String, CoinProps>> iteCoinProps = mapCoinProps.entrySet().iterator();

			//资金类型2btc,3...
			int fundType = 0;
			String fundTypeName = "";
			while(iteCoinProps.hasNext()) {
				Entry<String, CoinProps> entryCoinProps = iteCoinProps.next();
				CoinProps tmpCoinProps = entryCoinProps.getValue();
				fundType = tmpCoinProps.getFundsType();
				fundTypeName = entryCoinProps.getKey();
				if(fundTypeName.toLowerCase().equals("usdte")){
					fundType = 	tmpCoinProps.getAgreement();
				}
				log.info("fundTypeName = " + fundTypeName + ", fundType = " + fundType);
				if(fundType > 0) {
					/*创建线程,按每个币种进行处理查询*/
					FinAccDetailsThread finAccDetailsThread = new FinAccDetailsThread(fundType, fundTypeName);
//						finAccDetailsThread.run();
					finAccDetaislWorkPool.execute(finAccDetailsThread);
				} else {
					log.info("没有此币种,无法同步!...");
				}
			}
			
			/*按过去执行已提交任务的顺序发起一个有序的关闭，但是不接受新任务。如果已经关闭，则调用没有其他作用。*/
			finAccDetaislWorkPool.shutdown();
			
			while (true) {
				/*如果关闭后所有任务都已完成，则返回 true。注意，除非首先调用 shutdown 或 shutdownNow，否则 isTerminated 永不为 true。*/
				if(finAccDetaislWorkPool.isTerminated()) {
					break;
				} else {
					log.info("充值记录对账进行中...");
					Thread.sleep(10 * 1000L);
				}
			}
			
			long endTime = System.currentTimeMillis();
			logContent = "充值记录对账查询结束!!!【核算耗时：" + (endTime - startTime) + "】";
			log.info(logContent);
//			sysLogDao.addSysLog("admin", logId, logId, "adminMonitor", "FinAccDetaislWork", 2, logContent, "admin", "admin", "");
//			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	public static void main(String[] args) {
		FinAccDetaislWork detailPaymentQueryWork = new FinAccDetaislWork("DetailPaymentQueryWork", "充值记录对账查询");
		detailPaymentQueryWork.run();
		
		//记录积分日志
//		SysLogDao sysLogDao = new SysLogDao();
//		String logId = "adminMonitor-FinAccDetaislWork-2-admin-" + System.currentTimeMillis();
//        String batchId = "adminMonitor-FinAccDetaislWork-2-admin-" + System.currentTimeMillis();
//        String logContent = "开始进行充值记录对账查询";
//        sysLogDao.addSysLog("admin", logId, batchId, "adminMonitor", "FinAccDetaislWork", 2, logContent , "admin", "admin", "");
	}
	
}
