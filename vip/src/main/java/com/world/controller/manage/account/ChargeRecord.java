package com.world.controller.manage.account;


import com.world.data.mysql.Query;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.KeyDao;
import com.world.model.entity.pay.DetailsBean;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("serial")
public class ChargeRecord extends UserAction{
	KeyDao keyDao = new KeyDao();
	DetailsDao drDao = new DetailsDao();

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer="/cn/manage/account/upRecord/list.jsp")
	public void Index(){
		initLoginUser();
		
		ajax();
	}

	//Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer="/cn/manage/account/upRecord/listAjax.jsp")
    public void ajax(){
    	try{
			String userId =userIdStr();
			int pageSize = 10;
			String currentTab = param("tab");
			int currentPage = intParam("page");
			int pagesize = intParam("pageSize");
			String title = param("title");
			boolean isPage = param("isPage") == "" ? true : false;
			
			if(currentTab.length()==0){
				currentTab="all";
			}
			if(pagesize > 0)
				pageSize = pagesize;
			
			drDao.setCoint(coint);
			Query<DetailsBean> query = drDao.getQuery();
			query.setSql("select * from "+drDao.getTableName());
			query.setCls(DetailsBean.class);
			
			query.append(" userId='" + userId + "' and type=1 ");
			
			request.setAttribute("tab", currentTab);
			request.setAttribute("currentPage", currentPage);
			
			if(currentTab.equals("processing"))
				query.append(" and status=0");
			else if(currentTab.equals("successful"))
				query.append(" and status=2");
			
			if(title.length()>0)
				query.append(" and remark like '%"+title.replace('\'', ' ').replace('"', ' ')+"%'");
			
			int total = query.count();
			if(total > 0){
				query.append("order by detailsId desc");
				//分页查询
				List<DetailsBean> downloads = drDao.findPage(currentPage, pageSize);
				
				request.setAttribute("dataList", downloads);
			}
			
			if(isPage)
				setPaging(total, currentPage, pageSize);
				
		}catch (Exception e) {
			log.error("内部异常", e);
		}
    }
	
	/**
	 * 获取 充值记录
	 */
    @Page(Viewer = JSON)
    public void getChargeRecordList(){
    	try{
			String userId =userIdStr();
			String currentTab = param("tab");
			int pageIndex = intParam("pageIndex");
			int pageSize = intParam("pageSize");
			String title = param("title");
			boolean isPage = param("isPage") == "" ? true : false;


			if(currentTab.length()==0){
				currentTab="all";
			}

			drDao.setCoint(coint);
			Query<DetailsBean> query = drDao.getQuery();
			query.setSql("select * from "+drDao.getTableName());
			query.setCls(DetailsBean.class);
			query.append(" userId='" + userId + "' and type=1 ");


			if(currentTab.equals("processing"))
				query.append(" and status=0");
			else if(currentTab.equals("successful"))
				query.append(" and status=2");
			
			if(title.length()>0)
				query.append(" and remark like '%"+title.replace('\'', ' ').replace('"', ' ')+"%'");
			List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
			int total = query.count();
			if(total > 0){
				query.append("order by detailsId desc");
				//分页查询
				List<DetailsBean> details = drDao.findPage(pageIndex, pageSize);
				for(DetailsBean detail:details){
					Map<String,Object> downloadMap = new HashMap<String,Object>();
					downloadMap.put("id", detail.getDetailsId());
					downloadMap.put("submitTime", detail.getSendTime());
					downloadMap.put("coinName", coint.getPropTag());
					downloadMap.put("amount", detail.getAmount());
					downloadMap.put("showStatus", L(detail.getShowStatu()));
					downloadMap.put("confirmTimes", detail.getConfirmTimes());
					downloadMap.put("toAddress", detail.getToAddr());
					String txId = detail.getAddHash().split("_")[0];
					downloadMap.put("txId", txId);
					downloadMap.put("webUrl", coint.getWeb() + txId);
                    downloadMap.put("totalConfirmTimes", coint.getInConfirmTimes());
                    downloadMap.put("status", detail.getStatus());

					if(detail.getConfigTime() != null){

						downloadMap.put("confirmTime", detail.getConfigTime());
					}

					list.add(downloadMap);
				}
			}
			
			Map<String, Object> page = new HashMap<String, Object>();
			page.put("pageIndex", pageIndex);
			page.put("totalCount", total);
			page.put("list", list);
			json("", true, JSONObject.fromObject(page).toString());
		}catch (Exception e) {
			log.error("内部异常", e);
		}
    }
    
    
}

