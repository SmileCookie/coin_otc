package com.world.model.loan.dao;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.loan.entity.DeductCoupon;

/**
 * 抵扣券 DAO接口
 *
 */
@SuppressWarnings("rawtypes")
public class DeductCouponDao extends DataDaoSupport {

	private static final long serialVersionUID = 1L;

	/**
	 * 根据userId查询并排序
	 *
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Bean> findUserId(String userId) {
		DeductCouponDao dao = new DeductCouponDao();
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT * FROM deductcoupon WHERE userId=? ORDER BY id").setParams(new Object[] { userId }).setCls(DeductCoupon.class);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return dao.find();
	}
	/**
	 * @param （根据userId&使用状态）查询并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Bean> findUserId(String userId, int useState) {
		DeductCouponDao dao = new DeductCouponDao();
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT * FROM deductcoupon WHERE userId=? AND useState=? AND now()<endTime ORDER BY id").setParams(new Object[] { userId, useState }).setCls(DeductCoupon.class);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return dao.find();
	}
	/**
	 * 根据类型处理完再循环显示出来
	 * @param userId、useState、fundsType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Bean> getfindUserId(String userId, int useState, int fundsType) {
		DeductCouponDao dao = new DeductCouponDao();
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT * FROM deductcoupon WHERE userId=? AND useState=? AND fundsType=? ").setParams(new Object[] { userId, useState, fundsType }).setCls(DeductCoupon.class);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return dao.find();
	}
	/**
	 * 根据类型处理完再循环显示出来
	 * @param userId、useState
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Bean> getfindUserId(String userId, int useState) {
		DeductCouponDao dao = new DeductCouponDao();
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT * FROM deductcoupon WHERE userId=? AND useState=? AND NOW()<endTime").setParams(new Object[] { userId, useState }).setCls(DeductCoupon.class);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return dao.find();
	}
	
	
	/**
	 * 根据抵扣券秘钥查找
	 * @param secretkey
	 * @return
	 */
	public DeductCoupon findBySecret(String secretkey) {
		return (DeductCoupon) Data.GetOne("SELECT * FROM deductcoupon WHERE secretkey=?", new Object[] { secretkey }, DeductCoupon.class);
	}
	/**
	 * 根据抵扣id查询一条数据
	 * @param id
	 * @return
	 */
	public DeductCoupon findIdKey(int id) {
		return (DeductCoupon) Data.GetOne("SELECT * FROM deductcoupon WHERE id=?", new Object[] { id }, DeductCoupon.class);
	}
	//还款页面的抵扣查询
	public DeductCoupon findIdUseKey(int id) {
		return (DeductCoupon) Data.GetOne("SELECT * FROM deductcoupon WHERE id=? AND useState=2 or useState=5", new Object[] { id }, DeductCoupon.class);
	}
	/**
	 * 
	 * @param userId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DeductCoupon getByUserId(String userId) {
		return (DeductCoupon) super.get("SELECT * FROM deductcoupon WHERE userId=?", new Object[] { userId }, DeductCoupon.class);
	}
	/**
	 * 获取单条 
	 * 用抵扣券id精确查找出抵扣券
	 * 为以下状态不能使用： 0未激活、2已使用、3已过期、4已禁用、不属于本用户的抵扣券
	 * @param userId、useState、fundsType
	 * @return
	 */
	public DeductCoupon getbox(int id, String userId, int useState) {
		return (DeductCoupon) Data.GetOne( "SELECT * FROM deductcoupon WHERE userId=? AND useState=? AND fundsType=?", new Object[] { userId, id, useState }, DeductCoupon.class);
	}
	/**
	 * 通过抵扣券
	 * 
	 * @param id 通过抵扣券id
	 * @param fundType 币种类型 获取信息
	 * @return
	 */
	public DeductCoupon getDiKouId(int id, int fundType) {
		return (DeductCoupon) Data.GetOne("SELECT * from deductcoupon where id=?", new Object[] { id, fundType }, DeductCoupon.class);
	}
	/**
	 * 不用传参查询标识
	 *
	 */
	public DeductCoupon batchName() {
		return (DeductCoupon) Data.GetOne("SELECT * FROM deductcoupon ORDER BY startTime DESC", new Object[]{}, DeductCoupon.class);
	}
	
	
	/**
	 * 禁用
	 * @param secretkey、useState
	 * @return
	 */
	public OneSql getUpdateStatusSQL(String secretkey, int useState) {
		if (useState == 1) {
			DeductCoupon obj = (DeductCoupon) Data.GetOne("SELECT * FROM deductcoupon WHERE secretkey=?", new Object[] { secretkey }, DeductCoupon.class);
			
			if (StringUtils.isEmpty(obj.getUserId())) {
				return new OneSql("UPDATE deductcoupon SET useState=0 WHERE id=?", 1, new Object[] { obj.getId() });
			} else {
				return new OneSql("UPDATE deductcoupon SET useState=1 WHERE id=?", 1, new Object[] { obj.getId() });
			}
		} else {
			return new OneSql("UPDATE deductcoupon SET useState=? WHERE secretkey=?", 1, new Object[] { useState, secretkey });
		}
	}
	/**
	 * 根据id修改状态
	 * @param id&useState
	 * @return
	 */
	public int updateUseStatus(int useState, int id) {
		return Data.Update("UPDATE deductcoupon SET useState=? WHERE id=?", new Object[] { useState, id });
	}
	/**
	 * 根据id & userId 修改 状态
	 * @param id
	 * @param userId
	 * @param useState
	 * @return
	 */
	public int updateUseStatus2(int id, String userId, int useState) {
		return Data.Update("UPDATE deductcoupon SET useState=? WHERE id=? AND userId=?", new Object[] { useState, id, userId });
	}

	public OneSql updateUseStatus3(int id, String userId, int useState) {
		return new OneSql("UPDATE deductcoupon SET useState=? WHERE id=? AND userId=?", 1, new Object[] { useState, id, userId });
	}

	public OneSql upStatusTime( int useState,Timestamp useTime, int id, String userId) {
		return new OneSql("UPDATE deductcoupon SET useState=?,useTime=? WHERE id=? AND userId=?", 1, new Object[] { useState, useTime, id, userId });
	}
	/**
	 * 更改状态
	 * @param useState
	 * @param useTime
	 * @param id
	 * @param UserId
	 * @return
	 */
	public OneSql updateUseStatus4(int useState, Timestamp useTime, int id, String userId) {
		return new OneSql("UPDATE deductcoupon SET useState=?, useTime=? WHERE id=? AND userId=?", 1, new Object[] { useState, useTime, id, userId });
	}
	
	//快速增加用户
	public int updateUserName4(String userId, String userName, int useState, Timestamp actTime, int id) {
		return Data.Update("UPDATE deductcoupon SET userId=?, userName=?, useState=?, actTime=? WHERE id=?", new Object[] { userId, userName, useState, actTime, id });
	}
	
	/**
	 * 根据抵扣券秘钥修改用户id&用户名
	 * @param userId、userName、useTime、actTime、userState、secretkey
	 * @return
	 */
	public int updateSecretkey(String userId, String userName, Timestamp actTime, int userState, String secretkey) {
		return Data.Update("UPDATE deductcoupon SET userId=?, userName=?, actTime=?, useState=? WHERE secretkey=?", new Object[] { userId, userName, actTime, userState, secretkey });
	}
	/**
	 * 用户自行激活抵扣券
	 * @param dCoupon
	 * @return
	 */
	public int update(DeductCoupon dCoupon) {
		return Data.Update("UPDATE deductcoupon SET userId=?, userName=?, useTime=?, useState=? where id=?", new Object[] { dCoupon.getUserId(), dCoupon.getUserName(), dCoupon.getUseTime(), dCoupon.getUseState(), dCoupon.getId() });
	}
	
	/**
	 * 删除抵扣券秘钥
	 * @param secretkey
	 * @return
	 */
	public OneSql getDeleteSQL(String secretkey) {
		return new OneSql("DELETE FROM deductcoupon where secretkey=?", 1, new Object[] { secretkey });
	}
	
	/**
	 * 单条添加,多条添加
	 * @param dCoupon
	 * @return
	 */
	public OneSql getInsert(DeductCoupon dCoupon) {
		return new OneSql(
				"INSERT INTO deductcoupon(userId,userName,startTime,title,getWay,couponType,secretkey,fundsType,amountDeg,useCondition,useState,actTime,useTime,endTime,batchMark) VALUE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				1,
				new Object[] { 
						dCoupon.getUserId(), 
						dCoupon.getUserName(), 
						dCoupon.getStartTime(), 
						dCoupon.getTitle(),
						dCoupon.getGetWay(), 
						dCoupon.getCouponType(), 
						dCoupon.getSecretkey(), 
						dCoupon.getFundsType(),
						dCoupon.getAmountDeg(), 
						dCoupon.getUseCondition(), 
						dCoupon.getUseState(),
						dCoupon.getActTime(),
						dCoupon.getUseTime(),
						dCoupon.getEndTime(), 
						dCoupon.getBatchMark() 
						});
	}
	
}
