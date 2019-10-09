package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.Lan;
import com.world.cache.Cache;
import com.world.model.jifenmanage.JifenManage;
import com.world.model.singleton.SingletonThreadPool;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.tenstar.HTTPTcp;
import com.tenstar.Message;
import com.tenstar.MessageCancle;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.fee.FeeDao;
import com.world.model.dao.pay.FreezDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.Market;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.loan.entity.DeductCoupon;
import com.world.model.loan.entity.DefaultLimitType;
import com.world.model.loan.entity.Loan;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.LoanStatus;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.entity.RepayOfQi;
import com.world.model.loan.entity.RepayOfQiStatus;
import com.world.model.loan.entity.RiskType;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;
import com.world.web.Pages;
import com.world.web.response.DataResponse;

public class LoanRecordDao extends DataDaoSupport{
	
	private static final String userFundsKey = "user_funds_";
	public static final int BUY = 0;
	public static final int SELL = 1;
	DefaultLimitDao defaDao=new DefaultLimitDao();
	P2pUserDao p2pUserDao = new P2pUserDao();
	RepayOfQiDao rQiDao=new RepayOfQiDao();	
	FundsDao fundsDao = new FundsDao();
	FeeDao feeDao = new FeeDao();
	LoanDao loanDao = new LoanDao();
	/****
	 * 处理借贷成交的方法，借贷双方达成共识，借贷成立后的操作
	 * @param curLoan
	 * @param amount
	 * @param userId
	 * @param userName
	 * @return
	 */
	public DataResponse doLoan(Pages pages, int inId , BigDecimal amount , String userId , String userName , RiskType riskType, int freeCouponId, BigDecimal useAmount, int deductcouponId,int sourceType ) {
		DataResponse dr = new DataResponse();
		dr.setDataStr("");
		
		LoanDao inDao = new LoanDao();
		DeductCouponDao dCouponDao=new DeductCouponDao();//抵扣券
		P2pUserDao p2pUserDao = new P2pUserDao();
		
		Loan curIn = (Loan) inDao.getById(Loan.class, inId);
		
		if(curIn == null){
			dr.setSuc(false);
			dr.setDes(pages.L("该借贷不存在，请刷新页面重试。"));
			return dr;
		}
		
		if(curIn.getStatus() == LoanStatus.canceled.getKey()){
			dr.setSuc(false);
			dr.setDes(pages.L("该借贷已经取消，请刷新页面重试。"));
			return dr;
		}
		
		if(curIn.getStatus() == LoanStatus.success.getKey()){
			dr.setSuc(false);
			dr.setDes(pages.L("该借贷已经满标，请刷新页面重试。"));
			return dr;
		}
		CoinProps coint = DatabasesUtil.coinProps(curIn.getFundsType());
		p2pUserDao.setCoint(coint);
		
		P2pUser p2pUser = p2pUserDao.initLoanUser(userId);
		PayUserBean payUser = p2pUser.getFunds().get(coint.getStag());
		
		if(payUser == null){
			dr.setSuc(false);
			dr.setDes(pages.L("用户不存在。"));
			return dr;
		}
		if(curIn.getUserId().equals(userId)){
			dr.setSuc(false);
			dr.setDes(pages.L("您不能对自己发布的借贷进行投标！"));
			return dr;
		}
		
		if(amount.compareTo(curIn.getBalanceAmount()) > 0){
			DecimalFormat df = new DecimalFormat("0.00##");
			dr.setSuc(false);
			dr.setDes(pages.L("当前借贷剩余金额%%，借贷金额不能高于%%。", df.format(curIn.getBalanceAmount())+coint.getUnitTag(), df.format(curIn.getBalanceAmount())+coint.getUnitTag() ));
			return dr;
		}
		
		BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
//		if(minUnit.compareTo(curIn.getBalanceAmount()) > 0){
//			if(amount.compareTo(curIn.getBalanceAmount()) != 0){//剩余的金额不够最小单位，就必须全部接收
//				dr.setSuc(false);
//				dr.setDes("当前借贷剩余金额已不足您的最小借贷单位，您本次的借贷金额只能是余下的全部！");
//				return dr;
//			}
//			minUnit = curIn.getBalanceAmount();
//		}
		
		if(amount.compareTo(minUnit) < 0){
			dr.setSuc(false);
			dr.setDes(pages.L("您的最低借入金额不能小于%%%%!", minUnit.toString(), coint.getPropTag()));
			return dr;
		}
		
		if(curIn.getIsIn()){//借入标  有借出禁止借入
			//如果当前标识借入标需要判断用户资产是否足以抵押借入   ， 由于种种原因可能导致当前借入标已经无效了 譬如：价格波动导致用户资产已经没那么多了
			String lInUserId = curIn.getUserId();//借入者ID
			P2pUser inUser = p2pUserDao.initLoanUser(lInUserId);
			if(inUser == null){
				dr.setSuc(false);
				dr.setDes(pages.L("当前借入标用户已经不存在了！"));
				return dr;
			}
			
			
			BigDecimal loan_120 = curIn.getBalance().multiply(DigitalUtil.getBigDecimal(1.2));
			JSONObject prices = LoanAutoFactory.getPrices();
			
			BigDecimal balanceConvert = loan_120;
			if(prices.containsKey(coint.getStag())){
				balanceConvert = loan_120.multiply(prices.getBigDecimal(coint.getStag()));
			}
			
			if(inUser.getTotalAssetsSubOverdraft().compareTo(balanceConvert) <= 0){//借入者已经没有能力借入本笔借入了   取消掉
				try {
					List<OneSql> p2pSqls = new ArrayList<OneSql>();
					int status = 0;
					if(curIn.getHasAmount().compareTo(BigDecimal.ZERO) == 0){
						status = LoanStatus.canceled.getKey();
					}else{
						status = LoanStatus.success.getKey();
					}
					p2pSqls.add(inDao.updateSql(status, curIn.getId()));
					p2pUserDao.cancelIn(curIn.getBalanceAmount(), lInUserId, p2pSqls);
					
					Data.doTrans(p2pSqls);
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				dr.setSuc(false);
				dr.setDes(pages.L("当前借入标已经无效，请刷新页面！"));
				return dr;
			}
			BigDecimal loanInAssets = inUser.getLoanInAssets();
			
			if(loanInAssets.compareTo(BigDecimal.ZERO) > 0){
				dr.setSuc(false);
				dr.setDes(pages.L("您是借入者，不能投资！"));
				return dr;
			}
			
			if(amount.compareTo(payUser.getBalance()) > 0){
				dr.setSuc(false);
				dr.setDes(pages.L("您的可用%%不足，投资失败，请先充值！",coint.getPropTag()));
				return dr;
			}
		}else{//借出标
			if(p2pUser.getLoanOutAssets().compareTo(BigDecimal.ZERO) > 0){
				dr.setSuc(false);
				dr.setDes(pages.L("您是投资者，不能借入！"));
				return dr;
			}
			
			BigDecimal canLoan = payUser.getCanLoan();
			log.info("amount：" + amount + ", canLoan:" + canLoan);
			if(amount.compareTo(canLoan) > 0){
				dr.setSuc(false);
				dr.setDes(pages.L("您借入的%%已超出您可借的范围！",coint.getPropTag()));
				return dr;
			}
			
			riskType = (RiskType) EnumUtils.getEnumByKey(curIn.getRiskManage(), RiskType.class);
		}
		
		if(riskType == null){
			dr.setSuc(false);
			dr.setDes(pages.L("未找到的风险控制类型！"));
			return dr;
		}
		
		if(amount.compareTo(curIn.getLowestAmount()) < 0){
			dr.setSuc(false);
			dr.setDes(pages.L("借贷金额不能低于")+curIn.getLowestAmount()+coint.getPropTag()+pages.L("。"));
			return dr;
		}
		
		if(amount.compareTo(curIn.getHighestAmount()) > 0 && curIn.getHighestAmount().compareTo(BigDecimal.ZERO) > 0){
			dr.setSuc(false);
			dr.setDes(pages.L("借贷金额不能高于")+curIn.getHighestAmount()+coint.getPropTag()+pages.L("。"));
			return dr;
		}
		
		List<OneSql> sqls = new ArrayList<OneSql>();
		
		//借出成功后，发布的借款还剩的余额
		BigDecimal inBalanceAmount = curIn.getAmount().subtract(curIn.getHasAmount()).subtract(amount);
		//修改投资信息
		sqls.add(inDao.updateBidSqls(curIn, amount, inBalanceAmount));
		Timestamp now = now();
		Timestamp nextRepayDate = TimeUtil.getAfterDayDate(now , 1);
		String inUserId = "";
		String inUserName = "";
		String outUserId = "";
		String outUserName = "";
		int dealType = 0;
		if(curIn.getIsIn()){
			inUserId = curIn.getUserId();
			inUserName = curIn.getUserName();
			outUserId = userId;
			outUserName = userName;
		}else{
			dealType = 1;
			outUserId = curIn.getUserId();
			outUserName = curIn.getUserName();
			inUserId = userId;
			inUserName = userName;
		}
		///扣除借出者的资金   并添加明细记录
		p2pUserDao.loanOut(amount , outUserId, outUserName , curIn.getIsIn(), sqls);
		FreezDao freezDao = new FreezDao();
		freezDao.setCoint(coint);
		//解冻不扣除
		FreezeBean fbean = new FreezeBean(outUserId, outUserName, "投资成功", 0, amount, 0, 0);
		freezDao.unFreezSqls(sqls, fbean, BillType.outToP2p, true);
		///添加借入者的资金   并添加明细记录
		p2pUserDao.loanIn(amount,  curIn.getInterestRateOfDay(), inUserId, inUserName, curIn.getIsIn() , sqls);
		sqls.addAll(fundsDao.addMoney(amount, inUserId, inUserName, "借入"+coint.getPropTag(), BillType.fromP2pIn.getKey(), coint.getFundsType(), BigDecimal.ZERO, "0", true));
		
		LoanRecord lr = new LoanRecord(inId, curIn.getIsIn(), inUserId, inUserName, outUserId, outUserName, 
				curIn.getFundsType(), amount, LoanRecordStatus.Returning.getKey(), now, inBalanceAmount, 
				curIn.getInterestRateOfDay(), BigDecimal.ZERO, "0", nextRepayDate , riskType.getKey() , curIn.getRateForm() , curIn.getRateAddVal() , deductcouponId);
		//TODO:来源---WEB、APP、API
		lr.setSourceType(sourceType);
		
		/*处理抵扣券	Start*/
		if (deductcouponId > 0) {
			DeductCoupon dc = dCouponDao.findIdKey(deductcouponId);
			// 有抵扣券的折换线上价格，否则为0
			dc.setConverAmou(coint.getStag());
			lr.setZheLx(dc.getConverAmou());// 保存线上价
			log.info("使用抵扣券---" + TimeUtil.getNow());
			sqls.add(dCouponDao.upStatusTime(5, TimeUtil.getNow(), dc.getId(),dc.getUserId()));// 把抵扣券状态改成抵扣中
		}else {
			lr.setZheLx(BigDecimal.ZERO);// 保存线上价
		}
		/*处理抵扣券	End*/
		
		sqls.add(this.getTransInsertSql(lr));
		if(Data.doTrans(sqls)){
			//重设借入用户平仓级别
 			new UserDetectDao().resetUserLevel(inUserId);
			
			try {
				UserCache.resetUserFunds(inUserId);
				UserCache.resetUserFunds(outUserId);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			/*start by xwz 20170625 融资融币借币加积分*/
			JifenManage jifenManager = new JifenManage(inUserId, 9, amount, coint.getStag(), "VIP");//9：融资融币借币
			SingletonThreadPool.addJiFenThread(jifenManager);
			/*end*/
			dr.setSuc(true);
			dr.setDes(pages.L("借贷成功。"));

		}else{
			dr.setSuc(false);
			dr.setDes(pages.L("借贷失败。"));
			
		}
		return dr;
	}

	/***
	 * 查找需要还息的借款
	 * @return
	 */
	public List<Bean> getNeedRepayInterest(int pageNo , int pageSize){
		//status in(1,3): Returning还款中,forceRepay需要平仓
		return Data.Query("select * from LoanRecord where status in(1,3) and nextRepayDate<=? limit ?,?",
				new Object[]{now(), (pageNo-1) * pageSize , pageSize}, LoanRecord.class);
	}
	
	/***
	 * 查找强制还款的记录  按金额从小到大排列
	 * @return
	 */
	public List<Bean> getNeedForceRepay(String userId){
		if(userId != null){
			return Data.Query("select * from loanrecord where status=? and inUserId='"+userId+"' order by amount asc",
					new Object[]{LoanRecordStatus.forceRepay.getKey()}, LoanRecord.class);
		}else{
			return Data.Query("select * from loanrecord where status=? order by amount asc limit 0,50",
					new Object[]{LoanRecordStatus.forceRepay.getKey()}, LoanRecord.class);
		}
	}
	
	/**
	 * 查询用户是否设置借贷费率,针对还款币种
	 * @param list
	 */
	public void setFees(List<Bean> list){
		String userIds = "";
		Map<String, String> map = new HashMap<String, String>();
		for(Bean b : list){
			LoanRecord record = (LoanRecord)b;
			if(!map.containsKey(record.getOutUserId())){
				map.put(record.getOutUserId(), record.getOutUserId());
				userIds += "," + record.getOutUserId();
			}
		}
		if(userIds.length() > 1){
			userIds = userIds.substring(1);
		}
		
		Map<String, P2pUser> maps = p2pUserDao.getUserMap(userIds);
		for(Bean b : list){
			LoanRecord record = (LoanRecord)b;
			P2pUser user = maps.get(record.getOutUserId());
			if(user != null && user.getIsSetFees() == 1){
				record.setOutUserFees(user.getFees());
			}
		}
	}
	
	/**
	 * 定时器计息处理
	 * @param fundsType
	 * @param pageNo
	 * @param pageSize
	 */
	public void repayInterest2016(int pageNo, int pageSize) {
		List<Bean> lists = getNeedRepayInterest(pageNo , pageSize);
		if(lists.size() > 0){
			//查询用户有没有设置服务费费率
			setFees(lists);
			for(Bean b : lists){
				LoanRecord po = (LoanRecord) b;
				if(_repay2016(po).isSuc()){
					log.info("为借款记录["+po.getId()+"]设置计息成功,日期：" + po.getNextRepayDate());
				}else{
					log.error("为借款记录["+po.getId()+"]设置计息失败,日期：" + po.getNextRepayDate());
				}
			}
		}else{
			log.info("没有需要设置还息的借贷");
		}
		pageNo++;
		//下一页
		if(lists.size() >= pageSize){
			repayInterest2016(pageNo , pageSize);
		}
	}
	
	public DataResponse _repay2016(LoanRecord out){
		try {
			List<OneSql> sqls = new ArrayList<OneSql>();
			//当前利率
			BigDecimal rate = out.getRate();
			
			//计息金额 * 利率
			BigDecimal totalLx = out.getJx().multiply(rate);
			
			String otherSql = "";
			if(out.getBalanceWithoutLxDays() > 0){//
				int diffDay = TimeUtil.getDiffDay(now(), out.getCreateTime());
				otherSql += ",balanceWithoutLxDays=" + (10-diffDay);
			}
			
			//status in(1,3) : Returning,forceRepay
			sqls.add(new OneSql("update LoanRecord set arrearsLx=arrearsLx+?"+otherSql+",nextRepayDate=? where id=? and status in(1,3) and nextRepayDate<=?" , 1 , 
					new Object[]{totalLx , getNextDate(out),out.getId() , now()}));
			
			p2pUserDao.addOverdraft(totalLx, out.getFundsType(), out.getInUserId(), sqls);
			
			if(Data.doTrans(sqls)){
				try {
					UserCache.resetUserFunds(out.getInUserId());
					UserCache.resetUserFunds(out.getOutUserId());
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				return new DataResponse("", true, "");
			}else{
				return new DataResponse("数据出错", false, "");
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return new DataResponse("数据出错", false, "");
	}
	
	/***
	 * 计算下次还息时间
	 * @param out
	 * @return
	 */
	public Timestamp getNextDate(LoanRecord out){
		return TimeUtil.getAfterDayDate(out.getNextRepayDate() , 1);
	}
	
	/****
	 * 自动还款
	 * 返回未还的资本信息  数组分别表示  RMB btc ltc
	 */
	public BigDecimal[] autoRepay(String userId){
		List<Bean> lists = getNeedForceRepay(userId);
		/*start by xwz 20160621 重新去取inUserId,userId*/
		String inUserId = "";
		/*end*/
		if(lists.size() > 0){
			//查询用户有没有设置服务费费率
			setFees(lists);
			for(Bean b : lists){
				LoanRecord record = (LoanRecord) b;
				log.info("为借款：["+record.getId()+"]强制还款");
				
				CoinProps coint = DatabasesUtil.coinProps(record.getFundsType());
				BigDecimal totalLx = record.getNeedLx();

				/*start by xwz 20170621*/
//				if(userId==null || userId.equals("")){
//					userId = record.getInUserId();
//				}
				if(null == userId || "".equals(userId)) {
					inUserId = record.getInUserId();
				} else {
					inUserId = userId;
				}
				/*end*/

				P2pUser p2pUser = p2pUserDao.initLoanUser(inUserId);

				PayUserBean payUser = p2pUser.getFunds().get(coint.getStag());
				BigDecimal available = payUser.getBalance();
				
				BigDecimal benxi = totalLx.add(record.getAmount());
				boolean canRepay = benxi.compareTo(available) <= 0;//有能力还本还息
				
				if(canRepay){//可以还款
					if(!_autoRepay(record)){
						//noHuans[ft.getKey() -1] = noHuans[ft.getKey() -1].add(bx);
					}
				}else{
					//noHuans[ft.getKey() -1] = noHuans[ft.getKey() -1].add(bx);
					log.info("记录ID：[" + record.getId() + "]目前无法还款。bx:" + benxi + ",available:" + available);
				}
			}
		}else{
			log.info("没有需要强制还款的借贷");
		}
		return null;
	}
	
	public boolean _autoRepay(LoanRecord record){
		return repay(record).isSuc();
	}
	
	/****
	 * 还款还息
	 * @param out
	 * @return
	 */
	public DataResponse repay(LoanRecord out){
		return _repayNew(out);
	}
	
	/***
	 * 还本还息
	 * @param out
	 * @return
	 */
	public DataResponse _repayNew(LoanRecord out) {
		
		DeductCouponDao dCouponDao = new DeductCouponDao();
		
		try {
			int lrStatus = out.getStatus();
			boolean hasEnd = false;

			BigDecimal arrearsLx = BigDecimal.ZERO;

			if (out.getThisRepay().compareTo(BigDecimal.ZERO) > 0) {// 本次要还款金额大于0
				if (out.getThisRepay().compareTo(out.getAmount()) == 0) {
					hasEnd = true;// 相等时为已结束
				} else {
					
					 /* modify by jiahua,如果是部分免息借款，应先还掉计息部分*/
					BigDecimal notFree = out.getAmount().subtract(out.getWithoutLxAmount());
					BigDecimal notFreeBalance = notFree.subtract(out.getThisRepay());
					if (notFreeBalance.doubleValue() >= 0) {
						out.setWithoutLxAmount(BigDecimal.ZERO);
					} else {
						out.setWithoutLxAmount(notFreeBalance.abs());
					}
					out.setAmount(out.getThisRepay());
				}
			} else {
				hasEnd = true;
			}
			if (hasEnd) {
				if (out.getStatus() == LoanRecordStatus.Returning.getKey()) {
					lrStatus = LoanRecordStatus.hasEnd.getKey();
				} else if (out.getStatus() == LoanRecordStatus.forceRepay.getKey()) {
					lrStatus = LoanRecordStatus.forceSuccess.getKey();
				}
			}

			/* 抵扣券减利息 Start */
			
			BigDecimal totalLx = out.getNeedLx();// 原利息
			BigDecimal surpluLx = BigDecimal.ZERO;// 抵扣后剩余还款利息
			BigDecimal dikouLx = BigDecimal.ZERO;// 最终抵扣利息（抵扣了多少利息）
			BigDecimal deglixi = BigDecimal.ZERO;// 抵扣券剩余金额
			BigDecimal dlx = BigDecimal.ZERO;// 抵扣券剩余金额 - 还款利息=剩余还款利息
			
			// 用户使用抵扣券时进入
			DeductCoupon dC = dCouponDao.findIdKey(out.getDeDuctCouponId());
			RepayOfQi rfOfQi = (RepayOfQi) rQiDao.getSumIdKey(out.getId(), out.getInUserId());
			if (out.getDeDuctCouponId() > 0 && dC != null) {
				deglixi = out.getZheLx().subtract(rfOfQi.getSumDeglx());
				
				/**利息总和计算 Start   dc.useState==5抵扣中*/ 
				if (rfOfQi.getSumDeglx() != null) {
					if (out.getAmount().compareTo(BigDecimal.ZERO) > 0) {
						if (dC.getUseState() == 5 || deglixi.compareTo(BigDecimal.ZERO) <= 0) {
							dC.setAmountDeg(deglixi);
						}
					}
				}
				/** 利息总和计算 End*/ 
				
				/**抵扣剩余计算	Start*/
				if (totalLx.compareTo(deglixi) > 0) {
					surpluLx = totalLx.subtract(deglixi);//抵扣利息后剩下的利息余额
					dikouLx = deglixi;//抵扣了多少利息
				}
				if (totalLx.compareTo(deglixi) <= 0) {
					surpluLx = BigDecimal.ZERO;//剩余
					dikouLx = totalLx;//抵扣
				}
				/**抵扣剩余计算	End*/
			}
			/** 抵扣券减利息 End*/ 

			String otherSql = "";
			if (out.getBalanceWithoutLxDays() > 0) {//
				int diffDay = TimeUtil.getDiffDay(now(), out.getCreateTime());
				otherSql += ",balanceWithoutLxDays=" + (9 - diffDay);
			}

			// 给网站（选择这一笔的原利息）
			BigDecimal sxf = totalLx.multiply(out.getFwfScale());
			
			CoinProps coint = DatabasesUtil.coinProps(out.getFundsType());
			P2pUser p2pUser = p2pUserDao.initLoanUser(out.getInUserId());
			PayUserBean inUser = p2pUser.getFunds().get(coint.getStag());
			BigDecimal available = inUser.getBalance();
			p2pUserDao.setCoint(coint);

			// 借入者
			boolean canRepayInterest = totalLx.compareTo(available) <= 0;// 有能力还息（原利息）
			BigDecimal bx = out.getShouldRepayBX();// allLiXi.add(out.getAmount());

			boolean canRepay = bx.compareTo(available) <= 0;// 有能力还本还息
			Timestamp actureDate = null;
			RepayOfQiStatus repayOfQiStatus = RepayOfQiStatus.hasRepay;
			
			/** canRepayInterest=true canRepay =false*/
			
			if (!canRepay || !canRepayInterest) {// 余额不足以偿还本息
				return new DataResponse(Lan.LanguageFormat(lan,"您的%%余额不足，不足以偿还本息。",coint.getPropTag()), false, "");
			}
			actureDate = now();
			BigDecimal benJin = BigDecimal.ZERO;
			benJin = out.getAmount();
			// 扣除借出中的
			List<OneSql> sqls = new ArrayList<OneSql>();
			p2pUserDao.subOuting(out.getAmount(), out.getOutUserId(), out.getOutUserName(), sqls);
			//增加借出者的资产
			sqls.addAll(fundsDao.addMoney(bx.subtract(sxf), out.getOutUserId(), out.getOutUserName(), coint.getPropTag()+"收款", BillType.repaymentFromP2pIn.getKey(), coint.getFundsType(), BigDecimal.ZERO, "0", true));
			sqls.add(feeDao.addFee(Integer.parseInt(out.getOutUserId()), 2, sxf, coint.getPropTag(),out.getId(),"vip_main",0));
			sqls.add(loanDao.repayLoan(out.getLoanId(),out.getAmount()));
			BigDecimal subarrearsLx = out.getArrearsLx();
			BigDecimal userArrearsLx = inUser.getOverdraft();
			
			if (subarrearsLx.compareTo(userArrearsLx) > 0) {
				subarrearsLx = userArrearsLx;
			}

			// 扣除借入者借入中的资产
			p2pUserDao.subIning(out.getAmount(), out.getRate(), out.getInUserId(), out.getInUserName(), subarrearsLx, sqls);
			//扣除借入者的资产
			sqls.addAll(fundsDao.subtractMoney(bx.subtract(dikouLx), out.getInUserId(), out.getInUserName(), coint.getPropTag()+"还款", BillType.repaymentOutToP2p.getKey(), coint.getFundsType(), BigDecimal.ZERO, "0", true));
			
			// 记录期还款(获取原利息)
			RepayOfQi roq = new RepayOfQi(out.getId(), benJin, totalLx, repayOfQiStatus.getKey(), actureDate,
					out.getNextRepayDate(), out.getFwfScale(), out.getFundsType(), out.getInUserId(), out.getInUserName(), out.getOutUserId());
			
			roq.setDealStatus(1);// 添加一个收集 0不处理、1未收集、2已收集
			//TODO:来源---Web
			if(out.getSource()>0){
				roq.setSourcetype(out.getSource());//获取来源类型	
			}

			if (out.getDeDuctCouponId() > 0) {
				if (dC.getUseState() == 5) {
					roq.setAmountDegLiXi(dikouLx);// 抵扣券，(保存抵扣利息的值)
				}
				if (dC.getUseState() == 2) {
					roq.setAmountDegLiXi(BigDecimal.ZERO);// 抵扣券，(保存抵扣利息的值)
				}
			}
			sqls.add(super.getTransInsertSql(roq));

			if (out.getStatus() == LoanRecordStatus.Returning.getKey()) {
				// 下次还息时间
				sqls.add(new OneSql(
						"UPDATE LoanRecord SET amount=amount-?, hasLx=hasLx+?, dikouLx=dikouLx+?,hasRepay=hasRepay+?,status=?,arrearsLx=arrearsLx-?,repayDate=?" + otherSql + " where id=? and status=? AND amount-?>=0",
						1, new Object[] { out.getAmount(), totalLx, dikouLx, benJin, lrStatus, out.getArrearsLx(), now(), out.getId(), LoanRecordStatus.Returning.getKey(), out.getAmount() }));
			} else if (out.getStatus() == LoanRecordStatus.forceRepay.getKey()) {
				// 下次还息时间
				sqls.add(new OneSql(
						"UPDATE LoanRecord SET amount=amount-?, hasLx=hasLx+?, dikouLx=dikouLx+?,hasRepay=hasRepay+?,status=?,repayDate=?" + otherSql + " where id=? and status=? AND amount-?>=0",
						1, new Object[] { out.getAmount(), totalLx, dikouLx, benJin, lrStatus, now(), out.getId(), LoanRecordStatus.forceRepay.getKey(), out.getAmount() }));
			}

			//更改抵扣券状态
			if (dC != null && dC.getUseState()==5) {
				BigDecimal aa=rfOfQi.getSumDeglx().add(dikouLx);
				
				if ((deglixi.compareTo(dikouLx) == 0 && deglixi.compareTo(totalLx) <= 0) || hasEnd
						|| (hasEnd==true && aa.compareTo(BigDecimal.ZERO) > 0)) {
					sqls.add(dCouponDao.updateUseStatus3(dC.getId(), dC.getUserId(), 2));
				}
			}

			if (Data.doTrans(sqls)) {
				// 重置平仓等级
				try {
					UserCache.resetUserFunds(String.valueOf(inUser.getUserId()));
					UserCache.resetUserFunds(out.getOutUserId());
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
//				new UserDetectDao().resetUserLevel(out.getInUserId());
				return new DataResponse("", true, "");
			} else {
				log.info("融资融币还款事务执行失败!");
				return new DataResponse(Lan.Language(lan, "数据出错"), false, "");
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return new DataResponse("数据出错", false, "");
	}
	
	public boolean cancelAndAutoRepay(P2pUser user) {
		return cancelAndAutoRepay(user, true);
	}
	
	/****
	 * 取消相关借入用户的交易并锁定用户
	 * 按借入记录倒序排列后自动发起试图还款一次
	 * @param record
	 */
	public boolean cancelAndAutoRepay(P2pUser user, boolean isUpdate) {//取消并自发委托
		if(user == null){
			return false;
		}
		
		String inUserId = user.getUserId();
		/*****
		 * 1.上锁交易记录*****/
		if(!isUpdate || Data.Update("update LoanRecord set inUserLock=? where inUserId=? and status=? and inUserLock=?" , new Object[]{true, inUserId , LoanRecordStatus.forceRepay.getKey() , false}) >= 0){
			if(user.getSysForce() != 1){
				log.info("用户["+user.getUserName()+"]内系统设置为不可平仓状态......");
				return false;
			}
			/*****
			 * 2.取消用户交易  调用取消用户交易API*****/
			cancel(inUserId);
			
			/*** 2.取消用户计划委托  调用取消计划委托API*****/
 			cancelPlan(inUserId);
			
			/***3.取消成功后  倒排借款记录，按当前的本金自动还款一次***/
			///取消成功后
			autoRepay(user.getUserId());
			
			user = p2pUserDao.initLoanUser(user.getUserId());
			
			// 应还金额= 借入金额+欠息
			Map<String, PayUserBean> funds = user.getFunds();
			JSONObject noHuanObj = new JSONObject();
			JSONObject needsObj = new JSONObject();
			boolean has = false;
			for(Entry<String, PayUserBean> entry : funds.entrySet()){
				String key = entry.getKey();
				PayUserBean payUser = entry.getValue();
				/** 未还金额*/
				BigDecimal noHuan = payUser.getInSuccess().add(payUser.getOverdraft()).add(payUser.getInterestOfDay());
				noHuanObj.put(key, noHuan);
				/** 需交易金额*/
				BigDecimal need = noHuan.subtract(payUser.getBalance()).setScale(3, RoundingMode.CEILING);
				needsObj.put(key, need);
				
				if(need.compareTo(BigDecimal.ZERO) > 0){
					has = true;
				}
			}
			
			if(has){
				BigDecimal converts = user.getAvailableSubOverdraft();
				//买的比例
				BigDecimal buyBiLi = converts.divide(user.getLoanInAssets() , 4 , RoundingMode.DOWN);
				
				//买的比例=资产/杠杆的比例, 决定买入价,1.1为平仓比例, 1.0为最低平仓比例,小于1则平仓后不够还款, 
				//现在的策略为:买的比例 = 当前资产比例 >1.03 时, 在1.03相对应的价格来挂单, <1.03时,以当前资产比例
				buyBiLi = buyBiLi.subtract(new BigDecimal("0.03"));
				if(buyBiLi.compareTo(BigDecimal.ONE)<0){
					buyBiLi = BigDecimal.ONE;
				}
				
				//卖的比例
				BigDecimal sellBiLi = BigDecimal.ONE.divide(buyBiLi, 4 , RoundingMode.UP);
				
				log.info("买的比例:" + buyBiLi + ", 卖的比例:" + sellBiLi);
				
				/***4.发起自动下单委托***/
				//强制处理 这里默认按挂单处理
				String _forceType = "1";
				if(StringUtils.isBlank(_forceType)){
					_forceType = "1";
				}
				if(_forceType.equals("1")){
					_auto(user , needsObj , sellBiLi , buyBiLi);
				}else if(_forceType.equals("2")){
				}
			}else{//已经全部还款  不用强制委托下单了
				List<OneSql> sqls2 = new ArrayList<OneSql>();
				sqls2.add(new OneSql("update p2pUser set repayLock=? where userId=? and repayLock=?" , 1 , new Object[]{false , user.getUserId() , true}));
				/** 解锁操作，需要对api解锁*/
				
				new UserDao().updateRepayLock(user.getUserId(), 2);
				
				Data.doTrans(sqls2);
				return true;
			}
			
		}else{//可能已被关联的记录处理过了
			log.info("用户ID：["+inUserId+"]已被上锁]");
		}
		return false;
	}
	
	/******
	 * <h3>平仓操作</h3>
	 * 1.取消用户委托单<br>
	 * 2.根据需求进行新的委托<br>
	 * @param userId 用户ID
	 */
	private boolean _auto(P2pUser user, JSONObject needs , BigDecimal sellBili , BigDecimal buyBili) {
		////此时理论上只会存在要么欠RMB  要么欠BTC、LTC
		String userId = user.getUserId();
		Map<String, PayUserBean> funds = user.getFunds();
		
		////.....待对接自动
		log.info("对用户["+user.getUserName()+"]启动自动委托订单平仓处理程序......");
		JSONObject prices = LoanAutoFactory.getPrices();
		//为了满足不亏本100%  需要算出一个卖价
		Iterator<String> it = needs.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if(needs.getBigDecimal(key).compareTo(BigDecimal.ZERO) > 0){
				Iterator<String> sonit = needs.keySet().iterator();
				while (sonit.hasNext()) {
					String coin = (String)sonit.next();
					if(!key.equals(coin)){//去除本身
						if(prices.containsKey(coin) //有市场价才可以成交，没有需要还的并且资金够的才可以成交
								&& needs.getBigDecimal(coin).compareTo(BigDecimal.ZERO) <= 0 
								&& funds.get(coin).getBalance().compareTo(DigitalUtil.getBigDecimal(0.001)) > 0){
							BigDecimal sellPrice = prices.getBigDecimal(coin).multiply(sellBili).setScale(8 , RoundingMode.UP);
							log.info("进行买操作，userId=" + userId + ", coin=" + coin + "， key=" + key+", sellPrice=" + sellPrice);
							sell(userId, coin, funds.get(coin).getBalance().setScale(8, RoundingMode.DOWN), sellPrice);
						}
					}else{//是本身，如果有市场价，就买
						if(prices.containsKey(key)){
							BigDecimal buyPrice = prices.getBigDecimal(key).multiply(buyBili).setScale(8, RoundingMode.DOWN);
							log.info("进行卖操作，userId=" + userId + ", coin=" + coin + "， key=" + key+", buyPrice=" + buyPrice);
							buy(userId, key, needs.getBigDecimal(key), buyPrice);
						}
					}
				}
			}
		}

		//处理完毕,解除锁定, 不管委托交易是否完全成交,也要解锁
		List<OneSql> sqls2 = new ArrayList<OneSql>();
		sqls2.add(new OneSql("update p2pUser set repayLock=? where userId=? and repayLock=?" , 1 , new Object[]{false , user.getUserId() , true}));
		/** 解锁操作，需要对api解锁*/
		/*ApiKeyDao apiDao = new ApiKeyDao();
		sqls2.add(apiDao.unlock(user.getUserId()));*/
		new UserDao().updateRepayLock(user.getUserId(), 2);
		
		Data.doTrans(sqls2);
		
		return true;
	}
	
	private void buy(String userId, String currency, BigDecimal number, BigDecimal unitPrice){
		entrust(userId, currency, number, unitPrice, 1);
	}
	
	private void sell(String userId, String currency, BigDecimal number, BigDecimal unitPrice){
		entrust(userId, currency, number, unitPrice, 0);
	}
	
	/**
	 * 通过API委托
	 * @param userId 用户ID
	 * @param currency 币种
	 * @param number 数量
	 * @param unitPrice 单价
	 * @param isBuy 是否买 1买 0卖
	 * @author zhanglinbo 20170104
	 */
	public void entrust(String userId, String currency, BigDecimal number, BigDecimal unitPrice, int isBuy) {
			try {
				Map<String, JSONObject> marketMaps =  Market.getMarketsMap();//获取盘口配置信息
				JSONObject m = null;//市场信息
				if(marketMaps!=null && !marketMaps.isEmpty()){
					for(Entry<String, JSONObject> entry :marketMaps.entrySet()){
						 JSONObject temp = entry.getValue();
						 if(temp.getString("numberBi").equalsIgnoreCase(currency)){
							 m = temp;
							 break;
						 }	
					}
				}
				if(m==null){
					log.error(currency+" 币种找不到对应的市场 进行委托下单");
					return;
				}

				/*start by flym 20170622 购买时添加上手续费的扣除数量*/
        		/*先把价格和数量按币种市场配置的小数位数截取*/
				if(0 == isBuy) {
					/*卖数量DOWN*/
					number = number.setScale(m.getIntValue("numberBixDian"), RoundingMode.DOWN);
					log.info("number1 = " + number);
				} else {
          			/*买数量UP*/
					number = number.setScale(m.getIntValue("numberBixDian"), RoundingMode.UP);
					log.info("number1 = " + number);
          			/*总共加上交易书续费扣除后应该买入的数量*/
          			/*获取计算用户的手续费费率 标志手续费*折扣率*/
					BigDecimal transFeeRate = getFeeRateByUserId(String.valueOf(userId), m.getBigDecimal("feeRate"));
					log.info("transFeeRate = " + transFeeRate);
					log.info("num1-1= " + number.multiply(transFeeRate));
					number = number.divide(BigDecimal.ONE.subtract(transFeeRate), m.getIntValue("numberBixDian"), RoundingMode.UP);
					log.info("number2 = " + number);
				}

				log.info("numberBixDian = " + m.getIntValue("numberBixDian"));
				/*买和卖价格都是UP*/
				log.info("unitPrice1 = " + unitPrice);
				unitPrice = unitPrice.setScale(m.getIntValue("exchangeBixDian"), RoundingMode.UP);
				log.info("unitPrice2 = " + unitPrice + ", exchangeBixDian = " + m.getIntValue("exchangeBixDian"));
				log.info("isBuy = " + isBuy + ", userId = " + userId + ", currency = " + currency + ", number = " + number + ", unitPrice = " + unitPrice);
        		/*end*/
				Message myObj = new Message();
				myObj.setUserId(Integer.parseInt(userId));
				myObj.setWebId(6);
				myObj.setNumbers(number);
				myObj.setTypes(isBuy);
				myObj.setUnitPrice(unitPrice);
				myObj.setStatus(0);
				myObj.setMarket(m.getString("market"));//市场名称
				String param = HTTPTcp.ObjectToString(myObj);
				String rtn = HTTPTcp.Post(m.getString("ip"), m.getIntValue("port"), "/server/entrust", param);
//				log.info(rtn);
				Message rtn2 = (Message) HTTPTcp.StringToObject(rtn);
				
				log.info(rtn2.getMessage() + ",code : " + rtn2.getStatus());
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		
	}

	/**
	 * start by flym 20170622 购买时添加上手续费的扣除数量
	 * 获取计算用户的手续费费率 标志手续费*折扣率
	 * @param userId 用户ID
	 * @param m 市场配置对象
	 * @return 计算后的费率
	 */
	private static BigDecimal getFeeRateByUserId(String userId, BigDecimal feeRate) {
		log.info("feeRate = " + feeRate);
		BigDecimal feeDiscount =(BigDecimal) Cache.GetObj("user_vip_fee_discount_" + userId);
		log.info("feeDiscount = " + feeDiscount);
		if(feeDiscount != null){
			feeRate = feeRate.multiply(feeDiscount);
		}
		return feeRate;
	}
	
	
	/**
	 * 撤销该用户的所有挂单 
	 * @param userId 用户ID
	 * @return 返回操作成功
	 * @author zhanglinbo 20170104
	 */
	private boolean cancel(String userId) {
		try {
			Map<String, JSONObject> marketMaps =  Market.getMarketsMap();//获取盘口配置信息
			int webid = 6;
			BigDecimal priceLow = BigDecimal.ZERO;
			BigDecimal priceHigh = BigDecimal.ZERO;
			int type = 3;//0// 按照区间设置 1取消买入 2取消卖出 3 取消所有
			MessageCancle myObj = new MessageCancle();
			myObj.setUserId(Integer.parseInt(userId));
			myObj.setWebId(webid);
			myObj.setPriceLow(priceLow);
			myObj.setPriceHigh(priceHigh);
			myObj.setType(type);
			if(marketMaps!=null && !marketMaps.isEmpty()){
				for(Entry<String, JSONObject> entry :marketMaps.entrySet()){
					JSONObject m = entry.getValue();
					myObj.setMarket(m.getString("market"));
					String param = HTTPTcp.ObjectToString(myObj);
					String rtn = HTTPTcp.DoRequest2(true, m.getString("ip"),m.getIntValue("port"),"/server/cancelmore", param);
//					log.info(rtn);
					MessageCancle rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
					log.info(rtn2.getMessage());
				}
			}
			return true;	
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return false;
	}
	
	/**
	 * 取消所有计划委托
	 * @param userId
	 * @return
	 */
	private boolean cancelPlan(String userId) {
		try {
			Map<String, JSONObject> marketMaps =  Market.getMarketsMap();//获取盘口配置信息
			int webid = 6;
			BigDecimal priceLow = BigDecimal.ZERO;
			BigDecimal priceHigh = BigDecimal.ZERO;
			int type = 3;//0// 按照区间设置 1取消买入 2取消卖出 3 取消所有
			MessageCancle myObj = new MessageCancle();
			myObj.setUserId(Integer.parseInt(userId));
			myObj.setWebId(webid);
			myObj.setPriceLow(priceLow);
			myObj.setPriceHigh(priceHigh);
			myObj.setType(type);
			
			if(marketMaps!=null && !marketMaps.isEmpty()){
				for(Entry<String, JSONObject> entry :marketMaps.entrySet()){
					JSONObject m = entry.getValue();
					myObj.setMarket(m.getString("market"));
					String param = HTTPTcp.ObjectToString(myObj);
					String rtn = HTTPTcp.DoRequest2(true, m.getString("ip"),m.getIntValue("port"),"/server/cancelmorePlanEntrust", param);
					log.info(rtn);
					MessageCancle rtn2 = (MessageCancle) HTTPTcp.StringToObject(rtn);
					log.info(rtn2.getMessage());
				}
			}
			return true;	
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		return false;
	}

	/**
	 * 获取投资人收益
	 * @param userId
	 * @param fundsType
     * @return
     */
	public BigDecimal getProfit(String userId,int fundsType, String columnStr) {
		List<List<Object>> list =  (List<List<Object>>)Data.Query("select riskManage,sum(" + columnStr + ") from loanrecord where  outUserId = '" +userId + "' and status in(1,3) and fundsType = ? group by riskManage",new Object[]{fundsType});
		BigDecimal profit = BigDecimal.ZERO;
		BigDecimal risk1 = new BigDecimal("0.6");
		BigDecimal risk2 = new BigDecimal("0.5");
		if(list != null && list.size() > 0) {
			for(List<Object> tmplist :list){
				int riskManage = (int)tmplist.get(0);
				BigDecimal interest = (BigDecimal)tmplist.get(1);
				if(riskManage == 1){
					profit = profit.add(interest.multiply(risk1));
				}else{
					profit = profit.add(interest.multiply(risk2));
				}
			}
		}
		return profit;
	}

}