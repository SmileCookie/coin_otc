package com.world.timer;
import java.io.File;

import org.apache.log4j.helpers.LogLog;

import com.world.model.dao.task.Worker;
import com.world.util.path.PathUtil;

public abstract class FileWatchdog  extends Worker{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6789884756512839909L;

	protected String filename;
	  public  String lan;

	File file;
	long lastModif = 0;
	boolean warnedAlready = false;
	boolean interrupted = false;
	public FileWatchdog() {}
	public FileWatchdog(String f , String name , String des) {
		super(name, des);
		lan=f;
		PathUtil util = new PathUtil();
		String csPath = util.getWebClassesPath();
		String filename=csPath + "L_" + f + ".txt";
		this.filename = filename;
		file = new File(filename);
		//setDaemon(true);
		checkAndConfigure();
	}

	abstract protected void doOnChange();

	protected void checkAndConfigure() {
		boolean fileExists;
		try {
			fileExists = file.exists();
		} catch (SecurityException e) {
			LogLog.warn("Was not allowed to read check file existance, file:["
					+ filename + "].");
			interrupted = true; // there is no point in continuing
			return;
		}

		if (fileExists) {
			long l = file.lastModified(); // this can also throw a
											// SecurityException
			if (l > lastModif) { // however, if we reached this point this
				lastModif = l; // is very unlikely.
				doOnChange();
				warnedAlready = false;
			}
		} else {
			if (!warnedAlready) {
				LogLog.debug("[" + filename + "] does not exist.");
				warnedAlready = true;
			}
		}
	}

	public void run() {
		//super.run();
		checkAndConfigure();
	}
	
	public void stopThread(){
		interrupted = true;
	}
}
