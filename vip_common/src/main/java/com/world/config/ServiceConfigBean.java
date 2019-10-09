package com.world.config;

public class ServiceConfigBean extends ConfigBean
{
  private ServiceServerBean[] serviceServerBean;

  public ServiceServerBean[] getConnectServerBean()
  {
    return this.serviceServerBean;
  }
  public void setConnectServerBean(ServiceServerBean[] connectServerBean) {
    this.serviceServerBean = connectServerBean;
  }
}