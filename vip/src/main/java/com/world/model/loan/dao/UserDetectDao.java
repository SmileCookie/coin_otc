package com.world.model.loan.dao;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.Lan;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.data.mysql.OneSql;
import com.world.model.dao.api.ApiKeyDao;
import com.world.model.dao.mobile.PostCodeType;
import com.world.model.dao.user.UserDao;
import com.world.model.dao.user.mem.UserCache;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.model.loan.MarketPrices;
import com.world.model.loan.entity.CheckInfo;
import com.world.model.loan.entity.LoanLevel;
import com.world.model.loan.entity.LoanRecordStatus;
import com.world.model.loan.entity.P2pUser;
import com.world.model.loan.worker.LoanAutoFactory;
import com.world.util.DigitalUtil;
import com.world.util.jpush.MsgType;
import com.world.util.jpush.Pusher;

/****
 * 用户借入资金检测
 * 分析：当用户的总资产不足借贷金额的120%时，系统会三次发送短信到您的账户绑定的手机通知强制清算风险；<br/>
 * 当用户的总资产不足借贷金额的110%时，系统将按照实时委托单价格对用户的资产进行强制买入或卖出清算
 * 并归还借款和借币
 * @author apple
 *
 */
public class UserDetectDao extends DataDaoSupport{
	//与外网价格差异百分比,高于此百分比禁止平仓
//	dif_from_outsite_in_persent=0.05
//	private static final double dif_from_outsite_in_persent = Double.valueOf(GlobalConfig.getValue("dif_from_outsite_in_persent"));

	P2pUserDao p2pUserDao = new P2pUserDao();
	ApiKeyDao apiDao = new ApiKeyDao();
	/****
	 * 强制还款检测系统
	 * 
	 */
	public void force(P2pUser user){
		log.info(user.getUserName() + ",平仓操作。");
		///从数据库重新获取用户资产  再次判断
		try {
			log.info("重新从数据库取出用户["+user.getUserName()+"]资产进行核对......");
			UserCache.resetUserFunds(user.getUserId());
			//重新获取
			user = p2pUserDao.initLoanUser(user.getUserId());
					
			JSONObject THIS_PRICES = LoanAutoFactory.getPrices();
			if(MarketPrices.isReturn(THIS_PRICES)){
				log.info("价格有误，无法判断价格差异，暂不平仓。");
			}

			int level = getForceLevelByUser(user);
			log.info("userId=" + user.getUserId() + ", level=" + level);
			if(level < 100){
				log.info("发现用户["+user.getUserName()+"]平仓级别为:"+level+" ，并未平仓......");
				return;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
			return;
		}
		
		List<OneSql> sqls = new ArrayList<OneSql>();
		
		sqls.add(new OneSql("update p2pUser set repayLock=?,repayLevel=? where userId=? and repayLock=?" , 1 , new Object[]{true , 100 , user.getUserId() , false}));
		//更改后将被监听还款
		sqls.add(new OneSql("update LoanRecord set status=? where inUserId=? and status=?", -2 ,
				new Object[]{LoanRecordStatus.forceRepay.getKey() , user.getUserId() , LoanRecordStatus.Returning.getKey()}));
		
		//锁定账户交易
		/** 增加锁定方法 */
		//sqls.add(apiDao.lock(user.getUserId()));
		new UserDao().updateRepayLock(user.getUserId(), 1);
		
		if(Data.doTrans(sqls)){
			//委托给分析师进行智能委托交易
			LoanRecordDao lrDao = new LoanRecordDao();
			lrDao.cancelAndAutoRepay(user);
		}
	}
	
	
	/****
	 * 检测用户资金状况
	 * 每隔一定时间进行检测用户借贷资金系统一次
	 * level 爆仓级别  1-10
	 */
	public void detect(int level , int pageNo , int pageSize , JSONObject prices){
		try {
			List<Bean> users = getHasLoanUsersByLevel(pageNo , pageSize , level);
			int size = users.size();
			if(size > 0){
				for(Bean b : users){
					P2pUser user = (P2pUser) b;
					p2pUserDao.initLoanUser(user);
					//智能强制分析判断处理
					_detect(user);
				}
			}
			if(size >= pageSize){
				log.info("处理级别[" + ((level-1) * 10 + 1) + "-" + level * 10 + "]的用户pageNo:" + (pageNo + 1) + ",pageSize:" + pageSize);
				detect(level , pageNo + 1 , pageSize , prices);
			}else{
				//log.info("未找到级别[" + ((level-1) * 10 + 1) + "-" + level * 10 + "]的用户记录需要更新");
				return;
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	/***
	 * 分析：当用户的总资产不足借贷金额的120%时，系统会三次发送短信到您的账户绑定的手机通知强制清算风险；
	 * 当用户的总资产不足借贷金额的110%时，系统将按照实时委托单价格对用户的资产进行强制买入或卖出清算
	 * @param user
	 */
	public void _detect(P2pUser user){
		try {
			/***
			 * funds 取自内存需要严格的内存判断，如果不是真实的用户资产  此处会出大错，间接导致用户平仓
			 */
			////用户的当前余额应该是减掉拖欠和滞纳金后的金额
			BigDecimal userTotal = user.getTotalAssetsSubOverdraft();
			
			if(userTotal.compareTo(BigDecimal.ZERO) <= 0){//取到的资金总额小于等于0  一定有问题
				return;
			}
			//log.info("用户["+user.getUserName()+"的]可用资金" + userTotal);
			///借款折合总额
			BigDecimal loanInAssetsConvert = user.getLoanInAssets();
			
			BigDecimal loan_120 = loanInAssetsConvert.multiply(DigitalUtil.getBigDecimal(1.2));
			BigDecimal loan_110 = loanInAssetsConvert.multiply(DigitalUtil.getBigDecimal(1.1));
			//
			int forceLevel = getForceLevel(userTotal , loanInAssetsConvert , user.getLever());
			
			log.info("用户["+user.getUserName()+"]的平仓级别为" + forceLevel);
			if(userTotal.compareTo(loan_120) <= 0){//当总资产不足借贷金额的120%时，系统会三次发送短信到您的账户绑定的手机通知强制清算风险
				///发送短信
				try {///try不影响下次发送
					log.info("重新从数据库取出用户["+user.getUserName()+"]资产进行核对......");
					UserCache.resetUserFunds(user.getUserId());
					user = p2pUserDao.initLoanUser(user.getUserId());
								
					loanInAssetsConvert = user.getLoanInAssets();
					loan_120 = loanInAssetsConvert.multiply(DigitalUtil.getBigDecimal(1.2));
					userTotal = user.getTotalAssetsSubOverdraft();
					if(userTotal.compareTo(loan_120) > 0){
						log.info("用户用户净资产大于20%，不再发送短信");
						return;
					}
					//发送短信
					tips(user , forceLevel);
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}
			
			if(userTotal.compareTo(loan_110) < 0){//总资产不足借贷金额的110%时，系统将按照实时委托单价格对您的资产进行强制买入或卖出清算并归还借款和借币
				if(!user.isRepayLock()){//还未被锁定的用户
					if(user.getSysForce() == 1){//处理强制平仓用户
						//强制处理
						force(user);
					}
				}
			}else{
				if(user.isRepayLock()){//解锁还款锁  下次还会平仓
					//解除锁定
					List<OneSql> sqls = new ArrayList<OneSql>();
					
					sqls.add(new OneSql("update p2pUser set repayLock=? where userId=? and repayLock=?" , 1 , new Object[]{false , user.getUserId() , true}));
					//sqls.add(apiDao.unlock(user.getUserId()));
					new UserDao().updateRepayLock(user.getUserId(), 2);
					
				}
			}
			///偏移量超过2 更新数据库
			if(Math.abs(forceLevel - user.getRepayLevel()) >= 1){///此过程包含了从有借款到无借款的解锁问题
				///////计算平仓价
				BigDecimal balanceBigger = userTotal.subtract(loan_110);
				JSONObject unwindPrices = calUnwindPirce(user, balanceBigger);
				resetLevel(user , forceLevel, unwindPrices);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}
	
	/**
	 * 在风险级别变化时调用, 不在借入时调用, 这方法会考虑所有币的资产情况. 如我的btc资产足够还btc借款,则不会被平仓,则没有预计平仓价,
	 * 在预计平仓价偏离5倍时,不显示平仓价,比如: 当前btc价格为3000, 当预计平仓价大于15000时不显示
	 * {a:{},b{},c:{}}
	 * @return
	 */
	public JSONObject calUnwindPirce(P2pUser user, BigDecimal balanceBigger){
		
		Map<String, PayUserBean> funds = user.getFunds();
		JSONObject prices = user.getPrices();
		BigDecimal minAvailable = DigitalUtil.getBigDecimal("0.001");
		
		JSONObject unwindPrices = new JSONObject();
		JSONObject unwindPrices2 = new JSONObject();
		for(Entry<String, PayUserBean> e : funds.entrySet()){
			String key = e.getKey();
			PayUserBean payUser = e.getValue();
			
			/**
			 * 没有市场价默认为借钱做多
			 */
			if(!prices.containsKey(key)){
				if(payUser.getInSuccess().compareTo(BigDecimal.ZERO) > 0){
					Iterator<String> it = prices.keySet().iterator();
					while (it.hasNext()) {
						BigDecimal unwindPrice = BigDecimal.ZERO;
						String coint = (String) it.next();
						PayUserBean cointPay = funds.get(coint);
						BigDecimal thisPrice = prices.getBigDecimal(coint);
						//有可用的，并且借入是0，计算平仓价
						if(cointPay.getTotal().compareTo(minAvailable) > 0 && cointPay.getInSuccess().compareTo(BigDecimal.ZERO) == 0){
							unwindPrice = user.getLoanInAssets().multiply(new BigDecimal("1.1"))
									.subtract(user.getTotalAssets())
									.add(user.getOverdraftConvert())
									.add(user.getInterestOfDayConvert())
									.divide(cointPay.getTotal(), 2, RoundingMode.UP);
							if (unwindPrice.doubleValue() > thisPrice.doubleValue() *1.2
									|| unwindPrice.doubleValue() < thisPrice.doubleValue() / 5) {
								unwindPrice = BigDecimal.ZERO;
							}
							unwindPrices.put(coint, unwindPrice);
						}
					}
				}
			}else{
				BigDecimal unwindPrice2 = BigDecimal.ZERO;
				/**
				 * 有市场价格就代表借币做空
				 */
				BigDecimal thisPrice = prices.getBigDecimal(key);
				if(payUser.getInSuccess().subtract(payUser.getTotal()).compareTo(BigDecimal.ZERO) > 0){//借币做空
					unwindPrice2 = user.getTotalAssets()
							.subtract(user.getOverdraftConvert())
							.subtract(user.getInterestOfDayConvert())
							.subtract(user.getLoanInAssets().subtract(payUser.getInSuccess().multiply(thisPrice)))
									.multiply(new BigDecimal("1.1"))
							.divide(payUser.getInSuccess().multiply(new BigDecimal("1.1")).subtract(payUser.getTotal()), 2, RoundingMode.DOWN);
					
					if (unwindPrice2.doubleValue() < thisPrice.doubleValue()
							|| unwindPrice2.doubleValue() > thisPrice.doubleValue() * 5) {
						unwindPrice2 = BigDecimal.ZERO;
					}
					unwindPrices2.put(key, unwindPrice2);
				}
			}
		}
		
		Iterator<String> it = unwindPrices.keySet().iterator();
		while (it.hasNext()) {
			String key = (String)it.next();
			BigDecimal unwindPrice1 = unwindPrices.getBigDecimal(key);
			BigDecimal unwindPrice2 = unwindPrices2.getBigDecimal(key);
			if(unwindPrice1!=null && unwindPrice2!=null){
				if(unwindPrice1.doubleValue()>0 && unwindPrice2!=null&& unwindPrice2.doubleValue()>0){
					unwindPrice1 = unwindPrice1.subtract(prices.getBigDecimal("key")).abs().min(unwindPrice2.subtract(prices.getBigDecimal("key")).abs());
				}else {
					unwindPrice1 = unwindPrice1.add(unwindPrice2);
				}
				unwindPrices.put(key, unwindPrice1);
			}
			
		}
		
		/*JSONObject funds = user.getFunds();
		Iterator<String> it = funds.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			JSONObject obj = funds.getJSONObject(key);
			BigDecimal unwindPrice = BigDecimal.ZERO;
			BigDecimal unwindPrice2 = BigDecimal.ZERO;
			
		}
		
		BigDecimal ltcUnwindPrice = BigDecimal.ZERO;
		BigDecimal ethUnwindPrice = BigDecimal.ZERO;
		BigDecimal etcUnwindPrice = BigDecimal.ZERO;
		
		BigDecimal btcUnwindPrice2 = BigDecimal.ZERO;
		BigDecimal ltcUnwindPrice2 = BigDecimal.ZERO;
		BigDecimal ethUnwindPrice2 = BigDecimal.ZERO;
		BigDecimal etcUnwindPrice2 = BigDecimal.ZERO;
		
		///偏移量超过2 更新数据库
		///////计算平仓价
		//判断做空还是做多   借钱做多   借币做空
		BigDecimal iningRmb = user.getIningRmb();
		BigDecimal iningBtc = user.getIningBtc();
		BigDecimal iningLtc = user.getIningLtc();
		BigDecimal iningEth = user.getIningEth();
		BigDecimal iningEtc = user.getIningEtc();
		
		BigDecimal totalIning = iningRmb.add(iningBtc).add(iningLtc).add(iningEth).add(iningEtc);
		
		
		BigDecimal btcPrice = user.getPrices()[0];
		BigDecimal ltcPrice = user.getPrices()[1];
		BigDecimal ethPrice = user.getPrices()[2];
		BigDecimal etcPrice = user.getPrices()[3];
		
		//RoundingMode.DOWN =1
		BigDecimal myRmb = user.getFunds()[0].setScale(2, 1).add(user.getFunds()[1].setScale(2, 1));
		BigDecimal myBtc = user.getFunds()[2].setScale(3, 1).add(user.getFunds()[3].setScale(3, 1));
		BigDecimal myLtc = user.getFunds()[4].setScale(3, 1).add(user.getFunds()[5].setScale(3, 1));
		BigDecimal myEth = user.getFunds()[15].setScale(3, 1).add(user.getFunds()[16].setScale(3, 1));
		BigDecimal myEtc = user.getFunds()[24].setScale(3, 1).add(user.getFunds()[25].setScale(3, 1));
		
//		log.info("======= iningEth:" + iningEth + ", myEth:" + myEth + ", ethPrice:" + ethPrice + "  =========================");
		
		 *//**做多平仓价计算:
		 * 在计算某币种的平仓价时,假设其它币种的价格不变
		 * 以ltc为例:
		 * (借入btc*价格  + 借入ltc*<平仓价y> + 借入eth*价格  + 借入rmb) * 1.1
		 * 				 = (可用btc*价格  + 可用ltc*<平仓价y> + 可用eth*价格 + 可用etc*价格  + 可用rmb) - 所有欠息 - 所有借款1天利息
		 * 
		 * 计算用<平仓价y> = ??
		 * 
		 *  注::::: 如果当前币种有借入,则是做空,平仓价较市场价高;  如果当前币种无借入且有可用余额, 则认为是做多,平仓价较市场价低; 
		 **//*

		 
		
//		if(iningRmb.compareTo(BigDecimal.ZERO) > 0){//借钱做多
			///比特币平仓价
		if (myBtc.compareTo(new BigDecimal("0.001")) > 0 && iningBtc.compareTo(BigDecimal.ZERO) == 0 && totalIning.compareTo(BigDecimal.ZERO)>0 ) {
				btcUnwindPrice = iningRmb
						.add(iningLtc.multiply(ltcPrice))
						.add(iningEth.multiply(ethPrice))
						.add(iningEtc.multiply(etcPrice))
						.multiply(new BigDecimal("1.1"))
						.subtract(myRmb)
						.subtract(myLtc.multiply(ltcPrice))
						.subtract(myEth.multiply(ethPrice))
						.subtract(myEtc.multiply(etcPrice))
						.add(user.overdraftConertRmb())
						.add(user.dayOfLxConertRmb())
						.divide(myBtc, 2, RoundingMode.UP);
				if (btcUnwindPrice.doubleValue() > btcPrice.doubleValue() *1.2
						|| btcUnwindPrice.doubleValue() < btcPrice.doubleValue() / 5) {
					btcUnwindPrice = BigDecimal.ZERO;
				}
			}

			//莱特币平仓价
		if (myLtc.compareTo(new BigDecimal("0.001")) > 0 && iningLtc.compareTo(BigDecimal.ZERO) == 0 && totalIning.compareTo(BigDecimal.ZERO)>0) {
				ltcUnwindPrice = iningRmb
						.add(iningBtc.multiply(btcPrice))
						.add(iningEth.multiply(ethPrice))
						.add(iningEtc.multiply(etcPrice))
						.multiply(new BigDecimal("1.1"))
						.subtract(myRmb)
						.subtract(myBtc.multiply(btcPrice))
						.subtract(myEth.multiply(ethPrice))
						.subtract(myEtc.multiply(etcPrice))
						.add(user.overdraftConertRmb())
						.add(user.dayOfLxConertRmb())
						.divide(myLtc, 2, RoundingMode.UP);
				if (ltcUnwindPrice.doubleValue() > ltcPrice.doubleValue() *1.2
						|| ltcUnwindPrice.doubleValue() < ltcPrice.doubleValue() / 5) {
					ltcUnwindPrice = BigDecimal.ZERO;
				}
			}
			//以太币平仓价
		if (myEth.compareTo(new BigDecimal("0.001")) > 0 && iningEth.compareTo(BigDecimal.ZERO) == 0 && totalIning.compareTo(BigDecimal.ZERO)>0) {
				ethUnwindPrice = iningRmb
						.add(iningBtc.multiply(btcPrice))
						.add(iningLtc.multiply(ltcPrice))
						.add(iningEtc.multiply(etcPrice))
						.multiply(new BigDecimal("1.1"))
						.subtract(myRmb)
						.subtract(myBtc.multiply(btcPrice))
						.subtract(myLtc.multiply(ltcPrice))
						.subtract(myEtc.multiply(etcPrice))
						.add(user.overdraftConertRmb())
						.add(user.dayOfLxConertRmb())
						.divide(myEth, 2, RoundingMode.UP);
				
				if (ethUnwindPrice.doubleValue() > ethPrice.doubleValue() *1.2
						|| ethUnwindPrice.doubleValue() < ethPrice.doubleValue() / 5) {
					ethUnwindPrice = BigDecimal.ZERO;
				}
			}
			//Etc平仓价
		if (myEtc.compareTo(new BigDecimal("0.001")) > 0 && iningEtc.compareTo(BigDecimal.ZERO) == 0 && totalIning.compareTo(BigDecimal.ZERO)>0) {
				etcUnwindPrice = iningRmb
						.add(iningBtc.multiply(btcPrice))
						.add(iningLtc.multiply(ltcPrice))
						.add(iningEth.multiply(ethPrice))
						.multiply(new BigDecimal("1.1"))
						.subtract(myRmb)
						.subtract(myBtc.multiply(btcPrice))
						.subtract(myLtc.multiply(ltcPrice))
						.subtract(myEth.multiply(ethPrice))
						.add(user.overdraftConertRmb())
						.add(user.dayOfLxConertRmb())
						.divide(myEtc, 2, RoundingMode.UP);
				if (etcUnwindPrice.doubleValue() > etcPrice.doubleValue() *1.2
						|| etcUnwindPrice.doubleValue() < etcPrice.doubleValue() / 5) {
					etcUnwindPrice = BigDecimal.ZERO;
				}
			}
//		}
		
		if(iningBtc.subtract(myBtc).compareTo(BigDecimal.ZERO) > 0){//借币做空
			btcUnwindPrice2 = myRmb.add(myLtc.multiply(ltcPrice)).add(myEth.multiply(ethPrice)).add(myEtc.multiply(etcPrice))
					.subtract(user.overdraftConertRmb())
					.subtract(user.dayOfLxConertRmb())
					.subtract(iningRmb
							.add(iningLtc.multiply(ltcPrice))
							.add(iningEth.multiply(ethPrice))
							.add(iningEtc.multiply(etcPrice))
							.multiply(new BigDecimal("1.1")))
					.divide(iningBtc.multiply(new BigDecimal("1.1")).subtract(myBtc), 2, RoundingMode.DOWN);
			
			if (btcUnwindPrice2.doubleValue() < btcPrice.doubleValue()
					|| btcUnwindPrice2.doubleValue() > btcPrice.doubleValue() * 5) {
				btcUnwindPrice2 = BigDecimal.ZERO;
			}
		}
		
		if(iningLtc.subtract(myLtc).compareTo(BigDecimal.ZERO) > 0){
			ltcUnwindPrice2 = myRmb.add(myBtc.multiply(btcPrice)).add(myEth.multiply(ethPrice)).add(myEtc.multiply(etcPrice))
					.subtract(user.overdraftConertRmb())
					.subtract(user.dayOfLxConertRmb())
					.subtract(iningRmb
							.add(iningBtc.multiply(btcPrice))
							.add(iningEth.multiply(ethPrice))
							.add(iningEtc.multiply(etcPrice))
							.multiply(new BigDecimal("1.1")))
					.divide(iningLtc.multiply(new BigDecimal("1.1")).subtract(myLtc), 2, RoundingMode.DOWN);
			
			if (ltcUnwindPrice2.doubleValue() < ltcPrice.doubleValue()
					|| ltcUnwindPrice2.doubleValue() > ltcPrice.doubleValue() * 5) {
				ltcUnwindPrice2 = BigDecimal.ZERO;
			}
		}
		
		if(iningEth.subtract(myEth).compareTo(BigDecimal.ZERO) > 0){
			ethUnwindPrice2 = myRmb.add(myLtc.multiply(ltcPrice)).add(myBtc.multiply(btcPrice)).add(myEtc.multiply(etcPrice))
					.subtract(user.overdraftConertRmb())
					.subtract(user.dayOfLxConertRmb())
					.subtract(iningRmb
							.add(iningLtc.multiply(ltcPrice))
							.add(iningBtc.multiply(btcPrice))
							.add(iningEtc.multiply(etcPrice))
							.multiply(new BigDecimal("1.1")))
					.divide(iningEth.multiply(new BigDecimal("1.1")).subtract(myEth), 2, RoundingMode.DOWN);
			
			if (ethUnwindPrice2.doubleValue() < ethPrice.doubleValue()
					|| ethUnwindPrice2.doubleValue() > ethPrice.doubleValue() * 5) {
				ethUnwindPrice2 = BigDecimal.ZERO;
			}
		}
		
		if(iningEtc.subtract(myEtc).compareTo(BigDecimal.ZERO) > 0){
			etcUnwindPrice2 = myRmb.add(myLtc.multiply(ltcPrice)).add(myBtc.multiply(btcPrice)).add(myEth.multiply(ethPrice))
					.subtract(user.overdraftConertRmb())
					.subtract(user.dayOfLxConertRmb())
					.subtract(iningRmb
							.add(iningLtc.multiply(ltcPrice))
							.add(iningBtc.multiply(btcPrice))
							.add(iningEth.multiply(ethPrice))
							.multiply(new BigDecimal("1.1")))
							.divide(iningEtc.multiply(new BigDecimal("1.1")).subtract(myEtc), 2, RoundingMode.DOWN);
			
			if (etcUnwindPrice2.doubleValue() < etcPrice.doubleValue()
					|| etcUnwindPrice2.doubleValue() > etcPrice.doubleValue() * 5) {
				etcUnwindPrice2 = BigDecimal.ZERO;
			}
		}
		
//		log.info("\n ========= btcUnwindPrice=" + btcUnwindPrice + ", btcUnwindPrice2=" + btcUnwindPrice2
//				+ ", ltcUnwindPrice=" + ltcUnwindPrice + ", ltcUnwindPrice2=" + ltcUnwindPrice2 
//				+ ", ethUnwindPrice=" + ethUnwindPrice + ", ethUnwindPrice2=" + ethUnwindPrice2 + " ==============================");

		
		
		if(btcUnwindPrice.doubleValue()>0 && btcUnwindPrice2.doubleValue()>0){
			btcUnwindPrice = btcUnwindPrice.subtract(btcPrice).abs().min(btcUnwindPrice2.subtract(btcPrice).abs());
		}else {
			btcUnwindPrice = btcUnwindPrice.add(btcUnwindPrice2);
		}
		if(ltcUnwindPrice.doubleValue()>0 && ltcUnwindPrice2.doubleValue()>0){
			ltcUnwindPrice = ltcUnwindPrice.subtract(ltcPrice).abs().min(ltcUnwindPrice2.subtract(ltcPrice).abs());
		}else {
			ltcUnwindPrice = ltcUnwindPrice.add(ltcUnwindPrice2);
		}
		if(ethUnwindPrice.doubleValue()>0 && ethUnwindPrice2.doubleValue()>0){
			ethUnwindPrice = ethUnwindPrice.subtract(ethPrice).abs().min(ethUnwindPrice2.subtract(ethPrice).abs());
		}else {
			ethUnwindPrice = ethUnwindPrice.add(ethUnwindPrice2);
		}
		if(etcUnwindPrice.doubleValue()>0 && etcUnwindPrice2.doubleValue()>0){
			etcUnwindPrice = etcUnwindPrice.subtract(etcPrice).abs().min(etcUnwindPrice2.subtract(etcPrice).abs());
		}else {
			etcUnwindPrice = etcUnwindPrice.add(etcUnwindPrice2);
		}*/
		
		
		return null;
	}
	
	RepayOfQiDao roqDao = new RepayOfQiDao();
	
	/***
	 * 检测是否有预期未还利息，当前是否能还上了
	 * 如果可以还上了就给还了
	 * @param user
	 */
	private void checkOverdraft(P2pUser user) {
		if(user.getOverdraftConvert().compareTo(BigDecimal.ZERO) > 0){
			List<Bean> nos = roqDao.getNoRepaysOfQi(user.getUserId());
			Map<String, PayUserBean> funds = user.getFunds();
			for(Entry<String, PayUserBean> entry : funds.entrySet()){
				String key = entry.getKey();
				PayUserBean payUser = entry.getValue();
				
				BigDecimal dueAmount = payUser.getOverdraft();
				if(dueAmount.compareTo(BigDecimal.ZERO) > 0 && dueAmount.compareTo(payUser.getBalance()) <= 0){//归还
					roqDao.repqyOfFunds(nos, payUser.getFundsType());
				}
			}
		}else{
			log.info("用户["+user.getUserName()+"]没有欠息");
		}
	}

	/****
	 * 发送消息提醒
//	 * @param funds
//	 * @param prices
	 * @param user
	 * @param forceLevel
	 */
	private void tips(P2pUser user , int forceLevel) {
		Map<String, PayUserBean> funds = user.getFunds();
		JSONObject prices = LoanAutoFactory.getPrices();
		CheckInfo info = new CheckInfo();
		info.setUserId(user.getUserId());
		info.setPrices(prices);
		
		JSONObject balance = new JSONObject();
		JSONObject borrowed = new JSONObject();
		for(Entry<String, PayUserBean> entry : funds.entrySet()){
			String key = entry.getKey();
			PayUserBean pay = entry.getValue();
			balance.put(key, pay.getTotal());
			borrowed.put(key, pay.getInSuccess());
		}
		info.setBalance(balance);
		info.setBorrowed(borrowed);
		info.setGrade(forceLevel);
		info.setSendSms(1);
		info.setAddTime(now());
		String language = new UserDao().getUserLanguageById(user.getUserId());
		try{
			new CheckInfoDao().addCheckInfo(info, String.format(Lan.Language(language, PostCodeType.loanForce.getDes()), user.getUserName()));
			//通过api调用推送接口
			User u = null;
			try {
				 u = new UserDao().get(user.getUserId());
				if(StringUtils.isNotBlank(u.getJpushKey())) {
					Pusher.push("达到平仓预警点!", u.getJpushKey(),MsgType.liquidationRiskRemind);
					log.warn("平仓推送成功!");
				}
			} catch (Exception e) {
				log.error("【极光推送】当前用户:"+user.getUserId()+",所用registrationId："+u.getJpushKey()+"推送："+MsgType.liquidationRiskRemind.getValue()+"消息异常，异常信息为:", e);
			}
		}catch(Exception ex){
			log.error("推送平仓预警失败!", ex);
		}
	}
	/****
	 * 重置用户的平仓等级
	 * @param userId
	 */
	public void resetUserLevel(String userId){
		P2pUserDao p2pUserDao = new P2pUserDao();
		P2pUser user = p2pUserDao.initLoanUser(userId);
		if(user != null){
			//设置用户资产
			int forceLevel = getForceLevelByUser(user);
			JSONObject unwindPrices = new JSONObject();
			
			BigDecimal userTotal = user.getTotalAssetsSubOverdraft();
			BigDecimal loanConvertRmbs = user.getLoanInAssets();//user.getIningRmb().add(user.getIningBtc().multiply(prices[0])).add(user.getIningLtc().multiply(prices[1]));
			BigDecimal loan_110 = loanConvertRmbs.multiply(DigitalUtil.getBigDecimal(1.1));
			if(userTotal.compareTo(loan_110) > 0){
				unwindPrices = calUnwindPirce(user, null);
			}
			
			resetLevel(user , forceLevel, unwindPrices);
		}
	}
	
	public void resetLevel(P2pUser user , int forceLevel, JSONObject unwindPrices){
		
		UserDao userDao = new UserDao();
		List<OneSql> sqls = new ArrayList<OneSql>();
		if(unwindPrices!=null){
			Iterator<String> it = unwindPrices.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				CoinProps coint = DatabasesUtil.coinProps(key);
				sqls.add(new OneSql("UPDATE pay_user SET unwindPrice = ? WHERE userId = ? AND fundsType = ?", 1, new Object[]{unwindPrices.getBigDecimal(key), user.getUserId(), coint.getFundsType()}));
			}
		}
		
		sqls.add(new OneSql("update p2pUser set repayLevel=? where userId=?" , 1 , 
				new Object[]{forceLevel, user.getUserId()}));
		
		if(user.getRepayLevel() > 0 && forceLevel == 0){//从有借款到无借款的解锁问题
			log.info("$$$$重设用户["+user.getUserName()+"]的平仓级别为：" + forceLevel + ",从有借款到无借款。");
			if(user.getLoanInAssets().compareTo(BigDecimal.ZERO) > 0){
				userDao.updateRepayLock(user.getUserId(), 3);
			}else{
				/**解锁*/
				//sqls.add(apiDao.unlock(user.getUserId()));
				userDao.updateRepayLock(user.getUserId(), 0);
			}
		}else if(user.getRepayLevel() == 0 && forceLevel > 0){//从无借款到有借款的变动
			log.info("$$$$重设用户["+user.getUserName()+"]的平仓级别为：" + forceLevel + ",从无借款到有借款。");
			/**锁定*/
			//sqls.add(apiDao.lock(user.getUserId()));
			userDao.updateRepayLock(user.getUserId(), 2);
		}else{
			log.info("$$$$重设用户["+user.getUserName()+"]的平仓级别为：" + forceLevel);
			
		}
		Data.doTrans(sqls);
	}
	
	/*****
	 * 比例 =（资产总额 / 借款总额）
	 * (2-比例)
	 * 平仓风险 = 比例 >= 2 ? 1 :  (2-比例) * 1000 / 9
	 * @param user
	 * @return
	 */
	public int getForceLevelByUser(P2pUser user){
		BigDecimal userTotal = user.getTotalAssetsSubOverdraft();//用户当前的资产总额
		///借款折合总额
		BigDecimal loanConverts = user.getLoanInAssets();
		///范围从0 - 0.9   计算爆仓级别正比
		///userTotal = 2 * loanConvertRmbs
		MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
		
		if(loanConverts.compareTo(BigDecimal.ZERO) <= 0){//无借入时为0
			return 0;
		}
		
		//用户资产折合RMB总额/借入折合RMB总额
		BigDecimal biLi = userTotal.divide(loanConverts , mc);
		
		return getLevelByBiLi(biLi , user.getLever());
	}
	/****
	 * 有借入至少1
	 * @param biLi 用户资产折合RMB总额/借入折合RMB总额
	 */
	public int getLevelByBiLi(BigDecimal biLi , LoanLevel lever){
		BigDecimal n_1p1 = DigitalUtil.getBigDecimal(1.1);
		BigDecimal totalJinDu = lever.getBaseBili().subtract(n_1p1);//满仓到爆仓的距离  比如一倍杠杆：2-1.1 = 0.9  两倍杠杆：1.5 - 1.1 = 0.4
		
		int forceLevel = 0;
		if(biLi.compareTo(lever.getBaseBili()) >= 0){
			forceLevel = 1;
		}else{
			BigDecimal duoTag = lever.getBaseBili().subtract(biLi);
			MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
			//
			forceLevel = biLi.compareTo(n_1p1) < 0 ? 100 : 
									duoTag.multiply(DigitalUtil.getBigDecimal(100)).divide(DigitalUtil.getBigDecimal(totalJinDu) , mc).intValue();
			if(forceLevel < 1){
				forceLevel = 1;
			}
		}
		return forceLevel;
	}
	
	/********
	 * 
	 * @param userTotal  用户账户余额
	 * @param loanConvertRmbs  借入折合RMB金额
	 * @return
	 */
	public int getForceLevel(BigDecimal userTotal , BigDecimal loanConvertRmbs , LoanLevel lever){
		if(loanConvertRmbs.compareTo(BigDecimal.ZERO) <= 0){//没有借入时设置为0
			return 0;
		}
		MathContext mc = new MathContext(20, RoundingMode.HALF_DOWN);
		BigDecimal biLi = userTotal.divide(loanConvertRmbs , mc);
		return getLevelByBiLi(biLi ,lever);
	}
	
	/******
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @param level  1-10 从1到10
	 * @return
	 */
	private List<Bean> getHasLoanUsersByLevel(int pageNo , int pageSize, int level){
		return Data.Query("select * from P2pUser where repayLevel>=? and repayLevel<=? limit ?,?", 
				new Object[]{((level-1) * 10 + 1 ), level * 10 , pageSize * (pageNo - 1) , pageSize}, P2pUser.class);
	}

}
