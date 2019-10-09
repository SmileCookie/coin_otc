package com.world.controller.manage.account;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.dao.pay.FreezDao;
import com.world.model.entity.pay.FreezeBean;
import com.world.web.Page;
import com.world.web.action.UserAction;

@SuppressWarnings("serial")
public class FreezeDetails extends UserAction{
	
	FreezDao freezDao = new FreezDao();

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer="/cn/manage/account/freeze/list.jsp")
	public void Index(){  
		try{
			initLoginUser();
			String userId =userIdStr();
			initLoginUser();
			int pageSize = 10;
			
			String currentTab = param("tab");
			int currentPage = intParam("page");
			String searchKey = param("searchKey");
			double sMoney= doubleParam("sMoney");
			double lMoney= doubleParam("lMoney");
			Timestamp startTime = dateParam("startDate");
			Timestamp endTime = dateParam("endDate");
			
			if(currentTab.length()==0)
				currentTab="all";
			
			request.setAttribute("currentTab", currentTab);
			request.setAttribute("currentPage", currentPage);
			
			freezDao.setCoint(coint);
			Query<FreezeBean> query = freezDao.getQuery();
			query.setSql("select * from "+freezDao.getTableName());
			query.setCls(FreezeBean.class);
		
			query.append(" userId='"+userId+"' ");
			if(searchKey!=null&&!"".equals(searchKey.trim())){
				try {
					searchKey=URLDecoder.decode(searchKey,"UTF-8");
					query.append(" and reMark like '%"+searchKey.replace('\'', ' ').replace('"', ' ')+"%'");
				} catch (UnsupportedEncodingException e) {
					log.error("内部异常", e);
				}
			}
			if(startTime!=null){
				
				query.append(" and freezeTime>=cast('"+startTime+"' as datetime)");
			}
			if(endTime!=null){
				query.append(" and freezeTime<=cast('"+endTime+"' as datetime)");
			}
			
			if(sMoney > 0){
				query.append(" and btcNumber>="+(sMoney));
			}
			
			if(lMoney > 0){
				query.append(" and btcNumber<="+lMoney);
			}
			
			if(!currentTab.equals("all")){
				if(currentTab.equals("freezing")){
					query.append(" and statu=0 ");
				}else if(currentTab.equals("relieved")){
					query.append(" and statu=1 ");
				}else if(currentTab.equals("cancled")){
					query.append(" and statu=2 ");
				}
			}
			
			int total = query.count();
			if(total > 0){
				query.append("order by btcFreezeId desc");
				//分页查询
				List<FreezeBean> downloads = freezDao.findPage(currentPage, pageSize);
				
				request.setAttribute("dataList", downloads);
			}
			
			setPaging(total, currentPage, pageSize);
		}catch (Exception e) {
			log.error("内部异常", e);
		}
	 }

	//Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer="/cn/manage/account/freeze/listAjax.jsp")
    public void ajax(){
	   Index(); 
    }
	
	
}

