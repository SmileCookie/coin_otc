package com.world.model.ico.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.ico.entity.ICOExchangeNumConfig;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by xie on 2017/7/12.
 */
public class ICOExchangeNumConfigDao extends DataDaoSupport<ICOExchangeNumConfig> {


    /**
     * 获取当前ICO兑换配置
     * @return
     */
    // TODO: 2017/7/14
    public List<ICOExchangeNumConfig> getCurrentICOExchangeNumList(Timestamp currentTime) {
        return super.find("select * from icoexchangenumconfig where exchangeStartTime <= ? and exchangeEndTime >= ?", new Object[] {currentTime, currentTime},  ICOExchangeNumConfig.class);
    }



    /**
     * 查询所有的ICO配置信息
     * @return
     */
    public List< ICOExchangeNumConfig> getSimpleICOConfig(int saleType) {
        return super.find("select * from icoexchangenumconfig where saleType=?", new Object[] {saleType},  ICOExchangeNumConfig.class);
    }





}
