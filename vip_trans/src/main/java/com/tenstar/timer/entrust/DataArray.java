package com.tenstar.timer.entrust;

import com.match.entrust.MemEntrustDataProcessor;
import com.redis.RedisUtil;
import com.tenstar.timer.TransRecordBean;
import com.tenstar.timer.chart.ChartData;
import com.tenstar.timer.dish.DishDataManager;
import com.world.cache.Cache;
import com.world.data.big.MysqlDownTable;
import com.world.data.big.table.DownTableManager;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.QueryDataType;
import com.world.dish.DishDataCacheService;
import com.world.model.Market;
import com.world.model.daos.chart.ChartManager;
import com.world.model.entitys.record.TransRecord;
import com.world.util.WebUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 保存数组
 * @author netpet
 */
public class DataArray {
    public static Logger log = Logger.getLogger(DataArray.class);
    /////////////////////////////以下通过定时更新产生，可以有延时//////////////////////////////////////////
    private long version = 0;//获取的时间戳，版本号
    public static BigDecimal todayMax;//今日最高，没间隔10秒钟获取一次新的
    public static BigDecimal todayMin;//今日最低，没间隔10秒钟获取一次新的
    public static BigDecimal numberOf24hour;//没间隔10秒钟获取一次新的


    /////////////////////////////以下计算产生//////////////////////////////////////////
    private boolean currentIsBuy;//启动时计算，每次变更更新
    private BigDecimal lastPrice;//最新价格，设置，以后每次更新
    private BigDecimal buyOne =BigDecimal.ZERO;//买一价格，启动时设置，以后更新
    private BigDecimal sellOne = BigDecimal.ZERO;//卖一价格,启动时设置，以后更新

    /////////////////////////////以下为容器相关//////////////////////////////////////////
    // 最大价格，从1开始
    private int maxPrice;//当前系统支持的最大价格

    public int tradeIndex = 0;// 成交记录表指针，用于指向最后一个记录的index
    private String name;//本服务的名称
    //private Market m;//币种

    public static String trade_record_key = "trade_record_key_";

    private static ExecutorService topPool = Executors.newFixedThreadPool(50);

    /**
     * 设置缓存
     * @param nameKey 名称
     * @param valueKey 值
     */
    public void SetCache(String nameKey,String valueKey ){

        Cache.Set(nameKey, valueKey, 60*120);//1小时
        //log.info(name+"_"+nameKey+"::"+Cache.Get(name+"_"+nameKey).length());
    }
    /**
     * 从头初始化列表，每次运行加载一次即可
     * @param name
     * @param maxPrice
     */
    public DataArray(String name,int maxPrice){
        this.name=name;
        this.maxPrice=maxPrice;
        //this.m = Market.getMarket(name);
        //ReInit();
    }




    public void initEntrust(int newLastPrice,long number,int type){

    }

    public void asyncUpdateEntrust(BigDecimal newLastPrice,BigDecimal number,int type,Market m){
//        AsynMethodFactory.addWork(DataArray.class, "UpdateEntrust", new Object[]{newLastPrice,number,type,m});
        lastPrice=newLastPrice;
        String cacheKey = m.numberBiEn.toLowerCase()+"_"+m.exchangeBiEn.toLowerCase()+"_l_price";
        Cache.Set(cacheKey, Market.formatMoney(lastPrice,m) + "", 15*60*60*24);//保存15天
    }

    /**
     * 针对已经发生成交的情况的更新
     */
    public void UpdateEntrust(BigDecimal newLastPrice, BigDecimal number, int type, Market m){
        lastPrice=newLastPrice;
        if(type==1){
            //data[lastPrice][0]=data[lastPrice][0]-number;//这里是反着的，因为买入成交，所以原本的负职应该成为0或者大于原职
            currentIsBuy=true;
        }else if(type==0){
            //data[lastPrice][0]=data[lastPrice][0]+number;//也是反着的
            currentIsBuy=false;
        }else{
            log.error("发生了错误！未知的类型");
            return;
        }

        BigDecimal[] bss = MemEntrustDataProcessor.getBuyOneAndSellOne(m);
        if(bss != null){
            buyOne = (BigDecimal)bss[0];
            sellOne = (BigDecimal)bss[1];
        }

        String cacheKey = m.numberBiEn.toLowerCase()+"_"+m.exchangeBiEn.toLowerCase()+"_l_price";
        Cache.Set(cacheKey, Market.formatMoney(lastPrice,m) + "", 15*60*60*24);//保存15天
    }

    public void updateTicker(BigDecimal newLastPrice,BigDecimal number,int type,Market m){
        lastPrice=newLastPrice;
        setCurrentPrice(m);
    }


    /**
     * 全站通用的当前价格
     */
    public void setCurrentPrice(Market m){
        String cacheKey = m.numberBiEn.toLowerCase() + "_" + m.exchangeBiEn.toLowerCase() + "_l_price";
        log.info("[买一卖一价格] 买一卖一价格信息(缓存15天): " + cacheKey + "=" + Market.formatMoney(lastPrice,m));
        Cache.Set(cacheKey, Market.formatMoney(lastPrice,m)+"",  15*60*60*24);//保存15天
        ChartData.GetTiker(m);
    }
    /**
     * 全站通用的当前价格
     */
    public String getCurrentPrice(Market m){
        String cacheKey = m.numberBiEn.toLowerCase() + "_" + m.exchangeBiEn.toLowerCase() + "_l_price";
        log.info("[买一卖一价格] 买一卖一价格信息(缓存15天)" + cacheKey + "=" + Market.formatMoney(lastPrice,m));
        Cache.Set(cacheKey, Market.formatMoney(lastPrice,m)+"",  15*60*60*24);//保存15天
        return Market.formatMoney(lastPrice,m)+"";
    }




    public String getSqlTradeList(int pageSize,Market m){
        String sql = "select unitPrice, totalPrice, numbers, types, times,userIdBuy,userIdSell from transrecord where unitPrice > 0  order by times desc limit 0, ?";
        List lists = Data.Query(m.db,sql, new Object[]{pageSize});
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;
            sb.append(",["+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(0).toString())),m)+","+Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","
                    +Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+beb.get(4)+"]");
        }
        String rtn=sb.toString();
        if(rtn.length()>0)
            rtn=rtn.substring(1);
        return rtn;
    }



    /**
     * 获取交易记录列表
     * @return
     */
    public String GetTradeListOuter(int since,Market m){
        String rtn = ChartManager.getSince(since, 60, m).toJSONString();
        DishDataCacheService.setSinceTrade(m.market, since, rtn, 60);
        return rtn;

    }

    public String GetDepth50(Market m){

        StringBuilder data50Outer=new StringBuilder();
        String sells50 = DishDataManager.getSellList(50,true,m);//GetSellList();


        String buy50 = DishDataManager.getBuyList(50,m);//GetBuyList(50);
        data50Outer.append("{\"asks\":[").append(sells50).append("],\"bids\":[").append(buy50).append("]}");
        String rtn=data50Outer.toString();
        DishDataCacheService.setDishDepthKline50(m.market, rtn, 2 * 60 * 60);
        return rtn;

    }

    public  void getTop(int userId, Market m){
//        AsynMethodFactory.addWork(DataArray.class, "getTopThread2016", new Object[]{userId,m});
        topPool.execute(() -> {
            getTopThread2016(userId, m);
        });
    }

    /****
     * 为用户生成显示页面数据
     * @param userId
     * @return
     */
    public String getTopThread2016(int userId,Market m){

        long s1 = System.currentTimeMillis();

        //log.info(userId+"：Gettop"+userId +",new:" + total);
        String planstring = "[]";
        String planHisString = "[]";

        String plansql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feerate,triggerPrice,webId,triggerPriceProfit,unitPriceProfit,totalMoney,formalEntrustId,'true' as planType  from plan_entrust where  userId=? and status = '-1' order by status asc, entrustId desc limit ?,? ";
        List planlists = Data.Query(m.db,plansql, new Object[]{userId, 0 ,30});//计划委托记录
        StringBuilder plansb=new StringBuilder();
        for(Object b : planlists){
            List beb = (List) b;
            plansb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                    Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString())),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(11).toString())),m)+","+
                    Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(12).toString())),m)+","+Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(13).toString())),m)+",'"+beb.get(14)+"','"+beb.get(15)+"','"+m.getMarket()+"']");
        }


        if(plansb.length() > 0){
            planstring = "[" + plansb.substring(1) +"]";
        }

        /* 计划委托数据 暂时先不查询，还没用到计划委托.zhanglinbo 20160928

        //获取计划委托的历史记录zhanglinbo 20160829
       /* String planHissql="select  entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feerate,triggerPrice,webId from plan_entrust where  userId=? and status>=0  and unitPrice>0 order by entrustId desc limit ?,? ";

        List planHislists = Data.Query(m.db,planHissql, new Object[]{userId, 0 ,10});//历史计划委托记录
        StringBuilder planHissb=new StringBuilder();
        for(Object b : planHislists){
        	List beb = (List) b;
        	plansb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
        	Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
        	Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString())),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+"]");
        }


        if(planHissb.length() > 0){
        	planHisString = "[" + planHissb.substring(1) +"]";
        }*/

        String rtn = "";//最后结果字符串

        //1.获取待成交数据，状态为 status = 3 ,未成交记录 查询开始 ,获取前10条 ......
        long total = 0;

        String sql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status ,feeRate, 0 as triggerPrice,webId from entrust where  userId=? and status=3 and unitPrice>0   order by entrustId desc limit ?,?";
        List<Object> lists = Data.Query(m.db,sql, new Object[]{userId , 0 ,30});//未成交记录列表
        //待成交列表数据
        StringBuilder sb=new StringBuilder();
        total = lists.size();//状态为3的记录总条数

        for(Object b : lists){
            List<Object> beb = (List<Object>) b;
            sb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                    Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString())),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","
                    +Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+",'"+m.getMarket()+"']");
        }
        String status3rtn=sb.toString();
        if(status3rtn.length() > 0){
            status3rtn = "[" + status3rtn.substring(1) +"]";
        }else{
            status3rtn = "[]";
        }

        //2.获取已成交数据，状态 status = 2 已成交记录差先开始，获取前10条
        //只获取entrust表数据，近七天数据，单独另外接口获取历史委托 entrust_all add by buxianguan
        String status2Rtn = "";
        long status2Total = 0;
        //有成交列表记录
        String status2sql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feerate, 0 as triggerPrice,webId  from entrust where userId=? and status=2 and completeNumber>0 order by entrustId desc limit ?,?";
        List<Object> status2lists = Data.Query(m.db,status2sql, new Object[]{userId , 0 ,30});//有成交记录
        StringBuilder status2sb=new StringBuilder();
        status2Total = status2lists.size();//状态为2的记录总条数
        for(Object b : status2lists){
            List<Object> beb = (List<Object>) b;
            status2sb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                    Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString())),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+",'"+m.getMarket()+"']");

        }
        status2Rtn = status2sb.toString();
        if(status2Rtn.length() > 0){
            status2Rtn = "[" + status2Rtn.substring(1) +"]";
        }else{
            status2Rtn = "[]";
        }

        long version=System.currentTimeMillis();

        rtn= "\"lastTime\":" + version + ",\"record\":"+status3rtn+",\"hrecord\":[],\"precord\":" + planstring;

        status2Rtn = "\"lastTime\":" + version + ",\"record\":" + status2Rtn;

        //存储用户的第一页
//        Cache.Set(m.market+"_"+"userrecord_version_"+userId, version+"", 60*120);

        RedisUtil.set(m.market+"_"+"userrecord_"+userId, rtn, 60*120);

        RedisUtil.set(m.market+"_"+"userrecord_status2_"+userId, status2Rtn, 60*120);

//        Cache.Set(m.market+"_"+"userrecord_status_1_"+userId, planstring, 60*120);
        long s2 = System.currentTimeMillis();

        log.info("[缓存用户第一页成交信息] userId:" + userId+", 缓存第一页成交信息, 耗时：" + (s2 - s1));
        // ClientAsynCallBack.execute(WebSocketClient.class, "noticeRecordUser", new Object[]{String.valueOf(userId),rtn,MemEntrustMatchProcessor.da.name});
        return rtn;
    }

    /**
     * 清除用户委托缓存，撮合的时候调用
     * @param m
     * @param userId
     */
    public void clearUserRecord(Market m, long userId) {
        topPool.execute(() -> {
//            Cache.Delete(m.market + "_userrecord_version_" + userId);
            RedisUtil.delete(m.market + "_userrecord_" + userId);
            RedisUtil.delete(m.market + "_userrecord_status2_" + userId);
//            Cache.Delete(m.market + "_userrecord_status_1_" + userId);
        });
    }

    /**
     * 用户主动生成用户数据
     * @param userId 用户id 用户id
     * @return
     */
    public  String getDetails(int userId,long entrustId,Market m){
/*//		QueryDataType qdt = QueryDataType.DEFAULT;
//		if(type == 2){
//			qdt = QueryDataType.DOWN;
//		}
		//and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
		StringBuilder where=new StringBuilder();
	    String w=where.toString();
	    //String sql = DownTableManager.getProxySql("select transRecordId,unitPrice,totalPrice,numbers,TYPES,times from transrecord where  ((userIdBuy="+userId+" and entrustIdBuy="+entrustId+") or (userIdSell="+userId+" AND entrustIdSell="+entrustId+")) and unitPrice>0", QueryDataType.DEFAULT);
	    //ETC盘查询费率
        String feeRateFiled = "";
			feeRateFiled=" ,(select feeRate from entrust where entrustId = "+entrustId+") feeRate ";
		String sql="SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times "+feeRateFiled+" FROM transrecord WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0 ";
		List lists = Data.Query(m.db,sql, new Object[]{userId,entrustId,userId,entrustId});//提现记录
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
        	List beb = (List) b;

        	sb.append(",["+beb.get(0)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
        			Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+beb.get(4)+","+beb.get(5));
        	sb.append(","+beb.get(6)+"]");

        }

        String rtn=sb.toString();

        if(rtn.length()==0)
        	rtn= "\"record\":[]";
        else
        	rtn= "\"record\":["+rtn.substring(1)+"]";

        return rtn;*/

        // FIXME: 2017/8/17 suxinjie 查询迁移后的数据
        String sql = "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0 union all " +
                "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord_all WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0";

        List lists = Data.Query(m.db, sql, new Object[]{userId, entrustId, userId, entrustId, userId, entrustId, userId, entrustId});
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;
            sb.append(",["+beb.get(0) + ","
                    + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m) + ","
                    + Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m) + ","
                    + Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m) + ","
                    + beb.get(4) + ","
                    + beb.get(5) +",'"
                    +m.getMarket()+"']");
        }

        String rtn=sb.toString();
        if(rtn.length()==0)
            rtn= "\"record\":[]";
        else
            rtn= "\"record\":["+rtn.substring(1)+"]";
        return rtn;
    }

    /**
     * 历史委托详情
     * @param userId 用户id 用户id
     * @return
     */
    public List<TransRecord> getTransRecordDetails(int userId, long entrustId, Market m){
        List<TransRecord> transRecords = new ArrayList<>();
        // FIXME: 2017/8/17 suxinjie 查询迁移后的数据
        String sql = "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times,feesBuy,feesSell FROM transrecord WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0 union all " +
                "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times,feesBuy,feesSell FROM transrecord_all WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0";

        List<Bean> lists = Data.Query(m.db, sql, new Object[]{userId, entrustId, userId, entrustId, userId, entrustId, userId, entrustId},TransRecord.class);
        for(Bean bean : lists){
            TransRecord transRecord = (TransRecord)bean;
            transRecords.add(transRecord);
        }
        return transRecords;
    }


    /**
     * 根据用户id，获取这个用户的交易记录
     * @param userId 用户id
     * @param
     * @return
     */
    public String getUserTransRecord(int userId,long sinceTransRecordId, int pageSize,Market m){
//		QueryDataType qdt = QueryDataType.DEFAULT;
//		if(type == 2){
//			qdt = QueryDataType.DOWN;
//		}
        //and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
        StringBuilder where=new StringBuilder();
        String w=where.toString();
        //String sql = DownTableManager.getProxySql("select transRecordId,unitPrice,totalPrice,numbers,TYPES,times from transrecord where  ((userIdBuy="+userId+" and entrustIdBuy="+entrustId+") or (userIdSell="+userId+" AND entrustIdSell="+entrustId+")) and unitPrice>0", QueryDataType.DEFAULT);

        String sql=DownTableManager.getProxySql("SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times,entrustIdBuy,entrustIdSell FROM transrecord WHERE (userIdBuy=? or userIdSell=?) and transRecordId>=? and unitPrice>0 limit 0,?", QueryDataType.DEFAULT);
//		log.info("根据用户id，获取这个用户的交易记录sql:" + sql + "，参数：" + userId+","+sinceTransRecordId);
        List lists = Data.Query(m.db,sql, new Object[]{userId,userId,sinceTransRecordId, pageSize});//提现记录
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;

            sb.append(",["+beb.get(0)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+beb.get(4)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+"]");
        }

        String rtn=sb.toString();

        if(rtn.length()==0)
            rtn= "\"record\":[]";
        else
            rtn= "\"record\":["+rtn.substring(1)+"]";

        return rtn;
    }


    /**
     * 根据委托单id，获取这个委托单交易记录
     * @param userId 用户id
     * @param
     * @return
     */
    public String getOrderTransRecord(int userId,long entrustId,Market m){
//		QueryDataType qdt = QueryDataType.DEFAULT;
//		if(type == 2){
//			qdt = QueryDataType.DOWN;
//		}
        //and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
        StringBuilder where=new StringBuilder();
        String w=where.toString();
        //String sql = DownTableManager.getProxySql("select transRecordId,unitPrice,totalPrice,numbers,TYPES,times from transrecord where  ((userIdBuy="+userId+" and entrustIdBuy="+entrustId+") or (userIdSell="+userId+" AND entrustIdSell="+entrustId+")) and unitPrice>0", QueryDataType.DEFAULT);

        String sql="SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times,entrustIdBuy,entrustIdSell from transrecord where ( (userIdBuy=? and entrustIdBuy=?) or (userIdSell=? and entrustIdSell=?) ) and unitPrice>0 ";
        log.info("根据委托单id，获取这个委托单交易记录sql:" + sql + "，参数：" + userId+","+entrustId);
        List lists = Data.Query(m.db,sql, new Object[]{userId, entrustId, userId, entrustId});//提现记录
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;

            sb.append(",["+beb.get(0)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+beb.get(4)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+"]");
        }

        String bakSql="SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times,entrustIdBuy,entrustIdSell from transrecord_all where ( (userIdBuy=? and entrustIdBuy=?) or (userIdSell=? and entrustIdSell=?) ) and unitPrice>0 ";
        log.info("备份数据，根据委托单id，获取这个委托单交易记录sql:" + bakSql + "，参数：" + userId+","+entrustId);
        List bakLists = Data.Query(m.db,bakSql, new Object[]{userId, entrustId, userId, entrustId});//提现记录
        for(Object b : bakLists){
            List beb = (List) b;

            sb.append(",["+beb.get(0)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString()))),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+beb.get(4)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+"]");
        }

        String rtn=sb.toString();

        if(rtn.length()==0)
            rtn= "\"record\":[]";
        else
            rtn= "\"record\":["+rtn.substring(1)+"]";

        return rtn;
    }

    /**
     * 用户主动生成用户数据
     * @param userId 用户id 用户id
     * @param entrustId
     * @return
     */
    public  String getOrderByEntrustId(int userId,long entrustId,Market m){
        StringBuilder sb=new StringBuilder();


        if(userId > 0 && entrustId > 0){
            String sql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status ,feeRate from entrust where entrustId=? and userId=? and unitPrice>0";
            List beb = (List)Data.GetOne(m.db,sql, new Object[]{entrustId, userId});//提现记录

            if(beb != null){
                //for(Object b : lists){
                //List beb = (List) b;
                sb.append(",["+beb.get(0)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                        Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                        Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString())),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7));

                sb.append(","+beb.get(8)+"]");

                //}
            }else{
                sql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feeRate from entrust_all where entrustId=? and userId=? and unitPrice>0";
                beb = (List)Data.GetOne(m.db,sql, new Object[]{entrustId, userId});//提现记录

                if(beb != null){
                    //for(Object b : lists){
                    //List beb = (List) b;
                    sb.append(",["+beb.get(0)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                            Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                            Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString())),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7));
                    sb.append(","+beb.get(8)+"]");


                }
                //}
            }
        }
        String rtn=sb.toString();
        if(rtn.length()==0)
            rtn= "\"record\":[]";
        else
            rtn= "\"record\":["+rtn.substring(1)+"]";
        return rtn;
    }

    public  String getTop(int webId,int userId,int pageIndex,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long pageSize,int status,Market m){
        return getTop(webId, userId, pageIndex, type, timeFrom, timeTo, numberFrom, numberTo, priceFrom, priceTo, pageSize, status, 1,m);
    }

    /**
     * 获取用户指定类型的交易管理数据
     * @param webId 网站id 网站id（暂时都设置城8）
     * @param userId 用户id 用户id
     * @param pageIndex 页码从1开始
     * @param pageSize 页码大小 10
     * @param type 类型   0 卖出  1 买入  -1不限制
     * @param timeFrom //时间   System.currentTimeMillis()
     * @param timeTo
     * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
     * @param numberTo//数量查询
     * @param priceFrom 最低价格
     * @param priceTo 最高价格
     * @param pageSize 页码大小 最大200
     * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
     * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
     */
    public  String getTop(int webId,int userId,int pageIndex,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long pageSize,int status,int dateTo,Market m){

        if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==3&&pageSize<=30){
            //getTop(userId);
            return getTopThread2016(userId,m);
        }

        if(pageSize==0)
            pageSize=30;
        if(pageSize>1000)
            pageSize=1000;

        //and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
        StringBuilder where=new StringBuilder();
        where.append(" userId = "+userId);

        if(status>0){
            if(status==3)//交易中
                //where.append(" and (status=3 or status=0 or status=-1)");//这种状态是都显示
                where.append(" and (status=3 or status=0 )");//这种状态是都显示 ，李福要求计划中不显示在待成交
            else if(status==2)//完成
                where.append(" and status=2");//仅仅显示这个状态的
            else if(status==1)//取消完成
                where.append(" and status=1");//仅仅显示这个状态的
        }else if(status<0){
            where.append(" and status=-1");//显示计划中的
        }
        //status==0 是所有的意思

        if(type>-1){
            where.append(" and types="+type);//类型
        }

        if(timeFrom>0)
            where.append(" and submitTime>="+timeFrom);//时间从
        if(timeTo>0)
            where.append(" and submitTime<="+timeTo);//时间到
        if(numberFrom>0)
            where.append(" and numbers>="+numberFrom);//时间从
        if(numberTo>0)
            where.append(" and numbers<="+numberTo);//时间到

        if(priceFrom>0)
            where.append(" and unitPrice>="+priceFrom);//时间到
        if(priceTo>0)
            where.append(" and unitPrice<="+priceTo);//时间到

        String w=where.toString();
        //  if(w.length()>0)
        //  	w=w.substring(4);
        //String sql="select SQL_CALC_FOUND_ROWS entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status from entrust where  userId=? "+w+"  and unitPrice>0 order by entrustId desc limit ?,?";

        String sql = "";

//	    	if(dateTo == 5){
//		    	//全部挂单历史记录--查询表名entrust_all
//		    	sql = getUnionSql(w,"entrust_all");
//		    }else{
//		    	//当前时间段挂单记录--查询表名entrust
//		    	sql = getOneSql(w, "entrust");
//		    }

        //只查最近委托，历史委托单独接口处理 add by buxianguan
        sql = getOneSql(w, "entrust");

        sql += " order by submitTime desc limit ?,?";

        //String countSql="select found_rows() as num";
        List lists = Data.Query(m.db,sql, new Object[]{(pageIndex-1)*pageSize , pageSize});//提现记录
        //List count = (List)Data.GetOne(countSql, new Object[]{});//提现记录数量

        log.info("...............sqls:" + sql + "," + pageIndex + "," + pageSize);

        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;
            sb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                    Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString()))),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+",'"+m.getMarket()+"']");

        }
        String rtn=sb.toString();



        String status2Rtn = "";

        //if(status == 3){
        //已成交列表
        //String status2sqlCount="select  count(1) as total from entrust where userId=? and status=2  and unitPrice>0  ";
        //count = (List)Data.GetOne(status2sqlCount, new Object[]{userId});//

        //long status2Total = Long.parseLong(count.get(0).toString());//未成交记录

        //String status2sql="select  entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status from entrust where userId=? and status=2  and unitPrice>0 order by entrustId desc limit ?,?";
        //List status2lists = Data.Query(status2sql, new Object[]{userId , 0 ,10});//未成交记录
        //StringBuilder status2sb=new StringBuilder();
//            for(Object b : status2lists){
//            	List beb = (List) b;
//            	status2sb.append(",['"+beb.get(0)+"',"+Market.formatMoney(Long.parseLong(beb.get(1).toString()))+","+
//            	Market.formatNumber(Long.parseLong(beb.get(2).toString()))+","+Market.formatNumber(Long.parseLong(beb.get(3).toString()))+","+
//            	Market.formatMoneyAndNumber(Long.parseLong(beb.get(4).toString()))+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+"]");
//            }
        //}


        if(status2Rtn.length() > 0){
            status2Rtn = "[" + status2Rtn.substring(1) +"]";
        }else{
            status2Rtn = "[]";
        }


        if(rtn.length()==0)
            return "\"count\":0,\"record\":[],\"hrecord\":" + status2Rtn;
        else
            return "\"count\":0,\"record\":["+rtn.substring(1)+"],\"hrecord\":" + status2Rtn;
    }

    /**
     * 获取用户指定类型的交易管理数据，直接读数据库，会union all表
     * @param webId 网站id 网站id（暂时都设置城8）
     * @param userId 用户id 用户id
     * @param pageIndex 页码从1开始
     * @param pageSize 页码大小 10
     * @param type 类型   0 卖出  1 买入  -1不限制
     * @param timeFrom //时间   System.currentTimeMillis()
     * @param timeTo
     * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
     * @param numberTo//数量查询
     * @param priceFrom 最低价格
     * @param priceTo 最高价格
     * @param pageSize 页码大小 最大200
     * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
     * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
     */
    public  String getTopNoCache(int webId,int userId,int pageIndex,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long pageSize,int status,int dateTo,Market m){

//        if(type==-1&&pageIndex==1&&timeFrom==0&&timeTo==0&&numberFrom==0&&numberTo==0&&priceFrom==0&&priceTo==0&&status==3&&pageSize==0){
//            //getTop(userId);
//            return getTopThread2016(userId,m);
//        }

        if(pageSize==0)
            pageSize=30;
        if(pageSize>200)
            pageSize=200;

        //and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
        StringBuilder where=new StringBuilder();
        where.append(" userId = "+userId);

        if(status>0){
            if(status==3)//交易中
                //where.append(" and (status=3 or status=0 or status=-1)");//这种状态是都显示
                where.append(" and (status=3 or status=0 )");//这种状态是都显示 ，李福要求计划中不显示在待成交
            else if(status==2)//完成
                where.append(" and status=2");//仅仅显示这个状态的
            else if(status==1)//取消完成
                where.append(" and status=1");//仅仅显示这个状态的
        }else if(status<0){
            where.append(" and status=-1");//显示计划中的
        }
        //status==0 是所有的意思

        if(type>-1){
            where.append(" and types="+type);//类型
        }

        if(timeFrom>0)
            where.append(" and submitTime>="+timeFrom);//时间从
        if(timeTo>0)
            where.append(" and submitTime<="+timeTo);//时间到
        if(numberFrom>0)
            where.append(" and numbers>="+numberFrom);//时间从
        if(numberTo>0)
            where.append(" and numbers<="+numberTo);//时间到

        if(priceFrom>0)
            where.append(" and unitPrice>="+priceFrom);//时间到
        if(priceTo>0)
            where.append(" and unitPrice<="+priceTo);//时间到

        String w=where.toString();

        String sql = "";
        //如果查询已完成状态委托（历史委托），关联entrust_all表 add by buxianguan
        if (status == 2) {
            sql = "(" + getOneSql(w, "entrust") + ") union all (" + getOneSql(w, "entrust_all") + ")";
        } else {
            //当前时间段挂单记录--查询表名entrust
            sql = getOneSql(w, "entrust");
        }

        sql += " order by submitTime desc limit ?,?";

        List lists = Data.Query(m.db,sql, new Object[]{(pageIndex-1)*pageSize , pageSize});//提现记录

        log.info("...............sqls:" + sql + "," + pageIndex + "," + pageSize);

        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;
            sb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                    Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString()))),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+"]");

        }
        String rtn=sb.toString();

        String status2Rtn = "";

        if(status2Rtn.length() > 0){
            status2Rtn = "[" + status2Rtn.substring(1) +"]";
        }else{
            status2Rtn = "[]";
        }

        if(rtn.length()==0)
            return "\"count\":0,\"record\":[],\"hrecord\":" + status2Rtn;
        else
            return "\"count\":0,\"record\":["+rtn.substring(1)+"],\"hrecord\":" + status2Rtn;
    }

    /**
     * 获取用户指定类型的交易管理数据，历史委托交易成功的 status=2
     * @param webId 网站id 网站id（暂时都设置城8）
     * @param userId 用户id 用户id
     * @param pageIndex 页码从1开始
     * @param pageSize 页码大小 10
     * @param type 类型   0 卖出  1 买入  -1不限制
     * @param timeFrom //时间   System.currentTimeMillis()
     * @param timeTo
     * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
     * @param numberTo//数量查询
     * @param priceFrom 最低价格
     * @param priceTo 最高价格
     * @param pageSize 页码大小 最大200
     * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
     * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
     */
    public  String getUserRecordHistory(int webId,int userId,int pageIndex,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long pageSize,int status,int dateTo,Market m){
        if(pageSize==0)
            pageSize=30;
        if(pageSize>200)
            pageSize=200;

        //and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
        StringBuilder where=new StringBuilder();
        where.append(" userId = "+userId);

        where.append(" and status=2");//查询成交的历史记录，取消不管

        if(type>-1){
            where.append(" and types="+type);//类型
        }

        if(timeFrom>0)
            where.append(" and submitTime>="+timeFrom);//时间从
        if(timeTo>0)
            where.append(" and submitTime<="+timeTo);//时间到
        if(numberFrom>0)
            where.append(" and numbers>="+numberFrom);//时间从
        if(numberTo>0)
            where.append(" and numbers<="+numberTo);//时间到

        if(priceFrom>0)
            where.append(" and unitPrice>="+priceFrom);//时间到
        if(priceTo>0)
            where.append(" and unitPrice<="+priceTo);//时间到

        String w=where.toString();

        //只查历史委托
        String sql = getOneSql(w, "entrust_all");

        sql += " order by submitTime desc limit ?,?";

        List lists = Data.Query(m.db,sql, new Object[]{(pageIndex-1)*pageSize , pageSize});//历史委托记录

        log.info("...............sqls:" + sql + "," + pageIndex + "," + pageSize);

        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;
            sb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                    Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString()))),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+"]");

        }
        String rtn=sb.toString();

        String status2Rtn = "";
        if(status2Rtn.length() > 0){
            status2Rtn = "[" + status2Rtn.substring(1) +"]";
        }else{
            status2Rtn = "[]";
        }

        if(rtn.length()==0)
            return "\"count\":0,\"record\":[],\"hrecord\":" + status2Rtn;
        else
            return "\"count\":0,\"record\":["+rtn.substring(1)+"],\"hrecord\":" + status2Rtn;
    }

/*	private String getOneSql(String w, String table){
		String sql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feeRate,0 as triggerPrice,webId from "+table+" where  "+w+"  and unitPrice>0 ";
		 sql+=" union all select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status ,feeRate,triggerPrice,webId from plan_entrust where  "+w+" and unitPrice>0 ";

		return sql;
	}
	private String getUnionSql(String w){
		String sql = DownTableManager.getProxySql("select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feeRate,0 as triggerPrice,webId from entrust where " + w + " and unitPrice>0", QueryDataType.DOWN);
		sql +=" union all ";
		sql += DownTableManager.getProxySql(" select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status ,feeRate,triggerPrice,webId from plan_entrust where " + w + "  and unitPrice>0", QueryDataType.DOWN);

		//String sql="select SQL_CALC_FOUND_ROWS * from (SELECT entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status from entrust where  "+w+"  and unitPrice>0 UNION SELECT entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status FROM entrust_all where "+w+" and unitPrice>0) as entrust ";
		return sql;
	}
	*/

    private String getOneSql(String w, String table){
        String sql="select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feeRate,0 as triggerPrice,webId from "+table+" where  "+w+"  and unitPrice>0 ";

        return sql;
    }
    private String getUnionSql(String w, String tableName){
        String sql = DownTableManager.getProxySql("select entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feeRate,0 as triggerPrice,webId from "+tableName+" where " + w + " and unitPrice>0", QueryDataType.DOWN);
        return sql;
    }
    private String getOneCountSql(String w, String table){
        String sql="select count(*) from "+table+" where  "+w+"  and unitPrice>0 ";

        return sql;
    }

    /**
     * 查询当前的计划委托数据
     * @param w
     * @param table
     * @return
     */
    private String getPlanOneSql(String w, String table){
        String sql="select  entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feerate,triggerPrice,webId,triggerPriceProfit,unitPriceProfit,totalMoney,formalEntrustId,'true' as planType  from "+table+" where  "+w+" ";
        return sql;
    }

    private String getCountPlanOneSql(String w, String table){
        String sql="select  count(*) as num from "+table+" where  "+w+" ";
        return sql;
    }

    /**
     * 查询历史的计划表委托数据
     * @param w
     * @return
     */
    private String getPlanUnionSql(String w,String tableName){
        String sql = DownTableManager.getProxySql("select  entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status,feerate,triggerPrice,webId,triggerPriceProfit,unitPriceProfit,totalMoney,formalEntrustId,'true' as planType  from "+tableName+" where " + w , QueryDataType.DOWN);
        return sql;
    }

    private String getCountPlanUnionSql(String w,String tableName){
        String sql = DownTableManager.getProxySql("select  count(*) as num  from "+tableName+" where " + w , QueryDataType.DOWN);
        return sql;
    }

    /**
     *
     * @param webId
     * @param userId
     * @param pageIndex
     * @param type
     * @param timeFrom
     * @param timeTo
     * @param numberFrom
     * @param numberTo
     * @param priceFrom
     * @param priceTo
     * @param pageSize
     * @param status
     * @param dateTo
     * @return
     */
    public  String getTopPlan(int webId,int userId,int pageIndex,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,long pageSize,int status,int dateTo,Market m){



        if(pageSize==0)
            pageSize=30;
        if(pageSize>200)
            pageSize=200;

        StringBuilder where=new StringBuilder();
        where.append(" userId = "+userId);

        if(status>0){
            if(status==2)//完成
                where.append(" and status=2");//仅仅显示这个状态的
            else if(status==1)//取消完成
                where.append(" and status=1");//仅仅显示这个状态的
        }else if(status<0){
            where.append(" and status=-1");//显示计划中的
        }
        //status==0 是所有的意思

        if(type>-1){
            where.append(" and types="+type);//类型
        }

        if(timeFrom>0)
            where.append(" and submitTime>="+timeFrom);//时间从
        if(timeTo>0)
            where.append(" and submitTime<="+timeTo);//时间到
        if(numberFrom>0)
            where.append(" and numbers>="+numberFrom);//时间从
        if(numberTo>0)
            where.append(" and numbers<="+numberTo);//时间到

        if(priceFrom>0)
            where.append(" and unitPrice>="+priceFrom);//时间到
        if(priceTo>0)
            where.append(" and unitPrice<="+priceTo);//时间到

        String w=where.toString();
        String sql = "";
        String countSql = "";

        if(dateTo == 5){
            //全部挂单历史记录--查询表名plan_entrust_all
            sql = getPlanUnionSql(w,"plan_entrust_all");
            countSql = getCountPlanOneSql(w, "plan_entrust_all");
        }else{
            //当前时间段挂单记录--查询表名plan_entrust
            sql = getPlanOneSql(w, "plan_entrust");
            countSql = getCountPlanOneSql(w, "plan_entrust");
        }


        sql += " order by entrustId desc limit ?,?";

        List planlists = Data.Query(m.db,sql, new Object[]{(pageIndex-1)*pageSize , pageSize});//提现记录
        List count = (List)Data.GetOne(m.db,countSql, new Object[]{});//数量

        log.info("...............sqls:" + sql + "," + pageIndex + "," + pageSize);

        StringBuilder plansb=new StringBuilder();

        if(planlists!=null && !planlists.isEmpty()){
            for(Object b : planlists){
                List beb = (List) b;

                plansb.append(",['"+beb.get(0)+"',"+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                        Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())),m)+","+
                        Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString())),m)+","+beb.get(5)+","+beb.get(6)+","+beb.get(7)+","+beb.get(8)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())),m)+","+beb.get(10)+","+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(11).toString())),m)+","+
                        Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(12).toString())),m)+","+Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(13).toString())),m)+",'"+beb.get(14)+"','"+beb.get(15)+"','"+m.getMarket()+"']");
            }
        }
        String rtn=plansb.toString();
        String status2Rtn = "";
        if(status2Rtn.length() > 0){
            status2Rtn = "[" + status2Rtn.substring(1) +"]";
        }else{
            status2Rtn = "[]";
        }

        if(rtn.length()==0)
            return "\"count\":0,\"record\":[],\"hrecord\":" + status2Rtn;
        else
            return "\"count\":"+count.get(0).toString()+",\"record\":["+rtn.substring(1)+"],\"hrecord\":" + status2Rtn;
    }

    /**
     * 获取用户指定类型的交易管理数据
     * @param webId 网站id 网站id（暂时都设置城8）
     * @param userId 用户id 用户id
     * @param type 类型   0 卖出  1 买入  -1不限制
     * @param timeFrom //时间   System.currentTimeMillis()
     * @param timeTo
     * @param numberFrom//数量查询，数量等于用户提交的数量*Market.numberBixNormal    提交过来
     * @param numberTo//数量查询
     * @param priceFrom 最低价格
     * @param priceTo 最高价格
    //	 * @param pagesize 页码大小 最大200
     * @param status 订单状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）
     * @return 返回的是json数据，格式为 count：总数量  record数组代表结果集合entrustId,unitPrice,numbers,completeNumber,completeTotalMoney,types,submitTime,status
     */
    public String getStatisticsRecord(int webId,int userId,int type,long timeFrom,long timeTo,long numberFrom,long numberTo,long priceFrom,long priceTo,int status,Market m){

        //and ((status <> 1) OR (status = 1 AND transBtc > 0))  原来的去掉了这些
        StringBuilder where=new StringBuilder();
        if(status>0){
            if(status==3)//交易中
                where.append(" and (status=3 or status=0 or status=-1)");//这种状态是都显示
            else if(status==2)//完成
                where.append(" and status=2");//仅仅显示这个状态的
            else if(status==1)//取消完成
                where.append(" and status=1");//仅仅显示这个状态的
        }else if(status<0){
            where.append(" and status=-1");//显示计划中的
        }
        //status==0 是所有的意思

        if(type>-1){
            where.append(" and types="+type);//类型
        }

        if(timeFrom>0)
            where.append(" and submitTime>="+timeFrom);//时间从
        if(timeTo>0)
            where.append(" and submitTime<="+timeTo);//时间到
        if(numberFrom>0)
            where.append(" and numbers>="+numberFrom);//时间从
        if(numberTo>0)
            where.append(" and numbers<="+numberTo);//时间到

        if(priceFrom>0)
            where.append(" and unitPrice>="+priceFrom);//时间到
        if(priceTo>0)
            where.append(" and unitPrice<="+priceTo);//时间到

        String w=where.toString();
        //  if(w.length()>0)
        //  	w=w.substring(4);
        String sql="select SUM(numbers),SUM(completeNumber),SUM(completeTotalMoney),types,status from entrust where  userId=? "+w+"  and unitPrice>0 group by types,status";
        List lists = Data.Query(m.db,sql, new Object[]{userId });//提现记录
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;
            sb.append(",["+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(0).toString())),m)+","+Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","+
                    Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString()))),m)+","+beb.get(3)+","+beb.get(4)+"]");
        }
        String rtn=sb.toString();
        if(rtn.length()==0)
            return "\"record\":[]";
        else
            return "\"record\":["+rtn.substring(1)+"]";
    }

    public String getTraderecord(int userId, int pageNo, int pageSize,Market m){
        return getTraderecord(userId, pageNo, pageSize, 0,m);
    }

    public String getTraderecord(int userId, int pageNo, int pageSize, int dateTo,Market m){
        String sql = getOneTradeSql(userId, "transrecord");
        if(dateTo == 5){
            sql = getUnionTradeSql(userId);
        }

        sql += " order by times desc limit ?, ?";
        String countSql="select found_rows() as num";
        List lists = Data.Query(m.db,sql, new Object[]{(pageNo-1)*pageSize , pageSize});
        List count = (List)Data.GetOne(m.db,countSql, new Object[]{});//提现记录数量
        StringBuilder sb=new StringBuilder();
        for(Object b : lists){
            List beb = (List) b;
            int type = 1;
            if(userId == Integer.parseInt(beb.get(5).toString())){
                type = 1;
            }else if(userId == Integer.parseInt(beb.get(6).toString())){
                type = 0;
            }
            sb.append(",["+Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(0).toString())),m)+","+Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m)+","
                    +Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m)+","+type+","+beb.get(4)+"]");
        }
        String rtn=sb.toString();
        if(rtn.length()==0)
            return "\"count\":0,\"record\":[]";
        else
            return "\"count\":"+count.get(0).toString()+",\"record\":["+rtn.substring(1)+"]";
    }

    private String getOneTradeSql(int userId, String table){
        String sql="select SQL_CALC_FOUND_ROWS unitPrice, totalPrice, numbers, types, times,userIdBuy,userIdSell from "+table+" where (userIdBuy="+userId+" or userIdSell="+userId+") and unitPrice>0 ";
        return sql;
    }
    private String getUnionTradeSql(int userId){
        String sql = DownTableManager.getProxySql("select SQL_CALC_FOUND_ROWS unitPrice, totalPrice, numbers, types, times,userIdBuy,userIdSell from transrecord where  (userIdBuy="+userId+" or userIdSell="+userId+") and unitPrice>0", QueryDataType.DOWN);

        //String sql="select SQL_CALC_FOUND_ROWS * from (SELECT unitPrice, totalPrice, numbers, types, times,userIdBuy,userIdSell from transrecord where  (userIdBuy="+userId+" or userIdSell="+userId+")  and unitPrice>0 UNION SELECT unitPrice, totalPrice, numbers, types, times,userIdBuy,userIdSell from transrecord_all where (userIdBuy="+userId+" or userIdSell="+userId+") and unitPrice>0) as transrecord ";
        return sql;
    }

    /**
     * 通过加载本地文件的形式加载
     * @param isLoad
     */
    public DataArray(boolean isLoad){

    }

    /**
     * 获取买一
     * @return
     */
    public BigDecimal getBuyOne() {

        return buyOne;
    }

    public void setBuyOne(BigDecimal buyOne) {
        this.buyOne = buyOne;
    }

    /**
     * 获取卖一
     * @return
     */
    public BigDecimal getSellOne() {

        return sellOne;

    }

    public void setSellOne(BigDecimal sellOneIndex) {
        this.sellOne = sellOneIndex;
    }

    public int getMaxPrice() {
        return maxPrice;
    }
    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //指定用户设置前10条成交记录到mem
    public void setTraderecordToMem(int userId, TransRecordBean r , boolean isMe,Market m){
        String key = trade_record_key + m.market + userId;
        //最后一下列标是总数
        Object[][] array = (Object[][]) Cache.GetObj(key);
        if(array == null || array.length <= 0){
            array = getArray(userId,m);
        }else{

            if(r != null){
                for (int i = 9;i > 0;i--){
                    array[i] = (Object[]) WebUtil.deepCopyObj(array[i-1]);
                }
                //最新数据插入到数组中，放在第一条
                array[0][0] = Market.formatMoney(r.getUnitPrice(),m);
                array[0][1] = Market.formatMoneyAndNumber(r.getTotalPrice(),m);
                array[0][2] = Market.formatNumber(r.getNumbers(),m);
                if(userId == r.getUserIdBuy()){
                    array[0][3] = 1;
                }else if(userId == r.getUserIdSell()){
                    array[0][3] = 0;
                }
                array[0][4] = r.getTimes();
                if(isMe){
                    array[0][3] = 0;
                }else{
                    long count = (Long) array[10][0];
                    array[10][0] = count + 1;
                }
            }
        }

        Cache.SetObj(key, array, 2*60*60);
    }


    public Object[][] getArray(int userId,Market m){
        String sql = "select SQL_CALC_FOUND_ROWS unitPrice, totalPrice, numbers, types, times,userIdBuy,userIdSell from transrecord where (userIdBuy=? or userIdSell=?) and unitPrice > 0  order by times desc limit ?, ?";
        String countSql="select found_rows() as num";
        List lists = Data.Query(m.db,sql, new Object[]{userId, userId, 0 , 10});
        List count = (List)Data.GetOne(m.db,countSql, new Object[]{});//提现记录数量
        long num = 0;
        if(count != null && count.size() > 0){
            try {
                num = Long.parseLong(count.get(0).toString());
            } catch (NumberFormatException e) {
                log.error("类型转换异常", e);
            }
        }
        Object [][] array = new Object[11][5];
        int i = 0;
        int jia = 0;
        for(Object b : lists){
            if(i >= 10){
                break;
            }
            List beb = (List) b;
            int res = setArray(userId, beb, array, i,m);
            if(res > i){
                i = res;
                num++;
                jia ++;
            }
            i++;
        }

        if(num > 10){
            num = num - jia;
        }

        array[10][0] = num;
        //
        // Cache.SetObj(key, array, 2*60*60);
        return array;
    }


    public int setArray(int userId , List beb , Object [][] array , int i,Market m){
        int buyId = Integer.parseInt(beb.get(5).toString());
        int sellId = Integer.parseInt(beb.get(6).toString());
        array[i][0] = Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(0).toString())),m);
        array[i][1] = Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m);
        array[i][2] = Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m);
        array[i][3] = beb.get(3);
        array[i][4] = beb.get(4);
        if(userId == buyId){
            array[i][3] = 1;
        }else{
            array[i][3] = 0;
        }
        if(buyId == sellId){
            i = i +1;
            array[i][0] = Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(0).toString())),m);
            array[i][1] = Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())),m);
            array[i][2] = Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())),m);
            array[i][4] = beb.get(4);
            array[i][3] = 0;
        }
        return i;
    }

    public String getTraderecordFromMem(int userId,Market m){
        String key = trade_record_key + m.market + userId;
        Object [][] array = (Object[][]) Cache.GetObj(key);
        if(array == null || array.length <= 0){
            array = getArray(userId,m);
            Cache.SetObj(key, array, 2*60*60);
        }

        if(array.length == 0)
            return "\"count\":0,\"record\":[]";
        else{
            StringBuilder sb = new StringBuilder();
            int i = 0;
            long count = (Long)array[array.length-1][0];
            for (Object[] objects : array) {
                if(i == count || objects[1] == null) break;
                sb.append(",["+objects[0]+","+objects[1]+","
                        +objects[2]+","+objects[3]+","+objects[4]+"]");
                i++;
            }

            String res = sb.toString();
            if(res.length() > 0){
                res = res.substring(1);
            }
            return "\"count\":"+array[array.length-1][0]+",\"record\":["+res+"]";
        }
    }

    public DataArray(){}

    /**
     * 获取用户历史委托（已成交和已取消）
     */
    public String getUserEntrustHistory(int userId, int type, int includeCancel, int pageNum, int pageSize, Market m, String table) {
        //首次进入页面，走缓存
        if (pageNum == 1 && includeCancel == 0 && type == -1 && "entrust".equals(table)) {
            String cacheData = RedisUtil.get(m.market + "_userrecord_status2_" + userId);
            if (cacheData != null && cacheData.length() > 0) {
                //获取数量
                String where = " userId=" + userId + " and status=2 ";
                String countSql = getOneCountSql(where, table);
                List count = (List) Data.GetOne(m.db, countSql, new Object[]{});
                if (CollectionUtils.isEmpty(count) || "0".equals(count.get(0).toString())) {
                    return "\"count\":0,\"record\":[]";
                } else {
                    return "\"count\":" + count.get(0).toString() + "," + cacheData;
                }
            }
        }

        StringBuilder where = new StringBuilder();
        where.append(" userId=" + userId);
        if (type > -1) {
            where.append(" and types=" + type);
        }
        if (includeCancel == 0) {
            where.append(" and status=2");
        } else {
            where.append(" and status between 1 and 2");
        }
        String w = where.toString();

        String countSql = getOneCountSql(w, table);
        List count = (List) Data.GetOne(m.db, countSql, new Object[]{});
        if (CollectionUtils.isEmpty(count) || "0".equals(count.get(0).toString())) {
            return "\"count\":0,\"record\":[]";
        }

        String sql = getOneSql(w, table);
        sql += " order by submitTime desc limit ?,?";
        List lists = Data.Query(m.db, sql, new Object[]{(pageNum - 1) * pageSize, pageSize});
        StringBuilder sb = new StringBuilder();
        for (Object b : lists) {
            List beb = (List) b;
            sb.append(",['" + beb.get(0) + "'," + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())), m) + "," +
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())), m) + "," + Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())), m) + "," +
                    Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString()))), m) + "," + beb.get(5) + "," + beb.get(6) + "," + beb.get(7) + "," + beb.get(8) + "," + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())), m) + "," + beb.get(10) +",'"+m.getMarket()+"']");

        }
        String rtn = sb.toString();

        if (rtn.length() > 0) {
            rtn = "[" + rtn.substring(1) + "]";
        } else {
            rtn = "[]";
        }

        if (rtn.length() == 0) {
            return "\"count\":" + count.get(0).toString() + ",\"record\":[]";
        } else {
            return "\"count\":" + count.get(0).toString() + ",\"record\":" + rtn;
        }
    }


    /**
     * 获取用户历史委托（已成交和已取消）仅包含24成交量，不统计总数
     */
    public String getUserEntrustHistoryFor24(int userId, int type, int includeCancel, int pageNum, int pageSize, Market m, String table) {
        //首次进入页面，走缓存
        if (pageNum == 1 && includeCancel == 0 && type == -1 &&pageSize <= 30) {
            String cacheData = RedisUtil.get(m.market + "_userrecord_status2_" + userId);
            if (cacheData != null && cacheData.length() > 0) {
                return cacheData;
            }
        }

        StringBuilder where = new StringBuilder();
        where.append(" userId=" + userId);
        if (type > -1) {
            where.append(" and types=" + type);
        }
        if (includeCancel == 0) {
            where.append(" and status=2");
        } else {
            where.append(" and status between 1 and 2");
        }
        String w = where.toString();

        String sql = getOneSql(w, table);
        sql += " order by submitTime desc limit ?,?";
        List lists = Data.Query(m.db, sql, new Object[]{(pageNum - 1) * pageSize, pageSize});
        StringBuilder sb = new StringBuilder();
        for (Object b : lists) {
            List beb = (List) b;
            sb.append(",['" + beb.get(0) + "'," + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())), m) + "," +
                    Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())), m) + "," + Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())), m) + "," +
                    Market.formatMoneyAndNumber((BigDecimal.valueOf(Double.parseDouble(beb.get(4).toString()))), m) + "," + beb.get(5) + "," + beb.get(6) + "," + beb.get(7) + "," + beb.get(8) + "," + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())), m) + "," + beb.get(10) + ",'"+m.getMarket()+"']");

        }
        String rtn = sb.toString();
        if (rtn.length() > 0) {
            rtn = "[" + rtn.substring(1) + "]";
        } else {
            rtn = "[]";
        }

        if (rtn.length() == 0) {
            return "\"record\":[]";
        } else {
            return "\"record\":" + rtn;
        }
    }

    /**
     * 获取用户成交记录
     */
    public String getUserTransRecordHistory(int userId, int type, int pageNum, int pageSize, Market m, String table) {
        StringBuilder where = new StringBuilder();
        if (type == 0) {
            where.append(" userIdSell=" + userId);
        } else if (type == 1) {
            where.append(" userIdBuy=" + userId);
        } else {
            where.append(" (userIdBuy=" + userId + " or userIdSell=" + userId + ")");
        }
        String w = where.toString();

        String countSql = "SELECT count(1) FROM " + table + " WHERE " + w + " and unitPrice>0";
        List count = (List) Data.GetOne(m.db, countSql, new Object[]{});
        if (CollectionUtils.isEmpty(count) || "0".equals(count.get(0).toString())) {
            return "\"count\":0,\"record\":[]";
        }

        String sql = "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times,userIdBuy,userIdSell,feesBuy,feesSell FROM " + table +
                " WHERE " + w + " and unitPrice>0 order by times desc limit ?,?";
        List lists = Data.Query(m.db, sql, new Object[]{(pageNum - 1) * pageSize, pageSize});
        StringBuilder sb = new StringBuilder();
        for (Object b : lists) {
            List beb = (List) b;
            sb.append(",[" + beb.get(0) + ","
                    + Market.formatMoney(BigDecimal.valueOf(Double.parseDouble(beb.get(1).toString())), m) + ","
                    + Market.formatMoneyAndNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(2).toString())), m) + ","
                    + Market.formatNumber(BigDecimal.valueOf(Double.parseDouble(beb.get(3).toString())), m) + ","
                    + beb.get(4) + ","
                    + beb.get(5) + ","
                    + beb.get(6) + ","
                    + beb.get(7) + ","
                    + BigDecimal.valueOf(Double.parseDouble(beb.get(8).toString())) + ","
                    + BigDecimal.valueOf(Double.parseDouble(beb.get(9).toString())) + ",'"
                    + m.getMarket()+"']");

        }
        String rtn = sb.toString();

        if (rtn.length() > 0) {
            rtn = "[" + rtn.substring(1) + "]";
        } else {
            rtn = "[]";
        }

        if (rtn.length() == 0) {
            return "\"count\":" + count.get(0).toString() + ",\"record\":[]";
        } else {
            return "\"count\":" + count.get(0).toString() + ",\"record\":" + rtn;
        }
    }

    public static void main(String[] args) {
//		String msg = "[['2015052788049090',1660,0.02,0,0,1,1432714640191,3],['2015052788049047',1650,0.02,0,0,1,1432714623655,3],['2015052788048994',1600,0.02,0,0,1,1432714611362,3],['2015052788047651',1800,0.02,0,0,0,1432714146721,3],['2015052788047502',1800,0.02,0,0,0,1432714104060,3]]";
//		JSONArray array = JSONArray.fromObject(msg);
//		log.info(array.size());

//		DataArray arr = new DataArray();
        //log.info(arr.getOrderByEntrustId(74122, 2015052788049047l));

        String sql = "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord WHERE ((userIdBuy=? and entrustIdBuy=?) or (userIdSell=? AND entrustIdSell=?)) and unitPrice>0";

        MysqlDownTable.DownSql downSql = DownTableManager.getProxyDownSql(sql, QueryDataType.DOWN);

        System.out.println(downSql.dealedSql);
    }
}
