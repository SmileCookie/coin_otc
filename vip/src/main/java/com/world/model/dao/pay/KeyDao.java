package com.world.model.dao.pay;

import java.util.List;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.CointTable;
import com.world.model.entity.pay.KeyBean;

@SuppressWarnings("serial")
public class KeyDao extends DataDaoSupport<KeyBean>{

	public String getTableName() {
		return coint.getStag()+CointTable.key;
	}
	/******
	 * 用户在本站的BTC充值地址
	 * @param userId
	 * @param userName
	 * @return  
	 */
	public KeyBean getRechargeKey(int userId , String userName){
		KeyBean lastBkb = getLastRechargeKey(userId);
		int lastKeyId = lastBkb == null?0:lastBkb.getKeyId();
		
		KeyBean bkb=(KeyBean)Data.GetOne("select * from  "+getTableName()+" where userId=? and tag = 0 AND keyId > ? ORDER BY keyId DESC", new Object[]{userId, lastKeyId},KeyBean.class);
		if(bkb==null){
			 bkb=(KeyBean)Data.GetOne("select * from  "+getTableName()+" where userId=0 and usedTimes < ? and tag = 0 order by keyId", new Object[]{1},KeyBean.class);
			if(bkb!=null){
				int count=Data.Update("update "+getTableName()+" set userId=?,username=? where keyId=? and userId=0", new Object[]{userId , userName , bkb.getKeyId()});
				if(count>0){
					return bkb;
				}
			}
		}else{
			return bkb;
		}
		return null;
	}

	public KeyBean getERCRechargeKey(int userId , String userName){
		KeyBean lastBkb = getLastRechargeKey(userId);
		int lastKeyId = lastBkb == null?0:lastBkb.getKeyId();

		KeyBean bkb=(KeyBean)Data.GetOne("select * from  ethkey where userId=? and tag = 0 AND keyId > ? ORDER BY keyId DESC", new Object[]{userId, lastKeyId},KeyBean.class);
		if(bkb==null){
			bkb=(KeyBean)Data.GetOne("select * from  ethkey where userId=0 and usedTimes < ? and tag = 0 order by keyId", new Object[]{1},KeyBean.class);
			if(bkb!=null){
				int count=Data.Update("update ethkey set userId=?,username=? where keyId=? and userId=0", new Object[]{userId , userName , bkb.getKeyId()});
				if(count>0){
					return bkb;
				}
			}
		}else{
			return bkb;
		}
		return null;
	}


	/******
	 * 用户在本站的BTC充值地址
	 * @param userId
	 * @param userName
	 * @return
	 */
	public KeyBean getRechargeEthKey(int userId , String userName){
		KeyBean lastBkb = getLastRechargeKey(userId);
		int lastKeyId = lastBkb == null?0:lastBkb.getKeyId();

		KeyBean bkb=(KeyBean)Data.GetOne("select * from  ethkey where userId=? and tag = 0 AND keyId > ? ORDER BY keyId DESC", new Object[]{userId, lastKeyId},KeyBean.class);
		if(bkb==null){
			bkb=(KeyBean)Data.GetOne("select * from  ethkey where userId=0 and usedTimes < ? and tag = 0 order by keyId", new Object[]{1},KeyBean.class);
			if(bkb!=null){
				int count=Data.Update("update ethkey set userId=?,username=? where keyId=? and userId=0", new Object[]{userId , userName , bkb.getKeyId()});
				if(count>0){
					return bkb;
				}
			}
		}else{
			return bkb;
		}
		return null;
	}
	
	/***
	 * 
	 * 获取所有充值地址  非理财地址
	 * @param userId
	 * @return
	 */
	public List<KeyBean> getRechargeKeys(int userId, String userName){
		List<KeyBean> bkbs = Data.QueryT("select * from  "+getTableName()+" where userId=? and tag = 0 order by keyId desc", new Object[]{userId}, KeyBean.class);
		
		return bkbs;
	}

	/***
	 *
	 * 获取用户在本站的ETH充值地址
	 * @param userId
	 * @return
	 */
	public List<KeyBean> getEthKeys(int userId, String userName){
		List<KeyBean> bkbs = Data.QueryT("select * from  ethkey  where userId=? and tag = 0 order by keyId desc", new Object[]{userId}, KeyBean.class);
		return bkbs;
	}

	/***
	 *
	 * 分配用户在本站的ETH充值地址
	 * @param userId
	 * @return
	 */
	public KeyBean updateEthKeys(int userId, String userName){
		KeyBean newBkb=(KeyBean)Data.GetOne("select * from  ethkey where userId=0 and usedTimes < ? and tag = 0 order by keyId", new Object[]{1}, KeyBean.class);
		if(newBkb != null){
			int count=Data.Update("update ethkey set userId=?,username=? where keyId=? and userId=0",
					new Object[]{userId , userName, newBkb.getKeyId()});
			if(count <= 0){
				newBkb = null;
			}
		}
		return newBkb;
	}


	public KeyBean getNewKey(int userId, String userName){
		KeyBean newBkb=(KeyBean)Data.GetOne("select * from  "+getTableName()+" where userId=0 and usedTimes < ? and tag = 0 order by keyId", new Object[]{1}, KeyBean.class);

		if(newBkb != null){
			int count=Data.Update("update "+getTableName()+" set userId=?,username=? where keyId=? and userId=0", 
					new Object[]{userId , userName, newBkb.getKeyId()});
			if(count <= 0){
				newBkb = null;
			}
		}
		return newBkb;
	}
	
	/**
	 * 获取BTC最后一个标记过的地址
	 * @param userId
	 * @return
	 */
	public KeyBean getLastRechargeKey(int userId){
		
		KeyBean bkb=(KeyBean)Data.GetOne("select * from  "+getTableName()+" where userId=? and tag = 1 order by keyId desc", new Object[]{userId},KeyBean.class);
		return bkb;
	}
	
	public OneSql saveAddress(String address, String walletName, String orderNo){
		return new OneSql("insert into "+getTableName()+" (keyPre, wallet, createTime, merchantOrderNo) values (?, ?, ?, ?)", 1, new Object[]{
				address, walletName, now(), orderNo
		});
	}
	
	public long getNoUseCount() {
		long count = 0;
		String sql = "select count(*) usedTimes from "+getTableName()+" where userId<=0 AND tag = 0";
		List li = (List) Data.GetOne(sql, null);
		if (null != li && li.get(0) != null) {
			count = Long.parseLong(li.get(0).toString());
		}
		return count;
	}
}
