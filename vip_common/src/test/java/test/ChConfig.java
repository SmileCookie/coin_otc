package test;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.world.config.GlobalConfig;
import com.world.config.InitConfig;
import com.world.util.path.PathUtil;

public class ChConfig {

	private final static Logger log = Logger.getLogger(ChConfig.class);

	@Test
	public void test1(){
		//InitConfig.InitAllConfig();
			log.info(GlobalConfig.baseDomain);
			
	}
}
