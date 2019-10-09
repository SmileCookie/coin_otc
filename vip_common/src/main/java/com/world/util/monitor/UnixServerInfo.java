package com.world.util.monitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

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
                 log.error(e.toString(), e);
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
                 log.error(e.toString(), e);
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
                 log.error(e.toString(), e);
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
        
        public int[] getNetWorkInfo(String ethName) throws Exception, InterruptedException {
        	Process process = null;  
            List<String> processList = new ArrayList<String>();  
            try {  
                process = Runtime.getRuntime().exec("cat /proc/net/dev");  
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));  
                String line = "";  
                while ((line = input.readLine()) != null) {  
                    processList.add(line);  
                }  
                input.close();  
            } catch (IOException e) {  
                log.error(e.toString(), e);
            }  
            int[] result =  new int[2];
            if(processList.size() > 0){
            	if(StringUtils.isEmpty(ethName))
            		ethName = "eth0";
            	String eth = "";
            	for (String string : processList) {
    				if(string.indexOf(ethName) > 0){
    					eth = string;
    				}
    			}
            	StringTokenizer token = new StringTokenizer(eth);
            	int i = 0;
                while(token.hasMoreTokens()){
                	String str = token.nextToken();
                	if(i == 1){
                		result[0] = Integer.parseInt(str);
                	}
                	if(i == 9){
                		result[1] = Integer.parseInt(str);
                	}
                	i++;
                }
            }
            return result;
        }
        
        public static double testRandom(int i){
    		Random r = new Random();
    		if(i == 2){
    			double result1 = r.nextDouble() * 100;
        		double result2 = r.nextDouble() * 10;
        		return result1 - result2;
    		}else{
    			double result1 = r.nextDouble() * 100000000;
        		double result2 = r.nextDouble() * 10000000;
        		return result1 - result2;
    		}
    	}
        
        public static String getSysInfo(String serverType) throws Exception{
        	 UnixServerInfo usi = new UnixServerInfo(); 
        	 int[] memInfo = usi.getMemInfo();
//        	 int[] ethInfo = usi.getNetWorkInfo(ethName);
        	 Runtime lRuntime = Runtime.getRuntime();   
        	 SysInfo sysInfo = new SysInfo();
        	 sysInfo.setCpuUsed(usi.getCpuUsage());
        	 sysInfo.setMemUsed(usi.getMemUsage());
        	 sysInfo.setDiskUsed(usi.getDiskUsage());
        	 sysInfo.setMemFree(memInfo[1]);
        	 sysInfo.setMemTotal(memInfo[0]);
//        	 sysInfo.setEth_receive(ethInfo[0]);
//        	 sysInfo.setEth_transmit(ethInfo[1]);
        	 sysInfo.setJvm_memFree(lRuntime.freeMemory());
        	 sysInfo.setJvm_memMax(lRuntime.maxMemory());
        	 sysInfo.setJvm_memTotal(lRuntime.totalMemory());
        	 sysInfo.setProcessors(lRuntime.availableProcessors());
//        	 sysInfo.setCpuUsed(testRandom(2));
//        	 sysInfo.setMemUsed(testRandom(2));
//        	 sysInfo.setDiskUsed(testRandom(2));
//        	 sysInfo.setMemFree(testRandom(2));
//        	 sysInfo.setMemTotal(testRandom(2));
//        	 sysInfo.setEth_receive(testRandom(8));
//        	 sysInfo.setEth_transmit(testRandom(8));
        	 sysInfo.setAddTime(System.currentTimeMillis());
        	 sysInfo.setServerType(serverType);
        	 
        	 return JSONObject.fromObject(sysInfo).toString();
        }
        
//        public static void main(String[] args) throws Exception {   
//            UnixServiceInfo cpu = new UnixServiceInfo();   
//            log.info("---------------cpu used:" + cpu.getCpuUsage() + "%");
//            log.info("---------------mem used:" + cpu.getMemUsage() + "%");
//            log.info("---------------HD used:" + cpu.getDeskUsage() + "%");
//            log.info("------------jvm----------------------");
//            Runtime lRuntime = Runtime.getRuntime();   
//            log.info("--------------Free Momery:" + lRuntime.freeMemory()+"K");
//            log.info("--------------Max Momery:" + lRuntime.maxMemory()+"K");
//            log.info("--------------Total Momery:" + lRuntime.totalMemory()+"K");
//            log.info("---------------Available Processors :"
//                    + lRuntime.availableProcessors());   
//            
//            log.info("------------system mem----------------------");
//            int[] memInfo = cpu.getMemInfo();
//	  	    log.info("MemTotal:" + memInfo[0]+"K");
//	  	    log.info("MemFree:" + memInfo[1]+"K");
//        }

}
