package com.world.cache;

import com.world.config.ConfigBean;

public class MemoryBean extends ConfigBean
{
  private int connectionPoolSize;
  private String commandFactory;
  private String sessionLocator;
  private String transcoder;
  private String bufferAllocator;
  private MemoryServerBean[] memoryServerBean;

  public int getConnectionPoolSize()
  {
    return this.connectionPoolSize;
  }
  public void setConnectionPoolSize(int connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
  }
  public String getCommandFactory() {
    return this.commandFactory;
  }
  public void setCommandFactory(String commandFactory) {
    this.commandFactory = commandFactory;
  }
  public String getSessionLocator() {
    return this.sessionLocator;
  }
  public void setSessionLocator(String sessionLocator) {
    this.sessionLocator = sessionLocator;
  }
  public String getTranscoder() {
    return this.transcoder;
  }
  public void setTranscoder(String transcoder) {
    this.transcoder = transcoder;
  }
  public String getBufferAllocator() {
    return this.bufferAllocator;
  }
  public void setBufferAllocator(String bufferAllocator) {
    this.bufferAllocator = bufferAllocator;
  }
  public MemoryServerBean[] getMemoryServerBean() {
    return this.memoryServerBean;
  }
  public void setMemoryServerBean(MemoryServerBean[] memoryServerBean) {
    this.memoryServerBean = memoryServerBean;
  }
}