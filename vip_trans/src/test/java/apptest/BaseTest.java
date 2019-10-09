package apptest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.util.sign.RSACoder;
import com.world.web.sso.session.CodeInfo;
import com.world.web.sso.session.SessionInfo;

import apptest.v1.CancelEntrustTest;
import apptest.v1.DoEntrustTest;
import apptest.v1.EntrustRecordTest;
import junit.framework.TestCase;



/**
 * 
 * @author jiahua
 *
 */
public class BaseTest extends TestCase{
	protected static Logger log = Logger.getLogger(BaseTest.class);
	static HttpClient httpclient = new HttpClient();
	protected String clientSessionPre = "s_p_";
	/**
	 * 环境： 0:本地 1:测试 2:正式
	 */
	protected String environment;
	protected String uri = "";
	protected String vipUri = "";
	protected static String token = "";	//登录时赋值
	protected String userId = "";
	protected String userName = "";
	protected String password = "";
	protected String safePwd = "";
	protected String mobileNumber = "";
	protected String countryCode = "+86";
	protected String email = "";

	protected String pubKey = "";
	protected String priKey = "";

	protected String key = "";
	protected String secret = "";
	protected String googleSecret = "";
	
	protected long startTime = System.currentTimeMillis();
	protected long endTime = System.currentTimeMillis();
	

	public void setUp() throws Exception {
		environment = TestConfig.getValue("environment");
		if (environment.equals("0")) {
			uri = TestConfig.getValue("localUri");
			vipUri =TestConfig.getValue("localvipUri");
			userId =TestConfig.getValue("localUserId");
			userName =TestConfig.getValue("localUserName");
			password =TestConfig.getValue("localPassword");
			safePwd =TestConfig.getValue("localSafePwd");
			mobileNumber =TestConfig.getValue("localMobileNumber");
			pubKey =TestConfig.getValue("pubKey");
			priKey =TestConfig.getValue("priKey");
			key =TestConfig.getValue("localkey");
			secret =TestConfig.getValue("localsecret");
			googleSecret =TestConfig.getValue("localgoogleSecret");
		} else if (environment.equals("1")) {
			uri =TestConfig.getValue("tturi");
			vipUri =TestConfig.getValue("ttvipUri");
			userId =TestConfig.getValue("ttUserId");
			userName =TestConfig.getValue("ttUserName");
			password =TestConfig.getValue("ttPassword");
			safePwd =TestConfig.getValue("ttSafePwd");
			mobileNumber =TestConfig.getValue("ttMobileNumber");
			pubKey =TestConfig.getValue("pubKey");
			priKey =TestConfig.getValue("priKey");
			key =TestConfig.getValue("ttkey");
			secret =TestConfig.getValue("ttsecret");
			googleSecret =TestConfig.getValue("ttgoogleSecret");
		} else if (environment.equals("2")) {
			uri =TestConfig.getValue("finalUri");
			vipUri =TestConfig.getValue("finalvipUri");
			userId =TestConfig.getValue("finalUserId");
			userName =TestConfig.getValue("finalUserName");
			password =TestConfig.getValue("finalPassword");
			safePwd =TestConfig.getValue("finalSafePwd");
			mobileNumber =TestConfig.getValue("finalMobileNumber");
			pubKey =TestConfig.getValue("pubKey");
			priKey =TestConfig.getValue("priKey");
			key =TestConfig.getValue("finalkey");
			secret =TestConfig.getValue("finalsecret");
			googleSecret =TestConfig.getValue("finalgoogleSecret");
		}


	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSafePwd(String safePwd) {
		this.safePwd = safePwd;
	}
	
	/**
	 * 调用http接口和检查结果
	 * @param test	检查条件
	 * <pre>
	 * 支持比较符：==,!=,>,<,>=,<=,^=,$=,*=
	 * 使用示例：resMsg.code==1000，左边为从json结果中获取数据的路径，右边为期望值
	 * 
	 * 如果比较数值，需要用[xxx]框住期望值，支持比较符：==,!=,>,<,>=,<=
	 * 使用示例：data.unitPrice>[4000.12]
	 * 
	 * 如果获取数组里的值，json路径中数组字段名后加“[xx]”这样的下标
	 * 使用示例：data.entrustTrades[0].entrustId==20161215122101242
	 * 
	 * 支持访问数组通用方法（无参方法）
	 * 使用示例：data.entrustTrades[].size>0
	 * </pre>
	 * @param method	http请求方法
	 */
	protected String doCheck(String test, PostMethod method) {
		try {
			String json = executeHttp(method);
			
			String[] tests = StringUtils.split(test, "|");
			for (String t : tests) {
				boolean heat = false;
				for (Operator operator : Operator.values()) {
					if (t.contains(operator.getOpr())) {
						String[] split = StringUtils.splitByWholeSeparator(t, operator.getOpr());
						String key = split[0];
						String value = split[1];
						
						String result = getStringFromJson(json,key);
						boolean check = operator.check(result, value );
						Assert.assertTrue(operator.getMessage(),check);
						
						heat = true;
						break;
					}
				}
				if (!heat) {
					Assert.fail("未找到比较符");
				}
			}
			
			return json;
		} catch (Exception e) {
			log.error(e.toString(), e);
			Assert.fail(e.getMessage());
			return null;
		}
	}

	protected static String executeHttp(PostMethod method) throws IOException, HttpException {
		httpclient.executeMethod(method);
		// 打印服务器返回的状态
		log.info(method.getStatusLine());
		InputStream stream = method.getResponseBodyAsStream();
		String str = convertStreamToString(stream);
		// 打印返回的信息
		log.info(str);
		return str;
	}
	
	protected static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			log.error(e.toString(), e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				log.error(e.toString(), e);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 从json字符串中获取特定的值
	 * @param json json String
	 * @param path 
	 * <pre>
	 * 访问属性， 例：resMsg.code
	 * 支持访问数组里的值，json路径中数组字段名后加“[xx]”这样的下标
	 * 使用示例：data.entrustTrades[0].entrustId
	 * 
	 * 支持访问数组通用方法（无参方法）
	 * 使用示例：data.entrustTrades[].size
	 * </pre>
	 * @return
	 */
	public static String getStringFromJson(String json, String path) throws Exception{
		String rs = json;
		String[] paths = StringUtils.split(path, ".");
		int length = paths.length;
		if (length > 0) {
			for (int i = 0; i < paths.length; i++) {
				String _path = paths[i];
				if (_path.matches(".+\\[\\d+\\]")) {	//用索引访问一个数组
					JSONArray jsonArray = JSONObject.parseObject(rs).getJSONArray(_path.replaceAll("\\[\\d+\\]", ""));
					String index = _path.substring(_path.indexOf("[")+1, _path.indexOf("]"));
					rs = jsonArray.getString(Integer.valueOf(index));
				}else if (_path.matches(".+\\[\\]")) {	//访问整合数组，如数组的size，array[].size
					JSONArray jsonArray = JSONObject.parseObject(rs).getJSONArray(_path.replaceAll("\\[\\]", ""));
					if(paths.length>=i+2){
						String arrayMethod = paths[i+1];
						Object invoke = jsonArray.getClass().getMethod(arrayMethod).invoke(jsonArray);
						rs = invoke.toString();
						break;
					}else{
						rs = "";
					}

				}else {
					rs = JSONObject.parseObject(rs).getString(_path);
				}
			}
		}

		return rs;
	}
	
	/**
	 * 
	 * @param json json String
	 * @param path 例：resMsg.code
	 * @return
	 */
	public static JSONArray getArrayFromJson(String json, String path) {
		String rs = json;
		JSONArray arrayResult = null ;
		String[] paths = StringUtils.split(path, ".");
		int length = paths.length;
		if (length > 0) {
			for (int i = 0; i < paths.length; i++) {
				if(i!=paths.length-1){
					rs = JSONObject.parseObject(rs).getString(paths[i]);
				}else{
					arrayResult = JSONObject.parseObject(rs).getJSONArray(paths[i]);
				}
			}
		}
		
		return arrayResult;
	}



	protected void printTitle(String title) {
		this.startTime = System.currentTimeMillis();
		log.info(StringUtils.center(" | " + title + " | ", 100, "="));
	}

	protected void printSubTitle(String subtitle) {
		this.startTime = System.currentTimeMillis();
		log.info(StringUtils.center(StringUtils.center(subtitle, 50, "-"), 100, ""));
	}
	
	protected void printSpendTime() {
		this.endTime = System.currentTimeMillis();
		log.info(StringUtils.center(StringUtils.center("耗时：" + (endTime-startTime) + " ms" , 50, "-"), 100, ""));
	}

	public BaseTest() {
		super();
	}

	/**
	 * 默认用户：类指定的userId
	 */
	public String getGoogleCode()
			throws NoSuchAlgorithmException, InvalidKeyException {
//		User user = new UserDao().get(userId);
//		UserContact uc = user.getUserContact();
		Base32 codec = new Base32();
		byte[] key = codec.decode(googleSecret);

		long t = (System.currentTimeMillis() / 1000L) / 30L;
		byte[] data = new byte[8];
		long value = t ;
		for (int i = 8; i-- > 0; value >>>= 8) {
			data[i] = (byte) value;
		}

		SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(signKey);
		byte[] hash = mac.doFinal(data);

		int offset = hash[20 - 1] & 0xF;

		long truncatedHash = 0;
		for (int i = 0; i < 4; ++i) {
			truncatedHash <<= 8;
			truncatedHash |= (hash[offset + i] & 0xFF);
		}

		truncatedHash &= 0x7FFFFFFF;
		truncatedHash %= 1000000;

		return String.valueOf(truncatedHash);
	}
	
	/**
	 * 默认用户：类指定的userName,默认版本：V1_1
	 * @param type
	 * 验证类型：<br/>
			1：注册<br/>
			2：找回密码 <br/>
			3 ：其他 <br/>
			4：提现人民币 <br/>
			5：提现比特币 <br/>
			6：提现莱特币 <br/>
			7：提现ETH <br/>
			8：设置谷歌验证码 <br/>
			9：安全设置（修改资金密码、关闭Google、关闭支付短信等等操作）<br/>
			10：异地登录短信验证 <br/>
			11：提现ETC<br/>
	 */
	public String getMobileCode(int type) throws Exception {
		return this.getMobileCode(type, "V1_1");
	}
	/**
	 * 默认用户：类指定的userName,可选版本：V1_5,V1_6,V1_7,V1_1
	 * @param type
	 * 验证类型：<br/>
			1：注册<br/>
			2：找回密码 <br/>
			3 ：其他 <br/>
			4：提现人民币 <br/>
			5：提现比特币 <br/>
			6：提现莱特币 <br/>
			7：提现ETH <br/>
			8：设置谷歌验证码 <br/>
			9：安全设置（修改资金密码、关闭Google、关闭支付短信等等操作）<br/>
			10：异地登录短信验证 <br/>
			11：提现ETC<br/>
		@param version 可选版本：V1_5,V1_6,V1_7,V1_1
	 */
	public String getMobileCode(int type, String version) throws Exception {
		return this.getMobileCode(type, version, mobileNumber);
	}
	
	/**
	 * 指定用户,可选版本：V1_5,V1_6,V1_7,V1_1
	 * @param type
	 * 验证类型：<br/>
			1：注册<br/>
			2：找回密码 <br/>
			3 ：其他 <br/>
			4：提现人民币 <br/>
			5：提现比特币 <br/>
			6：提现莱特币 <br/>
			7：提现ETH <br/>
			8：设置谷歌验证码 <br/>
			9：安全设置（修改资金密码、关闭Google、关闭支付短信等等操作）<br/>
			10：异地登录短信验证 <br/>
			11：提现ETC<br/>
	 * @param version 可选版本：V1_5,V1_6,V1_7,V1_1
	 */
	public String getMobileCode(int type, String version, String mobileNumber) throws Exception {
		
		doLoginIfNot();
		
		printSubTitle("getMobileCode");
		
		PostMethod method = new PostMethod(vipUri + version + "/userSendCode");
		addTokenUserId(method);
		
		method.addParameter("type", type + "");
		if(version.equalsIgnoreCase("V1_1")){
			addConfig(method);
			method.addParameter("sign",getSign(method.getParameters()));
		}
		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		String mCode = "";
		try {
			String json = executeHttp(method);
			String code = getStringFromJson(json, version.equalsIgnoreCase("V1_5") ? "code" : "resMsg.code");
			String message = getStringFromJson(json, version.equalsIgnoreCase("V1_5") ? "message" : "resMsg.message");
			
			if ("1000".equals(code)) {
				// 获取验证码
				SessionInfo ssi = getSessionInfo(this.countryCode + mobileNumber);
				String key;
				if (type == 3) {
					key = "其他";
				} else if (type == 9) {
					key = "SAFE验证码";
				} else if (type == 10) {// 异地登录短信验证
					key = "异地登录";
				} else {
					key = "验证码";
				}
				CodeInfo ci = ssi.codeInfos.get(key);
				mCode = ci.lastCode;
			} else {
				mCode = message;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return mCode;
	}
	
	protected SessionInfo getSessionInfo(String sessionId) {
		sessionId = sessionId.replace(" ", "");
		String sessionInfoId = clientSessionPre + sessionId;
		SessionInfo ssi = Cache.T(sessionInfoId);
		return ssi;
	}
	
	/**
	 * 指定用户,可选版本：V1_5,V1_6,V1_7,V1_1
	 * @param type
	 * 验证类型：<br/>
			1：注册<br/>
			2：找回密码 <br/>
			3 ：其他 <br/>
			4：提现人民币 <br/>
			5：提现比特币 <br/>
			6：提现莱特币 <br/>
			7：提现ETH <br/>
			8：设置谷歌验证码 <br/>
			9：安全设置（修改资金密码、关闭Google、关闭支付短信等等操作）<br/>
			10：异地登录短信验证 <br/>
			11：提现ETC<br/>
	 * @param version 可选版本：V1_5,V1_6,V1_7,V1_1
	 */
	public String getMobileCodeNoLogin(int type,String version, String mobileNumber, String email) {
		printSubTitle("getMobileCodeNoLogin");

		PostMethod method = new PostMethod(vipUri + version + "/sendCode");

		method.addParameter("type", type + "");
		String encryptNumber = "";
		String encryptEmail = "";
		try {
			if (StringUtils.isNotBlank(mobileNumber)) {
				encryptNumber = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(mobileNumber.getBytes(), pubKey));
			}
			if (StringUtils.isNotBlank(email)) {
				encryptEmail = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(email.getBytes(), pubKey));
			}
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}
		method.addParameter("encryptNumber", encryptNumber);
		method.addParameter("encryptEmail", encryptEmail);
		
		if(version.equalsIgnoreCase("V1_1")){
			addConfig(method);
			method.addParameter("sign",getSign(method.getParameters()));
		}
		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		String mCode = "";
		try {
			String json = executeHttp(method);
			String code = getStringFromJson(json, version.equalsIgnoreCase("V1_5") ? "code" : "resMsg.code");
			String message = getStringFromJson(json, version.equalsIgnoreCase("V1_5") ? "message" : "resMsg.message");

			if ("1000".equals(code)) {
				// 获取验证码
				String countryCode = isEmail(mobileNumber) ? "" : this.countryCode;
				SessionInfo ssi = getSessionInfo(countryCode + mobileNumber);
				
				String key;
				if (type == 1) {
					key = "注册";
				} else if (type == 2) {
					key = "验证码";
				} else {
					key = "其它";
				}
				CodeInfo ci = ssi.codeInfos.get(key);
				mCode = ci.lastCode;
				
			} else {
				mCode = message;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		printSubTitle("mCode::" + mCode);
		return mCode;
	}


    // 判断邮箱
    public static boolean isEmail(String email) {
        if(!email.contains("@")){
    		return false;
    	}
    	String str = "^((([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z])|[a-z0-9A-Z])@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    

	protected void addTokenUserId(PostMethod method) {
		method.addParameter("token", token);
		method.addParameter("userId", userId);
	}

	protected void addConfig(PostMethod method) {
		method.addParameter("appKey", key);
	}

	protected String getSign(NameValuePair[] parameters) {
		String sign = "";
		Map<String, String> rawParams = new HashMap<String, String>();
		for (NameValuePair e : parameters) {
			rawParams.put(e.getName(), e.getValue());
		}
		Map<String, String> sortedParams = new TreeMap<String, String>(rawParams);// 所有请求参数按照字母先后顺序排序
		for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
			if ("sign".equals(entry.getKey()))
				continue;
			sign += entry.getKey() + entry.getValue();
		}
		sign = secret + DigestUtils.md5Hex(sign).toLowerCase() + key;
		sign = DigestUtils.md5Hex(sign);
		sign = sign.toLowerCase();


		return sign;
	}


	public String doLoginV1_1(String test, String userName, String password, String dynamicCode,
			String googleCode, String countryCode) throws Exception {
		
		// 使用POST方法
		PostMethod method = new PostMethod(vipUri + "V1_1/login");
		log.info(method.getURI());
		
		String _password = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(password.getBytes(), pubKey));
		String _dynamicCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(dynamicCode.getBytes(), pubKey));
		String _googleCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(googleCode.getBytes(), pubKey));
		
		method.addParameter("userName", userName);
		method.addParameter("password", _password);
		method.addParameter("dynamicCode", _dynamicCode);
		method.addParameter("googleCode", _googleCode);
		method.addParameter("countryCode", countryCode);
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));
		
		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		String result = doCheck(test, method);
		String datas = getStringFromJson(result, "datas");
		if(StringUtils.isNotBlank(datas)){
			String _token = getStringFromJson(result, "datas.token");
			token = _token;
		}

		return result;
	}
	
	
	/**
	 * <pre>
	 *  category：1：登录，2：交易，3：提现
	 *  category 	type值	安全策略描述
	 * 	1	1	只需密码
	 * 	1	2	密码+Google验证码
	 * 	1	3	密码+异地登录验证（短信/邮件）
	 * 	1	4	密码+Google验证码+异地登录验证（短信/邮件）
	 * 	
	 * 	2	1	永不输入资金密码
	 * 	2	2	6小时内免输资金密码
	 * 	2	3	每次交易均验证资金密码
	 * 	
	 * 	3	1	资金密码+短信/邮件验证码
	 * 	3	2	资金密码+Google验证码
	 * 	3	3	资金密码+短信/邮件验证码+Google验证码
	 * </pre>
	 * 
	 */
	public String doChangeAuthV1_1(String test, String category, String type, String safePwd,
			String dynamicCode, String googleCode) throws Exception {

		/*
		 * 参数名	类型	是否必须	描述
			userId	String	是	用户id
			token	String	是	登录token
			type	Integer	是	验证策略类型, 详见下表
			category	String	是	验证分类（1. 登录 2. 交易 3. 提现）
			safePwd	String	否	资金密码（RSA加密）（设置交易验证策略时需要）
			dynamicCode	String	否	动态（短信/邮件）验证码（RSA加密）（设置登录、提现验证策略时需要）
			googleCode	String	否	谷歌验证码（RSA加密）（设置登录、提现验证策略时, 如用户已通过谷歌验证，则需要谷歌验证码）
		 * 
		 */
		
		doLoginIfNot();
		
		PostMethod method = new PostMethod(vipUri + "V1_1/changeAuth");
		log.info(method.getURI());
		
		
		
		String _safePwd = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(safePwd.getBytes(), pubKey));
		String _dynamicCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(dynamicCode.getBytes(), pubKey));
		String _googleCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(googleCode.getBytes(), pubKey));
		
		addTokenUserId(method);
		method.addParameter("type", type);
		method.addParameter("category", category);
		method.addParameter("safePwd", _safePwd);
		method.addParameter("dynamicCode", _dynamicCode);
		method.addParameter("googleCode", _googleCode);
		addConfig(method);
		method.addParameter("sign",getSign(method.getParameters()));
		
		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");
		
		return doCheck(test, method);
	}
	
	/**
	 * V1_6,V1_7可用
	 * @param version TODO
	 */
	public String doLoginV1_7(String test, String userName, String password, String dynamicCode,
							 String googleCode, String countryCode) throws Exception {

		// 使用POST方法
		PostMethod method = new PostMethod(vipUri + "V1_7/login");
		log.info(method.getURI());

		String _password = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(password.getBytes(), pubKey));
		String _dynamicCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(dynamicCode.getBytes(), pubKey));

		method.addParameter("userName", userName);
		method.addParameter("password", _password);
		method.addParameter("dynamicCode", _dynamicCode);
		method.addParameter("googleCode", googleCode);
		method.addParameter("countryCode", countryCode);

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");

		return doCheck(test, method);

	}

	public String doLoginV1_6(String test, String userName, String password, String dynamicCode, String googleCode,
							String countryCode) throws Exception {
		// 使用POST方法
		PostMethod method = new PostMethod(vipUri + "V1_6/login");
		log.info(method.getURI());

		String _password = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(password.getBytes(), pubKey));
		String _dynamicCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(dynamicCode.getBytes(), pubKey));

		method.addParameter("userName", userName);
		method.addParameter("password", _password);
		method.addParameter("dynamicCode", _dynamicCode);
		method.addParameter("googleCode", googleCode);
		method.addParameter("countryCode", countryCode);

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");

		return doCheck(test, method);
	}

	public String doLoginV1_5(String test, String userName, String password, String dynamicCode,
							 String googleCode, String countryCode) throws Exception {

		// 使用POST方法
		PostMethod method = new PostMethod(vipUri + "V1_5/login");
		log.info(method.getURI());

		String _password = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(password.getBytes(), pubKey));
		String _dynamicCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(dynamicCode.getBytes(), pubKey));
		String _googleCode = RSACoder.encryptBASE64(RSACoder.encryptByPublicKey(googleCode.getBytes(), pubKey));

		method.addParameter("userName", userName);
		method.addParameter("password", _password);
		method.addParameter("mobileCode", _dynamicCode);
		method.addParameter("googleCode", _googleCode);
		method.addParameter("countryCode", countryCode);

		HttpMethodParams param = method.getParams();
		param.setContentCharset("UTF-8");

		return doCheck(test, method);
	}
	
	
	/**
	 * entrustRecordV1_1接口适配器
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * type			String	是	类型 0：卖出 1：买入 -1：不限制
	 * currencyType	String	是	货币类型： BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic
	 * dayIn3		String	是	3天内数据 0：否 1：是 默认1
	 * status		String	是	状态 0不限制 1 已取消成功 2 交易成功 3 交易中（未完全成交）-1计划中
	 * pageIndex	String	是	页码 从1开始
	 * pageSize		String	是	每页显示数量 最大200
	 * </pre>
	 */
	public String entrustRecordV1_1(String test, String type, String currencyType, String exchangeType, String dayIn3,
			String status, String pageIndex, String pageSize) throws Exception {
	
		EntrustRecordTest entrustRecordTest = new EntrustRecordTest();
		entrustRecordTest.setUp();
		return entrustRecordTest.entrustRecordV1_1(test, type, currencyType, exchangeType, dayIn3, status, pageIndex, pageSize, "", "", "", "", "", "");

	}
	
	/**
	 * doEntrust接口适配器
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * timeStamp	String	是	时间戳
	 * type			String	是	类型  1：买入 0：卖出
	 * currencyType	String	是	货币类型：BTC：比特币，LTC：莱特币 ，，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic
	 * isPlan		String	是	类型  1：计划/委托交易 0：立即交易
	 * unitPrice	String	是	买入/卖出单价
	 * number		String	是	数量
	 * </pre>
	 */
	public String doEntrustV1_1(String test, String timeStamp, String type, String currencyType, String exchangeType,
			String isPlan, String unitPrice, String number) throws Exception {
		
		DoEntrustTest doEntrustTest = new DoEntrustTest();
		doEntrustTest.setUp();
		return doEntrustTest.doEntrustV1_1(test, timeStamp, type, currencyType, exchangeType, isPlan, unitPrice, number, "", "", "");
		
	}
	
	/**
	 * 4.18 取消单笔交易
	 * 
	 * <pre>
	 * 参数名		类型		必须	描述
	 * currencyType	String	是	货币类型：BTC：比特币，LTC：莱特币，ETH：Ethereum，ETC：Ethereum Classic
	 * exchangeType	String	是	兑换货币类型： CNY：人民币 BTC：比特币，LTC：莱特币 ，ETH：Ethereum，ETC：Ethereum Classic
	 * entrustId	String	是	交易id
	 * </pre>
	 */
	public String cancelEntrustV1_1(String test, String currencyType, String exchangeType, String entrustId) throws Exception {
		
		CancelEntrustTest cancelEntrustTest = new CancelEntrustTest();
		cancelEntrustTest.setUp();
		return cancelEntrustV1_1(test, currencyType, exchangeType, entrustId);
	}

	/**
	 * 如果未登录则先登录 
	 */
	public void doLoginIfNot() throws Exception {
		if (token.isEmpty()) {
			printSubTitle("重新登录获取token::");
			String result = doLoginV1_1("resMsg.code==1000|datas.token!=null",userName,password,"","", countryCode);
			token = getStringFromJson(result, "datas.token");
			printSubTitle("新token::" + token);
		}
	}
	
	// 判断数值
	public static boolean isNumberic(String numberic) {
		String reg = "^\\[\\d+(\\.\\d+)?\\]$";
		return numberic.matches(reg);
	}
	/**
	 * 操作枚举,应用于test表达式，支持比较符：==,!=,>,<,>=,<=,^=,$=,*=
	 * 使用示例：resMsg.code==1000
	 * 如果比较数值，需要用[xxx]框住期望值，支持比较符：==,!=,>,<,>=,<=
	 * @author jiahua
	 */
	public enum Operator {
		// ==,!=,>,<,>=,<=,^=,$=,*=

		
		EQ("==") {
			@Override
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				if (isNumberic(expecteds)) { // 比较数值,[***]表示数字格式
					return new BigDecimal(actuals).compareTo(new BigDecimal(expecteds.replaceAll("\\[|\\]", ""))) == 0;
				}else{
					if(expecteds.equals("null") || expecteds.equals("empty") || expecteds.equals(" ")){
						return actuals==null || actuals.trim().isEmpty();
					}else {
						return actuals.equalsIgnoreCase(expecteds);
					}
				}
			}
		},
		NE("!=") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				if (isNumberic(expecteds)) { // 比较数值,[***]表示数字格式
					return new BigDecimal(actuals).compareTo(new BigDecimal(expecteds)) != 0;
				}else{
					if(expecteds.equals("null") || expecteds.equals("empty") || expecteds.equals(" ")){
						 return actuals!=null && !actuals.isEmpty();
					}else {
						return !actuals.equalsIgnoreCase(expecteds);
					}
				}
			}
		},
		GE(">=") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				if (isNumberic(expecteds)) { // 比较数值,[***]表示数字格式
					return new BigDecimal(expecteds).compareTo(new BigDecimal(actuals)) >= 0;
				}else{
					return actuals.compareTo(expecteds) >= 0;
				}
			}
		},
		LE("<=") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				if (isNumberic(expecteds)) { // 比较数值,[***]表示数字格式
					return new BigDecimal(actuals).compareTo(new BigDecimal(expecteds)) <= 0;
				}else{
					return actuals.compareTo(expecteds) <= 0;
				}
			}
		},
		GT(">") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				if (isNumberic(expecteds)) { // 比较数值,[***]表示数字格式
					return new BigDecimal(actuals).compareTo(new BigDecimal(expecteds.substring(1, expecteds.length()-1))) > 0;
				}else{
					return actuals.compareTo(expecteds) > 0;
				}
			}
		},
		LT("<") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				if (isNumberic(expecteds)) { // 比较数值,[***]表示数字格式
					return new BigDecimal(actuals).compareTo(new BigDecimal(expecteds)) < 0;
				}else{
					return actuals.compareTo(expecteds) < 0;
				}
			}
		},
		StartsWith("^=") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				return actuals.startsWith(expecteds);
			}
		},
		EndsWith("$=") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				return actuals.endsWith(expecteds);
			}
		},
		Contains("*=") {
			public boolean check(String actuals, String expecteds) {
				super.check(actuals, expecteds);
				String[] split = expecteds.split("\\*");
				boolean isPass = true;
				for (int i = 0; i < split.length; i++) {
					if(!actuals.contains(split[i])){
						isPass = false;
					}
				}
				return isPass;
			}
		},
		;

		String opr;
		String expecteds;
		String actuals;
		
		private Operator(String opr) {
			this.opr = opr;
		}



		/**
		 * 
		 * @param actuals	
		 * @param expecteds	[***]表示数字格式
		 * @return
		 */
		protected boolean check(String actuals, String expecteds){
			setExpecteds(expecteds);
			setActuals(actuals);
			return false;
		}
		
		protected String getMessage(){
			return "比较【参数1 " + this.getOpr() + " 参数2】 >>>>【参数1 】：" + expecteds + "，【 参数2】：" + actuals;

		}
		
		/*
		 * setter and getter =======================================
		 */

		public String getOpr() {
			return opr;
		}

		public void setOpr(String opr) {
			this.opr = opr;
		}



		public String getExpecteds() {
			return expecteds;
		}



		public void setExpecteds(String expecteds) {
			this.expecteds = expecteds;
		}



		public String getActuals() {
			return actuals;
		}



		public void setActuals(String actuals) {
			this.actuals = actuals;
		}
	}

}