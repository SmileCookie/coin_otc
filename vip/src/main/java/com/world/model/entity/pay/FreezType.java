package com.world.model.entity.pay;

import com.world.model.entity.SysEnum;

public enum FreezType implements SysEnum{
	download(1 , "提现冻结") , 
	other(3, "其它冻结") ,
	cashUnFreez(4,"提现成功解冻"), 
	bid(6, "投资冻结") , 
	glyFreez(7 , "管理员冻结") , 
	glyUnFreez(8 , "管理员解冻"),
	buyBShare(11, "购买云算力冻结"), 
	cancelBuy(12, "取消买入解冻"),
	buySuccess(13, "买入云算力"),
	buyRemain(14, "买入剩余金额"),
	rmbFromP2pIn(40 , "从P2P转入"),rmbOutToP2p(41 , "转出至P2P"),
	btcFromP2pIn(42 , "从P2P转入"),btcOutToP2p(43 , "转出至P2P"),
	ltcFromP2pIn(44 , "从P2P转入"),ltcOutToP2p(45 , "转出至P2P"),
	;
	
	private FreezType(int key, String value) {
		this.key = key;
		this.value = value;
	}

	private int key;
	private String value;
	
	public int getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
