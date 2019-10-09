package com.yc.entity.msg;

import java.sql.Timestamp;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.yc.entity.SysGroups;

@Entity(value = "Msg", noClassnameStored = true)
public class Msg {

	@Id
	private ObjectId _id;//锟斤拷锟斤拷
	private int sysId;//所属的系统ID
	private String userId;//接受的用户ID
	private String userName;//用户名
	private String realName;//真实姓名
	private int type;//消息类型  邮箱发送或手机发送  或者两者都发送   邮箱1  手机2
	private String title;//标题
	private String cont;//消息内容
	private Timestamp addDate;//添加时间
	private int sendStat;//发送状态          0未发送   1发送中   2发送成功  3发送失败
	private int sendTimes;//发送次数    已发送次数
	private String sendIp;//发送端ip
	private Timestamp upStatDate;//状态更新时间
	
	private String sendUserName;//发送方用户名
	
	private String receivePhoneNumber;//接收方手机号码
	private String receiveEmail;//接收方email
	private String id;//主键字符串
	private SysGroups sg;
	
	private int codec;//8是中文韩文日文等 ，3是英文
	
	private String sendWay;
	
	private int templateId;
	
	private String mCode;
	
	public String getmCode() {
		return mCode;
	}
	public void setmCode(String mCode) {
		this.mCode = mCode;
	}
	public int getTemplateId() {
		return templateId;
	}
	public void setTemplateId(int templateId) {
		this.templateId = templateId;
	}
	public int getCodec() {
		return codec;
	}
	public void setCodec(int codec) {
		this.codec = codec;
	}
	public SysGroups getSg() {
		if(sysId == SysGroups.vip.getId()){
			sg = SysGroups.vip;
		}
		return sg;
	}
	public void setSg(SysGroups sg) {
		this.sg = sg;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		id=_id.toString();
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId id) {
		_id = id;
	}
	public int getSysId() {
		return sysId;
	}
	public void setSysId(int sysId) {
		this.sysId = sysId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getCont() {
		return cont;
	}
	public void setCont(String cont) {
		this.cont = cont;
	}
	public Timestamp getAddDate() {
		return addDate;
	}
	public void setAddDate(Timestamp addDate) {
		this.addDate = addDate;
	}
	public int getSendStat() {
		return sendStat;
	}
	public void setSendStat(int sendStat) {
		this.sendStat = sendStat;
	}
	public int getSendTimes() {
		return sendTimes;
	}
	public void setSendTimes(int sendTimes) {
		this.sendTimes = sendTimes;
	}
	public String getSendIp() {
		return sendIp;
	}
	public void setSendIp(String sendIp) {
		this.sendIp = sendIp;
	}
	public Timestamp getUpStatDate() {
		return upStatDate;
	}
	public void setUpStatDate(Timestamp upStatDate) {
		this.upStatDate = upStatDate;
	}
	public String getSendUserName() {
		return sendUserName;
	}
	public void setSendUserName(String sendUserName) {
		this.sendUserName = sendUserName;
	}
	public String getReceivePhoneNumber() {
		return receivePhoneNumber;
	}
	public void setReceivePhoneNumber(String receivePhoneNumber) {
		this.receivePhoneNumber = receivePhoneNumber;
	}
	public String getReceiveEmail() {
		return receiveEmail;
	}
	public void setReceiveEmail(String receiveEmail) {
		this.receiveEmail = receiveEmail;
	}
	public String getSendWay() {
		return sendWay;
	}
	public void setSendWay(String sendWay) {
		this.sendWay = sendWay;
	}
	
}
