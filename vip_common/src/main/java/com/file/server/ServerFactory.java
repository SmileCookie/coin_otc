package com.file.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.file.config.FileConfig;
import org.apache.log4j.Logger;

/****
 * server生产中心
 * @author Administrator
 *
 */
public class ServerFactory {

	private final static Logger log = Logger.getLogger(ServerFactory.class.getName());

	public static Map<Integer , ServerGroup> groups = null;
	public static Map<String , TomcatServer> servers = new HashMap<String, TomcatServer>();
	public static Map<Integer , TomcatServer> intServers = new HashMap<Integer, TomcatServer>();
	
	private synchronized static void init(){
		if(groups == null){
			groups = new HashMap<Integer, ServerGroup>();
			String gs = FileConfig.getValue("groups");
			JSONArray jas = JSONArray.fromObject(gs);
			for(Object o : jas){
				JSONObject jo = (JSONObject) o;
				Object serversStr = jo.get("servers");
				log.info(serversStr);
				
				List<TomcatServer> ss = new ArrayList<TomcatServer>();
				JSONArray ja2 = JSONArray.fromObject(serversStr);
				for(Object o2 : ja2){
					JSONObject jo2 = (JSONObject) o2;
					TomcatServer s = (TomcatServer)jo2.toBean(jo2 , TomcatServer.class);
					servers.put(s.getDomain(), s);
					intServers.put(s.getId(), s);
					ss.add(s);
				}
				ServerGroup sg = (ServerGroup) jo.toBean(jo, ServerGroup.class);
				sg.setServers(ss);
				
				groups.put(sg.getId(), sg);
			}
			
		}
	}
	
	public static Map<Integer , ServerGroup> getGroups(){
		if(groups == null){
			init();
		}
		return groups;
	}
	/****
	 * 获取服务器组
	 * @param id
	 * @return
	 */
	public static ServerGroup getGroupById(int id){
		if(groups == null){
			init();
		}
		
		if(groups != null){
			return groups.get(id);
		}
		return null;
	}
	/****
	 * 根据域获取其所在的服务器
	 * @param domain
	 * @return
	 */
	public static TomcatServer getServerByDomain(String domain){
		if(groups == null){
			init();
		}
		
		if(servers != null){
			return servers.get(domain);
		}
		return null;
	}
	/****
	 * 获取域所在的组
	 * @param domain
	 * @return
	 */
	public static ServerGroup getGroupByDomain(String domain){
		TomcatServer ts = getServerByDomain(domain);
		if(ts != null){
			return getGroupById(ts.getSgId());
		}
		return null;
	}
	/****
	 * 获取当前主机域名
	 * @return
	 */
	public static String getHostDomain(){
		return FileConfig.getValue("domain");
	}
	/***
	 * 获取主机ID
	 * @return
	 */
	public static int getHostServerId(){
		String hd = getHostDomain();
		TomcatServer ts = getServerByDomain(hd);
		if(ts != null){
			return ts.getId();
		}
		return 1;
	}
	
	public static String getBaseDomain(){
		return FileConfig.getValue("bdomain");
	}
	
	/****
	 * 文件保存的基路径
	 * @return
	 */
	public static String getSaveBasePath(){
		return FileConfig.getValue("saveBasePath");
	}
	
	/****
	 * 上传文件保存的基路径
	 * @return
	 */
	public static String getUploadBasePath(){
		return FileConfig.getValue("uploadBasePath");
	}
	
	/****
	 * 上传文件保存的基路径
	 * @return
	 */
	public static int getUploadFolderMaxNum(){
		return Integer.parseInt(FileConfig.getValue("uploadMaxNum"));
	}
	
	public static TomcatServer getServerById(int id){
		if(groups == null){
			init();
		}
		
		if(intServers != null){
			return intServers.get(id);
		}
		return null;
	}
	

	
	public static void main(String[] args) {
		ServerGroup sg = ServerFactory.getGroupById(1);
		log.info(sg.getServers().get(1).getIp());
	}
	
	
}
