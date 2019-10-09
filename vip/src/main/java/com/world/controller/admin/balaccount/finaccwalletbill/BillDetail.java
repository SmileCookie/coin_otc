package com.world.controller.admin.balaccount.finaccwalletbill;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.balaccount.dao.FinAccWalletBillDetailsDao;
import com.world.model.balaccount.entity.FinAccWalletBillDetailsBean;
import com.world.util.CommonUtilNoStatic;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@FunctionAction(jspPath = "/admins/balaccount/finaccwalletbilldetail/", des = "钱包流水明细查询")
public class BillDetail extends UserAction {

    private FinAccWalletBillDetailsDao finAccWalletBillDetailsDao = new FinAccWalletBillDetailsDao();

    private final static int pageSize = 20;

    @Page(Viewer = "/admins/balaccount/finaccwalletbilldetail/list.jsp")
    public void index() {
        try {
            //查询条件
            int currentPage = intParam("page");

            int fundsType = intParam("fundsType");//资金类型
            int walType = intParam("walType");//钱包类型
            int dealType = intParam("dealType");//交易类型

            Timestamp startTime = dateParam("startDate");
            Timestamp endTime = dateParam("endDate");

            int startBlockHeight = intParam("startBlockHeight");//区块高度
            int endBlockHeight = intParam("endBlockHeight");//区块高度
            String walId = param("walId");//钱包编号
            String txId = param("txId");//交易流水号

            //先赋值下拉币种选择框
            setAttr("ft", DatabasesUtil.getCoinPropMaps());

            if (currentPage < 1) {
                return;
            }

            /* 获取查询 */
            Query<FinAccWalletBillDetailsBean> query = finAccWalletBillDetailsDao.getQuery();
            //sql查询：walType 4：热冲到冷，2：热提到用户，3：冷到热提
            String sql = "select *,case when walType = 4 then '热冲到冷' when walType = 2 then '热提到用户' when walType = 3 then '冷到热提' else '' end walTypeName from finaccwalletbilldetails ";

            //是否已经有where如果有则拼接and
            Map<String, Boolean> mapHasWhere = new HashMap<String, Boolean>();
            mapHasWhere.put("hasWhere", false);

            //拼接SQL语句:where 和 and 组合
            CommonUtilNoStatic commonUtilNoStatic = new CommonUtilNoStatic();

            //资金类型
            if (fundsType > 0) {
                sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "fundsType", fundsType, "=");
            }
            //钱包类型
            if (walType > 0) {
                sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "walType", walType, "=");
            }
            //交易类型
            if (dealType > 0) {
                sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "dealType", dealType, "=");
            }
            //确认时间
            if (null != startTime) {
                sql = commonUtilNoStatic.giveSqlWhereByTime(sql, mapHasWhere, "configTime", startTime, ">=");
            }
            if (null != endTime) {
                String strEndTime = endTime.toString();
                strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
                endTime = Timestamp.valueOf(strEndTime);
                sql = commonUtilNoStatic.giveSqlWhereByTime(sql, mapHasWhere, "configTime", endTime, "<=");
            }
            //区块高度
            if (startBlockHeight > 0) {
                sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "blockHeight", startBlockHeight, ">=");
            }
            if (endBlockHeight > 0) {
                sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "blockHeight", endBlockHeight, "<=");
            }
            //钱包编号(模糊匹配)
            if (StringUtils.isNotBlank(walId)) {
                sql = commonUtilNoStatic.giveSqlWhereByStr(sql, mapHasWhere, "walId", walId, "like");
            }

            //流水号(模糊匹配)
            if (StringUtils.isNotBlank(txId)) {
                sql = commonUtilNoStatic.giveSqlWhereByStr(sql, mapHasWhere, "txId", txId, "like");
            }

            //排序字段：按交易流水号升序；
            sql += " order by txIdN asc";

            log.info("sql:" + sql);

            query.setSql(sql);
            query.setCls(FinAccWalletBillDetailsBean.class);

            //总条数
            int total = query.count();
            if (total > 0) {
                //分页查询
                List<FinAccWalletBillDetailsBean> finAccWalletBillDetails = finAccWalletBillDetailsDao.findPage(currentPage, pageSize);
                setAttr("finAccWalletBillDetail", finAccWalletBillDetails);
            }
            setPaging(total, currentPage, pageSize);
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
    public void exportWalletBillDetail() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            List<FinAccWalletBillDetailsBean> walletBillDetailList = getWalletBillDetailList();
            if (null == walletBillDetailList || 0 == walletBillDetailList.size()) {
                return;
            }

            String[] column = {"txId","txIdN" ,"toAddress","strTxNAmount","fundsTypeName", "walName", "walTypeName", "strTxAmount", "strFee", "strWalBalance", "dealTypeName", "blockHeight", "configTime"};
            String[] tabHead = {"交易流水号", "交易流水号N","地址","金额","资金类型", "钱包名称", "钱包类型", "交易金额", "网络费", "钱包余额", "交易类型", "区块高度", "确认时间"};
            HSSFWorkbook workbook = ExcelManager.exportNormal(walletBillDetailList, column, tabHead);
            OutputStream out = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("excel_listWalletBillDetail.xls", "UTF-8"));
            response.setContentType("application/msexcel;charset=UTF-8");
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }


    public List<FinAccWalletBillDetailsBean> getWalletBillDetailList() {
        //查询条件
        int fundsType = intParam("fundsType");
        int walType = intParam("walType");
        int dealType = intParam("dealType");

        Timestamp startTime = dateParam("startDate");
        Timestamp endTime = dateParam("endDate");

        int startBlockHeight = intParam("startBlockHeight");
        int endBlockHeight = intParam("endBlockHeight");
        String walId = param("walId");
        String txId = param("txId");//交易流水号

        //获取查询
        Query<FinAccWalletBillDetailsBean> query = finAccWalletBillDetailsDao.getQuery();
        //sql查询：walType 4：热冲到冷，2：热提到用户，3：冷到热提
        String sql = "select *,case when walType = 4 then '热冲到冷' when walType = 2 then '热提到用户' when walType = 3 then '冷到热提' else '' end walTypeName from finaccwalletbilldetails ";

        //是否已经有where如果有则拼接and
        Map<String, Boolean> mapHasWhere = new HashMap<String, Boolean>();
        mapHasWhere.put("hasWhere", false);

        //拼接SQL语句:where 和 and 组合
        CommonUtilNoStatic commonUtilNoStatic = new CommonUtilNoStatic();

        //资金类型
        if (fundsType > 0) {
            sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "fundsType", fundsType, "=");
        }
        //钱包类型
        if (walType > 0) {
            sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "walType", walType, "=");
        }
        //交易类型
        if (dealType > 0) {
            sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "dealType", dealType, "=");
        }
        //确认时间
        if (null != startTime) {
            sql = commonUtilNoStatic.giveSqlWhereByTime(sql, mapHasWhere, "configTime", startTime, ">=");
        }
        if (null != endTime) {
            String strEndTime = endTime.toString();
            strEndTime = strEndTime.replace("00:00:00.0", "23:59:59.999");
            endTime = Timestamp.valueOf(strEndTime);
            sql = commonUtilNoStatic.giveSqlWhereByTime(sql, mapHasWhere, "configTime", endTime, "<=");
        }
        //区块高度
        if (startBlockHeight > 0) {
            sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "blockHeight", startBlockHeight, ">=");
        }
        if (endBlockHeight > 0) {
            sql = commonUtilNoStatic.giveSqlWhereByInt(sql, mapHasWhere, "blockHeight", endBlockHeight, "<=");
        }
        //钱包编号(模糊匹配)
        if (StringUtils.isNotBlank(walId)) {
            sql = commonUtilNoStatic.giveSqlWhereByStr(sql, mapHasWhere, "walId", walId, "like");
        }
        //流水号(模糊匹配)
        if (StringUtils.isNotBlank(txId)) {
            sql = commonUtilNoStatic.giveSqlWhereByStr(sql, mapHasWhere, "txId", txId, "like");
        }

        //排序字段：按交易流水号升序；
        sql += " order by txIdN asc";

        log.info("sql:" + sql);

        query.setSql(sql);
        query.setCls(FinAccWalletBillDetailsBean.class);

        //总条数
        int total = query.count();
        if (total > 0) {
            List<FinAccWalletBillDetailsBean> finAccWalletBillDetails = query.getList();
            return finAccWalletBillDetails;
        }
        return null;
    }
}
