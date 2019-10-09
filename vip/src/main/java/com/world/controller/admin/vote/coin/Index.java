package com.world.controller.admin.vote.coin;

import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Query;
import com.world.model.dao.vote.CoinDao;
import com.world.model.entity.vote.Coin;
import com.world.web.Page;
import com.world.web.action.FinanAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.log4j.Logger;

import java.util.List;

@FunctionAction(jspPath = "/admins/vote/coin/", des = "币种查看")
public class Index extends FinanAction {
    Logger logger = Logger.getLogger(Index.class.getName());
    CoinDao coinDao = new CoinDao();


    @Page(Viewer = "/admins/vote/coin/coinAjax.jsp")
    public void ajax() {
        index();
    }

    @Page(Viewer = "/admins/vote/coin/coin.jsp")
    public void index() {
        try {
            int currentPage = intParam("page");
            int pageSize = 10;
            String coinName = param("coinName");
            String lan = param("lan");
            if (null == lan || "".equals(lan)) {
                lan = "cn";
            }
            Query logQuery = coinDao.getQuery();
            logQuery.setCls(Coin.class);
            logQuery.setSql("select * from coin");
            if (!"".equals(coinName) && null != coinName) {
                logQuery.append(" AND coinNameJson like '%" + coinName + "%'");
            }
            int total = coinDao.count();
            List<Coin> coinList = coinDao.findPage(currentPage, pageSize);
            for (Coin coin : coinList) {
                JSONObject coinNameJson = JSONObject.parseObject(coin.getCoinNameJson());
                JSONObject coinFullNameJson = JSONObject.parseObject(coin.getCoinFullNameJson());
                JSONObject urlJson = JSONObject.parseObject(coin.getUrlJson());
                coin.setCoinNameJson(coinNameJson.getString(lan));
                coin.setCoinFullNameJson(coinFullNameJson.getString(lan));
                coin.setUrlJson(urlJson.getString(lan));
            }
            setAttr("coinList", coinList);
            setPaging(total, currentPage, pageSize);
        } catch (Exception e) {
            logger.error("进入币库页面失败", e);
        }


    }

}
