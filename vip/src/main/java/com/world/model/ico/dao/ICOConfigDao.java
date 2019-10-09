package com.world.model.ico.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.ico.entity.ICOConfig;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by xie on 2017/7/12.
 */
public class ICOConfigDao extends DataDaoSupport<ICOConfig> {


    /**
     * 查询所有的ICO配置信息
     * @return
     */
    public List<ICOConfig> getAllICOConfig() {
        return super.find("select * from icoconfig", new Object[] {}, ICOConfig.class);
    }

    /**
     * 查询所有的ICO配置信息
     * @return
     */
    public List<ICOConfig> getAllICOConfig(String filter) {
        return super.find("select * from icoconfig ? ", new Object[] {filter}, ICOConfig.class);
    }

    /**
     * 取出符合条件的配售或者申购的类型
     * @return
     */
    public ICOConfig getICOConfigByTime(Timestamp currentTime){
//        String filter = "where exchangeStartTime <= now() and exchangeEndTime >= now() ";
        String sql = "select * from icoconfig where exchangeStartTime <= ? and exchangeEndTime >= ?";
        List<ICOConfig> list = super.find(sql, new Object[] {currentTime,currentTime}, ICOConfig.class);
        if(null == list || list.size() == 0){
            sql = "select * from icoconfig where saleType = ?";
            list = super.find(sql, new Object[] {1}, ICOConfig.class);
        }
        return list.get(0);
    }

}
