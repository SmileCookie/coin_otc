package com.world.model.daos.chart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.tenstar.timer.chart.ChartDataBean;
import org.apache.log4j.Logger;

public class StatAssistBean implements Cloneable {
	private final static Logger log = Logger.getLogger(StatAssistBean.class);

	LinkedList<ChartDataBean> records = new LinkedList<ChartDataBean>();
	
	private static Map<Integer,StatAssistBean> instances = new HashMap<Integer, StatAssistBean>();

	public static StatAssistBean getInstance(Integer freq){
		if (instances.get(freq)==null) {
			instances.put(freq, new StatAssistBean());
		}
		return instances.get(freq);
	}
	
	public static void printRecords(Integer freq){
		log.info("freq " + freq + " records.size()=" + StatAssistBean.getInstance(freq).records.size());
		for (ChartDataBean rec : StatAssistBean.getInstance(freq).records) {
			log.info(rec);
		}
	}
	public boolean add(ChartDataBean e) {
		ChartDataBean last = null;
		if(records.size() > 0){
			last = records.getLast();
		}
		 
		if(last != null && last.getTimes() == e.getTimes()){//已经有当前节点的值作修改操作
			last.setOpen(e.getOpen());
			last.setClose(e.getClose());
			last.setHigh(e.getHigh());
			last.setLow(e.getLow());
			last.setMiddle(e.getMiddle());
			last.setTotalMoney(e.getTotalMoney());
			last.setTotalNumber(e.getTotalNumber());
			return true;
		}else{
			return records.add(e);
		}
	}

	public List<ChartDataBean> getRecords() {
		return records;
	}

	public void setRecords(LinkedList<ChartDataBean> records) {
		this.records = records;
	}


	public void reset() {
		this.records.clear();
	}
	
	public BigDecimal getOpen() {
		if(records.size()>0){
			return records.getFirst().getOpen();
		}
		return BigDecimal.ZERO;
	}
	public BigDecimal getClose() {
		if(records.size()>0){
			return records.getLast().getClose();
		}
		return BigDecimal.ZERO;
	}
	
	
	public BigDecimal getHigh() {
		if(records.size()>0){
			List<BigDecimal> list = new ArrayList<BigDecimal>();
			for (ChartDataBean bean : records) {
				list.add(bean.getHigh());
			}
			return Collections.max(list);
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal getLow() {
		if(records.size()>0){
			List<BigDecimal> list = new ArrayList<BigDecimal>();
			for (ChartDataBean bean : records) {
				list.add(bean.getLow());
			}
			return Collections.min(list);
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal getTotalMoney() {
		
		BigDecimal total = BigDecimal.ZERO;
		for (ChartDataBean bean : records) {
			total.add(bean.getTotalMoney());
		}
		return total;

	}
	
	public BigDecimal getTotalNumber() {
		BigDecimal total = BigDecimal.ZERO;
		for (ChartDataBean bean : records) {
			total.add(bean.getTotalNumber());
		}
		return total;

	}
	
	public BigDecimal getMiddle(){
		if(!getTotalNumber().equals(BigDecimal.ZERO)){
			return getTotalMoney().divide(getTotalNumber());
		}
		return BigDecimal.ZERO;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, new String[]{""});
				
	}
	
	public static void main(String[] args) {
		StatAssistBean obj1 = new StatAssistBean();
		StatAssistBean obj2 = null;
		obj1.add(new ChartDataBean());
		try {
			obj2 = (StatAssistBean) obj1.clone();
		} catch (CloneNotSupportedException e) {
			log.error(e.toString(), e);
		}
		log.info(obj1.equals(obj2));
		
	}
	
	
}
