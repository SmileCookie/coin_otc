package com.world.util.jpush;

import java.util.Properties;

/**
 * Constants
 * 
 * @author Lynch 2014-09-15
 *
 */
public interface Constants {
	
	Properties properties = PropertiesUtils.getProperties("jpush.properties");

	String appKey =properties.getProperty("appKey");

	String masterSecret = properties.getProperty("masterSecret");

	String testAppKey =properties.getProperty("testAppKey");

	String testMasterSecret = properties.getProperty("testMasterSecret");

	String prefix = ModelPrefix.getPrefix(properties.getProperty("modeKey"));

	String loginMsg = properties.getProperty("loginMsg");

	String modeKey = properties.getProperty("modeKey");

}
