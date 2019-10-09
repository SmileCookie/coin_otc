package com.world.model.job.dao;

import java.util.Date;
import java.util.List;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.job.entity.JobDefinBean;

/**
 * Created by xie on 2017/10/17.
 */
public class JobDefinDao extends DataDaoSupport<JobDefinBean> {


    /**
     * 根据任务类查询对应的任务
     *
     * @param jobClass
     * @return
     */
    public JobDefinBean getJobDefinBeanByJobClass(String jobClass, int jobStatus) {

        String sql = "select * from jobDefin where jobClass = ? and jobStatus = ? ";
        List<JobDefinBean> list = super.find(sql, new Object[]{jobClass, jobStatus}, JobDefinBean.class);
        if (null != list && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public List<JobDefinBean> getJobDefinList() {
        String sql = "select * from jobDefin";
        List<JobDefinBean> list = super.find(sql, new Object[]{}, JobDefinBean.class);
        return list;
    }

    public JobDefinBean getJobDefinBeanById(int id) {
        String sql = "select * from jobDefin where id = ?";
        List<JobDefinBean> list = super.find(sql, new Object[]{id}, JobDefinBean.class);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public void updateJobDefinBean(JobDefinBean bean) {
//        String sql = "update jobdefin set jobName = ?, jobStartTime= ?, jobEndTime = ?, jobInterval = ?, jobStatus = ?, remark = ?, jobClass =? where id = ?";
//        super.update(sql,new Object[]{bean.getJobName(),bean.getJobStartTime(),bean.getJobEndTime(),bean.getJobInterval(),bean.getJobStatus(),bean.getRemark(),bean.getJobClass(),bean.getId()});
        String sql = "update jobdefin set jobStartTime= ?, jobEndTime = ?, jobStatus = ?, remark = ? where id = ?";
        super.update(sql,new Object[]{bean.getJobStartTime(), bean.getJobEndTime(), bean.getJobStatus(), bean.getRemark(), bean.getId()});

    }

    public void insertJobDefinBean(JobDefinBean bean) {
        String sql = "insert into jobdefin (jobName,jobStartTime,jobEndTime,jobInterval,jobStatus,remark,jobClass,createTime) values(?,?,?,?,?,?,?,now())";
        super.save(sql,new Object[]{bean.getJobName(),bean.getJobStartTime(),bean.getJobEndTime(),bean.getJobInterval(),bean.getJobStatus(),bean.getRemark(),bean.getJobClass(),bean.getId()});
    }

    public void deleteJobDefinBean(long id){
        super.delete("delete from jobdefin where id = ?", new Object[]{id});
    }

}
