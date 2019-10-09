package com.world.web.convention.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*****
 * 定义缓存相关
 * 此注解为补充viewer cache  但不影响之前的功能
 * @author apple
 *
 */
@Target(value = {ElementType.METHOD , ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionCache {

	boolean commonCache() default false;//普通的cache
	boolean userCache() default false;//用户cache
	boolean pageCache() default false;//分页cache
	int maxPage() default 1;//最大缓存的页数
	String pageKey() default "page";//保存页码的key
	int cacheTime() default 0;//缓存时间
	String split() default "_";//缓存分割符 默认_
	String[] ignoreKey() default {};//url中要忽略的key
	String logoKey() default "";//要标识的字段   如：key1只和用户相关并唯一  则 选择标识为key1
	boolean proxyCache() default true;//是否为代理端的cache  及如果有缓存是否在nginx端直接返回缓存信息   false则需要到后端
}
