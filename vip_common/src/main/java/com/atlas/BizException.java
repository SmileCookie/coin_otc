package com.atlas;

/**
 * 业务异常
 * 如果业务中出现异常,
 * 应直接抛出此异常
 * 不应使用try catch+ log.error的方式
 * Created by wangfei on 2017/7/27.
 */
public class BizException extends RuntimeException{
	public BizException(String message){
		super(message);
	}

	public BizException(Throwable e){
		super(e);
	}
}
