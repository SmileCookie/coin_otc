package com.world.model.entity.pay;

import java.util.*;

public class ChinaBank4Mobile {
	private static Map<String,String> chinaBanks4Mobile = new HashMap<String, String>();
	
	static{
		
		chinaBanks4Mobile.put(ChinaBank.gsyh_.getTag(), "/statics/img/cn/yh/mobile/bank-icbc.png");
		
		chinaBanks4Mobile.put(ChinaBank.yzyh_.getTag(), "/statics/img/cn/yh/mobile/bank-psbc.png");
		chinaBanks4Mobile.put(ChinaBank.jsyh_.getTag(), "/statics/img/cn/yh/mobile/bank-ccb.png");
		chinaBanks4Mobile.put(ChinaBank.nyyh_.getTag(), "/statics/img/cn/yh/mobile/bank-abc.png");
		chinaBanks4Mobile.put(ChinaBank.zsyh_.getTag(), "/statics/img/cn/yh/mobile/bank-cmb.png");
		chinaBanks4Mobile.put(ChinaBank.zhongGuo_.getTag(), "/statics/img/cn/yh/mobile/bank-bocsh.png");
		chinaBanks4Mobile.put(ChinaBank.jiaoTong_.getTag(), "/statics/img/cn/yh/mobile/bank-bocom.png");
		chinaBanks4Mobile.put(ChinaBank.guangFa_.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		chinaBanks4Mobile.put(ChinaBank.zhongXin_.getTag(), "/statics/img/cn/yh/mobile/cncb.png");
		chinaBanks4Mobile.put(ChinaBank.guangDa_.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		chinaBanks4Mobile.put(ChinaBank.pufa_.getTag(), "/statics/img/cn/yh/mobile/bank-spdb.png");
		chinaBanks4Mobile.put(ChinaBank.minSheng_.getTag(), "/statics/img/cn/yh/mobile/cmbc.png");
		chinaBanks4Mobile.put(ChinaBank.xingYe_.getTag(), "/statics/img/cn/yh/mobile/bank-cib.png");
		chinaBanks4Mobile.put(ChinaBank.pingAn_.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		chinaBanks4Mobile.put(ChinaBank.beiJing_.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		chinaBanks4Mobile.put(ChinaBank.huaXia_.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		chinaBanks4Mobile.put(ChinaBank.shangHai_.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		chinaBanks4Mobile.put(ChinaBank.shangHaiNongShang_.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		chinaBanks4Mobile.put(ChinaBank.other.getTag(), "/statics/img/cn/yh/mobile/default.png");//
		
		//withdrawBanks=EnumSet.allOf(ChinaBank.class.getTag(), "/statics/img/cn/yh/mobile/bank-icbc.png");
	}

	public static Map<String, String> getChinabanks4mobile() {
		return chinaBanks4Mobile;
	}
}
