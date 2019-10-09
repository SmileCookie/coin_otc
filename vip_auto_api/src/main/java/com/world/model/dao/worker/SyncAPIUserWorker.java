package com.world.model.dao.worker;

import org.apache.log4j.Logger;

import com.api.VipResponse;
import com.api.user.UserManager;
import com.world.common.VerifiUtil;
import com.world.controller.Base;
import com.world.model.dao.task.Worker;

import net.sf.json.JSONObject;

/**
 * 同步Memcached API IP访问数据
 * @author Lijiawen
 */
public class SyncAPIUserWorker extends Worker {

	/*private static final long serialVersionUID = 4180292500703424744L;

	static Logger logger = Logger.getLogger(Base.class);

	public SyncAPIUserWorker(String name, String des) {
		super(name, des);
	}

	@Override
	public void run() {
		super.run();

		log.info("同步API 用户限制信息数据 start...");
		try{
			sync();
		}catch (Exception ex){
			log.error("同步API 用户限制信息数据出错", ex);
		}
		log.info("同步API 用户限制信息数据 end...");
	}

	private synchronized void sync() throws Exception{
		if( VerifiUtil.users!=null ){
			for(String userKey : VerifiUtil.users.keySet()){
				JSONObject userObject = (JSONObject)  VerifiUtil.users.get(userKey);
				String key = userKey.split("_")[ userKey.split("_").length-1 ];
				
				//读取数据
				VipResponse response = UserManager.getInstance().getUserByAccessKey(key, 0);
				if(response == null || !response.getMsg().startsWith("{")){
					log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
					continue;
				}
				if(response.taskIsFinish()){
					JSONObject json = JSONObject.fromObject(response.getMsg());
					if(json.getBoolean("isSuc")){
						userObject = JSONObject.fromObject(json.getString("datas"));
						//最后一次查询时间
						VerifiUtil.users.put("user_object_json_" + key, userObject);
					}else{
						log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
					}
				}else{
					log.error(key+"VIP项目-API请求错误，返回内容：" + response.getMsg());
				}
			}
		}
	}*/
}
