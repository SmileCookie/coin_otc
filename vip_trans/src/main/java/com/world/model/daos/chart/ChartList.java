package com.world.model.daos.chart;

import java.io.Serializable;
import java.util.LinkedList;

import com.tenstar.timer.chart.ChartDataBean;

public class ChartList implements Serializable{

	private static final long serialVersionUID = 3354370239410512910L;
	public ChartDataBean tool;
	public int max;
	public LinkedList<ChartDataBean> lists = new LinkedList<ChartDataBean>();
}
