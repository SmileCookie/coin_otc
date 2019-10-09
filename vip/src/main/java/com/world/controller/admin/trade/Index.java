package com.world.controller.admin.trade;

import java.math.BigDecimal;
import java.util.List;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Query;
import com.world.model.dao.trace.TradeDao;
import com.world.model.entity.trace.Trade;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/trade/", des = "交易行情")
public class Index extends AdminAction {
	private static final long serialVersionUID = 4615012581421814331L;
	TradeDao dao = new TradeDao();

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		String tab = param("tab");
		int pageNo = intParam("page");
		String name = param("name");

		Query<Trade> query = dao.getQuery();
		query.setSql("select * from trade where 1=1 ");
		query.setCls(Trade.class);
		int pageSize = 20;

		if (name.length() > 0)
			query.append(" and name like '%" + name + "%'");

		try {
			long total = query.count();
			if (total > 0) {
				query.append("order by fundsType");
				List<Trade> dataList = dao.findPage(pageNo, pageSize);
				setAttr("dataList", dataList);
				setAttr("itemCount", total);
			}
			setPaging((int) total, pageNo, pageSize);
			setAttr("tab", tab);
			setAttr("page", pageNo);
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	/**
	 * 刷新缓存，并重启线程
	 */
	@Page(Viewer = JSON)
	public void refreshCache() {
		try {
			dao.resetCacheList();
			json("刷新成功", true, null);
		} catch (Exception e) {
			log.error("内部异常", e);
			json("刷新失败", false, "");
		}
	}

	@Page(Viewer = DEFAULT_AORU)
	public void aoru() {
		try {
			int id = intParam("id");
			if (id > 0) {
				Trade trade = (Trade) dao.getById(Trade.class, id);
				setAttr("trade", trade);
			}
			setAttr("ft", DatabasesUtil.getCoinPropMaps());
		} catch (Exception ex) {
			log.error("内部异常", ex);
		}
	}

	@Page(Viewer = JSON)
	public void doAoru() {
		try {
			int id = intParam("id");
			String name = param("name");
			String tickerUrl = param("tickerUrl");
			String remark = param("remark");
			String symbol = param("symbol");// 网站标识
			int fundsType = intParam("fundsType");
			BigDecimal lastPrice = decimalParam("lastPrice");

			Trade trade = null;
			int rtn = 0;
			if (id == 0) {
				trade = new Trade();
				trade.setName(name);
				trade.setTickerUrl(tickerUrl);
				trade.setIsDeleted(0);
				trade.setRemark(remark);
				trade.setAddTime(now());
				trade.setSymbol(symbol);
				trade.setFundsType(fundsType);
				trade.setLastPrice(lastPrice);
				rtn = dao.save(trade);
			} else {
				trade = (Trade) dao.getById(Trade.class, id);
				trade.setName(name);
				trade.setTickerUrl(tickerUrl);
				trade.setRemark(remark);
				trade.setSymbol(symbol);
				trade.setFundsType(fundsType);
				trade.setLastPrice(lastPrice);
				rtn = dao.update(trade);
			}
			if (rtn > 0) {
				json("保存成功", true, "");
			} else {
				json("保存失败", false, "");
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			json("保存失败", false, "");
		}
	}

	@Page(Viewer = ".xml")
	public void doDel() {
		try {
			int id = intParam("id");
			if (id == 0) {
				WriteError("系统找不到要删除的数据");
			} else {
				if (dao.delete(id) > 0) {
					WriteRight("删除成功");
				} else {
					WriteError("删除失败");
				}
			}
		} catch (Exception ex) {
			log.error("内部异常", ex);
			WriteError("未知异常");
		}
	}

}
