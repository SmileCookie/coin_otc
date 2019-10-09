/*package com.world.util.checker;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.DocumentHelper;
//
//import cn.edu.zju.cst.mina.im.server.entity.User;
//import cn.edu.zju.cst.mina.im.server.handler.ServerControler;

public class UserStateManage extends Thread {
	
	//在线用户状态列表
	static HashMap<Integer, UserState> userStateList = new HashMap<Integer, UserState>();
	Object hashLock = new Object();
	
	//当前的连接数和工作线程数
	static int workThreadNum = 0;
	static int socketConnect = 0;
	
	private ServerSocket serverSocket;
	//服务器IP
	private String host = "10.82.81.79";
	
	//服务器端口
	private int stateReportPort = 60001;
	
	//设置心跳包的结束标记
	String endFlag = "</protocol>";
	CharSequence csEndFlag = endFlag.subSequence(0, 10);
	
	//扫描间隔
	private int scanTime = 1800; 
	
	
	@Override
	public void run() {
		//绑定端口,并开始侦听用户的心跳包
		serverSocket = startListenUserReport(stateReportPort);
		if(serverSocket == null){
			log.info("【创建ServerSocket失败！】");
			return;
		}
		//启动扫描线程
		Thread scanThread = new Thread(new scan());
		scanThread.start();
		//等待用户心跳包请求
		while(true){
			Socket socket = null;
			try {
				socketConnect = socketConnect + 1;
				//接收客户端的连接
				socket = serverSocket.accept();   
				//为该连接创建一个工作线程
				Thread workThread = new Thread(new Handler(socket));
				//启动工作线程
				workThread.start();
			} catch (IOException e) {
				log.error(e.toString(), e);
			}
		}
	}
	
	*//**
	 * 创建一个ServerSocket来侦听用户心跳包请求
	 * @param port 指定的服务器端的端口
	 * @return 返回ServerSocket
	 * @author dream
	*//*
	public ServerSocket startListenUserReport(int port){
		try {
			ServerSocket serverSocket = new ServerSocket();
			if(!serverSocket.getReuseAddress()){
				serverSocket.setReuseAddress(true);
			}
			serverSocket.bind(new InetSocketAddress(host,port));
			log.info("【开始在"+serverSocket.getLocalSocketAddress()+"上侦听用户的心跳包请求！】");
			return serverSocket;
		} catch (IOException e) {
			log.info("【端口"+port+"已经被占用！】");
			if (serverSocket != null) {
				if (!serverSocket.isClosed()) {
					try {
						serverSocket.close();
					} catch (IOException e1) {
						log.error(e1.toString(), e1);
					}
				}
			}
		}
		return serverSocket;
	}
	
	
	//工作线程类
	class Handler implements Runnable{
		private Socket socket;
		UserState us = null;
		User newUser = null;
		private int userId;
		private int userState;
		*//**
		 * 构造函数，从调用者那里取得socket
		 * @param socket 指定的socket
		 * @author dream
		*//*
		public Handler(Socket socket){
			this.socket = socket;
		}
		
		*//**
		 * 从指定的socket中得到输入流
		 * @param socket 指定的socket
		 * @return 返回BufferedReader
		 * @author dream
		 *//*
		private BufferedReader getReader(Socket socket){
			InputStream is = null;
			BufferedReader br = null;

			try {
				is = socket.getInputStream();
				br = new BufferedReader(new InputStreamReader(is));
			} catch (IOException e) {
				log.error(e.toString(), e);
			}
			return br;
		}
		
 		public void run() {
			try{
				workThreadNum = workThreadNum +1;
				log.info("【第"+workThreadNum+"个的连接:"+socket.getInetAddress()+":"+socket.getPort()+"】");
				BufferedReader br = getReader(socket);
				String meg = null;
				StringBuffer report = new StringBuffer();
				while ((meg = br.readLine()) != null) {
					report.append(meg);
					if (meg.contains(csEndFlag)) {
						us = getReporterUserState(meg, socket);
						synchronized (hashLock) {
							userStateList.put(userId, us);
						}
					}
				}
			}catch(IOException e){
				log.info("【客户:"+newUser.getUser_id()+"已经断开连接！】");
				userStateList.remove( userId );
				announceStateChange( userId , -1);
			}finally{
				if(socket != null){
					try {
						//断开连接
						socket.close();
					} catch (IOException e) {
						log.error(e.toString(), e);
					}
				}
			}
		}
 		private UserState getReporterUserState(String meg , Socket socket){
 			UserState us = new UserState();
 			try {
				Document requestDoc = DocumentHelper.parseText(meg);
				newUser = ServerControler.parseXmlToUserState(requestDoc,socket);
				userId = newUser.getUser_id();
				userState = newUser.getUser_state();
				us.setFlag(2);
				us.setUser_state( userState );
				us.setUser_id( userId );
				us.setUser_ip(newUser.getUser_ip());
				us.setUser_port(newUser.getUser_port());
			} catch (DocumentException e) {
				log.info("【来自客户端的信息不是一个合法的心跳包协议】");
			}
			return us;
 		}
	}
	
	//扫描线程
	class scan implements Runnable{
		public void run() {
			while (true) {
				log.info("*******"+new Date()+"：扫描线程开始扫描"+"*******");
				synchronized (hashLock) {
					if(!userStateList.isEmpty()){
						//遍历在线用户列表
						for (Map.Entry<Integer, UserState> entry : userStateList.entrySet()) {
							int flag = entry.getValue().getFlag();
							if ( (flag - 1) < 0) {
								//在这里通知该用户的好友其状态发生改变
//								announceStateChange(entry.getKey() , 0);
							}else{
								entry.getValue().setFlag(flag - 1);
								userStateList.put(entry.getKey(), entry.getValue());
							}
							log.info(entry.getKey() + "-->" + entry.getValue().toString());
						}
					}else{
						log.info("现在还没有在线用户！");
					}
				}
				//实现定时扫描
				try {
					sleep(scanTime);
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
				}
			}
		}
	}
	
	private void announceStateChange(int userId , int state){
		log.info("通知其好友!");
	}
	
	*//**
	 * 查询一个用户是否在线
	 * @param userId 指定要查询状态的用户的ID
	 * @return true 在线； false 不在线；
	 * @author dream
	*//*
	public boolean isAlive(int userId){
		synchronized (hashLock) {
			return userStateList.containsKey(userId);
		}
	}
	
	*//**
	 * 返回指定用户ID的状态
	 * @param userId 指定要查询状态的用户的ID
	 * @return >0 该用户在线;  -1 该用户离线
	 * @author dream
	*//*
	public int getUserState(int userId){
		synchronized (hashLock) {
			if(userStateList.containsKey(userId)){
				return userStateList.get(userId).getUser_state();
			}else{
				return -1;
			}
		}
	}
	
	public Object getHashLock() {
		return hashLock;
	}

	public void setHashLock(Object hashLock) {
		this.hashLock = hashLock;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getStateReportPort() {
		return stateReportPort;
	}

	public void setStateReportPort(int stateReportPort) {
		this.stateReportPort = stateReportPort;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
	}
//fast boot
	public int getScanTime() {
		return scanTime;
	}

	public void setScanTime(int scanTime) {
		this.scanTime = scanTime;
	}

	public static HashMap<Integer, UserState> getUserStateList() {
		return userStateList;
	}

	public static int getWorkThreadNum() {
		return workThreadNum;
	}

	public static int getSocketConnect() {
		return socketConnect;
	}
	//测试本函数的main函数
	public static void main(String arg[]){
		UserStateManage usm = new UserStateManage();
		usm.start();
	}
}*/