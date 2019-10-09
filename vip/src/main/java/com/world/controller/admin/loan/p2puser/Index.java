package com.world.controller.admin.loan.p2puser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.UpdateOperations;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.LoanLever;
import com.world.model.loan.dao.DefaultLimitDao;
import com.world.model.loan.dao.InvestorApplyDao;
import com.world.model.loan.dao.P2pUserDao;
import com.world.model.loan.entity.DefaultLimit;
import com.world.model.loan.entity.InterestRateForm;
import com.world.model.loan.entity.MyInvestorApply;
import com.world.model.loan.entity.P2pUser;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;



@FunctionAction(jspPath = "/admins/loan/p2puser/", des = "借贷用户管理")
public class Index extends AdminAction {
	P2pUserDao dao = new P2pUserDao();
	PayUserDao payUserDao = new PayUserDao();
	DailyRecordDao rDao = new DailyRecordDao();
	AdminUserDao adminUserDao = new AdminUserDao();
	DefaultLimitDao defaDao=new DefaultLimitDao();	//默认配置表

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		String tab = param("tab");
		int pageNo = intParam("page");
		String userId = param("userId");
		String userName = param("userName");
		String orderby = param("order");
		int level = intParam("level");
		String status = param("status");
		String loanOutStatus = param("loanOutStatus");
		String loanInStatus = param("loanInStatus");
		String sysForce = param("sysForce");
		String userLend= param("userLend");

		Query query = dao.getQuery();
		query.setSql("select * from p2puser");
		query.setCls(P2pUser.class);
		int pageSize = 20;

		if(StringUtils.isBlank(orderby)){
			orderby = "repayLevel";
		}
		
		boolean search = false;
		// 将参数保存为attribute
		try {
			if(tab.length() == 0)
				tab = "all";
			
			//query.append(" AND inSuccess>0 ");//有借款
			if("level100".equals(tab)){
				query.append(" AND repayLevel = 100");
			}else if("locked".equals(tab)){
				query.append(" AND repayLock = 1");
			}
			
			if (userId.length() > 0) {
				search = true;
				query.append(" AND userId = '" + userId+"'");
			}
			if (userName.length() > 0) {
				search = true;
				query.append(" AND userName like '%" + userName + "%'");
			}
			if (level > 0) {
				search = true;
				query.append(" AND level = " + level);
			}
			//暂无开放给他人放贷
			/*if (StringUtils.isNotBlank(status)) {
				query.append(" AND status = " + status);
			}*/
			if (StringUtils.isNotBlank(loanOutStatus)) {
				query.append(" AND loanOutStatus = " + loanOutStatus);
			}
			if (StringUtils.isNotBlank(loanInStatus)) {
				query.append(" AND loanInStatus = " + loanInStatus);
			}
			if (StringUtils.isNotBlank(sysForce)) {
				query.append(" AND sysForce = " + sysForce);
			}
			if(StringUtils.isNotBlank(userLend)) {
				query.append(" AND userLend = " + userLend);
			}
			

			
		
			/*
			String duokong = param("duokong");
			boolean isduokong = StringUtils.isNotBlank(duokong);
			String order = "repayLevel DESC";
			if(StringUtils.isNotBlank(orderby)){
				String opr = duokong.equals("1")? "<" : ">";
				if(orderby.equals("repayLevel")){
					order = "repayLevel DESC";
					if(isduokong){
						query.append(" and ((btcUnwindPrice>0 and btcUnwindPrice" + opr + prices[0] + ")  ");
						query.append(" or (ltcUnwindPrice>0 and ltcUnwindPrice" + opr + prices[1] + ")  ");
						query.append(" or (etcUnwindPrice>0 and etcUnwindPrice" + opr + prices[3] + ")  ");
						query.append(" or (ethUnwindPrice>0 and ethUnwindPrice" + opr + prices[2] + ")) ");
					}
				}else if(orderby.equals("btc")){
					if(isduokong){
						query.append(" and btcUnwindPrice>0 and btcUnwindPrice" + opr + prices[0]);
					}
					order = "(CASE WHEN btcUnwindPrice=0 then 0 else 1 END) DESC,abs(btcUnwindPrice-" + prices[0] + ") asc,repayLevel DESC";
				}else if(orderby.equals("ltc")){
					if(isduokong){
						query.append(" and ltcUnwindPrice>0 and ltcUnwindPrice" + opr + prices[1]);
					}
					order = "(CASE WHEN ltcUnwindPrice=0 then 0 else 1 END) DESC,abs(ltcUnwindPrice-" + prices[1] + ") asc, repayLevel DESC";
				}else if(orderby.equals("eth")){
					if(isduokong){
						query.append(" and ethUnwindPrice>0 and ethUnwindPrice" +  opr + prices[2]);
					}
					order = "(CASE WHEN ethUnwindPrice=0 then 0 else 1 END) DESC,abs(ethUnwindPrice-" + prices[2] + ") asc, repayLevel DESC";
				}else if(orderby.equals("etc")){
					if(isduokong){
						query.append(" and etcUnwindPrice>0 and etcUnwindPrice" +  opr + prices[3]);
					}
					order = "(CASE WHEN etcUnwindPrice=0 then 0 else 1 END) DESC,abs(etcUnwindPrice-" + prices[3] + ") asc, repayLevel DESC";
				}
			}*/


			setAttr("tab", tab);
			setAttr("page", pageNo);
		/*	
			if(search){
			}*/
			String order = "repayLevel DESC";
			if(orderby.equals("repayLevel")){
				order = "repayLevel DESC";
			}
			long total = query.count();
			if (total > 0) {
				query.append("ORDER BY "+order);
				List<P2pUser> dataList = dao.findPage(pageNo, pageSize);
				
				//加载备注
			/*	for (P2pUser bean : dataList) {
					 P2pUser user =  bean;
					List<DailyRecord> records = rDao.find(rDao.getQuery().filter("userId", user.getUserId()).order("-createTime").limit(1)).asList();
					if(records!=null && !records.isEmpty()){
						user.setLastRecord(records.get(0));
						user.getLastRecord().setaUser(adminUserDao.get(user.getLastRecord().getAdminId()));
					}
				}*/
				JSONObject prices = UserCache.getPrices();//获取所有盘口当前价格
				
				/*for (P2pUser bean : dataList) {
					CoinProps coin = DatabasesUtil.coinProps(bean.getFundsType());
					if(prices!=null){
						String pricekey = coin.getDatabaseKey(); 
						bean.setCurrPrice(prices.getBigDecimal(pricekey));
						bean.setCoint(coin);
					}
				}*/
				
				setAttr("within1day", TimeUtil.getTodayFirst(TimeUtil.getAfterDay(-1)));
				setAttr("within3day", TimeUtil.getTodayFirst(TimeUtil.getAfterDay(-3)));
				setAttr("within5day", TimeUtil.getTodayFirst(TimeUtil.getAfterDay(-5)));
				
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);

			EnumSet<LoanLever> levers = (EnumSet<LoanLever>)EnumUtils.getAll(LoanLever.class);
			setAttr("levers", levers);
			
			// 显示投资默认值数额过大，除以10000再显示，除后如：1W
			List<DefaultLimit> querylimit =  defaDao.findAll(0, "LimitKey", "", "");
			Map<String,BigDecimal> defaultLimitMap = new HashMap<String,BigDecimal>();
			BigDecimal muns = new BigDecimal("10000");
			for (DefaultLimit dt : querylimit) {
				
				BigDecimal valueName =StringUtils.isBlank(dt.getValueName())?BigDecimal.ZERO:new BigDecimal(dt.getValueName()).setScale(0);
				 
					if (valueName.compareTo(muns) >= 0) {
						valueName = valueName.divide(muns, 0, RoundingMode.DOWN);
						defaultLimitMap.put(dt.getKeyName(), valueName );
					} else {
						defaultLimitMap.put(dt.getKeyName(),  valueName);
					}
			}
			setAttr("defaultLimits", defaultLimitMap);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@SuppressWarnings("unchecked")
	@Page(Viewer = "/admins/loan/p2puser/level.jsp")
	public void level() {
		String userId = param("userId");
		P2pUser pUser = (P2pUser)dao.getById(userId);
		setAttr("user", pUser);
		
		EnumSet<LoanLever> levers = (EnumSet<LoanLever>)EnumUtils.getAll(LoanLever.class);
		setAttr("levers", levers);
	}
	
	/***
	 * @version 显示初始信息，添加了默认值
	 * @author chenruidong
	 */
	@Page(Viewer = "/admins/loan/p2puser/lend.jsp")
	public void lend() {
		try {
			String userId = param("userId");
			// 数据库为零时，给一个默认值，
			if (!userId.equals("0")) {
				
				P2pUser  user = dao.getById(userId);
				List<PayUserBean> payUserList = payUserDao.getFunds(userId);
				//user.init();// 初始化资产信息
				if(payUserList!=null && !payUserList.isEmpty()){
					for(PayUserBean payUser:payUserList){
						CoinProps  coin= getCoinByFundsType(payUser.getFundsType());
						payUser.setCoint(coin);
					}
				}
			
				setAttr("curUser", user);
				setAttr("payUserList", payUserList);
				setAttr("rateForms", EnumUtils.getAll(InterestRateForm.class));
			} 
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	/***
	 * @see 后台页面，一共加了六个字段
	 * @version 获取页面传过来的值，进行修改
	 * @author chenruidong
	 */
	@Page(Viewer = JSON)
	public void upadelend() {
		try {

			String userId = param("userId");
			int userLend = intParam("userLend");// 用户投资开关
			//BigDecimal loanLimit = decimalParam("loanLimit");// 用户RMB放贷范围
			
			List<PayUserBean> payUserList = payUserDao.getFunds(userId);
			//user.init();// 初始化资产信息
			
			List<OneSql> listOne = new ArrayList<OneSql>();
			
			if(payUserList!=null && !payUserList.isEmpty()){
				for(PayUserBean payUser:payUserList){
					CoinProps  coin= getCoinByFundsType(payUser.getFundsType());
					payUser.setCoint(coin);
					String coinType ="";
					if(payUser.getCoint()!=null){
						coinType = payUser.getCoint().getPropTag();
					}
					int fundsType=intParam(coinType+"FundsType");// fundsType
					BigDecimal loanLimit =decimalParam(coinType+"Limit");// Limit 
					listOne.add(new OneSql("update pay_user set loanLimit = ? where userId=? and fundsType=?", 1,new Object[]{loanLimit,userId,fundsType}));
				}
			}
			listOne.add(new OneSql("update p2puser set userLend=? WHERE userId=?",1,new Object[] { userLend, userId }));
			
			if (Data.doTrans(listOne) ) {
				json("修改成功", true, "");
			} else {
				json("修改失败", false, "");
			}
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	/**
	 * ajax的调用
	 * 更新用户资产杠杆级别
	 * @author zhanglinbo 20161230
	 */
	@Page(Viewer = JSON)
	public void saveLevel() {
		String userId = param("userId");
		int level = intParam("level");
		
		if(dao.update("UPDATE p2puser SET level = ? WHERE userId = ? ", new Object[]{level, userId}) > 0){
			json("修改成功", true, "");
		}else{
			json("修改失败", false, "");
		}
	}
	
	@Page(Viewer = XML)
	public void modifyStatus() {
		int userId = intParam("userId");
		int status = intParam("status");
		if(userId==0) {
			Write("用户编号或者状态不能为空",false,"{}");
			return;
		}
		
		if(dao.update("UPDATE p2pUser SET status = ? WHERE userId = ?", new Object[]{status, userId}) > 0){
			try {
				//UserManager.getInstance().resetUserFunds(new String[]{String.valueOf(userId)});
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			Write("修改成功", true, "");
		}else{
			Write("修改失败", false, "");
		}
	}
	
	@Page(Viewer = XML)
	public void modifyLoanOutStatus() {
		int userId = intParam("userId");
		int status = intParam("status");
		if(userId==0) {
			Write("用户编号或者状态不能为空",false,"{}");
			return;
		}
		
		if(dao.update("UPDATE p2puser SET loanOutStatus = ? WHERE userId = ? ", new Object[]{status, userId}) > 0){
			try {
				//UserManager.getInstance().resetUserFunds(new String[]{String.valueOf(userId)});
				
				InvestorApplyDao iaDao = new InvestorApplyDao();
				Datastore ds = iaDao.getDatastore();
				com.google.code.morphia.query.Query<MyInvestorApply> query = ds.find(MyInvestorApply.class, "userId", userId+"");
				UpdateOperations<MyInvestorApply> operate = ds.createUpdateOperations(MyInvestorApply.class).set("status", status==1?2:3);
				ds.update(query, operate);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			Write("修改成功", true, "");
		}else{
			Write("修改失败", false, "");
		}
	}
	
	@Page(Viewer = XML)
	public void modifyLoanInStatus() {
		int userId = intParam("userId");
		int status = intParam("status");
		if(userId==0) {
			Write("用户编号或者状态不能为空",false,"{}");
			return;
		}
		
		if(dao.update("UPDATE p2pUser SET loanInStatus = ? WHERE userId = ?", new Object[]{status, userId}) > 0){
			Write("修改成功", true, "");
		}else{
			Write("修改失败", false, "");
		}
	}
	
	@Page(Viewer = XML)
	public void unlockUser() {
		String userId = param("userId");
		String mCode = param("mCode");
		if(!codeCorrect(XML)){
			return;
		}
		if(userId == null || "".equals(userId)) {
			Write("用户编号或者状态不能为空",false,"{}");
			return;
		}
		
		
		if(dao.update("update p2pUser set repayLock=0 where userId=?", new Object[]{userId}) > 0){
			Write("修改成功", true, "");
		}else{
			Write("修改失败", false, "");
		}
	}
	
	@Page(Viewer = XML)
	public void modifyForce() {
		int userId = intParam("userId");
		int status = intParam("status");
		if(userId==0) {
			Write("用户编号或者状态不能为空",false,"{}");
			return;
		}
		
		if(dao.update("UPDATE p2pUser SET sysForce = ? WHERE userId = ? ", new Object[]{status, userId}) > 0){
			Write("修改成功", true, "");
		}else{
			Write("修改失败", false, "");
		}
	}
	
	@Page(Viewer = "/admins/loan/p2puser/modifyFees.jsp")
	public void modifyFees() {
		String userId = param("userId");
		P2pUser pUser = dao.getById(userId);
		setAttr("user", pUser);
		//DecimalFormat df = new  DecimalFormat("0.00####");
		setAttr("fees", pUser.getFees());
	}
	
	@Page(Viewer = XML)
	public void doModifyFees() {
		int userId = intParam("userId");
		int status = intParam("isSetFees");
		BigDecimal fees = decimalParam("fees");
		
		if(userId==0) {
			Write("用户编号或者状态不能为空",false,"{}");
			return;
		}
		
		if(dao.update("UPDATE p2puser SET isSetFees = ?, fees = ? WHERE userId = ? ", new Object[]{status, fees, userId}) > 0){
			Write("修改成功", true, "");
		}else{
			Write("修改失败", false, "");
		}
	}
	
	/***
	 * 默认值显示
	 * @author chenruidong
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Page(Viewer = "/admins/loan/p2puser/delimit.jsp")
	public void delimit() {
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT * from defaultlimit");
			query.setCls(DefaultLimit.class);
			List<DefaultLimit> defaultLimitList = defaDao.findAll(0, "limitKey", "", "");
//			List<DefaultLimit> defaultLimitList = query.getList();
			
			setAttr("typeName", "limitKey");
			setAttr("defaultLimitList", defaultLimitList);
			
			/*for (DefaultLimit dt : defaultLimitList) {
				
				if(dt.getKeyName().equals("rmb")){
					setAttr("rmbid", dt.getId());
					setAttr("rmb", dt.getKeyName());
					setAttr("rmbValue", new BigDecimal(dt.getValueName()));
				}
				if(dt.getKeyName().equals("btc")){
					setAttr("btcid", dt.getId());
//					setAttr("btcType", dt.getTypeName());
					setAttr("btc", dt.getKeyName());
//					setAttr("btcValue", dt.getValueName().toString());
					setAttr("btcValue", new BigDecimal(dt.getValueName()));
				}
				if(dt.getKeyName().equals("ltc")){
					setAttr("ltcid", dt.getId());
//					setAttr("ltcType", dt.getTypeName());
					setAttr("ltc", dt.getKeyName());
					setAttr("ltcValue", new BigDecimal(dt.getValueName()));
				}
				if(dt.getKeyName().equals("eth")){
					setAttr("ethid", dt.getId());
//					setAttr("ethType", dt.getTypeName());
					setAttr("eth", dt.getKeyName());
					setAttr("ethValue", new BigDecimal(dt.getValueName()));
				}
				if(dt.getKeyName().equals("etc")){
					setAttr("etcid", dt.getId());
//					setAttr("etcType", dt.getTypeName());
					setAttr("etc", dt.getKeyName());
					setAttr("etcValue", new BigDecimal(dt.getValueName()));
				}
			}*/
		} catch (Exception e) {
			log.error("手动设置显示有异常！---", e);
		}
	}

	/***
	 * 接收修改处理 系统默认值
	 * @author chenruidong
	 */
	@Page(Viewer = JSON)
	public void updateMoRen() {
		try {
			/*获取参数	Start*/
			String typeName=param("typeName");// typeName
			
			Query query = dao.getQuery();
			query.setSql("SELECT * from defaultlimit");
			query.setCls(DefaultLimit.class);
			List<DefaultLimit> defaultLimitList = defaDao.findAll(0, "limitKey", "", "");
			List<OneSql> listOne = new ArrayList<OneSql>();
			
			for(DefaultLimit defaultLimit:defaultLimitList){
				int id=intParam(defaultLimit.getKeyName()+"id");// id
				String key=param(defaultLimit.getKeyName()+"key");// keyName
				BigDecimal value =decimalParam(defaultLimit.getKeyName()+"Value");// valueName 
				
				/*不允许为负数	Start*/
				//-0 会存储为0
				if (value.compareTo(DigitalUtil.getBigDecimal("0")) < 0) {
					json("不允许负数!", false, "");
					return;
				}
				/*不允许为负数	End*/
				
				listOne.add(defaDao.getUpdateValue(value.toString(), id, typeName, key));
			}
			
			/*获取参数	End*/
			
			
			
			/*进行事物处理	Start*/
			
			if (Data.doTrans(listOne)) {
				json("默认值 更改成功。", true, "");
			} else {
				json("默认值更改失败！", false, "");
			}
			/*事物处理	End*/
			
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	/*
	 * 统计功能
	 */
	@Page(Viewer = JSON)
	public void statistic() {
		try {
			// 获取参数
			String currentTab = param("tab");
			int pageNo = intParam("page");
			String userId = param("userId");
			String userName = param("userName");
			String orderby = param("order");
			int level = intParam("level");
			String status = param("status");
			String loanOutStatus = param("loanOutStatus");
			String loanInStatus = param("loanInStatus");
			String sysForce = param("sysForce");
			String userLend= param("userLend");
			
			Query query = dao.getQuery();
			query.setSql("select * from p2puser");
			query.setCls(P2pUser.class);
			
			String ids = param("userIds");
			boolean isAll = booleanParam("isAll");
			
			if(isAll){
				if(currentTab.length() == 0)
					currentTab = "all";
				if("level100".equals(currentTab)){
					query.append(" AND repayLevel = 100");
				}else if("locked".equals(currentTab)){
					query.append(" AND repayLock = true");
				}
				
				if (userId.length() > 0) {
					query.append(" AND userId = '" + userId+"'");
				}
				if (userName.length() > 0) {
					query.append(" AND userName like '%" + userName + "%'");
				}
				if (level > 0) {
					query.append(" AND level = " + level);
				}
				if (StringUtils.isNotBlank(status)) {
					query.append(" AND status = " + status);
				}
				if (StringUtils.isNotBlank(loanOutStatus)) {
					query.append(" AND loanOutStatus = " + loanOutStatus);
				}
				if (StringUtils.isNotBlank(loanInStatus)) {
					query.append(" AND loanInStatus = " + loanInStatus);
				}
				if (StringUtils.isNotBlank(sysForce)) {
					query.append(" AND sysForce = " + sysForce);
				}
				if(StringUtils.isNotBlank(userLend)) {
					query.append(" AND userLend = " + userLend);
				}
			}else{
				if(ids.endsWith(",")){
					ids = ids.substring(0, ids.length()-1);
				}
				query.append(" AND userId IN ("+ids+")");
			}
			
			BigDecimal beOutRmb = BigDecimal.ZERO;//待借出RMB金额
			BigDecimal beOutBtc = BigDecimal.ZERO;//待借出BTC金额
			BigDecimal beOutLtc = BigDecimal.ZERO;//待借出LTC金额
			BigDecimal beOutEth = BigDecimal.ZERO;//待借出ETH金额
			BigDecimal beOutEtc = BigDecimal.ZERO;//待借出ETC金额
			
			BigDecimal beInRmb = BigDecimal.ZERO;//待借入RMB金额
			BigDecimal beInBtc = BigDecimal.ZERO;//待借入BTC金额
			BigDecimal beInLtc = BigDecimal.ZERO;//待借入LTC金额
			BigDecimal beInEth = BigDecimal.ZERO;//待借入ETH金额
			BigDecimal beInEtc = BigDecimal.ZERO;//待借入ETC金额
			
			BigDecimal outingRmb = BigDecimal.ZERO;//借出中的RMB金额
			BigDecimal outingBtc = BigDecimal.ZERO;//借出中的BTC金额
			BigDecimal outingLtc = BigDecimal.ZERO;//借出中的LTC金额
			BigDecimal outingEth = BigDecimal.ZERO;//借出中的ETH金额
			BigDecimal outingEtc = BigDecimal.ZERO;//借出中的ETC金额
			
			BigDecimal iningRmb = BigDecimal.ZERO;//借入中的RMB金额
			BigDecimal iningBtc = BigDecimal.ZERO;//借入中的BTC金额
			BigDecimal iningLtc = BigDecimal.ZERO;//借入中的LTC金额
			BigDecimal iningEth = BigDecimal.ZERO;//借入中的ETH金额
			BigDecimal iningEtc = BigDecimal.ZERO;//借入中的ETC金额
			
			BigDecimal overdraftRmb = BigDecimal.ZERO;//拖欠RMB金额
			BigDecimal overdraftBtc = BigDecimal.ZERO;//拖欠BTC金额
			BigDecimal overdraftLtc = BigDecimal.ZERO;//拖欠LTC金额
			BigDecimal overdraftEth = BigDecimal.ZERO;//拖欠Eth金额
			BigDecimal overdraftEtc = BigDecimal.ZERO;//拖欠ETC金额
			
			/*List<P2pUser> list = dao.find();
			for (P2pUser p2pUser : list) {
				 beOutRmb = beOutRmb.add(p2pUser.getBeOutRmb());//待借出RMB金额
				 beOutBtc = beOutBtc.add(p2pUser.getBeOutBtc());//待借出BTC金额
				 beOutLtc = beOutLtc.add(p2pUser.getBeOutLtc());//待借出LTC金额
				 beOutEth = beOutEth.add(p2pUser.getBeOutEth());//待借出ETH金额
				 beOutEtc = beOutEtc.add(p2pUser.getBeOutEtc());//待借出ETC金额
				
				 beInRmb = beInRmb.add(p2pUser.getBeInRmb());//待借入RMB金额
				 beInBtc = beInBtc.add(p2pUser.getBeInBtc());//待借入BTC金额
				 beInLtc = beInLtc.add(p2pUser.getBeInLtc());//待借入LTC金额
				 beInEth = beInEth.add(p2pUser.getBeInEtc());//待借入ETH金额
				 beInEtc = beInEtc.add(p2pUser.getBeInEth());//待借入ETC金额
				
				 outingRmb = outingRmb.add(p2pUser.getOutingRmb());//借出中的RMB金额
				 outingBtc = outingBtc.add(p2pUser.getOutingBtc());//借出中的BTC金额
				 outingLtc = outingLtc.add(p2pUser.getOutingLtc());//借出中的LTC金额
				 outingEth = outingEth.add(p2pUser.getOutingEth());//借出中的ETH金额
				 outingEtc = outingEtc.add(p2pUser.getOutingEtc());//借出中的ETC金额
				
				 iningRmb = iningRmb.add(p2pUser.getIningRmb());//借入中的RMB金额
				 iningBtc = iningBtc.add(p2pUser.getIningBtc());//借入中的BTC金额
				 iningLtc = iningLtc.add(p2pUser.getIningLtc());//借入中的LTC金额
				 iningEth = iningEth.add(p2pUser.getIningEth());//借入中的ETH金额
				 iningEtc = iningEtc.add(p2pUser.getIningEtc());//借入中的ETC金额
				
				 overdraftRmb = overdraftRmb.add(p2pUser.getOverdraftRmb());//拖欠RMB金额
				 overdraftBtc = overdraftBtc.add(p2pUser.getOverdraftBtc());//拖欠BTC金额
				 overdraftLtc = overdraftLtc.add(p2pUser.getOverdraftLtc());//拖欠LTC金额
				 overdraftEth = overdraftEth.add(p2pUser.getOverdraftEth());//拖欠Eth金额
				 overdraftEtc = overdraftEtc.add(p2pUser.getOverdraftEtc());//拖欠ETC金额
			}*/
			
			JSONObject obj = new JSONObject();
			obj.put("beOutRmb", beOutRmb);
			obj.put("beOutBtc", beOutBtc);
			obj.put("beOutLtc", beOutLtc);
			obj.put("beOutEth", beOutEth);
			obj.put("beOutEtc", beOutEtc);
			
			obj.put("beInRmb", beInRmb);
			obj.put("beInBtc", beInBtc);
			obj.put("beInLtc", beInLtc);
			obj.put("beInEth", beInEth);
			obj.put("beInEtc", beInEtc);
			
			obj.put("outingRmb", outingRmb);
			obj.put("outingBtc", outingBtc);
			obj.put("outingLtc", outingLtc);
			obj.put("outingEth", outingEth);
			obj.put("outingEtc", outingEtc);
			
			obj.put("iningRmb", iningRmb);
			obj.put("iningBtc", iningBtc);
			obj.put("iningLtc", iningLtc);
			obj.put("iningEth", iningEth);
			obj.put("iningEtc", iningEtc);
			
			obj.put("overdraftRmb", overdraftRmb);
			obj.put("overdraftBtc", overdraftBtc);
			obj.put("overdraftLtc", overdraftLtc);
			obj.put("overdraftEth", overdraftEth);
			obj.put("overdraftEtc", overdraftEtc);
			
			json("", true, obj.toString());
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	
	@Page(Viewer = "/admins/loan/p2puser/viewLoanDetail.jsp")
	public void viewLoanDetail() {
		String userId = param("userId");
	
		List<PayUserBean> payUserList =  payUserDao.getFunds(userId);
		if(payUserList!=null && !payUserList.isEmpty()){
			for(PayUserBean payUser:payUserList){
				CoinProps  coin= getCoinByFundsType(payUser.getFundsType());
				payUser.setCoint(coin);
			}
		}
		super.setAttr("payUserList", payUserList);
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
}
