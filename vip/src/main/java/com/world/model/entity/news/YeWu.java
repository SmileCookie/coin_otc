package com.world.model.entity.news;

import java.sql.Timestamp;

import org.bson.types.ObjectId;
import com.google.code.morphia.annotations.Id;
import com.world.model.entity.BaseEntity;
 
public class YeWu  extends BaseEntity{
	private static final long serialVersionUID = -2396118543279873656L;
	@Id
	private ObjectId _id;//hui回复ID
    private String userName;//昵称
    private String lianXi;//联系方式
    private String message;//说明
    private  Timestamp postTime;//发布时间
    private String ip;//发布ip
    private int zhuangtai;//处理状态
    private String type;//业务类型
	public ObjectId get_id() {
		return _id; 
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLianXi() {
		return lianXi;
	}
	public void setLianXi(String lianXi) {
		this.lianXi = lianXi;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Timestamp getPostTime() {
		return postTime;
	}
	public void setPostTime(Timestamp postTime) {
		this.postTime = postTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getZhuangtai() {
		return zhuangtai;
	}
	public void setZhuangtai(int zhuangtai) {
		this.zhuangtai = zhuangtai;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
}
