package com.world.model.dao.recharge;

import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.recharge.ChargeManagement;
import java.util.List;

/**
 * Created by Zero on 2019/1/23.
 */
public class ChargeManagementDao extends DataDaoSupport<ChargeManagement> {

    public List<ChargeManagement> findList(){
        return (List<ChargeManagement>)super.find("SELECT * FROM charge_management", new Object[]{}, ChargeManagement.class);
    }

}
