package com.world.model.dao.auto.worker;

import java.util.Map;
import java.util.Map.Entry;

import com.world.data.database.DatabasesUtil;
import com.world.model.dao.pay.charge.ChargeFacotry;
import com.world.model.dao.task.Worker;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.coin.CoinProps;

public class DealChargeWorker extends Worker{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5589116084131547078L;

	public DealChargeWorker(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		super.run();
		Map<String, CoinProps> map = DatabasesUtil.getNewCoinPropMaps();
		for(Entry<String, CoinProps> entry : map.entrySet()){
			CoinProps coint = entry.getValue();
			refreshCharge(coint);
		}
	}
	
	private void refreshCharge(CoinProps coint){
		log.info("[充值财务确认任务]开始处理"+coint.getPropCnName()+"充值");
		ChargeFacotry chargeFactory = new ChargeFacotry(DailyType.btcCharge, coint);// fixme DailyType.btcCharge 用来打印日志的,跟业务无关 renfei
		chargeFactory.refreshChargeTables();

		log.info("[充值财务确认任务]结束");
	}


	public static void main(String[] args) {
		Map<String, CoinProps> map = DatabasesUtil.getCoinPropMaps();
		for(Entry<String, CoinProps> entry : map.entrySet()){

			CoinProps coint = entry.getValue();
			if (coint.getDatabaseKey().equals("eos")){
				DealChargeWorker dealChargeWorker = new DealChargeWorker("DealChargeWorker", "充值财务到账定时器");
				dealChargeWorker.refreshCharge(coint);
			}

		}
	}
}
