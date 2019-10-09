package com.world.model.entitys.summary;

import java.math.BigDecimal;

/**
 * Created by suxinjie on 2017/6/24.
 *
 * 交易摘要
 */
public class TransactionSummary {

    /**
     * 买/卖量    成交价格    买入成本    卖出成本    买入净额    卖出净额    买入成本价    卖出成本价
     *  +200	     10	      2000	     0	       200	     0	        10	          0
     *  -100	     5	      1500	     0	       100	     0	        15	          0
     *  +100	     20	      3500	     0	       200	     0	        17.5	      0
     *  -100	     30	      500	     0	       100	     0	        5	          0
     *  -200	     40	      0	         7500	   0	     100	    0	          75
     *  -100	     20	      0	         9500	   0	     200	    0	          47.5
     *  +100	     5	      0	         9000	   0	     100	    0	          90
     *
     *  每次交易时进行计算存储到memcached中即可.
     *
     *  买入净额	= 买入总量 - 卖出总量
     *  买入成本价 = 买入成本 ÷ 买入净额
     *  市值 = 买入净额 * 市价
     *  盈亏 = 市值 - 买入成本价*买入净额
     *
     *  卖出净额	= 卖出总量-买入总
     *  卖出成本价 = 卖出成本÷卖出净额
     *  市值 = 卖出净额*市价
     *  盈亏 = 卖出成本价*卖出净额 - 市值
     *
     *  ================================= 上述需求可以总结成下面的实现 =================================
     *
     *  约定: + 表示 入; - 表示 出
     *
     *  买/卖量    成交价格     成本      净额       成本价
     *  +200      10          +2000    +200       +10
     *  -100      5           +1500    +100       +15
     *  +100      20          +3500    +200       +17.5
     *  -100      30          +500     +100       +5
     *  .............  再减100的话,净额就为0  .............
     *  -200      40          -7500    -100       +75 (应该根据净额或者成本判断是买入还是卖出)
     *  -100      20          -9500    -200       +47.5 (应该根据净额或者成本判断是买入还是卖出)
     *  +100      5           -9000    -100       +90 (应该根据净额或者成本判断是买入还是卖出)
     *
     *  净额	= 买入总量 - 卖出总量
     *  成本价 = |成本 ÷ 净额|
     *  市值 = |净额 * 市价|
     *  盈亏 = 市值 - |成本价*净额|
     *
     *
     *  注意: 在计算的时候可能出现"净额"为0的情况,这回对计算"成本价"造成困难
     *       这时候取上一次的 摘要记录 ,用当前的市价计算出 "盈亏值" 供前台使用
     *
     *
     */

    private BigDecimal num;             //买卖数量
    private BigDecimal transactionPrice;//成交价格
    private BigDecimal cost;            //成本
    private BigDecimal netAmount;       //净额
    private BigDecimal costPrice;       //成本价
    private BigDecimal profitLoss;      //盈亏

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
        this.num = num;
    }

    public BigDecimal getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(BigDecimal transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public BigDecimal getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(BigDecimal profitLoss) {
        this.profitLoss = profitLoss;
    }
}
