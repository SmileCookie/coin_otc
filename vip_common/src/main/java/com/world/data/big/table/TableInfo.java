package com.world.data.big.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/****
 * 
 * @author apple
 *
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TableInfo {
	String[] databases();
	String[] targetDatabases() default {};//目标数据库
	
	String tableName();
	boolean tableDown() default false;//是否拆分表
	String field() default "userId";//拆分表的字段
	int shardNum() default 0;//拆分表的总数
	int mainDays() default 30;//主表保存的天数
	UpdateWay updateWay() default UpdateWay.ASYNC;//插入方式
	String[] conditions() default {};//UpdateWay.ASYNC 异步同步数据到分表的条件 、 及需要把那些个数据同步到分表，可以是过期的数据  或者 状态可以到分表的数据
	String[] conditionsParams() default {};//如果conditions里面有 ? 则靠此处的类和方法复制  及  class method  //只支持无参方法
	int asyncFrequency() default 5;//异步同步的频率 单位秒   及多长时间检测一下是否有需要同步的数据
	String primaryKey() default "id";
	
	
}
