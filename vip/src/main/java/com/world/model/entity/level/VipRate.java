package com.world.model.entity.level;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.model.entity.EnumUtils;
import com.world.model.entity.SysEnum;
import com.world.web.action.Action;

public enum VipRate implements SysEnum{
	
	vip0(0,"vip0",0			,"0.0020"),
	vip1(1,"vip1",10000		,"0.0018"),
	vip2(2,"vip2",30000		,"0.0016"),
	vip3(3,"vip3",60000		,"0.0014"),
	vip4(4,"vip4",100000	,"0.0012"),
	vip5(5,"vip5",150000	,"0.0010"),
	vip6(6,"vip6",300000	,"0.0009"),
	vip7(7,"vip7",500000	,"0.0008"),
	vip8(8,"vip8",800000	,"0.0007"),
	vip9(9,"vip9",1200000	,"0.0006"),
	vip10(10,"vip10",2000000	,"0.0"),
	;
	
	private VipRate(int id,String name, double jifen, String withdrawFee) {
		this.id = id;
		this.name = name;
		this.jifen = jifen;
		this.withdrawFee = new BigDecimal(withdrawFee);
	}
	private int id;
	private String name;
	private double jifen;
	private BigDecimal withdrawFee;
	
	/**
	 * 按类型返回次提现最低额度,参数:rmb/btc/ltc
	 */
	public BigDecimal getOnetimeMin(String type){
		if("rmb".equalsIgnoreCase(type)){
			return new BigDecimal("10");
		}else if ("btc".equalsIgnoreCase(type)) {
			return new BigDecimal("0.001");
		}else if ("ltc".equalsIgnoreCase(type)) {
			return new BigDecimal("0.01");
		}else{
			return null;
		}
	}
	
	public static BigDecimal getOnetimeMax(){
		return new BigDecimal(50000);
	}
	
	public static BigDecimal getOnedayMax(){
		return new BigDecimal(1000000);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getJifen() {
		return jifen;
	}

	public BigDecimal getWithdrawFee() {
		return withdrawFee;
	}

	public int getKey() {
		return id;
	}

	public String getValue() {
		return null;
	}
	
	
	public static VipRate getEnumByKey(int key){
		EnumSet<VipRate> viprates = EnumUtils.getAll(VipRate.class);
		VipRate newVip = null ;
		if(viprates != null){
			Iterator<VipRate> it = viprates.iterator();
			while(it.hasNext()){
				VipRate vip=it.next();
				if(key == vip.getKey() && vip.getId()<11){
					newVip = vip;
					break;
				}
			}
		}
		return newVip;
	}
}
