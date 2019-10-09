package com.world.controller.admin.trade.stat;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.world.data.mysql.Query;
import com.world.model.dao.GuadanDao;
import com.world.model.dao.TransRecordDao2;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.Guadan;
import com.world.model.entity.Market;
import com.world.model.entity.TransRecord2;
import com.world.model.entity.user.User;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@FunctionAction(jspPath = "/admins/trade/stat/", des = "合并盘口交易")
public class Merge extends FinanAction {
    UserDao uDao = new UserDao();
    GuadanDao guadanDao = new GuadanDao();
    TransRecordDao2 transRecordDao = new TransRecordDao2();
    CommAttrDao commAttrDao = new CommAttrDao();
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        String currentTab = param("tab");
        if (StringUtils.isBlank(currentTab))
            currentTab =  Market.getDefMarketName();;

        setAttr("tab", currentTab);

        setAttr("merge", "/Merge");
        JSONObject marketJson = Market.getMarketByName(currentTab);
        if(marketJson==null){
        	log.error("获取不到盘口信息，请检查参数tab"+currentTab);
        	return;
        }
        
        String dbName = marketJson.getString("db");//数据库名称
        guadanDao.setDatabase(dbName);
        transRecordDao.setDatabase(dbName);

//        Query<Guadan> query1 = guadanDao.getQuery();
//        query1.setSql("select unitPrice/100 avgPrice,(numbers-completeNumber)/100000000.0 numbers,userId,types isBuy " +
//        		"from entrust where types=1 and status=3 and (numbers-completeNumber)>0  ORDER BY unitPrice DESC LIMIT 100");
//        query1.setCls(Guadan.class);
//        List<Guadan> buyList = guadanDao.find();
//        
//        query1.setSql("select unitPrice/100 avgPrice,(numbers-completeNumber)/100000000.0 numbers,userId,types isBuy " +
//        		"from entrust where types=0 and status=3 and (numbers-completeNumber)>0  ORDER BY unitPrice asc LIMIT 100");
//        List<Guadan> sellList = guadanDao.find();
        
        Query<Guadan> query1 = guadanDao.getQuery();
        query1.setSql("select * from (select sum(totalMoney)/sum(numbers) as avgPrice, sum(numbers-completeNumber) as numbers ,userId,types isBuy" +
        		" from entrust where types=1"
        		+ " and status=3 GROUP BY userId ) x where numbers > 0 order by avgPrice DESC limit 100");
        query1.setCls(Guadan.class);
        List<Guadan> buyList = guadanDao.find();

        query1.setSql("select * from (select sum(totalMoney)/sum(numbers) as avgPrice, sum(numbers-completeNumber) as numbers ,userId,types isBuy" +
                " from entrust where types=0"
                + " and status=3 GROUP BY userId ) x where numbers > 0 order by avgPrice ASC limit 100");
        List<Guadan> sellList = guadanDao.find();

        Query<TransRecord2> query2 = transRecordDao.getQuery();
        query2.setSql("select * from transrecord where unitPrice>0 and unitPrice>0 and status>1 order by times desc limit 100");
        query2.setCls(TransRecord2.class);
        List<TransRecord2> tradeList = transRecordDao.find();

        //add by xwz 20170806查询账户类型
        Map<String, String> userTypeMap = commAttrDao.queryUserTypeMap();

        Set<String> userIds = new HashSet<String>();
        for (Guadan guadan: buyList) {
            userIds.add(String.valueOf(guadan.getUserId()));
        }
        for (Guadan guadan: sellList) {
            userIds.add(String.valueOf(guadan.getUserId()));
        }
        for (TransRecord2 tr : tradeList) {
            userIds.add(String.valueOf(tr.getUserIdBuy()));
            userIds.add(String.valueOf(tr.getUserIdSell()));
        }

        if (userIds.size() > 0) {
            Map<String, User> userMaps = uDao.getUserMapByIds(Lists.newArrayList(userIds.iterator()));

            for (Guadan guadan: buyList) {
                User user = userMaps.get(String.valueOf(guadan.getUserId()));
                if(user != null){
                    guadan.setUserName(user.getUserName());
                }
                /*add by xwz 20180806*/
                if(userTypeMap.containsKey(guadan.getUserId() + "")){
                    guadan.setUserType(userTypeMap.get(guadan.getUserId() + ""));//设置用户类型
                }
                /*end*/
            }
            for (Guadan guadan: sellList) {
                User user = userMaps.get(String.valueOf(guadan.getUserId()));
                if(user != null){
                    guadan.setUserName(user.getUserName());
                }
                /*add by xwz 20180806*/
                if(userTypeMap.containsKey(guadan.getUserId() + "")){
                    guadan.setUserType(userTypeMap.get(guadan.getUserId() + ""));//设置用户类型
                }
                /*end*/
            }

            for (TransRecord2 tr : tradeList) {
                User user = userMaps.get(String.valueOf(tr.getUserIdBuy()));
                if (user != null) {
                    tr.setUserNameBuy(user.getUserName());
                }

                user = userMaps.get(String.valueOf(tr.getUserIdSell()));
                if (user != null) {
                    tr.setUserNameSell(user.getUserName());
                }
            }
        }

        //盘口集合
        setAttr("markets",Market.getMarketsMap());
        
        setAttr("buyList", buyList);
        setAttr("sellList", sellList);
        setAttr("tradeList", tradeList);
    }


    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

}
