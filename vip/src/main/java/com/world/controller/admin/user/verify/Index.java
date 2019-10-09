package com.world.controller.admin.user.verify;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.VerifyUserInfoDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.user.User;
import com.world.model.entity.user.UserContact;
import com.world.model.entity.user.VerifyUserInfo;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@SuppressWarnings("serial")
@FunctionAction(jspPath = "/admins/user/verify/", des = "手机/Google审核")
public class Index extends AdminAction {
	
	VerifyUserInfoDao dao = new VerifyUserInfoDao();
	UserDao userDao = new UserDao();
	DetailsDao detailsDao = new DetailsDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		try{
			// 获取参数
			int tab = intParam("tab");
			int pageNo = intParam("page");
			String userName = param("userName");//用户名
			String userId = param("userId");//用户id
			Query<VerifyUserInfo> query = dao.getQuery();
			
			if(userName.length()>0){//用户名
				Pattern pattern = Pattern.compile("^.*"  + userName.replace("+", "")+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				query.filter("userName", pattern);
			}
			if(userId.length()>0){//用户id
				query.filter("userId", userId);
			}
			if (tab == 4) {
				query.filter("type", 3);
			} else {
				query.filter("status", tab);
			}
			
			setAttr("tab", tab);
			
			long total = dao.count(query);
			String queryCondition = query.toString();
			
			log.info("搜索的sql语句:" + queryCondition);
			
			int pageSize = 20;
			
			//查数量时就不用排序了
			query.order("- addTime");
			if (total > 0) {
				List<VerifyUserInfo> dataList = dao.findPage(query, pageNo, pageSize);
				
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
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
		try {
			String id = param("id");
			if(id.length() > 0){
				VerifyUserInfo info = dao.get(id);
				request.setAttribute("info", info);
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}
	
	@Page(Viewer = JSON)
	public void doAoru() {
		try {
			String id = param("id");
			int status = intParam("status");
			String memo = param("memo");
			
			if(!codeCorrect(JSON)){
				return;
			}
			
			VerifyUserInfo info = dao.get(id);
			
			if(info == null || info.getStatus() != 0){
				json(L("不存在的记录"), false, "");
				return;
			}
			
			User loginUser = userDao.get(info.getUserId());
			if(loginUser == null){
				json(L("用户不存在"), false, "");
				return;
			}
			
			UserContact uc = loginUser.getUserContact();
			
			boolean flag = false;
			String sendMsg = "";
			
			//修改手机
			if(info.getType() == 1 && status == 2){	
				String newUserName = "";
				if (null != uc.getSafeMobile()) {
					newUserName = info.getInfo().split(" ")[1];
				}
				
				Datastore ds = userDao.getDatastore();
				Query<User> q = ds.find(User.class, "_id", info.getUserId());  
				UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
				
				if (!"".equals(newUserName)) {
					ops.set("userName", newUserName);
				}
				ops.set("userContact.mCode", info.getMcode());
				ops.set("userContact.safeMobile", info.getInfo());
				ops.set("userContact.loginCheckMobile", newUserName);//手机号（不带国家码）
				ops.set("userContact.mobileCode", "");
				ops.set("userContact.mobileStatu", AuditStatus.pass.getKey());
				ops.set("userContact.checkMobile", "");
				ops.set("userContact.codeTime", new Timestamp(0));
				ops.set("userContact.modifyMobileTime", now());
				try {
					JSONArray array = userDao.authenMemo(loginUser, "mobile", info.getInfo(), info.getIp());
					ops.set("userContact.memo", array.toString());
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				
				UpdateResults<User> ur = userDao.update(q, ops);
				
				if(!ur.getHadError()){
					// 修改后锁定24小时
					flag = true;
					if(StringUtils.isEmpty(memo))
						memo = "已审核，通过用户申请";
				}
			}
			
			//修改GOOGLE
			if(info.getType() == 2 && status == 2){	
				if(!userDao.updateGoogleAu(info.getUserId(), AuditStatus.pass.getKey()).getHadError()){
					userDao.updateSecret(info.getUserId(), info.getInfo());
					flag = true;
					if(StringUtils.isEmpty(memo))
						memo = "已审核，通过用户申请";
				}
			}
			
			if(flag){
				//给用户发送一条短信
				
			}
			
			//更改操作信息
			if(StringUtils.isEmpty(memo)){
				if(info.getStatus() == 1)	memo = "已审核，拒绝用户申请";
				if(info.getStatus() == 3)	memo = "已审核，撤消用户申请";
			}
			
			info.setMemo(memo);
			info.setStatus(status);
			info.setAdminId(adminId()+"");
			info.setVerifyTime(TimeUtil.getNow().getTime());
			
			dao.update(info);
			
			json(L("操作成功"), true, "");
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json(L("内部异常"), false, "");
		}
	}
	
	@Page(Viewer = "/admins/user/verify/show.jsp")
	public void show(){
		try {
			String id = param("id");
			if(id.length() > 0){
				VerifyUserInfo info = dao.get(id);
				request.setAttribute("info", info);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = "/admins/user/verify/compareWithSystemInfo.jsp")
	public void compareWithSystemInfo(){
		try {
			String id = param("id");
			if(id.length() > 0){
				VerifyUserInfo info = dao.get(id);
				request.setAttribute("info", info);
				VerifyUserInfo sysInfo = getSysInfo(info.getUserId());
				request.setAttribute("sysInfo", sysInfo);
			}
			
			Map<Integer, String> typeMap = new HashMap<Integer, String>();
//			for (CurrencyType item : CurrencyType.values()) {
//				typeMap.put(item.getKey(), item.getValue());
//			}
			setAttr("typeMap", typeMap);
			
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	private VerifyUserInfo getSysInfo(String userIdStr) {/*
		// TODO Auto-generated method stub
		int userId = Integer.valueOf(userIdStr);
		VerifyUserInfo sysInfo = new VerifyUserInfo();
		User user = userDao.getUserById(userIdStr);
		//注册年月
		Timestamp registerTime = user.getRegisterTime();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(registerTime);
		sysInfo.setRegYear( calendar.get(Calendar.YEAR) );
		sysInfo.setRegMonth( calendar.get(Calendar.MONTH) );
		
		//get 4 beans of different currency
		DetailsBean details = detailsDao.getFirst(userId, 1);
		
		boolean everDepositBtc = null != btcDepositBean ? true : false;
		boolean everDepositLtc = null != ltcDepositBean ? true : false;
		boolean everDepositEth = null != ethDepositBean ? true : false;
		boolean everDepositEtc = null != etcDepositBean ? true : false;
		
		//首次充值币种
		int firstDepositCurrency = 0;
		//首次充值金额
		String firstDepositAmount = "";
		Timestamp tempDepositTime = null;
		
		if (!everDepositBtc && !everDepositLtc && !everDepositEth && !everDepositEtc) {
			firstDepositCurrency = 0;
			firstDepositAmount = "";
		} else {
			//充值过比特币
			if (everDepositBtc) {
				firstDepositCurrency = CurrencyType.BTC.getKey();
				firstDepositAmount = String.valueOf(btcDepositBean.getAmount());
				tempDepositTime = btcDepositBean.getSendimeTime();
			}
			//充值过莱特币
			if (everDepositLtc) {
				
				//没有充值过 或者 莱特币比比特币早
				if (null == tempDepositTime || ltcDepositBean.getSendimeTime().compareTo(tempDepositTime) < 0) {
					firstDepositCurrency = CurrencyType.LTC.getKey();
					firstDepositAmount = String.valueOf(ltcDepositBean.getAmount());
					tempDepositTime = ltcDepositBean.getSendimeTime();
				}
			}
			//充值过eth
			if (everDepositEth) {
				
				//没有充值过 或者 eth 比 之前早
				if (null == tempDepositTime || ethDepositBean.getSendimeTime().compareTo(tempDepositTime) < 0) {
					firstDepositCurrency = CurrencyType.ETH.getKey();
					firstDepositAmount = String.valueOf(ethDepositBean.getAmount());
					tempDepositTime = ethDepositBean.getSendimeTime();
				}
			}
			//充值过eth
			if (everDepositEtc) {
				
				//没有充值过 或者 eth 比 之前早
				if (null == tempDepositTime || etcDepositBean.getSendimeTime().compareTo(tempDepositTime) < 0) {
					firstDepositCurrency = CurrencyType.ETC.getKey();
					firstDepositAmount = String.valueOf(etcDepositBean.getAmount());
				}
			}
		}
		
		btcDetailsBean btcWithdrawBean = btcDetailsDao.getFirst(userId, 0);
		LtcDetailsBean ltcWithdrawBean = ltcDetailsDao.getFirst(userId, 0);
		EthDetailsBean ethWithdrawBean = ethDetailsDao.getFirst(userId, 0);
		EtcDetailsBean etcWithdrawBean = etcDetailsDao.getFirst(userId, 0);

		boolean everWithdrawBtc = null != btcWithdrawBean ? true : false;
		boolean everWithdrawLtc = null != ltcWithdrawBean ? true : false;
		boolean everWithdrawEth = null != ethWithdrawBean ? true : false;
		boolean everWithdrawEtc = null != etcWithdrawBean ? true : false;
		
		//首次Withdraw币种
		int firstWithdrawCurrency = CurrencyType.BTC.getKey();
		//首次Withdraw金额
		String firstWithdrawAmount = "";
		Timestamp tempWithdrawTime = null;
		
		if (!everWithdrawBtc && !everWithdrawLtc && !everWithdrawEth && !everWithdrawEtc) {
			firstWithdrawCurrency = 0;
			firstWithdrawAmount = "";
		} else {
			//Withdraw过比特币
			if (everWithdrawBtc) {
				firstWithdrawCurrency = CurrencyType.BTC.getKey();
				firstWithdrawAmount = String.valueOf(btcWithdrawBean.getAmount());
				tempWithdrawTime = btcWithdrawBean.getSendimeTime();
			}
			//Withdraw过莱特币
			if (everWithdrawLtc) {
				
				//没有Withdraw过 或者 莱特币比比特币早
				if (null == tempWithdrawTime || ltcWithdrawBean.getSendimeTime().compareTo(tempWithdrawTime) < 0) {
					firstWithdrawCurrency = CurrencyType.LTC.getKey();
					firstWithdrawAmount = String.valueOf(ltcWithdrawBean.getAmount());
					tempWithdrawTime = ltcWithdrawBean.getSendimeTime();
				}
			}
			//Withdraw过eth
			if (everWithdrawEth) {
				
				//没有Withdraw过 或者 eth 比 之前早
				if (null == tempWithdrawTime || ethWithdrawBean.getSendimeTime().compareTo(tempWithdrawTime) < 0) {
					firstWithdrawCurrency = CurrencyType.ETH.getKey();
					firstWithdrawAmount = String.valueOf(ethWithdrawBean.getAmount());
					tempWithdrawTime = ethWithdrawBean.getSendimeTime();
				}
			}
			//Withdraw过eth
			if (everWithdrawEtc) {
				
				//没有Withdraw过 或者 eth 比 之前早
				if (null == tempWithdrawTime || etcWithdrawBean.getSendimeTime().compareTo(tempWithdrawTime) < 0) {
					firstWithdrawCurrency = CurrencyType.ETC.getKey();
					firstWithdrawAmount = String.valueOf(etcWithdrawBean.getAmount());
				}
			}
		}
		
//		firstDepositCurrency = 2;
//		firstDepositAmount = "50";
		
		//put the currency type and amount into the model
		sysInfo.setFirstDepositCurrency(firstDepositCurrency);
		sysInfo.setFirstDepositAmount(firstDepositAmount);
		sysInfo.setFirstWithdrawCurrency(firstWithdrawCurrency);
		sysInfo.setFirstWithdrawAmount(firstWithdrawAmount);
		
		return sysInfo;
	*/
		return null;}
}

