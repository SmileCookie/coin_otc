package com.world.model.financial.dao;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.world.data.mysql.Bean;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.dao.admin.user.AdminUserDao;
import com.world.model.entity.admin.AdminUser;
import com.world.model.financial.entity.FinanUseType;

public class FinanUseTypeDao extends DataDaoSupport<FinanUseType>{
	
	public FinanUseType get(int id){
		return (FinanUseType)super.get("SELECT * FROM finanusetype WHERE id = ? AND isDel = ?", new Object[]{id, false}, FinanUseType.class);
	}

	public int delById(int id){
		return super.delete("UPDATE finanusetype SET isDel = ? WHERE id = ?", new Object[]{true, id});
	}
	
	public List<FinanUseType> findList(){
		return (List<FinanUseType>)super.find("SELECT * FROM finanusetype WHERE isDel = ? AND fundType = 0 ORDER BY createTime", new Object[]{false}, FinanUseType.class);
	}
	
	public int save(FinanUseType useType){
		
		int count = super.save("INSERT INTO finanusetype (name, memo, isIn, turnRound, createId, createTime, fundType, type) VALUES (?,?,?,?,?,?,?,?)", new Object[]{
				useType.getName(), useType.getMemo(), useType.getIsIn(), useType.getTurnRound(), useType.getCreateId(), useType.getCreateTime(), useType.getFundType(), useType.getType()
		});
		return count;
	}
	
	public int update(FinanUseType useType){
		
		int count = super.update("UPDATE finanusetype SET name=?, memo=?, isIn=?, turnRound=?, updateId=?, updateTime=?, fundType=?, type=? WHERE id=?", new Object[]{
				useType.getName(), useType.getMemo(), useType.getIsIn(), useType.getTurnRound(), useType.getUpdateId(), useType.getUpdateTime(), useType.getFundType(), useType.getType()
				,useType.getId()
		});
		return count;
	}
	
	public FinanUseType getByType(int type){
		return (FinanUseType)super.get("SELECT * FROM finanusetype WHERE type = ? AND isDel = ? ", new Object[]{type, false}, FinanUseType.class);
	}
	
	public List<FinanUseType> findList(int fundType){
		return (List<FinanUseType>)super.find("SELECT * FROM finanusetype WHERE isDel = ? AND (fundType = ? OR fundType = 0) ORDER BY createTime", new Object[]{false, fundType}, FinanUseType.class);
	}
	
	public Map<Integer , FinanUseType> getUseTypeMapByIds(String ids){
		Map<Integer , FinanUseType> maps = new LinkedHashMap<Integer, FinanUseType>();
		List<FinanUseType> beans = (List<FinanUseType>)super.find("SELECT * FROM finanusetype WHERE id IN ("+ids+")", new Object[]{}, FinanUseType.class);
		if(beans != null && beans.size() > 0){
			for(FinanUseType a : beans){
				maps.put(a.getId(), a);
			}
		}
		return maps;
	}
	
	public void setaUser(List<FinanUseType> dataList){
		List<String> adminIds = new ArrayList<String>();
		for(FinanUseType fa : dataList){
			if(fa.getCreateId() > 0)
				adminIds.add(fa.getCreateId()+"");
		}
		
		if(adminIds.size() > 0){
			Map<String , AdminUser> users = new AdminUserDao().getUserMapByIds(adminIds);
			for(FinanUseType fa : dataList){
				fa.setaUser(users.get(fa.getCreateId()+""));
			}
		}
	}
}
