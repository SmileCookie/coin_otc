package com.world.netty.client;

import java.math.BigDecimal;

import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;

import com.api.market.MarketManager;
import com.world.cache.Cache;
import com.world.model.dao.cache.BaseCommonCache;
import com.world.util.DigitalUtil;

/****
 * 用户缓存处理
 * @author Administrator
 *
 */
public class UserCache implements BaseCommonCache{
	private static Logger log = Logger.getLogger(UserCache.class);
	private final static String totalAssetsBaseKey = "totalAssetsKey_";
	private final static String userJsonDataKey = "userJsonDataKey_";
//	private static PayUserDao puDao = new PayUserDao();
	///用会员资产信息KEY
	private final static String userFundsKey = "user_funds_";

	private final static int cacheSeconds = 30;

	private final static int cacheOneHourSeconds = 60 * 60;

	public static boolean setTotalAssetsJson(int userId , String totalJson){
		return Cache.Set(totalAssetsBaseKey + userId, totalJson, cacheSeconds);
	}

	public static String getTotal(int userId){
		String data = Cache.Get(totalAssetsBaseKey + userId);
		if(data == null){
			data = "{\"total\" : 0}";
		}
		return data;
	}
	public static void removeTotal(int userId){
		Cache.Delete(totalAssetsBaseKey + userId);
	}

	public static boolean setUserJson(int userId , String totalJson){
		return Cache.Set(userJsonDataKey + userId, totalJson, cacheSeconds);
	}

	/****
	 * 获取当前系统所有币种的价格
	 * @return 0 BTC 1 LTC  2 BTQ 3ETH
	 */
	public static BigDecimal[] getPrices(){
		BigDecimal btc = BigDecimal.ZERO;
		BigDecimal ltc = BigDecimal.ZERO;
		BigDecimal btq = BigDecimal.ZERO;
		BigDecimal eth = BigDecimal.ZERO;
		BigDecimal dao = BigDecimal.ZERO;
		BigDecimal ethbtc = BigDecimal.ZERO;
		BigDecimal daoeth = BigDecimal.ZERO;
		String btcs = Cache.Get(lastBtcPriceKey);
		String ltcs = Cache.Get(lastLtcPriceKey);
		String btqs = Cache.Get(lastBtqPriceKey)==null?"0":Cache.Get(lastBtqPriceKey);
		String eths = Cache.Get(lastEthPriceKey);
		String daos = Cache.Get(lastDaoPriceKey);
		String ethbtcs = Cache.Get(lastEthBtcPriceKey);
//		String daoeths = Cache.Get(lastDaoEthPriceKey);
		if(btcs != null){
			btc = DigitalUtil.getBigDecimal(btcs);
		}else{
			MarketManager.getInstance().getBTCCurrentPrice();
			//重新取出
			btcs = Cache.Get(lastBtcPriceKey);
			if(btcs != null){
				btc = DigitalUtil.getBigDecimal(btcs);
			}
		}
		if(ltcs != null){
			ltc = DigitalUtil.getBigDecimal(ltcs);
		}else{
			MarketManager.getInstance().getLTCCurrentPrice();
			//重新取出
			ltcs = Cache.Get(lastLtcPriceKey);
			if(ltcs != null){
				ltc = DigitalUtil.getBigDecimal(ltcs);
			}
		}
		if(btqs != null){
			btq = DigitalUtil.getBigDecimal(btqs);
		}else{
			MarketManager.getInstance().getBTQCurrentPrice();
			//重新取出
			btqs = Cache.Get(lastBtqPriceKey);
			if(btqs != null){
				btq = DigitalUtil.getBigDecimal(btqs);
			}
		}
		if(eths != null){
			eth = DigitalUtil.getBigDecimal(eths);
		}else{
			MarketManager.getInstance().getETHCurrentPrice();
			//重新取出
			eths = Cache.Get(lastEthPriceKey);
			if(eths != null){
				eth = DigitalUtil.getBigDecimal(eths);
			}
		}
		if(daos != null){
			dao = DigitalUtil.getBigDecimal(daos);
		}else{
			//TODO dao开盘时打开
//			MarketManager.getInstance().getDAOCurrentPrice();
			//重新取出
			Cache.Set(lastDaoPriceKey,"0");
			daos = Cache.Get(lastDaoPriceKey);
			if(daos != null){
				dao = DigitalUtil.getBigDecimal(daos);
			}
		}
		if(ethbtcs != null){
			ethbtc = DigitalUtil.getBigDecimal(ethbtcs);
		}else{
			MarketManager.getInstance().getETHBTCCurrentPrice();
			//重新取出
			ethbtcs = Cache.Get(lastEthBtcPriceKey);
			if(ethbtcs != null){
				ethbtc = DigitalUtil.getBigDecimal(ethbtcs);
			}
		}
//		if(daoeths != null){
//			daoeth = DigitalUtil.getBigDecimal(daoeths);
//		}else{
//			MarketManager.getInstance().getDAOETHCurrentPrice();
//			//重新取出
//			daoeths = Cache.Get(lastDaoEthPriceKey);
//			if(daoeths != null){
//				daoeth = DigitalUtil.getBigDecimal(daoeths);
//			}
//		}
		if(btc.compareTo(BigDecimal.ZERO) < 0){
			btc = BigDecimal.ZERO;
//			throw new RuntimeException("异常的价格导致程序无法继续了");
		}
		return new BigDecimal[]{btc , ltc, btq, eth, ethbtc, dao};
	}

	public static BigDecimal getBtcPrice(){
		return getPriceByKey(lastBtcPriceKey);
	}

	public static BigDecimal getLtcPrice(){
		return getPriceByKey(lastLtcPriceKey);
	}

	public static BigDecimal getBtqPrice(){
		return getPriceByKey(lastBtqPriceKey);
	}

	public static BigDecimal getEthPrice(){
		return getPriceByKey(lastEthPriceKey);
	}

	public static BigDecimal getDaoPrice(){
		return getPriceByKey(lastDaoPriceKey);
	}

	public static BigDecimal getEthBtcPrice(){
		return getPriceByKey(lastEthBtcPriceKey);
	}

	public static BigDecimal getDaoEthPrice(){
		return getPriceByKey(lastDaoEthPriceKey);
	}

	public static BigDecimal getPriceByKey(String key){
		BigDecimal btc = BigDecimal.ZERO;
		String ms = Cache.Get(key);
		if(ms != null){
			btc = DigitalUtil.getBigDecimal(ms);
		}
		if(btc.compareTo(BigDecimal.ZERO) < 0){
			throw new RuntimeException("异常的价格导致程序无法继续了");
		}
		return btc;
	}

	public static String getUserJson(int userId){
		BigDecimal[] datas = getUserFunds(String.valueOf(userId));
		String json = "";
		if(datas == null){
			json = "{\"assets\" : [0, 0]}";
		}else{
			reset_8(datas , getPrices());
			json = "{\"assets\" : ["+ DigitalUtil.roundDown(datas[8], 2) +", "+DigitalUtil.roundDown(datas[9], 2)+"]}";
		}
		return json;
	}
	public static void removeUserJson(int userId){
		Cache.Delete(userJsonDataKey + userId);
	}

	public static void reset_8(BigDecimal[] funds , BigDecimal[] prices){
		BigDecimal btcToRmb = funds[2].add(funds[3]).multiply(prices[0]);
		BigDecimal ltcToRmb = funds[4].add(funds[5]).multiply(prices[1]);
		BigDecimal btqToRmb = funds[6].add(funds[7]).multiply(prices[2]).multiply(prices[0]);
		BigDecimal ethToRmb = BigDecimal.ZERO;
		BigDecimal daoToRmb = BigDecimal.ZERO;
		if(funds.length > 16){
			ethToRmb = funds[15].add(funds[16]).multiply(prices[3]);
		}
		if(funds.length > 25){
			daoToRmb = funds[24].add(funds[25]).multiply(prices[5]);
		}

		funds[8] = ltcToRmb.add(btcToRmb).add(btqToRmb).add(ethToRmb).add(daoToRmb).add(funds[0]).add(funds[1]);
		//不包含借贷的全部资产=净资产，如果有借入：可用总资产=净+借入金额；如果有借出：总资产=可用资产(净)+借出金额。
		if(funds[13].compareTo(BigDecimal.ONE)==0){
			funds[9] = funds[8].subtract(funds[10]).subtract(funds[11].multiply(prices[0])).subtract(funds[12].multiply(prices[1]));

			if(funds.length > 17){
				funds[9] = funds[9].subtract(funds[17].multiply(prices[3]));
			}
		}else{
			funds[9] = funds[8];
			funds[8] = funds[9].add(funds[10]).add(funds[11].multiply(prices[0])).add(funds[12].multiply(prices[1]));


			if(funds.length > 17){
				funds[8] = funds[8].add(funds[17].multiply(prices[3]));
			}
		}
	}

	
	

	/****
	 * 获取当前用户的资金信息 ,只从缓存取
	 * @param userId
	 * @return  用户资金信息数组    0 可用RMB 1冻结RMB   2 可用BTC 3 冻结BTC  4 可用LTC  5 冻结LTC 6 可用BTQ 7 冻结BTQ 8 资产折合RMB
	 */
	public static BigDecimal[] getUserFunds(String userId){
		BigDecimal[] data = (BigDecimal[]) Cache.GetObj(userFundsKey + userId);
		if(data == null){///重置一下
			//resetUserFundsFromDatabase(userId);
			data = (BigDecimal[]) Cache.GetObj(userFundsKey + userId);
		}else{
			log.debug("从内存中读取到用户：[" + userId +"]资金");
		}
		if(data == null){
			data = new BigDecimal[]{BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO
					,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
		}
		reset_8(data , getPrices());
		return data;
	}

	public static BigDecimal[] getUserFunds(String userId,BigDecimal[] prices){
		BigDecimal[] data = (BigDecimal[]) Cache.GetObj(userFundsKey + userId);
		if(data == null){///重置一下
			//resetUserFundsFromDatabase(userId);
			data = (BigDecimal[]) Cache.GetObj(userFundsKey + userId);
		}else{
			log.debug("从内存中读取到用户：[" + userId +"]资金");
		}
		if(data == null){
			data = new BigDecimal[]{BigDecimal.ZERO};
		}
		reset_8(data , prices);
		return data;
	}
	

}
