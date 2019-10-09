package com.world.model.dao;

import com.world.data.mongo.MongoDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.SysLog;
import com.world.model.entity.user.User;
import org.apache.log4j.Logger;

import java.sql.Timestamp;

/**
 * Created by xie on 2017/6/26.
 * 系统日志DAO
 */
public class SysLogDao extends MongoDao<SysLog, String> {

    Logger logger = Logger.getLogger(SysLogDao.class);

    // TODO: 2017/7/14 loginType 添加日志
    public String addSysLog(String userId, String logId, String batchId, String systemName, String modularName,int modularType, String logContent, String logUserId, String logUserName, String remark) {
//        UserDao userDao =  new UserDao();
//        User user = userDao.getUserById(userId);
        SysLog sysLog = new SysLog(this.getDatastore());
        sysLog.setLogId(logId);
        sysLog.setBatchId(batchId);
        sysLog.setSystemName(systemName);
        sysLog.setModularName(modularName);
        sysLog.setModularType(modularType);
        sysLog.setLogContent(logContent);
        sysLog.setLogUserId(logUserId);
        sysLog.setLogUserName(logUserName);
        sysLog.setLogTime(new Timestamp(System.currentTimeMillis()));
//        sysLog.setLogIp(user.getLoginIp());
        sysLog.setRemark(remark);
        String nid = super.save(sysLog).getId().toString();
        logger.info("系统日志表成功添加一条新数据，主键："+nid);
        return nid;
    }

    //添加数据
    public String addSysLog(SysLog sysLog){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        sysLog.setLogTime(ts);  //日志记录时间
        String nid = super.save(sysLog).getId().toString();
        logger.info("系统日志表成功添加一条新数据，主键："+nid);
        return nid;
    }



}
