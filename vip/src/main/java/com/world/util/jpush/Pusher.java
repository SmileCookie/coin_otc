package com.world.util.jpush;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.world.model.dao.app.PushSettingDao;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.app.PushSetting;
import com.world.model.entity.user.User;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.APIConnectionException;
import cn.jpush.api.common.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;

public class Pusher {
    protected static final Logger log = LoggerFactory.getLogger(Pusher.class);
    
    // demo App defined in resources/jpush-api.conf
//	private static final String appKey ="e5c0d34f58732cf09b2d4d74";
	private static final String appKey =Constants.appKey;
//	private static final String masterSecret = "4cdda6d3c8b029941dbc5cb3";
	private static final String masterSecret = Constants.masterSecret;

	private static final String testAppKey =Constants.testAppKey;
	private static final String testMasterSecret = Constants.testMasterSecret;
	
	public static final String TITLE = "Test from API example";
    public static final String ALERT = "Hello,dev_6422 - 推送测试,你好世界"+System.currentTimeMillis();
    public static final String MSG_CONTENT = "Test from API Example - msgContent";
    public static final String REGISTRATION_ID = "0900e8d85ef";
    public static final String TAG = "tag_api";
    public static final String ALIAS = Constants.prefix;

    public static final String MODE_KEY = Constants.modeKey;

	public static void main(String[] args) {
//        testSendPushWithCustomConfig();
//        testSendIosAlert();
//		testSendPush();
//		push(ALERT,"6422");//山高月小
//		push(ALERT,"6497");//麦总
//		push(ALERT,"1002");//hello world
		try {
			push("测试", "120c83f7602987d884f", MsgType.abnormalLogin);
		} catch (APIConnectionException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		} catch (APIRequestException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
	}
	
	
	public static void pushAccordingMsgType(String msg, String userId, MsgType msgType) throws APIConnectionException, APIRequestException {
		UserDao userDao = new UserDao();
		User user = userDao.getUserById(userId);
		String registrationId = user.getJpushKey();
		if (StringUtils.isNotBlank(registrationId)) {
			PushSettingDao pushSettingDao = new PushSettingDao();
			PushSetting setting = pushSettingDao.getPushSettingBySymbol(userId, msgType.getKey());
			if (null != setting && StringUtils.isNotBlank(setting.getSound())) {
				push(msg, registrationId, msgType, setting.getSound());
			} else {
				push(msg, registrationId, msgType);
			}
		}
	}
	
	
	public static void push(String msg, String registrationId,MsgType msgType) throws APIConnectionException, APIRequestException {
	    // HttpProxy proxy = new HttpProxy("localhost", 3128);
		boolean apnsProduction = true;
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
        if (MODE_KEY.equals("2")) {
        	jpushClient = new JPushClient(testMasterSecret, testAppKey, 3);
		}
        
        PushPayload payload = buildPushObject_android_and_ios(msg,registrationId,msgType.getKey()+"",apnsProduction);
        PushResult result = jpushClient.sendPush(payload);

	}
	
	public static void push(String msg, String registrationId, MsgType msgType, String sound) throws APIConnectionException, APIRequestException {
	    // HttpProxy proxy = new HttpProxy("localhost", 3128);
	    // Can use this https proxy: https://github.com/Exa-Networks/exaproxy
		boolean apnsProduction = true;
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
        if (MODE_KEY.equals("2")) {
        	jpushClient = new JPushClient(testMasterSecret, testAppKey, 3);
		}
        
        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObject_android_and_ios(msg, registrationId, msgType.getKey()+"", apnsProduction, sound);
        
//        try {
            PushResult result = jpushClient.sendPush(payload);
//            log.info("Got result - " + result);
//            
//        } catch (APIConnectionException e) {
//            log.error("Connection error. Should retry later. ", e);
//            
//        } catch (APIRequestException e) {
//            log.error("Error response from JPush server. Should review and fix it. " + appKey, e);
//            log.info("HTTP Status: " + e.getStatus());
//            log.info("Error Code: " + e.getErrorCode());
//            log.info("Error Message: " + e.getErrorMessage());
//            log.info("Msg ID: " + e.getMsgId());
//        }
	}

	
	public static PushPayload buildPushObject_android_and_ios(String msg,String registrationId,String typeStr,boolean apnsProduction, String sound) {
    	log.info("new version");
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
//                .setAudience(Audience.alias(ALIAS.concat(userId)))
                .setAudience(Audience.registrationId(registrationId))
                .setNotification(Notification.newBuilder()
                		.setAlert(msg)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.setTitle("BTCWINEX")
                				.setAlert(msg)
                				.addExtra("type", typeStr)
                				.build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.incrBadge(1)
                				.setAlert(msg)
                				.setSound(sound)
                				.addExtra("type", typeStr)
                				.build())
                		.build())
//				.setMessage(Message.content(msg)) //自定义消息
                .setOptions(Options.newBuilder()
                         .setApnsProduction(apnsProduction)
                         .build())
                .build();
    }

    public static PushPayload buildPushObject_android_and_ios(String msg,String registrationId,String typeStr,boolean apnsProduction) {
    	log.info("new version");
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
//                .setAudience(Audience.alias(ALIAS.concat(userId)))
                .setAudience(Audience.registrationId(registrationId))
                .setNotification(Notification.newBuilder()
                		.setAlert(msg)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.setTitle("BTCWINEX")
                				.setAlert(msg)
                				.addExtra("type", typeStr)
                				.build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.incrBadge(1)
                				.setAlert(msg)
                				.setSound("happy.caf")
                				.addExtra("type", typeStr)
                				.build())
                		.build())
//				.setMessage(Message.content(msg)) //自定义消息
                .setOptions(Options.newBuilder()
						.setApnsProduction(apnsProduction)
						.build())
                .build();
    }

    public static PushPayload buildPushObject_all_alias_alert(String msg,String userId) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(ALIAS.concat(userId)))
                .setNotification(Notification.alert(msg))
                .setOptions(Options.newBuilder()
                         .setApnsProduction(true)
                         .build())
//                .setNotification(Notification.newBuilder()
//                		.addPlatformNotification(IosNotification.newBuilder()
//                				.setAlert(msg)
//                				.setBadge(5)
//                				.setSound("happy.caf")
//                				.addExtra("from", "JPush")
//                				.build()))
                
//                .setNotification(Notification.newBuilder()
//                        .addPlatformNotification(IosNotification.newBuilder()
//                                .setAlert(ALERT)
//                                .setBadge(5)
//                                .setSound("happy.caf")
//                                .addExtra("from", "JPush")
//                                .build())
                
                .build();
    }
	
	public static void testSendPush() {
	    // HttpProxy proxy = new HttpProxy("localhost", 3128);
	    // Can use this https proxy: https://github.com/Exa-Networks/exaproxy
        JPushClient jpushClient = new JPushClient(masterSecret, appKey, 3);
        
        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObject_all_all_alert();
        
        try {
            PushResult result = jpushClient.sendPush(payload);
            log.info("Got result - " + result);
            
        } catch (APIConnectionException e) {
            log.error("Connection error. Should retry later. ", e);
            
        } catch (APIRequestException e) {
            log.error("Error response from JPush server. Should review and fix it. ", e);
            log.error("HTTP Status: " + e.getStatus());
            log.error("Error Code: " + e.getErrorCode());
            log.error("Error Message: " + e.getErrorMessage());
            log.error("Msg ID: " + e.getMsgId());
        }
	}
	
	public static PushPayload buildPushObject_all_all_alert() {
	    return PushPayload.alertAll(ALERT);
	}
	
    public static PushPayload buildPushObject_all_alias_alert() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias("alias1"))
                .setNotification(Notification.alert(ALERT))
                .build();
    }
    
    public static PushPayload buildPushObject_android_tag_alertWithTitle() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.tag("tag1"))
                .setNotification(Notification.android(ALERT, TITLE, null))
                .build();
    }
    
    public static PushPayload buildPushObject_android_and_ios() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag("tag1"))
                .setNotification(Notification.newBuilder()
                		.setAlert("alert content")
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.setTitle("Android Title").build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.incrBadge(1)
                				.addExtra("extra_key", "extra_value").build())
                		.build())
                .build();
    }
    
    public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag_and("tag1", "tag_all"))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(ALERT)
                                .setBadge(5)
                                .setSound("happy")
                                .addExtra("from", "JPush")
                                .build())
                        .build())
                 .setMessage(Message.content(MSG_CONTENT))
                 .setOptions(Options.newBuilder()
                         .setApnsProduction(true)
                         .build())
                 .build();
    }
    
    public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))
                        .addAudienceTarget(AudienceTarget.alias("alias1", "alias2"))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(MSG_CONTENT)
                        .addExtra("from", "JPush")
                        .build())
                .build();
    }

}

