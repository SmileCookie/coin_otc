package com.world.data.id;

import com.world.config.GlobalConfig;
import org.apache.log4j.Logger;

public class IdWorkerUtil {

	private static Logger log = Logger.getLogger(IdWorkerUtil.class.getName());

	public static IdWorker worker = null;
	
	private synchronized static void init(){
		if(worker == null){
			int workerId = 1;//工作者的id
			try {
				String idWorkerId = GlobalConfig.getValue("idWorkerId");
				if(idWorkerId != null && idWorkerId.length() > 0){
					workerId = Integer.parseInt(idWorkerId);
				}
			} catch (NumberFormatException e) {
				log.error(e.toString(), e);
			}
			worker = new IdWorker(workerId, 1, 1);
		}
	}
	
	public static long getId(){
		if(worker == null){
			init();
		}
		
		return worker.getId();
	}
}
