package com.world.model.entity.transLog;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;

import java.math.BigDecimal;

/**
 * 资金划转记录表
 * <p>@Description: </p>
 *
 * @author shujianfang
 * @date 2018/7/28下午3:33
 */
public class FundTransferLogBean extends Bean {

    private Integer id;
    private String uid;
    private BigDecimal amount;
    private int fundType;
    private int src; //来源  1 我的钱包 2 币币交易 3 法币交易（otc）
    private int dst;//目的地  1 我的钱包 2 币币交易 3 法币交易（otc)
    private Data time;
    private String srcName;
    private String dstName;

    public String getSrcName() {
        if (src != 0){
            if (src == 1){
                srcName = "我的钱包";
            }else if (src ==2){
                srcName = "币币交易";
            }else if (src ==3){
                srcName = "法币交易";
            }
        }
        return srcName;
    }

    public void setSrcName(String srcName) {
        this.srcName = srcName;
    }

    public String getDstName() {
        if (dst != 0){
            if (dst == 1){
                dstName = "我的钱包";
            }else if (dst ==2){
                dstName = "币币交易";
            }else if (dst ==3){
                dstName = "法币交易";
            }
        }
        return dstName;
    }

    public void setDstName(String dstName) {
        this.dstName = dstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getFundType() {
        return fundType;
    }

    public void setFundType(int fundType) {
        this.fundType = fundType;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }

    public int getDst() {
        return dst;
    }

    public void setDst(int dst) {
        this.dst = dst;
    }

    public Data getTime() {
        return time;
    }

    public void setTime(Data time) {
        this.time = time;
    }
}
