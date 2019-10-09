package com.world.controller.admin.report.recharge;

import com.alibaba.fastjson.JSONArray;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.FundsDao;
import com.world.model.dao.pay.KeyDao;
import com.world.model.dao.pay.PayUserDao;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.wallet.WalletDao;
import com.world.model.dao.wallet.WalletDetailsDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.user.User;
import com.world.util.poi.ExcelManager;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@FunctionAction(jspPath = "/admins/report/recharge/" , des = "充值记录查询报表")
public class index extends FinanAction {

    private static final long serialVersionUID = 1L;

    private UserDao uDao = new UserDao();
    private DetailsDao bdDao = new DetailsDao();
    private FundsDao fundsDao = new FundsDao();
    WalletDetailsDao walletDetailsDao = new WalletDetailsDao();
    WalletDao walletDao = new WalletDao();
    KeyDao keyDao = new KeyDao();
    private PayUserDao payDao = new PayUserDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index(){
        //查询条件
        int currentPage = intParam("page");
        Timestamp startTime = dateParam("startTime");//提交时间
        Timestamp endTime = dateParam("endTime");
        Timestamp configStartDate = dateParam("configStartDate");//确认时间
        Timestamp configEndDate = dateParam("configEndDate");
        String status= param("status");
        String userId = param("userId");

        bdDao.setCoint(coint);
        Query<DetailsBean> query = bdDao.getQuery();
        query.setSql("select * from "+bdDao.getTableName());
        query.setCls(DetailsBean.class);


        if(startTime != null){
            query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
        }
        if(endTime != null){
            query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
        }

        if(configStartDate != null){
            query.append(" and configTime>=cast('"+configStartDate+"' as datetime)");
        }
        if(configEndDate != null){
            query.append(" and configTime<=cast('"+configEndDate+"' as datetime)");
        }

        if(StringUtil.exist(status)){
            if("10".equals(status)){
                query.append(" and Status=0 and type = 1 ");
            }else if("11".equals(status)){//失败
                query.append(" and Status=1 and type = 1 ");
            }else if("12".equals(status)){//成功
                query.append(" and Status=2 and type = 1 ");
            }
        }else{
            query.append(" and Status=2 and type = 1 ");
        }
        if(userId.length() > 0){
            query.append(" and userId = "+userId+" ");
        }
        int total = query.count();
        if(total > 0){
            query.append("order by sendTime desc");
            //分页查询
            List<DetailsBean> btcDetails = bdDao.findPage(currentPage, PAGE_SIZE);

            List<String> userIds = new ArrayList<String>();

            List<String> adminIds = new ArrayList<String>();
            for(DetailsBean bdb : btcDetails){
                userIds.add(bdb.getUserId()+"");
                if(bdb.getAdminId() > 0){
                    adminIds.add(bdb.getAdminId()+"");
                }
            }
            if(userIds.size()>0){
                Map<String, User> userMaps = uDao.getUserMapByIds(userIds);
                for(DetailsBean bdb : btcDetails){
                    bdb.setUser(userMaps.get(bdb.getUserId()+""));
                }
            }
            if(adminIds.size()>0){
                Map<String, AdminUser> userMaps = new AdminUserDao().getUserMapByIds(adminIds);
                for(DetailsBean bdb : btcDetails){
                    if(bdb.getAdminId() > 0){
                        bdb.setaUser(userMaps.get(bdb.getAdminId()+""));
                    }
                }
            }
            setAttr("dataList", btcDetails);
        }
        //页面顶部币种切换
        setAttr("coinMap", DatabasesUtil.getCoinPropMaps());
        setPaging(total, currentPage);
    }
    @Page(Viewer = DEFAULT_AJAX)
    public void ajax(){
        index();
    }

    @Page(Viewer = "")
    public void exportRechargeReport(){
        try {
            List<DetailsBean> needUser = getRechargeList();
            String [] column = {"detailsId","userId","inType","currency","amount","toAddr","showStatu","sendTime","configTime","remark"};//{"userName","submitTime","toAddress","amount","showStat"};
            String [] tabHead = {"充值编号","用户编号","交易类型","币种","金额","地址","状态","充值时间","确实时间","备注"};//{"用户名","提交时间","提现地址","数量","状态"};
            HSSFWorkbook workbook = ExcelManager.exportNormal(needUser, column, tabHead);
            OutputStream out = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment;filename="+ URLEncoder.encode("excel_recharge_record.xls", "UTF-8"));
            response.setContentType("application/msexcel;charset=UTF-8");
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    public List<DetailsBean> getRechargeList(){
        Timestamp startTime = dateParam("startTime");
        Timestamp endTime = dateParam("endTime");
//        int status=intParam("status");
        String status= param("status");
        String btcFrom = param("btcFrom");
        long entrustId = longParam("entrustId");
        String userId = param("userId");
        Timestamp configStartDate = dateParam("configStartDate");//确认时间
        Timestamp configEndDate = dateParam("configEndDate");

        bdDao.setCoint(coint);
        Query<DetailsBean> query = bdDao.getQuery();
        query.setSql("select * from "+bdDao.getTableName());
        query.setCls(DetailsBean.class);

        if(startTime != null){
            query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
        }
        if(endTime != null){
            query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
        }
        if(configStartDate != null){
            query.append(" and configTime>=cast('"+configStartDate+"' as datetime)");
        }
        if(configEndDate != null){
            query.append(" and configTime<=cast('"+configEndDate+"' as datetime)");
        }
        if(StringUtil.exist(status)){
            if("10".equals(status)){
                query.append(" and Status=0 and type = 1 ");
            }else if("11".equals(status)){//失败
                query.append(" and Status=1 and type = 1 ");
            }else if("12".equals(status)){//成功
                query.append(" and Status=2 and type = 1 ");
            }
        }else{
            query.append(" and Status=2 and type = 1 ");
        }
        if(btcFrom.length()>0){
            query.append(" and fromAddr="+btcFrom);
        }

        if(entrustId > 0){
            query.append(" and entrustId="+entrustId);
        }

        if(userId.length() > 0){
            query.append(" and userId = "+ userId +"");
        }

        int total = query.count();
        if(total > 0){
            query.append("order by sendTime desc");
            List<DetailsBean> btcDownloads = query.getList();
            for(DetailsBean vo :btcDownloads){
                vo.setCurrency(coint.getPropTag());
                if(vo.getType()==1){
                    if(StringUtils.isNotBlank(vo.getRemark())){
                        vo.setRemark(vo.getRemark()+"（确认次数："+vo.getConfirmTimes()+"）");
                    }else{
                        vo.setRemark("（确认次数："+vo.getConfirmTimes()+"）");
                    }
                }
            }
            return btcDownloads;
        }
        return null;
    }

    @Page(Viewer = JSON)
    public void tongji() {
        try {
            //查询条件
            String currentTab = param("tab");
            Timestamp startTime = dateParam("startTime");
            Timestamp endTime = dateParam("endTime");
//            int status=intParam("status");
            String status= param("status");
            String btcFrom = param("btcFrom");
            long entrustId = longParam("entrustId");
            String userId = param("userId");
            Timestamp configStartDate = dateParam("configStartDate");//确认时间
            Timestamp configEndDate = dateParam("configEndDate");

            bdDao.setCoint(coint);
            Query<DetailsBean> query = bdDao.getQuery();
            query.setSql("select * from "+bdDao.getTableName());
            query.setCls(DetailsBean.class);

            String ids = param("eIds");
            boolean isAll = booleanParam("isAll");

            if(isAll){
                if(currentTab.length()==0)
                    currentTab = "charge";

                request.setAttribute("currentTab", currentTab);

                if(startTime != null){
                    query.append(" and sendTime>=cast('"+startTime+"' as datetime)");
                }
                if(endTime != null){
                    query.append(" and sendTime<=cast('"+endTime+"' as datetime)");
                }
                if(StringUtil.exist(status)){
                    if("10".equals(status)){
                        query.append(" and Status=0 and type = 1 ");
                    }else if("11".equals(status)){//失败
                        query.append(" and Status=1 and type = 1 ");
                    }else if("12".equals(status)){//成功
                        query.append(" and Status=2 and type = 1 ");
                    }
                }else{
                    query.append(" and Status=2 and type = 1 ");
                }

                if(btcFrom.length()>0){
                    query.append(" and fromAddr="+btcFrom);
                }

                if(entrustId > 0){
                    query.append(" and entrustId="+entrustId);
                }

                if(configStartDate != null){
                    query.append(" and configTime>=cast('"+configStartDate+"' as datetime)");
                }
                if(configEndDate != null){
                    query.append(" and configTime<=cast('"+configEndDate+"' as datetime)");
                }

                if(userId.length() > 0){
                    query.append(" and userId = '"+ userId +"'");
                }
            }else{
                if(ids.endsWith(",")){
                    ids = ids.substring(0, ids.length()-1);
                }
                query.append(" AND detailsId IN ("+ids+")");
            }

            List<DetailsBean> list = bdDao.find();

            BigDecimal totalMoney = BigDecimal.ZERO;
            for(DetailsBean bdb : list){
                totalMoney = totalMoney.add(bdb.getAmount());
            }
            JSONArray array = new JSONArray();
            array.add(totalMoney);

            json("", true, array.toString());

        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }
}
