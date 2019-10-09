package com.world.web.sso.session;

import com.world.model.LimitType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.Lan;
import com.world.cache.Cache;
import com.world.web.response.DataResponse;

/***
 * 为了满足验证码系统限制  以及  验证 而设计，大致能满足的需求如下：
 *  注册登录验证码系统设计：
	
	////////////获取验证码
	
	针对手机号：
	
	单日发送量10个
	2min内只能发送一次
	3次时弹出图形验证码
	
	针对ip:
	单日发送量16个
	6次时弹出图形验证码
	
	
	
	////检测验证码
	 
	针对手机号：
	3次时弹出图形验证码
	最多验证6次
	ip验证是否和发送匹配
	单个ip最多验证次数

 * @author apple
 *
 */
public class ClientSession {
	
	static Logger log = Logger.getLogger(ClientSession.class.getName());
	
	private static final String clientSessionPre = "s_p_";
	
	private static final int resendtimes = 1000 * 60 * 1;
	
	private static final int sessionMAXTimes = 1000 * 60 * 1440;//sessoin有效时间 24个小时
	
	private static final long codeAvailableMAXMillSeconds = 1000 * 60 * 10;//验证码的有效验证时间  10min
	
	public static final int codeAvailableMAXTimes = 3;//验证码的有效验证次数
	
	public static final int pwdAvailableMAXTimes = 10;//验证码的有效验证次数
	
	private static final int DEVICE_NUMBER_MAX_SEND_TIMES = 20;//单个用户发送单类型短信次数超过20次则被禁止发送
	
	private static final int IP_MAX_SEND_TIMES = 16;//单个用户发送单类型短信次数超过16次则被禁止发送
	
	private static final int IP_MAX_CHECKER_TIMES = 200;//当个ip 24h 最多验证是否存在设备号的的数量
	
	public String ip;//当前客户端ip
	public String deviceNumber;//接收验证码的设备号 如：手机号  邮箱
	public String lan;//当前客户端的语言
	public String type;//验证码的类型
	public boolean graphicalCode;//是否需要图形验证码
	public int rs;
	public ClientSession(){}
	public ClientSession(String ip, String deviceNumber, String lan, String type, boolean graphicalCode) {
		this.ip = ip;
		this.deviceNumber = deviceNumber;
		this.lan = lan;
		this.type = type;
		this.graphicalCode = graphicalCode;
	}
	
	public void clearIpLock(String ip){
		deleteSessionInfo(ip);
	}
	
	public void clearDeviceNumberLock(String deviceNumber){
		deleteSessionInfo(deviceNumber);
	}
	/***
	 * 检测当前客户端是否可以发送验证码
	 * @return
	 */
	public DataResponse checkSend(){
		DataResponse dr = new DataResponse("", false, "");
		///号码发送限制验证
		SessionInfo numberSsi = getSessionInfo(deviceNumber);
		//当前时间
		long currentTime = System.currentTimeMillis();
		
		CodeInfo codeInfo = null;
		if(numberSsi == null){
			numberSsi = new SessionInfo();
			numberSsi.ip = ip;
			numberSsi.codeTime = 0;
			codeInfo = new CodeInfo();
			codeInfo.type = type;
			codeInfo.times = 0;
			numberSsi.codeInfos.put(type, codeInfo);
			numberSsi.startTime = currentTime;
		}else{
			/***
			 *  单日发送量10个
				2min内只能发送一次
				3次时弹出图形验证码
			 */


			codeInfo = numberSsi.codeInfos.get(type);
			
			if(codeInfo == null){
				codeInfo = new CodeInfo();
				numberSsi.codeInfos.put(type, codeInfo);
			}

			//判断单日单类型发送短信次数是否超出上线 add comment by xwz
			if(codeInfo.times > DEVICE_NUMBER_MAX_SEND_TIMES){
				dr.setDes(Lan.LanguageFormat(lan , "今日发送已超过%%次，请24小时之后再试" , DEVICE_NUMBER_MAX_SEND_TIMES+""));
				return dr;
			}
			
			long balanceTimes = codeAvailableMAXTimes - codeInfo.lastHasValidateTimes;
			
			if(balanceTimes <= 0){
				dr.setDes(Lan.LanguageFormat(lan , "当前账号验证输入错误次数过多,已被锁定。", ""));
				return dr;
			}

			//发送验证码的时间
			long codeTime = numberSsi.codeTime;
			if((currentTime - codeTime) < resendtimes){//验证码发送超过1分钟之后才重新发送 add comment by xwz
				long second = 60-((currentTime-codeTime)/1000);
				if(second > 1){
					dr.setDes(Lan.LanguageFormat(lan , "重复提交，请等待%%秒后再次尝试s" , second+""));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "重复提交，请等待%%秒后再次尝试" , second+""));
				}
				return dr;
			}
			
			if(!deviceNumber.startsWith("+86") && !graphicalCode && rs == 0){
				dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码！" , ""));
				dr.setDataStr("{\"graphicalCode\" : true}");
				return dr;
			}
		}
		
		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);
		
		CodeInfo ipcodeInfo = null;
		if(ipSsi == null){
			ipSsi = new SessionInfo();
			ipSsi.ip = ip;
			ipSsi.codeTime = 0;
			ipcodeInfo = new CodeInfo();
			ipcodeInfo.type = type;
			ipcodeInfo.times = 0;
			ipSsi.codeInfos.put(type, ipcodeInfo);
			ipSsi.startTime = currentTime;
		}else{
			/***
			  单日发送量16个
			 6次时弹出图形验证码
			 */
			ipcodeInfo = ipSsi.codeInfos.get(type);
			if(ipcodeInfo == null){
				ipcodeInfo = new CodeInfo();
				ipSsi.codeInfos.put(type, ipcodeInfo);
			}
			int hasCheckedNumbers = 0;
			if(ipSsi.hasCheckedNumbers == null){
				ipSsi.hasCheckedNumbers = "";
			}else{
				hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
			}
			//当前ip验证是否注册过的所有手机号码24h不得超过30个
			if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
				log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
				//dr.setDes(Lan.LanguageFormat(lan , "您所在的ip短时间内验证的设备号码验证已超过"+IP_MAX_CHECKER_TIMES+"个。" , ""));
				dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , IP_MAX_CHECKER_TIMES+""));
				return dr;
			}
			
			if(ipcodeInfo.times > IP_MAX_SEND_TIMES){
				dr.setDes(Lan.LanguageFormat(lan , "今日发送已超过规定的次数，不能再发送。" , ""));
				return dr;
			}
			if(ipcodeInfo.times > 6 && !graphicalCode && rs == 0){
				dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码。" , "{\"graphicalCode\" : true}"));
				return dr;
			}
		}
		
		reSaveSessionInfo(deviceNumber , numberSsi);
		reSaveSessionInfo(ip , ipSsi);
		
		dr.setSuc(true);
		return dr;
	}
	
	
	public boolean sendCode(String mobileCode){
		long currentTime = System.currentTimeMillis();
		SessionInfo numberSsi = getSessionInfo(deviceNumber);
		CodeInfo codeInfo = numberSsi.codeInfos.get(type);
		if(codeInfo != null){
			codeInfo.times++;
			codeInfo.lastCode = mobileCode;
			codeInfo.mobileOrEmail = deviceNumber;
			codeInfo.lastDate = currentTime;
			/*codeInfo.lastHasValidateTimes = 0;*/
			codeInfo.type = type;
			numberSsi.codeTime = currentTime;
			reSaveSessionInfo(deviceNumber , numberSsi);
			return true;
		}
		return false;
	}

    public boolean sendCodeWithTime(String mobileCode, int sessionMaxTimes){
        long currentTime = System.currentTimeMillis();
        SessionInfo numberSsi = getSessionInfo(deviceNumber);
        CodeInfo codeInfo = numberSsi.codeInfos.get(type);
        if(codeInfo != null){
            codeInfo.times++;
            codeInfo.lastCode = mobileCode;
            codeInfo.mobileOrEmail = deviceNumber;
            codeInfo.lastDate = currentTime;
            /*codeInfo.lastHasValidateTimes = 0;*/
            codeInfo.type = type;
            numberSsi.codeTime = currentTime;
            reSaveSessionInfoWithTime(deviceNumber , numberSsi, sessionMaxTimes);
            return true;
        }
        return false;
    }

	/**
	 * 检测验证码是否合法,不清除缓存
	 * @param code
	 * @param flag trueq缓存，false:清除缓存
     * @return
     */
	public DataResponse checkCode(String code,boolean flag){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
		if(!(code.length() == 6 && StringUtils.isNumeric(code))){
			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，六位数字！" , ""));
			return dr;
		}

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}

//		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > 10 && !graphicalCode){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码。" , "{\"graphicalCode\" : true}"));
//			return dr;
//		}

		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		if(!ci.mobileOrEmail.equals(deviceNumber)){
			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
			return dr;
		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "验证码失效，请重新发送验证码。" , ""));
			return dr;
		}

		long balanceTimes = codeAvailableMAXTimes - ci.lastHasValidateTimes;

		if(balanceTimes <= 0){
			dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
			return dr;
		}
		if(!ci.lastCode.equals(code)){

			if(ci.lastHasValidateStrs == null){
				ci.lastHasValidateStrs = "";
			}

			if(ci.lastHasValidateStrs.indexOf(code) < 0){
				ci.lastHasValidateTimes++;
				ci.lastHasValidateStrs += " " + code;
				balanceTimes--;//本次的
				reSaveSessionInfo(deviceNumber, ssi);
			}
			if(balanceTimes > 0){
				if(balanceTimes > 1){
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(balanceTimes)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(balanceTimes)));
				}
			}else{
				dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
			}
			return dr;
		}else{
			if(flag){
				ci.lastCode = null;
			}
		}
		dr.setSuc(true);
		if(flag){
			ci = null;//清空
		}
		ci.lastHasValidateTimes = 0;//清除验证过的错误次数
		ci.lastHasValidateStrs = "";//清除验证过的字符串
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}

	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkCode(String code){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}

		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
				针对手机号：
				20min 内有效
				3次时弹出图形验证码
				最多验证6次
				ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		if(!ci.mobileOrEmail.equals(deviceNumber)){
			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
			return dr;
		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "验证码失效，请重新发送验证码。" , ""));
			return dr;
		}

		long balanceTimes = codeAvailableMAXTimes - ci.lastHasValidateTimes;

		if(balanceTimes <= 0){
			dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
			return dr;
		}

		if(!ci.lastCode.equals(code)){

			if(ci.lastHasValidateStrs == null){
				ci.lastHasValidateStrs = "";
			}

			//if(ci.lastHasValidateStrs.indexOf(code) < 0){
				ci.lastHasValidateTimes++;
				ci.lastHasValidateStrs += " " + code;
				balanceTimes--;//本次的
				reSaveSessionInfo(deviceNumber, ssi);
			//}
			//2017.08.15 xzhang 修改重置登录密码或资金交易密码提示信息错误问题；BITA-514
			if (balanceTimes > 0) {
				if(balanceTimes > 1){
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(balanceTimes)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(balanceTimes)));
				}
			} else {
				dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
				return dr;
			}

			return dr;
		}else{
			ci.lastCode = null;
			ci.lastHasValidateTimes = 0;//清除验证过的错误次数
			ci.lastHasValidateStrs = "";//清除验证过的字符串
		}

		dr.setSuc(true);
		ci = null;//清空
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}


	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkOnlyCode(String code){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
		/*if(!(code.length() == 4 && StringUtils.isNumeric(code))){
			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，4位数字！" , ""));
			return dr;
		}*/

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}

//		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > 10 && !graphicalCode){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码。" , "{\"graphicalCode\" : true}"));
//			return dr;
//		}

		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		if(!ci.mobileOrEmail.equals(deviceNumber)){
			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
			return dr;
		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "验证码失效，请重新发送验证码。" , ""));
			return dr;
		}

		long balanceTimes = codeAvailableMAXTimes - ci.lastHasValidateTimes;
		if(balanceTimes <= 0){
			dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
			return dr;
		}

		if(!ci.lastCode.equals(code)){

			if(ci.lastHasValidateStrs == null){
				ci.lastHasValidateStrs = "";
			}
				ci.lastHasValidateTimes++;
				ci.lastHasValidateStrs += " " + code;
				balanceTimes--;//本次的
				reSaveSessionInfo(deviceNumber, ssi);
			//2017.08.15 xzhang 修改重置登录密码或资金交易密码提示信息错误问题；BITA-514
//			dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，您还有%%次机会" , String.valueOf(balanceTimes)));
			if(balanceTimes > 0) {
				if(balanceTimes > 1){
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(balanceTimes)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(balanceTimes)));
				}
			}else{
				dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
				return dr;
			}

			return dr;
		}else{
			ci.lastCode = null;
		}

		dr.setSuc(true);
		return dr;
	}


    /***
     * 检测验证码是否合法，自定义超时时间，自定义是否删除缓存
     * @param maxAliveTime 单位小时
     */
    public DataResponse checkCodeWithTime(String code, int maxAliveTime, boolean isDelete){
        long currentTime = System.currentTimeMillis();
        DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
        ///ip发送限制验证
        SessionInfo ipSsi = getSessionInfo(ip);

        if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。" , ""));
			return dr;
		}

        int hasCheckedNumbers = 0;
        if(ipSsi.hasCheckedNumbers == null){
            ipSsi.hasCheckedNumbers = "";
        }else{
            hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
        }

        //当前ip验证是否注册过的所有手机号码24h不得超过30个
        if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
            log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
            dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
            return dr;
        }

        SessionInfo ssi = getSessionInfo(deviceNumber);
        if(ssi == null){
            dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
            return dr;
        }

        CodeInfo ci = ssi.codeInfos.get(type);

        if(ci == null || ci.lastCode == null){
            dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
            return dr;
        }

        if(!ci.mobileOrEmail.equals(deviceNumber)){
            dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
            return dr;
        }

        if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
            dr.setDes(Lan.LanguageFormat(lan , "验证码失效，请重新发送验证码。" , ""));
            return dr;
        }

        long balanceTimes = codeAvailableMAXTimes - ci.lastHasValidateTimes;
		log.info("我就打印出来看看到底是个啥"+balanceTimes);
        if(balanceTimes <= 0){
            dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请%%小时后重试。" , maxAliveTime + ""));
            return dr;
        }
		log.info("我就打印出来看看到底是个啥2"+ci.lastCode+"这次的是"+code);
        if(!ci.lastCode.equals(code)){
            if(ci.lastHasValidateStrs == null){
                ci.lastHasValidateStrs = "";
            }
            ci.lastHasValidateTimes++;
            ci.lastHasValidateStrs += " " + code;
            balanceTimes--;//本次的
            reSaveSessionInfoWithTime(deviceNumber, ssi, maxAliveTime * 60 * 60 * 1000);
            if(balanceTimes > 0) {
            	if(balanceTimes > 1){
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(balanceTimes)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(balanceTimes)));
				}
            }else{
                dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请%%小时后重试。" , maxAliveTime + ""));
                return dr;
			}

			return dr;
        }else{
            ci.lastCode = null;
        }

        dr.setSuc(true);
        if(isDelete){
            ci = null;//清空
            reSaveSessionInfoWithTime(deviceNumber, ssi, maxAliveTime * 60 * 60 * 1000);
        }
        return dr;
    }


    public void deleteSession(int maxAliveTime){
		SessionInfo ssi = getSessionInfo(deviceNumber);
		if(null == ssi){
			return;
		}
		CodeInfo ci = ssi.codeInfos.get(type);
		if(ci == null){
			return;
		}
		ci.lastHasValidateTimes = 0;
		ci = null;//清空
		reSaveSessionInfoWithTime(deviceNumber, ssi, maxAliveTime * 60 * 60 * 1000);
	}

	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkCodeNew(String userId,String code,LimitType lt,String functionName,String lockTime){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
//		if(!(code.length() == 4 && StringUtils.isNumeric(code))){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，4位数字！" , ""));
//			return dr;
//		}

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}


		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

//		if(!ci.mobileOrEmail.equals(deviceNumber)){
//			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
//			return dr;
//		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。" , ""));
			return dr;
		}

		int status=lt.GetStatusNew(userId);
		if(status==-1){
            dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
			return dr;
		}

		if(ci.lastCode.equals(code)){
			//输入正确了，清除
			lt.ClearStatus(userId);
			ci.lastCode = null;
		}else{
			//记录一次
			lt.UpdateStatus(userId);
			status = lt.GetStatusNew(userId);
			//已经锁定
			if(status == -1){
                dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
				return dr;
			}else{
				if(status > 1){
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(status)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(status)));
				}
				return dr;
			}
		}


		dr.setSuc(true);
		//清空
		ci = null;
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}

	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkCodeApp(String userId,String code,LimitType lt,String functionName,String lockTime){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
//		if(!(code.length() == 4 && StringUtils.isNumeric(code))){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，4位数字！" , ""));
//			return dr;
//		}

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}


		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。", ""));
			return dr;
		}

//		if(!ci.mobileOrEmail.equals(deviceNumber)){
//			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
//			return dr;
//		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "请获取短信验证码。" , ""));
			return dr;
		}

		int status=lt.GetStatusNew(userId);
		if(status==-1){
			//锁定
			dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
			return dr;
		}

		if(ci.lastCode.equals(code)){
			//输入正确了，清除
			lt.ClearStatus(userId);
			ci.lastCode = null;
		}else{
			//记录一次
			lt.UpdateStatus(userId);
			status = lt.GetStatusNew(userId);
			//已经锁定
			if(status == -1){
				//锁定
				dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
				return dr;
			}else{
				if(status > 1){
					dr.setDes(Lan.LanguageFormat(lan , "短信验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(status)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "短信验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(status)));
				}
				return dr;
			}
		}


		dr.setSuc(true);
		//清空
		ci = null;
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}

	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkCodeMailForget(String code){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
/*		if(!(code.length() == 6 && StringUtils.isNumeric(code))){
			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，6位数字！" , ""));
			return dr;
		}*/

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取邮箱验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}

//		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > 10 && !graphicalCode){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码。" , "{\"graphicalCode\" : true}"));
//			return dr;
//		}

		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个。", String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		if(!ci.mobileOrEmail.equals(deviceNumber)){
			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
			return dr;
		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "验证码失效，请重新发送验证码。" , ""));
			return dr;
		}

		long balanceTimes = codeAvailableMAXTimes - ci.lastHasValidateTimes;

		if(balanceTimes <= 0){
			dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
			return dr;
		}

		if(!ci.lastCode.equals(code)){

			if(ci.lastHasValidateStrs == null){
				ci.lastHasValidateStrs = "";
			}
			ci.lastHasValidateTimes++;
			ci.lastHasValidateStrs += " " + code;
			balanceTimes--;//本次的
			reSaveSessionInfo(deviceNumber, ssi);
			//2017.08.15 xzhang 修改重置登录密码或资金交易密码提示信息错误问题；BITA-514
//			dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，您还有%%次机会" , String.valueOf(balanceTimes)));
			if(balanceTimes > 0) {
				if(balanceTimes > 1){
					dr.setDes(Lan.LanguageFormat(lan , "邮箱验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(balanceTimes)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "邮箱验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(balanceTimes)));
				}
			}else{
				dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
				return dr;
			}

			return dr;
		}else{
			ci.lastCode = null;
		}

		dr.setSuc(true);
		ci = null;//清空
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}





















	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkCodeMail(String code){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
/*		if(!(code.length() == 6 && StringUtils.isNumeric(code))){
			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，6位数字！" , ""));
			return dr;
		}*/

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取邮箱验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}

//		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > 10 && !graphicalCode){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码。" , "{\"graphicalCode\" : true}"));
//			return dr;
//		}

		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个。" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);
		log.info("重新获取验证码以后的次数"+ci.lastHasValidateTimes);
		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		if(!ci.mobileOrEmail.equals(deviceNumber)){
			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
			return dr;
		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "验证码失效，请重新发送验证码。" , ""));
			return dr;
		}

		long balanceTimes = codeAvailableMAXTimes - ci.lastHasValidateTimes;

		if(balanceTimes <= 0){
			dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
			return dr;
		}

		if(!ci.lastCode.equals(code)){

			if(ci.lastHasValidateStrs == null){
				ci.lastHasValidateStrs = "";
			}
				ci.lastHasValidateTimes++;
				ci.lastHasValidateStrs += " " + code;
				balanceTimes--;//本次的
				reSaveSessionInfo(deviceNumber, ssi);
			//2017.08.15 xzhang 修改重置登录密码或资金交易密码提示信息错误问题；BITA-514
//			dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，您还有%%次机会" , String.valueOf(balanceTimes)));
			if(balanceTimes > 0) {
				if(balanceTimes > 1){
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(balanceTimes)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(balanceTimes)));
				}
			}else{
				dr.setDes(Lan.LanguageFormat(lan , "验证码输入错误次数过多,已被锁定,请24小时后重试。" , ""));
				return dr;
			}

			return dr;
		}else{
			ci.lastCode = null;
			ci.lastHasValidateTimes = 0;//清除验证过的错误次数
			ci.lastHasValidateStrs = "";//清除验证过的字符串
		}

		dr.setSuc(true);
		ci = null;//清空
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}

	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkCodeMailNew(String userId,String code,LimitType lt,String functionName,String lockTime){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
//		if(!(code.length() == 6 && StringUtils.isNumeric(code))){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，6位数字！" , ""));
//			return dr;
//		}

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}


		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。", ""));
			return dr;
		}

//		if(!ci.mobileOrEmail.equals(deviceNumber)){
//			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
//			return dr;
//		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "请获取验证码。" , ""));
			return dr;
		}

		int status=lt.GetStatusNew(userId);
		if(status==-1){
            dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
			return dr;
		}

		if(ci.lastCode.equals(code)){
			//输入正确了，清除
			lt.ClearStatus(userId);
			ci.lastCode = null;
		}else{
			//记录一次
			lt.UpdateStatus(userId);
			status = lt.GetStatusNew(userId);
			//已经锁定
			if(status == -1){
				dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
				return dr;
			}else{
				if(status > 1){
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(status)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(status)));
				}
				return dr;
			}
		}
		dr.setSuc(true);
		//清空
		ci = null;
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}

	/***
	 * 检测验证码是否合法,重置缓存验证码(验证码二次验证失效)
	 */
	public DataResponse checkCodeMailApp(String userId,String code,LimitType lt,String functionName,String lockTime){

		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"vercode\"}");
		//格式验证
//		if(!(code.length() == 6 && StringUtils.isNumeric(code))){
//			dr.setDes(Lan.LanguageFormat(lan , "请输入您收到的验证码，6位数字！" , ""));
//			return dr;
//		}

		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);

		if(ipSsi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取邮箱验证码。" , ""));
			return dr;
		}

		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}


		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , String.valueOf(IP_MAX_CHECKER_TIMES)));
			return dr;
		}

		SessionInfo ssi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
		 针对手机号：
		 20min 内有效
		 3次时弹出图形验证码
		 最多验证6次
		 ip验证是否和发送匹配
		 */
		if(ssi == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取邮箱验证码。", ""));
			return dr;
		}

		CodeInfo ci = ssi.codeInfos.get(type);

		if(ci == null || ci.lastCode == null){
			dr.setDes(Lan.LanguageFormat(lan , "请获取邮箱验证码。", ""));
			return dr;
		}

//		if(!ci.mobileOrEmail.equals(deviceNumber)){
//			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
//			return dr;
//		}

		if((currentTime - ci.lastDate) > codeAvailableMAXMillSeconds){
			dr.setDes(Lan.LanguageFormat(lan , "请获取邮箱验证码。" , ""));
			return dr;
		}

		int status=lt.GetStatusNew(userId);
		if(status==-1){
			//锁定
			dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
			return dr;
		}

		if(ci.lastCode.equals(code)){
			//输入正确了，清除
			lt.ClearStatus(userId);
			ci.lastCode = null;
		}else{
			//记录一次
			lt.UpdateStatus(userId);
			status = lt.GetStatusNew(userId);
			//已经锁定
			if(status == -1){
				//锁定
				dr.setDes(Lan.LanguageFormat(lan , String.format("验证码输入次数超出限制，将锁定%s功能，请%s小时之后再试。", functionName, lockTime), ""));
				return dr;
			}else{
				if(status > 1){
					dr.setDes(Lan.LanguageFormat(lan , "邮箱验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会s" , String.valueOf(status)));
				}else{
					dr.setDes(Lan.LanguageFormat(lan , "邮箱验证码输入有误，" , "")+Lan.LanguageFormat(lan , "您还有%%次机会。" , String.valueOf(status)));
				}
				return dr;
			}
		}
		dr.setSuc(true);
		//清空
		ci = null;
		reSaveSessionInfo(deviceNumber, ssi);
		return dr;
	}

	/****
	 * 检测密码的有效性
	 * @param inPwd
	 * @param encrytedPwd
	 * @param userPwd
	 * @return
	 */
	public DataResponse checkPwd(String inPwd, String encrytedPwd, String userPwd){
		long currentTime = System.currentTimeMillis();
		DataResponse dr = new DataResponse("", false, "{\"id\" : \"pwd\"}");
		
		//格式验证
		if(inPwd.length() < 6){
			dr.setDes(Lan.LanguageFormat(lan , "密码输入有误，请重新输入。" , ""));
			return dr;
		}
		
		///ip发送限制验证
		SessionInfo ipSsi = getSessionInfo(ip);
		
		if(ipSsi == null){
			ipSsi = new SessionInfo();
			ipSsi.ip = ip;
			ipSsi.codeTime = 0;
			ipSsi.startTime = currentTime;
		}
		
		int hasCheckedNumbers = 0;
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}else{
			hasCheckedNumbers = ipSsi.hasCheckedNumbers.split(",").length;
		}
		
		//验证密码是否存在
		if(userPwd == null || userPwd.length() <= 0){
			dr.setDes(Lan.LanguageFormat(lan , "您还未设置登录密码!" , ""));
			return dr;
		}
		
		if(hasCheckedNumbers > 10 && !graphicalCode){
			dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码。" , ""));
			dr.setDataStr("{\"graphicalCode\" : true}");
			return dr;
		}
		
		//当前ip验证是否注册过的所有手机号码24h不得超过30个
		if((ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0) && hasCheckedNumbers > IP_MAX_CHECKER_TIMES){
			log.error("ip:" + ip +",已验证号码：" + ipSsi.hasCheckedNumbers);
			dr.setDes(Lan.LanguageFormat(lan , "您所在的IP短时间内验证的设备号码验证已超过%%个" , IP_MAX_CHECKER_TIMES+""));
			return dr;
		}
		
		SessionInfo numberSsi = getSessionInfo(deviceNumber);
		/***
		 * //// 检测验证码
				针对手机号：
				20min 内有效
				3次时弹出图形验证码
				最多验证6次
				ip验证是否和发送匹配
		 */
		
		CodeInfo codeInfo = null;
		if(numberSsi == null){
			numberSsi = new SessionInfo();
			numberSsi.ip = ip;
			numberSsi.codeTime = 0;
			numberSsi.startTime = currentTime;
		}else{
			codeInfo = numberSsi.codeInfos.get(type);
		}
		
		if(codeInfo == null){
			codeInfo = new CodeInfo();
			codeInfo.type = type;
			codeInfo.times = 0;
			codeInfo.mobileOrEmail = deviceNumber;
			numberSsi.codeInfos.put(type, codeInfo);
		}
		
		if(!codeInfo.mobileOrEmail.equals(deviceNumber)){
			dr.setDes(Lan.LanguageFormat(lan , "未知错误，注册失败。" , ""));
			return dr;
		}
		
		long balanceTimes = pwdAvailableMAXTimes - codeInfo.lastHasValidateTimes;
		
		if(balanceTimes <= 0){
			dr.setDes(Lan.LanguageFormat(lan , "验证输入错误次数过多,已被锁定。" , ""));
			return dr;
		}
		//已验证超过3次  且 没有填写图形验证码
		if(codeInfo.lastHasValidateTimes >= 3 && !graphicalCode) {
			dr.setDes(Lan.LanguageFormat(lan , "请输入图形验证码。" , ""));
			dr.setDataStr("{\"graphicalCode\" : true}");
			return dr;
		}
		
		if(!encrytedPwd.equals(userPwd)){
			if(codeInfo.lastHasValidateStrs == null){
				codeInfo.lastHasValidateStrs = "";
			}
			
			if(codeInfo.lastHasValidateStrs.indexOf(encrytedPwd) < 0){
				codeInfo.lastHasValidateTimes++;
				codeInfo.lastHasValidateStrs += " " + encrytedPwd;
				balanceTimes--;//本次的
				reSaveSessionInfo(deviceNumber, numberSsi);
			}
			if(balanceTimes > 1){
				dr.setDes(Lan.LanguageFormat(lan ,"密码输入有误,您还有%%次机会s。",balanceTimes+""));
			}else{
				dr.setDes(Lan.LanguageFormat(lan ,"密码输入有误,您还有%%次机会。",balanceTimes+""));
			}
			
			return dr;
		}
		
		dr.setSuc(true);
		codeInfo = null;//清空
		reSaveSessionInfo(ip, ipSsi);
		reSaveSessionInfo(deviceNumber, numberSsi);
		return dr;
	}
	
	
	public boolean reSaveSessionInfo(String sessionId, SessionInfo ssi){
//		sessionId = sessionId.replace(" ", "");
//		long currentTime = System.currentTimeMillis();
//		long hasTimes = currentTime - ssi.startTime;
//		int balanceSeconds = sessionMAXTimes / 1000;
//
//		if(hasTimes < sessionMAXTimes){
//			balanceSeconds = (int)(sessionMAXTimes - hasTimes) / 1000;
//		}
//		String sessionInfoId = clientSessionPre + sessionId;
//
//		return Cache.SetObj(sessionInfoId, ssi, balanceSeconds);
		return reSaveSessionInfoWithTime(sessionId, ssi, sessionMAXTimes);
	}

    public boolean reSaveSessionInfoWithTime(String sessionId, SessionInfo ssi, int sessionMaxTimes){
        sessionId = sessionId.replace(" ", "");
        long currentTime = System.currentTimeMillis();
        long hasTimes = currentTime - ssi.startTime;
        int balanceSeconds = sessionMaxTimes / 1000;

        if(hasTimes < sessionMaxTimes){
            balanceSeconds = (int)(sessionMaxTimes - hasTimes) / 1000;
        }
        String sessionInfoId = clientSessionPre + sessionId;

        return Cache.SetObj(sessionInfoId, ssi, balanceSeconds);
    }
	
	public SessionInfo getSessionInfo(String sessionId){
		sessionId = sessionId.replace(" ", "");
		String sessionInfoId = clientSessionPre + sessionId;
		SessionInfo ssi = Cache.T(sessionInfoId);
		return ssi;
	}
	
	public boolean deleteSessionInfo(String sessionId){
		sessionId = sessionId.replace(" ", "");
		String sessionInfoId = clientSessionPre + sessionId;
		return Cache.Delete(sessionInfoId);
	}
	
	/***
	 * 对当前ip添加手机验证
	 */
	public void addCheckNumber() {
		SessionInfo ipSsi = getSessionInfo(ip);
		long currentTime = System.currentTimeMillis();
		
		CodeInfo ipcodeInfo = null;
		if(ipSsi == null){
			ipSsi = new SessionInfo();
			ipSsi.ip = ip;
			ipSsi.codeTime = 0;
			ipcodeInfo = new CodeInfo();
			ipcodeInfo.type = type;
			ipcodeInfo.times = 0;
			ipSsi.codeInfos.put(type, ipcodeInfo);
			ipSsi.startTime = currentTime;
		}
		if(ipSsi.hasCheckedNumbers == null){
			ipSsi.hasCheckedNumbers = "";
		}
		
		if(ipSsi.hasCheckedNumbers.indexOf(deviceNumber) < 0){//不包含
			ipSsi.hasCheckedNumbers += "," + deviceNumber;
			reSaveSessionInfo(ip, ipSsi);
		}
	}
}
