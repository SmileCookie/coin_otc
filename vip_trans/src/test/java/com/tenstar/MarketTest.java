package com.tenstar;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.junit.Test;

public class MarketTest {

	private final static Logger log = Logger.getLogger(MarketTest.class);

	@Test
	public void testJson() {
		//用fastjson解析数组对象
		String json = "[[1488002700,0.045235,0.045235,0.04157,0.04157,280.0],[1488012960,0.05,0.05,0.05,0.05,200.0],\n" +
				"[1488013680,0.06,0.06,0.06,0.06,100.0],\n" +
				"[1488157920,0.06,0.06,0.06,0.06,100.0],\n" +
				"[1488174840,0.06,0.06,0.05,0.05,12.0],\n" +
				"[1488185820,0.06,0.06,0.06,0.06,2.0],\n" +
				"[1488245700,0.06,0.06,0.06,0.06,200.0],\n" +
				"[1488508140,0.05,0.05,0.05,0.05,1.0],\n" +
				"[1488508800,0.06,0.06,0.06,0.06,10.0],[1488513660,0.05,0.05825,0.05,0.052454,146.697]]";


		JSONArray jsonArray = JSON.parseArray(json);
		for(int i=0 ;i<jsonArray.size(); i++) {
			JSONArray j2 = (JSONArray) jsonArray.get(i);

			for(int j=0; j<j2.size(); j++) {
				log.info(j2.get(0));
			}
		}

	}

	
	/*@Test
	public void times(){
		
		Timestamp start = Timestamp.valueOf("2016-08-15 12:00:00");
		long curr = System.currentTimeMillis();
		log.info(start.getTime());
		log.info(curr);
		log.info(curr<start.getTime());
	}*/
	
	@Test
	public void num() throws UnknownHostException{
		InetAddress inet = InetAddress.getLocalHost();
		  log.info("本机的ip=" + inet.getHostAddress() + " port:" + System.getProperty("java.class.path"));
	}
	
	public static BigDecimal dealMegerBuyPrice(String depth,BigDecimal priceKey){
		
				if(depth.equals("0.1")){
					priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.1))[1]);
				}
				else if(depth.equals("0.3")){
					priceKey = priceKey.subtract(priceKey.divideAndRemainder(BigDecimal.valueOf(0.3))[1]);
				}/*else if(depth.equals("0.5")){
					priceKey = priceKey - (priceKey % 50);
				}else if(depth.equals("1")){
					priceKey = priceKey - (priceKey % 100);
				}else if(depth.equals("0.000001")){
					priceKey = priceKey - (priceKey % 100);
				}else if(depth.equals("0.0001")){
					priceKey = priceKey - (priceKey % 10000);
				}else if(depth.equals("0.0003")){
					priceKey = priceKey - (priceKey % 30000);
				}else if(depth.equals("0.0005")){
					priceKey = priceKey - (priceKey % 50000);
				}else if(depth.equals("0.001")){
					priceKey = priceKey - (priceKey % 100000);
				}else if(depth.equals("0.002")){
					priceKey = priceKey - (priceKey % 200000);
				}else if(depth.equals("0.003")){
					priceKey = priceKey - (priceKey % 300000);
				}
				*/
				return priceKey;
			}
	
	 public static void main(String[] args) {
	      // creating maps 
	      TreeMap<BigDecimal, String> treemap = new TreeMap<BigDecimal, String>(new Comparator<BigDecimal>() {
				@Override
				public int compare(BigDecimal o1, BigDecimal o2) {
					return o2.compareTo(o1);
				}
			});
	      SortedMap<BigDecimal, String> treemaphead = new TreeMap<BigDecimal, String>();
	      
	      // populating tree map
	      treemap.put(BigDecimal.valueOf(320.02), "two");
	      treemap.put(BigDecimal.valueOf(300.00), "one");
	      treemap.put(BigDecimal.valueOf(360.02), "three");
	      treemap.put(BigDecimal.valueOf(420.02), "six");
	      treemap.put(BigDecimal.valueOf(370.02), "five");
	      
	      // getting head map
	      treemaphead=treemap.headMap(BigDecimal.valueOf(320.02));
	      
	      log.info("Checking values of the sorted map");
	      log.info("Value is: " + treemaphead);
	   }    
	
}
