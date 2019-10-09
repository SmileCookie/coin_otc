package com.api.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.api.VipClient;
import com.api.VipClientFactory;
import com.api.VipRequest;
import com.api.VipResponse;
import com.api.request.VipUserManagerRequest;

/**
 * 关于用户信息的一些api操作
 * @author guosj
 */
public class UserManager {
	
	private static UserManager userManager;
	
	public static UserManager getInstance(){
		if(userManager == null) 
			userManager = new UserManager();
		return userManager;
	}
	
	/**
	 * 安全密码是否已关闭
	 * @param userId
	 * @param safePwd
	 */
	public VipResponse isUseSafePwd(String userId) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/isUseSafePwd");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userId", userId);
		request.setTextParams(params);
		return client.execute(request);
	}
	
	/**
	 * 是否通过安全认证
	 * @param params
	 */
	public VipResponse validateSafePwd(Map<String , String> params) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/validateSafePwd");
		
		request.setTextParams(params);
		
		return client.execute(request);
	}

	/**
	 * 是否通过安全认证
	 * @param params
	 */
	public VipResponse verifySafePwd(Map<String , String> params) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();

		request.setApiMethod("api/user/verfiSafePwd");

		request.setTextParams(params);

		return client.execute(request);
	}
	
	public VipResponse validateFingerprintOrSafePwd(Map<String , String> params) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/validateFingerprintOrSafePwd");
		
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 是否通过安全认证
	 * @param userId
	 * @param safePwd
	 */
	public VipResponse validateSafePwd(String userId, String safePwd) throws Exception{
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userId", userId);
		params.put("safePwd", safePwd);
		params.put("use", "1");
		
		return validateSafePwd(params);
	}
	
	/**
	 * 更改用户交易状态
	 * @return
	 */
	public VipResponse changeUserTransStatus(String userId, int status) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/changeUserTransStatus");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userId", userId);
		params.put("status", status + "");
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 发送用户短信
	 * @param userIds 支持多个用户(1,2,3)
	 * @param content
	 */
	public VipResponse sendSms(String userIds, String content) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/sendSms");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userIds", userIds);
		params.put("content", content);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	/**
	 * 推送给移动端
	 * @param userIds 支持多个用户(1,2,3)
	 * @param content
	 */
	public VipResponse pushToApp(String userIds,int msgType, String content) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/pushToApp");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userIds", userIds);
		params.put("content", content);
		params.put("msgType", String.valueOf(msgType));
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 发送邮件
	 * @param userIds 支持多个用户(1,2,3)
	 * @param content
	 * @param title
	 */
	public VipResponse sendEmail(String userIds, String content, String title) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/sendEmail");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userIds", userIds);
		params.put("content", content);
		params.put("title", title);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 自动交易API通过accesskey获取用户充值地址
	 * @param accessKey 
	 * @param type 货币类型
	 * @return
	 */
	public VipResponse getUserAddress(String accessKey, String currency) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getUserAddress");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("currency", currency);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	
	/**
	 * 自动交易API通过accesskey获取用户信息接口
	 * @param accessKey 
	 * @param type 如果type=0，只查询用户基本信息，如果type=1，查询用户账户信息
	 * @return
	 */
	public VipResponse getUserByAccessKey(String accessKey, int type) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getUserByAccessKey");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("type", type + "");
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 关闭用户API访问状态
	 * @param accessKey
	 * @return
	 */
	public VipResponse closeUserAutoApi(String accessKey) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/closeUserAutoApi");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 是否允许用户提现
	 * @param userIds 支持多个用户(1,2,3)
	 * @param content
	 */
	public VipResponse allowWithdrawal(String userId) throws Exception{
		VipClient client = VipClientFactory.getClient("p2p");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/allowWithdrawal");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userId", userId);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 获取用户P2P产生资金，以数组形式返回
	 * 数组格式：[借入的CNY,借入的BTC,借入的LTC,借出的CNY,借出的BTC,借出的LTC]
	 * @param userId
	 * @param reqUrl
	 * @return
	 * @throws Exception 
	 */
	public VipResponse getUserP2pFunds(String userId) throws Exception{
		VipClient client = VipClientFactory.getClient("p2p");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/getUserP2pFunds");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userId", userId);
		request.setTextParams(params);
		
		return client.execute(request , 2000 , 2000);
	}
	
	/**
	 * 返回用户推荐人用户ID
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public VipResponse getRecommendUser(String userId) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getRecommendUser");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("userId", userId);
		request.setTextParams(params);
		
		return client.execute(request , 2000 , 2000);
	}
	
	/***
	 * 根据用户ID或者用户名查找用户
	 * @param userId  可以传NULL
	 * @param userName 可以传NULL
	 * @return
	 * @throws Exception
	 */
	public VipResponse getUser(String userId , String userName) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getUser");
		
		Map<String , String> params = new HashMap<String , String>();
		if(userId != null && userId.length()>0){
			params.put("userId", userId);
		}
		
		if(userName != null && userName.length()>0){
			params.put("userName", userName);
		}
		request.setTextParams(params);
		
		return client.execute(request , 20000 , 20000);
	}

	/****
	 * 
	 * @param userId
	 * @param date 
	 * @param type 1被动成交 2主动成交 3推荐人来交易
	 * @param nums
	 * @return
	 * @throws Exception
	 */
	public VipResponse givingNumber(int hostUserId , int beUserId ,long date , int baseNum) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/giving");
		
		Map<String , String> params = new HashMap<String , String>();
		if(hostUserId > 0 && beUserId > 0 && baseNum > 0){
			params.put("hostUserId", String.valueOf(hostUserId));
			params.put("beUserId", String.valueOf(beUserId));
			params.put("base", String.valueOf(baseNum));
		}
		params.put("date", String.valueOf(date));
		request.setTextParams(params);
		
		return client.execute(request , 2000 , 2000);
	}
	
	
	/**
	 * 自动交易API通过accesskey获取用户认证的提币地址
	 * @param accessKey 
	 * @param type 货币类型
	 * @return
	 */
	public VipResponse getWithdrawAddress(String accessKey, String currency) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getWithdrawAddress");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("currency", currency);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 自动交易API通过accesskey获取提现记录
	 * @param accessKey 
	 * @param type 货币类型
	 * @param pageIndex 查询页码
	 * @param pageSize 每页页数
	 * @return
	 */
	public VipResponse getWithdrawRecord(String accessKey, String currency, int pageIndex, int pageSize) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getWithdrawRecord");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("currency", currency);
		params.put("pageIndex", pageIndex+"");
		params.put("pageSize", pageSize+"");
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 自动交易API通过accesskey取消提现操作
	 * @param accessKey 
	 * @param type 货币类型
	 * @return
	 */
	public VipResponse cancelWithdraw(String accessKey, String currency, long downloadId, String safePwd) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/cancelWithdraw");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("currency", currency);
		params.put("downloadId", downloadId+"");
		params.put("safePwd", safePwd);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 自动交易API通过accesskey进行提现操作
	 * @param accessKey 
	 * @param type 货币类型
	 * @return
	 */
	public VipResponse withdraw(String accessKey, String currency, double amount, String receiveAddr, double fees, String safePwd) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/withdraw");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("currency", currency);
		params.put("amount", amount+"");
		params.put("receiveAddr", receiveAddr);
		params.put("fees", fees+"");
		params.put("safePwd", safePwd);
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 自动交易API通过accesskey获取充值记录
	 * @param accessKey 
	 * @param type 货币类型
	 * @param pageIndex 查询页码
	 * @param pageSize 每页页数
	 * @return
	 */
	public VipResponse getChargeRecord(String accessKey, String currency, int pageIndex, int pageSize) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getChargeRecord");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("currency", currency);
		params.put("pageIndex", pageIndex+"");
		params.put("pageSize", pageSize+"");
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 自动交易API通过accesskey进行提现操作
	 * @param accessKey 
	 * @param type 货币类型
	 * @return
	 */
	public VipResponse withdraw(String accessKey, String currency, double amount, String receiveAddr, double fees, String safePwd, int itransfer) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/withdraw");
		
		Map<String , String> params = new HashMap<String , String>();
		params.put("key", accessKey);
		params.put("currency", currency);
		params.put("amount", amount+"");
		params.put("receiveAddr", receiveAddr);
		params.put("fees", fees+"");
		params.put("safePwd", safePwd);
		params.put("itransfer", itransfer+"");
		request.setTextParams(params);
		
		return client.execute(request);
	}
	
	/**
	 * 获取后台配置的白名单信息
	 * @param accessKey 访问Key
	 * @return JSONArray
	 * @throws Exception
	 */
	public VipResponse getWhiteIp() throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		
		request.setApiMethod("api/user/getWhiteIp");
		
		Map<String , String> params = new HashMap<String , String>();
		//params.put("key", accessKey);
		request.setTextParams(params);
		
		return client.execute(request);
	}


	/*start by xzhang 20171215 交易页面三期PRD:*/
	/**
	 * 获取用户收藏
	 * @param userId
	 */
	public VipResponse getUserCollect(String userId) throws Exception{
		VipClient client = VipClientFactory.getClient("vip");
		VipRequest request = new VipUserManagerRequest();
		request.setApiMethod("manage/getUserCollect");
		Map<String , String> params = new HashMap<String , String>();
		params.put("userId", userId);
		request.setTextParams(params);
		return client.execute(request);
	}
	/*end*/
}
