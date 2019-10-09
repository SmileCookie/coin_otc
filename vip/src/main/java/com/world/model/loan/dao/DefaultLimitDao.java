package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.data.mysql.Query;
import com.world.model.loan.entity.DefaultLimit;
import com.world.util.DigitalUtil;
import com.world.util.date.TimeUtil;

/***
 * @version
 */
@SuppressWarnings("rawtypes")
public class DefaultLimitDao extends DataDaoSupport {

	private static final long serialVersionUID = -9067717050107401853L;

	/**
	 * @param 根据类型keyName查询,去除相同key值并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Bean> findkeyName() {
		DefaultLimitDao dao = new DefaultLimitDao();
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT keyName from defaultlimit GROUP BY keyName ORDER BY id").setCls(DefaultLimit.class);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return dao.find();
	}

	/**
	 * @param 根据类型keyName查询,去除相同key值并排序
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Bean> findTypeName() {
		DefaultLimitDao dao = new DefaultLimitDao();
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT typeName from defaultlimit GROUP BY typeName ORDER BY id").setCls(DefaultLimit.class);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return dao.find();
	}
	
	/**
	 * 查询条件全部独立开来,条件可以一个&多个&为空
	 * @param ids
	 * @param typeName
	 * @param keyName
	 * @param valueName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DefaultLimit> findAll(int ids, String typeName, String keyName, String valueName) {
		DefaultLimitDao dao = new DefaultLimitDao();
		Query query = dao.getQuery();
		try {
			// Query query = dao.getQuery();
			query.setSql("SELECT * from defaultlimit").setCls(DefaultLimit.class);
			if (ids > 0) {
				query.append("AND id=" + ids);
			}
			if (typeName.length() > 0) {
				query.append(" AND typeName like '%" + typeName + "%'");
//				query.append(" AND typeName='" + typeName + "'");
			}
			if (keyName.length() > 0) {
				query.append(" AND keyName='" + keyName + "'");
			}
			if (valueName.length() > 0) {
				query.append("AND valueName=" + valueName);
			}
		} catch (Exception e) {
			log.error("调用默认值接口失败 ---" + TimeUtil.getNow(), e);
		}
		return query.getList();
	}
	
//	@SuppressWarnings("unchecked")
//	public List<Bean> findAll8(int ids, String typeName, String keyName, String valueName, int pageNo, int pageSize) {
//		DefaultLimitDao dao = new DefaultLimitDao();
//		Query query = dao.getQuery();
//		try {
//			query.setSql("SELECT * from defaultlimit").setCls(DefaultLimit.class);
//			
//			if (ids > 0) {
//				query.append("AND id=" + ids);
//			}
//			if (typeName.length() > 0) {
//				query.append(" AND typeName like '%" + typeName + "%'");
////				query.append(" AND typeName='" + typeName + "'");
//			}
//			if (keyName.length() > 0) {
//				query.append(" AND keyName='" + keyName + "'");
//			}
//			if (valueName.length() > 0) {
//				query.append("AND valueName=" + valueName);
//			}
//			long total = query.count();
//			if (total > 0) {
//				query.append("ORDER BY id DESC");
//				
//				List<Bean> dataList = dao.findPage(pageNo, pageSize);
//				return dataList;
//				 
//			}
//			return total;
//		} catch (Exception e) {
//			log.info("调用默认值接口失败 ---" + TimeUtil.getNow());
//			log.error(e.toString(), e);
//		}
//		return query.getList();
//	}
	/**
	 * @param ids
	 * @param typeName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DefaultLimit> findAll2() {
		DefaultLimitDao dao = new DefaultLimitDao();
		try {
			Query query = dao.getQuery();
			query.setSql("SELECT * from defaultlimit").setCls(DefaultLimit.class);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return dao.find();
	}

	/**
	 * 查询出单条记录
	 * @param ids、keyName
	 * @return
	 */
	public DefaultLimit findOneKey(int ids, String typeName, String keyName) {
		return (DefaultLimit) Data.GetOne("SELECT * from defaultlimit WHERE id=? AND typeName=? AND keyName=?", new Object[] { ids, typeName, keyName }, DefaultLimit.class);
	}

	public DefaultLimit findOKT(String keyName, String typeName) {
		return (DefaultLimit) Data.GetOne("SELECT * from defaultlimit WHERE keyName=? AND typeName=?", new Object[] { keyName, typeName }, DefaultLimit.class);
	}

	/**
	 * 通过id和值的名称修改 默认值
	 * @param ids
	 * @param typeName
	 * @param valueName
	 * @return
	 */
	public OneSql getUpdateValue(String valueName, int ids, String typeName, String keyName) {
		return new OneSql("UPDATE defaultlimit SET valueName=? WHERE id=? AND typeName=? AND keyName=?", 1, new Object[] { valueName, ids, typeName, keyName });
	}
	//不需要修改备注的用
	public OneSql getUpdateValue(String valueName, int ids, String typeName, String keyName, String reMarks) {
		return new OneSql("UPDATE defaultlimit SET valueName=?, reMarks=? WHERE id=? AND typeName=? AND keyName=?", 1, new Object[] { valueName, reMarks, ids, typeName, keyName });
	}

	// public int getUpdateValue(int ids, String typeName, String valueName) {
	// return Data.Update("UPDATE defaultlimit SET valueName=? WHERE id=? AND
	// typeName=?", new Object[] { valueName, ids, typeName });
	// }
	/**
	 * 添加默认值
	 * @param DefaultLimit
	 * @return
	 */
	public OneSql findInsert(String typeName, String keyName, String valueName) {
		return new OneSql("INSERT into defaultlimit (typeName, keyName, valueName) VALUE(?,?,?)", 1, new Object[] { typeName, keyName, valueName });
	}
	/**
	 * 慎用---删除默认值
	 * @param ids
	 * @param typeName
	 * @return
	 */
	public OneSql findDelete(int ids, String typeName, String keyName) {
		return new OneSql("DELETE FROM defaultlimit WHERE id=? AND typeName=? AND keyName=?", 1, new Object[] { ids, typeName, keyName });
	}

	public BigDecimal getLimitBigDecimal(String keyName, String typeName){
		DefaultLimit defaultLimit = findOKT(keyName, typeName);
		String sqlLimiyKey = "";
		if(defaultLimit!=null){
			sqlLimiyKey = defaultLimit.getValueName();
		}
		if (StringUtils.isBlank(sqlLimiyKey)) {
			log.info("借贷-默认范围值异常---" + typeName + ":--- " + sqlLimiyKey);
			sqlLimiyKey = "0";
		}
		return DigitalUtil.getBigDecimal(sqlLimiyKey);
	}
	
	/**
	 * 根据类型名称查找对应数据集合
	 * @param typeName 
	 * @return
	 */
	public List<DefaultLimit> findByTypeName(String typeName) {
		DefaultLimitDao dao = new DefaultLimitDao();
		Query query = dao.getQuery();
		try {
			// Query query = dao.getQuery();
			query.setSql("SELECT * from defaultlimit").setCls(DefaultLimit.class);
			
			query.append(" AND typeName='" + typeName + "'");
		
		} catch (Exception e) {
			log.error("调用默认值接口失败 ---" + TimeUtil.getNow(), e);
		}
		return query.getList();
	}
}
