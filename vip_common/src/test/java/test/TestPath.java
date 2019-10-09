package test;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.world.data.big.MysqlDownTable;
import com.world.data.big.table.DownTableManager;
import com.world.util.WebUtil;

public class TestPath {

	private final static Logger log = Logger.getLogger(TestPath.class);

	@Test
	public void test1(){
//		PathUtil util = new PathUtil();
//		//String csPath = util.getWebClassesPath();
//		InputStreamReader stream = util.getConfigFileInputStreamReader("/L_cn.txt");
//		try {
//			ResourceBundle Prrb = new PropertyResourceBundle(stream);
//			log.info(Prrb.getString("关于我们"));
//			log.info(util.getBasePath());
//		} catch (IOException e) {
//			log.error(e.toString(), e);
//		}
	}
	
	@Test
	public void testUrlParam(){
		String url = "?coint=LTC&callback=jsonp1410165007208";
		log.info(WebUtil.removeParams(url, new String[]{"callback"}));
	}
	@Test
	public void testStr(){
		String sql = "select *    from user where userId  =  2";
		
		String[] strs = sql.split(" ");
		StringBuffer newSql = new StringBuffer();
		boolean beforeIsEqual = false;
		for(String stt : strs){
			log.info(stt);
			if(!stt.equals("")){
				if(!stt.equals("=")){
					if(!beforeIsEqual){
						newSql.append(" ");
					}else{
						beforeIsEqual = false;
					}
				}else{
					beforeIsEqual = true;
				}
				newSql.append(stt);
			}
		}
		
		log.info(newSql.substring(1));
		
	}
	
}
