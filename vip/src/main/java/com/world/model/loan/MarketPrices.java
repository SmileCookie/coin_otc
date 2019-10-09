package com.world.model.loan;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.world.model.dao.user.mem.UserCache;
import com.world.util.DigitalUtil;

/*****
 * 市场价 
 * 记录之前版本的市场价  和 当前的市场价对比  以核对当前的价格是否合法
 * 不合法则去除不进行 平仓处理
 * @author apple
 */
public class MarketPrices {
	protected static Logger log = Logger.getLogger(MarketPrices.class.getName());
	private static final int MAX_SIZE = 20;
	private static final int NEED_SIMILE = 60;
	
	private static final BigDecimal ASK_MOVE = DigitalUtil.getBigDecimal(10000);//有问题的价格偏移
	
	private static final BigDecimal BTC_MAX_MOVE = DigitalUtil.getBigDecimal(1000);
	private static final BigDecimal LTC_MAX_MOVE = DigitalUtil.getBigDecimal(500);
	private static final BigDecimal ETH_MAX_MOVE = DigitalUtil.getBigDecimal(500);
	private static final BigDecimal ETC_MAX_MOVE = DigitalUtil.getBigDecimal(500);
	
	public static LinkedList<JSONObject> queuePrices = new LinkedList<>();
	private static long LAST_GET = System.currentTimeMillis();
	private static final long MIN_GET_TIME = 2000;//取价格时间间隔值
	
	/****
	 * 添加新价格
	 * @param prices
	 */
	public static void add(final JSONObject prices){
		if(queuePrices.size() >= MAX_SIZE){
			queuePrices.poll();//移除第一个
		}
		queuePrices.offer(prices);//添加新的
	}
	/****
	 * 获取最新价格
	 * @return
	 */
	public static JSONObject get(){
		JSONObject lastPrices= null;
		try {
			if(queuePrices == null){
				queuePrices = new LinkedList<>();
			}
			int size = queuePrices.size();
			long now = System.currentTimeMillis();
			if(size > 0){
				lastPrices = queuePrices.getLast();
				if(((now - LAST_GET) <= MIN_GET_TIME)){
					log.info("请求频繁直接返回上次的价格："+lastPricesToString(lastPrices));
					return lastPrices;
				}
			}
			LAST_GET = now;
			
			JSONObject prices = UserCache.getPrices();
			//非零判断  
			if(isReturn(prices)){
				return prices;
			}
			
			if(lastPrices != null){//判断新的是否变化了
				if(lastPrices.equals(prices)){
					log.info("========价格没变化，就不继续判断了");
					return lastPrices;
				}
			}
			
			//判断新价格是否合法
			int similar = 0;
			int similarNum = 0;
			boolean canAdd = true;//是否可添加到队列
			
			if(queuePrices.size() < MAX_SIZE){
				similar = 100;
			}else{
				for(int i=0;i<size;i++){
					
					JSONObject p = null;
					try {
						p = queuePrices.get(i);//存在被移除的情况，其他线程可能已经去除了
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
					if(p == null){
						break;
					}
					
					int moveValue = canAddQueuePrices(lastPrices, p);
					if(moveValue == 0){//位移在规定的范围内视为有效价格
						
						similarNum++;
						
						similar = (int)((double)similarNum / MAX_SIZE * 100);
						if(similar > NEED_SIMILE){//这样可以少一点计算判断
							break;
						}
					}else{
						if(moveValue == 2){//偏移超过最大了
							canAdd = false;
						}
					}
				}
			}
			if(canAdd){
				add(prices);
			}else{
				log.info("：：：：：启动价格报警：：：：：\n\n报警价格："+lastPricesToString(prices)+"\n\n");
			}
			log.info("相似度：" + similar + "，是否可视为今后的判断标准：" + canAdd);
			
			if(similar >= NEED_SIMILE){//至少有50%的相似度才能用
				lastPrices = prices;
			}else{
				lastPrices = queuePrices.getLast();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return lastPrices;
	}
	
	/**
	 * 输出最新价格
	 * @param lastPrices
	 * @return
	 */
	public static String lastPricesToString(JSONObject lastPrices){
		Iterator<String> it = lastPrices.keySet().iterator();
		StringBuffer buffer = new StringBuffer(); 
		while (it.hasNext()) {
			String key = (String) it.next();
			buffer.append(">>"+key+":"+lastPrices.getBigDecimal(key));
		}
		return buffer.toString();
	}
	
	/**
	 * 是否返回
	 * @param lastPrices
	 * @return
	 */
	public static boolean isReturn(JSONObject lastPrices){
		Iterator<String> it = lastPrices.keySet().iterator();
		boolean isReturn = false;//获取出来的lastPrices一定是有值的
		while (it.hasNext()) {
			String key = it.next();
			BigDecimal price = lastPrices.getBigDecimal(key);
			if(price.compareTo(BigDecimal.ZERO) <= 0){
				isReturn = true;
				break;
			}
		}
		return isReturn;
	}
	
	/**
	 * 判断价格是否正常，可以添加到队列
	 * @param lastPrices
	 * @param currentPrice
	 * @return   1为偏移在允许范围内    2为偏移超过最大范围
	 */
	private static int canAddQueuePrices(JSONObject lastPrices, JSONObject currentPrice){
		Iterator<String> it = lastPrices.keySet().iterator();
		JSONObject moveObject = maxMove();
		int moveValue = 0;
		while (it.hasNext()) {
			String key = it.next();
			BigDecimal price1 = lastPrices.getBigDecimal(key);
			BigDecimal price2 = currentPrice.getBigDecimal(key);
			BigDecimal[] move_ask = (BigDecimal[]) moveObject.get(key);
			boolean canOp = (price1!=null && price2!=null && move_ask!=null && move_ask.length>1);//进行空指针判断
			if(canOp && price1.subtract(price2).abs().compareTo(move_ask[1]) > 0){
				moveValue = 2;
				break;
			}
			if(canOp && price1.subtract(price2).abs().compareTo(move_ask[0]) > 0){
				moveValue = 1;
				break;
			}
		}
		
		return moveValue;
	}
	
	/**
	 * 封装不同币种偏移的范围
	 * @return
	 */
	private static JSONObject maxMove(){
		JSONObject moveObject = new JSONObject();
		moveObject.put("ltc", new BigDecimal[]{DigitalUtil.getBigDecimal(500), DigitalUtil.getBigDecimal(10000)});
		moveObject.put("eth", new BigDecimal[]{DigitalUtil.getBigDecimal(500), DigitalUtil.getBigDecimal(10000)});
		moveObject.put("etc", new BigDecimal[]{DigitalUtil.getBigDecimal(500), DigitalUtil.getBigDecimal(10000)});
		return moveObject;
	}
	
	public static void main(String[] args) {
		JSONObject a = new JSONObject();
		a.put("a", "a");
		a.put("b", 0);
		JSONObject b = new JSONObject();
		b.put("a", "a");
		b.put("b", 1);
		log.info(a.equals(b));
	}
}
