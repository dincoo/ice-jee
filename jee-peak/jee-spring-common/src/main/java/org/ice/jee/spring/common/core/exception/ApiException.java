package org.ice.jee.spring.common.core.exception;

/**
 * 接口请求逻辑异常
 */
public class ApiException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ApiException() {
		super();
	}
	
	public ApiException(String message){
		super(message);
	}

	public ApiException(String message, Throwable cause){
		super(message, cause);
	}
	
	public ApiException(Throwable cause){
		super(cause);
	}
}
