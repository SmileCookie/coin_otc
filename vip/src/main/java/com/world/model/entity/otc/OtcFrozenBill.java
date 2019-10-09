package com.world.model.entity.otc;

import com.world.data.mysql.Bean;
import com.world.model.enums.TradeFrozenType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/8/13上午9:25
 */
public class OtcFrozenBill extends Bean{
    /**
     * 主键
     */
    private BigInteger id;
    /**
     * 流水类型：0-发布广告；1-广告下架 ，2-交易取消， 3-交易成功， 4-出售订单
     */
    private Integer action ;
    /**
     * 流水类型名
     */
    private String actionName ;
    /**
     * 发生额
     */
    private BigDecimal amount ;
    /**
     * 币种类型：otc_coin_type.id
     */
    private BigInteger coinTypeId ;
    /**
     * 冻结余额
     */
    private BigDecimal frozenAmount ;
    /**
     * 本笔流水产生原因
     */
    private String memo ;
    /**
     * 相关数据记录，比如交易id等（恢复数据用）
     */
    private String refer ;
    /**
     * 用户ID
     */
    private BigInteger userId ;
    /**
     * 添加时间
     */
    private Date addTime ;

    private Long time;

    /**
     * 发生额
     */
    private String amountStr;
    /**
     * 小数点
     */
    private Integer coinBixDian;
    /**
     * 当前余额
     */
    private String currAmountStr ;

    /**
     * 当前余额
     */
    private String coinTypeName ;

    /**
     * 流水类型名
     */
    private String actionTypeName ;

    /**
     * 冻结手续费
     */
    private BigDecimal frozenFee ;

    public OtcFrozenBill() {
    }

    public Long getTime() {
        if(addTime != null){
            return addTime.getTime();
        }
        return 0L;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getAction(){
        return  action;
    }

    public void setAction(Integer action ){
        this.action = action;
    }

    public BigDecimal getAmount(){
        return  amount;
    }

    public void setAmount(BigDecimal amount ){
        this.amount = amount;
    }


    public BigDecimal getFrozenAmount(){
        return  frozenAmount;
    }

    public void setFrozenAmount(BigDecimal frozenAmount ){
        this.frozenAmount = frozenAmount;
    }

    public String getMemo(){
        return  memo;
    }

    public void setMemo(String memo ){
        this.memo = memo;
    }

    public String getRefer(){
        return  refer;
    }

    public void setRefer(String refer ){
        this.refer = refer;
    }

    public Date getAddTime(){
        return  addTime;
    }

    public void setAddTime(Date addTime ){
        this.addTime = addTime;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getAmountStr() {
        return amountStr;
    }

    public void setAmountStr(String amountStr) {
        this.amountStr = amountStr;
    }

    public Integer getCoinBixDian() {
        return coinBixDian;
    }

    public void setCoinBixDian(Integer coinBixDian) {
        this.coinBixDian = coinBixDian;
    }

    public String getCurrAmountStr() {
        return currAmountStr;
    }

    public void setCurrAmountStr(String currAmountStr) {
        this.currAmountStr = currAmountStr;
    }

    public String getCoinTypeName() {
        return coinTypeName;
    }

    public void setCoinTypeName(String coinTypeName) {
        this.coinTypeName = coinTypeName;
    }

    public String getActionTypeName() {
        return TradeFrozenType.getValue(action);
    }

    public void setActionTypeName(String actionTypeName) {
        this.actionTypeName = actionTypeName;
    }

    public BigDecimal getFrozenFee() {
        return frozenFee;
    }

    public void setFrozenFee(BigDecimal frozenFee) {
        this.frozenFee = frozenFee;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getCoinTypeId() {
        return coinTypeId;
    }

    public void setCoinTypeId(BigInteger coinTypeId) {
        this.coinTypeId = coinTypeId;
    }

    public BigInteger getUserId() {
        return userId;
    }

    public void setUserId(BigInteger userId) {
        this.userId = userId;
    }
}
