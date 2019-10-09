package com.world.web;
 import  java.lang.annotation.Documented;  
 import  java.lang.annotation.ElementType;  
 import  java.lang.annotation.Retention;  
 import  java.lang.annotation.RetentionPolicy;  
 import  java.lang.annotation.Target;
/*
 * 测试类
 *Viewer：视图 为空直接输出 .xslt xslt视图 .jsp jsp视图
*/
@Target (ElementType.METHOD)  
@Retention (RetentionPolicy.RUNTIME)  
@Documented 
public @interface Page {
	  ViewerPathType value() default ViewerPathType.DEFAULT;
	  String Viewer() default "";
	  int Cache() default 0;//缓存
	  int Auth() default 3;
	  boolean compress() default false;//是否压缩文档
	  String des() default "";//描述
	  /****
	   * 标记角色权限中是否有板块的划分
	   * @return
	   */
	  boolean plate() default false;
	  //是否存储访问日志
	  boolean saveLog() default false;
	  //是否存储访问日志
	  boolean ipCheck() default false;
}
