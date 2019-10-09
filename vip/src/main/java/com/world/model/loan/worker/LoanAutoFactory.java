package com.world.model.loan.worker;

import com.alibaba.fastjson.JSONObject;
import com.world.model.dao.task.TaskFactory;
import com.world.model.loan.MarketPrices;
import com.world.model.loan.product.LevelUserProduct;
import com.world.model.loan.product.P2pPrices;

import java.util.HashMap;
import java.util.Map;

/****
 * 自动工厂 确保产品的最新可用性，过期或者停产后 会有新的接替者不至于产品不可用
 * 
 * @author apple
 *
 */
public class LoanAutoFactory extends TaskFactory {
	public static P2pPrices priceProduct = new P2pPrices();// 系统启动初始化价格产品

	public static Map<String, LevelUserProduct> levelUsers = new HashMap<String, LevelUserProduct>(100);

	public static boolean couldAdd(String userId) {
		LevelUserProduct lup = levelUsers.get(userId);
		if (lup == null || (System.currentTimeMillis() - lup.version) > 60000) {
			return true;
		}
		return false;
	}

	public static JSONObject getPrices() {
		if (priceProduct.version <= 0 || priceProduct.prices == null
				|| (System.currentTimeMillis() - priceProduct.version) >= 60000) {// 产品未生产或者已过期
																					// ，
																					// 从外部高价购买
																					// 此时可能是生产已经出现异常
			log.info("价格线程已经出问题了。。。");
			return MarketPrices.get();
		}
		return priceProduct.prices;
	}

	// 开工
	public static void start() {
		// 火币网最新价格
//		work(new LastTickerWorker("outer-net-price", "外网价格定时器"), 60 * 1000);
		/// 跟进最新价格
		work(new PriceWorker("price", "刷新价格定时器"), 5000);
		/// 处理等级任务
		work(new LevelWorker("quequ-level", "更新平仓级别队列"), 5000);
		/// 平仓 + 逾期还息
		userForceWorkStart();
		/*****
		 * 此线程负责 1.每日的还息工作 2.预期还息的滞纳金产生
		 */
		// 处理还息
		work(new RepayWorker("repay-interest", "还息定时器"), 60 * 1000);

		// 处理还款
		work(new ForceRepayWorker("force-repay", "还款定时器"), 60 * 1000);

		// 发送消息
//		work(new TipsWorker("sms-tips", "消息提醒定时器"), 60 * 1000);

		//监视p2p用户 可用金额，  当可用金额大于设定的值时 进行自动委托
//		work(new AutomaticWork("AutomaticWork", "用户自动委托"), 60 * 1000);
		
		//分钟利息收集定时器，各币种利息收益，总折合人民币
		work(new RevenuedayWork("RevenuedayWork", "利息收益定时器"), 60 * 1000);
		
		//定时更新后台预计平仓价
		work(new UpdateUnwindPriceWorker("UpdateUnwindPriceWorker", "定时更新后台预计平仓价"), 60 * 1000);
		
	}

	public static void userForceWorkStart() {
		/// 开启十个线程分别处理10个不同阶层的用户
		// 分析用户平仓级别
		for (int i = 1; i < 11; i++) {
			int level = i;
			// 默认（11-level）分钟执行一次
			int minute = 11 - i;
			//// 额外情况 可在此列举
			switch (level) {
				case 1:
					minute = 15;
					break;
				default:
					break;
			}
			work(new UserForceWorker(i, "level-force-" + level, "处理级别波段：" + level + "定时器"), minute * 30 * 1000);
		}
	}

}
