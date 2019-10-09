/*package com.world.controller.admin.btc.wallet;

import java.math.BigDecimal;
import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.Query;
import com.world.model.dao.wallet.WalletDao;
import com.world.model.entity.pay.WalletBean;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/btc/wallet/", des = "钱包管理")
public class Index extends AdminAction {

	private static final long serialVersionUID = 1L;
	WalletDao dao = new WalletDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index(){
		//查询条件
		int currentPage = intParam("page");
		String currentTab = param("tab");

		dao.setCoint(coint);
		Query<WalletBean> query = dao.getQuery();
		query.setSql("select * from "+dao.getTableName());
		query.setCls(WalletBean.class);
		
		if(currentTab.length() == 0)
			currentTab = "wait";
		request.setAttribute("currentTab", currentTab);
		
		query.append(" ORDER BY  walletId desc");
		int total = query.count();
		if(total > 0){
			//分页查询
			List<WalletBean> lists = dao.findPage(currentPage, PAGE_SIZE);
			
			request.setAttribute("dataList", lists);
		}
		
		setPaging(total, currentPage);
		
	}
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax(){
		index();
	}	

	*//**
	 * 功能:响应添加的函数
	 *//*
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			int id = intParam("id");
			setAttr("id", id);
			if (id > 0) {
				WalletBean data = (WalletBean) Data.GetOne("select * from "+dao.getTableName()+" where walletId=?", new Object[]{id} , WalletBean.class);
				setAttr("curData", data);
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
	}

	*//**
	 * 功能:响应添加的函数
	 *//*
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			int id = intParam("id");
			String name = param("name");
			int maxKeyNums = intParam("maxKeyNums");
			BigDecimal balance = decimalParam("btcs");
			int withdraw = intParam("withdraw");
			
			dao.setCoint(coint);
			int res = 0;
			WalletBean data = (WalletBean) Data.GetOne("select * from "+dao.getTableName()+" where name=?", new Object[]{name}, WalletBean.class);
			if (id > 0) {
				if(data != null && data.getWalletId() != id){
					Write("当前名称的钱包已存在。", false, "");
					return;
				}
				res = Data.Update("update "+dao.getTableName()+" set name=?, maxKeyNums=?,btcs=?,withdraw=?  where walletId=?", new Object[]{name,maxKeyNums, balance, withdraw, id});
			} else {
				if(data != null){
					Write("当前名称的钱包已存在。", false, "");
					return;
				}
				res = Data.Insert("insert into "+dao.getTableName()+" (name , keysNumber , btcs , createDate , maxKeyNums,withdraw) values(?,?,?,?,?,?)", new Object[]{name , 0 , balance , now() , maxKeyNums, withdraw});
			}
			if (res > 0) {
				Write("操作成功", true, "");
				return;
			}
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
		}
		Write("未知错误导致添加失败！", false, "");
	}
}

*/