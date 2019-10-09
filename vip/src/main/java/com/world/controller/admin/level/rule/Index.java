package com.world.controller.admin.level.rule;

import com.google.code.morphia.query.Query;
import com.world.model.dao.level.IntegralRuleDao;
import com.world.model.entity.level.IntegType;
import com.world.model.entity.level.IntegralRule;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import me.chanjar.weixin.common.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

@FunctionAction(jspPath = "/admins/level/rule/", des = "积分规则")
public class Index extends AdminAction {

	IntegralRuleDao integralRuleDao = new IntegralRuleDao();
	
	
	@Page(Viewer = DEFAULT_INDEX, des="列表")
	public void index(){
		try {
			int pageSize = intParam("pageSize");
			String type = param("type");
			String rule = param("rule");
			int pageIndex = 1;
			Query<IntegralRule> q = integralRuleDao.getQuery(IntegralRule.class);
			if(StringUtils.isNotBlank(type)){//模糊查找
				Pattern pattern = Pattern.compile("^.*"  + type+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("type", pattern);
			}
			if(StringUtils.isNotBlank(rule)){
				Pattern pattern = Pattern.compile("^.*"  + rule+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("type", pattern);
			}
			q.order("seqNo");
			
			long count = integralRuleDao.count(q);
			if (count > 0) {
				List<IntegralRule> dataList = integralRuleDao.findPage(q, pageIndex, pageSize);
				setAttr("dataList", dataList);
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		
	}

	
	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	@Page(Viewer = XML,des="删除")
	public void doDel() {
		String id = param("id");
		if (id.length() > 0) {
			boolean res = true;
			if (res) {
                integralRuleDao.deleteByQuery(integralRuleDao.getQuery().filter("_id", id));
                Write("删除成功", true, "");
                return;
			}
		}
		Write("未知错误导致删除失败！", false, "");
	}

	@Page(Viewer = DEFAULT_AORU,des="新增/编辑")
	public void aoru() {
		try {
			String id = param("id");
			if (id.length() > 0) {
				IntegralRule integralRule = integralRuleDao.get(id);
				setAttr("integralRule", integralRule);
				setAttr("integType", IntegType.getIntegTypeMap());

			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = ".xml",des="保存新增/编辑")
	public void doAoru() {
		try {
			
			String id = param("id");//id
			int seqNo = intParam("seqNo");//排序号
			String type = param("type");//类型

			String rule = param("rule");//规则
			String memo = request.getParameter("memo");//备注说明
			/*start by xwz 20170729*/
			String typeCode = param("typeCode");//类型代码
			String score = param("score");//积分
			int integType = intParam("integType");//积分类型（1：一次性，2：周期性，3：每次）
			String period = param("period");//周期
			/*end*/
			
			IntegralRule integralRule = new IntegralRule(integralRuleDao.getDatastore());
			integralRule.setSeqNo(seqNo);
			integralRule.setRule(rule);
			integralRule.setType(type);
			integralRule.setTypeCode(typeCode);
			integralRule.setMemo(memo);
			integralRule.setScore(score);
			integralRule.setIntegType(integType);
			integralRule.setPeriod(period);

			int res = 0;
			if (id.length() > 0) {
				integralRule.setMyId(id);
				boolean flag = integralRuleDao.updateIntegralRule(integralRule);
				if(flag){
					res = 2;
				}
			} else {
				String nId  = integralRuleDao.addIntegralRule(integralRule);
				if (nId != null) {
					res = 2;
				}
			}
			if (res > 0) {
				WriteRight("操作成功");
				return;
			}else{
				WriteError("操作失败");
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteError(L("操作失败"));
			return;
		}
	}

}

