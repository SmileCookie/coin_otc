package com.world.model.quanttrade.dao;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.record.TransRecord;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buxianguan on 17/8/8.
 */
@SuppressWarnings("serial")
public class TransRecordDao extends DataDaoSupport<TransRecord> {
    public TransRecordDao() {
        super();
    }

    /**
     * 获取某人买或者卖的交易记录
     */
    public List<TransRecord> getUserTrans(String userId, long startTime, long endTime) {
        String sql = "select * from transrecord where (userIdBuy = ? or userIdSell = ?) and times>=? and times<? ";
        List<TransRecord> list = super.find(sql, new Object[]{userId, userId, startTime, endTime}, TransRecord.class);
        return list;
    }

    /**
     * 获取某人不是跟自己成交的交易记录
     */
    public List<TransRecord> getNotSelfTrans(String userId, long startTime, long endTime) {
        String sql = "select * from transrecord where ((userIdBuy = ? and userIdSell != ?) or (userIdBuy != ? and userIdSell = ?)) and times>=? and times<?";
        List<TransRecord> list = super.find(sql, new Object[]{userId, userId, userId, userId, startTime, endTime}, TransRecord.class);
        return list;
    }

    /**
     * 获取不是某一些人成交的交易记录，用于刷量启用多个账号的监控
     */
    public List<TransRecord> getNotAllSelfTrans(String userIds, long startTime, long endTime) {
        String sql = "select * from transrecord where ((userIdBuy in (" + userIds +
                ") and userIdSell not in (" + userIds +
                ") ) or (userIdBuy not in (" + userIds +
                ") and userIdSell in (" + userIds +
                ") )) and times>=? and times<?";
        List<TransRecord> list = super.find(sql, new Object[]{startTime, endTime}, TransRecord.class);
        return list;
    }

    public List<TransRecord> getNoTwoUserTrans(String userId1, String userId2, long startTime, long endTime) {
        String sql = "select * from transrecord where ((userIdBuy = ? and userIdSell != ?) or (userIdSell = ? and userIdBuy != ?)) and times>=? and times<?";
        List<TransRecord> list = super.find(sql, new Object[]{userId1, userId2, userId1, userId2, startTime, endTime}, TransRecord.class);
        return list;
    }

    public List<TransRecord> getTransRecordByEntrust(long entrustIdBuy, String dbName) {
        String sql = "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord WHERE entrustIdBuy=? union all " +
                "SELECT transRecordId,unitPrice,totalPrice,numbers,TYPES,times FROM transrecord_all WHERE entrustIdBuy=? ";

        List<TransRecord> lists = Data.QueryT(dbName, sql, new Object[]{entrustIdBuy, entrustIdBuy}, TransRecord.class);
        if (CollectionUtils.isEmpty(lists)) {
            return new ArrayList<>();
        }
        return lists;
    }

}
