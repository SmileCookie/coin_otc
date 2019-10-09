package com.world.data.mysql.pool;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

public class CommonBasicDataSource extends BasicDataSource{

	private final static Logger log = Logger.getLogger(CommonBasicDataSource.class);

	@Override
    public synchronized void close() throws SQLException {
		log.info("URL:" + url);
		Driver driver = DriverManager.getDriver(url);
		if(driver != null){
			DriverManager.deregisterDriver(driver);
		}
        super.close();
    }
	
}
