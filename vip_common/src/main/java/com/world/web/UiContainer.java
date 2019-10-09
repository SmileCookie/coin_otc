package com.world.web;

import com.google.code.morphia.annotations.Id;
import com.world.model.entity.decoration.UiModule;
import com.sun.corba.se.spi.ior.ObjectId;
import java.io.Serializable;

public class UiContainer
  implements Serializable
{
  private static final long serialVersionUID = -2396118543279873659L;

  @Id
  private ObjectId _id;
  public UiContainer next;
  public String staticStr;
  public UiModule uiModule;
  public String staticStrAfter;

  public ObjectId get_id()
  {
    return this._id;
  }
  public void set_id(ObjectId _id) {
    this._id = _id;
  }
  public UiContainer getNext() {
    return this.next;
  }
  public void setNext(UiContainer next) {
    this.next = next;
  }
  public String getStaticStr() {
    return this.staticStr;
  }
  public void setStaticStr(String staticStr) {
    this.staticStr = staticStr;
  }
  public UiModule getUiModule() {
    return this.uiModule;
  }
  public void setUiModule(UiModule uiModule) {
    this.uiModule = uiModule;
  }
  public String getStaticStrAfter() {
    return this.staticStrAfter;
  }
  public void setStaticStrAfter(String staticStrAfter) {
    this.staticStrAfter = staticStrAfter;
  }
  public static long getSerialversionuid() {
    return -2396118543279873659L;
  }
}