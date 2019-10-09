package com.world.model.entity.seo;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;

import java.sql.Timestamp;

/**
 * <p>@Description: </p>
 *
 * @author guankaili
 * @date 2018/11/282:09 PM
 */
@Entity(noClassnameStored = true , value = "seo")
public class Seo extends StrBaseLongIdEntity {
    private static final long serialVersionUID = -6839179453330559967L;
    public Seo() {
    }
    public Seo(Datastore ds) {
        super(ds);
    }
    /**
     * 内容
     */
    private String tith;
    /**
     * 标题
     */
    private String title;
    /**
     * url
     */
    private String url;
    /**
     * 添加时间
     */
    private Timestamp addTime;

    public String getTith() {
        return tith;
    }

    public void setTith(String tith) {
        this.tith = tith;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }
}
