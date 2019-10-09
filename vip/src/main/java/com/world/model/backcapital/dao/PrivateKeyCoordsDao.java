package com.world.model.backcapital.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.backcapital.PrivateKeyCoords;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/12下午4:00
 */
public class PrivateKeyCoordsDao extends DataDaoSupport<PrivateKeyCoords> {

    public List<PrivateKeyCoords> getCoordsList() {
        String sql = "select id, x, y from gbcprivatekeycoords where status=1";
        List<PrivateKeyCoords> list = super.find(sql, new Object[]{}, PrivateKeyCoords.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public void updateBalance(long id, BigDecimal balance) {
        super.update("update gbcdividend set balance = ? where id = ? ", new Object[]{balance, id});
    }

    public void updateShareCount(long id, int totalShareCount) {
        super.update("update gbcdividend set totalShareCount = ? where id = ? ", new Object[]{totalShareCount, id});
    }
}
