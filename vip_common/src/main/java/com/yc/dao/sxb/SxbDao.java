package com.yc.dao.sxb;

import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.yc.entity.sxb.Sxb;
import com.yc.mongo.MorphiaMongo;

/**
 * 资产凭证DAO
 * @author guosj
 */
public class SxbDao extends BasicDAO<Sxb, String> {

	private final static Logger log = Logger.getLogger(SxbDao.class.getName());

	public SxbDao(MorphiaMongo mm) {
		super(mm.getMongo(), mm.getMorphia(), mm.getDbName());
	}
	
	public Sxb getSxb(String sxbId, String userId){
		Sxb sxb = null;
		try {
			Datastore ds = this.getDatastore();
			Query<Sxb> q = ds.createQuery(Sxb.class)
					.filter("userid =", userId)
					.filter("_id", new ObjectId(sxbId))
					.order("createTime").limit(1);
			List<Sxb> list = this.find(q).asList();
			if(list.size() > 0) sxb = list.get(0);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return sxb;
	}
	
	public Sxb getLastSxbByUserId(String userId){
		Sxb sxb = null;
		try {
			Datastore ds = this.getDatastore();
			Query<Sxb> q = ds.createQuery(Sxb.class)
					.filter("userid =", userId)
					.order("-createTime").limit(1);
			List<Sxb> list = this.find(q).asList();
			if(list.size() > 0) sxb = list.get(0);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return sxb;
	}
}
