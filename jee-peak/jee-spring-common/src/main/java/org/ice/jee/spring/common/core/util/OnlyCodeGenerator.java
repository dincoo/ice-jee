package org.ice.jee.spring.common.core.util;

/**
 * 唯一编码生成器
 * @author jaden
 *
 */
public class OnlyCodeGenerator {
	
	private static final SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
	
	public static String secriet(){
		return "Se"+idWorker.nextId();
	}
	
	/**
	 * 用户每次登陆申请一次密钥
	 * @return
	 */
	public static String sessionSecuretKey(){
		return String.valueOf(System.currentTimeMillis());
	}
	
	/**
	 * 每次请求唯一id
	 * @return
	 */
	public static String getRequestId(){
		return "Rq"+idWorker.nextId();
	}


	/**
	*	分布式ID生成算法，结果是一个long型的ID
	*/
	public static String distriId(){
		return String.valueOf(idWorker.nextId());
	}

}
