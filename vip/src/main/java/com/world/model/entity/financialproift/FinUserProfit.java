package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;

/**
 * @Author Ethan
 * @Date 2019-07-27 11:51
 * @Description
 **/

public class FinUserProfit extends Bean {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer userid;
    private BigDecimal percentamount;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public BigDecimal getPercentamount() {
        return percentamount;
    }

    public void setPercentamount(BigDecimal percentamount) {
        this.percentamount = percentamount;
    }
}
