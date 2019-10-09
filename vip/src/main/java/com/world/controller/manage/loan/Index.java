package com.world.controller.manage.loan;

import com.alibaba.fastjson.JSONObject;
import com.api.user.UserManager;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.data.mysql.transaction.TransactionObject;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.SourceType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.loan.MarketPrices;
import com.world.model.loan.dao.AutomaticDao;
import com.world.model.loan.dao.DeductCouponDao;
import com.world.model.loan.dao.DefaultLimitDao;
import com.world.model.loan.dao.InvestorApplyDao;
import com.world.model.loan.dao.LoanDao;
import com.world.model.loan.dao.LoanRecordDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.dao.RevenuedayDao;
import com.world.model.loan.entity.DeductCoupon;
import com.world.model.loan.entity.DefaultLimit;
import com.world.model.loan.entity.DefaultLimitType;
import com.world.model.loan.entity.FundsRange;
import com.world.model.loan.entity.InterestRateForm;
import com.world.model.loan.entity.Loan;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.LoanStatus;
import com.world.model.loan.entity.MyInvestorApply;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.entity.RiskType;
import com.world.model.loan.worker.LevelWorker;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.LoanUserAction;
import com.world.web.response.DataResponse;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Index extends LoanUserAction {

	PayUserDao payDao = new PayUserDao();
	P2pUserDao p2pUserDao = new P2pUserDao();
	DeductCouponDao dCouponDao=new DeductCouponDao();// 抵扣券
	LoanRecordDao recordDao = new LoanRecordDao();//借贷记录表
	LoanDao inDao = new LoanDao();
	AutomaticDao automDao = new AutomaticDao();// 自动委托 dao
	DefaultLimitDao defaDao=new DefaultLimitDao();// 限制投资金额
	RevenuedayDao revdao = new RevenuedayDao();

//	@Page(Viewer = "/cn/manage/loan/loanIn.jsp")
	public void index() {
		String userIdStr = userIdStr();
		if (!userIdStr.equals("0")) {
				initLoanUser();
			// 进入页面即时处理等级更新
			LevelWorker.add(p2pUser);
		}
		//P2pUser p2pUser = (P2pUser)request.getAttribute("p2pUser");
		//1、获取用户资产(净资产+借入)
		Map<String, PayUserBean> userLoadMaps = p2pUser.getFunds(); //UserCache.getUserFundsLoan(userIdStr);
		Iterator<Entry<String, PayUserBean>> iter = userLoadMaps.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,PayUserBean> entry = iter.next();
			PayUserBean payUserBean = entry.getValue();
			CoinProps coin = DatabasesUtil.coinProps(payUserBean.getFundsType());
			payUserBean.setCoint(coin);
		}
		super.setAttr("userLoadMaps", userLoadMaps);
		setAttr("coinMaps",DatabasesUtil.getCoinPropMapsInter(lan));

		//2获取资产总览
		super.setAttr("userAssetsInfo", getUserAssetsInfo(p2pUser));
		super.setAttr("btcAssetsInfo", userLoadMaps.get("btc"));

		//3、查找用户可用优惠券
		List<Bean>  couponList =  dCouponDao.findUserId(userIdStr, 1);
		List<DeductCoupon> dataList = new ArrayList<DeductCoupon>();
		if(couponList!=null){
			for(int i=0;i<couponList.size();i++){
				DeductCoupon coupon = (DeductCoupon)couponList.get(i);
				dataList.add(coupon);
			}
		}
		setAttr("couponList",dataList);
		// 净资产,已申请,可申请
		if (!userIdStr.equals("0")) {
			InvestorApplyDao iaDao = new InvestorApplyDao();
			try {
				MyInvestorApply ia = iaDao.getByField("userId", userIdStr);
				setAttr("apply", ia);
			}catch (Exception ex){
				log.error("内部异常", ex);
			}
		}
		
		
		//各币种借贷汇率
		List<DefaultLimit> defaultLimitList = defaDao.findByTypeName(DefaultLimitType.p2pOutRate.getValue());

		setAttr("defaultLimitList",defaultLimitList);
	}

	/**
	 * add by xwz 2017-06-01
	 * 获取资产总览
	 * @param p2pUser
	 * @return
     */
	private Map<String, Object> getUserAssetsInfo(P2pUser p2pUser) {
		Map<String,Object> userAssetsInfo = new HashMap<>();
		//此处净资产减去提现冻结
		BigDecimal netAssetsNew =  p2pUser.getAvailableSubOverdraft().subtract(p2pUser.getLoanInAssets()).setScale(4,BigDecimal.ROUND_DOWN); //净资产(去除利息之后)
		int level = p2pUser.getLevel();//杠杆
		BigDecimal highestTradable = netAssetsNew.multiply(new BigDecimal(level)).setScale(4,BigDecimal.ROUND_DOWN);//最高运作资金
		BigDecimal loanInAssets = p2pUser.getLoanInAssets().setScale(4,BigDecimal.ROUND_DOWN);//借入总资产折合
		BigDecimal loanInMaxNew = netAssetsNew.multiply(new BigDecimal(2));//最大可借入
		BigDecimal surplusLoanIn = highestTradable.subtract(loanInAssets).subtract(netAssetsNew).setScale(4,BigDecimal.ROUND_DOWN);//剩余可借入
		String netAssetsPersent = "N/A";
		BigDecimal loanRate = BigDecimal.ZERO;
		//计算净资产百分比
		if(loanInAssets.compareTo(BigDecimal.ZERO) == 0){
			netAssetsPersent = "N/A";
		}else{
			if(netAssetsNew.compareTo(BigDecimal.ZERO) < 0){
				netAssetsPersent = "0.0%";
			}else{
				BigDecimal tmpNetAssetsPersent = netAssetsNew.multiply(new BigDecimal(100)).divide(loanInAssets,1,BigDecimal.ROUND_DOWN);
				if(tmpNetAssetsPersent.compareTo(new BigDecimal(100)) > 0){
					netAssetsPersent = ">100%";
				}else{
					netAssetsPersent = tmpNetAssetsPersent + "%";
				}
			}
		}

		//计算风险提示
		String repayLevelShowNew = getRepayLevelShow(netAssetsPersent);


		//计算借入总额／最高借入额
		if(loanInAssets.compareTo(BigDecimal.ZERO) <= 0){//借入总额 <= 0
			loanRate = BigDecimal.ZERO;
		}else{
			if(loanInMaxNew.compareTo(BigDecimal.ZERO) <=0){//借入总额 > 0
				loanRate = BigDecimal.ONE;
			}else{
				loanRate = loanInAssets.divide(loanInMaxNew,2,BigDecimal.ROUND_DOWN);
				loanRate = loanRate.compareTo(BigDecimal.ONE) > 0 ? BigDecimal.ONE : loanRate;
 			}
		}

		userAssetsInfo.put("netAssetsPersent",netAssetsPersent);
		userAssetsInfo.put("netAssets",netAssetsNew);
		userAssetsInfo.put("level",level);
		userAssetsInfo.put("loanInMax",highestTradable);
		userAssetsInfo.put("loanInAssets",loanInAssets);
		userAssetsInfo.put("surplusLoanIn",surplusLoanIn);
		userAssetsInfo.put("loanRate",loanRate);
		userAssetsInfo.put("repayLevelShowNew",L(repayLevelShowNew));
		return userAssetsInfo;
	}

	private String getRepayLevelShow(String netAssetsPersent){
		String repayLevelShow = "后台-融资融币借入-风险提示-1";
		netAssetsPersent = netAssetsPersent.replaceAll(">","").replaceAll("%","");
		if(netAssetsPersent.equals("N/A")){
			repayLevelShow = "后台-融资融币借入-风险提示-1";
		}else{
			BigDecimal netAssetsPersentNumber = new BigDecimal(netAssetsPersent);
			if(netAssetsPersentNumber.compareTo(new BigDecimal(10)) < 0){
				repayLevelShow = "后台-融资融币借入-风险提示-5";
			}else if(netAssetsPersentNumber.compareTo(new BigDecimal(10)) >= 0 && netAssetsPersentNumber.compareTo(new BigDecimal(30)) <= 0) {
				repayLevelShow = "后台-融资融币借入-风险提示-4";
			}else  if(netAssetsPersentNumber.compareTo(new BigDecimal(30)) > 0 && netAssetsPersentNumber.compareTo(new BigDecimal(60)) <= 0) {
				repayLevelShow = "后台-融资融币借入-风险提示-3";
			}else{
				repayLevelShow = "后台-融资融币借入-风险提示-2";
			}
		}
		return repayLevelShow;
	}


	// 发布借入视图
//	@Page(Viewer = "/en/u/loan/in/in.jsp")
	public void pin() {
		String userId = userIdStr();

		if (!userId.equals("0")) {
			initLoanUser();
			
			JSONObject prices = MarketPrices.get();
			if(!MarketPrices.isReturn(prices)){
				//处理用户的平仓等级
				LevelWorker.add(p2pUser);
			}
		}
	}

	private void recordList() {
		String userId = userIdStr();
		// 获取参数
		int pageNo = intParam("page");
		int list = intParam("list");

		Query query = recordDao.getQuery();
		query.setSql("select * from loanrecord");
		query.setCls(LoanRecord.class);
		int pageSize = 10;

		if (!userId.equals("0")) {
			initLoanUser();
		}

		query.append("inUserId='" + userId + "'");

		query.append("ORDER BY field(status,3,0,1,2,4), createTime desc");

		// 将参数保存为attribute
		try {
			int total = recordDao.count();
			if (total > 0) {
				List<Bean> dataList = recordDao.findPage(pageNo, pageSize);

				// recordDao.setLoan(dataList);
				setAttr("lists", dataList);
				setAttr("itemCount", total);
			}
			setPaging(total, pageNo, pageSize);
			setAttr("pageNo", pageNo);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	//// 成交
	/**
	 * TODO：立即借款
	 *
	 * @throws Exception
	 */
	@Page(Viewer = JSON)
	public void doLoan() throws Exception {

 		BigDecimal amount = decimalParam("amount").setScale(3, BigDecimal.ROUND_DOWN);//数量
		String cointName = param("coint");//币种类型
		String userId = userId(true, true);
		String userName = userName();

		int freeCouponId = intParam("freecouponId");
		int deductcouponId = intParam("deductcouponId");
		if (deductcouponId < 0) {
			json(L("后台-融资融币借入-借币提示-1"), false, "");
			return;
		}
		
		if (cointName.length()<=0) {
			json(L("后台-融资融币借入-借币提示-2"), false, "");
			return;
		}
		

		/*抵扣券处理	Start
		查找是否有这张抵扣券*/
		DeductCoupon dCoupon = null;
		if (deductcouponId > 0) {
			// 抵扣券的状态不能使用： 0未激活、1、未使用、2已使用、3已过期、4已禁用 、5等待还款中，
			dCoupon = (DeductCoupon) dCouponDao.findIdKey(deductcouponId);
			if (dCoupon == null) {
				json(L("后台-融资融币借入-借币提示-3"), false, "", false);
				return;
			}
			if (!dCoupon.getUserId().equals(userId)) {
				json(L("后台-融资融币借入-借币提示-4"), false, "", false);
				return;
			}
			dCoupon.setConverAmou(coint.getStag());// 折换线上价格
			if (dCoupon.getConverAmou().compareTo(amount) > 0) {
				json(L("后台-融资融币借入-借币提示-5"), false, "", false);
				return;
			}
			if (dCoupon.getUseState() == 0) {
				json(L("后台-融资融币借入-借币提示-6"), false, "");
				return;
			}
			if (dCoupon.getUseState() == 2) {
				json(L("后台-融资融币借入-借币提示-7"), false, "");
				return;
			}
			if (dCoupon.getUseState() == 3) {
				json(L("后台-融资融币借入-借币提示-8"), false, "");
				return;
			}
			if (dCoupon.getUseState() == 4) {
				json(L("后台-融资融币借入-借币提示-9"), false, "");
				return;
			}
			if (dCoupon.getUseState() == 5) {
				json(L("后台-融资融币借入-借币提示-10"), false, "");
				return;
			}
		}
		/*		抵扣券处理	End*/
		
		BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
		// 自动匹配一个借出标
		Query query = inDao.getQuery();
		query.setSql("select loan.* from loan join p2puser on loan.userId=p2puser.userId " +
				"where loan.amount-loan.hasamount>=? and loan.amount-loan.hasamount>=? and loan.isin=0 and loan.status <= 1");
		query.setCls(Loan.class);
		List<Object> params = new ArrayList<Object>();
		params.add(amount);
		params.add(minUnit);

		//先不增加投资验证
//		query.append("p2puser.loanOutStatus=1");

		String order = "order by ";
		if (freeCouponId > 0) {
			query.append("loan.withoutLx=1 ");
		}else{
			query.append("loan.withoutLx=0 "); 
//			order +="loan.withoutLx asc,";
		}

		query.append("loan.fundstype=?");
		params.add(coint.getFundsType());
		
		order+=" loan.interestRateOfDay asc, loan.createtime asc ";
		query.append(order);
		query.setParams(params.toArray());
		
		
		Loan loan = (Loan) query.getOne();
		if (loan == null) {
			params.set(0, 0);
			query.setSql("select max(amount-hasamount) amount from (" + query.getSql() + ") a ");
			query.setParams(params.toArray());
			Loan loan2 = (Loan) query.getOne();
			if (freeCouponId > 0) {
				if (loan2 != null && loan2.getAmount() != null) {
					json(L("后台-融资融币借入-借币提示-12",
							loan2.getAmount().setScale(2, RoundingMode.DOWN).toPlainString()), false, "",false);
					return;
				} else {
					json(L("后台-融资融币借入-借币提示-13"), false, "",false);
					return;
				}
			} else {
				if (loan2 != null && loan2.getAmount() != null) {
					json(L("后台-融资融币借入-借币提示-14",
							loan2.getAmount().setScale(2, RoundingMode.DOWN).toPlainString()), false, "",false);
					return;
				} else {
					json(L("后台-融资融币借入-借币提示-15"), false, "",false);
					return;
				}
			}
		}else {
			//费率
			BigDecimal p2pOutRate = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pOutRate.getValue());
			if(p2pOutRate.compareTo(BigDecimal.ZERO) == 0){
				p2pOutRate = DigitalUtil.getBigDecimal(0.1);
			}
			int confirm = intParam("confirm"); //如果用户确认
			int onekeyentrust = intParam("onekeyentrust"); //如果一键杠杆
			
			if(confirm==0 && onekeyentrust==0){
				if(loan.getInterestRateOfDay().compareTo(p2pOutRate.movePointLeft(2)) > 0){
					json(L("后台-融资融币借入-借币提示-16", p2pOutRate + "%", loan.getInterestRateOfDay()
							.movePointRight(2).stripTrailingZeros().toPlainString() + "%"), false, "\"needConfirm\"", false);
					return;
				}
			}

		}
		
		if(freeCouponId==0 && loan.getWithoutLx()){
			log.error("非免息借款匹配错了免息贷款!!! userid=" + userId + ", username=" + userName + ", amount=" + amount + ", loan.UserName=" + loan.getUserName() + ", loan.id=" + loan.getId() + "," );
			log.error("借款sql::" + query.getSql());
			log.error("借款参数::" + Arrays.toString(query.getParams()));
			
			//jiahuaxyz的ID
			UserManager.getInstance().pushToApp(
					"115016",1,
					"非免息借款匹配错了免息贷款!!! userid=" + userId + ", username=" + userName + ", amount=" + amount
							+ ", loan.UserName=" + loan.getUserName() + ", loan.id=" + loan.getId() + ",");

		}

		RiskType riskType = (RiskType) EnumUtils.getEnumByKey(1, RiskType.class); // 默认---自担风险
		
		//TODO:来源---WEB
		DataResponse dr = recordDao.doLoan(this, loan.getId(), amount, userId, userName, riskType, freeCouponId, BigDecimal.ZERO, deductcouponId, SourceType.WEB.getKey());

		json(L(dr.getDes()), dr.isSuc(), dr.getDataStr(),false);

	}

	// 我要投资
//	@Page(Viewer = "/cn/manage/loan/loanOut.jsp")
	public void out() {
		String userId = userIdStr();
		if (!userId.equals("0")) {
			initLoanUser();
		}
		JSONObject jo = p2pUserDao.getOutTimes(userId);
		setAttr("outTimes", jo);

		
		Map<String, PayUserBean> userLoadMaps = UserCache.getUserFundsLoan(userId);
		Iterator<Entry<String, PayUserBean>> iter = userLoadMaps.entrySet().iterator();
		while(iter.hasNext()){

			Entry<String,PayUserBean> entry = iter.next();
			if(entry.getKey().equalsIgnoreCase("usd")){
				continue;
			}
			PayUserBean payUserBean = entry.getValue();
			CoinProps coin = DatabasesUtil.coinProps(payUserBean.getFundsType());
			payUserBean.setCoint(coin);
			/*start by xwz 20170620 最大限额*/
			payUserBean.setLoanLimit(trimZeroAfterPoint(payUserBean.getLoanLimit()));//取整
//			if(payUserBean.getLoanLimit().compareTo(BigDecimal.ZERO) <=0 ){
//				//// TODO: 2017/6/2 修改DefaultLimitType枚举类，将limitkey修改为p2pOutLimit
//				String loanLimit = defaDao.findOKT(coint.getStag(), "limitkey").getValueName();
//				payUserBean.setLoanLimit(trimZeroAfterPoint(new BigDecimal(loanLimit)));//取整
//			}else{
//				payUserBean.setLoanLimit(trimZeroAfterPoint(payUserBean.getLoanLimit()));//取整
//			}
			/*end*/
			//投资金额
			BigDecimal outSuccess = payUserBean.getOutSuccess();//成功投资金额
			//利率
			BigDecimal rate = new BigDecimal("0.1");
			String p2pOutRate = defaDao.findOKT(entry.getKey(), DefaultLimitType.p2pOutRate.getValue()).getValueName();
			if(StringUtils.isNotEmpty(p2pOutRate)) {
				rate = new BigDecimal(p2pOutRate);

			}
			payUserBean.setRate(rate);
			BigDecimal riskRate = payUserBean.getRiskManage() == 1 ? new BigDecimal("0.6") : new BigDecimal("0.5");//手续费系数

			int fundsType = payUserBean.getFundsType();
			//等待投资的金额
			payUserBean.setOutWait(trimZeroAfterPoint(payUserBean.getOutWait()));
			//投资金额
			payUserBean.setOutSuccess(trimZeroAfterPoint(outSuccess));

			//每日收益
			BigDecimal interestOfDay = recordDao.getProfit(userId, fundsType,"amount * rate");
			payUserBean.setEarningsOfDay(trimZeroAfterPoint(interestOfDay));

			// 到账收益
			BigDecimal arrivedOfDay = revdao.getArrivedProfit(userId,fundsType);
			payUserBean.setArrivedOfDay(trimZeroAfterPoint(arrivedOfDay));

			// 累计收益（到账收益 + 一日收益 + 拖欠收益)
			BigDecimal appearsProfit = recordDao.getProfit(userId, fundsType,"arrearsLx");
			BigDecimal totalEarnings = arrivedOfDay.add(interestOfDay).add(appearsProfit);
			payUserBean.setTotalEarnings(trimZeroAfterPoint(totalEarnings));

			//已投入(投资成功额+等待投资额)
			BigDecimal totalLoan = payUserBean.getOutWait().add(payUserBean.getOutSuccess());
			payUserBean.setTotalLoan(totalLoan.setScale(2,BigDecimal.ROUND_DOWN));

			//剩余投资额度(=投资限额-等待投资金额-成功投资的金额)
			// TODO: 2017/7/3 xie
			BigDecimal loanLimit = BigDecimal.ZERO;
			if(p2pUser.getUserLend() == 0 ){//取系统默认值
				String sqlLimitKey = defaDao.findOKT(coint.getStag(), "limitKey").getValueName();
				if (StringUtils.isBlank(sqlLimitKey)) {
					log.info("手动投资-默认范围值异常---" + coint.getPropTag() + ":--- " + sqlLimitKey);
					sqlLimitKey = "0";
				}
				loanLimit  = new BigDecimal(sqlLimitKey);
			}else{//取配置的值
				loanLimit = payUserBean.getLoanLimit();
			}
			payUserBean.setLoanLimit(loanLimit);
			BigDecimal surplusLoan = payUserBean.getLoanLimit().subtract(outSuccess).subtract(payUserBean.getOutWait());
			if(surplusLoan.compareTo(BigDecimal.ZERO) < 0){
				surplusLoan = BigDecimal.ZERO;
			}
			payUserBean.setSurplusLoan(trimZeroAfterPoint(surplusLoan.setScale(2, BigDecimal.ROUND_DOWN)));
		}

		super.setAttr("btcCoinInfo", userLoadMaps.get("btc"));
		super.setAttr("userLoadMaps", userLoadMaps);

		setAttr("coinMaps",DatabasesUtil.getCoinPropMapsInter(lan));//币种集合
		//各币种借贷汇率
		List<DefaultLimit> defaultLimitList = defaDao.findByTypeName(DefaultLimitType.p2pOutRate.getValue());
		setAttr("defaultLimitList",defaultLimitList);
		
	}

	//去掉小数点后的0和取整
	private BigDecimal trimZeroAfterPoint(BigDecimal val){
		DecimalFormat df = new DecimalFormat("###.#########");
		return new BigDecimal(df.format(val));
	}

	// 发布投资的表单(ajax嵌入)
//	@Page(Viewer = "/cn/manage/loan/out/out.jsp")
	public void pout() {
		String userId = userIdStr();
		if (!userId.equals("0")) {
			initLoanUser();
		}
		
		
		setAttr("isIn", true);// 投资显示借入标
		
		// 借贷费率,平台固定,各币种自定
		String p2pOutRate = defaDao.findOKT(coint.getStag(), DefaultLimitType.p2pOutRate.getValue()).getValueName();
		if (StringUtils.isBlank(p2pOutRate)) { p2pOutRate = "0.1"; }
		setAttr("p2pOutRate", p2pOutRate);
	}

	@Page(Viewer = JSON)
	public void getMyLoanOut() {
		String userId = userIdStr();
		if (!userId.equals("0")) {
			initLoanUser();
		}
		try{
			int pageIndex = intParam("pageIndex");			//页码
			// 获取参数
			String cointFundsType = request.getParameter("coint");
			String type = param("type");			//1:投资中，2：已收回
			//状态过滤条件
			String statusFilter = "";
			if(type.equals("1")){		//投资中
				statusFilter = " and loan1.status in (0,1,3)";
			}else if(type.equals("2")){	//已收回
				statusFilter = " and loan1.status = 4";
			}

			//资金类型过滤条件
			String fundsTypeFilter = "";
			//增加判断条件（不限币种）
			if(StringUtils.isNotEmpty(cointFundsType)){
				fundsTypeFilter = " and loan1.fundsType = " + coint.getFundsType();
			}
			String whereFilter = statusFilter + fundsTypeFilter;
			long total = getMyLoanOutTotal(userId, whereFilter);
			List<List<Object>> loanOutList = getMyLoanOutList(userId, whereFilter);

			Map<String,Object> returnMap = new HashMap<>();
			Map<String,Object> mapData = new HashMap<>();
			returnMap.put("totalCount", total);
			returnMap.put("pageIndex", pageIndex);
			mapData.put("loanOutList",loanOutList);
			returnMap.put("data", mapData);

			json("",true, com.alibaba.fastjson.JSON.toJSONString(returnMap));
		}catch(Exception e){
			log.error(e.toString(), e);
			json(L("内部错误"), false, "");
		}

	}

	//获取
	private List<List<Object>> getMyLoanOutList(String userId, String whereFilter) {
		String cointFundsType = request.getParameter("coint");
		String type = param("type");			//1:投资中，2：已收回
		int pageNo = intParam("pageIndex");			//页码
		int pageSize = intParam("pageSize");	//每页个数
		if(pageNo == 0){
			pageNo = 1;
		}
		if(pageSize ==0){
			pageSize = 10;
		}
		String orderFilter = " ORDER BY createTime desc ";
		//分页过滤条件
		String pageFilter = "limit " + (pageNo-1) * pageSize + "," + pageSize;

		//0-12：0:投资ID，1:资金类型，2:创建时间，3:收回时间，4:借出次数，5:收回次数，6:投资额，7:借出额，8:收回额度，9:利息，10:利率，11:状态，12：风险控制类型
		String column = "id,fundsType,createTime,lastRepayDate,inTimes,IFNULL(repayCount, 0) repayCount,amount,IFNULL(outAmount,0) outAmount,IFNULL(inAmount,0) inAmount,IFNULL(interest,0) interest,interestRateOfDay,status,riskManage ";
		String sql = "SELECT " + column
				+"FROM (SELECT id, userId, fundsType, createTime,lastRepayDate,amount,inTimes,interestRateOfDay,status,riskManage FROM loan WHERE userId =? AND isIn = 0) AS loan1 "
				+"LEFT JOIN ( SELECT loanId,count(*) 'inCount',sum(amount) + sum(hasRepay) 'outAmount',sum(hasRepay) 'inAmount',sum(hasLx) + sum(arrearsLx) + sum(amount*rate) 'interest' "
				+"FROM loanrecord WHERE outUserId = ? GROUP BY loanId ) AS loanrecord1 ON loan1.id = loanrecord1.loanId "
				+"left JOIN ( SELECT loanId,count(*) repayCount FROM loanrecord WHERE outUserId = ?  and status in (2,4) GROUP BY loanId "
				+") loanrecord2 ON loan1.id = loanrecord2.loanId where loan1.userId = ? " + whereFilter + orderFilter + pageFilter;
		List<List<Object>> list =  Data.Query(sql,new Object[]{userId,userId,userId,userId});

		try{
			for(List<Object> listObj : list){
				//处理币种
				listObj.set(1,DatabasesUtil.coinProps((Integer)listObj.get(1)).getDatabaseKey());

				listObj.set(6,trimZeroAfterPoint((BigDecimal)listObj.get(6)));
				listObj.set(7,trimZeroAfterPoint((BigDecimal)listObj.get(7)));
				listObj.set(8,trimZeroAfterPoint((BigDecimal)listObj.get(8)));
				listObj.set(10,trimZeroAfterPoint((BigDecimal)listObj.get(10)));
				//根据风险控制类型计算日收益
				BigDecimal riskRate = new BigDecimal("0.5");
				if(listObj.get(12).toString().equals("1")){
					riskRate = new BigDecimal("0.6");
				}
				listObj.set(9,trimZeroAfterPoint((BigDecimal)listObj.get(9)).multiply(riskRate));
			}
		}catch(Exception e){
			log.error("处理投资列表数据错误...userId:" + userId, e);
		}

		return list;
	}


	/**
	 * 根据资产类型id获取资产信息
	 * @param fundsType 类型ID
	 * @return CoinProps
	 */
	private CoinProps getCoinByFundsType(int fundsType){
		CoinProps curCoin = null;
		for(Entry<String, CoinProps> entry :  DatabasesUtil.getCoinPropMaps().entrySet()){
			if(entry.getValue().getFundsType() == fundsType){
				curCoin = entry.getValue();
				break;
			}
		}
		return curCoin;
	}

	//// TODO: 2017/6/6 xwz
	private long getMyLoanOutTotal(String userId,String whereFilter){
		String sql = "select count(*) from loan loan1 where userId = ?" + whereFilter;
		List<Long> list = (List<Long>) Data.GetOne(sql,new Object[]{userId});
		return list.get(0);
	}



//	@Page(Viewer = JSON)
//	public void getMyLoan() {
//		String userId = userIdStr();
//		if (!userId.equals("0")) {
//			initLoanUser();
//		}
//
//		// 获取参数
//		String fundsType = param("fundsType");
//		String status = param("status");
//		int pageNo = intParam("page");
//		int pageSize = intParam("pageSize");
//
//		if(pageNo == 0){
//			pageNo = 1;
//		}
//		if(pageSize ==0){
//			pageSize = 10;
//		}
//		// 防止sql注入
//		String inUserId = userId;
//
//		Query query = inDao.getQuery();
//		query.setSql("select * from loan where isIn=0");
//		query.setCls(Loan.class);
//		query.append("userId='" + userId + "'");
//		if (StringUtils.isNotBlank(fundsType)) {
//			query.append("fundsType = " + fundsType);
//		}
//		if(status.equals("1") || status.equals("2")){
//
//		}
//		query.append("ORDER BY createTime desc");
//		Map<String, Object> returnMap = new HashMap<>();
//		// 将参数保存为attribute
//		try {
//			long total = inDao.count();
//			if (total > 0) {
//				List<Bean> dataList = inDao.findPage(pageNo, pageSize);
//
//				Map<String,Object> mapData = new HashMap<>();
//				mapData.put("count",dataList.size());
//				mapData.put("loanList",dataList);
//				returnMap.put("data", mapData);
//				returnMap.put("total", total);
//				json("",true, com.alibaba.fastjson.JSON.toJSONString(returnMap));
//			}
//		} catch (Exception ex) {
//			json("", false , L("内部异常："));
//			log.error("内部异常", ex);
//		}
//
//	}

	// 我发起的投资(ajax嵌入, 点击触发)
//	@Page(Viewer = "/cn/manage/loan/out/my.jsp")
	public void ajax() {
		String userId = userIdStr();
		if (!userId.equals("0")) {
			initLoanUser();
		}

		// 获取参数
		int pageNo = intParam("page");
		int intUserId = intParam("inUserId");
		String fundsType = param("fundsType");
		String order = param("order");

		String riskManage = param("riskType");
		String withoutLx = param("noRate");

		// 防止sql注入
		// String inUserId = String.valueOf(intUserId);
		String inUserId = userId;

		setAttr("inUserId", inUserId.equals("0") ? "" : inUserId);
		setAttr("fundsType", fundsType);

		Query query = inDao.getQuery();
		query.setSql("select * from loan where isIn=0");
		query.setCls(Loan.class);
		int pageSize = 10;

		query.append("userId='" + userId + "'");
		if (StringUtils.isNotBlank(fundsType)) {
			query.append("fundsType = " + fundsType);
		}
		if (StringUtils.isNotBlank(riskManage)) {
			query.append("riskManage = " + riskManage);
		}
		if (StringUtils.isNotBlank(withoutLx)) {
			query.append("withoutLx = " + withoutLx);
		}
		if (StringUtils.isBlank(order)) {
			query.append("ORDER BY createTime desc");
		} else if (order.equals("amount_asc")) {
			query.append("ORDER BY (amount-hasAmount)");
		} else if (order.equals("amount_desc")) {
			query.append("ORDER BY (amount-hasAmount) desc");
		} else if (order.equals("rate_asc")) {
			query.append("ORDER BY interestRateOfDay,(amount-hasAmount)");
		} else if (order.equals("rate_desc")) {
			query.append("ORDER BY interestRateOfDay,(amount-hasAmount) desc");
		} else {// 默认
			query.append("ORDER BY createTime desc");
		}

		// 将参数保存为attribute
		try {
			long total = inDao.count();
			if (total > 0) {
				List<Bean> dataList = inDao.findPage(pageNo, pageSize);
				setAttr("lists", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}

		setAttr("pageNo", pageNo);
		setAttr("coinMaps", DatabasesUtil.getCoinPropMapsInter(lan));

	}

//	@Page(Viewer = "/en/u/loan/out/outAjax.jsp")
	public void inOutajax() {
		boolean isIn = booleanParam("isIn");
		if (!isIn) {
			SetViewerPath("/en/u/loan/in/inAjax.jsp");
		}
		base();
	}

//	@Page(Viewer = "/en/u/loan/in/index.jsp")
	public void base() {

		String userId = userIdStr();

		if (!userId.equals("0")) {
			log.info("userName:" + userName());
			initLoanUser();
		}
		boolean isIn = booleanParam("isIn");
		// 获取参数
		int pageNo = intParam("page");
		int intUserId = intParam("inUserId");
		int fundsType = intParam("fundsType");
		String order = param("order");
		// 防止sql注入
		// String inUserId = String.valueOf(intUserId);
		String inUserId = userId;

		setAttr("inUserId", inUserId.equals("0") ? "" : inUserId);
		setAttr("fundsType", fundsType);

		Query query = inDao.getQuery();
		query.setSql("select * from loan");
		query.setCls(Loan.class);
		int pageSize = 10;

		query.append("isIn=" + isIn);
		boolean isMy = inUserId.equals(userId);

		if (isMy && !isIn) {// 我的投资
			SetViewerPath("/en/u/loan/out/my.jsp");
		}
		if (inUserId.equals(userId)) {/// 我的借款或者投资
			query.append("userId='" + inUserId + "'");
		} else {
			// query.append("userId <> " + userId);
			query.append("status <= 1");// 不包含已取消的和已成功的
		}
		if (fundsType > 0) {
			query.append("fundsType = " + fundsType);
		}

		String amountCode = "";
		String rateCode = "";

		if (isMy) {
			query.append("ORDER BY createTime desc");
		} else {
			if (order.equals("amount_asc")) {
				query.append("ORDER BY (amount-hasAmount)");
				amountCode = "class=\"asc\" onclick=\"vip.p2p.getLoan('&order=amount_desc');\"";
				rateCode = "class=\"\" onclick=\"vip.p2p.getLoan('&order=rate_desc');\"";
			} else if (order.equals("amount_desc")) {
				query.append("ORDER BY (amount-hasAmount) desc");
				amountCode = "class=\"desc\" onclick=\"vip.p2p.getLoan('&order=amount_asc');\"";
				rateCode = "class=\"\" onclick=\"vip.p2p.getLoan('&order=rate_desc');\"";
			} else if (order.equals("rate_asc")) {
				query.append("ORDER BY interestRateOfDay,(amount-hasAmount)");
				amountCode = "class=\"\" onclick=\"vip.p2p.getLoan('&order=amount_desc');\"";
				rateCode = "class=\"asc\" onclick=\"vip.p2p.getLoan('&order=rate_desc');\"";
			} else if (order.equals("rate_desc")) {
				query.append("ORDER BY interestRateOfDay,(amount-hasAmount) desc");
				amountCode = "class=\"\" onclick=\"vip.p2p.getLoan('&order=amount_desc');\"";
				rateCode = "class=\"desc\" onclick=\"vip.p2p.getLoan('&order=rate_asc');\"";
			} else {// 默认
				if (!isIn) {// 投资标，显示顺序
					query.append("ORDER BY interestRateOfDay,(amount-hasAmount)");
					amountCode = "class=\"\" onclick=\"vip.p2p.getLoan('&order=amount_desc');\"";
					rateCode = "class=\"asc\" onclick=\"vip.p2p.getLoan('&order=rate_desc');\"";
				} else {// 借入标显示顺序
					query.append("ORDER BY interestRateOfDay,(amount-hasAmount) DESC");
					amountCode = "class=\"\" onclick=\"vip.p2p.getLoan('&order=amount_desc');\"";
					rateCode = "class=\"desc\" onclick=\"vip.p2p.getLoan('&order=rate_asc');\"";
				}
			}
		}

		setAttr("amountCode", amountCode);
		setAttr("rateCode", rateCode);

		// 将参数保存为attribute
		try {
			long total = inDao.count();
			if (total > 0) {
				List<Bean> dataList = inDao.findPage(pageNo, pageSize);
				setAttr("lists", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}

		setAttr("pageNo", pageNo);
	}

	//// 借出
	@SuppressWarnings("unchecked")
	@Page(Viewer = JSON)
	public void doOut() throws Exception {
		BigDecimal amount = decimalParam("amount").setScale(2, BigDecimal.ROUND_DOWN);	//借出总额
		BigDecimal rate = decimalParam("rate").setScale(3, BigDecimal.ROUND_DOWN);		//借入总额
		int fundsType = intParam("fundsType");	//资金类型
		int riskManage = intParam("risk");	//风险类型：1，自担风险；2，只要本金币种
		// RiskType.repayOwnBi.getKey();
		// 先定死为只要本金币种
		BigDecimal lowestAmount = decimalParam("lowestAmount");
		BigDecimal highestAmount = decimalParam("highestAmount");
		int rateForm = intParam("rateForm");
		BigDecimal rateAddVal = decimalParam("rateAddVal").setScale(3, BigDecimal.ROUND_DOWN);
		String pass = param("password");
		boolean withoutLx = booleanParam("withoutLx");
		boolean isLoop = booleanParam("isLoop");
		
		
		if (riskManage != 1 && riskManage != 2) {
			json(L("后台-融资融币借出-投资提示-1"), false, "");
			return;
		}
		String userId = userIdStr();
		// 安全密码验证
		/*add by xwz 20170705 输错密码提示有几次机会*/
//		if (!safePwd(pass, userId,JSON)) {
//			//json(L("资金密码错误，请重新输入！"), false, "",false);
//			return;
//		}

		if (!safePwdNew(pass, userId,JSON)) {
			//json(L("资金密码错误，请重新输入！"), false, "",false);
			return;
		}
		/*end*/

		//借出人
		P2pUser p2pUser = p2pUserDao.initLoanUser(userId);
		//借出人资金
		PayUserBean payUser = p2pUser.getFunds().get(coint.getStag());
		BigDecimal minUnit = BigDecimal.ZERO;
		try{
			minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
		}catch(Exception e){
			log.error("内部异常", e);
			json(L("后台-融资融币借出-投资提示-2"), false, "");
			return;
		}
		// 借贷金额
		if (amount.compareTo(minUnit) < 0) {
			json(L("后台-融资融币借出-投资提示-3", minUnit.toPlainString(),coint.getPropTag()), false, "");
			return;
		}

		if (p2pUser.getLoanInAssets().compareTo(BigDecimal.ZERO) > 0) {
			json(L("后台-融资融币借出-投资提示-4"), false, "");
			return;
		}



		if (amount.compareTo(payUser.getBalance()) > 0) {
			json(L("后台-融资融币借出-投资提示-5", coint.getPropTag()), false, "");
			return;
		}
		
	

//		 投资限额 Strat 
		// 获取币种类型,进行多个字段拼接
		String beOutSql = "outWait";// 待借
		String outIngSql = "outSuccess";// 借中
		String sqlLimit = "loanLimit";// 投资范围
		PayUserBean pQuery = (PayUserBean) payDao.get("SELECT userId, " + beOutSql + " BeOutsql, " + outIngSql + " Outingsql , " + sqlLimit + " sqlLimit  FROM pay_user WHERE userId=? AND fundsType = ?", new Object[] { userId, coint.getFundsType() }, PayUserBean.class);
		// 总投资=待借 + 借中 + 将要投资
		BigDecimal olds=pQuery.getBeOutSql().add(pQuery.getOutIngSql());
		BigDecimal newOlds = olds.add(amount);
//		BigDecimal newOlds = pQuery.getBeOutSql().add(pQuery.getOutIngSql()).add(amount).setScale(2, RoundingMode.DOWN);
		// 显示剩余投资额度=投资范围 - 借出 - 借出中
		BigDecimal surPlus = BigDecimal.ZERO;
		// ①优先加载手动设置值，如果等于0时，加载系统默认值;
		// ②剩余投资额度 引用到这个值。
		BigDecimal getLimits = pQuery.getSqlLimit();
		//获取转移到数据库的（默认配置）
//		DefaultLimit dQuery=(DefaultLimit) defaDao.get("SELECT * FROM defaultlimit WHERE typeName=?", new Object[]{limitkey}, DefaultLimit.class);
		
		
		
		// 为0（默认）时，获取币种系统默认值
		if (p2pUser.getUserLend() == 0) {
			//获取默认范围，异常处理
//			String sqlLimiyKey = dQuery.getValueName();
			String sqlLimitKey = defaDao.findOKT(coint.getStag(), "limitKey").getValueName();
			if (StringUtils.isBlank(sqlLimitKey)) {
				log.info("手动投资-默认范围值异常---" + coint.getPropTag() + ":--- " + sqlLimitKey);
				sqlLimitKey = "0";
			}
			
			BigDecimal sLK=new BigDecimal(sqlLimitKey);	//最大投资额
			if (sLK.compareTo(pQuery.getBeOutSql()) > 0 || sLK.compareTo(pQuery.getOutIngSql()) > 0) {
				surPlus = sLK.subtract(pQuery.getBeOutSql()).subtract(pQuery.getOutIngSql());
			}
			getLimits = sLK;
			//范围值 <（借出+借出中=负数）= 0
			if(sLK.compareTo(olds)<0){
				surPlus=BigDecimal.ZERO;
			}
		}else if (p2pUser.getUserLend() == 1) {// 为1(设置)时，获取手动设置的值
			BigDecimal getSL = pQuery.getSqlLimit();//手动值异常处理
			if (getSL == null) {
				log.info("手动投资-设置范围值异常---" + (sqlLimit + "Key") + ":" + getSL);
				getSL = BigDecimal.ZERO;
			}
			
			if (getSL.compareTo(pQuery.getBeOutSql()) > 0 || getSL.compareTo(pQuery.getOutIngSql()) > 0) {
				surPlus = getSL.subtract(olds);
			}
			getLimits = getSL;
			//范围值 <（借出+借出中=负数）= 0
			if(getSL.compareTo(olds)<0){
				surPlus=BigDecimal.ZERO;
			}
		}
		
		//范围值 <（借出+借出中=负数）= 0
//		if(getLimits.compareTo(newOlds)<0){
//			surPlus=BigDecimal.ZERO;
//		}

		// 当投资的总额大于设定的范围时，警告超标 同时显示可投余额
		if (p2pUser.getUserLend() == 1 || p2pUser.getUserLend() == 0) {
			if (newOlds.compareTo(getLimits) > 0) {
				json(coint.getPropTag() + L("后台-融资融币借出-投资提示-6") + coint.getUnitTag() + surPlus.setScale(2, RoundingMode.DOWN) + "。", false, "");
				return;
			}
		}
//		 投资限额 End 
		
		// 借贷费率,平台固定
		String p2pOutRate_ = defaDao.findOKT(coint.getStag(), "p2pOutRate").getValueName();
			
		if (StringUtils.isBlank(p2pOutRate_)) {
			p2pOutRate_ = "0.1";
		}

		BigDecimal p2pOutRate = new BigDecimal(p2pOutRate_);
		if (rate.compareTo(p2pOutRate) != 0) {
			json(L("后台-融资融币借出-投资提示-7"), false, "");
			return;
		}
		rate = rate.divide(DigitalUtil.getBigDecimal(100));
		if (rateAddVal.compareTo(BigDecimal.ZERO) > 0) {
			rateAddVal = rateAddVal.divide(DigitalUtil.getBigDecimal(100));
		}

	


		FundsRange fr = FundsRange.loanRate;
		String error = fr.error(rate);
		if (error != null) {
			json(L(error), false, "");
			return;
		}

		if (rateAddVal.compareTo(new BigDecimal(0.01)) > 0) {
			json(L("后台-融资融币借出-投资提示-8"), false, "");
			return;
		}

		LoanDao inDao = new LoanDao();
		List<OneSql> sqls = new ArrayList<OneSql>();
		
		// 最后一个“0”是投资标识，为手动投资----获取来源：SourceType.WEB.getKey()
		// ①最原始的方法
//		sqls.add(inDao.insertSql(new Loan(false, pUser.getUserId(), pUser.getUserName(), ft.getKey(), amount,
//				LoanStatus.waiting.getKey(), TimeUtil.getNow(), rate, 2, lowestAmount, highestAmount, "", rateForm,
//				rateAddVal, riskManage, withoutLx, isLoop, amount, 0, 0)));
		// ②后来改进，每次添加字段不用改接口
		Loan loan = new Loan(false, String.valueOf(payUser.getUserId()), payUser.getUserName(), coint.getFundsType(), amount,
				LoanStatus.waiting.getKey(), TimeUtil.getNow(), rate, 2, lowestAmount, highestAmount, "", rateForm,
				rateAddVal, riskManage, withoutLx, isLoop, amount, 0, 0);
		//TODO:来源----WEB
		loan.setSourceType(SourceType.WEB.getKey());
		sqls.add(inDao.insertSql(loan));

		DataResponse dr = p2pUserDao.trans(amount, String.valueOf(payUser.getUserId()), payUser.getUserName(), "P2P资金转出", false, sqls);

		json(L(dr.getDes()), dr.isSuc(), dr.getDataStr());
	}

	/**
	 * 自动投资,显示开关、资金 等参数
	 *
	 */
//	@Page(Viewer = "/cn/manage/loan/out/autom.jsp")
	public void autom() {
		String userId = userIdStr();
		if (!userId.equals("0")) {
			initLoginUser();
			p2pUser = p2pUserDao.getById(loginUser.getId(), loginUser.getUserName());
			p2pUserDao.initLoanUser(p2pUser);
			setAttr("p2pUser", p2pUser);
		}
		
		
	/*	if (!userId.equals("0")) {
			log.info("自动委托投资Name:" + userName());
			P2pUser user = (P2pUser) p2pUserDao.getById(userId, userName());
			user.init();// 初始化资产信息
			setAttr("curUser", user);
			setAttr("rateForms", EnumUtils.getAll(InterestRateForm.class));
		}*/
		/*JSONObject jo = p2pUserDao.getOutTimes(userIdStr());
		setAttr("outTimes", jo);*/
		setAttr("isIn", true);// 投资显示借入标
		setAttr("rateForms", EnumUtils.getAll(InterestRateForm.class));
	}

	/**
	 * 自动投资 （接收页面的输入值 进行更新）
	 *
	 *
	 * @author chenruidong
	 */
	@Page(Viewer = JSON)
	public void doAutoOut() throws Exception {

		int switchs = intParam("autoForm");// 自动委托 开关 0为关，1为开
		int freeSwitchs = intParam("freeSwitchs");// 免息券开关 0 为关， 1 为开
		
		String userId = userIdStr();
		TransactionObject transcation = new TransactionObject();
		try {
			List<OneSql> sqlList = new ArrayList<OneSql>();
			for(Map.Entry<String,CoinProps> coinEntry:DatabasesUtil.getCoinPropMaps().entrySet()){
				CoinProps coin = coinEntry.getValue();
				int fundsType = coin.getFundsType();
				BigDecimal entrustThreshold = decimalParam("value"+coin.getPropTag()).setScale(2, BigDecimal.ROUND_DOWN);// 各币种额定值
				OneSql oneSql = new OneSql("UPDATE pay_user SET entrustThreshold = ? WHERE userId=? and fundsType=? ", 1,new Object[]{entrustThreshold,userId,fundsType});
				sqlList.add(oneSql);
			}
	
			// 获取AutomaticDao里面的update方法，修改委托值 userId 为 1 的 用户 修改 开关、rmb、btc、ltc、eth 的值,freeSwitchs 只能固定是唯一的
			sqlList.add(new OneSql("UPDATE p2puser SET switchs = ?, freeSwitchs= ? WHERE userId = ?",1,
					new Object[] { switchs, freeSwitchs, userId}));
			
			
			String open = switchs == 1 ? "开启" : "关闭";// 用三元表达式进行代码简化判断,开关等于1的时候为开启状态、否则为关闭状态
			transcation.excuteUpdateList(sqlList);
			if (transcation.commit()) {
				json(L("设置成功," + open + "委托"), true, "");
			} else {
				json(L(open + "委托失败"), true, "");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
			transcation.rollback("自动投资保存失败");
			json(L("账号异常，刷新后在进入！"), false, "");
		}
	}

	private static List<Integer> LIST;
	private static boolean DUES = false;
	private static int NUMBER = 0;
	/**
	 *  单个取消借贷
	 *
	 **/
	@Page(Viewer = JSON)
	public void doCancel() throws Exception {
		int loanId = intParam("id");
		String userId = userIdStr();
		List<Integer> listInt;
		//判断 单，多 数据
		if (loanId == 0 && LIST.size() > 0) {
			listInt = LIST;
		} else {
			listInt=Arrays.asList(loanId);
		}
		//不管传进来单條还是多条，都能根据他的条数执行下面的数据。
		for (int i = 0; i < listInt.size(); i++) {
			int listLoanId=listInt.get(i);//Loan loan=listInt.get(i);
			
			Loan curIn = (Loan) Data.GetOne("select * from Loan where Id = ? and userId=?", new Object[] { listLoanId, userId }, Loan.class);
			List<BigDecimal> hasRepayList =(List<BigDecimal>) Data.GetOne("select IFNULL(loanrecord.hasRepay,0) hasRepay from (select sum(hasRepay) hasRepay from loanRecord where loanId = ?) loanrecord", new Object[]{listLoanId});
			BigDecimal hasRepay = hasRepayList.get(0);//已经还了多少
			// (Loan) new LoanDao().getById(Loan.class, loanId);
			if (curIn == null) {
				json(L("后台-融资融币借出-取消借出提示-1"), false, "");
				return;
			}
	
			if (curIn.getStatus() == LoanStatus.canceled.getKey() || curIn.getStatus() == LoanStatus.success.getKey()) {
				json(L("后台-融资融币借出-取消借出提示-2"), false, "");
				return;
			}
	
			int status = 0;
			List<OneSql> sqls = new ArrayList<OneSql>();

			/*start by xwz 20170625*/
			if (curIn.getHasAmount().compareTo(BigDecimal.ZERO) == 0) {//未有借入
				status = LoanStatus.canceled.getKey();
				sqls.add(inDao.updateSql(status, listLoanId));
			} else {//部分借入
				if(curIn.getHasAmount().compareTo(hasRepay) == 0){//借出额等于已还金额
					status = LoanStatus.allRepay.getKey();
				}else{
					status = LoanStatus.success.getKey();
				}
				sqls.add(inDao.updateSql(status, listLoanId,curIn.getHasAmount()));
			}
			/*end*/

			
			CoinProps coint = DatabasesUtil.coinProps(curIn.getFundsType());
			P2pUserDao dao = new P2pUserDao();
			dao.setCoint(coint);
			boolean due = false;
			
			if (curIn.getIsIn()) {
				dao.cancelIn(curIn.getBalanceAmount(), userId, sqls);
				PayUserBean payUser = payDao.getById(Integer.parseInt(userId), coint.getFundsType());
				// 有待借入
//				if (payUser.getRepayLevel() <= 0) {// 无借入
					/*if (ft.equals(FundsType.rmb)) {
						user.setBeInRmb(user.getBeInRmb().subtract(curIn.getBalance()));
					} else if (ft.equals(FundsType.btc)) {
						user.setBeInBtc(user.getBeInBtc().subtract(curIn.getBalance()));
					} else if (ft.equals(FundsType.ltc)) {
						user.setBeInLtc(user.getBeInLtc().subtract(curIn.getBalance()));
					} else if (ft.equals(FundsType.eth)) {
						user.setBeInEth(user.getBeInEth().subtract(curIn.getBalance()));
					} else if (ft.equals(FundsType.etc)) {
						user.setBeInEtc(user.getBeInEtc().subtract(curIn.getBalance()));
					}
					if (user.beInConertRmb().compareTo(BigDecimal.ZERO) > 0) {
						due = Data.doTransWithHttp(sqls, UserManager.class, "changeUserTransStatus",
								new Object[] { user.getUserId(), 3 });
					} else {
						due = Data.doTransWithHttp(sqls, UserManager.class, "changeUserTransStatus",
								new Object[] { user.getUserId(), 0 });
					}*/
//				} else {
//					due = Data.doTrans(sqls);
//				}
			} else {
				// P2PManager p2p = new P2PManager();
				// FundsType ft = curIn.getfundsType();
				//// BigDecimal[] amounts = ft.getAmounts(curIn.getBalanceAmount());
				// ChbtcResponse dr = p2p.cancelLoan(userId,
				// curIn.getBalanceAmount().doubleValue(), ft.getTag2());
				// if(dr.taskIsFinish()){
				// dao.cancelOut(curIn.getBalanceAmount(), ft, userId, sqls);
				// }else{
				// Response.append(dr.getMsg());
				// return;
				// }
				dao.cancelOut(curIn.getBalanceAmount(), userId, sqls);
				due = Data.doTrans(sqls);
				if (loanId == 0) {
					if(due){
						UserCache.resetUserFunds(userId);
						DUES = due;
						NUMBER++;
					}else{
						continue;
					}
				} else if (loanId > 0) {
					if (due) {
						UserCache.resetUserFunds(userId);
						json(L("后台-融资融币借出-取消借出提示-3"), true, "");
					} else {
						json(L("后台-融资融币借出-取消借出提示-4"), false, "");
					}
				}
			}
		}
		
	}
	/**
	 * 一键跨页取消
	 *
	 *
	 * @author chenruidong
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Page(Viewer = JSON)
	public void doCancels() throws Exception {
		try {
			// 传进来loan里面获取jsp用户的uids
			String uId = param("ids");
			String fundsType = param("fundsType");// 投资类型
			String riskManage = param("riskType");// 风险控制
			String withoutLx = param("noRate");// 免息券

			// 为了性能，这里只给了个id，有需求可改为*,不冲突。
//			 sql Start 
			Query query = inDao.getQuery();
			query.setSql("select id from loan where `status`<=?").setParams(new Object[] { 1 }).setCls(Loan.class);
			String inUserId = uId;
			query.append("userId=" + inUserId);
			if (StringUtils.isNotBlank(fundsType)) {
				query.append("fundsType=" + fundsType);
			}
			if (StringUtils.isNotBlank(riskManage)) {
				query.append("riskManage=" + riskManage);
			}
			if (StringUtils.isNotBlank(withoutLx)) {
				query.append("withoutLx=" + withoutLx);
			}
			
			List<Loan> loanList = query.getList();
			List<Integer> lists = new ArrayList<Integer>();// 把东西存储起来
			if (loanList.size() > 0) {
				for (Loan loan : loanList) {
					lists.add(loan.getId());
				}
				LIST = lists;
				doCancel();// 引用单个删除的方法
			} else {
				json(L("您没有发布借款信息。"), false, "");
				return;
			}
			// 执行完所有条记录后返回值，成功或者失败
			int nums = (loanList.size() - NUMBER);
			if (DUES && loanList.size() == NUMBER) {
				json(L("取消成功。"), true, "");
			} else if ((NUMBER != 0 || nums != 0) && (NUMBER >= nums || nums >= NUMBER) && (loanList.size() > 1)) {
				json(L("已帮您取消部分借款信息。"), true, "");
			} else {
				json(L("取消失败。"), false, "");
			}
			NUMBER = 0;// 必须要清零，不然会叠加相加
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	// 取消循环投资
	@Page(Viewer = JSON)
	public void doCancelLoop() throws Exception {
		int loanId = intParam("id");
		String userId = userIdStr();

		Loan curIn = (Loan) Data.GetOne("select * from Loan where Id = ? and userId=?", new Object[] { loanId, userId },
				Loan.class);// (Loan) new LoanDao().getById(Loan.class, loanId);
		if (curIn == null) {
			json(L("该借款不存在。"), false, "");
			return;
		}

		List<OneSql> sqls = new ArrayList<OneSql>();
		sqls.add(inDao.cancelLoopSql(loanId));

		boolean due = false;

		due = Data.doTrans(sqls);
		if (due) {
			json(L("取消成功。"), true, "");
		} else {
			json(L("取消失败。"), false, "");
		}
	}

//	Close By suxinjie 一期屏蔽该功能
//	@Page(Viewer = "/en/u/loan/in/myDeductCoupon.jsp")
	public  void  myDeductCoupon(){
		try {
			
		int pageNo = intParam("page");
		String userId = userIdStr();
		DeductCouponDao dao = new DeductCouponDao();
		Query query = dao.getQuery();
		query.setSql("SELECT * FROM deductcoupon WHERE userId='" + userId + "' order by actTime desc");
		query.setCls(DeductCoupon.class);
		int pageSize = 10;

		long total = dao.count();
		if (total > 0) {
			List<Bean> dataList = dao.findPage(pageNo, pageSize);
			setAttr("list", dataList);
			setAttr("itemCount", total);
		}
		setAttr("pNo", pageNo == 0 ? 1 : pageNo);
		setPaging((int) total, pageNo, pageSize);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	
	 /**
	  * 获取借贷记录列表 在前端页面显示
	  *
	  */
	 @Page(Viewer = JSON)
	 public void getLoanRecordList(){
	    	try{
				int type = intParam("type");//1:未还款，2：已还款
				String userId =userIdStr();
				String currentTab = param("tab");
				int pageIndex = intParam("pageIndex");
				int pageSize = intParam("pageSize");
				boolean isPage = param("isPage") == "" ? true : false;
				String cointFundsType = request.getParameter("coint");
				
				if(currentTab.length()==0){
					currentTab="all";
				}

				if (!userId.equals("0")) {
					initLoanUser();
				}
				
				recordDao.setCoint(coint);
				Query query = recordDao.getQuery();
				query.setSql("select * from loanrecord");
				query.setCls(LoanRecord.class);

				//增加判断条件（不限币种）
				if(StringUtils.isNotEmpty(cointFundsType)){
					query.append("fundsType=" + coint.getFundsType());
				}
				query.append("inUserId='" + userId + "'");
				if(type == 1){	//未还款
					query.append("status in (1,3)");
				}else if(type == 2){//已还款
					query.append("status in (2,4)");
				}

				query.append("ORDER BY field(status,3,0,1,2,4), createTime desc");

				List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
				Map<String, Object> page = new HashMap<String, Object>();
				// 将参数保存为attribute
				try {
					int total = recordDao.count();
					if (total > 0) {
						List<LoanRecord> dataList = recordDao.findPage(pageIndex, pageSize);

						// recordDao.setLoan(dataList);
						//setAttr("lists", dataList);
						//setAttr("itemCount", total);

						for(LoanRecord loanRecord:dataList){
							Map<String,Object> loanRecordMap = new HashMap<String,Object>();
							loanRecordMap.put("id", loanRecord.getId());//记录ID
							loanRecordMap.put("createTime", loanRecord.getCreateTime());//借贷时间
							loanRecordMap.put("repayDate", loanRecord.getRepayDate());//借贷总金额
							loanRecordMap.put("amount", loanRecord.getAmount());//借贷总金额
							int fundsType = loanRecord.getFundsType();
							String propTag = DatabasesUtil.coinProps(loanRecord.getFundsType()).getPropTag();
							//coint.getPropTag()
							loanRecordMap.put("coinName", propTag);//借贷币种
							loanRecordMap.put("hasRepay", loanRecord.getHasRepay());//已还本金金额
							loanRecordMap.put("rate", loanRecord.getRate());//利率
							loanRecordMap.put("hasLx", loanRecord.getHasLx());//已还利息
							loanRecordMap.put("dikouLx", loanRecord.getDikouLx());//已抵扣利息
							loanRecordMap.put("shouldRepayBX", loanRecord.getShouldRepayBX());//应还本息
							loanRecordMap.put("lx", loanRecord.getNeedLx());//应还利息
							loanRecordMap.put("statusShow", L(EnumUtils.enumToMap(LoanRecordStatus.values()).get(loanRecord.getStatus())));//状态
							loanRecordMap.put("status", loanRecord.getStatus());//状态
							//1.还款中，2.已还清,3.需平仓,4:平仓还款
							if(loanRecord.getStatus() == 3){
								loanRecordMap.put("showRepayButton",checkIsShowRepayButton() ? 1 : 0);
							}else if(loanRecord.getStatus() == 1){
								loanRecordMap.put("showRepayButton",1);
							}
							list.add(loanRecordMap);
						}
						page.put("pageIndex", pageIndex);
						page.put("totalCount", total);
						page.put("list", list);
					}
					//setPaging(total, pageIndex, pageSize);
					//setAttr("pageNo", pageIndex);
				} catch (Exception ex) {
					log.error("内部异常", ex);
				}
				

			/*	
				List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
				int total = query.count();
				if(total > 0){
					query.append("order by detailsId desc");
					//分页查询
					List<DetailsBean> details = recordDao.findPage(pageIndex, pageSize);
					
					for(DetailsBean detail:details){
						Map<String,Object> downloadMap = new HashMap<String,Object>();
						downloadMap.put("id", detail.getDetailsId());
						downloadMap.put("submitTime", detail.getSendTime());
						downloadMap.put("coinName", coint.getPropTag());
						downloadMap.put("amount", detail.getAmount());
						downloadMap.put("status", detail.getShowStatu());
						
						list.add(downloadMap);
					}
				}*/
				
				
				json("", true, JSONObject.toJSON(page).toString());
			}catch (Exception e) {
				log.error("内部异常", e);
			}
	    }

		//是否显示还款按钮
		private boolean checkIsShowRepayButton(){

			BigDecimal userAvailable = p2pUser.getFunds().get(coint.getStag()).getBalance();//可用资金
			Map<String, Object> assetsInfo = getUserAssetsInfo(p2pUser);
			String persent = (String)assetsInfo.get("netAssetsPersent");
			boolean isHigherTenPersent = false;

			if(persent.equals("N/A")){
				isHigherTenPersent = true;
			}else if(persent.equals("0.0%")){
				isHigherTenPersent = false;
			}else if(persent.equals(">100%")){
				isHigherTenPersent = true;
			}else{
				try{
					String tmp = persent.replace("%","");
					BigDecimal tmpDecimal = new BigDecimal(tmp);
					if(tmpDecimal.compareTo(BigDecimal.TEN) >= 0) {
						isHigherTenPersent = true;
					}
				}catch(Exception e){
					log.error("出现异常", e);
					isHigherTenPersent = false;
				}
			}

			if(userAvailable.compareTo(BigDecimal.ZERO) > 0 && isHigherTenPersent) {
				return true;
			}
			return false;
		}



	@SuppressWarnings({ "unchecked", "rawtypes" })
//		@Page(Viewer = "/cn/manage/loan/in/myDeductCoupon.jsp")
		public  void  coupon(){
			try {
				String userIdStr = userIdStr();
				if (!userIdStr.equals("0")) {
						initLoanUser();
				}
				int pageNo = intParam("page");
				String userId = userIdStr();
				DeductCouponDao dao = new DeductCouponDao();
				Query query = dao.getQuery();
				query.setSql("SELECT * FROM deductcoupon WHERE userId='" + userId + "' order by actTime desc");
				query.setCls(DeductCoupon.class);
				int pageSize = 10;
	
				long total = dao.count();
				if (total > 0) {
					List<Bean> dataList = dao.findPage(pageNo, pageSize);
					setAttr("list", dataList);
					setAttr("itemCount", total);
				}
				setAttr("pNo", pageNo == 0 ? 1 : pageNo);
				setPaging((int) total, pageNo, pageSize);
			} catch (Exception e) {
				log.error("内部异常", e);
			}
		}
	 	
	 	/**
	 	 * 获取币种当日最优贷款费率
		 *
	 	 */
	 	@Page(Viewer =JSON )
	 	public void getDefaultValue(){
	 		String keyName = param("coint");//币种
	 		String typeName = DefaultLimitType.p2pOutRate.getValue();//类型
	 		BigDecimal limit =	defaDao.getLimitBigDecimal(keyName, typeName);
	 		json("", true, limit.toPlainString());
			return;
	 	}
}
