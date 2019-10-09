package com.world.config;

import com.world.util.path.PathUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.apache.log4j.Logger;

public class Config
{
  private static Logger log = Logger.getLogger(Config.class);
  public static Config config;
  public static ConfigBean configBean;
  public static int cusId;

  public int startNumber()
  {
    return 0;
  }

  public void ReloadConfig()
  {
  }

  public void setConfigBean(ConfigBean configBean)
  {
    this.configBean = configBean;
  }

  public void Config()
  {
    config = InitConfig.GetConfig(Config.class.getName());
    configBean = configBean;
    cusId = configBean.getCusId();
  }

  public void SaveConfig()
  {
    try
    {
      ConfigBean configBean = new ConfigBean();
      configBean.setCusId(100);
      configBean.setBeanName("测试用的xml");
      XStream xstream = new XStream(new DomDriver());

      PathUtil util = new PathUtil();
      String path = util.getWebClassesPath() + "xml/" + getClass().getName().replace('.', '-') + ".xml";
      log.debug("根目录class路径为：" + path);

      String xml = xstream.toXML(configBean);
      xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + xml;
      OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path), "UTF-8");
      out.write(xml);
      out.flush();
      out.close();
    }
    catch (Exception ex) {
      log.error(ex.toString(), ex);
    }
  }
}