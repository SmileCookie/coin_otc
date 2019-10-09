package com.world.model.entity.news;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.file.PathUtil;
import com.file.config.FileConfig;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.annotations.Entity;
import com.world.data.mongo.id.StrBaseLongIdEntity;
import com.world.util.string.TitleBean;

@Entity(noClassnameStored = true , value = "news")
public class news  extends StrBaseLongIdEntity{

	private static final long serialVersionUID = -2386115543279873856L;

	public news() {
		super();
	}
	
	public news(Datastore ds) {
		super(ds);
	}
	
	private String biaoTi;//标题
	private String biaoZhu;//标注
	private String zhuTi;//主体
	private String photo;//照片
	private String leibie;//类别
	private String faBuZhe;//发布者
	private String biaoQian;//标签
	private Timestamp publishTime;//发布时间
	private boolean isTop;//是否置顶
	private Timestamp topTime;//置顶时间
	private int ishot;//是否是热帖
	private int isJingHua;//是否是精华
	private String lb;//拼音
	private Timestamp createTime;//真正的发布时间
	private TitleBean tb;
	
	private String srcFaBuZhe;//真实发布者
	private int comments;//评论数
	
	public int getComments() {
		return comments;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}
	
	public String getShowTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		if(publishTime != null){
			return sdf.format(publishTime);
		}else{
			return "";
		}
		
	}
	
	public String getSrcFaBuZhe() {
		return srcFaBuZhe;
	}

	public void setSrcFaBuZhe(String srcFaBuZhe) {
		this.srcFaBuZhe = srcFaBuZhe;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getLb(){
		if(leibie==null) return "livenews";
		if (leibie.equals("时事"))
			lb = "livenews";
		else if (leibie.equals("行业"))
			lb = "industry";
		else if (leibie.equals("公告"))
			lb = "proclamation";
		return lb;
	}
	
	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public String getTime(){
		if(publishTime != null){
			Calendar cal = Calendar.getInstance();
			cal.setTime(publishTime);
			
			int month = cal.get(Calendar.MONTH);
			int day = cal.get(Calendar.DAY_OF_MONTH);
			
			return day + " " + LetterUtil.getMonthPre(month);
		}else{
			return "";
		}
		
	}
	
	public String getType(){
		if (leibie.equals("时事"))
			return "livenews";
		else if (leibie.equals("行业"))
			return "industry";
		else if (leibie.equals("公告"))
			return "proclamation";
		
		return null;
	}
	
	private String keyWord;
	private String keyWordHref;
	
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getBiaoTi() {
		return biaoTi;
	}
	public void setBiaoTi(String biaoTi) {
		this.biaoTi = biaoTi;
	}
	public String getBiaoZhu() {
		return biaoZhu;
	}
	public void setBiaoZhu(String biaoZhu) {
		this.biaoZhu = biaoZhu;
	}
	public String getZhuTi() {
		return zhuTi;
	}
	public void setZhuTi(String zhuTi) {
		this.zhuTi = zhuTi;
	}
	public String getPhoto(){
		return photo;
	}
	public String getPhotoStr() {
		if(photo == null || photo.indexOf("/") >= 0)
			return null;
		return PathUtil.getListPic(photo, "88x88");
	}
	
	public String getIndexPhotoStr() {
		if(photo == null || photo.indexOf("/") >= 0 || photo.length() < 3)
			return FileConfig.getValue("imgDomain1")+"/up/11/s/313A313A3131_190-60x60.jpg";
		return PathUtil.getListPic(photo, "60x60");
	}
	public String getPhoto(String size) {
		if(photo == null || photo.indexOf("/") >= 0)
			return null;
		return PathUtil.getListPic(photo, size);
	}
	public void setPhoto(String photo) {
		

		this.photo = photo;
	}
	public String getLeibie() {
		return leibie;
	}
	public void setLeibie(String leibie) {
		this.leibie = leibie;
	}
	public String getFaBuZhe() {
		return faBuZhe;
	}
	public void setFaBuZhe(String faBuZhe) {
		this.faBuZhe = faBuZhe;
	}
	public String getBiaoQian() {
		return biaoQian;
	}
	public void setBiaoQian(String biaoQian) {
		this.biaoQian = biaoQian;
	}
	public Timestamp getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Timestamp publishTime) {
		this.publishTime = publishTime;
	}
	
	public int getIshot() {
		return ishot;
	}
	public void setIshot(int ishot) {
		this.ishot = ishot;
	}
	public int getIsJingHua() {
		return isJingHua;
	}
	public void setIsJingHua(int isJingHua) {
		this.isJingHua = isJingHua;
	}
	public Timestamp getTopTime() {
		return topTime;
	}

	public void setTopTime(Timestamp topTime) {
		this.topTime = topTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public TitleBean getTb() {
		return tb;
	}

	public void setTb(TitleBean tb) {
		this.tb = tb;
	}

	public String getKeyWordHref() {
		return keyWordHref;
	}

	public void setKeyWordHref(String keyWordHref) {
		this.keyWordHref = keyWordHref;
	}
	
	
}
