package com.world.model.dao.report;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.report.ReportType;
import com.world.util.date.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ReportTypeDao
 * @Description
 * @Author kinghao
 * @Date 2018/8/7   17:37
 * @Version 1.0
 * @Description
 */
public class ReportTypeDao extends DataDaoSupport<ReportType> {

    private static Logger logger = Logger.getLogger(ReportTypeDao.class);

    /**
     * 获取报表控制
     */
    public List<ReportType> getAllReportType(int dealType) {
        String sql = "SELECT id , dealType ,attribute,type, `key` , initial , `start` , `end` , trueOrFalse FROM reporttype WHERE dealType = ?";
        List<ReportType> list = super.find(sql, new Object[]{dealType}, ReportType.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }


    public List<ReportType> getSumByCoinType(int dealType) {
        String sql = "SELECT id , dealType , attribute , type , `key` , sum(initial) AS initial, `start` , `end` , trueOrFalse FROM reporttype where dealType =? GROUP BY attribute";
        List<ReportType> list = super.find(sql, new Object[]{dealType}, ReportType.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }


    public int updateReportType(int initial, String updayeBy, int id) {
        String curr = TimeUtil.getFormatCurrentDateTime20();
        int count = super.update("update reporttype set initial=? ,updateTime= '" + curr + "',updayeBy=? where id=?",
                new Object[]{
                        initial, updayeBy, id
                });
        return count;
    }


    /**
     * 假数据
     * 当前操作必须通过真假数据校验
     **/
    public List<ReportType> forFalse(int dealType) {
        List<ReportType> reportTypes = this.getAllReportType(dealType);
        for (ReportType reportType : reportTypes) {
            //自增量
            int resultInt = (int) (Math.random() * ((Integer.valueOf(reportType.getEnd()) - Integer.valueOf(reportType.getStart())) + 1)
                    + Integer.valueOf(reportType.getStart())) + reportType.getInitial().intValue();
            reportType.setInitial(new BigDecimal(resultInt));
            this.updateReportType(resultInt, "admin", reportType.getId());
        }
        return reportTypes;

    }

    /**
     * 假数据
     * 当前操作必须通过真假数据校验
     **/
    public List<ReportType> forFalseUserOnline(int dealType) {
        List<ReportType> reportTypes = this.getAllReportType(dealType);
        for (ReportType reportType : reportTypes) {
            //自增量
            int resultInt = (int) (Math.random() * ((Integer.valueOf(reportType.getEnd()) - Integer.valueOf(reportType.getStart())) + 1)
                    + Integer.valueOf(reportType.getStart())) + reportType.getInitial().intValue();
            reportType.setInitial(new BigDecimal(resultInt));
        }
        return reportTypes;

    }


    public static void main(String[] args) {
        ReportTypeDao dao = new ReportTypeDao();
        dao.forFalse(3);
        System.out.println("结束！");
    }

}
