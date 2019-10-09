//package com.world.model.backcapital.worker;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.world.cache.Cache;
//import com.world.data.mysql.Bean;
//import com.world.data.mysql.Data;
//import com.world.model.dao.task.Worker;
//import com.world.model.entity.Market;
//import com.world.model.entity.record.TransRecord;
//import com.world.model.entity.usercap.dao.CommAttrDao;
//import com.world.model.entity.usercap.entity.CommAttrBean;
//import org.apache.log4j.Logger;
//
//import java.math.BigDecimal;
//import java.util.List;
//
///**
// * 回购记录同步到缓存定时任务
// * Created by buxianguan on 17/8/18.
// */
//public class BackCapitalRecordCacheWorker extends Worker {
//    private final static Logger log = Logger.getLogger(BackCapitalRecordCacheWorker.class);
//
//    private CommAttrDao commAttrDao = new CommAttrDao();
//
//    private volatile boolean running = false;
//
//    private final static String BACK_CAPITAL_RECORD_CACHE_KEY = "BACK_CAPITAL_RECORD";
//
//    public BackCapitalRecordCacheWorker(String name, String des) {
//        super(name, des);
//    }
//
//    @Override
//    public void run() {
//        try {
//            super.run();
//
//            log.info("回购记录同步到缓存定时任务开始...");
//
//            if (running) {
//                log.info(super.getName() + "-上一个任务还没有执行完毕,等待下一个轮询");
//                return;
//            }
//            running = true;
//
//            //获取回购账户userId
//            CommAttrBean commAttrBean = commAttrDao.queryByAttrTypeAndParaCode(10000002, "01");
//            if (null == commAttrBean) {
//                log.error("回购账户为null！");
//                return;
//            }
//            String userId = commAttrBean.getParaValue();
//
//            JSONObject market = Market.getMarketByName("gbc_btc");
//            if (null == market) {
//                log.error("找不到对应的gbc_btc市场!");
//                return;
//            }
//
//            String dbName = market.getString("db");
//
//            JSONArray recordArray = new JSONArray();
//
//            //从数据库里读取最新的30条记录
//            String sql = "SELECT transRecordId,unitPrice,numbers,types,times FROM transrecord WHERE userIdBuy=? and userIdSell>0 and unitPrice>0 ORDER BY transRecordId desc limit 0,30";
//            List<Bean> transrecords = Data.Query(dbName, sql, new Object[]{userId}, TransRecord.class);
//            for (int i = transrecords.size() - 1; i >= 0; i--) {
//                TransRecord tr = (TransRecord) transrecords.get(i);
//                recordArray.add(getObjFromTransRecord(tr, market));
//            }
//
//            //同步到缓存中
//            Cache.Set(BACK_CAPITAL_RECORD_CACHE_KEY, recordArray.toJSONString(), 10); //TODO 缓存时间
//
//        } catch (Exception e) {
//            log.error(e, e);
//        } finally {
//            running = false;
//        }
//    }
//
//
//    private JSONObject getObjFromTransRecord(TransRecord tr, JSONObject m) {
//        JSONObject jo = new JSONObject();
//        jo.put("date", tr.getTimes() / 1000);
//        jo.put("price", formatMoney(tr.getUnitPrice(), m));
//        jo.put("amount", formatNumber(tr.getNumbers(), m));
//        jo.put("tid", tr.getTransRecordId());
//        jo.put("type", tr.getTypes() == 1 ? "buy" : "sell");
//        jo.put("trade_type", tr.getTypes() == 1 ? "bid" : "ask");
//        return jo;
//    }
//
//
//    //格式化金钱 3.456 变成3.45
//    private double formatMoney(BigDecimal num, JSONObject m) {
//        double numNew = num.setScale(m.getIntValue("exchangeBixDian"), BigDecimal.ROUND_DOWN).doubleValue();
//        return numNew;
//    }
//
//    //双重格式化，保留应有的小数点位数，避免麻烦
//    private double formatNumber(BigDecimal num, JSONObject m) {
//        double numNew = num.setScale(m.getIntValue("numberBixDian"), BigDecimal.ROUND_DOWN).doubleValue();
//        return numNew;
//    }
//
//    public static void main(String[] args) {
//        BackCapitalRecordCacheWorker worker = new BackCapitalRecordCacheWorker("BackCapitalRecordCacheWorker", "回购记录同步到缓存定时任务");
//        worker.run();
//    }
//
//}
