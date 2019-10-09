package com.world.model.entity.level;

import java.math.BigDecimal;

import com.world.config.GlobalConfig;
import com.world.model.dao.jifen.JifenDao;
import com.world.model.entity.SysEnum;
import com.world.util.date.TimeUtil;

/**
 *	积分类型 
 */
public enum JifenType implements SysEnum{
	
	//-1
	//首次积分
	/*  register(1,"注册","注册",						"1000","1000","1",1),
		bindMobile(1,"绑定手机","绑定手机",			"1000","1000","1"),
        emailCheck(2,"验证邮箱","验证邮箱",			"1000","1000","1"),
        safePassword(3,"设置资金安全密码","设置资金安全密码",	"1000","1000","1"),
        deepAuthen(4,"高级实名认证","高级实名认证",		"2000","2000","1"),
        firstCharge(5,"首次充值",	"首次充值",			"2000","2000","1"),
        originUser(6,"老用户初始积分","老用户初始积分",	"0","0","0"),
        googleAuth(7,"Google认证","老Google认证初始积分",	"1000","1000","1"),
        login(10,"登录","每天首次登录获得积分",			"10",	"70",	"1"),
        charge(11,"充值","当天充值累计积分",			"1",	"0",	"100"),*/
	//	trans(12,"交易",	"当天交易累计积分",			"1",	"50000",    "0.001"),
	//	netAsset(13,"净资产额度","昨天净资产折合积分",	"1",	"0",	"0.002")
		trans(12,"交易",	"当天交易累计积分",			"1",	"5000",    "4"),//100个USDT送25积分
		netAsset(13,"净资产额度","昨天净资产折合积分",	"1",	"0",	"10"),//100个USDT送10积分

		/*allBeginerTask(14,"额外奖励","完成全部新手任务额外奖励",	"10000",	"10000",	"1"),
		activity(15,"活动奖励","活动奖励",	"1",	"0",	"1"),

		expire(20,"积分过期","积分过期",						"0","0","0"),
		fastWithdraw(21,"快速提现积分","快速提现积分",	"0","0","0"),
		interestFree(22,"积分兑换免息卷","积分兑换免息卷",		"0","0","0"),*/
		admOper(30,"其它","后台积分操作",	"0","0","0"),
	;

	private int key;
	private String value;
	private String memo;
	private BigDecimal jifen;	//积分
	private BigDecimal max;		//每天最大额度
	private BigDecimal divisor;	//比率, 如:满100元+1分
	private int jifenCategory;	//积分类别（1：一次性（如绑定手机），2：周期性（如：登录），3：每次（如：充值获取积分））
	
	private JifenType(int key, String value,String memo, String jifen, String max,String divisor) {
		this.key = key;
		this.value = value;
		this.memo = memo;
		this.jifen = new BigDecimal(jifen);
		this.max = new BigDecimal(max);
		this.divisor = new BigDecimal(divisor);
	}
	private JifenType(int key, String value,String memo, String jifen, String max,String divisor,int jifenCategory) {
		this.key = key;
		this.value = value;
		this.memo = memo;
		this.jifen = new BigDecimal(jifen);
		this.max = new BigDecimal(max);
		this.divisor = new BigDecimal(divisor);
		this.jifenCategory = jifenCategory;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public BigDecimal getJifen() {
		if(JifenDao.isActivityDay()){
			if(key==11){
				return jifen.multiply(new BigDecimal(3));
			}else if (key==12) {
				return new BigDecimal(5);
			}else if (key==13) {
				return new BigDecimal(2);
			}else if (key==10) {
				return new BigDecimal(2000);
			}
		}
		
		
		return jifen;
	}
	public void setJifen(BigDecimal jifen) {
		this.jifen = jifen;
	}
	public BigDecimal getMax() {
		if(JifenDao.isActivityDay()){
			if(key==11){
				return max.multiply(new BigDecimal(3));
			}else if(key==10){
				return new BigDecimal(6000);
			}
		}
		return max;
	}
	public void setMax(BigDecimal max) {
		this.max = max;
	}
	public BigDecimal getDivisor() {
		return divisor;
	}
	public void setDivisor(BigDecimal divisor) {
		this.divisor = divisor;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getJifenCategory() {
		return jifenCategory;
	}

	public void setJifenCategory(int jifenCategory) {
		this.jifenCategory = jifenCategory;
	}
}
