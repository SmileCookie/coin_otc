package com.world.model.entity.msg;

import com.Lan;



/********
 * 网站提示消息处理
 * @author Administrator
 *
 */
public class TipMsg {

	private String userId;//提示的用户ID
	private String userName;//提示的用户名
	private TipType tipType;//提示的类型 枚举
	private MsgBean conBean;//关联的Bean
	private String title;
	private String msgStr;//需要提示的字符串
	private String language;
	private String[] other;
	
	public TipMsg(){
		
	}
	public TipMsg(String userId, String userName, TipType tipType,String... other){
		super();
		this.userId = userId;
		this.userName = userName;
		this.tipType = tipType;
		this.other = other;
	}
	public TipMsg(String userId, String userName, TipType tipType, MsgBean conBean){
		super();
		this.userId = userId;
		this.userName = userName;
		this.tipType = tipType;
		this.conBean = conBean;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public TipType getTipType() {
		return tipType;
	}
	public void setTipType(TipType tipType) {
		this.tipType = tipType;
	}
	public MsgBean getConBean() {
		return conBean;
	}
	public void setConBean(MsgBean conBean) {
		this.conBean = conBean;
	}
	
	public String getMsgStr() {
		String p3 = "";
		if(other.length > 0){
			p3 = other[0];
		}
		switch (tipType) {
			case withdrawSuccess : //已对
				msgStr=Lan.LanguageFormat(language, "您的提现已经成功，资金已经提现到您的银行卡中", conBean.getOther());
				break;
			case withdrawFail : //已对
				msgStr=Lan.LanguageFormat(language, "您的提现未成功，资金已冻结", conBean.getOther(), conBean.getReason());
				break;
			case withdrawFailAfterSuc : //已对
				msgStr=Lan.LanguageFormat(language, "您的提现未成功，资金已退回", conBean.getOther(), conBean.getReason());
				break;
			case chargeSuccess:
				msgStr = Lan.LanguageFormat(language, "您的充值确认已成功", conBean.getOther());
				break;
			case chargeFaild:
				msgStr = Lan.LanguageFormat(language, "您的充值未成功", conBean.getReason());
				break;
			case recommendHasReply : //已对
				msgStr=Lan.Language(language, "您的建议已回复");
				break;
			case askHasReply : //已对
				msgStr=Lan.Language(language, "您的提问已回复");
				break;
			case bandPhoneSuc:
				msgStr="手机认证成功";
				break;
			case bandEmailSuc:
				msgStr="邮箱认证成功";//Lan.LanguageFormat(language, "邮箱认证成功，系统奖励您%%比特币",WebUtil.saveFourShow((float)RewardSource.EmailCertification.getBtc()/fee.btcFee));
				break;
			case btcWithdrawSuccess : 
				msgStr=Lan.LanguageFormat(language, "比特币提现成功，您提现的%%比特币已成功到账，请检查，如有疑问，请联系在线客服。" , p3);
				break;
			case btcRechargeSuccess :
				msgStr=Lan.LanguageFormat(language, "比特币充值成功，您充值的%%比特币已成功到账，请检查，如有疑问，请联系在线客服。" , p3);
				break;
			case btcBuySuccess : 
				msgStr=Lan.LanguageFormat(language, "您已经成功买入%%比特币，详情请查看交易记录，如有疑问，请联系在线客服。" , p3);
				break;
			case commonTospecialPass : 
				msgStr="恭喜您已经成功成为专业版用户，了解专业版特权，请<a href='/help/feilv' target='_blank'>点击查看</a>。";
				break;
			case commonTospecialUnPass : 
				msgStr="对不起，您提交的专业版申请没有通过，原因是：" + conBean.getReason();
				break;
			case specialToguestPass : 
				msgStr="恭喜您已经成功成为贵宾版用户，了解贵宾版特权，请<a href='/help/feilv' target='_blank'>点击查看</a>。";
				break;
			case specialToguestUnPass : 
				msgStr="对不起，您提交的贵宾版申请没有通过，原因是：" + conBean.getReason();
				break;
			case specialToCancel : 
				msgStr="您的账户已成功恢复大众版，原因是：" + conBean.getReason();
				break;
			default:
				break;
		}
		
		return msgStr;
	}
	public void setMsgStr(String msgStr) {
		this.msgStr = msgStr;
	}
}
