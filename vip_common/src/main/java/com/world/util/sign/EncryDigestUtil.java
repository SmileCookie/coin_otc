package com.world.util.sign;

import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class EncryDigestUtil {

	private final static Logger log = Logger.getLogger(EncryDigestUtil.class.getName());

	private static String encodingCharset = "UTF-8";
	
	
	public static String hmacSign(String aValue, String aKey) {
		return hmacSign(aValue, aKey , "MD5");
	}
	
	/**
	 * 生成签名消息
	 * @param aValue  要签名的字符串
	 * @param aKey  签名密钥
	 * @return
	 */
	public static String hmacSign(String aValue, String aKey,String jiami) {
		byte k_ipad[] = new byte[64];
		byte k_opad[] = new byte[64];
		byte keyb[];
		byte value[];
		try {
			keyb = aKey.getBytes(encodingCharset);
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			keyb = aKey.getBytes();
			value = aValue.getBytes();
		}

		Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(jiami);//"MD5"
		} catch (NoSuchAlgorithmException e) {

			return null;
		}
		md.update(k_ipad);
		md.update(value);
		byte dg[] = md.digest();
		md.reset();
		md.update(k_opad);
		md.update(dg, 0, 16);
		dg = md.digest();
		return toHex(dg);
	}
	
	/***
	 * 对应python里面的hmac.new(API_SECRET, msg=message, digestmod=hashlib.sha256).hexdigest().upper() 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String hamacSha256(String key , String value){
		String result = null;
		  byte[] keyBytes = key.getBytes();
		  SecretKeySpec localMac = new SecretKeySpec(keyBytes, "HmacSHA256");
		  try {
		    Mac mac = Mac.getInstance("HmacSHA256");
		    mac.init(localMac);
		    byte[] arrayOfByte = mac.doFinal(value.getBytes());
		    BigInteger localBigInteger = new BigInteger(1,
		        arrayOfByte);
		    result = String.format("%0" + (arrayOfByte.length << 1) + "x",
		        new Object[] { localBigInteger });
		    
		  } catch (InvalidKeyException e) {
		    log.error(e.toString(), e);
		  } catch (NoSuchAlgorithmException e) {
		    log.error(e.toString(), e);
		  } catch (IllegalStateException e) {
		    log.error(e.toString(), e);
		  }
		  
		  return result;
	}

	public static String toHex(byte input[]) {
		if (input == null)
			return null;
		StringBuffer output = new StringBuffer(input.length * 2);
		for (int i = 0; i < input.length; i++) {
			int current = input[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}

		return output.toString();
	}

	/**
	 * 
	 * @param args
	 * @param key
	 * @return
	 */
	public static String getHmac(String[] args, String key) {
		if (args == null || args.length == 0) {
			return (null);
		}
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			str.append(args[i]);
		}
		return (hmacSign(str.toString(), key));
	}

	/**
	 * SHA加密
	 * @param aValue
	 * @return
	 */
	public static String digest(String aValue , String algorithm) {
		aValue = aValue.trim();
		byte value[];
		try {
			value = aValue.getBytes(encodingCharset);
		} catch (UnsupportedEncodingException e) {
			value = aValue.getBytes();
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			log.error(e.toString(), e);
			return null;
		}
		return toHex(md.digest(value));

	}
	
	/***
	 * sha-1散列加密
	 * @param aValue
	 * @return
	 */
	public static String digest(String aValue) {
			return digest(aValue, "SHA");
	}
	/***
	 * sha-256散列加密
	 * @param aValue
	 * @return
	 */
	public static String digestSha256(String aValue) {
		return digest(aValue, "SHA-256");
	}
	
	public static void main(String[] args) {
		log.info(hmacSign("accesskey=2bf8eccb-56f5-4c17-8e1c-e54adc5f00ba&cashAmount=0.03&currency=eth&payFee=0.00000000&receiveAddress=0xc1d6468fa242ecf7255fac7a36a7cc034e81f4da&remark= ", "a841b0468d0a26b24f440cb048960ce36cd4e2f5"));
	}
}
