package com.world.model.dao.vote;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.Query;
import com.world.model.entity.vote.Coin;
import com.world.model.entity.vote.Count;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CountDao extends DataDaoSupport<Count> {
    Logger logger = Logger.getLogger(CountDao.class);


    public List<Count> getList(String activityId, String userId,int limitCount){
        List<Count> countList = new ArrayList<>();
        try {
            String id = "";
            if(limitCount == 2){
                id = new SimpleDateFormat("yyyyMMdd").format(new Date());
            }else{
                id = "0";
            }
            String sql = "select * from count where activityId =? and userId=? and date=?";
            countList = find(sql,new Object[]{activityId, userId,id}, Count.class);
        }catch (Exception e){
            logger.error("投票失败", e);
        }
        return countList;
    }


    public Boolean insert(String activityId, String userId,int limitCount){
        Boolean flag = false;
        try {
            String id = "";
            if(limitCount == 2){
                id = new SimpleDateFormat("yyyyMMdd").format(new Date());
            }else{
                id = "0";
            }
            String sql = "insert into count (activityId,userId,voteTimes,date) values(?,?,?,?)";
            int row = save(sql, new Object[]{activityId, userId, 0,id});
            if (row != -1) {
                flag = true;
            }
        }catch (Exception e){
            logger.error("投票失败", e);
        }
        return flag;
    }




}
