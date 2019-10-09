package com.world.model.quanttrade.dao;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.record.QtTransRecord;

/**
 * Created by suxinjie on 2017/8/21.
 */
public class QtTransRecordDao extends DataDaoSupport<QtTransRecord> {

    public QtTransRecordDao() {
        setDatabase("vip_main");
    }


}
