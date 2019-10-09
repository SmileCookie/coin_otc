package com.world.model.financialproift.userfininfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.redis.RedisUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.dao.task.Worker;
import com.world.model.entity.financialproift.FinSuperNodeMiningDetail;
import com.world.model.entity.financialproift.FinUserRewardStatus;
import com.world.model.entity.financialproift.FinancialProduct;
import com.world.model.financial.entity.FinSuperNode;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

/**
 * @Author Ethan
 * @Date 2019-08-04 12:26
 * @Description
 **/

public class SetPlatEcologySystemWork extends Worker {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static int run_index = 1;

    public SetPlatEcologySystemWork(String name, String des) {
        super(name, des);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {

        /****************************************************
         *
         *  rediskey_prefix : financial_
         *
         * 1 生态回馈奖金 platEcologySystemAmount,platEcologySystemAmountUsdt
         *
         * 2 超级节点 statu = 1累积分配 platSuperNodePayAmount platSuperNodePayAmountUsdt status=0 待分配：platSuperNodeNotPayAmount  platSuperNodeNotPayAmountUsdt
         *
         * 3 用户分红奖励状态  distStatus=0 platNewVipWeekAmount platNewVipWeekAmountUsdt
         *
         * 4 全球领袖分红  platLeaderBonusAmount platLeaderBonusAmountUsdt  platLeaderBonusWeight
         *
         * 5 投资总人数，投资金额，投资预期收益 proTotalUser proTotalAmount sumInvestUsdtAmount  sumExpectProfitUsdt
         ****************************************************/

        try {
            String sql = "";
            //1  生态回馈奖金
            if (run_index == 1) {
                try {
                    String platEcologySystemAmount = "0";
                    String platEcologySystemAmountUsdt = "0";
                    /*执行标识 1：执行完成 0未执行*/
                    sql = "select sum(profitAmount) profitAmount, sum(usdtAmount) usdtAmount from fin_profit_assign_detail "
                            + "where profitType = 6 and flag = 1 ";
                    log.info("SetPlatEcologySystemWork sql = " + sql);
                    List<BigDecimal> listFinProfitAssignDetail = (List<BigDecimal>) Data.GetOne("vip_financial", sql, null);
                    /*接收处理变量*/
                    if (null != listFinProfitAssignDetail && listFinProfitAssignDetail.size() > 0) {
                        if (null != listFinProfitAssignDetail.get(0)) {
                            platEcologySystemAmount = listFinProfitAssignDetail.get(0).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                        if (null != listFinProfitAssignDetail.get(1)) {
                            platEcologySystemAmountUsdt = listFinProfitAssignDetail.get(1).setScale(3, BigDecimal.ROUND_DOWN) + "";
                        }
                    }
                    RedisUtil.set("financial_platEcologySystemAmount", platEcologySystemAmount, 0);
                    RedisUtil.set("financial_platEcologySystemAmountUsdt", platEcologySystemAmountUsdt, 0);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }

                run_index++;
            }

            //2 超级节点
            if (run_index == 2) {
                try {
                    /**
                     * 7vip分红,5超级节点
                     * distType
                     */
                    sql = "select distType, distStatus, distFlag, sum(distBal) distBal, sum(distBal * usdtPrice) usdtPrice "
                            + "from fin_user_reward_status group by distType, distStatus";
                    log.info("SetPlatEcologySystemWork-SuperNode fin_user_reward_status sql = " + sql);
                    List<Bean> listPlatAmount = (List<Bean>) Data.Query("vip_financial", sql, null, FinUserRewardStatus.class);

                    /**
                     * 先设定值，防止循环不到
                     */
//                    RedisUtil.set("financial_platSuperNodeNotPayAmount", "0", 0);
//    				RedisUtil.set("financial_platSuperNodeNotPayAmountUsdt", "0", 0);
                    /*定义循环变量*/
                    int distType = 0;
                    int distFlag = 0;
                    int distStatus = 0;
                    BigDecimal distBal = BigDecimal.ZERO;
                    BigDecimal usdtPrice = BigDecimal.ZERO;
                    FinUserRewardStatus finUserRewardStatus = null;
                    if (null != listPlatAmount && listPlatAmount.size() > 0) {
                        for (int i = 0; i < listPlatAmount.size(); i++) {
                            finUserRewardStatus = (FinUserRewardStatus) listPlatAmount.get(i);
                            /*获取循环值*/
                            distType = finUserRewardStatus.getDistType();
                            distFlag = finUserRewardStatus.getDistFlag();
                            distStatus = finUserRewardStatus.getDistStatus();
                            distBal = finUserRewardStatus.getDistBal().setScale(3, BigDecimal.ROUND_DOWN);
                            /*其实是折算后的USDT，借用变量*/
                            usdtPrice = finUserRewardStatus.getUsdtPrice().setScale(3, BigDecimal.ROUND_DOWN);
                            /**
                             * distStatus = 1 && distFlag = 0 已分配完成,
                             * distStatus = 0 && distFlag = 1   未分配,
                             * 0 0 暂不使用，预留
                             * 7vip分红,5超级节点
                             */
                            if (1 == distStatus && 0 == distFlag) {
                                /*已分配完成*/
                                if (5 == distType) {
                                    RedisUtil.set("financial_platSuperNodePayAmount", distBal + "", 0);
                                    RedisUtil.set("financial_platSuperNodePayAmountUsdt", usdtPrice + "", 0);
                                }
//                    			else if (7 == distType) {
//                    				RedisUtil.set("financial_platNewVipWeekAmount", distBal + "", 0);
//                                    RedisUtil.set("financial_platNewVipWeekAmountUsdt", usdtPrice + "", 0);
//                    			}
                            }
                            if (0 == distStatus && 1 == distFlag) {
                                /*待分配*/
                                if (5 == distType) {
                                    RedisUtil.set("financial_platSuperNodeNotPayAmount", distBal + "", 0);
                                    RedisUtil.set("financial_platSuperNodeNotPayAmountUsdt", usdtPrice + "", 0);
                                }
//                    			else if (7 == distType) {
//                    				RedisUtil.set("financial_platNewVipWeekNotPayAmount", distBal + "", 0);
//                    				RedisUtil.set("financial_platNewVipWeekNotPayAmountUsdt", usdtPrice + "", 0);
//                    			}
                            }
                        }
                    }
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork设置新人加成和VIP奖励", e);
                }
                run_index++;
            }

            /*全球领袖分红*/
            if (run_index == 3) {
                try {
                    String platLeaderBonusAmount = "0";
                    String platLeaderBonusAmountUsdt = "0";
                    BigDecimal bdPlatLeaderBonusAmount = BigDecimal.ZERO;
                    BigDecimal bdPlatLeaderBonusAmountUsdt = BigDecimal.ZERO;

                    sql = "select count(distinct userId) sumProTotalUser, sum(investAmount) sumProTotalAmount, "
                            + "sum(investUsdtAmount) sumInvestUsdtAmount, sum(expectProfitUsdt) sumExpectProfitUsdt from fin_productinvest "
                            + "where leaderBonusDealFlag = 0";
                    FinancialProduct financialProduct = (FinancialProduct) Data.GetOne("vip_financial", sql, null, FinancialProduct.class);
                    if (null != financialProduct) {
                        log.info("financialProduct.getSumProTotalUser() = " + financialProduct.getSumProTotalUser());
                        if (null != financialProduct.getSumProTotalAmount()) {
                            bdPlatLeaderBonusAmount = financialProduct.getSumProTotalAmount();
                        }
                        if (null != financialProduct.getSumInvestUsdtAmount()) {
                            bdPlatLeaderBonusAmountUsdt = financialProduct.getSumInvestUsdtAmount();
                        }
                    }

                    platLeaderBonusAmount = bdPlatLeaderBonusAmount.multiply(BigDecimal.valueOf(0.06)).setScale(0, BigDecimal.ROUND_DOWN) + "";
                    platLeaderBonusAmountUsdt = bdPlatLeaderBonusAmountUsdt.multiply(BigDecimal.valueOf(0.06)).setScale(0, BigDecimal.ROUND_DOWN) + "";

                    RedisUtil.set("financial_platLeaderBonusAmount", platLeaderBonusAmount, 0);
                    RedisUtil.set("financial_platLeaderBonusAmountUsdt", platLeaderBonusAmountUsdt, 0);

                    /*全球分红总权重*/
                    String platLeaderBonusWeight = "0";
                    sql = "select sum(weight) from t_user ";
                    log.info("SetPlatEcologySystemWork sql = " + sql);
                    List<BigDecimal> listPlatLeaderBonusWeight = (List<BigDecimal>) Data.GetOne("vdsapollo", sql, null);
                    /**/
                    if (null != listPlatLeaderBonusWeight && listPlatLeaderBonusWeight.size() > 0) {
                        if (null != listPlatLeaderBonusWeight.get(0)) {
                            platLeaderBonusWeight = listPlatLeaderBonusWeight.get(0).setScale(0, BigDecimal.ROUND_DOWN) + "";
                        }
                    }
                    RedisUtil.set("financial_platLeaderBonusWeight", platLeaderBonusWeight, 0);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }

                run_index++;
            }

            /*投资总人数，投资金额，投资预期收益*/
            if (run_index == 4) {
                try {
                    /*投资人数*/
                    /*投资人数*/
                    String proTotalUser = "0";
                    String proTotalAmount = "0";
                    String sumInvestUsdtAmount = "0";
                    String sumExpectProfitUsdt = "0";
                    long bdProTotalUser = 0;
                    BigDecimal bdProTotalAmount = BigDecimal.ZERO;
                    BigDecimal bdSumInvestUsdtAmount = BigDecimal.ZERO;
                    BigDecimal bdSumExpectProfitUsdt = BigDecimal.ZERO;

                    /*重新从数据库获取,先不考虑多产品 where proState = 1*/
                    sql = "select count(distinct userId) sumProTotalUser, sum(investAmount) sumProTotalAmount, "
                            + "sum(investUsdtAmount) sumInvestUsdtAmount, sum(expectProfitUsdt) sumExpectProfitUsdt from fin_productinvest";
                    log.info("productSuperNode sql = " + sql);
                    FinancialProduct financialProduct = (FinancialProduct) Data.GetOne("vip_financial", sql, null, FinancialProduct.class);
                    if (null != financialProduct) {
                        log.info("financialProduct.getSumProTotalUser() = " + financialProduct.getSumProTotalUser());
                        bdProTotalUser = financialProduct.getSumProTotalUser();
                        if (null != financialProduct.getSumProTotalAmount()) {
                            bdProTotalAmount = financialProduct.getSumProTotalAmount();
                        }
                        if (null != financialProduct.getSumInvestUsdtAmount()) {
                            bdSumInvestUsdtAmount = financialProduct.getSumInvestUsdtAmount();
                        }
                        if (null != financialProduct.getSumExpectProfitUsdt()) {
                            bdSumExpectProfitUsdt = financialProduct.getSumExpectProfitUsdt();
                        }
                    }
                    proTotalUser = bdProTotalUser + "";
                    proTotalAmount = bdProTotalAmount.setScale(0, BigDecimal.ROUND_DOWN) + "";
                    sumInvestUsdtAmount = bdSumInvestUsdtAmount.setScale(0, BigDecimal.ROUND_DOWN) + "";
                    sumExpectProfitUsdt = bdSumExpectProfitUsdt.setScale(0, BigDecimal.ROUND_DOWN) + "";
                    log.info("intProTotalUser = " + proTotalUser + ", bdProTotalAmount = " + proTotalAmount);

//                    RedisUtil.set("financial_proTotalUser", proTotalUser, 0);
                    RedisUtil.set("financial_proTotalAmount", proTotalAmount, 0);
                    RedisUtil.set("financial_sumInvestUsdtAmount", sumInvestUsdtAmount, 0);
                    RedisUtil.set("financial_sumExpectProfitUsdt", sumExpectProfitUsdt, 0);

                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }
                run_index++;
            }

            if (run_index == 5) {
                /*总分红权重，人数*100*/
                try {
                    String profitWeightTotal = "0";
                    sql = "select sum(vipWeight) vipWeight from fin_userfinancialinfo where authPayFlag = 2 and matrixLevel = 6 ";
                    log.info("productSuperNode sql = " + sql);
                    List<BigDecimal> listTotalWeight = (List<BigDecimal>) Data.GetOne("vip_financial", sql, null);
                    if (null != listTotalWeight) {
                        profitWeightTotal = listTotalWeight.get(0).setScale(0, BigDecimal.ROUND_DOWN) + "";
                    }
                    //VIP总分红权
                    RedisUtil.set("financial_profitWeightTotal", profitWeightTotal, 60);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }
                run_index++;
            }

            if (run_index == 6) {
                try {
                    /*获取分红时间*/
                    String bonusTime = "0";
                    sql = "select UNIX_TIMESTAMP (paramTime) from fin_sys_config where paramState = 1 and now() < paramTime order by paramTime asc limit 1";
                    log.info("productSuperNode = " + sql);
                    List<Long> listParamTime = (List<Long>) Data.GetOne("vip_financial", sql, null);
                    if (null != listParamTime && listParamTime.size() > 0) {
                        if (null != listParamTime.get(0)) {
                            bonusTime = listParamTime.get(0) * 1000 + "";
                        }
                    }
                    RedisUtil.set("financial_bonusTime", bonusTime, 0);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }
                run_index++;
            }

            /**
             * 本周新VIP加成未分配VDS
             */
            if (run_index == 7) {
//            	RedisUtil.set("financial_platNewVipWeekNotPayAmount", "0", 0);
//				RedisUtil.set("financial_platNewVipWeekNotPayAmountUsdt", "0", 0);
                String strMiningAmount = "0";
                try {
                    sql = "select sum(miningAmount) miningAmount from fin_supernode_mining_detail "
                            + "where sNodeBelType != 1 and sNodeShowFlag = 1 and profitBatchNo = 0";
                    log.info("fin_supernode_mining_detail = " + sql);
                    List<BigDecimal> listMiningAmount = (List<BigDecimal>) Data.GetOne("vip_financial", sql, null);
                    if (null != listMiningAmount && null != listMiningAmount.get(0)) {
                        strMiningAmount = listMiningAmount.get(0).setScale(0, BigDecimal.ROUND_DOWN) + "";
                    }
                    RedisUtil.set("financial_platNewVipWeekNotPayAmount", strMiningAmount, 0);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }
                run_index++;
            }

            /**
             * 平台是否贡献
             * platReleaseNotPayAmount	本周释放贡献	
             * platReleasePayAmount	累积释放贡献
             */
            if (run_index == 8) {
//            	RedisUtil.set("financial_platReleaseNotPayAmount", "0", 0);
//            	RedisUtil.set("financial_platReleasePayAmount", "0", 0);
                String platReleaseNotPayAmount = "0";
                String platReleasePayAmount = "0";
                try {
                    sql = "select sum(toSuperNodeAmount) from fin_dou_profit_log where batchNo = 0";
                    log.info("fin_supernode_mining_detail = " + sql);
                    List<BigDecimal> listPlatReleaseNotPayAmount = (List<BigDecimal>) Data.GetOne("vip_financial", sql, null);
                    if (null != listPlatReleaseNotPayAmount && null != listPlatReleaseNotPayAmount.get(0)) {
                        platReleaseNotPayAmount = listPlatReleaseNotPayAmount.get(0).setScale(0, BigDecimal.ROUND_DOWN) + "";
                    }

                    sql = "select sum(toSuperNodeAmount) from fin_dou_profit_log where batchNo != 0";
                    log.info("fin_supernode_mining_detail = " + sql);
                    List<BigDecimal> listPlatReleasePayAmount = (List<BigDecimal>) Data.GetOne("vip_financial", sql, null);
                    if (null != listPlatReleasePayAmount && null != listPlatReleasePayAmount.get(0)) {
                        platReleasePayAmount = listPlatReleasePayAmount.get(0).setScale(0, BigDecimal.ROUND_DOWN) + "";
                    }
                    RedisUtil.set("financial_platReleaseNotPayAmount", platReleaseNotPayAmount, 0);
                    RedisUtil.set("financial_platReleasePayAmount", platReleasePayAmount, 0);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }
                run_index++;
            }

            /*投资产品信息*/
            if (run_index == 9) {
                try {
                    sql = "select proState, proAmount, proTotalUser from fin_product order by id asc";
                    log.info("resetProductRedis = " + sql);
                    FinancialProduct financialProduct = (FinancialProduct) Data.GetOne("vip_financial", sql, null, FinancialProduct.class);
                    String proState = "";
                    BigDecimal bdProAmount = BigDecimal.ZERO;
                    int proTotalUser = 0;
                    proState = financialProduct.getProState() + "";
                    bdProAmount = financialProduct.getProAmount();
                    proTotalUser = financialProduct.getProTotalUser();
                    
                    RedisUtil.set("financial_proState", proState, 0);
                    RedisUtil.set("financial_proAmount", bdProAmount + "", 0);
                    RedisUtil.set("financial_proTotalUser", proTotalUser + "", 0);
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }
                run_index++;
            }

            /*超级主节点收益*/
            if (run_index == 10) {
                try {
                    //# 累积。1 初创，2 固定，3 动态   profitBatchNo 0 代表 未分配，非0 代表是分配的批次
                    sql = "select floor(sum(miningAmount)) as profit, sNodeType as type from fin_supernode_mining_detail"
                            + " where sNodeShowFlag = 1 group by sNodeType";
                    log.info("resetProductRedis = " + sql);
                    List<Bean> listNodeTotalProfit = (List<Bean>) Data.Query("vip_financial", sql, null, FinSuperNodeMiningDetail.class);
                    /**
                     * 定义需要的变量
                     * trendsMadeNodeTotalProfit	初创节点总收益
                     * fixedMadeNodeTotalProfit		固定节点总收益
                     * trendsMadeNodeTotalProfit	动态节点总收益
                     */
                    BigDecimal homeMadeNodeTotalProfit = BigDecimal.ZERO;
                    BigDecimal fixedMadeNodeTotalProfit = BigDecimal.ZERO;
                    BigDecimal trendsMadeNodeTotalProfit = BigDecimal.ZERO;
                    if (null != listNodeTotalProfit && listNodeTotalProfit.size() > 0) {
                        FinSuperNodeMiningDetail finSuperNodeMiningDetail = new FinSuperNodeMiningDetail();
                        for (int i = 0; i < listNodeTotalProfit.size(); i++) {
                             finSuperNodeMiningDetail = (FinSuperNodeMiningDetail) listNodeTotalProfit.get(i);
                            if (finSuperNodeMiningDetail.getType() == 1) {
                                //初创累计

                                homeMadeNodeTotalProfit = finSuperNodeMiningDetail.getProfit();
                                if(homeMadeNodeTotalProfit!=null){
                                    RedisUtil.set("fin_homeMadeNodeTotalProfit", homeMadeNodeTotalProfit + "", 0);

                                }
                            } else if (finSuperNodeMiningDetail.getType() == 2) {
                                //固定累计
                                fixedMadeNodeTotalProfit = finSuperNodeMiningDetail.getProfit();
                                if(fixedMadeNodeTotalProfit!=null){
                                    RedisUtil.set("fin_fixedMadeNodeTotalProfit", fixedMadeNodeTotalProfit + "", 0);

                                }
                            } else {
                                //动态累计
                                trendsMadeNodeTotalProfit = finSuperNodeMiningDetail.getProfit();
                                if(trendsMadeNodeTotalProfit!=null){
                                    RedisUtil.set("fin_trendsMadeNodeTotalProfit", trendsMadeNodeTotalProfit + "", 0);
                                }
                            }
                        }
                    }

                    //# 已发放。1 初创，2 固定，3 动态
                    sql = "select floor(sum(miningAmount)) as profit , sNodeType as type from fin_supernode_mining_detail"
                            + " where profitBatchNo != 0 and sNodeShowFlag = 1 group by sNodeType";
                    log.info("sql = " + sql);
                    List<Bean> fixedList = (List<Bean>) Data.Query("vip_financial", sql, null, FinSuperNodeMiningDetail.class);
                    BigDecimal homeMadeNodePayProfit = BigDecimal.ZERO;
                    BigDecimal fixedMadeNodePayProfit = BigDecimal.ZERO;
                    BigDecimal trendsMadeNodePayProfit = BigDecimal.ZERO;
                    if (fixedList != null && fixedList.size() > 0) {
                        FinSuperNodeMiningDetail madeNodeDetail = new FinSuperNodeMiningDetail();
                        for (int i = 0; i < fixedList.size(); i++) {
                            madeNodeDetail = (FinSuperNodeMiningDetail) fixedList.get(i);
                            if (madeNodeDetail.getType() == 1) {
                                //初创已发放
                                homeMadeNodePayProfit = madeNodeDetail.getProfit();
                               if(homeMadeNodePayProfit!=null){
                                   RedisUtil.set("fin_homeMadeNodePayProfit", homeMadeNodePayProfit + "", 0);

                               }
                            } else if (madeNodeDetail.getType() == 2) {
                                //固态已发放
                                fixedMadeNodePayProfit = madeNodeDetail.getProfit();
                                if(fixedMadeNodePayProfit!=null){
                                    RedisUtil.set("fin_fixedMadeNodePayProfit", fixedMadeNodePayProfit + "", 0);

                                }
                            } else {
                                //动态已发放
                                trendsMadeNodePayProfit = madeNodeDetail.getProfit();
                                if(trendsMadeNodePayProfit!=null){
                                    RedisUtil.set("fin_trendsMadeNodePayProfit", trendsMadeNodePayProfit + "", 0);

                                }
                            }
                        }
                    }

//
                    //# 统计 节点数量 sNodeShowFlag 显示给用户
                    sql = "select sNodeType, count(*) cnt from fin_supernode where sNodeShowFlag = 1 group by sNodeType";
                    log.info("sql = " + sql);
                    List<Bean> trendsList = (List<Bean>) Data.Query("vip_financial", sql, null, FinSuperNode.class);
                    long homeMadeNodeShowNum = 0L;
                    long fixedMadeNodeShowNum = 0L;
                    long trendsMadeNodeShowNum = 0L;

                    if (trendsList != null && trendsList.size() > 0) {
                        FinSuperNode trendsDetail = new FinSuperNode();
                        for (int i = 0; i < trendsList.size(); i++) {
                             trendsDetail = (FinSuperNode) trendsList.get(i);
                            if (trendsDetail.getsNodeType() == 1) {
                                //初创
                                homeMadeNodeShowNum = trendsDetail.getCnt();
                                   RedisUtil.set("fin_homeMadeNodeShowNum", homeMadeNodeShowNum + "", 0);
                            } else if (trendsDetail.getsNodeType() == 2) {
                                //固定
                                fixedMadeNodeShowNum = trendsDetail.getCnt();
                                    RedisUtil.set("fin_fixedMadeNodeShowNum", fixedMadeNodeShowNum + "", 0);
                            } else {
                                //动态
                                trendsMadeNodeShowNum = trendsDetail.getCnt();
                                  RedisUtil.set("fin_trendsMadeNodeShowNum", trendsMadeNodeShowNum + "", 0);
                            }
                        }

                    }
                } catch (Exception e) {
                    log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
                }
                run_index++;
            }

        } catch (Exception e) {
            log.info("理财报警REWARDERROR: SetPlatEcologySystemWork", e);
        } finally {
            run_index = 1;
        }


    }


    public static void main(String[] args) {
        new SetPlatEcologySystemWork("", "").run();

        String financial_platEcologySystemAmount = RedisUtil.get("financial_platEcologySystemAmount");
        System.out.println("financial_platEcologySystemAmount:" + financial_platEcologySystemAmount);
        String financial_platEcologySystemAmountUsdt = RedisUtil.get("financial_platEcologySystemAmountUsdt");
        System.out.println("financial_platEcologySystemAmountUsdt:" + financial_platEcologySystemAmountUsdt);
        String financial_platSuperNodePayAmount = RedisUtil.get("financial_platSuperNodePayAmount");
        System.out.println("financial_platSuperNodePayAmount:" + financial_platSuperNodePayAmount);
        String financial_platSuperNodePayAmountUsdt = RedisUtil.get("financial_platSuperNodePayAmountUsdt");
        System.out.println("financial_platSuperNodePayAmountUsdt:" + financial_platSuperNodePayAmountUsdt);
        String financial_platSuperNodeNotPayAmount = RedisUtil.get("financial_platSuperNodeNotPayAmount");
        System.out.println("financial_platSuperNodeNotPayAmount:" + financial_platSuperNodeNotPayAmount);
        String financial_platSuperNodeNotPayAmountUsdt = RedisUtil.get("financial_platSuperNodeNotPayAmountUsdt");
        System.out.println("financial_platSuperNodeNotPayAmountUsdt:" + financial_platSuperNodeNotPayAmountUsdt);
        String financial_platNewVipWeekAmount = RedisUtil.get("financial_platNewVipWeekAmount");
        System.out.println("financial_platNewVipWeekAmount:" + financial_platNewVipWeekAmount);
        String financial_platNewVipWeekAmountUsdt = RedisUtil.get("financial_platNewVipWeekAmountUsdt");
        System.out.println("financial_platNewVipWeekAmountUsdt:" + financial_platNewVipWeekAmountUsdt);
        String financial_platLeaderBonusAmount = RedisUtil.get("financial_platLeaderBonusAmount");
        System.out.println("financial_platLeaderBonusAmount:" + financial_platLeaderBonusAmount);
        String financial_platLeaderBonusAmountUsdt = RedisUtil.get("financial_platLeaderBonusAmountUsdt");
        System.out.println("financial_platLeaderBonusAmountUsdt:" + financial_platLeaderBonusAmountUsdt);

        System.out.println("financial_proTotalUser:" + RedisUtil.get("financial_proTotalUser"));
        System.out.println("financial_proTotalAmount:" + RedisUtil.get("financial_proTotalAmount"));
        System.out.println("financial_sumInvestUsdtAmount:" + RedisUtil.get("financial_sumInvestUsdtAmount"));
        System.out.println("financial_sumExpectProfitUsdt:" + RedisUtil.get("financial_sumExpectProfitUsdt"));
        System.out.println("financial_platLeaderBonusWeight:" + RedisUtil.get("financial_platLeaderBonusWeight"));
        System.out.println("financial_profitWeightTotal:" + RedisUtil.get("financial_profitWeightTotal"));
        System.out.println("financial_bonusTime:" + RedisUtil.get("financial_bonusTime"));

    }
}
