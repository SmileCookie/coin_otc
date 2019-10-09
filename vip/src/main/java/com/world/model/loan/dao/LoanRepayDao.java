/*package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.P2pUser;

public class LoanRepayDao extends DataDaoSupport{
	private static final long serialVersionUID = 1L;
	
	LoanRecordDao recordDao = new LoanRecordDao();
	
	*//****
	 * 为借款付息
	 *//*
	public void maybeRepay(int pageNo , int pageSize){
		List<Bean> lists = getMaybeRepay(pageNo , pageSize);
		String lastTimeUIds = "";
		String freezTimeUIds = "";
		List<Bean> needRepay = new ArrayList<Bean>();
		if(lists.size() > 0){
			boolean needStatistics = false;
			for(Bean b : lists){
				P2pUser user = (P2pUser)b;
				
				if(!user.needStatistics()){
					if(user.getLastTime() == null){
						lastTimeUIds += ","+user.getUserId();
					}
					continue;
				}
				needStatistics = true;
				lastTimeUIds += ","+user.getUserId();
				
				user.initFunds();
				//0 可用RMB 1冻结RMB   2 可用BTC 3 冻结BTC  4 可用LTC  5 冻结LTC 6 可用BTQ 7 冻结BTQ 8 资产折合RMB
				BigDecimal[] funds = user.getFunds();
				log.info("用户"+user.getUserId()+":"+user.getUserName()+"的可用/借入资金："+funds[0]+":"+user.getIningRmb()+"rmb:"+funds[2]+":"+user.getIningBtc()+"btc");
				boolean isAdd = true;
				if(user.getIningRmb().compareTo(BigDecimal.ZERO) > 0 && funds[0].compareTo(BigDecimal.ZERO) > 0){
					//使用资金低于借入资金的50%   100 120
					log.info("变化比例："+funds[0].subtract(user.getIningRmb()).abs().divide(funds[0], 3, RoundingMode.HALF_DOWN)+",funds[0]:"+funds[0]);
					if(funds[0].subtract(user.getIningRmb()).abs().divide(funds[0], 3, RoundingMode.HALF_DOWN).compareTo(new BigDecimal(0.2)) > 0){
						needRepay.add(user);
						freezTimeUIds += ","+user.getUserId();
						isAdd = false;
					}
				}
				if(user.getIningBtc().compareTo(BigDecimal.ZERO) > 0 && funds[2].compareTo(BigDecimal.ZERO) > 0){
					//使用资金低于借入资金的50%   100 120
					if(funds[2].subtract(user.getIningBtc()).abs().divide(funds[2], 3, RoundingMode.HALF_DOWN).compareTo(new BigDecimal(0.2)) > 0){
						if(isAdd){
							needRepay.add(user);
							freezTimeUIds += ","+user.getUserId();
						}
					}
				}
			}
			if(!needStatistics){
				log.info("没有需要统计的记录");
			}
			repay(needRepay);
			updateLastTime(lastTimeUIds);
			updateFreezTime(freezTimeUIds);
		}else{
			log.info("没有需要查询的用户");
		}
		pageNo++;
		//下一页
		if(lists.size() >= pageSize){
			maybeRepay(pageNo , pageSize);
		}
	}
	
	public void repay(List<Bean> users){
		for(Bean b : users){
			P2pUser user = (P2pUser)b;
			_repay(user);
		}
	}
	
	public void _repay(P2pUser user){
		List<Bean> lists = getLoanRecordWithoutLx(user);
		if(lists.size() > 0){
			log.info("借贷用户"+user.getUserName()+"的24小时资产变动小于50%，系统强制还款。");
			for(Bean b : lists){
				LoanRecord lr = (LoanRecord) b;
				recordDao.repay(lr);
			}
		}
	}
	
	public List<Bean> getLoanRecordWithoutLx(P2pUser user){
		List<Bean> list = Data.Query("SELECT * FROM loanrecord WHERE inUserId = ? AND status = ? AND withoutLxAmount > 0", 
				new Object[]{user.getUserId(), LoanRecordStatus.Returning.getKey()}, LoanRecord.class);
		return list;
	}
	
	*//***
	 * 查找需要还款的借款
	 * @return
	 *//*
	public List<Bean> getMaybeRepay(int pageNo , int pageSize){
		List<Bean> list = (List<Bean>)Data.Query("SELECT * FROM p2puser WHERE (iningRmb > 0 OR iningBtc > 0) LIMIT ?,?", new Object[]{(pageNo-1) * pageSize, pageSize}, P2pUser.class);
		return list;
	}
	
	public void updateLastTime(String ids){
		if(ids.length() > 0){
			if(ids.startsWith(",")){
				ids = ids.substring(1);
			}
			Data.Update("UPDATE p2puser SET lastTime = ? WHERE userId IN ("+ids+")", new Object[]{now()});
			log.info("更新lastTime");
		}
	}
	
	public void updateFreezTime(String ids){
		if(ids.length() > 0){
			if(ids.startsWith(",")){
				ids = ids.substring(1);
			}
			Data.Update("UPDATE p2puser SET freezTime = ? WHERE userId IN ("+ids+")", new Object[]{now()});
			log.info("更新FreezTime");
		}
	}
}
*/