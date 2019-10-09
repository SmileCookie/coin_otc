package com.world.model.daos.chart;

import com.tenstar.timer.chart.ChartDataBean;
import com.world.model.entitys.record.TransRecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/***
 * 24小时统计
 * @author apple
 *
 */
public class StatisticsFor24Hour extends ChartDataBean{

	/**
	 *
	 */
	private static final long serialVersionUID = -2121097522689791549L;

	public StatisticsFor24Hour(
			TreeMap<Long, ChartDataBean> last1440Minutes) {
		super();
		this.last1440Minutes = last1440Minutes;
		reset();//完成初始化
	}

	private final long daySeconds = 24 * 60 * 60 * 1000;
	private final long sixHourSeconds = 6 * 60 * 60 * 1000;

	//过去1440分的记录列表
	private TreeMap<Long, ChartDataBean> last1440Minutes;

	private ChartDataBean maxMinute;//过去24小时最高的分钟值

	private ChartDataBean minMinute;//过去24小时最低的分钟值

	private ChartDataBean lastMinute;//过去24小时最低的分钟值

	private ChartDataBean sixHourMinute;//过去6小时的分钟值

    private ChartDataBean lastDealDayChart;//历史今天之前有成交的最后一天记录

	public BigDecimal getHigh(){
		if(maxMinute != null){
			return maxMinute.getHigh();
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal getLow(){
		if(minMinute != null){
			return minMinute.getLow();
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal getSix() {
		if(sixHourMinute != null){
			return sixHourMinute.getLow();
		}
		return BigDecimal.ZERO;
	}

	public void resetByTransRecord(TransRecord tr){
		ChartDataBean currentMinute = last1440Minutes.get(tr.getTimeMinute());
	}

	public void addMinute(ChartDataBean minute){
		if(minute.getTotalNumber().compareTo(BigDecimal.ZERO)<=0){
			return;
		}

		long history24Hour = minute.getTimes() - daySeconds;//24小时之前
		NavigableMap<Long, ChartDataBean> deletes = last1440Minutes.headMap(history24Hour, true);
		boolean needReinit = false;
		if(deletes.size() > 0){
			List<ChartDataBean> deleteMinutes = new ArrayList<ChartDataBean>();

			for(Map.Entry<Long, ChartDataBean> entry : deletes.entrySet()){
				deleteMinutes.add(entry.getValue());
			}

			for(ChartDataBean entry : deleteMinutes){
				//Long key = entry.getKey();
				ChartDataBean cminute = entry;//entry.getValue();

				Long key = cminute.getTimes();
				last1440Minutes.remove(key);

				setTotalNumber(getTotalNumber().subtract(cminute.getTotalNumber()));
				setTotalMoney(getTotalMoney().subtract(cminute.getTotalMoney()));
				//增加空指针判断 20160804 盘口日志有空指针错误
				if(maxMinute==null || (key!=null && key.equals(maxMinute.getTimes()))){//最高或最低被移除
					maxMinute = null;
					needReinit = true;
				}

				if(minMinute==null || (key!=null && key.equals(minMinute.getTimes()))){
					minMinute = null;
					needReinit = true;
				}
			}
		}

		last1440Minutes.put(minute.getTimes(), minute);
		BigDecimal changeNumber = BigDecimal.ZERO;
		BigDecimal changeBtcNum = BigDecimal.ZERO;
		if(lastMinute != null && (lastMinute.getTimes() == minute.getTimes())){
			changeNumber = minute.getTotalNumber().subtract(lastMinute.getTotalNumber());
			changeBtcNum = minute.getTotalMoney().subtract(lastMinute.getTotalMoney());

			if(changeNumber.compareTo(BigDecimal.ZERO) < 0){
				changeNumber = BigDecimal.ZERO;
				changeBtcNum = BigDecimal.ZERO;
			}
		}else{
			changeNumber = minute.getTotalNumber();
			changeBtcNum = minute.getTotalMoney();
		}

		lastMinute = minute;

		if(needReinit){//重新计数  重新筛选最高最低
			reset();
		}else{
			if((maxMinute == null) || (minute.getHigh().compareTo(maxMinute.getHigh())>0)){
				maxMinute = minute;
			}

			if((minMinute == null) || (minute.getLow().compareTo(minMinute.getLow())<0)){
				minMinute = minute;
			}

			// 6小时前的数据,取最近一条即可
			Map.Entry<Long, ChartDataBean> firstEntry = last1440Minutes.firstEntry();
			for(Map.Entry<Long, ChartDataBean> entry : last1440Minutes.entrySet()){
				ChartDataBean bean = entry.getValue();

				if (sixHourMinute == null || firstEntry.getValue().getTimes() - bean.getTimes() >= sixHourSeconds) {
					sixHourMinute = bean;
					break;
				}
			}

			setTotalNumber(getTotalNumber().add(changeNumber));
			setTotalMoney(getTotalMoney().add(changeBtcNum));
		}

	}

	public void reset(){
		if(last1440Minutes != null && last1440Minutes.size() > 0){
			boolean breakFlag = false;
			Map.Entry<Long, ChartDataBean> firstEntry = last1440Minutes.firstEntry();

			setTotalNumber(BigDecimal.ZERO);
			setTotalMoney(BigDecimal.ZERO);
			for(Map.Entry<Long, ChartDataBean> entry : last1440Minutes.entrySet()){
				ChartDataBean minute = entry.getValue();

				if(maxMinute == null || (minute.getHigh().compareTo(maxMinute.getHigh())>0)){
					maxMinute = minute;
				}

				if(minMinute == null || (minute.getLow().compareTo(minMinute.getLow())<0)){
					minMinute = minute;
				}

				// 6小时前的数据,取最近一条即可
				if (sixHourMinute == null || firstEntry.getValue().getTimes() - minute.getTimes() >= sixHourSeconds) {

					if (!breakFlag) {
						sixHourMinute = minute;
						breakFlag = true;
					}
				}

				setTotalNumber(getTotalNumber().add( minute.getTotalNumber()));
				setTotalMoney(getTotalMoney().add(minute.getTotalMoney()));
			}
		}
	}

	public TreeMap<Long, ChartDataBean> getLast1440Minutes() {
		return last1440Minutes;
	}

	public void setLast1440Minutes(
			TreeMap<Long, ChartDataBean> last1440Minutes) {
		this.last1440Minutes = last1440Minutes;
	}

	public ChartDataBean getMaxMinute() {
		return maxMinute;
	}


	public void setMaxMinute(ChartDataBean maxMinute) {
		this.maxMinute = maxMinute;
	}


	public ChartDataBean getMinMinute() {
		return minMinute;
	}


	public void setMinMinute(ChartDataBean minMinute) {
		this.minMinute = minMinute;
	}

    public ChartDataBean getLastDealDayChart() {
        return lastDealDayChart;
    }

    public void setLastDealDayChart(ChartDataBean lastDealDayChart) {
        this.lastDealDayChart = lastDealDayChart;
    }
}
