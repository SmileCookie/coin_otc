package com.world.controller.admin;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.model.dao.user.CountryDao;
import com.world.model.entity.user.Country;
import com.world.web.Page;
import com.world.web.action.BaseAction;
import com.world.web.convention.annotation.FunctionAction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
@FunctionAction(jspPath = "/admins/country/" , des = "国家")
public class Countrym extends BaseAction {

	CountryDao dao = new CountryDao();
	
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		int pageNo = intParam("page");
		String  name = param("name");
		String  des = param("des");

		Query<Country> q = dao.getQuery();
		int pageSize = 50;

		// 将参数保存为attribute
		try {
			// 构建查询条件
			if(name.length()>0){//标题
				Pattern pattern = Pattern.compile("^.*"  + name+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("name", pattern);
			}
			if(des.length()>0){
				Pattern pattern = Pattern.compile("^.*"  + des+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				q.filter("des", pattern);
			}
			
			long total = dao.count(q);
			if (total > 0) {
				q.order("seq");
				List<Country> dataList = dao.findPage(q, pageNo, pageSize);
				
				setAttr("dataList", dataList);
			}
			setPaging((int) total, pageNo, pageSize);
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
				Country data = dao.getById(id);
				setAttr("country", data);
			}
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
			String  name = param("name");//标题
			String  des = param("des");//内容
			String  code = param("code");//
			String position = param("position");//
			String	seq = param("seq");

			
			int res = 0;
			Datastore ds = dao.getDatastore();
			if (id.length() > 0) {
				Query<Country> query = ds.find(Country.class, "_id", id);
				
				UpdateOperations<Country> operate = ds.createUpdateOperations(Country.class)
														.set("name" , name)//标题
														.set("des" , des)//内容
														.set("code" , code)//摘要
														.set("position" , position)
														.set("seq" , seq);
				
				UpdateResults<Country> ur = ds.update(query, operate);
				if (!ur.getHadError()) {
					res = 2;
				}
			} else {
				Country e = new Country(ds);
				e.setName(name);
				e.setDes(des);
				e.setCode(code);
				e.setPosition(position);
				e.setSeq(seq);
				if (dao.save(e) != null) {
					res = 2;
				}
			}
			if (res > 0) {
				Write("操作成功", true, "");
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
			boolean res = true;
			if (res) {
                dao.delById(id);
                Write("删除成功", true, "");
                return;
			}
		}
		Write("未知错误导致删除失败！", false, "");
	}
	
	@Page(Viewer = XML)
	public void country(){
		try {
			Document doc = Jsoup.connect("http://www.btcbt.com/country.jsp").get();

			Elements ess = doc.select("div .goog-menuitem-content");
			
			CountryDao cDao = new CountryDao();

			for (Element e : ess) {
				
				String style = e.getElementsByClass("talk-flag").attr("style");
				style = style.substring(25, style.indexOf(";"));

				String nameA = e.getElementsByClass("talk-select-country-name").text();
				
				String name = nameA.indexOf("(")>0 ? nameA.substring(0, nameA.indexOf("(")) : nameA;
				
				String des = nameA.indexOf("(")>0 ? nameA.substring(nameA.indexOf("(")+1, nameA.indexOf(")")) : "";
				
				String code = e.getElementsByClass("talk-select-country-code").text();

				
				Country c = new Country(cDao.getDatastore());
				c.setName(name);
				c.setDes(des);
				c.setCode(code);
				c.setPosition(style);
				
				cDao.save(c);
				
				WriteRight("保存成功");
			}
		} catch (IOException e) {
			log.error("内部异常", e);
		}
	}
}
