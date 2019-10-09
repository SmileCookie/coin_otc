package com.world.model.dao.level;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;

import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.google.code.morphia.query.UpdateResults;
import com.world.data.mongo.MongoDao;
import com.world.model.entity.level.UserVipLevel;
import com.world.model.entity.level.VipRate;

@SuppressWarnings("serial")
public class UserVipLevelDao extends MongoDao<UserVipLevel, String>{
	Logger logger = Logger.getLogger(UserVipLevelDao.class);
	

	public static List<UserVipLevel> stUserVipLevelList = new ArrayList<UserVipLevel>();
	
	/**
	 * 根据id查询一条用户积分等级配置
	 * @param id 标识号
	 * @return 查询的唯一一条结果
	 */
	public UserVipLevel getById(String id){
		Query<UserVipLevel> q = getQuery(UserVipLevel.class).filter("_id =", id); 
		return super.findOne(q); 
	}
	/**
	 * 根据vipRate查询一条用户积分等级配置
	 * @param vipRate 标识号
	 * @return 查询的唯一一条结果
	 */
	public UserVipLevel getByVipRate(int vipRate){
		Query<UserVipLevel> q = getQuery(UserVipLevel.class).filter("vipRate =", vipRate).limit(1);
		return super.findOne(q);
	}
	/**
	 * 添加一条用户积分等级配置
	 */
	public String addUserVipLevel(UserVipLevel userVipLevel){
		String nId=super.save(userVipLevel).getId().toString();
		reInitList();//有修改重新初始化
		return nId;
	}
	
	/**
	 * 更新用户积分等级配置
	 * @param userVipLevel
	 * @return
	 */
	public boolean updateUserVipLevel(UserVipLevel userVipLevel){
		Query<UserVipLevel> q = getQuery(UserVipLevel.class).filter("_id =", userVipLevel.getId()); 
		UpdateOperations<UserVipLevel> ops =super.createUpdateOperations();
		ops.set("vipRate", userVipLevel.getVipRate());
		ops.set("jifen", userVipLevel.getJifen());
		ops.set("discount", userVipLevel.getDiscount());
		ops.set("memo", userVipLevel.getMemo());
		
		UpdateResults<UserVipLevel> ur =super.update(q, ops);
		reInitList();//有修改重新初始化
		return !ur.getHadError();
	}
	
	/**
	 * 查询用户积分等级配置列表
	 */
	public List<UserVipLevel> search(Query<UserVipLevel> q,int pageIndex,int pageSize){
		q.offset((pageIndex-1)*pageSize).limit(pageSize);
		return super.find(q).asList(); 
	}
	
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
	 * 根据积分获取对应的等级
	 * @param totalJifen 用户当前总积分
	 * @return VipRate 对应等级对象
	 */
	public VipRate getVipRateByJiFen(BigDecimal totalJifen){
		VipRate newVip = VipRate.vip0;
		if(stUserVipLevelList==null || stUserVipLevelList.size()<=0)
		{
			stUserVipLevelList = getList();
		}
		if(stUserVipLevelList!=null && stUserVipLevelList.size()>0){
			for(UserVipLevel vipLevel:stUserVipLevelList){
				if(totalJifen.doubleValue()>=vipLevel.getJifen()){
					if(vipLevel.getVipRate()>newVip.getKey()){
						newVip = VipRate.getEnumByKey(vipLevel.getVipRate());
					}
				}
			}
		}
		return newVip;
	}

	/**
	 * 根据积分获取对应的等级
	 * @param totalJifen 用户当前总积分
	 * @return VipRate 对应等级对象
	 */
	public int getVipRateByJiFenUpd(BigDecimal totalJifen){
		int newVip = 0;
		stUserVipLevelList = getList();
		if(stUserVipLevelList!=null && stUserVipLevelList.size()>0){
			for(UserVipLevel vipLevel:stUserVipLevelList){
				if(totalJifen.doubleValue()>=vipLevel.getJifen()){
					if(vipLevel.getVipRate()>newVip){
						newVip = vipLevel.getVipRate();
					}
				}
			}
		}
		return newVip;
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
