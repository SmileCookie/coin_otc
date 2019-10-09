package com.yc.test;

import com.yc.entity.SysGroups;
import com.yc.entity.msg.Msg;
import com.yc.util.MsgUtil;
import org.apache.log4j.Logger;

public class AddMsgs {

	private final static Logger log = Logger.getLogger(AddMsgs.class.getName());

	public static void main(String[] args) {
		SysGroups sg = SysGroups.vip;
		
//		for(int i=0;i<1;i++){
//			Msg m=new Msg();
//			m.setSysId(sg.getId());
//			m.setSendIp("211.69.0.2");
//			m.setUserId("12");
//			m.setUserName("xiao er");
//			m.setTitle(sg.getValue()+"֪ͨ");
//			m.setCont("<h1>ddd</h1>"+sg.getValue()+"bbb");
//			m.setReceiveEmail("843715520@qq.com");
//			m.setSendUserName("xx");
//			int res=MsgUtil.sendEmail(m);//�����ʼ�
//			if(res==1){
//				log.info("1");
//			}else{
//				log.info(res);
//			}
//		}
		
		for(int i=0;i<1;i++){
			Msg m=new Msg();
			m.setSysId(sg.getId());
			m.setSendIp("213.69.0.2");
			m.setUserId("12");
			m.setUserName("yanghe");
			m.setCont("中文短信测试");
			m.setReceivePhoneNumber("18610792236");
			m.setSendUserName("yang");
			m.setCodec(8);////8是中文韩文日文等 ，3是英文
			
			int res=MsgUtil.sendSms(m);//
			if(res==1){
				log.info("success");
			}else{
				log.info("fail");
			}
		}
	}
}
