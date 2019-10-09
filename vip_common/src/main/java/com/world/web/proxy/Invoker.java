package com.world.web.proxy;

import java.lang.reflect.Method;

/**
 * 调用器接口
 */
public interface Invoker {
	/**
	 * 获取方法本身
	 * 
	 * @return
	 */
	Method method();

	/**
	 * 调用方法
	 * 
	 * @param host
	 *            执行对象
	 * @param args
	 *            执行参数
	 * @return
	 */
	Object invoke(Object host, Object[] args);
}
