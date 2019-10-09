package com.tenstar.timer.wisdom;

import org.apache.log4j.Logger;

import com.world.cache.Cache;
import com.world.model.Market;
import com.world.model.daos.chart.ChartDataFactory;

/**
 * 图表内存数组
 * 
 * @author pc
 */
public class ChartArray {

	public static Logger log = Logger.getLogger(ChartArray.class);
	private String name;

	/**
	 * 
	 * @param name
	 * @param maxday
	 *            小时是天的10倍 分钟是小时的10倍 不用保持完整的分钟
	 */
	public ChartArray(String name, int maxday, int maxHour, int maxMinute) {
		this.name = name;
		Market m = Market.getMarket(name);//币种对象
		reflushMinute(m);

		reflushHour(m);

		reflushDay(m);
	}

	public void reflushMinute(Market m) {
		Get(60,m);
		Get(300,m);
		Get(900,m);
		Get(1800,m);
	}

	public void reflushHour(Market m) {
		Get(3600,m);
		Get(3600 * 2,m);
		Get(3600 * 4,m);
		Get(3600 * 6,m);
		Get(3600 * 12,m);
	}

	public void reflushDay(Market m) {
		Get(3600 * 24,m);
		Get(3 * 3600 * 24,m);
		Get(7 * 3600 * 24,m);
	}

	/**
	 * 获取分钟
	 * 
	 * @param group
	 *            组
	 * @return 缓存并返回数字
	 */
	public String Get(int time,Market m) {
		log.info("获取数据：:" + time);
		if (time < 3600) {
			int count = time / 60;
			return GetMinute(300, count,m);
		} else if (time < 3600 * 24) {
			int count = time / 3600;
			return Gethour(300, count,m);
		} else {
			int count = time / (60 * 60 * 24);
			return Getday(300, count,m);
		}

	}

	/**
	 * 获取分钟
	 * 
	 * @param group
	 *            组
	 * @return 缓存并返回数字
	 */
	private String GetMinute(int num, int group,Market m) {
		String rtn = ChartDataFactory.getJson(1, group, 1,m);
		Cache.Set(name + "_wisdomchar" + group * 60, rtn, 10);// 10秒
		return rtn;
	}

	/**
	 * 获取小时
	 * 
	 * @param group
	 *            组
	 * @return 缓存并返回数字
	 */
	private String Gethour(int num, int group,Market m) {
		String rtn = ChartDataFactory.getJson(2, group, 1,m);
		Cache.Set(name + "_wisdomchar" + group * 3600, rtn, 5 * 60);// 5分钟
		return rtn;
	}

	/**
	 * 获取天
	 * 
	 * @param group
	 *            组
	 * @return 缓存并返回数字
	 */
	private String Getday(int num, int group,Market m) {
		String rtn = ChartDataFactory.getJson(3, group, 1,m);
		Cache.Set(name + "_wisdomchar" + group * 3600 * 24, rtn, 1 * 60 * 60);// 1小时
		return rtn;
	}
}