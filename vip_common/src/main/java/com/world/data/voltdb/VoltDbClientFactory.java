package com.world.data.voltdb;

import org.apache.log4j.Logger;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.NoConnectionsException;

import com.world.config.GlobalConfig;

public class VoltDbClientFactory {
private static Client client = null;

	private static Logger log = Logger.getLogger(VoltDbClientFactory.class.getName());
	
	private static synchronized void init(){
		if(client == null){
			try {
				// Create a client and connect.
//				config = new ClientConfig("admin","voltdb");
//				config.setProcedureCallTimeout(90 * 1000);
//				config.setConnectionResponseTimeout(180 * 1000);
				ClientConfig config = new ClientConfig();
				config.setReconnectOnConnectionLoss(true);
				client = ClientFactory.createClient(config);
				String ip = GlobalConfig.getValue("voltdb_1_ip");
				int port = Integer.parseInt(GlobalConfig.getValue("voltdb_1_port"));
				
				client.createConnection(ip , port);
//	            client = ClientFactory.createClient();
//	            client.createConnection("192.168.2.167", 8080);
//				client = ClientFactory.createClient(config);
//				client.createConnection("myserver.xyz.net");
			} catch (java.io.IOException e) {
				log.error(e.toString(), e);
			} catch(Exception ex){
				log.error(ex.toString(), ex);
			}
		}
	}
	
	public static Client getClient(){
		if(client == null){
			init();
		}
		return client;
	}
	
	public static void close(){
		if(client != null){
			try {
				client.drain();
				client.close();
			} catch (NoConnectionsException e) {
				log.error(e.toString(), e);
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
			}
		}
	}
}
