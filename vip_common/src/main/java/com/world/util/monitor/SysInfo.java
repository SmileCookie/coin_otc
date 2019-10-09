package com.world.util.monitor;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * 系统使用信息
 * @author guosj
 */
public class SysInfo implements Serializable{
	//cpu使用率
	private double cpuUsed;
	//内存使用率
	private double memUsed;
	//硬盘使用率
	private double diskUsed;
	
	//服务器空闲内存k
	private double memFree;
	//服务器内存总使用量k
	private double memTotal;
	
	//jvm空闲内存量byte
	private double jvm_memFree;
	//jvm试图使用的最大内存量byte
	private double jvm_memMax;
	//jvm当前占用的内存总数byte
	private double jvm_memTotal;
	
	//jvm返回可用处理器的数目
	private int processors;
	
	//eth网卡接收流量
	private double eth_receive;
	//eth网卡发送流量
	private double eth_transmit;
	
	//插入时间
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

	public double getEth_receive() {
		return eth_receive;
	}

	public void setEth_receive(double eth_receive) {
		this.eth_receive = eth_receive;
	}

	public double getEth_transmit() {
		return eth_transmit;
	}

	public void setEth_transmit(double eth_transmit) {
		this.eth_transmit = eth_transmit;
	}
}