package com.world.model.entity.pay.fee;

import java.math.BigDecimal;

import com.world.model.entity.coin.CoinProps;
import com.world.util.DigitalUtil;

/****
 * 
 * @author Administrator
 *
 */
public class FeeFactory {

	/***
	 * 获取费率
	 * @param fd
	 * @param version
	 * @return
	 */
	public static BigDecimal getFee(FeeDetails fd , int version){
		return DigitalUtil.getBigDecimal(fd.getFeeByVersion(version));
	}
	/***
	 * 获取提现费率
	 * @param fd
	 * @param version
	 * @return
	 */
	public static BigDecimal getFee(CoinProps coin){
		FeeDetails fd = getFd(coin);
		return DigitalUtil.getBigDecimal(fd.getFeeByVersion(1));
	}

	public static FeeDetails getFd(CoinProps coin){
		FeeDetails fd = null;
		if(coin.getStag().equals("rmb") || coin.getStag().equals("cny")){
			fd = FeeDetails.commonReachCard;
		}else if(coin.getStag().equals("btc")){
			fd = FeeDetails.btcOut;
		}else if(coin.getStag().equals("ltc")){
			fd = FeeDetails.ltcOut;
		}else if(coin.getStag().equals("doge")){
			fd = FeeDetails.dogeOut;
		}else if(coin.getStag().equals("eth")){
			fd = FeeDetails.ethOut;
		}else if(coin.getStag().equals("etc")){
			fd = FeeDetails.ethcOut;
		}
		return fd;
	}
	
	
	/****
	 * 获取最大额度
	 * @param fd
	 * @param version
	 * @return
	 */
	public static BigDecimal getEd(FeeDetails fd , int version){
		return DigitalUtil.getBigDecimal(fd.getEdByVersion(version));
	}
	/****
	 * 获取最小
	 * @param fd
	 * @param version
	 * @return
	 */
	public static BigDecimal getMin(FeeDetails fd , int version){
		return DigitalUtil.getBigDecimal(fd.getMinByVersion(version));
	}
	/*****
	 * 获取确认次数
	 * @param fd
	 * @param version
	 * @return
	 */
	public static double getConfirmTimes(FeeDetails fd , int version){
		return fd.getConfirmTimesByVersion(version);
	}
	
	public static BigDecimal getAfterFeeRate(FeeDetails fd , int version){
		return  BigDecimal.ONE.subtract(getFee(fd, version));//DigitalUtil.sub(1d , getFee(fd, version));
	}
	
	
	public static FeeDetails getFdByLxDays(int lxDays){
		if(lxDays > 7){
			lxDays = 7;
		}
		switch (lxDays) {
		case 0: return FeeDetails.than1OneDay;
		case 1: return FeeDetails.than1OneDay;
		case 2: return FeeDetails.than1TwoDay;
		case 3: return FeeDetails.than1ThreeDay;
		case 4: return FeeDetails.than1FourDay;
		case 5: return FeeDetails.than1FiveDay;
		case 6: return FeeDetails.than1SixDay;
		case 7: return FeeDetails.than1SevenDay;
		default:
			break;
		}
		return null;
	}
	
}
