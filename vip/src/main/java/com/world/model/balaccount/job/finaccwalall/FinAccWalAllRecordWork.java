package com.world.model.balaccount.job.finaccwalall;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.world.data.database.DatabasesUtil;
import com.world.model.balaccount.job.finaccwalall.thread.FinAccWalAllRecordThread;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;
import com.world.util.date.TimeUtil;

/**
 * <p>标题: 提现热钱包余额查询</p>
 * <p>描述: 提现热钱包余额查询</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
public class FinAccWalAllRecordWork extends Worker {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*上次更新时间默认为当天,即明天凌晨执行数据同步; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));*/
	private Timestamp lastUpdateTime =TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));
	
	public FinAccWalAllRecordWork(String name, String des) {
		super(name, des);
	}
	
	@Override
	public void run() {
		/**
		 * 按照币种查询各币种钱包的余额
		 */
		try {
			/*时间控制*/
			Timestamp tsTodayTime  = TimeUtil.getTodayFirst();
			Date nowDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			/*现在时间获取*/
			String strNowTime = sdf.format(nowDate);
			log.info("FinAccDetaislWork...strNowTime = " + strNowTime + ", tsTodayTime = " + tsTodayTime + ", lastUpdateTime = " + lastUpdateTime);
			/*01点10分之后执行*/
			if(strNowTime.indexOf("01:1") >= 0 && tsTodayTime.compareTo(lastUpdateTime) > 0) {
				log.info("开始进行钱包对账查询...");
				/*记录核算开始时间*/
				long startTime = System.currentTimeMillis();
				/*创建一个可重用固定线程数的线程池*/
				ExecutorService finAccDetaislWorkPool = Executors.newFixedThreadPool(1);
				/*循环币种进行充值流水抽取，即从支付中心同步充值流水*/
				Map<String , CoinProps> mapCoinProps = DatabasesUtil.getCoinPropMaps();
				Iterator<Entry<String, CoinProps>> iteCoinProps = mapCoinProps.entrySet().iterator();
				/*资金类型2btc,3...*/
				int fundType = 0;
				String fundTypeName = "";
				while(iteCoinProps.hasNext()) {
					Entry<String, CoinProps> entryCoinProps = iteCoinProps.next();
					CoinProps tmpCoinProps = entryCoinProps.getValue();
					fundType = tmpCoinProps.getFundsType();
					fundTypeName = entryCoinProps.getKey();
					log.info("fundTypeName = " + fundTypeName + ", fundType = " + fundType);
					if(fundType > 0) {
						FinAccWalAllRecordThread finAccWalRecordThread = new FinAccWalAllRecordThread(fundType, fundTypeName);
						finAccDetaislWorkPool.execute(finAccWalRecordThread);
					} else {
						log.info("没有此币种,无法同步!...");
					}
				}
				
				finAccDetaislWorkPool.shutdown();
				
				while (true) {
					if(finAccDetaislWorkPool.isTerminated()) {
						break;
					} else {
						log.info("充值记录对账进行中...");
						Thread.sleep(10 * 1000L);
					}
				}
				
				long endTime = System.currentTimeMillis();
				log.info("钱包对账查询结束!!!【核算耗时：" + (endTime - startTime) + "】");
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	public static void main(String[] args) {
		FinAccWalAllRecordWork finAccWalAllRecordWork = new FinAccWalAllRecordWork("FinAccWalAllRecordWork", "提现热钱包余额查询");
		finAccWalAllRecordWork.run();
	}
}
