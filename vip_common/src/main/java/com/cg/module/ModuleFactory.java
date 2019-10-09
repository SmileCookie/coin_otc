package com.cg.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.cg.jsp.MainTest;
import org.apache.log4j.Logger;

/****
 * 
 * @author Administrator
 *
 */
public class ModuleFactory {

	private final static Logger log = Logger.getLogger(ModuleFactory.class.getName());

	/****
	 * 获取不通jsp模版的jsp代码 
	 * @param type 
	 * @return
	 */
	public static String getModule(ModuleType type){
		if(type != null){
			return getModuleByPath(type.getPath());
		}
		return null;
	}
	
	//aoru.jsp
	public static String getModuleByPath(String path){
		String currentPath = MainTest.class.getResource("").getPath();
		String jspFoldPath = currentPath.substring(0 , currentPath.indexOf("/WEB-INF"));
		String aoruPath = jspFoldPath + "/" + "cg/" + path; 
		File aoruFile = new File(aoruPath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(aoruFile);
			byte[] bs = new byte[(int)aoruFile.length()];
			int r = 0;
			while(r > -1){
				r = fis.read(bs);
			}
			
			log.info(new String(bs));
			return new String(bs);
		} catch (FileNotFoundException e) {
			log.error(e.toString(), e);
		} catch (IOException e) {
			log.error(e.toString(), e);
		}finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
		}
		return null;
	}
}
