package com.world.model.entity.decoration;

import com.google.code.morphia.annotations.Id;
import com.world.web.UiContainer;
import java.io.Serializable;
import org.bson.types.ObjectId;

public class UiPage
  implements Serializable
{
  private static final long serialVersionUID = -2396118543279873657L;

  @Id
  private ObjectId _id;
  public String name;
  public UiPageType uiPageType;
  public int UserId = 0;
  public String UserDomain;
  public boolean IsBeStatic;
  public int TimeOut = 0;
  public String pageCode;
  public UiContainer uiContainer;

  public ObjectId get_id()
  {
    return this._id;
  }
  public void set_id(ObjectId _id) {
    this._id = _id;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public UiPageType getUiPageType() {
    return this.uiPageType;
  }
  public void setUiPageType(UiPageType uiPageType) {
    this.uiPageType = uiPageType;
  }
  public int getUserId() {
    return this.UserId;
  }
  public void setUserId(int userId) {
    this.UserId = userId;
  }
  public String getUserDomain() {
    return this.UserDomain;
  }
  public void setUserDomain(String userDomain) {
    this.UserDomain = userDomain;
  }
  public boolean isIsBeStatic() {
    return this.IsBeStatic;
  }
  public void setIsBeStatic(boolean isBeStatic) {
    this.IsBeStatic = isBeStatic;
  }
  public int getTimeOut() {
    return this.TimeOut;
  }
  public void setTimeOut(int timeOut) {
    this.TimeOut = timeOut;
  }
  public String getPageCode() {
    return this.pageCode;
  }
  public void setPageCode(String pageCode) {
    this.pageCode = pageCode;
  }
  public UiContainer getUiContainer() {
    return this.uiContainer;
  }
  public void setUiContainer(UiContainer uiContainer) {
    this.uiContainer = uiContainer;
  }
}