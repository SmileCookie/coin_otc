package com.world.util.sign;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA安全编码组件
 *
 * FIXME suxinjie 加密解密可能需要分段进行
 *
 * RSA加密明文最大长度117字节，解密要求密文最大长度为128字节，所以在加密和解密的过程中需要分块进行。
 * RSA加密对明文的长度是有限制的，如果加密数据过大会抛出如下异常：

 * Exception in thread "main" javax.crypto.IllegalBlockSizeException: Data must not be longer than 117 bytes
 * at com.sun.crypto.provider.RSACipher.a(DashoA13*..)
 * at com.sun.crypto.provider.RSACipher.engineDoFinal(DashoA13*..)
 * at javax.crypto.Cipher.doFinal(DashoA13*..)

 * 
 * @author apple
 */
public abstract class RSACoder extends Coder {

	private static Logger log = Logger.getLogger(RSACoder.class);

	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";

	private static String publicKey; // renfei add 不需要像上面那样动态生成

	private static final String RSA_PUBLIC_PATH = "/id_rsa.pub";//RSA公钥路径

	/**
	 * 从配置文件获取公钥  文件中是base64后的字符串
	 * <p/>
	 * renfei
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getPublicKey() {
		return publicKey;
	}

	/**
	 * rsa 文件只加载一次，不需要一直读取
	 *
	 * renfei
	 */
	static {
		try {

			InputStream is = RSACoder.class.getResourceAsStream(RSA_PUBLIC_PATH);

			StringBuffer out = new StringBuffer();
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1; ) {
				out.append(new String(b, 0, n));
			}

			publicKey = out.toString();
		} catch (Exception e) {
			log.error("public rsa load failed!", e);
		}

	}

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		// 解密由base64编码的私钥
		byte[] keyBytes = decryptBASE64(privateKey);

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);

		return encryptBASE64(signature.sign());
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * 
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, String publicKey, String sign)
			throws Exception {

		// 解密由base64编码的公钥
		byte[] keyBytes = decryptBASE64(publicKey);

		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);

		// 验证签名是否正常
		return signature.verify(decryptBASE64(sign));
	}

	/**
	 * 解密<br>
	 * 用私钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPrivateKey(byte[] data, String key)
			throws Exception {
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 解密<br>
	 * 用公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key)
			throws Exception {
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPublicKey(byte[] data, String key)
			throws Exception {
		// 对公钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);
		
		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(data);
	}

	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String key)
			throws Exception {
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap)
			throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap)
			throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);
		
		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator
				.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);

		KeyPair keyPair = keyPairGen.generateKeyPair();

		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, Object> keyMap = new HashMap<String, Object>(2);

		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}

	/**
	 * 测试加解密
	 * @param args
	 * @throws Exception
     */
	public static void main(String[] args) throws Exception {
//		Map<String, Object> keyMap = initKey();
//
//		String publicKey = RSACoder.getPublicKey(keyMap);
//		String privateKey = RSACoder.getPrivateKey(keyMap);
//
//		log.info("公钥:  \n\r" +publicKey.replace(" ", "").replace("\n", "").replace("\r", ""));
//		log.info("私钥： \n\r" + privateKey);

		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCSAXI99VpE1TRwk/Z2nRidmCfu3NdLrMYniOZm0hPb57kIu2v4w7ynFBbSdXGusyGT+IqQ1sdpvO5QkqwSZX0moce4yMvIFrXd5g9jh3pRx8lmdT2enxewu/qjplJDxzbKK7Hokb9l2EoEwdoW0otVP/5dAfmGvXW1mQm71MKHRwIDAQAB";
		String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJIBcj31WkTVNHCT9nadGJ2YJ+7c10usxieI5mbSE9vnuQi7a/jDvKcUFtJ1ca6zIZP4ipDWx2m87lCSrBJlfSahx7jIy8gWtd3mD2OHelHHyWZ1PZ6fF7C7+qOmUkPHNsorseiRv2XYSgTB2hbSi1U//l0B+Ya9dbWZCbvUwodHAgMBAAECgYByt8owXmf3r2FNlyROnC5sHNI7fq922SC0jX8iiKtr3EzpTIGQaxL+X+1ynS6eakbWwbD2DpuOPBEvo56psa47jB/FkB7XPBiDwaycKGLf7YLI3E22aCGRWRy1z4ZQ40fm/uSTlOi1kSVvLf7aT4rJVSqTy5KAwdAoDIbZDHNSQQJBANUWBduutoTuEWvdAQmlAPGhMQLcGrzpuGlEBd66jBmKw5MMBuFBwgWZ9f/3vIrDFhg6Hx8elnA1cELPKM84gycCQQCvaP3X0YJeh65wM8H5oncXOJ42Oi/KYDwmy1zstGLpF814Hwp2sfAyWGXNVWC8id/PqotDOiwLFctrzQX6L+7hAkAfbl8w12We2AsD0RatGIS6H5++Hz9mbEdCZ8FB6FxwDBLrJtQA+BUphFtQBXfvM/WXX0Nl8LoaFRfZEvufDWPzAkEAmH8esNJkFOrArKtSqESmZHnEkkBj6/1eThve3aq1kxAugY/6+NYZjKttVeY0A7WZ7mDGUdvxDnIUN9Q2rhueAQJAANYbO1VP/APbRcvGd20ypTySpf9CserAngKsddc2+0AjN2nH0PapjmUMWXcAN7QhzkqhbLcKN/kZ4vDYHRcwUg==";

//		String jiemi = new String(decryptByPrivateKey(RSACoder.decryptBASE64(jiami), privateKey));
//		log.info("jiemi : " + jiemi);

	}
}
