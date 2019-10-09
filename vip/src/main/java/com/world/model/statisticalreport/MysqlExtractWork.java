package com.world.model.statisticalreport;

import com.world.config.GlobalConfig;
import com.world.model.dao.task.Worker;
import com.world.util.RunSH;
import com.world.util.date.TimeUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>标题: mysql同步</p>
 * <p>描述: 从mysql中抽取数据放入新库中</p>
 * <p>版权: Copyright (c) 2017</p>
 *
 * @author chendi
 */
public class MysqlExtractWork extends Worker {
    private static final long serialVersionUID = 1L;
    /*上次更新时间默认为当天,即明天凌晨执行数据同步; 如果要改为前天:TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(1));*/
    private Timestamp lastUpdateTime = TimeUtil.getTodayFirst(TimeUtil.getBeforeTime(-1));

    public MysqlExtractWork(String name, String des){
        super(name, des);
    }


    @Override
    public void run() {
        try {
            /*时间控制*/
            Timestamp tsTodayTime = TimeUtil.getTodayFirst();
            Date nowDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            /*现在时间获取*/
            String strNowTime = sdf.format(nowDate);
            log.info("MysqlExtractWork...strNowTime = " + strNowTime + ", tsTodayTime = " + tsTodayTime + ", lastUpdateTime = " + lastUpdateTime);
            if (tsTodayTime.compareTo(lastUpdateTime) > 0) {
                RunSH s = new RunSH();
                s.run(GlobalConfig.getValue("shell_path"));
            }

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


}
