package com.world.config;

import com.world.cache.MemoryBean;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.apache.log4j.Logger;

public class MemoryConfig {
	private static Logger log = Logger.getLogger(MemoryConfig.class);
	public static MemoryConfig memory;
	public static MemoryBean memoryBean;
	public static MemcachedClientBuilder memBuilder;
	public static MemcachedClient mem = null;
	public static boolean init = false;
	public static MemcachedClient getMem() {
		if (!init) {
			mem = Config();
		}
		return mem;
	}

	public int startNumber() {
		return 101;
	}

	public synchronized static MemcachedClient Config() {
		if(mem != null){
			return mem;
		}
		String ip = "";
		String port = "";
		try {
			ip = GlobalConfig.memcachedIp;
			port = GlobalConfig.memcachedPort;
			log.info("ip:" + ip + ",port:" + port);
			memBuilder = new XMemcachedClientBuilder(AddrUtil.getAddresses(ip + ":" + port));
			memBuilder.setConnectionPoolSize(100);
			memBuilder.setFailureMode(true);

			// 使用二进制文件
//			memBuilder.setCommandFactory(new BinaryCommandFactory());
			
			mem = memBuilder.build();
			mem.getTranscoder().setCompressionThreshold(10485760);
		} catch (Exception ex) {
			log.error("error: ip:" + ip + ",port:" + port, ex);
		}
		init = true;
		return mem;
	}

}