package com.world.model.loan.worker;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.Logger;

import com.world.config.GlobalConfig;
import com.world.model.dao.task.Worker;
import com.world.model.loan.dao.UserDetectDao;
import com.world.model.loan.entity.P2pUser;

public class LevelWorker extends Worker{
	static Logger log = Logger.getLogger(LevelWorker.class.getName());
	public LevelWorker(String name , String des){
		this.name = name;
		this.des = des;
	}
	private static UserDetectDao userDetectDao = new UserDetectDao();
	public static Queue<P2pUser> users = new LinkedList<P2pUser>();
	public static void add(P2pUser user){
		String open = GlobalConfig.getValue("LevelWorker");
		boolean ipOpen = false;
		try {
			ipOpen = open == null ? false : Boolean.parseBoolean(open);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		if(ipOpen && user.hasLoanIn() && users.size() < 500 ){//不易过多，多了就不接受了
			users.add(user);
		}
	}
	
	@Override
	public void run() {
		super.run();
		P2pUser user = null;
		while((user = users.poll()) != null){
			log.info("主动触发：" + user.getUserName());
			try {
				userDetectDao._detect(user);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			//设定用户1min后过期
//			try {
//				AutoFactory.levelUsers.put(user.getUserId(), new LevelUserProduct(user.getUserId() , System.currentTimeMillis() + 60000));
//			} catch (Exception e) {
//				log.error(e.toString(), e);
//			}
		}
		
	}
}
