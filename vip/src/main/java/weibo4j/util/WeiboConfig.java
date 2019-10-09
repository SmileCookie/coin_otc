package weibo4j.util;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class WeiboConfig {
	private final static Logger log = Logger.getLogger(WeiboConfig.class);

	public WeiboConfig(){}
	private static Properties props = new Properties(); 
	static{
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
		} catch (FileNotFoundException e) {
			log.error(e.toString(), e);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}
	}
	public static String getValue(String key){
		return props.getProperty(key);
	}

    public static void updateProperties(String key,String value) {    
            props.setProperty(key, value); 
    } 
}
