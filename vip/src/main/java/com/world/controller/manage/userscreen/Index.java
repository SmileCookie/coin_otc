package com.world.controller.manage.userscreen;

import com.atlas.BizException;
import com.world.data.mysql.Data;
import com.world.model.dao.user.UserScreenDao;
import com.world.model.entity.user.UserScreen;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName index
 * @Description
 * @Author kinghao
 * @Date 2018/7/31   14:01
 * @Version 1.0
 * @Description
 */
public class Index extends UserAction {


    private static final long serialVersionUID = 1L;
    private UserScreenDao userScreenDao = new UserScreenDao();


    public static void main(String[] args) {
        Index index = new Index();
        index.operatingScreens();

    }

    /**
     * 查询
     **/
    @Page(Viewer = JSON)
    public void operatingScreens() {
        List<UserScreen> userScreens = new ArrayList<>();
        try {
            UserScreen userScreen = new UserScreen();
            userScreen.setOperationType(Integer.valueOf(param("operationType")));

            //查询
            if (userScreen.getOperationType() == 1) {
                userScreen.setUserId(userId());
                userScreens = userScreenDao.sendUserScreen(userScreen);
                if (userScreens.size() > 0) {
                    List<String> list = new ArrayList<>();
                    for (UserScreen screen : userScreens) {
                        list.add(screen.getMultiScreen());
                    }
                    json("success", true, com.alibaba.fastjson.JSONObject.toJSON(list).toString());

                }else{

                        StringBuilder insertBatchSql = new StringBuilder("insert into userscreen (userId, multiScreen, groupByScreen, createTime, createBy) values ");
                        for (int j = 0; j < 4; j++) {
                            insertBatchSql.append("(").append(userId()).append(",'',").append(j+1).append(",'").append(now()).append("',").append(userId()).append("),");
                        }
                        insertBatchSql = insertBatchSql.deleteCharAt(insertBatchSql.length() - 1).append(";");
                        Data.Insert(insertBatchSql.toString(), new Object[]{});


                }
             //新增
            } else if (userScreen.getOperationType() == 2) {
                userScreen.setUserId(userId());
                userScreen.setMultiScreen(param("multiScreen"));
                userScreen.setGroupByScreen(Integer.valueOf(param("groupByScreen")));
                userScreen.setCreateTime(new Timestamp(System.currentTimeMillis()));
                userScreen.setCreateBy(userId());
                log.info("[新增看板]   用户" + userId() + "，看板：" + param("multiScreen"));
                Integer integer = userScreenDao.saveUserScreen(userScreen);
                json("success", true, integer.toString());
                //替换screen
            } else if (userScreen.getOperationType() == 4) {
                userScreen.setUserId(userId());
                userScreen.setMultiScreenOld(param("multiScreenOld"));
                userScreen.setGroupByScreen(Integer.valueOf(param("groupByScreen")));
                userScreen.setMultiScreen(param("multiScreen"));
                userScreen.setCreateBy(userId());
                userScreen.setCreateTime(new Timestamp(System.currentTimeMillis()));
                Integer yorn = userScreenDao.updateUserScreen(userScreen);
                json("success", true, yorn.toString());
                //删除
            } else {
                userScreen.setUserId(userId());
                userScreen.setMultiScreen(param("multiScreen"));
                userScreen.setGroupByScreen(Integer.valueOf(param("groupByScreen")));
                userScreen.setCreateBy(userId());
                userScreen.setCreateTime(new Timestamp(System.currentTimeMillis()));
                Boolean yorn = userScreenDao.deleteUserScreen(userScreen);
                json("success", true, yorn.toString());
            }
        } catch (BizException e) {
            log.error("【多屏看板】操作活动发生受控异常，异常信息为:" + e.getMessage());
        } catch (Exception e) {
            log.error("【多屏看板】操作活动发生非受控异常，异常信息为:", e);
        }
    }

    @Override
//    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

}
