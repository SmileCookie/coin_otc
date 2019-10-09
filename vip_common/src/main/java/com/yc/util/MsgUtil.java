package com.yc.util;


import com.messi.msg.MsgProvider;
import com.messi.msg.common.Constant;
import com.messi.msg.common.EmailMsgTemplate;
import com.messi.msg.common.MobileMsgTemplate;
import com.world.config.GlobalConfig;
import com.world.util.date.TimeUtil;
import com.yc.entity.msg.Msg;
import com.yc.entity.msg.MsgSendStatus;
import com.yc.entity.msg.MsgType;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MsgUtil {

	private final static Logger log = Logger.getLogger(MsgUtil.class.getName());

	/*start by xzhang 20171123 新增根据当前web端语言环境发送短信的允许地区，其他地区全部英文*/
	public static final Map<String, String> CHINAMAP = new HashMap<String, String>();

	static {
		CHINAMAP.put("+86", "大陆");
		CHINAMAP.put("+852", "香港");
		CHINAMAP.put("+853", "澳门");
		CHINAMAP.put("+886", "台湾");
	}

	/****
	 * 添加一条消息
	 * @param m
	 * @return 0发送失败  1 发送成功 2 已达到日最大发送量
	 */
	public static int addMsg(Msg m , MsgType mt){
		MsgProvider msgProvider = new MsgProvider(GlobalConfig.rabbitmqUri);
//		MsgBiz msgBiz=new MsgBiz(new MsgDao(MorphiaMongoUtil.getMorphiaMongo()));
//		long sendNum=msgBiz.todayCount(m);

//		if(m.getUserId() == null || m.getUserId().length() <= 0){
//			mt.setMaxNum(100000);
//		}
//		if(sendNum < mt.getMaxNum()){//发送数量未达到限制量
			try {
				if(m.getType() == MsgType.email.getKey()){
					EmailMsgTemplate emailMsgTemplate = new EmailMsgTemplate(m.getReceiveEmail(), m.getTitle(), m.getCont(), Constant.EMAIL_TYPE.KEWAIL_EMAIL);
					msgProvider.emailSend(emailMsgTemplate);
				}else {
				    //短信区分国内和国外，国内走螺丝帽，国外走kewail
                    String type = Constant.SMS_TYPE.KEWAIL_SMS;
                    String receivePhoneNumber = m.getReceivePhoneNumber();
                    if (receivePhoneNumber.startsWith("+86")) {
                        type = Constant.SMS_TYPE.LUOSIMAO_SMS;
                    }
                    MobileMsgTemplate mobileMsgTemplate = new MobileMsgTemplate(receivePhoneNumber, m.getCont(), type);
					msgProvider.smsSend(mobileMsgTemplate);
				}
//				msgBiz.addMsg(m);
//				log.info("今日用户ID：" + m.getUserId() + ",已发数量：" + sendNum + "还未达到上限：" + mt.getMaxNum());
			} catch (Exception e) {
				log.error(e.toString(), e);
				return 0;
			}
			return 1;
//		}else{
//			log.info("今日用户ID：" + m.getUserId() + ",已发数量：" + sendNum + "已经达到上限：" + mt.getMaxNum());
//			return 2;
//		}
	}

    /****
	 */
	public static int sendSms(Msg m){
		m.setAddDate(TimeUtil.getNow());
		m.setType(MsgType.sms.getKey());
		m.setSendStat(MsgSendStatus.no.getKey());
		m.setSendTimes(0);
		return addMsg(m,MsgType.sms);
	}
	
	/**
	 * 发送语音验证码
	 * @param m
	 * @return
	 */
	public static int sendAudioSms(Msg m){
		m.setAddDate(TimeUtil.getNow());
		m.setType(MsgType.speech.getKey());
		m.setSendStat(MsgSendStatus.no.getKey());
		m.setSendTimes(0);
		return addMsg(m,MsgType.sms);
	}
	
	/****
	 */
	public static int sendSpeech(Msg m){
		m.setAddDate(TimeUtil.getNow());
		m.setType(MsgType.speech.getKey());
		m.setSendStat(MsgSendStatus.no.getKey());
		m.setSendTimes(0);
		return addMsg(m,MsgType.speech);
	}
	
	/****
	 */
	public static int sendEmail(Msg m){
		m.setAddDate(TimeUtil.getNow());
		m.setType(MsgType.email.getKey());
		m.setSendStat(MsgSendStatus.no.getKey());
		m.setSendTimes(0);
		// 2. 构建消息实例，进行发送

		return addMsg(m,MsgType.email);
	}

	public static boolean isContain(String mobile){
		Iterator<Map.Entry<String, String>> it = CHINAMAP.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			if(mobile.startsWith(entry.getKey())){
				return true;
			}
		}
		return false;
	}
}
