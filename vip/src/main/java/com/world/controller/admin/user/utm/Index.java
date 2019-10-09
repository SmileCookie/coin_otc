package com.world.controller.admin.user.utm;

import com.google.code.morphia.query.Query;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.user.User;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.*;

/**
 * Created by xie on 2017/10/9.
 */
@FunctionAction(jspPath = "/admins/user/utm/", des = "来源统计")
public class Index extends AdminAction {
    UserDao userDao = new UserDao();

    @Page(Viewer = DEFAULT_INDEX)
    public void index() {
        String utm_medium = param("utm_medium");//媒介
        String utm_source = param("utm_source");//来源
        Date startDate = dateParam("startDate");//开始时间
        Date endDate = dateParam("endDate");//结束时间

        Query<User> q = userDao.getQuery();
        try{
            if(StringUtils.isNotEmpty(utm_medium)){
                q.filter("utmMedium", utm_medium);
            }
            if(StringUtils.isNotEmpty(utm_source)){
                q.filter("utmSource", utm_source);
            }
            if(startDate != null) {
                q.filter("registerTime >= ", startDate);
            }
            if(endDate != null) {
            q.filter("registerTime <= ", endDate);
        }
            q.filter("utmMedium <>", null);//不能为空
            q.order("utmMedium");
            q.order("utmSource");
            // TODO: 2017/10/9 时间过滤
            List<User> list = q.asList();
            Map<String,Integer> utmMap = new LinkedHashMap<>();
            List<String> mediumList = new ArrayList<>();
            List<String> sourceList = new ArrayList<>();
            for(User user : list){
                if(StringUtils.isNotEmpty(user.getUtmMedium()) && !mediumList.contains(user.getUtmMedium())){
                    mediumList.add(user.getUtmMedium());
                }
                if(StringUtils.isNotEmpty(user.getUtmSource()) && !sourceList.contains(user.getUtmSource())){
                    sourceList.add(user.getUtmSource());
                }
                String key = user.getUtmMedium() + "_" + user.getUtmSource();
                if(utmMap.containsKey(key)){
                    int count = utmMap.get(key) + 1;
                    utmMap.put(key,count);
                }else{
                    utmMap.put(key,1);
                }
            }
            setAttr("utmMap", utmMap);
            setAttr("mediumList", mediumList);
            setAttr("sourceList", sourceList);
        }catch (Exception e){
            log.error("查询来源统计报表出错，错误信息："+ e);
        }

    }
    // ajax的调用
    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

}
