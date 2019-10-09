package com.world.model.entity.financialproift;

import java.time.LocalDateTime;

import com.world.data.mysql.Bean;


@SuppressWarnings("serial")
public class FinancialTaskLogs extends Bean{
	
	private Long id;
	private String taskName;
	private int taskType;
	private LocalDateTime taskTime;
	private int taskIndex;
	private int sumStep;
	private int nowStep;
	private LocalDateTime taskStartTime;
	private LocalDateTime taskEndTime;
	private Integer taskResult;
	private String resultInfo;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public int getTaskType() {
		return taskType;
	}
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}
	public LocalDateTime getTaskTime() {
		return taskTime;
	}
	public void setTaskTime(LocalDateTime taskTime) {
		this.taskTime = taskTime;
	}
	public int getTaskIndex() {
		return taskIndex;
	}
	public void setTaskIndex(int taskIndex) {
		this.taskIndex = taskIndex;
	}
	public int getSumStep() {
		return sumStep;
	}
	public void setSumStep(int sumStep) {
		this.sumStep = sumStep;
	}
	public int getNowStep() {
		return nowStep;
	}
	public void setNowStep(int nowStep) {
		this.nowStep = nowStep;
	}
	public LocalDateTime getTaskStartTime() {
		return taskStartTime;
	}
	public void setTaskStartTime(LocalDateTime taskStartTime) {
		this.taskStartTime = taskStartTime;
	}
	public LocalDateTime getTaskEndTime() {
		return taskEndTime;
	}
	public void setTaskEndTime(LocalDateTime taskEndTime) {
		this.taskEndTime = taskEndTime;
	}
	public Integer getTaskResult() {
		return taskResult;
	}
	public void setTaskResult(Integer taskResult) {
		this.taskResult = taskResult;
	}
	public String getResultInfo() {
		return resultInfo;
	}
	public void setResultInfo(String resultInfo) {
		this.resultInfo = resultInfo;
	}
	
}
