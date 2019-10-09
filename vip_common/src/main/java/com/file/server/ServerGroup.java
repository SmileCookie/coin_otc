package com.file.server;

import java.util.List;

/******
 * 服务器组
 * @author Administrator
 *
 */
public class ServerGroup {

	private int id;
	private String name;
	private List<TomcatServer> servers;
	
	public List<TomcatServer> getServers() {
		return servers;
	}
	public void setServers(List<TomcatServer> servers) {
		this.servers = servers;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
