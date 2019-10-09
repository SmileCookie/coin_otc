package com.world.model.dao.auto.worker.rpc;

import java.util.Map;
import java.util.Map.Entry;

import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.model.dao.task.Worker;
import com.world.model.entity.coin.CoinProps;

public class RpcChargeWithdrawWorker extends Worker{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5589116084131547078L;

	public RpcChargeWithdrawWorker(String name, String des, boolean autoReplace) {
		super(name, des, autoReplace);
	}
	
	@Override
	public void run() {
		super.run();
		Map<String, CoinProps> map = DatabasesUtil.getCoinPropMaps();
		for(Entry<String, CoinProps> entry : map.entrySet()){
			CoinProps coint = entry.getValue();
			if(!coint.isCoin()){//不可以在商户版获取地址的，用此种方式
				initFactory(coint);
			}
		}
		
	}
	
	private void initFactory(CoinProps coint){
		String currency = coint.getStag();
		//收币钱包的密码，用于生成地址
		String receive_passphrase = GlobalConfig.getValue(currency+"_receive_passphrase");
		//打币钱包的密码，用于打币
		String send_passphrase = GlobalConfig.getValue(currency+"_send_passphrase");
		String walletName = GlobalConfig.getValue(currency+"_receive_name");
		RpcFactory factory = new RpcFactory(coint, receive_passphrase, send_passphrase, walletName);
		//生成一批地址  XXX 生成地址的为什么放在充值里面?
		factory.saveAddresss(100);
		log.info("同步"+currency.toUpperCase()+"交易记录");
		/**
		 * 调用收币钱包
		 */
		//获取最近的100条交易记录，并保存
		factory.saveTransactions(100);
		//查找并更新100条已确认的记录
		factory.updateTransactions(100);
		
		/**
		 * 调用打币钱包
		 */
		//处理100条打币记录
		factory.updateDownload(1000);
	}
	
}
