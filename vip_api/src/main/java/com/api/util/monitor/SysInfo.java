package com.api.util.monitor;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * ϵͳʹ����Ϣ
 * @author guosj
 */
public class SysInfo implements Serializable{
	//cpuʹ����
	private double cpuUsed;
	//�ڴ�ʹ����
	private double memUsed;
	//Ӳ��ʹ����
	private double diskUsed;
	
	//�����������ڴ�k
	private double memFree;
	//�������ڴ���ʹ����k
	private double memTotal;
	
	//jvm�����ڴ���byte
	private double jvm_memFree;
	//jvm��ͼʹ�õ�����ڴ���byte
	private double jvm_memMax;
	//jvm��ǰռ�õ��ڴ�����byte
	private double jvm_memTotal;
	
	//jvm���ؿ��ô���������Ŀ
	private int processors;
	
	//����ʱ��
	private long addTime;
	
	private String serverType;

	public double getCpuUsed() {
		return cpuUsed;
	}

	public void setCpuUsed(double cpuUsed) {
		this.cpuUsed = cpuUsed;
	}

	public double getMemUsed() {
		return memUsed;
	}

	public void setMemUsed(double memUsed) {
		this.memUsed = memUsed;
	}

	public double getDiskUsed() {
		return diskUsed;
	}

	public void setDiskUsed(double diskUsed) {
		this.diskUsed = diskUsed;
	}
	public double getMemFree() {
		return memFree;
	}

	public void setMemFree(double memFree) {
		this.memFree = memFree;
	}

	public double getMemTotal() {
		return memTotal;
	}

	public void setMemTotal(double memTotal) {
		this.memTotal = memTotal;
	}

	public double getJvm_memFree() {
		return jvm_memFree;
	}

	public void setJvm_memFree(double jvmMemFree) {
		jvm_memFree = jvmMemFree;
	}

	public double getJvm_memMax() {
		return jvm_memMax;
	}

	public void setJvm_memMax(double jvmMemMax) {
		jvm_memMax = jvmMemMax;
	}

	public double getJvm_memTotal() {
		return jvm_memTotal;
	}

	public void setJvm_memTotal(double jvmMemTotal) {
		jvm_memTotal = jvmMemTotal;
	}

	public int getProcessors() {
		return processors;
	}

	public void setProcessors(int processors) {
		this.processors = processors;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}
	
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
}
