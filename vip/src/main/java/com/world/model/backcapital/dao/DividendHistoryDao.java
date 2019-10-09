package com.world.model.backcapital.dao;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.backcapital.DividendHistory;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/12下午4:00
 */
public class DividendHistoryDao extends DataDaoSupport<DividendHistory> {

    public DividendHistory getLastDividendHistory() {
        String sql = "select id, uniqueKey, number, amount, shareCount, time from gbcdividendhistory order by number desc limit 1";
        List<DividendHistory> list = super.find(sql, new Object[]{}, DividendHistory.class);
        if (CollectionUtils.isEmpty(list)) {
            return new DividendHistory();
        }
        return list.get(0);
    }

    public BigDecimal getTotalDividendAmount() {
        String sql = "select IFNULL(sum(amount),0) as amount from gbcdividendhistory";
        List<DividendHistory> list = super.find(sql, new Object[]{}, DividendHistory.class);
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.get(0).getAmount();
    }

    public DividendHistory getByUniqueKey(String uniqueKey) {
        String sql = "select id from gbcdividendhistory where uniqueKey=? ";
        List<DividendHistory> list = super.find(sql, new Object[]{uniqueKey}, DividendHistory.class);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public int getLastNumber() {
        String sql = "select number from gbcdividendhistory order by number desc limit 1";
        List<DividendHistory> list = super.find(sql, new Object[]{}, DividendHistory.class);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return list.get(0).getNumber();
    }

    public int insert(String uniqueKey, int number, BigDecimal amount, int shareCount, long time) {
        String sql = "insert into gbcdividendhistory (`uniqueKey`, `number`, `amount`, `shareCount`, `time`) values ( ?, ?, ?, ?, ?)";
        Object[] param = new Object[]{uniqueKey, number, amount, shareCount, time};
        return Data.Insert(sql, param);
    }

    public void update(String uniqueKey, BigDecimal amount, int shareCount, long time) {
        super.update("update gbcdividendhistory set amount = ?, shareCount=?, time=? where uniqueKey = ? ",
                new Object[]{amount, shareCount, time, uniqueKey});
    }
}
