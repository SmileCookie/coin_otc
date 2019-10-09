package com.world.model.backcapital.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.backcapital.BcEntrustTransRecord;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author buxianguan
 * @date 2017/11/15
 */
public class EntrustRecordDao extends DataDaoSupport<BcEntrustTransRecord> {

    public List<BcEntrustTransRecord> getEntrustPrice() {
        String sql = "select distinct(batchId),entrustId,completeTotalMoney from bcentrusttransrecord where entrustId>=0 order by batchId desc limit 20";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public List<BcEntrustTransRecord> getUserEntrusts(long userId, int pageSize) {
        String sql = "select distinct(entrustId),completeTotalMoney,completeNumber,entrustTime from bcentrusttransrecord where userId=? and entrustId>0 order by entrustTime desc limit ?";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{userId, pageSize}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public List<BcEntrustTransRecord> getUserEntrustsByEntrustId(long userId, long entrustId, int pageSize) {
        String sql = "select distinct(entrustId),completeTotalMoney,completeNumber,entrustTime from bcentrusttransrecord where userId=? and entrustId<? and entrustId>0 order by entrustTime desc limit ?";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{userId, entrustId, pageSize}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public List<BcEntrustTransRecord> getEntrustRecordsByEntrustId(long entrustId) {
        String sql = "select userId,transRecordId,transRecordTime,market,amount,feeRatio from bcentrusttransrecord where entrustId=? order by transRecordTime desc";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{entrustId}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

    public void updateEntrustByEntrustId(long entrustId, BigDecimal completeTotalMoney, BigDecimal completeNumber, long entrustTime) {
        String sql = "update bcentrusttransrecord set completeTotalMoney=?, completeNumber=?, entrustTime=? where entrustId=? ";
        super.update(sql, new Object[]{completeTotalMoney, completeNumber, entrustTime, entrustId});
    }

    public BcEntrustTransRecord getLastEntrust() {
        String sql = "select completeNumber, entrustTime from bcentrusttransrecord order by id desc limit 1";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public long getFirstEntrustTime() {
        String sql = "select entrustTime from bcentrusttransrecord order by id asc limit 1";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return list.get(0).getEntrustTime();
    }

    public boolean isHaveBeforeTime(long entrustTime) {
        String sql = "select id from bcentrusttransrecord where entrustTime<? limit 1";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{entrustTime}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        return true;
    }

    public BigDecimal getTotalNumbersByTime(long entrustTime) {
        String sql = "select IFNULL(sum(completeNumber),0) as completeNumber from (select distinct(batchId), completeNumber from bcentrusttransrecord where entrustTime>=?)a";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{entrustTime}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.get(0).getCompleteNumber();
    }

    public BigDecimal getLast(long entrustTime) {
        String sql = "select completeTotalMoney from bcentrusttransrecord where entrustId>0 order by id desc limit 1";
        List<BcEntrustTransRecord> list = super.find(sql, new Object[]{entrustTime}, BcEntrustTransRecord.class);
        if (CollectionUtils.isEmpty(list)) {
            return BigDecimal.ZERO;
        }
        return list.get(0).getCompleteTotalMoney();
    }
}
