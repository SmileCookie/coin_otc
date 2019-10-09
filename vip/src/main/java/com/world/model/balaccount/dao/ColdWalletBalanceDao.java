package com.world.model.balaccount.dao;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.balaccount.entity.ColdWalletBalanceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ColdWalletBalanceDao
 * @Author hunter
 * @Date 2019-05-27 14:49
 * @Version v1.0.0
 * @Description
 */
public class ColdWalletBalanceDao extends DataDaoSupport<ColdWalletBalanceBean> {

    private static final long serialVersionUID = 1L;

    Logger logger = LoggerFactory.getLogger(ColdWalletBalanceDao.class);


    public void insertOrUpdateBalance(Map<Integer, BigDecimal> balanceMap){
        //先进行update，如果数据库不存在记录，更新失败进行插入操作
        for(Integer fundsType:balanceMap.keySet()){
            try {
                logger.info("fundstype={},balance={}",fundsType,balanceMap.get(fundsType));
                String updateSql = "update coldWalletBalance set balance = "+balanceMap.get(fundsType).toPlainString()+",updateDate= now() where fundsType = "+fundsType;
                int rtn = super.update(updateSql,null);
                if(rtn<=0){
                    String insertSql = "INSERT INTO `vip_main`.`coldwalletbalance`(`fundsType`, `balance`, `updateDate`) VALUES ("+fundsType+", "+balanceMap.get(fundsType).toPlainString()+", now());";
                    Data.Insert(insertSql, null);
                }
            } catch (Exception e) {
                logger.error("ColdWalletBalanceDao.insertOrUpdateBalance发生错误",e);
            }
        }
    }

    public List<ColdWalletBalanceBean> findAllColdWalletBalance(){

        String sql = "select * from coldWalletBalance limit 0,100";

        List<ColdWalletBalanceBean> list = super.find(sql,null,ColdWalletBalanceBean.class);

        return list;
    }

}
