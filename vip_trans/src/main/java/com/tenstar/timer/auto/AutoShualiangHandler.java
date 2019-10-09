package com.tenstar.timer.auto;

import java.math.BigDecimal;
import java.util.Random;

import com.tenstar.timer.auto.AutoBrushAmount.AutoBrushAmountManager;
import org.apache.log4j.Logger;

import com.tenstar.RobotShualiangConfig;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.util.DigitalUtil;


public class AutoShualiangHandler{

	public static Logger log = Logger.getLogger(AutoShualiangHandler.class.getName());
	
	
	public AutoShualiangHandler(RobotShualiangConfig rc, double amount,Market m){
		this.rc = rc;
		this.amount = amount;
		this.m=m;
	}
	
	private RobotShualiangConfig rc;
	private double amount;
	private Market m;
	
	public void auto() {
		// TODO Auto-generated method stub
		try {
			if(ChartManager.getBuyOneAndSellOne(m) == null){
				log.error("买一，卖一尚未初始化,不能刷单！");
				return;
			}
			double leftAmount = amount;
			
			Random r = new Random();
			
			
			int ss = (int) getAmountRandom(rc.getStartWave(), rc.getEndWave(), 0);
			
			//int r.nextInt();
			
			for (int i = 0; i < rc.getKtype() * 60; i++) {
				//这个循环一次走一分钟就可以了
				int sr = r.nextInt(100);
				
				log.info("sr:" + sr + " startWave:" + rc.getStartWave() + " endWave:" + rc.getEndWave());
				if(sr > ss){///限制为30%的秒有单 70%无成交单
					//这个时间不处理了
					Thread.sleep(1000);
					continue;
				}
				
				long start = System.currentTimeMillis();
				
				double avgAmount = DigitalUtil.div(leftAmount, rc.getKtype());
				
				if(avgAmount > 0){
				
					double exs = 0d;
					
					if(r.nextInt(10) % 9 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.10), DigitalUtil.mul(avgAmount, 0.3), 3);
					}else if(r.nextInt(10) % 8 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.08), DigitalUtil.mul(avgAmount, 0.2), 3);
					}else if(r.nextInt(10) % 7 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.07), DigitalUtil.mul(avgAmount, 0.12), 3);
					}else if(r.nextInt(10) % 6 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.06), DigitalUtil.mul(avgAmount, 0.35), 3);
					}else if(r.nextInt(10) % 5 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.05), DigitalUtil.mul(avgAmount, 0.25), 3);
					}else if(r.nextInt(10) % 4 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.04), DigitalUtil.mul(avgAmount, 0.12), 3);
					}else if(r.nextInt(10) % 3 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.03), DigitalUtil.mul(avgAmount, 0.10), 3);
					}else if(r.nextInt(10) % 2 == 0){
						exs = getAmountRandom(DigitalUtil.mul(avgAmount, 0.02), DigitalUtil.mul(avgAmount, 0.10), 3);
					}else{
						exs = DigitalUtil.div(avgAmount, 30, 3);
					}
					
					leftAmount = DigitalUtil.sub(leftAmount, exs);
					
					if(exs < 0.1){
						double[] buysell = ChartManager.getBuyOneAndSellOne(m);
						if(buysell == null){
							log.error("买一，卖一尚未初始化,不能刷单！");
							return;
						}
						double buy = buysell[0];//Market.formatMoney();
						double sell = buysell[1];//Market.formatMoney(MemEntrustMatchProcessor.da.sellOne);
						//价格
						double price = 0d;
						double rate = 0.01;
						if(DigitalUtil.mul(DigitalUtil.sub(sell, buy), 0.5d) > 0.01)
							rate = getAmountRandom(0.01, DigitalUtil.mul(DigitalUtil.sub(sell, buy), 0.5d), 2);
						//买
						if(r.nextInt(10) % 2 == 0){
							price = DigitalUtil.add(buy, rate);
							//在这个位置下一个买记录
							AutoBrushAmountManager.INSTANCE.insertRecord(BigDecimal.valueOf(price), BigDecimal.valueOf(exs), 1,m);
							log.info("[刷买]已成功刷一笔买单，价格=" + price + "，数量="+exs + " leftAmount=" + leftAmount);
						//卖
						}else{
							price = DigitalUtil.sub(sell, rate);
							//在这个位置下一个卖记录
							AutoBrushAmountManager.INSTANCE.insertRecord(BigDecimal.valueOf(price), BigDecimal.valueOf(exs), 0,m);
							log.info("[刷买]已成功刷一笔卖单，价格=" + price + "，数量="+exs + " leftAmount=" + leftAmount);
						}
						continue;
					}
					
					int count = r.nextInt(3);
				
					if(count == 0) count = 1;
					
					for (int j = 0; j < count; j++) {
						//不处理
						if(exs <= 0) break;
						//委托数量
						double amt = 0d;
						if(exs <= 0.05){
							amt = exs;
						}else{
							amt = getAmountRandom(0.01, exs, 2);
						}
						
						exs = DigitalUtil.sub(exs, amt);
						
						//保留三位
						amt = DigitalUtil.div(amt, 1d, 2);

						if(amt >= 0.01){
						
							double[] buysell = ChartManager.getBuyOneAndSellOne(m);
							if(buysell == null){
								log.error("买一，卖一尚未初始化,不能刷单！");
								return;
							}
							double buy = buysell[0];//Market.formatMoney();
							double sell = buysell[1];
							//价格
							double price = 0d;
							double rate = 0.01;
							if(DigitalUtil.mul(DigitalUtil.sub(sell, buy), 0.5d) > 0.01)
								rate = getAmountRandom(0.01, DigitalUtil.mul(DigitalUtil.sub(sell, buy), 0.5d), 2);
							//买
							if(r.nextInt(10) % 2 == 0){
								price = DigitalUtil.add(buy, rate);
								//在这个位置下一个买记录
								AutoBrushAmountManager.INSTANCE.insertRecord(BigDecimal.valueOf(price), BigDecimal.valueOf(amt), 1,m);
								log.info("[刷买]已成功刷一笔买单，价格=" + price + "，数量="+amt + " leftAmount=" + leftAmount);
							//卖
							}else{
								price = DigitalUtil.sub(sell, rate);
								//在这个位置下一个卖记录
								AutoBrushAmountManager.INSTANCE.insertRecord(BigDecimal.valueOf(price), BigDecimal.valueOf(amt), 0,m);
								log.info("[刷买]已成功刷一笔卖单，价格=" + price + "，数量="+amt + " leftAmount=" + leftAmount);
							}
						}
					}
				}
				
				long end = System.currentTimeMillis();
				
				if(end - start < 1000){
					Thread.sleep(1000 - (end - start));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
	}
	

	public static double getAmount(double amount, double amountMin, double amountMax, int points){
		if(amount >= amountMax){
			return getAmountRandom(amountMin, amountMax, points);
		}
		return amount;
	}
	
	public static double getAmountRandom(double amountMin, double amountMax, int points){
		try {
			double minx = new BigDecimal(amountMin).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue(); 
			double manx = new BigDecimal(amountMax).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();  
			
			int min = (int) DigitalUtil.mul(minx, 1000d);
			int max = (int) DigitalUtil.mul(manx, 1000d);
			
		    Random random = new Random();
		    int s = random.nextInt(max)%(max-min+1) + min;
			
			return DigitalUtil.div(s, 1000d);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		return 0.01d;
	}
	
	public static int getCount(){
		return new Random().nextInt(3) + 1;
	}
	
	public static void main(String[] args) {
//		Date d = new Date(1433240396000L);
//		log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d));
//		
//		log.info(AutoShualiangHandler.getCount());
		int ss = (int) getAmountRandom(20, 30, 0);
	}
	
}
