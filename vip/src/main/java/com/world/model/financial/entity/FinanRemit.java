package com.world.model.financial.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.world.data.mysql.Bean;
import com.world.model.entity.admin.AdminUser;

/**
 * 财务平账记录
 * @author Administrator
 *
 */

public class FinanRemit extends Bean {
	
	public FinanRemit() {
		super();
	}
	
	public FinanRemit(BigDecimal inAmount, BigDecimal outAmount, BigDecimal inTotalAmount, BigDecimal outTotalAmount, String adminId){
		super();
		this.inAmount = inAmount;
		this.outAmount = outAmount;
		this.adminId = adminId;
	}
	
	private static final long serialVersionUID = -8791435182844009949L;
  
	private int id;
	private BigDecimal inAmount;
	private BigDecimal outAmount;
	private BigDecimal inTotalAmount;
	private BigDecimal outTotalAmount;
	private int isDel;

    private String adminId;//创建者id
    private Timestamp addTime;
    private int status;
    private Timestamp startTime;
    private Timestamp endTime;
    
    private AdminUser aUser;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getInAmount() {
		return inAmount;
	}

	public void setInAmount(BigDecimal inAmount) {
		this.inAmount = inAmount;
	}

	public BigDecimal getOutAmount() {
		return outAmount;
	}

	public void setOutAmount(BigDecimal outAmount) {
		this.outAmount = outAmount;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public Timestamp getAddTime() {
		return addTime;
	}

	public void setAddTime(Timestamp addTime) {
		this.addTime = addTime;
	}

	public int getIsDel() {
		return isDel;
	}

	public void setIsDel(int isDel) {
		this.isDel = isDel;
	}

	public AdminUser getaUser() {
		return aUser;
	}

	public void setaUser(AdminUser aUser) {
		this.aUser = aUser;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BigDecimal getInTotalAmount() {
		return inTotalAmount;
	}

	public void setInTotalAmount(BigDecimal inTotalAmount) {
		this.inTotalAmount = inTotalAmount;
	}

	public BigDecimal getOutTotalAmount() {
		return outTotalAmount;
	}

	public void setOutTotalAmount(BigDecimal outTotalAmount) {
		this.outTotalAmount = outTotalAmount;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
    

 }   
