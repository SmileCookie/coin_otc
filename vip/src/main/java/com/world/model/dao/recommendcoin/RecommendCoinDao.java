package com.world.model.dao.recommendcoin;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.recommendcoin.RecommendCoin;

import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/1/253:21 PM
 */
public class RecommendCoinDao extends DataDaoSupport<RecommendCoin> {

    /**
     * 获取推荐币
     * @return
     */
    public List<RecommendCoin> getRecommendCoinList(){
        String sql = "select * from recommend_coin r where r.recommend = 1 order by r.dateTime desc limit 0,3";
        List<RecommendCoin> list = find(sql,new Object[]{},RecommendCoin.class);
        return list;
    }

}
