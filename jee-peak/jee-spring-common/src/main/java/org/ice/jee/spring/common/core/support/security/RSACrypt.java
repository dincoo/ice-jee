package org.ice.jee.spring.common.core.support.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.util.Base64Utils;

/**
 * RSA加解密算法工具 
 * @author Jaden
 *
 */
public class RSACrypt {

	private static final String KEY_ALGORITHM = "RSA";
	
	public static final String SPECIFIC_KEY_ALGORITHM = "RSA/ECB/PKCS1Padding";

	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
	
	private static final String PRIVATEKEY = "privatekey";
	
	private static final String PUBLICKEY = "publickey";
	
	private static final int DEFAULTKEYSIZE = 1024;
	
	private static final int MAX_ENCRYPT_BLOCK = 117;  
     
    private static final int MAX_DECRYPT_BLOCK = 128;  
    
    

	/**
	 * 生成公私钥对
	 * @param randomStr 用于产生随机数的字符
	 * @return
	 * @throws Exception
	 */
	public static Map<String,Object> genKeyPair(boolean useRamdomString, String... randomStr) throws Exception{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		if(useRamdomString){
			SecureRandom ranDom = new SecureRandom(randomStr[0].getBytes());
			keyPairGenerator.initialize(DEFAULTKEYSIZE, ranDom);
		}else{
			keyPairGenerator.initialize(DEFAULTKEYSIZE);
		}
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		Map<String, Object> keyPairMap = new HashMap<String,Object>(2);
		keyPairMap.put(PRIVATEKEY, privateKey);
		keyPairMap.put(PUBLICKEY, publicKey);
		return keyPairMap;
	}

	/**
	 * 获取私钥
	 * @param keyPairMap
	 * @return
	 */
	public static String getPrivateKey(Map<String,Object> keyPairMap){
		Key key =  (Key) keyPairMap.get(PRIVATEKEY);
		return Base64Utils.encodeToString(key.getEncoded());
	}

	/**
	 * 获取公钥
	 * @param keyPairMap
	 * @return
	 */
	public static String getPublicKey(Map<String,Object> keyPairMap){
		Key key =  (Key) keyPairMap.get(PUBLICKEY);
		return Base64Utils.encodeToString(key.getEncoded());
	}

	/**
	 * 使用私钥对数据进行签名
	 * @param data 需要签名的数据
	 * @param privateKey 私钥（使用BASE64进行编码）
	 * @return 返回加签名后的BASE64编码的字符串
	 * @throws Exception 
	 */
	public static String signByPrivateKey(String data, String privateKeyStr,String charsetName) throws Exception{

		byte[] privateKeyByte = Base64Utils.decodeFromString(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(privateKey);
		signature.update(data.getBytes(charsetName));

		return Base64Utils.encodeToString(signature.sign());
	}

	/**
	 * 使用公钥进行数字签名的校验
	 * @param data 需要进行验签的原始数据
	 * @param publicKey 公钥（BASE64编码字符串）
	 * @param sign 签名 （BASE64编码字符串）
	 * @return
	 * @throws Exception 
	 */
	public static boolean verifyByPublicKey(String data, String publicKeyStr, String sign,String charsetName) throws Exception{
		try{
			byte[] publicKeyByte = Base64Utils.decodeFromString(publicKeyStr);
			X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKeyByte);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(encodedKeySpec);
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initVerify(publicKey);
			signature.update(data.getBytes(charsetName));
			return signature.verify(Base64Utils.decodeFromString(sign));
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	 /** 
     * 公钥加密 
     *  
     * @param data 
     * @param publicKey 
     * @return 
     * @throws Exception 
     */  
    public static String encryptByPublicKey(String data, String publicKeyStr)  
            throws Exception {
    	if(null == data) return null;
    	byte[] dataB = data.getBytes("UTF-8");
    	byte[] publicKeyByte = Base64Utils.decodeFromString(publicKeyStr);
		X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKeyByte);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(encodedKeySpec);
		
        Cipher cipher = Cipher.getInstance(SPECIFIC_KEY_ALGORITHM); 
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        int inputLen = dataB.length;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        int offSet = 0;  
        byte[] cache;  
        int i = 0;  
        // 对数据分段加密  
        while (inputLen - offSet > 0) {  
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {  
                cache = cipher.doFinal(dataB, offSet, MAX_ENCRYPT_BLOCK);  
            } else {  
                cache = cipher.doFinal(dataB, offSet, inputLen - offSet);  
            }  
            out.write(cache, 0, cache.length);  
            i++;  
            offSet = i * MAX_ENCRYPT_BLOCK;  
        }  
        byte[] encryptedData = out.toByteArray();  
        out.close();  
        return Base64Utils.encodeToString(encryptedData);   
    } 
    
    /** 
     * 私钥解密 
     *  
     * @param data 
     * @param privateKey 
     * @return 
     * @throws Exception 
     */  
    public static String decryptByPrivateKey(String data, String privateKeyStr)  
            throws Exception {  
    	if(null == data) return null;
//    	byte[] dataB = data.getBytes("UTF-8");
    	byte[] dataB = Base64Utils.decodeFromString(data);
    	byte[] privateKeyByte = Base64Utils.decodeFromString(privateKeyStr);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		
        Cipher cipher = Cipher.getInstance(SPECIFIC_KEY_ALGORITHM);  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        //模长  
        int key_len = privateKey.getModulus().bitLength() / 8;  
//        byte[] bytes = data.getBytes();  
//        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);  
//        System.err.println(bcd.length);  
//        //如果密文长度大于模长则要分组解密  
//        String ming = "";  
//        byte[][] arrays = splitArray(bcd, key_len);  
//        for(byte[] arr : arrays){  
//            ming += new String(cipher.doFinal(arr));  
//        }  
        byte[] decryptedData = null;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            int dataLength = dataB.length;
            for (int i = 0; i < dataLength; i += key_len) {
                int decryptLength = dataLength - i < key_len ? dataLength - i
                    : key_len;
                byte[] doFinal = cipher.doFinal(dataB, i, decryptLength);
                bout.write(doFinal);
            }
            decryptedData = bout.toByteArray();
        } finally {
            if (bout != null) {
                bout.close();
            }
        }
        return Base64Utils.encodeToString(decryptedData);  
    }  
	
	/**
	 * 
	 * @param url  加密地址
	 * @param secret secretKey
	 * @return
	 * @throws Exception
	 */
	public static String getSign(String apiKey, String timeStrap , String data , String secret) throws Exception {
		
		Map<String,String> signMap = new HashMap<String,String>();
		
		signMap.put("appkey" , apiKey);
		signMap.put("data" , data);
		signMap.put("timestrap" , timeStrap);
		
		return Signing(signMap, secret);
	}

	
	
	private static String Signing(Map<String,String> paramsMap, String secret)
			throws Exception {
		
		// 第一步：检查参数是否已经排序
		String[] keys = paramsMap.keySet().toArray(new String[0]);
		Arrays.sort(keys);
		// 第二步：把所有参数名和参数值串在一起
		StringBuilder strBuilder = new StringBuilder();
		for (String key : keys) {
			String value = paramsMap.get(key);
			strBuilder.append(key).append(value);
		}
		// 第三步：使用MD5/HMAC加密
		byte[] bytes = encryptHMAC(strBuilder.toString(), secret);
		// 第四步：把二进制转化为大写的十六进制
		return byte2hex(bytes);
	}

	public static byte[] encryptHMAC(String data, String secret)
			throws IOException {
		byte[] bytes = null;
		try {
			SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"),
					"HmacMD5");
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			bytes = mac.doFinal(data.getBytes("UTF-8"));
		} catch (GeneralSecurityException gse) {
			//Log.e("TB_ERR", gse.getMessage());
		}
		return bytes;
	}

	public static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}
	
	
	
	 /** 
     * 拆分字符串 
     */  
    public static String[] splitString(String string, int len) {  
        int x = string.length() / len;  
        int y = string.length() % len;  
        int z = 0;  
        if (y != 0) {  
            z = 1;  
        }  
        String[] strings = new String[x + z];  
        String str = "";  
        for (int i=0; i<x+z; i++) {  
            if (i==x+z-1 && y!=0) {  
                str = string.substring(i*len, i*len+y);  
            }else{  
                str = string.substring(i*len, i*len+len);  
            }  
            strings[i] = str;  
        }  
        return strings;  
    }  
    /** 
     *拆分数组  
     */  
    public static byte[][] splitArray(byte[] data,int len){  
        int x = data.length / len;  
        int y = data.length % len;  
        int z = 0;  
        if(y!=0){  
            z = 1;  
        }  
        byte[][] arrays = new byte[x+z][];  
        byte[] arr;  
        for(int i=0; i<x+z; i++){  
            arr = new byte[len];  
            if(i==x+z-1 && y!=0){  
                System.arraycopy(data, i*len, arr, 0, y);  
            }else{  
                System.arraycopy(data, i*len, arr, 0, len);  
            }  
            arrays[i] = arr;  
        }  
        return arrays;  
    }  

    
    /** 
     * ASCII码转BCD码 
     *  
     */  
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {  
        byte[] bcd = new byte[asc_len / 2];  
        int j = 0;  
        for (int i = 0; i < (asc_len + 1) / 2; i++) {  
            bcd[i] = asc_to_bcd(ascii[j++]);  
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));  
        }  
        return bcd;  
    }  
    public static byte asc_to_bcd(byte asc) {  
        byte bcd;  
  
        if ((asc >= '0') && (asc <= '9'))  
            bcd = (byte) (asc - '0');  
        else if ((asc >= 'A') && (asc <= 'F'))  
            bcd = (byte) (asc - 'A' + 10);  
        else if ((asc >= 'a') && (asc <= 'f'))  
            bcd = (byte) (asc - 'a' + 10);  
        else  
            bcd = (byte) (asc - 48);  
        return bcd;  
    }  
    /** 
     * BCD转字符串 
     */  
    public static String bcd2Str(byte[] bytes) {  
        char temp[] = new char[bytes.length * 2], val;  
  
        for (int i = 0; i < bytes.length; i++) {  
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);  
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
  
            val = (char) (bytes[i] & 0x0f);  
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
        }  
        return new String(temp);  
    } 
	
	
    public static void main(String[] ags) {
		try {
//			Map<String, Object> keyPairMap = genKeyPair(false);
//			String publicKey = getPublicKey(keyPairMap);
//			String privateKey = getPrivateKey(keyPairMap);
			
			String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDajoIHVNjFzQppjxKRJDv2aXCOclZrzngNKBPR6BuZaaJqCRQu4I0ZYR8edNsEfVXHI6ubyEJtusjvqWyrRN7KpZMsAAHyxXhfpjy1xs80wDQLDJiR+9e75iSpwC4rfFwfmQAaYkaRo0Zw/JMUWNw7SEizpS40+QSnDpaKobttpwIDAQAB";
		    String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANqOggdU2MXNCmmPEpEkO/ZpcI5yVmvOeA0oE9HoG5lpomoJFC7gjRlhHx502wR9Vccjq5vIQm26yO+pbKtE3sqlkywAAfLFeF+mPLXGzzTANAsMmJH717vmJKnALit8XB+ZABpiRpGjRnD8kxRY3DtISLOlLjT5BKcOloqhu22nAgMBAAECgYAcn4NKAIOvTA4sCu/MkGlF+UMjdLf6wHyahIAsAnDy45HAv1WkWgZAp6aSr40rhlqgjPxhl5WTl13PfWnc+FURCQ9i3/f06pJETCUK0k3dIsMhwaZ2Bi0OrS6qo1ewzyhxX2V0+HcH5MPJ50nYNHMdzADXXIcZquGjnqujzr/I2QJBAPwCbeIANBXVcHnhLZsyn5nQuq4Z5OUg7ziqWjaimyxMUmwiCHwbasASBKk2IdrQNaa1GXKymM998tD7Zpph4kUCQQDeBHiUz3YDYi9gks0eYMwhk/IBQEJZ0i8/K2U4huBfDpAQ1fI5zT9pMcdLOl0c7DCbgLYajF3a++/Lc9/7q4T7AkB6ELMoxPB1ouzYHDn68opb9r51lhVI5qr4x4rq69g9nLpPpix18NHENvYLAC1lz3QtS4sIA3oBBwCWOS95l/ABAkEAmSH2VZW+rVjEmtTGe+SCB8r2idxbp35sriJtX47QJWBzB9m5iBrEWPdHoeCwpqlWYXtmn2sftVI+NFNJM1o7jwJBAPlGuFTKdyjx1vQW3DdiXpd7D+k8EcmKWhI/408aZrC1vWHGfcX+il6iOjFQkc7X+ymuuyD/Pk2q+STk5iO/4Jk=";

			//String content = "{\"orderNo\":\"26333331\",\"amount\":100,\"applyChannel\":\"pc\",\"applyName\":\"马大大\",\"cardType\":\"学生证\",\"cardno\":\"442728198011258855\",\"companyName\":\"阿里巴巴\",\"guaranteeWay\":\"1\",\"mobile\":\"18607558899\",\"nsrsbh\":\"440681574458790\",\"productId\":\"26\",\"remark\":\"订单备注内容\",\"repayType\":\"还款方式2\"}";
			String content = "{\"orderNo\":\"26333331\"}";
			//生成签名
			String sign = RSACrypt.signByPrivateKey(content, privateKey, "UTF-8");
			
			//验签
			System.out.println("验签结果--->"+verifyByPublicKey(content,publicKey,sign,"UTF-8"));
			//公钥加密
			String encryptToPublicKey = encryptByPublicKey(content , publicKey);
			System.out.println("加密----->"+encryptToPublicKey);
			
			//解密
			String decryptToPublicKey = decryptByPrivateKey(encryptToPublicKey , privateKey);
			System.out.println("解密----->"+new String(Base64Utils.decodeFromString(decryptToPublicKey)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
}
