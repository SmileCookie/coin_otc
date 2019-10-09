package com.file;

import org.apache.commons.lang.StringUtils;

import com.file.config.FileConfig;
import com.file.server.ServerFactory;
import com.file.server.TomcatServer;
import com.world.util.string.StringUtil;
import org.apache.log4j.Logger;

/*****
 * 
 * @author Administrator
 *
 */
public class PathUtil {

	private final static Logger log = Logger.getLogger(PathUtil.class.getName());

	/****
	 * 获取list的显示图片
	 * @param fileName
	 * @param size
	 * @return
	 */
	public static String getListPic(String pics , String size){
		String[] paths = pics.split(" ");
		if(paths.length > 0){
			return getPathByFileName(paths[0] , size);
		}
		return null;
	}
	/*******
	 * 获取要显示的图片
	 * @param pics  图片路径
	 * @param size  尺寸  88x88
	 * @param index 要显示的是第几个图片
	 * @return
	 */
	public static String getShowPic(String pics , String size , int index){
		String[] picArrs = getPics(pics , size);
		if(picArrs.length >= index){
			return picArrs[index];
		}
		return null;
	}
	
	
	/****
	 * 获取一组图片
	 * @param pics
	 * @param size
	 * @return
	 */
	public static String[] getPics(String pics , String size){
		String[] paths = pics.split(" ");
		try {
			if(paths.length > 0){
				for(int i = 0 ; i < paths.length ; i++){
					paths[i] = getPathByFileName(paths[i] , size);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return paths;
	}
	//空格隔开
	public static String getAllPics(String pics , String size){
		return StringUtils.join(getPics(pics , size), " ");
	}
	
	/*****
	 * 获取文件扩展名 返回值是否含点号
	 * @param fileName 
	 * @param hasPoint 
	 * @return
	 */
	public static String getExtension(String fileName,boolean hasPoint){
		if(fileName.contains(".")){
			int li=hasPoint?fileName.lastIndexOf("."):(fileName.lastIndexOf(".")+1);
			return fileName.substring(li);
		} else {
			return null;
		}
	}
	
	public static boolean isPic(String ext){
		return FileInfo.upExt.indexOf(ext.toLowerCase()) >= 0;
	}

	/****
	 * 根据文件名称获取访问路径
	 * @param fileName
	 * @return
	 */
	public static String getPathByFileName(String fileName){
		String src = getFileInfo(fileName);
		if(src != null){
			String[] srcss = src.split(FileInfo.split);
			if(srcss.length == 3){
				String serverId = srcss[0];
				TomcatServer ts = ServerFactory.getServerById(Integer.parseInt(serverId));
				
				String ext = getExtension(fileName, true);
			    boolean isPic = isPic(ext);
				if(ts != null){
					if(isPic){
						return FileConfig.getValue("imgDomain1")+"/up/"+ srcss[2]+"/"+fileName;
					}else{
						return FileConfig.getValue("imgDomain1")+"/file/"+ srcss[2]+"/"+fileName;
					}
				}
			}
		}
		return null;
	}
	
	public static String getPathByFileName(String fileName , String size){
		String src = getFileInfo(fileName);
		if(src != null){
			String[] srcss = src.split(FileInfo.split);
			if(srcss.length == 3){
				String serverId = srcss[0];
				TomcatServer ts = ServerFactory.getServerById(Integer.parseInt(serverId));
				if(ts != null){
					if(size != null && size.length() > 0){
						return FileConfig.getValue("imgDomain1")+"/up/"+ srcss[2]+"/s/"+fileName.replace(".", "-"+size+".");
					}else{
						return FileConfig.getValue("imgDomain1")+"/up/"+ srcss[2]+"/s/"+fileName;
					}
					
				}
			}
		}
		return null;
	}
	
	/****
	 * 获取文件的上传服务器
	 * @param fileName
	 * @return
	 */
	public static TomcatServer getServer(String fileName){
		String src = getFileInfo(fileName);
		if(src != null){
			String[] srcss = src.split(FileInfo.split);
			if(srcss.length == 3){
				String serverId = srcss[0];
				TomcatServer ts = ServerFactory.getServerById(Integer.parseInt(serverId));
				return ts;
			}
		}
		
		return null;
	}



	public static String getFileInfo(String fileName){
		String[] srcs = fileName.split(FileInfo.split2);
		
		if(srcs.length >= 2){
			String src = StringUtil.toStringHex(srcs[0]);
			return src;
		}
		return null;
	}
	
	public static void main(String[] args) {
		log.info("df".split(" ").length);
	}

}

