package com.tenstar.timer.chart;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.world.data.mysql.Bean;

public class ChartDataBean extends Bean implements Cloneable{

	public ChartDataBean() {
		super();
	}

	public ChartDataBean(BigDecimal open, BigDecimal close, BigDecimal high,
			BigDecimal low, BigDecimal middle, long type, long times, BigDecimal totalMoney,
			BigDecimal totalNumber, long startTransId, long endTransId) {
		super();
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.middle = middle;
		this.type = type;
		this.times = times;
		this.totalMoney = totalMoney;
		this.totalNumber = totalNumber;
		this.startTransId = startTransId;
		this.endTransId = endTransId;
	}

	private static final long serialVersionUID = -3400227086666658934L;
	private long chartDataId; //
	private BigDecimal open; //高开
	private BigDecimal close; //关闭
	private BigDecimal high; //高
	private BigDecimal low; //低
	private BigDecimal middle; //中间价
	private long type; //类型  1分钟  2 小时 3 天
	private long times; //时间
	private BigDecimal totalMoney; //在那个金额
	private BigDecimal totalNumber;//总数量
	private long startTransId;//开盘的交易id
	private long endTransId;//关盘的交易id
	
	private boolean saveToMysql = true;
	
	public boolean isSaveToMysql() {
		return saveToMysql;
	}

	public void setSaveToMysql(boolean saveToMysql) {
		this.saveToMysql = saveToMysql;
	}

	@Override
	public String toString() {
		return "open=" + open + ", close=" + close + ", high=" + high + ", low=" + low + ", middle=" + middle
				+ ", times=" + times + " : " + new java.util.Date(times).toLocaleString() + ", totalMoney="
				+ totalMoney + ", totalNumber=" + totalNumber;
	}
	
	@Override
	public ChartDataBean clone()  {
		try {
			return (ChartDataBean) super.clone();
		} catch (CloneNotSupportedException e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	
	public long getChartDataId() {
		return chartDataId;
	}
	public void setChartDataId(long chartDataId) {
		this.chartDataId = chartDataId;
	}
	public BigDecimal getOpen() {
		return open;
	}
	public void setOpen(BigDecimal open) {
		this.open = open;
	}
	public BigDecimal getClose() {
		return close;
	}
	public void setClose(BigDecimal close) {
		this.close = close;
	}
	public BigDecimal getHigh() {
		return high;
	}
	public void setHigh(BigDecimal high) {
		this.high = high;
	}
	public BigDecimal getLow() {
		return low;
	}
	public void setLow(BigDecimal low) {
		this.low = low;
	}
	public BigDecimal getMiddle() {
		return middle;
	}
	public void setMiddle(BigDecimal middle) {
		this.middle = middle;
	}
	public long getType() {
		return type;
	}
	public void setType(long type) {
		this.type = type;
	}
	public long getTimes() {
		return times;
	}

	static long baseTime = Timestamp.valueOf("2000-01-01 00:00:00").getTime();

	@Deprecated
	public long getMinuteCha() {
		return times - baseTime;
	}
	
	public void setTimes(long time) {
		this.times = time;
	}
	public BigDecimal getTotalMoney() {
		return totalMoney==null?BigDecimal.ZERO:totalMoney;
	}
	public void setTotalMoney(BigDecimal totalMoney) {
		this.totalMoney = totalMoney;
	}
	public BigDecimal getTotalNumber() {
		return totalNumber==null?BigDecimal.ZERO:totalNumber;
	}
	public void setTotalNumber(BigDecimal totalNumber) {
		this.totalNumber = totalNumber;
	}
	public long getStartTransId() {
		return startTransId;
	}

	public void setStartTransId(long startTransId) {
		this.startTransId = startTransId;
	}

	public long getEndTransId() {
		return endTransId;
	}

	public void setEndTransId(long endTransId) {
		this.endTransId = endTransId;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, new String[]{"formBean","middle","times","totalNumber"});
				
	}
	public static void main(String[] args) {
		ChartDataBean obj1 = new ChartDataBean();
		ChartDataBean obj2 = new ChartDataBean();
		obj1.setOpen(BigDecimal.valueOf(111));
		obj2 = (ChartDataBean) obj1.clone();
		log.info(obj1.equals(obj2));
		
	}
	
}
