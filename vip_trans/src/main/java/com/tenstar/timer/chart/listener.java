package com.tenstar.timer.chart;

import com.world.model.Market;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;

public class listener  implements ServletContextListener{
	private final static Logger log = Logger.getLogger(listener.class);

private Timer timer = null;

public static final int SECOND = 1000;
public static final int MINUTE = 60* 1000;
public static final int HOUR = 60 *60* 1000;

	//销毁
	public void contextDestroyed(ServletContextEvent event) {
		 timer.cancel();
		 log.info("图表定时器销毁");
	}


	public void contextInitialized(ServletContextEvent event) {
		
		timer = new Timer(true);
	
//		new ChartData();
		Map<String,Market> markets = Market.markets;
		Iterator<Entry<String,Market>> iter = markets.entrySet().iterator();

		while(iter.hasNext()){
			Market m = iter.next().getValue();
			if(m.listenerOpen){//打开监听器定时器
				timer.schedule(new ChartData(m) , 0 , 2 * SECOND);
			}
		}
		log.info("图表系统定时器 已启动");
		//timer.schedule(new ReflushCasheTask(1), 1 * SECOND, 2 * SECOND);	//10秒
		//timer.schedule(new ReflushCasheTask(2), 5 * MINUTE, 5 * MINUTE);	//5分钟
		//timer.schedule(new ReflushCasheTask(3), 1 * HOUR, 1 * HOUR);	//1小时
		log.info("图表缓存数据定时刷新定时器 已启动");
		
		
	}
}
