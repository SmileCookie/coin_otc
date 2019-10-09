package com.world.controller.admin.capmonitor;

import java.sql.Timestamp;
import java.util.List;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.entity.usercap.entity.AbnUserRecordBean;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.model.entity.usercap.entity.UserCapMonitorBean;
import com.world.model.entity.usercap.dao.UserCapMonitorDao;
import com.world.model.entity.usercap.dao.AbnUserRecordDao;
import com.world.web.response.DataResponse;

@FunctionAction(jspPath = "/admins/capmonitor/" , des = "资金监控")
public class Index extends AdminAction{

    UserCapMonitorDao dao = new UserCapMonitorDao();
    AbnUserRecordDao recordDao = new AbnUserRecordDao();
    @Page(Viewer = DEFAULT_INDEX)
    public void index(){
        int currentPage = intParam("page");
        int fundsType = intParam("fundsType");     //资金类型
        int checkResult = intParam("checkResult"); //检查结果
        Timestamp startTime = dateParam("startTime");//开始时间
        Timestamp endTime = dateParam("endTime");//结束时间

        Query query = dao.getQuery();
        query.setSql("select * from mon_usercapmonitor");
        query.setCls(UserCapMonitorBean.class);
        //查询附加条件
        if(fundsType > 0){
            query.append(" and fundsType = " + fundsType);
        }
        if(checkResult > 0){
            query.append(" and checkResult = " + checkResult);
        }

        if(startTime != null){
            query.append(" and monTime >= cast('"+startTime+"' as datetime)");
        }
        if(endTime != null){
            query.append(" and monTime <= cast('"+endTime+"' as datetime)");
        }

        query.append(" order by monTime desc");

        int total = query.count();
        if(total > 0){
            List<UserCapMonitorBean> userCapMonitorBeans = query.getPageList(currentPage, 18);//提现记录
            request.setAttribute("dataList", userCapMonitorBeans);
            setAttr("itemCount", total);
        }
        setPaging(total, currentPage);
        setAttr("ft", DatabasesUtil.getCoinPropMaps());
    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax(){
        index();
    }


    @Page(Viewer = DEFAULT_AORU)
    public void aoru() {
        int currentPage = intParam("page");
        try {
            Query query = recordDao.getQuery();
            String ucmId = param("ucmId");
            query.setSql("select * from mon_abnuserrecord where  ucmId = '" + ucmId + "'");
            query.setCls(AbnUserRecordBean.class);

            int total = query.count();

            if(total > 0){
                List<Bean> abnUserRecordList = query.getPageList(currentPage, 6);
                request.setAttribute("dataList", abnUserRecordList);
                setAttr("itemCount", total);
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    //保存处理备注
    @Page(Viewer = JSON)
    public void saveRemark(){
        String id = param("id");
        String dealreamark = param("dealreamark");
        DataResponse dr = new DataResponse();
        try{
            Query query = new Query("update mon_userCapMonitor set dealRemark='" + dealreamark + "' where id=" + id, null,UserCapMonitorBean.class);
            query.update();
            json("操作成功！", true, "");
        }catch(Exception e){
            json("操作失败！", false, "");
        }
        return;
    }

    //保存监控处理备注
    @Page(Viewer = JSON)
    public void saveMonRemark(){
        String id = param("id");
        String dealreamark = param("dealreamark");
        try{
            Query query = new Query("update mon_abnUserRecord set dealRemark='" + dealreamark + "' where id=" + id, null,AbnUserRecordBean.class);
            query.update();
            json("操作成功！", true, "");
        }catch(Exception e){
            json("操作失败！", false, "");
        }
    }


}
