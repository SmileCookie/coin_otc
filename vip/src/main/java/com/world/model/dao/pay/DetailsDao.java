package com.world.model.dao.pay;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Bean;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.entity.CointTable;
import com.world.model.entity.pay.DetailsBean;

@SuppressWarnings("serial")
public class DetailsDao extends DataDaoSupport<DetailsBean>{

	public DetailsDao(){
	}

	public DetailsDao(String lan){
		this.lan = lan;
	}
	public String getTableName(){
		return coint.getStag()+CointTable.details;
	}

	/**
	 * 根据交易流水号查询是否有记录
	 * @return
     */
	public int count(String txId){
		String sql = "select count(1) from "+getTableName()+" where addHash = ?";
		return super.count(sql, new Object[]{txId});
	}

	/**
	 * 根据流水查询一条记录
	 * @param txId
	 * @return
     */
	public DetailsBean queryOneByTxid(String txId){
		List<DetailsBean> list = super.find("select * from "+getTableName()+" where addHash = ? ", new Object[]{txId}, DetailsBean.class);

		if(CollectionUtils.isNotEmpty(list)){
			return list.get(0);

		}else{
			return null;
		}
	}

	/**
	 * 根据流水查询一条记录
	 * @param address
	 * @return
	 */
	public DetailsBean queryOneByAddress(String address,int fundsType,String userId){
		List<DetailsBean> list = super.find("select * from detailssummary where toAddr = ? and userId = ? and fundsType = ?", new Object[]{address,userId,fundsType}, DetailsBean.class);
		if(CollectionUtils.isNotEmpty(list)){
			return list.get(0);
		}else{
			return null;
		}
	}








	//返回已存在的交易的map列表
	public Map<String, Long> getChargeMapByTime(String walletName, Timestamp sendTime){
		List<DetailsBean> list = super.find("select * from "+getTableName()+" where wallet = ? AND sendTime >= ?", new Object[]{walletName, sendTime}, DetailsBean.class);
		Map<String, Long> map = new HashMap<String, Long>();
		for(DetailsBean charge : list){
			map.put(charge.getAddHash(), charge.getDetailsId());
		}
		
		return map;
	}
	
	/**
	 * 保存一条记录
	 *
	 * @param charge
	 * @return
	 */
	public OneSql saveOne(DetailsBean charge){
		return new OneSql("insert into "+getTableName()+" (fromAddr, toAddr, addHash, amount, sendTime, confirmTimes, addressTag, wallet, status, type,configTime,userId,userName, blockHeight) values (?,?,?,?,?,?,?,?,?, ?, ?,?,?,?)",
				1, new Object[]{
				charge.getFromAddr(), charge.getToAddr(), charge.getAddHash(), charge.getAmount(), charge.getSendTime(),charge.getConfirmTimes(),charge.getAddressTag(), charge.getWallet(),charge.getStatus(),1, charge.getConfigTime(),charge.getUserId(),charge.getUserName(), charge.getBlockHeight()}, database);
	}
	
	/**
	 * 更新一条记录，更新确认次数和确认状态
	 * @return
	 */
	public OneSql updateOne(DetailsBean charge){
		return new OneSql("update "+getTableName()+" set confirmTimes = ?, configTime = ?, status = ? where addHash = ?", 1, new Object[]{charge.getConfirmTimes(), charge.getConfigTime(), charge.getStatus(),  charge.getAddHash()});
	}
	
	/**
	 * 查询获得状态为200,钱包名称为给定参数的交易记录
	 *
	 * @param walletName
	 * @return
	 */
	public List<DetailsBean> getChargeList(String walletName, int pageSize){
		List<DetailsBean> list = super.find("select * from "+getTableName()+" where wallet = ? AND status = 200 LIMIT 0, ?", new Object[]{walletName, pageSize}, DetailsBean.class);
		return list;
	}
	
	/**
	 * 
	 * @param userId
	 * @param pageIndex
	 * @param pageSize
	 * @param status -1 全部 0 等待确认 1失败 2 成功
	 */
	public  JSONObject getChargeRecord(String userId, int pageIndex, int pageSize, int status) {
		JSONObject json = new JSONObject();
		JSONArray jarry = new JSONArray();
		try {
			
			Query query = getQuery();
			query.setSql("select * from "+getTableName()+" where 1=1 ");
			query.setCls(DetailsBean.class);

			query.append(" and userId='" + userId + "'  ");
			if (status == 0 || status == 1 || status == 2) {
				query.append(" and status=" + status);
			}

			int total = query.count();
			if (total > 0) {
				query.append(" order by detailsId desc");
				// 分页查询
				List<DetailsBean> downloads = findPage(pageIndex, pageSize);
				
				for (Bean b : downloads) {
					DetailsBean dBean = (DetailsBean) b;
					JSONObject jObj = new JSONObject();
					jObj.put("hash", dBean.getAddHash());
					jObj.put("type", dBean.getInType());
					jObj.put("amount", dBean.getAmount());
					jObj.put("status", dBean.getShowStatu());
					jObj.put("submit_time", DateFormatUtils.format(dBean.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
					
					jarry.add(jObj);
				}

			}
			json.put("list", jarry);
			json.put("total", total);
			json.put("pageIndex", pageIndex);
			json.put("pageSize", pageSize);

		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return json;
	}
	
}
