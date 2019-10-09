package com.world.model.dao.auto.worker;

import com.Lan;
import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.query.Query;
import com.world.model.dao.app.MarketRemindDao;
import com.world.model.dao.task.Worker;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.app.MarketRemind;
import com.world.model.entity.user.User;
import com.world.util.jpush.MsgType;
import com.world.util.jpush.Pusher;
import com.world.util.string.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * 价格预警设置规则:
 *
 * 用户在设置价格预警的时候,会传入当前的市场价格(curPrice)和用户期望的预警价格(alertPrice)
 * 如果curPrice < alertPrice 认为是价格超过alertPrice进行预警
 * 如果curPrice > alertPrice 认为是价格低于alertPrice进行预警
 *
 */
public class PriceReminder extends Worker{

	private static final long serialVersionUID = 3602091939582043298L;

	//当前任务是否执行完毕,如果轮询时为false则停止操作,等待下一轮
	private static boolean flag = true;

	MarketRemindDao remindDao = new MarketRemindDao();
	UserDao userDao = new UserDao();

	public PriceReminder(String name, String des) {
		super(name, des);
		log.info("PriceReminder 初始化成功!!!");
	}

	@Override
	public void run() {
		if (!flag) {
			log.info("上一个任务还未执行完毕,等待下一轮询");
			return;
		}

		try {
			//锁定任务
			flag = false;

			super.run();

			log.info("价格推送任务开始执行.");

			//1.从缓存获取价格信息
			JSONObject prices = UserCache.getPrices();

			//2.获取价格预警列表
			Query<MarketRemind> query = remindDao.getQuery();
			List<MarketRemind> list = remindDao.find(query).asList();

			//3.遍历预警列表进行推送
			for (MarketRemind remind : list) {
				User safeUser = userDao.getById(remind.getUserId());
				if(StringUtils.isBlank(safeUser.getJpushKey())){
					continue;
				}
				reminder(safeUser, remind, prices);
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		} finally {
			flag = true;
		}
	}

	private void reminder(User safeUser,MarketRemind remind,JSONObject prices) {

		// TODO: 2017/4/27 判断是否开启推送,一期需求没提

		BigDecimal currentPrice = prices.getBigDecimal(remind.getCurrency() + "_" + remind.getExchange());

		String currencyStr = remind.getCurrency().toUpperCase();

		if (StringUtil.exist(remind.getCurrencyPrice()) && StringUtil.exist(remind.getPrice())) {
			if (new BigDecimal(remind.getCurrencyPrice()).compareTo(new BigDecimal(remind.getPrice())) > 0) { // 低于
				if (new BigDecimal(remind.getPrice()).compareTo(BigDecimal.ZERO)>0  && currentPrice.compareTo(BigDecimal.ZERO)>0 && new BigDecimal(remind.getPrice()).compareTo(currentPrice)>=0) {
					try {
						Pusher.pushAccordingMsgType(Lan.LanguageFormat(safeUser.getLanguage(), "当前%%价格为%%,低于您设置的价格%%。", currencyStr, currentPrice.toPlainString(), remind.getPrice()), safeUser.get_Id(), MsgType.priceRemind);
						remindDao.deleteMarketRemind(String.valueOf(remind.getId()));
					} catch (Exception e) {
						log.error("【极光推送】当前用户:"+safeUser.get_Id()+",所用registrationId："+safeUser.getJpushKey()+"推送："+MsgType.priceRemind.getValue()+"消息异常，异常信息为:", e);
					}
				}
			} else if (new BigDecimal(remind.getCurrencyPrice()).compareTo(new BigDecimal(remind.getPrice())) < 0) { // 高于
				if (new BigDecimal(remind.getPrice()).compareTo(BigDecimal.ZERO) > 0 && currentPrice.compareTo(BigDecimal.ZERO) > 0 && currentPrice.compareTo(new BigDecimal(remind.getPrice()))>=0) {
					try {
						Pusher.pushAccordingMsgType(Lan.LanguageFormat(safeUser.getLanguage(), "当前%%价格为%%,高于您设置的价格%%。", currencyStr, currentPrice.toPlainString(), remind.getPrice()), safeUser.get_Id(), MsgType.priceRemind);
						remindDao.deleteMarketRemind(String.valueOf(remind.getId()));
					} catch (Exception e) {
						log.error("【极光推送】当前用户:"+safeUser.get_Id()+",所用registrationId："+safeUser.getJpushKey()+"推送："+MsgType.priceRemind.getValue()+"消息异常，异常信息为:", e);
					}
				}
			} else {
				log.info("价格推送,没有需要推送的用户");
			}
		}

	}
	
}
