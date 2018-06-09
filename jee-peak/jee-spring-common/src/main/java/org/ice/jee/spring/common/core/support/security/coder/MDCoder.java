/**
 * 2011-01-11
 */
package org.ice.jee.spring.common.core.support.security.coder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.ice.jee.spring.common.core.support.security.Hex;
import org.ice.jee.spring.common.core.support.security.SecurityCoder;

/**
 * MD加密组件
 * 
 * @author ShenHuaJie
 * @version 1.0
 * @since 1.0
 */
public abstract class MDCoder extends SecurityCoder {

	/**
	 * MD2加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeMD2(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("MD2");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * MD4加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeMD4(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("MD4");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * MD5加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeMD5(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("MD5");
		// 执行消息摘要
		return md.digest(data);
	}


	/**
	 * MD5加密
	 * 
	 * @param data 待加密数据
	 * @param salt 盐
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static String MD5(byte[] message , byte[] salt) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		// 获得MD5摘要算法的 MessageDigest 对象
		MessageDigest mdInst;
		try {
			mdInst = MessageDigest.getInstance("MD5");
			if(null != salt){
				// 使用指定的字节更新摘要
				mdInst.update(salt);
			}
			// 获得密文
			byte[] md = mdInst.digest(message);
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
		
	}
	
	/**
	 * Tiger加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeTiger(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("Tiger");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * TigerHex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static String encodeTigerHex(byte[] data) throws Exception {
		// 执行消息摘要
		byte[] b = encodeTiger(data);
		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * Whirlpool加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeWhirlpool(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("Whirlpool");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * WhirlpoolHex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static String encodeWhirlpoolHex(byte[] data) throws Exception {
		// 执行消息摘要
		byte[] b = encodeWhirlpool(data);
		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}

	/**
	 * GOST3411加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static byte[] encodeGOST3411(byte[] data) throws Exception {
		// 初始化MessageDigest
		MessageDigest md = MessageDigest.getInstance("GOST3411");
		// 执行消息摘要
		return md.digest(data);
	}

	/**
	 * GOST3411Hex加密
	 * 
	 * @param data 待加密数据
	 * @return byte[] 消息摘要
	 * @throws Exception
	 */
	public static String encodeGOST3411Hex(byte[] data) throws Exception {
		// 执行消息摘要
		byte[] b = encodeGOST3411(data);
		// 做十六进制编码处理
		return new String(Hex.encode(b));
	}
}
