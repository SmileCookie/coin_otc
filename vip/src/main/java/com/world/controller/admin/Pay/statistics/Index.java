package com.world.controller.admin.Pay.statistics;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import net.sf.json.JSONArray;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@FunctionAction(jspPath = "/admins/Pays/statistics/" , des = "资金统计")
public class Index extends AdminAction{
	
	UserDao userDao = new UserDao();
	PayUserDao payDao = new PayUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		int currentPage = intParam("page");
		String currentTab = param("tab");
		String userName = param("userName");
		String userId = param("userId");
		double minBalance = doubleParam("minB");
		double maxBalance = doubleParam("maxB");
		String balanceOrder = param("balance");
		String freezOrder = param("freez");
		String outingOrder = param("outing");
		String storageOrder = param("storage");
		
		setAttr("balance", balanceOrder);
		setAttr("freez", freezOrder);
		setAttr("outing", outingOrder);
		//setAttr("storage", storageOrder);
		
		if(currentTab.length() == 0){
			currentTab = "btc";
		}
		setAttr("tab", currentTab);

		int pageSize = 20;
		CoinProps coint = DatabasesUtil.coinProps(currentTab);
		int fundsType = coint.getFundsType();//资金类型
		//payDao.setDatabase(coint.getDatabasesName());
		
		StringBuffer q = new StringBuffer();
		q.append("SELECT p.balance, p.freez, p.userId ,p.userName ,p.inSuccess, p.outWait, p.withdrawFreeze, p.fundsType FROM pay_user p ");
		
		Query<PayUserBean> query = payDao.getQuery();
		query.setSql("SELECT * FROM ("+q.toString()+") a");
		query.setCls(PayUserBean.class);

		//Start by chendi 用户id置换用户名称
		if(userId.trim().length()>0){
			query.append(" AND userId = '"+userId+"'");
		}
		/*if(userName.trim().length()>0){
			Pattern pattern = Pattern.compile("^.*"  + userName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
			List<User> users = userDao.find(userDao.getQuery().filter("userName", pattern)).asList();
			String userIds="";
			if(users.size()>0){
				for(User b : users){
					userIds+=","+b.getId();
				}
			}
			if(userIds.length()>0){
				query.append(" AND userId in("+userIds.substring(1)+")");
			}else {
				query.append(" AND userName like '%"+userName+"%'");
			}
		}*/
		//end
		if(fundsType>0){
			query.append(" AND fundsType = "+fundsType);
		}
		
		
		if(minBalance > 0){
			query.append(" AND (balance+freez) >= "+minBalance);
		}
		if(maxBalance > 0){
			query.append(" AND (balance+freez) <= "+maxBalance);
		}
		query.append(" AND (balance+freez) > 0");
		
		int total = query.count();
		if(total > 0){
			if(balanceOrder.length() > 0){
				query.append(" order by balance " + balanceOrder);
			}else if(freezOrder.length() > 0){
				query.append(" order by freez " + freezOrder);
			}else{
				query.append(" order by (balance+freez) desc");
			}
			List<PayUserBean> payUsers = query.getPageList(currentPage, pageSize);//提现记录
			setAttr("coinMap",DatabasesUtil.getCoinPropMaps());//币种集合
			request.setAttribute("dataList", payUsers);
		}
		setPaging(total, currentPage, pageSize);
	}
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}	
	
	@Page(Viewer = JSON)
	public void tongji(){
		List<PayUserBean> list = findList();
		
		BigDecimal totalMoney = BigDecimal.ZERO;
		BigDecimal totalBalance = BigDecimal.ZERO;
		BigDecimal totalFreez = BigDecimal.ZERO;
	
		for(Bean b : list){
			PayUserBean payUser = (PayUserBean)b;
			
			totalMoney = totalMoney.add(payUser.getTotal());
			totalBalance = totalBalance.add(payUser.getBalance());
			totalFreez = totalFreez.add(payUser.getFreez());
		}
		JSONArray array = new JSONArray();
		array.add(totalMoney);
		array.add(totalBalance);
		array.add(totalFreez);
		json("", true, array.toString());
	}
	
	@Page(Viewer = "")
	public void exportUser(){
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<PayUserBean> needUser = getUserList();
				
			String [] column = {"userName","total","balance","inSuccess","freez","withdrawFreeze","outWait","entrustFreeze"};
			String [] tabHead = {"用户名","总额","可用余额","融资融币借入金额","冻结余额","提现冻结金额","融资融币放贷冻结金额","挂单委托冻结金额"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_user_info.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<PayUserBean> getUserList(){
		List<PayUserBean> payUsers = findList();
		
		List<String> userIds = new ArrayList<String>();
		for(PayUserBean pay : payUsers){
			userIds.add(String.valueOf(pay.getUserId()));
		}
		
		UserDao uDao = new UserDao();
		Map<String, User> users = uDao.getUserMapByIds(userIds);
		
		for(Bean b : payUsers){
			PayUserBean pay = (PayUserBean) b;
			User user = users.get(pay.getUserId());
			if(user != null){
				pay.setRealName(user.getRealName());
				UserContact uc = user.getUserContact();
				if(uc != null){
					pay.setSafeMobile(uc.getSafeMobile());
					pay.setEmail(uc.getSafeEmail());
				}
			}
		}
		return payUsers;
	}
	
	public List<PayUserBean> findList(){
		String currentTab = param("tab");
		String userName = param("userName");
		double minBalance = doubleParam("minB");
		double maxBalance = doubleParam("maxB");
		
		if(currentTab.length() == 0){
			currentTab = "btc";
		}
		setAttr("tab", currentTab);

		CoinProps coint = DatabasesUtil.coinProps(currentTab);
		//payDao.setDatabase(coint.getDatabasesName());
		int fundsType = coint.getFundsType();//资金类型
		
		StringBuffer q = new StringBuffer();
		q.append("SELECT p.balance, p.freez, p.userId ,p.userName ,p.inSuccess, p.outWait, p.withdrawFreeze, p.fundsType FROM pay_user p ");
		
		Query<PayUserBean> query = payDao.getQuery();
		query.setSql("SELECT * FROM ("+q.toString()+") a");
		query.setCls(PayUserBean.class);
		
		String ids = param("eIds");
		boolean isAll = booleanParam("isAll");
		
		if(isAll){
			if(userName.trim().length()>0){
				Pattern pattern = Pattern.compile("^.*"  + userName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				List<User> users = userDao.find(userDao.getQuery().filter("userName", pattern)).asList();
				String userIds="";
				if(users.size()>0){
					for(User b : users){
						userIds+=","+b.getId();
					}
				}
				if(userIds.length()>0){
					query.append("userId in("+userIds.substring(1)+")");
				}
			}
			if(fundsType>0){
				query.append(" AND fundsType = "+fundsType);
			}
			
			if(minBalance > 0){
				query.append(" (balance+freez) >= "+minBalance);
			}
			if(maxBalance > 0){
				query.append(" (balance+freez) <= "+maxBalance);
			}
			query.append(" (balance+freez) > 0");
		}else{
			if(fundsType>0){
				query.append(" AND fundsType = "+fundsType);
			}
			
			if(ids.endsWith(",")){
				ids = ids.substring(0, ids.length()-1);
			}
			query.append(" AND userId IN ("+ids+")");
		}
		query.append(" order by (balance+freez) desc");
		List<PayUserBean> list = query.getList();
		return list;
	}
}
