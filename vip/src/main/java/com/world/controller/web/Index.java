package com.world.controller.web;

import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.model.dao.msg.NewsDao;
import com.world.model.entity.msg.News;
import com.world.util.cookie.CookieUtil;
import com.world.util.ip.IpUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import java.sql.Timestamp;
import java.util.List;

public class Index extends BaseAction {

    private static final long serialVersionUID = 1L;

    @Page(Viewer = "/cn/room.jsp")
    public void room() {
        setAttr("coint", coint);
        long currentTime = System.currentTimeMillis();
        setAttr("currentime", currentTime);
        boolean isSale = false;
        setAttr("isSale", isSale);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        NewsDao nd = new NewsDao();
        List<News> rds = nd.findPage(nd.getQuery(News.class).filter("type <>", 1).filter("type <>", 2).order("-isTop,-topTime,-pubTime"), 1, 6);

        //增加发布时间判断和置顶排序 zhanglinbo 20170210
        List<News> ggs = nd.findPage(nd.getQuery(News.class).filter("type", 1).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
        List<News> news4seo = nd.findPage(nd.getQuery(News.class).filter("type", 2).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
        List<News> newTitles = nd.findPage(nd.getQuery(News.class).filter("language", lan).filter("type", 1).filter("pubTime <=", now).order("-isTop,-topTime,-pubTime"), 1, 6);
        setAttr("ggs", ggs);
        setAttr("rds", rds);
        setAttr("news4seo", news4seo);
        if (newTitles != null && newTitles.size() > 0 && newTitles.get(0) != null) {
            setAttr("newTitle", newTitles.get(0).getTitle());
            setAttr("newTitleId", newTitles.get(0).getId());
        } else {
            setAttr("newTitle", "");
            setAttr("newTitleId", "");
        }

        String ip = IpUtil.getIp(request);
        CookieUtil cookieUtil = new CookieUtil(request, response);
        String currencyCur = cookieUtil.getCookieValue("currency");
        if (!StringUtil.exist(currencyCur)) {
            String currency = "USD";
            String paramCurrency = param("currency");
            //国家
            String province = "";
            if (!StringUtil.exist(paramCurrency)) {
                //是否是外网IP
                if (IpUtil.isIpv4(ip) && IpUtil.isPublicIp(ip)) {
                    if (StringUtils.isNotBlank(Cache.Get("ip_currency_" + ip))) {
                        currency = Cache.Get("ip_currency_" + ip);
                    } else {
                        province = IpUtil.getProvinceBySina(ip);
                        if ("中国".equals(province)) {
                            currency = "CNY";
                        } else {
                            currency = "USD";
                        }
                        Cache.Set("ip_currency_" + ip, currency, 5 * 60);
                    }
                }
            } else {
                currency = paramCurrency.toUpperCase();
            }
            Cookie userCurrency = new Cookie("currency", currency);
            userCurrency.setMaxAge(60 * 60 * 2);// s为单位，1个月60*60*24,存储一天
            userCurrency.setDomain(Session.SETDOMAIN);
            userCurrency.setPath("/");
            response.addCookie(userCurrency);
        }


        String lanCur = cookieUtil.getCookieValue(SsoSessionManager.LANGUAGE);
        if (!StringUtil.exist(lanCur)) {
            try {
                String curLan = "en";
                /* TODO 目前只支持英文，如果国际化，需要打开这个配置 add by buxianguan 20190306
                //国家
                String province = "";
                //是否是外网IP
                if (IpUtil.isIpv4(ip) && IpUtil.isPublicIp(ip)) {
                    if (StringUtils.isNotBlank(Cache.Get("ip_" + ip))) {
                        curLan = Cache.Get("ip_" + ip);
                    } else {
                        province = IpUtil.getProvinceBySina(ip);
                        if ("中国".equals(province)) {
                            curLan = "cn";
                        } else if ("香港".equals(province) || "澳门".equals(province) || "台湾".equals(province)) {
                            curLan = "hk";
                        } else {
                            curLan = "en";
                        }
                        Cache.Set("ip_" + ip, curLan, 5 * 60);
                    }
                }*/
                setAttr("lan", curLan);
                Cookie lanCookie = new Cookie(SsoSessionManager.LANGUAGE, curLan);
                lanCookie.setHttpOnly(false);
                //失效时间7天
                lanCookie.setMaxAge(60 * 60 * 24 * 7);
                lanCookie.setPath("/");
                lanCookie.setDomain(GlobalConfig.baseDomain);
                response.addCookie(lanCookie);
            } catch (Exception e) {
                log.error("VIP设置语言失败，失败信息为：", e);
            }
        }

    }

}

