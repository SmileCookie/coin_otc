package com.world.model.dao.autofactory;

import com.google.common.base.Joiner;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.autodownload.AutoDownloadRecordBean;
import com.world.model.entity.pay.DownloadBean;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xie on 2017/10/17.
 */
public class AutoDownloadRecordDao extends DataDaoSupport{

    /**
     * 根据downloadId查询id
     * @param uuids
     * @return
     */
    public Map<String, Long> getAutoDownloadRecordId(List<String> uuids){
        Map<String, Long> autoDownloadMap = new HashMap<>();
        String uuidsStr =  "'" + Joiner.on("','").join(uuids) +  "'";
        String sql = "select * from autoDownloadRecords where downloadId in ( "+uuidsStr+" )";
        List<AutoDownloadRecordBean> list = super.find(sql, new Object[]{}, AutoDownloadRecordBean.class);
        for(AutoDownloadRecordBean bean : list) {
            autoDownloadMap.put(bean.getDownloadId(),bean.getId());
        }
        return autoDownloadMap;
    }

//    `id` int(11) NOT NULL AUTO_INCREMENT,
//    `batchId` varchar(32) comment '批次编号:币种+时间戳',
//            `downloadId` bigint(20) DEFAULT '0' comment '比特币提币表主键',
//            `userId` varchar(20) DEFAULT '' COMMENT '用户ID',
//            `userName` varchar(50) DEFAULT '' COMMENT '用户名',
//            `fundsType` int(6) DEFAULT '0' COMMENT '资金类型 2:比特币   5:以太币   6:ETC，根据config.json配置',
//            `createTime` DATETIME comment '创建日期',
//            `createUserId` varchar(30) DEFAULT '' comment '创建人userid',
//            `modifyTime` DATETIME comment '修改日期',
//            `modifyUserId` varchar(30) DEFAULT '' comment '修改人userid',
//    PRIMARY KEY (`id`),
//    key `autoDownloadRecords_batchId_index` (`batchId`),
//    key `autoDownloadRecords_downloadId_index` (`downloadId`)
//
    /**
     * 新增自动打币记录
     * @param bean
     */
    public void insertAutoDownloadRecordBean(DownloadBean bean,int fundType){
        try{
            String sql = "insert into `vip_main`.`autodownloadrecords` ( `downloadId`, `batchId`, `userId`, `userName`, `amount`, `fundsType`, `submitTime`, `createTime`) values ( ?, ?,?, ?, ?, ?, ?, ?)";
            Object[] param = new Object[] { bean.getUuid(), bean.getBatchId(), bean.getUserId(), bean.getUserName(), bean.getAmount(),fundType,bean.getSubmitTime(),new Timestamp(System.currentTimeMillis())};
            Data.Insert(sql, param);
        }catch (Exception e){
            log.error("新增自动打币(币种代码：" + fundType + ")记录出错，错误信息：" + e.toString());
        }

    }




}
