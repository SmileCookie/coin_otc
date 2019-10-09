package com.world.web.sso;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

/****
 * 登录用户信息
 * @author apple
 *
 */
public class SessionUser implements Serializable{

	private static final long serialVersionUID = -2277144503198368995L;
	public String uid;//用户id
	public String uname;//用户名
	public long ltime;//登录时间
	public String lip;//登录ip
	public long lastTime;//最后活动时间
	public boolean ipv;//ip验证
	public String rid;//角色id
	public String validType = "0";//googlevalid
	public String prid;//角色id
	public String token;//表单令牌  csrf(跨站点请求伪造)
	public boolean nupdate=false;//是否需要更新
	public JSONObject others = null;//其他相关

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public long getLtime() {
		return ltime;
	}

	public String getLip() {
		return lip;
	}

	public long getLastTime() {
		return lastTime;
	}

	public boolean isIpv() {
		return ipv;
	}

	public String getValidType() {
		return validType;
	}

	public String getPrid() {
		return prid;
	}

	public String getToken() {
		return token;
	}

	public boolean isNupdate() {
		return nupdate;
	}

	public JSONObject getOthers(){
		return others;
	}
	
	public String getUname(){
		return uname;
	}
	public String getRid() {
		return rid;
	}
	public String getUid() {
		return uid;
	}

}
