package com.world.controller.admin.financial.fee;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Query;
import com.world.model.dao.fee.FeeDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.financial.fee.Fee;
import com.world.util.date.TimeUtil;
import com.world.web.Page;
import com.world.web.action.AdminAction;
import com.world.web.convention.annotation.FunctionAction;

@FunctionAction(jspPath = "/admins/financial/fee/", des = "手续费收益")
public class Index extends AdminAction {

	private FeeDao feeDao = new FeeDao();

	@Page(Viewer = DEFAULT_INDEX)
	public void index() {
		// 获取参数
		String currency = param("currency");
		int feeType = intParam("feeType");
		int surverType = intParam("surverType");
		String startDate = param("startDate");
		String endDate = param("endDate");
		String loadFlag = param("loadFlag");
		if(surverType == 0){
			surverType = 2;
		}

		Timestamp start;
		Timestamp end;
		try{
			//如果开始时间或结束时间为空，则默认为当天
			if(StringUtils.isEmpty(startDate)){
				/**start by xzhang 20170905 为防止影响后续代码逻辑，做到最小变动。start设置最小开始时间，覆盖业务发生数据**/
				if(!"1".equals(loadFlag)){
					start = TimeUtil.getTodayFirst();
				}else{
					start = Timestamp.valueOf("2017-01-01 00:00:00");
				}
				/**end**/
			}else{
				start = TimeUtil.getTodayFirst(Timestamp.valueOf(startDate+" 01:00:00"));
			}
			if(StringUtils.isEmpty(endDate)){
				end = TimeUtil.getTodayLast();
			}else{
				end = TimeUtil.getTodayLast(Timestamp.valueOf(endDate+" 01:00:00"));
			}
		
			String sql = feeDao.findListSql(currency, feeType, surverType, start, end);
			Query query = feeDao.getQuery();
			query.setSql(sql);
			query.setCls(Fee.class);
			List<Bean> dataList = query.getList();

			List<String> showDateTime = getShowDateTime(surverType, dataList);
			Map<String,BigDecimal> totalAmount = getTotalAmount(dataList);

			setAttr("dateTime", showDateTime);
			/*setAttr("rmbTotalAmount", totalAmount[0]);
			setAttr("btcTotalAmount", totalAmount[1]);
			setAttr("ltcTotalAmount", totalAmount[2]);
			setAttr("ethTotalAmount", totalAmount[3]);
			setAttr("etcTotalAmount", totalAmount[4]);*/
			setAttr("totalAmount",totalAmount);
			setAttr("dataList", dataList);
			setAttr("feeType", feeType);
			setAttr("coinMap",getCoinMap());
			setAttr("loadFlag","1");
		}catch(Exception ex){
			log.error("内部异常", ex);
		}
	}

	// ajax的调用
	@Page(Viewer = DEFAULT_AJAX)
	public void ajax() {
		index();
	}

	public List<String> getShowDateTime(int surverType,List<Bean> dataList){
		List<String> result = new ArrayList<String>();
		if(surverType != 2){
			String right = "";
			for (Bean bean : dataList) {
				Fee fee = (Fee)bean;
				if(!right.equals(fee.getTimestr())){
					result.add(fee.getTimestr());
				}
				right = fee.getTimestr();
			}
		}else{
			String right = "";
			for (Bean bean : dataList) {
				Fee fee = (Fee)bean;
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(fee.getTime());
				Calendar cal = new GregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				Calendar calFirstDayInThisWeek = (Calendar)cal.clone();
				calFirstDayInThisWeek.add(Calendar.DATE, cal.getActualMinimum(Calendar.DAY_OF_WEEK)-dayOfWeek + 1);
				Calendar calLastDayInThisWeek = (Calendar)cal.clone();
				calLastDayInThisWeek.add(Calendar.DATE,cal.getActualMaximum(Calendar.DAY_OF_WEEK)-dayOfWeek + 1);
				if(!right.equals(fee.getTimestr())){
					result.add(fee.getTimestr() + "(" +new SimpleDateFormat("yyyy-MM-dd").format(calFirstDayInThisWeek.getTime())+ "至"
							+ new SimpleDateFormat("yyyy-MM-dd").format(calLastDayInThisWeek.getTime()) +  ")");
				}
				right = fee.getTimestr();
			}
		}
		return result;
	}

	public Map<String,BigDecimal> getTotalAmount(List<Bean> dataList){
		/*BigDecimal [] result = new BigDecimal[5];
		BigDecimal rmb = BigDecimal.ZERO;
		BigDecimal btc = BigDecimal.ZERO;
		BigDecimal ltc = BigDecimal.ZERO;
		BigDecimal eth = BigDecimal.ZERO;
		BigDecimal etc = BigDecimal.ZERO;
		for (Bean bean : dataList) {
			Fee fee = (Fee)bean;
			if("cny".equalsIgnoreCase(fee.getCurrency())){
				rmb = rmb.add(fee.getAmount());
			}else if("btc".equalsIgnoreCase(fee.getCurrency())){
				btc = btc.add(fee.getAmount());
			}else if("ltc".equalsIgnoreCase(fee.getCurrency())){
				ltc = ltc.add(fee.getAmount());
			}else if("eth".equalsIgnoreCase(fee.getCurrency())){
				eth = ltc.add(fee.getAmount());
			}else if("etc".equalsIgnoreCase(fee.getCurrency())){
				etc = ltc.add(fee.getAmount());
			}
		}
		result[0] = rmb;
		result[1] = btc;
		result[2] = ltc;
		result[3] = eth;
		result[4] = etc;*/
		Map<String,BigDecimal> result = new HashMap<String,BigDecimal>();
		for (Bean bean : dataList) {
			Fee fee = (Fee)bean;
			if(!"".equals(fee.getCurrency())){
				BigDecimal amount = result.get(fee.getCurrency());
				if(amount==null){
					amount = fee.getAmount();
					result.put(fee.getCurrency(), amount);
				}else{
					amount = amount.add(fee.getAmount());
				}
			}
		}
		
		return result;
	}
	
	
	
	public Fee getP2pFee(){
		Fee fee = new Fee();
		return fee;
	}
	
	public Map<String,String> getCoinMap(){
		Map<String,String> coinMap = new HashMap<String,String>();
		Iterator<Entry<String,CoinProps>> iter = DatabasesUtil.getCoinPropMaps().entrySet().iterator();
		while(iter.hasNext()){
			Entry<String,CoinProps> entry = iter.next();
			coinMap.put(entry.getKey(), entry.getValue().getPropCnName());
		}
		return coinMap;
	}
	
}

