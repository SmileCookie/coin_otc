package com.world.model.entity.usercap.dao;

import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.usercap.entity.CommAttrBean;
import com.world.model.entity.usercap.entity.SpecialAddress;
import org.apache.commons.collections.CollectionUtils;
import org.beetl.sql.core.annotatoin.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommAttrDao extends DataDaoSupport<CommAttrBean> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 根据条件查询开启的配置
	 *
	 * @param attrType
	 * @param paraCode
     * @return
     */
	public CommAttrBean queryByAttrTypeAndParaCode(int attrType, String paraCode) {
		String sql = "select * from comm_attr where attrType = ? and paraCode = ? and attrState = 1";
		List<CommAttrBean> list = super.find(sql, new Object[]{attrType, paraCode}, CommAttrBean.class);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		return list.get(0);
	}


	/**
	 * 根据条件查询开启的配置
	 *
	 * @param attrType
	 * @param paraCode
	 * @return
	 */
	public List<CommAttrBean> queryListByAttrTypeAndParaCode(int attrType, String paraCode) {
		String sql = "select * from comm_attr where attrType = ? and paraCode = ? and attrState = 1";
		List<CommAttrBean> list = super.find(sql, new Object[]{attrType, paraCode}, CommAttrBean.class);
		return list;
	}

    /**
     * 根据条件查询开启的配置
     *
     * @param attrType
     * @return
     */
    public List<CommAttrBean> queryListByAttrType(int attrType) {
        String sql = "select * from comm_attr where attrType = ? and attrState = 1";
        List<CommAttrBean> list = super.find(sql, new Object[]{attrType}, CommAttrBean.class);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list;
    }

	/**
	 * 查询用户类型
	 * @return
     */
	public Map<String,String> queryUserTypeMap(){
		Map<String, String> userTypeMap = new HashMap<>();
		String sql = "select paraValue,paraName from comm_attr where attrType = 10000003 and attrState = 1";
		List<CommAttrBean> list = super.find(sql, new Object[]{}, CommAttrBean.class);
		for(CommAttrBean commAttrBean : list) {
			userTypeMap.put(commAttrBean.getParaValue(), commAttrBean.getParaName());
		}
		return userTypeMap;
	}

	/**
	 * 根据类型代码查询paraValue,paraName
	 * @return
	 */
	public Map<Integer,String> queryUserTypeMap(int attrType){
		Map<Integer, String> userTypeMap = new HashMap<>();
		String sql = "select paraCode,paraValue from comm_attr where attrType = " + attrType + " and attrState = 1";
		List<CommAttrBean> list = super.find(sql, new Object[]{}, CommAttrBean.class);
		for(CommAttrBean commAttrBean : list) {
			userTypeMap.put(Integer.valueOf(commAttrBean.getParaCode()), commAttrBean.getParaValue());
		}
		return userTypeMap;
	}

    /**
     * 根据类型代码查询paraValue,paraName
     * @return
     */
    public Map<String,String> queryAttrTypeMap(int attrType){
        Map<String, String> attrTypeMap = new HashMap<>();
        String sql = "select paraCode,paraValue from comm_attr where attrType = " + attrType + " and attrState = 1";
        List<CommAttrBean> list = super.find(sql, new Object[]{}, CommAttrBean.class);
        for(CommAttrBean commAttrBean : list) {
            attrTypeMap.put(commAttrBean.getParaCode(), commAttrBean.getParaValue());
        }
        return attrTypeMap;
    }

	/**
	 * 查询字典表配置的其他特殊地址
	 * 10000004：冷转其他地址，10000005：热提转其他地址
	 * @param jsonAddress
	 * @param code
     * @return
     */
	public boolean checkSpecialAddress(String jsonAddress, int code, int paraCode){
		Map<String, String> userTypeMap = new HashMap<>();
		String sql = "select group_concat(paraValue) from comm_attr where attrType = " + code + " and paraCode = " + paraCode + " and attrState = 1";
		List<String> addressList = (List<String>)Data.GetOne(sql, new Object[]{});
		if(null != addressList && addressList.size() > 0 && addressList.get(0) != null){
			String[] addressArr = addressList.get(0).split(",");
			for(String address : addressArr) {
				if(jsonAddress.contains(address)){
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * 根据addrType查询字典表列表
	 * @param attrType
	 * @return
     */
//	public List<SpecialAddress> querySpecialAddressByAttrType(int attrType) {
//		String sql = "select paraName coinType, paraValue address from comm_attr where attrType = ? and attrState = 1";
//		List<SpecialAddress> list = Data.Query(sql, new Object[]{attrType});
//		if (CollectionUtils.isEmpty(list)) {
//			return null;
//		}
//		return list;
//	}


	public List<SpecialAddress> querySpecialAddressByAttrType(int attrType){

		List<SpecialAddress> listAddress = new ArrayList<>();
		List<CommAttrBean> list = super.find("select * from comm_attr where attrType = ? and attrState = 1", new Object[]{attrType}, CommAttrBean.class);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}else{
			for(CommAttrBean bean : list) {
				SpecialAddress address = new SpecialAddress();
				address.setCoinType(bean.getParaName());
				address.setAddress(bean.getParaValue());
				listAddress.add(address);
			}
		}
		return listAddress;
	}

	@Override
	public int insert(CommAttrBean commAttrBean){
		String sql = "insert into `vip_main`.`comm_attr` ( `attrTypeDesc`, `paraName`, `paraDesc`, `paraCode`, `attrState`, `attrType`, `paraValue`) values ( ?, ?, ?, ?, ?, ?, ?)";
		Object[] param = new Object[] { commAttrBean.getAttrTypeDesc(), commAttrBean.getParaName(), commAttrBean.getParaDesc(), commAttrBean.getParaCode(),commAttrBean.getAttrState(),commAttrBean.getAttrType(),commAttrBean.getParaValue()};
		return Data.Insert(sql, param);
	}

	/**
	 * 修改
	 * @param commAttrBean
     */
	public void update(CommAttrBean commAttrBean){
		String sql = "update comm_attr set attrTypeDesc = ?, paraName = ?, paraDesc = ?, paraCode = ?, attrState = ?, attrType = ?, paraValue= ? where attrId = ?";
		Object[] param = new Object[] { commAttrBean.getAttrTypeDesc(), commAttrBean.getParaName(), commAttrBean.getParaDesc(), commAttrBean.getParaCode(),commAttrBean.getAttrState(),commAttrBean.getAttrType(),commAttrBean.getParaValue(),commAttrBean.getAttrId()};
		Data.Update(sql, param);
	}

	/**
	 * 删除
	 * @param attrId
     */
	public void delete(int attrId){
		if(attrId > 0){
			String sql = "delete from comm_attr where attrId=?";
			Object[] param = new Object[]{attrId};
			Data.Delete(sql, param);
		}
	}

	public CommAttrBean queryCommAttrById(int attrId){
		return Data.GetOneT("select * from comm_attr where attrId = ?", new Object[]{attrId}, CommAttrBean.class);
	}

	/**
	 * 获取数据字典
	 * @param parentId
	 * @return
	 */
	public List<CommAttrBean> getCommAttrList(String parentId){
		String sql = "SELECT paraValue FROM comm_attr WHERE parentId = ? AND attrState = 1";
		List<CommAttrBean> list = super.find(sql, new Object[]{parentId}, CommAttrBean.class);
		return list;
	}

}
