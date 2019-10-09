package com.world.model.balaccount.job.report;


import com.world.cache.Cache;
import com.world.model.dao.report.OnlineNumConfigDao;
import com.world.model.dao.report.OnlineNumPeakConfigDao;
import com.world.model.dao.report.ReportTypeDao;
import com.world.model.dao.task.Worker;
import com.world.model.entity.report.OnlineNumConfig;
import com.world.model.entity.report.OnlineNumPeakConfig;
import com.world.model.entity.report.ReportType;
import com.world.model.entity.report.UserOnline;
import com.world.system.Sys;
import com.world.util.string.StringUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName UserOnlineWork
 * @Description
 * @Author kinghao
 * @Date 2018/8/21   10:09
 * @Version 1.0
 * @Description
 */
public class UserOnlineWork extends Worker {


    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UserOnlineWork.class);

    OnlineNumConfigDao onlineNumConfigDao = new OnlineNumConfigDao();
    OnlineNumPeakConfigDao onlineNumPeakConfigDao = new OnlineNumPeakConfigDao();
    Random random = new Random(System.currentTimeMillis());
    public UserOnlineWork(String name, String des) {
        super(name, des);
    }

    public UserOnlineWork() {
    }


    public static void main(String[] args) {
//        UserOnlineWork userOnlineWork = new UserOnlineWork("UserOnlineWork", "用户在线");
//        userOnlineWork.run();
//        String s = Cache.Get("USER_ONLINE_WORK_KEY");
//        System.out.println("====>>" + s);
    }

    @Override
    public void run() {
        try {
            // 获取系统时间
            Date date = new Date();
            // 判断当前是星期几
            Calendar calendar = Calendar.getInstance();

            int configType = getConfigType(calendar);
            // 查询在线人数生成规则
            OnlineNumConfig onlineNumConfig = onlineNumConfigDao.findByType(configType);
            if (onlineNumConfig == null ||
                    onlineNumConfig.getMax() == null ||
                    onlineNumConfig.getMin() == null ||
                    onlineNumConfig.getIncr() == null) {
                // 未配置在线人数
                return;
            }
            int max;
            int min;
            int incr;
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            long timeDiff = 2 * 7 * 24 * 60 * 60 * 1000;// 2周
            float rate = 1.2f;

            // 根据当前时间点判断在峰值区间
            OnlineNumPeakConfig onlineNumPeakConfig = onlineNumPeakConfigDao.findByTypeAndHour(configType, hour);
            if (onlineNumPeakConfig == null) {
                // 非峰值区间
                if (date.getTime() - onlineNumConfig.getUpdateTime().getTime() > timeDiff) {
                    min = (int) (onlineNumConfig.getMin()*rate);
                    max = (int) (onlineNumConfig.getMax() * rate);
                    incr = (int) (onlineNumConfig.getIncr() * rate);
                    onlineNumConfigDao.updateIntervalById(min, max, incr, onlineNumConfig.getId());
                } else {
                    max = onlineNumConfig.getMax();
                    min = onlineNumConfig.getMin();
                    incr = onlineNumConfig.getIncr();
                }
            } else {
                if (date.getTime() - onlineNumPeakConfig.getUpdateTime().getTime() > timeDiff) {
                    min = (int) (onlineNumPeakConfig.getMin()*rate);
                    max = (int) (onlineNumPeakConfig.getMax() * rate);
                    incr = (int) (onlineNumPeakConfig.getIncr() * rate);
                    onlineNumConfigDao.updateIntervalById(min, max, incr, onlineNumConfig.getId());
                } else {
                    max = onlineNumPeakConfig.getMax();
                    min = onlineNumPeakConfig.getMin();
                    incr = onlineNumPeakConfig.getIncr();
                }
            }
            // 获取当前在线人数
            int curNum = onlineNumConfig.getCurNum() == null ? min : onlineNumConfig.getCurNum();
            do {
                curNum = getCurNum(curNum, min, max, incr);
            } while (curNum <= 0);
            // 保存当前在线人数
            onlineNumConfigDao.updateCurNum(curNum);
            UserOnline userOnline = new UserOnline();
            userOnline.setInitial(new BigDecimal(curNum));
            Cache.Set("USER_ONLINE_WORK_KEY", JSONObject.fromObject(userOnline).toString(), 60 * 60);
        } catch (Exception e) {
            logger.error("[用户分布报表定时任务处理数据异常] ：" + e);
        }
    }

    private int getCurNum(int curNum, int min, int max, int incr) {
        if (curNum % 3 < 2) {
            // 防止拉升过快
            if (curNum < min) {
                return curNum + random.nextInt(incr);
            }
            if (curNum >= max) {
                return curNum - random.nextInt(incr);
            }
        }
        return curNum + (incr - random.nextInt(incr * 2));

    }

    /**
     * 获取配置类型
     * @param calendar
     * @return
     */
    private int getConfigType(Calendar calendar) {
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week != 1 && week != 7) {
            return 0;
        } else {
            return 1;
        }
    }

}
