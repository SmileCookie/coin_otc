package com.api.request;

import java.math.BigDecimal;
import java.util.Map;

import com.api.VipRequest;

public class VipFundsConvertRequest implements VipRequest{

	private long timestamp;
	
	private int type;
	
	private BigDecimal amount;
	
	private Map<String , String> params;
	
	private String apiMethod;
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setApiMethod(String apiMethod) {
		this.apiMethod = apiMethod;
	}
	
	public String getApiMethod() {
		return apiMethod;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long time) {
		this.timestamp = time;
	}

	public Map<String, String> getTextParams() {
		return params;
	}

	public void setTextParams(Map<String, String> params) {
		this.params = params;
	}
	
}
