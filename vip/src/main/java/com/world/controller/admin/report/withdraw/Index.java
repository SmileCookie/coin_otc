package com.world.controller.admin.report.withdraw;


import com.alibaba.fastjson.JSONArray;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.dao.autofactory.AutoDownloadRecordDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.user.User;
import com.world.util.poi.ExcelManager;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/report/withdraw/", des = "提现记录查询报表")
public class Index extends FinanAction {

    DownloadDao bdDao = new DownloadDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        //查询条件
        int currentPage = intParam("page");
        int pageSize = 20;
        String status = param("status");
        Timestamp confirmStartDate = dateParam("confirmStartDate");//确认时间(区间)
        Timestamp confirmEndDate = dateParam("confirmEndDate");//确认时间(区间)
        Timestamp startTime = dateParam("startDate");
        Timestamp endTime = dateParam("endDate");
        String userId = param("userId").trim();
        double moneyMin = doubleParam("moneyMin");
        double moneyMax = doubleParam("moneyMax");
        String  commandId = param("commandId");

        bdDao.setCoint(coint);
        Query<DownloadBean> query = bdDao.getQuery();

        query.setSql("select * from "+bdDao.getTableName());
        query.setCls(DownloadBean.class);

        if (status.length() == 0)
            status = "success";

        String order = "desc";
        if (status.equals("wait")) {
            order = "asc";
            query.append(" AND  (status=0 and commandId = 0)");
        } else if (status.equals("confirm")) {
            query.append(" AND  (status=0 and commandId > 0)");
        } else if (status.equals("success")) {
            query.append(" AND  (status=2)");
        } else if (status.equals("fail")) {
            query.append(" AND  (status=1)");
        } else if (status.equals("cancel")) {
            query.append(" AND  (status=3)");
        } else if(status.equals("sendding")) {
            query.append(" AND  (status=7)");
        }
        if (startTime != null) {
            query.append(" and submitTime>=cast('" + startTime + "' as datetime)");
        }
        if (endTime != null) {
            query.append(" and submitTime<=cast('" + endTime + "' as datetime)");
        }

        if (confirmStartDate != null) {
            query.append(" and manageTime>=cast('" + confirmStartDate + "' as datetime)");
        }
        if (confirmEndDate != null) {
            query.append(" and manageTime<=cast('" + confirmEndDate + "' as datetime)");
        }
        if (userId.length() > 0) {
            query.append(" and userId = "+ userId + " ");
        }

        if(moneyMin > 0){
            query.append(" and amount >=" + moneyMin);
        }
        if(moneyMax > 0){
            query.append(" and amount <=" + moneyMax);
        }
        if("0".equals(commandId)){
            query.append("and uuid not in (select downLoadId from autoDownloadRecords ) and commandId >0 ");
        }else if("1".equals(commandId)){
            query.append("and uuid in (select downLoadId from autoDownloadRecords )");
        }
        int total = query.count();
        if (total > 0) {
            query.append(" ORDER BY submitTime " + order);
            //分页查询
            List<DownloadBean> btcDownloads = bdDao.findPage(currentPage, pageSize);
            List<String> uuids = new ArrayList<String>();
            List<String> userIds = new ArrayList<String>();
            for (DownloadBean bdb : btcDownloads) {
                userIds.add(bdb.getUserId() + "");
                bdb.setCurrency(coint.getPropTag());
                uuids.add(bdb.getUuid());       //获取uuid
            }

            if (userIds.size() > 0) {
                Map<String, User> userMaps = new UserDao().getUserMapByIds(userIds);
                Map<String,Long> uuidMaps = new AutoDownloadRecordDao().getAutoDownloadRecordId(uuids);
                for (DownloadBean bdb : btcDownloads) {
                    bdb.setUser(userMaps.get(bdb.getUserId() + ""));
                    if(uuidMaps.containsKey(bdb.getUuid())){
                        bdb.setAutoDownloadId(bdb.getId());
                    }
                }
            }
            request.setAttribute("dataList", btcDownloads);
        }
        request.setAttribute("status", status);
        //页面顶部币种切换
        super.setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
        setPaging(total, currentPage, pageSize);

    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

    @Page(Viewer = JSON)
    public void tongji() {
        try {
            //查询条件
            int currentPage = intParam("page");
            int pageSize = 20;
            String status = param("status");
            Timestamp confirmStartDate = dateParam("confirmStartDate");//确认时间(区间)
            Timestamp confirmEndDate = dateParam("confirmEndDate");//确认时间(区间)
            Timestamp startTime = dateParam("startDate");
            Timestamp endTime = dateParam("endDate");
            String userId = param("userId").trim();
            double moneyMin = doubleParam("moneyMin");
            double moneyMax = doubleParam("moneyMax");
            String commandId = param("commandId");

            CoinProps coin = coinProps();
            bdDao.setCoint(coin);
            Query<DownloadBean> query = bdDao.getQuery();

            query.setSql("select * from "+bdDao.getTableName());
            query.setCls(DownloadBean.class);

            String ids = param("eIds");
            boolean isAll = booleanParam("isAll");
            if (!isAll){
                if (ids.endsWith(",")) {
                    ids = ids.substring(0, ids.length() - 1);
                }
                query.append(" AND id IN (" + ids + ")");
            }
            if (status.length() == 0)
                status = "all";
            if (status.equals("wait")) {
                query.append(" AND  (status=0 and commandId = 0)");
            } else if (status.equals("confirm")) {
                query.append(" AND  (status=0 and commandId > 0)");
            } else if (status.equals("success")) {
                query.append(" AND  (status=2)");
            } else if (status.equals("fail")) {
                query.append(" AND  (status=1)");
            } else if (status.equals("cancel")) {
                query.append(" AND  (status=3)");
            } else if(status.equals("sendding")) {
                query.append(" AND  (status=7)");
            }
            if (startTime != null) {
                query.append(" and submitTime>=cast('" + startTime + "' as datetime)");
            }
            if (endTime != null) {
                query.append(" and submitTime<=cast('" + endTime + "' as datetime)");
            }

            if (confirmStartDate != null) {
                query.append(" and manageTime>=cast('" + confirmStartDate + "' as datetime)");
            }
            if (confirmEndDate != null) {
                query.append(" and manageTime<=cast('" + confirmEndDate + "' as datetime)");
            }
            if (userId.length() > 0) {
                query.append(" and userId= " + userId + " ");
            }
            if(moneyMin > 0){
                query.append(" and amount >=" + moneyMin);
            }
            if(moneyMax > 0){
                query.append(" and amount <=" + moneyMax);
            }
            if("0".equals(commandId)){
                query.append("and uuid not in (select downLoadId from autoDownloadRecords )  and commandId >0 ");
            }else if("1".equals(commandId)){
                query.append("and uuid in (select downLoadId from autoDownloadRecords )");
            }
            List<DownloadBean> list = bdDao.find();

            String pattern = "0.000000##";//格式代码
            DecimalFormat df = new DecimalFormat();
            df.applyPattern(pattern);

            BigDecimal totalMoney = BigDecimal.ZERO;
            BigDecimal totalMoney2 = BigDecimal.ZERO;
            for (DownloadBean bdb : list) {
                totalMoney = totalMoney.add(bdb.getAmount());
                totalMoney2 = totalMoney2.add(bdb.getAfterAmount());
            }
            JSONArray array = new JSONArray();
            array.add(df.format(totalMoney));
            array.add(totalMoney2);
            json("", true, array.toString());
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = "")
    public void exportWithdrawReport(){
        try {
            List<DownloadBean> needUser = getUserList();

            String [] column = {"id","userId","submitTime","manageTime","toAddress","currency","amount","afterAmount","fees","autoDownloadView","showStat","remark"};
            String [] tabHead = {"流水号","用户编号","提交时间","确认时间","提现地址","币种","数量","实际个数","手续费","打币类型","状态","备注"};//{"用户名","提交时间","提现地址","数量","状态"};
            HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
            OutputStream out = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_download_info.xls", "UTF-8"));
            response.setContentType("application/msexcel;charset=UTF-8");
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }


    public List<DownloadBean> getUserList(){
        //查询条件
        int currentPage = intParam("page");
        int pageSize = 20;
        String status = param("status");
        Timestamp confirmStartDate = dateParam("confirmStartDate");//确认时间(区间)
        Timestamp confirmEndDate = dateParam("confirmEndDate");//确认时间(区间)
        Timestamp startTime = dateParam("startDate");
        Timestamp endTime = dateParam("endDate");
        String userId = param("userId").trim();
        double moneyMin = doubleParam("moneyMin");
        double moneyMax = doubleParam("moneyMax");
        String commandId = param("commandId");

        CoinProps coin = coinProps();
        bdDao.setCoint(coin);
        String ids = param("eIds");
        boolean isAll = booleanParam("isAll");

        Query<DownloadBean> query = bdDao.getQuery();
        query.setSql("select * from "+bdDao.getTableName());
        query.setCls(DownloadBean.class);

        if(!isAll){
            if(ids.endsWith(",")){
                ids = ids.substring(0, ids.length()-1);
            }
            query.append(" AND id IN ("+ids+")");
        }
        if (status.length() == 0)
            status = "success";
        String order = "desc";
        if (status.equals("wait")) {
            order = "asc";
            query.append(" AND  (status=0 and commandId = 0)");
        } else if (status.equals("confirm")) {
            query.append(" AND  (status=0 and commandId > 0)");
        } else if (status.equals("success")) {
            query.append(" AND  (status=2)");
        } else if (status.equals("fail")) {
            query.append(" AND  (status=1)");
        } else if (status.equals("cancel")) {
            query.append(" AND  (status=3)");
        } else if(status.equals("sendding")) {
            query.append(" AND  (status=7)");
        }
        if(confirmStartDate != null){
            query.append(" and manageTime >= cast('"+confirmStartDate+"' as datetime)");
        }
        if(confirmEndDate != null){
            query.append(" and manageTime <= cast('"+confirmEndDate+"' as datetime)");
        }

        if(startTime != null){
            query.append(" and submitTime>=cast('"+startTime+"' as datetime)");
        }
        if(endTime != null){
            query.append(" and submitTime<=cast('"+endTime+"' as datetime)");
        }
        if (userId.length() > 0) {
            query.append(" and userId=" + userId + "");
        }

        if(moneyMin > 0){
            query.append(" and amount >=" + moneyMin);
        }
        if(moneyMax > 0){
            query.append(" and amount <=" + moneyMax);
        }
        if("0".equals(commandId)){
            query.append("and uuid not in (select downLoadId from autoDownloadRecords )  and commandId >0 ");
        }else if("1".equals(commandId)){
            query.append("and uuid in (select downLoadId from autoDownloadRecords )");
        }

        int total = query.count();
        if(total > 0){
            query.append(" ORDER BY submitTime " + order);
            List<DownloadBean> btcDownloads = query.getList();

            List<String> uuids = new ArrayList<String>();
            for (DownloadBean bdb : btcDownloads) {
                uuids.add(bdb.getUuid());//获取uuid
            }

            for (DownloadBean bdb : btcDownloads) {
                bdb.setCurrency(coint.getPropTag());
                Map<String,Long> uuidMaps = new AutoDownloadRecordDao().getAutoDownloadRecordId(uuids);
                if(uuidMaps.containsKey(bdb.getUuid())){
                    bdb.setAutoDownloadId(bdb.getId());
                }
            }
            return btcDownloads;
        }
        return null;
    }
}
