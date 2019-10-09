package com.world.util.callback;

/***
 * 回调方法
 * @author apple
 *
 */
public class CallbackMethod {

	public CallbackMethod(Class<?> cls, String method, Object[] params) {
		super();
		this.cls = cls;
		this.method = method;
		this.params = params;
	}
	private Class<?> cls;//方法所在的类
	private String method;//方法名称
	private Object[] params;//参数
	public Class<?> getCls() {
		return cls;
	}
	public void setCls(Class<?> cls) {
		this.cls = cls;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
}
