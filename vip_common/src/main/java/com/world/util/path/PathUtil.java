package com.world.util.path;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;

public class PathUtil {

	private static Logger log = Logger.getLogger(PathUtil.class.getName());

	public String getWebClassesPath() {
		String path = getClass().getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		return path.replace("com/world/util/path/PathUtil.class", "");
	}

	public String getWebInfPath()

	{
		try {
			String path = getWebClassesPath();
			if (path.indexOf("WEB-INF") > 0)
				path = path.substring(0, path.indexOf("WEB-INF") + 8);
			else {
				return null;
			}
			return path;
		} catch (Exception ex) {
			return null;
		}
	}

	public static String FileToString(String strFileName) {

		StringBuffer buf = null;
		BufferedReader breader = null;
		try {
			breader = new BufferedReader(new InputStreamReader(
					new FileInputStream(strFileName), Charset.forName("utf-8")));

			buf = new StringBuffer();
			while (breader.ready())
				buf.append((char) breader.read());
			breader.close();
		} catch (Exception localException) {
		}

		return buf.toString();
	}

	public String getWebRoot()

	{
		try {
			String path = getWebClassesPath();
			if (path.indexOf("WEB-INF") > 0) {
				path = path.substring(0, path.indexOf("WEB-INF/"));
			} else {
				return null;
			}
			return path;
		} catch (Exception ex) {
			log.error(ex.toString(), ex);
			return null;
		}
	}

	public InputStreamReader getConfigFileInputStreamReader(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		log.info(this.getClass().getResource(path).getPath());
		try {
			return new InputStreamReader(is, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	
	public String getBasePath(){
		return this.getClass().getResource("/L_cn.txt").getPath().replace("/L_cn.txt", "/");
	}
	
	public String getJarConfigPath(String filePath){
		String p = "";
		try {
			
			URL u = this.getClass().getResource(filePath);
			if(u != null){
				p = u.getFile();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		return p;
	}
	
	public String jarFileToString(String filePath) {

		StringBuffer buf = null;
		BufferedReader breader = null;
		try {
			InputStream is = this.getClass().getResourceAsStream(filePath);
			if(is != null){
				breader = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));

				buf = new StringBuffer();
				while (breader.ready())
					buf.append((char) breader.read());
				breader.close();
				return buf.toString();
			}
			
		} catch (Exception localException) {
			log.error(localException.toString(), localException);
		}

		return null;
	}
}