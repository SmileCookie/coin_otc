package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

public class FinEcoRewardAssign extends Bean {

    private static final long serialVersionUID = 1L;
    //主键
    private Integer id;
    //Vds生态Userid
    private Integer ecoUserid;
    //Vds生态Vid
    private Integer ecoVid;
    //状态：0未结算 1已结算
    private Integer billstatus;
    //投资编号-投资表主键
    private Integer investid;
    //分配人userid
    private Integer assignUserid;
    //投资金额
    private BigDecimal investamount;
    //分配人Vid
    private String assignVid;
    //本次投资分配金额
    private BigDecimal profitamount;
    //记录创建时间
    private Date createtime;

    /**
     * 主键
     *
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }


    /**
     * 状态：0未结算 1已结算
     *
     * @return
     */
    public Integer getBillstatus() {
        return billstatus;
    }

    /**
     * 状态：0未结算 1已结算
     *
     * @param billstatus
     */
    public void setBillstatus(Integer billstatus) {
        this.billstatus = billstatus;
    }

    /**
     * 投资编号-投资表主键
     *
     * @return
     */
    public Integer getInvestid() {
        return investid;
    }

    /**
     * 投资编号-投资表主键
     *
     * @param investid
     */
    public void setInvestid(Integer investid) {
        this.investid = investid;
    }


    /**
     * 投资金额
     *
     * @return
     */
    public BigDecimal getInvestamount() {
        return investamount;
    }

    /**
     * 投资金额
     *
     * @param investamount
     */
    public void setInvestamount(BigDecimal investamount) {
        this.investamount = investamount;
    }


    /**
     * 本次投资分配金额
     *
     * @return
     */
    public BigDecimal getProfitamount() {
        return profitamount;
    }

    /**
     * 本次投资分配金额
     *
     * @param profitamount
     */
    public void setProfitamount(BigDecimal profitamount) {
        this.profitamount = profitamount;
    }

    /**
     * 记录创建时间
     *
     * @return
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * 记录创建时间
     *
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Integer getEcoUserid() {
        return ecoUserid;
    }

    public void setEcoUserid(Integer ecoUserid) {
        this.ecoUserid = ecoUserid;
    }

    public Integer getEcoVid() {
        return ecoVid;
    }

    public void setEcoVid(Integer ecoVid) {
        this.ecoVid = ecoVid;
    }

    public Integer getAssignUserid() {
        return assignUserid;
    }

    public void setAssignUserid(Integer assignUserid) {
        this.assignUserid = assignUserid;
    }

    public String getAssignVid() {
        return assignVid;
    }

    public void setAssignVid(String assignVid) {
        this.assignVid = assignVid;
    }
}
