package com.world.model.entity.decoration;

import com.google.code.morphia.annotations.Id;
import java.io.Serializable;
import org.bson.types.ObjectId;

public class UiModule
  implements Serializable
{
  private static final long serialVersionUID = -2396118543279873658L;

  @Id
  private ObjectId _id;
  public String defaultParams;
  public String htmlId;
  public String moduleId;
  public String Css;
  public String CssPath;
  public String jsInitName;
  public String jspath;
  public String ViewerPath;
  public String FunctionNames;
  public String name;

  public ObjectId get_id()
  {
    return this._id;
  }

  public void set_id(ObjectId _id) {
    this._id = _id;
  }

  public String getDefaultParams() {
    return this.defaultParams;
  }

  public void setDefaultParams(String defaultParams) {
    this.defaultParams = defaultParams;
  }

  public String getHtmlId() {
    return this.htmlId;
  }

  public void setHtmlId(String htmlId) {
    this.htmlId = htmlId;
  }

  public String getModuleId() {
    return this.moduleId;
  }

  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  public String getCss() {
    return this.Css;
  }

  public void setCss(String css) {
    this.Css = css;
  }

  public String getCssPath() {
    return this.CssPath;
  }

  public void setCssPath(String cssPath) {
    this.CssPath = cssPath;
  }

  public String getJsInitName() {
    return this.jsInitName;
  }

  public void setJsInitName(String jsInitName) {
    this.jsInitName = jsInitName;
  }

  public String getJspath() {
    return this.jspath;
  }

  public void setJspath(String jspath) {
    this.jspath = jspath;
  }

  public String getViewerPath() {
    return this.ViewerPath;
  }

  public void setViewerPath(String viewerPath) {
    this.ViewerPath = viewerPath;
  }

  public String getFunctionNames() {
    return this.FunctionNames;
  }

  public void setFunctionNames(String functionNames) {
    this.FunctionNames = functionNames;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public static long getSerialversionuid() {
    return -2396118543279873658L;
  }
}