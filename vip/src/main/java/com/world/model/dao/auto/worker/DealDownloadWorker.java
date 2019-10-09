package com.world.model.dao.auto.worker;

import java.util.Map;
import java.util.Map.Entry;

import com.world.data.database.DatabasesUtil;
import com.world.model.dao.pay.download.DownloadFacotry;
import com.world.model.dao.task.Worker;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.coin.CoinProps;

public class DealDownloadWorker extends Worker {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2870758437947986105L;

	public DealDownloadWorker(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		super.run();
		
		Map<String, CoinProps> map = DatabasesUtil.getNewCoinPropMaps();
		for(Entry<String, CoinProps> entry : map.entrySet()){
			CoinProps coint = entry.getValue();
			refreshDownload(coint);
		}
	}
	
	private void refreshDownload(CoinProps coint){
		log.info("开始处理"+coint.getPropCnName()+"下载");
		DownloadFacotry downloadFactory = new DownloadFacotry(DailyType.btcDownload, coint);
		downloadFactory.refreshDownloadTables();
	}

}