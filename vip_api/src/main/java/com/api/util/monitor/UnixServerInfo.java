package com.api.util.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.StringTokenizer;
import java.util.logging.Logger;


import net.sf.json.JSONObject;

/**
 * 服务器使用信息
 * @author Administrator
 */
public class UnixServerInfo {  
		static Logger log = Logger.getLogger(UnixServerInfo.class.getName());
        /**
         * CPU使用情况
         * @return
         * @throws Exception
         */
        public double getCpuUsage() throws Exception {   
             double cpuUsed = 0;   
             Runtime rt = Runtime.getRuntime();   
             Process p = rt.exec("top -b -n 1");	// 调用系统的“top"命令   
             BufferedReader in = null;   
             try {   
                 in = new BufferedReader(new InputStreamReader(p.getInputStream()));   
                 String str = null;   
                 String[] strArray = null;   
                 while ((str = in.readLine()) != null) {   
                    int m = 0;   
                    if (str.indexOf(" R ") != -1) {	//只分析正在运行的进程，top进程本身除外 &&    
                    	strArray = str.split(" ");   
                        for (String tmp : strArray) {   
                            if (tmp.trim().length() == 0)   
                                continue;   
                            if (++m == 9) {	//第9列为CPU的使用百分比(RedHat    
                                 cpuUsed += Double.parseDouble(tmp);   
                             }   
                         }   
                     }   
                 }   
             } catch (Exception e) {   
                 e.printStackTrace();   
             } finally {   
                 in.close();   
             }   
            return cpuUsed;   
         }   
      
        /**
         * 内存使用情况
         * @return
         * @throws Exception
         */
        public double getMemUsage() throws Exception {   
             double menUsed = 0;   
             Runtime rt = Runtime.getRuntime();   
             Process p = rt.exec("top -b -n 1");// 调用系统的“top"命令   
             BufferedReader in = null;   
             try {   
                 in = new BufferedReader(new InputStreamReader(p.getInputStream()));   
                 String str = null;   
                 String[] strArray = null;   
                 while ((str = in.readLine()) != null) {   
                    int m = 0;   
                    if (str.indexOf(" R ") != -1) {//只分析正在运行的进程，top进程本身除外 && 
                         strArray = str.split(" ");   
                         for (String tmp : strArray) {   
                            if (tmp.trim().length() == 0)   
                                continue;   
                            if (++m == 10) {   
                            	// 9)--第10列为mem的使用百分比(RedHat 9)   
                                 menUsed += Double.parseDouble(tmp);   
                             }   
                         }   
                     }   
                 }   
             } catch (Exception e) {   
                 e.printStackTrace();   
             } finally {   
                 in.close();   
             }   
            return menUsed;   
         }   
      
        /**
         * 硬盘使用情况
         * @return
         * @throws Exception
         */
        public double getDiskUsage() throws Exception {   
             double totalHD = 0;   
             double usedHD = 0;   
             Runtime rt = Runtime.getRuntime();   
             Process p = rt.exec("df -hl");//df -hl 查看硬盘空间   
             BufferedReader in = null;   
             try {   
                 in = new BufferedReader(new InputStreamReader(p.getInputStream()));   
                 String str = null;   
                 String[] strArray = null;   
                 while ((str = in.readLine()) != null) {   
                    int m = 0;   
                         strArray = str.split(" ");   
                         for (String tmp : strArray) {   
                            if (tmp.trim().length() == 0)   
                                continue;   
                             ++m;   
                            if (tmp.indexOf("G") != -1) {   
                                if (m == 2) {   
                                    if (!tmp.equals("") && !tmp.equals("0"))   
                                         totalHD += Double.parseDouble(tmp   
                                                 .substring(0, tmp.length() - 1)) * 1024;   
                                 }   
                                if (m == 3) {   
                                    if (!tmp.equals("none") && !tmp.equals("0"))   
                                         usedHD += Double.parseDouble(tmp.substring(   
                                                0, tmp.length() - 1)) * 1024;   
                                 }   
                             }   
                            if (tmp.indexOf("M") != -1) {   
                                if (m == 2) {   
                                    if (!tmp.equals("") && !tmp.equals("0"))   
                                         totalHD += Double.parseDouble(tmp   
                                                 .substring(0, tmp.length() - 1));   
                                 }   
                                if (m == 3) {   
                                    if (!tmp.equals("none") && !tmp.equals("0"))   
                                         usedHD += Double.parseDouble(tmp.substring(   
                                                0, tmp.length() - 1));   
                                 }   
                             }   
                               
                         }   
                 }   
             } catch (Exception e) {   
                 e.printStackTrace();   
             } finally {   
                 in.close();   
             }   
            return (usedHD / totalHD) * 100;   
         }   
        
        /**
         * get memory by used info
         *
         * @return int[] result 
         * result.length == 4;int[0]=MemTotal;int[1]=MemFree;int[2]=SwapTotal;int[3]=SwapFree;
         * @throws IOException
         * @throws InterruptedException
         */
        public int[] getMemInfo() throws IOException, InterruptedException {
           File file = new File("/proc/meminfo");
           BufferedReader br = new BufferedReader(new InputStreamReader(
           new FileInputStream(file)));
           int[] result = new int[4];
           String str = null;
           StringTokenizer token = null;
           while((str = br.readLine()) != null){
              token = new StringTokenizer(str);
              if(!token.hasMoreTokens())
                 continue;
        
              str = token.nextToken();
              if(!token.hasMoreTokens())
                 continue;
        
              if(str.equalsIgnoreCase("MemTotal:"))
                 result[0] = Integer.parseInt(token.nextToken());
              else if(str.equalsIgnoreCase("MemFree:"))
                 result[1] = Integer.parseInt(token.nextToken());
              else if(str.equalsIgnoreCase("SwapTotal:"))
                 result[2] = Integer.parseInt(token.nextToken());
              else if(str.equalsIgnoreCase("SwapFree:"))
                 result[3] = Integer.parseInt(token.nextToken());
           }
        
           return result;
        }
        
        public static String getSaveInfo(String serverType){
        	try{
	        	 UnixServerInfo usi = new UnixServerInfo(); 
	        	// int[] memInfo = usi.getMemInfo();
	        	 Runtime lRuntime = Runtime.getRuntime();   
	        	 SysInfo sysInfo = new SysInfo();
//	        	 sysInfo.setCpuUsed(usi.getCpuUsage());
//	        	 sysInfo.setMemUsed(usi.getMemUsage());
//	        	 sysInfo.setDiskUsed(usi.getDiskUsage());
//	        	 sysInfo.setMemFree(memInfo[1]);
//	        	 sysInfo.setMemTotal(memInfo[0]);
	        	 sysInfo.setJvm_memFree(lRuntime.freeMemory());
	        	 sysInfo.setJvm_memMax(lRuntime.maxMemory());
	        	 sysInfo.setJvm_memTotal(lRuntime.totalMemory());
	        	 sysInfo.setProcessors(lRuntime.availableProcessors());
	        	 sysInfo.setCpuUsed(123.23);
	        	 sysInfo.setMemUsed(123.23);
	        	 sysInfo.setDiskUsed(123.23);
	        	 sysInfo.setMemFree(123.23);
	        	 sysInfo.setMemTotal(456.46);
	        	 sysInfo.setAddTime(System.currentTimeMillis());
	        	 sysInfo.setServerType(serverType);
	        	 
	        	 return JSONObject.fromObject(sysInfo).toString();
        	}catch(Exception ex){
        		ex.printStackTrace();
        	}
        	return null;
        }
        
//        public static void main(String[] args) throws Exception {   
//            UnixServiceInfo cpu = new UnixServiceInfo();   
//            System.out.println("---------------cpu used:" + cpu.getCpuUsage() + "%");   
//            System.out.println("---------------mem used:" + cpu.getMemUsage() + "%");   
//            System.out.println("---------------HD used:" + cpu.getDeskUsage() + "%");   
//            System.out.println("------------jvm----------------------");   
//            Runtime lRuntime = Runtime.getRuntime();   
//            System.out.println("--------------Free Momery:" + lRuntime.freeMemory()+"K");   
//            System.out.println("--------------Max Momery:" + lRuntime.maxMemory()+"K");   
//            System.out.println("--------------Total Momery:" + lRuntime.totalMemory()+"K");   
//            System.out.println("---------------Available Processors :"  
//                    + lRuntime.availableProcessors());   
//            
//            System.out.println("------------system mem----------------------"); 
//            int[] memInfo = cpu.getMemInfo();
//	  	    System.out.println("MemTotal:" + memInfo[0]+"K");
//	  	    System.out.println("MemFree:" + memInfo[1]+"K");
//        }

}
