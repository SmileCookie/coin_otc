package com.wallet.eth.rpc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wallet.eth.bean.EtherBlock;
import com.wallet.eth.bean.EtherTransaction;
import com.wallet.eth.bean.EtherTransactionReceipt;
import com.wallet.eth.format.EtherHexFormatUtils;
import org.apache.log4j.Logger;

/**
 * rpc response msg
 * @author guosj
 */
public class RpcResponse {

	private static Logger log = Logger.getLogger(RpcResponse.class.getName());
	
	private JSONObject json;
	
	private int id;
	
	private Object result;
	
	private String message;
	
	private int code;
	
	
	private boolean isFinished;
	
	public static RpcResponse parseObject(int id, String callbackText){
		return new RpcResponse(id, callbackText);
	}
	
	public RpcResponse(int id, String callbackText){
		
		//校验返回值
		if(StringUtils.isEmpty(callbackText) || "0".equals(callbackText) || !callbackText.startsWith("{")){
			this.isFinished = false;
		}else{
			this.isFinished = true;
		}
		
		//解析
		JSONObject json = null;
		try {
			if(callbackText != null && callbackText.startsWith("{")){
				json = JSONObject.parseObject(callbackText);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		//校验提交成员跟返回id对比，跟请求时的不一致不做任何处理
		if(json == null || !json.containsKey("id") || json.getIntValue("id") != id){
			this.isFinished = false;
			return;
		}else{
			this.isFinished = true;
			this.id = id;
		}
		
		/**
		 * 校验返回的结果
		 */
		
		if(json.containsKey("error")){
			JSONObject errorJSON = json.getJSONObject("error");
			this.code = errorJSON.getIntValue("code");
			this.message = errorJSON.getString("message");
		}
		
		else if(json.containsKey("result")){
			this.result = json.get("result");
			this.code = 1;
		}
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public boolean isFinished() {
		return isFinished;
	}

	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public boolean resultTrue(){
		if(code == 1){
			return true;
		}else{
			return false;
		}
	}
	
	public String parseResultToString(){
		if(result == null){
			return null;
		}
		return result.toString();
	}
	
	public long parseResultToLong(){
		if(result == null){
			return 0l;
		}
		return EtherHexFormatUtils.hexToInteger(result.toString()).longValue();
	}
	
	public BigInteger parseResultToInteger(){
		if(result == null){
			return BigInteger.ZERO;
		}
		return EtherHexFormatUtils.hexToInteger(result.toString());
	}
	
	public boolean parseResultToBoolean(){
		if(result == null){
			return false;
		}
		return new Boolean(result.toString()).booleanValue();
	}
	
	public String[] parseResultToArray(){
		JSONArray ja = (JSONArray) result;
		String[] array = new String[ja.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = ja.getString(i);
		}
		return array;
	}
	
	public EtherTransactionReceipt parseResultToEtherTransactionReceipt(){
		if(result == null || !result.toString().startsWith("{")){
			return null;
		}
		
		return parseEtherTransactionReceipt(result.toString());
	}
	
	public EtherTransactionReceipt parseEtherTransactionReceipt(String jsonText){
		
		JSONObject json = JSONObject.parseObject(result.toString());
		
		EtherTransactionReceipt er = new EtherTransactionReceipt();
		
		if(json.containsKey("transactionHash")){
			er.setTransactionHash(json.getString("transactionHash"));
		}
		
		if(json.containsKey("transactionIndex")){
			if(json.get("transactionIndex") != null){
				er.setTransactionIndex(EtherHexFormatUtils.hexToInteger(json.getString("transactionIndex")));
			}
		}
		
		if(json.containsKey("blockNumber")){
			if(json.get("blockNumber") != null){
				er.setBlockNumber(EtherHexFormatUtils.hexToInteger(json.getString("blockNumber")));
			}
		}
		
		if(json.containsKey("blockHash")){
			er.setBlockHash(json.getString("blockHash"));
		}
		
		if(json.containsKey("contractAddress")){
			er.setContractAddress(json.getString("contractAddress"));
		}
		
		if(json.containsKey("from")){
			er.setFrom(json.getString("from"));
		}
		
		if(json.containsKey("to")){
			er.setTo(json.getString("to"));
		}
		
		if(json.containsKey("cumulativeGasUsed")){
			if(json.get("cumulativeGasUsed") != null){
				er.setCumulativeGasUsed(EtherHexFormatUtils.hexToInteger(json.getString("cumulativeGasUsed")));
			}
		}
		
		if(json.containsKey("gasUsed")){
			if(json.get("gasUsed") != null){
				er.setGasUsed(EtherHexFormatUtils.hexToInteger(json.getString("gasUsed")));
			}
		}
		
		if(json.containsKey("logs")){
			JSONArray logs = json.getJSONArray("logs");
			if( logs.size()>0 ){
				JSONObject logsItem = logs.getJSONObject(0);
				if(logsItem.containsKey("address") && logsItem.get("address")!=null ){
					er.setLogsAddress( logsItem.getString("address") );
				}
				if(logsItem.containsKey("blockHash") && logsItem.get("blockHash")!=null ){
					er.setLogsBlockHash( logsItem.getString("blockHash") );
				}
				if(logsItem.containsKey("blockNumber") && logsItem.get("blockNumber")!=null ){
					er.setLogsBlockNumber( logsItem.getString("blockNumber") );
				}
				if(logsItem.containsKey("data") && logsItem.get("data")!=null ){
					er.setLogsData( logsItem.getString("data") );
				}
				if(logsItem.containsKey("logIndex") && logsItem.get("logIndex")!=null ){
					er.setLogsIndex( logsItem.getString("logIndex") );
				}
				if(logsItem.containsKey("transactionHash") && logsItem.get("transactionHash")!=null ){
					er.setLogsTransactionHash( logsItem.getString("transactionHash") );
				}
				if(logsItem.containsKey("transactionIndex") && logsItem.get("transactionIndex")!=null ){
					er.setLogsTransactionIndex( logsItem.getString("transactionIndex") );
				}
				if(logsItem.containsKey("topics") && logsItem.get("topics")!=null ){
					JSONArray topics = logsItem.getJSONArray("topics");
					if(topics.size()>0){
						er.setLogsTxid( topics.getString(0) );
					}
					if(topics.size()>1){
						er.setLogsTxFromAddress( topics.getString(1) );
					}
					if(topics.size()>2){
						er.setLogsTxToAddress( topics.getString(2) );
					}
				}
			}
		}
		
		return er;
	}
	
	
	public EtherBlock parseResultToEtherBlock(){
		if(result == null || !result.toString().startsWith("{")){
			return null;
		}
		
		return parseEtherBlock(result.toString());
	}
	
	public EtherBlock parseEtherBlock(String jsonText){
		
		JSONObject json = JSONObject.parseObject(jsonText);
		
		EtherBlock eb = new EtherBlock();
		
		if(json.containsKey("number")){
			if(json.get("number") != null){
				eb.setNumber(EtherHexFormatUtils.hexToInteger(json.getString("number")));
			}
		}
		
		if(json.containsKey("hash")){
			eb.setHash(json.getString("hash"));
		}
		
		if(json.containsKey("parentHash")){
			eb.setParentHash(json.getString("parentHash"));
		}
		
		if(json.containsKey("nonce")){
			eb.setNonce(json.getString("nonce"));
		}
		
		if(json.containsKey("sha3Uncles")){
			eb.setSha3Uncles(json.getString("sha3Uncles"));
		}
		
		if(json.containsKey("logsBloom")){
			eb.setLogsBloom(json.getString("logsBloom"));
		}
		
		if(json.containsKey("transactionsRoot")){
			eb.setTransactionsRoot(json.getString("transactionsRoot"));
		}
		
		if(json.containsKey("stateRoot")){
			eb.setStateRoot(json.getString("stateRoot"));
		}
		
		if(json.containsKey("receiptsRoot")){
			eb.setReceiptsRoot(json.getString("receiptsRoot"));
		}
		
		if(json.containsKey("miner")){
			eb.setMiner(json.getString("miner"));
		}
		
		if(json.containsKey("difficulty")){
			eb.setDifficulty(EtherHexFormatUtils.hexToInteger(json.getString("difficulty")));
		}
		
		if(json.containsKey("totalDifficulty")){
			if(json.get("totalDifficulty") != null){
				eb.setTotalDifficulty(EtherHexFormatUtils.hexToInteger(json.getString("totalDifficulty")));
			}
		}
		
		if(json.containsKey("extraData")){
			eb.setExtraData(json.getString("extraData"));
		}
		
		if(json.containsKey("size")){
			if(json.get("size") != null){
				eb.setSize(EtherHexFormatUtils.hexToInteger(json.getString("size")));
			}
		}
		
		if(json.containsKey("gasLimit")){
			if(json.get("gasLimit") != null){
				eb.setGasLimit(EtherHexFormatUtils.hexToInteger(json.getString("gasLimit")));
			}
		}
		
		if(json.containsKey("gasUsed")){
			if(json.get("gasUsed") != null){
				eb.setGasUsed(EtherHexFormatUtils.hexToInteger(json.getString("gasUsed")));
			}
		}
		
		if(json.containsKey("timestamp")){
			if(json.get("timestamp") != null){
				eb.setTimestamp(EtherHexFormatUtils.hexToInteger(json.getString("timestamp")));
			}
		}
		
		if(json.containsKey("transactions") && json.get("transactions") != null){
			JSONArray array = json.getJSONArray("transactions");
			List<EtherTransaction> lists = new ArrayList<EtherTransaction>();
			for (Object object : array) {
				if(object != null){
					lists.add(parseEtherTransaction(object.toString()));
				}
			}
			eb.setTransactions(lists);
		}
		
		if(json.containsKey("uncles") && json.get("uncles") != null){
			JSONArray array = json.getJSONArray("uncles");
			String[] uncles = new String[array.size()];
			for (int i = 0; i < array.size(); i++) {
				uncles[i] = array.getString(i);
			}
			eb.setUncles(uncles);
		}
		
		return eb;
	}
	
	
	public EtherTransaction parseResultToEtherTransaction(){
		if(result == null || !result.toString().startsWith("{")){
			return null;
		}
//		log.info(result.toString());
		return parseEtherTransaction(result.toString());
	}
	
	public EtherTransaction parseEtherTransaction(String jsonText){
		
		JSONObject json = JSONObject.parseObject(jsonText);
		
		EtherTransaction et = new EtherTransaction();
		
		if(json.containsKey("hash")){
			et.setHash(json.getString("hash"));
		}
		
		if(json.containsKey("nonce")){
			if(json.get("nonce") != null){
				et.setNonce(EtherHexFormatUtils.hexToInteger(json.getString("nonce")));
			}
		}
		
		if(json.containsKey("blockHash")){
			et.setBlockHash(json.getString("blockHash"));
		}
		
		if(json.containsKey("blockNumber")){
			if(json.get("blockNumber") != null){
				et.setBlockNumber(EtherHexFormatUtils.hexToInteger(json.getString("blockNumber")));
			}
		}
		
		if(json.containsKey("transactionIndex")){
			if(json.get("transactionIndex") != null){
				et.setTransactionIndex(EtherHexFormatUtils.hexToInteger(json.getString("transactionIndex")));
			}
		}
		
		if(json.containsKey("from")){
			et.setFrom(json.getString("from"));
		}
		
		if(json.containsKey("to")){
			et.setTo(json.getString("to"));
		}
		
		if(json.containsKey("value")){
			if(json.get("value") != null){
				et.setValue(EtherHexFormatUtils.hexToInteger(json.getString("value")));
			}
		}

		if(json.containsKey("gasPrice")){
			if(json.get("gasPrice") != null){
				et.setGasPrice(EtherHexFormatUtils.hexToInteger(json.getString("gasPrice")));
			}
		}
		
		if(json.containsKey("gas")){
			if(json.get("gas") != null){
				et.setGas(EtherHexFormatUtils.hexToInteger(json.getString("gas")));
			}
		}
		
		if(json.containsKey("input")){
			et.setInput(json.getString("input"));
		}
		
		return et;
	}
	
}
