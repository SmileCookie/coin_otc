package com.world.timer;

import com.Lan;



public  class WatchLanguage extends FileWatchdog{
  
	/**
	 * 
	 */
	private static final long serialVersionUID = -4269739121594735893L;


	public  WatchLanguage(String filename , String name , String des) {
        //可以在这里加一些初始化的代码
		super(filename , name , des);
		this.name = filename + ":WatchLanguage";
		this.des = filename + "语言文件";
    }
	
	
    public void doOnChange() {
      //当文件发生变化时会调用这个方法,我们在这里写重新加载的代码就可以了.
    	log.info("===================="+lan+" changed===============");
    	Lan.setLanguageResource(lan);
    }
}
