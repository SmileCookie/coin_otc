package com.tenstar.timer.auto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.common.api.KLineApiFactory;
import com.tenstar.HTTPTcp;
import com.tenstar.RobotShualiangConfig;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.model.Market;
import com.world.util.DigitalUtil;


//在这里进行两个账户之间的各种委托，主要是形成真实的没卖

/**
 * 自动刷量任务
 * @author netpet
 *
 */
public class AutoShualiangTask extends TimerTask {
	
	public static Logger log = Logger.getLogger(AutoShualiangTask.class);
	
	private Market m;
	public AutoShualiangTask(Market m){  
		this.m=m;
	}

	RobotShualiangConfig rc = null;
	
	//重新加载config
	public void ReloadConfig(Market m){
		
		String cacheKey = "systemDefaultRobotShualiangConfig"+m.numberBiEn;
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey += m.exchangeBiEn;
		}
		
		Object obj=Cache.Get(cacheKey);
		if(obj==null){
			List l=(List)Data.GetOne(m.db,"select * from autoShualiangConfig where names=? and isDefault=1", new Object[]{"systemDefault"});
			if(l==null){
				rc = new RobotShualiangConfig();
				String objs=HTTPTcp.ObjectToString(rc);
				Data.Insert(m.db,"INSERT INTO autoShualiangConfig(NAMES, objs, isDefault, notes, times)VALUES(?,?,?,?,?);" ,new Object[]{
						"systemDefault",
						objs,
						1, 
						"系统创建",
						System.currentTimeMillis()
				});
				Cache.Set(cacheKey,objs,24*3600);
				obj=objs;
			}else{
				obj= l.get(2);
				Cache.Set(cacheKey, l.get(2).toString(),24*3600);
			}
		}
		
	    rc = (com.tenstar.RobotShualiangConfig)HTTPTcp.StringToObject(obj.toString());
	}
	
	public RobotShualiangConfig getConfig(){
		
		String cacheKey = "systemDefaultRobotShualiangConfig"+m.numberBiEn;
		if (!"cny".equalsIgnoreCase(m.exchangeBiEn) && !"rmb".equalsIgnoreCase(m.exchangeBiEn) && !"btq".equalsIgnoreCase(m.numberBiEn)) {
			cacheKey += m.exchangeBiEn;
		}
		
		Object obj=Cache.Get(cacheKey);
		if(obj == null)
			ReloadConfig(m);
		return (com.tenstar.RobotShualiangConfig)HTTPTcp.StringToObject(obj.toString());
	}
	
	private static AutoShualiangTask task;
	
	public static AutoShualiangTask getInstance(Market m){
		if(task == null) task = new AutoShualiangTask(m);
		return task;
	}
	
	//
	@Override
	public void run() {
		ReloadConfig(m);
		if(rc == null) return;
		try {
			Random r = new Random();
			int sleep = r.nextInt(15000);
			
			if(sleep < 3000){
				sleep = 3000;
			}
			
			log.info("刷掉线程休息：" + sleep +"ms");
			//延迟3秒钟
			Thread.sleep(sleep);
			
			//while(true){
				
				//每循环一次，加载一次配置
				rc = getConfig();
				if(rc.getIsStart() == 0) return;
				
				double amount = 0;
				
				if(!"eth".equalsIgnoreCase(m.numberBiEn)){
					//log.info("获取到[OKCOIN]K线数量，"+getKType(rc.getKtype())+"="+amount);
					amount = KLineApiFactory.getOkKLineVol(m.numberBiEn, rc.getKtype());
				}
				
				if(amount == 0) {
					amount = rc.getDfAmount();
				}else{
					if(rc.getRate() > 0){
						amount = DigitalUtil.mul(amount, rc.getRate());
					}
				}
				
				//if(amount == 0) break;
				
				//
				
				//根据不同的时间点，获取不一样的数量
				amount = getAmount(amount);
				new AutoShualiangHandler(rc, amount,m).auto();
				
				//Thread.sleep(60000);
			//}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
	}
	
	/**
	 * 根据不同的时间点，获取不一样的数量
	 * @param amount
	 * @return
	 */
	public double getAmount(double amount){
		
		Date d = new Date();
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(d);

	    int hours = calendar.get(Calendar.HOUR_OF_DAY);
		
		//23点 - 2点 50%
		if(hours >= 23 || hours < 2){
			return DigitalUtil.mul(amount, 0.55d);
		}
		//2点-8点  25%
		else if(hours >= 2 && hours < 8){
			return DigitalUtil.mul(amount, 0.4d);
		}
		//8点-10点 75%
		else if(hours >= 8 && hours < 10){
			return DigitalUtil.mul(amount, 0.75d);
		}
		//10点-12点 95%
		else if(hours >= 10 && hours < 12){
			return DigitalUtil.mul(amount, 0.95d);
		}
		//12点-14点 85%
		else if(hours >= 12 && hours < 14){
			return DigitalUtil.mul(amount, 0.85d);
		}
		//14点-20点 110%
		else if(hours >= 14 && hours < 20){
			return DigitalUtil.mul(amount, 1.1d);
		}
		//20点-23点 70%
		else if(hours >= 20 && hours < 23){
			return DigitalUtil.mul(amount, 0.7d);
		}
		
		return amount;
	}
	
	public static void main(String[] args) {
		Date d = new Date();
		
		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(d);

	    int hours = calendar.get(Calendar.HOUR_OF_DAY);
	    
	    log.info(hours);


		RobotShualiangConfig rsc = new RobotShualiangConfig();
		log.info(HTTPTcp.ObjectToString(rsc));
	}
}
