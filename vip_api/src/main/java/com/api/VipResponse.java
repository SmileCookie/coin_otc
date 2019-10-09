package com.api;

import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.Response;
import com.api.common.SystemCode;

public class VipResponse implements Response{
	private String code;
	private String msg;
	private Map<String , String> params;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public Map<String, String> getParams() {
		return params;
	}
	
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public boolean taskIsFinish(){
		if(code != null && code.equals(String.valueOf(SystemCode.code_1000.getKey()))){
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
