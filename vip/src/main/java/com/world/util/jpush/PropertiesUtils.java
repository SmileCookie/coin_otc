package com.world.util.jpush;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesUtils
 * 
 * @author apple
 *
 */
public class PropertiesUtils {

	private final static Logger log = Logger.getLogger(PropertiesUtils.class);

	public static Properties getProperties(String propertyFileName) {

		Properties p = new Properties();

		try {
			InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(propertyFileName);

			p.load(inputStream);

		} catch (IOException e1) {
			log.error(e1.toString(), e1);
		}

		try {
			for(String key : p.stringPropertyNames()){
                log.info("读取配置文件" + propertyFileName + ": " + key + "=" + p.getProperty(key));
            }
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return p;
	}

}
