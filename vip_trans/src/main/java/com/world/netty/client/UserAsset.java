package com.world.netty.client;

import java.math.BigDecimal;

import com.world.util.DigitalUtil;

import net.sf.json.JSONObject;

public class UserAsset {

	
	/**
	 * 根据用户ID获取用户资产
	 * @param userIdStr 用户ID 
	 * @return 资产信息json字符串
	 * @author zhanglinbo 20160716
	 */
	public static String getUserAsset(String userIdStr){
		
		BigDecimal[] ufunds = UserCache.getUserFunds(userIdStr);
		BigDecimal p2pStatus = BigDecimal.ZERO;
		if(ufunds.length > 14){
			p2pStatus = ufunds[13];
		}
		BigDecimal totalAssets = DigitalUtil.roundDown(ufunds[8], 8);
		BigDecimal netAssets = DigitalUtil.roundDown(ufunds[9], 8);
		BigDecimal availableRmb = DigitalUtil.roundDown(ufunds[0], 8);
		BigDecimal freezRmb = DigitalUtil.roundDown(ufunds[1], 8);
		BigDecimal loanRmb = DigitalUtil.roundDown(ufunds[10], 8);
		
		BigDecimal availableBtc = DigitalUtil.roundDown(ufunds[2], 8);
		BigDecimal freezBtc = DigitalUtil.roundDown(ufunds[3], 8);
		BigDecimal loanBtc = DigitalUtil.roundDown(ufunds[11], 8);
		
		BigDecimal availableLtc = DigitalUtil.roundDown(ufunds[4], 8);
		BigDecimal freezLtc = DigitalUtil.roundDown(ufunds[5], 8);
		BigDecimal loanLtc = DigitalUtil.roundDown(ufunds[12], 8);
		
		BigDecimal availableBtq = DigitalUtil.roundDown(ufunds[6], 8);
		BigDecimal freezBtq = DigitalUtil.roundDown(ufunds[7], 8);
		
		//15.可用ETH 16.冻结ETH 17.ETH借贷
		BigDecimal availableEth = BigDecimal.ZERO;
		BigDecimal freezEth = BigDecimal.ZERO;
		BigDecimal loanEth = BigDecimal.ZERO;
		
		//24.可用DAO 25.冻结DAO 26.DAO借贷
		BigDecimal availableDao = BigDecimal.ZERO;
		BigDecimal freezDao = BigDecimal.ZERO;
		BigDecimal loanDao = BigDecimal.ZERO;
		
		//27.可用ETC 28.冻结ETC 29.ETC借贷
		BigDecimal availableEtc = BigDecimal.ZERO;
		BigDecimal freezEtc = BigDecimal.ZERO;
		BigDecimal loanEtc = BigDecimal.ZERO;
		
		
		BigDecimal canRmb = BigDecimal.ZERO;
		BigDecimal canBtc = BigDecimal.ZERO;
		BigDecimal canLtc = BigDecimal.ZERO;
		BigDecimal canEth = BigDecimal.ZERO;
		BigDecimal canDao = BigDecimal.ZERO;
		BigDecimal canEtc = BigDecimal.ZERO;
		
		BigDecimal level = BigDecimal.ZERO;
		BigDecimal pingCangFengXian = BigDecimal.ZERO;
		
		if(ufunds.length > 16){
			availableEth = DigitalUtil.roundDown(ufunds[15], 8);
			freezEth = DigitalUtil.roundDown(ufunds[16], 8);
		}
		
		if(ufunds.length > 17){
			loanEth = DigitalUtil.roundDown(ufunds[17], 8); 
		}
		
		if(ufunds.length > 25){
			availableDao = DigitalUtil.roundDown(ufunds[24], 8);
			freezDao = DigitalUtil.roundDown(ufunds[25], 8);
		}
		
		if(ufunds.length > 26){
			loanDao = DigitalUtil.roundDown(ufunds[26], 8); 
		}
		
		if(ufunds.length > 27){
			availableEtc = DigitalUtil.roundDown(ufunds[27], 8);
			freezEtc = DigitalUtil.roundDown(ufunds[28], 8);
		}
		if(ufunds.length > 29){
			loanEtc = DigitalUtil.roundDown(ufunds[29], 8); 
		}
		
		if(ufunds.length > 18){
			canRmb = DigitalUtil.roundDown(ufunds[18], 8); 
			canBtc = DigitalUtil.roundDown(ufunds[19], 8); 
			canLtc = DigitalUtil.roundDown(ufunds[20], 8); 
			canEth = DigitalUtil.roundDown(ufunds[21], 8); 
		}
		if(ufunds.length > 22){
			level = DigitalUtil.roundDown(ufunds[22], 0); 
			pingCangFengXian = DigitalUtil.roundDown(ufunds[23], 1); 
		}
		
		JSONObject json = new JSONObject();
		
		json.put("totalAssets", totalAssets.stripTrailingZeros().toPlainString());
		json.put("netAssets", netAssets.stripTrailingZeros().toPlainString());
		json.put("availableRmb", availableRmb.stripTrailingZeros().toPlainString());
		json.put("freezRmb", freezRmb.stripTrailingZeros().toPlainString());
		json.put("loanRmb", loanRmb.stripTrailingZeros().toPlainString());
		json.put("availableBtc", availableBtc.stripTrailingZeros().toPlainString());
		json.put("freezBtc", freezBtc.stripTrailingZeros().toPlainString());
		json.put("loanBtc", loanBtc.stripTrailingZeros().toPlainString());
		json.put("availableLtc", availableLtc.stripTrailingZeros().toPlainString());
		json.put("freezLtc", freezLtc.stripTrailingZeros().toPlainString());
		json.put("loanLtc", loanLtc.stripTrailingZeros().toPlainString());
		json.put("availableBtq", availableBtq.stripTrailingZeros().toPlainString());
		json.put("freezBtq", freezBtq.stripTrailingZeros().toPlainString());
		json.put("availableEth", availableEth.stripTrailingZeros().toPlainString());
		json.put("freezEth", freezEth.stripTrailingZeros().toPlainString());
		json.put("loanEth", loanEth.stripTrailingZeros().toPlainString());
		json.put("availableEtc", availableEtc.stripTrailingZeros().toPlainString());
		json.put("freezEtc", freezEtc.stripTrailingZeros().toPlainString());
		json.put("loanEtc", loanEtc.stripTrailingZeros().toPlainString());
		json.put("availableDao", availableDao.stripTrailingZeros().toPlainString());
		json.put("freezDao", freezDao.stripTrailingZeros().toPlainString());
		json.put("loanDao", loanDao.stripTrailingZeros().toPlainString());
		json.put("p2pStatus", p2pStatus.stripTrailingZeros().toPlainString());
		json.put("canRmb", canRmb.stripTrailingZeros().toPlainString());
		json.put("canBtc", canBtc.stripTrailingZeros().toPlainString());
		json.put("canLtc", canLtc.stripTrailingZeros().toPlainString());
		json.put("canEth", canEth.stripTrailingZeros().toPlainString());
		json.put("canDao", canDao.stripTrailingZeros().toPlainString());
		json.put("canEtc", canEtc.stripTrailingZeros().toPlainString());
		json.put("level", level.stripTrailingZeros().toPlainString());
		json.put("pingCangFengXian", pingCangFengXian.stripTrailingZeros().toPlainString());
		
		return json.toString();
		
	}
}
