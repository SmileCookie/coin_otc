package com.world.web.jwt;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.log4j.Logger;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.world.web.sso.session.Session;
import com.world.web.sso.session.SsoSessionManager;

public class JwtUtil {
	static Logger log = Logger.getLogger(JwtUtil.class.getName());
	
	/**
	 * @see <a
	 *      href="http://commons.apache.org/proper/commons-codec/archives/1.9/apidocs/index.html">Apache
	 *      Commons Codec 1.9 API</a>
	 */
	private static Base64 decoder;

	/**
	 * @see <a
	 *      href="http://download.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#Mac">Java
	 *      Standard Algorithm Name Documentation</a>
	 */
	private static Map<String, String> algorithms;

	
	public final static String default_secret = "abcdefGGxxabcTTE123**xt(!";
	private static JWTVerifier jwtVerifier = null;

    public static synchronized void init(){
    	Base64 base = new Base64(true);
    	byte[] secret = base.decode(default_secret);
    	
    	if(jwtVerifier == null){
    		jwtVerifier = new JWTVerifier(secret,"vip");
    	}
    	
    	decoder = new Base64(true);

		algorithms = new HashMap<String, String>();
		algorithms.put("none", "none");
		algorithms.put("HS256", "HmacSHA256");
		algorithms.put("HS384", "HmacSHA384");
		algorithms.put("HS512", "HmacSHA512");
    }
    
    public static String getJWTSign(String claims)
			throws NoSuchAlgorithmException, InvalidKeyException {
    	if(algorithms == null || decoder == null){
    		init();
    	}
    	
		String header = "{\"typ\":\"JWT\",\"alg\":\"HS256\"}";
		
		// String claims =
		// "{\"email\":\"root@1blau.com\",\"given_name\":\"Domain\",\"family_name\":\"Root\",\"iss\":\"https://1blau.auth0.com/\",\"aud\":\"MaiIcH7U4hhj3MbGbK3kwtQEbZtWQXvt\"}";
		//String claims = "{\"email\":\"lluis@superumm.com\",\"given_name\":\"Lluís\",\"family_name\":\"Faja\",\"iss\":\"https://superumm.auth0.com/\",\""+SsoSessionManager.JSESSIONID+"\":\"7tgUhwIoDK8Pw1oSIqnY8qRgPstINbZy\"}";

		String headerEncoded = decoder.encodeBase64URLSafeString(header
				.getBytes());
		String claimsEncoded = decoder.encodeBase64URLSafeString(claims
				.getBytes());

		Mac hmac = Mac.getInstance(algorithms.get("HS256"));
		hmac.init(new SecretKeySpec(decoder.decodeBase64(default_secret), algorithms
				.get("HS256")));
		byte[] sig = hmac.doFinal(new StringBuilder(headerEncoded).append(".")
				.append(claimsEncoded).toString().getBytes());

		log.info("JWT=" + headerEncoded + "." + claimsEncoded + "."
				+ decoder.encodeBase64URLSafeString(sig));
		return headerEncoded + "." + claimsEncoded + "."
				+ decoder.encodeBase64URLSafeString(sig);
	}
    
    public static Session getSession(HttpServletRequest httpRequest){
		Map<String,Object> tokens = getDecodedsData(httpRequest);
		
		if(tokens != null){
			log.info("当前解析出来的token：" + tokens);
			String sessionId = (String)tokens.get(SsoSessionManager.JSESSIONID);
			
			log.info("当前解析出来的token-sessionID：" + sessionId);
			
			if(sessionId != null && sessionId.length() > 0){
				return new Session(sessionId, true);
			}
		}
		return null;
    }

    private static Map<String,Object> getDecodedsData(HttpServletRequest httpRequest){
    	String token = getToken(httpRequest);
    	
    	if(token != null){
    		return getDecodedsData(token);
    	}
    	return null;
    }
    
    private static Map<String,Object> getDecodedsData(String token){
    	if(jwtVerifier == null){
    		init();
    	}
    	try {
			return jwtVerifier.verify(token);
		} catch (InvalidKeyException e) {
			log.info("InvalidKeyException:" + token + "," + e.toString(), e);
		} catch (NoSuchAlgorithmException e) {
			log.info("InvalidKeyException:" + token + "," + e.toString(), e);
		} catch (IllegalStateException e) {
			log.info("InvalidKeyException:" + token + "," + e.toString(), e);
		} catch (SignatureException e) {
			log.info("InvalidKeyException:" + token + "," + e.toString(), e);
		} catch (IOException e) {
			log.info("InvalidKeyException:" + token + "," + e.toString(), e);
		} catch (JWTVerifyException e) {
			log.info("InvalidKeyException:" + token + "," + e.toString(), e);
		}
    	return null;
    }
    
   public static Map<String,Object> getDecodedsInfo(String token){
	   return getDecodedsData(token);
   }
    
	
	/***
	 * 获取请求token
	 * @param httpRequest
	 * @return
	 * @throws ServletException
	 */
    private static String getToken(HttpServletRequest httpRequest) {
    	String token = null;
        try {
			final String authorizationHeader = httpRequest.getHeader("authorization");

			if (authorizationHeader == null) {
			    //throw new ServletException("Unauthorized: No Authorization header was found");
				log.info("Unauthorized: No Authorization header was found");
				return token;
			}

			String[] parts = authorizationHeader.split(" ");
			if (parts.length != 2) {
			    //throw new ServletException("Unauthorized: Format is Authorization: Bearer [token]");
			    log.info("Unauthorized: Format is Authorization: Bearer [token]");
			    return token;
			}

			String scheme = parts[0];
			String credentials = parts[1];

			Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
			if (pattern.matcher(scheme).matches()) {
			    token = credentials;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
        return token;
    }
}
