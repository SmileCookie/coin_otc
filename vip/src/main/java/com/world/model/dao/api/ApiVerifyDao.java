package com.world.model.dao.api;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.world.model.entity.api.ApiKey;

public class ApiVerifyDao {
	
	/**
	 * Signature
	 * @return
	 */
	public static SignatureCode signature(ApiKey key, String userName, String accesskey, String nonce, String signature){
		if(key == null){
			//no such user
			return SignatureCode.code2;
		}
		String data = userName + accesskey + nonce;
		String newsignature = hmacSign(data, key.getSecretkey());
		if(!newsignature.equals(signature)){
			//can not verify through
			return SignatureCode.code4;
		}
		//success
		return SignatureCode.code1;
	}
	
	//JAVA
	public static String hmacSign(String datas, String key) {
		byte k_ipad[] = new byte[64];
		byte k_opad[] = new byte[64];
		byte keyb[];
		byte value[];
		try {
			keyb = key.getBytes("UTF-8");
			value = datas.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			keyb = key.getBytes();
			value = datas.getBytes();
		}
		Arrays.fill(k_ipad, keyb.length, 64, (byte) 54);
		Arrays.fill(k_opad, keyb.length, 64, (byte) 92);
		for (int i = 0; i < keyb.length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");//"MD5"
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
		if (dg == null)
			return null;
		StringBuffer output = new StringBuffer(dg.length * 2);
		for (int i = 0; i < dg.length; i++) {
			int current = dg[i] & 0xff;
			if (current < 16)
				output.append("0");
			output.append(Integer.toString(current, 16));
		}
		return output.toString();
	}
	

//  PHP
//	function hmacSign($Datas,$Key){    
//	    return hash_hmac('md5',$Datas,$Key);
//	}

//Python
/*	import hashlib,struct

	def __hmacSign(self, datas, key):
        keyb   = struct.pack("%ds" % len(key), key)
        value  = struct.pack("%ds" % len(datas), datas)
        k_ipad = self.__doXOr(keyb, 0x36)
        k_opad = self.__doXOr(keyb, 0x5c)
        k_ipad = self.__fill(k_ipad, 64, 54)
        k_opad = self.__fill(k_opad, 64, 92)
        m = hashlib.md5()
        m.update(k_ipad)
        m.update(value)
        dg = m.digest()
        
        m = hashlib.md5()
        m.update(k_opad)
        subStr = dg[0:16]
        m.update(subStr)
        dg = m.hexdigest()
        return dg
     def __fill(self, value, lenght, fillByte):
        if len(value) >= lenght:
            return value
        else:
            fillSize = lenght - len(value)
        return value + chr(fillByte) * fillSize

    def __doXOr(self, s, value):
        slist = list(s)
        for index in xrange(len(slist)):
            slist[index] = chr(ord(slist[index]) ^ value)
        return "".join(slist)
        */	

//C#
/*	public static String hmacSign(String datas, String key) {
		byte[] k_ipad = new byte[64];
		byte[] k_opad = new byte[64];
		byte[] keyb;
		byte[] value;
		Encoding coding = Encoding.GetEncoding("UTF-8");
		try {
            keyb = coding.GetBytes(key);
            value = coding.GetBytes(datas);
		} catch (Exception e) {
			keyb = null;
            value =null;
            //throw;
		}

		for (int i = keyb.Length; i < 64; i++){
            k_ipad[i] = (byte)54;
            k_opad[i] = (byte)92;
		}
             
		for (int i = 0; i < keyb.Length; i++) {
			k_ipad[i] = (byte) (keyb[i] ^ 0x36);
			k_opad[i] = (byte) (keyb[i] ^ 0x5c);
		}

		byte[] sMd5_1 = MakeMD5(k_ipad.Concat(value).ToArray());
      	byte[] dg = MakeMD5(k_opad.Concat(sMd5_1).ToArray());
     
		if (dg == null)
			return null;
		StringBuilder output = new StringBuilder(dg.Length * 2);
		for (int i = 0; i < dg.Length; i++) {
			int current = dg[i] & 0xff;
			if (current < 16)
				output.Append('0');
			output.Append( current.ToString("x"));
		}

		return output.ToString();	
	}
	public static byte[] MakeMD5(byte[] original){
	        MD5CryptoServiceProvider hashmd5 = new MD5CryptoServiceProvider();
	        byte[] keyhash = hashmd5.ComputeHash(original);
	        hashmd5 = null;
	        return keyhash;
   }*/

	
}