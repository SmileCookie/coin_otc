package com.common.api;

import java.io.IOException;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;
import com.world.util.request.HttpUtil;

public class KLineApiFactory {
	
	public static Logger log = Logger.getLogger(KLineApiFactory.class.getName());
	
	public static double getOkKLineVol(String currency, int type){
		try {
			String symbol = getSymbol(currency);
			String result = HttpUtil.doGet("https://www.okcoin.cn/api/v1/kline.do?symbol="+symbol+"&type="+getKType(type), null);
			JSONArray array = JSONArray.fromObject(result);
			if(array != null && array.size() > 0){
				double amount = 0d;
				for (int i = 0; i < array.size(); i++) {
					JSONArray arr = array.getJSONArray(i);
					if(TimeUtil.getMinuteFirst().getTime() == arr.getLong(0)){
						amount = arr.getDouble(5);
						break;
					}
				}
				return amount;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error(e.toString(), e);
		}
		return 0d;
	}
	
	public static String getSymbol(String symbol){
		if("btc".equalsIgnoreCase(symbol)){
			return "btc_cny";
		} else if("ltc".equalsIgnoreCase(symbol)){
			return "ltc_cny";
		} else if("eth".equalsIgnoreCase(symbol)){
			return "eth_cny";
		} else if("etc".equalsIgnoreCase(symbol)){
			return "etc_cny";
		} else if("dao".equalsIgnoreCase(symbol)){
			return "dao_cny";
		} else if("ethbtc".equalsIgnoreCase(symbol)){
			return "eth_btc";
		} else if("daoeth".equalsIgnoreCase(symbol)){
			return "dao_eth";
		}
		return "";
	}
	
	
	public static String getKType(int mins){
		String ktype = "5min";
		switch (mins) {
		case 1:
			ktype = "1min";
			break;
		case 3:
			ktype = "3min";
			break;
		case 5:
			ktype = "5min";
			break;
		case 15:
			ktype = "15min";
			break;
		case 30:
			ktype = "30min";
			break;
		default:
			break;
		}
		return ktype;
	}
	
	public static void main(String [] args){
		log.info(KLineApiFactory.getOkKLineVol("btc", 1));
	}
}
