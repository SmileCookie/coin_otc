package com.world.model.dao.level;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.admin.logs.DailyRecord;
import com.world.model.entity.level.IntegralRule;
import com.world.model.entity.level.UserVipLevel;

@SuppressWarnings("serial")
public class IntegralRuleDao extends MongoDao<IntegralRule, String>{
	Logger logger = Logger.getLogger(IntegralRuleDao.class);
	

	
	/**
	 * 根据id查询一条用户积分规则配置
	 * @param id 标识号
	 * @return 查询的唯一一条结果
	 */
	public IntegralRule getById(String id){
		Query<IntegralRule> q = getQuery(IntegralRule.class).filter("_id =", id); 
		return super.findOne(q); 
	}
	/*start by xwz 20170626*/
	/**
	 * 根据seqNo查询一条用户积分规则配置
	 * @param seqNo 标识号
	 * @return 查询的唯一一条结果
	 */
	public IntegralRule getBySeqNo(int seqNo){
		Query<IntegralRule> q = getQuery(IntegralRule.class).filter("seqNo =", seqNo);
		return super.findOne(q);
	}
	/*end*/
	
	/**
	 * 添加一条用户积分规则配置
	 */
	public String addIntegralRule(IntegralRule integralRule){
		String nId=super.save(integralRule).getId().toString();
		return nId;
	}
	
	/**
	 * 更新用户积分规则配置
	 * @param IntegralRule
	 * @return
	 */
	public boolean updateIntegralRule(IntegralRule integralRule){
		Query<IntegralRule> q = getQuery(IntegralRule.class).filter("_id =", integralRule.getId()); 
		UpdateOperations<IntegralRule> ops =super.createUpdateOperations();
		ops.set("type", integralRule.getType());
		ops.set("rule", integralRule.getRule());
		ops.set("memo", integralRule.getMemo());
		/*start by xwz 20170625*/
		ops.set("typeCode", integralRule.getTypeCode());
		ops.set("score", integralRule.getScore());
		ops.set("integType", integralRule.getIntegType());
		ops.set("period", integralRule.getPeriod());
		/*end*/
		UpdateResults<IntegralRule> ur =super.update(q, ops);
		
		return !ur.getHadError();
	}
	
	/**
	 * 查询用户积分积分规则列表
	 */
	public List<IntegralRule> search(Query<IntegralRule> q,int pageIndex,int pageSize){
		q.offset((pageIndex-1)*pageSize).limit(pageSize);
		return super.find(q).asList(); 
	}
	
	
	public List<IntegralRule> getList(){
		Query<IntegralRule> q = getQuery(IntegralRule.class);
		q.order("seqNo");
		List<IntegralRule> dataList = super.find(q).asList();
		return dataList;
		
	}

	/*start by xwz*/
	public Map<String, String> getRuleMap(){
		Map<String, String> map = new HashMap<>();
		List<IntegralRule> list =  getList();
		for(IntegralRule rule : list) {
			map.put(rule.getSeqNo() + "", rule.getType());
		}
		return map;
	}
	/*end*/
}
