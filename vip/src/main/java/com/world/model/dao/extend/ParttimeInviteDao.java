package com.world.model.dao.extend;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.extend.ParttimeInvite;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/3/102:17 PM
 */
public class ParttimeInviteDao extends DataDaoSupport<ParttimeInvite> {
    /**
     * 插入
     * @param name
     * @param mobile
     * @param wechat
     * @param walletAddress
     * @param applyPost
     * @param status
     * @return
     */
    public int insertParttimeInvite(String name,String mobile,String wechat,String walletAddress,String applyPost,int status) {
        String sql = "insert into parttime_invite (`name`, `mobile`, `wechat`, `walletAddress`, `applyPost`, `createTime`, `status`) values ( ?, ?, ?, ?, ?, ?, ?)";
        Object[] param = new Object[]{name, mobile, wechat, walletAddress, applyPost,now(),status};
        return Data.Insert(sql, param);
    }
}
