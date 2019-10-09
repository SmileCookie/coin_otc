package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.world.model.entity.pay.PayUserWalletBean;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.pay.FreezDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.financial.fee.Fee;
import com.world.model.entity.pay.FreezType;
import com.world.model.entity.pay.FreezeBean;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.loan.entity.LoanLevel;
import com.world.model.loan.entity.LoanRecord;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.CommonUtil;
import com.world.web.response.DataResponse;

public class P2pUserDao extends DataDaoSupport{

	private static String endMarketUSD = "_usdt";
	private static String endMarketBTC = "_btc";
	private static String endMarketETH = "_eth";

	private static String mainMarketName = "btc_usdt";


	public P2pUser getById(String userId){
		return (P2pUser) super.get("SELECT * FROM p2puser WHERE userId = ?", new Object[]{userId}, P2pUser.class);
	}
	
	public P2pUser getById(String userId , String userName){
		if(!userId.equals("0")){
			P2pUser user = getById(userId);
			if(user == null){
				//默认都有投资权限
				insert(userId , userName);
				user = getById(userId);
			}
			return user;
		}
		return null;
	}
	
	/***
	 * 添加新纪录
	 * @param userId
	 */
	public void insert(String userId , String userName){

		Data.Insert("insert into p2pUser(userId , userName) values(?,?)", new Object[]{userId , userName});
	}
	
	/****
	 * 发布借入
	 * @param amount
	 * @param userId
	 * @param userName
	 * @param sqls
	 */
	public void in(BigDecimal amount,  String userId, String userName , List<OneSql> sqls){
		sqls.add(new OneSql("UPDATE pay_user set inWait=inWait+? where userId=? AND fundsType = ?", 1 , new Object[]{amount, userId, coint.getFundsType()}));
	}
	
	public void cancelIn(BigDecimal amount , String userId , List<OneSql> sqls){
		sqls.add(new OneSql("UPDATE pay_user set inWait=inWait-? where userId=? and inWait>=? AND fundsType = ?", 1 , new Object[]{amount , userId , amount, coint.getFundsType()}));
	}
	
	/****
	 * 发布借出
	 * @param amount
	 * @param userId
	 * @param userName
	 * @param sqls
	 */
	public void out(BigDecimal amount , String userId, String userName , List<OneSql> sqls){
		sqls.add(new OneSql("UPDATE pay_user set outWait=outWait+? where userId=? AND fundsType = ?", 1 , new Object[]{amount , userId, coint.getFundsType()}));
	}

	public void cancelOut(BigDecimal amount , String userId , List<OneSql> sqls){
		sqls.add(new OneSql("UPDATE pay_user set outWait=outWait-?, balance=balance+?,freez=freez-? where userId=? and outWait>=? AND freez>=? AND FundsType = ?", 1 , new Object[]{amount, amount, amount, userId ,amount, amount, coint.getFundsType()}));
	}
	
	/****
	 * 借入资金
	 * @param amount
	 * @param lilv  日利率
	 * @param userId
	 * @param userName
	 * @param isIn
	 * @param sqls
	 */
	public void loanIn(BigDecimal amount , BigDecimal lilv , String userId, String userName , boolean isIn , List<OneSql> sqls){
		BigDecimal lx = amount.multiply(lilv).setScale(8, RoundingMode.DOWN);
		
		String lxOfdayField = "interestOfDay";
		if(isIn){//借入的
			String field = "inWait";
			String outField = "inSuccess";
			sqls.add(new OneSql("UPDATE pay_user set "+field+"="+field+"-?," + outField + "=" + outField + "+?," + lxOfdayField + "=" + lxOfdayField + "+?" +
					" where userId=? and "+field+">=? AND fundsType = ?", 1 , new Object[]{amount , amount , lx ,userId , amount, coint.getFundsType()}));
		}else{
			String inField = "inSuccess";
			sqls.add(new OneSql("UPDATE pay_user set " + inField + "=" + inField + "+?," + lxOfdayField + "=" + lxOfdayField + "+?" +
					" where userId=? AND fundsType = ?", 1 , new Object[]{amount, lx ,userId, coint.getFundsType()}));
		}
	}
	/****
	 * 借出资金
	 * @param amount
	 * @param type
	 * @param userId
	 * @param userName
	 * @param sqls
	 */
	public void loanOut(BigDecimal amount , String userId, String userName , boolean isIn , List<OneSql> sqls){
		if(!isIn){//借出单
			String field = "outWait";
			String outField = "outSuccess";
			sqls.add(new OneSql("UPDATE pay_user set "+field+"="+field+"-?," + outField + "=" + outField + "+?" +
					" where userId=? and "+field+">=? AND fundsType = ?", 1 , new Object[]{amount , amount ,userId , amount, coint.getFundsType()}));
		}else{
			String outField = "outSuccess";
			sqls.add(new OneSql("UPDATE pay_user set " + outField + "=" + outField + "+?" +
					" where userId=? AND fundsType = ?", 1 , new Object[]{amount,userId, coint.getFundsType()}));
		}
	}
	
	/**
	 * 减去借入的金额
	 * @param amount
	 * @param ll
	 * @param type
	 * @param userId
	 * @param userName
	 * @param arrearsLx
	 * @param sqls
	 */
	public void subIning(BigDecimal amount , BigDecimal ll , String userId, String userName, BigDecimal arrearsLx, List<OneSql> sqls){
		BigDecimal lx = amount.multiply(ll);//.setScale(8, RoundingMode.DOWN);
		String lxOfdayField = "interestOfDay";
		String field = "inSuccess";
		String overfield = "overdraft";
		sqls.add(new OneSql("update pay_user set " + field + "=" + field + "-?,"+ lxOfdayField + "=" + lxOfdayField + "-?,"+ overfield + "=" + overfield + "-?" +
				" where userId=? and "+field+">=? and "+overfield+">=? AND fundsType = ?", 1 , new Object[]{amount, lx, arrearsLx, userId, amount, arrearsLx, coint.getFundsType()}));
	}
	
	//加 overdraft    欠息
	public void addOverdraft(BigDecimal amount, int fundsType, String userId , List<OneSql> sqls){
		String field = "overdraft";
		sqls.add(new OneSql("update pay_user set " + field + "=" + field + "+? where userId=? AND fundsType = ?", 1 , new Object[]{amount,userId,fundsType}));
	}
	//减   欠息
	public void subOverdraft(BigDecimal amount , BigDecimal znj ,String userId , List<OneSql> sqls){
		String field = "overdraft";
		String znjField = "overdue";
		sqls.add(new OneSql("update pay_user set " + field + "=" + field + "-?,"+znjField+"="+znjField+"-? where userId=? and "+field+">=? and "+znjField+">=? AND fundsType=?", 1 , 
				new Object[]{amount,znj,userId,amount,znj, coint.getFundsType()}));
	}
	
	/****
	 * 减少借出成功的资金，并添加到可用
	 * @param amount 金额
	 * @param type   类型
	 * @param userId
	 * @param userName
	 * @param sqls
	 */
	public void subOuting(BigDecimal amount , String userId, String userName, List<OneSql> sqls){
		
		String outField = "outSuccess";
		sqls.add(new OneSql("update pay_user set " + outField + "=" + outField + "-?" +
				" where userId=? and "+outField+">=? AND fundsType = ?", 1 , new Object[]{amount ,userId,amount, coint.getFundsType()}));
	}
	
	/**
	 * 成交借贷的方法
	 * @param amount
	 * @param userId
	 * @param userName
	 * @param fundsType
	 * @param reason
	 * @param isIn
	 * @param sqls
	 * @return
	 * @throws Exception
	 */
	public DataResponse trans(BigDecimal amount , String userId , String userName , String reason , boolean isIn , List<OneSql> sqls) throws Exception{
		DataResponse response = null;
		if(!isIn){//借出
			out(amount, userId, userName, sqls);
			FreezDao freezDao = new FreezDao();
			freezDao.setCoint(coint);
			FreezeBean freez = new FreezeBean(userId, userName, coint.getPropTag() + "投资P2P", FreezType.bid.getKey(), amount, 0, 0);
			freezDao.freez(sqls, freez);
			
			if(Data.doTrans(sqls)){
				response = new DataResponse("发布投资成功。", true, "");
				UserCache.resetUserFunds(userId);
			}else{
				response = new DataResponse("发布投资信息出错，请重试或者联系客服。", false, "");
				log.error("冻结资金出错，请重试或者联系在线客服。");
			}
		}else{
			in(amount, userId, userName, sqls);
			//有待借入
			boolean res = Data.doTrans(sqls);
			if(res){
				UserCache.resetUserFunds(userId);
				response = new DataResponse("发布投资成功。", true, "");
			}else{
				response = new DataResponse("发布投资失败。", false, "");
			}
		}
		
		return response;
	}
	
	public void initLoanUser(P2pUser p2pUser){
		Map<String, PayUserBean> funds = UserCache.getUserFundsLoan(p2pUser.getUserId());

		//{"zec":1,"gbc":1,"dash":1,"etc":1,"eth":1,"ltc":22.85122}
		JSONObject prices = LoanAutoFactory.getPrices();

		resetAsset(funds, prices, p2pUser);//计算用户的总资产(USD)
		resetBtcAssets(funds, prices, p2pUser);//计算用户的总资产（BTC）
		resetUserMap(funds, prices, p2pUser);//重置用户可借入资产
		
		p2pUser.setPrices(prices);
		p2pUser.setFunds(funds);
	}



	public void initLoanWalletUser(P2pUser p2pUser){
		Map<String, PayUserWalletBean> funds = UserCache.getUserWalletFundsLoan(p2pUser.getUserId());

		//{"zec":1,"gbc":1,"dash":1,"etc":1,"eth":1,"ltc":22.85122}
		JSONObject prices = LoanAutoFactory.getPrices();

		resetWalletAsset(funds, prices, p2pUser);//计算用户的总资产(USD)
		resetWalletBtcAssets(funds, prices, p2pUser);//计算用户的总资产（BTC）
		resetWalletUserMap(funds, prices, p2pUser);//重置用户可借入资产

		p2pUser.setWalletPrices(prices);
		p2pUser.setWalletFunds(funds);
	}




	
	/**
	 * 初始化用户的借贷资产
	 * @param userId
	 * @return
	 */
	public P2pUser initLoanUser(String userId){
		P2pUser p2pUser = getById(userId);
		initLoanUser(p2pUser);

		return p2pUser;
	}
	
	/**
	 * 初始化用户的借贷资产
	 * @param userId
	 * @return
	 */
	public P2pUser initLoanUser(String userId, String userName){
		P2pUser p2pUser = getById(userId, userName);
		initLoanUser(p2pUser);
		initLoanWalletUser(p2pUser);
		return p2pUser;
	}


	/**
	 * @param 返回格式 {funds:{"btc":{},"ltc":{},"key":{}}, asset:{totalAssets:0,netAssets:0}}
	 * 重置数据，加载总资产，净资产 ，可用资金（包含冻结）=应该是减去欠息、滞纳金、以及一日的利息，可用资金（不包含提币冻结）=应该是减去欠息、滞纳金、以及一日的利息
	 * @x funds
	 * @param prices
	 * @return
	 */
	public void resetWalletAsset(Map<String, PayUserWalletBean> funds, JSONObject prices, P2pUser p2pUser){

		log.info( "重置" + p2pUser.getUserId() + "用户资产价格：" + prices );
		//不包含借贷的全部资产=净资产，如果有借入：净资产=可用总资产-借入金额；如果有借出：净资产=总资产-借出金额。
		BigDecimal totalWalletAssets = BigDecimal.ZERO;
		BigDecimal netAssets = BigDecimal.ZERO;
		BigDecimal inSuccess = BigDecimal.ZERO;
		BigDecimal withdrawFreeze = BigDecimal.ZERO;

		BigDecimal totalWalletAssetsBTC = BigDecimal.ZERO;
		BigDecimal netAssetsBTC = BigDecimal.ZERO;
		BigDecimal inSuccessBTC = BigDecimal.ZERO;
		BigDecimal withdrawFreezeBTC = BigDecimal.ZERO;


		BigDecimal totalAssetsETH = BigDecimal.ZERO;
		BigDecimal inSuccessETH = BigDecimal.ZERO;
		BigDecimal withdrawFreezeETH = BigDecimal.ZERO;



		//计算USD市场下的总市值（单位USD）和BTC市场下（但不包含USD市场的币种）的总市值（单位BTC）
		for(Entry<String, PayUserWalletBean> entry : funds.entrySet()){
			String key = entry.getKey();
			PayUserWalletBean payUser = entry.getValue();
			//计算USD相关
			if(prices.containsKey(key + endMarketUSD)){//usd市场包含该币种
				BigDecimal price = prices.getBigDecimal(key + endMarketUSD);
				if(null == price){
					price = BigDecimal.ONE;
				}
				totalWalletAssets = totalWalletAssets.add(payUser.getTotal().multiply(price));
				withdrawFreeze = withdrawFreeze.add(payUser.getFreez().multiply(price));
			}else if(prices.containsKey(key + endMarketBTC)){//btc市场单独包含币种
				BigDecimal price = prices.getBigDecimal(key + endMarketBTC);
				if(null == price){
					price = BigDecimal.ONE;
				}
				totalWalletAssetsBTC = totalWalletAssetsBTC.add(payUser.getTotal().multiply(price));
				withdrawFreezeBTC = withdrawFreezeBTC.add(payUser.getFreez().multiply(price));
			}else if(prices.containsKey(key + endMarketETH)) {//ETH市场单独包含币种
				BigDecimal price = prices.getBigDecimal(key + endMarketETH);
				if(null == price){
					price = BigDecimal.ONE;
				}
				totalAssetsETH = totalAssetsETH.add(payUser.getTotal().multiply(price));
				withdrawFreezeETH = withdrawFreezeETH.add(payUser.getFreez().multiply(price));
			}else if(key.equals("usdt")){//usd本身
				totalWalletAssets = totalWalletAssets.add(payUser.getTotal());
				withdrawFreeze = withdrawFreeze.add(payUser.getFreez());
			}
//			else{
//				//其他没有币种的市场不处理折算
//			}

		}
		BigDecimal ethprice = prices.getBigDecimal("eth" + endMarketUSD) != null ? prices.getBigDecimal("eth" + endMarketUSD) : new BigDecimal("1");
		BigDecimal btcprice = prices.getBigDecimal("eth" + endMarketBTC) != null ? prices.getBigDecimal("eth" + endMarketBTC) : new BigDecimal("1");
		//USD净资产（仅包含USD市场币种）
		netAssets = totalWalletAssets;
		if(inSuccess.compareTo(BigDecimal.ZERO) > 0){
			netAssets = totalWalletAssets.subtract(inSuccess).add(totalAssetsETH.multiply(ethprice)).subtract(inSuccessETH.multiply(ethprice));
		}
		//BTC净资产（BTC市场独有的币种）
		netAssetsBTC = totalWalletAssetsBTC;
		if(inSuccess.compareTo(BigDecimal.ZERO) > 0){
			netAssetsBTC = totalWalletAssetsBTC.subtract(inSuccessBTC).add(totalAssetsETH.multiply(btcprice)).subtract(inSuccessETH.multiply(btcprice));
		}
		withdrawFreezeBTC = withdrawFreezeBTC.add(withdrawFreezeETH.multiply(btcprice));

		if(prices.containsKey(mainMarketName)){
			BigDecimal price = prices.getBigDecimal(mainMarketName);
			totalWalletAssets = totalWalletAssets.add(totalWalletAssetsBTC.multiply(price));
			netAssets = netAssets.add(netAssetsBTC.multiply(price));
			withdrawFreeze = withdrawFreeze.add(withdrawFreezeBTC.multiply(price));
		}

		p2pUser.setTotalWalletAssets(totalWalletAssets);
		p2pUser.setNetWalletAssets(netAssets);
		p2pUser.setNetAssetsWalletSubWithdrawFreeze(netAssets.subtract(withdrawFreeze));
		p2pUser.setAvailableWalletSubOverdraft(p2pUser.getTotalAssetsSubOverdraft().subtract(withdrawFreeze));
	}








	/**
	 * @param 返回格式 {funds:{"btc":{},"ltc":{},"key":{}}, asset:{totalAssets:0,netAssets:0}}
	 * 重置数据，加载总资产，净资产 ，可用资金（包含冻结）=应该是减去欠息、滞纳金、以及一日的利息，可用资金（不包含提币冻结）=应该是减去欠息、滞纳金、以及一日的利息
	 * @x funds
	 * @param prices
	 * @return
	 */
	public void resetAsset(Map<String, PayUserBean> funds, JSONObject prices, P2pUser p2pUser){

		log.info( "重置" + p2pUser.getUserId() + "用户资产价格：" + prices );
		//不包含借贷的全部资产=净资产，如果有借入：净资产=可用总资产-借入金额；如果有借出：净资产=总资产-借出金额。
		BigDecimal totalAssets = BigDecimal.ZERO;
		BigDecimal netAssets = BigDecimal.ZERO;
		BigDecimal inSuccess = BigDecimal.ZERO;
		BigDecimal inWait = BigDecimal.ZERO;
		BigDecimal outSuccess = BigDecimal.ZERO;
		BigDecimal outWait = BigDecimal.ZERO;
		BigDecimal overdraft = BigDecimal.ZERO;
		BigDecimal interestOfDay = BigDecimal.ZERO;
		BigDecimal withdrawFreeze = BigDecimal.ZERO;

		BigDecimal totalAssetsBTC = BigDecimal.ZERO;
		BigDecimal netAssetsBTC = BigDecimal.ZERO;
		BigDecimal inSuccessBTC = BigDecimal.ZERO;
		BigDecimal inWaitBTC = BigDecimal.ZERO;
		BigDecimal outSuccessBTC = BigDecimal.ZERO;
		BigDecimal outWaitBTC = BigDecimal.ZERO;
		BigDecimal overdraftBTC = BigDecimal.ZERO;
		BigDecimal interestOfDayBTC = BigDecimal.ZERO;
		BigDecimal withdrawFreezeBTC = BigDecimal.ZERO;


		BigDecimal totalAssetsETH = BigDecimal.ZERO;
		BigDecimal netAssetsETH = BigDecimal.ZERO;
		BigDecimal inSuccessETH = BigDecimal.ZERO;
		BigDecimal inWaitETH = BigDecimal.ZERO;
		BigDecimal outSuccessETH = BigDecimal.ZERO;
		BigDecimal outWaitETH = BigDecimal.ZERO;
		BigDecimal overdraftETH = BigDecimal.ZERO;
		BigDecimal interestOfDayETH = BigDecimal.ZERO;
		BigDecimal withdrawFreezeETH = BigDecimal.ZERO;



		//计算USD市场下的总市值（单位USD）和BTC市场下（但不包含USD市场的币种）的总市值（单位BTC）
		for(Entry<String, PayUserBean> entry : funds.entrySet()){
			String key = entry.getKey();
			PayUserBean payUser = entry.getValue();
			//计算USD相关
			if(prices.containsKey(key + endMarketUSD)){//usd市场包含该币种
				BigDecimal price = prices.getBigDecimal(key + endMarketUSD);
				if(null == price){
					price = BigDecimal.ONE;
					log.info(key+"币种没取到价格默认为1");
				}
				totalAssets = totalAssets.add(payUser.getTotal().multiply(price));
				inSuccess = inSuccess.add(payUser.getInSuccess().multiply(price));
				inWait = inWait.add(payUser.getInWait().multiply(price));
				outSuccess = outSuccess.add(payUser.getOutSuccess().multiply(price));
				outWait = outWait.add(payUser.getOutWait().multiply(price));
				overdraft = overdraft.add(payUser.getOverdraft().multiply(price));
				interestOfDay = interestOfDay.add(payUser.getInterestOfDay().multiply(price));
				withdrawFreeze = withdrawFreeze.add(payUser.getWithdrawFreeze().multiply(price));
			}else if(prices.containsKey(key + endMarketBTC)){//btc市场单独包含币种
				BigDecimal price = prices.getBigDecimal(key + endMarketBTC);
				if(null == price){
					price = BigDecimal.ONE;
					log.info(key+"币种没取到价格默认为1");
				}
				totalAssetsBTC = totalAssetsBTC.add(payUser.getTotal().multiply(price));
				inSuccessBTC = inSuccessBTC.add(payUser.getInSuccess().multiply(price));
				inWaitBTC = inWaitBTC.add(payUser.getInWait().multiply(price));
				outSuccessBTC = outSuccessBTC.add(payUser.getOutSuccess().multiply(price));
				outWaitBTC = outWaitBTC.add(payUser.getOutWait().multiply(price));
				overdraftBTC = overdraftBTC.add(payUser.getOverdraft().multiply(price));
				interestOfDayBTC = interestOfDayBTC.add(payUser.getInterestOfDay().multiply(price));
				withdrawFreezeBTC = withdrawFreezeBTC.add(payUser.getWithdrawFreeze().multiply(price));
			}else if(prices.containsKey(key + endMarketETH)) {//ETH市场单独包含币种
				BigDecimal price = prices.getBigDecimal(key + endMarketETH);
				if(null == price){
					price = BigDecimal.ZERO;
				}
				totalAssetsETH = totalAssetsETH.add(payUser.getTotal().multiply(price));
				inSuccessETH = inSuccessETH.add(payUser.getInSuccess().multiply(price));
				inWaitETH = inWaitETH.add(payUser.getInWait().multiply(price));
				outSuccessETH = outSuccessETH.add(payUser.getOutSuccess().multiply(price));
				outWaitETH = outWaitETH.add(payUser.getOutWait().multiply(price));
				overdraftETH = overdraftETH.add(payUser.getOverdraft().multiply(price));
				interestOfDayETH = interestOfDayETH.add(payUser.getInterestOfDay().multiply(price));
				withdrawFreezeETH = withdrawFreezeETH.add(payUser.getWithdrawFreeze().multiply(price));
			}else if(key.equals("usdt")){//usd本身
				totalAssets = totalAssets.add(payUser.getTotal());
				inSuccess = inSuccess.add(payUser.getInSuccess());
				inWait = inWait.add(payUser.getInWait());
				outSuccess = outSuccess.add(payUser.getOutSuccess());
				outWait = outWait.add(payUser.getOutWait());
				overdraft = overdraft.add(payUser.getOverdraft());
				interestOfDay = interestOfDay.add(payUser.getInterestOfDay());
				withdrawFreeze = withdrawFreeze.add(payUser.getWithdrawFreeze());
			}
//			else{
//				//其他没有币种的市场不处理折算
//			}

		}
		BigDecimal ethprice = prices.getBigDecimal("eth" + endMarketUSD);
		if(null == ethprice){
			ethprice = BigDecimal.ONE;
		}

		BigDecimal btcprice = prices.getBigDecimal("eth" + endMarketBTC);
		if(null == btcprice){
			btcprice = BigDecimal.ONE;
		}
		//USD净资产（仅包含USD市场币种）
		netAssets = totalAssets;
		if(inSuccess.compareTo(BigDecimal.ZERO) > 0){
			netAssets = totalAssets.subtract(inSuccess).add(totalAssetsETH.multiply(ethprice)).subtract(inSuccessETH.multiply(ethprice));
		}
		//BTC净资产（BTC市场独有的币种）
		netAssetsBTC = totalAssetsBTC;
		if(inSuccess.compareTo(BigDecimal.ZERO) > 0){
			netAssetsBTC = totalAssetsBTC.subtract(inSuccessBTC).add(totalAssetsETH.multiply(btcprice)).subtract(inSuccessETH.multiply(btcprice));
		}
		withdrawFreezeBTC = withdrawFreezeBTC.add(withdrawFreezeETH.multiply(btcprice));
		inWaitBTC = inWaitBTC.add(inWaitETH.multiply(btcprice));
		inSuccessBTC = inSuccessBTC.add(inWaitETH.multiply(btcprice));
		outSuccessBTC = outSuccessBTC.add(outSuccessETH.multiply(btcprice));
		outWaitBTC = outWaitBTC.add(outWaitETH.multiply(btcprice));
		overdraftBTC = overdraftBTC.add(overdraftETH).multiply(btcprice);
		interestOfDayBTC = interestOfDayBTC.add(interestOfDayETH.multiply(btcprice));

		if(prices.containsKey(mainMarketName)){
			BigDecimal price = prices.getBigDecimal(mainMarketName);
			totalAssets = totalAssets.add(totalAssetsBTC.multiply(price));
			netAssets = netAssets.add(netAssetsBTC.multiply(price));
			withdrawFreeze = withdrawFreeze.add(withdrawFreezeBTC.multiply(price));
			inWait = inWait.add(inWaitBTC.multiply(price));
			inSuccess = inSuccess.add(inSuccessBTC.multiply(price));
			outSuccess = outSuccess.add(outSuccessBTC.multiply(price));
			outWait = outWait.add(outWaitBTC.multiply(price));
			overdraft = overdraft.add(overdraftBTC.multiply(price));
			interestOfDay = interestOfDay.add(interestOfDayBTC.multiply(price));
		}

		p2pUser.setTotalAssets(totalAssets);
		p2pUser.setNetAssets(netAssets);
		p2pUser.setNetAssetsSubWithdrawFreeze(netAssets.subtract(withdrawFreeze));
		p2pUser.setLoanInAssets(inSuccess.add(inWait));
		p2pUser.setLoanOutAssets(outSuccess.add(outWait));
		p2pUser.setTotalAssetsSubOverdraft(totalAssets.subtract(overdraft.add(interestOfDay)));
		p2pUser.setAvailableSubOverdraft(p2pUser.getTotalAssetsSubOverdraft().subtract(withdrawFreeze));
		p2pUser.setOverdraftConvert(overdraft);
		p2pUser.setInterestOfDayConvert(interestOfDay);
	}


	/**
	 * 重置用户BTC总额
	 * @param funds
	 * @param prices
	 * @param p2pUser
     */
	public void resetWalletBtcAssets(Map<String, PayUserWalletBean> funds, JSONObject prices,P2pUser p2pUser){

		BigDecimal totalAssetsBTC = BigDecimal.ZERO; //BTC总额 辅助字段计算BTC总额
		BigDecimal totalAssetsUSD = BigDecimal.ZERO;	//USD总额 辅助字段计算USD总额
		BigDecimal totalAssetsETH = BigDecimal.ZERO;	//USD总额 辅助字段计算USD总额
		for(Entry<String, PayUserWalletBean> entry : funds.entrySet()) {
			String key = entry.getKey();
			PayUserWalletBean payUser = entry.getValue();
			if (key.equalsIgnoreCase("btc")) {//btc本身
				totalAssetsBTC = totalAssetsBTC.add(payUser.getTotal());
			} else if (key.equalsIgnoreCase("usdt")) {//usd本身
				totalAssetsUSD = totalAssetsUSD.add(payUser.getTotal());
			} else if (key.equalsIgnoreCase("gbc")) {
				//gbc市场取usdt的价格，忽略btc市场，兼容gbc_btc市场未下掉的情况 modify by buxianguan
				BigDecimal price = prices.getBigDecimal(key + endMarketUSD);
				if(null == price){
					price = BigDecimal.ONE;
					log.info(key+"币种没取到价格默认为1");
				}
				totalAssetsUSD = totalAssetsUSD.add(payUser.getTotal().multiply(price));
			} else if(key.equalsIgnoreCase("eth")){
				BigDecimal price = prices.getBigDecimal(key + endMarketETH);
				if(null == price){
					price = BigDecimal.ZERO;
					log.info(key+"币种没取到价格默认为1");
				}
				totalAssetsETH = totalAssetsETH.add(payUser.getTotal().multiply(price));
			}else{//除btc和usd,eth和gbc之外
				if(prices.containsKey(key + endMarketBTC)){//btc市场包含该币种
					BigDecimal price = prices.getBigDecimal(key + endMarketBTC);
					if(null == price){
						price = BigDecimal.ONE;
						log.info(key+"币种没取到价格默认为1");
					}
					totalAssetsBTC = totalAssetsBTC.add(payUser.getTotal().multiply(price));
				}else if(prices.containsKey(key + endMarketUSD)){//usd市场单独包含币种(不包括BTC)
					BigDecimal price = prices.getBigDecimal(key + endMarketUSD);
					if(null == price){
						price = BigDecimal.ONE;
						log.info(key+"币种没取到价格默认为1");
					}
					totalAssetsUSD = totalAssetsUSD.add(payUser.getTotal().multiply(price));
				}else if(prices.containsKey(key + endMarketETH)){
					BigDecimal price = prices.getBigDecimal(key + endMarketETH);
					if(null == price){
						price = BigDecimal.ONE;
						log.info(key+"币种没取到价格默认为1");
					}
					totalAssetsETH = totalAssetsETH.add(payUser.getTotal().multiply(price));
				}
			}

		}
		if(prices.containsKey(mainMarketName) && totalAssetsUSD.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal price = prices.getBigDecimal(mainMarketName);
			if(null == price){
				price = BigDecimal.ONE;
				log.info(mainMarketName+"币种没取到价格默认为1");
			}
			BigDecimal btcprice = prices.getBigDecimal("eth" + endMarketBTC);
			if(null == btcprice){
				btcprice = BigDecimal.ONE;
				log.info(mainMarketName+"币种没取到价格默认为1");
			}
			totalAssetsBTC = totalAssetsBTC.add(totalAssetsUSD.divide(price,6,BigDecimal.ROUND_DOWN)).add(totalAssetsETH.multiply(btcprice));
		}
		p2pUser.setTotalWalletAssetsBtc(totalAssetsBTC);
	}


	/**
	 * 重置用户BTC总额
	 * @param funds
	 * @param prices
	 * @param p2pUser
	 */
	public void resetBtcAssets(Map<String, PayUserBean> funds, JSONObject prices,P2pUser p2pUser){
		try {
			BigDecimal totalAssetsBTC = BigDecimal.ZERO; //BTC总额 辅助字段计算BTC总额
			BigDecimal totalAssetsUSD = BigDecimal.ZERO;	//USD总额 辅助字段计算USD总额
			BigDecimal totalAssetsETH = BigDecimal.ZERO;	//USD总额 辅助字段计算USD总额
			for(Entry<String, PayUserBean> entry : funds.entrySet()) {
				String key = entry.getKey();
				PayUserBean payUser = entry.getValue();

				if (key.equalsIgnoreCase("btc")) {//btc本身
					totalAssetsBTC = totalAssetsBTC.add(payUser.getTotal());
				} else if (key.equalsIgnoreCase("usdt")) {//usd本身
					totalAssetsUSD = totalAssetsUSD.add(payUser.getTotal());
				} else if (key.equalsIgnoreCase("gbc")) {
					//gbc市场取usdt的价格，忽略btc市场，兼容gbc_btc市场未下掉的情况 modify by buxianguan
					BigDecimal price = prices.getBigDecimal(key + endMarketUSD);
					if(null == price){
						price = BigDecimal.ZERO;
						log.info(key+"币种没取到价格默认为1");
					}
					totalAssetsUSD = totalAssetsUSD.add(payUser.getTotal().multiply(price));
				} else if(key.equalsIgnoreCase("eth")){
					BigDecimal price = prices.getBigDecimal(key + endMarketETH);
					if(null == price){
						price = BigDecimal.ZERO;
						log.info(key+"币种没取到价格默认为1");
					}
					totalAssetsETH = totalAssetsETH.add(payUser.getTotal().multiply(price));
				}else{//除btc和usd,eth和gbc之外
					if(prices.containsKey(key + endMarketBTC)){//btc市场包含该币种
						BigDecimal price = prices.getBigDecimal(key + endMarketBTC);
						if(null == price){
							price = BigDecimal.ZERO;
							log.info(key+"币种没取到价格默认为1");
						}
						totalAssetsBTC = totalAssetsBTC.add(payUser.getTotal().multiply(price));
					}else if(prices.containsKey(key + endMarketUSD)){//usd市场单独包含币种(不包括BTC)
						BigDecimal price = prices.getBigDecimal(key + endMarketUSD);
						if(null == price){
							price = BigDecimal.ZERO;
							log.info(key+"币种没取到价格默认为1");
						}
						totalAssetsUSD = totalAssetsUSD.add(payUser.getTotal().multiply(price));
					}else if(prices.containsKey(key + endMarketETH)){
						BigDecimal price = prices.getBigDecimal(key + endMarketETH);
						if(null == price){
							price = BigDecimal.ZERO;
							log.info(key+"币种没取到价格默认为1");
						}
						totalAssetsETH = totalAssetsETH.add(payUser.getTotal().multiply(price));
					}
				}

			}
			if(prices.containsKey(mainMarketName) && totalAssetsUSD.compareTo(BigDecimal.ZERO) > 0){
				BigDecimal price = prices.getBigDecimal(mainMarketName);
				if(null == price){
					price = BigDecimal.ZERO;
					log.info(mainMarketName+"币种没取到价格默认为1");
				}
				BigDecimal btcprice = prices.getBigDecimal("eth" + endMarketBTC);
				if(null == btcprice){
					btcprice = BigDecimal.ZERO;
					log.info(endMarketBTC+"币种没取到价格默认为1");
				}
				totalAssetsBTC = totalAssetsBTC.add(totalAssetsUSD.divide(price,6,BigDecimal.ROUND_DOWN)).add(totalAssetsETH.multiply(btcprice));
			}
			p2pUser.setTotalAssetsBtc(totalAssetsBTC);
		}catch (Exception e){
			log.error("+++++++++++++++++++",e);
		}

	}






//
//	public void resetAsset(Map<String, PayUserBean> funds, JSONObject prices, P2pUser p2pUser){
//		//不包含借贷的全部资产=净资产，如果有借入：净资产=可用总资产-借入金额；如果有借出：净资产=总资产-借出金额。
//		BigDecimal totalAssets = BigDecimal.ZERO;
//		BigDecimal netAssets = BigDecimal.ZERO;
//		BigDecimal inSuccess = BigDecimal.ZERO;
//		BigDecimal inWait = BigDecimal.ZERO;
//		BigDecimal outSuccess = BigDecimal.ZERO;
//		BigDecimal outWait = BigDecimal.ZERO;
//		BigDecimal overdraft = BigDecimal.ZERO;
//		BigDecimal interestOfDay = BigDecimal.ZERO;
//		BigDecimal withdrawFreeze = BigDecimal.ZERO;
//		for(Entry<String, PayUserBean> entry : funds.entrySet()){
//			String key = entry.getKey();
//			PayUserBean payUser = entry.getValue();
//
//			if(prices.containsKey(key)){
//				BigDecimal price = prices.getBigDecimal(key);
//
//				totalAssets = totalAssets.add(payUser.getTotal().multiply(price));
//				inSuccess = inSuccess.add(payUser.getInSuccess().multiply(price));
//				inWait = inWait.add(payUser.getInWait().multiply(price));
//				outSuccess = outSuccess.add(payUser.getOutSuccess().multiply(price));
//				outWait = outWait.add(payUser.getOutWait().multiply(price));
//				overdraft = overdraft.add(payUser.getOverdraft().multiply(price));
//				/*start by xwz 20170608*/
//				interestOfDay = interestOfDay.add(payUser.getInterestOfDay().multiply(price));
//				/*end*/
//				withdrawFreeze = withdrawFreeze.add(payUser.getWithdrawFreeze().multiply(price));
//			}else{
//				totalAssets = totalAssets.add(payUser.getTotal());
//				inSuccess = inSuccess.add(payUser.getInSuccess());
//				inWait = inWait.add(payUser.getInWait());
//				outSuccess = outSuccess.add(payUser.getOutSuccess());
//				outWait = outWait.add(payUser.getOutWait());
//				overdraft = overdraft.add(payUser.getOverdraft());
//				interestOfDay = interestOfDay.add(payUser.getInterestOfDay());
//				withdrawFreeze = withdrawFreeze.add(payUser.getWithdrawFreeze());
//			}
//		}
//
//		netAssets = totalAssets;
//		if(inSuccess.compareTo(BigDecimal.ZERO) > 0){
//			netAssets = totalAssets.subtract(inSuccess);
//		}else if(outSuccess.compareTo(BigDecimal.ZERO) > 0){
//			/*start by xwz 2017-06-08,ps:投资时数据库中已经减掉投资*/
////			netAssets = totalAssets.subtract(outSuccess);
//			/*end*/
//		}
//
//		p2pUser.setTotalAssets(totalAssets);
//		p2pUser.setNetAssets(netAssets);
//		/*start by xwz 20170620*/
//		p2pUser.setNetAssetsSubWithdrawFreeze(netAssets.subtract(withdrawFreeze));
//		/*end*/
//		p2pUser.setLoanInAssets(inSuccess.add(inWait));
//		p2pUser.setLoanOutAssets(outSuccess.add(outWait));
//		p2pUser.setTotalAssetsSubOverdraft(totalAssets.subtract(overdraft.add(interestOfDay)));
//		p2pUser.setAvailableSubOverdraft(p2pUser.getTotalAssetsSubOverdraft().subtract(withdrawFreeze));
//		p2pUser.setOverdraftConvert(overdraft);
//		p2pUser.setInterestOfDayConvert(interestOfDay);
//	}
	
	/**
	 * 分析用户的借贷信息
	 * @param netAssetsConvert  净资产折合
	 * @param availableSubOverdraftConvert  总资产-拖欠的-当天的利息-提币冻结的 折合的比特币数量
	 * @param fundsloan
	 * @param prices
	 * @return
	 */
	public void resetUserMap(Map<String, PayUserBean> funds, JSONObject prices, P2pUser p2pUser){
		//不包含借贷的全部资产=净资产，如果有借入：净资产=可用总资产-借入金额；如果有借出：净资产=总资产-借出金额。
		for(Entry<String, PayUserBean> entry : funds.entrySet()){
			String key = entry.getKey();
			PayUserBean payUser = entry.getValue();
			
			BigDecimal netAssets = p2pUser.getNetAssets();
			/*start by xwz 20170620 ps:修改为净资产-提现冻结*/
			BigDecimal availableSubOverdraft = p2pUser.getAvailableSubOverdraft();
	//		BigDecimal availableSubOverdraft = p2pUser.getNetAssetsSubWithdrawFreeze();
			/*end*/

			BigDecimal canLoan = BigDecimal.ZERO;
			BigDecimal inSuccess = getUserAllInSuccess(funds,prices,key);//payUser.getInSuccess();
			BigDecimal inWait = getUserAllInWait(funds,prices,key);//payUser.getInWait();
			
			if(prices.containsKey(key)){
				BigDecimal price = prices.getBigDecimal(key);
				if(price.compareTo(BigDecimal.ZERO)>0){
					netAssets = netAssets.divide(price, 8, RoundingMode.DOWN);//净资产
					availableSubOverdraft = availableSubOverdraft.divide(price, 8, RoundingMode.DOWN);//可用资产

				}else{
					netAssets = BigDecimal.ZERO;
					availableSubOverdraft = BigDecimal.ZERO;
				}
				
			}
			
			LoanLevel ll = p2pUser.getLever();
			/*start by xwz 20170722 可借入增加提现额度*/
			BigDecimal netAssetsNew = availableSubOverdraft.subtract(inSuccess);//净资产
			BigDecimal highestTradable = netAssetsNew.multiply(ll.getInBili());//最高运作资金
			canLoan = highestTradable.subtract(inSuccess).subtract(inWait).subtract(netAssetsNew);//当前最高可借入
//			canLoan = netAssets.subtract(inSuccess).multiply(ll.getInBili()).subtract(inSuccess).subtract(inWait);
			/*end*/
			if(canLoan.compareTo(BigDecimal.ZERO)<0){//可借小于0 则显示为0
				canLoan = BigDecimal.ZERO;
			}
			payUser.setNetAssets(netAssets);
			payUser.setCanLoan(canLoan);
			
		}
	}

	public void resetWalletUserMap(Map<String, PayUserWalletBean> funds, JSONObject prices, P2pUser p2pUser){
		//不包含借贷的全部资产=净资产，如果有借入：净资产=可用总资产-借入金额；如果有借出：净资产=总资产-借出金额。
		for(Entry<String, PayUserWalletBean> entry : funds.entrySet()){
			String key = entry.getKey();
			PayUserWalletBean payUser = entry.getValue();

			BigDecimal netAssets = p2pUser.getNetAssets();
			/*start by xwz 20170620 ps:修改为净资产-提现冻结*/
			BigDecimal availableSubOverdraft = p2pUser.getAvailableSubOverdraft();
			//		BigDecimal availableSubOverdraft = p2pUser.getNetAssetsSubWithdrawFreeze();
			/*end*/
			if(prices.containsKey(key)){
				BigDecimal price = prices.getBigDecimal(key);
				if(price.compareTo(BigDecimal.ZERO)>0){
					netAssets = netAssets.divide(price, 8, RoundingMode.DOWN);//净资产
					availableSubOverdraft = availableSubOverdraft.divide(price, 8, RoundingMode.DOWN);//可用资产

				}else{
					netAssets = BigDecimal.ZERO;
					availableSubOverdraft = BigDecimal.ZERO;
				}
			}
			LoanLevel ll = p2pUser.getLever();
			/*start by xwz 20170722 可借入增加提现额度*/
			BigDecimal netAssetsNew = availableSubOverdraft;//净资产
			BigDecimal highestTradable = netAssetsNew.multiply(ll.getInBili());//最高运作资金
			payUser.setNetAssets(netAssets);

		}
	}








	
	/**
	 * 获取用户 币种的折合借入， 先计算所有其他币种的借入数量折合成BTC，再将折合借入BTC的数量换算成需要计算的币种数量。
	 * @param funds 资产集合
	 * @param prices 当前盘口价格集合
	 * @param coinName 当前处理的计算可借入币种名称
	 * @return 成功借入 折合本币资产数量
	 * @author zhanglinbo 20170211
	 */
	public BigDecimal getUserAllInSuccess(Map<String, PayUserBean> funds,JSONObject prices,String coinName){
		BigDecimal inSuccess = BigDecimal.ZERO;
		BigDecimal currentCoinPrice = BigDecimal.ONE;//折算币种的价格
		for(Entry<String, PayUserBean> entry : funds.entrySet()){
			String key = entry.getKey();
			PayUserBean payUser = entry.getValue();
			BigDecimal payUserInSuccess = payUser.getInSuccess();
			if(prices.containsKey(key)){
				BigDecimal price = prices.getBigDecimal(key);
				if(key.equals(coinName)){
					currentCoinPrice = price;//当前币种价格
				}
				
				if(price.compareTo(BigDecimal.ZERO)>0){
					payUserInSuccess = payUserInSuccess.multiply(price);
				}else{
					payUserInSuccess = BigDecimal.ZERO;
				}
			}
			inSuccess = inSuccess.add(payUserInSuccess);
		}
		
		inSuccess = inSuccess.divide(currentCoinPrice, 8, RoundingMode.DOWN);
		
		return inSuccess;
	}
	
	
	/**
	 * 获取用户 币种的折合等待借入， 先计算所有其他币种的等待借入数量折合成BTC，再将折合借入BTC的数量换算成需要计算的币种数量。
	 * @param funds 资产集合
	 * @param prices 当前盘口价格集合
	 * @param coinName 当前处理的计算可借入币种名称
	 * @return 等待借入 折合本币资产数量
	 * @author zhanglinbo 20170211
	 */
	public BigDecimal getUserAllInWait(Map<String, PayUserBean> funds,JSONObject prices,String coinName){
		BigDecimal inWait = BigDecimal.ZERO;
		
		BigDecimal currentCoinPrice = BigDecimal.ONE;//折算币种的价格
		for(Entry<String, PayUserBean> entry : funds.entrySet()){
			String key = entry.getKey();
			PayUserBean payUser = entry.getValue();
			BigDecimal payUserInWait = payUser.getInWait();
			if(prices.containsKey(key)){
				BigDecimal price = prices.getBigDecimal(key);
				if(key.equals(coinName)){
					currentCoinPrice = price;//当前币种价格
				}
				if(price.compareTo(BigDecimal.ZERO)>0){
					payUserInWait = payUserInWait.multiply(price);
				}else{
					payUserInWait = BigDecimal.ZERO;
				}
			}
			inWait = inWait.add(payUserInWait);
		}
		
		inWait = inWait.divide(currentCoinPrice, 2, RoundingMode.DOWN);
		return inWait;
	}
	
	
	public Map<String, P2pUser> getUserMap(String userIds) {
		return getUserMap(userIds, "userId");
	}

	public Map<String, P2pUser> getUserMap(String userIds, String column) {
		List<P2pUser> list = getUsers(userIds, column);
		
		Map<String, P2pUser> maps = new HashMap<String, P2pUser>();
		if (list != null && list.size() > 0) {
			for (P2pUser ub : list) {
				String uid = ub.getUserId();
				if (maps.get(uid) == null) {
					maps.put(uid, ub);
				}
			}
		}
		return maps;
	}
	
	public List<P2pUser> getUsers(String userIds, String column) {
		List<P2pUser> list = super.find("select * from Pay_User where "+column+" in ("+ userIds + ")", new Object[] {}, P2pUser.class);
		return list;
	}
	
	/****
	 * 
	 * @param userId
	 * @return
	 */
	public JSONObject getOutTimes(String userId){
		List<LoanRecord> hasLxList = (List<LoanRecord>)Data.QueryT(
				"SELECT ifnull(sum(hasLx),0) hasLx,fundsType FROM loanrecord WHERE outUserId = ? and status > 0  GROUP BY fundsType",
				new Object[] { userId }, LoanRecord.class);	
		//获取服务费
		List<Fee> feeList = (List<Fee>)Data.QueryT("SELECT ifnull(sum(amount),0) amount,currency FROM fee WHERE userId = ? and type = 2  GROUP BY currency", new Object[]{userId}, Fee.class);
		
		List<Bean> list = (List<Bean>) Data.Query("SELECT * FROM loanrecord WHERE outUserId = ? AND (status = ? OR status = ?)", new Object[]{userId, LoanRecordStatus.Returning.getKey(), LoanRecordStatus.forceRepay.getKey()}, LoanRecord.class);
	
		JSONObject object = new JSONObject();
		for (LoanRecord  loanRecord : hasLxList) {
			CoinProps coint = DatabasesUtil.coinProps(loanRecord.getFundsType());
			BigDecimal fees = BigDecimal.ZERO;
			for(Fee fee : feeList){
				if(fee.getCurrency().toLowerCase().equals(coint.getStag())){
					fees = fee.getAmount();
					break;
				}
			}
			
			JSONObject obj = new JSONObject();
			obj.put("hasLx", loanRecord.getHasLx().subtract(fees));
			
			object.put(coint.getStag(), obj);
		}
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		if(list != null && list.size() > 0){
			for(Bean b : list){
				LoanRecord record = (LoanRecord)b;
				String key = record.getInUserId();
				String coint = record.getFt().getStag();
				
				JSONObject obj = object.getJSONObject(coint);
				if(obj==null){
					obj = new JSONObject();
				}
				if(obj.containsKey("count")){
					obj.put("count" ,obj.getIntValue("count")+1);
				}else{
					obj.put("count", 1);
				}
				if(!map.containsKey(record.getFundsType()+"_"+key)){
					map.put(record.getFundsType()+"_"+key, 1);
					if(obj.containsKey("people")){
						obj.put("people", obj.getIntValue("people")+1);
					}else{
						obj.put("people", 1);
					}
				}
			}
		}
		return object;
	}
	
	/**
     * 有借入, 可提现=用户总资产-2*借入-未确认的币
     *
     * @param thisTimeCouldCash 当前可提现的币种数量
     * @param currency          btc,ltc,eth,etc
     * @return
     */
    public BigDecimal getCanWithdraw(String userId, String userName, BigDecimal thisTimeCouldCash, String currency) {
    	/**
    	 * 重置用户的资产信息
    	 */
        UserCache.resetUserWalletFunds(userId);
		UserCache.resetUserFunds(userId);
        P2pUser p2pUser = initLoanUser(userId, userName);

        JSONObject prices = p2pUser.getWalletPrices();
        //提现币种当前价格
        BigDecimal curPrice = BigDecimal.ONE;
        if(prices.containsKey(currency.toLowerCase())){
        	curPrice = prices.getBigDecimal(currency.toLowerCase());
        }

        BigDecimal noConfirmConverts = CommonUtil.getNoConfirmConverts(userId, prices);
        BigDecimal canWithdraw = BigDecimal.ZERO;
        if(p2pUser.hasLoanIn()){
            BigDecimal totalIning = p2pUser.getLoanInAssets();
            BigDecimal userTotalFund = p2pUser.getAvailableWalletSubOverdraft();

            //可提现=用户总资产-2*借入
			canWithdraw = userTotalFund.subtract(totalIning.multiply(new BigDecimal("2"))).subtract(noConfirmConverts)
					.divide(curPrice, 8, RoundingMode.DOWN);
            canWithdraw = canWithdraw.min(thisTimeCouldCash);
        } else {
            canWithdraw = thisTimeCouldCash;
            if(noConfirmConverts.compareTo(BigDecimal.ZERO) > 0){
                // 折算成该币种计算
                canWithdraw = p2pUser.getTotalWalletAssets().subtract(noConfirmConverts).divide(curPrice, 8, RoundingMode.DOWN);
                canWithdraw = canWithdraw.min(thisTimeCouldCash);
            }
        }
        if (canWithdraw.compareTo(BigDecimal.ZERO) < 0) {
            canWithdraw = BigDecimal.ZERO;
        }

        return canWithdraw;
    }
    
    
	public P2pUser getByUserName(String userName){
		if(StringUtils.isNotEmpty(userName)){
			return (P2pUser) super.get("SELECT * FROM p2puser WHERE userName = ?", new Object[]{userName}, P2pUser.class);
		}
		return null;
	}


}
