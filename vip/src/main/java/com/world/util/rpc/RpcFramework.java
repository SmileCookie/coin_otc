/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.world.util.rpc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.file.config.FileConfig;
import com.world.model.entity.coin.CoinProps;

/**
 * RpcFramework
 * 
 * @author william.liangf
 */
public class RpcFramework {
	protected static Logger log = Logger.getLogger(RpcFramework.class.getName());
    /**
     * 暴露服务
     * 
     * @param service 服务实现
     * @param port 服务端口
     * @throws Exception
     */
    public static void export(final Object service, int port) throws Exception {
        if (service == null)
            throw new IllegalArgumentException("service instance == null");
        if (port <= 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port " + port);
        log.info("Export service " + service.getClass().getName() + " on port " + port);
        ServerSocket server = new ServerSocket(port);
        for(;;) {
            try {
                final Socket socket = server.accept();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            try {
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                                try {
                                    String methodName = input.readUTF();
                                    Class<?>[] parameterTypes = (Class<?>[])input.readObject();
                                    Object[] arguments = (Object[])input.readObject();
                                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                    try {
                                        Method method = service.getClass().getMethod(methodName, parameterTypes);
                                        Object result = method.invoke(service, arguments);
                                        output.writeObject(result);
                                    } catch (Throwable t) {
                                        output.writeObject(t);
                                    } finally {
                                        output.close();
                                    }
                                } finally {
                                    input.close();
                                }
                            } finally {
                                socket.close();
                            }
                        } catch (Exception e) {
                            log.error(e.toString(), e);
                        }
                    }
                }).start();
            } catch (Exception e) {
                log.error(e.toString(), e);
            }
        }
    }

    /**
     * 引用服务
     * 
     * @param <T> 接口泛型
     * @param interfaceClass 接口类型
     * @param host 服务器主机名
     * @param port 服务器端口
     * @return 远程服务
     * @throws Exception
     */
    public static Object refer(final String host, final int port) throws Exception {
    	Socket socket = new Socket(host, port);
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            try {
                //output.writeUTF("getinfo");
                //output.writeObject(method.getParameterTypes());
                //output.writeObject(arguments);
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                try {
                    Object result = input.readObject();
                    if (result instanceof Throwable) {
                        try {
							throw (Throwable) result;
						} catch (Throwable e) {
							log.error(e.toString(), e);
						}
                    }
                    return result;
                } finally {
                    input.close();
                }
            } finally {
                output.close();
            }
        } finally {
            socket.close();
        }
    }
    
    public static void main(String[] args) {
    	String result = callJson("http://127.0.0.1:28332" , "{\"jsonrpc\": \"1.0\", \"id\":\"curltest\", \"method\": \"getinfo\", \"params\": [] }");
    	log.info(result);
    	//String result = callJson(GlobalConfig.getValue("wallet"), "{\"jsonrpc\" : \"1.0\", \"id\" : \"curltest\", \"method\" : \"getnewaddress\", \"params\" : []}");
	}
    
    public static String callJson(String urlString , String param) {
    	final String rpcuser = FileConfig.getValue("rpcUse");
    	final String rpcpassword = FileConfig.getValue("rpcpassword");
    	Authenticator.setDefault(
    			new Authenticator(){
    				protected PasswordAuthentication getPasswordAuthentication(){
    					return new PasswordAuthentication(rpcuser, rpcpassword.toCharArray());}}
    			);
    	
    	return doHttpPost(param, urlString);
    }

    //传入用户名密码
    public String callJson(CoinProps coint, String type, String param) {
    	String urlString = FileConfig.getValue(coint.getStag()+type+"wallet");
    	final String rpcuser = FileConfig.getValue(coint.getStag()+type+"rpcUse");
    	final String rpcpassword = FileConfig.getValue(coint.getStag()+type+"rpcpassword");
    	Authenticator.setDefault(
    			new Authenticator(){
    				protected PasswordAuthentication getPasswordAuthentication(){
    					return new PasswordAuthentication(rpcuser, rpcpassword.toCharArray());}}
    			);
    	
    	return doHttpPost(param, urlString);
	}
    
    
    public static String doHttpPost(String xmlInfo,String URL){        
	       // log.info("发起的数据:"+xmlInfo);
	       byte[] xmlData = xmlInfo.getBytes();           
	        InputStream instr  = null;
	        ByteArrayOutputStream out = null;    
	        HttpURLConnection urlCon = null;
	        String ResponseString = "0";
	        DataOutputStream printout = null;
	         try{                         
	                URL url = new URL(URL);               
	                urlCon = (HttpURLConnection)url.openConnection();              
	                urlCon.setDoOutput(true);             
	                urlCon.setDoInput(true);              
	                urlCon.setUseCaches(false);                           
	                urlCon.setRequestProperty("Content-Type", "text/xml");      
	                urlCon.setRequestProperty("Content-length",String.valueOf(xmlData.length));
	               // log.info(String.valueOf(xmlData.length));
	                printout = new DataOutputStream(urlCon.getOutputStream());           
	                printout.write(xmlData);
	                printout.flush();             
	                printout.close();             
	                instr = urlCon.getInputStream();  
	                byte[] bis = IOUtils.toByteArray(instr);
	                ResponseString = new String(bis, "UTF-8"); 
	                if((ResponseString == null) || ("".equals(ResponseString.trim()))) {
	                    log.info("返回空");
	                }
	         }catch(ConnectException ce){
	        	 log.error("RPC无法连接了X.X.X.X.X.X" + ce.toString(), ce);
	         } catch (Exception e){   
	                log.error(e.toString(), e);
	         } finally {  
	        	   try {
	                	if(printout != null){
	                		printout.close();
	                	}
	                }catch (Exception ex) {     
	                       log.error(ex.toString(), ex);
	                } 
	        	    try {
	                	if(urlCon != null){
	                		urlCon.disconnect();
	                	}
	                }catch (Exception ex) {     
	                       log.error(ex.toString(), ex);
	                } 
	                try {
	                	if(out != null){
	                		out.close();
	                	}
	                }catch (Exception ex) {     
	                       log.error(ex.toString(), ex);
	                } 
	                try {
	                	if(instr != null){
	                		instr.close();
	                	}
	                }catch (Exception ex) {     
	                       log.error(ex.toString(), ex);
	                } 
	          }
	         return ResponseString;
	     }                    
	

}
