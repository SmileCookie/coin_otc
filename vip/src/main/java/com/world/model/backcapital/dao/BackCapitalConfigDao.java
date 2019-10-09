package com.world.model.backcapital.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.backcapital.BackCapitalConfig;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author buxianguan
 * @date 2017/11/15
 */
public class BackCapitalConfigDao extends DataDaoSupport<BackCapitalConfig> {

    public BackCapitalConfig getConfig() {
        String sql = "select id, bcUserId, bcFrequency, feeRatio, baseBalance, luckyUserId, withdrawFrequency, withdrawAddress, webUrl, bcTaskStatus, withdrawTaskStatus, updateTime from backcapitalconfig order by id desc limit 1";
        List<BackCapitalConfig> list = super.find(sql, new Object[]{}, BackCapitalConfig.class);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public void updateFeeRatio(int feeRatio) {
        super.update("update backcapitalconfig set feeRatio = ? ", new Object[]{feeRatio});
    }
}
