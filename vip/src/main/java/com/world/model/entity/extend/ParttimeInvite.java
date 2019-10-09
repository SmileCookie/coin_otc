package com.world.model.entity.extend;

import com.world.data.mysql.Bean;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/3/102:08 PM
 */
public class ParttimeInvite extends Bean{
    /**
     * id
     */
    private BigInteger id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 微信号
     */
    private String wechat;
    /**
     * 钱包地址
     */
    private String walletAddress;
    /**
     * 申请职位
     */
    private String applyPost;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 类型：状态：0-领办，1-已领办
     */
    private int status;
    /**
     * 备注
     */
    private String memo;

    public ParttimeInvite(HttpServletRequest request, String name, String mobile, String wechat, String walletAddress, String applyPost, Timestamp createTime) {
        super();
        this.name = name;
        this.mobile = mobile;
        this.wechat = wechat;
        this.walletAddress = walletAddress;
        this.applyPost = applyPost;
        this.createTime = createTime;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getApplyPost() {
        return applyPost;
    }

    public void setApplyPost(String applyPost) {
        this.applyPost = applyPost;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
