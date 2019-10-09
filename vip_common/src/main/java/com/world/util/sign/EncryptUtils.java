package com.world.util.sign;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * 加密公用类
 * 
 * aes加密类，输出以base64转化的加密密文
 * 
 * @author renfei
 *
 */

public class EncryptUtils {

	private static Logger log = Logger.getLogger(EncryptUtils.class.getName());

	private static final int PBKDF2_KEY_LENGTH = 64 * 8; // PBKDF2加密长度
	private static final int PBKDF2_IT_COUNT = 1000; // PBKDF2加密长度

	public static String encryptAES(String seed, String clearText) {
		// Log.d(TAG, "加密前的seed=" + seed + ",内容为:" + clearText);
		byte[] result = null;
		try {
			byte[] rawkey = getRawKeyAES(seed.getBytes());
			result = encryptAES(rawkey, clearText.getBytes("UTF-8"));
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		String content = toHex(result);
		// Log.d(TAG, "加密后的内容为:" + content);
		return content;

	}

	public static String decryptAES(String seed, String encrypted) {
		// Log.d(TAG, "解密前的seed=" + seed + ",内容为:" + encrypted);
		byte[] rawKey;

		try {
			rawKey = getRawKeyAES(seed.getBytes());
			byte[] enc = toByte(encrypted);
			byte[] result = decryptAES(rawKey, enc);
			String coentn = new String(result,"UTF-8");
			// Log.d(TAG, "解密后的内容为:" + coentn);
			return coentn;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return null;
		}

	}

	private static byte[] getRawKeyAES(byte[] seed) throws Exception {
		// 这里不要使用sha1进行对key的加密，跟客户端统一为生成byte 16字节，不够以0填充 renfei
		// KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		// sr.setSeed(seed);
		// kgen.init(128, sr);
		// SecretKey sKey = kgen.generateKey();
		// byte[] raw = sKey.getEncoded();
		//
		// return raw;

		byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）,不够以0填充
		for (int i = 0; i < seed.length && i < arrB.length; i++) {
			arrB[i] = seed[i];
		}

		return arrB;
	}

	private static byte[] encryptAES(byte[] raw, byte[] clear) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		// Cipher cipher = Cipher.getInstance("AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());// 跟客户端统一偏移量为0102030405060708
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
		byte[] encrypted = cipher.doFinal(clear);
		return encrypted;
	}

	private static byte[] decryptAES(byte[] raw, byte[] encrypted) throws Exception {
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		// Cipher cipher = Cipher.getInstance("AES");
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());// 跟客户端统一偏移量为0102030405060708
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		byte[] decrypted = cipher.doFinal(encrypted);
		return decrypted;
	}

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}

	public static String fromHex(String hex) {
		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		// int len = hexString.length() / 2;
		// byte[] result = new byte[len];
		// for (int i = 0; i < len; i++)
		// result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
		// 16).byteValue();
		// return result;
		return Base64.decodeBase64(hexString);
	}

	public static String toHex(byte[] buf) {
		// if (buf == null)
		// return "";
		// StringBuffer result = new StringBuffer(2 * buf.length);
		// for (int i = 0; i < buf.length; i++) {
		// appendHex(result, buf[i]);
		// }
		// return result.toString();
		return Base64.encodeBase64String(buf);
	}

	/**
	 * PBKDF2加密
	 * @param salt
	 * @param msg
	 * @return
	 */
	public static String encryptPBKDF2(byte[] salt,String msg) {
		try {
			int iterations = PBKDF2_IT_COUNT;
			char[] chars = msg.toCharArray();

			PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, PBKDF2_KEY_LENGTH);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] hash = skf.generateSecret(spec).getEncoded();

			return toHex(hash);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return null;
	}

	/**
	 * 盐值
	 * 
	 * @return
	 */
	public static byte[] getSalt() {
		SecureRandom sr;
		byte[] salt = new byte[16];
		try {
			sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
			sr.nextBytes(salt);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return salt;
	}
	
	public static void main(String[] args) throws Exception {
		byte[] salt = getSalt();
		log.info(new String(salt,"UTF-8"));
		log.info(encryptPBKDF2(salt,"ddd"));

	}
}