package com.world.controller.m;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.Iterator;

import com.world.web.Page;
import com.world.web.Pages;

/**
 * Close By suxinjie 一期屏蔽该功能
 */
public class Index extends Pages {

//	 @Page(Viewer = "")
	public void index() {
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
		
		double total = (Runtime.getRuntime().totalMemory()) / (1024.0 * 1024);  
	    double max = (Runtime.getRuntime().maxMemory()) / (1024.0 * 1024);  
	    double free = (Runtime.getRuntime().freeMemory()) / (1024.0 * 1024);  
	    out.println("<div style=\"background:gray;text-align:left;line-height:60px;padding:30px;font-size:22px;\">Java 虚拟机试图使用的最大内存量(当前JVM的最大可用内存)maxMemory(): " + max + "MB<br/>");  
	    out.println("Java 虚拟机中的内存总量(当前JVM占用的内存总数)totalMemory(): " + total + "MB<br/>");  
	    out.println("Java 虚拟机中的空闲内存量(当前JVM空闲内存)freeMemory(): " + free + "MB<br/>");  
	    out.println("因为JVM只有在需要内存时才占用物理内存使用，所以freeMemory()的值一般情况下都很小，<br/>" +  
	    "而JVM实际可用内存并不等于freeMemory()，而应该等于 maxMemory()-totalMemory()+freeMemory()。<br/>");  
	    out.println("JVM实际可用内存: " + (max - total + free) + "MB<br/></div>");  

		out.write("<table border=\"0\" width=\"100%\" style=\"background:gray;\">");
		out.write("<tr><td colspan=\"2\" align=\"center\"><h3>Memory MXBean</h3></td></tr>");
		out.write("<tr><td width=\"200\">Heap Memory Usage</td><td>");
		out.write(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage() + "");
		out.write("</td></tr>");
		out.write("<tr>");
		out.write("<td>Non-Heap Memory Usage</td>");
		out.write("<td>"
				+ ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage()
				+ "</td>");
		out.write("</tr>");
		out.write("<tr><td colspan=\"2\"> </td></tr>");
		out.write("<tr><td colspan=\"2\" align=\"center\"><h3>Memory Pool MXBeans</h3></td></tr>");
		Iterator iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
		while (iter.hasNext()) {
			MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
			out.write("<tr><td colspan=\"2\">");
			out.write("<table border=\"0\" width=\"100%\" style=\"border: 1px #98AAB1 solid;background:gray;\">");
			out.write("<tr><td colspan=\"2\" align=\"center\"><b>" + item.getName() +"</b></td></tr>");
			out.write("<tr><td width=\"200\">Type</td><td>" + item.getType() + "</td></tr>");
			out.write("<tr><td>Usage</td><td>" + item.getUsage() +"</td></tr>");
			out.write("<tr><td>Peak Usage</td><td>" + item.getPeakUsage() +"</td></tr>");
			out.write("<tr><td>Collection Usage</td><td>" + item.getCollectionUsage() +"</td></tr>");
			out.write("</table>");
			out.write("</td></tr>");
			out.write("<tr><td colspan=\"2\"> </td></tr>");
			out.write("</table>");
		}
	    out.flush();
	    out.close();
	}
}
