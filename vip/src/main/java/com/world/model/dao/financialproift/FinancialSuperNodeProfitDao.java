package com.world.model.dao.financialproift;

import com.alibaba.fastjson.JSON;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.financialproift.FinSupernodeProfit;
import com.world.model.entity.financialproift.FinUserProfit;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Ethan
 * @Date 2019-07-26 10:11
 * @Description
 **/

public class FinancialSuperNodeProfitDao extends DataDaoSupport<FinSupernodeProfit> {

    private static final String DATABASE = "vip_financial";
    private String sql="";

    /**
     * 获取超级节点收益可分配记录
     * @return
     */
    public List<FinSupernodeProfit> getSuperNodeProfitList(int status){
        sql="select * from fin_supernode_profit where status="+status;
        log.info("getSuperNodeProfitList-sql:" + sql);
        List<FinSupernodeProfit> supernodeProfits = Data.QueryT(DATABASE, sql, null, FinSupernodeProfit.class);
        return supernodeProfits;
    }

    /**
     * 获取超级节点收益可分配记录
     * @return
     */
    public List<FinSupernodeProfit> getAllSuperNodeProfitList(){
        sql="select * from fin_supernode_profit";
        log.info("getAllSuperNodeProfitList-sql:" + sql);
        List<FinSupernodeProfit> supernodeProfits = Data.QueryT(DATABASE, sql, null, FinSupernodeProfit.class);
        return supernodeProfits;
    }


    public List<FinUserProfit> getUserProfitList(BigDecimal total){
        String sql="select a.userid,round((a.vipweight/b.sumweight)*"+total+",9) percentamount from (select userid,vipweight from fin_userfinancialinfo where authPayFlag != 1 group by userid) a,(select sum(vipweight) sumweight from fin_userfinancialinfo where authPayFlag != 1) b";
        List<FinUserProfit> query = Data.QueryT(DATABASE, sql, null, FinUserProfit.class);
        return query;
    }
}
