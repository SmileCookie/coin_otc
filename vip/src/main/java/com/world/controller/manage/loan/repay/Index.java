package com.world.controller.manage.loan.repay;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.entity.SourceType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.loan.dao.DeductCouponDao;
import com.world.model.loan.dao.DefaultLimitDao;
import com.world.model.loan.dao.LoanDao;
import com.world.model.loan.dao.LoanRecordDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.dao.RepayOfQiDao;
import com.world.model.loan.dao.RevenuedayDao;
import com.world.model.loan.entity.DeductCoupon;
import com.world.model.loan.entity.DefaultLimitType;
import com.world.model.loan.entity.Loan;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.LoanStatus;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.entity.RepayOfQi;
import com.world.model.loan.entity.Revenueday;
import com.world.model.loan.entity.RewardEntity;
import com.world.util.DigitalUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.SessionUser;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index extends UserAction {
	RepayOfQiDao dao = new RepayOfQiDao();
	DeductCouponDao dCouponDao=new DeductCouponDao();
	DefaultLimitDao defaDao=new DefaultLimitDao();
	P2pUserDao p2pDao = new P2pUserDao();

//	@Page(Viewer = "/cn/manage/loan/repay/list.jsp")
	public void index() {
		int pageNo = intParam("page");
		int loanId = intParam("id");
		boolean isHost = booleanParam("host");//是否为当前的投资者
		Timestamp date = null;
		if(param("date").length() > 0){
			date = dateParam("date");
		}
		String userId = userIdStr();
		
		Query q = dao.getQuery();
		q.setSql("select actureDate,benJin,liXi,repayofqi.status,repayofqi.fundsType,amount + hasRepay amount from repayofqi " +
				"left join  loanrecord on repayofqi.loanRecordId = loanrecord.id where loanRecordId = ?")
					  .setParams(new Object[]{loanId})
					  .setCls(RepayOfQi.class);
		if(date != null){
			if(isHost){
				q.append(" forecastDate>='" + date+"'");
			}else{
				q.append(" forecastDate<'" + date+"'");
			}
		}
		
		int total = dao.count();
 		if(total > 0){
 			q.append("order by forecastDate desc");
 			List<Bean> list = dao.findPage(pageNo, PAGE_SIZE);
			BigDecimal accruedRepay = BigDecimal.ZERO;
			for(Bean bean :list){
				RepayOfQi repayOfQi = (RepayOfQi) bean;
				accruedRepay = accruedRepay.add(repayOfQi.getBenJin());
				repayOfQi.setAccruedRepay(accruedRepay);
			}
 			setAttr("lists", list);
 		}
		Bean sumkey = dao.getSumIdKey(loanId, userId);
		setAttr("sumkey", sumkey);
 		LoanRecordDao lrDao = new LoanRecordDao();
 		LoanRecord lr = (LoanRecord) lrDao.getById(LoanRecord.class, loanId);
 		
 		if(lr == null){
 			new RuntimeException("没有找到的借款");
 		}
 		int isIn = 0;
 		if(lr.getInUserId().equals(userId)){
 			isIn = 1;
 		}else if(lr.getOutUserId().equals(userId)){
 			isIn = 2;
 		}
 		
 		setAttr("loanRecord", lr);
 		setAttr("isIn", isIn);
 		
		setPaging(total, pageNo);
		setAttr("lid", loanId);
		setAttr("pageNo", pageNo);
	}

//	@Page(Viewer = "/en/u/repay/ajax.jsp")
	public void ajax() {
		index();
	}
	
	/**
	 * TODO:显示还款框的抵扣计算,不做任何存储过程
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 *
	 * @author chenruidong
	 */
	@Page(Viewer = JSON)
	public void dikoulixi() {
		try {
			int id = intParam("id");
			String userId = userIdStr();
			BigDecimal shuruje = decimalParam("repay");// 手动输入还款金额
			int repayType = intParam("repayType");// 还款方式
			LoanRecord lr = (LoanRecord) Data.GetOne("select * from LoanRecord where id=? and inUserId=? AND status=?",
					new Object[] { id, userId, LoanRecordStatus.Returning.getKey() }, LoanRecord.class);

			if (shuruje.compareTo(lr.getShouldRepayBX()) > 0) {
				json(L("不能大于应还总额。"), false, "");
				return;
			}

			if (repayType == 1) {
				lr.setAmount(shuruje);
			}

			BigDecimal zlx = lr.getNeedLx();// 利息
			BigDecimal zj = BigDecimal.ZERO;// 总金额

			if (lr.getDeDuctCouponId() > 0) {
				DeductCoupon dCoupon = dCouponDao.findIdKey(lr.getDeDuctCouponId());

				RepayOfQi rfOfQi = (RepayOfQi) dao.getSumIdKey(lr.getId(), lr.getInUserId());
				
				if (rfOfQi.getSumDeglx() != null) {
					// 剩余抵扣 = 总抵扣券金额-已用抵扣券金额
					BigDecimal deglixi = dCoupon.getAmountDeg().subtract(rfOfQi.getSumDeglx());
					if (lr.getAmount().compareTo(BigDecimal.ZERO) > 0) {

						if (dCoupon.getUseState() == 5 || deglixi.compareTo(BigDecimal.ZERO) <= 0) {
							dCoupon.setAmountDeg(deglixi);
						}
					}
					
				}
				
				// 如果已使用 ，抵扣券为零
				if (dCoupon.getUseState() == 2) {
					dCoupon.setAmountDeg(BigDecimal.ZERO);
				}

				// 获取剩下的利息
				if (zlx.compareTo(dCoupon.getAmountDeg()) > 0) {
					zj = zlx.subtract(dCoupon.getAmountDeg()).add(lr.getAmount());
				}
				if (zlx.compareTo(dCoupon.getAmountDeg()) <= 0) {
					zj = lr.getAmount();
				}
			} else {
				zj = lr.getAmount().add(zlx);
			}

			if (shuruje == null || shuruje.doubleValue() == 0) {
				zj = BigDecimal.ZERO;
			}

			json("", true, String.valueOf(zj));
//			json("", true, String.valueOf(zlx));

		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

//	@Page(Viewer = "/cn/manage/loan/repay/huan.jsp")
	public void repay() {
		int id = intParam("id");
		int pageNo = intParam("pageNo");
		setAttr("pageNo", pageNo);
		String userId = userIdStr();

		/*start by xwz 20170623 ps:需平仓状态的也需要还款*/
//		LoanRecord lr = (LoanRecord) Data.GetOne("select * from LoanRecord where id=? and inUserId=? AND status=?",
//				new Object[] { id, userId, LoanRecordStatus.Returning.getKey() }, LoanRecord.class);
		LoanRecord lr = (LoanRecord) Data.GetOne("select * from LoanRecord where id=? and inUserId=? AND status in(1,3)",
				new Object[] { id, userId}, LoanRecord.class);
		/*end*/
		setAttr("curLoan", lr);

		// 有抵扣券的
		if (lr.getDeDuctCouponId() > 0) {
			DeductCoupon dc = dCouponDao.findIdUseKey(lr.getDeDuctCouponId());
			RepayOfQi rfOfQi = (RepayOfQi) dao.getSumIdKey(lr.getId(), lr.getInUserId());
			setAttr("jine", dc);// 获取抵扣券金额
			setAttr("zhelx", lr.getZheLx());// 折算利息
			setAttr("yidikou", rfOfQi.getSumDeglx());// 获取抵扣券已用金额
			// dc.setConverAmou(lr.getFundType());

		}
		//获取费率
//		String p2pOutRate = GlobalConfig.getValue("p2pOutRate" + EnumUtils.getEnumByKey(lr.getFundType(), FundsType.class).getValue());
		
		CoinProps coint = DatabasesUtil.coinProps(lr.getFundsType());

		// TODO: 2017/6/18 直接取的配置文件里的利率
		String p2pOutRate = defaDao.findOKT(coint.getStag(), "p2pOutRate").getValueName();
		setAttr("p2pOutRate", p2pOutRate);
		if (lr != null) {
			DecimalFormat df = new DecimalFormat("0.0#######");
			P2pUser p2pUser = p2pDao.initLoanUser(userId);

			if (p2pUser != null) {
				BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
				setAttr("minUnit", df.format(minUnit.compareTo(lr.getCouldRepay()) > 0 ? lr.getCouldRepay() : minUnit));


				BigDecimal userAvailable = p2pUser.getFunds().get(coint.getStag()).getBalance();
				setAttr("userAvailable", trimZeroAfterPoint(userAvailable.setScale(6, BigDecimal.ROUND_DOWN)));
			}
		}
	}

	//去掉小数点后的0和取整
	private BigDecimal trimZeroAfterPoint(BigDecimal val){
		DecimalFormat df = new DecimalFormat("###.#########");
		return new BigDecimal(df.format(val));
	}

	@Page(Viewer = JSON)
	public void huan() {
		int id = intParam("id");
		String userId = userIdStr();
		BigDecimal repay = decimalParam("repay").setScale(2, RoundingMode.DOWN);
		int repayType = intParam("repayType");
		LoanRecordDao loanRecordDao = new LoanRecordDao();
		loanRecordDao.setLan(lan);
		try {
			LoanRecord lr = (LoanRecord) Data.GetOne("select * from LoanRecord where id=? and inUserId=? AND status=?", 
					new Object[]{id , userId, LoanRecordStatus.Returning.getKey()} , LoanRecord.class);
			if(lr != null){
				CoinProps coint = DatabasesUtil.coinProps(lr.getFundsType());
				
				//还欠息
				if(repayType==2){
					Query q = dao.getQuery();
					q.setSql("select * from repayOfQi where loanRecordId = ? and status in(0,2) and userId=?")
								  .setParams(new Object[]{id,userId})
								  .setCls(RepayOfQi.class);
					
					List<RepayOfQi> list = q.getList();
					boolean success = true;
					String failMsg = "";
					for (RepayOfQi roq : list) {
						DataResponse dr = dao.repayInterest(roq);
						if(!dr.isSuc()){
							if(dr.getDes().contains("资金操作异常") || dr.getDes().contains("余额不足")){
								success = false;
								failMsg = L(dr.getDes());
							}
						}
					}
					
					if(!success){
						json(failMsg, false, "");
					}else{
						json(L("还息成功！"), true, "");
					}
					return;
				}
				
				if(lr.getArrearsLx().compareTo(BigDecimal.ZERO) > 0){
					json(L("您还有欠息未还，请先还清利息。"), false, "");
					return;
				}
				
				BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
				if(lr.getAmount().compareTo(BigDecimal.ZERO) > 0){
					minUnit = lr.getAmount().compareTo(minUnit)>0?minUnit:lr.getAmount();
				}
				
				//部分还款
				if(repayType == 1){
					if(repay.compareTo(minUnit) < 0){
						json(L("还款金额不能小于最小还款金额")+minUnit + coint.getPropTag(), false, "");
						return;
					}
					
					if(lr.getAmount().subtract(repay).compareTo(minUnit) < 0 && repay.compareTo(lr.getAmount()) < 0){
						json(L("部分还款之后剩余还款金额不能小于")+minUnit + coint.getPropTag(), false, "");
						return;
					}
					
					if(repay.compareTo(lr.getAmount()) > 0){
						json(L("还款金额不能大于可还款的金额。"), false, "");
						return;
					}
				}
				String pass = param("password");
				if(adminId() == 0){
//					if(!safePwd(pass, userId)){
//						return;
//					}
				}
				
				if(repayType == 1){
					lr.setThisRepay(repay);
				}
				
				P2pUser p2pUser = new P2pUserDao().getById(lr.getOutUserId());
				if(p2pUser.getIsSetFees() == 1){
					//设置用户自己的费率
					lr.setOutUserFees(p2pUser.getFees());
				}
				DataResponse dr = loanRecordDao.repay(lr);
				if(dr.isSuc()){
					json("还款成功", true, "");
					//已完全还款
					LoanRecord lrRepayed = (LoanRecord) Data.GetOne("select * from LoanRecord where id=? and inUserId=? AND status=?", 
							new Object[]{id , userId, LoanRecordStatus.hasEnd.getKey()} , LoanRecord.class);
					if(lrRepayed != null ){
						
						Loan loan = (Loan) Data.GetOne("select * from Loan where Id = ? and userId=?", new Object[]{lrRepayed.getLoanId(), lr.getOutUserId()}, Loan.class);//(Loan) new LoanDao().getById(Loan.class, loanId);
						//成功借出&&部分借出 （有机会点击"取消"）
						if(loan == null || (loan.getStatus() == LoanStatus.success.getKey() && loan.getHasAmount().compareTo(loan.getAmount()) < 0 )) return;
						List hasRepayTotal = (List)Data.GetOne("Select sum(hasRepay) from LoanRecord where loanId=? AND outUserId=? AND status=?",new Object[]{lrRepayed.getLoanId(), lr.getOutUserId(), LoanRecordStatus.hasEnd.getKey()});
//						if(hasRepayTotal.size() == 0) return;
						BigDecimal repayTotal = DigitalUtil.getBigDecimal(hasRepayTotal.get(0)==null?0:hasRepayTotal.get(0));

						 //循环 && 还款记录完全还款
						if(loan.getIsLoop()){// && loan.getAmount().equals(repayTotal)
							P2pUser pUser = p2pDao.initLoanUser(lr.getOutUserId());
							
							BigDecimal amount = lrRepayed.getHasRepay();
							//借贷金额
							if(amount.compareTo(minUnit) < 0){
								log.info("您的最低投资金额不能小于"+minUnit+"！");
								return;
							}
							
							if(pUser.hasLoanIn()){
								log.info("您是借入者，不能投资！");
								return;
							}
							
							if(amount.compareTo(pUser.getBalance(coint.getStag())) > 0){
								log.info("您的可投资"+coint.getStag()+"余额不足，发布新投资失败！");
								return;
							}
							
//							FundsRange fr = FundsRange.loanRate;
//							String error = fr.error(rate);
//							if(error != null){
//								json(error , false , "");
//								return;
//							}
							
							P2pUserDao p2pUserDao = new P2pUserDao();
							LoanDao inDao = new LoanDao();
							List<OneSql> sqls = new ArrayList<OneSql>();
//							if(loan.getAmount().equals(repayTotal)){
//								sqls.add(inDao.cancelLoopSql(loan.getId()));
//							}
							
							sqls.add(inDao.updateBidSqls(loan, amount));
//							sqls.add(inDao.insertSql(new Loan(false , loan.getUserId(), loan.getUserName(), ft.getKey(), amount, LoanStatus.waiting.getKey(), TimeUtil.getNow(),
//									loan.getInterestRateOfDay(), 2, loan.getLowestAmount(), loan.getHighestAmount(), "", loan.getRateForm() , loan.getRateAddVal() ,loan.getRiskManage(),loan.getWithoutLx(),
//									true,lrRepayed.getId())));//自动生成记录，isLoop=false, loanId=Main Load Id

							p2pUserDao.trans(amount, loan.getUserId(), loan.getUserName(), "P2P资金转出(循环投资)", false , sqls);
						}
					}
				}else{
					json(dr.getDes(), false, "");
				}
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
		json(L("未知错误导致操作失败！"), false, "");
	}
	
	
	/**
	 * 最新还款
	 *
	 */
	@Page(Viewer = JSON)
	public void huanNew() {
		try {
			int id = intParam("id");
			String userId = userIdStr();
			BigDecimal repay = BigDecimal.ZERO;
			try{
				repay = decimalParam("repay");// 还款金额
			}catch(Exception e){
				json(L("后台-融资融币借入-还款提示-6"), false, "");
				return;
			}


			int repayType = intParam("repayType");// 还款方式

			LoanRecordDao loanRecordDao = new LoanRecordDao();
			loanRecordDao.setLan(lan);
			LoanRecord lr = (LoanRecord) Data.GetOne("select * from LoanRecord where id=? and inUserId=? AND status in (?,?)",
					new Object[] { id, userId, LoanRecordStatus.Returning.getKey(), LoanRecordStatus.forceRepay.getKey()}, LoanRecord.class);

			if (lr != null) {
				CoinProps coint = DatabasesUtil.coinProps(lr.getFundsType());
				BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
				if (lr.getAmount().compareTo(BigDecimal.ZERO) > 0) {
					minUnit = lr.getAmount().compareTo(minUnit) > 0 ? minUnit : lr.getAmount();
				}

//				// 部分还款
//				if (repayType == 1) {
//					if (repay.compareTo(minUnit) < 0) {
//						json(L("还款金额不能小于最小还款金额%%.", minUnit + coint.getPropTag()), false, "");
//						return;
//					}
//
//					if (lr.getAmount().subtract(repay).compareTo(minUnit) < 0 && repay.compareTo(lr.getAmount()) < 0) {
//						json(L("部分还款之后剩余还款金额不能小于%%.", minUnit + coint.getPropTag()), false, "");
//						return;
//					}
//
//					if (repay.compareTo(lr.getAmount()) > 0) {
//						json(L("还款金额不能大于可还款的金额。"), false, "");
//						return;
//					}
//
//					lr.setThisRepay(repay);
//				}

				/*start by xwz 2016-06-09*/
				if (repayType == 0) {// 全部还款
					if(repay.compareTo(lr.getCouldRepay().add(lr.getNeedLx())) != 0){
						throw new Exception("该客户非法还款");
					}
				}else if(repayType == 1){// 部分还款
					if (repay.compareTo(minUnit) < 0) {
						json(L("后台-融资融币借入-还款提示-1", minUnit + coint.getPropTag()), false, "");
						return;

					}

					if (lr.getAmount().subtract(repay).compareTo(minUnit) < 0 && repay.compareTo(lr.getAmount()) < 0) {
						json(L("后台-融资融币借入-还款提示-2", minUnit + coint.getPropTag()), false, "");
						return;
					}

					if (repay.compareTo(lr.getAmount()) > 0) {
						json(L("后台-融资融币借入-还款提示-3"), false, "");
						return;
					}
					lr.setThisRepay(repay);
				}else{
					throw new Exception("该客户非法还款");
				}
				/*end*/

				P2pUser p2pUser = new P2pUserDao().getById(lr.getOutUserId());
				if (p2pUser.getIsSetFees() == 1) {
					// 设置用户自己的费率
					lr.setOutUserFees(p2pUser.getFees());
				}
				//TODO:来源---Web
				lr.setSource(SourceType.WEB.getKey());
				DataResponse dr = loanRecordDao._repayNew(lr);


				if (dr.isSuc()) {
					json(L("后台-融资融币借入-还款提示-4"), true, "");
				} else {
					json(L(dr.getDes()), false, "");
				}
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
		json(L("后台-融资融币借入-还款提示-5"), false, "");
	}

	//Close By suxinjie 一期屏蔽该功能
	@SuppressWarnings({ "unchecked", "rawtypes" })
	//@Page(Viewer = XML)
	public void qihuan() {
		int id = intParam("id");
		String userId = userIdStr();
		RepayOfQiDao roqd = new RepayOfQiDao();
		Query q = dao.getQuery();
		q.setSql("select * from repayOfQi where id = ? and userId=?").setParams(new Object[]{id , userId}).setCls(RepayOfQi.class);
		try {
			RepayOfQi roq = (RepayOfQi) q.getOne();
			if(roq != null){
				DataResponse dr = roqd.repayInterest(roq);
				Write(dr.getDes() , dr.isSuc() , "");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
		Write(L("未知错误导致操作失败！"), false, "");
	}

//	@Page(Viewer = XML)
	public void batchQihuan() {
		String ids = param("ids");
		String userId = userIdStr();
		if(StringUtils.isEmpty(ids)){
			Write(L("请先择需要还款的记录！"), false, "");
			return;
		}
		try {
			String [] array = ids.split(",");
			List<RepayOfQi> list = new ArrayList<RepayOfQi>();
			RepayOfQiDao roqd = new RepayOfQiDao();
			for (int i=array.length-1;i >=0 ;i--) {
				String string = array[i];
				if(StringUtils.isNotEmpty(string)){
					int id = Integer.parseInt(string);
					Query q = dao.getQuery();
					q.setSql("select * from repayOfQi where id = ?")
								  .setParams(new Object[]{id})
								  .setCls(RepayOfQi.class);
					RepayOfQi roq = (RepayOfQi) q.getOne();
					list.add(roq);
				}
			}
			if(list.size() > 0){
				DataResponse dr = roqd.batchRepayInterest(list, userId);
				Write(dr.getDes() , dr.isSuc() , "");
				return;
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
		Write(L("未知错误导致操作失败！"), false, "");
	}
	
	protected int adminId() {
		
		try {
			SessionUser user = session.getAdmin(this);
			if(user != null){
				return Integer.parseInt(user.getRid());
			}
		} catch (NumberFormatException e) {
			log.error("内部异常", e);
		}
		return 0;
	}
	
	// 每天的利息折合人民币收入(ajax嵌入, 点击触发)
//	@Page(Viewer = "/cn/manage/loan/repay/reward.jsp")
	public void reward() {

		// 获取参数
		String fundsType = param("fundsType");
		String outUserId = userIdStr();
		try {

			List<String> params = new ArrayList<String>();
			params.add(outUserId);
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DATE(actureDate) date,fundsType,ROUND(SUM(liXi*(1-fwf)),2) liXi ");
			sql.append(" FROM repayofqi where actureDate>=DATE_SUB(NOW(),INTERVAL 15 DAY) ");
			sql.append(" and outUserId=? ");
			if (StringUtils.isNotBlank(fundsType)) {
				sql.append("and fundsType=?");
				params.add(fundsType);
			}
			sql.append(" group by outUserId,DATE(actureDate),fundsType");
			sql.append(" ORDER BY DATE DESC ");
			//放到实体里面
			List<RewardEntity> lists = Data.QueryT(sql.toString(), params.toArray(),RewardEntity.class);
			setAttr("lists", lists);
			//计算最大值
			BigDecimal max = BigDecimal.ZERO;
			for (RewardEntity rewardEntity : lists) {
				BigDecimal converts = rewardEntity.getConverts();
				if (converts.compareTo(max)>0) {
					max = converts;
				}
			}
			setAttr("max", max);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	/**
	 * 日收益
	 */
	// 折合总利息(ajax嵌入, 点击触发)
	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@Page(Viewer = "/cn/manage/loan/earnings/earn.jsp")
	public void earn() {
		RevenuedayDao reveDao = new RevenuedayDao();
		try {
			// 辅助
			String userId = userIdStr();
			int pageNo = intParam("page");
			int pageSize = 10;
			
			if (userId != null) {
				//查出30天平均值
				Bean bean = Data.GetOneT(
						"SELECT AVG(converts) dailyAverage FROM revenueday WHERE userId=? AND DATE_SUB(CURDATE(), INTERVAL 1 MONTH) <= date(earningTime)",
						new Object[] { userId }, Revenueday.class);
				Revenueday reven = (Revenueday) bean;
				//今月内的汇总
				Bean bean2 = Data.GetOneT(
						"SELECT SUM(converts) thisMonthSum  FROM revenueday WHERE userId=? AND date_format(earningTime,'%Y-%m')=date_format(now(),'%Y-%m')",
						new Object[] { userId }, Revenueday.class);
				Revenueday reven2 = (Revenueday) bean2;
				//查出上月的汇总
				Bean bean3 = Data.GetOneT(
						"SELECT SUM(converts) lastMonthSum FROM revenueday WHERE userId=? AND date_format(earningTime,'%Y-%m')=date_format(date_sub(curdate(), interval 1 month),'%Y-%m')",
						new Object[] { userId }, Revenueday.class);
				Revenueday reven3 = (Revenueday) bean3;
				setAttr("reven", reven);
				setAttr("reven2", reven2);
				setAttr("reven3", reven3);
				log.info("用户Id："+userId +", 30天平均值：" + reven.getDailyAverage() + ", 今月汇总： " + reven2.getThisMonthSum() + ", 上月汇总： " + reven3.getLastMonthSum());
			}
			// 查询
			Query query = reveDao.getQuery();
			query.setSql("SELECT * FROM revenueday");
			query.setCls(Revenueday.class);
			// 添加条件
			query.append("userId='" + userId + "'");
			query.append("ORDER BY earningTime DESC");

			long pageCount = reveDao.count();
			if (pageCount > 0) {
				List<Revenueday> listBean = reveDao.findPage(pageNo, pageSize);
				// 获取各币最大值
				BigDecimal earnMax = null;
				BigDecimal conMax = null;
				
				Map<Integer,BigDecimal> earnMaxMap = new HashMap<Integer,BigDecimal>();
				Map<Integer,BigDecimal> conMaxMap = new HashMap<Integer,BigDecimal>();
				
				for (Revenueday revenueday : listBean) {
				
					//各币种未折算最大值
					earnMax = earnMaxMap.get(revenueday.getFundsType());
					if (revenueday.getEarnings() != null) {
						BigDecimal earn = revenueday.getEarnings();
						if (earnMax == null) {
							earnMax = earn;
							earnMaxMap.put(revenueday.getFundsType(), earnMax);
						}else if(earnMax.compareTo(earn)<0){
							earnMax = earn;
							earnMaxMap.put(revenueday.getFundsType(), earnMax);
						}
					}
					
					//折算最大值
					conMax = conMaxMap.get(revenueday.getFundsType());
					if (revenueday.getConverts() != null) {
						BigDecimal convert = revenueday.getConverts();
						if (conMax == null) {
							conMax = convert;
							conMaxMap.put(revenueday.getFundsType(), conMax);
						}else if(conMax.compareTo(convert)<0){
							conMax = convert;
							conMaxMap.put(revenueday.getFundsType(), conMax);
						}
					}
					
					/*if (revenueday.getBtcEarnings() != null) {
						BigDecimal btcCon = revenueday.getBtcEarnings();
						if (btcCon.compareTo(btcMax) > 0) {
							btcMax = btcCon;
						}
					}
					if (revenueday.getLtcEarnings() != null) {
						BigDecimal ltcCon = revenueday.getLtcEarnings();
						if (ltcCon.compareTo(ltcMax) > 0) {
							ltcMax = ltcCon;
						}
					}
					if (revenueday.getEthEarnings() != null) {
						BigDecimal ethCon = revenueday.getEthEarnings();
						if (ethCon.compareTo(ethMax) > 0) {
							ethMax = ethCon;
						}
					}
					if (revenueday.getEtcEarnings() != null) {
						BigDecimal etcCon = revenueday.getEtcEarnings();
						if (etcCon.compareTo(etcMax) > 0) {
							etcMax = etcCon;
						}
					}
					if (revenueday.getConvertRMB() != null) {
						BigDecimal conRmb = revenueday.getConvertRMB();
						if (conRmb.compareTo(conMax) > 0) {
							conMax = conRmb;
						}
					}*/
				}
				setAttr("coinMaps", DatabasesUtil.getCoinPropMaps());
				setAttr("earnMaxMap", earnMaxMap);
				setAttr("conMaxMap", conMaxMap);
				setAttr("listBean", listBean);
				setAttr("itemCount", pageCount);
			}
			setAttr("No", pageNo == 0 ? 1 : pageNo);
			setPaging((int) pageCount, pageNo, pageSize);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
}
