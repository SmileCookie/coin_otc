package com.world.model.entity.recharge;

import com.world.data.mysql.Bean;
import org.beetl.sql.core.annotatoin.Table;

/*
 *
 * gen by beetlsql 2019-01-21
 */
@Table(name = "charge_management")
public class ChargeManagement extends Bean {

    public ChargeManagement(Integer id, Integer fundstype, Integer recharge, Integer withdraw, String fundstypename) {
        this.id = id;
        this.fundstype = fundstype;
        this.recharge = recharge;
        this.withdraw = withdraw;
        this.fundstypename = fundstypename;
    }

    //主键
    private Integer id ;
    //币种
    private Integer fundstype ;
    //充值  0：关  1：开
    private Integer recharge ;
    //提现  0：关  1：开
    private Integer withdraw ;
    //是否展示，1-展示，0-不展示
    private Integer display ;

    private String fundstypename;

    public ChargeManagement() {
            super();
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

    /**币种
     *@return
     */
    public Integer getFundstype(){
        return  fundstype;
    }
    /**币种
     *@param  fundstype
     */
    public void setFundstype(Integer fundstype ){
        this.fundstype = fundstype;
    }

    /**充值  0：关  1：开
     *@return
     */
    public Integer getRecharge(){
        return  recharge;
    }
    /**充值  0：关  1：开
     *@param  recharge
     */
    public void setRecharge(Integer recharge ){
        this.recharge = recharge;
    }

    /**提现  0：关  1：开
     *@return
     */
    public Integer getWithdraw(){
        return  withdraw;
    }
    /**提现  0：关  1：开
     *@param  withdraw
     */
    public void setWithdraw(Integer withdraw ){
        this.withdraw = withdraw;
    }

    public String getFundstypename() {
        return fundstypename;
    }

    public void setFundstypename(String fundstypename) {
        this.fundstypename = fundstypename;
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }
}