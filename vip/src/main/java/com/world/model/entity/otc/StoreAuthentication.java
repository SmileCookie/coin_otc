package com.world.model.entity.otc;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class StoreAuthentication extends Bean {

    private Long id;

    private Long userId;

    private String userName;

    private Integer type;

    private Integer status;

    private Date sendTime;

    private Date auditTime;

    private Long auditUserId;

    private String auditUserName;

    private BigDecimal storeFreez;

    private String ip;

    private Integer area; //0:未知，1：国内  2：海外用户

    private Integer reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public Long getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(Long auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public BigDecimal getStoreFreez() {
        return storeFreez;
    }

    public void setStoreFreez(BigDecimal storeFreez) {
        this.storeFreez = storeFreez;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getArea() {
        return area;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }
}
