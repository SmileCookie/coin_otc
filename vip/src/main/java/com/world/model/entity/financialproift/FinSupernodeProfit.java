package com.world.model.entity.financialproift;

import com.world.data.mysql.Bean;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Ethan
 * @Date 2019-07-26 10:07
 * @Description
 **/

public class FinSupernodeProfit extends Bean {
    private static final long serialVersionUID = 1L;

    //主键
    private Integer id ;
    //本次分配人数
    private Integer profitusers ;
    //收益分配状态 1已分配 0未分配
    private Integer status ;
    //产出累计金额
    private BigDecimal balance ;
    //上次产出累计金额
    private BigDecimal balancepre ;
    //实际分配金额   profitoriginal*0.98
    private BigDecimal profit ;
    //原始分配金额
    private BigDecimal profitoriginal ;
    //每人分配usdt数量
    private BigDecimal usdtamountPeruser ;
    //vds转usdt价格
    private BigDecimal usdtprice ;
    //每人分配vds数量
    private BigDecimal vdsamountPeruser ;
    //创建时间
    private Date profittime ;
    //更新时间
    private Date updatetime ;
    //本次分配的笔数
    private Integer superNodeProfitCount;

    public FinSupernodeProfit() {
    }

    /**主键
     *@return
     */
    public Integer getId(){
        return  id;
    }
    /**主键
     *@param  id
     */
    public void setId(Integer id ){
        this.id = id;
    }

    /**本次分配人数
     *@return
     */
    public Integer getProfitusers(){
        return  profitusers;
    }
    /**本次分配人数
     *@param  profitusers
     */
    public void setProfitusers(Integer profitusers ){
        this.profitusers = profitusers;
    }

    /**收益分配状态 1已分配 0未分配
     *@return
     */
    public Integer getStatus(){
        return  status;
    }
    /**收益分配状态 1已分配 0未分配
     *@param  status
     */
    public void setStatus(Integer status ){
        this.status = status;
    }

    /**产出累计金额
     *@return
     */
    public BigDecimal getBalance(){
        return  balance;
    }
    /**产出累计金额
     *@param  balance
     */
    public void setBalance(BigDecimal balance ){
        this.balance = balance;
    }

    /**上次产出累计金额
     *@return
     */
    public BigDecimal getBalancepre(){
        return  balancepre;
    }
    /**上次产出累计金额
     *@param  balancepre
     */
    public void setBalancepre(BigDecimal balancepre ){
        this.balancepre = balancepre;
    }

    /**实际分配金额   profitoriginal*0.98
     *@return
     */
    public BigDecimal getProfit(){
        return  profit;
    }
    /**实际分配金额   profitoriginal*0.98
     *@param  profit
     */
    public void setProfit(BigDecimal profit ){
        this.profit = profit;
    }

    /**原始分配金额
     *@return
     */
    public BigDecimal getProfitoriginal(){
        return  profitoriginal;
    }
    /**原始分配金额
     *@param  profitoriginal
     */
    public void setProfitoriginal(BigDecimal profitoriginal ){
        this.profitoriginal = profitoriginal;
    }

    /**每人分配usdt数量
     *@return
     */
    public BigDecimal getUsdtamountPeruser(){
        return  usdtamountPeruser;
    }
    /**每人分配usdt数量
     *@param  usdtamountPeruser
     */
    public void setUsdtamountPeruser(BigDecimal usdtamountPeruser ){
        this.usdtamountPeruser = usdtamountPeruser;
    }

    /**vds转usdt价格
     *@return
     */
    public BigDecimal getUsdtprice(){
        return  usdtprice;
    }
    /**vds转usdt价格
     *@param  usdtprice
     */
    public void setUsdtprice(BigDecimal usdtprice ){
        this.usdtprice = usdtprice;
    }

    /**每人分配vds数量
     *@return
     */
    public BigDecimal getVdsamountPeruser(){
        return  vdsamountPeruser;
    }
    /**每人分配vds数量
     *@param  vdsamountPeruser
     */
    public void setVdsamountPeruser(BigDecimal vdsamountPeruser ){
        this.vdsamountPeruser = vdsamountPeruser;
    }

    /**创建时间
     *@return
     */
    public Date getProfittime(){
        return  profittime;
    }
    /**创建时间
     *@param  profittime
     */
    public void setProfittime(Date profittime ){
        this.profittime = profittime;
    }

    /**更新时间
     *@return
     */
    public Date getUpdatetime(){
        return  updatetime;
    }
    /**更新时间
     *@param  updatetime
     */
    public void setUpdatetime(Date updatetime ){
        this.updatetime = updatetime;
    }

    public Integer getSuperNodeProfitCount() {
        return superNodeProfitCount;
    }

    public void setSuperNodeProfitCount(Integer superNodeProfitCount) {
        this.superNodeProfitCount = superNodeProfitCount;
    }
}
