package com.world.controller.admin.loan.p2puser.defaultvalue;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.dao.admin.logs.DailyRecordDao;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.loan.dao.DefaultLimitDao;
import com.world.model.loan.entity.DefaultLimit;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;


@FunctionAction(jspPath = "/admins/loan/p2puser/defaultvalue/", des = "默认值管理")
public class Index extends AdminAction {

	private static final long serialVersionUID = 1L;
	
	DefaultLimitDao defaDao = new DefaultLimitDao();
	DailyRecordDao reDao = new DailyRecordDao();
	AdminUserDao aUDao = new AdminUserDao();

	/***
	 * 配置的修改
	 * @author chenruidong
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		try {
			// 获取参数
			int pageNo = intParam("page");
			//加载下拉列表
			setAttr("typeName", defaDao.findTypeName());
			String typeName = param("typeName");
			
			Query query = defaDao.getQuery();
				query.setSql("SELECT * from defaultlimit").setCls(DefaultLimit.class);
				if (typeName.length() > 0) { query.append(" AND typeName='" + typeName + "'"); }
				
				int total = query.count();
				if (total > 0) {
					query.append("ORDER BY id");
					List<Bean> dataList = defaDao.findPage(pageNo, 10);
					setAttr("dataList", dataList);
					setAttr("itemCount", total);
				}
			setPaging(total, pageNo, 10);
		} catch (Exception e) {
			log.error("手动设置显示有异常！---", e);
		}
	}

	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	/***
	 * 显示默认值修改框
	 * @author chenruidong
	 */
	@Page(Viewer = "/admins/loan/p2puser/defaultvalue/delimitvalue.jsp")
	public void delimitvalue() {
		try {
			int ids = intParam("ids");
			String typeName = param("fundsType");
			String keyName = param("fkey");
			
			if(StringUtils.isBlank(typeName)){
				log.info("显示默认值修改框异常。");
				return;
			}
			DefaultLimit delist = defaDao.findOneKey(ids, typeName, keyName);
			setAttr("dataList", delist);

		} catch (Exception e) {
			log.error("手动设置显示有异常！---", e);
		}
	}
	
	/***
	 * 接收修改处理系统默认值
	 * @author chenruidong
	 */
	@Page(Viewer = JSON)
	public void updateMoRenValue() {
		List<OneSql> sqls=new ArrayList<OneSql>();
		try {
			int ids = intParam("ids");
			String typeName = param("typeName");
			String keyName =param("keyName");
			String valueName = param("valueName");
			String reMarks=param("reMark");
			if(reMarks.length()>100){
				json("备注字数不能大于一百！", false, "");
				return;
			}
			sqls.add(defaDao.getUpdateValue(valueName, ids, typeName, keyName, reMarks));
			if (Data.doTrans(sqls)) {
				json("费率默认值更改成功。", true, "");
			} else {
				json("费率默认值更改失败！", false, "");
			}
		} catch (Exception e) {
			log.error("费率默认值修改失败---", e);
		}
	}
	
	/***
	 * 新增默认值
	 * @author chenruidong
	 */
	@Page(Viewer = "/admins/loan/p2puser/defaultvalue/AddDeLimit.jsp")
	public void AddDeLimit() {
		try {
			setAttr("keyName", DatabasesUtil.getCoinPropMaps());
			
		} catch (Exception e) {
			log.error("内部异常", e);
		}
	}
	@SuppressWarnings("unchecked")
	@Page(Viewer = JSON)
	public void addMoRenValue() {
		try {
			String keyName = param("keyName");
			String typeName = param("typeName");
			String valueName = param("valueName");
			String reMarks=param("reMark");
			
			if (StringUtils.isBlank(typeName)) {
				json("类型名称不可为空！", false, "");
				return;
			}
			if(!typeName.matches("^[A-Za-z1-9_]+$")){
				json("类型名称只允许大小字母数字下划线！", false, "");
				return;
			}
			if (StringUtils.isBlank(keyName)) {
				json("请选择币种！", false, "");
				return;
			}
			if (reMarks.length() > 100) {
				json("备注字数不能大于一百!", false, "");
				return;
			}
			if (defaDao.count("SELECT * FROM defaultlimit WHERE typeName=? AND keyName=?", new Object[] { typeName, keyName }) > 0) {
				json("一个币种不能有相同类型名称！", false, "");
				return;
			}
			List<OneSql> sqls=new ArrayList<OneSql>();
//			sqls.add(defaDao.findInsert(typeName, keyName, valueName, reMarks));
			DefaultLimit df=new DefaultLimit(typeName, keyName, valueName);
			df.setReMarks(reMarks);
			sqls.add(defaDao.getTransInsertSql(df));

			if(Data.doTrans(sqls)){
				json("添加费率默认值成功！", true, "");
			}else {
				json("添加费率默认值失败！", false, "");
			}
		} catch (Exception e) {
			log.error("添加默认值失败---", e);
		}
	}
	/***
	 * @sees 删除默认配置
	 * @author chenruidong
	 */
	@Page(Viewer = XML)
	public void deleteDeFaLimit() {
		try {
			int ids = intParam("ids");
			String typeName = param("fundsType");
			String keyName = param("fkey");
			
			if (ids == 0 || StringUtils.isBlank(typeName)) {
				Write("配置编号或者状态不能为空", false, "{}");
				return;
			}
			List<OneSql> sqls=new ArrayList<OneSql>();
			sqls.add(defaDao.findDelete(ids, typeName, keyName));
			if (Data.doTrans(sqls)) {
				Write("删除成功", true, "");
			} else {
				Write("删除失败", false, "");
			}
		} catch (Exception e) {
			log.error("删除一条费率默认值失败---", e);
		}
	}
}
