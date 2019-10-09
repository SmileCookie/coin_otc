package com.world.model.dao.pay.download;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


/**
 * Created by xie on 2017/3/28.
 */
public class DownloadRemoteInfo {

    private Integer status;
    private List<DataInfo> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<DataInfo> getData() {
        return data;
    }

    public void setData(List<DataInfo> data) {
        this.data = data;
    }
}

class DataInfo{

    private String id;
    private BigDecimal amount;
    private Integer status;
    private String coinType;
    private Integer confirm;
    private Timestamp deal_time;
    private String withdrawal_address;
    private BigDecimal service_charge;
    private Integer target_confirm;

    private String tx_id;   //交易编号
    private String vn;  //序号
    private String txIdN;  //交易编号+序号
    private Integer blockHeight;//区块高度
    private BigDecimal real_fee; //真实手续费
    private long timestamp;//区块确认时间


    public String getTx_id() {
        return tx_id;
    }

    public void setTx_id(String tx_id) {
        this.tx_id = tx_id;
    }

    public void setTxIdN(String txIdN) {
        this.txIdN = txIdN;
    }

    public BigDecimal getReal_fee() {
        return real_fee;
    }

    public void setReal_fee(BigDecimal real_fee) {
        this.real_fee = real_fee;
    }

    public String getVn() {
        return vn;
    }

    public void setVn(String vn) {
        this.vn = vn;
    }

    public String getTxIdN() {
        return this.tx_id + "_" + this.vn;
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setConfirm(Integer confirm) {
        this.confirm = confirm;
    }

    public void setTarget_confirm(Integer target_confirm) {
        this.target_confirm = target_confirm;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCoinType() {
        return coinType;
    }

    public void setCoinType(String coinType) {
        this.coinType = coinType;
    }

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public Timestamp getDeal_time() {
        return deal_time;
    }

    public void setDeal_time(Timestamp deal_time) {
        this.deal_time = deal_time;
    }

    public String getWithdrawal_address() {
        return withdrawal_address;
    }

    public void setWithdrawal_address(String withdrawal_address) {
        this.withdrawal_address = withdrawal_address;
    }

    public BigDecimal getService_charge() {
        return service_charge;
    }

    public void setService_charge(BigDecimal service_charge) {
        this.service_charge = service_charge;
    }

    public int getTarget_confirm() {
        return target_confirm;
    }

    public void setTarget_confirm(int target_confirm) {
        this.target_confirm = target_confirm;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "DataInfo{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", coinType='" + coinType + '\'' +
                ", confirm=" + confirm +
                ", deal_time=" + deal_time +
                ", withdrawal_address='" + withdrawal_address + '\'' +
                ", service_charge=" + service_charge +
                ", target_confirm=" + target_confirm +
                ", tx_id='" + tx_id + '\'' +
                ", vn='" + vn + '\'' +
                ", txIdN='" + txIdN + '\'' +
                ", blockHeight=" + blockHeight +
                ", real_fee=" + real_fee +
                ", timestamp=" + timestamp +
                '}';
    }
}