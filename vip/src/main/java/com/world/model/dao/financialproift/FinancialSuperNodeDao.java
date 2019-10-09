package com.world.model.dao.financialproift;

import com.redis.RedisUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.financialproift.ProfitBlockConfig;
import com.world.model.entity.financialproift.SuperNode;

import java.math.BigDecimal;
import java.util.List;

public class FinancialSuperNodeDao extends DataDaoSupport<SuperNode> {

    private static final String DATABASE = "vip_financial";

    private static final String FINANCIAL_SNODETOTALPROFIT = "financial_sNodeTotalProfit";
    private static final String FINANCIAL_SNODENUM = "financial_sNodeNum";

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 查询有效的超级节点
     *
     * @return
     */
    public List<SuperNode> selectAvialableNode() {

        //查询状态为正常的超级节点
        String sql = "select * from fin_supernode where sNodeState = 1";
        log.info("selectAvialableNode-sql:" + sql);
        List<SuperNode> superNodeList = Data.QueryT(DATABASE, sql, new Object[]{}, SuperNode.class);
        return superNodeList;
    }

    /**
     * 将更新后的节点数量和节点总额同步到REDIS
     */
    public void updateSumNodeInfoToRedis() {

        //查询已启用的超级节点个数
        String sql = "select count(1) sNodeNum from fin_supernode where sNodeState = 1 and sNodeShowFlag = 1 ";
        log.info("updateSumNodeInfoToRedis-sql:" + sql);

        SuperNode superNodeCount = (SuperNode)
                Data.GetOne(DATABASE, sql, new Object[]{}, SuperNode.class);

        //查询所有超级节点的累积收益
        sql = "select sum(MiningAmount) as sNodeTotalProfit from fin_supernode_mining_detail "
        	+ "where sNodeId in (select sNodeId from fin_supernode where sNodeState = 1 and sNodeShowFlag = 1)";
        log.info("updateSumNodeInfoToRedis-sql:" + sql);
        
        SuperNode superNodeProfit = (SuperNode)
                Data.GetOne(DATABASE, sql, new Object[]{}, SuperNode.class);

        Long sNodeNum = superNodeCount.getsNodeNum();
        sNodeNum = sNodeNum == null ? 0 : sNodeNum;
        log.info("updateSumNodeInfoToRedis-sNodeNum:" + sNodeNum);

        BigDecimal totalProfit = superNodeProfit.getsNodeTotalProfit();
        totalProfit = totalProfit == null ? BigDecimal.ZERO : totalProfit;
        /*只设置整数部分*/
        totalProfit = totalProfit.setScale(0, BigDecimal.ROUND_DOWN);
        log.info("updateSumNodeInfoToRedis-totalProfit:" + totalProfit);

        RedisUtil.set(FINANCIAL_SNODENUM, String.valueOf(sNodeNum), 0);
        log.info("update sNodeNum redis success.");

        RedisUtil.set(FINANCIAL_SNODETOTALPROFIT, totalProfit.toPlainString(), 0);
        log.info("update sNodeTotalProfit redis success.");

    }

    public void updateBalance(SuperNode superNode, BigDecimal currentBalance) {

        String sql = "update fin_supernode set sNodeBalance  = " + currentBalance + ",sNodeModifyTime= now() where id = " + superNode.getId();
        log.info("update updateBalance-sql:" + sql);

        Data.Update(DATABASE, sql, null);
        log.info("update sNodeBalance mysql success.");

    }

    public void updateLateTimeAmount(SuperNode superNode, String amount, String createtime) {
        String sql = "update fin_supernode set lateMiningAmount = " + amount + ",lateMiningTime='" + createtime + "' where id=" + superNode.getId();
        log.info("updateLateTimeAmount-sql:" + sql);

        Data.Update(DATABASE, sql, null);
        log.info("update lateMiningAmount,lateMiningTime mysql success.");
    }
}
