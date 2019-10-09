package com.world.model.ico.dao;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.ico.entity.ICOUserExchangeNum;

import java.math.BigDecimal;

/**
 * Created by xie on 2017/7/12.
 */
public class ICOUserExchangeNumDao extends DataDaoSupport< ICOUserExchangeNum> {


    /**
     * 判断用户配售或者申购权限
     * @param userId
     * @return
     */
    public boolean isHasPower(String userId, int saleType){
        ICOUserExchangeNum userExchangeNum = getUserExchangeBean(userId,saleType);
        if(userExchangeNum == null){
            return false;
        }
        return true;
    }

    public ICOUserExchangeNum getICOUserExchangeNum(String userId, int saleType){
        return getUserExchangeBean(userId,saleType);
    }

    /**
     * 根据UserId和saleType获取唯一对象
     * @param userId
     * @param saleType
     * @return
     */
    public ICOUserExchangeNum getUserExchangeBean(String userId, int saleType) {
        ICOUserExchangeNum icoUserExchangeNum = ( ICOUserExchangeNum) Data.GetOne("SELECT * FROM icouserexchangeNum where userId = ? and saleType = ? ", new Object[] { userId, saleType },  ICOUserExchangeNum.class);
        return icoUserExchangeNum;
    }

    private String userId;          //用户编号
    private int saleType;           //发售类型(1配售,2申购)
    private BigDecimal totalNum;


    /**
     * 插入ICOUserExchangeNum
     * @param userId
     * @param saleType
     * @param totalNum
     * @return
     */
    public boolean insertICOUserExchangeNum(String userId,int saleType,int totalNum) {
       try{
           Data.Update("insert into ICOUserExchangeNum (userId, saleType, totalNum) values(?, ?, ?)",
                   new Object[]{userId, saleType, totalNum});
       } catch(Exception e){
           log.info("分配申购权限失败，userId:" + userId + "，saleType：" + saleType);
           return false;
       }
       return false;
    }



    /**
     * 更新ICOUserExchangeNum log.info("分配申购权限失败，userId:" + userId + "，saleType：" + saleType);
     * @param userId
     * @param saleType
     * @param totalNum
     * @return
     */
    public boolean updateICOUserExchangeNum(String userId,int saleType,BigDecimal totalNum) {
        try{
            Data.Update("update ICOUserExchangeNum set totalNum = totalNum + ? where userId = ? and saleType = ?",
                    new Object[]{totalNum, userId, saleType});
        } catch(Exception e){
            log.info("更新申购权限失败，userId:" + userId + "，saleType：" + saleType);
            return false;
        }
        return true;
    }

}
