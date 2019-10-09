package com.world.model.dao.financialproift;

import com.redis.RedisUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.financialproift.ProfitBlockConfig;

public class FinancialProiftBlockConfigDao extends DataDaoSupport<ProfitBlockConfig> {

    private static final String DATABASE = "vip_financial";

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取当前数据库区块高度
     *
     * @param blockTypeCurrent
     * @return
     */
    public Long selectMaxBlockHeight(int blockTypeCurrent) {

        String sql = "select * from fin_profitblockconfig where blockType=? order by blockHeight desc limit 1";
        log.info("selectMaxBlockHeight-sql:" + sql);
        ProfitBlockConfig blockHeight = (ProfitBlockConfig)
                Data.GetOne(DATABASE, sql, new Object[]{blockTypeCurrent}, ProfitBlockConfig.class);
        return blockHeight == null ? null : blockHeight.getBlockHeight();
    }

    /**
     * 插入当前爬虫获取区块高度
     *
     * @param blockTypeCurrent
     * @param currentHeight
     * @param financialCurrentBlock
     */
    public void insertBlockHeight(int blockTypeCurrent, Integer currentHeight, String financialCurrentBlock) {

        String sql = "insert into fin_profitblockconfig (blockType,blockHeight,blockRemark,addTime) " +
                "values(" + blockTypeCurrent + "," + currentHeight + ",'当前区块高度',now())";
        log.info("insertBlockHeight-sql:" + sql);

        Data.Insert(DATABASE, sql, null);
        log.info("insertBlockHeight mysql success.");

        RedisUtil.set(financialCurrentBlock, String.valueOf(currentHeight), 0);
        log.info("insertBlockHeight redis success.");
    }

    /**
     * 更新数据库当前区块高度
     *
     * @param blockTypeCurrent
     * @param currentHeight
     * @param financialCurrentBlock
     */
    public void updateCurrentBlockHeight(int blockTypeCurrent, Integer currentHeight, String financialCurrentBlock) {

        String sql = "update fin_profitblockconfig set blockHeight = " + currentHeight + ",modifyTime= now() where blockType = " + blockTypeCurrent;
        log.info("update BlockHeight-sql:" + sql);

        Data.Update(DATABASE, sql, null);
        log.info("update BlockHeight mysql success.");

        RedisUtil.set(financialCurrentBlock, String.valueOf(currentHeight), 0);
        log.info("update CurrentBlockHeight redis success.");

    }

    /**
     * 获取最小分红区块高度
     *
     * @param blockTypeProfit
     * @param blockTypeCurrent
     * @return
     */
    public ProfitBlockConfig selectMinProfitHeight(int blockTypeProfit, int blockTypeCurrent) {

        String sql = "select min(blockHeight) blockHeight,id from fin_profitblockconfig where  blockType =   " + blockTypeProfit
                + " and blockHeight >= ( select  max(blockHeight) from fin_profitblockconfig where blockType = " + blockTypeCurrent + ")";
        log.info("selectMinProfitHeight-sql:" + sql);
        ProfitBlockConfig minProfitHeight = (ProfitBlockConfig)
                Data.GetOne(DATABASE, sql, new Object[]{}, ProfitBlockConfig.class);
        return minProfitHeight;
    }

    public void updateProfitBlockHeight(ProfitBlockConfig profitBlockConfig, String financialProfitBlock) {

        RedisUtil.set(financialProfitBlock, String.valueOf(profitBlockConfig.getBlockHeight()), 0);
        log.info("update ProfitBlockHeight redis success.");

    }
}
