package com.world.model.entity.pay;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.world.data.mongo.ids;
import org.apache.log4j.Logger;


public class OrderNumberGeneration {
	private final static Logger log = Logger.getLogger(OrderNumberGeneration.class);

	/**初始化订单getkey**/

	
	public static long getNewNumber(String key){
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
			//log.info(sdf.format(new Date()));
			return Long.valueOf(sdf.format(new Date())+""+ids.getId(key));
		} catch (NumberFormatException e) {
			log.error(e.toString(), e);
			return 0;
		}
	}
	
	/**test**/
	public static void main(String[] args) {
		
		log.info(getNewNumber("order"));
	}
}
