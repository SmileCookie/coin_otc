package com.world.model.job.entity;

import com.world.data.mysql.Bean;

import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by xie on 2017/10/17.
 * 作业定义表
 */
public class JobDefinBean extends Bean {

    private long id;                //主键
    private String jobName;         //作业名称
//    private int jobType;            //作业类型1-自动，2-手动,预留字段
    private String jobStartTime;      //开始执行时间
    private String jobEndTime;        //结束执行时间
    private long jobInterval;       //时间间隔秒
    private int jobStatus;          //作业状态 0-停用，1-启用
    private String remark;          //备注
    private Timestamp createTime;   //创建时间
    private String createUserId;    //创建人ID
    private Timestamp modifyTime;   //修改时间
    private String modifyUserId;    //修改人ID
    private String jobClass;        //执行类

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

//    public int getJobType() {
//        return jobType;
//    }
//
//    public void setJobType(int jobType) {
//        this.jobType = jobType;
//    }

    public String getJobStartTime() {
        return jobStartTime;
    }

    public void setJobStartTime(String jobStartTime) {
        this.jobStartTime = jobStartTime;
    }

    public String getJobEndTime() {
        return jobEndTime;
    }

    public void setJobEndTime(String jobEndTime) {
        this.jobEndTime = jobEndTime;
    }

    public long getJobInterval() {
        return jobInterval;
    }

    public void setJobInterval(long jobInterval) {
        this.jobInterval = jobInterval;
    }

    public int getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(int jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(String modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public String getStatusDes(){
        if(jobStatus == 1){
            return "启用";
        }else{
            return "停用";
        }
    }
}
