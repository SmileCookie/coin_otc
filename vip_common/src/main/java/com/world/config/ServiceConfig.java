package com.world.config;

import com.world.util.path.PathUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

public class ServiceConfig extends Config
{
  private static Logger log = Logger.getLogger(ServiceConfig.class.getName());

  public static ServiceConfig connect;
  public static ServiceConfigBean connectBean;
  public static ServiceServerBean[] csr;

  public int startNumber()
  {
    return 105;
  }

  public static ServiceServerBean GetServer(String name)
  {
    name = name.toLowerCase();
    if (csr == null)
      return null;
    for (int i = 0; i < csr.length; i++) {
      if (csr[i].getServerName().equals(name))
        return csr[i];
    }
    return null;
  }

  public void Config()
  {
    try
    {
      connect = (ServiceConfig)InitConfig.GetConfig(ServiceConfig.class.getName());
      connectBean = (ServiceConfigBean)configBean;
      csr = connectBean.getConnectServerBean();
      if (csr != null) {
        for (int i = 0; i < csr.length; i++) {
          csr[i].setServerName(csr[i].getServerName().toLowerCase());
        }

      }

    }
    catch (Exception ex)
    {
      log.error(ex.toString(), ex);
    }
  }

  public void SaveConfig()
  {
    try
    {
      ServiceConfigBean connectBean = new ServiceConfigBean();

      ServiceServerBean connectServerBean = new ServiceServerBean();
      connectServerBean.setIp("127.0.0.1");
      connectServerBean.setServerName("sphinxPool");
      connectServerBean.setPort(112211);

      ServiceServerBean connectServerBean2 = new ServiceServerBean();
      connectServerBean2.setServerName("vedioDue");
      connectServerBean.setIp("127.0.0.1");
      connectServerBean2.setPort(112211);

      ServiceServerBean[] connectServerBeans = { connectServerBean, connectServerBean2 };
      connectBean.setConnectServerBean(connectServerBeans);

      XStream xstream = new XStream(new DomDriver());

      PathUtil util = new PathUtil();
      String csPath = util.getWebClassesPath();
      if (csPath.endsWith(".jar"))
        csPath = csPath.substring(0, csPath.indexOf("/lib/") + 1) + "classes/";
      String path = csPath + "xml/" + getClass().getName().replace('.', '-') + ".xml";
      log.info(path);

      String xml = xstream.toXML(connectBean);
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