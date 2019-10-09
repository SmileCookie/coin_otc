package com.tenstar.timer.auto;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.world.model.Market;
import org.apache.log4j.Logger;

public class listenerEntrust  implements ServletContextListener{
	private final static Logger log = Logger.getLogger(listenerEntrust.class);
private Timer timer = null; 

private Timer timer2 = null;

	//销毁
	public void contextDestroyed(ServletContextEvent event) {
		 timer.cancel();
		 timer2.cancel();
		 log.info("自动交易定时器销毁");
	}


	public void contextInitialized(ServletContextEvent event) {
		
		//刷量是否启动
//		Thread t = new Thread(new AutoShualiangTask());
//		t.start();
		
		timer = new Timer(true);
		timer2 = new Timer();
		log.info("自动交易系统定时器已启动");
		Map<String,Market> markets = Market.markets;
		Iterator<Entry<String,Market>> iter = markets.entrySet().iterator();
		
		while(iter.hasNext()){
			Market m = iter.next().getValue();
			if(m.listenerOpen){//打开监听器定时器
				//timer.schedule(new AutoEntrustTask(m),0,1000); 
				//timer2.schedule(new AutoShualiangTask(m),0,60000); 
			}
		}
		
		
		
		
		
		
		
	}
}
