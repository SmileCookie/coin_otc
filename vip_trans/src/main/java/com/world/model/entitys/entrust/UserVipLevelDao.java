package com.world.model.entitys.entrust;

import com.google.code.morphia.query.Query;
import com.world.data.mongo.MongoDao;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UserVipLevelDao extends MongoDao<UserVipLevel, String>{
	Logger logger = Logger.getLogger(UserVipLevelDao.class);
	

	public static List<UserVipLevel> stUserVipLevelList = new ArrayList<UserVipLevel>();
	

	public List<UserVipLevel> getList(){
		Query<UserVipLevel> q = getQuery(UserVipLevel.class);
		q.order("vipRate");
		List<UserVipLevel> dataList = super.find(q).asList();

		return dataList;
	}

	/**
	 * 重新初始化集合
	 */
	public  void reInitList(){
		stUserVipLevelList = getList();
	}



	/**
	 * 计算用户等级对应的折扣费率
	 * @param vipRate 用户等级
	 * @return 返回折扣费率 90% 返回0.90 100%返回1.00
	 * @author zhanglinbo 
	 */
	public BigDecimal getDiscountByVipRate(int vipRate){
		BigDecimal discount = BigDecimal.ONE;
		if(stUserVipLevelList==null || stUserVipLevelList.size()<=0)
		{
			stUserVipLevelList = getList();
		}
		
		if(stUserVipLevelList!=null && stUserVipLevelList.size()>0){
			for(UserVipLevel vipLevel:stUserVipLevelList){
				if(vipRate == vipLevel.getVipRate()){
					//折扣转成 百分之几，保留小数点后2位小数
					discount = BigDecimal.valueOf(vipLevel.getDiscount()).divide(BigDecimal.valueOf(100),2,BigDecimal.ROUND_CEILING);
				}
			}
		}
		return discount;
	}
	 
	 
}
