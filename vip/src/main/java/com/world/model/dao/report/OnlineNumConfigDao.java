package com.world.model.dao.report;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.report.OnlineNumConfig;

/**
 * 在线人数配置
 *
 * @author Jack
 * @since 2019-02-21
 */
public class OnlineNumConfigDao extends DataDaoSupport<OnlineNumConfig> {


    /**
     * 根据类型获取在线人数配置
     * @param configType 类型
     * @return
     */
    public OnlineNumConfig findByType(int configType) {
        String sql = "SELECT id, configType, max, min, incr, curNum, updateTime from online_num_config where configType = ?";
        return super.getT(sql, new Object[]{configType}, OnlineNumConfig.class);
    }

    /**
     * 更新当前在线人数
     * @param curNum 当前在线人数
     */
    public void updateCurNum(int curNum) {
        String sql = "UPDATE online_num_config SET curNum = ?";
        super.update(sql, new Object[]{curNum});
    }

    /**
     * 更新区间值
     * @param min 最小值
     * @param max 最大值
     * @param id 主键
     */
    public void updateIntervalById(int min, int max, int incr, Integer id) {
        String sql = "UPDATE online_num_config SET max = ? , min = ? , incr = ?, updateTime = now() where id = ?";
        super.update(sql, new Object[]{max, min, incr, id});
    }
}
