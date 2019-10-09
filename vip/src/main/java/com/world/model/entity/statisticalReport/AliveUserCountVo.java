package com.world.model.entity.statisticalReport;

import com.world.data.mysql.Bean;

import java.util.Date;

public class AliveUserCountVo extends Bean {
    private int id;
    private int pvCount;
    private int uvCount;
    private int ipCount;
    private int registerCount;
    private int allRegisterCount;
    private int loginCount;
    private String loginRate;
    private String registrationConversionRate;
    private Date countDate;
    private int accessingIp;


    public int getAccessingIp() {
        return accessingIp;
    }

    public void setAccessingIp(int accessingIp) {
        this.accessingIp = accessingIp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoginRate() {
        return loginRate;
    }

    public void setLoginRate(String loginRate) {
        this.loginRate = loginRate;
    }

    public String getRegistrationConversionRate() {
        return registrationConversionRate;
    }

    public void setRegistrationConversionRate(String registrationConversionRate) {
        this.registrationConversionRate = registrationConversionRate;
    }

    public int getPvCount() {
        return pvCount;
    }

    public void setPvCount(int pvCount) {
        this.pvCount = pvCount;
    }

    public int getUvCount() {
        return uvCount;
    }

    public void setUvCount(int uvCount) {
        this.uvCount = uvCount;
    }

    public int getIpCount() {
        return ipCount;
    }

    public void setIpCount(int ipCount) {
        this.ipCount = ipCount;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(int registerCount) {
        this.registerCount = registerCount;
    }

    public int getAllRegisterCount() {
        return allRegisterCount;
    }

    public void setAllRegisterCount(int allRegisterCount) {
        this.allRegisterCount = allRegisterCount;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }
}
