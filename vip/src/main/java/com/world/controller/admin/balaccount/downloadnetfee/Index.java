package com.world.controller.admin.balaccount.downloadnetfee;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.balaccount.dao.DownloadAccountDao;
import com.world.model.balaccount.entity.DownloadAccountBean;
import com.world.model.entity.coin.CoinProps;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.UserAction;

/**
 * <p>标题: 提现网络费对账-交易平台VS支付中心</p>
 * <p>描述: 提现网络费对账-交易平台VS支付中心</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
//@FunctionAction(jspPath = "/admins/balaccount/downloadnetfee/", des = "提现网络费对账-支付中心")
public class Index extends UserAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DownloadAccountDao downloadAccountDao = new DownloadAccountDao();
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
			
			log.info("fundsType = " + fundsType + ", dealStauts = " + dealStauts);
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
			Query<DownloadAccountBean> query = downloadAccountDao.getQuery();
			/*拼接查询条件,充值记录对账表*/
			String sqlFinAccWhere = "";
			/*拼接查询条件,充值记录表*/
			String sqlDetailsWhere = "";
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
			/*full查询left,right联合使用*/
			sql = "select ffa.*, (bgTxAmount - pmTxAmount) amountFlag "
				+ "from (select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
				+ "bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
				+ "(select * from finAccDownload where fundsType = " + fundsType + sqlFinAccWhere + " )fa "
				+ "left join (select addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
				+ "from " + fundTypeName + "download where status >= 0" + sqlDetailsWhere + ") fb "
				+ "on fa.addHash = fb.bgId union "
				+ "select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
				+ "bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
				+ "(select * from finAccDownload where fundsType = " + fundsType + sqlFinAccWhere + ")fa "
				+ "right join (select addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
				+ "from " + fundTypeName + "download where status >= 0" + sqlDetailsWhere + ") fb "
				+ "on fa.addHash = fb.bgId) ffa order by bgid, bgBlockHeight asc ";
			log.info("sql = " + sql);
			query.setSql(sql);
			query.setCls(DownloadAccountBean.class);
			
			
			int total = query.count();
			if(total > 0){
				/*分页查询*/
				List<DownloadAccountBean> listDownloadAccount = downloadAccountDao.findPage(currentPage, 20);
				setAttr("listDownloadAccount", listDownloadAccount);
			}
			setPaging(total, currentPage , 20);
			
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
	public void exportDownloadAccount(){
		int fundsType = intParam("fundsType");
		/*货币属性类*/
		CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
		String fundTypeName = coinProps.getDatabaseKey();
		log.info("exportDetailAccount, fundsType = " + fundsType + ", fundTypeName = " + fundTypeName);
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<DownloadAccountBean> listDownloadAccount = getDetailAccountList();
			if(null == listDownloadAccount || 0 == listDownloadAccount.size()) {
				return;
			}
			
			String [] column = {"bgId", "bgTxAmount", "strBgStatus", "strBgBlockHeight", "bgConfigTime", "pmId", "pmTxAmount", "strPmStatus", "strPmBlockHeight", "pmConfigTime", "strAmountFlag"};
			String [] tabHead = {"交易平台流水号", "提现金额", "状态", "区块高度", "确认时间", "支付中心流水号", "提现金额", "状态", "区块高度", "确认时间", "是否一致"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(listDownloadAccount, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_" + fundTypeName + "_listDownloadAccount.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<DownloadAccountBean> getDetailAccountList(){
		/*查询条件*/
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		int fundsType = intParam("fundsType");
		int dealStauts = intParam("dealStauts");
		int startBlockHeight = intParam("startBlockHeight");
		int endBlockHeight = intParam("endBlockHeight");
		
		log.info("fundsType = " + fundsType + ", dealStauts = " + dealStauts);
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
		Query<DownloadAccountBean> query = downloadAccountDao.getQuery();
		/*拼接查询条件,充值记录对账表*/
		String sqlFinAccWhere = "";
		/*拼接查询条件,充值记录表*/
		String sqlDetailsWhere = "";
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
		
		/*full查询left,right联合使用*/
		sql = "select pmId, pmTxAmount, "
			+ "case when pmStatus = 1 then '失败' when pmStatus = 2 then '成功' else '' end strPmStatus, "
			+ "case when pmBlockHeight > 0 then pmBlockHeight else '' end strPmBlockHeight, "
			+ "pmConfigTime, bgId, bgTxAmount, "
			+ "case when bgStatus = 1 then '失败' when bgStatus = 2 then '成功' else '' end strBgStatus, "
			+ "case when bgBlockHeight > 0 then bgBlockHeight else '' end strBgBlockHeight, "
			+ "bgConfigTime, "
			+ "case when (bgTxAmount - pmTxAmount = 0 and pmStatus = bgStatus) then '一致' else '不一致' end strAmountFlag "
			+ "from (select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
			+ "bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
			+ "(select * from finAccDownload where fundsType = " + fundsType + sqlFinAccWhere + " )fa "
			+ "left join (select addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
			+ "from " + fundTypeName + "download where status >= 0" + sqlDetailsWhere + ") fb "
			+ "on fa.addHash = fb.bgId union "
			+ "select addHash pmId, txAmount pmTxAmount, status pmStatus, blockHeight pmBlockHeight, configTime pmConfigTime, "
			+ "bgId, bgTxAmount, bgStatus, bgBlockHeight, bgConfigTime from "
			+ "(select * from finAccDownload where fundsType = " + fundsType + sqlFinAccWhere + ")fa "
			+ "right join (select addHash bgId, amount bgTxAmount, status bgStatus, blockHeight bgBlockHeight, configTime bgConfigTime "
			+ "from " + fundTypeName + "download where status >= 0" + sqlDetailsWhere + ") fb "
			+ "on fa.addHash = fb.bgId) ffa order by bgid, bgBlockHeight asc ";
		log.info("sql = " + sql);
		query.setSql(sql);
		query.setCls(DownloadAccountBean.class);
		
		int total = query.count();
		if(total > 0){
			List<DownloadAccountBean> listDownloadAccount = query.getList();
			
			return listDownloadAccount;
		}
		return null;
	}
}