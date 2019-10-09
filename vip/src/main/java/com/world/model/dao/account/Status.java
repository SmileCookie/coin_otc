package com.world.model.dao.account;

import com.world.config.GlobalConfig;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.user.User;
import com.world.util.string.MD5;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Random;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class Status
{
  static Logger logger = Logger.getLogger(Status.class);

  public static String getRemortIP(HttpServletRequest request)
  {
    String ip = request.getHeader("Cdn-Src-Ip");

    if ((ip == null) || (ip.length() == 0) || (" unknown".equalsIgnoreCase(ip))) {
      ip = request.getHeader("X-Real-IP");
    }
    if ((ip == null) || (ip.length() == 0) || (" unknown".equalsIgnoreCase(ip))) {
      ip = request.getRemoteAddr();
    }

    return ip;
  }

  public static String GetRadomStr()
  {
    String[] str = { "a", "B", "c", "D", "e", "f", "G", "h", "i", "J", "k", "L", "m", "n", "o", "P", "q", "r", "s", "T", "u", "v", "w", "X", "y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "!", "@", "#", "$", "%" };
    Random r = new Random();
    int length = r.nextInt(6);
    if (length <= 0)
      length = 1;
    String ls = "";
    for (int j = 0; j < length; j++)
    {
      int a = r.nextInt(str.length);
      ls = ls + str[a];
    }

    return ls;
  }

  public static boolean isNumberic(String str)
  {
    if (str == null) {
      return false;
    }
    int sz = str.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isDigit(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static User DoLogin(String userName, String passWord, int keepMnute, Boolean safeLogin, HttpServletRequest request, HttpServletResponse response)
  {
    if (keepMnute <= 80) {
      keepMnute = 1440;
    }
    logger.debug("收到验证登录的请求:" + userName);
    try {
      if ((userName.length() < 1) || (passWord.length() < 1)) {
        logger.error("收到一个过短的用户名或者密码登录请求,登录失败!");
        return null;
      }
      UserDao userDao = new UserDao();
      User safeUser = null;
      if ((userName.indexOf('@') > 0) && (userName.indexOf('.') > 0))
        safeUser = userDao.getUserByColumn(userName, "email");
      else {
        safeUser = userDao.getUserByColumn(userName, "userName");
      }

      if (safeUser != null)
      {
        passWord = MD5.toMD5(safeUser.getEmail() + passWord);
        if (!safeUser.getPwd().equals(passWord)) {
          logger.error("密码不正确!" + safeUser.getPwd() + ":" + passWord);
          return null;
        }

        String loginStatus = "True";
        String ip = getRemortIP(request);
        long nowTime = System.currentTimeMillis();

        String safe = GetRadomStr() + ":" + safeUser.get_Id() + ":" + GlobalConfig.baseDomain + ":" + nowTime + ":" + keepMnute + ":" + ip + ":" + GetRadomStr();

        safe = Encryption.encrypt(safe);
        Cookie c = new Cookie("userID", safeUser.get_Id().toString());
        Cookie cu = new Cookie("userName", URLEncoder.encode(safeUser.getUserName(), "UTF-8"));
        Cookie cs = new Cookie("loginStatus", loginStatus);
        Cookie cd = new Cookie("domains", GlobalConfig.baseDomain);
        Cookie css = new Cookie("safe", safe);
        int keepTime = keepMnute * 60;
        c.setMaxAge(keepTime);
        cu.setMaxAge(keepTime * 24 * 30);
        cs.setMaxAge(keepTime);
        css.setMaxAge(keepTime);
        cd.setMaxAge(keepTime);
        Timestamp ts = new Timestamp(nowTime);
        safeUser.setLastLoginTime(ts);
        safeUser.setLoginIp(ip);
        userDao.save(safeUser);

        logger.debug("本地本地域名：" + GlobalConfig.baseDomain);
        css.setDomain(GlobalConfig.baseDomain);
        cs.setDomain(GlobalConfig.baseDomain);
        cu.setDomain(GlobalConfig.baseDomain);
        c.setDomain(GlobalConfig.baseDomain);
        cd.setDomain(GlobalConfig.baseDomain);

        css.setPath("/");
        cs.setPath("/");
        cu.setPath("/");
        c.setPath("/");
        cd.setPath("/");
        response.addCookie(c);
        response.addCookie(cu);
        response.addCookie(cs);
        response.addCookie(css);
        response.addCookie(cd);

        logger.debug("用户'" + userName + "'登陆成功");
        return safeUser;
      }
      logger.debug("用户名或者密码错误导致失败");
      return null;
    }
    catch (Exception ex) {
      logger.error(ex.toString(), ex);
    }return null;
  }

  public Boolean DoQQLogin(String userName, String passWord, int keepMnute, Boolean safeLogin, HttpServletRequest request, HttpServletResponse response)
  {
    return Boolean.valueOf(false);
  }

  public static Boolean IsCheckedUser(HttpServletRequest request)
  {
    boolean login = Check(request).booleanValue();

    if (login)
    {
      return Boolean.valueOf(false);
    }
    return Boolean.valueOf(false);
  }

  public static Boolean Check(HttpServletRequest request)
  {
    try
    {
      String safe = "";
      Boolean loginning = Boolean.valueOf(false);
      Cookie[] mycookies = request.getCookies();
      if (mycookies != null) {
        String clientId = "";
        String clientdomain = "";
        for (int i = 0; i < mycookies.length; i++) {
          if ("userID".equals(mycookies[i].getName()))
            clientId = mycookies[i].getValue();
          if ("domains".equals(mycookies[i].getName()))
            clientdomain = mycookies[i].getValue();
        }
        for (int i = 0; i < mycookies.length; i++) {
          if (!"safe".equals(mycookies[i].getName()))
            continue;
          safe = mycookies[i].getValue();
          if (safe.length() > 0) {
            safe = Encryption.decrypt(safe);
            if (safe == null) {
              logger.error("用户安全密钥解压错误！");
              return Boolean.valueOf(false);
            }

            String[] params = safe.split(":");

            if (params.length == 7) {
              if ((!params[1].equals(clientId)) || (!params[2].equals(clientdomain))) {
                logger.error("发现明文用户id或域名与加密id域名不同步,疑似攻击");
                return Boolean.valueOf(false);
              }

              long SaveTime = Long.parseLong(params[3]);
              int minite = Integer.parseInt(params[4]);
              long now = System.currentTimeMillis();

              if ((now - SaveTime) / 60000L > minite) {
                logger.error("用户'" + params[1] + "'用一个已经过期的cookie进行了验证,有可能是手动攻击" + (now - SaveTime) / 60000L);
                return Boolean.valueOf(false);
              }

              loginning = Boolean.valueOf(true);
            } else {
              logger.error("safe异常：" + safe);
              return Boolean.valueOf(false);
            }
          } else {
            logger.error("含有safe，但是为空");
          }
        }
      }
      else {
        logger.debug("没有coodie,登录失败" + getRemortIP(request));
      }
      logger.debug("登录判断状态：" + loginning + getRemortIP(request));
      return loginning; } catch (Exception ex) {
    }
    return Boolean.valueOf(false);
  }
}