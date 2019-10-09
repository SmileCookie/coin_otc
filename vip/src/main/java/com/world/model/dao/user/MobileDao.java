package com.world.model.dao.user;

import java.util.Random;

import com.world.cache.Cache;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.User;
import com.world.web.action.Action;
import com.yc.entity.SysGroups;
import com.yc.entity.msg.Msg;
import com.yc.util.MsgUtil;

public class MobileDao extends MongoDao<User, String> implements Action{
	UserDao userDao = new UserDao();
	private static int times = 30;//每天每个用户最多发送100条短信验证码
	private static int limitTime = 1440;//一天的分钟数
	private static int limitTenMinute = 10;//10分钟内最多6条
	
	public boolean sendSms(User user, String ip, String title, String cont, String mobile) {
		SysGroups sg = SysGroups.vip;
		
		Msg m=new Msg();
		m.setSysId(sg.getId());
		m.setSendIp(ip);
		m.setUserId(user.getId());
		m.setUserName(user.getUserName());
		m.setTitle(title);
		m.setCont(cont);
		m.setReceivePhoneNumber(mobile);
		m.setSendUserName("VIP");
		
		if(user.getLanguage() != null && user.getLanguage().equals("cn")){
			//8是中文韩文日文等 ，3是英文
			m.setCodec(8);
		}else{
			m.setCodec(3);
		}
		
		int res=MsgUtil.sendSms(m);
		if(res==1){
			return true;
		}else{
			return false;
		}
	}
	
	public static String GetRadomStr(int type) {
		String[] str = {"0", "1", "2", "3", "5", "6", "7", "8", "9", "9", "8", "7", "6", "5", "3", "2", "1", "0"};
		Random r = new Random();
		String ls = "";
		if(type == 1){
			int length = 4;
			for (int j = 0; j < length; j++) {
				int a = r.nextInt(str.length);
				ls += str[a];
			}
		}else{
			int length = 6;
			for (int j = 0; j < length; j++) {
				int a = r.nextInt(str.length);
				ls += str[a];
			}
		}

		return ls;
	}
	
	public boolean couldSend2(String userId){
		Object sessionId = null;
		sessionId = Cache.GetObj(userId+"_s");
		if(sessionId == null){
			Cache.SetObj(userId+"_s", 1, 60*60*24);
		}else{
			int count = Integer.parseInt(sessionId.toString());
			if(count < times){
				count++;
				Cache.SetObj(userId+"_s", count, 5*60);
			}else{
				return false;
			}
		}
		
		return true;
	}
	
	public boolean couldSend(String userId){
		
//		if(!tenMinuteCouldSend(userId)){
//			return false;
//		}
		
		String key = userId + "_s";
		String current=Cache.Get(key);
		if(current==null){
			Cache.Set(key, "1_"+System.currentTimeMillis());
		}else{
			int currNum=Integer.parseInt(current.split("_")[0]);
			long old=Long.parseLong(current.split("_")[1]);
			long now=System.currentTimeMillis();
			
			long sp=now-old;
			long minits=sp/(1000 * 60);//间隔的时间

			//按照时间来算，过期这个时间才会过期
			if(limitTime<=minits){
				//说明已经离上次记时过期了，可以重新开始了
				Cache.Set(key, "1_"+System.currentTimeMillis());
			}else{
				currNum ++;
				if(currNum <= times){
					Cache.Set(key, currNum+"_"+old);
				}else{
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean tenMinuteCouldSend(String userId){
		
		String key = userId + "_ten_c";
		String current=Cache.Get(key);
		if(current==null){
			Cache.Set(key, "1_"+System.currentTimeMillis());
		}else{
			int currNum=Integer.parseInt(current.split("_")[0]);
			long old=Long.parseLong(current.split("_")[1]);
			long now=System.currentTimeMillis();
			
			long sp=now-old;
			long minits=sp/(1000 * 60);//间隔的时间

			//按照时间来算，过期这个时间才会过期
			if(limitTenMinute<=minits){
				//说明已经离上次记时过期了，可以重新开始了
				Cache.Set(key, "1_"+System.currentTimeMillis());
			}else{
				currNum ++;
				if(currNum <= 6){
					Cache.Set(key, currNum+"_"+old);
				}else{
					return false;
				}
			}
		}
		return true;
	}
}
