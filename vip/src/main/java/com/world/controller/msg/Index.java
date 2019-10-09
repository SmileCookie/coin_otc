package com.world.controller.msg;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.QueryResults;
import com.world.cache.Cache;
import com.world.model.dao.msg.NewsDao;
import com.world.model.entity.msg.News;
import com.world.model.entity.msg.NewsType;
import com.world.util.date.TimeUtil;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.action.UserAction;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class Index extends UserAction {
    private static final String MEM_NOTICE_KEY_PREFIX = "user_notice_";
    static Logger logger = Logger.getLogger(Index.class.getName());
    NewsDao nd = new NewsDao();

    //
    //获取公告列表
    //@ActionCache(pageCache = true, cacheTime=300 , maxPage = 2)
//    @Page(Viewer = "/cn/msg/index.jsp")
    public void index() {
        try {
            log.info("url:" + request.getRequestURL() + ",ip:" + ip() + "打开公告/新闻页面");
            int pageSize = intParam("pageSize");
            int pageIndex = intParam("page");
            int noticeType = intParam("noticeType");
            if (pageSize == 0) {
                pageSize = 10;
            }
            NewsDao nd = new NewsDao();
            Query<News> q = nd.getQuery(News.class)
                    .retrievedFields(true, "_id", "title", "pubTime", "content", "isTop", "type")
                    .filter("type", 1)
                    .filter("language", lan)
                    .filter("pubTime <", new Date());
            if (noticeType > 0) {
                q.filter("noticeType", noticeType);
            }
            long count = nd.count(q);
            if (count > 0) {
                q.order("-isTop,-topTime,-pubTime");
                List<News> li = nd.findPage(q, pageIndex, pageSize);
                dealPubTimeShow(li);
                setAttr("li", li);
                setPaging((int) count, pageIndex, pageSize);
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    //获取新闻列表
//    @Page(Viewer = "/cn/msg/newslist.jsp")
    public void newslist() {
        try {
            log.info("url:" + request.getRequestURL() + ",ip:" + ip() + "打开新闻页面");

            int pageSize = intParam("pageSize");
            int pageIndex = intParam("page");
            if (pageSize == 0) {
                pageSize = 10;
            }
            NewsDao nd = new NewsDao();
            Query<News> q = nd.getQuery(News.class)
                    .retrievedFields(true, "_id", "photo", "title", "digest", "pubTime", "source", "sourceLink", "isTop", "type")
                    .filter("language", lan)
                    .filter("type", 2)
                    .filter("pubTime <", new Date());
            long count = nd.count(q);
            if (count > 0) {
                q.order("-isTop,-topTime,-pubTime");
                List<News> li = nd.findPage(q, pageIndex, pageSize);
                dealPubTimeShow(li);
                setAttr("li", li);
                setPaging((int) count, pageIndex, pageSize);
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }

    }

    //获取APP下载地址
//    @Page(Viewer = "/cn/app_download.jsp")
    public void appdownload() {
        //2017.08.15 前端设置APP下载地址
    }

    //获取新闻或公告列表（json格式）
    @Page(Viewer = JSON)
    public void newsOrAnnList() {
        try {
            log.info("url:" + request.getRequestURL() + ",ip:" + ip() + "打开公告/新闻页面");
            int pageSize = 0;
            int pageIndex = 0;
            int type = 1;
            int noticeType = 1;
            String title = "";
            try {
                /*Start by guankaili 20181120 新闻每页显示15条 */
//                pageSize = intParam("pageSize");
                pageSize = 15;
                /*end*/
                pageIndex = intParam("pageIndex");
                type = intParam("type"); //type:1,公告；2：新闻
                noticeType = intParam("noticeType");
                title = param("title");

            } catch (Exception e) {
                log.info("Ip:" + ip() + "参数传递错误，恶意刷接口");
            }
            Query<News> q = null;
            NewsDao nd = new NewsDao();
            Date time = new Date();
            if (type == 1) {    //公告
                q = nd.getQuery(News.class).
                        retrievedFields(true, "_id", "title", "pubTime", "content", "isTop", "type", "noticeType", "digest")
                        .filter("language", lan)
                        .filter("type", type)
                        .filter("pubTime <", time);
                if (noticeType > 0) {
                    q.filter("noticeType", noticeType);
                }
                if (StringUtils.isNotBlank(title)) {
                    Pattern pattern = Pattern.compile("^.*" + title + ".*$", Pattern.CASE_INSENSITIVE);
                    q.filter("title", pattern);
                }
            } else if (type == 2) {
                //新闻
                q = nd.getQuery(News.class)
                        .retrievedFields(true, "_id", "photo", "title", "digest", "pubTime", "source", "sourceLink", "isTop", "type")
                        .filter("language", lan)
                        .filter("type", type)
                        .filter("pubTime <", time);

            } else {
                json("failure-parameter error!", false, "", true);
                return;
            }
            long count = nd.count(q);
            Map<String, Object> returnMap = new HashMap<>();
            if (count > 0) {
                /*Start by guankaili 20181122 排序逻辑 */
//                q.order("-isTop,-topTime,-pubTime");
                q.order("-isTop,-pubTime");
                /*end*/
                List<News> list = nd.findPage(q, pageIndex, pageSize);
                dealPubTimeShow(list);
                returnMap.put("total", count);
                returnMap.put("count", list.size());
                returnMap.put("pageIndex", pageIndex);
                returnMap.put("pageSize", pageSize);
                returnMap.put("datalist", list);
            }
            json("success", true, com.alibaba.fastjson.JSON.toJSONString(returnMap), true);
        } catch (Exception ex) {
            log.error("内部异常", ex);
            json(L("获取失败！"), false, "", true);
        }
    }

    //首页=获取新闻或公告列表（json格式）
    @Page(Viewer = JSON)
    public void newsListHome() {
        try {
            log.info("url:" + request.getRequestURL() + ",ip:" + ip() + "打开首页新闻页面");
            int pageSize = 15;
            int pageIndex = 1;
            Query<News> q = null;
            NewsDao nd = new NewsDao();
            Date time = new Date();
            q = nd.getQuery(News.class)
                    .retrievedFields(true, "_id", "photo", "title", "digest", "pubTime", "source", "sourceLink", "isTop", "type")
                    .filter("language", lan)
                    .filter("type", 2)
                    .filter("pubTime <", time);
            long count = 30;
            Map<String, Object> returnMap = new HashMap<>();
            if (count > 0) {
                q.order("-isTop,-pubTime");
                String def = null;
                List<News> list = nd.findPage(q, pageIndex, pageSize);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                for (News news : list) {

                    long lt = new Long(String.valueOf(news.getPubTime().getTime()));
                    Date date = new Date(lt);
                    String dates = simpleDateFormat.format(date);
                    if (news.isTop()) {
                        news.setTopInfo(L("[置顶]"));
                        continue;
                    }
                    news.setTopInfo("");
                    if (dates.equals(def)) {
                        news.setPubTime(null);
                    } else {
                        def = dates;
                    }
                }
                returnMap.put("total", count);
                returnMap.put("count", list.size());
                returnMap.put("pageIndex", pageIndex);
                returnMap.put("pageSize", pageSize);
                returnMap.put("datalist", list);
            }
            json("success", true, com.alibaba.fastjson.JSON.toJSONString(returnMap), true);
        } catch (Exception ex) {
            log.error("内部异常", ex);
            json(L("获取失败！"), false, "", true);
        }
    }

    //获取新闻详情(json格式)
    @Page(Viewer = JSON)
    public void newsdetails() {
        try {
            int type = 1;
            type = intParam("type"); //type:1,公告；2：新闻
            String id = param("id");
            Map<String, Object> returnMap = new HashMap<>();
            if (StringUtils.isEmpty(id)) {
                returnMap.put("newsdetial", new News());
                json("success", true, com.alibaba.fastjson.JSON.toJSONString(returnMap), true);
                return;
            }

            NewsDao nd = new NewsDao();
            News news = nd.getById(id);

            if(null != news){
                if (StringUtils.isNotEmpty(news.getBaseId()) && StringUtils.isNotEmpty(lan) && !news.getLanguage().equalsIgnoreCase(lan)) {
                    // 语言不同，需要切换语言
                    News curLanNews = nd.findOne(nd.getQuery().filter("baseId", news.getBaseId()).filter("language", lan));
                    if (curLanNews != null) {
                        news = curLanNews;
                    }
                }
                dealPubTimeShow(news);

            }
            /* start by kinghao 20181120 添加新闻推荐 */
            returnMap.put("newsdetial", news);

            com.google.code.morphia.query.Query<News> q = null;
            if (type == 2) {
                //推荐新闻
                q = nd.getQuery(News.class)
                        .retrievedFields(true, "_id", "photo", "title", "digest", "pubTime", "source", "sourceLink", "isTop", "type", "recommend")
                        .filter("language", lan)
                        .filter("type", 2)
//                        .filter("recommend", true)
                        .filter("_id !=",id);

                long count = nd.count(q);
                if (count > 0) {
                    q.order("-isTop,-topTime,-pubTime");
                    List<News> list = nd.findPage(q, 1, 999);
                    list = this.newsRecommend(list);
                    dealPubTimeShow(list);
                    returnMap.put("datalist", list);
                }
            }
            json("success", true, com.alibaba.fastjson.JSON.toJSONString(returnMap), true);
            /*end*/


        } catch (Exception ex) {
            log.error("内部异常", ex);
            json(L("获取失败！"), false, "", true);
        }
    }

    //处理发布时间
    private void dealPubTimeShow(List<News> list) {
        for (News news : list) {
            if (news.isTop()) {
                news.setTopInfo(L("[置顶]"));
            }
            dealPubTimeShow(news);
        }
    }

    private void dealPubTimeShow(News news) {
        String pubTimeStr = null;
        Timestamp pubTime = news.getPubTime();
        long timeDiff = System.currentTimeMillis() - pubTime.getTime();
        //与当前时间之差
        if (timeDiff > 24 * 60 * 60 * 1000 || timeDiff < 0) {
            String text = "MM-dd-yyyy HH:mm";
            if (lan.equals("cn") || lan.equals("hk") || lan.equals("tw")) {
                text = "yyyy-MM-dd HH:mm";
            }
            SimpleDateFormat sdf = new SimpleDateFormat(text);

            pubTimeStr = sdf.format(pubTime);
        } else {
            pubTimeStr = millisConvertTime(timeDiff);
        }
        news.setPubTimeStr(pubTimeStr);
        news.setPubTimePage(getTime(pubTime));
    }

    /**
     * 将传入时间与当前时间进行对比，是否今天昨天
     *
     * @param date
     * @return
     */
    private String getTime(Date date) {
        //今天
        String todySDF = "HH时mm分";
        //昨天
        String yesterDaySDF = "昨天";
        //昨天之前
        String otherSDF = "M月d日";
        SimpleDateFormat sfd = null;
        String time = "";
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Date now = new Date();
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(now);
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        if (dateCalendar.after(targetCalendar)) {
            sfd = new SimpleDateFormat(todySDF);
            time = sfd.format(date);
            return time;
        } else {
            targetCalendar.add(Calendar.DATE, -1);
            if (dateCalendar.after(targetCalendar)) {
                sfd = new SimpleDateFormat(yesterDaySDF);
                time = sfd.format(date);
                return time;
            }
        }
        sfd = new SimpleDateFormat(otherSDF);
        time = sfd.format(date);
        return time;
    }

    //毫秒数转成时间
    private String millisConvertTime(long time) {
        String hourDes = L("小时");
        String minDes = L("分钟");
        long hours = (time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (time % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (time % (1000 * 60 * 60 * 60)) / (1000);
        if (hours == 0 && minutes == 0) {
            return L("现在");
        } else {
            return (hours == 0 ? "" : (hours + " " + hourDes)) + " " +
                    (minutes == 0 ? "" : (minutes + " " + minDes)) + " " +
                    L("前");
        }
    }

    //Close By suxinjie 一期屏蔽该功能
//    @Page(Viewer = "/cn/msg/details.jsp")
    public void details() {
        try {
            String id = param(0);
            if (StringUtils.isEmpty(id)) {
                id = param("id");
            }
            if (id.length() > 0) {
                News s = nd.getById(id, "_id", "photo", "title", "content", "pubTime", "source", "sourceLink", "type");
                setAttr("n", s);
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    /**
     * 返回的最近一周的公告数据
     */
    //Close By suxinjie 一期屏蔽该功能
    @Page(Viewer = JSON, Cache = 60)
    public void lastnote() {

        try {
            String id = param("id");
            News prev = nd.get(id);

            Timestamp time = TimeUtil.getAfterDayTime(new Date(), -7);
            if (prev != null) {
                time = prev.getPubTime().compareTo(time) < 0 ? time : prev.getPubTime();
            }

            News last = nd.findOne(nd.getQuery().filter("pubTime <", time).filter("type", NewsType.notice.getKey()).order("pubTime"));
            String json = "";
            if (last != null) {
                JSONArray array = new JSONArray();
                array.add(last.getId());
                array.add(last.getTitle());
                array.add(last.getDigest());
                json = "{\"last\" : " + array.toString() + "}";
            } else {
                json = "{\"last\" : []}";
            }
            Response.append(jsonp(json));
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    /**
     * 返回的最近一周的公告数据
     * Close By suxinjie 一期屏蔽该功能
     */
    @Page(Viewer = JSON)
    public void lastid() {
        try {
            NewsDao nd = new NewsDao();
            News last = nd.findOne(nd.getQuery().filter("type", NewsType.notice.getKey()).order("- pubTime"));
            String json = "{\"last\" : [" + last.getId() + "]}";
            Response.append(jsonp(json));
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    //Close By suxinjie 一期屏蔽该功能
    @Page(Viewer = JSON)
    public void lastNews() {
        int pageNo = intParam("pageNo");
        int pageSize = intParam("pageSize");
        String type = param(0);
        pageNo = pageNo == 0 ? 1 : pageNo;
        pageSize = pageSize == 0 ? 4 : pageSize;


        try {
            NewsDao nd = new NewsDao();
            Query<News> q = nd.getQuery(News.class);
            if (type.length() > 0) {
                NewsType nt = NewsType.getByValue(type);
                q.filter("type", nt.getKey());
            }
            q.order("-pubTime");
            List<News> lasts = nd.findPage(q, pageNo, pageSize);
            StringBuilder json = new StringBuilder();//JSONArray.fromObject(lasts).toString();
            json.append("[");
            int i = 0;
            for (News n : lasts) {
                if (i > 0) {
                    json.append(",");
                }
                json.append("{");
                json.append("\"id\" : \"" + n.getId() + "\"");
                json.append(",\"title\" : \"" + n.getTitle() + "\"");
                json.append(",\"date\" : \"" + sdf.format(n.getPubTime()) + "\"");
                json.append(",\"photo\" : \"" + n.getPhoto() + "\"");
                json.append(",\"id\" : \"" + n.getId() + "\"");
                json.append(",\"type\" : \"" + n.getType() + "\"");
                json.append(",\"typeName\" : \"" + n.getNt().getValue() + "\"");
                json.append("}");
                i++;
            }
            json.append("]");
            json("success", true, json.toString(), true);
        } catch (Exception ex) {
            log.error("内部异常", ex);
            json(L("获取失败！"), false, "", true);
        }
    }

    /**
     * 查询新闻明细
     * <p>
     * Close By suxinjie 一期屏蔽该功能
     */
    @Page(Viewer = JSON)
    public void detail() {
        String id = param("id");
        try {
            NewsDao nd = new NewsDao();
            News n = nd.get(id);
            StringBuilder json = new StringBuilder();//JSONArray.fromObject(lasts).toString();
            if (n != null) {
                json.append("{");
                json.append("\"id\" : \"" + n.getId() + "\"");
                json.append(",\"type\" : \"" + n.getType() + "\"");
                json.append(",\"title\" : \"" + n.getTitle() + "\"");
                json.append(",\"date\" : \"" + sdf.format(n.getPubTime()) + "\"");
                json.append(",\"content\" : \"" + n.getContent().replace("\"", "\\\"") + "\"");
                json.append(",\"photo\" : \"" + n.getPhoto() + "\"");
                json.append(",\"href\" : \"/msg/detail-" + n.getId() + "-" + n.getNt().name() + "\"");
                json.append("}");
            }
            json("success", true, json.toString(), true);
        } catch (Exception ex) {
            log.error("内部异常", ex);
            json(L("获取失败！"), false, "", true);
        }
    }

    /**
     * 读取公告
     */
    @Page(Viewer = JSON)
    public void readNotice() {
        int maxNoticeId = intParam("maxNoticeId");//获取公告ID
        if (maxNoticeId == 0) {
            NewsDao nd = new NewsDao();
            Query<News> query = nd.getQuery(News.class);
            Date time = new Date();

            query.filter("type", 1).filter("pubTime <", time);
            if (StringUtils.isNotBlank(lan)) {
                query.filter("language", lan);
            }
            query.order("-pubTime").limit(1);
            News notice = nd.findOne(query);
            if (notice != null) {
                maxNoticeId = Integer.valueOf(notice.getId());
            }
        }

        Map<String, String> noticeIdMap = new HashMap<>();
        if (maxNoticeId > 0) {
            News news = null;
            // TODO 解决弹出公告BUG修复时放开注释
//            News news = nd.getById(String.valueOf(maxNoticeId));

            if (news != null && StringUtils.isNotEmpty(news.getBaseId())) {
                // 存在基础ID 查询公告其他语言版本
                QueryResults<News> results = nd.find(nd.getQuery(News.class).filter("baseId", news.getBaseId()));
                for (News item : results) {
                    noticeIdMap.put(item.getLanguage(), item.getId());
                }
            } else {
                noticeIdMap.put(lan, String.valueOf(maxNoticeId));
            }
        }

        for (Map.Entry<String, String> entity : noticeIdMap.entrySet()) {
            String lantmp = entity.getKey().equalsIgnoreCase("cn") ? "" : entity.getKey().toLowerCase();
            String key = MEM_NOTICE_KEY_PREFIX + lantmp + userIdStr();
            String cacheNoticeId = Cache.Get(key);
            if (userId() > 0 &&
                    (!StringUtil.exist(cacheNoticeId) || Integer.valueOf(cacheNoticeId).compareTo(Integer.valueOf(entity.getValue())) < 0)) {
                Cache.Set(key, entity.getValue());
            }
        }

        json("", true, "", false);
    }

    /**
     * 返回未读公告列表
     */
    @Page(Viewer = JSON)
    public void getUserUnReadNotice() {
        NewsDao nd = new NewsDao();
        Query<News> query = nd.getQuery(News.class);
        Date time = new Date();

        query.filter("type", 1).filter("pubTime <", time);
        if (StringUtils.isNotBlank(lan)) {
            query.filter("language", lan);
        }
        query.order("-pubTime").limit(1);

        News unreadNews = query.get();
        String lantmp = lan.equalsIgnoreCase("cn") ? "" : lan.toLowerCase();
        String cacheNoticeId = Cache.Get(MEM_NOTICE_KEY_PREFIX + lantmp + userIdStr());

        List<Map<String, Object>> list = new ArrayList<>();

        if (StringUtils.isNotEmpty(cacheNoticeId) && unreadNews.getId().equals(cacheNoticeId)) {
            // 已读
            json("", true, com.alibaba.fastjson.JSON.toJSONString(list), false);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("id", unreadNews.getId());
        map.put("title", unreadNews.getTitle());
        map.put("content", unreadNews.getContent());
        map.put("publishTime", unreadNews.getPubTime() == null ? "" : unreadNews.getPubTime().getTime());
        list.add(map);

        json("", true, com.alibaba.fastjson.JSON.toJSONString(list), false);
    }

//  统计当前项目的所有请求地址
//	@Page(Viewer = JSON)
//	public void test() {
//		Map<String, ViewCode> content = ViewCodeContainer.content;
//		String basePath = "com.world.controller";
//
//		List<String> result = new ArrayList<>();
//
//		for (Map.Entry<String, ViewCode> entry : content.entrySet()) {
//			String classPath = entry.getKey();
//			if (basePath.equals(classPath)) {
//				log.info("/");
//				result.add("/");
//			}
//
//			String url = classPath.replace('.', '/');
//
//			result.add(url.substring(basePath.length()));
//
//		}
//
//		Collections.sort(result);
//
//		json("" , true , com.alibaba.fastjson.JSON.toJSONString(result), false);
//	}

    public List<News> newsRecommend(List<News> list) {
        List<News> listResult = new ArrayList<>();
        final int num = list.size();
        int Random[] = null;
        int loopInt = 4;
        if (list.size() < loopInt) {
            loopInt = list.size();
            Random = new int[loopInt];
        }else{
            Random = new int[loopInt];
        }
        for (int i = 0; i < loopInt; i++) {
            // int ran=-1;
            boolean isContinue = true;
            while (isContinue) {
                int ran = (int) (num * Math.random());
                for (int j = 0; j < i; j++) {
                    if (Random[j] == ran) {
                        ran = -1;
                        break;
                    }
                }
                if (ran != -1) {
                    Random[i] = ran;
                    break;
                }
            }
        }
        for (int ranInt = 0; ranInt < Random.length; ranInt++) {
            listResult.add(list.get(Random[ranInt]));
        }
        list.clear();
        list = listResult;
        return list;

    }


}