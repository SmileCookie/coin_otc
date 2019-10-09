package com.world.web;

import javax.servlet.http.HttpServletRequest;

import com.world.cache.Cache;
import com.world.config.GlobalConfig;
import com.world.util.ip.IpUtil;
import org.apache.log4j.Logger;

/**
 * 限制用户访问的类
 * 根据ip具有白名单黑名单和指定key黑名单功能
 * @author pc
 *
 */
public class Limit {

	private final static Logger log = Logger.getLogger(Limit.class.getName());

	/**
	 * ip限制
	 * @param request 请求
	 * @param name  限制名称
	 * @return -1:黑名单限制 -2:   0:没有被限制 
	 */
	  private int ipLimit(HttpServletRequest request,String name){
		 
		  String ip=IpUtil.getIp(request);
		  //Cache.Delete("iplimit10mHei_"+ip);
		  //先取白名单
		  String currentBai=Cache.Get("iplimit10mBai_"+ip);//10分钟的,一分钟内的意义不大
		  if(currentBai!=null)
		  {
			  
			  return 1;//如果在白名单中就直接ok
		  }
		  String currentHei=Cache.Get("iplimit10mHei_"+ip);//10分钟的,一分钟内的意义不大
		  
		  if(currentHei!=null)
		  {log.info("ip为" + ip + "的用户因为过多访问" + currentHei + "被列入黑名单");
			  return 0;//如果已经在黑名单中就直接false
		  }
		  //再取当前的一分钟内的数据
		  String current=Cache.Get("iplimit10m_"+ip);//10分钟的,一分钟内的意义不大
		  if(current==null){
			  Cache.Set("iplimit10m_"+ip, "1_"+System.currentTimeMillis(),600);
			  log.info("第一次访问");
					  return 1;
		  }
		  else
		  {
			  int time=Integer.parseInt(current.split("_")[0]);
			
			  if(time<GlobalConfig.forbidfIp10MinuteTimes){
				  long lastTime=Long.parseLong(current.split("_")[1]);//第一次存入的时间
				  //新的时间剩余秒数为如下的：
				  long sec=600-(System.currentTimeMillis()-lastTime)/1000;
				
				  Cache.Set("iplimit10m_"+ip, Integer.toString(time+1)+"_"+lastTime,Integer.parseInt(Long.toString(sec)));
				  log.info(Integer.toString(time + 1) + "_" + lastTime + "秒钟" + sec);
				  return 0;
			  }
			  else
			  {
				  Cache.Set("iplimit10mHei_"+ip, request.getRequestURI());
				  log.info("禁止访问");
				  return 0;
			  }
		  }
		 
	  }
	  
}
