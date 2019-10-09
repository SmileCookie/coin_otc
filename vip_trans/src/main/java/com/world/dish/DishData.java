//package com.world.dish;
//
//import com.socket.IOSocketCache;
//import com.tenstar.timer.dish.DishDataManager;
//import com.world.model.Market;
//import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
///**
// * 将调用频繁的行情数据缓存起来，减少对服务器的访问耗时和性能
// *
// * @author zhanglinbo
// *
// */
//public class DishData {
//
//	static String url = "http://192.168.3.27:8080/";
//	private static String[][] periods;
//	public static Logger log = Logger.getLogger(DishData.class.getName());
//
//	static {
//
//		periods = new String[][] { {"1week","604800"}, {"3day","259200"}, {"1day","86400"}, {"12hour","43200"}, {"6hour","21600"},
//			{"4hour","14400"}, {"2hour","7200"}, {"1hour","3600"}, {"30min","1800"},
//				{"15min","900"}, {"5min","300"}, {"3min","180"}, {"1min","60"} };
//    }
//
//	//缓存盘口数据
//	public static ConcurrentMap<String, String> dataMap = new ConcurrentHashMap<>();
//
//	/**
//	 * 初始化K线盘口数据
//	 * @param market 币种 btcdefault\ltcdefault\ethdefault
//	 */
//	public static void initDishData(String market) {
//			try {
//					//档位深度
//					initDepthData(market);
//					//合并档位深度
//					initMegerDepthData(market);
//					//交易数据
//                    //直接从缓存获取，不放内存，缓存压力太大
////					initTrade(market);
//					//行情ticker
//					initTicker(market);
//					//k线数据
//					initKlineData(market);
//			} catch (Exception e) {
//				log.error(e.toString(), e);
//			}
//	}
//
//
//	/**
//	 * 初始化档位深度盘口数据
//	 * 获取10档盘口数据 默认10档数据
//	 * @param market 币种市场
//	 */
//	private static void initDepthData(String market){
//		try {
//
//			String depthData5 = IOSocketCache.get( market+"_datachart5");//
//			if(StringUtils.isNotBlank(depthData5)){
//				dataMap.put(DataKey.DepthKey5+"_"+market, depthData5);
//			}
//
//			String depthData10 =  IOSocketCache.get( market+"_datachart10");//
//			if(StringUtils.isNotBlank(depthData10)){
//				dataMap.put(DataKey.DepthKey10+"_"+market, depthData10);
//			}
//
//			String depthData20 =  IOSocketCache.get( market+"_datachart20");//
//			if(StringUtils.isNotBlank(depthData20)){
//				dataMap.put(DataKey.DepthKey20+"_"+market, depthData20);
//			}
//
//			String depthData50 =  IOSocketCache.get( market+"_datachart50");//
//			if(StringUtils.isNotBlank(depthData50)){
//				dataMap.put(DataKey.DepthKey50+"_"+market, depthData50);
//			}
//
//			String depthData60 =  IOSocketCache.get( market+"_datachart60");//
//			if(StringUtils.isNotBlank(depthData60)){
//				dataMap.put(DataKey.DepthKey60+"_"+market, depthData60);
//			}
//			/*start by xwz 20171108增加200档的初始化*/
//			String depthData200 =  IOSocketCache.get( market+"_datachart200");//
//			if(StringUtils.isNotBlank(depthData200)){
//				dataMap.put(DataKey.DepthKey200+"_"+market, depthData200);
//			}
//			/*end*/
//
//			String depthKlineData50 =  IOSocketCache.get( market+"_datachart50Outer");
//			if(StringUtils.isNotBlank(depthKlineData50)){
//				dataMap.put(DataKey.DepthKlineKey50+"_"+market, depthKlineData50);
//			}
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//
//	}
//
//	/**
//	 * 初始化档位合并深度深度盘口数据
//	 * 默认10档数据
//	 * @param market 币种市场
//	 */
//	private static void initMegerDepthData(String market){
//		try {
//			//合并深度档位
//			String[]  depth = DishDataManager.getDepthByMarket(Market.getMarket(market));
//			if(depth!=null && depth.length>0){
//				for(int i=0;i<depth.length;i++){
//					String key = DishDataManager.header+depth[i].replace(".", "")+"_" + market;
//					String megerDepthData =  IOSocketCache.get( key);
//					//缓存合并深度的数据
//					if(StringUtils.isNotBlank(megerDepthData)){
//						dataMap.put(DataKey.MegerDepthKey+"_"+depth[i].replace(".", "")+"_"+market, megerDepthData);
//					}
//				}
//			}
//		} catch (Exception e) {
//			log.error(e.toString(), e);
//		}
//
//	}
//
//
//	/**
//	 * kx线交易数据
//	 * 返回最新成交的50条成交记录
//	 * @param market
//	 */
//	private static void initTrade(String market){
//
//			try {
//				String key = DataKey.TradeKey+"_"+market;
//				String tradesData =   IOSocketCache.get( market+"_OuderTrade_0");;
//				if(tradesData!=null){
//					dataMap.put(key, tradesData);
//				}
//			} catch (Exception e) {
//				log.error(e.toString(), e);
//			}
//
//	}
//
//	/**
//	 * 初始化行情ticker信息
//	 * @param market etc_btc/eth_btc
//	 */
//	private static void initTicker(String market){
//
//		try {
//			String datas = IOSocketCache.get( market+"_hotdata2");
//			if (datas != null) {
//				dataMap.put(DataKey.TickerKey+"_"+market, datas);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * k线数据
//	 * @param market etc_btc/eth_btc
//	 */
//	private static void initKlineData(String market){
//		try {
//			for(int i=0;i<periods.length;i++){
//				String key = DataKey.KlineKey+"_"+market+"_"+periods[i][1];
//				String time = periods[i][1];//时间
//				String name=market+"_getchar"+time;
//		        String klineData= IOSocketCache.get( name);
//				if(klineData!=null ){
//					dataMap.put(key, klineData);
//				}
//			}
//		}catch (Exception e) {
//			// TODO Auto-generated catch block
//			log.error(e.toString(), e);
//		}
//	}
//
//}
