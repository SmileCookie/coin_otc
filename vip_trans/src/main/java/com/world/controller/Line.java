package com.world.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tenstar.HTTPTcp;
import com.tenstar.RecordMessage;
import com.world.cache.Cache;
import com.world.dish.DishDataCacheService;
import com.world.model.CacheString;
import com.world.model.Market;
import com.world.util.CommonUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
/**
 * 图表数据
 * @author pc
 *
 */
public class Line extends BaseAction{

    /**
     *
     */
    private static final long serialVersionUID = -4887183524863239439L;
    IndexServer server = new IndexServer();

//    @Page(Viewer = "/cn/chart/ktop.jsp" , Cache= 600)
    public void index() {
        String type = param(0);
        if(type.length() == 0){
            type = "btc";
        }

        log.info("访问到后台，type:" + type + "," + request.getRequestURI());

        if("btc".equals(type)){
            setAttr("url", "/line/btcFull");
        }else if("ltc".equals(type)){
            setAttr("url", "/line/ltcFull");
        }else if("btq".equals(type)){
            setAttr("url", "/line/btqFull");
        }
    }


    /**
     *比特币K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void btc() {
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));

    }
    /**
     *莱特币K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void ltc() {

        request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void eth() {
        request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void etc() {
        request.setAttribute("serijavascripparam", Market.getMarket("etc_cny"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void ethbtc() {
        request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void dao() {
        request.setAttribute("serijavascripparam", Market.getMarket("dao_cny"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void daoeth() {
        request.setAttribute("serijavascripparam", Market.getMarket("dao_eth"));
    }

    /**
     *比特权K线图简化
     */
//    @Page(Viewer = "/cn/chart/simple.jsp")
    public void btq() {
        request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));

    }

    /**
     *比特币K线图简化
     */
//    @Page(Viewer = "/cn/chart/index.jsp")
    public void btcFull() {
        request.setAttribute("serijavascripparam", Market.getMarket("btc_cny"));

    }
    /**
     *莱特币K线图简化
     */
//    @Page(Viewer = "/cn/chart/index.jsp")
    public void ltcFull() {

        request.setAttribute("serijavascripparam", Market.getMarket("ltc_cny"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/index.jsp")
    public void ethFull() {
        request.setAttribute("serijavascripparam", Market.getMarket("eth_cny"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/index.jsp")
    public void ethbtcFull() {
        request.setAttribute("serijavascripparam", Market.getMarket("eth_btc"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/index.jsp")
    public void daoFull() {
        request.setAttribute("serijavascripparam", Market.getMarket("dao_cny"));
    }

    /**
     * 以太币K线图简化
     */
//    @Page(Viewer = "/cn/chart/index.jsp")
    public void daoethFull() {
        request.setAttribute("serijavascripparam", Market.getMarket("dao_eth"));
    }

    /**
     *比特权K线图简化
     */
//    @Page(Viewer = "/cn/chart/index.jsp")
    public void btqFull() {
        request.setAttribute("serijavascripparam", Market.getMarket("btq_cny"));

    }

    /**
     *获取图表数据
     */
    @Page(Viewer = ".json" )
    public void get(){
        try{
            int time=Integer.parseInt(GetPrama(1));
            String jsoncallback=request.getParameter("jsoncallback");

            Market m=Market.getMarkeByName(GetPrama(0));
            if(m==null){
                Response.append(jsoncallback+"([{error market}])");
                return;
            }


            String rtn=data(time,m);
            Response.append(jsoncallback+"(["+rtn+"])");
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }

    /**
     *获取图表数据
     */
    @Page(Viewer = ".json" )
    public void period(){
        try{
            int time=intParam("step");

            Market m = Market.getMarkeByName(GetPrama(0));
            if(m==null){
                Response.append("[{error market}]");
                return;
            }


            String rtn=data(time,m);
            Response.append("["+rtn+"]");
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
	 *获取顶部三个完整数据
	 */
	@Page(Viewer = ".json")
	public void topall(){
		try{
			//接收页面参数
			String jsoncallback=request.getParameter("jsoncallback");

			// 搜索框 币种名称
//			String coinName=request.getParameter("coinName");
//			if (StringUtils.isEmpty(coinName)){
//				coinName = "";
//			}

			String legalTender = param("legalTender");
			String topall = getTopall(legalTender);
			//解决NEO(ANS)，括号乱码问题
			topall = java.net.URLDecoder.decode(topall, "UTF-8");
//			if (! coinName.equals("")){
//				List<Map<String,Object>> list = new ArrayList<>();
//				topall = topall.substring(topall.indexOf("(")+1,topall.lastIndexOf(")"));
//				JSONArray jsonArray = JSONArray.parseArray(topall);
//				for (Object obj : jsonArray) {
//					Map map = (Map)obj;
//					for (Object pp : map.keySet()) {
//						if (pp.toString().contains(coinName)) {
//							Map<String,Object> map1 = new HashMap<>();
//							Object obj1 = map.get(pp);
//							map1.put(pp.toString(),obj1);
//							list.add(map1);
//						}
//					}
//				}
				//Response.append(jsoncallback +list);
			//}else {
				Response.append(jsoncallback +topall);
			//}
		}catch(Exception ex){
			log.error(ex.toString(), ex);
		}
	}
    private static CacheString topallStr = null;


    /**
     *返回数据：[当前价格，买一价格，卖一价格，24小时最低价格，24小时最高价格，24小时成交总量]
     *
     * @param legalTender 法币
     * @return
     */
    public String getTopall(String legalTender){
        try{
            if(topallStr != null && topallStr.isAvailable()){
                return topallStr.getStr();
            }else{
                String topall = "";//所有币种的最新价格信息
                StringBuffer sb = new StringBuffer();
                Map<String, Market> map = CommonUtil.sortMapByValue(Market.markets);
                Iterator<Entry<String,Market>> iter = map.entrySet().iterator();

                while(iter.hasNext()){
                    Entry<String,Market> entry = iter.next();

                    String hotdata = g(entry.getKey(), entry.getValue().getNumberBiFullName(), legalTender);
                    sb.append(",");
                    sb.append(hotdata);
                }
                if(sb.length()>1){
                    topall = "([{"+sb.substring(1).toString()+"}])";
                }

                topallStr = new CacheString(topall, System.currentTimeMillis(), 3000);
                return topallStr.getStr();
            }
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
        return "([])";
    }

    public String g(String name){
        String data=DishDataCacheService.getHotData(name);
        if(data==null)
            data="0,0,0,0,0,0";
        String key ="";

        key =name+"_hotdata";
        return "\""+key+"\":["+data+"]";
    }

    public String g(String name, String fullName, String legalTender) throws UnsupportedEncodingException {
        String data=DishDataCacheService.getHotData(name);
        if (data == null) {
            data = "0,0,0,0,0,0,0,[[]],0,0";
        }

        String key ="";

//		key =name+"_hotdata"+"_"+ URLEncoder.encode(fullName, "UTF-8");
        key =name+"_hotdata"+"_"+ URLEncoder.encode(fullName, "UTF-8").replaceAll(" ","+");
        return "\""+key+"\":["+data+"]";
    }

    /**
     *获取顶部数据，本函数不提供顶部数据获取功能
     */
    @Page(Viewer = ".json" )
    public void top(){
        try{

            String jsoncallback=request.getParameter("jsoncallback");

            Market m=Market.getMarkeByName(GetPrama(0));
            if(m==null){
                Response.append(jsoncallback+"([{error market}])");
                return;
            }

            Response.append(jsoncallback+"([{"+g(m.market)+"}])");

        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }

    /**
     * 获取当前价格
     *   /Line/GetCurrentPrice-btc_cny?lastTime=1300000
     *   返回 直接返回内容
     */
    @Deprecated
    @Page()
    public void GetCurrentPrice() {
        try {

            Market m = Market.getMarkeByName(GetPrama(0));
            if (m == null) {
                response.getWriter().write("no market for param 0");
                return;
            }
            long lastTime = Long.parseLong(request.getParameter("lastTime"));

            String cacheKey = m.numberBiEn.toLowerCase() + "_" + m.exchangeBiEn.toLowerCase() + "_l_price";

            Object obj = Cache.Get(cacheKey);

            if (obj == null) {
                RecordMessage myObj = new RecordMessage();
                myObj.setMarket(m.market);
                if (m.listenerOpen) {
                    try {
                        RecordMessage rtn2 = server.getCurrentPrice(myObj, m);
                        response.getWriter().write(rtn2.getMessage());
                    } catch (Exception ex2) {
                        response.getWriter().write(ex2.toString());
                    }
                } else {
                    try {
                        String param = HTTPTcp.ObjectToString(myObj);
                        log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
                        String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getCurrentPrice", param);
                        log.info(rtn);
                        RecordMessage rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
                        response.getWriter().write(rtn2.getMessage());
                    } catch (Exception ex2) {
                        response.getWriter().write(ex2.toString());
                    }
                }
            } else {
                response.getWriter().write(obj.toString());
            }
        } catch (Exception ex) {
        }
    }

    /**
     * 获取委托和交易历史记录数据
     */
    @Page(Viewer = ".json")
    public void GetTrans(){
        try{
            String jsoncallback=request.getParameter("jsoncallback");
            Market m=Market.getMarkeByName(GetPrama(0));
            if(m==null){
                Response.append(jsoncallback+"([{error market}])");
                return;
            }
            int length=Integer.parseInt(request.getParameter("length"));
            String data = DishDataCacheService.getDishDepthData(m.market, length);
            if(data==null){
                data=DishDataCacheService.getDishDepthData(m.market, 60);
            }
            Response.append(jsoncallback+data);

        }catch(Exception ex){
        }
    }

    /**
     * 获取市场深度图
     * modify by xwz 20171108
     * 由显示各50条改为按成交价格x%偏移
     */
    @Page(Viewer = JSON)
    public void getMarketDepth() {
        String jsoncallback=request.getParameter("jsoncallback");
        Market m=Market.getMarkeByName(GetPrama(0));
        if(m==null){
            Response.append(jsoncallback+"([{error market}])");
            return;
        }

//		String info = Cache.Get(m.market + "_market_depth_50");
        String info = Cache.Get(m.market + "_market_depth_200");
        if (StringUtil.exist(info)) {
            Response.append(jsoncallback + info);
            return;
        } else {
            long startTime = System.currentTimeMillis();
            String data = DishDataCacheService.getDishDepthData(m.market, 200);
            if (data != null && data.trim().length() > 0) {
//                JSONArray arr = JSONArray.parseArray(data.substring(0,data.length()-1).substring(1));
                JSONObject json = JSONObject.parseObject(data);

                JSONArray listDown = json.getJSONArray("listDown");
                JSONArray listUp = json.getJSONArray("listUp");

                String downRtn="", upRtn="";

                BigDecimal downNum = BigDecimal.ZERO; // down中的总额
                BigDecimal upNum = BigDecimal.ZERO; // up中的总额
                BigDecimal downTotal = BigDecimal.ZERO; // down的总币量
                BigDecimal upTotal = BigDecimal.ZERO; // up中的总币量

				/*start by xwz 20171108 按价格偏移取市场深度*/
                //当前市场价格
                BigDecimal currentPrice = json.getBigDecimal("currentPrice");
                Map<String, BigDecimal> priceLineMap = getUpAndDownPrice(m.getMarket(), currentPrice);
                BigDecimal downPriceLine  = priceLineMap.get("downPriceLine");
                BigDecimal upPriceLine  = priceLineMap.get("upPriceLine");
                if(currentPrice.compareTo(BigDecimal.ZERO) > 0){
                    for (Object obj : listDown) {
                        if(downPriceLine.compareTo(((JSONArray)obj).getBigDecimal(0)) > 0){
                            continue;
                        }
                        downNum = downNum.add(((JSONArray) obj).getBigDecimal(1).multiply(((JSONArray)obj).getBigDecimal(0)));
                        downTotal = downTotal.add(((JSONArray) obj).getBigDecimal(1));
                        downRtn = downRtn + ",[" + ((JSONArray)obj).getBigDecimal(0) + "," + downTotal + "," + downNum + "]";
                    }


                    for (Object obj : listUp) {
                        if(upPriceLine.compareTo(((JSONArray)obj).getBigDecimal(0)) < 0){
                            continue;
                        }
                        upNum = upNum.add(((JSONArray) obj).getBigDecimal(1).multiply(((JSONArray)obj).getBigDecimal(0)));
                        upTotal = upTotal.add(((JSONArray) obj).getBigDecimal(1));
                        upRtn = upRtn + ",[" + ((JSONArray)obj).getBigDecimal(0) + "," + upTotal + "," + upNum + "]";
                    }
                }

                downRtn = downRtn.trim().length() > 0 ? downRtn.substring(1) : "";
                upRtn = upRtn.trim().length() > 0 ? upRtn.substring(1) : "";

                String rtn = "([{\"listDown\":[" + downRtn + "],\"listUp\":[" + upRtn + "]}])";

//				Cache.Set(m.market + "_market_depth_50", rtn, 2 * 60);
                //缓存数据改为10s一次
                Cache.Set(m.market + "_market_depth_200", rtn, 10);
                Response.append(jsoncallback+rtn);
                return;
            }

            Response.append(jsoncallback+"([])");
        }
    }

    /**
     * 计算取市场深度的偏移价格
     * @param marketName
     * @param currentPrice
     * @return
     */
    public Map<String, BigDecimal> getUpAndDownPrice(String marketName, BigDecimal currentPrice){
        Map<String, BigDecimal> upAndDownPriceMap = new HashMap<>();
        BigDecimal downPriceLine = BigDecimal.ZERO;
        BigDecimal upPriceLine = BigDecimal.ZERO;
        BigDecimal deptRate = new BigDecimal("5");
        if(StringUtils.isNotBlank(Cache.Get(marketName + "_market_depth_deviation_rate"))){
            try{
                deptRate = new BigDecimal(Cache.Get(marketName + "_market_depth_deviation_rate"));
            }catch (Exception e){
                log.error("缓存中market_depth_deviation_rate配置错误", e);
            }
        }else{
            log.error("缓存中market_depth_deviation_rate没有配置，请在重新修改价格偏移Exception！market:" + marketName);
        }
        downPriceLine = currentPrice.multiply(new BigDecimal("100").subtract(deptRate)).divide(new BigDecimal("100"));
        upPriceLine = currentPrice.multiply(new BigDecimal("100").add(deptRate)).divide(new BigDecimal("100"));
        upAndDownPriceMap.put("downPriceLine", downPriceLine);
        upAndDownPriceMap.put("upPriceLine", upPriceLine);
        return upAndDownPriceMap;
    }

    /**
     *获取BTC/LTC/BTQ的 最新价，最高价，最低价，买一，卖一，24小时成交，24小时均价
     */
    @Page(Viewer=JSON)
    public void getCoinInfo(){
        try{
            String type = param("type");
            if(StringUtils.isNotEmpty(type) && ("btc".equals(type)||"ltc".equals(type)||"btq".equals(type))) {
                String coin = g(type+"default");
                json("success...",true,"[{\""+coin.substring(coin.indexOf("_")+1)+"}]",true);
            } else {
                json("fail...",false,"[{}]",true);
            }
        }catch(Exception ex){
            log.error(ex.toString(), ex);
        }
    }

    /**
     * 获取委托和交易历史记录数据
     */
    @Page(Viewer = ".json" )
    public void getDeepth() {
        try {
            String jsoncallback = request.getParameter("jsoncallback");
            Market m = Market.getMarkeByName(GetPrama(0));
            if (m == null) {
                Response.append(jsoncallback + "({error market})");
                return;
            }
            long lastTime = Long.parseLong(request.getParameter("lastTime"));
            int length = Integer.parseInt(request.getParameter("length"));
            if (length != 20 && length != 50 && length != 100) {
                Response.append(jsoncallback + "([{\"lastTime\":" + lastTime + "}])");
                return;
            }
            Object obj = null;//Cache.Get(m.market + "_deepthchartLastTime");

            if (obj == null) {
                RecordMessage myObj = new RecordMessage();
                myObj.setPageSize(length);
                myObj.setMarket(m.market);
                try {
                    RecordMessage rtn2 ;
                    if(m.listenerOpen){
                        rtn2 = server.getDeepth(myObj,m);
                        Response.append(jsoncallback + rtn2.getMessage());
                    }else{
                        String param = HTTPTcp.ObjectToString(myObj);
                        log.info("[请求转发] 请求转发到业务处理服务器 " + m.ip + ":" + m.port);
                        String rtn = HTTPTcp.Post(m.ip, m.port, "/server/getDeepth", param);
                        rtn2 = (RecordMessage) HTTPTcp.StringToObject(rtn);
                    }
                } catch (Exception ex2) {
                    Response.append(jsoncallback + "({\"lastTime\":0})");
                }
            } else if (lastTime == Long.parseLong(obj.toString())) {
                Response.append(jsoncallback + "({\"lastTime\":" + lastTime + "})");
            } else {
                String data = Cache.Get(m.market + "_deepthchart" + length);
                Response.append(jsoncallback + data);
            }
        } catch (Exception ex) {
        }
    }
}
