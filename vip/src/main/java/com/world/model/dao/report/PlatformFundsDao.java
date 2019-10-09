package com.world.model.dao.report;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.report.PlatformFunds;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PlatformFundsDao
 * @Description
 * @Author kinghao
 * @Date 2018/8/7   16:34
 * @Version 1.0
 * @Description
 */
public class PlatformFundsDao extends DataDaoSupport<PlatformFunds> {


    /**
     * 平台资金报表数据获取
     */
    public List<PlatformFunds> getPlatformFunds() {
        String sql = "SELECT fundsType , dealType , sum(t.txAmount) as txAmount FROM wallettransbill t WHERE t.configTime >(NOW() - INTERVAL 24 HOUR) and t.dealType in (1,2) GROUP BY t.fundsType , t.dealType ORDER BY txAmount DESC;";
        List<PlatformFunds> list = super.find(sql, new Object[]{}, PlatformFunds.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }
}
