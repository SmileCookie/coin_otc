package com.tenstar.timer.entrust;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Random;

import org.apache.log4j.Logger;

import com.world.model.Market;



public class Tools {
	public static Logger log = Logger.getLogger(Tools.class);
	public static Random commonRandom = new Random();
	/**
	 * 一个不等于0和最大值的max int
	 * @param max
	 * @return 随机数
	 */
private static int radomInt(int max){
	if(max<0)
		max=-max;
	if(max==0)
		max=100;
	 int rm =0;
	 while(rm==0)
		 rm= commonRandom.nextInt(max);
	 return rm;
}
//尽量不要太多的小数
public static double radomSet(double value,BigDecimal qujian){
	 int rm = commonRandom.nextInt(100);
	DecimalFormat df=null;
	if(rm<20 && qujian.compareTo(BigDecimal.valueOf(300))>0)
		df=new DecimalFormat("#.");
	else if(rm<55 && qujian.compareTo(BigDecimal.valueOf(100))>0)
		df=new DecimalFormat("#.#");
	else
		df=new DecimalFormat("#.##");
	
	String st=df.format(value);
	if(Double.parseDouble(st)==0){
		if(rm<10)
		   st="0.01";
		else if(rm<20)
			st="0.02";
		else if(rm<30) 
			st="0.03";
		else if(rm<40)
			st="0.04";
		else if(rm<50)
			st="0.05";
		else if(rm<60)
			st="0.06";
		else if(rm<70)
			st="0.07";
		else if(rm<90)
			st="0.02";
		else
			st="0.1";
	}
	double s=Double.parseDouble(st);
	return s;
}

	
	/**
	 * 获取指定的随机数数组
	 * @param minUnitPrice 最小的价格
	 * @param maxUnitPrice 最高的价格
	 * @param btcNumber 比特币数量
	 * @param splitNum 分割数量
	 * @param type 类型   1 均匀分布
	 * @param gaoWeiDuiQi  是否是按照价格从上向下对其
	 * @return 一个数组
	 */
	public static BigDecimal[][] GetRadom(BigDecimal minUnitPrice, BigDecimal maxUnitPrice, BigDecimal btcNumber, int splitNum, int type,
			boolean gaoWeiDuiQi,Market m) {

		//BigDecimal sub = maxUnitPrice.subtract(minUnitPrice);
		
		BigDecimal qujian = maxUnitPrice.subtract(minUnitPrice);

		BigDecimal[][] array = new BigDecimal[300][2];// 随便固定一个数量

		BigDecimal base = gaoWeiDuiQi ? maxUnitPrice : minUnitPrice;
		BigDecimal baseBtc = btcNumber;
		int i = 0;
		for (; i < splitNum; i++) {
			BigDecimal fudu = qujian.divide(BigDecimal.valueOf(splitNum),m.exchangeBixDian, RoundingMode.DOWN);// 直线类型的每次幅度是这样的
			
			BigDecimal btcSplit = btcNumber.divide(BigDecimal.valueOf(splitNum),m.numberBixDian, RoundingMode.DOWN);
			int rm = radomInt(160);
			fudu = fudu.multiply(BigDecimal.valueOf(180 - rm)).divide(BigDecimal.valueOf(100),m.exchangeBixDian, RoundingMode.DOWN);// 左右摆动20%
			int rm2 = radomInt(160);
			btcSplit = btcSplit.multiply(BigDecimal.valueOf(180).subtract(BigDecimal.valueOf(rm2))).divide(BigDecimal.valueOf(100),m.numberBixDian, RoundingMode.DOWN);// 也是左右摆动20%

			// log.info(i+":"+fudu+":"+isbuy+":"+baseBtc);

			if (gaoWeiDuiQi) {
				//base -= fudu;
				base = base.subtract(fudu);
			} else {
				//base += fudu;
				base = base.add(fudu);
			}
			//baseBtc -= btcSplit;
			baseBtc = baseBtc.subtract(btcSplit);
			if (baseBtc.compareTo(BigDecimal.ZERO) <= 0 || base.compareTo(maxUnitPrice) >0 || base.compareTo(minUnitPrice) < 0)
				break;
			double baseNew = Market.formatMoney(base,m);
			//baseNew = radomSetPrice(baseNew, qujian);
			BigDecimal money = Market.formatMoneyLong(baseNew,m);

			double splitNew = Market.formatNumber(btcSplit,m);
			splitNew = radomSet(splitNew, qujian);
			BigDecimal mumber = Market.formatNumberLong(splitNew,m);
			/*if (m.market.equals("btq_cny") || m.market.equals("eth_btc")
					|| m.market.equals("dao_eth"))// btq的小数位是不能去掉的，所以这样
				array[i] = new BigDecimal[] { Market.ffMoney(base,m), Market.ffNumber(btcSplit,m) };
			else*/
				array[i] = new BigDecimal[] { money, mumber };
		}
		return array;
	}
	
	public static void main(String[] args) throws Exception {
		Market m = Market.getMarkeByName("btc_cny");
		BigDecimal[][] entrustModel = GetRadom(BigDecimal.valueOf(7915), BigDecimal.valueOf(7920), BigDecimal.valueOf(200), 10, 1, true,m);
		for (int i = 0; i < 15; i++) {
			BigDecimal[] one = entrustModel[i];

			log.info(one[0] + ":" + one[1]);
			// log.info("仅仅是档位数量多了，但是数量"+hasEntityNumber+"还不够"+maxEntityNumber+",随机增加"+(gaoWeiDuiQi?"低位":"高位")+"："+can[i]+":"+one[1]);
		}

	}
	
}
