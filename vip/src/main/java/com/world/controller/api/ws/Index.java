package com.world.controller.api.ws;

import com.world.util.sign.RSACoder;
import com.world.web.Page;
import com.world.web.action.UserAction;
import com.world.web.sso.SessionUser;
import com.world.web.sso.session.Session;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index extends UserAction{
    private final static Logger logger = LoggerFactory.getLogger(Index.class);
	
	/**
	 * 验证用户登录状态，返回用户id
	 */
	@Page(Viewer = JSON)
	public void verifyUserSession() {
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

            String sessionId = param("sid");
            if(StringUtils.isBlank(sessionId)){
                json("参数错误！", false, "", true);
                return;
            }

            Session session = new Session(sessionId);
            SessionUser su = session.getUser("");
            if(null == su){
                json("session不存在！", false, "", true);
                return;
            }

            json("success", true, su.getUid(), true);
        } catch (Exception e) {
            logger.error("获取币种配置信息异常", e);
        }
	}
}