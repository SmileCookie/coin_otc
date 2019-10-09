package com.world.web.action;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.user.User;
import com.world.model.financial.dao.FinanAccountDao;
import com.world.model.financial.dao.FinanEntryDao;
import com.world.model.financial.dao.FinanUseTypeDao;
import com.world.model.financial.entity.FinanAccount;
import com.world.model.financial.entity.FinanEntry;
import com.world.model.financial.entity.FinanUseType;
import com.world.util.WebUtil;
import com.world.web.sso.session.Session;

/****
*   
* 项目名称：jua  
* 类名称：UserAction  
* 类描述：  
* 创建人：yangchunhe  
* 创建时间：2012-5-15 下午08:39:21  
* 修改人：yangchunhe  
* 修改时间：2012-5-15 下午08:39:21  
* 修改备注：  
* @version   
*
*/
public class FinanAction extends UserAction{

	/*****
	 * 获取当前用户ID
	 * @param isRidirect 是否返回历史页面  对于xml的请求isHistory=false
	 * @param end  是否结束方法，方法是否继续执行
	 * @param isIframe 是否处于iframe模式
	 * @return
	 */
	protected int adminId() {
		
		try {
			String userId = GetCookie(Session.aid);
			if(userId != null && userId.length() > 0){
				return Integer.parseInt(userId);
			}
		} catch (NumberFormatException e) {
			log.error("内部异常", e);
		}
		return 0;
	}
	

	protected int roleId() {
		
		try {
			String roleId = GetCookie(Session.rid);
			if(roleId != null && roleId.length() > 0){
				return Integer.parseInt(roleId);
			}
		} catch (NumberFormatException e) {
			log.error("内部异常", e);
		}
		return 0;
	}
	
	protected String adminName(){ 
		
		String userName = WebUtil.getCookieByName(request, Session.aname);
		try {
			userName = URLDecoder.decode(userName , "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("内部异常", e);
		}
		return userName;
	}
	

	protected User getUserByUserId(int userId) {
		return new UserDao().getUserById(userId+"");
	}
	
	FinanUseTypeDao utDao = new FinanUseTypeDao();
	FinanAccountDao aDao = new FinanAccountDao();
	FinanEntryDao dao = new FinanEntryDao();
	public boolean saveEntry(){
		List<OneSql> paySqls = saveEntrySqls();
		
		if(paySqls == null){
			return false;
		}
		
		boolean due = Data.doTrans(paySqls);
		if (!due) {
			WriteError(L("财务录入执行失败。"));
			return false;
		}
		return true;
	}
	
	public List<OneSql> saveEntrySqls(){
		int accountId = intParam("accountId");
		int useTypeId = intParam("useTypeId");
		
		FinanUseType useType = utDao.get(useTypeId);
		if(useType == null){
			WriteError(L("用途不存在。"));
			return null;
		}
		
		int userId = 0;
		String userName = param("userName");
		User user = new UserDao().getByField("userName", userName);
		if(user != null){
			userId = Integer.parseInt(user.getId());
		}
		
		BigDecimal funds = decimalParam("funds");
		BigDecimal fundsComm = decimalParam("fundsComm");

		int fundType = intParam("fundType");
		
		FinanAccount account = aDao.get(accountId);
		if(account.getFundType() != fundType){
			WriteError(L("该账户的资金类型与您输入的资金类型不符。"));
			return null;
		}
		
		int toAccountId = 0;
		if(useType.getTurnRound() == 1){
			toAccountId = intParam("toAccountId");
			if(toAccountId == 0){
				WriteError(L("必须选择周转账户。"));
				return null;
			}
		}

		boolean isAdd = true;
		if(useType.getIsIn() == 2){//金额+手续费==本次要支出的金额
			if(funds.compareTo(BigDecimal.ZERO) < 0){
				WriteError(L("支出的资金必须是正值。"));
				return null;
			}
			
			BigDecimal mous = funds.subtract(fundsComm);
			if(mous.compareTo(BigDecimal.ZERO) > 0 && mous.compareTo(account.getFunds()) > 0){
				WriteError(L("该账户的资金不足。"));
				return null;
			}
			isAdd = false;
		}else if(funds.compareTo(BigDecimal.ZERO) < 0){//代表支出
			if(funds.abs().compareTo(account.getFunds()) > 0){
				WriteError(L("该账户的资金不足。"));
				return null;
			}
		}
		long connId = longParam("connId");
		
		String memo = param("memo");
		
		FinanEntry entry = new FinanEntry(accountId, useTypeId, fundType, funds, fundsComm, userId, userName, memo, ip(), toAccountId, 0, connId);
		Timestamp now = now();
		List<OneSql> paySqls = new ArrayList<OneSql>();
		
		entry.setCreateId(adminId());
		entry.setCreateTime(now);
		
		paySqls.add(aDao.updateMoney(accountId, funds, fundsComm, fundType, isAdd));
		paySqls.add(dao.insertSql(entry));
		if(toAccountId > 0){
			paySqls.add(aDao.updateMoney(toAccountId, funds, BigDecimal.ZERO, fundType, true));
			
			FinanEntry outEntry = new FinanEntry(toAccountId, useTypeId, fundType, funds, BigDecimal.ZERO, userId, userName, memo, ip(), 0, accountId, 0);
			outEntry.setCreateId(adminId());
			outEntry.setCreateTime(now);
			
			paySqls.add(dao.insertSql(outEntry));
		}
		return paySqls;
	}
}
