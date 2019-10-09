package com.world.controller.manage.level;

import com.google.code.morphia.query.UpdateResults;
import com.world.cache.Cache;
import com.world.data.mysql.Query;
import com.world.model.dao.jifen.FuncJumpDao;
import com.world.model.dao.jifen.JifenDao;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.dao.level.UserVipLevelDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.Market;
import com.world.model.entity.level.*;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.util.CommonUtil;
import com.world.util.date.TimeUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.sso.SSOLoginManager;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能：vip等级
 */
public class Index extends ApproveAction {

	private static final double UpgradeCost = 888D;
	PayUserDao payUserDao = new PayUserDao();
	JifenDao jifenDao = new JifenDao();
	UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
	IntegralRuleDao integralRuleDao = new IntegralRuleDao();
	FuncJumpDao funcJumpDao = new FuncJumpDao();

//	@Page(Viewer = "/cn/manage/level/index.jsp")
	public void index() {
		initLoginUser();
		UserContact uc = loginUser.getUserContact();
		setAttr("curUser", loginUser);
		//handleJifen(userIdStr());

		List<UserVipLevel> userVipList =  userVipLevelDao.getList();
		/**
		 * 等级
		 */
		handleUpgradeVip(loginUser,userVipList);
		/*PayUserBean payUser = payUserDao.getById(userId());

		payUser = payUserDao.getById(userId());
		setAttr("payUser", payUser);*/

		int currentPointStart = 0;
		int nextid = loginUser.getVipRate()+1;
		/*modify by xwz 20170707*/
		if(nextid>10){
			nextid = 10;
		}
		/*end*/
		UserVipLevel nextVip = new UserVipLevel();
		if(userVipList!=null && userVipList.size()>0){
			for(UserVipLevel vipLevel:userVipList){
				if(vipLevel.getVipRate() == nextid){
					nextVip = vipLevel;
				}
				if (vipLevel.getVipRate() == loginUser.getVipRate()) {
					currentPointStart = vipLevel.getJifen();
					setAttr("currentPointStart", currentPointStart);
				}
			}
		}
		setAttr("nextVip", nextVip);

		if(loginUser.getVipRate()==6){
			//int diffDay = TimeUtil.getDiffDay(TimeUtil.getTodayFirst(payUser.getSuperVipTime()) , TimeUtil.getTodayFirst() );
			//setAttr("remainDay", diffDay);
		}

		/**
		 * 认证
		 */
	/*	Authentication au = new AuthenticationDao().getByUserId(loginUser.getId());
		if(au == null || au.getStatus() != AuditStatus.pass.getKey()){
			setAttr("noauth", true);
		}else{
			setAttr("sxbAuth", au.getSxbAuth());
		}

		if(uc.getIdCard() != null){
			setAttr("realName", loginUser.getRealName()==null||loginUser.getRealName().length()==0?null:loginUser.getRealName());
			setAttr("cardId", UserUtil.getShortCardId(uc.getIdCard()));
		}

		setAttr("userName", loginUser.getUserName());

		setAttr("hasSafe", loginUser.getHasSafePwd());
		setAttr("useSafePwd", loginUser.getUseSafePwd());

		int emailStatu = uc.getEmailStatu();
		int mobileStatu = uc.getMobileStatu();

		setAttr("emailStatu", emailStatu);
		setAttr("mobileStatu", mobileStatu);
		setAttr("googleAuth", uc.getGoogleAu());
		setAttr("loginAuth", uc.getLoginGoogleAuth());

		setAttr("showEmail", uc.getShowEmail());
		setAttr("showMobile", uc.getShowMobile());
		setAttr("showAuth", uc.getShowAuth());
		setAttr("showLoginAuth", uc.getShowLoginAuth());

		BigDecimal chargeJifen = BigDecimal.ZERO;
		Jifen jifenObj = jifenDao.findByType(JifenType.charge, loginUser.getId());
		if(jifenObj!=null){
			chargeJifen =jifenObj.getJifen();
		}
		setAttr("chargeJifen", chargeJifen);

		BigDecimal transJifen = BigDecimal.ZERO;
		Jifen jifenObj2 = jifenDao.findByType(JifenType.trans, loginUser.getId());
		if(jifenObj2!=null){
			transJifen =jifenObj2.getJifen();
		}
		setAttr("transJifen", transJifen);

		BigDecimal loginJifen = BigDecimal.ZERO;
		int continuityLoginTimes = 0;
		Jifen jifenObj4 = jifenDao.findByType(JifenType.login, loginUser.getId());
		if(jifenObj4!=null){
			loginJifen =jifenObj4.getJifen();
			continuityLoginTimes = jifenObj4.getContinuityLoginTimes();
		}
		setAttr("loginJifen", loginJifen);
		setAttr("continuityLoginTimes", continuityLoginTimes+1);

		BigDecimal netAssetJifen = BigDecimal.ZERO;
		Jifen jifenObj3 = jifenDao.findByType(JifenType.netAsset, loginUser.getId());
		if(jifenObj3!=null){
			netAssetJifen =jifenObj3.getJifen();
		}
		setAttr("netAssetJifen", netAssetJifen);

		*//**
		 * 明细
		 */
		EnumSet<JifenType> jifentypes = EnumUtils.getAll(JifenType.class);
		setAttr("jifentypes", jifentypes);
		/*
		//活动期
		boolean activityDay = JifenDao.isActivityDay();
		setAttr("activityDay", activityDay);

		setAttr("logintype", JifenType.login);
		setAttr("chargetype", JifenType.charge);
		setAttr("transtype", JifenType.trans);
		setAttr("netAssettype", JifenType.netAsset);*/
		setAttr("userVipList",userVipList);
		List<IntegralRule> integralRuleList = integralRuleDao.getList();

		setAttr("integralRuleList",integralRuleList);
		ajax();
		/*
		if (lan.equalsIgnoreCase("en")) {
			SetViewerPath("/en/u/level/index_en.jsp");
		}*/
	}

	@Page(Viewer = JSON)
	public void level() {
		List<UserVipLevel> userVipList =  userVipLevelDao.getList();

		Map<String, Object> levelResult = new HashMap<>(1);
		levelResult.put("userVipList", userVipList);

		json("ok", true, com.alibaba.fastjson.JSONObject.toJSONString(levelResult), true);
	}

	private UserDao userDao = new UserDao();

	private void handleJifen(String userId) {
	/*	User user = userDao.get(userId);
		UserContact uc = user.getUserContact();
		if (uc.getEmailStatu() == 2) {
			Bean jifen = jifenDao.get("select * from jifen where type=? and userId=?  limit 1"
					, new Object[]{JifenType.emailCheck.getKey(),userId}, Jifen.class);
			if (null == jifen) {
				jifenDao.updateJifen(JifenType.emailCheck, userId);
			}
		}
		if (uc.getMobileStatu() == 2) {
			Bean jifen = jifenDao.get("select * from jifen where type=? and userId=?  limit 1"
					, new Object[]{JifenType.bindMobile.getKey(),userId}, Jifen.class);
			if (null == jifen) {
				jifenDao.updateJifen(JifenType.bindMobile, userId);
			}
		}
		if (user.getHasSafePwd()) {
			Bean jifen = jifenDao.get("select * from jifen where type=? and userId=?  limit 1"
					, new Object[]{JifenType.safePassword.getKey(),userId}, Jifen.class);
			if (null == jifen) {
				jifenDao.updateJifen(JifenType.safePassword, userId);
			}
		}*/
	}

	private void handleUpgradeVip(User user,List<UserVipLevel> userVipList) {
		double totalJifen = user.getTotalJifen();
		VipRate oldVip = (VipRate)EnumUtils.getEnumByKey(user.getVipRate(), VipRate.class);
		if (totalJifen < 0) {
			totalJifen = 0;
			log.error("\n ==== 用户" + user.getUserName() + "的总积分为负数,请检查  ====");
		}
		if(oldVip==null){
			oldVip = VipRate.vip0;
		}

		BigDecimal feeDiscount = userVipLevelDao.getDiscountByVipRate(oldVip.getId());
		//List<UserVipLevel> userVipList =  userVipLevelDao.getList();
		VipRate newVip = userVipLevelDao.getVipRateByJiFen(BigDecimal.valueOf(totalJifen));
		if (newVip != oldVip) {
			// 需要upgrade
			feeDiscount = userVipLevelDao.getDiscountByVipRate(newVip.getId());
			user.setVipRate(newVip.getId());
			UpdateResults<User> ur = userDao.updateUserVipRate(user);
			if (ur.getUpdatedCount() > 0) {
				// 更新内存里,cookie里的vip等级
				SSOLoginManager.updateVip(user.getId() + "", newVip.getId());

			}
		}
		//缓存用户的手续费折扣率  为计算手续费准备
		Cache.SetObj("user_vip_fee_discount_"+user.getId(), feeDiscount);
	}

//	@Page(Viewer="/cn/manage/level/ajax.jsp")
	public void ajax() {

		//查询条件
		int currentPage = intParam("page");
		String tab = param("tab");
		String type = param("type");
		Timestamp timeS = dateParam("timeS");
		Timestamp timeE = dateParam("timeE");
		Query<Jifen> query = jifenDao.getQuery();
		List<Object> params = new ArrayList<Object>();

		query.setSql("select * from jifen where userId='"+userIdStr()+"' ");
		query.setCls(Jifen.class);
//		params.add(userIdStr());

		if(StringUtils.isBlank(tab)){
			tab = "all";
		}
		setAttr("tab", tab);
		if(tab.equals("in")){
			query.append("ioType=0");
		}else if(tab.equals("out")){
			query.append("ioType=1");
		}
		if(StringUtils.isNotBlank(type)){
			query.append("type = ?");
			params.add(Integer.valueOf(type));
		}
		/*add by xwz 后台操作积分加过滤*/
		query.append("type != 30");
		if(timeS!=null){
			query.append("addTime >=?");
			params.add(timeS);
		}
		if(timeE!=null){
			query.append("addTime <=?");
			params.add(TimeUtil.getTodayLast(timeE));
		}

		query.setParams(params.toArray());
		int total = query.count();
//		int total = 1000;
		query.append(" ORDER BY  id desc");
		if(total > 0){
			//分页查询
			List<Jifen> lists = jifenDao.findPage(currentPage, PAGE_SIZE);

			Map<String,String> ruleMap = integralRuleDao.getRuleMap();
			for(Jifen jifen : lists){
				String typeShow = ruleMap.get(jifen.getType() + "");
				if(StringUtil.exist(typeShow)){
					jifen.setTypeShowNew(L("积分规则-" + typeShow));
				}else{
					jifen.setTypeShowNew(L("积分留存"));
				}

			}

			for (Jifen item : lists) {
				if (item.getIoType()==0) {//add credit
					item.setMemo("加积分");
				} else {
					item.setMemo("扣积分");
				}
			}
			request.setAttribute("dataList", lists);
		}

		setPaging(total, currentPage);
	}

	/**
	 * 配合前端改造
	 */
	@Page(Viewer=JSON)
	public void ajaxJson() {

		//查询条件
		int currentPage = intParam("page");
		String tab = param("tab");
		String type = param("type");
		Timestamp timeS = dateParam("timeS");
		Timestamp timeE = dateParam("timeE");
		Query<Jifen> query = jifenDao.getQuery();
		List<Object> params = new ArrayList<Object>();

		Map<String, Object> result = new HashMap<>();

		query.setSql("select * from jifen where userId='"+userIdStr()+"' ");
		query.setCls(Jifen.class);
//		params.add(userIdStr());

		if(StringUtils.isBlank(tab)){
			tab = "all";
		}
		setAttr("tab", tab);
		if(tab.equals("in")){
			query.append("ioType=0");
		}else if(tab.equals("out")){
			query.append("ioType=1");
		}
		if(StringUtils.isNotBlank(type)){
			query.append("type = ?");
			params.add(Integer.valueOf(type));
		}
		/*add by xwz 后台操作积分加过滤*/
		query.append("type != 30");
		if(timeS!=null){
			query.append("addTime >=?");
			params.add(timeS);
		}
		if(timeE!=null){
			query.append("addTime <=?");
			params.add(TimeUtil.getTodayLast(timeE));
		}

		query.setParams(params.toArray());
		int total = query.count();
//		int total = 1000;
		query.append(" ORDER BY  id desc");
		if(total > 0){
			//分页查询
			List<Jifen> lists = jifenDao.findPage(currentPage, PAGE_SIZE);

			Map<String,String> ruleMap = integralRuleDao.getRuleMap();
			for(Jifen jifen : lists){
				String typeShow = ruleMap.get(jifen.getType() + "");
				if(StringUtil.exist(typeShow)){
					jifen.setTypeShowNew(L("积分规则-" + typeShow));
				}else{
					jifen.setTypeShowNew(L("积分留存"));
				}

			}

			for (Jifen item : lists) {
				if (item.getIoType()==0) {//add credit
					item.setMemo(L("加积分"));
				} else {
					item.setMemo(L("扣积分"));
				}
			}
			result.put("info", lists);
		}

		int pages;
		if(total % PAGE_SIZE == 0){
			pages = total / PAGE_SIZE;
		}else{
			pages = total / PAGE_SIZE + 1;
		}

		result.put("pageNo", currentPage);
		result.put("total", total);
		result.put("pageSize", PAGE_SIZE);
		result.put("pages", pages);

		json("success", true, com.alibaba.fastjson.JSONObject.toJSONString(result), true);
	}

	@Page(Viewer=JSON)
	public void getLevel() {
		initLoginUser();
		json("success",true,"{\"version\":\""+loginUser.getUserVersion().getValue()+"\"}",true);
	}

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer = "/cn/manage/level/upgradePage.jsp")
	public void upgradePage() {
	}

	@Page(Viewer=JSON)
	public void doUpgrade(){
		String safePwd = param("payPass");
		initLoginUser();


		try {
			/*暂时注释购买会员
			 * if(loginUser != null && safePwd.length() > 0){
				if(!safePwd(safePwd, loginUser.getId(), JSON)){
					return;
				}

				// 检查用户余额,升级vip6需要888元
				PayUserBean payUser = payUserDao.getById(userId());
				if (payUser.getBalance_Money() >= UpgradeCost) {

					// 获取扣费语句
					List<OneSql> paySqls = new ArrayList<OneSql>();
					paySqls.addAll(FundsConvertUtil.getInstance().assetChanges(CurrencyType.CNY, MergeType.MINUS, BillType.buyVip6,
							Integer.valueOf(loginUser.getId()), UpgradeCost, 0, 0, "购买升级vip6", "", "", true, ConsumeType.noType.getKey(), 0));

					Timestamp superVipTime = TimeUtil.getNow();
					if (payUser.getVipRate()==6) {
						superVipTime = payUser.getSuperVipTime();
					}
					superVipTime = TimeUtil.getAfterDayDate(superVipTime, 30);

					paySqls.add(new OneSql("update pay_user set viprate=6,superVipTime=?,vip6SmsStatus=0 where user_id=?",
						1,new Object[] { superVipTime, payUser.getUser_Id() }));


					if (Data.doTrans(paySqls)) {
						SSOLoginManager.updateVip(payUser.getUser_Id()+"", 6);

						log.info("\n=====升级付费VIP6成功");
						UserCache.resetUserFundsFromDatabase(loginUser.getId());
						if(payUser.getVipRate()==6){
							json(L("续费成功"), true, "");
						}else{
							json(L("升级付费VIP6成功"), true, "");
						}
						return;
					}else{
						log.info("\n=====升级失败");
						json(L("升级过程出现异常,请联系客服"), false, "");
						return;
					}
				}else{
					json(L("您的余额不足,请充值后再试"), false, "");
					return;
				}


			}else{
				if(StringUtils.isBlank(safePwd)){
					json(L("资金安全密码填写有误"), false, "");
				}else{
					json(L("用户已离线,请重新登录"), false, "");
				}
			}*/
		} catch (Exception e) {
			log.error("内部异常", e);
			json(L("升级过程出现异常,请联系客服"), false, "");

		}

	}

	// ================= 新增接口 =================

	/**
	 *
	 */
	@Page(Viewer = JSON)
	public void getLevelInfo() {
		//条形接口
		Map<String, Object> levelInfomap = new HashMap<>();
		/*Start by guankaili 20190403 优化查询逻辑 */
//		User u = userDao.getById(userIdStr());
		User u = userDao.findOne("_id", userIdStr());
		/*End*/
		if (u == null) {
			json("user must login", false, "");

		}
		int currentRate = u.getVipRate();
		double currentPoints = u.getTotalJifen();
		boolean isFull = false;
		double currentRateBeginPoint = 0;
		double nextRateBeginPoint = 0;
		int nextRate;

		List<UserVipLevel> userVipList =  userVipLevelDao.getList();

		//TODO 这里不做等级更新的操作,留给web处理 handleUpgradeVip()
		/*modify by xwz 20170707*/
		nextRate = (u.getVipRate() + 1) > 10 ? 10 : (u.getVipRate() + 1);

		if (currentRate > 0 && currentRate < 9) {
			nextRate = u.getVipRate() + 1;
		}

		if (currentRate == 9) {
			isFull = true;
		}
		/*end*/

		for (UserVipLevel level : userVipList) {
			if (level.getVipRate() == currentRate) {
				currentRateBeginPoint = level.getJifen();
			}
			if (level.getVipRate() == nextRate) {
				nextRateBeginPoint = level.getJifen();
			}
		}

		levelInfomap.put("currentRate", currentRate);
		levelInfomap.put("currentPoints", currentPoints);
		levelInfomap.put("nextRateBeginPoint", nextRateBeginPoint);
		levelInfomap.put("currentRateBeginPoint", currentRateBeginPoint);
		levelInfomap.put("nextRate", nextRate);
		levelInfomap.put("ifFull", isFull);


		json("", true, JSONObject.fromObject(levelInfomap).toString());
	}

	/*start by xzhang 20171026 新增用户手续费接口，预留扩展*/
	/**
	 *1、 返回用户手续费信息。
	 * 2、预留该接口扩展功能，作为后续字段扩展。作为用户明细统一出口
	 */
	@Page(Viewer = JSON)
	public void getUserInfo() {
		initLoginUser();
		Map<String, Object> map = new HashMap<>();
		int userId = userId();
		try{
			Map<String, com.alibaba.fastjson.JSONObject> marketMaps =  CommonUtil.sortMapByValue(Market.getMarketsMap());//获取盘口配置信息
			map.put("userId", userId);
			BigDecimal buyFeeRate = BigDecimal.ZERO;
			BigDecimal sellFeeRate = BigDecimal.ZERO;
			BigDecimal feeDiscount = BigDecimal.ZERO;
			Object feeDiscountObj = Cache.GetObj("user_vip_fee_discount_"+userId);
			if(feeDiscountObj == null){
				UserVipLevelDao userVipLevelDao = new UserVipLevelDao();
//				VipRate oldVip = (VipRate)EnumUtils.getEnumByKey(loginUser.getVipRate(), VipRate.class);
//				if(oldVip==null){
//					oldVip = VipRate.vip0;
//				}
				UserVipLevel userVipLevel = userVipLevelDao.getByVipRate(loginUser.getVipRate());
				if(null != userVipLevel){
					feeDiscount = new BigDecimal(String.valueOf(userVipLevel.getDiscount()));
				}
			}else{
				feeDiscount = (BigDecimal) feeDiscountObj;
			}
			if(marketMaps!=null && !marketMaps.isEmpty()){
				for(Map.Entry<String, com.alibaba.fastjson.JSONObject> entry :marketMaps.entrySet()){
					List<Object> feeRateList = new ArrayList<Object>();
					com.alibaba.fastjson.JSONObject market = entry.getValue();
					com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                    buyFeeRate = new BigDecimal(market.getString("takerFeeRate")).multiply(feeDiscount);
                    sellFeeRate = new BigDecimal(market.getString("makerFeeRate")).multiply(feeDiscount);
					jsonObject.put("buyFeeRate", buyFeeRate.multiply(new BigDecimal("100")).setScale(3,BigDecimal.ROUND_DOWN).toPlainString());
					jsonObject.put("sellFeeRate", sellFeeRate.multiply(new BigDecimal("100")).setScale(3,BigDecimal.ROUND_DOWN).toPlainString());
					feeRateList.add(jsonObject);
					map.put(market.getString("market"), feeRateList);
				}
			}
		}catch (Exception e){
			log.error("当前用户"+userId+"获取手续费信息异常，异常信息为：",e);
			json(L("获取手续费失败"), false, null);
		}
		json("成功", true, JSONObject.fromObject(map).toString());
	}
	/*end*/

	/**
	 * 功能跳转
	 */
	@Page(Viewer = JSON)
	public void getFuncJumpInfo() {
		List<FuncJump> funcJumps = funcJumpDao.getFuncJumpList(userIdStr());
		User user = userDao.getById(userIdStr());
		FuncJump funcJump = new FuncJump();
		if(null != funcJumps){
			for(FuncJump funcJump1 : funcJumps){
				//手机验证
				if(4 == funcJump1.getSeqNo()){
					funcJump.setMobileState(funcJump1.isMobileState());
					funcJump.setMobileClose(user.getSmsOpen());
					//谷歌验证
				}else if(5 == funcJump1.getSeqNo()){
					funcJump.setGoogleStatus(funcJump1.isGoogleStatus());
					funcJump.setGoogleClose(user.getGoogleOpen());
					//首次充值
				}else if(6 == funcJump1.getSeqNo()){
					funcJump.setFstStatus(funcJump1.isFstStatus());
					//首次交易
				}else if(8 == funcJump1.getSeqNo()){
					funcJump.setCoinStatus(funcJump1.isCoinStatus());
				}else{
					log.info(L("没有进行过首次操作"));
				}
			}
		}
		funcJump.setRegStatus(true);
		funcJump.setLoginState(true);
		funcJump.setDayStatus(false);
		funcJump.setSomeCoinStatus(false);
		json(L("成功"), true, JSONObject.fromObject(funcJump).toString());
	}
}
