package com.world.model.dao.user;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.user.UserScreen;
import com.world.util.string.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName UserScreenDao
 * @Description
 * @Author kinghao
 * @Date 2018/7/28   16:39
 * @Version 1.0
 * @Description
 */
public class UserScreenDao extends DataDaoSupport<UserScreen> {
    public UserScreenDao() {
    }

    /**
     * 根据用户ID获取数量
     **/
    public long getScreenByUserId(String userId) {
        List<Long> list = (List<Long>) Data.GetOne("select count(*) from userscreen where userId = ?", new Object[]{userId});
        if (list == null) {
            return 0;
        } else {
            return list.get(0);
        }
    }

    /**
     * 根据用户ID 插入模板配置
     **/
    public int saveUserScreen(UserScreen userScreen) {
        Integer saveCount = Data.Update("update  userscreen set multiScreen= ? where userId =? and groupByScreen=?",
                new Object[]{userScreen.getMultiScreen(), userScreen.getUserId(), userScreen.getGroupByScreen()});
        return saveCount;
    }

    /**
     * 根据用户ID  模板  删除
     **/
    public boolean deleteUserScreen(UserScreen userScreen) {
        Integer saveCount = Data.Update("update  userscreen set multiScreen= ? where userId =? and groupByScreen=?",
                new Object[]{"", userScreen.getUserId(), userScreen.getGroupByScreen()});
        if (saveCount > 0) {
            return true;
        }
        return false;
    }

    /**
     * 根据用户ID 查询所有模板
     **/
    public List<UserScreen> sendUserScreen(UserScreen userScreen) {
        String sql = "select multiScreen from userscreen  where userId=?  order by groupByScreen";
        List<UserScreen> list = Data.QueryT(sql, new Object[]{userScreen.getUserId()}, UserScreen.class);
        return list;
    }

    /**
     * 根据用户ID原模板 修改模板类型
     **/
    public int updateUserScreen(UserScreen userScreen) {
        Integer saveCount = Data.Update("update  userscreen set multiScreen= ? where userId =? and groupByScreen=?",
                new Object[]{userScreen.getMultiScreen(), userScreen.getUserId(), userScreen.getGroupByScreen()});
        return saveCount;
    }


    public static void main(String[] args) {
        String screen = "---";
        String userId="1003264";
        String cookieValue="BTC/USDT--BTC/USDT- ";
        //1.2 Y:存在的情况下，查询缓存是否存在用户看板信息。
        UserDao userDao= new UserDao();
        userDao.getUserScreen(userId,cookieValue);
        UserScreenDao userScreenDao= new UserScreenDao();
        UserScreen userScreen= new UserScreen();
        userScreen.setUserId(1003264);
        List<UserScreen>  userScreens = userScreenDao.sendUserScreen(userScreen);
        if (userScreens.size() > 0) {
            List<String> lista = new ArrayList<>();
            for (UserScreen screens : userScreens) {
                lista.add(screens.getMultiScreen());
            }
            System.out.println("====================================="+JSONObject.toJSON(lista).toString());

        }

    }
}
