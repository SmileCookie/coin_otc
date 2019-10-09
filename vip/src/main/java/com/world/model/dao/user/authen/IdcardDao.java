package com.world.model.dao.user.authen;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.constant.Const;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.authen.AuthUtil;
import com.world.model.entity.user.authen.Idcard;

public class IdcardDao extends MongoDao<Idcard, String>{

	private static final long serialVersionUID = 1L;

	public void add(Idcard aus){
		Idcard oaus = super.findOne(super.getQuery().filter("_id =", aus.getId()));
		if(oaus == null){
			super.save(aus);
		}
	}
	
	public UpdateResults<Idcard> update(Idcard aus){
		Datastore ds = getDatastore();
		Query<Idcard> q = ds.find(Idcard.class, "_id =", aus.getId());  
		UpdateOperations<Idcard> ops = ds.createUpdateOperations(Idcard.class);
		ops.set("name", aus.getName());
		ops.set("cardno", aus.getCardno());
		ops.set("address", aus.getAddress());
		ops.set("sex", aus.getSex());
		ops.set("birthday", aus.getBirthday());
		ops.set("photo", aus.getPhoto());
		UpdateResults<Idcard> ur = update(q, ops);
		return ur;
	}
	
	public boolean validIdcard(String name, String cardno) {
		if(!Const.IS_VALIDATE_DALU_IDCARD){
			return true;
		}

		boolean pass = false;
		if (null == name || "".equals(name) || null == cardno || "".equals(cardno)) {
			return pass;
		}
		try {
			Idcard idcard = findOne(getQuery().filter("name =", name).filter("cardno =", cardno));
			if (null != idcard) {
				pass = true;
			} else {
				idcard = AuthUtil.getIdcard(name, cardno);
				if (null != idcard) {
					save(idcard);
					pass = true;
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return pass;
	}
	
	public Idcard getIdcardPhoto(String name, String cardno) {
		Idcard idcard = null;
		if (null == name || "".equals(name) || null == cardno || "".equals(cardno)) {
			return idcard;
		}
		try {
			idcard = findOne(getQuery().filter("name =", name).filter("cardno =", cardno));
			if (null == idcard) {
				Idcard idcard1 = AuthUtil.getIdcardPhoto(name, cardno);
				if (null != idcard1) {
					save(idcard1);
					idcard = idcard1;
				}
			} else if (null == idcard.getPhoto() || "".equals(idcard.getPhoto())) {
				Idcard idcard1 = AuthUtil.getIdcardPhoto(name, cardno);
				if(idcard1 == null ){
					return null;
				}
				idcard1.setMyId(idcard.getId());
				update(idcard1);
				idcard = idcard1;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return idcard;
	}
}
