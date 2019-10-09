package com.world.model.statisticalreport.dao;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.statisticalReport.TimerLog;
import com.world.util.date.TimeUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.world.constant.Const.*;

public class TimerLogDao extends DataDaoSupport<TimerLog> {
    Logger logger = Logger.getLogger(TimerLogDao.class);

    public List<TimerLog> getList(String beginTime, String endTime,String type) {
        List<TimerLog> timerLogList = new ArrayList<TimerLog>();
        String sql = "";
        try {
            sql = "select * from timerLog where beginTime >=? and endTime <=? and timerType=?";
            logger.info(sql.toString()+beginTime+"到"+endTime);
            List<Bean> list = Data.Query("messi_ods",sql, new Object[]{beginTime, endTime,type},TimerLog.class);
            for(Bean b:list){
                TimerLog timerLog=(TimerLog) b;
                timerLogList.add(timerLog);
            }
        } catch (Exception e) {
            logger.error("查询历史记录信息失败!", e);
        }
        return timerLogList;
    }



    public void delete(String beginTime, String endTime,String type){
        try {
            String sql = "delete from timerLog where beginTime >=? and endTime <=? and timerType=?";
            Object[] param = new Object[]{beginTime, endTime,type};
            Data.Delete("messi_ods",sql, param);
        } catch (Exception e) {
            logger.error("删除历史记录信息失败!", e);
        }
    }


    public void insert(String beginTime, String endTime,String type,int timerDataCount){
        try {
            String sql = "insert into timerLog (createDate,timerName,beginTime,endTime,timerDataCount,timerState,timerType,mark) values(?,?,?,?,?,?,?,?)";
            Object[] param = new Object[]{TimeUtil.parseDate(System.currentTimeMillis()), TIMER_NAME.get(type).toString(),beginTime,endTime,timerDataCount,"0",type,""};
            Data.Insert("messi_ods",sql,param);
        } catch (Exception e) {
            logger.error("插入历史记录信息失败!", e);
        }

    }

}
