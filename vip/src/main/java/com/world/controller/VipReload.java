package com.world.controller;

import com.alibaba.fastjson.JSONObject;
import com.api.config.ApiConfig;
import com.world.config.json.DbConfig;
import com.world.config.json.JsonConfig;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.connection;
import com.world.model.entity.Market;
import com.world.web.Page;
import com.world.web.action.AdminAction;

import java.util.HashMap;
import java.util.Map;

public class VipReload extends AdminAction {

    @Page(Viewer = JSON)
    public void reloadConfig() {
        try {

            StringBuilder success = new StringBuilder();
            StringBuilder error = new StringBuilder();

            // 1.重新加载config.json
            if (JsonConfig.reloadConfig()) {
                log.info("1.重新加载config.json成功");
                success.append("[1.重新加载config.json成功],");
            } else {
                log.error("1.重新加载config.json失败");
                error.append("[1.重新加载config.json失败],");
            }

            // 1.1 重新加载mysql.json
            if (DbConfig.reloadConfig()) {
                log.info("1.重新加载mysql.json成功");
                success.append("[1.重新加载mysql.json成功],");
            } else {
                log.error("1.重新加载mysql.json失败");
                error.append("[1.重新加载mysql.json失败],");
            }

            // 2.重新加载币种配置
            if (DatabasesUtil.reloadCoinProp()) {
                log.info("2.重新加载币种配置成功");
                success.append("[2.重新加载币种配置成功],");
            } else {
                log.error("2.重新加载币种配置失败");
                error.append("[2.重新加载币种配置失败],");

            }

            // 3.重新加载币种类型
            if (DatabasesUtil.reloadCoinPropFundsTypeMaps()) {
                log.info("3.重新加载币种类型成功");
                success.append("[3.重新加载币种类型成功],");
            } else {
                log.error("3.重新加载币种类型失败");
                error.append("[3.重新加载币种类型失败],");
            }

            // 4.获取数据库链接池
            if (connection.reloadMysql()) {
                log.info("4.重新加载币种类型成功");
                success.append("[4.增量获取数据库链接池成功],");
            } else {
                log.error("4.增量获取数据库链接池失败");
                error.append("[4.增量获取数据库链接池失败],");
            }

            // 5.重新加载市场配置（vip）
            if (Market.reloadMarket()) {
                log.info("5.重新加载市场配置（vip）成功");
                success.append("[5.重新加载市场配置（vip）成功],");
            } else {
                log.error("5.重新加载市场配置（vip）失败");
                error.append("[5.重新加载市场配置（vip）失败],");
            }

            // 6.重新加载api配置
            if (ApiConfig.reloadConfig()) {
                log.info("6.重新加载api配置成功");
                success.append("[6.重新加载api配置成功],");
            } else {
                log.error("6.重新加载api配置失败");
                error.append("[6.重新加载api配置失败],");
            }

            Map<String, String> result = new HashMap<>();
            result.put("success", success.toString());
            result.put("error", error.toString());

            if (error.toString().trim().length() > 0) {
                json("刷新配置失败", false, JSONObject.toJSONString(result));
            } else {
                json("刷新配置成功", true, JSONObject.toJSONString(result));
            }

        } catch (Exception e) {
            log.error("刷新配置失败", e);
            json("刷新配置失败", false, e.toString());
        }
    }

}
