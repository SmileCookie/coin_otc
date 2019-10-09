package com.world.controller.admin.balaccount.finaccwaldailyacc;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.balaccount.dao.FinAccWalDailyAccDao;
import com.world.model.balaccount.entity.FinAccWalDailyAccBean;
import com.world.model.entity.coin.CoinProps;
import com.world.util.CommonUtilNoStatic;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;

/**
 * <p>标题: 钱包每日对账-支付中心</p>
 * <p>描述: 钱包每日对账-支付中心</p>
 * <p>版权: Copyright (c) 2017</p>
 * @author flym
 * @version
 */
@FunctionAction(jspPath = "/admins/balaccount/finaccwaldailyacc/", des = "钱包每日对账-支付中心")
public class Index extends UserAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private FinAccWalDailyAccDao finAccWalDailyAccDao = new FinAccWalDailyAccDao();
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
//			int checkState = intParam("checkState");
			int startBlockHeight = intParam("startBlockHeight");
			int endBlockHeight = intParam("endBlockHeight");

			log.info("fundsType = " + fundsType);
			log.info("startTime = " + startTime + ", endTime = " + endTime);
			log.info("startBlockHeight = " + startBlockHeight + ", endBlockHeight = " + endBlockHeight);
			/*先赋值下拉币种选择框*/
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
			if(currentPage < 1) {
				return;
			}
			/*设置查询*/
			Query<FinAccWalDailyAccBean> query = finAccWalDailyAccDao.getQuery();
			/*拼接查询条件*/
			String sqlWhere = "", sqlAnd = "";
			/*是否已经有where如果有则拼接and*/
			Map<String, Boolean> mapHasWhere = new HashMap<String, Boolean>();
			mapHasWhere.put("hasWhere", false);
			
			/*拼接SQL语句:where 和 and 组合*/
			CommonUtilNoStatic commonUtilNoStatic = new CommonUtilNoStatic();
			if(fundsType > 0) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "fundsType", fundsType, "=");
				sqlAnd += "and fundsType = " + fundsType + " ";
			}
			/*确认时间*/
			if(null != startTime) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", startTime, ">=");
				sqlAnd += "and configTime >= '" + startTime + "' ";
			}
			if(null != endTime) {
				String strEndTime = endTime.toString();
				strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
				endTime = Timestamp.valueOf(strEndTime);
				sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", endTime, "<=");
				sqlAnd += "and configTime <= '" + endTime + "' ";
			}
			/*区块高度*/
			if(startBlockHeight > 0) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", startBlockHeight, ">=");
				sqlAnd += "and blockHeight >= " + startBlockHeight + " ";
			}
			if(endBlockHeight > 0) {
				sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", endBlockHeight, "<=");
				sqlAnd += "and blockHeight <= " + endBlockHeight + " ";
			}
			/*sql查询:checkState1：平衡，2：不平衡*/
//			sql = "select fa.fundsType fundsType, abs(f1a.1amount) amount1, abs(f1a.1fee) fee1, abs(f2a.2amount) amount2, abs(f2a.2fee) fee2, "
//				+ "abs(f3a.3amount) amount3, abs(f3a.3fee) fee3, abs(f4a.4amount) amount4, abs(f4a.4fee) fee4, "
//					+"abs(f5a.5amount) amount5, abs(f5a.5fee) fee5, abs(f6a.6amount) amount6, abs(f6a.6fee) fee6, "
//				+"abs(f7a.7amount) amount7, abs(f7a.7fee) fee7, abs(f8a.8amount) amount8, abs(f8a.8fee) fee8, "
//				+ "fma.maxHeight, fma.minHeight, fma.maxTime, fma.minTime "
//				+ "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
//				+ "left join (select fundsType, sum(txAmount) 1amount, sum(fee) 1fee from finAccWalletBill "
//				+ "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
//				+ "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
//				+ "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
//				+ "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
//				//xzhang 2017.08.21 新增四种类型
//				+ "left join (select fundsType, sum(txAmount) 5amount, sum(fee) 5fee from finAccWalletBill "
//				+ "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 6amount, sum(fee) 6fee from finAccWalletBill "
//				+ "where dealType = 6 " + sqlAnd + " group by fundsType ) f6a on fa.fundsType = f6a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 7amount, sum(fee) 7fee from finAccWalletBill "
//				+ "where dealType = 7 " + sqlAnd + " group by fundsType ) f7a on fa.fundsType = f7a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 8amount, sum(fee) 8fee from finAccWalletBill "
//				+ "where dealType = 8 " + sqlAnd + " group by fundsType ) f8a on fa.fundsType = f8a.fundsType "
//				+ "left join (select fundsType, max(blockHeight) maxHeight, min(blockHeight) minHeight, max(configTime) maxTime, min(configTime) minTime "
//				+ "from finAccWalletBill " + sqlWhere + " group by fundsType) fma on fa.fundsType = fma.fundsType";


			sql = "select fa.fundsType fundsType, abs(f1a.1amount) amount1, abs(f1a.1fee) fee1, abs(f2a.2amount) amount2, abs(f2a.2fee) fee2, "
					+ "abs(f3a.3amount) amount3, abs(f3a.3fee) fee3, abs(f4a.4amount) amount4, abs(f4a.4fee) fee4, "
					+"abs(f5a.5amount) amount5, abs(f5a.5fee) fee5, abs(f6a.6amount) amount6, abs(f6a.6fee) fee6, "
					+"abs(f7a.7amount) amount7, abs(f7a.7fee) fee7, abs(f8a.8amount) amount8, abs(f8a.8fee) fee8, "
					+"abs(f37a.sumSameFee37) sumSameFee37, abs(f3a2.sumSameFee3) sumSameFee3, abs(f7a2.sumSameFee7) sumSameFee7, "
					+ "fma.maxHeight, fma.minHeight, fma.maxTime, fma.minTime "
					+ "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
					+ "left join (select fundsType, sum(txAmount) 1amount, sum(fee) 1fee from finAccWalletBill "
					+ "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
					+ "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
					+ "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
					+ "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
					+ "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
					+ "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
					+ "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
					//xzhang 2017.08.21 新增四种类型
					+ "left join (select fundsType, sum(txAmount) 5amount, sum(fee) 5fee from finAccWalletBill "
					+ "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
					+ "left join (select fundsType, sum(txAmount) 6amount, sum(fee) 6fee from finAccWalletBill "
					+ "where dealType = 6 " + sqlAnd + " group by fundsType ) f6a on fa.fundsType = f6a.fundsType "
					+ "left join (select fundsType, sum(txAmount) 7amount, sum(fee) 7fee from finAccWalletBill "
					+ "where dealType = 7 " + sqlAnd + " group by fundsType ) f7a on fa.fundsType = f7a.fundsType "
					+ "left join (select fundsType, sum(txAmount) 8amount, sum(fee) 8fee from finAccWalletBill "
					+ "where dealType = 8 " + sqlAnd + " group by fundsType ) f8a on fa.fundsType = f8a.fundsType "
				/*start by xwz 20171014*/
					//类型为3，7时多计手续费
					+ "left join (select ff37a.fundsType, sum(ff37a.37sameFee * (ff37a.37cnt - 1) / ff37a.37cnt) sumSameFee37 from ( "
					+ "select fundsType, txid, sum(fee) 37sameFee, count(id) 37cnt from finAccWalletBill "
					+ "where dealType in (3, 7) and txid in (select txid from finAccWalletBill where dealType in (3, 7) " + sqlAnd + " group by txid having count(id) > 1 ) "
					+ ") ff37a) f37a on fa.fundsType = f37a.fundsType "
					//类型为3时多计手续费
					+ "left join (select ff3a.fundsType, sum(ff3a.3sameFee * (ff3a.3cnt - 1) / ff3a.3cnt) sumSameFee3 from ( "
					+ "select fundsType, txid, sum(fee) 3sameFee, count(id) 3cnt from finAccWalletBill "
					+ "where dealType = 3 and txid in (select txid from finAccWalletBill where dealType = 3 " + sqlAnd + " group by txid having count(id) > 1 ) "
					+ ") ff3a) f3a2 on fa.fundsType = f3a2.fundsType "
					//类型为7时多计手续费
					+ "left join (select ff7a.fundsType, sum(ff7a.7sameFee * (ff7a.7cnt - 1) / ff7a.7cnt) sumSameFee7 from ( "
					+ "select fundsType, txid, sum(fee) 7sameFee, count(id) 7cnt from finAccWalletBill "
					+ "where dealType = 7 and txid in (select txid from finAccWalletBill where dealType = 7 " + sqlAnd + " group by txid having count(id) > 1 ) "
					+ ") ff7a) f7a2 on fa.fundsType = f7a2.fundsType "
				/*end*/
					+ "left join (select fundsType, max(blockHeight) maxHeight, min(blockHeight) minHeight, max(configTime) maxTime, min(configTime) minTime "
					+ "from finAccWalletBill " + sqlWhere + " group by fundsType) fma on fa.fundsType = fma.fundsType";

			log.info("sql = " + sql);
			query.setSql(sql);
			query.setCls(FinAccWalDailyAccBean.class);
			/*总条数*/
			int total = query.count();
			if(total > 0){
				/*分页查询*/
				List<FinAccWalDailyAccBean> listFinAccWalDailyAcc = finAccWalDailyAccDao.findPage(currentPage, 20);
				//xzhang 2017.08.21 直接在vo中处理展示字段赋值问题，避免没必要的循环。注释
//				/*循环处理资金类型*/
//				FinAccWalDailyAccBean finAccWalDailyAccBean;
//				int tmpFundsType = 0;
//				for(int i = 0; i < listFinAccWalDailyAcc.size(); i++) {
//					finAccWalDailyAccBean = listFinAccWalDailyAcc.get(i);
//					tmpFundsType = finAccWalDailyAccBean.getFundsType();
//					/*货币属性类*/
//					CoinProps coinProps = DatabasesUtil.coinProps(tmpFundsType);
//					finAccWalDailyAccBean.setFundTypeName(coinProps.getDatabaseKey());
//				}
				setAttr("listFinAccWalDailyAcc", listFinAccWalDailyAcc);
			}
			setPaging(total, currentPage, 20);
			setAttr("itemCount", total);
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	@Page(Viewer = "")
	public void exportFinAccWalDailyAcc(){
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
			List<FinAccWalDailyAccBean> listFinAccWalDailyAcc = getFinAccWalDailyAccList();
			if(null == listFinAccWalDailyAcc || 0 == listFinAccWalDailyAcc.size()) {
				return;
			}
			//xzhang 2017.08.21 新增四种类型
			String [] column = {"fundTypeName", "amount1", "amount2", "hotDetailWalOcAmount", "fee4", "coldWalOcAmount",
					"amount7", "fee7","fee3","hotDownloadWalOcAmount","fee2","amount6","amount5","amount8","fee8","heightRange","timeRange"};
			String [] tabHead = {"资金类型", "用户充值", "用户提现", "热冲发生额", "热冲到冷网络费", "冷钱包发生额", "冷到其他发生额",
					"冷到其他网络费","冷到热提网络费","热提发生额","热提到用户网络费","其他到冷发生额","其他到热提发生额","热提到其他发生额","热提到其他网络费","区块高度","对账日期"};
			HSSFWorkbook workbook = ExcelManager.exportNormal(listFinAccWalDailyAcc, column, tabHead);
			OutputStream out = response.getOutputStream();
			response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_" + fundTypeName + "_llistFinAccWalDailyAcc.xls", "UTF-8"));
			response.setContentType("application/msexcel;charset=UTF-8");
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}

	public List<FinAccWalDailyAccBean> getFinAccWalDailyAccList(){
		/*查询条件*/
		Timestamp startTime = dateParam("startDate");
		Timestamp endTime = dateParam("endDate");
		int fundsType = intParam("fundsType");
		int startBlockHeight = intParam("startBlockHeight");
		int endBlockHeight = intParam("endBlockHeight");

		log.info("fundsType = " + fundsType);
		log.info("startTime = " + startTime + ", endTime = " + endTime);
		log.info("startBlockHeight = " + startBlockHeight + ", endBlockHeight = " + endBlockHeight);

		/*设置查询*/
		Query<FinAccWalDailyAccBean> query = finAccWalDailyAccDao.getQuery();
		/*拼接查询条件*/
		String sqlWhere = "", sqlAnd = "";
		/*是否已经有where如果有则拼接and*/
		Map<String, Boolean> mapHasWhere = new HashMap<String, Boolean>();
		mapHasWhere.put("hasWhere", false);

		/*拼接SQL语句:where 和 and 组合*/
		CommonUtilNoStatic commonUtilNoStatic = new CommonUtilNoStatic();
		if(fundsType > 0) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "fundsType", fundsType, "=");
			sqlAnd += "and fundsType = " + fundsType + " ";
		}
		/*确认时间*/
		if(null != startTime) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", startTime, ">=");
			sqlAnd += "and configTime >= '" + startTime + "' ";
		}
		if(null != endTime) {
			String strEndTime = endTime.toString();
			strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
			endTime = Timestamp.valueOf(strEndTime);
			sqlWhere = commonUtilNoStatic.giveSqlWhereByTime(sqlWhere, mapHasWhere, "configTime", endTime, "<=");
			sqlAnd += "and configTime <= '" + endTime + "' ";
		}
		/*区块高度*/
		if(startBlockHeight > 0) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", startBlockHeight, ">=");
			sqlAnd += "and blockHeight >= " + startBlockHeight + " ";
		}
		if(endBlockHeight > 0) {
			sqlWhere = commonUtilNoStatic.giveSqlWhereByInt(sqlWhere, mapHasWhere, "blockHeight", endBlockHeight, "<=");
			sqlAnd += "and blockHeight <= " + endBlockHeight + " ";
		}
		/*sql查询:checkState1：平衡，2：不平衡*/
		//@TODO 当前sql涉及过多函数，影响sql执行效率。待后期优化sql。判断交给java程序处理。 20170823 xzhang
//		sql ="select fa.fundsType fundsType, abs(f1a.1amount) amount1, abs(f1a.1fee) fee1, abs(f2a.2amount) amount2, abs(f2a.2fee) fee2, "
//				+ "abs(f3a.3amount) amount3, abs(f3a.3fee) fee3, abs(f4a.4amount) amount4, abs(f4a.4fee) fee4, "
//				+"abs(f5a.5amount) amount5, abs(f5a.5fee) fee5, abs(f6a.6amount) amount6, abs(f6a.6fee) fee6, "
//				+"abs(f7a.7amount) amount7, abs(f7a.7fee) fee7, abs(f8a.8amount) amount8, abs(f8a.8fee) fee8, "
//				+"(ifnull(abs(f4a.4amount),0) - ifnull(abs(f3a.3amount),0) - ifnull(abs(f3a.3fee),0) + ifnull(abs(f6a.6amount),0) - ifnull(abs(f7a.7amount),0) - ifnull(abs(f7a.7fee),0)) coldWalOcAmount, "
//				+"(ifnull(abs(f1a.1amount),0) - ifnull(abs(f4a.4amount),0) - ifnull(abs(f4a.4fee),0)) hotDetailWalOcAmount, "
//				+"(ifnull(abs(f3a.3amount),0) - ifnull(abs(f2a.2amount),0) - ifnull(abs(f2a.2fee),0) + ifnull(abs(f5a.5amount),0) - ifnull(abs(f8a.8amount),0) - ifnull(abs(f8a.8fee),0)) hotDownloadWalOcAmount, "
//				+ "fma.maxHeight, fma.minHeight, fma.maxTime, fma.minTime "
//				+ "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
//				+ "left join (select fundsType, sum(txAmount) 1amount, sum(fee) 1fee from finAccWalletBill "
//				+ "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
//				+ "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
//				+ "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
//				+ "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 5amount, sum(fee) 5fee from finAccWalletBill "
//				+ "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 6amount, sum(fee) 6fee from finAccWalletBill "
//				+ "where dealType = 6 " + sqlAnd + " group by fundsType ) f6a on fa.fundsType = f6a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 7amount, sum(fee) 7fee from finAccWalletBill "
//				+ "where dealType = 7 " + sqlAnd + " group by fundsType ) f7a on fa.fundsType = f7a.fundsType "
//				+ "left join (select fundsType, sum(txAmount) 8amount, sum(fee) 8fee from finAccWalletBill "
//				+ "where dealType = 8 " + sqlAnd + " group by fundsType ) f8a on fa.fundsType = f8a.fundsType "
//				+ "left join (select fundsType, max(blockHeight) maxHeight, min(blockHeight) minHeight, max(configTime) maxTime, min(configTime) minTime "
//				+ "from finAccWalletBill " + sqlWhere + " group by fundsType) fma on fa.fundsType = fma.fundsType";

		sql ="select fa.fundsType fundsType, abs(f1a.1amount) amount1, abs(f1a.1fee) fee1, abs(f2a.2amount) amount2, abs(f2a.2fee) fee2, "
				+ "abs(f3a.3amount) amount3, abs(f3a.3fee)-ifnull(abs(f3a2.sumSameFee3),0) fee3, abs(f4a.4amount) amount4, abs(f4a.4fee) fee4, "
				+"abs(f5a.5amount) amount5, abs(f5a.5fee) fee5, abs(f6a.6amount) amount6, abs(f6a.6fee) fee6, "
				+"abs(f7a.7amount) amount7, abs(f7a.7fee)-ifnull(abs(f7a2.sumSameFee7),0)fee7, abs(f8a.8amount) amount8, abs(f8a.8fee) fee8, "
				+"(ifnull(abs(f4a.4amount),0) - ifnull(abs(f3a.3amount),0) - ifnull(abs(f3a.3fee),0) + ifnull(abs(f6a.6amount),0) - ifnull(abs(f7a.7amount),0) - ifnull(abs(f7a.7fee),0) + ifnull(abs(f37a.sumSameFee37),0)) coldWalOcAmount, "
				+"(ifnull(abs(f1a.1amount),0) - ifnull(abs(f4a.4amount),0) - ifnull(abs(f4a.4fee),0)) hotDetailWalOcAmount, "
				+"(ifnull(abs(f3a.3amount),0) - ifnull(abs(f2a.2amount),0) - ifnull(abs(f2a.2fee),0) + ifnull(abs(f5a.5amount),0) - ifnull(abs(f8a.8amount),0) - ifnull(abs(f8a.8fee),0)) hotDownloadWalOcAmount, "
				+ "fma.maxHeight, fma.minHeight, fma.maxTime, fma.minTime "
				+ "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
				+ "left join (select fundsType, sum(txAmount) 1amount, sum(fee) 1fee from finAccWalletBill "
				+ "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
				+ "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
				+ "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
				+ "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
				+ "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
				+ "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
				+ "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
				+ "left join (select fundsType, sum(txAmount) 5amount, sum(fee) 5fee from finAccWalletBill "
				+ "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
				+ "left join (select fundsType, sum(txAmount) 6amount, sum(fee) 6fee from finAccWalletBill "
				+ "where dealType = 6 " + sqlAnd + " group by fundsType ) f6a on fa.fundsType = f6a.fundsType "
				+ "left join (select fundsType, sum(txAmount) 7amount, sum(fee) 7fee from finAccWalletBill "
				+ "where dealType = 7 " + sqlAnd + " group by fundsType ) f7a on fa.fundsType = f7a.fundsType "
				+ "left join (select fundsType, sum(txAmount) 8amount, sum(fee) 8fee from finAccWalletBill "
				+ "where dealType = 8 " + sqlAnd + " group by fundsType ) f8a on fa.fundsType = f8a.fundsType "

				/*start by xwz 20171014*/
				+ "left join (select ff37a.fundsType, sum(ff37a.37sameFee * (ff37a.37cnt - 1) / ff37a.37cnt) sumSameFee37 from ( "
				+ "select fundsType, txid, sum(fee) 37sameFee, count(id) 37cnt from finAccWalletBill "
				+ "where dealType in (3, 7) and txid in (select txid from finAccWalletBill where dealType in (3, 7) " + sqlAnd + " group by txid having count(id) > 1 ) "
				+ ") ff37a) f37a on fa.fundsType = f37a.fundsType "
				+ "left join (select ff3a.fundsType, sum(ff3a.3sameFee * (ff3a.3cnt - 1) / ff3a.3cnt) sumSameFee3 from ( "
				+ "select fundsType, txid, sum(fee) 3sameFee, count(id) 3cnt from finAccWalletBill "
				+ "where dealType = 3 and txid in (select txid from finAccWalletBill where dealType = 3 " + sqlAnd + " group by txid having count(id) > 1 ) "
				+ ") ff3a) f3a2 on fa.fundsType = f3a2.fundsType "
				+ "left join (select ff7a.fundsType, sum(ff7a.7sameFee * (ff7a.7cnt - 1) / ff7a.7cnt) sumSameFee7 from ( "
				+ "select fundsType, txid, sum(fee) 7sameFee, count(id) 7cnt from finAccWalletBill "
				+ "where dealType = 7 and txid in (select txid from finAccWalletBill where dealType = 7 " + sqlAnd + " group by txid having count(id) > 1 ) "
				+ ") ff7a) f7a2 on fa.fundsType = f7a2.fundsType "
				/*end*/

				+ "left join (select fundsType, max(blockHeight) maxHeight, min(blockHeight) minHeight, max(configTime) maxTime, min(configTime) minTime "
				+ "from finAccWalletBill " + sqlWhere + " group by fundsType) fma on fa.fundsType = fma.fundsType";

		log.info("sql = " + sql);
		query.setSql(sql);
		query.setCls(FinAccWalDailyAccBean.class);

		int total = query.count();
		if(total > 0){
			List<FinAccWalDailyAccBean> listFinAccWalDailyAcc = query.getList();
			return listFinAccWalDailyAcc;
		}
		return null;
	}
}