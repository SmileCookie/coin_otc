package com.world.model.loan.worker;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.world.model.dao.task.Worker;
import com.world.model.loan.MarketPrices;
import com.world.model.loan.dao.UserDetectDao;

/****
 * 处理用户平仓
 * @author apple
 *
 */
public class UserForceWorker extends Worker{
	private static final long serialVersionUID = 1L;
	public UserForceWorker(int level , String name , String des) {
		this.level = level;
		this.name = name;
		this.des = des;
	}

	static Logger log = Logger.getLogger(PriceWorker.class.getName());
	private int level;
	UserDetectDao userDetectDao = new UserDetectDao();
	public void run() {
		super.run();
		try {
			//产品重造
			//在这里初始化监听器，在tomcat启动的时候监听器启动，可以在这里实现定时器功能
			//当前市场价
			JSONObject prices = LoanAutoFactory.getPrices();
			log.info("@@平仓判断处理@@开始处理平仓级别段:[" + ((level-1) * 10 + 1) + "-" + level * 10 + "],"+ MarketPrices.lastPricesToString(prices) + "重设平仓级别");
			
			if(MarketPrices.isReturn(prices)){
				log.info("当前价格有问题，不能继续判断平仓了.......\n请立即修复.....\n请立即修复.....");
				return;
			}
			userDetectDao.detect(level , 1 , 50 , prices);
		} catch (Exception e) {///保证线程不死
			log.error(e.toString(), e);
		}
	}
}
