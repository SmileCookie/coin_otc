package com.world.controller.manage.account;


import com.api.config.ApiConfig;
import com.messi.user.core.FeignContainer;
import com.messi.user.feign.DowoloadApiService;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.DownloadSummaryDao;
import com.world.model.dao.pay.FreezDao;
import com.world.model.entity.AuditStatus;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.DownloadSummaryBean;
import com.world.model.entity.user.UserContact;
import com.world.util.Message;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.ApproveAction;
import com.world.web.convention.annotation.FunctionAction;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

@SuppressWarnings("serial")
@FunctionAction(jspPath = "/cn/manage/account/downRecord/", des = "提现记录")
public class DownRecord extends ApproveAction{

	FreezDao freezDao = new FreezDao();
	DownloadDao drDao = new DownloadDao();
	DownloadSummaryDao downloadSummaryDao = new DownloadSummaryDao();

	//Close By suxinjie 一期屏蔽该功能
	//@Page(Viewer="/cn/manage/account/downRecord/list.jsp")
	public void index(){
		ajax();
	}

	//Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer="/cn/manage/account/downRecord/listAjax.jsp")
    public void ajax(){
    	try{
			String userId =userIdStr();
			int pageSize = 10;
			String currentTab = param("tab");
			int currentPage = intParam("page");
			int pagesize = intParam("pageSize");
			String title = param("title");
			boolean isPage = param("isPage")==""?true:false;
			Timestamp startTime = dateParam("startDate");
			Timestamp endTime = dateParam("endDate");
			
			drDao.setCoint(coint);
			Query<DownloadBean> query = drDao.getQuery();
			query.setSql("select * from "+drDao.getTableName());
			query.setCls(DownloadBean.class);
			
			query.append(" userId='" + userId + "' and isDel=0 ");
			
			if(currentTab.length()==0){
				currentTab="all";
			}
			try{
				if(pagesize > 0)
					pageSize = pagesize;
			}catch (NumberFormatException e) {
				log.error("内部异常", e);
			}
			
			request.setAttribute("currentTab", currentTab);
			request.setAttribute("currentPage", currentPage);
			   
			if(currentTab.equals("processing")){
				query.append(" and status=0");
			}else if(currentTab.equals("faild")){
				query.append(" and status=1");
			}else if(currentTab.equals("successful")){
				query.append(" and status=2");
			}else if(currentTab.equals("cancel")){
				query.append(" and status=3");
			}
			
			if(startTime != null){
				query.append(" and submitTime>=cast('"+startTime+"' as datetime)");
			}
			if(endTime != null){
				query.append(" and submitTime<=cast('"+endTime+"' as datetime)");
			}
			
			if(title.length()>0)
				query.append(" and remark like '%"+title.replace('\'', ' ').replace('"', ' ')+"%'");
			
			int total = query.count();
			if(total > 0){
				query.append("order by id desc");
				//分页查询
				List<DownloadBean> downloads = drDao.findPage(currentPage, pageSize);
				
				request.setAttribute("dataList", downloads);
			}
			
			if (isPage)
				setPaging(total, currentPage, pageSize);
				
		}catch (Exception e) {
			log.error("内部异常", e);
		}
    }

	//Close By suxinjie 一期屏蔽该功能
    //@Page(Viewer = "")
	public void exportUser(){
		try {
			List<Bean> needUser = getUserList();
			if(needUser == null){
				return;
			}
				
			String [] column = {"submitTime","toAddress","amount","afterAmount","statusT"};
			String [] tabHead = {"时间","地址","金额","实到","状态"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_download_details.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<Bean> getUserList(){
		String userId =userIdStr();
		String currentTab = param("tab");
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		
		drDao.setCoint(coint);
		Query query = drDao.getQuery();
		query.setSql("select * from "+drDao.getTableName());
		query.setCls(DownloadBean.class);
		
		query.append(" userId='" + userId + "' and isDel=0 ");
		
		if(currentTab.length()==0){
			currentTab="all";
		}
		
		if(currentTab.equals("processing")){
			query.append(" and status=0");
		}else if(currentTab.equals("faild")){
			query.append(" and status=1");
		}else if(currentTab.equals("successful")){
			query.append(" and status=2");
		}else if(currentTab.equals("cancel")){
			query.append(" and status=3");
		}
		
		if(startTime != null){
			query.append(" and submitTime>=cast('"+startTime+"' as datetime)");
		}
		if(endTime != null){
			query.append(" and submitTime<=cast('"+endTime+"' as datetime)");
		}
		
		int total = query.count();
		if(total > 0){
			query.append("order by id desc");
			List<Bean> billDetails = query.getList();
			
			return billDetails;
		}
		return null;
	}
    
    /**
	 * 取消提现
	 */
	@Page(Viewer = ".xml")
	public void confirmCancel(){
		try{
			//取消开始
//			String userId = userIdStr();
//			long did = longParam("did");
//			Message msg = drDao.doCancelCash(userId, did, ip(), coint);
//			if(msg.isSuc()){
//				WriteRight(L(msg.getMsg()));
//			}else{
//				WriteError(L(msg.getMsg()));
//			}
			long did = longParam("did");
			DownloadSummaryBean downloadBean = (DownloadSummaryBean) downloadSummaryDao.getOne(did);
			if(downloadBean.getStatus() == 0 && downloadBean.getCommandId()> 0){
				WriteRight(L("该笔提现已确认，不可取消"));
				return;
			}
			if("VDS生态回馈提现".equals(downloadBean.getRemark())){
				WriteRight(L("该笔提现为VDS生态回馈提现，不可取消"));
				return;
			}
			FeignContainer container = new FeignContainer(ApiConfig.getValue("usecenter.url")+"/download");
			DowoloadApiService dowoloadApiService = container.getFeignClient(DowoloadApiService.class);
			Boolean flag = dowoloadApiService.cancelDownload(userIdStr(),did,coint.getFundsType(),1);
			if(flag){
				WriteRight(L("操作成功。"));
			}else{
				WriteError(L("操作异常。"));
			}
			return;
		}catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	/**
	 * 申请取消时填写手机验证码
	 *
	 * Close By suxinjie 一期屏蔽该功能
	 */
//	@Page(Viewer = "/cn/manage/account/downRecord/win.jsp")
	public void win(){
		initLoginUser();
		coinProps();
		UserContact uc = loginUser.getUserContact();
		String did = param("did");
		setAttr("did",did);
		
		if (uc.getGoogleAu() == AuditStatus.pass.getKey()) {
			setAttr("googleAuth", true);
		}else{
			setAttr("googleAuth", false);
		}
		
	}

	/**
	 * 获取 提现记录
	 */
	@Page(Viewer = JSON)
	public void getDownloadRecordList(){
		try{
			String userId =userIdStr();
			
			
			int pageIndex = intParam("pageIndex");
			int pageSize = intParam("pageSize");
			
			String title = param("title");
			Timestamp startTime = dateParam("startDate");
			Timestamp endTime = dateParam("endDate");


			drDao.setCoint(coint);
			
			Query<DownloadBean> query = drDao.getQuery();
			query.setSql("select * from "+drDao.getTableName());
			query.setCls(DownloadBean.class);
			
			query.append(" userId='" + userId + "' and isDel=0 ");

			
			if(startTime != null){
				query.append(" and submitTime>=cast('"+startTime+"' as datetime)");
			}
			if(endTime != null){
				query.append(" and submitTime<=cast('"+endTime+"' as datetime)");
			}
			
			if(title.length()>0)
				query.append(" and remark like '%"+title.replace('\'', ' ').replace('"', ' ')+"%'");
			
			int total = query.count();
			List<Map<String,Object>>  list = new ArrayList<Map<String,Object>>();
			if(total > 0){
				query.append("order by id desc");
				//分页查询
				List<DownloadBean> downloads = drDao.findPage(pageIndex, pageSize);
				for(DownloadBean download:downloads){
					Map<String,Object> downloadMap = new HashMap<String,Object>();
					downloadMap.put("id", download.getId());
					downloadMap.put("submitTime", download.getSubmitTime());
					downloadMap.put("coinName", coint.getPropTag());
					downloadMap.put("amount", download.getAmount());
					downloadMap.put("status", download.getStatus());
					downloadMap.put("confirmTime", download.getManageTime());
					downloadMap.put("toAddress", download.getToAddress());
					downloadMap.put("afterAmount", download.getAfterAmount());
					downloadMap.put("commandId", download.getCommandId());
                    downloadMap.put("txId", download.getTxId());
                    downloadMap.put("webUrl", coint.getWeb() + download.getTxId());
                    downloadMap.put("memo", download.getMemo());
                    downloadMap.put("addressMemo", download.getAddressMemo());

					list.add(downloadMap);
				}
				downloads.clear();
				
			}
			
			//if (isPage)
			//	setPaging(total, currentPage, pageSize);
			
			Map<String, Object> page = new HashMap<String, Object>();
			page.put("pageIndex", pageIndex);
			page.put("totalCount", total);
			page.put("list", list);
			json("", true, JSONObject.fromObject(page).toString());
				
		}catch (Exception e) {
			log.error("内部异常", e);
			json("fail", false, "");
		}
	}
	
	
	
}

