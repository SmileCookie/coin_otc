package com.world.model.backcapital.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.backcapital.Dividend;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/12下午4:00
 */
public class DividendDao extends DataDaoSupport<Dividend> {

    public Dividend getDividendInfo() {
        String sql = "select id, balance, totalShareCount from gbcdividend order by id desc limit 1";
        List<Dividend> list = super.find(sql, new Object[]{}, Dividend.class);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public void updateBalance(BigDecimal balance) {
        super.update("update gbcdividend set balance = ? ", new Object[]{balance});
    }

    public void updateShareCount(int totalShareCount) {
        super.update("update gbcdividend set totalShareCount = ? where totalShareCount < ?", new Object[]{totalShareCount, totalShareCount});
    }
}
