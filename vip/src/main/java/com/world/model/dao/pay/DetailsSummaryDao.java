package com.world.model.dao.pay;

import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DetailsSummaryBean;

@SuppressWarnings("serial")
public class DetailsSummaryDao extends DataDaoSupport<DetailsSummaryBean> {

    public DetailsSummaryDao() {
    }

    /**
     * 保存一条记录
     *
     * @param charge
     * @return
     */
    public OneSql saveOne(DetailsBean charge, int id, int fundsType) {
        return new OneSql("insert into detailssummary (detailsId, fundsType, fromAddr, toAddr, addHash, amount, sendTime, confirmTimes, wallet, status, type,configTime,userId,userName, blockHeight) values (?,?,?,?,?,?,?,?,?, ?, ?,?,?,?,?)",
                1, new Object[]{
                id, fundsType, charge.getFromAddr(), charge.getToAddr(), charge.getAddHash(), charge.getAmount(), charge.getSendTime(), charge.getConfirmTimes(), charge.getWallet(), charge.getStatus(), 1, charge.getConfigTime(), charge.getUserId(), charge.getUserName(), charge.getBlockHeight()}, database);
    }
}
