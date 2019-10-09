package com.world.model.entity.autodownload;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by xie on 2017/10/17.
 * 自动打币记录
 */
public class AutoDownloadRecordBean extends Bean {
//    autoDownloadRecords.batchId,
//    autoDownloadRecords.id,
//    autoDownloadRecords.downloadId,
//    autoDownloadRecords.userName,
//    autoDownloadRecords.amount,
//    autoDownloadRecords.submitTime,
//    autoDownloadRecords.createTime

    private long id;
    private String batchId;
    private String downloadId;
    private String userId;
    private String userName;
    private int fundsType;
    private Timestamp submitTime;
    private Timestamp createTime;
    private BigDecimal amount;
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getFundsType() {
        return fundsType;
    }

    public void setFundsType(int fundsType) {
        this.fundsType = fundsType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Timestamp submitTime) {
        this.submitTime = submitTime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
