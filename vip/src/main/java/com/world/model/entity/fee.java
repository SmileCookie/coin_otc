package com.world.model.entity;

import java.math.BigDecimal;

public class fee {
	public static long btcFee=100000000;
   public static long mbtcFee=100000;
   
   public static BigDecimal converts = new BigDecimal(btcFee);
   
   
   public static int MoneyScale = 5;//全站资金最多保留的小数位
   
   public static int priceScale = 3;
   
   public static int towBei = 2;
}
