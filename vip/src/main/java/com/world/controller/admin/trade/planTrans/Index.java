package com.world.controller.admin.trade.planTrans;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.world.data.mysql.Query;
import com.world.model.dao.TransRecordDao2;
import com.world.model.dao.trace.EntrustDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.Market;
import com.world.model.entity.TransRecord2;
import com.world.model.entity.trace.PlanEntrust;
import com.world.model.entity.user.User;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@FunctionAction(jspPath = "/admins/trade/planTrace/", des = "计划委托追踪")
public class Index extends FinanAction {
    UserDao uDao = new UserDao();
    EntrustDao entrustDao = new EntrustDao();
    TransRecordDao2 transRecordDao = new TransRecordDao2();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        String currentTab = param("tab");
        if (StringUtils.isBlank(currentTab))
            currentTab =  Market.getDefMarketName();;

        setAttr("tab", currentTab);

        JSONObject marketJson = Market.getMarketByName(currentTab);
        if(marketJson==null){
            log.error("获取不到盘口信息，请检查参数tab"+currentTab);
            return;
        }

        String dbName = marketJson.getString("db");//数据库名称
        entrustDao.setDatabase(dbName);

        String strEntrustId = param("entrustId");//查询委托ID
//        String userName = param("userName");//按用户名查询
        String userTrueId = param("userId");//按用户名ID查询
        String type=param("type");
        int status=intParam("status");
        if(type.equals("")){
            type="2";
        }
        long userId=0L;
        long entrustId = 0;
        try{
            if(StringUtils.isNotBlank(strEntrustId)){
                entrustId = Long.parseLong(strEntrustId);
            }
        }catch(Exception e){
            entrustId = 0;
        }
       /* if(StringUtils.isNotBlank(userName)){
            User user =  uDao.getUserByColumn(userName,"userName");
            if(user!=null){
                userId = Long.parseLong(user.getId());
            }
        }*/
        if(StringUtils.isNotBlank(userTrueId)){
            userId = Long.parseLong(userTrueId);
        }
        String timeFrom=param("timeFrom");
        String timeTo=param("timeTo");
        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd");

        long numberFrom=longParam("numberFrom");
        long numberTo=longParam("numberTo");
        long priceFrom=longParam("priceFrom");
        long priceTo=longParam("priceTo");
        Long TimeFrom=0l;
        Long TimeTo=0l;
        if(!timeFrom.equals("")){
            try {
                TimeFrom=format.parse(timeFrom).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(!timeTo.equals("")){
            try {
                TimeTo=format.parse(timeTo).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //
        int currentPage = intParam("page");
        int pageSize = intParam("pageSize")==0?10:intParam("pageSize");
        Map<String,Object> dataMap = entrustDao.getPlanEntrustList(entrustId,Integer.parseInt(type),TimeFrom,TimeTo,numberFrom,numberTo,priceFrom,priceTo,userId,currentPage,pageSize,dbName,status);

        @SuppressWarnings("unchecked")
        List<PlanEntrust> dataist = (List<PlanEntrust>)dataMap.get("dataList");

        int total = (int)dataMap.get("total");



        Set<String> userIds = new HashSet<String>();
        for (PlanEntrust entrust: dataist) {
            userIds.add(String.valueOf(entrust.getUserId()));

        }


        if (userIds.size() > 0) {
            Map<String, User> userMaps = uDao.getUserMapByIds(Lists.newArrayList(userIds.iterator()));

            for (PlanEntrust entrust: dataist) {
                User user = userMaps.get(String.valueOf(entrust.getUserId()));
                if(user != null){
                    entrust.setUserName(user.getUserName());
                }
            }

        }
        setPaging(total, currentPage , pageSize);

        //盘口集合
        setAttr("markets",Market.getMarketsMap());

        setAttr("dataList", dataist);

    }


    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }


    @Page(Viewer = DEFAULT_AORU)
    public void aoru() {
        try {

            String currentTab = param("tab");
            if (StringUtils.isBlank(currentTab))
                currentTab =  Market.getDefMarketName();;

            setAttr("tab", currentTab);

            JSONObject marketJson = Market.getMarketByName(currentTab);
            if(marketJson==null){
                log.error("获取不到盘口信息，请检查参数tab"+currentTab);
                return;
            }

            String dbName = marketJson.getString("db");//数据库名称
            transRecordDao.setDatabase(dbName);

            String id = param("id");
            if (id.length() > 0) {
                Query<TransRecord2> query2 = transRecordDao.getQuery();
                //20170826 xzhang 修改报表分表数据展示
                query2.setSql("select * from (select * from transrecord where unitPrice>0 and (entrustIdBuy="+id+" or entrustIdSell="+id+")  and status>1 union all select * from transrecord_all where unitPrice>0 and (entrustIdBuy="+id+" or entrustIdSell="+id+")  and status>1 ) fa");
                query2.setCls(TransRecord2.class);
                List<TransRecord2> tradeList = transRecordDao.find();
                Set<String> userIds = new HashSet<String>();
                for (TransRecord2 tr : tradeList) {
                    userIds.add(String.valueOf(tr.getUserIdBuy()));
                    userIds.add(String.valueOf(tr.getUserIdSell()));
                }

                if (userIds.size() > 0) {
                    Map<String, User> userMaps = uDao.getUserMapByIds(Lists.newArrayList(userIds.iterator()));
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
                setAttr("tradeList", tradeList);
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }




}
