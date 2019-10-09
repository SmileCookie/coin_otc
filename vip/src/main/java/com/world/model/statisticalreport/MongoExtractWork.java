package com.world.model.statisticalreport;

import com.google.code.morphia.query.Query;
import com.world.constant.Const;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.task.Worker;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.entity.statisticalReport.TimerLog;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserLoginIp;
import com.world.model.statisticalreport.dao.TimerLogDao;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static com.world.constant.Const.*;

public class MongoExtractWork extends Worker {
    private static final long serialVersionUID = 1L;
    UserDao dao = new UserDao();
    TimerLogDao timerLogDao = new TimerLogDao();
    UserLoginIpDao userLoginIpDao = new UserLoginIpDao();

    /*上次更新时间默认为当天,即明天凌晨执行数据同步; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));*/
    private Timestamp lastUpdateTime = TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));

    public MongoExtractWork(String name, String des) {
        super(name, des);
    }

    @Override
    public void run() {
        List<OneSql> sqls = new ArrayList<OneSql>();
        long t1 = System.currentTimeMillis();
        try {
        /*时间控制*/
            Timestamp tsTodayTime = TimeUtil.getTodayFirst();
            Timestamp tsTodayLastTime = TimeUtil.getTodayLast();
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*现在时间获取*/
            String strNowTime = sdf.format(nowDate);
            List<TimerLog> timerExtractList = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Mongo_User_Extract);
            List<TimerLog> timerImportList = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Mongo_User_Import);
            /**
             * mongo中user表抽取
             */
            if (CollectionUtils.isEmpty(timerExtractList) || CollectionUtils.isEmpty(timerImportList)) {
                if (!CollectionUtils.isEmpty(timerExtractList)) {
                    timerLogDao.delete(sdf.format(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1))), sdf.format(tsTodayTime), Const.Mongo_User_Extract);
                }
                if (!CollectionUtils.isEmpty(timerImportList)) {
                    timerLogDao.delete(sdf.format(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1))), sdf.format(tsTodayTime), Const.Mongo_User_Import);
                }
                sqls.add(new OneSql("DELETE FROM userinfo", -2, null, "messi_ods"));
                log.info("MongoExtractWork...strNowTime = " + strNowTime + ", tsTodayTime = " + tsTodayTime + ", lastUpdateTime = " + lastUpdateTime);
                if (tsTodayTime.compareTo(lastUpdateTime) > 0) {
                    Query<User> q = dao.getQuery();
                    q.filter("userName !=",null);
                    /*q.filter("registerTime >=", TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1)))
                            .filter("registerTime <", tsTodayTime);*/
                    List<User> dataList = dao.findPage(q, 0, Integer.MAX_VALUE);
                    timerLogDao.insert(sdf.format(nowDate), sdf.format(new Date()), Mongo_User_Extract, dataList.size());
                    Date mongoStart = new Date();
                    if (CollectionUtils.isEmpty(dataList)) {
                        log.info("用户表无任何数据");
                    } else {
                        for (User user : dataList) {
                            sqls.add(new OneSql("insert into userinfo(id,username,registerTime,utype) values(?,?,?,?)", -2, new Object[]{user.get_Id(), user.getUserName(), user.getActivationTime(), user.getCustomerType()}, "messi_ods"));
                        }
                        if (Data.doTransWithBatch(sqls)) {
                            timerLogDao.insert(sdf.format(mongoStart), sdf.format(new Date()), Mongo_User_Import, dataList.size());
                            long t3 = System.currentTimeMillis();
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                log.error(e.toString(), e);
                            }
                            log.info("--------------执行------------------" + (t3 - t1) + "ms");
                            log.info("成功转移" + dataList.size() + "条数据到新表中。休息50ms...");
                        } else {
                            log.error("处理数据出错。休息1分钟...");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                log.error(e.toString(), e);
                            }
                        }
                    }

                }
            }
            /**
             * mongo中userLoginIp表抽取
             */
            List<TimerLog> timerUserloginExtractList = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Mongo_UserLoginIp_Extract);
            List<TimerLog> timerUserloginImportList = timerLogDao.getList(sdf.format(tsTodayTime), sdf.format(tsTodayLastTime), Const.Mongo_UserLoginIp_Import);
            if (CollectionUtils.isEmpty(timerUserloginExtractList) || CollectionUtils.isEmpty(timerUserloginImportList)) {
                if (!CollectionUtils.isEmpty(timerUserloginExtractList)) {
                    timerLogDao.delete(sdf.format(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1))), sdf.format(tsTodayTime), Const.Mongo_UserLoginIp_Extract);
                }
                if (!CollectionUtils.isEmpty(timerUserloginImportList)) {
                    timerLogDao.delete(sdf.format(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1))), sdf.format(tsTodayTime), Const.Mongo_UserLoginIp_Import);
                }
                sqls.add(new OneSql("DELETE FROM userloginip where date >=? and date<?", -2, new Object[]{sdf.format(TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1))), sdf.format(tsTodayTime)}, "messi_ods"));
                log.info("MongoExtractWork...strNowTime = " + strNowTime + ", tsTodayTime = " + tsTodayTime + ", lastUpdateTime = " + lastUpdateTime);
                if (tsTodayTime.compareTo(lastUpdateTime) > 0) {
                    Query<UserLoginIp> q = userLoginIpDao.getQuery();
                    q.filter("date >=", TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1)))
                            .filter("date <", tsTodayTime);
                    List<UserLoginIp> dataList = userLoginIpDao.findPage(q, 0, 100000);
                    timerLogDao.insert(sdf.format(nowDate), sdf.format(new Date()), Mongo_UserLoginIp_Extract, dataList.size());
                    Date mongoStart = new Date();
                    if (CollectionUtils.isEmpty(dataList)) {
                        log.info("今日无用户登陆记录");
                    } else {
                        for (UserLoginIp user : dataList) {
                            sqls.add(new OneSql("insert into userloginip(userId,ip,date) values(?,?,?)", -2, new Object[]{user.getUserId(), user.getIp(),sdf.format(user.getDate())}, "messi_ods"));
                        }
                        if (Data.doTransWithBatch(sqls)) {
                            timerLogDao.insert(sdf.format(mongoStart), sdf.format(new Date()), Mongo_UserLoginIp_Import, dataList.size());
                            long t3 = System.currentTimeMillis();
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                                log.error(e.toString(), e);
                            }
                            log.info("--------------执行------------------" + (t3 - t1) + "ms");
                            log.info("成功转移" + dataList.size() + "条数据到新表中。休息50ms...");
                        } else {
                            log.error("处理数据出错。休息1分钟...");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                log.error(e.toString(), e);
                            }
                        }
                    }

                }

            }


        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }



    public static void main(String[] args) {
        MongoExtractWork statisticalReportWork = new MongoExtractWork("", "");
        statisticalReportWork.run();



    }

}


