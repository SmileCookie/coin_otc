package com.world.controller.data;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.world.cache.Cache;
import com.world.model.market.Market;
import com.world.web.Page;
import com.world.web.action.ApiAction;

public class Index extends ApiAction {

	@Page(Viewer = ".xml", Cache = 60)
	public void index() {

	}

	@Page(Viewer = JSON)
	public void ticker() {
		try {
			String symbol = GetPrama(0);
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

	@Page
	public void depth() {
		try {
			String symbol = GetPrama(0);
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

	@Page
	public void stock() {
		trades();
	}

	@Page
	public void getTrades() {
		trades();
	}

	/**
	 * 获取交易历史记录数据
	 */
	@Page
	public void trades() {
		try {
			String symbol = GetPrama(0);
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
			String symbol = GetPrama(0);
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
