package com.world.model.entity.news;

import java.sql.Timestamp;
import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.user.User;

@Entity(noClassnameStored = true , value = "newsReply")
public class NewsReply extends StrBaseLongIdEntity{
	private static final long serialVersionUID = -2396115543279873656L;

	public NewsReply() {
		super();
	}
	
	public NewsReply(Datastore ds) {
		super(ds);
	}
	
	private String newsId;//帖子文章id
	private String parentId;//主评论id
    private String userId;
    private String content;//信息
    private  Timestamp postTime;//发布时间
    private String ip;//发布ip
    private int agree;//同意、认可、鲜花
    private int unAgree;//不统一、不认可、鸡蛋
    private String srcUserId;
 
    private List<NewsReply> sonReplys;
    
    private User user;
    

	public List<NewsReply> getSonReplys() {
		return sonReplys;
	}

	public void setSonReplys(List<NewsReply> sonReplys) {
		this.sonReplys = sonReplys;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getSrcUserId() {
		return srcUserId;
	}

	public void setSrcUserId(String srcUserId) {
		this.srcUserId = srcUserId;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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

	public String getNewsId() {
		return newsId;
	}

	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    
}
