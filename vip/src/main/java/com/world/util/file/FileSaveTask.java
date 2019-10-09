package com.world.util.file;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/****
 * 序列化保存域反序列化读取
 * 保存对象到文件并读取出来
 * @author apple
 *
 */
public class FileSaveTask{

    private final static Logger log = Logger.getLogger(FileSaveTask.class);

	public static synchronized Object readObject(String fileName) {
		Object obj = null;
		ObjectInputStream oi = null;
		InputStream input = null;
		
		try {
			File noDealFile = new File(fileName + ".data");
			log.info(noDealFile.getAbsolutePath());
			if(noDealFile.exists()){
				input =  new FileInputStream(noDealFile);
				oi = new ObjectInputStream(input);
				obj = oi.readObject();
			}
		} catch (Exception e) {
		    log.error(e.toString(), e);
		}finally{
			if(oi != null){
				try {
					oi.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
		}
		return obj;
	}

	public static synchronized void save(Object obj , String fileName){
		OutputStream out = null;
		ObjectOutputStream oo = null;
		try {
			File saveData= new File(fileName + ".data");
			log.info("保存为文件：" + saveData.getAbsolutePath());
			out =  new FileOutputStream(saveData);
			oo = new ObjectOutputStream(out);
			oo.writeObject(obj);
		} catch (Exception e) {
		    log.error(e.toString(), e);
		}finally{
			if(oo != null){
				try {
					oo.flush();
					oo.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
			
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
		}
	    log.info("保存" + fileName);
    }

}
