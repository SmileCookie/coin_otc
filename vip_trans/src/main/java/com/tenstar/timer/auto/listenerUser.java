package com.tenstar.timer.auto;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.world.model.Market;
import org.apache.log4j.Logger;

public class listenerUser  implements ServletContextListener{
	private final static Logger log = Logger.getLogger(listenerUser.class);
private Timer timer = null; 

	//销毁
	public void contextDestroyed(ServletContextEvent event) {
		 timer.cancel();
		 log.info("交易定时器销毁");
	}


	public void contextInitialized(ServletContextEvent event) {
		
		timer = new Timer(true);
	
		log.info("交易系统定时器已启动");
		
		
		Map<String,Market> markets = Market.markets;
		Iterator<Entry<String,Market>> iter = markets.entrySet().iterator();
		
		while(iter.hasNext()){
			Market m = iter.next().getValue();
			if(m.listenerOpen){//打开监听器定时器
				//timer.schedule(new AutoTaskUser(m),0,10);
			}
		}
		
	}
}
