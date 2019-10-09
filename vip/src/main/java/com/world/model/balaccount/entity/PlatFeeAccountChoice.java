package com.world.model.balaccount.entity;

import java.math.BigDecimal;

/**
 * <p>@Description: </p>
 *
 * @author shujianfang
 * @date 2018/12/19下午2:36
 */
public class PlatFeeAccountChoice {

    private Long id ;
    //区块高度
    private Integer blockheight ;
    //已经确认的次数
    private Integer confirmtimes ;
    //运营账户确认入账: 0 ：成功 1：已首次确认 2：已二次确认
    private Integer confirmation ;
    //合约消耗 1 对方 0 本方
    private Integer dealfeetype ;
//    交易类型 ：
//  1 充值
//  2 提现(热提)
//  3 冷钱包到热提钱包转账
//  4 热冲钱包到冷钱包转账
//  5 其他到热提
//  6 其他到冷
//  7 冷到其他
//  8 热提到其他
    private Integer dealtype ;
    //资金类型 2:比特币根据config.json配置
    private Integer fundstype ;
    //匹配状态 0：未匹配  1：已匹配
    private Integer status ;
    //支付中心添加时间
    private Long addtime ;
    //对应审核表中的ID
    private Long checkid ;
    //确认时间
    private Long configtime ;
    //创建时间
    private Long createtime ;
    //手续费
    private BigDecimal fee ;
    //钱包名称
    private String receivewallet ;
    //钱包名称
    private String sendwallet ;
    //备注
    private String tmp ;
    //交易金额
    private BigDecimal txamount ;
    //交易编号
    private String txid ;
    //唯一标识接口对接使用
    private String uuid ;
    //钱包余额
    private BigDecimal walbalance ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBlockheight() {
        return blockheight;
    }

    public void setBlockheight(Integer blockheight) {
        this.blockheight = blockheight;
    }

    public Integer getConfirmtimes() {
        return confirmtimes;
    }

    public void setConfirmtimes(Integer confirmtimes) {
        this.confirmtimes = confirmtimes;
    }

    public Integer getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Integer confirmation) {
        this.confirmation = confirmation;
    }

    public Integer getDealfeetype() {
        return dealfeetype;
    }

    public void setDealfeetype(Integer dealfeetype) {
        this.dealfeetype = dealfeetype;
    }

    public Integer getDealtype() {
        return dealtype;
    }

    public void setDealtype(Integer dealtype) {
        this.dealtype = dealtype;
    }

    public Integer getFundstype() {
        return fundstype;
    }

    public void setFundstype(Integer fundstype) {
        this.fundstype = fundstype;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getAddtime() {
        return addtime;
    }

    public void setAddtime(Long addtime) {
        this.addtime = addtime;
    }

    public Long getCheckid() {
        return checkid;
    }

    public void setCheckid(Long checkid) {
        this.checkid = checkid;
    }

    public Long getConfigtime() {
        return configtime;
    }

    public void setConfigtime(Long configtime) {
        this.configtime = configtime;
    }

    public Long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Long createtime) {
        this.createtime = createtime;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getReceivewallet() {
        return receivewallet;
    }

    public void setReceivewallet(String receivewallet) {
        this.receivewallet = receivewallet;
    }

    public String getSendwallet() {
        return sendwallet;
    }

    public void setSendwallet(String sendwallet) {
        this.sendwallet = sendwallet;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public BigDecimal getTxamount() {
        return txamount;
    }

    public void setTxamount(BigDecimal txamount) {
        this.txamount = txamount;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getWalbalance() {
        return walbalance;
    }

    public void setWalbalance(BigDecimal walbalance) {
        this.walbalance = walbalance;
    }
}
