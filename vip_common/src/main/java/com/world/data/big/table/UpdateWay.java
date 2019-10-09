package com.world.data.big.table;

/***
 * 数据库更新操作，包括添加、修改、删除
 * @author apple
 */
public enum UpdateWay {
	SYNC, //同步方法  及插入时直接到分表中
	ASYNC;//异步方法 需要从主表里面异步获取过来 , 一般情况下同步都是有条件的
}
