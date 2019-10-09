package com.api.util;

import java.math.BigDecimal;

public class DigitalUtil {

	//默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;
    //这个类不能实例化
    private DigitalUtil(){
    }

    /**
     * 提供精确的加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    /**
     * 提供精确的减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
    
    public static double mul(double v1,double v2, int rounddown){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }
    
    public static double mul(double v1,long v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Long.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
    
    public static long longMultiply(double v1,long v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Long.toString(v2));
        return b1.multiply(b2).longValue();
    }

    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1,double v2){
        return div(v1,v2,DEF_DIV_SCALE);
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    /**
     * 
     * @param v1
     * @param v2
     * @param scale
     * @param 采用的数字舍入方式  
     * @return
     */
    public static double div(double v1,double v2,int scale, int round){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double roundDown(double v,int scale){
    	if(scale<0){
    		throw new IllegalArgumentException(
    				"The scale must be a positive integer or zero");
    	}
    	BigDecimal b = new BigDecimal(Double.toString(v));
    	BigDecimal one = new BigDecimal("1");
    	return b.divide(one,scale,BigDecimal.ROUND_DOWN).doubleValue();
    }
    
    public static BigDecimal roundDown(BigDecimal v,int scale){
    	if(scale<0){
    		throw new IllegalArgumentException(
    				"The scale must be a positive integer or zero");
    	}
    	BigDecimal one = new BigDecimal("1");
    	return v.divide(one,scale,BigDecimal.ROUND_DOWN);
    }
    
    public static long doubleToLong(double v1){
    	 BigDecimal b1 = new BigDecimal(Double.toString(v1));
    	 return b1.longValue();
    }
    
    public static long floatToLong(float v1){
   	 BigDecimal b1 = new BigDecimal(Float.toString(v1));
   	 return b1.longValue();
    }
    
    public static double floatToDouble(float v1){
    	BigDecimal bd = new BigDecimal(Float.toString(v1));
		return bd.doubleValue();
    }
    
    public static float doubleToFloat(double v1){
    	BigDecimal bd = new BigDecimal(String.valueOf(v1));
		return bd.floatValue();
    }
    
    public static int doubleToInt38(double v1){
    	BigDecimal bd = new BigDecimal(String.valueOf(v1));
		return bd.intValue();
    }
    
    public static BigDecimal getBigDecimal(Object v){
    	return new BigDecimal(v.toString());
    }
    
    public static void main(String[] args) {
//		System.out.println(sub(200, 3));
//		
//		long v1 = 2644700000l;
//		double v2 = v1;
//		System.out.println(v2);
//		
//		float v3 = Float.parseFloat(String.valueOf(v1));
//		
//		System.out.println(v3);
//		
//		float a = 498.3066f-0f;
//		double b = a;
//		System.out.println(b);
//		b = DigitalUtil.floatToDouble(498.3066f-0);
//		System.out.println(b);
//		
//		
//		double e = 16660.4355d - 0d;
//		
//		System.out.println("++"+e);
//		
//		double fmoney = DigitalUtil.sub(DigitalUtil.floatToDouble(17832f),DigitalUtil.floatToDouble(1172.454f));
//		System.out.println(fmoney);
//		
//		double p = DigitalUtil.div(1200400000, fee.btcFee);
//		System.out.println(p);
//		
//		double priceTotal = DigitalUtil.round(DigitalUtil.mul(742.48d, p), fee.MoneyScale);
//		
//		System.out.println(priceTotal);
		
//		BigDecimal b1 = new BigDecimal(Double.toString(175.4552));
//        System.out.println(b1.setScale(3,BigDecimal.ROUND_DOWN).doubleValue());
    	
    	System.out.println(roundDown(4.551, 2));
	}
}
