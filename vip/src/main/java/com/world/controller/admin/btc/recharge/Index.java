package com.world.controller.admin.btc.recharge;

import com.alibaba.fastjson.JSONArray;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.constant.Const;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.daily.MainDailyRecordDao;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.dao.wallet.WalletDao;
import com.world.model.dao.wallet.WalletDetailsDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.admin.logs.DailyType;
import com.world.model.entity.bill.BillType;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.KeyBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.pay.WalletBean;
import com.world.model.entity.user.User;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.AccountType;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanUseType;
import com.world.util.DigitalUtil;
import com.world.util.MerchantsUtil;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/btc/recharge/" , des = "充值记录")
public class Index extends FinanAction{
	
	private static final long serialVersionUID = 1L;
	
	private UserDao uDao = new UserDao();
	private DetailsDao bdDao = new DetailsDao();
	private FundsDao fundsDao = new FundsDao();
	WalletDetailsDao walletDetailsDao = new WalletDetailsDao();
	WalletDao walletDao = new WalletDao();
	KeyDao keyDao = new KeyDao();
	private PayUserDao payDao = new PayUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		//查询条件
		int currentPage = intParam("page");
		String currentTab = param("tab");
		Timestamp startTime = dateParam("startTime");
		Timestamp endTime = dateParam("endTime");
//		int isIn=intParam("isIn");
		int status=intParam("status");
		String btcFrom = param("btcFrom");
		String userName = param("userName");
		String userId = param("userId");
		
		bdDao.setCoint(coint);
		Query<DetailsBean> query = bdDao.getQuery();
		query.setSql("select * from "+bdDao.getTableName());
		query.setCls(DetailsBean.class);
		
		if(currentTab.length()==0)
			currentTab = "charge";
				
		request.setAttribute("currentTab", currentTab);
		
		if(startTime != null){
			query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
		}
		if(endTime != null){
			query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
		}
		if(status>0){
			if(status == 10){
				status = 0;
				query.append(" and Status="+status);
			}else if(status == 11){
				query.append(" and Status=2 and type = 1 AND sucConfirm=0");
			}
		}
		
		if(btcFrom.length()>0){
			query.append(" and fromAddr="+btcFrom);
	    }
		
		if(userName.length() > 0){
			query.append(" and userName like '%"+ userName +"%'");
		}
		if(userId.length() > 0){
			query.append(" and userId = '"+ userId +"'");
		}
		
		int total = query.count();
		if(total > 0){
			query.append("order by sendTime desc");
			//分页查询
			List<DetailsBean> btcDetails = bdDao.findPage(currentPage, PAGE_SIZE);
			
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
		}
		//页面顶部币种切换
        super.setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
		setPaging(total, currentPage);
		
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index(); 
	}	
	
	@Page(Viewer = "/admins/btc/recharge/entry.jsp")
	public void aoru() {
		try {
			long connId = longParam("connId");
			setAttr("connId", connId);
			
			bdDao.setCoint(coint);
			DetailsBean bdb = (DetailsBean)Data.GetOne("SELECT * FROM "+bdDao.getTableName()+" WHERE detailsId = ? AND status = 2", new Object[]{connId}, DetailsBean.class);
			
//			walletDao.setCoint(coint);
//			WalletBean bwb = (WalletBean) Data.GetOne("select * from "+walletDao.getTableName()+" where name=?", new Object[]{bdb.getWallet()}, WalletBean.class);
			
//			if(bwb != null){
				List<Bean> accounts = (List<Bean>)Data.Query("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ? AND isDefault = true", new Object[]{coint.getFundsType(), AccountType.charge.getKey()}, FinanAccount.class);
				setAttr("accounts", accounts);
//				setAttr("walletId", bwb.getWalletId());
//			}
			setAttr("btcTo", bdb.getToAddr());
			setAttr("fundType", coint.getFundsType());
			
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
			
			FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
			FinanUseType usetype = useTypeDao.getByType(1);
			setAttr("useTypeId", usetype.getId());
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer=".xml")
	public void doAoru(){
		if(!codeCorrect(XML)){
			return;
		}
		
		long connId = longParam("connId");
		
		List<OneSql> sqls = new ArrayList<OneSql>();

		bdDao.setCoint(coint);
		DetailsBean bdb = (DetailsBean)Data.GetOne("SELECT * FROM "+bdDao.getTableName()+" WHERE detailsId = ? AND status = 2", new Object[]{connId}, DetailsBean.class);
		
//		walletDao.setCoint(coint);
//		WalletBean btcWallet = (WalletBean)Data.GetOne("SELECT * FROM "+walletDao.getTableName()+" WHERE name = ?", new Object[]{bdb.getWallet()}, WalletBean.class);
//		if(btcWallet != null){
//			BigDecimal add = bdb.getAmount();
//			
//			sqls.add(new OneSql("update "+walletDao.getTableName()+" set btcs=btcs+? where walletId=?" , 1 , new Object[]{add , btcWallet.getWalletId()}));
//		}
		sqls.add(new OneSql("UPDATE "+bdDao.getTableName()+" SET succonfirm = 1 WHERE detailsId = ? and succonfirm = 0", 1, new Object[]{connId}));
		
		
		//=====================财务录入事务语句=============================
		List<OneSql> paySqls = saveEntrySqls();
		if(paySqls == null){
			return;
		}
		sqls.addAll(paySqls);
		//=====================财务录入操作结束=============================
		
		
		if(Data.doTrans(sqls)){
			WriteRight("确认充值成功。");
		}else{
			WriteRight("状态更新失败。");
		}
	}
	
	@Page(Viewer="/admins/btc/recharge/entry.jsp")
	public void charge(){
		String userId = param(0);
		User user = uDao.get(userId);
		if(user!=null){
			setAttr("userName", user.getUserName());
		}
		
		FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
		FinanUseType usetype = useTypeDao.getByType(3);
		setAttr("useTypeId", usetype.getId());
		
		List<Bean> accounts = (List<Bean>)Data.Query("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ?", new Object[]{coint.getFundsType(), AccountType.charge.getKey()}, FinanAccount.class);
		setAttr("accounts", accounts);
		
	}	

	/**
	 * 补单时填写地址自动查出充值账户
	 */
	@Page(Viewer = JSON)
	public void getWalletAccount() {
		String btcKey = param("btcKey");
		JSONObject object = new JSONObject();
		if(btcKey.length() > 0){
			keyDao.setCoint(coint);
			KeyBean keyBean = (KeyBean)Data.GetOne("select * from "+keyDao.getTableName()+" where keyPre=?", new Object[]{btcKey}, KeyBean.class);
			if(keyBean != null){
				walletDao.setCoint(coint);
				WalletBean btcWallet = (WalletBean)Data.GetOne("SELECT * FROM "+walletDao.getTableName()+" WHERE name = ?", new Object[]{keyBean.getWallet()}, WalletBean.class);
				if(btcWallet != null){
					object = JSONObject.fromObject(btcWallet);
				}
			}
		}
		json(object.isEmpty()?"no":"", true, object.toString());
	}
	
	@Page(Viewer=".xml")
	public void doCharge(){
		
		if(!codeCorrect(XML)){
			return;
		}
		
		String strMoney=request.getParameter("funds");
		BigDecimal money=BigDecimal.ZERO;
		if(strMoney.length()>0){
			money=DigitalUtil.getBigDecimal(strMoney);
		}
		String userName=request.getParameter("userName");
		String reason=request.getParameter("memo");
		if(reason.length() == 0){
			reason = BillType.sysRecharge.getValue();
		}
		
		//格式化日期
		if(money.compareTo(BigDecimal.ZERO) > 0){
			User user = new UserDao().getByField("userName", userName);
			if(user == null){
				Write("你输入的用户名不存在，充值失败，请重新输入用户名",false,"");
				return;
			}
			
			try {
				List<OneSql> sqls = new ArrayList<OneSql>();	

				PayUserBean payUser = payDao.getById(Integer.parseInt(user.getId()), coint.getFundsType());
				if(payUser == null){
					Write("用户资金信息不存在。",false,"");
					return;
				}
				
				sqls.addAll(fundsDao.addMoney(money, user.getId(), userName, reason, BillType.sysRecharge.getKey(), coint.getFundsType(), BigDecimal.ZERO, adminId()+"", true));
				
				int accountId = intParam("accountId");
				if(accountId > 0){
					FinanAccount account = new FinanAccountDao().get(accountId);
					
					keyDao.setCoint(coint);
					if(account.getBankAccountId() > 0){
						String btcKey = param("btcKey");
						if(btcKey.length() > 0){
							KeyBean keyBean = (KeyBean)Data.GetOne("select * from "+keyDao.getTableName()+" where keyPre=?", new Object[]{btcKey}, KeyBean.class);
							sqls.add(new OneSql("update "+keyDao.getTableName()+" SET usedTimes = usedTimes+1 WHERE keyId = ?", 1, new Object[]{keyBean.getKeyId()}));
						}
					}
					
					//=====================财务录入事务语句=============================
					List<OneSql> paySqls = saveEntrySqls();
					if(paySqls == null){
						return;
					}
					sqls.addAll(paySqls);
					//=====================财务录入操作结束=============================
				}
				
				if(Data.doTrans(sqls)){
					UserCache.resetUserFunds(user.getId());//即时刷新用户资产
					try {
						//插入一条管理员日志信息
						DailyType type = DailyType.btcCharge;
						new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "管理员充值", userName, money, reason, coint.getPropTag()), String.valueOf(adminId()), ip(), now() , Integer.parseInt(user.getId()),DigitalUtil.getBigDecimal(money));
					} catch (Exception e) {
						log.error("添加管理员日志失败", e);
					}
					//充值成功-设置该用户操作类型为提现受限
					Datastore ds = uDao.getDatastore();
					com.google.code.morphia.query.Query<User> q = ds.find(User.class, "_id", user.getId());
					UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
					ops.set("customerOperation", Const.CUSTOMER_OPERATION_NO_CASH);
					UpdateResults<User> ur = uDao.update(q, ops);
					if(ur.getHadError()){
						log.info("系统充值设置该用户为限制提现失败");
					}
					Write("充值成功！",true,"");
				}else{
					Write("充值失败",false,"");
				}
			} catch (Exception e) {
				log.error("内部异常", e);
				Write("程序出错",false,"");
			}
		}else{
			Write("请输入正确的充值金额",false,"");
		}
	}
	
	@Page(Viewer="/admins/btc/recharge/deduction.jsp")
	public void deduction(){
		String userId = param(0);
		if(StringUtils.isNotEmpty(userId)){
			User user = uDao.get(userId);
			setAttr("userName", user.getUserName());
			PayUserBean payUser = (PayUserBean) Data.GetOne("select * from pay_user where userid=? AND fundsType = ?",new Object[] {userId,coint.getFundsType() }, PayUserBean.class);
			setAttr("payUser", payUser);
		}
		
		FinanUseTypeDao useTypeDao = new FinanUseTypeDao();
		FinanUseType usetype = useTypeDao.getByType(3);
		setAttr("useTypeId", usetype.getId());
		
		List<Bean> accounts = (List<Bean>)Data.Query("SELECT * FROM finanaccount WHERE isDel = false AND fundType = ? AND type = ?", new Object[]{coint.getFundsType(), AccountType.charge.getKey()}, FinanAccount.class);
		setAttr("accounts", accounts);
	}
	
	@Page(Viewer=".xml")
	public void doDeduction(){
		
		if(!codeCorrect(XML)){
			return;
		}
		String strMoney=request.getParameter("money");
		BigDecimal money=BigDecimal.ZERO;
		if(strMoney.length()>0){
			money=DigitalUtil.getBigDecimal(strMoney).abs();
		}
		String userName=request.getParameter("userName");
		String reason=request.getParameter("memo");
		if(reason.length()<=0){
			reason=BillType.sysDeduct.getValue();
		}
		//格式化日期
		if(money.compareTo(BigDecimal.ZERO)>0){
			//修改Pay_user表信息并添加交易记录
			try {
				User curUser = uDao.getUserByColumn(userName , "userName");
				if(curUser == null){
					Write("你输入的用户名不存在，充值失败，请重新输入用户名", false, "");
					return;
				}
				
				int userId = Integer.parseInt(curUser.getId());
				
				List<OneSql> sqls = new ArrayList<OneSql>();
				
				sqls.addAll(fundsDao.subtractMoney(money, String.valueOf(userId), userName, reason, BillType.sysDeduct.getKey(), coint.getFundsType(), BigDecimal.ZERO, adminId()+"", true));
				
				
				int accountId = intParam("accountId");
				if(accountId > 0){
					//=====================财务录入事务语句=============================
					List<OneSql> paySqls = saveEntrySqls();
					if(paySqls == null){
						return;
					}
					sqls.addAll(paySqls);
					//=====================财务录入操作结束=============================
				}
				
				if(Data.doTrans(sqls)){
					UserCache.resetUserFunds(curUser.getId());//即时刷新用户资产
					try {
						//插入一条管理员日志信息
						DailyType type = DailyType.btcDeduct;
						new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "管理员扣除", userName, money, reason, coint.getPropTag()), String.valueOf(adminId()), ip(), now() , userId , DigitalUtil.getBigDecimal(money));
					} catch (Exception e) {
						log.error("添加管理员日志失败", e);
					}
					
					Write("成功扣除用户["+userName+"]的"+money+"个"+coint.getPropTag()+"。",true,"");
				}else{
					WriteError("扣除用户资金失败");
				}
			} catch (Exception e) {
				log.error("内部异常", e);
				Write("程序出错",false,"");
			}
		}else{
			Write("请输入正确的扣除金额",false,"");
		}
	}
	
	
	@Page(Viewer=".xml")
	public void doConfirm(){
		
		if(!codeCorrect(XML)){
			return;
		}
		
		long id = longParam("id");
		
		bdDao.setCoint(coint);
		String sqlDetails = "SELECT * FROM "+bdDao.getTableName()+" WHERE detailsId=? AND status=0";
		DetailsBean b = (DetailsBean) Data.GetOne(sqlDetails, new Object[] {id}, DetailsBean.class);
		
		if(b == null){
			Write("出错了！",false,"");
			return;
		}
		
		String userId = b.getUserId();

		PayUserBean buser = payDao.getById(Integer.parseInt(userId), coint.getFundsType());
		List<OneSql> sqls = new ArrayList<OneSql>();
		
		BigDecimal valueIn = b.getAmount();
		BigDecimal banlance = valueIn;
		if (buser != null)
			banlance = banlance.add(buser.getBalance()).add(buser.getFreez());

		sqls.add(new OneSql(
				"update "+bdDao.getTableName()+" set status=2,banlance=?,configTime=? where detailsId=? and status=0",
				1, new Object[] { banlance,
						new Timestamp(System.currentTimeMillis()),
						b.getDetailsId() }));

		if (buser != null)// 存在就帮更新总账户
			sqls.addAll(fundsDao.addMoney(valueIn, userId+"", b.getUserName(), "用户充值手动确认", BillType.recharge.getKey(), coint.getFundsType(), BigDecimal.ZERO, adminId()+"", true));
			
		BigDecimal add = valueIn;
		
		if (Data.doTrans(sqls)) {
			User rUser = new UserDao().getById(String.valueOf(userId));
				try {
					//插入一条管理员日志信息
					DailyType type = DailyType.btcHandConfirm;
					new MainDailyRecordDao().insertOneRecord(type, DailyType.getMemoByType(type, "管理员确认", rUser.getUserName(), add.doubleValue(), coint.getPropTag()), String.valueOf(adminId()), ip(), now());
				} catch (Exception e) {
					log.error("内部异常", e);
				}
				
				Write("发送成功！"  , true , "");
				return;
		}
	}
	@Page(Viewer = "")
	public void exportUser(){
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<DetailsBean> needUser = getUserList();
				
			String [] column = {"userName","inType","amount","toAddr","showStatu","sendTime"};//{"userName","submitTime","toAddress","amount","showStat"};
			String [] tabHead = {"用户名","交易类型","金额","地址","状态","时间"};//{"用户名","提交时间","提现地址","数量","状态"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_recharge_record.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<DetailsBean> getUserList(){
		String currentTab = param("tab");
		Timestamp startTime = dateParam("startTime");
		Timestamp endTime = dateParam("endTime");
		int status=intParam("status");
		String btcFrom = param("btcFrom");
		long entrustId = longParam("entrustId");
		String userName = param("userName");
		
		bdDao.setCoint(coint);
		Query<DetailsBean> query = bdDao.getQuery();
		query.setSql("select * from "+bdDao.getTableName());
		query.setCls(DetailsBean.class);
		
		if(currentTab.length()==0)
			currentTab = "charge";
				
		setAttr("currentTab", currentTab);
		
		if(startTime != null){
			query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
		}
		if(endTime != null){
			query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
		}
		if(status>0){
			if(status == 10){
				status = 0;
				query.append(" and Status="+status);
			}else if(status == 11){
				query.append(" and Status=2 and isIn = 1 AND sucConfirm=0");
			}
		}
		
		if(btcFrom.length()>0){
			query.append(" and fromAddr="+btcFrom);
	    }
		
		if(entrustId > 0){
			query.append(" and entrustId="+entrustId);
		}
		
		if(userName.length() > 0){
			query.append(" and userName = '"+ userName +"'");
		}

		int total = query.count();
		if(total > 0){
			query.append("order by sendTime desc");
			List<DetailsBean> btcDownloads = query.getList();
			
			return btcDownloads;
		}
		return null;
	}
	
	@Page(Viewer = JSON)
	public void tongji() {
		try {
			//查询条件
			String currentTab = param("tab");
			Timestamp startTime = dateParam("startTime");
			Timestamp endTime = dateParam("endTime");
			int status=intParam("status");
			String btcFrom = param("btcFrom");
			long entrustId = longParam("entrustId");
			String userName = param("userName");
			
			bdDao.setCoint(coint);
			Query<DetailsBean> query = bdDao.getQuery();
			query.setSql("select * from "+bdDao.getTableName());
			query.setCls(DetailsBean.class);
			
			String ids = param("eIds");
			boolean isAll = booleanParam("isAll");
			
			if(isAll){
				if(currentTab.length()==0)
					currentTab = "charge";
						
				request.setAttribute("currentTab", currentTab);
				
				if(startTime != null){
					query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
				}
				if(endTime != null){
					query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
				}
				if(status>0){
					if(status == 10){
						status = 0;
						query.append(" and Status="+status);
					}else if(status == 11){
						query.append(" and Status=2 and isIn = 1 AND sucConfirm=0");
					}
				}
				
				if(btcFrom.length()>0){
					query.append(" and fromAddr="+btcFrom);
			    }
				
				if(entrustId > 0){
					query.append(" and entrustId="+entrustId);
				}
				
				if(userName.length() > 0){
					query.append(" and userName = '"+ userName +"'");
				}
			}else{
				if(ids.endsWith(",")){
					ids = ids.substring(0, ids.length()-1);
				}
				query.append(" AND detailsId IN ("+ids+")");
			}
			
			List<DetailsBean> list = bdDao.find();
			
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
	public void syncConfirmTimes(){

		long id = longParam("id");
		bdDao.setCoint(coint);
		String sqlDetails = "SELECT * FROM "+bdDao.getTableName()+" WHERE detailsId=?";
		DetailsBean edBean = (DetailsBean) Data.GetOne(sqlDetails, new Object[] { id }, DetailsBean.class);
		int res = 0;
		if (null != edBean) {
			com.alibaba.fastjson.JSONObject json = MerchantsUtil.getConfirmTimes(coint.getStag(), edBean.getAddHash(), edBean.getToAddr());
			if (null != json && json.containsKey("data") && json.getJSONObject("data").containsKey("times")) {
				long times = json.getJSONObject("data").getLongValue("times");
				res = Data.Update("update "+bdDao.getTableName()+" set confirmTimes=? where detailsId=? and confirmTimes<=?",
						new Object[] { times, edBean.getDetailsId(), times });
			}

		}

		if (res > 0) {
			json("操作成功", true, "");
		} else {
			json(L("操作失败"), false, "");
		}
	}
}
