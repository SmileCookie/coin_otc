package com.yc.biz.sxb;

import com.google.code.morphia.Key;
import com.yc.dao.sxb.SxbDao;
import com.yc.entity.sxb.Sxb;
import org.apache.log4j.Logger;

/****
 * sxb业务类
 * @author guosj
 */
public class SxbBiz {
	private final static Logger log = Logger.getLogger(SxbBiz.class.getName());

	public SxbBiz() {}
	public SxbBiz(SxbDao sxbDao) {
		this.sxbDao = sxbDao;
		
	}
	private SxbDao sxbDao;
	/****
	 * 插入一条资产凭证
	 * @param m
	 */
	public String addSxb(Sxb m){
		Key<Sxb> key = sxbDao.save(m);
		log.info(key.toString());
		return key.getId().toString();
	}
	
	public Sxb getSxb(String sxbId, String userId){
		if(sxbDao!=null){
			return sxbDao.getSxb(sxbId, userId);
		}
		return null;
	}
	public Sxb getLastSxbByUserId(String userId){
		if(sxbDao!=null){
			return sxbDao.getLastSxbByUserId(userId);
		}
		return null;
	}
}
