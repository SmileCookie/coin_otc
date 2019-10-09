package com.world.config;

import java.io.Serializable;

public class ServiceServerBean
  implements Serializable
{
  private String serverName;
  private String ip;
  private int port;
  private String userName;
  private String passWord;
  private String url;
  private String dataBase;

  public String getIp()
  {
    return this.ip;
  }
  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getUserName()
  {
    return this.userName;
  }
  public void setUserName(String userName) {
    this.userName = userName;
  }
  public String getPassWord() {
    return this.passWord;
  }
  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }
  public String getUrl() {
    return this.url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getDataBase() {
    return this.dataBase;
  }
  public void setDataBase(String dataBase) {
    this.dataBase = dataBase;
  }
  public String getServerName() {
    return this.serverName;
  }
  public void setServerName(String serverName) {
    this.serverName = serverName;
  }
  public int getPort() {
    return this.port;
  }
  public void setPort(int port) {
    this.port = port;
  }
}