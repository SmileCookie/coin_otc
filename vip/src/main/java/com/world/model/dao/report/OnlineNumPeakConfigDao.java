package com.world.model.dao.report;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.report.OnlineNumPeakConfig;

/**
 * 在线人数峰值配置
 *
 * @author Jack
 * @since 2019-02-21
 */
public class OnlineNumPeakConfigDao extends DataDaoSupport<OnlineNumPeakConfig> {

    /**
     * 根据类型和时间查询峰值配置
     * @param configType 类型
     * @param hour 时间
     * @return
     */
    public OnlineNumPeakConfig findByTypeAndHour(int configType, int hour) {
        String sql = "SELECT id, configType, max, min, incr, updateTime from online_num_peak_config where configType = ? and startHour <= ? and endHour > ?";
        return super.getT(sql, new Object[]{configType, hour, hour}, OnlineNumPeakConfig.class);
    }
}
