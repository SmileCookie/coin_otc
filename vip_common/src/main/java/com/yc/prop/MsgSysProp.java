package com.yc.prop;

import com.world.config.GlobalConfig;

/*****
 * ��Ϣϵͳ��������
 * @author Administrator
 *
 */
public class MsgSysProp {

	public final static int everyGetNum=3000;//ÿ�δ�mongodb��ȡ��δ������Ϣ������
	
	public final static int everyDayUserMaxEmailNum=100;//每天每用户发送的最大email数量
	
	public final static int everyDayUserMaxSmsNum=100;//每天每用户发送的最大sms数量
	
	public final static int everyDayUserMaxSpeechNum=1000;//每天每用户发送的最大语音数量

	public final static int everyEmailSendMaxTimes=2;//每条邮件发送的最多次数
	
	public final static int everySmsSendMaxTimes=1;//每条短信发送的最多次数
	
	public final static int everySpeechSendMaxTimes=1;//每条语音发送的最多次数
	
	public final static long queueTimer=5*1000;//���ж�ʱ��ˢ��Ƶ��
	
	public final static long emailTimer=24*60*60*1000;//����email��ʱ��ˢ��Ƶ��  ******�������
	
	public final static long smsTimer=24*60*60*1000;//����sos��ʱ��ˢ��Ƶ��   ******�������
	
	public final static boolean isTest=false;//ϵͳ�Ƿ��ڲ�����
	
	///////mongodb信息
	public final static String dbName=GlobalConfig.getValue("moDbName");
	
	public final static String url=GlobalConfig.getValue("moUrl");
	
	public final static String testUrl=GlobalConfig.getValue("moUrl");
	
	public final static String userName=GlobalConfig.getValue("moUserName");
	
	public final static String password=GlobalConfig.getValue("moPassword");
	
	///////////ʮ�Ǳ���������Ϣ  163
	public final static String fromAddr="";
	public final static String mailServerHost="smtp.qiye.163.com";
	public final static String MailServerPort="25";
	public final static String emailUserName="";
	public final static String emailPassword="";
	//////////�ֻ����
	public final static String smsHost = "211.136.163.68";
	public final static int smsPort = 9981;
	
	
	public final static String ycsmsAccountId = "10657109042353";
	public final static String ycsmsPassword = "abcd1234";
	public final static String ycsmsServiceId = "10657109042353";
	
}
