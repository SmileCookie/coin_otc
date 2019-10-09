package com.world.model.dao.pay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.database.DatabasesUtil;
import com.world.data.mysql.Bean;
import com.world.data.mysql.Data;
import com.world.data.mysql.DataDaoSupport;
import com.world.model.dao.user.UserDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.PayUserBean;
import com.world.model.entity.user.User;
import com.world.model.loan.dao.DefaultLimitDao;
import com.world.util.string.StringUtil;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class PayUserDao extends DataDaoSupport<PayUserBean>{

	public PayUserDao(){
		super();
	}
	
	public PayUserDao(String database){
		this.database = database;
	}
	
	public List<PayUserBean> getFunds(String userId){
		List<PayUserBean> list = super.find("SELECT * FROM pay_user WHERE userId = ? ORDER BY fundsType", new Object[]{userId}, PayUserBean.class);
		return list;
	}
	public List<PayUserBean> getHoldCoin(String userId){
		List<PayUserBean> list = super.find("SELECT * FROM pay_user WHERE userId = ? and balance > 0 ORDER BY fundsType", new Object[]{userId}, PayUserBean.class);
		return list;
	}
	/***
	 * 
	 * @param id
	 * @return
	 */
	//modify by xwz 2016-06-10
	public PayUserBean getById(int id, int fundsType) {
		PayUserBean pub = (PayUserBean) Data.GetOne("select * from pay_user where userId=? AND fundsType = ?", new Object[] { id, fundsType }, PayUserBean.class);
		try {
			if (pub == null) {
				UserDao ud = new UserDao();
				User user = ud.get(String.valueOf(id));
				if (user != null && StringUtil.exist(user.getUserName())) {
					BigDecimal loanLimit = getLoanLimit(fundsType);
					//modify by xwz 用户注册时多次初始化，导致主键冲突，初始化语句加入ignore，防止主键冲突
					String insertPayUser = "insert ignore into Pay_User(userId,userName, fundsType,loanLimit) values(?,?,?,?)";
					Data.Insert(insertPayUser, new Object[] { id, user.getUserName()+"", fundsType, loanLimit });
					pub = (PayUserBean) Data.GetOne("select * from pay_user where userId=?", new Object[] { id }, PayUserBean.class);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		pub.setCoint(DatabasesUtil.coinProps(fundsType));
		return pub;
	}

	/**
	 * 根据资产类型id获取资产信息
	 * @param fundsType 类型ID
	 * @return CoinProps
	 */
	private CoinProps getCoinByFundsType(int fundsType){
		CoinProps curCoin = null;
		for(Entry<String, CoinProps> entry :  DatabasesUtil.getCoinPropMaps().entrySet()){
			if(entry.getValue().getFundsType() == fundsType){
				curCoin = entry.getValue();
				break;
			}
		}
		return curCoin;
	}


	// TODO: 2017/6/11 xwz
	private static BigDecimal getLoanLimit(int fundsType){
		String cointName = DatabasesUtil.coinProps(fundsType).getDatabaseKey();
		DefaultLimitDao dao = new DefaultLimitDao();
		String sql = "SELECT valueName from defaultlimit where typeName = 'limitKey' and keyName= ?";
		List<String> list = (List<String>) Data.GetOne(sql,new Object[]{cointName});
		BigDecimal limitLoan = BigDecimal.ZERO;
		try{
			limitLoan = new BigDecimal(list.get(0));
		}catch (Exception e){
			limitLoan = new BigDecimal("1000");
		}
		return limitLoan;
	}

	/**
	 * 根据用户ID获取用户余额Map
	 */
	public Map<String,BigDecimal> getBalanceMap(String userId){
		Map<String,BigDecimal> balanceMap = new HashMap<>();
		Map<String, PayUserBean>  payUserMaps = getFundsLoanMap(userId);
		for(Map.Entry<String, PayUserBean> entry : payUserMaps.entrySet()){
			PayUserBean payUserBean = entry.getValue();
			balanceMap.put(entry.getKey(),payUserBean.getBalance());
		}
		return balanceMap;
	}

	/**
	 *
	 * @param userName
	 * @param fundsType
     * @return
     */
	public PayUserBean getByUserName(String userName, int fundsType) {
		PayUserBean pub = (PayUserBean) Data.GetOne("select * from Pay_User where userName=? AND fundsType = ?", new Object[] { userName, fundsType }, PayUserBean.class);
		try {
			if (pub == null) {
				User user = new UserDao().getByField("userName", userName);
				if (user != null) {

					String insertPayUser = "insert into Pay_User(userId,userName, fundsType) values(?,?,?)";
					Data.Insert(insertPayUser, new Object[] { user.getId(), user.getUserName(), fundsType });
					pub = (PayUserBean) Data.GetOne("select * from Pay_User where userName=?", new Object[] { userName }, PayUserBean.class);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		return pub;
	}
	
	public Map<String, PayUserBean> getUserMap(String userIds, int fundsType) {
		return getUserMap(userIds, "userId", fundsType);
	}

	public Map<String, PayUserBean> getUserMap(String userIds, String column, int fundsType) {
		List<PayUserBean> list = getUsers(userIds, column, fundsType);
		
		Map<String, PayUserBean> maps = new HashMap<String, PayUserBean>();
		if (list != null && list.size() > 0) {
			for (Bean b : list) {
				PayUserBean ub = (PayUserBean) b;
				String uid = String.valueOf(ub.getUserId());
				if (maps.get(uid) == null) {
					maps.put(uid, ub);
				}
			}
		}
		return maps;
	}
	
	public List<PayUserBean> getUsersByName(String userNames, int fundsType) {
		return getUsers(userNames, "userName", fundsType);
	}
	
	public List<PayUserBean> getUsers(String userIds, String column, int fundsType) {
		List<PayUserBean> list = super.find("select * from Pay_User where "+column+" in ("
				+ userIds + ") AND fundsType = ?", new Object[] {fundsType}, PayUserBean.class);
		return list;
	}
	
	/***
	 * 获取用户的资金，包含借贷的参数
	 * @param userId 用户ID
	 */
	public Map<String, PayUserBean> getFundsLoanMap(String userId){
		List<PayUserBean> list = getFunds(userId);
		Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
		
		Map<String, PayUserBean> userMaps = new LinkedHashMap<String, PayUserBean>();
		for(Entry<String, CoinProps> entry : coinMap.entrySet()){
			boolean has = false;
			CoinProps coint = entry.getValue();
			for(PayUserBean payUser : list){
				payUser.setCoint(coint);
				if(coint.getFundsType() == payUser.getFundsType()){
					userMaps.put(coint.getStag(), payUser);
					has = true;
					break;
				}
			}
			if(!has){
				PayUserBean payUser = getById(Integer.parseInt(userId), coint.getFundsType());
				payUser.setCoint(coint);
				payUser.setBalance(BigDecimal.ZERO);
				payUser.setFreez(BigDecimal.ZERO);
				payUser.setTotal(BigDecimal.ZERO);
				payUser.setFundsType(coint.getFundsType());
				payUser.setInWait(BigDecimal.ZERO);
				payUser.setInSuccess(BigDecimal.ZERO);
				payUser.setOutWait(BigDecimal.ZERO);
				payUser.setOutSuccess(BigDecimal.ZERO);
				payUser.setOverdraft(BigDecimal.ZERO);
				payUser.setInterestOfDay(BigDecimal.ZERO);
				payUser.setWithdrawFreeze(BigDecimal.ZERO);
				
				userMaps.put(coint.getStag(), payUser);
			}
		}
		
		return userMaps;
	}








	/***
	 * 获取用户的资金，包含借贷的参数
	 * @param userMaps
	 */
	public JSONArray getFundsArray(Map<String, PayUserBean> userMaps){
		JSONArray funds = new JSONArray();
		Map<String, CoinProps> coinMap = DatabasesUtil.getCoinPropMaps();
		for(Entry<String, CoinProps> entry : coinMap.entrySet()){
			PayUserBean payUser = userMaps.get(entry.getKey());
			if (payUser != null) {
				CoinProps coint = entry.getValue();
				JSONObject obj = new JSONObject();
				obj.put("balance", payUser.getBalance());//可用余额
				obj.put("freeze", payUser.getFreez());//冻结余额
				obj.put("total", payUser.getTotal());//总金额
				obj.put("fundsType", payUser.getFundsType());//资金类型
				obj.put("unitTag", coint.getUnitTag());//符号
				obj.put("propTag", coint.getPropTag());//属性标签
				obj.put("coinFullNameEn", coint.getPropEnName());//币种全称
				obj.put("canCharge", coint.isCanCharge());
				obj.put("canWithdraw", coint.isCanWithdraw());
				obj.put("eventFreez", payUser.getEventFreez());

				funds.add(obj);
			}
		}
		
		return funds;
	}

	/**
	 * 获取用户某种资金类型数据
	 * @param userId
	 * @param fundsType
	 * @return
	 */
	public PayUserBean getByUserIdAndFundsType(String userId, int fundsType) {
		PayUserBean pub = (PayUserBean) Data.GetOne("select * from pay_user where userId=? AND fundsType = ?", new Object[]{userId, fundsType}, PayUserBean.class);
		if (null != pub) {
			pub.setCoint(DatabasesUtil.coinProps(fundsType));
		}
		return pub;
	}

	/**
	 * 获取用户低于某个价格的资金数据
	 * @param userId
	 * @param fundsType
	 * @param minBalance
	 * @return
	 */
	public PayUserBean getUserLessBalance(String userId, int fundsType, BigDecimal minBalance) {
		PayUserBean pub = (PayUserBean) Data.GetOne("select * from pay_user where userId = ? and fundsType = ? and balance+freez < ? ",
				new Object[]{userId, fundsType, minBalance}, PayUserBean.class);
		return pub;
	}

	public BigDecimal getBalance(int id, int fundsType) {
		BigDecimal balance = BigDecimal.ZERO;
		PayUserBean pub = (PayUserBean) Data.GetOne("select * from pay_user where userId=? AND fundsType = ?", new Object[] { id, fundsType }, PayUserBean.class);
		try {
			if (pub == null) {
				UserDao ud = new UserDao();
				User user = ud.get(String.valueOf(id));
				if (user != null && StringUtil.exist(user.getUserName())) {
					BigDecimal loanLimit = getLoanLimit(fundsType);
					//modify by xwz 用户注册时多次初始化，导致主键冲突，初始化语句加入ignore，防止主键冲突
					String insertPayUser = "insert ignore into Pay_User(userId,userName, fundsType,loanLimit) values(?,?,?,?)";
					Data.Insert(insertPayUser, new Object[] { id, user.getUserName()+"", fundsType, loanLimit });
					pub = (PayUserBean) Data.GetOne("select * from pay_user where userId=?", new Object[] { id }, PayUserBean.class);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		balance = pub.getBalance();
		return balance;
	}
}
