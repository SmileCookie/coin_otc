package com.world.controller.admin.financial.balance;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.user.User;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.dao.FinanBalanceDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanBalance;
import com.world.model.financial.entity.FinanEntry;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/balance/", des = "每日结算")
public class Index extends AdminAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FinanBalanceDao dao = new FinanBalanceDao();
	private FinanAccountDao aDao = new FinanAccountDao();
	
	/*start by flym 20170606 查询sql*/
	private String sql = "";
	/*切换币种充值*/
	private DetailsDao detailDao = new DetailsDao();
	/*操作员信息*/
	private UserDao uDao = new UserDao();
	/*提现信息*/
	private DownloadDao downloadDao = new DownloadDao();
	/*end*/
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		int accountId = intParam("accountId");
		String group = param("group");
		Timestamp date = dateParam("startDate");

		Query query = dao.getQuery();
		query.setSql("select * from finanbalance");
		query.setCls(FinanBalance.class);
		int pageSize = 20;
		
		// 将参数保存为attribute
		try {
			if(accountId > 0){
				query.append(" AND accountId = "+accountId);
			}
			if(group.length() > 0){
				Timestamp now = now();
				if(group.equals("00")){
					query.append(" AND groupTime ="+ FinanBalance.sdf0.format(now));
				}else if(group.equals("24")){
					query.append(" AND groupTime ="+ FinanBalance.sdf24.format(now));
				}else if(group.equals("day")&&date!=null){
					query.append(" AND (groupTime ="+FinanBalance.sdf0.format(date)+" or groupTime ="+FinanBalance.sdf24.format(date)+")");
				}
			}
			//xzhang 20170822 当前菜单不过滤创建人，拥有该权限的用户都可以查看  JYPT-1197
//			int roleId = roleId();
//			if(roleId != 1 && roleId != 6){
//				query.append(" AND createId = "+adminId());
//			}
			query.append(" AND isDel = 0");
			query.append(" ORDER BY createTime DESC");

			long total = query.count();
			if (total > 0) {
				List<Bean> dataList = dao.findPage(pageNo, pageSize);
				dao.setProperties(dataList);
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
			//xzhang 20170822 当前菜单不过滤创建人，拥有该权限的用户都可以查看  JYPT-1197
//			setAttr("accounts", aDao.findList(roleId()==1||roleId()==6?0:adminId()));
			setAttr("accounts", aDao.findList(0));
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		log.info("balance...aoru...");
		/*点击上班处理后的页面*/
		try {
			int accountId = intParam("accountId");
			log.info("accountId = " + accountId);
			long dayTag = longParam("dayTag");
			if (accountId > 0){
				FinanAccount account = aDao.get(accountId);
				log.info("getCurTotalAmount = " + account.getCurTotalAmount());
				setAttr("account", account);
				
//				FinanEntry entry = (FinanEntry)Data.GetOne("SELECT * FROM finanentry WHERE accountId = ? order by createTime DESC", new Object[]{accountId}, FinanEntry.class);
//				setAttr("entry", entry);
				/*start by flym 20170531 修改对账界面*/
				/*新取页面参数,账户类型1充值,2储备,3提现,4网络*/
				int accType = intParam("accType");
				log.info("accType = " + accType);
				/*用于查询不同币种的details表*/
				String fundTypeName = "btc";
//				log.info("coint = " + coint.getDatabaseKey() + "bdDao.getTableName() = " + bdDao.getTableName());
				/*查询finanbalance添加上次结算金额,从表finanbalance查找对应资金类型的上次结算记录，查找上次结算金额对应字段amount*/
				/*资金类型*/
				int fundType = 2;
				fundType = account.getFundType();
				log.info("fundType = " + fundType);
				/*货币属性类*/
				CoinProps coinProps = DatabasesUtil.coinProps(fundType);
				fundTypeName = coinProps.getDatabaseKey();
				log.info("fundTypeName = " + fundTypeName);
				sql = "select amount, perAmount, perTotalAmount from finanbalance where id = (select max(id) from finanbalance "
					+ "where fundType = " + fundType + " and accountId = " + accountId + ")";
				log.info("sql = " + sql);
				FinanBalance finanBalance = (FinanBalance) Data.GetOne(sql, null, FinanBalance.class);
				if (null == finanBalance) {
					/*第一次核算时使用*/
					finanBalance = new FinanBalance();
					finanBalance.setAmount(BigDecimal.ZERO);
					finanBalance.setPerTotalAmount(BigDecimal.ZERO);
				}
				
				/*从表XXXdetails或XXXdownload获取截止到本次核算之前该区间内的充值金额
				 * accType:1充值,3提现
				 * */
				if (accType == 1) {
					/*充值*/
					sql = "select max(detailsId) detailsId, sum(amount) amount from " + fundTypeName + "details where status = 2 and isFinaAccount <> 2";
				} else if(accType == 3) {
					/*提现*/
					sql = "select max(id) detailsId, sum(amount) amount, sum(amount - fees) fees from " + fundTypeName + "download where status = 2 and isFinaAccount <> 2";
				}
				
				log.info("sql = " + sql);
				DetailsBean detailsBean = (DetailsBean) Data.GetOne(sql, null, DetailsBean.class);
				if (null == detailsBean) {
					/*第一次核算时使用*/
					detailsBean = new DetailsBean();
					detailsBean.setAmount(BigDecimal.ZERO);
				}
				if (null != detailsBean && null == detailsBean.getAmount()){
					/*即时数据库查询出来没有数据，此处detailsBean接收到的也不是null,因此特作此处理*/
					detailsBean.setAmount(BigDecimal.ZERO);
				}
				if (null != detailsBean && null == detailsBean.getFees()) {
					detailsBean.setFees(BigDecimal.ZERO);
				}
				log.info("本次结算到充值编号【" + detailsBean.getDetailsId() + "】");
				
				/*结算提醒*/
				if (null != account.getAmount() && account.getAmount().compareTo(BigDecimal.ZERO) == 0) {
					account.setAmount(BigDecimal.ZERO);
				}
				if (null != account.getCurTotalAmount() && account.getCurTotalAmount().compareTo(BigDecimal.ZERO) == 0) {
					account.setCurTotalAmount(BigDecimal.ZERO);
				}
				int balanceFlag = 0;
				if (accType == 1) {
					/*充值
					 * 期初余额（上次金额）+ 发生额（充值金额）= 期末余额（当前余额）
					 * finanBalance.amount + detailsBean.amount = account.funds 
					 */
					if(finanBalance.getAmount().add(detailsBean.getAmount()).compareTo(account.getAmount()) == 0){
						balanceFlag = 1;
					}
				} else if (accType == 3) {
					/*提现
					 * 期初余额（上次累积金额）+ 发生额（提现成功金额）= 期末余额（当前累积金额）
					 * finanBalance.perTotalAmount + detailsBean.amount = account.curTotalAmount 
					 */
					if(finanBalance.getPerTotalAmount().add(detailsBean.getAmount()).compareTo(account.getCurTotalAmount()) == 0){
						balanceFlag = 1;
					}
				}
				
				log.info("finanBalance.amount = " + finanBalance.getAmount());
				log.info("detailsBean.amount = " + detailsBean.getAmount());
				log.info("account.amount = " + account.getAmount());
				log.info("detailsBean.fees = " + detailsBean.getFees());
				log.info("account.curTotalAmount = " + account.getCurTotalAmount());
				log.info("finanBalance.perTotalAmount = " + finanBalance.getPerTotalAmount());
				/*传入页面*/
				setAttr("balanceFlag", balanceFlag);
				setAttr("detailsBean", detailsBean);
				setAttr("finanBalance", finanBalance);
				setAttr("fundType", fundType);
				setAttr("accType", accType);
			}
			/*end*/
			setAttr("dayTag", dayTag);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doAoru() {
		log.info("doAoru...");
		/*结算确认*/
		try {
			int accountId = intParam("accountId");
			String memo = param("memo");
			long dayTag = longParam("dayTag");
			log.info("dayTag = " + dayTag);
			FinanAccount account = aDao.get(accountId);
			if(account == null){
				WriteError("账户不存在。");
				return;
			}
			
			Timestamp now = now();
			/*保存到每日结算表finanbalance*/
			FinanBalance balance = new FinanBalance(account, dayTag, memo);
			/*start by flym 20170606 提前获取资金类型：2 BTC*/
			int fundType = intParam("fundType");
			/*accType:1充值,3提现*/
			int accType = intParam("accType");
			/**/
			int balanceFlag = intParam("balanceFlag");
			/*上次结算金额*/
			BigDecimal perTotalAmount = decimalParam("perTotalAmount");
			/*上次累积提现金额*/
			BigDecimal perAmount = decimalParam("perAmount");
			log.info("fundType = " + fundType + ", accType = " + accType + ", balanceFlag = " + balanceFlag);
			log.info("perTotalAmount = " + perTotalAmount + ", perAmount = " + perAmount);
			/*用于查询不同币种的details表*/
			String fundTypeName = "btc";
			/*货币属性类*/
			CoinProps coinProps = DatabasesUtil.coinProps(fundType);
			fundTypeName = coinProps.getDatabaseKey();
			String finId = fundTypeName + System.currentTimeMillis();
			log.info("finId = " + finId);
			/*保存结算编号*/
			balance.setFinId(finId);
			/*保存上次累积实际提现金额*/
			balance.setPerTotalAmount(perTotalAmount);
			/*end*/
			List<OneSql> paySqls = new ArrayList<OneSql>();
			/**/
			/*注释掉原实现*/
//			paySqls.add(dao.saveSql(balance));
			sql = "insert into finanbalance (accountId, memo, fundType, amount, createId, createTime, groupTime, "
				+ "finId, perAmount, perTotalAmount, finResult) "
				+ "values (" + accountId + ", '" + memo + "', " + fundType + ", " + balance.getAmount() +", " + adminId() + ", "
				+ "'" + TimeUtil.getNow() + "', " + balance.getGroupTime() + ", '" + finId + "', " + perAmount + ", "
				+ "" + perTotalAmount + ", " + balanceFlag + ")";
			log.info("sql = " + sql);
			paySqls.add(new OneSql(sql, 1, null));
			paySqls.add(aDao.updateDayTag(accountId, dayTag));
			
			/*start by flym 20170606 更新相关数据库记录标识及备份*/
			/*获取页面传值本次结算到的最大充值编号*/
			long maxDetailsId = longParam("maxDetailsId");
			log.info("fundTypeName = " + fundTypeName);
			log.info("perTotalAmount = " + perTotalAmount + ", fundType = " + fundType);
			log.info("本次结算到充值编号【" + maxDetailsId + "】");
			
			/*备份数据到表finBalanceDetailsBack
			 * insert into finBalanceDetailsBack select NULL, 2, a.* from btcdetails a;
			 * */
			if(maxDetailsId > 0) {
				if(accType == 1) {
					/*操作充值账户对应充值信息*/
					/*存入每日结算充值备份记录表finBalanceDetailsBack*/
					sql = "insert into finBalanceDetailsBack "
						+ "select NULL, " + fundType + ", detailsId, type, status, fromAddr, toAddr, addHash, amount, "
						+ "sendTime, configTime, remark, userId, userName, banlance, entrustId, price, fees, confirmTimes, adminId, succonfirm, "
						+ "walletId, chargeId, wallet, merchantsSyncId, opUnique, isDelete, uuid, txIdN, blockHeight, isFinaAccount, '" + finId + "' "
						+ "from " + fundTypeName + "details a where status = 2 and isFinaAccount <> 2";
					log.info("sql = " + sql);
					paySqls.add(new OneSql(sql, -1, null));
					
					/*更新充值表XXXdetails中的isFinaAccount字段*/
					sql = "update " + fundTypeName + "details set isFinaAccount = 2 where status = 2 and detailsId <= " + maxDetailsId + "";
					log.info("sql = " + sql);
					paySqls.add(new OneSql(sql, -1, null));
				} else if (accType == 3) {
					/*操作提现账户对应提现信息*/
					/*存入每日结算提现备份记录表finBalanceDownloadBack*/
					sql = "insert into finBalanceDownloadBack "
						+ "select NULL, " + fundType + ", id, userId, userName, amount, submitTime, status, managerId, manageName, manageTime, "
						+ "fromAddress, toAddress, remark, freezeId, isDel, commandId, addHash, isMerchant, fees, realFee, confirm, payFee, "
						+ "hasFail, admins, opUnique, merchantOrderNo, uuid, txId, txIdN, blockHeight, isFinaAccount, '" + finId + "', configTime "
						+ "from " + fundTypeName + "download a where status = 2 and isFinaAccount <> 2";
					log.info("sql = " + sql);
					paySqls.add(new OneSql(sql, -1, null));
					
					/*更新充值表XXXdetails中的isFinaAccount字段*/
					sql = "update " + fundTypeName + "download set isFinaAccount = 2 where status = 2 and id <= " + maxDetailsId + "";
					log.info("sql = " + sql);
					paySqls.add(new OneSql(sql, -1, null));
				}
				
			}
			/*end*/

			if (Data.doTrans(paySqls)) {
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = "/admins/financial/balance/rechargeDetails.jsp")
	public void giveRechargeDetails() {
		log.info("giveRechargeDetails...");
		//查询条件
		int currentPage = intParam("page");
		String currentTab = param("tab");
		/*获取页面传值本次结算到的最大充值编号，资金编号*/
		long maxDetailsId = longParam("maxDetailsId");
		int fundType = intParam("fundType");
		log.info("maxDetailsId = " + maxDetailsId + ", fundType = " + fundType);
		/*用于查询不同币种的details表*/
		String fundTypeName = "btc";
		/*货币属性类*/
		CoinProps coinProps = DatabasesUtil.coinProps(fundType);
		fundTypeName = coinProps.getDatabaseKey();
		log.info("fundTypeName = " + fundTypeName);
//		int status = intParam("status");
		/*查询SQL*/
		sql = "select * from " + fundTypeName + "details where status = 2 and isFinaAccount <> 2 and detailsId <= " + maxDetailsId + "";
		log.info("sql = " + sql);
		Query<DetailsBean> query = detailDao.getQuery();
		query.setSql(sql);
		query.setCls(DetailsBean.class);
		
		if(currentTab.length()==0)
			currentTab = "charge";
				
		request.setAttribute("currentTab", currentTab);
		
		int total = query.count();
		if(total > 0){
			query.append("order by detailsId");
			//分页查询
			List<DetailsBean> btcDetails = detailDao.findPage(currentPage, PAGE_SIZE);
			
			List<String> userIds = new ArrayList<String>();
			
			List<String> adminIds = new ArrayList<String>();
			for(DetailsBean bdb : btcDetails){
			
				userIds.add(bdb.getUserId()+"");
				
				if(bdb.getAdminId() > 0){
					adminIds.add(bdb.getAdminId()+"");
				}
			}
			if(userIds.size()>0){
				Map<String, User> userMaps = uDao.getUserMapByIds(userIds);
				for(DetailsBean bdb : btcDetails){
					bdb.setUser(userMaps.get(bdb.getUserId()+""));
				}
			}
			
			if(adminIds.size()>0){
				Map<String, AdminUser> userMaps = new AdminUserDao().getUserMapByIds(adminIds);
				for(DetailsBean bdb : btcDetails){
					if(bdb.getAdminId() > 0){
						bdb.setaUser(userMaps.get(bdb.getAdminId()+""));
					}
				}
			}
			
			request.setAttribute("dataList", btcDetails);
			request.setAttribute("maxDetailsId", maxDetailsId);
		}
		/*设置币种*/
		setAttr("fundType", fundType);
		//页面顶部币种切换
        super.setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
		setPaging(total, currentPage);
	}
	
	@Page(Viewer = "/admins/financial/balance/download.jsp")
	public void giveDownload() {
		log.info("giveDownload...");
		//查询条件
        int currentPage = intParam("page");
        /*获取页面传值本次结算到的最大充值编号，资金编号*/
		long maxDetailsId = longParam("maxDetailsId");
		int fundType = intParam("fundType");
		log.info("maxDetailsId = " + maxDetailsId + ", fundType = " + fundType);
		/*用于查询不同币种的details表*/
		String fundTypeName = "btc";
		/*货币属性类*/
		CoinProps coinProps = DatabasesUtil.coinProps(fundType);
		fundTypeName = coinProps.getDatabaseKey();
		log.info("fundTypeName = " + fundTypeName);

        Query<DownloadBean> query = downloadDao.getQuery();
        sql = "select * from " + fundTypeName + "download where status = 2 and isFinaAccount <> 2 and id <= " + maxDetailsId + "";
        log.info("sql = " + sql);
        query.setSql(sql);
        query.setCls(DownloadBean.class);
        
        int total = query.count();
        if (total > 0) {
            query.append(" ORDER BY id");
            //分页查询
            List<DownloadBean> btcDownloads = downloadDao.findPage(currentPage, PAGE_SIZE);
            List<String> userIds = new ArrayList<String>();
            for (DownloadBean bdb : btcDownloads) {
                userIds.add(bdb.getUserId() + "");
            }
            
            if (userIds.size() > 0) {
                Map<String, User> userMaps = new UserDao().getUserMapByIds(userIds);
                for (DownloadBean bdb : btcDownloads) {
                	bdb.setUser(userMaps.get(bdb.getUserId() + ""));
                }
            }
            request.setAttribute("dataList", btcDownloads);
            request.setAttribute("maxDetailsId", maxDetailsId);
        }
        /*设置币种*/
		setAttr("fundType", fundType);
        //页面顶部币种切换
        super.setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
        setPaging(total, currentPage);
	}
	
	@Page(Viewer = JSON)
	public void tongjiDetail() {
		try {
			 /*获取页面传值本次结算到的最大充值编号，资金编号*/
			long maxDetailsId = longParam("maxDetailsId");
			int fundType = intParam("fundType");
			log.info("maxDetailsId = " + maxDetailsId + ", fundType = " + fundType);
			/*用于查询不同币种的details表*/
			String fundTypeName = "btc";
			/*货币属性类*/
			CoinProps coinProps = DatabasesUtil.coinProps(fundType);
			fundTypeName = coinProps.getDatabaseKey();
			log.info("fundTypeName = " + fundTypeName);
			Query<DetailsBean> query = detailDao.getQuery();
			/*查询SQL*/
			sql = "select * from " + fundTypeName + "details where status = 2 and isFinaAccount <> 2 and detailsId <= " + maxDetailsId + "";
			log.info("sql = " + sql);
			query.setSql(sql);
			query.setCls(DetailsBean.class);
			
			String ids = param("eIds");
			boolean isAll = booleanParam("isAll");
			
			if(!isAll){
				if(ids.endsWith(",")){
					ids = ids.substring(0, ids.length()-1);
				}
				query.append(" AND detailsId IN ("+ids+")");
			}
			
			List<DetailsBean> list = detailDao.find();
			
			BigDecimal totalMoney = BigDecimal.ZERO;
			for(DetailsBean bdb : list){
				totalMoney = totalMoney.add(bdb.getAmount());
			}
			JSONArray array = new JSONArray();
			array.add(totalMoney);
			
			json("", true, array.toString());
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = JSON)
    public void tongjiDownload() {
    	log.info("download...tongji...");
        try {
            /*获取页面传值本次结算到的最大充值编号，资金编号*/
			long maxDetailsId = longParam("maxDetailsId");
			int fundType = intParam("fundType");
			log.info("maxDetailsId = " + maxDetailsId + ", fundType = " + fundType);
			/*用于查询不同币种的details表*/
			String fundTypeName = "btc";
			/*货币属性类*/
			CoinProps coinProps = DatabasesUtil.coinProps(fundType);
			fundTypeName = coinProps.getDatabaseKey();
			log.info("fundTypeName = " + fundTypeName);
            Query<DownloadBean> query = downloadDao.getQuery();
            /*查询SQL*/
            sql = "select * from " + fundTypeName + "download where status = 2 and isFinaAccount <> 2 and id <= " + maxDetailsId + "";
			log.info("sql = " + sql);
            query.setSql(sql);
            query.setCls(DownloadBean.class);

            String ids = param("eIds");
            log.info("ids = " + ids);
            boolean isAll = booleanParam("isAll");
            
            if (!isAll) {
            	if (ids.endsWith(",")) {
                    ids = ids.substring(0, ids.length() - 1);
                    query.append(" AND id IN (" + ids + ")");
                }
            }
            List<DownloadBean> list = downloadDao.find();

            String pattern = "0.000000##";//格式代码
            DecimalFormat df = new DecimalFormat();
            df.applyPattern(pattern);

            BigDecimal totalMoney = BigDecimal.ZERO;
            BigDecimal totalMoney2 = BigDecimal.ZERO;
            for (DownloadBean bdb : list) {
                totalMoney = totalMoney.add(bdb.getAmount());
                totalMoney2 = totalMoney2.add(bdb.getAfterAmount());
            }
            JSONArray array = new JSONArray();
            array.add(df.format(totalMoney));
            array.add(totalMoney2);

            json("", true, array.toString());

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }
	
	@Page(Viewer = "/admins/financial/balance/memo.jsp")
	public void memo() {
		try {
				int id = intParam("id");

			if(id > 0){
				FinanBalance balance = dao.get(id);
				setAttr("balance", balance);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	@Page(Viewer = ".xml")
	public void doMemo() {
		try {
			int id = intParam("id");
			String memo = param("memo");
			
			int count =	dao.update("UPDATE finanbalance SET memo = ? WHERE id = ?", new Object[]{memo, id});

			if (count > 0) {
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = XML)
	public void doDel() {
		int id = intParam("id");
		if (id > 0) {
			dao.delById(id);
			
			Write("删除成功。", true, "");
			return;
		}
		Write("未知错误导致删除失败。", false, "");
	}
	
}