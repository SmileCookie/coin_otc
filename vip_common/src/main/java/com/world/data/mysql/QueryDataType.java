package com.world.data.mysql;

/***
 * 查询数据分类 及 表分类、
 * 
 * DEFAULT ,//默认表及主表（处理逻辑业务的表）
   ALL , //包含所有数据的表（保存所有数据的表，用户后台统计工作）
   DOWN; //拆分后的表（拆分后的表，用来提高查询速度）
 * @author apple
 *
 */
public enum QueryDataType {

	DEFAULT ,//默认表及主表
	ALL , //包含所有数据的表
	DOWN; //拆分后的表
}
