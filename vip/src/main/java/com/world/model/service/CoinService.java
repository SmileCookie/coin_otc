package com.world.model.service;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;

import com.world.data.mysql.Data;
import com.world.model.dao.user.UserLoginIpDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.enums.CoinChargeStatus;
import com.world.model.enums.CoinDownloadStatus;

/**
 * Created by micheal on 2016/12/19.
 */
public class CoinService {

    UserLoginIpDao userLoginIpDao = new UserLoginIpDao();

    static Logger logger = Logger.getLogger(CoinService.class.getName());
    /**
     *
     * @param coinType 除了人民币
     * @param startTime
     * @param endTime
     * @return
     */
    public List findChargeSucc(CoinProps coint, Timestamp startTime, Timestamp endTime){
        StringBuilder querySql = new StringBuilder();
        querySql.append("select ")
                .append("detailsId").append(" recordId, ")
                .append("amount, configTime changeTime, opUnique merchantsSyncId from ")
                .append(coint.getStag()).append("details where status=?")
                .append(" and ").append("type").append("=1")
                .append(" and configTime<=?");

        Object[] params = null;
        if(startTime == null){
            params = new Object[]{CoinChargeStatus.SUCCESS.getKey(), endTime};
        }else{
            querySql.append(" and configTime>=?");
            params = new Object[]{CoinChargeStatus.SUCCESS.getKey(), endTime, startTime};
        }

        return Data.Query(querySql.toString(), params, CoinChangeDetails.class);
    }

    /**
     *
     * @param userId
     * @return
     */
    private List findChargeSuccAmount(long userId, CoinProps coint){
        StringBuilder querySql = new StringBuilder();
        querySql.append("select amount from ")
                .append(coint.getStag()).append("details where userId=?")
                .append(" and status=?")
                .append(" and ").append("type").append("=1");

        return Data.Query(querySql.toString(), new Object[]{userId+"", CoinChargeStatus.SUCCESS.getKey()}, CoinChangeDetails.class);
    }

    /**
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public List findWithdrawSucc(CoinProps coint, Timestamp startTime, Timestamp endTime){
        StringBuilder querySql = new StringBuilder();
        querySql.append("select ")
                .append("id, ")
                .append("amount, realFee fees, manageTime changeTime, merchantOrderNo from ")
                .append(coint.getStag()).append("download where status=?")
                .append(" and manageTime<=?");

        Object[] params = null;
        if(startTime == null){
            params = new Object[]{CoinDownloadStatus.SUCCESS.getKey(), endTime};
        }else{
            querySql.append(" and manageTime>=?");
            params = new Object[]{CoinDownloadStatus.SUCCESS.getKey(), endTime, startTime};
        }

        return Data.Query(querySql.toString(), params, CoinChangeDetails.class);
    }

    private List findWithdrawSuccAmount(long userId, CoinProps coint){
        StringBuilder querySql = new StringBuilder();
        querySql.append("select amount from ")
                .append(coint.getStag()).append("download")
                .append(" where userId=? and status=?");
        return Data.Query(querySql.toString(), new Object[]{userId+"", CoinDownloadStatus.SUCCESS.getKey()}, CoinChangeDetails.class);
    }

}
