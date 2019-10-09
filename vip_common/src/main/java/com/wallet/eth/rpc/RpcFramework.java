/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.wallet.eth.rpc;

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
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

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
    	/**
    	 * 0x325e8fb67018e4f314be20d858b2a7ef2a4cb589
    	 * 0x42b98d50c200a6751766faea53133c6807f02d7c
    	 * 0x6a7b1c056ef41a9c4c7a6b1aa35fd893b136eb81
    	 */
    	//String template = RpcTemplates.getEth_getBalance("0x325e8fb67018e4f314be20d858b2a7ef2a4cb589");
    	//String template = RpcTemplates.getEth_sendTransaction("0x42b98d50c200a6751766faea53133c6807f02d7c", new BigDecimal("0.12"));
    	//String template = RpcTemplates.getEth_sendTransaction("0x325e8fb67018e4f314be20d858b2a7ef2a4cb589", "0x42b98d50c200a6751766faea53133c6807f02d7c", new BigDecimal("1.9"));
    	//String template = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_newPendingTransactionFilter\",\"params\":[],\"id\":73}";
    	//String template =  "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getFilterChanges\",\"params\":[\"0xf00ab5b7d9882bccd07e41eccd782a6b\"],\"id\":73}";
    	
    	/**
    	 * 0x2ed3827b4ff89a5a1b2889e76b3780db30cf9a866c375e5f8fdf6025328a3eaf
    	 * 0xdbec80ccef1f06afafbcac801e275a313ffd767d9d5985ecac88586d506d9b8e
    	 * 0x48da409734690ad750943e1f4f93aa924f676b22be1ea4b9d15e3566cacd95de
    	 * 0x7f5f0ae6b5024fbd5bd1ab9ae61595f9c3a56d3f08f1287280f21603b530aa2b
    	 * 0x1d870a07e21ee714d9982e2f28fd7c8ff3f4a0d8faa59b10d00cbc501339e201
    	 * 0x72d9d608545e95e8d3baf5dd776230667121b55b90f7d5965660e62f650d2f59
    	 * 0xc9be25bea7b9208190ce3a3b13b893808f352220655f8ba53f272462bfad784a
    	 */
    	//String template = RpcTemplates.getEth_getTransactionReceipt("0xc9be25bea7b9208190ce3a3b13b893808f352220655f8ba53f272462bfad784a");
    	//log.info(template);
    	//String result = callJson("http://192.168.2.70:8080" , template);
        //log.info(result);
	}
    

    
    public static String callRpcJson(String urlString , final String rpcUser, final String rpcPassword,  String params) {
    	Authenticator.setDefault(
    			new Authenticator(){
    				protected PasswordAuthentication getPasswordAuthentication(){
    					return new PasswordAuthentication(rpcUser, rpcPassword.toCharArray());}}
    			);
    	//log.info("urlString:" + urlString);
    	return doHttpPost(params, urlString);
	}
    
    /***
     * 404 网络异常
     * @param xmlInfo
     * @param URL
     * @return
     */
	public static String doHttpPost(String xmlInfo, String URL) {
		// log.info("发起的数据:"+xmlInfo);
		byte[] xmlData = xmlInfo.getBytes();
		InputStream instr = null;
		ByteArrayOutputStream out = null;
		HttpURLConnection urlCon = null;
		String ResponseString = "0";
		DataOutputStream printout = null;
		try {
			URL url = new URL(URL);
			urlCon = (HttpURLConnection) url.openConnection();
			urlCon.setDoOutput(true);
			urlCon.setDoInput(true);
			urlCon.setUseCaches(false);
			urlCon.setRequestProperty("Content-Type", "text/xml");
			urlCon.setRequestProperty("Content-length", String.valueOf(xmlData.length));
			urlCon.setReadTimeout(5000);
			urlCon.setConnectTimeout(5000);
			// log.info(String.valueOf(xmlData.length));
			printout = new DataOutputStream(urlCon.getOutputStream());
			printout.write(xmlData);
			printout.flush();
			printout.close();
			instr = urlCon.getInputStream();
			byte[] bis = IOUtils.toByteArray(instr);
			ResponseString = new String(bis, "UTF-8");
			if ((ResponseString == null) || ("".equals(ResponseString.trim()))) {
				log.info("返回空");
			}
		} catch (ConnectException ce) {
			log.error(URL+"RPC无法连接了X.X.X.X.X.X" + ce.toString(), ce);
			///throw new RuntimeException("rpc errors!");
			return "404";
		} catch (SocketTimeoutException se){
			log.error(URL+"RPC无法连接了X.X.X.X.X.X" + se.toString(), se);
			//throw new RuntimeException("rpc errors!");
			return "404";
		}catch (Exception e) {
			log.error(e.toString(), e);
			//throw new RuntimeException("rpc errors!");
			return "404";
		} finally {
			try {
				if (printout != null) {
					printout.close();
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
			try {
				if (urlCon != null) {
					urlCon.disconnect();
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
			try {
				if (instr != null) {
					instr.close();
				}
			} catch (Exception ex) {
				log.error(ex.toString(), ex);
			}
		}
		return ResponseString;
	}                
	

}
