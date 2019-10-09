package com.world.model.dao.account;

import org.apache.log4j.Logger;

public class PreBean
{
  Integer[] main = null;

  Integer[][] child = null;

  static Logger logger = Logger.getLogger(PreBean.class);

  public void SetPri(Integer[] m) {
    this.main = m;
  }

  public void SetPriC(Integer[][] m) {
    this.child = m;
  }

  public String CurrentPage()
  {
    if (this.main != null)
    {
      Integer[] m = this.main;
      String ls = "";
      for (int i = 0; i < m.length; i++)
      {
        if (m[i].intValue() == -1)
        {
          logger.info("顶级页面发现全板块控制权限，通过");
          return "";
        }
        if (m[i].intValue() != 0)
          ls = ls + "," + m[i];
      }
      if (ls.length() > 0)
        ls = ls.substring(1);
      return ls;
    }

    logger.info("顶级页面没找到权限控制（没有初始化），同样返回‘’ 通过了");
    return "";
  }

  public boolean GetControlView(int path, int borldId)
  {
    if (this.child != null)
    {
      Integer[][] m = this.child;
      for (int i = 0; i < m.length; i++)
      {
        if ((m[i][0].intValue() == -1) && (m[i][1].intValue() == path))
        {
          logger.info("子版块" + path + "获取权限时遇到全板块权限，直接通过");
          return true;
        }

        if ((m[i][0].intValue() != borldId) || (m[i][1].intValue() != path))
          continue;
        logger.info("子版块" + path + "获取权限时找到了自己的板块权限，直接通过");
        return true;
      }

      logger.info("子版块" + path + "没找到相应权限，返回false");
      return false;
    }

    logger.info("子版块" + path + "没有权限初始化，返回false");
    return false;
  }
}