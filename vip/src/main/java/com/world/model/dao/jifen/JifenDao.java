package com.world.model.dao.jifen;

import com.world.config.GlobalConfig;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.authen.AuthenticationDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.level.IntegralRule;
import com.world.model.entity.level.Jifen;
import com.world.model.entity.level.JifenType;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.authen.Authentication;
import com.world.util.date.TimeUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class JifenDao extends DataDaoSupport<Jifen>{
	

	public static Map<Long,Map<String,BigDecimal>> userDayJifenMap = new HashMap<Long,Map<String,BigDecimal>>();
	private IntegralRuleDao ruleDao = new IntegralRuleDao();




//	private String getAddJifenLogSql(String batchId,String logType,int systemId,String systemName,String modularName,String modularType,String logContent,User user,String remark){
//		Date logTime = new Date();
//		//systemId+modularId+systemtime
//
//		String logId = systemId + systemName + logTime;
//		String jifenLogSql = "insert into sysLog(logId, batchId, logType, systemName, modularName, modularType, logContent, logUserId, logUserName, logTime, logIp,remark)values("
//					+ logId + "," + batchId + "," + logType + "," + systemName + "," + modularName + "," + modularType + "," + logContent + "," + user.getId() + "," + user.getUserName()
//					+ "," + user.getUserName() + "," + logTime + "," + user.getLoginIp() + "," + remark + ")";
//		return jifenLogSql;
//	}


	/**
	 * 按类型获得积分记录,如果是单例积分,获得唯一的积分记录,如果是每天积分,获得当天积分记录
	 */
	public Jifen findByType(JifenType type,String userId){
		boolean singleton = type.getKey()<10;
		String param =  singleton?"" : " and DATEDIFF(addtime,NOW())=0";
		return (Jifen) this.get("select * from jifen where type=? and userId=? " + param + " limit 1", new Object[]{type.getKey(),userId}, Jifen.class);
	}

/*	public Jifen findLoginYesterday(String userId){
		return (Jifen) this.get("select * from jifen where type=? and userId=?  and DATEDIFF(addtime,NOW())=-1 limit 1"
				, new Object[]{JifenType.login.getKey(),userId}, Jifen.class);
	}*/

	private BigDecimal getAddJifen(Jifen jifen ,JifenType type,BigDecimal multiplier, String userId) {
		BigDecimal addJifen = BigDecimal.ZERO;
		if(jifen==null){
			jifen = new Jifen();
		}

		/**start by gkl**修改日常交易的积分等级20190509**/
		if(type!=JifenType.trans){

			BigDecimal curJifen = type.getJifen();//积分数

			addJifen = multiplier.divide(type.getDivisor(),0,RoundingMode.DOWN).multiply(curJifen);
		}else{
			//每交易等价为100个USDT得10积分，每日最高5000积分
			IntegralRule rule = ruleDao.getBySeqNo(12);
			addJifen = multiplier.multiply(new BigDecimal(rule.getScore()));
            log.info("增加日常交易积分："+addJifen.toPlainString()+"积分规则："+rule.getScore());
		}
        log.info("日常交易积分："+addJifen.toPlainString());
		/**end**/
		if(type.getMax().compareTo(BigDecimal.ZERO)>0){
			//有上限
			if(jifen.getJifen().compareTo(type.getMax())<0){
				//未到上限
				if(addJifen.add(jifen.getJifen()).compareTo(type.getMax())>0){
					addJifen = type.getMax().subtract(jifen.getJifen());
				}
			}else{
				addJifen = BigDecimal.ZERO;
			}
		}
		return addJifen;
	}
	
	
	/**
	 * 按类型添加积分,方法内已实现对类型分值与上限的判断,按参数传入即可.如果是交易积分,净资产额度积分,需要传入交易额或净资产,其它传入null即可
	 * @param type	JifenType枚举类型
	 * @param userId	User
	 * @param multiplier	交易额或净资产数值 
	 */
	public void updateJifen(JifenType type,String userId,BigDecimal multiplier) {
		
		List<OneSql> paySqls = getAddJifenSqls(type, userId, multiplier);
		double addJifen = 0d;
		List<OneSql> deleteSqls = new ArrayList<>();
		if(paySqls!=null && paySqls.size()>0){
			//获取积分数据
			for(OneSql oneSql:paySqls){
				if(oneSql.getSql().equals("")){
					addJifen = Double.parseDouble(oneSql.getPrams()[0].toString());
					deleteSqls.add(oneSql);//移除sql为空的对象
				}
			}
			
			//移除积分SQL
			for(OneSql dsql:deleteSqls){
				paySqls.remove(dsql);
			}
		}
		
		
		
		if(Data.doTrans(paySqls)){
			
			UserDao userDao = new UserDao();
			userDao.updateUserJifen(userId, addJifen);
			/*if(isActivityDay()){
				//type.getKey()<6为新手任务
				if(type.getKey()<6){
					//全部完成
					if(allBeginerTaskAllDone(userId)){
						paySqls = getAddJifenSqls(JifenType.allBeginerTask, userId, null);
						if(Data.doTrans(paySqls)){
							log.info("\n ======== 完成全部新手任务额外奖励. 用户[" + userId + "]");
						}
					}
				}
			}*/
			
			log.info("\n ======== 添加" + type.getValue() + "积分成功. 用户[" + userId + "]");
//			log.info("\n ======== 添加积分成功. 用户[" + user.getId() + "," +user.getUserName() + "]增加" + type.getValue() + "类型积分:" + addJifen);
		}else{
			log.info("添加" + type.getValue() + "积分失败. 用户[" + userId + "]");
		}
	}
	
	private boolean allBeginerTaskAllDone(String userId) {
		User loginUser = new UserDao().getUserById(userId);
		UserContact uc = loginUser.getUserContact();
		int emailStatu = uc.getEmailStatu();
		int mobileStatu = uc.getMobileStatu();
		boolean hasSafePwd = loginUser.getHasSafePwd();
		boolean noauth = false;
		Authentication au = new AuthenticationDao().getByUserId(loginUser.getId());
		if(au == null || au.getStatus() != AuditStatus.pass.getKey()){
			noauth=true;
		}
		PayUserBean payUser = new  PayUserDao().getById(Integer.parseInt(userId),2);//这里需要改动资产类型
		int firstChargedFlag =payUser.getFirstChargedFlag();
		if(emailStatu==2 && mobileStatu==2 && hasSafePwd && !noauth && firstChargedFlag==1){
			return true;
		}
		return false;
	}

	public List<OneSql> getAddJifenSqls(JifenType type, String userId, BigDecimal multiplier) {
		List<OneSql> paySqls  = new ArrayList<>();
		
		if(type==JifenType.trans || type==JifenType.netAsset ){
			if(multiplier==null){
				throw new RuntimeException(" =========交易额或净资产数值未传入=========");
			}
		}else{
			multiplier = BigDecimal.ONE;
		}
		
		//获取积分类型对应的积分记录
		Jifen jifen = this.findByType(type,userId);
		BigDecimal addJifen = BigDecimal.ZERO;
		if(jifen==null){
			//添加
			addJifen = getAddJifen(null,type, multiplier, userId);
			if(addJifen.compareTo(BigDecimal.ONE) >= 0){
				UserDao userDao =  new UserDao();
				User user = userDao.getUserById(userId);
				jifen = new Jifen(userId, user.getUserName(), addJifen, type.getMemo(), type.getKey(), 0, now());



				//如果登录,更新连续登录天数
			/*	if(jifen.getType()==JifenType.login.getKey()){
				Jifen jifen2 = this.findLoginYesterday(userId);
				if(jifen2!=null){
					int continuityLoginTimes = jifen2.getContinuityLoginTimes();
					int next = continuityLoginTimes+1;
					jifen.setContinuityLoginTimes(next);
					jifen.setMemo("连续登录" + (next+1) + "天");

				}
			}
			if(isActivityDay()){
				if(jifen.getType()==JifenType.login.getKey()){
					int diffDay = TimeUtil.getDiffDay(TimeUtil.getTodayFirst(), new Timestamp(TimeUtil.parseDate(getBeginTime())));
					jifen.setMemo(jifen.getMemo() + "<br/>活动期连续登录" + (diffDay+1) + "天");
				}else if (jifen.getType()==JifenType.charge.getKey()) {
					jifen.setMemo(jifen.getMemo() + "<br/>活动期充值获3倍积分");
				}else if (jifen.getType()==JifenType.netAsset.getKey()) {
					jifen.setMemo(jifen.getMemo() + "<br/>活动期净资产积分翻倍");
				}else if (jifen.getType()==JifenType.trans.getKey()) {
					jifen.setMemo(jifen.getMemo() + "<br/>活动期交易获5倍积分");
				}
			}*/
				paySqls.add(this.getTransInsertSql(jifen));
				paySqls.add(new OneSql("",1, new Object[]{addJifen}));

				//如果是首充即更新首充标识
			/*if(jifen.getType()==JifenType.firstCharge.getKey()){
				PayUserBean payuser = new PayUserDao().getById(jifen.getUserId(),2);//这里2是先写死，要改动的
				if(payuser.getFirstChargedFlag()==0){
					paySqls.add(new OneSql("update pay_user set firstChargedFlag=1 where userId=?", 1, new Object[] {jifen.getUserId()}));
				}
			}*/
			}
		}else {
			//累加
			addJifen = getAddJifen(jifen, type, multiplier, userId);
			if(addJifen.compareTo(BigDecimal.ONE) >= 0){
				addJifen = addJifen.setScale(2, RoundingMode.DOWN);
				if(addJifen.compareTo(BigDecimal.ZERO)>0){
					if(type==JifenType.trans){
						paySqls.add(new OneSql("update jifen set jifen=jifen +? where id=?",1, new Object[]{addJifen,jifen.getId()}));
						paySqls.add(new OneSql("",1, new Object[]{addJifen}));
					}
				}
			}
		}
		return paySqls;
	}
	

	/**
	 * 按类型添加积分,方法内已实现对类型分值与上限的判断(此方法为简化方法,不需要传入基数的可使用,交易额或净资产不可用)
	 * @param type	JifenType枚举类型
	 * @param userId	User
	 */
	public void updateJifen(JifenType type,String userId) {
		this.updateJifen(type, userId, null);
	}
	
	/**
	 * 快速提现花费积分
	 */
	/*public List<OneSql> subtractCashJifen(User user,BigDecimal value) {
		
		return subtractJifen(user, value, JifenType.fastWithdraw.getMemo(), JifenType.fastWithdraw);
	}*/

	
	/**
	 * 扣积分
	 */
	public List<OneSql> subtractJifen(User user,BigDecimal value,String desc,JifenType type) {
		List<OneSql> sqls = new ArrayList<OneSql>();
		Jifen jifen = new Jifen();
		jifen.setUserId(user.getId());
		jifen.setUserName(user.getUserName());
		jifen.setJifen(value);
		jifen.setMemo(desc);
		jifen.setType(type.getKey());
		jifen.setIoType(1);
		jifen.setStatus(0);
		jifen.setAddTime(TimeUtil.getNow());
		
		sqls.add(this.getTransInsertSql(jifen));
		//sqls.add(new OneSql("update pay_user set totalJifen=totalJifen-? where  totalJifen>=? and user_Id=? ",1, new Object[]{value,value,user.getId()}));
		return sqls;
	}
	
	/**
	 * 加积分
	 */
	public List<OneSql> addJifen(User user,BigDecimal value,String desc,JifenType type) {
		List<OneSql> sqls = new ArrayList<OneSql>();
		Jifen jifen = new Jifen();
		jifen.setUserId(user.getId());
		jifen.setUserName(user.getUserName());
		jifen.setJifen(value);
		jifen.setMemo(desc);
		jifen.setType(type.getKey());
		jifen.setIoType(0);
		jifen.setStatus(0);
		jifen.setAddTime(TimeUtil.getNow());
		
		sqls.add(this.getTransInsertSql(jifen));
		//sqls.add(new OneSql("update pay_user set totalJifen=totalJifen+? where  totalJifen>=? and user_Id=? ",1, new Object[]{value,value,user.getId()}));
		return sqls;
	}
	
	
	/**
	 * 活动时间
	 */
	public static boolean isActivityDay(){
		return  (TimeUtil.getNow().compareTo(TimeUtil.getStringToDate(getBeginTime())) >= 0
				&& TimeUtil.getNow().compareTo(TimeUtil.getStringToDate(getEndTime())) < 0) ;
	}
	
	public static String getBeginTime() {
		return GlobalConfig.getValue("ActivityBeginTime")==null?"2017-02-20 12:00:00":GlobalConfig.getValue("ActivityBeginTime");
	}

	public static String getEndTime() {
		return GlobalConfig.getValue("ActivityEndTime")==null?"2017-02-20 12:00:00":GlobalConfig.getValue("ActivityEndTime");
	}
	
}
