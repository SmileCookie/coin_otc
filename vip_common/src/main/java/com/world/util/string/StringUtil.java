package com.world.util.string;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

public class StringUtil {

	private final static Logger log = Logger.getLogger(StringUtil.class.getName());

	/****
	 * 判断当前字符串是否又存在的意义
	 * http://img12.360buyimg.com/n3/g8/M03/04/1C/rBEHZ1BAH8gIAAAAAAFKTseBNBsAAA9UwAPeyIAAUpm219.jpg
	 * http://img1.dengshicheng.com/up/0/s/       323A676F6F64733A30_50a202167de6faa45649ada5.jpg
	 * 
	 * @param s
	 * @return
	 */
	public static boolean exist(String s) {
		if (s == null || s.trim().length() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public static String getEnc(String str) {
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
		return str;
	}

	// 转化字符串为十六进制编码

	public static String toHexString(String s)

	{

		String str = "";

		for (int i = 0; i < s.length(); i++) {

			int ch = (int) s.charAt(i);

			String s4 = Integer.toHexString(ch);

			str = str + s4;

		}

		return "0x" + str;// 0x表示十六进制

	}

	// 转换十六进制编码为字符串

	public static String toStringHex(String s) {

		if ("0x".equals(s.substring(0, 2)))

		{

			s = s.substring(2);

		}

		byte[] baKeyword = new byte[s.length() / 2];

		for (int i = 0; i < baKeyword.length; i++)

		{

			try {

				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
						i * 2, i * 2 + 2), 16));

			}

			catch (Exception e) {

				log.error(e.toString(), e);

			}

		}

		try

		{

			s = new String(baKeyword, "utf-8");// UTF-16le:Not

		}

		catch (Exception e1) {

			log.error(e1.toString(), e1);

		}

		return s;

	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	// 下面做个测试

	public static String getHexString(String s) {
		byte[] b = null;
		try {
			b = s.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.toString(), e);
		}
		String h = "";
		for (int i = 0; i < b.length; i++) {

			String hex = Integer.toHexString(b[i] & 0xFF);

			if (hex.length() == 1) {

				hex = "0" + hex;

			}
			h += hex.toUpperCase();
		}
		return h;
	}

	public static String printHexString(byte[] b) {

		String h = "";
		for (int i = 0; i < b.length; i++) {

			String hex = Integer.toHexString(b[i] & 0xFF);

			if (hex.length() == 1) {

				hex = "0" + hex;

			}
			h += hex.toUpperCase();
		}
		return h;
	}
	
	//形如{"尺码":"43","颜色":"黑色"}
	public static String[] getValsByJsonStr(String attrs){
		JSONObject jo = JSONObject.fromObject(attrs);
		String[] ss = new String[jo.entrySet().size()];
		
		int j = 0;
		for(Iterator<Entry<String, String>> i = jo.entrySet().iterator(); i.hasNext();){
			Entry<String, String> ciMap = i.next();
			
			ss[j] = ciMap.getValue();
			j++;
		}
		return ss;
	}
	/*****
	 * {1302:{"尺码":"165/85(S)","颜色":"浅白"}}
	 * 获取键值形如 上
	 */
	public static String getJsonKey(String s){
		String key = null;
		JSONObject jo = JSONObject.fromObject(s);
		
		key = jo.getString("id");
//		Iterator<String> its = jo.keys();
//		if(its != null && its.hasNext()){
//			key = its.next();
//		}
		return key;
	}
	
	public static void main(String[] args) throws Exception
	{

		//String str = "1402-{\"尺码\":\"43\",\"颜色\":\"黑色\"}";
		//String bs = getHexString(str);
		
		//log.info(bs);
	  //313430322D7BE9A29CE889B23AE9BB91E889B22CE5A597E9A490E8AEBEE7BDAE3AE6A087E58786E9858DE7BDAE7D
//bs = "313430322D7BD1D5C9AB3ABADAC9AB2CCCD7B2CDC9E8D6C33AB1EAD7BCC5E4D6C37D";
		//log.info(toStringHex("7B22696422203A2031313032202C2022617474727322203A20227B22E9A29CE889B2223A22E7BAA2E889B2222C22E5B0BAE5AFB8223A223339227D227D"));
	
		//log.info(getHexString("test++test!)"));
		
		String a = "测试++！）";
//		byte[] unicodeb= a.getBytes("unicode");  
//	    String s_unidode = new String(unicodeb,"unicode");
//		log.info("---"+s_unidode);
//		log.info(getHexString(s_unidode));
		
		
		String b = URLEncoder.encode(a, "UTF-8");
		
		log.info(getHexString(b));
		
	}
}
