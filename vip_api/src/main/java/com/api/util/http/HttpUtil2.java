package com.api.util.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.api.util.MapSort;

import net.sf.json.JSONObject;

/**
 * 长连接的连接池机制
 * @author pc
 *
 */
public class HttpUtil2 {

	  private static String encoding = "UTF-8"; 
	  
		/***
		 * 解析置顶结果为json
		 * @param url
		 * @return
		 * @throws IOException 
		 */
		public static JSONObject getJson(boolean isPost, String url , Map<String , String> params, int connectTimeout,int readTimeout) throws IOException{
			String query = buildQuery(params, encoding);
			String res = DoRequest(isPost, url, query);
			JSONObject js = null;
			try {
				js = JSONObject.fromObject(res);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return js;
		}

		public static String buildQuery(Map<String, String> params, String charset) throws IOException {
			if (params == null || params.isEmpty()) {
				return null;
			}
			
			//按map键首字母顺序进行排序
			params = MapSort.sortMapByKey(params);
			
			StringBuilder query = new StringBuilder();
			Set<Entry<String, String>> entries = params.entrySet();
			boolean hasParam = false;

			for (Entry<String, String> entry : entries) {
				String name = entry.getKey();
				String value = entry.getValue();
				if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(value)) {
					if (hasParam) {
						query.append("&");
					} else {
						hasParam = true;
					}

					query.append(name).append("=").append(URLEncoder.encode(value, charset));
				}
			}

			return query.toString();
		}
	  
	  /**
	   * 请求一个结果
	   * @param isPost 是否使用post
	   * @param ip 地址
	   * @param port 端口
	   * @param uri 访问名
	   * @param pramas 参数，如果是post的话
	   * @return 服务器端返回的结果
	   */
	  public static String DoRequest(boolean isPost,String uri,Object pramas){
		  String param = HttpUtil2.ObjectToString(pramas);
		  return  DoRequest(isPost,uri,param);
	  }
	 
	  /**
	   * 请求一个结果
	   * @param isPost 是否使用post
	   * @param ip 地址
	   * @param port 端口
	   * @param uri 访问名
	   * @param pramas 参数，如果是post的话
	   * @return 服务器端返回的结果
	   */
	  public static String DoRequest( boolean isPost,String uri,String pramas){
				 try {  
					 //连接从线程池中获取
			            //Socket s =ConnectionPool.getConnection();// new Socket(ip, port);
					 	Socket s = new Socket("127.0.0.1", 80);
			            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());  
			            StringBuffer sb = new StringBuffer();  
			            if(isPost)
			            {
			                 sb.append("POST "+uri+" HTTP/1.1\r\n"); 
			                 sb.append("Content-Length:"+pramas.length()+"\r\n");  
			            }
			            else
			            	 sb.append("GET "+uri+" HTTP/1.1\r\n");  
			            sb.append("\r\n");  
			       
			            //注，这是关键的关键，忘了这里让我搞了半个小时。这里一定要一个回车换行，表示消息头完，不然服务器会等待  
			            if(isPost)
			            	  sb.append(pramas);  
			            
			            osw.write(sb.toString());  
			            osw.flush();  
			  
			            //--输出服务器传回的消息的头信息  
			            InputStream is = s.getInputStream();  
			            String line = null;  
			            int contentLength = 0;//服务器发送回来的消息长度  
			            // 读取所有服务器发送过来的请求参数头部信息  
			            do {  
			                line = readLine(is, 0);  
			                //如果有Content-Length消息头时取出  
			                if (line.startsWith("Content-Length")) {  
			                    contentLength = Integer.parseInt(line.split(":")[1].trim());  
			                }  
			                //打印请求部信息  
			              //  System.out.print(line);  
			                //如果遇到了一个单独的回车换行，则表示请求头结束  
			            } while (!line.equals("\r\n"));  
			  
			            //--输消息的体  
			           // System.out.print("主要消息体："+readLine(is, contentLength));  
			            String rtn=readLine(is, contentLength);
			            //关闭流  
			            is.close();  
			            osw.close();
			            //ConnectionPool.releaseConnection(s);
			            s.close();
			            return rtn;
			        } catch (UnknownHostException e) {  
			            e.printStackTrace();  
			            return null;
			        } catch (IOException e) {  
			            e.printStackTrace();   
			            return null;
			        }  
	  }
	  /**
	   * 请求一个结果
	   * @param isPost 是否使用post
	   * @param ip 地址
	   * @param port 端口
	   * @param uri 访问名
	   * @param pramas 参数，如果是post的话
	   * @return 服务器端返回的结果
	   */
	  public static String DoRequest2(boolean isPost,String ip,int port,String uri,String pramas){
				 try {  
			            Socket s = new Socket(ip, port);  
			            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());  
			            StringBuffer sb = new StringBuffer();  
			            if(isPost)
			            {
			                 sb.append("POST "+uri+" HTTP/1.1\r\n"); 
			                 sb.append("Content-Length:"+pramas.length()+"\r\n");  
			            }
			            else
			            	 sb.append("GET "+uri+" HTTP/1.1\r\n");  
			            sb.append("\r\n");  
			       
			            //注，这是关键的关键，忘了这里让我搞了半个小时。这里一定要一个回车换行，表示消息头完，不然服务器会等待  
			            if(isPost)
			            	  sb.append(pramas);  
			            
			            osw.write(sb.toString());  
			            osw.flush();  
			  
			            //--输出服务器传回的消息的头信息  
			            InputStream is = s.getInputStream();  
			            String line = null;  
			            int contentLength = 0;//服务器发送回来的消息长度  
			            // 读取所有服务器发送过来的请求参数头部信息  
			            do {  
			                line = readLine(is, 0);  
			                //如果有Content-Length消息头时取出  
			                if (line.startsWith("Content-Length")) {  
			                    contentLength = Integer.parseInt(line.split(":")[1].trim());  
			                }  
			                //打印请求部信息  
			              //  System.out.print(line);  
			                //如果遇到了一个单独的回车换行，则表示请求头结束  
			            } while (!line.equals("\r\n"));  
			  
			            //--输消息的体  
			           // System.out.print("主要消息体："+readLine(is, contentLength));  
			            String rtn=readLine(is, contentLength);
			            //关闭流  
			            is.close();  
			            osw.close();
			            s.close();
			            return rtn;
			        } catch (UnknownHostException e) {  
			            e.printStackTrace();  
			            return null;
			        } catch (IOException e) {  
			            e.printStackTrace();   
			            return null;
			        }  
	  }
	  
	  /**
	   * 把对象变成一个字符串返回
	   * @param obj
	   * @return
	   */
	  public static String ObjectToString(Object obj){
		  try{
		  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
	        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);  
	      objectOutputStream.writeObject(obj);   
	        String serStr = byteArrayOutputStream.toString("ISO-8859-1");  
	        serStr = java.net.URLEncoder.encode(serStr, "UTF-8"); 
	        objectOutputStream.close();  
	        byteArrayOutputStream.close();  
	        System.out.println(serStr);
	        return serStr;
		  }catch(Exception ex){
			  ex.printStackTrace();
			  return null;
		  }  
	  }
	  /**
	   * 把一个字符串还原成对象
	   * @param obj
	   * @return
	   */
	  public static Object StringToObject(String str){
		  try{
			  String redStr = java.net.URLDecoder.decode(str, "UTF-8");  
		        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));  
		        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);   
		        Object obj=objectInputStream.readObject();   
		        objectInputStream.close();  
		        byteArrayInputStream.close();  
		        return obj;
		  }catch(Exception ex){
			  ex.printStackTrace();
			  return null;
		  }  
	  }
	  
	  private static String readLine(InputStream is, int contentLe) throws IOException {  
	        ArrayList lineByteList = new ArrayList();  
	        byte readByte;  
	        int total = 0;  
	        if (contentLe != 0) {  
	            do {  
	                readByte = (byte) is.read();  
	                lineByteList.add(Byte.valueOf(readByte));  
	                total++;  
	            } while (total < contentLe);//消息体读还未读完  
	        } else {  
	            do {  
	                readByte = (byte) is.read();  
	                lineByteList.add(Byte.valueOf(readByte));  
	            } while (readByte != 10);  
	        }  
	  
	        byte[] tmpByteArr = new byte[lineByteList.size()];  
	        for (int i = 0; i < lineByteList.size(); i++) {  
	            tmpByteArr[i] = ((Byte) lineByteList.get(i)).byteValue();  
	        }  
	        lineByteList.clear();  
	  
	        return new String(tmpByteArr, encoding);  
	    }  
}
