package com.world.model;


import com.world.cache.Cache;

/**
 * 使用说明
 * @author btcboy
 *   输出：
 *          btc.Util.LimitType lt=btc.Util.LimitType.Login;
			log.info(lt);
           序列号：
            btc.Util.LimitType.Login.ordinal()
           对比：
            LimitType.Login.compareTo(LimitType.CreateAntique);  返回-1
枚举:
           LimitType[] types=LimitType.values();
           for(LimitType t:types){
              System.out.print(t+",");
           }
           //输出： Login,CreateAntique
          反解析：
          LimitType.valueOf(”Login“)；
                   返回   LimitType.Login
         switch：
             Color color=Color.RED;

　　          switch(color){

　        　  case RED: log.info("it's red");break;

　　           case BLUE: log.info("it's blue");break;

　　            case BLACK: log.info("it's blue");break;

　　    }
 */
public enum LimitType {
	//第一个值 是否需要一直保持，0代表保持一天，大于0代表保持这个时间之后解冻，单位分钟
	//第二个值代表需要显示验证码的地方，这里要求所有请求用户都是需要有用户名的
	//第三个值代表最大次数，就静止访问
	//连续错误登录次数限制
//	LoginLockError(120,0,5),
	//连续错误登录次数
	LoginError(120,3,5),//代表连续错误超过5次24小时内将被锁定，这几个值会保存到数据库中，也就是说可以单独对具体用户设置具体限制
	//安全验证码错误次数
	SafePassError(1440,0,5), //一旦锁定将锁定24小时，无需验证码，4次会锁定

	SafePassEntrustError(1440,0,5), //一旦锁定将锁定24小时，无需验证码，4次会锁定
	//注册
	RegisterPassError(120,0,5),
	//登录密码校验
	LoginPassError(1440,0,5),
	//修改登录密码时邮箱验证码输错三次锁定
	LoginEmailPassError(1440,0,5),
	//修改登录密码时手机验证码输错三次锁定
	LoginMobilePassError(1440,0,5),
	//修改登录密码时谷歌验证码输错三次锁定
	LoginGooglePassError(1440,0,5),

	//登录密码校验
	PayLoginPassError(1440,0,5),
	//修改资金密码时邮箱验证码输错三次锁定
	PayEmailPassError(1440,0,5),
	//修改资金密码时邮箱验证码输错三次锁定
	PayMobilePassError(1440,0,5),
	//修改资金密码时邮箱验证码输错三次锁定
	PayGooglePassError(1440,0,5),

	//开启手机验证手机验证码输错三次锁定
	OpenMobileVerifyError(1440,0,5),
	//开启谷歌验证手机验证码输错三次锁定
	OpenGoogleVerifyError(1440,0,5),

	//关闭手机验证邮箱验证码输错三次锁定
	CloseMobileVerifyEmailError(1440,0,5),
	//关闭手机验证手机验证码输错三次锁定
	CloseMobileVerifyMobileError(1440,0,5),

	//关闭谷歌验证邮箱验证码输错三次锁定
	CloseGoogleVerifyEmailError(1440,0,5),
	//关闭谷歌验证验证码输错三次锁定
	CloseGoogleVerifyGoogleError(1440,0,5),

	//设置个人信息手机验证码输错三次锁定
	SetUserInfoMobileError(1440,0,5),
	//设置个人信息登录密码输错三次锁定
	SetUserInfoLoginPwdError(1440,0,5),

	//修改手机手机验证码输错三次锁定
	UpdMobileError(1440,0,5),
	//修改手机手机校验验证码输错三次锁定
	UpdMobileCheckError(1440,0,5),

	//忘记密码邮箱验证码输错三次锁定
	ForgetPwdEmailError(1440,0,5),
	//忘记密码手机验证码输错三次锁定
	ForgetPwdMobileError(1440,0,5),
	//忘记密码谷歌验证码输错三次锁定
	ForgetPwdGoogleError(1440,0,5),

	//无法提供谷歌和短信验证码邮箱验证码输错三次锁定
	NoSecondVrifyEmailError(1440,0,5),
	//无法提供谷歌和短信验证码提现地址输错三次锁定
	NoSecondVrifyAddressError(1440,0,5),
	//无法提供谷歌和短信验证码资金密码输错三次锁定
	NoSecondVrifyPayPwdError(10,0,5),

	//登录手机验证码输错三次锁定
	LoginMobileError(120,0,5),
	//登录谷歌验证码输错三次锁定
	LoginGoogleError(120,0,5),


	//提现时手机验证码输错三次锁定
	WithdrawMobilePassError(1440,0,5),
	//提现时Google验证码输错三次锁定
	WithdrawGooglePassError(1440,0,5),
	//提现时邮箱输错三次锁定
	WithdrawEmailPassError(1440,0,5),
	//提现时资金密码输错三次锁定
	WithdrawPayPwdPassError(1440,0,5),

	//交易验证时资金密码输错三次锁定
	TransactionPayPwd(1440,0,5),
	//otc创建广告输错资金密码锁定
	OtcCadPayPwd(1440,0,5),
	//otc释放货币输错资金密码锁定
	OtcReleasecoinPayPwd(1440,0,5),
    
	withdrawZijin(1440,0,5),

	withdrawSms(1440,0,5),

	withdrawEmail(1440,0,5),

	withdrawGoogle(1440,0,5),
	addAddress(1440,0,5),
	//无法提供谷歌验证码锁定
	ForgetGoogle(1440,0,5),

	ForgetPassword(1440,0,5),
	//提现
	withdraw(1440,0,5);






	/**
	 * 构造函数
	 */
	private LimitType(int rv,int gv,int bv){
	this.limitTime=rv;
	this.imageCode=gv;
	this.limitForbid=bv;
}
    private int limitTime;  //限制时间。
	private int imageCode; //图像代码,0代表不限制
	private int limitForbid; //限制,0代表不限制
	
	public void UpdateStatus(int userId){
		UpdateStatus(userId+"");
	}
	/**
	 * 功能：更新并且获取当前枚举状态
	 * @param username 当前用户名或者id
	 * @return -1达到最大限额不可用，0需要显示验证码，>0为可用
	 */
	public void UpdateStatus(String username){
		//存储格式应该为：key： enum_username   value:当前次数_有效更新日期
		String key=this.toString()+"_"+username;
		String current=Cache.Get(key);
		if(current==null)
			Cache.Set(key,"1_"+System.currentTimeMillis());//比如记录错误登录次数，那么如果正确登录就不需要了
		else{
			//需要解析
			int currNum=Integer.parseInt(current.split("_")[0]);
			currNum++;
			long old=Long.parseLong(current.split("_")[1]);
			long now=System.currentTimeMillis();
			long sp=now-old;
			long minits=sp/(1000 * 60);//间隔的分钟数
			if(limitTime==0)
				limitTime=60*24;
				//按照时间来算，过期这个时间才会过期
			//log.info(currNum+":"+username);
				if(limitTime<=minits)
					//说明已经离上次记时过期了，可以重新开始了
					Cache.Set(key,"1_"+System.currentTimeMillis());//比如记录错误登录次数，那么如果正确登录就不需要了
				else
					Cache.Set(key,currNum+"_"+old);//增加次数，不更改时间
			
		}
		//return 0;
	}
	
	public int getRemainTime(String username){
		String key=this.toString()+"_"+username;
		String current=Cache.Get(key);
		if(current==null)
			return 0;
		else{
			//需要解析
			int currNum=Integer.parseInt(current.split("_")[0]);
			currNum++;
			long old=Long.parseLong(current.split("_")[1]);
			long now=System.currentTimeMillis();
			long sp=now-old;
			long minits=sp/(1000 * 60);//间隔的分钟数
			
			if(limitTime<=minits)
				return 0;
			else{
				return (int)(limitTime - minits)/60;
			}
		}
	}
	
	/**
	 * 功能：更新并且获取当前枚举状态
	 * @param username 当前用户名或者id
	 * @return -1达到最大限额不可用，0需要显示验证码，>0为可用
	 */
	public int GetStatus(String username){
		//存储格式应该为：key： enum_username   value:当前次数_有效更新日期
		String key=this.toString()+"_"+username;
//		log.info("主键值："+key);
		String current=Cache.Get(key);
		if(current==null)
		{
			Cache.Set(key,"0_"+System.currentTimeMillis());//比如记录错误登录次数，那么如果正确登录就不需要了
			return limitForbid;
		}
		else{
			//需要解析
			int currNum=Integer.parseInt(current.split("_")[0]);
			
			long old=Long.parseLong(current.split("_")[1]);
			long now=System.currentTimeMillis();
			long sp=now-old;
			long minits=sp/(1000 * 60);//间隔的分钟数
			   if(limitTime==0)
				   limitTime=60*24;
				//按照时间来算，过期这个时间才会过期
			   if(limitTime<=minits)
				{
					//说明已经离上次记时过期了，可以重新开始了
					Cache.Set(key,"0_"+System.currentTimeMillis());//比如记录错误登录次数，那么如果正确登录就不需要了
					return limitForbid;
				}else{
					//log.info(imageCode+":"+limitForbid);
					//还在计数期内
					
					if(limitForbid<=currNum)//说明已经禁止了
						return -1;//0;
					else if(imageCode<=currNum)//说明需要显示验证码并且
						   return 0;//currNum-limitForbid;
					else{//显示剩余次数
						return limitForbid-currNum;
				    }
				}
		}
		//return 0;
	}

	/*start by xwz 20170705*/
	public int GetStatusNew(String username){
		//存储格式应该为：key： enum_username   value:当前次数_有效更新日期
		String key=this.toString()+"_"+username;
//		log.info("主键值："+key);
		String current=Cache.Get(key);
		if(current==null)
		{
			Cache.Set(key,"0_"+System.currentTimeMillis());//比如记录错误登录次数，那么如果正确登录就不需要了
			return limitForbid;
		}
		else{
			//需要解析
			int currNum=Integer.parseInt(current.split("_")[0]);

			long old=Long.parseLong(current.split("_")[1]);
			long now=System.currentTimeMillis();
			long sp=now-old;
			long minits=sp/(1000 * 60);//间隔的分钟数
			if(limitTime==0)
				limitTime=60*24;
			//按照时间来算，过期这个时间才会过期
			if(limitTime<=minits)
			{
				//说明已经离上次记时过期了，可以重新开始了
				Cache.Set(key,"0_"+System.currentTimeMillis());//比如记录错误登录次数，那么如果正确登录就不需要了
				return limitForbid;
			}else{
				//log.info(imageCode+":"+limitForbid);
				//还在计数期内

				if(limitForbid<=currNum)//说明已经禁止了
					return -1;//0;
				else{//显示剩余次数
					return limitForbid-currNum;
				}
			}
		}
		//return 0;
	}
	/*end*/
	public int GetStatusAbs(int userId){
		return GetStatusAbs(userId+"");
	}
	public int GetStatusAbs(String username){
		int status= GetStatus(username);
		if(status<0)
			return -status;
		else
			return status;
	}
	/**
	 * 清理状态，比如错误登录状态就需要清理，不然下次还会累计
	 * @param username 用户名，也可以是用户id
	 */
	public void ClearStatus(String username){
          Cache.Delete(this.toString()+"_"+username);
	}
	public void ClearStatus(int userid){
        Cache.Delete(this.toString()+"_"+userid);
	}
}
