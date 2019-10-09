/*package com.world.controller.admin.lan;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.Lan;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.model.dao.lan.LanStoreDao;
import com.world.model.entity.lan.LanStore;
import com.world.util.date.TimeUtil;
import com.world.util.path.PathUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;
import com.world.web.language.VIpResourceBundle;

@FunctionAction(jspPath = "/admins/lan/", des = "语言库管理")
public class Index extends AdminAction {
	
	LanStoreDao dao = new LanStoreDao();
	
	@Page(Viewer = "/admins/lan/list.ftl")
	public void index() {
		String tab = param("tab");
		if(StringUtils.isEmpty(tab)) tab = "en";
		setAttr("tab", tab);
	}

	// ajax的调用
	@Page(Viewer = "/admins/lan/ajax.ftl")
	public void ajax() {
		index();
	}
	
	@Page(Viewer = JSON)
	public void like() {
		String key = request.getParameter("key");
		try {
			key = URLDecoder.decode(key, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		List<LanStore> lists = dao.like(key);
		json("ok", true, JSONArray.toJSONString(lists));
	}
	
	@Page(Viewer = JSON)
	public void get(){
		String key = request.getParameter("key");
		try {
			key = URLDecoder.decode(key, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		LanStore lan = dao.get(key);
		if(lan != null){
			json("ok", true, JSONObject.toJSONString(lan));
		}else{
			json("ok", false, "");
		}
	}
	
	@Page(Viewer = XML)
	public void submit(){
		
		try {
			String id = param("id");
			String key = request.getParameter("key");
			String value = request.getParameter("value");
			String tab = param("tab");
			
			boolean flag = false;
			
			if(StringUtils.isEmpty(id)){
				LanStore lan = new LanStore(dao.getDatastore());
				lan.setKey(key);
				lan.setValue(tab, toUpperCaseFirstOne(value));
				lan.setIsCacheValue(1);
				lan.setAddTime(TimeUtil.getNow());
				flag = dao.add(lan);
			}else{
				LanStore lan = dao.getById(id);
				lan.setKey(key);
				lan.setValue(tab, toUpperCaseFirstOne(value));
				lan.setIsCacheValue(1);
				flag = dao.update(lan);
			}
			
			//重新加载
			Lan.setLanguageResource(tab);
			
			if(flag){
				WriteRight("操作成功");
			}else{
				WriteError("操作失败");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			WriteError("内部异常");
			log.error(e.toString(), e);
		}
	}
	
	@Page(Viewer = JSON)
	public void check(){
		String lan = param("lan");
		InputStreamReader stream = null;
		try {
			log.info("当前加载语言包" + lan);
			PathUtil util = new PathUtil();
			stream = util.getConfigFileInputStreamReader("/L_" + lan + ".txt");
			VIpResourceBundle Prrb = new VIpResourceBundle(stream);
			Map<String, Object> map = Prrb.getLookup();
			if(map != null){
				List<LanStore> lists = new LanStoreDao().all();
				Iterator iter = map.entrySet().iterator();
				int snum = 0;
				int unum = 0;
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next(); 
					Object key = entry.getKey();
					boolean flag = false;
					for (LanStore lanStore : lists) {
						if(lanStore.getKey().equals(key.toString())){
							unum++;
							flag = true;
							break;
						}
					}
					if(!flag){
						snum++;
					}
				}
				JSONArray arr = new JSONArray();
				arr.add(snum);
				arr.add(unum);
				json("", true, arr.toJSONString());
			}else{
				json("", false, "");
			}
		 }catch(Exception ex){
			 json("内部错误", false, "");
			 log.error(ex.toString(), ex);
		 }finally{
			 if(stream != null){
				 try {
					stream.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			 }
		 }
	}
	
	@Page(Viewer = JSON)
	public void imp(){
		String lan = param("lan");
		String tp = request.getParameter("tp");
		InputStreamReader stream = null;
		try {
			log.info("当前加载语言包" + lan);
			PathUtil util = new PathUtil();
			stream = util.getConfigFileInputStreamReader("/L_" + lan + ".txt");
			VIpResourceBundle Prrb = new VIpResourceBundle(stream);
			Map<String, Object> map = Prrb.getLookup();
			if(map != null){
				List<LanStore> dlist = new ArrayList<LanStore>();
				List<LanStore> lists = new LanStoreDao().all();
				Iterator iter = map.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next(); 
					Object key = entry.getKey();
					Object val = entry.getValue();
					boolean flag = false;
					for (LanStore lanStore : lists) {
						if(lanStore.getKey().equals(key.toString())){
							lanStore.setValue(lan, toUpperCaseFirstOne(val.toString()));
							dlist.add(lanStore);
							flag = true;
							break;
						}
					}
					if(!flag){
						LanStore ln = new LanStore(dao.getDatastore());
						ln.setKey(key.toString());
						ln.setValue(lan, toUpperCaseFirstOne(val.toString()));
						ln.setIsCacheValue(1);
						ln.setAddTime(TimeUtil.getNow());
						dlist.add(ln);
					}
				}
				
				int succNum = 0;
				//准备做更新获插入操作
				for (LanStore lanStore : dlist) {
					try {
						//更新操作
						if("update".equalsIgnoreCase(tp) && StringUtils.isNotEmpty(lanStore.getId())){
							if(dao.update(lanStore)){
								succNum++;
							}
						}else if("add".equalsIgnoreCase(tp) && StringUtils.isEmpty(lanStore.getId())){
							if(dao.add(lanStore)){
								succNum++;
							}
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
				//重新加载
				Lan.setLanguageResource(lan);
				
				json("成功处理"+succNum+"个", true, "");
			}else{
				json("没有可用的数据", false, "");
			}
		 }catch(Exception ex){
			 json("内部错误", false, "");
			 log.error(ex.toString(), ex);
		 }finally{
			 if(stream != null){
				 try {
					stream.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			 }
		 }
	}
	
	//首字母大写
	public static String toUpperCaseFirstOne(String s){
		if(StringUtils.isEmpty(s)) return "";
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }
	
	public static void main(String[] args) {
	}
}

*/