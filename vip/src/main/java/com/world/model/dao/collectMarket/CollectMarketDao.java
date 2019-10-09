package com.world.model.dao.collectMarket;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.user.CollectMarket;
import org.apache.log4j.Logger;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/10/18下午8:03
 */
public class CollectMarketDao extends DataDaoSupport<CollectMarket> {
    Logger logger = Logger.getLogger(CollectMarketDao.class);
    /**
     * 收藏币种列表
     *
     * @param uid
     * @return
     */
    public CollectMarket getCollectMarket(String uid) {
        CollectMarket collectMarket = null;
        try {
            String sql = "select id,userId,collect from collectmarket where userId=?";
            collectMarket = (CollectMarket) Data.GetOne(sql, new Object[]{uid}, CollectMarket.class);
        } catch(Exception e){
            logger.error(e.toString(), e);
        }
        return collectMarket;
    }
}
