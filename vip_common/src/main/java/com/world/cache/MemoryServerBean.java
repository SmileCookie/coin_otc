package com.world.cache;

import java.io.Serializable;

public class MemoryServerBean
  implements Serializable
{
  private String serverName;
  private int port;
  private int weight;

  public String getServerName()
  {
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
  public int getWeight() {
    return this.weight;
  }
  public void setWeight(int weight) {
    this.weight = weight;
  }
}