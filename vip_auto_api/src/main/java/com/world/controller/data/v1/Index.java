package com.world.controller.data.v1;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.world.model.market.Market;
import com.world.web.Page;
import com.world.web.action.ApiAction;

public class Index extends ApiAction {

	@Page(Viewer = JSON)
	public void ticker() {
		try {

			String symbol = param("symbol");
			String marketName = symbol;

			JSONObject market = Market.getMarketByName(marketName);

			if (market == null) {
				marketName = Market.getDefMarketName();
				market = Market.getMarketByName(marketName);
			}

			String data = getTickerData(marketName);
			response.setContentType("text/javascript");
			response.getWriter().write(data);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Page(Viewer = JSON)
	public void depth() {

		try {
			String symbol = param("symbol");
			String marketName = symbol;

			JSONObject market = Market.getMarketByName(marketName);

			if (market == null) {
				marketName = Market.getDefMarketName();
				market = Market.getMarketByName(marketName);
			}

			int size = super.intParam("size");
			String merge = super.param("merge");
			if (StringUtils.isNotBlank(merge) && !super.validateMerge(marketName, merge)) {
				response.setContentType("text/javascript");
				response.getWriter().write("{\"error\":\"合并深度参数错误！\"}");
				return;
			}
			String data = getDepthData(marketName, size, merge);
			response.setContentType("text/javascript");
			response.getWriter().write(data);
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				response.getWriter().write("{\"code\" : \"error\"}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Page(Viewer = JSON)
	public void trades() {

		try {
			String symbol = param("symbol");
			String marketName = symbol;

			JSONObject market = Market.getMarketByName(marketName);

			if (market == null) {
				marketName = Market.getDefMarketName();
				market = Market.getMarketByName(marketName);
			}

			// 从指定交易ID后50条数据
			int since = request.getParameter("since") == null ? 0 : Integer.parseInt(request.getParameter("since"));
			String data = getTradesData(marketName, since);
			response.setContentType("text/javascript");
			response.getWriter().write(data);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取K线数据接口
	 */
	@Page(Viewer = JSON)
	public void kline() {

		if (!checkKlineRequest(ip())) {
			try {
				response.getWriter().write("{\"error\":\"请求太频繁。\"}");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		try {
			String symbol = param("symbol");
			String type = param("type");
			long since = longParam("since");
			int size = intParam("size");
			String data = getKlineData(symbol, type, size, since);
			response.setContentType("text/javascript");
			response.getWriter().write(data.toString());
		} catch (Exception ex) {
			try {
				response.getWriter().write("{\"error\":\"" + ex.getMessage() + "\"}");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.error(ex.getMessage());
		}
	}
}
