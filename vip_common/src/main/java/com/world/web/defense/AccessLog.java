package com.world.web.defense;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.utils.IndexDirection;
import com.world.model.entity.BaseEntity;
import com.world.util.WebUtil;
import com.world.util.string.MD5;
import org.apache.log4j.Logger;

@Entity(value = "access_log" , noClassnameStored = true)
public class AccessLog  extends BaseEntity {
	static String encode = "utf-8";
	public AccessLog() {}

	private static final long serialVersionUID = 7097303122473377579L;

	private static Logger log = Logger.getLogger(AccessLog.class.getName());

	@Id
	private String _id; 

	@Indexed(name = "date_i" , value = IndexDirection.DESC)
	private Timestamp date;//操作时间
	
	private String urls;//操作的urls
	
	private String params;//参数
	
	private String adminName;//操作者ID
	
	private String userName;//用户ID
	
	private String ip;//访问者ip
	
	@Indexed(name="minuteFirst_i" , value = IndexDirection.DESC)
	private long minuteFirst;//分,用于统计
	private long hourFirst;//时,用于统计
	
	private long times;//访问次数

	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public String getUrls() {
		return urls;
	}

	public void setUrls(String urls) {
		this.urls = urls;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	public long getMinuteFirst() {
		return minuteFirst;
	}
	public void setMinuteFirst(long minuteFirst) {
		this.minuteFirst = minuteFirst;
	}
	public long getHourFirst() {
		return hourFirst;
	}
	public void setHourFirst(long hourFirst) {
		this.hourFirst = hourFirst;
	}
	
	public String toHash(){
		return MD5.toMD5(toStr());
	}
	
	public String toStr(){
				
		StringBuilder sbl = new StringBuilder();
		
		sbl.append(minuteFirst).append("_").append(ip).append("_").append(urls);
		
		if(params != null){
			try {
				params = URLDecoder.decode(params, encode);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
			
			if(params.contains("callback=")){
				params = WebUtil.removeParams(params, new String[]{"callback"});
			}
			
			if(params.contains("pwd=")){
				params = WebUtil.removeParams(params, new String[]{"pwd"});
			}
			
			if(params.contains("password=")){
				params = WebUtil.removeParams(params, new String[]{"password"});
			}
			
			//payPass
			if(params.contains("payPass=")){
				params = WebUtil.removeParams(params, new String[]{"payPass"});
			}
			
			sbl.append("_").append(params);
		}else{
			params = "";
		}
		if(adminName != null && adminName.length() > 0){
			try {
				adminName = URLDecoder.decode(adminName , encode);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
			sbl.append("_").append(adminName);
		}
		if(userName != null){
			try {
				userName = URLDecoder.decode(userName , encode);
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
			sbl.append("_").append(userName);
		}
		return sbl.toString();
	}
	
}
