package com.wallet.eth.format;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class EtherHexFormatUtils {

	private final static Logger log = Logger.getLogger(EtherHexFormatUtils.class.getName());

	private final static long weiToEth = (long) Math.pow(10 , 18);
	
	private final static long dgdToEth = (long) Math.pow(10 , 9);
	
	private final static long daoToEth = (long) Math.pow(10 , 16);
	
	private final static int radix = 16;
	
	private final static long toEthLong = (long) Math.pow(10 , 8); 
	
	/**
	 * eth to hex
	 * @param hexString
	 * @return
	 */
	public static BigDecimal numberFormat(String hexString){
		return numberFormat(hexString, radix);
	}
	
	/**
	 * wei to eth
	 * @param wei
	 * @return
	 */
	public static BigDecimal numberFormat(BigInteger wei){
		return new BigDecimal(wei).divide(new BigDecimal(weiToEth), 8,BigDecimal.ROUND_DOWN);
	}
	
	/**
	 * hex to eth 
	 * @param hexString
	 * @param radix
	 * @return
	 */
	public static BigDecimal numberFormat(String hexString, int radix){
		BigInteger ret = hexToInteger(hexString, radix);
		return new BigDecimal(ret).divide(new BigDecimal(weiToEth), 8,BigDecimal.ROUND_DOWN);
	}
	
	public static BigInteger hexToInteger(String hexString){
		return hexToInteger(hexString, radix);
	}
	
	public static BigDecimal getDGD(BigInteger dgd){
		return new BigDecimal(dgd).divide(new BigDecimal(dgdToEth), 8,BigDecimal.ROUND_DOWN);
	}
	
	public static BigDecimal getDGD(String hex){
		return getDGD(hexToInteger(hex, radix));
	}
	
	public static BigDecimal getDAO(BigInteger dao){
		return new BigDecimal(dao).divide(new BigDecimal(daoToEth), 8,BigDecimal.ROUND_DOWN);
	}
	
	public static BigDecimal getDAO(String hex){
		return getDAO(hexToInteger(hex, radix));
	}
	
	public static BigInteger hexToInteger(String hexString, int radix){
		try {
			//
			if("0x".startsWith(hexString)){
				hexString = hexString.substring(2);
			}
			//
			if (hexString.length()>2 && "0x".equals(hexString.substring(0, 2))){
				hexString = hexString.substring(2);
			}
		
			return new BigInteger(hexString, radix);
		} catch (NumberFormatException e) {
			log.error(e.toString(), e);
		}
		return BigInteger.ZERO;
	}
	
	
	public static String hexToString(String hexString){
		try {
			//
			if("0x".startsWith(hexString)){
				hexString = hexString.substring(2);
			}
			//
			if ("0x".equals(hexString.substring(0, 2))){
				hexString = hexString.substring(2);
			}
			return new String(hexString);
		} catch (NumberFormatException e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	
	public static String integerToHex(BigInteger string){
		return "0x" + string.toString(radix);
	}
	
	public static String longToHex(long string){
		return "0x" + BigInteger.valueOf(string).toString(radix);
	}
	
	public static String toHexString(BigDecimal amount){
		try {
			amount = amount.multiply(new BigDecimal(weiToEth));
			//long ret = amount.longValue();
			return "0x" + amount.toBigInteger().toString(radix);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	
	public static String toDGDHexString(BigDecimal amount){
		try {
			amount = amount.multiply(new BigDecimal(dgdToEth));
			String ret = amount.toBigInteger().toString(radix);
			int blankLength = 64-ret.length();
			String result = "";
			for(int i=0;i<blankLength;i++){
				result = result + "0";
			}
			return result + ret;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	
	public static String toDAOHexString(BigDecimal amount){
		try {
			amount = amount.multiply(new BigDecimal(daoToEth));
			String ret = amount.toBigInteger().toString(radix);
			int blankLength = 64-ret.length();
			String result = "";
			for(int i=0;i<blankLength;i++){
				result = result + "0";
			}
			return result + ret;
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return null;
	}
	
	/**
	 * 10的8次方
	 * @return
	 */
	public static long toEthLong(BigInteger integer){
		BigDecimal amount = numberFormat(integer);
		return amount.multiply(BigDecimal.valueOf(toEthLong)).longValue();
	}
	
	/**
	 * 10的8次方
	 * @return
	 */
	public static long toEthLong(BigDecimal decimal){
		return decimal.multiply(BigDecimal.valueOf(toEthLong)).longValue();
	}
	
	public static String ethLongToWeiHex(long string){
		BigDecimal amount = new BigDecimal(string);
		amount = amount.multiply(BigDecimal.valueOf(weiToEth)).divide(BigDecimal.valueOf(toEthLong));
		return "0x" + amount.toBigInteger().toString(radix);
	}
	
	public static void main(String[] args) {
		log.info(hexToInteger("0xf00ab5b7d9882bccd07e41eccd782a6b"));
//		log.info(numberFormat("0x1aabd416dfb44d68").toPlainString());
		log.info(toDGDHexString(new BigDecimal("0.001")));
//		BigInteger bi = hexToInteger("0x1ebbd9a02c0c1a52ef419c3b6bcfe5b6");
//		log.info(bi.toString());
//		log.info(bi.longValue());
//		log.info(integerToHex(new BigInteger(bi.toString())));
		
//		String hex = ethLongToWeiHex(1500000l);
//		log.info(hex);
//		log.info(numberFormat(hex).toPlainString());
		
//		String ret = HexFormatUtils.ethLongToWeiHex(20000000000000000l - 10000l);
//		log.info(ret);
//		log.info(HexFormatUtils.numberFormat(ret).toPlainString());
		
		log.info(new DecimalFormat("0.0").format(EtherHexFormatUtils.weiToEth * 0.1d));
		
	}
}
