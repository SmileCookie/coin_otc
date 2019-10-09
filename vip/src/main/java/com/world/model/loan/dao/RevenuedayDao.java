package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.entity.coin.CoinProps;
import com.world.model.loan.entity.RepayOfQi;
import com.world.model.loan.entity.Revenueday;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.date.TimeUtil;
import com.world.web.response.DataResponse;

/**
 * @author Administrator 一天的利息收益、折合人民币
 */

@SuppressWarnings("rawtypes")
public class RevenuedayDao extends DataDaoSupport {
	private static final long serialVersionUID = 7905378933739897266L;

	@SuppressWarnings("unchecked")
	public DataResponse isExist(String userId, RepayOfQi repay) {
		
		log.info("对用户[" + userId + "]进行利息收益");		
		DataResponse dResponse = new DataResponse();
		List<OneSql> reveSqls = new ArrayList<OneSql>();
		//一开始给它判断，有错的话直接回滚提示处理
		if (StringUtils.isEmpty(userId)) {
			dResponse.setDes("userId is null");
			return dResponse;
		}
		// 获取当天(滞留)是否有记录存在
		Revenueday rnd = (Revenueday) this.getQuery()
				.setSql("SELECT * FROM revenueday WHERE userId=? AND earningTime=? AND fundsType = ?")
				.setParams(new Object[] { userId, TimeUtil.getTodayFirst(repay.getActureDate()), repay.getFundsType() })
				.setCls(Revenueday.class).getOne();
		try {
			JSONObject prices = LoanAutoFactory.getPrices();// 获取前台实时价格
			BigDecimal converts = BigDecimal.ZERO;// 折合人民币设置默认为0
			BigDecimal currentPrice = BigDecimal.ZERO;//线上价格
			BigDecimal liXi=repay.getLiXi().setScale(8);
			BigDecimal lxFwf=repay.getLxFwf().setScale(8, RoundingMode.UP);
			
			BigDecimal pureliXi = liXi.subtract(lxFwf);//纯利息=利息-(利息服务费=利息*服务费)

			CoinProps coint = DatabasesUtil.coinProps(repay.getFundsType());
			if(prices.containsKey(coint.getStag())){
				currentPrice = prices.getBigDecimal(coint.getStag());
			}else{
				currentPrice = BigDecimal.ONE;
			}
			converts = pureliXi.multiply(currentPrice);//折合=纯利息*当前实时价格
			// 如果查询出今天没有记录，就要给它插入一条值（当前值）
			if (rnd == null) {
				reveSqls.add(new OneSql("INSERT INTO revenueday (userId, earnings, converts, earningTime, fundsType) VALUE(?,?,?,?,?)", 1,
						new Object[] { userId, pureliXi, converts, repay.getActureDate(), repay.getFundsType() }));
			} else {
				reveSqls.add(new OneSql("UPDATE revenueday SET earnings=earnings+?, converts=converts+? WHERE id=?", 1,
						new Object[] { pureliXi, converts, rnd.getId() }));
			}
			// 执行完if里面的revenuedayDao，再修改标识
			reveSqls.add(new OneSql("UPDATE repayofqi SET dealStatus=? WHERE id=? AND userId=? AND dealStatus=?", 1,
					new Object[] { 2, repay.getId(), repay.getUserId(), repay.getDealStatus() }));
			// 多条物处理，如果有任何一条执行语句不成功，则抛出异常并返回定时器执行
			if (Data.doTrans(reveSqls)) {
				dResponse.setDes("修改成功！");
				dResponse.setSuc(true);
			} else {
				dResponse.setDes("修改失败！");
			}
		} catch (Exception e) {
			log.error("当前事物处理发生异常！", e);
			dResponse.setDes("当前事物处理发生异常！");
		}
		return dResponse;
	}

	/**
	 * 获取到账收益
	 * @param userId
	 * @param fundsType
     * @return
     */
	public BigDecimal getArrivedProfit(String userId,int fundsType){
		List<BigDecimal> list =  (List<BigDecimal>)Data.GetOne("select sum(earnings) amount from revenueday  where userId = '" +userId + "' and fundsType = ? ",new Object[]{fundsType});
		if(list != null && list.size() > 0 && list.get(0) != null) {
			return list.get(0);
		}
		return BigDecimal.ZERO;
	}


}

