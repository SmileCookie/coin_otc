package com.file;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.world.util.string.StringUtil;

@Entity(noClassnameStored = true , value = "file_info")
public class FileInfo {
	public final static String upExt = ".jpg.jpeg.gif.png.bmp";
	public final static String fileExt = ".rar.pdf.chm.txt.zip.docx.doc.flv.iso.exe.swf";
	
	public final static String split = ":";
	public final static String split2 = "_";
	public final static String split3 = "-";
	
	@Id
	private ObjectId _id;//主键
	private String fileType;//文件类型
	private String server;//主服务器
	private int fileSize;//文件大小
	private String name;//文件名称
	private String userId;//上传用户ID
	private String folder;//文件夹
	private String task;//规则号
	
	private String storageName;//储存名

	public String getStorageName() {
		
		storageName = server + split + task + split + folder;
		return StringUtil.getHexString(storageName) + split2 + _id;
	}
	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public ObjectId get_id() {
		return _id;
	}
	public void set_id(ObjectId _id) {
		this._id = _id;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public int getFileSize() {
		return fileSize;
	}
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	
}
