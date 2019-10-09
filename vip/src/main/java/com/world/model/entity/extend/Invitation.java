package com.world.model.entity.extend;

import com.world.data.mysql.Bean;

import java.math.BigInteger;
import java.sql.Timestamp;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/3/102:07 PM
 */
public class Invitation extends Bean {
    /**
     * id
     */
    private BigInteger id;
    /**
     * 名称：媒体名称-0，名人姓名-1
     */
    private String name;
    /**
     * 类型：媒体名称-0，名人姓名-1
     */
    private int type;
    /**
     * 类型：状态：0-领办，1-已领办
     */
    private int status;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 联系人
     */
    private String userName;
    /**
     * 微信号
     */
    private String wechat;
    /**
     * 平台链接
     */
    private String platformLine;
    /**
     * 合作类型
     */
    private String cooperateType;
    /**
     * 创建时间
     */
    private Timestamp createTime;
    /**
     * 备注
     */
    private String memo;

    public Invitation(String name, int type, String mobile, String userName, String wechat, String platformLine, String cooperateType, Timestamp createTime) {
        super();
        this.name = name;
        this.type = type;
        this.mobile = mobile;
        this.userName = userName;
        this.wechat = wechat;
        this.platformLine = platformLine;
        this.cooperateType = cooperateType;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
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

    public String getPlatformLine() {
        return platformLine;
    }

    public void setPlatformLine(String platformLine) {
        this.platformLine = platformLine;
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
