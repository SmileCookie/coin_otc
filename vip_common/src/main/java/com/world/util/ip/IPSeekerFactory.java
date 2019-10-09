package com.world.util.ip;

import com.world.config.GlobalConfig;
import com.world.util.ip.impl.IPSeeker;
import org.apache.log4j.Logger;


public class IPSeekerFactory {

	private final static Logger log = Logger.getLogger(IPSeekerFactory.class.getName());

	private static IPSeeker seeker = null;
	
	private synchronized static void init(){
		
		String path = IPSeekerFactory.class.getClassLoader().getResource("").getPath(); 
		log.info(path);
		if(seeker == null)
			seeker=new IPSeeker(GlobalConfig.getValue("ip_database"),path);
	}
	
	public static IPSeeker getSeeker(){
		if(seeker == null){
			init();
		}
		return seeker;
	}
}
