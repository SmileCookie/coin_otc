package com.world.model.entity.extend;

import com.world.data.mysql.Bean;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/3/102:14 PM
 */
public class WalletCooperate extends Bean{
    /**
     * id
     */
    private BigInteger id;
    /**
     * 钱包名称
     */
    private String walletName;
    /**
     * 官网链接
     */
    private String websitesLink;
    /**
     * 联系人
     */
    private String userName;
    /**
     * 微信号
     */
    private String wechat;
    /**
     * 合作类型
     */
    private String cooperateType;
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

    public WalletCooperate(HttpServletRequest request, String walletName, String websitesLink, String userName, String wechat, String cooperateType, Timestamp createTime) {
        super();
        this.walletName = walletName;
        this.websitesLink = websitesLink;
        this.userName = userName;
        this.wechat = wechat;
        this.cooperateType = cooperateType;
        this.createTime = createTime;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWebsitesLink() {
        return websitesLink;
    }

    public void setWebsitesLink(String websitesLink) {
        this.websitesLink = websitesLink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getCooperateType() {
        return cooperateType;
    }

    public void setCooperateType(String cooperateType) {
        this.cooperateType = cooperateType;
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
