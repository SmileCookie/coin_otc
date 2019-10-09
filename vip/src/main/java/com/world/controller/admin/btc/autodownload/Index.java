package com.world.controller.admin.btc.autodownload;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.dao.autofactory.AutoDownloadRecordDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.entity.autodownload.AutoDownloadRecordBean;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by xie on 2017/10/19.
 */
@FunctionAction(jspPath = "/admins/btc/autodownload/" , des="小额自动打币记录")
public class Index extends FinanAction {

    private AutoDownloadRecordDao autoDownloadRecordDao = new AutoDownloadRecordDao();
    private DownloadDao downloadDao = new DownloadDao();
    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        //查询条件
        int currentPage = intParam("page");
        int pageSize = 50;
        String status = param("status");
        Timestamp startTime = dateParam("startDate");//提交开始时间
        Timestamp endTime = dateParam("endDate");    //提交结束时间
        Timestamp confirmStartDate = dateParam("confirmStartDate");//打币结束时间
        Timestamp confirmEndDate = dateParam("confirmEndDate");//打币开始时间

        int fundType = intParam("fundType");
        coint = DatabasesUtil.coinProps(fundType);
        String downloadTableName = coint.getDatabaseKey() == null ? "btcdownload" : coint.getDatabaseKey() + "download";
        String sql = "SELECT" +
        " autoDownloadRecords.batchId batchId," +
        " autoDownloadRecords.id id," +
        " autoDownloadRecords.downloadId downloadId," +
        " autoDownloadRecords.userName userName," +
        " autoDownloadRecords.amount amount," +
        " autoDownloadRecords.submitTime submitTime," +
        " autoDownloadRecords.createTime createTime" +
//        " "+downloadTableName+ ".`status`" +
        " FROM" +
        " autoDownloadRecords" +
        " LEFT JOIN " + downloadTableName + " ON autoDownloadRecords.downloadId = "+downloadTableName+ ".uuid" +
        " where 1=1";

        //提现状态
        if (status.equals("confirm")) {
            sql += " AND "+downloadTableName+ ".status=0 and "+downloadTableName+ ".commandId > 0";
        } else if (status.equals("success")) {
            sql += " AND "+downloadTableName+ ".status=2";
        } else if (status.equals("fail")) {
            sql += " AND "+downloadTableName+ ".status=1";
        } else if (status.equals("sendding")) {
            sql += " AND "+downloadTableName+ ".status=1";
        }


        //提交时间
        if (startTime != null) {
            sql += " and "+downloadTableName+ ".submitTime>=cast('" + startTime + "' as datetime)";
        }

        if (endTime != null) {
            sql += " and "+downloadTableName+ ".submitTime<=cast('" + endTime + "' as datetime)";
        }

        //打币时间
        if (confirmStartDate != null) {
            sql += " and autoDownloadRecords.createTime>=cast('" + confirmStartDate + "' as datetime)";
        }

        if (confirmEndDate != null) {
            sql += " and autoDownloadRecords.createTime<=cast('" + confirmEndDate + "' as datetime)";
        }
        sql += " order by autoDownloadRecords.submitTime desc";
        Query<AutoDownloadRecordBean> query = autoDownloadRecordDao.getQuery();
        query.setCls(AutoDownloadRecordBean.class);
        query.setSql(sql);
        int total = query.count();
        if (total > 0) {
            //分页查询
            List<AutoDownloadRecordBean> list = autoDownloadRecordDao.findPage(currentPage, pageSize);
            super.setAttr("dataList", list);
        }
        //页面顶部币种切换
        super.setAttr("ft", DatabasesUtil.getCoinPropMaps());
        setPaging(total, currentPage, pageSize);


    }
    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }
}
