package com.world.controller.admin.btc.downloadKey;

import java.util.List;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.dao.pay.ReceiveAddrDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.pay.ReceiveAddr;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;


@FunctionAction(jspPath = "/admins/btc/downloadkey/" , des = "提现地址")
public class Index extends AdminAction{
	ReceiveAddrDao dao = new ReceiveAddrDao();
	
	@Page(Viewer=DEFAULT_INDEX)
	public void index(){
		//查询条件
		int currentPage = intParam("page");
		String tab = param("tab");
		int userId = intParam("userId");
		String userName = param("userName").trim();
		String address = param("address").trim();
		int isLocked = intParam("isLocked");
		
		dao.setCoint(coint);
		Query query = dao.getQuery();
		query.setSql("select * from "+dao.getTableName());
		query.setCls(ReceiveAddr.class);
		
		int pageSize = 20;
		
		if(tab.length() == 0){
			tab = "normal";
		}
		
		if(currentPage == 0)
			currentPage = 1;
		
		if(userName.trim().length()>0){
			query.append(" userName LIKE ('%"+userName+"%')");
		}
		
		if(userId > 0){
			query.append(" userId = " + userId);
		}

		if(address.length() > 0){
			query.append(" address = '" + address + "'");
		}
		
		int total = query.count();
		if(total>0){
			query.append(" ORDER BY createTime DESC");
			//分页查询
			List<ReceiveAddr> dataList = dao.findPage(currentPage, pageSize);
			
			setAttr("dataList", dataList);
			setPaging(total, currentPage, pageSize);
		}
		setAttr("itemCount", total);
		//页面顶部币种切换
        super.setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}
	
}

