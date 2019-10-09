package com.world.data.mysql.config;

import java.util.Hashtable;

import com.world.config.Config;
import com.world.data.mysql.BeanCode;

/**
 * 功能:数据库程序管理的启动过程
 * @author Administrator
 *
 */
public class BaseConfig extends Config{
	//全局存储数据库文件的地方
	public static Hashtable<String,BaseConfigBean> bcb=new Hashtable<String,BaseConfigBean>();
	public static Hashtable<String,BeanCode> beans=new Hashtable<String,BeanCode>();
	/**
	 * 功能:改配置项的启动顺序
	 * 配置数据源应该在比较靠后的地方,所以设置的值比较大一些
	 */
	public int startNumber()
	{
		return 200;
	}

	/**
	 * 功能:添加或者更新一个名字
	 * @param baseConfigBean
	 */
	public void setBcb(BaseConfigBean baseConfigBean)
	{
		if(baseConfigBean==null)
			return;
		//如果已经存在就删除,重新设置,这样重新加载的时候就不会出错
		String name=baseConfigBean.getMingzi().toUpperCase();
	//	log.info(name);
		if(bcb.contains(name))
				bcb.remove(name);
		bcb.put(name, baseConfigBean);
	}
	/**
	 * 获取指定名称的数据库程序
	 * @param name 名字
	 * @return BaseConfigBean
	 */
	public static BaseConfigBean getProgrom(String name){
		return bcb.get(name.toUpperCase());
	}
	/**
	 * 功能:配置基础数据库progrom程序的配置程序
	 * @param baseConfigBeans
	 * @return
	 */
	private BaseConfigBean configBaseConfigBeans(BaseConfigBean baseConfigBeans)
	{
		setBcb((BaseConfigBean)baseConfigBeans);
		return baseConfigBeans;
	}
	/**
	 * 功能:获取一个指定那么的bean
	 * @param name
	 * @return
	 */
	public static BeanCode getBean(String name)
	{
		name=name.toUpperCase();
		return beans.get(name);
	}
	


}
