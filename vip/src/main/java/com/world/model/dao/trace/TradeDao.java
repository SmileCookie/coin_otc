package com.world.model.dao.trace;

import java.util.List;

import com.world.cache.Cache;
import com.world.constant.Const;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.entity.trace.Trade;

public class TradeDao extends DataDaoSupport<Trade> {

	private static final long serialVersionUID = 134545675623452L;

	public int save(Trade trade){
		return super.save("INSERT INTO trade (name, tickerUrl, remark, addTime, symbol, lastPrice, fundsType) VALUES (?,?,?,?,?,?,?)", new Object[]{
				trade.getName(), trade.getTickerUrl(), trade.getRemark(), trade.getAddTime(),trade.getSymbol(), trade.getLastPrice(), trade.getFundsType()
		});
	}

	public Trade findById(int id){
		return (Trade) super.getById(Trade.class, id);
	}

	public int update(Trade trade){
		return super.update("UPDATE trade SET name = ?, tickerUrl = ?, remark = ?, symbol = ?, lastPrice = ?, fundsType = ? WHERE id = ?", new Object[]{
				trade.getName(), trade.getTickerUrl(), trade.getRemark(),trade.getSymbol(), trade.getLastPrice(), trade.getFundsType(), trade.getId()
		});
	}

	public int delete(int id){
		return super.update("UPDATE trade SET isDeleted = 1 WHERE id = ? AND isDeleted = 0", new Object[]{id});
	}
	/**
	 * 获取所有行情交易所
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Trade> getListForTicker(){
		List<Trade> list = (List<Trade>) Cache.GetObj(Const.CACHE_TICKER_TRADE_LIST_KEY);
		if(list == null || list.size() == 0){
			list=getListForTicker1();
		}
		return list;
	}

	private List<Trade> getListForTicker1(){
		List<Trade> list = super.find("select * from trade where symbol is not null and symbol<>'' order by fundsType", new Object[]{}, Trade.class);
		Cache.SetObj(Const.CACHE_TICKER_TRADE_LIST_KEY, list, 30*60);
		return list;
	}
	public void resetCacheList(){
		getListForTicker1();
	}
	/**
	 * 获取交易所行情数据 cache key
	 * @param name 交易所name
	 * @param type 币种类型
	 * @return
	 */
	public static final String getCacheKey(String symbol){
		return new StringBuffer().append("ticker_key_").append(symbol).toString();
	}

}
