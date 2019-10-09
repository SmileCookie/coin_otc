package com.world.model.entity.statisticalReport;

import com.world.data.mysql.Bean;

import java.util.Date;

public class AliveUserCount extends Bean {
    private int pvCount;
    private int uvCount;
    private Long ipCount;
    private Long registerCount;
    private Long allRegisterCount;
    private Long loginCount;
    private String loginRate;
    private String RegistrationConversionRate;
    private Date countDate;

    public String getLoginRate() {
        return loginRate;
    }

    public void setLoginRate(String loginRate) {
        this.loginRate = loginRate;
    }

    public String getRegistrationConversionRate() {
        return RegistrationConversionRate;
    }

    public void setRegistrationConversionRate(String registrationConversionRate) {
        RegistrationConversionRate = registrationConversionRate;
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

    public Long getIpCount() {
        return ipCount;
    }

    public void setIpCount(Long ipCount) {
        this.ipCount = ipCount;
    }

    public Long getRegisterCount() {
        return registerCount;
    }

    public void setRegisterCount(Long registerCount) {
        this.registerCount = registerCount;
    }

    public Long getAllRegisterCount() {
        return allRegisterCount;
    }

    public void setAllRegisterCount(Long allRegisterCount) {
        this.allRegisterCount = allRegisterCount;
    }

    public Long getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Long loginCount) {
        this.loginCount = loginCount;
    }

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }
}
