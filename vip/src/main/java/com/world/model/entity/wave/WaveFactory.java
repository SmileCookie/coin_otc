package com.world.model.entity.wave;

import java.util.Map;

import com.world.cache.Cache;
import com.world.model.entity.coin.CoinProps;
import org.apache.log4j.Logger;

public class WaveFactory {

	private final static Logger log = Logger.getLogger(WaveFactory.class);

	private final static String waveKey = "jua_wave_list_key";
	private final static int cacheTimes = 60*60*24;//一天
	private static Map<Object , Wave> staticWaves = null;
	
	private static long lastSeconds = 0;
	static WaveDao wd =  new WaveDao();
	public static Map<Object , Wave> getMaps(){
		staticWaves = (Map<Object, Wave>) Cache.GetObj(waveKey);
		
		if(staticWaves == null){//needUpdate 系统第一次启动
			//staticWaves = wd.getMapByField("type", wd.getQuery());
			reInit();
			log.info("空值时从mongoDB取出waves........");
		}else{
//			long cur = System.currentTimeMillis();
//			if((cur - lastSeconds) > 60 * 1000){
//				staticWaves = getFromMongo(cur);
//				log.info("超过一分钟缓存时间从mongoDB取出waves........");
//			}else{
//				lastSeconds = cur;
//			}
		}
		return staticWaves;
	}
	///包并发下引起的问题
	public synchronized static Map<Object , Wave> getFromMongo(long cur){
		Map<Object , Wave> maps = null;
		if((cur - lastSeconds) > 60 * 1000){
			maps = wd.getMapByField("type", wd.getQuery());
			lastSeconds = cur;
		}else{
			log.info("并发引起这里检测到了。");
			maps = staticWaves;
			lastSeconds = cur;
		}
		return maps;
	}
	
	public synchronized static void reInit(){
		
		Map<Object , Wave> maps = wd.getMapByField("type", wd.getQuery());
		/**
		 * 2013 01-03 xu 更改值 立即生效
		 */
		staticWaves = maps;
		
		Cache.SetObj(waveKey, maps ,cacheTimes);//保存一天
	}
	
	public static double getWaveValByType(WaveType wt){
		
		double val = 0d;
		try {
			Wave w = null;
			Map<Object , Wave> maps = getMaps();
			
			if(maps != null){
				w = maps.get(wt.getKey());
			}
			if(w != null){
				val = w.getWaveVal();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return val;
	}
	
	public static Wave getWaveByType(WaveType wt){
		Wave w = null;
		Map<Object , Wave> maps = getMaps();
		
		if(maps != null){
			w = maps.get(wt.getKey());
		}
		return w;
	}

	/**
	 * 判断当前的 COIN
	 * @param wt
	 * @return
	 */
	public static Wave getWaveByType(CoinProps coin){
		Wave w = null;
		Map<Object , Wave> maps = getMaps();
		
		if(maps != null){
			if(coin.getDatabaseKey().equals("btc")){
				w = maps.get(WaveType.btc_autoCashToUser.getKey());
			}else if(coin.getDatabaseKey().equals("ltc")){
				w = maps.get(WaveType.ltc_autoCashToUser.getKey());
			}
		}
		return w;
	}
	
}
