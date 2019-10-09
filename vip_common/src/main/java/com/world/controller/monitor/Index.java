package com.world.controller.monitor;

import com.atlas.BizException;
import com.world.config.json.JsonConfig;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.connection;
import com.world.model.dao.task.TaskFactory;
import com.world.model.dao.task.Worker;
import com.world.util.monitor.UnixServerInfo;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import net.sf.json.JSONArray;

import java.util.List;

/**
 * Close By suxinjie 一期屏蔽该功能
 */
public class Index  extends AdminAction {

	@Page()
	public void test500(){
		if (true){
			throw new BizException("test 500 error");
		}
	}

//   @Page(Viewer = "")
   public void index(){
	   
   }

//	@Page(Viewer = JSON)
	public void sysinfo() {
		try{
			String serverType = param("serverType");
			//String ethName = param("ethName");
			String sysinfo = UnixServerInfo.getSysInfo(serverType);
			json("操作成功", true, sysinfo);
		}catch(Exception ex){
			json("内部错误", false, "");
			log.error(ex.toString(), ex);
		}
	}
	
	@Page(Viewer = JSON)
	public void timers(){
		try{
			//记录工厂内所有的员工，这里指定时器
			List<Worker> list = TaskFactory.getWorkers();
			if(list.size() == 0){
				json("不存在指定的数据", false, "");
				return;
			}
//			for (Worker worker : list) {
//				log.info("============================" + worker.lastActivityDate);
//			}
			JSONArray array = JSONArray.fromObject(list);
			json("操作成功", true, array.toString());
		}catch(Exception ex){
			json("内部错误", false, "");
			log.error(ex.toString(), ex);
		}
	}
	
//	@Page(Viewer = JSON)
	public void getCurrentTime(){
		json("", true, "{\"des\":"+System.currentTimeMillis()+"}");
	}

}
