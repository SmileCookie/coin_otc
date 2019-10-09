package com.world.controller.admin.balaccount.detailaccount;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.balaccount.dao.DetailAccountDao;
import com.world.model.balaccount.entity.DetailAccountBean;
import com.world.model.entity.coin.CoinProps;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

/**
 * <p>标题: 充值对账-交易平台VS支付中心</p>
 * <p>描述: 充值对账-交易平台VS支付中心</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
@FunctionAction(jspPath = "/admins/balaccount/detailaccount/", des = "充值对账-支付中心")
public class Index extends UserAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DetailAccountDao detailAccountDao = new DetailAccountDao();
	/* 查询sql*/
	private String sql = "";

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		try {
			/*查询条件*/
			int currentPage = intParam("page");
			Timestamp startTime = dateParam("startDate");
			Timestamp endTime = dateParam("endDate");
			int fundsType = intParam("fundsType");
			int dealStauts = intParam("dealStauts");
			int startBlockHeight = intParam("startBlockHeight");
			int endBlockHeight = intParam("endBlockHeight");
			int balanceFlag = intParam("balanceFlag");
			long detailsId = longParam("detailsId");
			
			log.info("fundsType = " + fundsType + ", dealStauts = " + dealStauts + ", balanceFlag = " + balanceFlag);
			log.info("startTime = " + startTime + ", endTime = " + endTime);
			log.info("startBlockHeight = " + startBlockHeight + ", endBlockHeight = " + endBlockHeight);
			/*先赋值下拉币种选择框*/
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
			if(currentPage < 1) {
				return;
			}
			/*用于查询不同币种的details表*/
			String fundTypeName = "btc";
			if(0 == fundsType) {
				/*默认比特币先*/
				fundsType = 2;
			}
			/*货币属性类*/
			CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
			fundTypeName = coinProps.getDatabaseKey();
			/*设置查询*/
			Query<DetailAccountBean> query = detailAccountDao.getQuery();
			/*拼接查询条件,充值记录对账表*/
			String sqlFinAccWhere = "";
			/*拼接查询条件,充值记录表*/
			String sqlDetailsWhere = "";
			/*单独处理是否平衡*/
			String sqlBalanceFlag = " where 1=1 ";
			/*处理状态*/
			if(dealStauts > 0) {
				sqlFinAccWhere += " and status = " + dealStauts + "";
				sqlDetailsWhere += " and status = " + dealStauts + "";
			}
			/*确认时间*/
			if(null != startTime) {
				sqlFinAccWhere += " and configTime >= '" + startTime + "'";
				sqlDetailsWhere += " and configTime >= '" + startTime + "'";
			}
			if(null != endTime) {
				String strEndTime = endTime.toString();
				strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
				endTime = Timestamp.valueOf(strEndTime);
				sqlFinAccWhere += " and configTime <= '" + endTime + "'";
				sqlDetailsWhere += " and configTime <= '" + endTime + "'";
			}
			/*区块高度*/
			if(startBlockHeight > 0) {
				sqlFinAccWhere += " and blockHeight >= " + startBlockHeight + "";
				sqlDetailsWhere += " and blockHeight >= " + startBlockHeight + "";
			}
			if(endBlockHeight > 0) {
				sqlFinAccWhere += " and blockHeight <= " + endBlockHeight + "";
				sqlDetailsWhere += " and blockHeight <= " + endBlockHeight + "";
			}
			if(detailsId > 0) {
				sqlBalanceFlag += " and bgDetailsId = " + detailsId + " ";
			}
			if(balanceFlag > 0) {
				if(1 == balanceFlag) {
					sqlBalanceFlag += " and (ffa.bgTxAmount - round(ffa.pmTxAmount, 9)) = 0 and ffa.pmStatus = ffa.bgStatus";
				} else if (2 == balanceFlag) {
					sqlBalanceFlag += " and ((bgTxAmount is null and pmTxAmount is not null) "
								   + "or (bgTxAmount is not null and pmTxAmount is null) "
								   + "or (pmStatus is null and bgStatus is not null) "
								   + "or (pmStatus is not null and bgStatus is null) "
								   + "or (bgTxAmount - round(pmTxAmount, 9)) != 0 "
								   + "or pmStatus != bgStatus)";
				}
			}
			//20170827 xzhang 新增detailsid查询及展示字段
			/*full查询left,right联合使用:finAccDetails addHash = details addHash*/
			sql = "select ffa.*, (bgTxAmount - round(pmTxAmount, 9)) amountFlag "
				+ "from (select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
				+ "bgDetailsId,bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
				+ "(select * from finAccDetails where fundsType = " + fundsType + sqlFinAccWhere + " )fa "
				+ "left join (select detailsId bgDetailsId,addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
				+ "from " + fundTypeName + "details where status > 0" + sqlDetailsWhere + ") fb "
				+ "on fa.addHash = fb.bgId union "
				+ "select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
				+ "bgDetailsId,bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
				+ "(select * from finAccDetails where fundsType = " + fundsType + sqlFinAccWhere + ")fa "
				+ "right join (select detailsId bgDetailsId,addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
				+ "from " + fundTypeName + "details where status > 0" + sqlDetailsWhere + ") fb "
				+ "on fa.addHash = fb.bgId) ffa " + sqlBalanceFlag + " order by bgBlockHeight desc, bgid desc ";
			log.info("sql = " + sql);
			query.setSql(sql);
			query.setCls(DetailAccountBean.class);
			//(ffa.bgTxAmount - ffa.pmTxAmount) = 0 and ffa.pmStatus = ffa.bgStatus 
//			<div style="text-align: left;">
//			<c:if test="${detailAccount.amountFlag==0&&detailAccount.pmStatus==detailAccount.bgStatus}">
//				<font>一致</font>
//			</c:if>
//			<c:if test="${detailAccount.amountFlag!=0||detailAccount.pmStatus!=detailAccount.bgStatus}"><font color="red">不一致</font></c:if>
//			</div>
			
			int total = query.count();
			if(total > 0){
				/*分页查询*/
				List<DetailAccountBean> listDetailAccount = detailAccountDao.findPage(currentPage, 20);
				setAttr("listDetailAccount", listDetailAccount);
			}
			setPaging(total, currentPage, 20);
			setAttr("itemCount",total);
//			setAttr("types", EnumUtils.getAll(BillType.class));
//			setAttr("curUser", loginUser);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = "")
	public void exportDetailAccount(){
		log.info("exportDetailAccount...");
		int fundsType = intParam("fundsType");
		/*货币属性类*/
		CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
		String fundTypeName = coinProps.getDatabaseKey();
		log.info("exportDetailAccount, fundsType = " + fundsType + ", fundTypeName = " + fundTypeName);
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<DetailAccountBean> listDetailAccount = getDetailAccountList();
			if(null == listDetailAccount || 0 == listDetailAccount.size()) {
				return;
			}
			
			String [] column = {"bgDetailsId","bgId", "bgTxAmount", "strBgStatus", "strBgBlockHeight", "pmId", "pmTxAmount", "strPmStatus", "strPmBlockHeight", "pmConfigTime", "strAmountFlag"};
			String [] tabHead = {"充值编号","交易平台流水号", "充值金额", "状态", "区块高度", "支付中心流水号", "充值金额", "状态", "区块高度", "确认时间", "是否一致"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(listDetailAccount, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_" + fundTypeName + "_listDetailAccount.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<DetailAccountBean> getDetailAccountList(){
		/*查询条件*/
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		int fundsType = intParam("fundsType");
		int dealStauts = intParam("dealStauts");
		int startBlockHeight = intParam("startBlockHeight");
		int endBlockHeight = intParam("endBlockHeight");
		int balanceFlag = intParam("balanceFlag");
		long detailsId = longParam("detailsId");
		
		log.info("fundsType = " + fundsType + ", dealStauts = " + dealStauts + ", balanceFlag = " + balanceFlag);
		log.info("startTime = " + startTime + ", endTime = " + endTime);
		log.info("startBlockHeight = " + startBlockHeight + ", endBlockHeight = " + endBlockHeight);
		/*用于查询不同币种的details表*/
		String fundTypeName = "btc";
		if(0 == fundsType) {
			/*默认比特币先*/
			fundsType = 2;
		}
		/*货币属性类*/
		CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
		fundTypeName = coinProps.getDatabaseKey();
		/*设置查询*/
		Query<DetailAccountBean> query = detailAccountDao.getQuery();
		/*拼接查询条件,充值记录对账表*/
		String sqlFinAccWhere = "";
		/*拼接查询条件,充值记录表*/
		String sqlDetailsWhere = "";
		/*单独处理是否平衡*/
		String sqlBalanceFlag = " where 1=1 ";
		/*处理状态*/
		if(dealStauts > 0) {
			sqlFinAccWhere += " and status = " + dealStauts + "";
			sqlDetailsWhere += " and status = " + dealStauts + "";
		}
		/*确认时间*/
		if(null != startTime) {
			sqlFinAccWhere += " and configTime >= '" + startTime + "'";
			sqlDetailsWhere += " and configTime >= '" + startTime + "'";
		}
		if(null != endTime) {
			String strEndTime = endTime.toString();
			strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
			endTime = Timestamp.valueOf(strEndTime);
			sqlFinAccWhere += " and configTime <= '" + endTime + "'";
			sqlDetailsWhere += " and configTime <= '" + endTime + "'";
		}
		/*区块高度*/
		if(startBlockHeight > 0) {
			sqlFinAccWhere += " and blockHeight >= " + startBlockHeight + "";
			sqlDetailsWhere += " and blockHeight >= " + startBlockHeight + "";
		}
		if(endBlockHeight > 0) {
			sqlFinAccWhere += " and blockHeight <= " + endBlockHeight + "";
			sqlDetailsWhere += " and blockHeight <= " + endBlockHeight + "";
		}
		if(detailsId > 0) {
			sqlBalanceFlag += " and bgDetailsId = " + detailsId + " ";
		}
		if(balanceFlag > 0) {
			if(1 == balanceFlag) {
				sqlBalanceFlag += " and (ffa.bgTxAmount - round(ffa.pmTxAmount, 9)) = 0 and ffa.pmStatus = ffa.bgStatus ";
			} else if (2 == balanceFlag) {
				sqlBalanceFlag += " and ((bgTxAmount is null and pmTxAmount is not null) "
							   + "or (bgTxAmount is not null and pmTxAmount is null) "
							   + "or (pmStatus is null and bgStatus is not null) "
							   + "or (pmStatus is not null and bgStatus is null) "
							   + "or (bgTxAmount - round(pmTxAmount, 9)) != 0 "
							   + "or pmStatus != bgStatus) ";
			}
		}
		/*full查询left,right联合使用*/
		sql = "select bgDetailsId,pmId, pmTxAmount, "
			+ "case when pmStatus = 1 then '失败' when pmStatus = 2 then '成功' else '' end strPmStatus, "
			+ "case when pmBlockHeight > 0 then pmBlockHeight else '' end strPmBlockHeight, "
			+ "pmConfigTime, bgId, bgTxAmount, "
			+ "case when bgStatus = 1 then '失败' when bgStatus = 2 then '成功' else '' end strBgStatus, "
			+ "case when bgBlockHeight > 0 then bgBlockHeight else '' end strBgBlockHeight, "
			+ "bgConfigTime, "
			+ "case when (bgTxAmount - round(pmTxAmount, 9) = 0 and pmStatus = bgStatus) then '一致' else '不一致' end strAmountFlag "
			+ "from (select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
			+ "bgDetailsId,bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
			+ "(select * from finAccDetails where fundsType = " + fundsType + sqlFinAccWhere + " )fa "
			+ "left join (select detailsId bgDetailsId,addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
			+ "from " + fundTypeName + "details where status >= 0" + sqlDetailsWhere + ") fb "
			+ "on fa.addHash = fb.bgId union "
			+ "select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
			+ "bgDetailsId,bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
			+ "(select * from finAccDetails where fundsType = " + fundsType + sqlFinAccWhere + ")fa "
			+ "right join (select detailsId bgDetailsId,addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
			+ "from " + fundTypeName + "details where status >= 0" + sqlDetailsWhere + ") fb "
			+ "on fa.addHash = fb.bgId) ffa " + sqlBalanceFlag + " order by bgBlockHeight desc, bgid desc ";
		log.info("sql = " + sql);
		query.setSql(sql);
		query.setCls(DetailAccountBean.class);
		
		int total = query.count();
		if(total > 0){
			List<DetailAccountBean> listDetailAccount = query.getList();
			
			return listDetailAccount;
		}
		return null;
	}
	
}