package com.world.controller.admin.report.collection;

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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@FunctionAction(jspPath = "/admins/report/collection/", des = "平台资金汇总查询报表")
public class index  extends UserAction {

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
            int startBlockHeight = intParam("startBlockHeight");
            int endBlockHeight = intParam("endBlockHeight");

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
//            sql = "select fa.fundsType fundsType, abs(f1a.1amount) amount1, abs(f2a.2amount) amount2, abs(f2a.2fee) fee2, "
//                    + "abs(f3a.3amount) amount3, abs(f3a.3fee) fee3, abs(f4a.4amount) amount4, abs(f4a.4fee) fee4, "
//                    +"abs(f5a.5amount) amount5, abs(f6a.6amount) amount6, "
//                    +"abs(f7a.7amount) amount7, abs(f7a.7fee) fee7, abs(f8a.8amount) amount8, abs(f8a.8fee) fee8 "
//                    + "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
//                    + "left join (select fundsType, sum(txAmount) 1amount from finAccWalletBill "
//                    + "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
//                    + "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
//                    + "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
//                    + "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
//                    + "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
//                    + "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
//                    + "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
//                    + "left join (select fundsType, sum(txAmount) 5amount from finAccWalletBill "
//                    + "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
//                    + "left join (select fundsType, sum(txAmount) 6amount from finAccWalletBill "
//                    + "where dealType = 6 " + sqlAnd + " group by fundsType ) f6a on fa.fundsType = f6a.fundsType "
//                    + "left join (select fundsType, sum(txAmount) 7amount, sum(fee) 7fee from finAccWalletBill "
//                    + "where dealType = 7 " + sqlAnd + " group by fundsType ) f7a on fa.fundsType = f7a.fundsType "
//                    + "left join (select fundsType, sum(txAmount) 8amount, sum(fee) 8fee from finAccWalletBill "
//                    + "where dealType = 8 " + sqlAnd + " group by fundsType ) f8a on fa.fundsType = f8a.fundsType "
//                    + "left join (select fundsType, max(blockHeight) maxHeight, min(blockHeight) minHeight, max(configTime) maxTime, min(configTime) minTime "
//                    + "from finAccWalletBill " + sqlWhere + " group by fundsType) fma on fa.fundsType = fma.fundsType";

            sql = "select fa.fundsType fundsType, abs(f1a.1amount) amount1, abs(f2a.2amount) amount2, abs(f2a.2fee) fee2, "
                    + "abs(f3a.3amount) amount3, abs(f3a.3fee)fee3, abs(f4a.4amount) amount4, abs(f4a.4fee) fee4, "
                    +"abs(f5a.5amount) amount5, abs(f6a.6amount) amount6, "
                    +"abs(f7a.7amount) amount7, abs(f7a.7fee) fee7, abs(f8a.8amount) amount8, abs(f8a.8fee) fee8, "
                    +"abs(f37a.sumSameFee37) sumSameFee37,abs(f3a2.sumSameFee3) sumSameFee3, abs(f7a2.sumSameFee7) sumSameFee7 "
                    + "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
                    + "left join (select fundsType, sum(txAmount) 1amount from finAccWalletBill "
                    + "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
                    + "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
                    + "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
                    + "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
                    + "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
                    + "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
                    + "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
                    + "left join (select fundsType, sum(txAmount) 5amount from finAccWalletBill "
                    + "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
                    + "left join (select fundsType, sum(txAmount) 6amount from finAccWalletBill "
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
			/*总条数*/
            int total = query.count();
            if(total > 0){
				/*分页查询*/
                List<FinAccWalDailyAccBean> listFinAccWalDailyAcc = finAccWalDailyAccDao.findPage(currentPage, 20);
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
        int fundsType = intParam("fundsType");
        String fundTypeName = "all";
		/*货币属性类*/
        if(fundsType > 0) {
            CoinProps coinProps = DatabasesUtil.coinProps(fundsType);
            fundTypeName = coinProps.getDatabaseKey();
        }
        try {
            if(!codeCorrect(XML)){
                return;
            }
            List<FinAccWalDailyAccBean> listFinAccWalDailyAcc = getFinAccWalDailyAccList();
            if(null == listFinAccWalDailyAcc || 0 == listFinAccWalDailyAcc.size()) {
                return;
            }
            String [] column = {"fundTypeName", "colDeposit", "colRollOut", "colFee", "colBalance",
                    "hotDeposit", "hotRollOut", "hotFee", "hotBalance",
                    "amount1", "amount4", "fee4", "hotPayBalance"};
            String [] tabHead = {"资金类型", "冷存入", "冷转出", "冷手续费", "冷余额",
                    "热提存入", "热提转出","热提手续费","热提余额",
                    "热充存入","热充转出","热充手续费","热充余额"};
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
//        sql = "select fa.fundsType fundsType, "
//                +"(ifnull(abs(f4a.4amount),0)+ifnull(abs(f6a.6amount),0)) colDeposit ,(ifnull(abs(f3a.3amount),0)+ifnull(abs(f7a.7amount),0)) colRollOut , "
//                +"(ifnull(abs(f3a.3fee),0)+ifnull(abs(f7a.7fee),0)) colFee ,"
//                +"(ifnull(abs(f4a.4amount),0)+ifnull(abs(f6a.6amount),0) - ifnull(abs(f3a.3amount),0)-ifnull(abs(f7a.7amount),0)-ifnull(abs(f3a.3fee),0)-ifnull(abs(f7a.7fee),0)) colBalance , "
//                +"(ifnull(abs(f3a.3amount),0)+ifnull(abs(f5a.5amount),0)) hotDeposit ,(ifnull(abs(f2a.2amount),0)+ifnull(abs(f8a.8amount),0)) hotRollOut , "
//                +"(ifnull(abs(f2a.2fee),0)+ifnull(abs(f8a.8fee),0)) hotFee ,"
//                +"(ifnull(abs(f3a.3amount),0)+ifnull(abs(f5a.5amount),0) - ifnull(abs(f2a.2amount),0)-ifnull(abs(f8a.8amount),0)-ifnull(abs(f2a.2fee),0)-ifnull(abs(f8a.8fee),0)) hotBalance , "
//                +" ifnull(abs(f1a.1amount),0) amount1, ifnull(abs(f4a.4amount),0) amount4, ifnull(abs(f4a.4fee),0) fee4, "
//                +"(ifnull(abs(f1a.1amount),0)-ifnull(abs(f4a.4amount),0) -ifnull(abs(f4a.4fee),0)) hotPayBalance "
//                + "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
//                + "left join (select fundsType, sum(txAmount) 1amount from finAccWalletBill "
//                + "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
//                + "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
//                + "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
//                + "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
//                + "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
//                + "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
//                + "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
//                + "left join (select fundsType, sum(txAmount) 5amount from finAccWalletBill "
//                + "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
//                + "left join (select fundsType, sum(txAmount) 6amount from finAccWalletBill "
//                + "where dealType = 6 " + sqlAnd + " group by fundsType ) f6a on fa.fundsType = f6a.fundsType "
//                + "left join (select fundsType, sum(txAmount) 7amount, sum(fee) 7fee from finAccWalletBill "
//                + "where dealType = 7 " + sqlAnd + " group by fundsType ) f7a on fa.fundsType = f7a.fundsType "
//                + "left join (select fundsType, sum(txAmount) 8amount, sum(fee) 8fee from finAccWalletBill "
//                + "where dealType = 8 " + sqlAnd + " group by fundsType ) f8a on fa.fundsType = f8a.fundsType "
//                + "left join (select fundsType, max(blockHeight) maxHeight, min(blockHeight) minHeight, max(configTime) maxTime, min(configTime) minTime "
//                + "from finAccWalletBill " + sqlWhere + " group by fundsType) fma on fa.fundsType = fma.fundsType";

        sql = "select fa.fundsType fundsType, "
                +"(ifnull(abs(f4a.4amount),0)+ifnull(abs(f6a.6amount),0)) colDeposit ,(ifnull(abs(f3a.3amount),0)+ifnull(abs(f7a.7amount),0)) colRollOut , "
                +"(ifnull(abs(f3a.3fee),0)+ifnull(abs(f7a.7fee),0) - ifnull(abs(f37a.sumSameFee37),0)) colFee ,"
                +"(ifnull(abs(f4a.4amount),0)+ifnull(abs(f6a.6amount),0) - ifnull(abs(f3a.3amount),0)-ifnull(abs(f7a.7amount),0)-ifnull(abs(f3a.3fee),0)-ifnull(abs(f7a.7fee) + ifnull(abs(f37a.sumSameFee37),0),0)) colBalance , "
                +"(ifnull(abs(f3a.3amount),0)+ifnull(abs(f5a.5amount),0)) hotDeposit ,(ifnull(abs(f2a.2amount),0)+ifnull(abs(f8a.8amount),0)) hotRollOut , "
                +"(ifnull(abs(f2a.2fee),0)+ifnull(abs(f8a.8fee),0)) hotFee ,"
                +"(ifnull(abs(f3a.3amount),0)+ifnull(abs(f5a.5amount),0) - ifnull(abs(f2a.2amount),0)-ifnull(abs(f8a.8amount),0)-ifnull(abs(f2a.2fee),0)-ifnull(abs(f8a.8fee),0)) hotBalance , "
                +" ifnull(abs(f1a.1amount),0) amount1, ifnull(abs(f4a.4amount),0) amount4, ifnull(abs(f4a.4fee),0) fee4, "
                +"(ifnull(abs(f1a.1amount),0)-ifnull(abs(f4a.4amount),0) -ifnull(abs(f4a.4fee),0)) hotPayBalance "
                + "from (select distinct fundsType from finAccWalletBill " + sqlWhere + ") fa "
                + "left join (select fundsType, sum(txAmount) 1amount from finAccWalletBill "
                + "where dealType = 1 " + sqlAnd + " group by fundsType ) f1a on fa.fundsType = f1a.fundsType "
                + "left join (select fundsType, sum(txAmount) 2amount, sum(fee) 2fee from finAccWalletBill "
                + "where dealType = 2 " + sqlAnd + " group by fundsType ) f2a on fa.fundsType = f2a.fundsType "
                + "left join (select fundsType, sum(txAmount) 3amount, sum(fee) 3fee from finAccWalletBill "
                + "where dealType = 3 " + sqlAnd + " group by fundsType ) f3a on fa.fundsType = f3a.fundsType "
                + "left join (select fundsType, sum(txAmount) 4amount, sum(fee) 4fee from finAccWalletBill "
                + "where dealType = 4 " + sqlAnd + " group by fundsType ) f4a on fa.fundsType = f4a.fundsType "
                + "left join (select fundsType, sum(txAmount) 5amount from finAccWalletBill "
                + "where dealType = 5 " + sqlAnd + " group by fundsType ) f5a on fa.fundsType = f5a.fundsType "
                + "left join (select fundsType, sum(txAmount) 6amount from finAccWalletBill "
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
