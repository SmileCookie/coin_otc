package com.world.model.jifenmanage;

import com.alibaba.fastjson.JSONObject;
import com.world.constant.Const;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.entity.level.IntegralRule;
import com.world.model.entity.usercap.dao.CommAttrDao;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.model.jifenmanage.thread.JifenManageThread;
import com.world.model.loan.worker.LoanAutoFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xie on 2017/6/26.
 */
public class JifenManage extends Thread{

    private Logger log = Logger.getLogger(JifenManage.class.getName());

    private IntegralRuleDao ruleDao = new IntegralRuleDao();
    private CommAttrDao commAttrDao = new CommAttrDao();

    private String userId;      //用户ID
    private int seqNo;          //积分类型序号
    private BigDecimal amount;  //资金数量
    private String coint;        //币种
    private String systemName;  //系统名称
    private String modularName = "JFGL";    //模块名称
//    private SysLogDao sysLogDao = new SysLogDao();
    public JifenManage(String userId, int seqNo, BigDecimal amount,String coint, String systemName) {
        this.userId = userId;
        this.seqNo = seqNo;
        this.amount = amount;
        this.coint = coint;
        this.systemName = systemName;
    }

    @Override
    public void run() {
        dealJifenRules();
    }

    public void dealJifenRules() {
        try{

            long time = System.currentTimeMillis();
            String logUserId = "admin";
            String logUserName = "admin";
            IntegralRule rule = ruleDao.getBySeqNo(seqNo);
            String typeCode = rule.getTypeCode();
            String logContent =  "处理用户：" + userId + "的" + rule.getType() + "积分开始";
            String remark = "";
            String logId = systemName + "-" + modularName + "-" + typeCode + "-" + time;
            String batchId = systemName + "-" + modularName + "-" + typeCode + "-" + userId + time;
            int modularType = 1;  //1：普通方法，2：定时任务（监控）

            log.info(logContent);
            /*start by zhushuguo 20190322 过滤刷量账户*/
            //过滤数量用户
            List<CommAttrBean> commAttrBeans = commAttrDao.getCommAttrList(Const.BRUSH_USER);
            if(!CollectionUtils.isEmpty(commAttrBeans)){
                for(CommAttrBean commAttrBean : commAttrBeans){
                    if(userId.equals(commAttrBean.getParaValue())){
                        log.info("刷量账号不添加积分。");
                        return;
                    }
                }
            }
            /*end*/
            /**
             * 如果是其他币种，则折算成usd进行
             * 20170911
             * modify by xwz
             */

            if(StringUtils.isNotEmpty(coint) && !"usdt".equals(coint.toLowerCase()) && amount != null){
                JSONObject prices = LoanAutoFactory.getPrices();
                BigDecimal price = new BigDecimal("0");
                try{
                    if(prices.containsKey(coint.toLowerCase() + "_usdt")){//usd有该币种
                        price = new BigDecimal(prices.get(coint.toLowerCase() + "_usdt").toString());
                        amount = price.multiply(amount);
                    }else if(prices.containsKey(coint.toLowerCase() + "_btc")){
                        price = new BigDecimal(prices.get(coint.toLowerCase() + "_btc").toString());
                        price = new BigDecimal(prices.get("btc_usdt").toString()).multiply(price);
                        amount = price.multiply(amount);
                    }else{
                        log.info("币种市场没有该币种价格，币种:" + coint);
                        return;
                    }
                }catch(Exception e){
                    log.error("币种折算usdt异常！", e);
                    return;
                }

//                if(prices.containsKey(coint)){
//                    amount = prices.getBigDecimal(coint).multiply(amount);
//                }else if(!"btc".equals(coint)){
//                    log.error("币种市场没有该币种价格");
//                    //记录积分日志
////                    sysLogDao.addSysLog(userId, logId, batchId, systemName, modularName,modularType, logContent, logUserId,logUserName,"币种市场没有该币种价格");
//                    return;
//                }
            }
            /**start by gkl**修改日常充值的积分等级20190509**/
            if(null != amount){
                amount = amount.divide(new BigDecimal("100"),0,BigDecimal.ROUND_DOWN);
            }
            /**end**/
            if(12 != seqNo && 13 != seqNo ) {

                //记录积分日志
//                sysLogDao.addSysLog(userId, logId, batchId, systemName, modularName,modularType, logContent, logUserId,logUserName,remark);
                //如果币种不是btc，则转成btc价格
                JifenManageThread jifenManageThread = new JifenManageThread(userId, seqNo, amount, logId, batchId, systemName, modularName, modularType, logUserId, logUserName);
                jifenManageThread.addJifen();
            }

        }catch(Exception e){
            log.error("处理积分出错：" + e.toString(), e);
        }
    }
}
