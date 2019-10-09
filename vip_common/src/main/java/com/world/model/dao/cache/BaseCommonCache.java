package com.world.model.dao.cache;

public interface BaseCommonCache {
	///用会员资产信息KEY
	public final static String userFundsKey = "user_funds_";
	public final static String lastBtcPriceKey = "btc_cny_l_price";
	public final static String lastLtcPriceKey = "ltc_cny_l_price";
	public final static String lastBtqPriceKey = "btq_cny_l_price";
	public final static String lastEthPriceKey = "eth_cny_l_price";
	public final static String lastDaoPriceKey = "dao_cny_l_price";
	public final static String lastEtcPriceKey = "etc_cny_l_price";
	public final static String lastEthBtcPriceKey = "eth_btc_l_price";
	public final static String lastDaoEthPriceKey = "dao_eth_l_price";
}
