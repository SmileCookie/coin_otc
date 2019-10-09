package com.world.controller.admin.balaccount.finaccwalletbill;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.balaccount.dao.FinAccWalletBillDao;
import com.world.model.balaccount.dao.FinAccWalletBillDetailsDao;
import com.world.model.balaccount.entity.FinAccWalletBill;
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
 * Created by buxianguan on 17/8/2.
 */
@FunctionAction(jspPath = "/admins/balaccount/finaccwalletbill/", des = "钱包流水查询")
public class Index extends UserAction {
	
    private FinAccWalletBillDao finAccWalletBillDao = new FinAccWalletBillDao();
    
    private FinAccWalletBillDetailsDao finAccWalletBillDetailsDao = new FinAccWalletBillDetailsDao();

    private final static int pageSize = 20;
    
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        try {
            //查询条件
            int currentPage = intParam("page");

            int fundsType = intParam("fundsType");
            int walType = intParam("walType");
            int dealType = intParam("dealType");

            Timestamp startTime = dateParam("startDate");
            Timestamp endTime = dateParam("endDate");

            int startBlockHeight = intParam("startBlockHeight");
            int endBlockHeight = intParam("endBlockHeight");
            String walId = param("walId");

            //先赋值下拉币种选择框
            setAttr("ft", DatabasesUtil.getCoinPropMaps());

            if (currentPage < 1) {
                return;
            }

            //获取查询
            Query<FinAccWalletBill> query = finAccWalletBillDao.getQuery();
            //sql查询：walType 4：热冲到冷，2：热提到用户，3：冷到热提
            String sql = "select *,case when walType = 4 then '热冲到冷' when walType = 2 then '热提到用户' when walType = 3 then '冷到热提' else '' end walTypeName from finaccwalletbill ";

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

            //排序字段：按资金类型，确认时间，钱包类型，区块高度倒序
            sql += " order by fundsType desc,configTime desc,walType desc,blockHeight desc ";

            log.info("sql:" + sql);

            query.setSql(sql);
            query.setCls(FinAccWalletBill.class);

            //总条数
            int total = query.count();
            if (total > 0) {
                //分页查询
                List<FinAccWalletBill> finAccWalletBills = finAccWalletBillDao.findPage(currentPage, pageSize);
                setAttr("finAccWalletBills", finAccWalletBills);
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

    @Page(Viewer = DEFAULT_AORU)
    public void aoru() {
        try {
            //交易流水号，关联详情表
            String txId = param("txId");

            Query<FinAccWalletBillDetailsBean> query = finAccWalletBillDetailsDao.getQuery();

            //sql查询：walType 4：热冲到冷，2：热提到用户，3：冷到热提
            String sql = "select *,case when walType = 4 then '热冲到冷' when walType = 2 then '热提到用户' when walType = 3 then '冷到热提' else '' end walTypeName " +
                    "from finaccwalletbilldetails where txId='" + txId + "' order by txIdN asc";

            log.info("sql:" + sql);
            query.setSql(sql);
            query.setCls(FinAccWalletBillDetailsBean.class);

            List<FinAccWalletBillDetailsBean> finAccWalletBillDetails = finAccWalletBillDetailsDao.find();
            setAttr("finAccWalletBillDetails", finAccWalletBillDetails);
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = "")
    public void exportWalletBill() {
        try {
            if (!codeCorrect(XML)) {
                return;
            }
            List<FinAccWalletBill> walletBillList = getWalletBillList();
            if (null == walletBillList || 0 == walletBillList.size()) {
                return;
            }

            String[] column = {"txId", "fundsTypeName", "walName", "walTypeName", "strTxAmount", "strFee", "strWalBalance", "dealTypeName", "blockHeight", "configTime"};
            String[] tabHead = {"交易流水号", "资金类型", "钱包名称", "钱包类型", "交易金额", "网络费", "钱包余额", "交易类型", "区块高度", "确认时间"};
            HSSFWorkbook workbook = ExcelManager.exportNormal(walletBillList, column, tabHead);
            OutputStream out = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("excel_listWalletBill.xls", "UTF-8"));
            response.setContentType("application/msexcel;charset=UTF-8");
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }


    public List<FinAccWalletBill> getWalletBillList() {
        //查询条件
        int fundsType = intParam("fundsType");
        int walType = intParam("walType");
        int dealType = intParam("dealType");

        Timestamp startTime = dateParam("startDate");
        Timestamp endTime = dateParam("endDate");

        int startBlockHeight = intParam("startBlockHeight");
        int endBlockHeight = intParam("endBlockHeight");
        String walId = param("walId");

        //获取查询
        Query<FinAccWalletBill> query = finAccWalletBillDao.getQuery();
        //sql查询：walType 4：热冲到冷，2：热提到用户，3：冷到热提
        String sql = "select *,case when walType = 4 then '热冲到冷' when walType = 2 then '热提到用户' when walType = 3 then '冷到热提' else '' end walTypeName from finaccwalletbill ";

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
        //排序字段：按资金类型，确认时间，钱包类型，区块高度倒序
        sql += " order by fundsType desc,configTime desc,walType desc,blockHeight desc ";

        log.info("sql:" + sql);

        query.setSql(sql);
        query.setCls(FinAccWalletBill.class);

        //总条数
        int total = query.count();
        if (total > 0) {
            List<FinAccWalletBill> finAccWalletBills = query.getList();
            return finAccWalletBills;
        }
        return null;
    }
}
