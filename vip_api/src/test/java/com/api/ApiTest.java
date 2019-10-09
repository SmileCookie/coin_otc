package com.api;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;

import com.api.VipResponse;
import com.api.user.UserManager;
import com.api.util.DigitalUtil;

public class ApiTest {

	@Test
	public void testFundsConvert(){
		try {
//			long start = 0;
//			long end = 0;
//			long result = 0;
//			int len = 10000;
//			for (int i = 0;i < len; i++) {
//				start = System.currentTimeMillis();
//				response = new P2PManager().success("73844","73844",0, 1234,"btc", 0.578, 0l);
//				end = System.currentTimeMillis();
//				result += (end - start);
//				System.out.println(i + ": " + response.getCode() + " : " + (end - start));
//			}
//		
//			System.out.println("平均每次请求所花时间为：" + (result/len));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testValidateSafePwd(){
		try {
			VipResponse response = new UserManager().validateSafePwd("73829", "110100");
			System.out.println("[code]:" + response.getCode());
			System.out.println("[Msg]:" + response.getMsg());
			System.out.println("[Params]:" + response.getParams());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSendSms(){
		try {
			VipResponse response = new UserManager().sendSms("74122,741222354125", "test api sendsms content");
			System.out.println("[code]:" + response.getCode());
			System.out.println("[Msg]:" + response.getMsg());
			System.out.println("[Params]:" + response.getParams());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetUserP2pFunds(){
		try {
			VipResponse response = new UserManager().getUserP2pFunds("74579");
			JSONObject json = JSONObject.fromObject(response.getMsg());
			if(response.taskIsFinish()){
				if((Boolean) json.get("isSuc")){
					System.out.println(json.get("datas"));
					JSONArray array = JSONArray.fromObject(json.get("datas"));
					
//					BigDecimal[] result = (BigDecimal[]) JSONArray.toArray(array);
					System.out.println("借入的CNY：" + DigitalUtil.getBigDecimal(array.getDouble(0)));
					System.out.println("借入的BTC：" + DigitalUtil.roundDown(array.getDouble(1), 8));
					System.out.println("借入的LTC：" + DigitalUtil.roundDown(array.getDouble(2), 8));
					System.out.println("借出的CNY：" + DigitalUtil.roundDown(array.getDouble(3), 2));
					System.out.println("借出的BTC：" + DigitalUtil.roundDown(array.getDouble(4), 8));
					System.out.println("借出的LTC：" + DigitalUtil.roundDown(array.getDouble(5), 8));
				}else{
					System.out.println(json.get("des"));
				}
			}else{
				System.out.println(json.get("des"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
