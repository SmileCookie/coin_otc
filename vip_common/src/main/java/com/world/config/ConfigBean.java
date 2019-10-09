package com.world.config;

import java.io.Serializable;

public class ConfigBean
  implements Serializable
{
  private int cusId;
  private String beanName = "ConfigBean";

  public int getCusId() {
    return this.cusId; } 
  public void setCusId(int cusId) { this.cusId = cusId; } 
  public String getBeanName() {
    return this.beanName; } 
  public void setBeanName(String beanName) { this.beanName = beanName;
  }
}