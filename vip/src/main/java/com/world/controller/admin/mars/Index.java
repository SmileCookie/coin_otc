package com.world.controller.admin.mars;

import com.world.cache.Cache;
import com.world.controller.admin.balaccount.finaccwalletbill.BillDetail;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.balaccount.entity.FinAccWalletBill;
import com.world.model.dao.mars.MarsDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.bill.BillDetails;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.mars.CheckVo;
import com.world.model.entity.mars.Mars;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/mars/", des = "火星表")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    MarsDao marsDao = new MarsDao();

    /**
     * 统计火星表
     */
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        List<Mars> marsList = new ArrayList<>();
        int pageSize = 30;
        int currentPage = intParam("page");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp begin = dateParam("begin");
        Timestamp end = dateParam("end");
        String fundsType = param("fundsType");
        String changePosition = param("changePosition");
        String changeType = param("changeType");
        String companyChangeType = param("companyChangeType");
        String accountingType = param("accountingType");
        StringBuilder where = new StringBuilder();
        
        log.info("CompanyChangeType = " + companyChangeType);
        
        if (!StringUtils.isBlank(fundsType)) {
            where.append(" AND fundsType =  '" + Integer.parseInt(fundsType) + "'");
        }
        if (!StringUtils.isBlank(changePosition)) {
            where.append(" AND changePosition =  '" + changePosition + "'");
        }
        if (!StringUtils.isBlank(changeType)) {
            where.append(" AND changeType =  '" + changeType + "'");
        }
        if (!StringUtils.isBlank(companyChangeType)) {
            where.append(" AND companyChangeType =  '" + companyChangeType + "'");
        }
        if (!StringUtils.isBlank(accountingType)) {
            where.append(" AND accountingType =  '" + accountingType + "'");
        }
        if (null != begin) {
            where.append(" AND transDate >=  '" + format.format(begin) + "'");
        }
        if (null != end) {
            where.append(" AND transDate <=  '" + format.format(end) + "'");
        }
        String w = where.toString();
        if (w.length() > 0) {
            w = " where " + w.substring(4);
        }
        try {
            String sql = "select * from mars " + w + " order by transDate DESC";
            Query<Mars> query = marsDao.getQuery();
            query.setCls(Mars.class);
            query.setSql(sql);
            query.setDatabase("messi_ods");
            int total = query.count();
            if(total > 0){
                marsList = query.getPageList(currentPage, pageSize);
                Map<String, CoinProps> coinPropsMap = DatabasesUtil.getCoinPropMaps();
                for (Mars mars : marsList) {
                    for (Map.Entry<String, CoinProps> entry : coinPropsMap.entrySet()) {
                        CoinProps coint = entry.getValue();
                        if (mars.getFundsType() == coint.getFundsType()) {
                            mars.setFundsTypeName(coint.getPropTag());
                        }
                    }
                }
            }
            setPaging((int) total, currentPage, pageSize);
        } catch (Exception e) {
            logger.error("进入统计火星表页面失败", e);
        }
        setAttr("ft", DatabasesUtil.getCoinPropMaps());
        setAttr("dataList", marsList);
    }


    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }


    /**
     * 保存修改
     */
    @Page(Viewer = ".xml")
    public void saveMars() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String id = param("id");
            if (Integer.parseInt(id) > 0) {
                Timestamp transDate = dateParam("transDate");
                String summary = param("summary");
                String fundsType = param("fundsType");
                BigDecimal income = decimalParam("income");
                BigDecimal expense = decimalParam("expense");
                String changePosition = param("changePosition");
                String changeType = param("changeType");
                String companyChangeType = param("companyChangeType");
                String accountingType = param("accountingType");
                String comment = param("comment");
                String begin = format.format(transDate);
                String create = format.format(new Date());
                String sql = "UPDATE mars SET transDate=?, summary=?, fundsType=?, income=?,expense=?,changePosition=?, changeType=?,companyChangeType=?,accountingType=?,comment=?,operator=?,createDate=? WHERE id=?";
                int rtn = Data.Update("messi_ods", sql, new Object[]{begin, summary, Integer.parseInt(fundsType), income, expense, changePosition, changeType, companyChangeType, accountingType, comment, adminName(), create, Integer.parseInt(id)});
                if (rtn != -1) {
                    Write("操作成功,", true, "操作成功");
                } else {
                    Write("操作失败,", false, "操作失败");
                }
            } else {
                Timestamp transDate = dateParam("transDate");
                String summary = param("summary");
                String fundsType = param("fundsType");
                BigDecimal income = decimalParam("income");
                BigDecimal expense = decimalParam("expense");
                String changePosition = param("changePosition");
                String companyChangeType = param("companyChangeType");
                String changeType = param("changeType");
                String accountingType = param("accountingType");
                String comment = param("comment");
                String begin = format.format(transDate);
                String create = format.format(new Date());
                String sql = "insert into mars(transDate, summary, fundsType, income, expense, changePosition, " + "changeType, companyChangeType, accountingType, comment, operator, createDate ) " + "values ('" + begin + "', '" + summary + "','" + Integer.parseInt(fundsType) + "','" + income + "','" + expense + "','" + changePosition + "','" + changeType + "','" + companyChangeType + "','" + accountingType + "','" + comment + "','" + adminName() + "','" + create + "')";
                List<OneSql> paySqls = new ArrayList<>();
                paySqls.add(new OneSql(sql, 1, new Object[]{}, "messi_ods"));
                Boolean flag = Data.doTrans(paySqls);
                if (flag) {
                    Write("操作成功,", true, "操作成功");
                } else {
                    Write("操作失败,", false, "操作失败");
                }
            }

        } catch (Exception e) {
            logger.error(e.toString(), e);
            Write("操作失败,", false, "操作失败");
        }
    }


    /*
    /**
     * 火星表创建／修改 页面
     */
    @Page(Viewer = "/admins/mars/recommen1.jsp")
    public void showMars() {
        String id = param("id");
        Mars mars = new Mars();
        if (!StringUtils.isBlank(id)) {
            String sql = "select * from mars where id=?";
            mars = (Mars) Data.GetOne("messi_ods", sql, new Object[]{id}, Mars.class);
        }
        setAttr("ft", DatabasesUtil.getCoinPropMaps());
        setAttr("user", mars);
    }


    /**
     * 审核
     */
    @Page(Viewer = ".xml")
    public void check() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String id = param("id");
        Mars mars = new Mars();
        if (!StringUtils.isBlank(id)) {
            String sql = "select * from mars where id=?";
            mars = (Mars) Data.GetOne("messi_ods", sql, new Object[]{id}, Mars.class);
        }
        //CheckVo checkVo = new CheckVo();
        /*if (mars.getChangeType().equals("1")) {//冷到其他
            checkVo = q7check(mars.getFundsType());
        }
        if (mars.getChangeType().equals("2")) {//其他到热提
            checkVo = q5check(mars.getFundsType());
        }
        if (mars.getChangeType().equals("4")) {//其他到冷
            checkVo = q6check(mars.getFundsType());
        }
        if (mars.getChangeType().equals("3")) {//后台充值
            checkVo = j7check(mars.getFundsType());
        }*/
        Boolean flag = false;
        if(!adminName().equals(mars.getOperator())){
            flag = true;
        }
        if (flag) {
            String sql = "UPDATE mars SET checkState = ?,auditior = ?, checkDate = ? where id = ?";
            int rtn = Data.Update("messi_ods", sql, new Object[]{"1", adminName(),format.format(new Date()),Integer.parseInt(id)});
            if (rtn != -1) {
                Write("审核成功,", true, "审核成功");
            } else {
                Write("审核失败,系统异常", false, "审核失败，系统异常");
            }

        } else {
           // Write("审核失败,资金不正确,应当是:" + checkVo.getAccount(), false, "审核失败，资金不正确");
             Write("审核失败,您无权限审核", false, "审核失败,您无权限审核");
        }
    }


    public CheckVo q7check(int fundsType) {
        CheckVo checkVo = new CheckVo();
        Boolean flag = false;
        try {
            Mars income = new Mars();
            String incomeSql = "select sum(income) as income,fundsType as income from mars where fundsType=? and changeType=1";
            income = (Mars) Data.GetOne("messi_ods", incomeSql, new Object[]{fundsType}, Mars.class);
            Mars expense = new Mars();
            String expenseSql = "select sum(expense) as expense,fundsType as income from mars where fundsType=? and changeType=1";
            expense = (Mars) Data.GetOne("messi_ods", expenseSql, new Object[]{fundsType}, Mars.class);
            BigDecimal q7 = income.getIncome().subtract(expense.getExpense());
            if (q7.compareTo(BigDecimal.ZERO) < 0) {
                q7 = q7.multiply(new BigDecimal(-1));
            }
            FinAccWalletBill finAccWalletBill = new FinAccWalletBill();
            String finAccWalletBillSql = "select sum(txAmount) as txAmount  from finAccWalletBill where dealType=7 and fundsType=?";
            finAccWalletBill = (FinAccWalletBill) Data.GetOne(finAccWalletBillSql, new Object[]{fundsType}, FinAccWalletBill.class);
            if (finAccWalletBill.getTxAmount() == null) {
                finAccWalletBill.setTxAmount(BigDecimal.ZERO);
            }
            if (q7.compareTo(finAccWalletBill.getTxAmount()) == 0) {
                flag = true;
            }
            checkVo.setFlag(flag);
            checkVo.setAccount(finAccWalletBill.getTxAmount());

        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return checkVo;
    }


    public CheckVo q5check(int fundsType) {
        CheckVo checkVo = new CheckVo();
        Boolean flag = false;
        try {
            Mars income = new Mars();
            String incomeSql = "select sum(income) as income,fundsType as income from mars where fundsType=? and changeType=2";
            income = (Mars) Data.GetOne("messi_ods", incomeSql, new Object[]{fundsType}, Mars.class);
            Mars expense = new Mars();
            String expenseSql = "select sum(expense) as expense,fundsType as income from mars where fundsType=? and changeType=2";
            expense = (Mars) Data.GetOne("messi_ods", expenseSql, new Object[]{fundsType}, Mars.class);
            BigDecimal q7 = income.getIncome().subtract(expense.getExpense());
            if (q7.compareTo(BigDecimal.ZERO) < 0) {
                q7 = q7.multiply(new BigDecimal(-1));
            }
            FinAccWalletBill finAccWalletBill = new FinAccWalletBill();
            String finAccWalletBillSql = "select sum(txAmount) as txAmount  from finAccWalletBill where dealType=5 and fundsType=?";
            finAccWalletBill = (FinAccWalletBill) Data.GetOne(finAccWalletBillSql, new Object[]{fundsType}, FinAccWalletBill.class);
            if (finAccWalletBill.getTxAmount() == null) {
                finAccWalletBill.setTxAmount(BigDecimal.ZERO);
            }
            if (q7.compareTo(finAccWalletBill.getTxAmount()) == 0) {
                flag = true;
            }
            checkVo.setFlag(flag);
            checkVo.setAccount(finAccWalletBill.getTxAmount());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return checkVo;
    }


    public CheckVo q6check(int fundsType) {
        CheckVo checkVo = new CheckVo();
        Boolean flag = false;
        try {
            Mars income = new Mars();
            String incomeSql = "select sum(income) as income,fundsType as income from mars where fundsType=? and changeType=4";
            income = (Mars) Data.GetOne("messi_ods", incomeSql, new Object[]{fundsType}, Mars.class);
            Mars expense = new Mars();
            String expenseSql = "select sum(expense) as expense,fundsType as income from mars where fundsType=? and changeType=4";
            expense = (Mars) Data.GetOne("messi_ods", expenseSql, new Object[]{fundsType}, Mars.class);
            BigDecimal q7 = income.getIncome().subtract(expense.getExpense());
            if (q7.compareTo(BigDecimal.ZERO) < 0) {
                q7 = q7.multiply(new BigDecimal(-1));
            }
            FinAccWalletBill finAccWalletBill = new FinAccWalletBill();
            String finAccWalletBillSql = "select sum(txAmount) as txAmount  from finAccWalletBill where dealType=6 and fundsType=?";
            finAccWalletBill = (FinAccWalletBill) Data.GetOne(finAccWalletBillSql, new Object[]{fundsType}, FinAccWalletBill.class);
            if (finAccWalletBill.getTxAmount() == null) {
                finAccWalletBill.setTxAmount(BigDecimal.ZERO);
            }
            if (q7.compareTo(finAccWalletBill.getTxAmount()) == 0) {
                flag = true;
            }
            checkVo.setFlag(flag);
            checkVo.setAccount(finAccWalletBill.getTxAmount());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return checkVo;
    }


    public CheckVo j7check(int fundsType) {
        CheckVo checkVo = new CheckVo();
        Boolean flag = false;
        try {
            Mars income = new Mars();
            String incomeSql = "select sum(income) as income,fundsType as income from mars where fundsType=? and changeType=3";
            income = (Mars) Data.GetOne("messi_ods", incomeSql, new Object[]{fundsType}, Mars.class);
            Mars expense = new Mars();
            String expenseSql = "select sum(expense) as expense,fundsType as income from mars where fundsType=? and changeType=3";
            expense = (Mars) Data.GetOne("messi_ods", expenseSql, new Object[]{fundsType}, Mars.class);
            BigDecimal q7 = income.getIncome().subtract(expense.getExpense());
            if (q7.compareTo(BigDecimal.ZERO) < 0) {
                q7 = q7.multiply(new BigDecimal(-1));
            }
            BillDetails billDetails = new BillDetails();
            String billDetailsSql = "select sum(amount) as amount, fundsType  from bill where type = 7 and remark = '系统充值' and fundsType=?";
            billDetails = (BillDetails) Data.GetOne("messi_ods", billDetailsSql, new Object[]{fundsType}, FinAccWalletBill.class);
            if (billDetails.getAmount() == null) {
                billDetails.setAmount(BigDecimal.ZERO);
            }
            if (q7.compareTo(billDetails.getAmount()) == 0) {
                flag = true;
            }
            checkVo.setFlag(flag);
            checkVo.setAccount(billDetails.getAmount());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
        return checkVo;
    }


}
