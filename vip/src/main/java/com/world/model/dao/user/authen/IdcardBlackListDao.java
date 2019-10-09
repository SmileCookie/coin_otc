package com.world.model.dao.user.authen;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.user.authen.IdcardBlackList;
import com.world.util.date.TimeUtil;
import com.world.web.action.BaseAction;

public class IdcardBlackListDao extends MongoDao<IdcardBlackList, String>{

	private static final long serialVersionUID = 1L;


	public long search4Back(String cardNo, int pageSize, int pageNo, BaseAction action){
		Query<IdcardBlackList> q = this.getQuery();
		if(StringUtils.isNotBlank(cardNo)){
			q.filter("cardNo", cardNo);
		}
		long total = this.count(q);
		if (total > 0) {
			List<IdcardBlackList> dataList = this.findPage(q, pageNo, pageSize);
			action.setAttr("dataList", dataList);
			action.setAttr("itemCount", total);
		}
		action.setAttr("page", pageNo);

		return total;
	}

	public void save(String cardNo, String remark){
		IdcardBlackList idcardBlackList = new IdcardBlackList(getDs(), cardNo, remark, TimeUtil.getNow());
		this.save(idcardBlackList);
	}

	public void delete(String id){
		this.delById(id);
	}


	public boolean isBlackList(String cardNo){
		Query<IdcardBlackList> query = getQuery();
		query.filter("cardNo", cardNo);
		return this.count(query) > 0;
	}
}
