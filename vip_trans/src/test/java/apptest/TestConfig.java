package apptest;

import java.util.ResourceBundle;

import org.apache.log4j.Logger;

public class TestConfig {
	private static Logger log = Logger.getLogger(TestConfig.class);
	
	private static Object lock              = new Object();
	private static TestConfig config     = null;
	private static ResourceBundle rb        = null;
	private static final String CONFIG_FILE = "test";
	
	private TestConfig() {
		rb = ResourceBundle.getBundle(CONFIG_FILE);
	}
	
	public static TestConfig getInstance() {
		log.info("初始化单例对象TestConfig");
		synchronized(lock) {
			if(null == config) {
				config = new TestConfig();
			}
		}
		return (config);
	}
	
	public static String getValue(String key) {
		if(rb == null){
			getInstance();
		}
		try {
			if(rb != null){
				return (rb.getString(key));
			}else{
				return null;
			}
		} catch (Exception e) {
			log.error(e.toString() + ",key:" + key, e);
		}
		return null;
	}
	
}