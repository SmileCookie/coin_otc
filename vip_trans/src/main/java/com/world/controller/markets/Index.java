package com.world.controller.markets;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tenstar.HTTPTcp;
import com.tenstar.RecordMessage;
import com.world.controller.IndexServer;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.entitys.TradesData;
import com.world.model.entitys.TradesTimeData;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Index extends BaseAction {
	private static final long serialVersionUID = 1833277198306732411L;
    
	private IndexServer server = new IndexServer();
	
	
	@Page(Viewer = JSON)
	public void klineData(){
		
		JSONObject data=new JSONObject();
		
		//String market = GetPrama(0);
		String moneyType = "cny";
		
		String symbol = param("symbol");
		String market = symbol;
		
		data.put("symbol", symbol);
		
		data.put("moneyType", moneyType);
		
		
		data.put("contractUnit", "**");
		data.put("marketName", "**");
		data.put("USDCNY", "**");
		
		
		int time=intParam("step");
		
		if(time == 0){
			time = 900;
		}
		
		String type = param("type");
		if(type.equals("1min")){
			time = 60;
		}else if(type.equals("3min")){
			time = 180;
		}else if(type.equals("5min")){
			time = 300;
		}else if(type.equals("15min")){
			time = 900;
		}else if(type.equals("30min")){
			time = 1800;
		}else if(type.equals("1hour")){
			time = 3600;
		}else if(type.equals("2hour")){
			time = 7200;
		}else if(type.equals("4hour")){
			time = 14400;
		}else if(type.equals("6hour")){
			time = 21600;
		}else if(type.equals("12hour")){
			time = 43200;
		}else if(type.equals("1day")){
			time = 86400;
		}else if(type.equals("3day")){
			time = 259200;
		}else if(type.equals("1week")){
			time = 604800;
		}
		
		try{
			Market m = Market.getMarkeByName(market);
			if(m==null){
				Response.append("[{error market}]"); 
				return;
			}
			
			String rtn=data(time,m);
			//Response.append("["+rtn+"]");
		//List<TradesTimeData> list  = this.getKlineData();
		
		StringBuffer sb=new StringBuffer();
		sb.append("[").append(rtn).append("]");
		JSONArray arrays = JSONArray.parseArray(sb.toString());
		
		for(Object o : arrays){
			JSONArray ja = (JSONArray) o;
			if(ja.size()>0){
				ja.set(0, ja.getLong(0) * 1000);
			}
			if(ja.size() == 8){
				ja.remove(1);
				ja.remove(1);
			}
		}
		
		data.put("data", arrays);
		json("", true, data.toJSONString());
		
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}
	
	
	/**
	 * 获取图表数据
	 * @param time 时间
	 * @return
	 */
	public String data(int time,Market m){	
	    try{
            return DishDataCacheService.getKline(m.market, String.valueOf(time));
		 }catch(Exception ex){
			  log.error(ex.toString(), ex);
			  return null;
		 }
	}
	
	/**
	 * K线数据-获取委托和交易历史记录数据
	 * @return
	 */
	public List<TradesTimeData> getKlineData() {
		try {
			String market = GetPrama(0);
			if (market.length() == 0) {
				market = "btc_cny";
			}
			int since = 0;//request.getParameter("since") == null ? 0 : Integer.parseInt(request.getParameter("since"));
			Market m = Market.getMarkeByName(market);
			
			RecordMessage myObj = new RecordMessage();
			myObj.setPageindex(since);
			myObj.setMarket(market);
			
			RecordMessage rtn2 = new RecordMessage();
			if(m.listenerOpen){
				rtn2 = server.getTradeOuter(myObj,m);
			}else{
				String param = HTTPTcp.ObjectToString(myObj);
				log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
				String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getTradeOuter", param);
				rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
			}
			
			// log.info(rtn);
			
			//[{"date":"1457060904","price":"2797.93","amount":"0.01","tid":"64048211","type":"sell","trade_type":"ask"}
			//,{"date":"1457060956","price":"2797.32","amount":"0.001","tid":"64048376","type":"buy","trade_type":"bid"}]
//			response.getWriter().write(rtn2.getMessage());
			
			HttpClient client = new HttpClient();
			PostMethod method = new PostMethod("http://api.vip.com/data/trades");
			client.executeMethod(method); 
			String respBody = method.getResponseBodyAsString();
			
			return standardProcessASC(respBody);
			
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return standardProcessASC("");
	}
	
	/**
	 * 原始交易数据是按tid从小到大排序
	 * @return 
	 */
	public List<TradesTimeData> standardProcessASC(String ary){
		List<TradesTimeData> desList=new ArrayList<TradesTimeData>();
		
		String result=ary;
		if(StringUtils.isEmpty(result)) return desList;
		
		JSONArray array=JSONArray.parseArray(result);
		if(array==null || array.size()==0) return desList;
		
		List<TradesData> list=new ArrayList<TradesData>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject o = array.getJSONObject(i);
			TradesData t=new TradesData();
			t.setTimes(o.getLongValue("date"));
			t.setPrice(o.getDoubleValue("price"));
			t.setAmount(o.getDoubleValue("amount"));
			t.setTid(o.getLongValue("tid"));
			t.setType(o.getString("type"));
			list.add(t);
		}
		return dealTradesDataList(list);
	}

	private List<TradesTimeData> dealTradesDataList(List<TradesData> datalist) {
		
		List<TradesTimeData> desList=new ArrayList<TradesTimeData>();
		double totalPrice=0;
		long maxTime=0;//list里最大时间
		
		for(int i=0;i<datalist.size();i++){
			TradesTimeData timeData=new TradesTimeData();
			TradesData t=datalist.get(i);
			if(i==0){
				timeData.setOpen(t.getPrice());
				timeData.setHigh(t.getPrice());
				timeData.setLow(t.getPrice());
			}else{
				if(t.getPrice()>timeData.getHigh()){
					timeData.setHigh(t.getPrice());
				}
				if(t.getPrice()<timeData.getLow()){
					timeData.setLow(t.getPrice());
				}
			}
			
			timeData.setAmount(timeData.getAmount()+t.getAmount());
			timeData.setClose(t.getPrice());
			
			totalPrice+=t.getPrice();
			
//			求出最大的时间
			if(t.getTimes()>maxTime){
				maxTime=t.getTimes();
			}
			
			desList.add(timeData);
		}
		
		for (TradesTimeData item : desList) {
			item.setTimes(maxTime-maxTime%60);
			item.setMiddle(totalPrice/datalist.size());
		}
		
		return desList;
	}
	
	
	@Page(Viewer = JSON)
	public void klineLastData(){
		long since = longParam("since");
		long to = longParam("to");
//		int  limit = intParam("limit")==0?2000:intParam("limit");//获取数据限制最大数量
		String symbol = param("symbol");
		String market = symbol;
		

		int time=intParam("step");
		
		if(time == 0){
			time = 900;
		}
		
		String type = param("type");

		if(type.equals("1")){
			time = 60;
		}else if(type.equals("5")){
			time = 300;
		}else if(type.equals("15")){
            time = 900;
        }else if(type.equals("30")){
            time = 1800;
        }else if(type.equals("60")) {
			time = 3600;
		}else if(type.equals("240")) {
            time = 14400;
        }else if(type.equalsIgnoreCase("D")){
			time = 86400;
		}else if (type.equalsIgnoreCase("5D")) {
		    time = 432000;
        }else if (type.equalsIgnoreCase("W")) {
		    time = 604800;
        }else if (type.equalsIgnoreCase("M")) {
		    time = 2592000;
        }
		
		try{
			Market m = Market.getMarkeByName(market);
			if(m==null){
				Response.append("[{error market}]"); 
				return;
			}
			
			String rtn=data(time,m);

			StringBuffer sb=new StringBuffer();
			sb.append("[");

			sb.append(rtn);

			sb.append("]");

			JSONArray arrays = JSONArray.parseArray(sb.toString());
			int num =0;
			int start = 0;
//			if(arrays.size()>limit) start = arrays.size()-limit;//k线数据起始标识
			Map<String, LinkedList<Object>> map = new LinkedHashMap<String, LinkedList<Object>>(){{
				put("t", new LinkedList<>());
				put("c", new LinkedList<>());
				put("o", new LinkedList<>());
				put("l", new LinkedList<>());
				put("h", new LinkedList<>());
				put("v", new LinkedList<>());
			}};

			long pointTime;
			double close;
			long milliTimes = time * 1000;
			for (int i = start; i < arrays.size(); i++) {
				JSONArray ja = (JSONArray) arrays.get(i);
				if (ja != null && ja.size() > 0) {
					if (ja.size() == 8) {
						ja.remove(1);
						ja.remove(1);
					}

                    pointTime = ja.getLong(0) * 1000;
					close = ja.getDouble(4);
					if (since <= pointTime && pointTime <= to) {
						num++;
						map.get("t").add(pointTime);
						map.get("o").add(ja.getDouble(1));
						map.get("h").add(ja.getDouble(2));
						map.get("l").add(ja.getDouble(3));
						map.get("c").add(close);
						map.get("v").add(ja.getDouble(5));

                        // 获取一下个点位
                        JSONArray next = i == (arrays.size() - 1) ? null : (JSONArray) arrays.get(i + 1);
                        long diff;
                        if (next != null) {
                            // 中间存在断档
                            diff = next.getLong(0) * 1000 - pointTime - 1;
                        } else {
                            // 结尾数据断档
                            diff = to - pointTime;
                        }

                        if (diff > milliTimes) {
                            num += fillPoint(since, to, map, pointTime, close, milliTimes, diff);
                        }
					}
				}
			}
            if(num == 0) {
                if (arrays.size() > 0) {
                    // 获取最后一个点位
                    JSONArray last = (JSONArray) arrays.get(arrays.size() - 1);
                    if (!last.isEmpty()) {
                        long lastPointTime = last.getLong(0) * 1000;
                        if (lastPointTime <= since) {
                            // 最后一个点位在区间之前，自动补全
                            num += fillPoint(since, to, map, lastPointTime, last.getDouble(4), milliTimes, to - lastPointTime);
                        }
                    }
                }
            }
			JSONObject result = new JSONObject();
			if(num == 0){
                result.put("s", "no_data");
            }else{
			    result.put("s", "ok");
                JSONObject datas = JSONObject.parseObject(JSONObject.toJSONString(map));
                result.putAll(datas);
            }

			json("k-line", true, result.toJSONString(),true);

		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}

    /**
     * 自动填充
     * @param since 开始时间
     * @param to 结束时间
     * @param map 集合
     * @param pointTime 上一点位
     * @param close 收盘价格
     * @param milliTimes 间隔毫秒值
     * @param diff 点位差值
     * @return 填充数
     */
    private int fillPoint(long since, long to, Map<String, LinkedList<Object>> map,
                          long pointTime, double close, long milliTimes, long diff) {
        int num = 0;
        // 存在断档
        long count = diff / milliTimes;
        long nextTime = pointTime;
        for (int j = 0; j < count; j++) {
            num++;
            nextTime = nextTime + milliTimes;
            if (since <= nextTime && nextTime <= to) {
                map.get("t").add(nextTime);
                map.get("o").add(close);
                map.get("h").add(close);
                map.get("l").add(close);
                map.get("c").add(close);
                map.get("v").add(0);
            }
        }
        return num;
    }


    @Page(Viewer = JSON)
    public void klineLastDataBack(){

        JSONObject data=new JSONObject();

        //String market = GetPrama(0);
        String moneyType = "cny";
        long since = 0;
        since = longParam("since");
        int  limit = intParam("limit")==0?2000:intParam("limit");//获取数据限制最大数量
        String symbol = param("symbol");
        String market = symbol;

        data.put("symbol", symbol);

        data.put("moneyType", moneyType);


        data.put("contractUnit", "**");
        data.put("marketName", "**");
        data.put("USDCNY", "**");

        int time=intParam("step");

        if(time == 0){
            time = 900;
        }

        String type = param("type");
//		1min
//		3min
//		5min
//		15min
//		30min
//		1hour
//		2hour
//		4hour
//		6hour
//		12hour
//		1day
//		3day
//		1week
//		if(type.equals("1min")){
//			time = 60;
//		}else if(type.equals("3min")){
//			time = 180;
//		}else if(type.equals("5min")){
//			time = 300;
//		}else if(type.equals("15min")){
//			time = 900;
//		}else if(type.equals("30min")){
//			time = 1800;
//		}else if(type.equals("1hour")){
//			time = 3600;
//		}else if(type.equals("2hour")){
//			time = 7200;
//		}else if(type.equals("4hour")){
//			time = 14400;
//		}else if(type.equals("6hour")){
//			time = 21600;
//		}else if(type.equals("12hour")){
//			time = 43200;
//		}else if(type.equals("1day")){
//			time = 86400;
//		}else if(type.equals("3day")){
//			time = 259200;
//		}else if(type.equals("1week")){
//			time = 604800;
//		}

        if(type.equals("1")){
            time = 60;
        }else if(type.equals("5")){
            time = 300;
        }else if(type.equals("60")) {
            time = 3600;
        }else if(type.equalsIgnoreCase("D")){
            time = 86400;
        }

        try{
            Market m = Market.getMarkeByName(market);
            if(m==null){
                Response.append("[{error market}]");
                return;
            }

            String rtn=data(time,m);
            //Response.append("["+rtn+"]");
            //List<TradesTimeData> list  = this.getKlineData();

            StringBuffer sb=new StringBuffer();
            sb.append("[");

            sb.append(rtn);

            sb.append("]");

            // =================== 将老的chat数据,转化为新的chat数据 ===================
//			JSONArray arrays = JSONArray.parseArray(sb.toString());
//			JSONArray resultArr = new JSONArray();
//			int num =0;//获取数据记录计数//add by zhanglinbo 20160806
//			int start = 0;
//			if(arrays.size()>limit) start = arrays.size()-limit;//k线数据起始标识
//			for(int i=start;i<arrays.size();i++){
//				JSONArray ja = (JSONArray) arrays.get(i);
//				if(ja!=null && ja.size()>0){
//					long tid = ja.getLong(0) * 1000;
//					ja.set(0, tid);
//					if(since<=tid){
//						num++;
//						resultArr.add(ja);
//						if(num>=limit) break;
//					}
//					if(ja.size() == 8){
//						ja.remove(1);
//						ja.remove(1);
//					}
//				}
//
//			}
//			data.put("data", resultArr);

            JSONArray arrays = JSONArray.parseArray(sb.toString());
            int num =0;
            int start = 0;
            if(arrays.size()>limit) start = arrays.size()-limit;//k线数据起始标识
            Map<String, LinkedList<Object>> map = new LinkedHashMap<String, LinkedList<Object>>(){{
                put("t", new LinkedList<>());
                put("c", new LinkedList<>());
                put("o", new LinkedList<>());
                put("l", new LinkedList<>());
                put("h", new LinkedList<>());
                put("v", new LinkedList<>());
            }};

            for (int i = start; i < arrays.size(); i++) {
                JSONArray ja = (JSONArray) arrays.get(i);
                if (ja != null && ja.size() > 0) {
                    if (ja.size() == 8) {
                        ja.remove(1);
                        ja.remove(1);
                    }

                    if (since <= ja.getLong(0) * 1000) {
                        num++;
                        map.get("t").add(ja.getLong(0) * 1000);
                        map.get("o").add(ja.getDouble(1));
                        map.get("h").add(ja.getDouble(2));
                        map.get("l").add(ja.getDouble(3));
                        map.get("c").add(ja.getDouble(4));
                        map.get("v").add(ja.getDouble(5));
                        if (num >= limit) break;
                    }
                }
            }

            data.put("data", map);
            json("k-line", true, data.toJSONString(),true);

        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }

	// ============== 新增接口 ==============

	/**
	 * K-line全局配置
	 *
	 * TODO 测试桩
	 */
	@Page(Viewer = JSON)
	public void klineConfig() {
		JSONObject data=new JSONObject();

		List<Object> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		map.put("value", "BITMEX");
		map.put("name", "BitMEX");
		map.put("desc", "BitMEX - Bitcoin Mercantile Exchange");
		list.add(map);
		//这些都是固定值
		data.put("exchanges", list);

		List<Object> list1 = new ArrayList<>();
		Map<String, Object> map1 = new HashMap<>();
		map1.put("name", "Bitcoin");
		map1.put("value", "bitcoin");
		list1.add(map1);
		data.put("symbols_types", list1);

		data.put("supports_group_request", false);
		data.put("supports_marks", false);
		data.put("supports_timescale_marks", false);
		data.put("supports_search", true);
		data.put("has_daily", true);
		data.put("supports_time", true);
		data.put("max_bars", 10080);

		List<String> list2 = new ArrayList<String>(){{
			add("1");
			add("3");
			add("5");
			add("15");
			add("30");
			add("60");
			add("120");
			add("180");
			add("240");
			add("360");
			add("720");
			add("1D");
			add("3D");
			add("1W");
			add("2W");
			add("1M");
		}};
//		data.put("supported_resolutions", ChartManager.getSupportedResolutions());
		data.put("supported_resolutions", list2);

		json("k-line", true, data.toJSONString(), true);
	}

	/**
	 * K-LINE获取具体交易的配置信息
	 *
	 * TODO 测试桩
	 */
	@Page(Viewer = JSON)
	public void klineConfigBySymbol() {
//		String symbol = param("symbol");

		JSONObject data=new JSONObject();

		data.put("name", "XBTUSD");
		data.put("full_name", "XBTUSD");
		data.put("symbol", "XBTUSD");
		data.put("exchange", "BITMEX");
		data.put("exchange-traded", "BITMEX");
		data.put("exchange-listed", "BITMEX");
		data.put("timezone", "UTC");
		data.put("pricescale", 100);
		data.put("minmov", 1);
		data.put("minmove2", 0);
		data.put("has_intraday", true);

		List<String> list = new ArrayList<String>(){{
			add("1");
			add("5");
			add("60");
			add("1440");
		}};
		data.put("intraday_multipliers",list);
		data.put("has_daily",true);
		data.put("has_weekly_and_monthly", false);
		data.put("has_empty_bars", false);
		data.put("force_session_rebuild", false);
		data.put("has_no_volume", false);
		data.put("has_fractional_volume", false);
		data.put("ticker", "XBTUSD");
		data.put("description", "XBTUSD: Bitcoin / US Dollar Perpetual Inverse Swap Contract");
		data.put("session", "24x7");
		data.put("data_status", "streaming");

		List<String> list1 = new ArrayList<String>(){{
			add("1");
			add("3");
			add("5");
			add("15");
			add("30");
			add("60");
			add("120");
			add("180");
			add("240");
			add("360");
			add("720");
			add("1D");
			add("3D");
			add("1W");
			add("2W");
			add("1M");
		}};
		data.put("supported_resolutions", list1);
		data.put("type", "bitcoin");

		json("k-line", true, data.toJSONString(), true);
	}


}

