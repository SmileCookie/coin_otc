package com.world.model.dao.extend;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.extend.WalletCooperate;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/3/102:17 PM
 */
public class WalletCooperateDao extends DataDaoSupport<WalletCooperate> {
    /**
     * 插入
     * @param walletName
     * @param websitesLink
     * @param userName
     * @param wechat
     * @param cooperateType
     * @param status
     * @return
     */
    public int insertWalletCooperate(String walletName,String websitesLink,String userName,String wechat,String cooperateType,int status) {
        String sql = "insert into wallet_cooperate (`walletName`, `websitesLink`, `userName`, `wechat`, `cooperateType`, `createTime`, `status`) values ( ?, ?, ?, ?, ?, ?, ?)";
        Object[] param = new Object[]{walletName, websitesLink, userName, wechat,cooperateType,now(),status};
        return Data.Insert(sql, param);
    }
}
