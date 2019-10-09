package com.world.controller.admin.jobdatedeal;

import com.world.model.job.dao.JobDefinDao;
import com.world.model.job.entity.JobDefinBean;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;

import java.sql.Time;
import java.util.List;

/**
 * Created by xie on 2017/10/20.
 */
@FunctionAction(jspPath = "/admins/jobdatedeal/" , des="定时任务管理")
public class Index extends FinanAction {
    private JobDefinDao dao = new JobDefinDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index(){
        List<JobDefinBean> list = dao.getJobDefinList();
        setAttr("dataList", list);
    }

    @Page(Viewer = DEFAULT_AJAX)
    public void ajax(){
        index();
    }

    @Page(Viewer = DEFAULT_AORU)
    public void aoru() {
        try {
            int id = intParam("id");
            if(id > 0){//新增
                JobDefinBean bean = dao.getJobDefinBeanById(id);
                setAttr("job",bean);
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = ".xml")
    public void doAoru() {
        try{
            long id = longParam("id");
//            String jobName = param("jobName");
            String jobStartTime = param("jobStartTime");
            String jobEndTime = param("jobEndTime");
//            String jobClass = param("jobClass");
//            int jobInterval = intParam("jobInterval");
            int jobStatus = intParam("jobStatus");
            String remark = param("remark");
            JobDefinBean bean = new JobDefinBean();

//            bean.setJobName(jobName);

            bean.setJobStartTime(jobStartTime);
            bean.setJobEndTime(jobEndTime);
//            bean.setJobClass(jobClass);
//            bean.setJobInterval(jobInterval);
            bean.setJobStatus(jobStatus);
            bean.setRemark(remark);
            if(id > 0){//更新
                bean.setId(id);
                dao.updateJobDefinBean(bean);
                WriteRight("操作成功");
            }else{//新增
                dao.insertJobDefinBean(bean);
                WriteRight("操作成功");
            }
        }catch (Exception e){
            WriteRight("操作失败");
        }

    }

    @Page(Viewer = ".xml")
    public void doDel() {
        long id = longParam("id");
        try{
            dao.deleteJobDefinBean(id);
            WriteRight("操作成功");
        }catch (Exception e){
            WriteRight("操作失败");
        }

    }


}
