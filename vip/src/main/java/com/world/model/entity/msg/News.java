package com.world.model.entity.msg;

import java.sql.Timestamp;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.file.PathUtil;
import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.model.entity.EnumUtils;
import com.world.util.string.TitleBean;
import org.apache.log4j.Logger;

@Entity(noClassnameStored = true , value = "news")
public class News  extends StrBaseLongIdEntity{

	private static final long serialVersionUID = -2386115543279873856L;

	private static final Logger log = Logger.getLogger(News.class);

	public News() {
		super();
	}
	
	public News(Datastore ds) {
		super(ds);
	}
	
	public News(Datastore ds, String title, String digest, String content, String photo, int type, String adminName, String topInfo,Timestamp pubTime,boolean isTop,boolean recommend, String keyword, String srcPublisher, String source, String sourceLink, String language,int noticeType) {
		this(ds);
		this.title = title;
		this.digest = digest;
		this.content = content;
		this.photo = photo;
		this.type = type;
		this.adminName = adminName;
		this.topInfo=topInfo;
		this.pubTime = pubTime;
		this.isTop = isTop;
		this.recommend = recommend;
		this.keyword = keyword;
		this.srcPublisher = srcPublisher;
		this.source = source;
		this.sourceLink = sourceLink;
		this.language = language;
		this.noticeType = noticeType;

	}

	private String title;//标题
	private String digest;//标注
	private String content;//主体
	private String photo;//照片
	private int type;//类别
	private int noticeType;
	private String adminName;//发布者
	private String topInfo ;//发布者
	private Timestamp pubTime;//发布时间
	private boolean isTop;//是否置顶
	private boolean recommend;//是否推荐 //create by kinghao 20181120
	private Timestamp topTime;//置顶时间
	private Timestamp createTime;//真正的发布时间
	private String keyword;		//关键自
	private TitleBean tb;
	private String srcPublisher;//真实发布者
	private String source;		//来源
	private String sourceLink;	//来源链接
	private String pubTimeStr;//创建时间显示
	private String pubTimePage;//创建时间首页显示
	private String language;//语言
    private String baseId;
    private Boolean base;
    private String remark;
    private String hasLan;


	public int getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(int noticeType) {
		this.noticeType = noticeType;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSourceLink() {
		return sourceLink;
	}

	public void setSourceLink(String sourceLink) {
		this.sourceLink = sourceLink;
	}

	public void setPubTimeStr(String pubTimeStr){
		this.pubTimeStr = pubTimeStr;
	}

	public String getPubTimeStr(){
		return pubTimeStr;
	}

	public NewsType getNt(){
		return (NewsType)EnumUtils.getEnumByKey(type, NewsType.class);
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
		log.info(createTime);
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public String getPhoto(){
		return photo;
	}
	public String getPhotoStr() {
		if(photo == null || photo.indexOf("/") >= 0){
			return null;
		}
		return PathUtil.getListPic(photo, "88x88");
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public TitleBean getTb() {
		return tb;
	}

	public void setTb(TitleBean tb) {
		this.tb = tb;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public Timestamp getPubTime() {
		return pubTime;
	}

	public void setPubTime(Timestamp pubTime) {
		this.pubTime = pubTime;
	}

	public Timestamp getTopTime() {
		return topTime;
	}

	public void setTopTime(Timestamp topTime) {
		this.topTime = topTime;
	}

	public String getSrcPublisher() {
		return srcPublisher;
	}

	public void setSrcPublisher(String srcPublisher) {
		this.srcPublisher = srcPublisher;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

	public String getPubTimePage() {
		return pubTimePage;
	}

	public void setPubTimePage(String pubTimePage) {
		this.pubTimePage = pubTimePage;
	}

	public boolean isRecommend() {
		return recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	public String getTopInfo() {
		return topInfo;
	}

    public String getBaseId() {
        return baseId;
    }

    public void setBaseId(String baseId) {
        this.baseId = baseId;
    }

    public Boolean getBase() {
        return base;
    }

    public void setBase(Boolean base) {
        this.base = base;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHasLan() {
        return hasLan;
    }

    public void setHasLan(String hasLan) {
        this.hasLan = hasLan;
    }

    public void setTopInfo(String topInfo) {
		this.topInfo = topInfo;
	}

//	public String getLanguageStr(){
//		if("hk".equals(language)){
//			return "繁体中文";
//		}else if("en".equals(language)){
//			return "ENGLISH";
//		}else{	//cn或者其他
//			return "简体中文";
//		}
//	}

}
