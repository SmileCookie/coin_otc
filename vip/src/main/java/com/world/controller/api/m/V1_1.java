package com.world.controller.api.m;

import com.google.common.collect.Lists;
import com.messi.user.vo.DownloadInfoVo;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import com.redis.RedisUtil;
import com.world.cache.Cache;
import com.world.controller.api.util.SystemCode;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.LimitType;
import com.world.model.LockType;
import com.world.model.dao.BannerPhotoDao;
import com.world.model.dao.BannerRelationDao;
import com.world.model.entity.BannerPhoto;
import com.world.model.entity.BannerRelation;
import com.world.model.entity.CacheKeys;
import com.world.model.entity.Fundsintroduction;
import com.world.model.entity.user.User;
import com.world.util.QcloudCosUtil;
import com.world.util.date.TimeUtil;
import com.world.util.sign.RSACoder;
import com.world.util.string.StringUtil;
import com.world.web.Page;
import com.world.web.jwt.JwtUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手机app接口v1.0.1
 * @author dongzhihui
 *
 */
public class V1_1 extends BaseMobileAction {


	@Page(Viewer = JSON)
	public void version() {
		setLan();
		String version = param("2");

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("version", version);
		json(SystemCode.code_1000, map);
	}
	@Page(Viewer = JSON)
	public void appVersion(){
		this.updateAppVersion();
	}
	@Page(Viewer = JSON)
	public void cardStatus(){
		this.getCardStatus();
	}
    /**
     * 邮箱注册
     */
	@Page(Viewer = JSON)
	public void registerUseEmail() {
		this.registerWithEmail();
	}

    /**
     * 发送短信邮箱验证码
     */
	@Page(Viewer = JSON)
	public void userSendCode() {
		this.doUserSendCode();
	}

    /**
     * 登录
     */
    @Page(Viewer = JSON)
    public void login() {
        this.doLogin();
    }
	/**
	 * 1.1 当前无用户登录，则根据语言环境跳转到指定工单系统页面，无登录状态。
	 * 1.2 当前用户已登录，且存在邮箱。则同步当前用户会话信息到工单系统；
	 * 语言环境根据用户工单信息设置展示，默认在工单系统配置；会话同步不会传递语言环境。
	 * 1.3 当前用户已登录，但不存在邮箱。则无法同步用户会话信息；则根据语言环境跳转到指定工单系统页面，无登录状态。
	 * 异常：
	 * 屏蔽所有异常，展示未登录状态下工单系统
	 */
	@Page()
	public void zendesk() {
		this.doZendesk();
	}

	/**
	 * 拼图验证
	 */
//	@Page(Viewer = JSON)
	public void checkJigsawPuzzle() {
		this.doCheckJigsawPuzzle();
	}

    /**
     * 二次验证
     */
    @Page(Viewer = JSON)
    public void secondVerify() {
        this.doSecondVerify();
    }

    /**
     * 重置二次验证-邮箱验证
     */
    @Page(Viewer = JSON)
    public void checkNoSecondVerify() {
        this.doCheckNoSecondVerify();
    }

    /**
     * 重置二次验证-校验地址
     */
    @Page(Viewer = JSON)
    public void checkAddress() {
        this.doCheckAddress();
    }

    /**
     * 重置二次验证-校验手持照片
     */
    @Page(Viewer = JSON)
    public void idCardAuth() {
        this.doIdCardAuth();
    }

    /**
     * 重置二次验证-校验资金密码
     */
    @Page(Viewer = JSON)
    public void checkSafePwd() {
        this.doCheckSafePwd();
    }

    /**
     * 忘记密码-邮箱验证
     */
    @Page(Viewer = JSON)
    public void checkEmailForForgetPwd() {
        this.doCheckEmailForForgetPwd();
    }

    /**
     * 忘记密码---手机验证
     */
    @Page(Viewer = JSON)
    public void checkMobileForForgetPwd() {
        this.doCheckMobileForForgetPwd();
    }

    /**
     * 忘记密码---谷歌验证
     */
    @Page(Viewer = JSON)
    public void checkGoogleForForgerPwd() {
        this.doCheckGoogleForForgerPwd();
    }
    /**
     * 忘记密码
     */
    @Page(Viewer = JSON)
    public void forget() {
        this.doForget();
    }

    /**
     * 用户收藏市场(用户触发)
     */
    @Page(Viewer = JSON)
    public void userCollect() {
        this.doUserCollect();
    }

    /**
     * 用户取消收藏市场(用户触发)
     */
    @Page(Viewer = JSON)
    public void closeCollect() {
        this.doCloseCollect();
    }

    /**
     * 获取收藏的市场
     */
    @Page(Viewer = JSON)
    public void getCollects() {
        this.collects();
    }

	/**
	 * 查看钱包资金信息
	 */
	@Page(Viewer = JSON)
	public void walletDetail() {
		this.getWalletDetail();
	}

    /**
     * 查看币币交易账户资金信息
     */
    @Page(Viewer = JSON)
    public void biBiDetail() {
        this.getBiBiDetail();
    }

	/**
	 * 查看币法账户资金信息
	 */
	@Page(Viewer = JSON)
	public void otcDetail() {
		this.getOtcDetail();
	}

	/**
	 * 查看充值记录
	 */
	@Page(Viewer = JSON)
	public void rechargeList() {
		this.getRechargeList();
	}

	/**
	 * 查看提现记录
	 */
	@Page(Viewer = JSON)
	public void withdrawList() {
		this.getWithdrawList();
	}

	/**
	 * 查看划转记录
	 */
	@Page(Viewer = JSON)
	public void tranceList() {
		this.getTranceList();
	}

	/**
	 * 获取充值地址
	 */
	@Page(Viewer = JSON)
	public void getRechargeCoinInfo() {
		this.rechargeCoinInfo();
	}

	/**
	 * 获取提现信息
	 */
	@Page(Viewer = JSON)
	public void withdrawalInfo() {
		try {
			setLan();
			String coinTypeIdStr = param("coinTypeId");
			//币种
			Long coinTypeId = Long.valueOf(coinTypeIdStr);
			String userId = userIdStr();
			String token = param("token");
			if (isLogin(userId, token) == SystemCode.code_1003 || isLogin(userId, token) == SystemCode.code_402) {
				json(isLogin(userId, token), L(isLogin(userId, token).getValue()));
				return;
			}
			User user = userDao.getUserById(userId);
			int lockStatus = 0;
			//您的资金密码已经被锁定，在此期间不能进行提现操作，请等待24个小时后自动解锁。
			if (user.getSafePwdModifyTimes() > 1 && null != user.getSafePwdModifyTime()
					&& TimeUtil.getOriginDiffDay(now(), user.getSafePwdModifyTime()) < 1) {
				lockStatus = 1;
			}
			int payLoginLock = doGetErrorTimes(userId,LimitType.PayLoginPassError);
			int payEmailLock = doGetErrorTimes(userId,LimitType.PayEmailPassError);
			int payMobileLock = doGetErrorTimes(userId,LimitType.PayMobilePassError);
			int payGoogleLock = doGetErrorTimes(userId,LimitType.PayGooglePassError);
			if((payLoginLock == -2) || (payEmailLock == -2)
					|| (payMobileLock == -2) || (payGoogleLock == -2)){
				lockStatus = 1;
			}
			int withdrawPayPassLock = doGetErrorTimes(userId,LimitType.WithdrawPayPwdPassError);
			int withdrawEmailLock = doGetErrorTimes(userId,LimitType.WithdrawEmailPassError);
			int withdrawSmsLock = doGetErrorTimes(userId,LimitType.WithdrawMobilePassError);
			int withdrawGoogleLock = doGetErrorTimes(userId,LimitType.WithdrawGooglePassError);
			if((withdrawPayPassLock == -2) || (withdrawEmailLock == -2) || (withdrawSmsLock == -2) || (withdrawGoogleLock == -2)){
				lockStatus = 1;
			}

			//otc锁定
			int otcCadPayPwdLock = doGetErrorTimes(userId,LimitType.OtcCadPayPwd);
			int otcReleasecoinPayPwdLock = doGetErrorTimes(userId,LimitType.OtcCadPayPwd);
			if((otcCadPayPwdLock == -2) || (otcReleasecoinPayPwdLock == -2)){
				lockStatus = 1;
			}

			//安全模式 && 地址创建时间在24小时之内
			if (user.getWithdrawAddressAuthenSwitchStatus() == 2
					&& TimeUtil.getOriginDiffDay(now(), user.getWithdrawAddressAuthenModifyTime()) < 1) {
				lockStatus = 1;
			}
			DownloadInfoVo vo = dowoloadApi.getInfo(userId,coinTypeId.intValue());
			Map map = new HashMap();
			map.put("data",vo);
			map.put("lockStatus",lockStatus);
			withdrawalDescript(map,coinTypeIdStr);
			json(SystemCode.code_1000,"", map);
		}catch (Exception e){
			json(SystemCode.code_1002, L("内部异常"));
			return;
		}
	}
	public void withdrawalDescript(Map retMap,String fundsType) {

		List<Integer> fundsTypeList = new ArrayList<>();
		fundsTypeList.add(Integer.valueOf(fundsType));
		if (Integer.valueOf(fundsType) == 10) {
			fundsTypeList.add(102);
		}
		List<Fundsintroduction> fundsintroductionList = fundsinDao.getFunds(fundsTypeList, 1);
		if (CollectionUtils.isEmpty(fundsintroductionList)) {
			retMap.put("desc", null);
			return;
		}
		if (fundsintroductionList.size() == 1) {
			Fundsintroduction fundsintroduction1 = new Fundsintroduction();
			fundsintroduction1.setFundsType(fundsintroductionList.get(0).getFundsType());
			fundsintroduction1.setType(fundsintroductionList.get(0).getType());
			fundsintroduction1.setCoinName(DatabasesUtil.getUsdtAggrement(fundsintroductionList.get(0).getFundsType()).getPropTag().toLowerCase());
			Fundsintroduction fun = this.copyBean(fundsintroductionList.get(0), fundsintroduction1);
			retMap.put("desc",fun);
			return;
		} else {
			for (Fundsintroduction fundsintroduction : fundsintroductionList) {
				Fundsintroduction fundsintroduction1 = new Fundsintroduction();
				fundsintroduction1.setFundsType(fundsintroductionList.get(0).getFundsType());
				fundsintroduction1.setType(fundsintroduction.getType());
				fundsintroduction1.setCoinName(DatabasesUtil.getUsdtAggrement(fundsintroduction.getFundsType()).getPropTag().toLowerCase());
				Fundsintroduction fun = this.copyBean(fundsintroduction, fundsintroduction1);
				if(fundsintroduction.getFundsType() == 10){
                    retMap.put("desc",fun);
                }else{
                    retMap.put("usdteDesc",fun);
                }

			}
			return;
		}
	}

	private Fundsintroduction copyBean(Fundsintroduction fundsintroduction, Fundsintroduction fundsintroduction1) {
		switch (lan) {
			case "en":
				fundsintroduction1.setDescript(fundsintroduction.getDescriptEN());
				break;
			case "jp":
				fundsintroduction1.setDescript(fundsintroduction.getDescriptJP());
				break;
			case "kr":
				fundsintroduction1.setDescript(fundsintroduction.getDescriptKR());
				break;
			case "tw":
				fundsintroduction1.setDescript(fundsintroduction.getDescriptHK());
			default:
				fundsintroduction1.setDescript(fundsintroduction.getDescriptCN());
		}

		return fundsintroduction1;

	}
	/**
	 * 保存提现信息
	 */
	@Page(Viewer = JSON)
	public void addWithdrawalInfo() {
		this.saveWithdrawalInfo();
	}

	/**
	 * 划转
	 */
	@Page(Viewer = JSON)
	public void transfer() {
		this.doTransfer();
	}

	/**
	 * 获取OTC资金信息
	 */
	@Page(Viewer = JSON)
	public void payUserOtcList() {
		this.queryPayUserOtc();
	}

	/**
	 * 获取OTC资金记录
	 */
	@Page(Viewer = JSON)
	public void billOtcList() {
		this.queryBillOtc();
	}

	/**
	 * 获取OTC交易记录详情
	 */
	@Page(Viewer = JSON)
	public void billOtcInfo() {
		this.findBillOtc();
	}

	/**
	 * 查询OTC冻结记录列表
	 */
	@Page(Viewer = JSON)
	public void otcFrozenBillList() {
		this.queryOtcFrozenBill();
	}

	/**
	 * 获取冻结交易记录详情
	 */
	@Page(Viewer = JSON)
	public void otcFrozenBillInfo() {
		this.findOtcFrozenBill();
	}

	/**
	 * 获取用户资产评估包含钱包、币法、币币
	 */
	@Page(Viewer = JSON)
	public void assetEvaluation() {
		this.getAssetEvaluation();
	}

	/**
	 * 取消提现
	 */
	@Page(Viewer = JSON)
	public void cancelDownload() {
		this.doCancelDownload();
	}

	/**
	 * 个人资产
	 */
	@Page(Viewer = JSON)
	public void userAssets() {
		this.getUserAssets();
	}

	/**
	 * 个获取交易货币配置
	 */
	@Page(Viewer = JSON)
	public void currencySet() {
		this.getCurrencySet();
	}
	/**
	 * 实名认证
	 */
	@Page(Viewer = JSON)
	public void authSave() {
		this.doAuthSave();
	}
	/**
	 * 折算法币
	 */
	@Page(Viewer = JSON)
	public void convertRate() {
		this.getConvertRate();
	}
	/**
	 * 提现地址列表
	 */
	@Page(Viewer = JSON)
	public void addressPage() {
		this.getAddressPage();
	}
	/**
	 * 添加提现地址，新
	 */
	@Page(Viewer = JSON)
	public void addAddress() {
		this.doAddAddress();
	}
	/**
	 * 解锁
	 */
	@Page(Viewer = JSON)
	public void unlock() {
		String userId = userIdStr();
		LimitType[] limitTypes = LimitType.values();
		for(LimitType lt : limitTypes){
			String key=lt.toString()+"_"+userId;
			Cache.Delete(key);
		}
		LockType[] lockTypes = LockType.values();
		for(LockType lockType : lockTypes){
			String lockKey = CacheKeys.getFunctionLockKey(userId,lockType.getValue());
			Cache.Delete(lockKey);
		}
	}
	/**
	 * 获取用户余额信息
	 */
	@Page(Viewer = JSON)
	public void payUserInfo(){this.getPayUserInfo();}

	/**
	 * 交易时调用是否开启交易设置
	 */
	@Page(Viewer = JSON)
	public void transSafe(){this.isTransSafe();}
	/**
	 * 读取公告
	 */
	@Page(Viewer = JSON)
	public void readNotice(){this.doReadNotice();}
	/**
	 * 获取用户持有的币种
	 */
	@Page(Viewer = JSON)
	public void userHasCoin(){this.getUserHasCoin();}
	/**
	 * 获取公告列表
	 */
	@Page(Viewer = JSON)
	public void getProclamations(){this.getProclamationsList();}
	/**
	 * 读取公告
	 */
//	@Page(Viewer = JSON)
//	public void readNoticeOne(){this.doReadNoticeOne();}
	/**
	 * 读取全部公告
	 */
	@Page(Viewer = JSON)
	public void readNoticeAll(){this.doReadNoticeAll();}
	/**
	 * 是否有公告
	 */
	@Page(Viewer = JSON)
	public void checkNotice(){this.doCheckNotice();}
	/**
	 * 读取全部公告
	 */
	@Page(Viewer = "/admins/api/noticeDetail.jsp", des = "公告详情")
	public void newsdetails(){this.getNewsdetails();}

	/**
	 * 安全等级
	 */
	@Page(Viewer = JSON)
	public void safeModelType(){this.getSafeModelType();}
	/**
	 * 是否需要资金密码
	 */
	@Page(Viewer = JSON)
	public void isNeedSafePwd(){this.doIsNeedSafePwd();}
	/**
	 * 验证资金密码
	 */
	@Page(Viewer = JSON)
	public void safePwdForEnturst(){this.doSafePwdForEnturst();}

	/**
	 * 获取推荐币
	 */
	@Page(Viewer = JSON)
	public void recommendCoin(){this.getRecommendCoin();}
	/**
	 *市场信息
	 */
	@Page(Viewer = JSON)
	public void marketInfo() {
		this.getMarket();
	}

	/**
	 *币种列表
	 */
	@Page(Viewer = JSON)
	public void getCoinList() {
		this.getCoin();
	}

	/**
	 * 币种介绍
	 */
	@Page(Viewer = JSON)
	public void coinDesc() {
		this.getCoinInfo();
	}
	/**
	 *删除提现地址
	 */
	@Page(Viewer = JSON)
	public void addressDel() {
		this.doAddressDel();
	}
	/**
	 *推送驾驶舱浏览首页埋点
	 */
	@Page(Viewer = JSON)
	public void browseHome() {
		this.doBrowseHome();
	}
	/**
	 *推送驾驶舱浏览交易页埋点
	 */
	@Page(Viewer = JSON)
	public void browseTrade() {
		this.doBrowseTrade();
	}


	/**
	 *设置语言
	 */
	@Page(Viewer = JSON)
	public void setLang() {
		this.doSetLan();
	}

	/**
	 * 产品&超级节点信息
	 */
	@Page(Viewer = JSON)
	public void productSuperNode() {
		this.doProductSuperNode();
	}

	/**
	 *  用户认证支付信息
	 */
	@Page(Viewer = JSON)
	public void userFinancialInfo() {
		this.doUserFinancialInfo();
	}

	/**
	 *保存用户认证信息
	 */
	@Page(Viewer = JSON)
	public void userProductInfoSave() {
		this.doUserProductInfoSave();
	}

	/**
	 *用户理财产品支付
	 */
	@Page(Viewer = JSON)
	public void userProductInfoPay() {
		this.doUserProductInfoPay();
	}

	/**
	 * 用户理财信息
	 */
	@Page(Viewer = JSON)
	public void userFinCenInfo() {
		this.doUserFinCenInfo();
	}

	/**
	 * 用户理财投资流水
	 */
	@Page(Viewer = JSON)
	public void userFinProfitBill () {
		this.doUserFinProfitBill();
	}

	/**
	 *获取用户理财账户VDS余额
	 */
	@Page(Viewer = JSON)
	public void userFinVdsBalance() {
		this.doUserFinVdsBalance();
	}

	/**
	 *超级节点信息
	 */
	@Page(Viewer = JSON)
	public void superNodeInfo() {
		this.getSuperNodeInfo();
	}

	/**
	 * 理财信息
	 */
	@Page(Viewer = JSON)
	public void fundsDetail() {
		this.getFundsDetail();
	}

	/**
	 * 用户理财奖励查询
	 */
	@Page(Viewer = JSON)
	public void userFinCenRewardInfo() {
		this.doUserFinCenRewardInfo();
	}

	/**
	 *奖励枚举
	 */
	@Page(Viewer = JSON)
	public void bonusType() {
		this.getBonusType();
	}

	/**
	 *用户待划转资金解冻划转
	 */
	@Page(Viewer = JSON)
	public void userAvaTransferAmount() {
		this.doUserAvaTransferAmount();
	}

	/**
	 * bonus列表
	 */
	@Page(Viewer = JSON)
	public void bonusList() {
		this.getBonusList();
	}
	@Page(Viewer = JSON)
	public void userInvitationChart() {
		this.doUserInvitationChart();
	}

    /**
     * ws调用接口
     */
    @Page(Viewer = JSON)
    public void verifyUserSession(){
        try {
            // 加点防御措施
            String sign = param("sign");
            if (StringUtils.isBlank(sign)) {
                json("参数非法！", false, "", true);
                return;
            }
            String priKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPS4B7C6wM2D9S4s7KnsHFQ00AfyLTUpLbobbo1h9n06xhLPwSGGN5RHRkTi338db01Tjenocp1gNnZr/lDtMv1aVFrBR16FMQc8a6owUcVQhZFkCaX/A62QeosobVqCP8V3Zg35DwL8NMqYWPxrrn6ZXxjmZXwFCl8zuZmZoCgTAgMBAAECgYAJcnlJh0weIvP6Hl3ciXLmhUtqoxy/qqFLR/fSmW+MkhJHc6G/C5FltgKuchuyqo47a/hUiVazYJ15oN2mfiGzts7XH0eZvZjKwiBEUczDDG6qiTYHYHX83o2IbVwjquQXz2ZTJqcCzM7qVSwOO5bLmvUHZRi8ssBA873W8iGL8QJBAP8eFYpdxf0flZ0tM2MxvBl7cM4f5acSV7pGUcRZxu6v7jMfeqsutop7PRq5Xovqp2FwHB18BAESZpp9Z9PhrQcCQQD1kLzNzvteuv7I1CyGL4hix9qT1QzKWNCfHkTV9E6qdD9WRemeM4daR0y5FNmcEaIFEvlU8JKT9fo30HaU7DWVAkEAhtYRsHXrhONCojYXqN/KePVjI658JJdvQoaUBOEmYVUe4mpK3VrtI7gLDarXq7+0A63LTlITk7V0AUcyryvh1QJAHQYoaqLmLwInDxGU2Z9QnWxFt2ddBaWNsuDe/fLMQXVP7yCARkSM4OzAcre2KK4k2jit444zpO1Tz7kB6wQoKQJAOxRAFRNPa1WwqPpb/3AGsfjz+RCMVorC8YRLwpnYUP4UZ2AQn6AcI3rx7AyqFFugBdE02FpASBwWBhz2VkSKCA==";
            byte[] decodeSign = RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(sign), priKey);//解密
            if (!"messi_ws".equals(new String(decodeSign))) {
                json("参数非法！", false, "", true);
                return;
            }
            String token = param("token");
            String userId = "";
            Map<String,Object> map = JwtUtil.getDecodedsInfo(token);
            if(!map.isEmpty()){
                userId = map.get("zuid") != null ? map.get("zuid").toString() : "";
            }
            JSONObject js = new JSONObject();
            js.put("uid",userId);
            json(SystemCode.code_1000, js.toString());
        } catch (Exception e) {
            logger.error("OTC推送信息token校验失败", e);
        }
    }

//	public Map<String, JSONArray> getUserAssetInfo(User user) {
//		String userIdStr = user.get_Id();
//		Map<String, JSONArray> accountMap = new HashMap<>();
//		JSONArray funds = UserCache.getUserFunds(userIdStr);
//		accountMap.put("balances", funds);
//		return accountMap;
//	}
	/**
	 * 注册
	 *//*
	@Page(Viewer = JSON)
	public void register() {
		setLan();
		try {
			String countryCode = param("countryCode");
			if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
				countryCode = "+86";
			}
			String userName = param("userName");
			String mobileNumber = param("mobileNumber");
			String dynamicCode = param("dynamicCode");
			String password = param("password");
//			String safePwd = param("safePwd");
			String email = param("email");
//			String tuijianId = param("recommId");

			if(StringUtils.isNotBlank(email) && StringUtils.isBlank(mobileNumber)){
				if (!CheckRegex.isEmail(email)) {
					json(SystemCode.code_1001, L("邮箱不正确，请重新填写"));
					return;
				}
				registerWithEmail();
				return;
			}

			password = StringUtils.isNotBlank(password)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password), priKey)):"";
			dynamicCode = StringUtils.isNotBlank(dynamicCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey)):"";

			if (null == userName || "".equals(userName)) {
				userName = mobileNumber;
			}
			if (!UserUtil.checkNick(userName)) {
				json(SystemCode.code_1003, L("用户名不能含有特殊字符。"));
				return;
			}

			boolean res = userDao.mobileValidated(mobileNumber);
			if (!res) {
				json(SystemCode.code_1001, L("手机已注册，请重新填写。"));
				return;
			}

			if (!mobileNumber.startsWith("+")) {
				mobileNumber = countryCode + " " + mobileNumber;
			}

			if (!UserUtil.checkNick(userName)) {
				json(SystemCode.code_1001, L("手机已注册，请重新填写。"));
				return;
			}
			String userIp = ip();
			if (!userIp.equals("127.0.0.1")) {
				Query<User> q = userDao.getQuery(User.class).filter("loginIp =", userIp);
				// //
				long count = userDao.find(q).countAll();
				log.error("ip：" + userIp + ",次数：" + count + ",你所在的ip跟踪程序已启动，如有恶意请停止，如果报警有误，请联系网站在线客服...");
				if (count >= 300) {
					json(SystemCode.code_1001, L("您好，系统监测到您的IP可能存在大量刷注册推荐人奖励行为，因此本次注册不能被通过。如果您是真实用户，请联系我们客服，给您造成的不便敬请谅解！您当前IP：") + userIp);
					return;
				}
			}

			// 检查短信验证码
			ClientSession clientSession = new ClientSession(userIp, mobileNumber, lan, PostCodeType.register.getValue(), false);
			DataResponse dr = clientSession.checkCode(dynamicCode);
			if(!dr.isSuc()){
				json(SystemCode.code_1024, L(dr.getDes()));
				return;
			}

			String recommenders = "";
			String recommId = "";
			AppRecommendDao arDao = new AppRecommendDao();
			AppRecommend ar = arDao.getByField("phoneNumber", userName);
			if (null != ar) {
				recommId = ar.getRecommendId();
				recommenders = ar.getRecommendName();
			}

			User user = new User(userDao.getDatastore());
			user.setLanguage(lan);
			user.setCurrency(GlobalConfig.currency);
			user.setMarket(GlobalConfig.market);
			user.setCurrencyN(GlobalConfig.currencyN);
			user.setLoginIp(ip());
			user.setDeleted(false);
			user.setModifyTimes(0);
			user.setUserName(userName);
			user.setPwd("");
			user.setPwdLevel(0);
			user.setSafePwd("");
			user.setSafeLevel(0);
			user.setRecommendId(recommId);
			user.setRecommendName(recommenders);
			user.setLastLoginTime(TimeUtil.getNow());

			UserContact uc = new UserContact();
			uc.setMobileCode(dynamicCode);
			uc.setmCode(countryCode);
			uc.setSafeMobile(mobileNumber);
			uc.setMobileStatu(AuditStatus.pass.getKey());
			user.setUserContact(uc);

			String nid = userDao.addUser(user);
			if (null != nid) {
				user.set_Id(nid);
				UpdateResults<User> ur = userDao.updatePwd(nid, password, 0);
				Cache.Delete("md5CurrentCodeImage_" + sessionId);

				User userLs = userDao.get(user.getRecommendId());

				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> userMap = getUserInfo(user);

				//生成token 发送给用户
				String token = MD5.toMD5(nid + UUID.randomUUID().toString());
				String loginCacheKey = appLoginCache + nid;
				SessionUser su = new SessionUser();
				su.uid = nid;//用户id
				su.uname = userName;//用户名
				su.ltime = System.currentTimeMillis();//登录时间
				su.lip = userIp;//登录ip
				su.lastTime = su.ltime;//最后活动时间
				su.token = token;
				
				Cache.SetObj(loginCacheKey, su, 30 * 24 * 60 * 60);
				
				map.put("token", token);
				map.put("userInfo", userMap);
				map.put("userId", nid);
				json(SystemCode.code_1000, map);
				MsgDao.sendMsg(nid, userName, TipType.registerSuc);

				uld.add(user.getRealName(), user.getId(), user.getUserName(), ip(), 2, loginTerminal);// 保存登录IP
				} else {
					json(SystemCode.code_1001);
				}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	UserLoginIpDao uld = new UserLoginIpDao();

	*//**
	 * 登录
	 *//*
	@Page(Viewer = JSON)
	public void login() {
		setLan();
		try {
			//【第一步】接收参数

			*//**
			* **请求参数**

			  | 参数名      | 类型     | 是否必须 | 描述         |
			  | :------- | :----- | :--- | :--------- |
			  | userName | String | 是    | 用户名/手机号/邮箱 |
			  | password | String | 是    | 登录密码（RSA加密）       |
			  | dynamicCode| String | 否    | 短信验证码（RSA加密）服务端提示本次登录为异地登录时需要此参数|
			  | googleCode| String | 否| 谷歌验证码（RSA加密）开启了谷歌登录验证时需要此参数    |

			  **//*
			String userName = param("userName");
			String password = param("password");
			String googleCode = param("googleCode");
			String dynamicCode = param("dynamicCode");

			String countryCode = param("countryCode");
			if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
				countryCode = "+86";
			}

			//【第二步】解密RSA参数
			password = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password), priKey));
			String realGACode = "";
			if (!StringUtils.isBlank(googleCode)) {
				realGACode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey));
			}
			String realMobileCode = "";
			if (!StringUtils.isBlank(dynamicCode)) {
				realMobileCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));
			}

			if (userName != null)
				userName = userName.toLowerCase().trim();
			else {
				json(SystemCode.code_3004);
				return;
			}

			//【第三步】查询用户资料
			User safeUser = null;
			int loginType = 0;
			userName = userName.toLowerCase().trim();
			if(CheckRegex.isEmail(userName)){
				loginType = 1;//邮箱
				safeUser = userDao.getUserByColumn(userName, "userContact.safeEmail");
			}else if(CheckRegex.isPhoneNumber(countryCode + " " + userName)){
				loginType = 2;//手机
				safeUser = userDao.getUserByColumn(countryCode + " " + userName, "userContact.safeMobile");
			}
			if (null == safeUser) {
				safeUser = userDao.getUserByColumn(userName, "userName");
			}

			// 如果是邮箱登录，且找到了用户，判断邮箱是否已认证
			if (loginType == 1 && null == safeUser) {
				safeUser = userDao.getUserByColumn(userName, "email");
				if (null != safeUser && StringUtils.isBlank(safeUser.getUserContact().getSafeEmail()) && safeUser.getUserContact().isCanReg()) {
					password = safeUser.getEncryptedPwd(password);
					if (!safeUser.getPwd().equals(password)) {
						json(SystemCode.code_1008);
						return;
					}
					// 跳转到查看邮件提示页面
					//json(VIP_DOMAIN + "/user/emailTips?nid=" + safeUser.getId() + "&type=2", false, "{\"emailTips\":true}", true);
					Map<String, Object> retMap = new HashMap<String, Object>();
					retMap.put("userId", safeUser.getId());
					json(SystemCode.code_1025, "登录失败，您的账号未激活，请登录邮箱进行账号激活后重新登录。", retMap);
					return;
				} else if (null != safeUser && StringUtils.isBlank(safeUser.getUserContact().getSafeEmail()) && !safeUser.getUserContact().isCanReg()) {
					Map<String, Object> retMap = new HashMap<String, Object>();
					retMap.put("userId", safeUser.getId());
					json(SystemCode.code_1025, "登录失败，您的账号过期未激活，请重新获取激活邮件。", retMap);
					return;
				}
			}

			//用户找不到，返回找不到用户
			if (null == safeUser) {
				json(SystemCode.code_3004);
				return;
			}

			//【第五步】加密密码与数据库的密码字段判断
			String encryptionPwd = safeUser.getEncryptedPwd(password);
			if (!encryptionPwd.equals(safeUser.getPwd())) {
				json(SystemCode.code_1001, L("用户名或密码错误"));
				return;
			}

			if(safeUser.isFreez()){
				json(SystemCode.code_1001, L("该账户已冻结，暂时不能登录。"));
				return;
			}

			toLogin(safeUser.getId(), safeUser.getUserName(), ip());

			//封装用户对象数据到json
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> userMap = getUserInfo(safeUser);

			map.put("userInfo", userMap);

			//【第六步】判断IP是否异地登录
			boolean diffIpAuthen = false;
			boolean isGACodePassed = true;		//是否通过google认证
			boolean isMobileCodePassed = true;		//是否通过短信认证
			boolean needGACode = false;			//需要google验证
			boolean needDynamicCode = false;			//需要短信验证
			if (!safeUser.isDiffAreaLoginNoCheck()) {
				Object obj = uld.getLoginCache(sessionId, ip());//查一查是不是异地登录
				if (null != obj || uld.needCheckMobile(safeUser, ip())) {//发现是，就不保存本次IP地址↙
					diffIpAuthen = true;
				}
			}
//			diffIpAuthen = true;//测试，设为异地登录

			DataResponse dr = null;

			//【第七步】判断Google验证码是否正确
			if(StringUtils.isBlank(realGACode)) {
				//是否开启Google登录验证
				if(safeUser.isLoginGoogleAuth()) {// google和短信仅需要验证其中一个
					isGACodePassed = false;
					needGACode = true;
				} else {
					isGACodePassed = true;
				}
			} else {
				//判断google验证码是否正确
				long gCode = CommonUtil.stringToLong(realGACode);
				if (isGoogleCodeCorrect(safeUser.getUserContact().getSecret(), gCode, safeUser.get_Id())) {
					isGACodePassed = true;
				} else {
					isGACodePassed = false;
				}
				
			}
			
			if (diffIpAuthen && !safeUser.isLoginGoogleAuth()) {// 如果需要Google验证码就不需要短信验证
				//异地登录处理

				//【第八步】判断短信验证码是否正确
				if(StringUtils.isBlank(realMobileCode)) {
					//是否开启异地登录
					if(!safeUser.isDiffAreaLoginNoCheck()) {
						isMobileCodePassed = false;
						needDynamicCode = true;
					} else {
						isMobileCodePassed = true;
					}
				} else {

					// 检查短信验证码
					String sendAddr = safeUser.getUserContact().getSafeMobile();
					if (StringUtils.isBlank(sendAddr)) {
						sendAddr = safeUser.getUserContact().getSafeEmail();
					}
					ClientSession clientSession = new ClientSession(ip(), sendAddr, lan, PostCodeType.diffIpAuth.getValue(), false);
					dr = clientSession.checkCode(realMobileCode);
					if(dr.isSuc()){	//正确
						isMobileCodePassed = true;
					}else {//验证码不正确
						isMobileCodePassed = false;
					}

				}

			}

			*//********************************  最后的结果验证，返回对应值 ********************************//*
			List<String> exceptions = new ArrayList<String>();
			exceptions.add("mobileNumber");
			exceptions.add("email");
			exceptions.add("countryCode");
			exceptions.add("userId");
			
			//【第九步】是否需要进入登录二次验证
//			if(needGACode && needDynamicCode) {
//				//1020
//				toNewLocationLogin(safeUser.getId(), safeUser.getUserName(), ip());
//				
//				//clear userInfo
//				emptyValues(userMap,exceptions);
//				
//				map.put("token", newLocationToken(safeUser.getId()));
//				map.put("login2", getlogin2Info(needGACode, needDynamicCode));
//				json(SystemCode.code_1020, map);
//				return;
//			} else {
			if(needGACode) {
				
				//clear userInfo,except the required informations
				emptyValues(userMap,exceptions);
				
				//1018
				map.put("login2", getlogin2Info(needGACode, needDynamicCode));
				json(SystemCode.code_1018, map);
				return;
			} else if(needDynamicCode) {
				//1017
				toNewLocationLogin(safeUser.getId(), safeUser.getUserName(), ip());
				
				//clear userInfo
				emptyValues(userMap,exceptions);
				
				map.put("token", newLocationToken(safeUser.getId()));
				map.put("login2", getlogin2Info(needGACode, needDynamicCode));
				json(SystemCode.code_1017, map);
				return;
			}
//			}

			//【第十步】是否通过登录二次验证
			if (isMobileCodePassed && isGACodePassed) {
				//通过登录
				//保存登录IP
				uld.add(userName, safeUser.getId(), safeUser.getUserName(), ip(), 2, loginTerminal);

				map.put("token", token(safeUser.getId()));
				try {
					checkCountryCode(safeUser.getId());
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				json(SystemCode.code_1000, map);
				return;
			} else {
				if(!isMobileCodePassed) {
					//短信不通过验证
					json(SystemCode.code_1024, dr.getDes());
					return;
				}

				if(!isGACodePassed) {
					//Google不通过验证
					json(SystemCode.code_1001, L("谷歌验证码错误!"));
					return;
				}
			}

			*//********************************  end ********************************//*

		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	
	private Map<String, Object> getlogin2Info(boolean needGACode, boolean needDynamicCode) {
		Map<String, Object> login2 = new HashMap<String, Object>();
		login2.put("needGoogleCode", needGACode==true?1:0);
		login2.put("needDynamicCode", needDynamicCode==true?1:0);
		return login2;
	}
	private void emptyValues(Map<String, Object> inner, List<String> exceptions) {
		// TODO Auto-generated method stub
		for (String key : inner.keySet()) {
			if(exceptions.contains(key))
				continue;
			inner.put(key,"");
		}
	}

	public void checkCountryCode(String uid) {
		setLan();
		User u = userDao.getById(uid);
		if (null == u) {
			return;
		}
		UserContact uc = u.getUserContact();
		if (null == uc) {
			return;
		}
		if (uc.getMobileStatu() == 2 && StringUtils.isBlank(uc.getmCode()) && !uc.getSafeMobile().startsWith("+")
				&& CheckRegex.isPhoneNumber("+86 " + uc.getSafeMobile())) {
			
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", uid);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("userContact.mCode", "+86");
			ops.set("userContact.safeMobile", "+86 " + uc.getSafeMobile());
			
			userDao.update(q, ops);
		}
	}

	*//**
	 * 发送短信验证码
	 *//*
	@Page(Viewer = JSON)
	public void sendCode(){
		setLan();
		try {
			boolean isEmail = false;
			String countryCode = param("countryCode");
			if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
				countryCode = "+86";
			}
			String mobileNumber = "", email = "";
			String encryptNumber = param("encryptNumber").trim();
			String encryptEmail = param("encryptEmail").trim();
			if (StringUtils.isBlank(encryptNumber) && StringUtils.isBlank(encryptEmail)) {
				json(SystemCode.code_1001, L("未填写接收地址"));
				return;
			}
			
			if (StringUtils.isBlank(encryptNumber)) {
				email = StringUtils.isNotBlank(encryptEmail)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptEmail), priKey)).toLowerCase():"";
				if (StringUtils.isNotBlank(email) && !CheckRegex.isEmail(email)) {
					json(SystemCode.code_1001, L("邮箱格式不正确，请重新填写"));
					return;
				}
				isEmail = true;
			} else {
				mobileNumber = StringUtils.isNotBlank(encryptNumber)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptNumber), priKey)):"";
				if(StringUtils.isNotBlank(mobileNumber) && !CheckRegex.isPhoneNumber(countryCode + " " + mobileNumber)){
					json(SystemCode.code_3005, L("请输入正确的手机号！"));
					return;
				}
			}
			boolean graphicalCode = false;//是否有图形验证码
			
			int icodeType = intParam("type");
			PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
			String codeType = postCodeType.getValue();
			
			if(isForbid()){
				return;
			}

			if (session == null) {
				SsoSessionManager.initSession(this);
			}

			if(sessionId == null){
				json(SystemCode.code_1001, L("系统出错了，请稍后重试"));
				return;
			}

			String mNumber = countryCode + " " + mobileNumber;

			String ip = ip();
			String receiveAddr = isEmail ? email : mNumber;
			ClientSession clientSession = new ClientSession(ip, receiveAddr, lan, codeType, graphicalCode);
//			clientSession.rs = resoureRequest;
			clientSession.rs = 1;

			DataResponse dr = clientSession.checkSend();//检测当前客户端是否能够发送

			if(!dr.isSuc()){
				json(SystemCode.code_1001, dr.getDes());
				return;
			}//测试，暂时不验证这步

			//当前ip验证是否注册过的所有手机号码24h不得超过x个
			clientSession.addCheckNumber();

			Map<String, Object> extraValidations = new HashMap<String, Object>();
			if (icodeType == 1) {
				boolean res = userDao.mobileValidated(mNumber);
				if (!res) {
					json(SystemCode.code_3005, L("手机已注册！"));
					return;
				}
			} else {//发这个动态码不是为了注册的，说明这是已注册用户发的,找一找有无此用户
				User registeredUser = null;
				if (isEmail) {
					registeredUser = userDao.getUserByColumn(email, "userContact.safeEmail");
				} else {
					registeredUser = userDao.getUserByColumn(mNumber, "userContact.safeMobile");
				}
				if (null == registeredUser) {
					json(SystemCode.code_3004);
					return;
				} else {
					//是否开启Google登录验证
					boolean needGACode = false;
					if(registeredUser.isLoginGoogleAuth()) {//google和短信仅需要验证其中一个
						needGACode = true;
					}
					extraValidations = getlogin2Info(needGACode, false);
				}
				
			}

			String dynamicCode = MobileDao.GetRadomStr();
			if (!isEmail) {
				MobileDao mDao = new MobileDao();
				User user = userDao.getUserByColumn(mNumber, "userContact.safeMobile");
				if (null == user) {
					user = new User();
					user.set_Id("");
				}
				String encryptMobileCode = MD5.toMD5( MD5.toMD5(mobileNumber) + MD5.toMD5(dynamicCode) );
				if (mDao.sendSms(new User(), ip(), postCodeType.getValue(), L(postCodeType.getDes()) + dynamicCode, mNumber)) {
					if (clientSession.sendCode(dynamicCode)) {
						log.info("APP短信验证码：" + dynamicCode);
						Map<String, Object> reData = new HashMap<String, Object>();
						reData.put("dynamicCode", encryptMobileCode);
						reData.put("userId", user.get_Id());
						reData.put("login2", extraValidations);
						json(SystemCode.code_1000, L("短信验证码已发送到您的手机，10分钟内有效"), reData);
						return;
					}
				}
			} else {
				EmailDao eDao = new EmailDao();
				User user = userDao.getByField("userContact.safeEmail", email);
				if (null == user) {
					user = new User();
					user.setUserName(email);
					user.set_Id("");
				}
				lan = "cn";
				String info = eDao.getCodeEmailHtml(user, dynamicCode, this);
				int iResult = eDao.sendEmail(ip, user.getId(), email, "邮箱验证码", info, email);
				if (iResult == 1) {
					if(clientSession.sendCode(dynamicCode)){
						log.info("APP邮件验证码：" + dynamicCode);
						Map<String, Object> retData = new HashMap<String, Object>();
						retData.put("isEmailCode", "1");
						String encryptEmailCode = MD5.toMD5( MD5.toMD5(email) + MD5.toMD5(dynamicCode) );
						retData.put("dynamicCode", encryptEmailCode);
						retData.put("userId", user.get_Id());
						retData.put("login2", extraValidations);
						json(SystemCode.code_1000, L("验证码已发送到您的邮箱%%，请登录邮箱查看，10分钟内有效。", userDao.shortEmail(email)), retData);
						return;
					} else {
						json(SystemCode.code_1001, L("发送失败，请稍后重试"));
						return;
					}
				} else if (iResult == 2) {
					json(SystemCode.code_1001, L("您今天发送的验证码已超过限制。"));
					return;
				} else {
					json(SystemCode.code_1001, L("发送失败，请稍后重试"));
					return;
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void userSendCode(){
		setLan();
		try {
			boolean graphicalCode = true;
			int icodeType = intParam("type");
			String userId = param("userId");
			String token = param("token");

			if (icodeType == 65) {
				if (!newLocationTokenCheck(userId, token)) {
					json(SystemCode.code_1003);
					return;
				}
			} else {
				if (!isLogin(userId, token)) {
					json(SystemCode.code_1003);
					return;
				}
			}

			User user = userDao.getById(userId);
			if (null == user) {
				json(SystemCode.code_3004);
				return;
			}
			PostCodeType postCodeType = (PostCodeType) EnumUtils.getEnumByKey(icodeType, PostCodeType.class);
			String codeType = postCodeType.getValue();
			
			if(isForbid()){
				return;
			}
			
			String currency = param("currency");
			if(currency.length() > 0){
				currency = currency.toUpperCase();
			}
			
			int sendType = 1;
			String sendAddr = user.getUserContact().getSafeMobile();

			if(null == sendAddr || "".equals(sendAddr)){
//				json(SystemCode.code_1001, L("您还没有进行手机认证，请进行手机认证后重试"));
//				return;
				sendAddr = user.getUserContact().getSafeEmail();
				sendType = 2;
			}

			String ip = ip();

			ClientSession clientSession = new ClientSession(ip, sendAddr, lan, codeType, graphicalCode);
			clientSession.rs = resoureRequest;

			DataResponse dr = clientSession.checkSend();//检测当前客户端是否能够发送

			if(!dr.isSuc()){
				json(SystemCode.code_1001, dr.getDes());
				return;
			}//测试，暂时不验证这步

			//当前ip验证是否注册过的所有手机号码24h不得超过x个
			clientSession.addCheckNumber();
			String radomCode = MobileDao.GetRadomStr();
			if (sendType == 1) {
				
				MobileDao mDao = new MobileDao();
				String des = String.format(L(postCodeType.getDes(), currency), radomCode);
				if(mDao.sendSms(user, ip, L(codeType, currency), des, sendAddr)){
					if(clientSession.sendCode(radomCode)){
						log.info("APP短信验证码：" + des);
						json(SystemCode.code_1000, L("短信验证码已发送到您的手机，10分钟内有效"));
						return;
					}
				}
			} else {
				EmailDao eDao = new EmailDao();
				String info = eDao.getCodeEmailHtml(user, radomCode, this);
				int iResult = eDao.sendEmail(ip, user.getId(), user.getUserName(), "邮箱验证码", info, sendAddr);
				if (iResult == 1) {
					if(clientSession.sendCode(radomCode)){
						log.info(radomCode);
						Map<String, Object> retData = new HashMap<String, Object>();
						retData.put("isEmailCode", "1");
						json(SystemCode.code_1000, L("验证码已发送到您的邮箱%%，请登录邮箱查看，10分钟内有效。", userDao.shortEmail(sendAddr)), retData);
						return;
					} else {
						json(SystemCode.code_1001, L("发送失败，请稍后重试"));
						return;
					}
				} else if (iResult == 2) {
					json(SystemCode.code_1001, L("您今天发送的验证码已超过限制。"));
					return;
				} else {
					json(SystemCode.code_1001, L("发送失败，请稍后重试"));
					return;
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}


	*//**
	 * 手机短信修改密码
	 *//*
	@Page(Viewer = JSON)
	public void changePwd() {
		setLan();
		try {
			int method = intParam("method");
			String countryCode = param("countryCode");
			String mobileNumber = param("mobileNumber");
			String dynamicCode = param("dynamicCode");
			dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));
			String newPassword = param("newPassword");
			String email = param("email");
			String googleCode = param("googleCode");
			
			googleCode = StringUtils.isNotBlank(googleCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey)):"";
			newPassword = StringUtils.isNotBlank(newPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(newPassword), priKey)):"";

			User user = null;
			ClientSession clientSession = null;
			Query<User> q = userDao.getQuery(User.class);
			if (method == 1) {
				if (null == countryCode || "".equals(countryCode) || !countryCode.startsWith("+")) {
					countryCode = "+86";
				}
				if (!mobileNumber.startsWith("+")) {
					mobileNumber = countryCode + " " + mobileNumber;
				}
				// 检查短信验证码
				clientSession = new ClientSession(ip(), mobileNumber, lan, PostCodeType.resetPassword.getValue(), false);
				q.filter("userContact.safeMobile", mobileNumber);
			} else {
				// 检查邮件验证码
				clientSession = new ClientSession(ip(), email, lan, PostCodeType.resetPassword.getValue(), false);
				q.filter("userContact.safeEmail", email);
			}
			DataResponse dr = clientSession.checkCode(dynamicCode);
			if(!dr.isSuc()){
				json(SystemCode.code_1024, dr.getDes());
				return;
			}
			user = userDao.findOne(q);
			if (null == user) {
				json(SystemCode.code_3004);
				return;
			} else {
//				boolean needGACode = false;
				if(user.isLoginGoogleAuth()) {//该用户开启了谷歌登录验证
//					needGACode = true;
					long gCode = CommonUtil.stringToLong(googleCode);
					if (!isGoogleCodeCorrect(user.getUserContact().getSecret(), gCode, user.get_Id())) {
						return;
					}
				}
			}

			int safeLevel = 0;
			if (newPassword.length() > 8)
				safeLevel = 85;
			else
				safeLevel = 50;

			UpdateResults<User> ur = userDao.updatePwd(user.get_Id(), newPassword, safeLevel);
			if (ur.getHadError()) {
				json(SystemCode.code_1001, L("修改密码出错，请稍后重试。"));
				return;
			}
			toLogin(user.getId(), user.getUserName(), ip());

			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> userMap = getUserInfo(user);
			map.put("userInfo", userMap);
			map.put("token", token(user.getId()));
			json(SystemCode.code_1000, map);
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void getTradeData() {
		setLan();
		String symbol = param("symbol");

		if (StringUtils.isBlank(symbol))
			symbol = "btc";

		symbol = symbol.toLowerCase();

		try {

			String key = "BTC123_TRADE_DATA_";

			String data = Cache.Get(key + symbol);

			if (StringUtils.isBlank(data)) {

				String url = "https://www.btc123.com/api/btcTrade";
				if ("ltc".equalsIgnoreCase(symbol))
					url = "https://www.btc123.com/api/ltcTrade";
				String callback = HttpUtil.doGet(url, null, 1500, 2000);
				if (StringUtils.isNotBlank(callback) && callback.startsWith("{")) {
					com.alibaba.fastjson.JSONObject json = com.alibaba.fastjson.JSONObject.parseObject(callback);
					String datas = json.getJSONArray("datas").toJSONString();
					if (StringUtils.isBlank(datas)) {
						datas = "[]";
					} else {
						// 缓存30秒
						Cache.Set(key + symbol, datas, 30);
					}
					data = datas;
				}
			}

			if (StringUtils.isBlank(data))
				data = "[]";

			Map<String, Object> reData = new HashMap<String, Object>();
			reData.put("marketChartData", reData);
			json(SystemCode.code_1000, reData);
		} catch (IOException e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	*//**
	 * http://www.vip.com/api/m/getAccountData
	 *//*
	@Page(Viewer = JSON)
	public void getAccountData() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			int userIdInt = Integer.parseInt(userId);
			User user = userDao.getById(userId);
			if(user.isFreez()){
				json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
				return;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> userMap = getUserInfo(user);
			map.put("token", token);
			
			map.put("userInfo", userMap);

			Map<String, JSONArray> accountMap = getUserAssetInfo(user);

			map.put("userAccount", accountMap);

			json(SystemCode.code_1000, map);
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	
	@Page(Viewer = JSON)
	public void getUserInfo() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User user = userDao.getById(userId);
			if(user.isFreez()){
				json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
				return;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> userMap = getUserInfo(user);
			map.put("token", token);
			
			map.put("userInfo", userMap);
			
			json(SystemCode.code_1000, map);
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	
	@Page(Viewer = JSON)
	public void getUserAssets() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User user = userDao.getById(userId);
			if(user.isFreez()){
				json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
				return;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("token", token);
			
			Map<String, JSONArray> accountMap = getUserAssetInfo(user);
		//	JSONArray banlanceArr = accountMap.get("balances");
			
			P2pUser p2pUser = new P2pUser();
			p2pUser.setUserId(userId);
			Map<String, PayUserBean> payUsers = UserCache.getUserFundsLoan(p2pUser.getUserId());
			
			JSONObject prices = LoanAutoFactory.getPrices();
			p2pUserDao.resetAsset(payUsers, prices, p2pUser);//计算用户的总资产
			map.put("totalAmount", p2pUser.getTotalAssets());//折合总资产
			map.put("userAccount", accountMap);

			json(SystemCode.code_1000, map);
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	
	private Map<String, JSONArray> getUserAssetInfo(User user) {
		
		String userIdStr = user.get_Id();
		
		Map<String, JSONArray> accountMap = new HashMap<String, JSONArray>();

		JSONArray funds = UserCache.getUserFunds(userIdStr);//getBalanceArray();
		
		for(int i=0;i<funds.size();i++){
			JSONObject json = funds.getJSONObject(i);
			String tag = json.getString("propTag");
			if(tag!=null ){
				if(tag.equals("ETH")){
					json.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_type_sm@3x.png");
					json.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_finance.png");
				}else if(tag.equals("ETC")){
					json.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_type_sm@3x.png");
					json.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_finance.png");
				}else{//其他图标还没提供
					json.put("coinUrl", "");
					json.put("financeCoinUrl", "");
				}
			}
		}
		
		accountMap.put("balances", funds);
		return accountMap;
	}
	
	private Map<String, Object> getUserInfo(User user) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		
		String userId = user.get_Id();
		
		userMap.put("userId", userId);
		userMap.put("userName", user.getUserName());
		
		String mobile = user.getUserContact().getSafeMobile();
		if(StringUtils.isNotBlank(mobile) && mobile.startsWith("+")){
			String[] arr = mobile.split(" ");
			if(arr.length>1){
				mobile = arr[1];//增加过滤手机号前面的国家区号
			}
		}
		userMap.put("mobileNumber", mobile);
		userMap.put("email", StringUtils.isBlank(user.getUserContact().getSafeEmail())?"":user.getUserContact().getSafeEmail());

//		isHadSecurePassword String 是 是否设置有资金密码：0：否，1：是
//		isCloseSecurePassword String 是 是否交易时关闭资金密码：0：否，1：是
		int isHadSecurePassword = user.getHasSafePwd()?1:0;
		userMap.put("isHadSecurePassword", isHadSecurePassword);
		boolean isNeedSafePwd = userDao.isNeedSafePwd(user);
		int safePwdPeriod = 0;
		if (isNeedSafePwd) {
			safePwdPeriod = 1;
		}
		userMap.put("safePwdPeriod", safePwdPeriod);

		//			| juaUserId | String| 是    | | 为空时表示未绑定JUA|
		//			| bwUserId | String| 是    | | 为空时表示未绑定bw|
		//			| realName | String| 是    | | 为空时表示未认证|
		//			| googleAuth | Integer| 是    | 1| 1已通过谷歌认证0未通过谷歌认证|

		int googleAuth = user.getUserContact().getGoogleAu();//谷歌双重验证是否开启(0未验证     1验证未开启     2已开启)
		userMap.put("googleAuth", googleAuth==AuditStatus.pass.getKey()?1:0);

		Authentication authentication = auDao.getByUserId(userId);
		if(null == authentication){
			userMap.put("identityAuthStatus", AuditStatus.a1NoSubmit.getKey());
			userMap.put("realName", "");
		} else {
			userMap.put("identityAuthStatus", authentication.getStatus());
			userMap.put("realName", StringUtils.isBlank(authentication.getRealName())?"":authentication.getRealName());
		}

		userMap.put("loginSmsCheck", user.isDiffAreaLoginNoCheck()?0:1);
		userMap.put("loginGoogleAuth", user.isLoginGoogleAuth()?1:0);
		userMap.put("payGoogleAuth", user.isPayGoogleAuth()?1:0);
		userMap.put("paySmsAuth", user.isPayMobileAuth()?1:0);
		userMap.put("userOpenId", userOpenId(user));
		userMap.put("loginAuthenType", user.getLoginAuthenType());
	    userMap.put("tradeAuthenType", user.getTradeAuthenType());
	    userMap.put("withdrawAuthenType", user.getWithdrawAuthenType());
		CommonUtil.nullToEmpty(userMap);
		return userMap;
	}

	*//**
	 * 修改密码
	 *//*
	@Page(Viewer = JSON)
	public void resetPwd() {
		setLan();
		int type = intParam("type");//1 login password 2 transaction password

		if (type == 1) {
			resetLoginPwd();
		} else if (type == 2) {
			resetSafePwd();
		} else {
			json(SystemCode.code_1019);
		}
	}
	*//**
	 * http://www.vip.com/api/m/resetPwd
	 *//*
	@Page(Viewer = JSON)
	public void resetLoginPwd() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			String oldPassword = param("oldPassword");
			String newPassword = param("newPassword");
			String googleCode = param("googleCode");
			String dynamicCode = param("dynamicCode");

			//【第二步】解密RSA参数
			oldPassword = StringUtils.isNotBlank(oldPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(oldPassword), priKey)):"";
			newPassword = StringUtils.isNotBlank(newPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(newPassword), priKey)):"";
			googleCode = StringUtils.isNotBlank(googleCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey)):"";
			dynamicCode = StringUtils.isNotBlank(dynamicCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey)):"";
			
			int needDynamicCode = 0;
			int needGoogleCode = 0;
			Map<String, Object> retData = new HashMap<String, Object>();
			retData.put("needDynamicCode", needDynamicCode);
			retData.put("needGoogleCode", needGoogleCode);
			
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003, retData);
				return;
			}
			User user = userDao.getUserById(userId);
			if(user.isFreez()){
				json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"), retData);
				return;
			}
			
			boolean isOldPwdExisted = true;
			if(user.getPwdLevel() == 0 || StringUtils.isBlank(user.getPwd()) ){
				isOldPwdExisted = false;
			}
			//有密码 但 客户端没有传新旧两个密码
			if (isOldPwdExisted && (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)) ) {
				json(SystemCode.code_1001, L("新旧密码都要输入噢!"), retData);
				return;
			}
			
			LimitType lt = LimitType.SafePassError;
			
			if(isOldPwdExisted){
				String checkPwd = user.getEncryptedPwd(oldPassword);
				if(lt.GetStatus(userId) == -1){
					json(SystemCode.code_1001, L("密码已锁定"), retData);
					return;
				}

				if(!checkPwd.equals(user.getPwd())){
					lt.UpdateStatus(userId);
					json(SystemCode.code_1001, L("原始密码不正确"), retData);
					return;
				}

				if(checkPwd.equals(user.getEncryptedPwd(newPassword))){
					json(SystemCode.code_1001, L("修改后的密码不能和原密码一致。"), retData);
					return;
				}
			}
			
			UserContact userContact = user.getUserContact();
			
			boolean isGoogleRequired = false;			//需要google验证
			boolean isDynamicCodeRequired = false;			//需要短信验证
			boolean isGACodePassed = true;		//是否通过google认证
			boolean isDynamicCodePassed = true;		//是否通过短信认证
			
			//【第八步】判断Google验证码是否正确
			if(StringUtils.isBlank(googleCode)) {
				//是否开启Google登录验证
				if(userContact.getGoogleAu() == AuditStatus.pass.getKey()) {
					isGACodePassed = false;
					isGoogleRequired = true;
				} else {
					isGACodePassed = true;
				}
			} else {
				//判断google验证码是否正确
				long gCode = CommonUtil.stringToLong(googleCode);
				if (isGoogleCodeCorrect(userContact.getSecret(), gCode, user.get_Id())) {
					isGACodePassed = true;
				} else {
					isGACodePassed = false;
				}
			}
			
			//【第八步】判断dynamicCode是否正确
			DataResponse dr = null;
			if(StringUtils.isBlank(dynamicCode)) {
				//硬性要求，要这个dynamicCode
				isDynamicCodePassed = false;
				isDynamicCodeRequired = true;
			} else {
				//判断DynamicCode是否正确
				String codeRecvAddr = userContact.getSafeMobile();//找一找有没有手机号码
				if (StringUtils.isBlank(codeRecvAddr))
					codeRecvAddr = user.getUserContact().getSafeEmail();//没找到手机号码那就只有邮箱了，用来验证
				
				ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
				dr = clientSession.checkCode(dynamicCode);
				if(dr.isSuc()){	//正确
					isDynamicCodePassed = true;
				}else {//验证码不正确
					isDynamicCodePassed = false;
				}
			}
			
			//【第九步】是否需要进入二次验证
			if(isGoogleRequired && isDynamicCodeRequired) {
				//1020
				needDynamicCode = 1;
				needGoogleCode = 1;
				json(SystemCode.code_1020, retData);
				return;
			} else {
				if(isGoogleRequired) {
					needGoogleCode = 1;
					json(SystemCode.code_1018, retData);
					return;
				} else if(isDynamicCodeRequired) {
					needDynamicCode = 1;
					json(SystemCode.code_1017, retData);
					return;
				}
			}
			
			//【第十步】是否通过二次验证
			if (isDynamicCodePassed && isGACodePassed) {
				//开始改密
				int safeLevel = 0;
				if(newPassword.length()>8) safeLevel = 85; else safeLevel = 50;

				UpdateResults<User> ur = userDao.updatePwd(userId, newPassword, safeLevel);

				if (!ur.getHadError()) {
					logDao.insertOneRecord(AuthenType.modifyPwd.getKey(), userId()+"", "0", "成功修改登录密码。", ip());
					json(SystemCode.code_1000);
				} else {
					json(SystemCode.code_1001, retData);
				}

			} else {
				if(!isDynamicCodePassed) {
					//短信不通过验证
					json(SystemCode.code_1024, dr.getDes(), retData);
					return;
				}

				if(!isGACodePassed) {
					//Google不通过验证
					json(SystemCode.code_1001, L("谷歌验证码错误!"), retData);
					return;
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	*//**
	 * http://www.vip.com/api/m/resetSafePwd
	 *//*
	@Page(Viewer = JSON)
	public void resetSafePwd() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			String oldPassword = param("oldPassword");
			String newPassword = param("newPassword");
			String googleCode = param("googleCode");
			String dynamicCode = param("dynamicCode");

			//【第二步】解密RSA参数
			oldPassword = StringUtils.isNotBlank(oldPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(oldPassword), priKey)):"";
			newPassword = StringUtils.isNotBlank(newPassword)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(newPassword), priKey)):"";
			googleCode = StringUtils.isNotBlank(googleCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey)):"";
			dynamicCode = StringUtils.isNotBlank(dynamicCode)?new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey)):"";
			
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User user = userDao.getUserById(userId);
			if(user.isFreez()){
				json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
				return;
			}
			
			boolean isSet = false;
			int status = userDao.transCheckSecurityPwd(oldPassword, userId);
			if(status==-2){
				json(SystemCode.code_1001, L("资金安全密码已经被锁定"));
				return;
			}else if(status==-1){
				json(SystemCode.code_1001, L("您的原始资金安全密码不正确。"));
				return;
			}else if(status==0){
				isSet = true;
			}
			
			String safePwdRegex = "^(?![0-9]+$)(?![a-zA-Z]+$)(?![*[#@!~%^&*]]+$)[0-9A-Za-z*[#@!~%^&*]]{8,16}$";
			
			if(!newPassword.matches(safePwdRegex)){
				json(SystemCode.code_1001, L("资金安全密码建议由8-16位字母、数字和特殊符号组成，不能是纯数字或字母!"));
				return;
			}
			
			if(user.getSafePwd().equals(user.getEncryptedPwd(newPassword))){
				json(SystemCode.code_1001, L("修改后的密码不能和原密码一致。"));
				return;
			}
			
			UserContact userContact = user.getUserContact();
			
			boolean isGoogleRequired = false;			//需要google验证
			boolean isDynamicCodeRequired = false;			//需要短信验证
			boolean isGACodePassed = true;		//是否通过google认证
			boolean isDynamicCodePassed = true;		//是否通过短信认证
			
			//【第八步】判断Google验证码是否正确
			if(StringUtils.isBlank(googleCode)) {
				//是否开启Google登录验证
				if(userContact.getGoogleAu() == AuditStatus.pass.getKey()) {
					isGACodePassed = false;
					isGoogleRequired = true;
				} else {
					isGACodePassed = true;
				}
			} else {
				//判断google验证码是否正确
				long gCode = CommonUtil.stringToLong(googleCode);
				if (isGoogleCodeCorrect(userContact.getSecret(), gCode, user.get_Id())) {
					isGACodePassed = true;
				} else {
					isGACodePassed = false;
				}
			}
			
			//【第八步】判断dynamicCode是否正确
			DataResponse dr = null;
			if(StringUtils.isBlank(dynamicCode)) {
				//硬性要求，要这个dynamicCode
				isDynamicCodePassed = false;
				isDynamicCodeRequired = true;
			} else {
				//判断DynamicCode是否正确
				String codeRecvAddr = userContact.getSafeMobile();//找一找有没有手机号码
				if (StringUtils.isBlank(codeRecvAddr))
					codeRecvAddr = user.getUserContact().getSafeEmail();//没找到手机号码那就只有邮箱了，用来验证
				
				ClientSession clientSession = new ClientSession(ip(), codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
				dr = clientSession.checkCode(dynamicCode);
				if(dr.isSuc()){	//正确
					isDynamicCodePassed = true;
				}else {//验证码不正确
					isDynamicCodePassed = false;
				}
			}
			
			//【第九步】是否需要进入二次验证
			if(isGoogleRequired && isDynamicCodeRequired) {
				//1020
				json(SystemCode.code_1020);
				return;
			} else {
				if(isGoogleRequired) {
					json(SystemCode.code_1018);
					return;
				} else if(isDynamicCodeRequired) {
					json(SystemCode.code_1017);
					return;
				}
			}
			
			//【第十步】是否通过二次验证
			if (isDynamicCodePassed && isGACodePassed) {
				//开始改密
				int safeLevel = 0;
				if(newPassword.length()>8) safeLevel = 85; else safeLevel = 50;

				UpdateResults<User> ur = userDao.updateSecurityPwd(userId, newPassword, safeLevel);

				if (!ur.getHadError()) {
					logDao.insertOneRecord(AuthenType.modifySecurityPwd.getKey(), userId()+"", "0", "成功"+(isSet?"设置":"修改")+"资金安全密码。", ip());
					
					try {
						boolean isOldPwdExisted = user.getIsSafePwd();
						if (!isOldPwdExisted) {
						} else {
							MobileDao mDao = new MobileDao();
							PostCodeType postCodeType = PostCodeType.resetSafePwd;
							if (null != user.getUserContact().getSafeMobile()) {
								mDao.sendSms(user, ip(), postCodeType.getValue(), postCodeType.getDes(), user.getUserContact().getSafeMobile());
							}
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
					
					json(SystemCode.code_1000);
				} else {
					json(SystemCode.code_1001);
				}

			} else {
				if(!isDynamicCodePassed) {
					//短信不通过验证
					json(SystemCode.code_1024, dr.getDes());
					return;
				}

				if(!isGACodePassed) {
					//Google不通过验证
					json(SystemCode.code_1001, L("谷歌验证码错误!"));
					return;
				}
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void updateAppVersion(){
		setLan();
		String client = param("client");
		App app = appDao.findLastVesion(client);
		if(null==app){
			json(SystemCode.code_1001);
			return;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		//map.put("url", app.getUrl());
		if (build == app.getNum()) {
			json(SystemCode.code_1000, "您的应用已是最新版本!");
			return;
		}

		if("0".equals(Constants.modeKey))
			map.put("url", "https://www.vip.com/mobile/download");
		else if("1".equals(Constants.modeKey))
			map.put("url", "https://www.vip.com/mobile/download");

		map.put("version", app.getName());
		map.put("build", app.getNum()+"");
		map.put("description", app.getRemark());
		json(SystemCode.code_1000, map);
	}

	*//**
	 * 获取个人历史提现地址
	 * http://www.vip.com/api/m/getWithdrawAddress
	 *
	 *//*
	@Page(Viewer = JSON)
	public void getWithdrawAddress(){
		setLan();
		String userId = param("userId");
		String token = param("token");
		if (!isLogin(userId, token)) {
			json(SystemCode.code_1003);
			return;
		}
		String currencyType = param("currencyType");
		
		Map<String, CoinProps> cointMap = DatabasesUtil.getCoinPropMaps();
		if(!cointMap.containsKey(currencyType.toLowerCase())){
			currencyType = "BTC";
		}

		currencyType = currencyType.toLowerCase();

		String tableName = currencyType+"receiveAddr";
		String sql = "select * from "+tableName+" where userid=? and isdeleted = 0 order by createTime desc";
		List<Bean> addrs = Data.Query(sql, new Object[] { userId }, ReceiveAddr.class);

		Map<String, Object> addMap = new HashMap<String, Object>();

//		提现地址  WithdrawAddress
//		id String 是 地址ID
//		address String 是 地址
//		memo String 是 备注
		JSONArray ja = new JSONArray();
		if(null != addrs && addrs.size()>0){
			for (Bean bean : addrs) {
				JSONObject addJo = new JSONObject();
				ReceiveAddr addr = (ReceiveAddr) bean;
				addJo.put("id", addr.getId());
				addJo.put("address", addr.getAddress());
				addJo.put("memo", StringUtils.isBlank(addr.getMemo())?"":addr.getMemo());
				
				ja.add(addJo);
			}
		}
		addMap.put("withdrawAddrs", ja);
		json(SystemCode.code_1000, addMap);
	}
	*//**
	 * 获取个人充值地址
	 * http://www.vip.com/api/m/getRechargeAddress
	 *
	 *//*
	@Page(Viewer = JSON)
	public void getRechargeAddress(){
		setLan();
		String userId = param("userId");
		String token = param("token");
		if (!isLogin(userId, token)) {
			json(SystemCode.code_1003);
			return;
		}
		int userIdInt = Integer.parseInt(userId);
		String currencyType = param("currencyType");
		if(StringUtils.isNotBlank(currencyType)){
			currencyType = currencyType.toLowerCase();
		}
		
		User user = userDao.getById(userId);
		
		if(user.getUserContact().getGoogleAu() != AuditStatus.pass.getKey() && user.getUserContact().getMobileStatu() != AuditStatus.pass.getKey()){
			json(SystemCode.code_1023);
			return;
	    }
		
		JSONArray ja = new JSONArray();
		Map<String, CoinProps> cointMap = DatabasesUtil.getCoinPropMaps();
		if(StringUtils.isNotBlank(currencyType)){//有传币种获取该币种的地址
			CoinProps coint = cointMap.get(currencyType);
			if(coint!=null){
				keyDao.setCoint(coint);
				KeyBean key = keyDao.getRechargeKey(userIdInt, user.getUserName()); 
				if(key == null){
					key = new KeyBean();
					key.setKeyId(0);
					key.setKeyPre("");
				}
				JSONObject btcKeyJo = new JSONObject();
				btcKeyJo.put("id", key.getKeyId());
				btcKeyJo.put("address", key.getKeyPre());
				btcKeyJo.put("currencyType",currencyType);
				
				ja.add(btcKeyJo);
				
			}else{
				json(SystemCode.code_1001,L("找不到此币种!"));
				return;
			}
		}else{//没传币种，获取全部
			for(Entry<String, CoinProps> entry : cointMap.entrySet()){
				keyDao.setCoint(entry.getValue());
				KeyBean key = keyDao.getRechargeKey(userIdInt, user.getUserName()); 
				if(key == null){
					key = new KeyBean();
					key.setKeyId(0);
					key.setKeyPre("");
				}
				JSONObject btcKeyJo = new JSONObject();
				btcKeyJo.put("id", key.getKeyId());
				btcKeyJo.put("address", key.getKeyPre());
				btcKeyJo.put("currencyType", entry.getKey().toUpperCase());
				
				if (StringUtils.isBlank(currencyType)) {
					ja.add(btcKeyJo);
				} else if(currencyType.toLowerCase().equals(entry.getKey())){
					ja.add(btcKeyJo);
				}
			}
		}
		
		
		
		
		if(ja.size() == 0){
			json(SystemCode.code_1001,L("找不到此币种!"));
			return;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("rechargeAddrs", ja);
		json(SystemCode.code_1000, map);
	}
	*//**
	 * 提交BTC/LTC提现操作
	 * http://www.vip.com/api/m/withdraw
	 *
	 *//*
	@Page(Viewer = JSON)
	public void withdraw(){
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			String currencyType = param("currencyType");

			currencyType = currencyType.toLowerCase();
			CoinProps coint = DatabasesUtil.coinProps(currencyType);

			User loginUser = userDao.getById(userId);

			BigDecimal cashAmount = decimalParam("cashAmount").setScale(8, BigDecimal.ROUND_DOWN);
			String receiveAddr = param("receiveAddress");
			String liuyan = param("liuyan");
			if(StringUtils.isBlank(liuyan)){
				liuyan = "用户提现"+coint.getPropTag();
			}
			
			if(loginUser.getUserContact().getGoogleAu() != AuditStatus.pass.getKey() && loginUser.getUserContact().getMobileStatu() != AuditStatus.pass.getKey()){
				json(SystemCode.code_1023);
				return;
		    }

//			参数名 类型 是否必须 描述
//			userId String 是 用户id
//			token String 是 登录token
//			currencyType String 是 货币类型：BTC：比特币，LTC：莱特币
//			cashAmount Double 是 提现金额 BTC/LTC
//			receiveAddress String 是 接收地址
//			liuyan String 否 留言
//			safePwd String 否 资金密码 （RSA加密）
//			googleCode String 否 google验证码（RSA加密）
//			dynamicCode String 否 动态验证码（RSA加密）

//			int limitStatus = LimitUtil.moneyLimit(userIdInt, 3, Double.parseDouble(cashAmount));//Double.parseDouble(cashAmount) <=1?1:-1;//
//			if (limitStatus == -2 || limitStatus == -3) {
//				json(SystemCode.code_1011);
//				return;
//			}

			String fingerprint = param("fingerprint");// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证
			if(StringUtils.isNotBlank(fingerprint))
				fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint),priKey));
			String googleCode = param("googleCode");
			if(StringUtils.isNotBlank(googleCode))
				googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode),priKey));
			String dynamicCode = param("dynamicCode");
			if(StringUtils.isNotBlank(dynamicCode))
				dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode),priKey));
			String safePwd = param("safePwd");//资金密码 （RSA加密）
			if(StringUtils.isNotBlank(safePwd))
				safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));

			if(isRunningBarely(loginUser)){
				json(SystemCode.code_1023);
				return;
			}

			boolean aboveAmount = true;
			boolean isNewAddr = isANewAddr(currencyType,userId,receiveAddr);

			if (!validateFingerprintOrRelatives(loginUser, "", safePwd, dynamicCode, dynamicCode, "", isNewAddr, aboveAmount)) {
				return;
			}
			String ip = ip();
			
			long googleCodeLong = 0L;
//			if(StringUtils.isNotBlank(googleCode) && NumberUtils.isDigits(googleCode))
//				googleCodeLong = CommonUtil.stringToLong(googleCode);

			DownloadDao dDao = new DownloadDao();
			dDao.setCoint(coint);
			Message msg = dDao.doBtcDownload(loginUser, cashAmount, receiveAddr, coint.getMinFees(), safePwd, dynamicCode, googleCodeLong, ip, liuyan, getLanTag(), false, true, null);
			if (msg.isSuc()) {
				json(SystemCode.code_1000);
			} else {
				json(SystemCode.getSystemCodeByKey(msg.getCode()), msg.getMsg());
			}
			return;
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	private boolean isANewAddr(String currencyType, String userId, String receiveAddr) {
		boolean isNewAddr = false;

		String tableName = currencyType.toLowerCase()+CointTable.receiveaddr;
		String sql = "select * from "+tableName+" where userid=? and address=? and isdeleted = 0";
		List<Bean> addrs = Data.Query(sql, new Object[] { userId,receiveAddr }, ReceiveAddr.class);
		if (null == addrs || addrs.size() <= 0)
			isNewAddr = true;

		return isNewAddr;
	}

	*//**
	 * 取消提现
	 *//*
	@Page(Viewer = JSON)
	public void cancelWithdraw(){
		setLan();
		try{
			//验证安全密码
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			String currencyType = param("currencyType");

			CoinProps coint = DatabasesUtil.coinProps(currencyType);
			currencyType = coint.getStag();
			currencyType = currencyType.toLowerCase();
			//取消开始
			UserDao userDao = new UserDao();
			User user = userDao.getById(userId);
			String fingerprint = param("fingerprint");// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证

			if(StringUtils.isNotBlank(fingerprint)) {
				fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint),priKey));
				if (!user.getFingerprint().equals(fingerprint)) {
					json(SystemCode.code_1001, L("验证指纹失败，请重试"));
					return;
				}
			} else {
				String safePwd = param("safePwd");//资金密码 （RSA加密）
				if(StringUtils.isEmpty(safePwd)) {
					json(SystemCode.code_1001, L("资金安全密码不正确。"));
					return;
				}
				safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));
				Message passMsg = userDao.safePwd(safePwd, userId + "", getLanTag());
				if(!passMsg.isSuc()){
					json(SystemCode.code_1001, L(passMsg.getMsg()));
					return;
				}
			}

			long withdrawId = longParam("withdrawId");
			Message msg = bdDao.doCancelCash(userId, withdrawId, ip(), coint);
			json(msg.getScode(), L(msg.getMsg()));
			
		}catch (Exception e) {
			log.info("添加管理员日志失败");
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}


	@Page(Viewer = JSON)
	public void getBillType(){
		setLan();
		String userId = param("userId");
		String token = param("token");
		if (!isLogin(userId, token)) {
			json(SystemCode.code_1003);
			return;
		}
		JSONArray array = new JSONArray();
		array.add(BillType.getObjByType(BillType.recharge));
		array.add(BillType.getObjByType(BillType.download));
		array.add(BillType.getObjByType(BillType.buy));
		array.add(BillType.getObjByType(BillType.sell));
		
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("billTypes", array);
		json(SystemCode.code_1000, root);
	}

	private static Map<String, Map<String,Long>> REQUEST_TIMES_MAP = new HashMap<String, Map<String,Long>>();

	@Page(Viewer = JSON)
	public void searchBill(){
		String userId = param("userId");
		if(CommonUtil.cannotRequest(REQUEST_TIMES_MAP, userId)){
			json(SystemCode.code_4002);
			return;
		}

		setLan();
		try {

			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			//查询条件
			String currencyType = param("currencyType").toLowerCase();
			String memo = param("memo");
			Timestamp startTime = dateParam("startTime");
			Timestamp endTime = dateParam("endTime");
			int type = intParam("type");
			int dataType = intParam("dataType");
			int pageIndex = intParam("pageIndex");
			int pageSize = intParam("pageSize");
			if(pageSize == 0){
				pageSize = 10;
			}

			CoinProps coint = DatabasesUtil.coinProps(currencyType);
			BillDetailDao bwDao = new BillDetailDao();
			BeanProxy bp = MysqlDownTable.getProxy("bill");
			if(dataType == 1){
				bwDao.setDatabase(bp.tableInfo.targetDatabases()[0]);
			}	暂时没有备份库
			
			com.world.data.mysql.Query query = bwDao.getQuery();
			query.setSql("select * from bill");
			query.setCls(BillDetails.class);

			query.append(" and userId =" + userId);

			if(type > 0){
				query.append(" and type = " + type);
			}
			
			if(currencyType.length() > 0){
				query.append(" and fundsType = "+coint.getFundsType());
			}
			
			if(startTime != null){
				query.append(" and sendTime >= '"+startTime+"'");
			}
			
			if(endTime != null){
				query.append(" and sendTime <= '"+endTime+"'");
			}

			List<BillDetails> weight = new ArrayList<BillDetails>();
				query.append("order by id desc");
				//分页查询
				weight = bwDao.findPage(pageIndex, pageSize);

			Map<String, Object> reData = new HashMap<String, Object>();

			JSONArray ja = new JSONArray();
			for (Bean bean : weight) {
				BillDetails bd = (BillDetails) bean;
				JSONObject jo = new JSONObject();
				jo.put("id", bd.getId());
				jo.put("type", bd.getType());
				jo.put("typeName", bd.getBt().getValue());
				jo.put("change", bd.getAmount());
				jo.put("balance", bd.getBalance());
				jo.put("currencyType", bd.getCoinName());
				jo.put("billDate", bd.getSendTime().getTime());
				jo.put("showType",bd.getShowType());
				if(bd.getBt().getInout()==1){
					jo.put("show","+"+bd.getAmount());
				}else if(bd.getBt().getInout()==2){
					jo.put("show","-"+bd.getAmount());
				}else{
					jo.put("show",bd.getAmount());
				}
				
				ja.add(jo);
			}

			log.info("IP: "+ ip() +", 用户ID["+ userId +"]请求API V1.6 searchBill()方法, sql: " + query.getSql());

			reData.put("billDetails", ja);

			reData.put("pageIndex", pageIndex);
			reData.put("pageSize", pageSize);
			reData.put("totalPage", 0); // getTotalPage(total, pageSize)
			json(SystemCode.code_1000, reData);
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	@Page(Viewer = JSON)
	public void searchWithdraw(){
		setLan();
		try{
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			int status = intParam("status");
			Timestamp startTime = dateParam("startTime");
			Timestamp endTime = dateParam("endTime");
			int pageIndex = intParam("pageIndex");
			int pageSize = intParam("pageSize");
			
			if(pageSize == 0){
				pageSize = 10;
			}

			String currencyType = param("currencyType");
			if(StringUtils.isNotBlank(currencyType)) currencyType = currencyType.toLowerCase();
			
			CoinProps coint = DatabasesUtil.coinProps(currencyType);

			com.world.data.mysql.Query<DownloadBean> query = bdDao.getQuery();
			bdDao.setCoint(coint);
			query.setSql("select * from "+bdDao.getTableName());
			query.setCls(DownloadBean.class);

			query.append(" userId=" + userId + " and isDel=0 ");

			if (status > -1) {
				query.append(" and status=" + status);
			}

			int total = query.count();
			List<Bean> downloads = new ArrayList<Bean>();
			if(total > 0){
				query.append("order by submitTime desc");
				//分页查询
				downloads = bdDao.findPage(pageIndex, pageSize);
				for (Bean bean : downloads) {
					DownloadBean btcBean = (DownloadBean)bean;
					btcBean.setRemark(btcBean.getRemarkExHTML());
				}
			}

			Map<String, Object> root = new HashMap<String, Object>();
			root.put("withdrawDetails", downloads);
			root.put("pageIndex", pageIndex);
			root.put("pageSize", pageSize);
			root.put("totalPage", getTotalPage(total, pageSize));
			json(SystemCode.code_1000, root);
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	
	protected boolean validateFingerprintOrRelatives(User loginUser,String fingerprint,String safePwd,String mobileCode,String emailCode,String googleCode,boolean isNew,boolean aboveAmount) throws Exception{
		String userId = loginUser.get_Id();
		UserContact uc = loginUser.getUserContact();
		int safePwdFlag = 0,mobileCodeFlag = 0,emailCodeFlag = 0,googleCodeFlag = 0;
		Message msg = new Message();
		Map<String, Object> retData = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(fingerprint)) {//有传指纹过来
			if (StringUtils.isBlank(loginUser.getFingerprint())) {
				json(SystemCode.code_1001, L("指纹密码设置异常，请重新设置指纹密码"), retData);
				return false;
			}
			if (null == loginUser || StringUtils.isBlank(loginUser.getFingerprint()) || !loginUser.getFingerprint().equals(fingerprint)) {
				json(SystemCode.code_1001, L("验证指纹失败，请重试"), retData);
				return false;
			}
		 }else {//没有传指纹
			 safePwdFlag = 1;
			 if (uc.getGoogleAu() == AuditStatus.pass.getKey() && loginUser.isPayGoogleAuth()) {//Google提现验证开关：如果开启了，当需要验证手机的时候登录Google验证，关闭了的话就使用短信验证
				 googleCodeFlag = 1;
			 }
			 mobileCodeFlag = 1;
			 if (!loginUser.isPayMobileAuth()) {
				 
				 mobileCodeFlag = 0;
			 }
			 if (uc.getMobileStatu() != AuditStatus.pass.getKey() && loginUser.isPayEmailAuth()) {
				 emailCodeFlag = 1;
			 }
		}
		if (safePwdFlag == 1) {
			if (StringUtils.isBlank(safePwd)) {
				msg.setMsg("本次操作需要您填写资金密码");
			} else {
				msg = userDao.safePwd(safePwd, userId, getLanTag());
			}
			if(!msg.isSuc()){
				retData.put("needSafePwd", safePwdFlag);
				retData.put("needMobileCode", mobileCodeFlag);
				retData.put("needEmailCode", emailCodeFlag);
				retData.put("needGoogleCode", googleCodeFlag);
				json(SystemCode.code_1001, L(msg.getMsg()), retData);
				return false;
			}
		}
		if (googleCodeFlag == 1) {
			long gCode = CommonUtil.stringToLong(googleCode);
			msg = userDao.isCorrect(loginUser, uc.getSecret(), gCode);
			if(!msg.isSuc()){
				retData.put("needSafePwd", safePwdFlag);
				retData.put("needMobileCode", mobileCodeFlag);
				retData.put("needEmailCode", emailCodeFlag);
				retData.put("needGoogleCode", googleCodeFlag);
				json(SystemCode.code_1001, L(msg.getMsg()), retData);
				return false;
			}
		}
		if (mobileCodeFlag == 1) {
			// 检查短信验证码
			ClientSession clientSession = new ClientSession(ip(), uc.getSafeMobile(), lan, PostCodeType.cash.getValue(), false);
			DataResponse dr = clientSession.checkCode(mobileCode);
			if(!dr.isSuc()){
				retData.put("needSafePwd", safePwdFlag);
				retData.put("needMobileCode", mobileCodeFlag);
				retData.put("needEmailCode", emailCodeFlag);
				retData.put("needGoogleCode", googleCodeFlag);
				json(SystemCode.code_1001, L(dr.getDes()), retData);
				return false;
			}
		}
		if (emailCodeFlag == 1) {
			// 检查邮件验证码
			ClientSession clientSession = new ClientSession(ip(), uc.getSafeEmail(), lan, PostCodeType.cash.getValue(), false);
			DataResponse dr = clientSession.checkCode(mobileCode);
			if(!dr.isSuc()){
				retData.put("needSafePwd", safePwdFlag);
				retData.put("needMobileCode", mobileCodeFlag);
				retData.put("needEmailCode", emailCodeFlag);
				retData.put("needGoogleCode", googleCodeFlag);
				json(SystemCode.code_1001, L(dr.getDes()), retData);
				return false;
			}
		}
		return true;
	}
	
	@Page(Viewer = JSON)
	public void updateWithdrawAddressMemo() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			String currencyType = param("currencyType");
			currencyType = currencyType.toLowerCase();

			int userIdInt = intParam("userId");
			int withdrawAddressId = intParam("withdrawAddressId");
			String memo = param("memo");

			if (withdrawAddressId > 0) {
				String tableName = currencyType + CointTable.receiveaddr;
				int count = Data.Update("UPDATE "+tableName+" SET memo = ? Where userId = ? AND id = ?", new Object[] { memo, userIdInt, withdrawAddressId });

				if (count > 0) {
					json(SystemCode.code_1000);
				} else {
					json(SystemCode.code_1001, L("操作失败，请稍后重试"));
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void addWithdrawAddress() {
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			String currencyType = param("currencyType");
			currencyType = currencyType.toLowerCase();
			String address = request.getParameter("withdrawAddress");
			address = address.trim();
			int withdrawAddressId = intParam("withdrawAddressId");
			String memo = param("memo");
			
			String fingerprint = param("fingerprint");// 指纹识别码，只要有传入，谷歌/短信、资金密码都不用验证
			if(StringUtils.isNotBlank(fingerprint))
				fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint),priKey));
			String googleCode = param("googleCode");
			if(StringUtils.isNotBlank(googleCode))
				googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode),priKey));
			String dynamicCode = param("dynamicCode");
			if(StringUtils.isNotBlank(dynamicCode))
				dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode),priKey));
			String safePwd = param("safePwd");//资金密码 （RSA加密）
			if(StringUtils.isNotBlank(safePwd))
				safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));
			
			long googleCodeLong = 0L;
			if(StringUtils.isNotBlank(googleCode) && NumberUtils.isDigits(googleCode))
				googleCodeLong = CommonUtil.stringToLong(googleCode);
			
			CoinProps coint = DatabasesUtil.coinProps(currencyType);
			User user = userDao.get(userId);
			receiveDao.setCoint(coint);
			Message msg = receiveDao.doAddReceiveAddr(user, safePwd, dynamicCode, (int)googleCodeLong, memo, withdrawAddressId, address, ip(), getLanTag());
			if(msg.isSuc()){
				json(SystemCode.code_1000);
			}else{
				json(msg.getScode(), msg.getMsg());
			}
			
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void setFingerprint() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			String fingerprint = param("fingerprint");
			fingerprint = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(fingerprint), priKey));

			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("fingerprint", fingerprint);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (!ur.getHadError()) {
				json(SystemCode.code_1000);
			} else {
				json(SystemCode.code_1001, L("操作失败，请稍后重试"));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void setRegistrationID() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			String registrationID = param("registrationID");
			//registrationID = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(registrationID), priKey));

			String oldRegId = userDao.getById(userId).getJpushKey();

			if(StringUtils.isNotBlank(oldRegId)){
				log.info("用户"+userId+"老的jpushKey is:"+oldRegId);
			}else {
				log.info("用户"+userId+"老的jpushKey is empty");
			}

			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);

			User user = userDao.getUserByColumn(registrationID, "jpushKey");
			if (null !=user) {
				user.setJpushKey("");
				userDao.save(user);
			}

			ops.set("jpushKey", registrationID);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (!ur.getHadError()) {
				if(StringUtils.isNotBlank(oldRegId) && !oldRegId.equals(registrationID)){
					log.info("用户"+userId+"老的jpushKey is:"+oldRegId+"&新的jpushKey is:"+registrationID);
					try {
						Pusher.push("您的账号在新设备上登录了，如非您本人操作，请及时修改密码!", oldRegId,MsgType.abnormalLogin);
					} catch (Exception e) {
						log.info("jpush登录提示推送失败:所用registrationId="+oldRegId);
					}
				}
				json(SystemCode.code_1000);
			} else {
				json(SystemCode.code_1001, L("操作失败，请稍后重试"));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	 *//**
     * 行情价格提醒设置
     * http://vip.chbtc.com/api/m/setMarketRemind
     *//*
    @Page(Viewer = JSON)
    public void setMarketReminds() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (!isLogin(userId, token)) {
            json(SystemCode.code_1003);
            return;
        }
//		status String 是 开关状态，0：关闭 1：开启
//		high String 是 高价位
//		low String 是 低价位
        String marketReminds = request.getParameter("marketReminds");
        JSONArray remindsAry = JSONArray.parseArray(marketReminds);
        for (Object o : remindsAry) {
            JSONObject jsonObject = (JSONObject) o;
            int status = jsonObject.getIntValue("status");
            String high = jsonObject.getString("high");
            String low = jsonObject.getString("low");
            String currencyType = jsonObject.getString("currencyType");
            Map<String, CoinProps> types = DatabasesUtil.getCoinPropMaps();
           
            if (!types.containsKey(currencyType.toLowerCase())) {
                continue;
            }
            Query<MarketRemind> query = marketRemindDao.getQuery();
            query.filter("userId", userId);
            query.or(
                    query.criteria("symbol").equal(currencyType.toLowerCase()),
                    query.criteria("symbol").equal(currencyType.toUpperCase())
            );
            QueryResults<MarketRemind> results = marketRemindDao.find(query);
//			results.asList();
            long count = results.countAll();
            if (count > 0) {
                marketRemindDao.deleteByQuery(query);
            }

            MarketRemind entity = new MarketRemind();

            entity.setStatus(status);
            entity.setHigh(high);
            entity.setLow(low);
            entity.setSymbol(currencyType.toLowerCase());
            entity.setUserId(userId);

            String nid = marketRemindDao.save(entity).getId().toString();

        }
        json(SystemCode.code_1000);
    }

	*//**
	 * 获取行情价格提醒设置
	 * http://www.vip.com/api/m/getMarketReminds
	 *//*
    @Page(Viewer = JSON)
    public void getMarketReminds() {
        setLan();
        String userId = param("userId");
        String token = param("token");

        if (!isLogin(userId, token)) {
            json(SystemCode.code_1003);
            return;
        }
        List<MarketRemind> reminds = marketRemindDao.getList(userId);

        JSONArray marketReminds = new JSONArray();
        for (MarketRemind e : reminds) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", e.getStatus() + "");
            jsonObject.put("high", e.getHigh());
            jsonObject.put("low", e.getLow());
            jsonObject.put("currencyType", e.getSymbol().toUpperCase());
            jsonObject.put("currentPrice", MarketPrices.get().getString(e.getSymbol()));

            marketReminds.add(jsonObject);
        }

        Map<String, Object> reData = new HashMap<String, Object>();
        reData.put("marketReminds", marketReminds);
        log.info("当前价格提醒设置:" + reData);
        json(SystemCode.code_1000, reData);
    }

	@Page(Viewer = JSON)
	public void getCounterFee() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			
			boolean isLoginUser = false;
			if (!isLogin(userId, token)) {
			} else {
				isLoginUser = true;
			}
			String currencyType = param("currencyType");
			
			JSONArray ja = new JSONArray();
			Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
			for(Entry<String, CoinProps> entry : coinMap.entrySet()){
				JSONObject obj = new JSONObject();

				if(currencyType.length() > 0){
					if(entry.getKey().equals(currencyType.toLowerCase())){
						obj.put("currencyType", entry.getKey());
						obj.put("counterFee", entry.getValue().getMinFees());
						ja.add(obj);
					}
				}else{
					obj.put("currencyType", entry.getKey());
					obj.put("counterFee", entry.getValue().getMinFees());
					ja.add(obj);
				}
			}
			
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("feeInfos", ja);
			json(SystemCode.code_1000, map);
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	
	@Page(Viewer = JSON)
	public void printPrice() {
		 String name2="btcdefault"+"_hotdata";
		 BigDecimal currentBtcPrice = UserCache.getBtcPrice();
	     String data=Cache.Get(name2);
	     Map<String, Object> reData = new HashMap<String, Object>();
	     reData.put("ary", data);
	     reData.put("curBtc", currentBtcPrice);
	     json(SystemCode.code_1000, reData);
	}

	@Page(Viewer = JSON)
	public void getGoogleSecret(){
		String userId = param("userId");
		String token = param("token");

		if (!isLogin(userId, token)) {
			json(SystemCode.code_1003);
			return;
		}
		Map<String, Object> reData = new HashMap<String, Object>();
		String secret = GoogleAuthenticator.generateSecretKey();
		reData.put("secret", secret);

		String tips = "";

//		String fileName="statics/GoogleAuthTip.html";
	    String realPath=request.getServletContext().getRealPath("/statics/GoogleAuthTip.html");

	    File file=new File(realPath);
		try {
			//http://twww.vip.com/statics/GoogleAuthTip.html
			Document doc = Jsoup.parse(file, "UTF-8");
			Element secretEle = doc.getElementById("secret");
			Element webnameEle = doc.getElementById("webname");
			Elements elements = doc.getAllElements();
			for (Element e : elements) {
				log.info(e.toString());
			}
			secretEle.text(secret);
			webnameEle.text(WEB_NAME);

			tips = doc.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}

		reData.put("tips", tips);
		json(SystemCode.code_1000,reData);
	}

	@Page(Viewer = JSON)
	public void setGoogleCode() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");
			int type = intParam("type");//操作类型 1 设置/修改谷歌验证 0 关闭谷歌验证|

			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User loginUser = new UserDao().get(userId);
			// 先较验 用户修改申请审核中，此时不能设置，不能关闭，谷歌验证
			VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
			if (bean != null && bean.getStatus() == 0) {
				json(SystemCode.code_1021);
				return;
			}

			String googleCode = param("googleCode");
			if(StringUtils.isNotBlank(googleCode))
				googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey));

			log.info("参数Googlecode="+googleCode);

			long gCode = CommonUtil.stringToLong(googleCode, -1);

			String dynamicCode = param("dynamicCode");
			if(StringUtils.isNotBlank(dynamicCode))
				dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));
			log.info("参数mobileCode="+dynamicCode);

			String userIp = ip();
			UserContact uc = loginUser.getUserContact();
			String sendAddr = uc.getSafeMobile();
			if (StringUtils.isBlank(sendAddr)) {
				sendAddr = uc.getSafeEmail();
			}
			// 检查短信验证码
			ClientSession clientSession = new ClientSession(userIp, sendAddr, lan, PostCodeType.safeAuth.getValue(), false);
			DataResponse dr = clientSession.checkCode(dynamicCode);
			if (!dr.isSuc()) {
				json(SystemCode.code_1024, dr.getDes());
				return;
			}

			String secret = type==1?param("secret"):uc.getSecret();//设置或修改 用传过来的secret，关闭则用用户原有的secret
			log.info("secret="+secret);

			if (StringUtils.isBlank(secret) && type ==0) {//没有认证却要关闭
				json(SystemCode.code_1001,L("您还未开启Google认证"));
				return;
			}

			if (!isGoogleCodeCorrect(secret, gCode, userId)) {
				return;
			}

			int googleAu = 0;//谷歌双重验证是否开启(0未验证     1验证未开启     2已开启)
			if (type == 1 && StringUtils.isBlank(uc.getSecret())) {//设置
				googleAu = 2;
			} else if (type == 1 && StringUtils.isNotBlank(uc.getSecret())) {//修改，且已有secret，查查是不是在审核中，审核中驳回，非审核中插入申请记录
				// 用户是修改GOOGLE认证
				// 插入申请记录
				VerifyUserInfo info = new VerifyUserInfo(vudao.getDatastore());
				info.setUserId(loginUser.getId());
				info.setUserName(loginUser.getUserName());
				info.setType(2);// Google
				info.setInfo(secret);
				info.setBeforeInfo(uc.getSecret());
				info.setAddTime(TimeUtil.getNow().getTime());
				info.setIp(ip());

				vudao.add(info);
				json(SystemCode.code_1000,L("申请成功，客服将尽快为您审核，请耐心等待。"));
				return;
			} else {//关闭，清空secret
				googleAu = 1;
				secret = "";
			}

			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("userContact.googleAu", googleAu);
			ops.set("userContact.secret", secret);
			ops.set("payGoogleAuth", true);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (!ur.getHadError()) {
				json(SystemCode.code_1000,"您已成功开启谷歌验证!");
			} else {
				json(SystemCode.code_1001, L("出错了，请稍后重试!"));
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void changeDynamicCodeAuth() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");

			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User loginUser = userDao.get(userId);
			UserContact uc = loginUser.getUserContact();
			int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
			int authType = intParam("authType");//| authType| Integer| 是    | 验证类型 1：异地登录验证2：提现验证     |

			boolean dealVal = false;
			String des = "";
			String authTypeStr = authType == 1?"异地登录":"提现";
			String columnName = "";//authType == 1?"diffAreaLoginNoCheck":"payMobileAuth";
			if (authType == 1) {
				columnName = "diffAreaLoginNoCheck";
			} else if (authType == 2) {
				if (uc.getMobileStatu() == AuditStatus.pass.getKey()) {
					columnName = "payMobileAuth";
				} else {
					columnName = "payEmailAuth";
				}
			} else {
				json(SystemCode.code_1019);
			}

			if (oper == 0) {
//				if (loginUser.isDiffAreaLoginNoCheck()) {
//					json(SystemCode.code_1001,L("异地登录短信验证码验证已关闭。"));
//					return;
//				}
				if (authType == 2 && !loginUser.isPayGoogleAuth()) {
					json(SystemCode.code_1001, L("您没有开启提现Google验证不能关闭提现短信验证。"));
					return;
				}
				dealVal = true;
				if (authType==2) dealVal = false;
				des = "成功关闭"+authTypeStr+"动态验证码验证。";
			} else if (oper == 1) {
//				if (!loginUser.isDiffAreaLoginNoCheck()) {
//					json(SystemCode.code_1001,L("异地登录短信验证码验证已开启。"));
//					return;
//				}
				dealVal = false;
				if (authType==2) dealVal = true;
				des = "成功开启"+authTypeStr+"动态验证码验证。";
			} else {
				json(SystemCode.code_1019);
				return;
			}
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set(columnName, dealVal);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (!ur.getHadError()) {
				json(SystemCode.code_1000, L(des));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	//开启或关闭安全密码
	@Page(Viewer = JSON)
	public void useOrCloseSafePwd(){
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");

			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User loginUser = userDao.get(userId);
			int period = intParam("period");//是    | 关闭周期 1：始终开启 0：永久关闭 2：6个小时|

			if (period == 1) {//开启
				if(userDao.isNeedSafePwd(loginUser)){
					json(SystemCode.code_1001,L("资金安全密码已经开启。"));
					return;
				}

				loginUser.setNeedSafePwd(true);

				UpdateResults<User> ur = userDao.setSafePwdExpirationTime(loginUser);

				if (!ur.getHadError()) {
					logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId, "0", "手动开启安全密码。", ip());

					json(SystemCode.code_1000,L("安全密码成功开启。"));
					return;
				}else {
					json(SystemCode.code_1001,L("开启资金安全密码失败。"));
				}

			} else if (period == 0 || period ==2) {//关闭: 0 permanently 2 6 Hours
				String safePwd = param("safePwd");
				if(StringUtils.isNotBlank(safePwd))
					safePwd = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(safePwd),priKey));

				if(!safePwd(safePwd, userId)){
					return;
				}
				Date dateNow = new Date();
				long expirationTime = period == 0 ? 0L: dateNow.getTime() + 1000*60*60*6;//| 关闭周期 0：永久关闭 2：6个小时|

				loginUser.setNeedSafePwd(false);
				loginUser.setSafePwdExpiration(expirationTime);

				UpdateResults<User> ur = userDao.setSafePwdExpirationTime(loginUser);

				if (!ur.getHadError()) {
					logDao.insertOneRecord(AuthenType.closeSafePwd.getKey(), userId, "0", "成功关闭安全密码，时间："+(period==0?"永久":"6小时"), ip());

					if(period == 0){
						json(SystemCode.code_1000,L("安全密码关闭成功，如有需求，可手动开启。"));
					}else{
						json(SystemCode.code_1000,L("安全密码关闭成功，六小时后自动开启。"));
					}
				}else {
					json(SystemCode.code_1001,L("关闭失败!"));
				}

			}else {
				json(SystemCode.code_1019);
			}

		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void changeGoogleAuth() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");

			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User loginUser = userDao.get(userId);
			int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
			int authType = intParam("authType");//| authType| Integer| 是    | 验证类型 1：登录验证2：提现验证     |

			boolean dealVal = false;
			String des = "";

			UserContact uc = loginUser.getUserContact();

//			boolean destAuthSwitch = authType == 1?loginUser.isLoginGoogleAuth():loginUser.isPayGoogleAuth();
			String authTypeStr = authType == 1?"登录":"支付";
			String columnName = authType == 1?"loginGoogleAuth":"payGoogleAuth";

			if (oper == 0) {//close
				// 先较验 用户修改申请审核中，此时不能关闭相关谷歌验证
				VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
				if (bean != null && bean.getStatus() == 0) {
					json(SystemCode.code_1021);
					return;
				}
				if (uc.getMobileStatu() != 2 && authType == 2) {
					json(SystemCode.code_1001,L("您没有进行手机认证不能关闭Google支付验证码验证。"));
					return;
				}
				if (authType == 2 && !loginUser.isPayMobileAuth()) {
					json(SystemCode.code_1001, L("您没有开启提现短信验证不能关闭提现Google验证。"));
					return;
				}
//				if (authType == 2) {
				String googleCode = param("googleCode");
				if(StringUtils.isNotBlank(googleCode)){
					googleCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(googleCode), priKey));
					log.info("参数Googlecode="+googleCode);
					if (!NumberUtils.isNumber(googleCode)) {
						json(SystemCode.code_1001,L("谷歌验证码错误!"));
						return;
					}
					long gCode = CommonUtil.stringToLong(googleCode);

					if (uc.getGoogleAu() != AuditStatus.pass.getKey()) {
						json(SystemCode.code_1001,L("您还没开启Google认证，请开启Google认证后重试。"));
						return;
					}
					if (!isGoogleCodeCorrect(uc.getSecret(), gCode, userId)) {
						return;
					}
				}else {
					json(SystemCode.code_1015);
					return;
				}
//				}
				if (!destAuthSwitch) {
					json(SystemCode.code_1001,L(authTypeStr+"Google验证码验证已关闭。"));
					return;
				}
				dealVal = false;
				des = "已成功关闭"+authTypeStr+"Google验证码验证。";
			} else if (oper == 1) {//open
				if (destAuthSwitch) {
					json(SystemCode.code_1001,L(authTypeStr+"Google验证码验证已开启。"));
					return;
				}
				//当用户修改申请审核中时，谷歌认证状态为0=未验证,此时不能开启相关谷歌验证
				VerifyUserInfo bean = vudao.getVerifyingInfo(loginUser.getId(), 2);
				if (bean != null) {
					json(SystemCode.code_1021);
					return;
				}

				int safeBu = uc.getGoogleAu();
				if (safeBu != 2) {
					json(SystemCode.code_1001,L("请先开启Google认证。"));
					return;
				}

				dealVal = true;
				des = "成功开启"+authTypeStr+"Google验证码验证。";
			} else {
				json(SystemCode.code_1019);
				return;
			}

			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set(columnName, dealVal);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (!ur.getHadError()) {
				json(SystemCode.code_1000,L(des));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	@Page(Viewer = JSON)
	public void changePayEmailAuth() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");

			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User loginUser = userDao.get(userId);
			int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
			String dynamicCode = param("dynamicCode");
			if (StringUtils.isNotBlank(dynamicCode))
				dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));

			boolean dealVal = false;
			String des = "";

			String userIp = ip();
			// 检查短信验证码
			String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
			if (StringUtils.isBlank(codeRecvAddr)) {
				codeRecvAddr = loginUser.getUserContact().getSafeEmail();
			}
			ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
			DataResponse dr = clientSession.checkCode(dynamicCode);
			if (!dr.isSuc()) {
				json(SystemCode.code_1024,L(dr.getDes()));
				return;
			}

			if (oper == 0) {
				dealVal = false;
				des = "已成功关闭支付邮箱验证码验证。";
			} else if (oper == 1) {
				int emailAuth = loginUser.getUserContact().getEmailStatu();
				if (emailAuth != 2) {
					json(SystemCode.code_1001,L("请先进行邮箱认证。"));
					return;
				}
				dealVal = true;
				des = "成功开启支付邮箱验证码验证。";
			} else {
				json(SystemCode.code_1019);
				return;
			}
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("payEmailAuth", dealVal);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (!ur.getHadError()) {
				json(SystemCode.code_1000, L(des));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}
	
	@Page(Viewer = JSON)
	public void changePayMobileAuth() {
		setLan();
		try {
			String userId = param("userId");
			String token = param("token");

			if (!isLogin(userId, token)) {
				json(SystemCode.code_1003);
				return;
			}
			User loginUser = userDao.get(userId);
			int oper = intParam("operation");//操作类型 0 关闭 1 开启     |
			String dynamicCode = param("dynamicCode");
			if (StringUtils.isNotBlank(dynamicCode))
				dynamicCode = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(dynamicCode), priKey));

			boolean dealVal = false;
			String des = "";

			String userIp = ip();

			if (oper == 0) {
				// 检查短信验证码
				String codeRecvAddr = loginUser.getUserContact().getSafeMobile();
				ClientSession clientSession = new ClientSession(userIp, codeRecvAddr, lan, PostCodeType.safeAuth.getValue(), false);
				DataResponse dr = clientSession.checkCode(dynamicCode);
				if (!dr.isSuc()) {
					json(SystemCode.code_1024,L(dr.getDes()));
					return;
				}
				dealVal = false;
				des = "已成功关闭提现短信验证。";
			} else if (oper == 1) {
				int mobileAuth = loginUser.getUserContact().getMobileStatu();
				if (mobileAuth != 2) {
					json(SystemCode.code_1001,L("请先进行手机认证。"));
					return;
				}
				dealVal = true;
				des = "成功开启提现短信验证。";
			} else {
				json(SystemCode.code_1019);
				return;
			}
			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("payMobileAuth", dealVal);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (!ur.getHadError()) {
				json(SystemCode.code_1000, L(des));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
		}
	}

	public int getDiffDay(Timestamp endTime, Timestamp startTime) {
		long intervalMilli = endTime.getTime() - startTime.getTime();
		return (int) intervalMilli;
	}

	// 完成登录相关的操作
	private void wapLogin(String safe, int remember, User safeUser, String ip) {
		try {
			com.alibaba.fastjson.JSONObject json = new com.alibaba.fastjson.JSONObject();
			UserContact uc = safeUser.getUserContact();
			Authentication au = new AuthenticationDao().getByUserId(safeUser.get_Id());
			if (null != uc) {
				json.put("emailStatu", uc.getEmailStatu());
				json.put("mobileStatu", uc.getMobileStatu());
				json.put("googleAuth", uc.getGoogleAu());
			}
			if (null != au) {
				json.put("auth", au.getStatus());
			}
			String pwdStatus = "1", safePwdStatus = "1";
			if (null != safeUser.getPwd() && !"".equals(safeUser.getPwd())) {
				pwdStatus = "2";
			}
			if (null != safeUser.getSafePwd() && !"".equals(safeUser.getSafePwd())) {
				safePwdStatus = "2";
			}
			json.put("pwdStatus", pwdStatus);
			json.put("safePwdStatus", safePwdStatus);
			json.put("ipNeedAuthen", false);
			json.put("loginNeedGoogleAuth", false);
			SSOLoginManager.toLogin(this, remember, safeUser.getId(), safeUser.getUserName(), true, ip, safeUser.getVipRate()+"", "", false, json);

			Datastore ds = userDao.getDatastore();
			Query<User> query = ds.find(User.class, "_id", safeUser.getId());
			userDao.update(query, ds.createUpdateOperations(User.class).set("previousLogin", safeUser.getLastLoginTime() == null ? now() : safeUser.getLastLoginTime()).set("lastLoginTime", now()).set("loginIp", ip).set("trueIp", safeUser.getLoginIp() == null ? ip : safeUser.getLoginIp()));

			//更新内存里,cookie里的vip等级
			SSOLoginManager.updateVip(safeUser.getId(), safeUser.getVipRate());

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	@Page(Viewer = JSON)
	public void getUserOpenId(){
		setLan();
		String userId = param("userId");
		String token = param("token");

		if (!isLogin(userId, token)) {
			json(SystemCode.code_1003);
			return;
		}
		User loginUser = userDao.getUserById(userId);
		if(loginUser.isFreez()){
			json(SystemCode.code_1001, L("该账户已冻结，暂时不能操作。"));
			return;
		}

		String nowTime = new Date().getTime()+"";

		String uid = MD5.toMD5( MD5.toMD5(userId)+MD5.toMD5(nowTime) );//生成uid

		Map<String, Object> reData = new HashMap<String, Object>();

		if (StringUtils.isEmpty(loginUser.getWapUid())) {//如果已经有记录过了,就不再记录了

			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("wapUid", uid);
			UpdateResults<User> ur = userDao.update(q, ops);
			if (ur.getHadError()) {
				json(SystemCode.code_1001);
				return;
			}else {
				reData.put("userOpenId", uid);
			}
		}else {
			reData.put("userOpenId", loginUser.getWapUid());
		}
		json(SystemCode.code_1000,reData);
	}

	private String userOpenId(User user){
		setLan();
		String userId = user.getId();
		String nowTime = new Date().getTime()+"";

		String uid = MD5.toMD5( MD5.toMD5(userId)+MD5.toMD5(nowTime) );//生成uid

		if (StringUtils.isEmpty(user.getWapUid())) {//如果已经有记录过了,就不再记录了

			Datastore ds = userDao.getDatastore();
			Query<User> q = ds.find(User.class, "_id", userId);
			UpdateOperations<User> ops = ds.createUpdateOperations(User.class);
			ops.set("wapUid", uid);
			UpdateResults<User> ur = userDao.update(q, ops);
			ur.getHadError();
		}else {
			uid = user.getWapUid();
		}
		return uid;
	}

	@Page(Viewer = JSON)
	public void registerWithEmail(){
		try {
			String email = param("email");
			String password = param("password");
			int pwdLevel = intParam("pwdLevel");
			String tuijianId = param("recommId");

			boolean res = userDao.emailValidated(email);
			if (!res) {
				json(SystemCode.code_1001,L("邮箱已注册！"));
				return;
			}
			if (StringUtils.isBlank(password)) {
				json(SystemCode.code_1001,L("请输入您的登录密码!"));
				return;
			}else
				password = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(password), priKey));

			String userIp = ip();
			if (!userIp.equals("127.0.0.1")) {
				Query<User> q = userDao.getQuery(User.class).filter("loginIp =", userIp);
				// //
				long count = userDao.find(q).countAll();
				log.error("ip：" + userIp + ",次数：" + count + ",你所在的ip跟踪程序已启动，如有恶意请停止，如果报警有误，请联系网站在线客服...");
				if (count >= 3) {
					json(SystemCode.code_1001,L("您好，系统监测到您的IP可能存在大量刷注册推荐人奖励行为，因此本次注册不能被通过。如果您是真实用户，请联系我们客服，给您造成的不便敬请谅解！您当前IP：") + userIp);
					return;
				}
			}

			String recommenders = "";
			String recommId = "";
			User userLs = null;
			if (tuijianId != null) {
				userLs = userDao.get(tuijianId);
				if (userLs != null) {
					if (!userLs.isDeleted()) {
						if (userLs.isLockRecommend()){
							json(SystemCode.code_1001,L("你所用的推荐人已被网站锁定！"));
							return;
						}
						recommenders = userLs.getUserName();
						recommId = userLs.getId();
					} else {
						json(SystemCode.code_1001,L("你所用的推荐人是非法账户已被网站封号！"));
						return;
					}
				}
			}
			Query<User> q = userDao.getQuery();
			User user = userDao.findOne(q.field("email").endsWithIgnoreCase(email));
			if(null != user && !user.getUserContact().isCanReg()){
				userDao.delById(user.getId());
			}
			String emailCode = MD5.toMD5(System.currentTimeMillis()+email);

			user = new User(userDao.getDatastore());
			user.setLanguage(lan);
			user.setCurrency(GlobalConfig.currency);
			user.setMarket(GlobalConfig.market);
			user.setCurrencyN(GlobalConfig.currencyN);
			user.setLoginIp(userIp);
			user.setDeleted(false);
			user.setModifyTimes(0);
			user.setPwd("");
			user.setPwdLevel(0);
			user.setSafePwd("");
			user.setSafeLevel(0);
			user.setRecommendId(recommId);
			user.setRecommendName(recommenders);
			user.setLastLoginTime(TimeUtil.getNow());
			user.setEmail(email);

			UserContact uc = new UserContact();
			uc.setEmailCode(emailCode);
			uc.setCheckEmail(email);
			uc.setEmailTime(now());
			user.setUserContact(uc);
			String nid = userDao.addUser(user);
			if (nid != null) {
				// 设置密码
				userDao.updatePwd(nid, password, pwdLevel);
				EmailDao eDao = new EmailDao();

				String info = eDao.getRegEmailHtml(nid, lan, email, emailCode,this);
				eDao.sendEmail(ip(), nid, email, L("邮箱注册"), info, email);
				log.info(email + "注册:" + VIP_DOMAIN+"/register/emailConfirm?emailCode="+emailCode);
//				json(nid, true, "");
				Map<String, Object> reData = new HashMap<String, Object>();
				reData.put("userId", nid);
				json(SystemCode.code_1000,L("注册成功，请您登录邮箱激活帐号!"), reData);
			}else{
				json(SystemCode.code_1001,L("注册失败，请重新注册!"));
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			json(SystemCode.code_1001,L("注册出错，请稍后重试。"));
		}
	}

	@Page(Viewer = JSON)
	public void reSendEmail() {
		setLan();
		String nid = param("userId");
		User user = userDao.getById(nid);
		if (null == user) {
			json(SystemCode.code_3004);
			return;
		}
		EmailDao eDao = new EmailDao();
		String email = user.getUserContact().getCheckEmail();
		if (StringUtils.isBlank(email)) {
			json(SystemCode.code_1001,L("出错了,请您重新注册!"));
			return;
		}
		String emailCode = MD5.toMD5(System.currentTimeMillis()+email);
		userDao.updateEmailCode(nid, email, emailCode);
		String info = eDao.getRegEmailHtml(nid, lan, email, emailCode, this);
		eDao.sendEmail(ip(), nid, email, L("邮箱注册"), info, email);
		log.info(email + "注册:" + VIP_DOMAIN+"/register/emailConfirm?emailCode="+emailCode);
//		Map<String, Object> reData = new HashMap<String, Object>();
//		reData.put("userId", nid);
		json(SystemCode.code_1000,L("发送成功,请您登录邮箱激活帐号!"));
	}

	@Page(Viewer = JSON)
	public void getCountries(){
		setLan();
		CountryDao countryDao = new CountryDao();
//		List<Country> country = countryDao.find().asList();

		int version = intParam("version");

		Map<String, Object> reData = new HashMap<String, Object>();
		JSONArray ja = new JSONArray();
		AppSetting appSetting = settingDao.findOne(settingDao.getQuery());

		//if (version<appSetting.getCountryInfoVersion()) {
			Query<Country> q = countryDao.getQuery();
			long total = countryDao.count(q);
			if (total > 0) {
				List<Country> dataList = countryDao.find().asList();
				for (Country country : dataList) {
					JSONObject jo = new JSONObject();
					jo.put("id", country.getId());
					jo.put("name", country.getName());
					jo.put("code", country.getCode());

					ja.add(jo);
				}
			}
		//}
		reData.put("countries", ja);
		reData.put("version", 1);

		json(SystemCode.code_1000,reData);

	}

	@Page(Viewer = JSON)
	public void getRecommendGuide(){
		setLan();
		String userId = param("userId");
		String token = param("token");

		if (!isLogin(userId, token)) {
			json(SystemCode.code_1003);
			return;
		}

		String recommendGuide = "";

		String realPath=request.getServletContext().getRealPath("/statics/commonTip.html");

	    File file=new File(realPath);
		try {
			Document doc = Jsoup.parse(file, "UTF-8");
			Element desDiv = doc.getElementById("recommendGuide");
			doc.body().replaceWith(desDiv);
			recommendGuide = doc.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}


		Map<String, Object> reData = new HashMap<String, Object>();

		reData.put("recommendGuide", recommendGuide);
		reData.put("recommendLink", VIP_DOMAIN+"/user/register/"+userId);

		reData.put("recommendTitle", "邀请好友一起赚大钱");
		reData.put("recommendContent", "喊你的小伙伴一起来，有富同享");
		reData.put("shareImg", STATIC_DOMAIN + "/statics/img/v2/recommend-share.png");

		json(SystemCode.code_1000,reData);

	}

	@Page(Viewer = JSON)
	public void getVersion() {
		Map<String, Object> reData = new HashMap<String, Object>();
		
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd G 'at' hh:mm:ss z"); 
		
		String timeStr = "2016-08-19 14:49:00";
		reData.put("version", timeStr);
		json(SystemCode.code_1000, reData);
	}
	
	
	*//**
	 *//*
	@Page(Viewer = JSON)
	public void getRechargeBank() {
		setLan();
//		String userId = param("userId");
//		String token = param("token");
		int version = intParam("version");
		log.info("传入的Version="+version);
		if (!isLogin(userId, token)) {
			json(code_1003);
			return;
		}

//		code	Integer	是	1000	返回码
//		message	String	是	SUCCESS	说明
//		rechargeBanks	RechargeBank[]	是		充值银行数组

//		| id | Integer| 是    | |  ID  |
//		| name | String | 是    | | 名称   |
//		| tag | String | 是    | | 标签  |
//		| img | String | 是    || 图标路径   |

		List<ChinaBank> chinaBanks =ChinaBankHelp.getAllBanksHC();

		Map<String, Object> reData = new HashMap<String, Object>();
		List<Map<String, Object>> ary = new ArrayList<Map<String, Object>>();

		AppSetting appSetting = settingDao.findOne(settingDao.getQuery());
		if (appSetting!=null && version < appSetting.getRechargeBankVersion()) {
			log.info("新版rechargeBankVersion="+appSetting.getRechargeBankVersion());
//			for (ChinaBank bean : chinaBanks) {
//				bean.setImg(ChinaBank4Mobile.getChinabanks4mobile().get(bean.getTag()));
//			}

			for (ChinaBank bean : chinaBanks) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("id", bean.getId());
				item.put("name", bean.getValue());
				item.put("tag", bean.getTag());
//				item.put("img", "http://192.168.2.33:8880/ts"+bean.getImg());
				item.put("img", STATIC_DOMAIN+ChinaBank4Mobile.getChinabanks4mobile().get(bean.getTag()));

				ary.add(item);
			}
		}else{

			for (ChinaBank bean : chinaBanks) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put("id", bean.getId());
				item.put("name", bean.getValue());
				item.put("tag", bean.getTag());
//				item.put("img", "http://192.168.2.33:8880/ts"+bean.getImg());
				item.put("img", STATIC_DOMAIN+ChinaBank4Mobile.getChinabanks4mobile().get(bean.getTag()));

				ary.add(item);
			}
		}
		reData.put("rechargeBanks", ary);
		reData.put("version", appSetting==null?"1":appSetting.getRechargeBankVersion());

		json(SystemCode.code_1000, reData);
	}
	
	
	*//**  
	 * 获取交易货币配置
	 *//*
	@Page(Viewer = JSON)
	public void getCurrencySet() {
		setLan();
		int version = intParam("version");
		int curVersion = 1;
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();
		Map<String,CoinProps> coinMaps =   DatabasesUtil.getCoinPropMaps();
			
		if(coinMaps!=null){
			Iterator<Entry<String,CoinProps>> iter = coinMaps.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String,CoinProps> entry = iter.next();
				String key = entry.getKey();
				CoinProps coin = entry.getValue();
				
					
					Map<String, Object> coinMap = new HashMap<String, Object>();
					coinMap.put("currency",  coin.getPropTag());
					coinMap.put("symbol",coin.getUnitTag());
					coinMap.put("name", coin.getPropCnName());
					coinMap.put("englishName",  coin.getPropTag());
					if(key.equalsIgnoreCase("eth")){
						coinMap.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_type_sm@3x.png");
						coinMap.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_eth_finance.png");
					}else if(key.equalsIgnoreCase("etc")){
						coinMap.put("coinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_type_sm@3x.png");
						coinMap.put("financeCoinUrl", STATIC_DOMAIN + "/statics/img/v2/mobile/ico/ico_etc_finance.png");
					}else{
						coinMap.put("coinUrl", "");
						coinMap.put("financeCoinUrl", "");
					}					
					coinMap.put("prizeRange", "0.05");
					
					coinMap.put("dayFreetrial", coin.getDayFreetrial());//日免审额度
					coinMap.put("dayCash", coin.getDayCash());//每日允许提现的额度
					coinMap.put("timesCash", coin.getTimesCash());//次提现额度
					coinMap.put("minFees", coin.getMinFees());//交易手续费
					coinMap.put("inConfirmTimes", coin.getInConfirmTimes());//充值到账的确认次数
					coinMap.put("outConfirmTimes",coin.getOutConfirmTimes());//允许提现的确认次数
					coinMap.put("minCash", coin.getMinCash());//最小提现额度
					
					
					JSONArray jMLs = new JSONArray();
					jMLs.add(getMarketLength(key.toUpperCase()));
					
					coinMap.put("marketDepth", new JSONArray());
					coinMap.put("marketLength", jMLs);
					
					list.add(coinMap);
				}
			}
	
			
			
	
	
	
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("currencySets", list);
		retMap.put("version", curVersion);
		json(SystemCode.code_1000, retMap);
	}

	private JSONObject getMarketLength(String currency) {
		JSONObject jo = new JSONObject();
		jo.put("currency", currency);
		jo.put("optional", new Integer[]{5, 10, 20, 50});
		return jo;
	}
	
	
	*//**
	 * 更改验证
	 *//*
	 @Page(Viewer = JSON)
	 public void changeAuth() {
	        try {
	            setLan();
	            String userId = param("userId");
	            String token = param("token");

	            if (!isLogin(userId, token)) {
	                json(SystemCode.code_1003);
	                return;
	            }
	            User loginUser = userDao.getById(userId);

	            int category = intParam("category");
	            int type = intParam("type");
	            String safePwd = decryptRSAParam("safePwd");
	            String dynamicCode = decryptRSAParam("dynamicCode");
	            String fingerprint = decryptRSAParam("fingerprint");
	            String googleCode = decryptRSAParam("googleCode");

	            //确定需要的验证信息
	            //检查哪些验证信息没有传
	            //提示用户输入
	            //验证用户输入
	            boolean googleAuthRequired = safeStrategyNeedGoogleAuth(category, type);
	            if (googleAuthRequired && loginUser.getUserContact().getGoogleAu() != AuditStatus.pass.getKey()) {
	                json(SystemCode.code_1001, L("您还没开启Google认证，请开启Google认证后重试。"));
	                return;
	            }

	            if (!changeAuthValidate(loginUser, fingerprint, safePwd, dynamicCode, dynamicCode, googleCode, category, type)) {
	                return;
	            }

	            Message msg = null;
	            switch (category) {
	                case 1:        // 登录验证
	                    msg = userDao.switchLoginAuthen(loginUser, type, ip(), request);
	                    break;
	               case 2:        // 交易验证
	                    msg = userDao.switchTradeAuthen(loginUser, type, ip(), request);
	                    break;
	                case 3:            // 提现验证
	                    msg = userDao.switchWithdrawAuthen(loginUser, type, ip(), request);
	                    break;
	                default:
	                    json(SystemCode.code_1019);
	                    return;
	            }
	            if (msg.isSuc()) {
	                json(SystemCode.code_1000, msg.getMsg());
	            } else {
	                json(SystemCode.code_1001, msg.getMsg());
	            }
	        } catch (Exception e) {
	            log.error(e.toString(), e);
	            json(SystemCode.code_1002, L("内部异常：") + e.getMessage());
	        }
	    }

	    private boolean changeAuthValidate(User loginUser, String fingerprint, String safePwd, String mobileCode, String emailCode, String googleCode, int category, int type) throws Exception {
	        String userId = loginUser.get_Id();
	        UserContact uc = loginUser.getUserContact();
	        int safePwdFlag = 0, mobileCodeFlag = 0, emailCodeFlag = 0, googleCodeFlag = 0;
	        boolean lacksafePwdFlag = false,
	                lackmobileCodeFlag = false,
	                lackemailCodeFlag = false,
	                lackgoogleCodeFlag = false;

	        Map<String, Object> retData = new HashMap<String, Object>();
	        if (StringUtils.isNotBlank(fingerprint)) {//有传指纹过来
	            String fingerCode = param("fingerCode");
	            Message fingerMsg = isFingerprintCorrect(loginUser, fingerCode, fingerprint);
	            if (!fingerMsg.isSuc()) {
	                json(SystemCode.code_1001, fingerMsg.getMsg(), retData);
	                return false;
	            }
	        } else {//没有传指纹
	            if (category == 2) {
	               if (type != TradeAuthenType.TRADE_PASSWORD.getKey()) { // 验证交易密码
	                    safePwdFlag = 1;
	                }
	            } else {
	                googleCodeFlag = uc.getGoogleAu() == 2 ? 1 : 0;
	                if (uc.getMobileStatu() != AuditStatus.pass.getKey()) {
	                    emailCodeFlag = 1;
	                } else {
	                    mobileCodeFlag = 1;
	                }
	            }
	        }
	        retData.put("needSafePwd", safePwdFlag);
	        retData.put("needMobileCode", mobileCodeFlag);
	        retData.put("needEmailCode", emailCodeFlag);
	        retData.put("needGoogleCode", googleCodeFlag);

	        Message msg = new Message();

	        if (safePwdFlag == 1) {
	            if (StringUtils.isBlank(safePwd)) {
	                lacksafePwdFlag = true;
	            } else {
	                msg = userDao.safePwd(safePwd, userId, getLanTag());
	                if (!msg.isSuc()) {
	                    SystemCode safePwdErrorCode = SystemCode.getSystemCodeByKey(msg.getCode());
	                    json(safePwdErrorCode, retData);
	                    return false;
	                }
	            }

	        }
	        if (googleCodeFlag == 1) {
	            if (StringUtils.isBlank(googleCode)) {
	                lackgoogleCodeFlag = true;
	            } else {
	                long gCode = CommonUtil.stringToLong(googleCode);
	                msg = userDao.isCorrect(loginUser, uc.getSecret(), gCode);
	                if (!msg.isSuc()) {
	                    json(SystemCode.code_1001, L(msg.getMsg()), retData);
	                    return false;
	                }
	            }
	        }
	        if (mobileCodeFlag == 1) {
	            if (StringUtils.isBlank(mobileCode)) {
	                lackmobileCodeFlag = true;
	            } else {
	                // 检查短信验证码
	                ClientSession clientSession = new ClientSession(ip(), uc.getSafeMobile(), lan, PostCodeType.safeAuth.getValue(), false);
	                DataResponse dr = clientSession.checkCode(mobileCode);
	                if (!dr.isSuc()) {
	                    json(SystemCode.code_1001, L(dr.getDes()), retData);
	                    return false;
	                }
	            }
	        }
	        if (emailCodeFlag == 1) {
	            if (StringUtils.isBlank(emailCode)) {
	                lackemailCodeFlag = true;
	            } else {
	                // 检查邮件验证码
	                ClientSession clientSession = new ClientSession(ip(), uc.getSafeEmail(), lan, PostCodeType.safeAuth.getValue(), false);
	                DataResponse dr = clientSession.checkCode(emailCode);
	                if (!dr.isSuc()) {
	                    json(SystemCode.code_1001, L(dr.getDes()), retData);
	                    return false;
	                }
	            }
	        }
	        if (lacksafePwdFlag ||
	                lackmobileCodeFlag ||
	                lackemailCodeFlag ||
	                lackgoogleCodeFlag) {

	            String detail = "本次操作需要您填写";

	            detail += lacksafePwdFlag ? "资金密码&" : "";
	            detail += lackmobileCodeFlag ? "短信验证码&" : "";
	            detail += lackemailCodeFlag ? "邮箱验证码&" : "";
	            detail += lackgoogleCodeFlag ? "谷歌验证码&" : "";

	            detail = detail.substring(0, detail.length() - 1);
	            msg.setMsg(L(detail));
	            json(SystemCode.code_1001, L(msg.getMsg()), retData);
	            return false;
	        }
	        return true;
	    }

	    
	    @Page(Viewer = JSON)
		public void getProclamations(){
			String type = param("type");
			String title = param("keyword");
			int isTop = intParam("isTop");
			
			NewsDao nd = new NewsDao();
			Query<News> query = nd.getQuery(News.class).order("-pubTime");
			
			if (isTop == 1)
				query.filter("isTop", true);
			
			if(type.length() == 0)
				type = "1";//1公告 2新闻
			
			String typeName = "官方公告";
			if (type.length() > 0) {
				if (type.equals("1")) {
					query = query.filter("type", 1);
					typeName = "官方公告";
				} else{
					query = query.filter("type", 2);
					typeName = "新闻";
				}
			}
			if(title.length()>0){
				Pattern pattern = Pattern.compile("^.*"  + title+  ".*$" ,  Pattern.CASE_INSENSITIVE);
				query.filter("title", pattern);
			}
			List<News> articles = nd.search(query, 1, 3);
			Map<String, Object> reData = new HashMap<String, Object>();
			JSONArray ja = new JSONArray();
			if (null != articles && articles.size() > 0) {
				for (News item : articles) {
					JSONObject jo = new JSONObject();
					jo.put("id", item.getId());
					jo.put("title", item.getTitle());
//					jo.put("content", "");//列表不用返回内容
					jo.put("summary", "");
					jo.put("publishTime", item.getPubTime().getTime());
					jo.put("type", item.getType());
					jo.put("link", Action.MAIN_DOMAIN+"/msg/details-"+item.getId());
					
					ja.add(jo);
				}
			}
			
			reData.put("articles", ja);
			json(SystemCode.code_1000, reData);
			
		}
		
	
	    *//**
	     * 设置用户推送通知配置
	     *//*
	    @Page(Viewer = JSON)
	    public void setPushSettings() {
	        setLan();
	        String userId = param("userId");
	        String token = param("token");

	        if (!isLogin(userId, token)) {
	            json(SystemCode.code_1003);
	            return;
	        }
	        String pushSettings = request.getParameter("settings");
	        if (null == pushSettings) {
	            json(SystemCode.code_1001, L("请选择配置!"));
	            return;
	        }
	        JSONArray settingAry = JSONArray.parseArray(pushSettings);
	        for (Object o : settingAry) {
	            JSONObject jsonObject = (JSONObject) o;
	            int type = jsonObject.getIntValue("type");
	            int isOpen = jsonObject.getIntValue("isOpen");
	            String sound = jsonObject.getString("sound");

	            Query<PushSetting> query = pushSettingDao.getQuery();
	            query.filter("userId", userId);
	            query.filter("type", type);

	            QueryResults<PushSetting> results = pushSettingDao.find(query);

	            long count = results.countAll();
	            if (count > 0) {
	                pushSettingDao.deleteByQuery(query);
	            }

	            PushSetting entity = new PushSetting();

	            entity.setCreatetime(now());
	            
	            entity.setIsOpen(isOpen);
	            if (StringUtils.isNotBlank(sound))
	                entity.setSound(sound);
	            entity.setUserId(userId);
	            entity.setType(type);

	            String nid = pushSettingDao.save(entity).getId().toString();

	        }
	        json(SystemCode.code_1000);
	    }

	    *//**
	     * 获取用户推送通知配置
	     * 
	     *//*
	    @Page(Viewer = JSON)
	    public void getPushSettings() {
	        setLan();
	        String userId = param("userId");
	        String token = param("token");

	        if (!isLogin(userId, token)) {
	            json(SystemCode.code_1003);
	            return;
	        }
	        List<PushSetting> settings = pushSettingDao.getList(userId);

	        JSONArray settingsJsongAry = new JSONArray();
	        for (PushSetting e : settings) {
	            JSONObject jsonObject = new JSONObject();
	            jsonObject.put("type", e.getType() + "");
	            jsonObject.put("isOpen", e.getIsOpen());
	            jsonObject.put("sound", e.getSound());
	            settingsJsongAry.add(jsonObject);
	        }

	        Map<String, Object> reData = new HashMap<String, Object>();
	        reData.put("settings", settingsJsongAry);
	        json(SystemCode.code_1000, reData);
	    }
	    
		public String decryptRSAParam(String paramName) throws Exception {
			String encryptValue = param(paramName);
			String decryptValue = "";
			if (StringUtils.isNotBlank(encryptValue)) {
				decryptValue = new String(RSACoder.decryptByPrivateKey(RSACoder.decryptBASE64(encryptValue),priKey ));
			}
			return decryptValue;
		}
//	@Page(Viewer = JSON)
//	public void getPublicKey() {
//		String key = getPubKey();
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("publicKey", key);
//		json(SystemCode.code_1000, map);
//	}
*/

	@Page(Viewer = JSON)
	public void getPhotoUrl() {
		try {
			String bannerGroup = param("bannerGroup");
			//根据key获取缓存项
			String bannerPhotoCache = Cache.Get("bannerPhotoApp");
			if (StringUtils.isEmpty(bannerPhotoCache) || "[]".equals(bannerPhotoCache)) {
				if (bannerGroup != null) {
					//获取关联图片数据ID
					//构建Dao对象实例
					BannerRelationDao bannerRelationDao = new BannerRelationDao();
					//从默认数据库获取query
					Query<BannerRelation> query = bannerRelationDao.getQuery();
					//构建查询语句
					query.setCls(BannerRelation.class).setSql("SELECT * FROM bannerrelation");
					//设置查询条件
					query.append("groupid =( SELECT id FROM bannergroup WHERE bannergroup = '" + bannerGroup + "')");
					//SELECT * FROM bannerrelation WHERE groupid =( SELECT id FROM bannergroup WHERE bannergroup = '自测')
					//获取执行结果的list集合
					List<BannerRelation> bannerRelation = query.getList();
					if (bannerRelation==null){
						//json(L("Banner图片数据为空，请联系管理员。"), false, "");
						json(SystemCode.code_1001,L("Banner图片数据为空，请联系管理员。"));
						return;
					}
					//查询图片明细  插入缓存
					BannerPhotoDao photo = new BannerPhotoDao();
					Query<BannerPhoto> queryPhoto = photo.getQuery();
					queryPhoto.setCls(BannerPhoto.class).setSql("select id,bannerName,bannerUrl,linkUrl,checkUser,addTime from bannerPhoto");
					String countSql = null;
					List<Integer> ids = new ArrayList<>();
					for (BannerRelation banner : bannerRelation){
						ids.add(banner.getPhotoid());
					}
					//										替换
					countSql = "id in ("+ids.toString().replaceAll("\\[|\\]", "")+")";
					queryPhoto.append(countSql);
					queryPhoto.append(" and `status` = 1");
					//SELECT * FROM bannerPhoto WHERE id IN (18) AND status = 1
					//执行sql
					List<BannerPhoto> bannerPhoto = queryPhoto.getList();
					if (bannerPhoto == null || bannerPhoto.size() < 1) {
						//json(L("Banner图片数据为空，请联系管理员。"), false, "");
						json(SystemCode.code_1001,L("Banner图片数据为空，请联系管理员。"));
						return;
					}
/*					for (BannerPhoto bp:bannerPhoto) {
						bp.getBannerUrl().replace();
					}*/
					bannerPhotoCache = com.alibaba.fastjson.JSON.toJSONString(bannerPhoto);
					Cache.Set("bannerPhotoApp", bannerPhotoCache, 60 * 60 * 1);
				} else {
					log.error("Banner组 bannerGroup 不可为空！");
				}
			}
			json(SystemCode.code_1000,"操作成功", bannerPhotoCache);
			//json("success", true, bannerPhotoCache);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	/**
	 * 返回未读公告列表
	 */
	@Page(Viewer = JSON)
	public void getUserUnReadNotice() {
		this.getUserUnReadNoticeOne();
	}


	/**
	 * 语言设置
	 **/
	@Page(Viewer = JSON)
	public void queryUserDistribution() {
		try {
			String internationalization = param("internationalization");
			String entrustmentDis="";
			if (!StringUtil.exist(internationalization)){
				//json(L("国际化标识为空！"), false, internationalization);
				json(SystemCode.code_1001,"国际化标识为空！");
			}
			if ("CN".equals(internationalization.toUpperCase())){
				entrustmentDis = Cache.Get("USER_DISTRIBUTION_WORK_CN_KEY");
			}else if("EN".equals(internationalization.toUpperCase())){
				entrustmentDis = Cache.Get("USER_DISTRIBUTION_WORK_EN_KEY");
			}else{
				entrustmentDis = Cache.Get("USER_DISTRIBUTION_WORK_HK_KEY");
			}
			net.sf.json.JSONArray jsonArray = net.sf.json.JSONArray.fromObject(entrustmentDis);
			//json("", true, jsonArray.toString());
			json(SystemCode.code_1000,"操作成功",jsonArray.toString());
		} catch (Exception e) {
			//json(L("内部异常"), false, "");
			json(SystemCode.code_1002,L("内部异常"));
			log.error("切换语言异常！");
		}
	}

	@Page(Viewer = JSON)
	public void uploadToken() throws IOException {
		String key=QcloudCosUtil.getUploadDir() + DateFormatUtils.format(new Date(), "yyyyMMdd")  + "/AUTH/" ;
		//String token = QcloudCosUtil.getPostToken(QcloudCosUtil.getDefaultExpiredMills());
			Map<String, Object> datas=new HashMap<>();
			datas.put("host",QcloudCosUtil.getHost());
			datas.put("key",key);
			datas.put("appID",QcloudCosUtil.getAppID());
			datas.put("regionName",QcloudCosUtil.getRegionName());
			datas.put("bucket",QcloudCosUtil.getBucketName());
			JSONObject json =QcloudCosUtil.qCloud(key);

			datas.put("tmpSecretId",json.getJSONObject("credentials").get("tmpSecretId"));
			datas.put("tmpSecretKey",json.getJSONObject("credentials").get("tmpSecretKey"));
			datas.put("sessionToken",json.getJSONObject("credentials").get("sessionToken"));
			datas.put("startTime",json.get("startTime"));
			datas.put("requestId",json.get("requestId"));
			datas.put("expiration",json.get("expiration"));
			datas.put("expiredTime",json.get("expiredTime"));
			json(SystemCode.code_1000,datas);
			System.err.println(datas);
	}

	public static void main(String[] args) throws Exception {
		String key=QcloudCosUtil.getUploadDir() + DateFormatUtils.format(new Date(), "yyyyMMdd")  + "/AUTH/" ;

		JSONObject json =QcloudCosUtil.qCloud(key);
		// 用户基本信息
		String tmpSecretId = json.getJSONObject("credentials").getString("tmpSecretId");   // 替换为您的 SecretId
		String tmpSecretKey = json.getJSONObject("credentials").getString("tmpSecretKey");  // 替换为您的 SecretKey
		String sessionToken = json.getJSONObject("credentials").getString("sessionToken");  // 替换为您的 Token

		// 1 初始化用户身份信息(secretId, secretKey)
		COSCredentials cred = new BasicCOSCredentials(tmpSecretId, tmpSecretKey);
		// 2 设置 bucket 区域,详情请参阅 COS 地域 https://cloud.tencent.com/document/product/436/6224
		ClientConfig clientConfig = new ClientConfig(new Region("ap-seoul"));
		// 3 生成 cos 客户端
		COSClient cosclient = new COSClient(cred, clientConfig);
		// bucket 名需包含 appid
		String bucketName = "test-1257794003";

		// 上传 object, 建议 20M 以下的文件使用该接口
		File localFile = new File("/Users/weiqun/Documents/图片.png");
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key+"222.jpg", localFile);

		// 设置 x-cos-security-token header 字段
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setSecurityToken(sessionToken);
		putObjectRequest.setMetadata(objectMetadata);

		try {
			PutObjectResult putObjectResult = cosclient.putObject(putObjectRequest);
			// 成功：putobjectResult 会返回文件的 etag
			String etag = putObjectResult.getETag();
		} catch (CosServiceException e) {
			//失败，抛出 CosServiceException
			e.printStackTrace();
		} catch (CosClientException e) {
			//失败，抛出 CosClientException
			e.printStackTrace();
		}

		// 关闭客户端
		cosclient.shutdown();

	}



}
