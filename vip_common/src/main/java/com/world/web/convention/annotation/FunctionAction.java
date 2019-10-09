package com.world.web.convention.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.PACKAGE, java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionAction {
  /***
   * 路径
   * @return 功能包路径
   */
  public abstract String jspPath() default "";
  /***
   * 功能包描述
   * @return 功能包描述
   */
  public abstract String des() default "";
  
  /****
   * 标记角色权限中是否有板块的划分
   * @return
   */
  boolean plate() default false;
  
  
}
