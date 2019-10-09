package com.world.model.dao.pay;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.pay.SmallPayManagementBean;

import java.util.List;

/**
 * Created by Zero on 2019/4/4.
 */
public class SmallPayManagementDao extends DataDaoSupport<SmallPayManagementBean> {

    public List<SmallPayManagementBean> findList(){
        return (List<SmallPayManagementBean>)super.find("SELECT * FROM small_pay_management", new Object[]{}, SmallPayManagementBean.class);
    }

}
