package com.world.controller.admin.msg;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.msg.NewsDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.msg.News;
import com.world.model.entity.msg.NewsType;
import com.world.model.entity.msg.NoticeType;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.List;

@FunctionAction(jspPath = "/admins/msg/", des = "新闻编辑")
public class Index extends AdminAction {

    private static final long serialVersionUID = 1L;

    NewsDao nd = new NewsDao();
    static Logger logger = Logger.getLogger(Index.class.getName());

    @Page(Viewer = DEFAULT_INDEX, Cache = 0)
    public void index() {
        try {
            String tab = param("tab");
            int pageSize = intParam("pageSize");
            int pageIndex = 1;

            String title = param("title");        //标题
            String content = param("content");    //内容
            int noticeType = intParam("noticeType");
            if (tab.length() == 0)
                tab = "notice";

            Query<News> q = nd.getQuery(News.class);
            if (!tab.equals("all")) {
                NewsType nt = NewsType.getByValue(tab);
                q.filter("type", nt.getKey());
            }
            if (noticeType > 0) {
                q.filter("noticeType", noticeType);
            }
            if (StringUtils.isNotEmpty(title)) {    //标题
                q.field("title").contains(title);
            }

            if (StringUtils.isNotEmpty(content)) {    //内容
                q.field("content").contains(content);
            }

            q.order("-pubTime");

            long count = nd.count(q);
            if (count > 0) {
                List<News> dataList = nd.findPage(q, pageIndex, pageSize);
                setAttr("dataList", dataList);
            }
            setAttr("noticeType", EnumUtils.getAll(NoticeType.class));
            setAttr("nts", EnumUtils.getAll(NewsType.class));
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    // ajax的调用
    @Page(Viewer = DEFAULT_AJAX)
    public void ajax() {
        index();
    }

    @Page(Viewer = XML)
    public void doDel() {
        String id = param("id");
        if (id.length() > 0) {
            boolean res = true;
            if (res) {// .filter("faBuZhe", adminName)
                nd.deleteByQuery(nd.getQuery().filter("_id", id));
                Write("删除成功", true, "");
                return;
            }
        }
        Write("未知错误导致删除失败！", false, "");
    }

    @Page(Viewer = DEFAULT_AORU)
    public void aoru() {
        try {
            String id = param("id");
            if (id.length() > 0) {
                News s = nd.get(id);
                setAttr("n", s);
            }
            setAttr("nts", EnumUtils.getAll(NewsType.class));
            setAttr("noticeType", EnumUtils.getAll(NoticeType.class));
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = ".xml")
    public void doAoru() {
        try {
            String id = param("id");

            String title = param("title");                        // 标题
            String digest = request.getParameter("digest");        // 摘要
            String photo = request.getParameter("photo") == null ? "" : request.getParameter("photo");    //封面照片
            String content = request.getParameter("newContent").replaceAll("<br>", "").replaceAll("<p></p>", "");    //内容
            int type = intParam("type");                        //1:公告，2：新闻
            Boolean isTop = booleanParam("isTop");                //是否置顶
            Boolean recommend = booleanParam("recommend");                //是否推荐
            String keyword = param("keyword");                    //关键字
            Timestamp pubTime = dateParam("pubTime");            //发布时间
            String srcPublisher = adminName();            //发布人
            String source = param("source");                    //新闻来源
            String sourceLink = param("sourceLink");            //新闻链接
            String language = param("language");//语言
            NewsDao nd = new NewsDao();
            Query<News> q = nd.getQuery(News.class)
                    .filter("type", 1)
                    .filter("pubTime =", pubTime);

            long count = nd.count(q);
            int noticeType = intParam("noticeType");
            int res = 0;
            Datastore ds = nd.getDatastore();
            if (id.length() > 0) {
                if (count > 1) {
                    WriteRight("操作失败，已存在该发布时间公告，请更改发布时间。");
                    return;
                }
                Query<News> query = ds.find(News.class, "_id", id);

                UpdateOperations<News> operate = ds.createUpdateOperations(News.class).set("title", title)// 标题
                        .set("digest", digest)
                        .set("photo", photo)
                        .set("type", type)
                        .set("keyword", keyword)
                        .set("isTop", isTop)
                        .set("recommend", recommend)
                        .set("content", content)
                        .set("pubTime", pubTime)
                        .set("source", source)
                        .set("sourceLink", sourceLink)
                        .set("noticeType", noticeType)
                        .set("language", language);
                if (isTop) {
                    operate.set("topTime", now());//置顶时间
                }
                UpdateResults<News> ur = ds.update(query, operate);
                if (!ur.getHadError()) {
                    res = 2;
                }
            } else {
                if (count > 0) {
                    WriteError("操作失败，已存在该发布时间公告，请更改发布时间。");
                    return;
                }
                News e = new News(ds, title, digest, content, photo, type, "","", pubTime, isTop, recommend, keyword, srcPublisher, source, sourceLink, language, noticeType);
                e.setTop(isTop);
                e.setRecommend(recommend);
                e.setCreateTime(now());
                // 置顶时间
                if (isTop){
                    e.setTopTime(now());
                }
                else{
                    e.setTopTime(new Timestamp(0));

                }

                if (nd.save(e) != null) {
                    res = 2;
                }
            }
            if (res > 0) {
                WriteRight("操作成功");
                return;
            }
        } catch (Exception ex) {
            log.error("内部异常", ex);
        }
    }

    @Page(Viewer = ".xml")
    public void doTop() {
        try {
            String id = param("id");

            if (id.length() > 0) {
                News n = nd.get(id);
                if (n == null) {
                    Write("没有要操作的项,", false, "请正确操作");
                    return;
                }

                Query<News> q = nd.getQuery().filter("_id =", id);
                UpdateOperations<News> operate = nd.getDatastore().createUpdateOperations(News.class);
                operate.set("isTop", true);
                operate.set("topTime", now());

                UpdateResults<News> ur = nd.update(q, operate);
                if (!ur.getHadError()) {

                    Write("操作成功,", true, "操作成功");
                } else {
                    Write("操作失败", false, "操作失败");
                }
            } else {
                Write("没有要操作的项。", false, "请正确操作");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }

    @Page(Viewer = ".xml")
    public void cancelTop() {
        try {
            String id = param("id");

            if (id.length() > 0) {
                News n = nd.get(id);

                if (n == null) {
                    Write("没有要操作的项,", false, "请正确操作");
                    return;
                }

                Query<News> q = nd.getQuery().filter("_id =", id);
                UpdateOperations<News> operate = nd.getDatastore().createUpdateOperations(News.class);
                operate.set("isTop", false);
                operate.set("topTime", new Timestamp(0));

                UpdateResults<News> ur = nd.update(q, operate);
                if (!ur.getHadError()) {

                    Write("操作成功,", true, "操作成功");
                } else {
                    Write("操作失败", false, "操作失败");
                }
            } else {
                Write("没有要操作的项。", false, "请正确操作");
            }
        } catch (Exception e) {
            log.error("内部异常", e);
        }
    }
}