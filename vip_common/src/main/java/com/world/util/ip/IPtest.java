package com.world.util.ip;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class IPtest extends TestCase {

	private final static Logger log = Logger.getLogger(IPtest.class);
	
	public void testIp(){
//		 String sep = System.getProperty("file.separator");
//
//		    // Uncomment for windows
//		    // String dir = System.getProperty("user.dir"); 
//
//		    // Uncomment for Linux
//		    String dir = "/usr/local/share/GeoIP";
//
//		    String dbfile = "";//dir + sep; 
//		
//                //指定纯真数据库的文件名，所在文件夹
//		IPSeeker ip=new IPSeeker("qqwry.dat",null);
//		 //测试IP 58.20.43.13
//		log.info(ip.getIPLocation("123.108.212.116").getCountry()+":"+ip.getIPLocation("123.108.212.116").getArea());
		long initUsedMemory = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) ;
	log.info(System.currentTimeMillis());
		log.info("省份：" + IpUtil.getProvince("192.168.3.40"));
		log.info("市：" + IpUtil.getCity("123.108.212.116"));
		log.info(System.currentTimeMillis());

	long endUsedMemory = ( Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() ) ;
	
	log.info("memory consumes:" + ("init:" + initUsedMemory / 1024 + "K,>" + (endUsedMemory - initUsedMemory)));
	}
}


