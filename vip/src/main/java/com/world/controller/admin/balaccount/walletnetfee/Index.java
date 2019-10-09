package com.world.controller.admin.balaccount.walletnetfee;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.balaccount.dao.FinAccWalletNetFeeDao;
import com.world.model.balaccount.entity.FinAccWalletNetFeeBean;
import com.world.model.entity.coin.CoinProps;
import com.world.util.CommonUtilNoStatic;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>标题: 网络费查询-支付中心</p>
 * <p>描述: 网络费查询-支付中心</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version 
 */
@FunctionAction(jspPath = "/admins/balaccount/walletnetfee/", des = "网络费查询-支付中心")
public class Index extends UserAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FinAccWalletNetFeeDao walletNetFeeDao = new FinAccWalletNetFeeDao();
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
			int walType = intParam("walType");
			String walId = getByParam("walId");
			int startBlockHeight = intParam("startBlockHeight");
			int endBlockHeight = intParam("endBlockHeight");
			
			log.info("fundsType = " + fundsType + ", walType = " + walType + ", walId = " + walId);
			log.info("startTime = " + startTime + ", endTime = " + endTime);
			log.info("startBlockHeight = " + startBlockHeight + ", endBlockHeight = " + endBlockHeight);
			/*先赋值下拉币种选择框*/
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
			if(currentPage < 1) {
				return;
			}
			/*设置查询*/
			Query<FinAccWalletNetFeeBean> query = walletNetFeeDao.getQuery();
			/*拼接查询条件*/
			String sqlWhere = "";
			/*是否已经有where如果有则拼接and*/
			Map<String, Boolean> mapHasWhere = new HashMap<String, Boolean>();
			mapHasWhere.put("hasWhere", false);
			
			/*拼接SQL语句:where 和 and 组合*/
			CommonUtilNoStatic commonUtilNoStatic = new CommonUtilNoStatic();
			if(fundsType > 0) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "fundsType", fundsType, "=");
			}
			/*钱包类型*/
			if(walType > 0) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "walType", walType, "=");
			}
			/*钱包编号*/
			if(null != walId && !"".equals(walId)) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByStr(sqlWhere, mapHasWhere, "walId", walId, "like");
			}
			/*确认时间*/
			if(null != startTime) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", startTime, ">=");
			}
			if(null != endTime) {
				String strEndTime = endTime.toString();
				strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
				endTime = Timestamp.valueOf(strEndTime);
				sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", endTime, "<=");
			}
			/*区块高度*/
			if(startBlockHeight > 0) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", startBlockHeight, ">=");
			}
			if(endBlockHeight > 0) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", endBlockHeight, "<=");
			}
			/*sql查询:walType 1：充值热钱包，2：提现热钱包，3：冷钱包*/
			sql = "select distinct txId, walId, walName, "
					+ "case when walType = 4 then '热冲到冷' when walType = 2 then '热提到用户' when walType = 3 then '冷到热提' when walType = 7 then '冷到其他' else '' end walType, "
					+ "txAmount, netCost, blockHeight, configTime from finAccNetFeeRecord " + sqlWhere + " order by fundsType asc, blockHeight desc, txId desc ";

			log.info("sql = " + sql);
			query.setSql(sql);
			query.setCls(FinAccWalletNetFeeBean.class);
			/*总条数*/
			int total = query.count();
			if(total > 0){
				/*分页查询*/
				List<FinAccWalletNetFeeBean> listWalletNetFee = walletNetFeeDao.findPage(currentPage, 20);
				setAttr("listWalletNetFee", listWalletNetFee);
			}
			setPaging(total, currentPage, 20);
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	@Page(Viewer = "")
	public void exportWalletnetFee(){
		log.info("exportWalletnetFee...");
		int fundsType = intParam("fundsType");
		String fundTypeName = "all";
		/*货币属性类*/
		if(fundsType > 0) {
			CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
			fundTypeName = coinProps.getDatabaseKey();
		}
		log.info("exportWalletnetFee, fundsType = " + fundsType + ", fundTypeName = " + fundTypeName);
		try {
			if(!codeCorrect(XML)){
				return;
			}
			List<FinAccWalletNetFeeBean> listWalletnetFee = getWalletnetFeeList();
			if(null == listWalletnetFee || 0 == listWalletnetFee.size()) {
				return;
			}
			
			String [] column = {"txId", "walId", "walName", "walType", "txAmount", "netCost", "blockHeight", "configTime"};
			String [] tabHead = {"交易流水号", "钱包编号", "钱包名称", "钱包类型", "交易金额", "网络费", "区块高度", "确认时间"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(listWalletnetFee, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_" + fundTypeName + "_listWalletnetFee.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	
	public List<FinAccWalletNetFeeBean> getWalletnetFeeList(){
		/*查询条件*/
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		int fundsType = intParam("fundsType");
		int walType = intParam("walType");
		String walId = getByParam("walId");
		int startBlockHeight = intParam("startBlockHeight");
		int endBlockHeight = intParam("endBlockHeight");
		
		log.info("fundsType = " + fundsType + ", walType = " + walType + ", walId = " + walId);
		log.info("startTime = " + startTime + ", endTime = " + endTime);
		log.info("startBlockHeight = " + startBlockHeight + ", endBlockHeight = " + endBlockHeight);
		
		/*设置查询*/
		Query<FinAccWalletNetFeeBean> query = walletNetFeeDao.getQuery();
		/*拼接查询条件*/
		String sqlWhere = "";
		/*是否已经有where如果有则拼接and*/
		Map<String, Boolean> mapHasWhere = new HashMap<String, Boolean>();
		mapHasWhere.put("hasWhere", false);
		
		/*拼接SQL语句:where 和 and 组合*/
		CommonUtilNoStatic commonUtilNoStatic = new CommonUtilNoStatic();
		if(fundsType > 0) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "fundsType", fundsType, "=");
		}
		/*钱包类型*/
		if(walType > 0) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "walType", walType, "=");
		}
		/*钱包编号*/
		if(null != walId && !"".equals(walId)) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByStr(sqlWhere, mapHasWhere, "walId", walId, "like");
		}
		/*确认时间*/
		if(null != startTime) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", startTime, ">=");
		}
		if(null != endTime) {
			String strEndTime = endTime.toString();
			strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
			endTime = Timestamp.valueOf(strEndTime);
			sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", endTime, "<=");
		}
		/*区块高度*/
		if(startBlockHeight > 0) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", startBlockHeight, ">=");
		}
		if(endBlockHeight > 0) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", endBlockHeight, "<=");
		}
		/*sql查询:walType 1：充值热钱包，2：提现热钱包，3：冷钱包*/
		sql = "select txId, walId, walName, "
			+ "case when walType = 1 then '充值热钱包' when walType = 1 then '提现热钱包' when walType = 3 then '冷钱包' else '' end walType, "
			+ "txAmount, netCost, blockHeight, configTime from finAccNetFeeRecord " + sqlWhere + " order by fundsType asc, blockHeight desc, txId desc ";
		log.info("sql = " + sql);
		query.setSql(sql);
		query.setCls(FinAccWalletNetFeeBean.class);
		
		int total = query.count();
		if(total > 0){
			List<FinAccWalletNetFeeBean> listWalletnetFee = query.getList();
			
			return listWalletnetFee;
		}
		return null;
	}
}