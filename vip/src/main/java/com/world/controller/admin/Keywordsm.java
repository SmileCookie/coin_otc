package com.world.controller.admin;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.user.KeywordDao;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.user.Keyword;
import com.world.util.string.CnCharactManager;
import com.world.util.string.KeyWordFilter;
import com.world.util.string.KeyWordType;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

import java.util.List;
import java.util.regex.Pattern;
@FunctionAction(jspPath = "/admins/keyword/" , des = "关键字")
public class Keywordsm extends AdminAction {

	KeywordDao dao = new KeywordDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		String  word = param("word");
		int typeId = intParam("kType");

		Query<Keyword> q = dao.getQuery();
		int pageSize = 50;

		// 将参数保存为attribute
		try {
			// 构建查询条件
			if(word.length()>0){//标题
				Pattern pattern = Pattern.compile("^.*"  + word+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("word", pattern);
			}
			if(typeId > 0){
				q.filter("typeId", typeId);
			}
			
			long total = dao.count(q);
			if (total > 0) {
				List<Keyword> dataList = dao.findPage(q.order("- adate"), pageNo, pageSize);
				
				setAttr("dataList", dataList);
			}
			setPaging((int) total, pageNo, pageSize);
			setAttr("types", EnumUtils.getAll(KeyWordType.class));
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}
	
	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			String id = param("id");
			setAttr("id", id);
			if (id.length() > 0) {
				Keyword data = dao.getById(id);
				setAttr("keyword", data);
			}
			setAttr("types", EnumUtils.getAll(KeyWordType.class));
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	/**
	 * 功能:响应添加的函数
	 */
	@Page(Viewer = XML)
	public void doAoru() {
		try {
			String  id = param("id");
			String  word = param("word");//标题
			int typeId = intParam("kType");
			
			word = word.toLowerCase();
			
			Keyword  key = dao.findOne(dao.getQuery().filter("word", word).filter("typeId", typeId));
			if(key != null){
				WriteError("关键字已存在。");
				return;
			}
			
			int res = 0;
			Datastore ds = dao.getDatastore();
			if (id.length() > 0) {
				Query<Keyword> query = ds.find(Keyword.class, "_id", id);
				
				UpdateOperations<Keyword> operate = ds.createUpdateOperations(Keyword.class)
														.set("word" , word)//标题
														.set("typeId", typeId)
				;
				UpdateResults<Keyword> ur = ds.update(query, operate);
				if (!ur.getHadError()) {
					res = 2;
				}
			} else {
				String splitTag = "";
				if(word.contains(";")){
					splitTag = ";";
				}else if(word.contains(",")){
					splitTag = ",";
				}else if(word.contains(" ")){
					splitTag = " ";
				}else if(word.contains(".")){
					splitTag = ":";
				}else if(word.contains("；")){
					splitTag = "；";
				}else if(word.contains("\n")){
					splitTag = "\n";
				}else if(word.contains("\t")){
					splitTag = "\t";
				}
				
				if(splitTag.length() > 0){
					String[] worlds = word.split(splitTag);
					if(worlds.length > 0){
						for(String w : worlds){
							w = CnCharactManager.getPingYin(w);
							w = w.toLowerCase();
							Keyword  ckey = dao.findOne(dao.getQuery().filter("word", w).filter("typeId", typeId));
							if(ckey == null){
								Keyword e = new Keyword(ds);
								e.setWord(w);
								e.setAdate(now());
								e.setTypeId(typeId);
								if (dao.save(e) != null) {
									res = 2;
								}
							}
						}
					}
				}else{
					Keyword e = new Keyword(ds);
					e.setWord(word);
					e.setAdate(now());
					e.setTypeId(typeId);
					if (dao.save(e) != null) {
						res = 2;
					}
				}
			}
			if (res > 0) {
				
				KeyWordFilter.initPattern(typeId);
				Write("操作成功", true, "");
				return;
			}else{
				Write("没有有效的添加项", false, "");
				return;
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
		Write("未知错误导致添加失败！", false, "");
	}
	
	@Page(Viewer = XML)
	public void doDel() {
		String id = param("id");
		if (id.length() > 0) {
			Keyword key = dao.get(id);
			boolean res = true;
			if (res) {
                dao.delById(id);
                KeyWordFilter.initPattern2(key.getTypeId());
                Write("删除成功", true, "");
                return;
			}
		}
		Write("未知错误导致删除失败！", false, "");
	}
	
}
