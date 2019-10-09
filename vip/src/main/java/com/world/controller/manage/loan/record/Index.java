package com.world.controller.manage.loan.record;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.SourceType;
import com.world.model.entity.coin.CoinProps;
import com.world.model.loan.dao.DeductCouponDao;
import com.world.model.loan.dao.DefaultLimitDao;
import com.world.model.loan.dao.LoanDao;
import com.world.model.loan.dao.LoanRecordDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.dao.UserDetectDao;
import com.world.model.loan.entity.DeductCoupon;
import com.world.model.loan.entity.DefaultLimitType;
import com.world.model.loan.entity.Loan;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.entity.RiskType;
import com.world.web.Page;
import com.world.web.action.LoanUserAction;
import com.world.web.response.DataResponse;
import com.world.web.sso.SessionUser;

public class Index extends LoanUserAction {
	
	P2pUserDao pUDao = new P2pUserDao();
	LoanRecordDao recordDao = new LoanRecordDao();
	DeductCouponDao dCouponDao=new DeductCouponDao();
	DefaultLimitDao defaDao=new DefaultLimitDao();
	
	@Page(Viewer = "/en/u/loan/out/index.jsp")
	public void index() {
		String userId = userIdStr();
		boolean isIn = booleanParam("isIn");
		int loanId = intParam("lid");
		String fundsType = param("fundsType");
		String riskManage = param("riskType");
		String withoutLx = param("noRate");
		String status = param("status");
		

		if (!userId.equals("0")) {
			log.info("userName:" + userName());
			initLoanUser();
		}
		// 获取参数
		int pageNo = intParam("page");
		setAttr("isIn", isIn);

		Query query = recordDao.getQuery();
		query.setSql("select * from loanrecord");
		query.setCls(LoanRecord.class);
		int pageSize = 10;
		
		if(!userId.equals("0")){
			P2pUser user = (P2pUser) pUDao.getById(userId);
			setAttr("curUser", user);
		}
		
		if(isIn){
			query.append("inUserId='" + userId+"'");
		}else{
			query.append("outUserId='" + userId + "'");
		}
		if(loanId > 0){
			query.append("loanId=" + loanId);
		}
		if (!isIn) {
			if (StringUtils.isNotBlank(fundsType)) {
				query.append("fundsType = " + fundsType);
			}
		}
		if (StringUtils.isNotBlank(riskManage)) {
			query.append("riskManage = " + riskManage);
		}
		if (StringUtils.isNotBlank(status)) {
			query.append("status = " + status);
		}
		if (StringUtils.isNotBlank(withoutLx)) {
			if(withoutLx.equals("1")){
				query.append("withoutLxAmount > 0 ");
			}else if (withoutLx.equals("0")) {
				query.append("withoutLxAmount = 0 ");
			}
		}
		
		query.append("ORDER BY (case when status=3 then 0 when status=1 then 1 else 2 end), createTime desc");

		// 将参数保存为attribute
		try {
			int total = recordDao.count();
			if (total > 0) {
				List<Bean> dataList = recordDao.findPage(pageNo, pageSize);
				
				//recordDao.setLoan(dataList);
				setAttr("lists", dataList);
				setAttr("itemCount", total);
			}
			setPaging(total, pageNo, pageSize);
			setAttr("pageNo", pageNo);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	/**
	 * 已成功的投资或借款
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer = "/cn/manage/loan/record/ajax.jsp")
	public void ajax() {
		try {
		String userId = userIdStr();
		boolean isIn = booleanParam("isIn");
		int fundsType = intParam("fundsType");
		
		if (!isIn) {
			SetViewerPath("/cn/manage/loan/record/out/ajax.jsp");
		}
		JSONObject jo = new P2pUserDao().getOutTimes(userIdStr());
		setAttr("outTimes", jo);
		index();//out/index.jsp
		CoinProps coint = DatabasesUtil.coinProps(fundsType);
//		 净资产,已申请,可申请
		if (!userId.equals("0")) {
			//P2pUser user = (P2pUser) pUDao.getById(userId);
			super.initLoanUser();
			setAttr("curUser", p2pUser);
			BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
			setAttr("minUnit", minUnit);
			//预计平仓价
			long begintime = System.currentTimeMillis();
			JSONObject calUnwindPirce = new UserDetectDao().calUnwindPirce(p2pUser, null);
			setAttr("calUnwindPirce", calUnwindPirce);
			log.info(" ==== 计算平仓价耗时: " + (System.currentTimeMillis()-begintime));
		}

		
		//折算先上价，显示在借款页面的选中抵扣券框中。 ****没有deductCoupon 表***先不实现，zhanglinbo
		//List<Bean> listCoupon = dCouponDao.getfindUserId(userId, 1);
		List<DeductCoupon> listDeDu=new ArrayList<DeductCoupon>();
		/*for (Bean bean : listCoupon) {
			DeductCoupon dc=(DeductCoupon) bean;
			dc.setConverAmou(coint.getStag());// 折换线上价格
			if (dc.getConverAmou().doubleValue() >= 0.0001) {
				listDeDu.add(dc);
			}
		}*/
		
		//根据状态类型查找
		setAttr("dataList", listDeDu);
		//币种集合
		setAttr("coinMaps",DatabasesUtil.getCoinPropMaps());
		
//		手续费率0.1
//		借贷费率,平台固定

		
		String p2pOutRate = defaDao.findOKT(coint.getStag(), "p2pOutRate").getValueName();
		
		if(StringUtils.isBlank(p2pOutRate)){
			p2pOutRate="0.1";
		}
		setAttr("p2pOutRate", p2pOutRate);
		
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	
	@Page(Viewer = "/cn/manage/loan/record/loan.jsp")
	public void loan(){
		int id = intParam(0);
		String userId = userIdStr();
		Loan curLoan = (Loan) new LoanDao().getById(Loan.class, id);
		
		setAttr("curLoan", curLoan);

		if(!userId.equals("0")){
			P2pUser user = pUDao.initLoanUser(userId);
			setAttr("curUser", user);
			BigDecimal minUnit = defaDao.getLimitBigDecimal(coint.getStag(), DefaultLimitType.p2pMinLoan.getValue());
			setAttr("minUnit", minUnit.compareTo(curLoan.getBalanceAmount()) > 0 ? curLoan.getBalanceAmount() : minUnit);
		}
	}
	
	////成交
	@Page(Viewer = JSON)
	public void doLoan() throws Exception {
		int loanId = intParam("id");
		BigDecimal amount = decimalParam("amount").setScale(2, BigDecimal.ROUND_DOWN);
		String userId = userId(true , true);
		String userName = userName();
		String pass = param("password");
		
		int freeCouponId = intParam("freecouponId");
		BigDecimal useAmount = decimalParam("useamount").setScale(2, BigDecimal.ROUND_DOWN);
		int riskManage = intParam("riskManage");
		if(adminId() == 0){
//			if(!safePwd(pass, userId)){
//				return;
//			}
		}
		//TODO:来源---web
		RiskType riskType = (RiskType) EnumUtils.getEnumByKey(riskManage, RiskType.class);
		DataResponse dr = recordDao.doLoan(this, loanId , amount , userId , userName , riskType, freeCouponId, useAmount, 0, SourceType.WEB.getKey());
		
		json(dr.getDes(), dr.isSuc(), dr.getDataStr());
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
}

