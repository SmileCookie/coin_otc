package com.world.web.sso.rsa;

import java.util.Map;

import com.world.cache.Cache;
import com.world.util.sign.RSACoder;
import com.world.web.Pages;
import com.world.web.sso.session.SsoSessionManager;
import org.apache.log4j.Logger;

public class RsaLoginUtil {

	private final static Logger log = Logger.getLogger(RsaLoginUtil.class.getName());

	final static int rsaTime = 10 * 60 * 60;
	public final static String rsaSessionPre = "Rsa_";

	public static RsaUser getRsaUser(Pages p){
		log.info("获取rsakey前的sessionid:" + p.sessionId);
		SsoSessionManager.initSession(p);
		RsaUser rsaUser = null;
		if(p.session != null){
			rsaUser = Cache.T(rsaSessionPre + p.sessionId);
			if(rsaUser == null){
				rsaUser = addRsaUser(p);
			}
		}
		return rsaUser;
	}
	
	public static boolean removeRsaUser(Pages p) {
		SsoSessionManager.initSession(p);
		if(p.session != null){
			return Cache.Delete(rsaSessionPre + p.sessionId);
		}
		return false;
	}
	
	private static RsaUser addRsaUser(Pages p){
		Map<String, Object> keyMap = null;
		try {
			keyMap = RSACoder.initKey();
			
			String publicKey = RSACoder.getPublicKey(keyMap);
			String privateKey = RSACoder.getPrivateKey(keyMap);
			
			log.info(p.sessionId +  " 新生成公钥: " + publicKey);
            log.info(p.sessionId +  " 新生成私钥: " + privateKey);
			
			RsaUser rsaUser = new RsaUser();
			rsaUser.setPubKey(publicKey.replace(" ", "").replace("\n", "").replace("\r", ""));
			rsaUser.setPriKey(privateKey);
			rsaUser.setStimes(System.currentTimeMillis());
			Cache.SetObj(rsaSessionPre + p.sessionId, rsaUser,rsaTime);
			Cache.SetObj("Rsa_Private_Key_" + p.sessionId, privateKey,rsaTime);
			return rsaUser;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

		return null;
	}
}
