package com.world.model.daos.chart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tenstar.timer.chart.ChartDataBean;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.Market;

public class ChartDataDao extends DataDaoSupport{

	/***
	 * 获取某类时间段的K线列表
	 * @param type
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Map<Long , ChartDataBean> getCharts(int type , long startTime , long endTime,Market m ){
		List<Bean> lists = Data.Query(m.db,"select * from ChartData where type=? and times>=? and times<=?", new Object[]{type , startTime , endTime}, ChartDataBean.class);
		
		Map<Long , ChartDataBean> every = new HashMap<Long, ChartDataBean>();
		for(Bean b : lists){
			ChartDataBean cd = (ChartDataBean) b;
			every.put(cd.getTimes(), cd);
		}
		return every;
	}
}
