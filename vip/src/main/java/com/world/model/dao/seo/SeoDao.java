package com.world.model.dao.seo;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.seo.Seo;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/11/282:18 PM
 */
public class SeoDao extends MongoDao<Seo, String> {
    Logger logger = Logger.getLogger(SeoDao.class);

    /**
     * 添加
     * @param seo
     */
    public void addSeo(Seo seo) {
        seo.setAddTime(now());
        super.save(seo);
    }

    /**
     * 获取集合
     * @return
     */
    public List<Seo> getSeoList(){
        Query<Seo> seoQuery = super.getQuery();
        List<Seo> list = seoQuery.asList();
        return list;
    }
}
