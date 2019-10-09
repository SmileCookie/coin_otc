package com.world.model.entity.mars;

import java.math.BigDecimal;

public class CheckVo {
    private Boolean flag = false;
    private BigDecimal account;

    public Boolean getFlag() {
        return flag;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public BigDecimal getAccount() {
        return account;
    }

    public void setAccount(BigDecimal account) {
        this.account = account;
    }
}
