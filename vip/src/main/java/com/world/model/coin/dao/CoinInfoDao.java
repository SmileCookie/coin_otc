package com.world.model.coin.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.coin.CoinInfo;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @ClassName CoinInfoDao
 * @Description
 * @Author kinghao
 * @Date 2018/8/15   15:57
 * @Version 1.0
 * @Description
 */
public class CoinInfoDao extends DataDaoSupport<CoinInfo> {


    public List<CoinInfo> getCoinIntroduction(String coinName) {
        String sql = "select * from coin where coinName = ?";
//        String sql = "select * from coin where 1=1";
        List<CoinInfo> list = super.find(sql, new Object[]{coinName}, CoinInfo.class);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list;
    }
}
