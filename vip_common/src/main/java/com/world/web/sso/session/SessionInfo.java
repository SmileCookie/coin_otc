package com.world.web.sso.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SessionInfo implements Serializable{
	private static final long serialVersionUID = 4164782286960053400L;
	
	public String ip;//客户端ip
	public long codeTime;//最后发送验证码的时间
	public Map<String , CodeInfo> codeInfos = new HashMap<String, CodeInfo>();//各种类型的验证码发送次数
	public long startTime;//创建时间 ms
	public String hasCheckedNumbers;//已经检测的设备号码  如：+86 133322111,+66 83827837,
	
}
