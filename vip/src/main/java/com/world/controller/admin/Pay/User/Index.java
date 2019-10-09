package com.world.controller.admin.Pay.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.world.config.GlobalConfig;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.pay.fee.FeeDetails;
import com.world.model.entity.pay.fee.FeeFactory;
import com.world.model.entity.user.User;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;


@FunctionAction(jspPath = "/admins/Pays/user/" , des = "用户资金")
public class Index extends AdminAction{
	
	UserDao userDao = new UserDao();
	PayUserDao dao = new PayUserDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		int currentPage = intParam("page");
		String tab = param("tab");
		String userName = param("userName").trim();
		String userId = param("userId");
		int orderWay = intParam("orderWay");
		
		Query query = dao.getQuery();
		query.setSql("select * from Pay_User");
		query.setCls(PayUserBean.class);
		//查询附加条件
		if(tab.length() == 0){
			tab = "all";
		}
		if(!tab.equals("all")){
			CoinProps coint = DatabasesUtil.coinProps(tab);
			query.append(" AND fundsType = "+coint.getFundsType());
		}
		
		request.setAttribute("currentTab", tab);

		if(userName.length()>0){
			query.append("userName LIKE '%"+userName+"%'");
		}
		if(userId.length()>0){
			query.append("userId = '"+userId+"' ");
		}
		int total = query.count();
		if(total > 0){
			if(orderWay==1 || orderWay == 0){
				query.append("order by userId desc");
			}else if(orderWay==2){
				query.append("order by balance desc");
			}
			List<Bean> payUsers = query.getPageList(currentPage, PAGE_SIZE);//提现记录
			
			List<String> userIds = new ArrayList<String>();
			for(Bean b : payUsers){
				PayUserBean btb=(PayUserBean) b;
				userIds.add(btb.getUserId()+"");
			}
			if(userIds.size()>0){
				Map<String,User> userMaps = userDao.getUserMapByIds(userIds);
				for(Bean b : payUsers){
					PayUserBean btb=(PayUserBean) b;
					btb.setUser(userMaps.get(btb.getUserId()+""));
				}
			}
			
			request.setAttribute("dataList", payUsers);
			setAttr("itemCount", total);
		}
		setPaging(total, currentPage);
		setPaging(total, currentPage);
		setAttr("isOpenManagement", GlobalConfig.isOpenManagement);
		setAttr("ft", DatabasesUtil.getCoinPropMaps());
	}
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}	

	public BigDecimal getFee(PayUserBean user) {
		BigDecimal fee = BigDecimal.ZERO;
		FeeDetails detail = FeeDetails.commonReachCard;
		if(detail != null){
			fee = FeeFactory.getFee(detail, 1);
		}
		return fee;
	}
	
}
