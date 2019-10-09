package com.world.web.convention.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JspPackage {
  /***
   * 路径
   * @return 功能包路径
   */
  public abstract String path();
  /***
   * 功能包描述
   * @return 功能包描述
   */
  public abstract String des();
}
