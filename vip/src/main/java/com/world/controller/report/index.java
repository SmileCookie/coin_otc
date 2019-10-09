package com.world.controller.report;

import com.world.cache.Cache;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @ClassName index
 * @Description
 * @Author kinghao
 * @Date 2018/8/11   14:49
 * @Version 1.0
 * @Description
 */
public class index extends BaseAction {


    /**
     * 控制前端页面加载方法
     */
    @Override
//    @Page(Viewer = "/cn/report/report.jsp")
    public void index() {

    }

    /**
     * 委托分布报表
     **/
    @Page(Viewer = JSON)
    public void queryEntrustmentDisstribution() {
        try {
            String entrustmentDis = Cache.Get("ENTRUSTMENT_DIS_WORK_KEY");
            JSONArray jsonArray = JSONArray.fromObject(entrustmentDis);
            json("", true, jsonArray.toString());
        } catch (Exception e) {
            json(L("内部异常"), false, "");
            log.error("获取委托分布缓存数据异常！");
        }
    }

    /**
     * 平台资金报表
     **/
    @Page(Viewer = JSON)
    public void queryPlatformFunds() {
        try {
            String entrustmentDis = Cache.Get("PLATFORM_FUNDS_WORK_KEY");
            JSONArray jsonArray = JSONArray.fromObject(entrustmentDis);
            json("", true, jsonArray.toString());
        } catch (Exception e) {
            json(L("内部异常"), false, "");
            log.error("获取平台资金报表缓存数据异常！");
        }
    }

    /**
     * 交易量分布报表
     **/
    @Page(Viewer = JSON)
    public void queryTransactionVolume() {
        try {
            String entrustmentDis = Cache.Get("TRANSACTION_VOLUM_WORK_KEY");
            JSONArray jsonArray = JSONArray.fromObject(entrustmentDis);
            json("", true, jsonArray.toString());
        } catch (Exception e) {
            json(L("内部异常"), false, "");
            log.error("获取交易量分布报表缓存数据异常！");
        }
    }


    /**
     * 用户分布报表
     **/
    @Page(Viewer = JSON)
    public void queryUserDistribution() {
        try {
            String internationalization = param("internationalization");
            String entrustmentDis="";
            if (!StringUtil.exist(internationalization)){
                json(L("国际化标识为空！"), false, internationalization);
            }
            if ("CN".equals(internationalization.toUpperCase())){
                entrustmentDis = Cache.Get("USER_DISTRIBUTION_WORK_CN_KEY");
            }else if("EN".equals(internationalization.toUpperCase())){
                entrustmentDis = Cache.Get("USER_DISTRIBUTION_WORK_EN_KEY");
            }else{
                entrustmentDis = Cache.Get("USER_DISTRIBUTION_WORK_HK_KEY");
            }
            JSONArray jsonArray = JSONArray.fromObject(entrustmentDis);
            json("", true, jsonArray.toString());
        } catch (Exception e) {
            json(L("内部异常"), false, "");
            log.error("用户分布报表缓存数据异常！");
        }
    }

    /**
     * 用户在线数量报表
     **/
    @Page(Viewer = JSON)
    public void queryUserOnline() {
        try {
            String entrustmentDis = Cache.Get("USER_ONLINE_WORK_KEY");
            JSONObject jsonObject = JSONObject.fromObject(entrustmentDis);
            json("", true, jsonObject.toString());
        } catch (Exception e) {
            json(L("内部异常"), false, "");
            log.error("用户在线报表缓存数据异常！");
        }
    }
}
