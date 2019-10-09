package com.world.model.entity.user;

import com.world.data.mysql.Bean;

import java.util.Date;

/**
 * @ClassName UserScreen
 * @Description
 * @Author kinghao
 * @Date 2018/7/28   16:12
 * @Version 1.0
 * @Description
 */
public class UserScreen extends Bean {
    /**
     * ID
     **/
    private int id;
    /**
     * 用户IDD
     **/
    private int userId;
    /**
     * 看板
     **/
    private String multiScreen;

    /**
     * NEW看板
     **/
    private String multiScreenOld;

    /**
     * 创建时间
     **/
    private Date createTime;
    /**
     * 创建人
     **/
    private int createBy;

    /**
     * 操作  1 查询   2 修改  新增   3 删除
     **/
    private int operationType;

    /**
     * 看板排序
     **/
    private int groupByScreen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMultiScreen() {
        return multiScreen;
    }

    public void setMultiScreen(String multiScreen) {
        this.multiScreen = multiScreen;
    }

    public String getMultiScreenOld() {
        return multiScreenOld;
    }

    public void setMultiScreenOld(String multiScreenOld) {
        this.multiScreenOld = multiScreenOld;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public int getOperationType() {
        return operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public int getGroupByScreen() {
        return groupByScreen;
    }

    public void setGroupByScreen(int groupByScreen) {
        this.groupByScreen = groupByScreen;
    }
}
