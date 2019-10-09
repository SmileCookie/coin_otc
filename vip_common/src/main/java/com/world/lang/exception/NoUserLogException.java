package com.world.lang.exception;

/****
 * 没有用户登录的异常
*   
* @version   
*
 */
public class NoUserLogException extends RuntimeException {

	private static final long serialVersionUID = -4567448936194084879L;

	public NoUserLogException() {
		super("");
	}
	
    public NoUserLogException(String message) {
    	super(message);
    }
    
    public NoUserLogException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NoUserLogException(Throwable cause) {
        super(cause);
    }
}
