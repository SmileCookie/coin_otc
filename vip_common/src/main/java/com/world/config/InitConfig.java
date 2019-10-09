package com.world.config;

import com.world.util.path.FindClass;
import com.world.util.path.PathUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

public class InitConfig
{
  static Logger log = Logger.getLogger(InitConfig.class);
  public static Hashtable<String, Config> configs = new Hashtable();

  public static Boolean isLocal = Boolean.valueOf(false);
  
  public static boolean isInit = false;

  public static Config GetConfig(String name)
  {
	 if (configs.containsKey(name)) {
      return (Config)configs.get(name);
    }
    return null;
  }

  public static Config GetConfig(Class config)
  {
    String name = config.getName();
    return GetConfig(name);
  }

  public static void SetConfig(Object config)
  {
    String name = config.getClass().getName();
    if (configs.contains(name)) {
      return;
    }

    configs.put(name, (Config)config);
  }

  public static void SaveConfig(String name)
  {
    if (configs.contains(name))
    {
      Config config = GetConfig(name);
      config.SaveConfig();
    }
    else
    {
      try {
        Class configClass = Class.forName(name);
        Config config = (Config)configClass.newInstance();
        config.SaveConfig();
      }
      catch (Exception ex) {
        log.error(ex.toString(), ex);
      }
    }
  }

  public static void ClearAllConfig()
  {
  }

  public synchronized static void InitAllConfig()
  {
	  if(isInit){
	      return;
	  }
	  InitAllConfigTrue();
  }

  public synchronized static void InitAllConfigTrue()
  {
	 isInit = true;
    configs = new Hashtable();

    Set classes = FindClass.getClasses2(Config.class.getPackage());

    List configClass = new ArrayList();

    Iterator it = classes.iterator();
    PathUtil util = new PathUtil();
    while (it.hasNext())
    {
      Class cs = (Class)it.next();
      try
      {
        if (!Config.class.isAssignableFrom(cs))
          continue;
        Object config = cs.newInstance();
        configClass.add(config);
      }
      catch (Exception ex)
      {
        log.error(ex.toString(), ex);
      }
    }

    ComparatorConfig sortConfig = new ComparatorConfig();
    Collections.sort(configClass, sortConfig);

    int i = 0;
    String csPath = util.getBasePath();//util.getWebClassesPath();
    
    if (csPath.endsWith(".jar"))
      csPath = csPath.substring(0, csPath.indexOf("/lib/") + 1) + "classes/";
    log.info("配置的根路径为：" + csPath + "xml/");
    int m = 0;
    for (i = 0; i < configClass.size(); i++) {
      try
      {
        Config config = (Config)configClass.get(i);
        String path = "/xml/" + config.getClass().getName().replace('.', '-') + ".xml";
        
        //path = util.getJarConfigPath(path);
        String rtn = util.jarFileToString(path);
        
//        File f = new File(path);
//        if (!f.exists()) {
//          continue;
//        }
        
        if (rtn == null || rtn.length() <= 0) {
          continue;
        }
        XStream xstream = new XStream(new DomDriver());

        //PathUtil.FileToString(path);
        ConfigBean bean = (ConfigBean)xstream.fromXML(rtn);
        Method setConfigBean = config.getClass().getMethod("setConfigBean", new Class[] { ConfigBean.class });
        setConfigBean.invoke(config, new Object[] { bean });

        SetConfig(config);

        Method doConfig = config.getClass().getMethod("Config", new Class[0]);
        doConfig.invoke(config, new Object[0]);
        log.info("成功初始化配置:" + config.getClass().getName().toString());
        m++;
      }
      catch (Exception ex)
      {
        log.error(ex.toString(), ex);
      }
    }
    log.info("初始化了" + m + "个配置项,系统启动完毕!");
  }
}