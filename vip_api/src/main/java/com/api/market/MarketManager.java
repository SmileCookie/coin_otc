package com.api.market;

import java.io.IOException;

import com.api.config.ApiConfig;
import com.api.util.http.HttpUtil;

public class MarketManager {
	
	private static MarketManager marketManager;
	
	public static MarketManager getInstance(){
		if(marketManager == null) 
			marketManager = new MarketManager();
		return marketManager;
	}
	
	private int connectTimeout = 20000;
	private int readTimeout = 20000;
	
	public void getBTCCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-btcdefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getLTCCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-ltcdefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getBTQCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-btqdefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getETHCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-ethdefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getDAOCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-daodefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getETHBTCCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-ethbtcdefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getETCCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-etcdefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getDAOETHCurrentPrice(){
		try {
			HttpUtil.doPost(ApiConfig.getInstance().getValue("transServerUrl") + "Line/GetCurrentPrice-daoethdefault?lastTime=" + System.currentTimeMillis(), null, connectTimeout, readTimeout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
