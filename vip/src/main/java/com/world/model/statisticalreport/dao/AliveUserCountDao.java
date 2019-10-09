package com.world.model.statisticalreport.dao;

import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.entity.statisticalReport.AliveUserCount;
import com.world.model.entity.statisticalReport.AliveUserCountVo;
import com.world.util.date.TimeUtil;
import org.apache.http.Consts;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AliveUserCountDao extends DataDaoSupport<AliveUserCount> {

    Logger logger = Logger.getLogger(AliveUserCountDao.class);


    public Boolean saveBillAllCount(AliveUserCount aliveUserCount, Timestamp beginTime, Timestamp endTime) {
        Boolean flag = false;
        try {
            List<OneSql> sqls = new ArrayList<OneSql>();
            sqls.add(new OneSql("DELETE FROM aliveusercount where countDate >=? and countDate<=?", -2, new Object[]{TimeUtil.parseDate(beginTime.getTime()), TimeUtil.parseDate(endTime.getTime())}, "messi_ods"));
            sqls.add(getSql(aliveUserCount));
            flag = Data.doTransWithBatch(sqls);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return flag;
    }


    public List<AliveUserCountVo> getList(Date startTime, Date endTime) {
        List<AliveUserCountVo> aliveUserCountList = new ArrayList<AliveUserCountVo>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = TimeUtil.dayBegin(startTime);
        Date end = TimeUtil.dayEnd(endTime);
        try {
            StringBuilder where = new StringBuilder();
            where.append(" AND countDate >='" + sdf.format(begin) + "'");
            where.append(" AND countDate <'" + sdf.format(end) + "'");
            String w = where.toString();
            if (w.length() > 0) {
                w = " where " + w.substring(4);
            }
            String sql = "select id as id,pv as pvCount,accessingIp as accessingIp,uv as uvCount,ip as ipCount,registerCount as registerCount,allRegisterCount as allRegisterCount,loginCount as loginCount,loginRate as loginRate,registrationConversionRate as registrationConversionRate,countDate as countDate from aliveusercount " + w + " ";
            aliveUserCountList = Data.QueryT("messi_ods", sql, new Object[]{}, AliveUserCountVo.class);
            for(AliveUserCountVo aliveUserCountVo:aliveUserCountList){
                aliveUserCountVo.setCountDate(TimeUtil.getSpecifiedDayBefore(aliveUserCountVo.getCountDate()));
            }
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return aliveUserCountList;
    }


    public OneSql getSql(AliveUserCount aliveUserCount) {

        String sql = "insert into aliveusercount(";
        String cloumn = "", conditions = "";
        List<Object> values = new LinkedList<Object>();
        cloumn += "countDate";
        conditions += "now()";

        if (aliveUserCount.getPvCount() != 0) {//PV
            cloumn += ",pv";
            conditions += ",?";
            values.add(aliveUserCount.getPvCount());

        }
        if (aliveUserCount.getUvCount() != 0) {//UV
            cloumn += ",uv";
            conditions += ",?";
            values.add(aliveUserCount.getUvCount());

        }
        if (aliveUserCount.getIpCount() != null) {//IP 数量
            cloumn += ",ip";
            conditions += ",?";
            values.add(aliveUserCount.getIpCount());

        }
        if (aliveUserCount.getRegisterCount() != null) {//当日注册人数
            cloumn += ",registerCount";
            conditions += ",?";
            values.add(aliveUserCount.getRegisterCount());

        }
        if (aliveUserCount.getAllRegisterCount() != null) {//总注册人数
            cloumn += ",allRegisterCount";
            conditions += ",?";
            values.add(aliveUserCount.getAllRegisterCount());

        }
        if (aliveUserCount.getLoginCount() != null) {//当日登陆人数
            cloumn += ",loginCount";
            conditions += ",?";
            values.add(aliveUserCount.getLoginCount());
        }
        if (aliveUserCount.getLoginRate() != null) {//用户登陆率
            cloumn += ",loginRate";
            conditions += ",?";
            values.add(aliveUserCount.getLoginRate());
        }

        sql += cloumn + ") values (" + conditions + ")";
        log.info(sql);
        return new OneSql(sql, -1, values.toArray(), "messi_ods");


    }


    public AliveUserCount sumAliveUserData(Timestamp lastUpdateTime, Timestamp tsTodayTime) {
        AliveUserCount aliveUserCount = new AliveUserCount();
        try {
            //获取ip数量
            AliveUserCount ipCount = getIpCount(lastUpdateTime, tsTodayTime);
            if (ipCount != null) {
                aliveUserCount.setIpCount(ipCount.getIpCount());
            }
            //获取注册总量
            AliveUserCount allRegisterCount = getAllRegisterCount(null, tsTodayTime, Const.CUSTOMER_TYPE_NORMAL);
            if (allRegisterCount != null) {
                aliveUserCount.setAllRegisterCount(allRegisterCount.getAllRegisterCount());
            }
            //获取当日注册总量
            AliveUserCount registerCount = getAllRegisterCount(lastUpdateTime, tsTodayTime, Const.CUSTOMER_TYPE_NORMAL);
            if (registerCount != null) {
                aliveUserCount.setRegisterCount(registerCount.getAllRegisterCount());
            }
            //每日登陆人数
            AliveUserCount loginCount = getLoginCount(lastUpdateTime, tsTodayTime, Const.CUSTOMER_TYPE_NORMAL);
            if(loginCount != null){
                aliveUserCount.setLoginCount(loginCount.getLoginCount());
            }
            //用户登陆率
            if(loginCount.getLoginCount()!=0){
                double f1 = new BigDecimal((float) loginCount.getLoginCount() / aliveUserCount.getAllRegisterCount()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
                double f2 = (float) (f1 * 100);
                BigDecimal f = new BigDecimal(f2);
                double f3 = f.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                DecimalFormat df = new DecimalFormat("0.00");
                aliveUserCount.setLoginRate(df.format(f3));
            }else{
                aliveUserCount.setLoginRate("0.00");
            }



        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return aliveUserCount;
    }


    public AliveUserCount getIpCount(Timestamp lastUpdateTime, Timestamp tsTodayTime) {
        AliveUserCount ipCount = null;
        try {
            String ipSql = "select count(DISTINCT ip) as ipCount from userloginip where date >=? and date < ? ";
            ipCount = (AliveUserCount) Data.GetOne("messi_ods", ipSql, new Object[]{TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime())}, AliveUserCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return ipCount;
    }

    public AliveUserCount getAllRegisterCount(Timestamp lastUpdateTime, Timestamp tsTodayTime, String type) {
        AliveUserCount allRegisterCount = null;
        try {
            String sql = "select count(*) as allRegisterCount  from userinfo  WHERE uType =? and userName is not null ";
            StringBuilder where = new StringBuilder();
            if (lastUpdateTime != null) {
                where.append(" and registerTime>='" + TimeUtil.parseDate(lastUpdateTime.getTime()) + "'");
            }
            if (tsTodayTime != null) {
                where.append(" and registerTime<'" + TimeUtil.parseDate(tsTodayTime.getTime()) + "'");
            }
            String w = where.toString();
            sql = sql + w;
            allRegisterCount = (AliveUserCount) Data.GetOne("messi_ods", sql, new Object[]{type}, AliveUserCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return allRegisterCount;
    }


    public AliveUserCount getLoginCount(Timestamp lastUpdateTime, Timestamp tsTodayTime,String type) {
        AliveUserCount loginCount = null;
        try {
            String sql = "select count(DISTINCT userid) as loginCount from userloginip where userid in (select id from userinfo where userName is not null and uType =?) and date >=? and date < ? ";
            loginCount = (AliveUserCount) Data.GetOne("messi_ods", sql, new Object[]{type,TimeUtil.parseDate(lastUpdateTime.getTime()), TimeUtil.parseDate(tsTodayTime.getTime())}, AliveUserCount.class);
        } catch (Exception e) {
            logger.error(e.toString(), e);
        } return loginCount;
    }


}
