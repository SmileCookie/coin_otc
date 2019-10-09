package com.world.model.dao.extend;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.extend.Invitation;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2019/3/102:06 PM
 */
public class InvitationDao extends DataDaoSupport<Invitation> {
    /**
     *
     * @param name
     * @param type
     * @param status
     * @param mobile
     * @param userName
     * @param wechat
     * @param platformLine
     * @param cooperateType
     * @return
     */
    public int insertInvitation(String name,String type,int status,String mobile,String userName,String wechat,String platformLine,String cooperateType){
        String sql = "insert into invitation (`name`, `type`,`status`, `mobile`, `userName`, `wechat`, `platformLine`, `cooperateType`, `createTime`) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] param = new Object[]{name, Integer.valueOf(type), status,mobile, userName, wechat,platformLine,cooperateType,now()};
        return Data.Insert(sql, param);
    }
}
