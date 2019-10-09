package com.world.model.entity.pay;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class ChinaBankHelp {
	private final static List<ChinaBank> chinaBanks=new ArrayList<ChinaBank>();
	private static List<ChinaBank> withdrawBanks=null;
	private final static List<ChinaBank> chinaBanksHC = new ArrayList<ChinaBank>();
	
	static{
		chinaBanks.add(ChinaBank.zfb);
		chinaBanks.add(ChinaBank.gsyh);
		chinaBanks.add(ChinaBank.yzyh);
		chinaBanks.add(ChinaBank.jsyh);
		chinaBanks.add(ChinaBank.nyyh);
		chinaBanks.add(ChinaBank.zsyh);
		
		chinaBanks.add(ChinaBank.zhongGuo);
		chinaBanks.add(ChinaBank.jiaoTong);
		chinaBanks.add(ChinaBank.zhongXin);
		chinaBanks.add(ChinaBank.guangDa);
		chinaBanks.add(ChinaBank.pufa);
		chinaBanks.add(ChinaBank.shenZhenFaZhan);
		chinaBanks.add(ChinaBank.minSheng);
		chinaBanks.add(ChinaBank.xingYe);
		
		chinaBanks.add(ChinaBank.pingAn);
		chinaBanks.add(ChinaBank.beiJing);
		
		//chinaBanks.add(ChinaBank.dongya);
		chinaBanks.add(ChinaBank.cft);
//		chinaBanks.add(ChinaBank.international);
		chinaBanks.add(ChinaBank.taobao);
		
		//chinaBanks.add(ChinaBank.huaXia);
		//艺萃提现四大银行
//		withdrawBanks.add(ChinaBank.gsyh);
//		withdrawBanks.add(ChinaBank.jsyh);
//		withdrawBanks.add(ChinaBank.nyyh);
//		withdrawBanks.add(ChinaBank.zsyh);
		
		
		
		chinaBanksHC.add(ChinaBank.gsyh_);
		chinaBanksHC.add(ChinaBank.yzyh_);
		chinaBanksHC.add(ChinaBank.jsyh_);
		chinaBanksHC.add(ChinaBank.nyyh_);
		chinaBanksHC.add(ChinaBank.zsyh_);
		chinaBanksHC.add(ChinaBank.zhongGuo_);
		chinaBanksHC.add(ChinaBank.jiaoTong_);
		chinaBanksHC.add(ChinaBank.guangFa_);
		chinaBanksHC.add(ChinaBank.zhongXin_);
		chinaBanksHC.add(ChinaBank.guangDa_);
		chinaBanksHC.add(ChinaBank.pufa_);
		chinaBanksHC.add(ChinaBank.minSheng_);
		chinaBanksHC.add(ChinaBank.xingYe_);
		chinaBanksHC.add(ChinaBank.pingAn_);
		chinaBanksHC.add(ChinaBank.beiJing_);
		chinaBanksHC.add(ChinaBank.huaXia_);
		chinaBanksHC.add(ChinaBank.shangHai_);
		chinaBanksHC.add(ChinaBank.shangHaiNongShang_);
		chinaBanksHC.add(ChinaBank.other);
		
		//withdrawBanks=EnumSet.allOf(ChinaBank.class);
	}
	private static final int version = 1;
	
	public static int getVersion() {
		return version;
	}

	public static List<ChinaBank> getAllBanks(){
		return chinaBanks;
	}
	
	public static List<ChinaBank> getAllBanksHC(){
		return chinaBanksHC;
	}
	public static ChinaBank getCBByTag(String tag){
		for(ChinaBank cb : chinaBanks){
			if(cb.getTag().equals(tag)){
				return cb;
			}
		}
		return null;
	}
	
	public static ChinaBank getHCBankById(int id){
		for(ChinaBank cb : chinaBanksHC){
			if(cb.getId() == id){
				return cb;
			}
		}
		return null;
	}
	
	public static ChinaBank getBankById(int id){
		for(ChinaBank cb : chinaBanks){
			if(cb.getId() == id){
				return cb;
			}
		}
		return null;
	}
	
	/****
	 * 可提现的银行
	 * @param tag
	 * @return
	 */
	public static List<ChinaBank> getWithdrawBank(){
		if(withdrawBanks == null){
			withdrawBanks = new ArrayList<ChinaBank>();
		}else{
			return withdrawBanks;
		}
	    Iterator<ChinaBank> it=EnumSet.allOf(ChinaBank.class).iterator();
		while(it.hasNext()){
			ChinaBank cb=it.next();
			if (cb == ChinaBank.gsyh || cb == ChinaBank.nyyh || cb == ChinaBank.zhongGuo
					|| cb == ChinaBank.jsyh || cb == ChinaBank.jiaoTong || cb == ChinaBank.zhongXin
					|| cb == ChinaBank.guangDa || cb == ChinaBank.huaXia || cb == ChinaBank.minSheng
					|| cb == ChinaBank.xingYe || cb == ChinaBank.pufa || cb == ChinaBank.zsyh
					|| cb == ChinaBank.guangFa || cb == ChinaBank.yzyh || cb == ChinaBank.pingAn) {
				withdrawBanks.add(cb);
			}
			
		}
		return withdrawBanks;
	}
	
}
