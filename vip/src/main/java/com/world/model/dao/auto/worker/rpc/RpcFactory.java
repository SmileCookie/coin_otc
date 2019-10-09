package com.world.model.dao.auto.worker.rpc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.world.data.mysql.Data;
import com.world.data.mysql.OneSql;
import com.world.model.dao.pay.DetailsDao;
import com.world.model.dao.pay.DownloadDao;
import com.world.model.dao.pay.KeyDao;
import com.world.model.entity.coin.CoinProps;
import com.world.model.entity.pay.DetailsBean;
import com.world.model.entity.pay.DownloadBean;
import com.world.model.entity.pay.KeyBean;
import com.world.util.rpc.RpcFramework;

/**
 * 用于连接钱包更新记录
 * 
 */
public class RpcFactory {
	protected static Logger log = Logger.getLogger(RpcFactory.class.getName());
	private KeyDao keyDao = new KeyDao();
	private DownloadDao downloadDao = new DownloadDao();
	private DetailsDao detailsDao = new DetailsDao();
	
	private static String RECEIVE_TYPE = "_receive_";
	private static String SEND_TYPE    = "_send_";
	
	RpcFramework framework = new RpcFramework();
	
	private String receive_passphrase;
	private String passphrase;
	private String walletName;
	
	private CoinProps coint;
	
	public RpcFactory() {
		super();
	}
	
	public RpcFactory(CoinProps coint, String receive_passphrase, String passphrase, String walletName){
		super();
		this.coint = coint;
		this.receive_passphrase = receive_passphrase;
		this.passphrase = passphrase;
		this.walletName = walletName;
	}
    
    /**
     * 处理用户提币
     * 更新狗币提币信息
     *
     * @return
     */
    public boolean updateDownload(int count){
    	downloadDao.setDatabase(coint.getOutDatabaseName());
    	try {
			//1、查询出所有的btcDownload表中状态为0的提币记录
    		List<DownloadBean> list = downloadDao.findByStatus(0, count);
    		if(list != null && list.size() > 0){
    			Map<String, BigDecimal> legalMap = new HashMap<String, BigDecimal>();//合法且唯一的地址保存map
    			List<DownloadBean> sameList = new ArrayList<DownloadBean>();//具有相同充值地址的记录
    			
    			String ids = "",firstIds = "";
    			BigDecimal totalAmount = BigDecimal.ZERO;//统计总共需要打币的总额
    			List<OneSql> sqls = new ArrayList<OneSql>();
    			//1、首先将list中的记录状态全部更改成失败
    			for(DownloadBean db : list){
    				firstIds += "," + db.getId();
    				totalAmount = totalAmount.add(db.getAfterAmount());
    			}
    			
    			//获得钱包总资产
				BigDecimal balance = getBalance();
				//如果钱包总余额小于需打币数，则btcDownload 状态仍为0 ：等待处理中
				if(balance.compareTo(totalAmount) < 0){
					log.info(coint.getTag()+"钱包余额不足");
					return false;
				}
    			
    			firstIds = firstIds.substring(1);
    			sqls.add(downloadDao.updateStatusByIds(firstIds, 5));
    			//执行更改状态为4
    			boolean result = Data.doTrans(sqls);
    			if(result){
    				sqls.clear();
    				//2、遍历list，根据实际情况更改btcDownload
    				long t1 = System.currentTimeMillis();
    				for(DownloadBean bdl : list){
    					//验证地址是否非法，如果非法，则更改提币记录状态为失败
    					if(!validateAddressIsWrong(bdl.getToAddress())){
    						sqls.add(downloadDao.updateStatusByIds(String.valueOf(bdl.getId()), 4));//失败
    					} else {//地址合法
    						BigDecimal amount = legalMap.get(bdl.getToAddress());
    						BigDecimal temp = bdl.getAfterAmount();
    						//amount为空，表明不存在
    						if(amount == null){
    							legalMap.put(bdl.getToAddress(), temp);
    							ids += "," + bdl.getId(); 
    						} else {
    							sameList.add(bdl);//存在相同的提币地址的情况，将该重复的存放到sameList中
    						}
    					}
    				}
    				long t2 = System.currentTimeMillis();
    				log.info("验证地址耗时："+(t2-t1)+" ms.");
    				//解锁
    				walletpassphrase(passphrase, SEND_TYPE);
    				if(sameList.size() > 0){
    					//相同地址的处理
    					sqls.addAll(updateDownloadSameAddress(sameList));
    				}
    				if(ids.length() > 0){
    					//一般的，不相同地址且合法的
    					sqls.addAll(updateDownloadNormal(legalMap, ids, balance, totalAmount));
    				}
    			}
    			//钱包加锁
    			walletlock(SEND_TYPE);
    			//遍历，
    			//持久化操作
    			return Data.doTrans(sqls);
    		}
		} catch (Exception e) {
			log.error(e.toString(), e);
		} 
    	
    	return false;
    }
    
    
    /**
     * 验证地址是否是非法的
     *
     * @param amount
     * @return
     */
    public boolean validateAddressIsWrong(String address){
    	boolean valid = false;
    	try {
    		String param = "{\"jsonrpc\": \"1.0\", \"id\":\"curltest\", \"method\": \"validateaddress\", \"params\": [\"" + address + "\"]}";
    		String result = framework.callJson(coint, SEND_TYPE, param);
    		JSONObject json = JSONObject.parseObject(result);
    		valid = json.getJSONObject("result").getBooleanValue("isvalid");
		} catch (Exception e) {
			log.error("验证钱包地址[" + address + "]不是合法地址！！", e);
			valid = false;
		}
		
		return valid;
    }
    
    /**
     * 钱包解锁
     * @param passphrase
     * @return
     */
    public boolean walletpassphrase(String passphrase, String wallet){
    	boolean valid = true;
    	try {
    		String param = "{\"jsonrpc\": \"1.0\", \"id\":\"curltest\", \"method\": \"walletpassphrase\", \"params\": [\"" + passphrase + "\", 60]}";
    		framework.callJson(coint, wallet, param);
		} catch (Exception e) {
			log.error("钱包解锁失败。", e);
			valid = false;
		}
		
		return valid;
    }

    /**
     * 钱包加锁
     * @return
     */
    public boolean walletlock(String wallet){
    	boolean valid = true;
    	try {
    		String param = "{\"jsonrpc\": \"1.0\", \"id\":\"curltest\", \"method\": \"walletlock\", \"params\": []}";
    		framework.callJson(coint, wallet, param);
    	} catch (Exception e) {
			log.error("钱包加锁失败。", e);
    		valid = false;
    	}
    	
    	return valid;
    }
    
    /**
     * 提币：处理相同接收地址的
     * <p>注：相同地址需要逐条发币
     *
     * @return
     */
    public List<OneSql> updateDownloadSameAddress(List<DownloadBean> list){
    	List<OneSql> sqls = new ArrayList<OneSql>();
		String failIds = "";//发送失败与成功的对应的提笔记录id
		int failSize = 0;
    	for(DownloadBean bdl : list){
    		BigDecimal amount = bdl.getAfterAmount();
    		String param = "{\"jsonrpc\": \"1.0\", \"id\":\"curltest\", \"method\": \"sendfrom\", \"params\": [\"\", \""+bdl.getToAddress()+"\", "+amount.toString()+"] }";
    		String result = framework.callJson(coint, SEND_TYPE, param);
    		if("0".equals(result)){
    			failIds += "," + bdl.getId();
    			failSize++;
    		} else {
    			JSONObject json = JSONObject.parseObject(result);
    			String txid = json.getString("result");
    			BigDecimal realFee = getTransFeeByTxid(txid);
    			sqls.add(downloadDao.updateDownloadByIds(String.valueOf(bdl.getId()), 6, getBalance(), txid));
    			sqls.add(downloadDao.updateDownloadFirstId(bdl.getId(), realFee));
    			log.info(coint.getTag()+"成功打出一笔记录，手续费："+realFee+"。");
    		}
    	}
    	//最后在更新发币失败的
    	if(failSize > 0){
    		failIds = failIds.substring(1);
    		sqls.add(downloadDao.updateStatusByIds(failIds, 0));
    		log.info(coint.getTag()+"有几笔记录打币失败，ids：+"+failIds+"。");
    	}
    	return sqls;
    }
    
    
    /**
     * 批量发币的
     *
     * @param legalMap
     * @param ids
     * @return
     */
    public List<OneSql> updateDownloadNormal(Map<String, BigDecimal> legalMap, String ids, BigDecimal balance, BigDecimal totalAmount){
    	List<OneSql> sqls = new ArrayList<OneSql>();
    	String addressAmount = "{";
    	//拼接address:amount json格式参数
    	int firstId = 0;
		for(String address : legalMap.keySet()){
			addressAmount += ",\"" + address + "\":" + legalMap.get(address);
		}
		/*此处为了处理找零，把打币后钱包的余额转移到打币地址*/
		/*if(coint.getStag().equals("btc")){
			BigDecimal needBackMoney = balance.subtract(totalAmount);
			addressAmount += ",\"" + HOT_RECEIVE + "\":" + needBackMoney.subtract(DigitalUtil.getBigDecimal("0.1"));
		}*/
		addressAmount = addressAmount.replaceFirst(",", "") + "}";
		ids = ids.substring(1);
//		if(ids.indexOf(",") > 0){
			firstId = Integer.parseInt(ids.split("\\,")[0]);
//		}
		String param = "{\"jsonrpc\": \"1.0\", \"id\":\"curltest\", \"method\": \"sendmany\", \"params\": [\"\", " + addressAmount + "] }";
		//请求API，打币
		String result = framework.callJson(coint, SEND_TYPE, param);
		if("0".equals(result)){
			sqls.add(downloadDao.updateStatusByIds(ids, 0));
			log.info(coint.getTag()+"RPC调用失败，打币失败。");
		} else {
			JSONObject json = JSONObject.parseObject(result);
			String txid = json.getString("result");
			BigDecimal realFee = getTransFeeByTxid(txid);//获得交易费用
			//根据 返回的hash值，查询得交交易记录
			sqls.add(downloadDao.updateDownloadByIds(ids, 6, getBalance(), txid));
			sqls.add(downloadDao.updateDownloadFirstId(firstId, realFee));
			log.info(coint.getTag()+"成功打出一批记录，手续费："+realFee+"。");
		}
    	return sqls;
    }
    
    /**
     * 获得交易费用
     *
     * @param txid
     * @return
     */
    public BigDecimal getTransFeeByTxid(String txid){
		String result3 = framework.callJson(coint, SEND_TYPE, "{\"jsonrpc\":\"1.0\",\"id\":\"curltest\",\"method\":\"gettransaction\",\"params\":[\""+txid+"\"]}");
		JSONObject json3 = JSONObject.parseObject(result3).getJSONObject("result");
		return json3.getBigDecimal("fee").abs();
    }
    
    /**
     * 获取钱包余额
     *
     * @return
     */
    public BigDecimal getBalance(){
    	String result0 = framework.callJson(coint, SEND_TYPE, "{\"jsonrpc\":\"1.0\",\"id\":\"curltest\",\"method\":\"getbalance\",\"params\":[]}");
		JSONObject json0 = JSONObject.parseObject(result0);
		//获得钱包总资产
		BigDecimal balance =  (BigDecimal) json0.get("result");
		return balance;
    }
    
    /**
     * 获取钱包余额
     *
     * @date 2015-12-7
     * @author 币网-刘刚
     *
     * @return
     */
    public boolean setTxFee(BigDecimal fees){
    	String result0 = framework.callJson(coint, SEND_TYPE, "{\"jsonrpc\":\"1.0\",\"id\":\"curltest\",\"method\":\"settxfee\",\"params\":["+fees+"]}");
		JSONObject json0 = JSONObject.parseObject(result0);
		//获得钱包总资产
		return Boolean.parseBoolean(json0.getString("result"));
    }
    
    /**
     * 保存钱包地址 通过RPC获取钱包地址保存到数据库
     * @param count 需要新建地址的个数
     *
     */
	public boolean saveAddresss(int count){
    	if(count <= 0) return false;
    	keyDao.setCoint(coint);
    	
    	List<KeyBean> list = keyDao.find("SELECT * FROM "+keyDao.getTableName()+" WHERE userId = 0 AND tag = 0", null, KeyBean.class);
    	if(list.size() > 0){
    		log.info(coint.getTag()+"还有未使用的充值地址，不用新生成。");
    		return false;
    	}

    	String param = "{\"jsonrpc\" : \"1.0\", \"id\" : \"curltest\", \"method\" : \"getnewaddress\", \"params\" : []}";
    	
    	//解锁
		walletpassphrase(receive_passphrase, RECEIVE_TYPE);
    	List<OneSql> sqls = new ArrayList<OneSql>();
    	try {
    		for(int index = 0; index < count; index++){
    			String result = framework.callJson(coint, RECEIVE_TYPE, param);
    			if(result.startsWith("{")){
    				JSONObject json = JSONObject.parseObject(result);
    				sqls.add(keyDao.saveAddress(String.valueOf(json.getString("result")), walletName, "0"));
    			}
    		}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
    	walletlock(RECEIVE_TYPE);
    	return Data.doTrans(sqls);
    }
    
	/**
	 * 保存交易记录
	 * 注：该方法只保存表中还没有交易记录
	 *
	 * @param count
	 * @return
	 */
	public boolean saveTransactions(int count){
		detailsDao.setCoint(coint);
    	try {
    		String param = "{\"jsonrpc\" : \"1.0\", \"id\" : \"curltest\", \"method\" : \"listtransactions\", \"params\" : [\"*\", "+count+"]}";
    		//获得交易数据
    		String result = framework.callJson(coint, RECEIVE_TYPE, param);
    		if("0".equals(result)){
				log.info(coint.getTag()+"RPC调用失败，保存充值记录失败。");
				return false;
			}
    		JSONObject json = JSONObject.parseObject(result);
    		if(json != null){
    			JSONArray array = json.getJSONArray("result");
    			if(array.size() == 0){
    				log.info("还没有交易发生。");
    				return false;
    			}
    			//得到最早那条交易记录，也就是第一条交易记录
    			JSONObject firstJson = array.getJSONObject(0);
    			//得到第一条交易记录的发送时间
    			Timestamp sendTime = new Timestamp((firstJson.getLongValue("time")-60) * 1000);
    			//查询交易表（btccharge）中发送时间大于等于这个发送时间的记录
    			Map<String, Long> map = detailsDao.getChargeMapByTime(walletName, sendTime);
    			List<OneSql> sqls = new ArrayList<OneSql>();
    			//遍历map和array，只有map中不存在array中的相同hash值的记录，就进行插入操作
    			for(int index = 0; index < array.size(); index++){
    				JSONObject obj = array.getJSONObject(index);
    				String category = obj.getString("category");//获得交易类型，接受或发送
    				if("receive".equals(category) || "generate".equals(category)){
    					//map中不存在blockhash值，则将该交易记录插入btccharge表
    					String txid = obj.getString("txid");
    					if(map.get(txid) == null){
	    					DetailsBean charge = new DetailsBean();
	    					charge.setConfirmTimes(obj.getIntValue("confirmations"));//确认次数
	    					//得到blockhash值
	    					charge.setAddHash(txid);//记录发送或接受时的hash值
							BigDecimal amount = obj.getBigDecimal("amount");
	    					charge.setAmount(amount);
	    					charge.setToAddr(obj.getString("address"));//币接受地址
	    					charge.setFromAddr("");//币发送地址
	    					charge.setStatus(200);//初始状态为200
	    					charge.setWallet(walletName);
	    					charge.setSendTime(new Timestamp(obj.getLong("time")*1000));//发送时间
	    					log.info(obj.getLongValue("blocktime"));
	    					log.info("插入一条"+coint.getTag()+"充值记录txid："+txid);
    						sqls.add(detailsDao.saveOne(charge));
    					} 
    				}
    			}
    			return Data.doTrans(sqls);
    		}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
    	return false;
	}
	
	/**
	 * 将充值表中的状态为200的记录，根据rpc接口得到确认次数，更新状态、确认次数
	 *
	 * @date 2015-12-10
	 * @author 币网-刘刚
	 *
	 * @return
	 */
	public boolean updateTransactions(int count){
		//设置币种类为DOGE（狗币）
		detailsDao.setCoint(coint);
    	//查询获得状态为200的记录
    	List<DetailsBean> list = detailsDao.getChargeList(walletName, count);
    	if(list.size() > 0){
    		//解锁
    		List<OneSql> sqls = new ArrayList<OneSql>();
    		for(DetailsBean charge : list){
    			String blockhash = charge.getAddHash();
    			//替换参数
    			String params = "{\"jsonrpc\" : \"1.0\", \"id\" : \"curltest\", \"method\" : \"gettransaction\", \"params\" : [\""+blockhash+"\"]}";
    			String result = framework.callJson(coint, RECEIVE_TYPE, params);
    			if("0".equals(result)){
    				log.info(coint.getTag()+"RPC调用失败，更新充值记录失败。");
    				return false;
    			}
    			JSONObject json = JSONObject.parseObject(result);
    			//根据blockhash值调用钱包rpc接口，得到了结果json不为null，表明存在该hash值的交易记录，可以得到确认次数，进行更新
    			if(json != null){
    				JSONObject obj = json.getJSONObject("result");
    				charge.setConfirmTimes(obj.getIntValue("confirmations"));
    				charge.setConfigTime(new Timestamp(obj.getLongValue("blocktime")*1000));
    				sqls.add(detailsDao.updateOne(charge));
    			}
    		}
    		return Data.doTrans(sqls);
    	}
    	
		return false;
	}

	public CoinProps getCoint() {
		return coint;
	}

	public void setCoint(CoinProps coint) {
		this.coint = coint;
	}
	
}
