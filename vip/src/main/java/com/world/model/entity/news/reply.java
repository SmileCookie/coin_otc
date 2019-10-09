package com.world.model.entity.news;

import java.sql.Timestamp;

import org.bson.types.ObjectId;
import com.google.code.morphia.annotations.Id;

import com.world.model.entity.BaseEntity;
import com.world.model.entity.user.User;

 
public class reply  extends BaseEntity{
	private static final long serialVersionUID = -2396115543279873656L;
	@Id
	private String _id;//hui回复ID

	public String typeId;//回复类别
	private String articalId;//帖子文章id
	private long numId;//顺序号id，重复的即为二级回复
	private int isLow;//是否为下级回复
    private String userName;//昵称
    private String userId;
    private String photo;//照片
    private String message;//信息
    private  Timestamp postTime;//发布时间
    private String from;//来源 
    private String ip;//发布ip
    private int agree;//同意、认可、鲜花
    private int unAgree;//不统一、不认可、鸡蛋
 
    private User user;
    
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getIsLow() {
		return isLow;
	}
	public void setIsLow(int isLow) {
		this.isLow = isLow;
	}
	public int getAgree() {
		return agree;
	}
	public void setAgree(int agree) {
		this.agree = agree;
	}
	public int getUnAgree() {
		return unAgree;
	}
	public void setUnAgree(int unAgree) {
		this.unAgree = unAgree;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getArticalId() {
		return articalId;
	}
	public void setArticalId(String articalId) {
		this.articalId = articalId;
	}
	public long getNumId() {
		return numId;
	}
	public void setNumId(long numId) {
		this.numId = numId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
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
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
}
