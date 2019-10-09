package com.world.model.financialproift.userfininfo.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.world.data.mysql.OneSql;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.entity.financialproift.FinProductInvest;

public class DynamicBonusResetCalThread extends Thread {
	/*sql语句*/
	private FinProductInvest finProductInvest;
	private String sql = "";
	private static Logger log = Logger.getLogger(DynamicBonusResetCalThread.class.getName());
	private CountDownLatch countDownLatch;
//	private Date calStart;
//	private Date calEnd;
	
	public DynamicBonusResetCalThread (FinProductInvest finProductInvest, CountDownLatch countDownLatch) {
		this.finProductInvest = finProductInvest;
		this.countDownLatch = countDownLatch;
	}
	
	@Override
	public void run() {
		try {
			/**
			 * 定义需要用到的变量
			 */
			/*用户ID*/
			int userId = 0;
			/*该笔投资的矩阵*/
			int matrixLevel = 0;
			/*该笔投资期次*/
			String investProPeriod = "0";
			userId = finProductInvest.getUserId();
			matrixLevel = finProductInvest.getMatrixLevel();
			investProPeriod = finProductInvest.getInvestProPeriod();
			/**
			 * 先删除投资信息
			 * 获取投资信息，重新传入vdspool系统进行计算
			 */
			/**
			 * 先删除已生成的计算数据
			 * t_bonus 
			 * UNIQUE INDEX `ubfbmr_index` (`user_id`, `bonus_type`, `from_user_id`, `base_user_id`, `matrix_level`, `rein_times`) USING BTREE
			 * t_bonus_surplus
			 * UNIQUE INDEX `ubfbmr_index` (`user_id`, `bonus_type`, `from_user_id`, `base_user_id`, `matrix_level`, `rein_times`) USING BTREE
			 */
			List<OneSql> sqls = new ArrayList<>();
			TransactionObject txObj = new TransactionObject();
			sql = "delete from t_bonus where from_user_id = " + userId + " and deal_flag = 0 and matrix_level = " + matrixLevel + " "
				+ "and rein_times = " + investProPeriod + " ";
			log.info("理财报警:DynamicBonusResetCalThread sql = " + sql);
			sqls.add(new OneSql(sql, -1, null, "vip_main"));
			
			sql = "delete from t_bonus_surplus where from_user_id = " + userId + " and deal_flag = 0 and matrix_level = " + matrixLevel + " "
				+ "and rein_times = " + investProPeriod + " ";
			log.info("理财报警:DynamicBonusResetCalThread sql = " + sql);
			sqls.add(new OneSql(sql, -1, null, "vip_main"));
			
			if (txObj.commit()) {
            	log.info("理财报警:用户【" + userId + "】 动态奖金(建点,指导,晋升)重新计算成功");
            } else {
            	log.info("理财报警REWARDERROR:用户【" + userId + "】动态奖金(建点,指导,晋升)重新计算失败");
            	return;
            }
			
			
			
		} catch (Exception e) {
			log.info("理财报警REWARDERROR:DynamicBonusResetCalThread", e);
		} finally {
    		countDownLatch.countDown();
    	}
	}
}
