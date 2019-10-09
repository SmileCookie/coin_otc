package com.world.controller.admin.btc.rechargeKey;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.pay.KeyBean;
import com.world.model.entity.user.User;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;


@SuppressWarnings("serial")
@FunctionAction(jspPath = "/admins/btc/rechargekey/" , des = "充值地址")
public class Index extends AdminAction{
	KeyDao keyDao = new KeyDao();
	
	@Page(Viewer=DEFAULT_INDEX)
	public void index(){
		UserDao uDao = new UserDao();
		//查询条件
		int currentPage = intParam("page");
		String currentTab = param("tab");
		int userId = intParam("userId");
		String userName = param("userName").trim();
		String address = param("address").trim();
		String wallet = param("wallet");
		
		StringBuffer query = new StringBuffer();
		
		if(currentTab.length() == 0){
			currentTab = "hasset";
		}
		if(currentTab.equals("hasset")){
			query.append(" and userId > 0");
		}else if(currentTab.equals("hascharge")){
			query.append(" and usedTimes > 0");
		}else if(currentTab.equals("nocharge")){
			query.append(" and usedTimes = 0");
		}else{
			query.append(" and userId = 0");
		}
		
		if(userName.trim().length()>0){
			
			Pattern pattern = Pattern.compile("^.*"  + userName+  ".*$" ,  Pattern.CASE_INSENSITIVE);
			List<User> users = uDao.find(uDao.getQuery().filter("userName", pattern)).asList();
			String userIds="";
			if(users.size()>0){
				for(User b : users){
					userIds+=","+b.getId();
				}
			}
			if(userIds.length()>0){
				userIds=userIds.substring(1);
				query.append(" AND UserId in("+userIds+")");
			}
		}
		
		if(userId > 0){
			query.append(" AND userId = " + userId);
		}
		
		if(address.length() > 0){
			query.append(" AND keyPre = '" + address + "'");
		}
		
		if(wallet.length() > 0){
			query.append(" AND wallet = '" + wallet + "'");
		}
		String where = query.toString();
		if(where.length() > 0){
			where = " where " + where.substring(4);
		}
		
		keyDao.setCoint(coint);
		int total = 0;
		List li = (List)Data.GetOne("select count(*) from "+ keyDao.getTableName()+" " + where, new Object[]{});
		if(li != null){
			total = Integer.parseInt(li.get(0).toString());
		}
		
		if(total>0){
			//分页查询
			query.append("order by createTime");
			if(currentPage == 0)
				currentPage = 1;
			
			int pageSize = 20;
			List<Bean> receives = (List<Bean>)Data.Query("select * from "+ keyDao.getTableName()+" " + where + " order by createTime limit " + (currentPage-1)*pageSize + ","+pageSize, new Object[]{}, KeyBean.class);
			
			setAttr("dataList", receives);
			setPaging(total, currentPage, pageSize);
		}
		setAttr("itemCount", total);
		
		List<Bean> wallets = (List<Bean>)Data.Query("SELECT * FROM "+ keyDao.getTableName()+" GROUP BY wallet order by wallet", new Object[]{}, KeyBean.class);
		setAttr("wallets", wallets);
		
		//页面顶部币种切换
        super.setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}
	
	//
	@Page(Viewer = "/admins/btc/rechargekey/sign.jsp")
	public void sign(){
		List<Bean> wallets = (List<Bean>)Data.Query("SELECT * FROM "+ keyDao.getTableName()+" GROUP BY wallet order by wallet", new Object[]{}, KeyBean.class);
		setAttr("wallets", wallets);
	}
	
	/**
	 * 标识钱包地址已使用
	 */
	@Page(Viewer = ".xml")
	public void doSign(){
		try {
			if(!codeCorrect(XML)){
				return;
			}
			String wallet = param("wallet");
			if(StringUtils.isEmpty(wallet)){
				WriteError("钱包不能为空");
				return;
			}
			if(Data.Update("update "+ keyDao.getTableName()+" set tag=1 where wallet=?", new Object[]{wallet}) > 0)
				WriteRight("标记成功");
			else
				WriteError("标记失败");
		} catch (Exception e) {
			log.error("内部异常", e);
			WriteError("未知异常");
		}
	}
}

