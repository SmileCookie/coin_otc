package com.world.model.entity.decoration;

import com.google.code.morphia.annotations.Id;
import java.io.Serializable;

public class UiFunction
  implements Serializable
{
  private static final long serialVersionUID = -2967025277463042477L;

  @Id
  private String _id;
  private String name;
  private String functionNames;
  private int progromId;
  private String params;
  private String paramsEditPath;
  private int usedCount;
  private int dropCount;
  private float price;
  private int lever;
  private String photos;
  private String des;
  private String managePath;
  private int manageWidth;
  private int manageHeight;

  public String get_id()
  {
    return this._id;
  }

  public void set_id(String fId) {
    this._id = fId;
  }
  public String getName() {
    return this.name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getFunctionNames() {
    return this.functionNames;
  }
  public void setFunctionNames(String functionNames) {
    this.functionNames = functionNames;
  }
  public int getProgromId() {
    return this.progromId;
  }
  public void setProgromId(int progromId) {
    this.progromId = progromId;
  }
  public String getParams() {
    return this.params;
  }
  public void setParams(String params) {
    this.params = params;
  }
  public String getParamsEditPath() {
    return this.paramsEditPath;
  }
  public void setParamsEditPath(String paramsEditPath) {
    this.paramsEditPath = paramsEditPath;
  }
  public int getUsedCount() {
    return this.usedCount;
  }
  public void setUsedCount(int usedCount) {
    this.usedCount = usedCount;
  }
  public int getDropCount() {
    return this.dropCount;
  }
  public void setDropCount(int dropCount) {
    this.dropCount = dropCount;
  }
  public float getPrice() {
    return this.price;
  }
  public void setPrice(float price) {
    this.price = price;
  }
  public int getLever() {
    return this.lever;
  }
  public void setLever(int lever) {
    this.lever = lever;
  }
  public String getPhotos() {
    return this.photos;
  }
  public void setPhotos(String photos) {
    this.photos = photos;
  }
  public String getDes() {
    return this.des;
  }
  public void setDes(String des) {
    this.des = des;
  }
  public String getManagePath() {
    return this.managePath;
  }
  public void setManagePath(String managePath) {
    this.managePath = managePath;
  }
  public int getManageWidth() {
    return this.manageWidth;
  }
  public void setManageWidth(int manageWidth) {
    this.manageWidth = manageWidth;
  }
  public int getManageHeight() {
    return this.manageHeight;
  }
  public void setManageHeight(int manageHeight) {
    this.manageHeight = manageHeight;
  }
}