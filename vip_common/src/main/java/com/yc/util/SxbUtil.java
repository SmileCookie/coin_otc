package com.yc.util;

import com.yc.biz.sxb.SxbBiz;
import com.yc.dao.sxb.SxbDao;
import com.yc.entity.sxb.Sxb;
import com.yc.mongo.MorphiaMongoUtil;
import org.apache.log4j.Logger;

public class SxbUtil {

	private final static Logger log = Logger.getLogger(SxbUtil.class.getName());
	
	public static String addSxb(Sxb m){
		try {
			SxbBiz sxbBiz=new SxbBiz(new SxbDao(MorphiaMongoUtil.getMorphiaMongo()));
			return sxbBiz.addSxb(m);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return "";
	}
	
	public static Sxb getSxb(String sxbId, String userId){
		try {
			SxbBiz sxbBiz=new SxbBiz(new SxbDao(MorphiaMongoUtil.getMorphiaMongo()));
			return sxbBiz.getSxb(sxbId, userId);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	
	public static Sxb getLastSxb(String userId){
		try {
			SxbBiz sxbBiz=new SxbBiz(new SxbDao(MorphiaMongoUtil.getMorphiaMongo()));
			return sxbBiz.getLastSxbByUserId(userId);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
}
