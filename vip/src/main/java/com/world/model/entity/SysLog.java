package com.world.model.entity;

import com.google.code.morphia.Datastore;
import com.world.data.mongo.id.StrBaseLongIdEntity;

import java.sql.Timestamp;


/**
 * Created by xie on 2017/6/26.
 * 系统日志表
 */
public class SysLog extends StrBaseLongIdEntity {

    private String logId;       //日志编号(systemId+modularId+systemtime)
    private String batchId;     //批次编号(同一个批次的日志记录编号,用于查询此功能的日志流水)
    private String logType;     //日志类型，1=log,2=error
    private String systemName;  //系统名称
    private String modularName; //模块名称
    private int modularType; //模块类型
    private String logContent;  //日志内容
    private String logUserId;   //操作|记录人编号
    private String logUserName; //操作|记录人名称
    private Timestamp logTime;  //日志时间
    private String logIp;       //登录人IP
    private String remark;      //备注

    public SysLog(){
        super(null);
    }

    public SysLog(Datastore ds) {
        super(ds);
    }

    public SysLog(Datastore ds, String batchId, String logType, String systemName, String modularName, int modularType, String logUserId, String logUserName,String logIp) {
        super(ds);
        this.batchId = batchId;
        this.logType = logType;
        this.systemName = systemName;
        this.modularName = modularName;
        this.modularType = modularType;
        this.logUserId = logUserId;
        this.logUserName = logUserName;
        this.logIp = logIp;
    }

//    public SysLog(Datastore ds, String logId, String batchId, String logType, String systemName, String modularName, String modularType, String logContent, String logUserId, String logUserName, Timestamp logTime, String logIp, String remark) {
//        super(ds);
//        this.logId = logId;
//        this.batchId = batchId;
//        this.logType = logType;
//        this.systemName = systemName;
//        this.modularName = modularName;
//        this.modularType = modularType;
//        this.logContent = logContent;
//        this.logUserId = logUserId;
//        this.logUserName = logUserName;
//        this.logTime = logTime;
//        this.logIp = logIp;
//        this.remark = remark;
//    }




    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getModularName() {
        return modularName;
    }

    public void setModularName(String modularName) {
        this.modularName = modularName;
    }

    public int getModularType() {
        return modularType;
    }

    public void setModularType(int modularType) {
        this.modularType = modularType;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public String getLogUserId() {
        return logUserId;
    }

    public void setLogUserId(String logUserId) {
        this.logUserId = logUserId;
    }

    public String getLogUserName() {
        return logUserName;
    }

    public void setLogUserName(String logUserName) {
        this.logUserName = logUserName;
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    public void setLogTime(Timestamp logTime) {
        this.logTime = logTime;
    }

    public String getLogIp() {
        return logIp;
    }

    public void setLogIp(String logIp) {
        this.logIp = logIp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
