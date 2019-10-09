package com.wallet.eth;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import org.apache.log4j.Logger;

import com.wallet.eth.api.EtherWalletApi;
import com.wallet.eth.bean.EtherBlock;
import com.wallet.eth.bean.EtherBlockStatus;
import com.wallet.eth.bean.EtherTransaction;
import com.wallet.eth.bean.EtherTransactionReceipt;
import com.wallet.eth.format.EtherHexFormatUtils;
import com.wallet.eth.rpc.RpcFramework;
import com.wallet.eth.rpc.RpcResponse;
import com.world.util.DigitalUtil;

/**
 * Ethereum Wallet JSON PRC API
 * 
 * https://ethereum.gitbooks.io/frontier-guide/content/rpc.html
 * 
 * @author guosj
 */
public class EtherWalletApiFactory implements EtherWalletApi {
	
	public static Logger log = Logger.getLogger(EtherWalletApiFactory.class.getName());
	
	private String ip;
	
	private String port;
	
	private static String rpcUser = "root";
	
	private static String rpcPassword = "root";
	
	public EtherWalletApiFactory(String rpcIp, String rpcPort, String rpcuser, String rpcpassword){
		ip = rpcIp;
		port = rpcPort;
		rpcUser = rpcuser;
		rpcPassword = rpcpassword;
	}
	
	public static EtherWalletApiFactory getInstance(String rpcIp, String rpcPort){
		return new EtherWalletApiFactory(rpcIp, rpcPort, rpcUser, rpcPassword);
	}

	
	public boolean net_listening() {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"net_listening\",\"params\":[],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToBoolean();
			}else{
				log.info("Call ehter rpc method net_listening error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return false;
	}

	public String[] eth_accounts() {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_accounts\",\"params\":[],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToArray();
			}else{
				log.info("Call ehter rpc method eth_accounts error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}

	public long eth_blockNumber() {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_blockNumber\",\"params\":[],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				String hexString = response.parseResultToString();
				return EtherHexFormatUtils.hexToInteger(hexString).longValue();
			}else{
				log.info("Call ehter rpc method eth_blockNumber error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return 0l;
	}

	/**
	 * 查询当前钱包的gas是多少
	 * @return
	 */
	public BigDecimal eth_gasPrice(){
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_gasPrice\",\"params\":[],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				String hexString = response.parseResultToString();
				return EtherHexFormatUtils.numberFormat(hexString);
			}else{
				log.info("Call ehter rpc method eth_getBalance error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return BigDecimal.ZERO;
	}
	
	public BigDecimal eth_getBalance(String address, String quantityOrTag) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\":[\""+address+"\", \""+quantityOrTag+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				String hexString = response.parseResultToString();
				return EtherHexFormatUtils.numberFormat(hexString);
			}else{
				log.info("Call ehter rpc method eth_getBalance error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return BigDecimal.ZERO;
	}
	
	public long eth_getTransactionCount(String address, String quantityOrTag) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\":[\""+address+"\", \""+quantityOrTag+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				String hexString = response.parseResultToString();
				return EtherHexFormatUtils.hexToInteger(hexString).longValue();
			}else{
				log.info("Call ehter rpc method eth_getTransactionCount error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return 0l;
	}

	public String eth_sendTransaction(String fromAddress, String toAddress,
			BigDecimal amount) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendTransaction\",\"params\":[{\"from\": \""+fromAddress+"\", \"to\": \""+toAddress+"\", \"value\": \""+EtherHexFormatUtils.toHexString(amount)+"\"}],\"id\":"+id+"}";
		log.info(ip+":"+port+"，eth_sendTransaction params = " + params);
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		log.info("eth_sendTransaction callbackText =" + callbackText);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToString();
			}else{
				log.info("Call ehter rpc method eth_sendTransaction error，code："+response.getCode()+"，message："+response.getMessage());
				return "{\"code\": \""+response.getCode()+"\", \"message\": \""+response.getMessage()+"\"}";
			}
		}
		return null;
	}
	
	public String eth_sendTransaction(String fromAddress, String toAddress,
			String hexString) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendTransaction\",\"params\":[{\"from\": \""+fromAddress+"\", \"to\": \""+toAddress+"\", \"value\": \""+hexString+"\"}],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToString();
			}else{
				log.info("Call ehter rpc method eth_sendTransaction error，code："+response.getCode()+"，message："+response.getMessage());
				return "{\"code\": \""+response.getCode()+"\", \"message\": \""+response.getMessage()+"\"}";
			}
		}
		return null;
	}

	public EtherBlock eth_getBlockByHash(String hash, boolean flag) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByHash\",\"params\":[\""+hash+"\", "+flag+"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToEtherBlock();
			}else{
				log.info("Call ehter rpc method eth_getBlockByHash error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}

	public EtherBlock eth_getBlockByNumber(String quantityOrTag, boolean flag) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBlockByNumber\",\"params\":[\""+quantityOrTag+"\", "+flag+"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToEtherBlock();
			}else{
				log.info("Call ehter rpc method eth_getBlockByNumber error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}
	
	
	public EtherTransaction eth_getTransactionByHash(String hash) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionByHash\",\"params\":[\""+hash+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		
//		log.info("eth_getTransactionByHash callbackText = " + callbackText);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToEtherTransaction();
				//return response.parseResultToEtherTransaction();
			}else{
				log.info("Call ehter rpc method eth_getTransactionByHash error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		
		return null;
	}

	public Object[] eth_getTransactionByHashNew(String hash) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionByHash\",\"params\":[\""+hash+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		
//		log.info("eth_getTransactionByHash callbackText = " + callbackText);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		
		int code = callbackText.equals("404") ? 0 : 1;//404代表超时或者没连上
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return new Object[]{code, response.parseResultToEtherTransaction()};
				//return response.parseResultToEtherTransaction();
			}else{
				log.info("Call ehter rpc method eth_getTransactionByHash error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		
		return new Object[]{code, null};
	}

	public EtherTransaction eth_getTransactionByBlockHashAndIndex(String hash, long index) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionByBlockHashAndIndex\",\"params\":[\""+hash+"\", \""+EtherHexFormatUtils.integerToHex(BigInteger.valueOf(index))+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToEtherTransaction();
			}else{
				log.info("Call ehter rpc method eth_getTransactionByBlockHashAndIndex error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}

	public EtherTransaction eth_getTransactionByBlockNumberAndIndex(String quantityOrTag,
			long index) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionByBlockNumberAndIndex\",\"params\":[\""+quantityOrTag+"\", \""+EtherHexFormatUtils.integerToHex(BigInteger.valueOf(index))+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToEtherTransaction();
			}else{
				log.info("Call ehter rpc method eth_getTransactionByBlockNumberAndIndex error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}

	public EtherTransactionReceipt eth_getTransactionReceipt(String hash) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionReceipt\",\"params\":[\""+hash+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
//		log.info("eth_getTransactionReceipt callbackText = " + callbackText);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToEtherTransactionReceipt();
			}else{
				log.info("Call ehter rpc method eth_getTransactionReceipt error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}

	public String eth_newBlockFilter() {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_newBlockFilter\",\"params\":[],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToInteger().toString();
			}else{
				log.info("Call ehter rpc method eth_newBlockFilter error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}

	public String eth_newPendingTransactionFilter() {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_newPendingTransactionFilter\",\"params\":[],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToInteger().toString();
			}else{
				log.info("Call ehter rpc method eth_newPendingTransactionFilter error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}

	public boolean eth_uninstallFilter(String filter) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_uninstallFilter\",\"params\":[\""+EtherHexFormatUtils.integerToHex(new BigInteger(filter))+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToBoolean();
			}else{
				log.info("Call ehter rpc method eth_uninstallFilter error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return false;
	}

	public String[] eth_getFilterChanges(String filter) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getFilterChanges\",\"params\":[\""+EtherHexFormatUtils.integerToHex(new BigInteger(filter))+"\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
//		log.info("eth_getFilterChanges callbackText = " + callbackText);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToArray();
			}else{
				log.info("Call ehter rpc method eth_getFilterChanges error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		return null;
	}
	
	/***
	 * {"jsonrpc":"2.0","method":"eth_call","params":[{"to":"0xbb9bc244d798123fde783fcc1c72d3bb8c189413","data":"0x70a08231000000000000000000000000AD586CDAD8BAa96C22EF316e1d6B8F2176fAb422"},"latest"],"id":15877}:
		Name	
		Method
		Status	
		Type
		Initiator
		Size	
		Time	
	 * @return
	 */
	
	
	public BigDecimal eth_call(String currency, String contractAddress, String userAddress) {
		// TODO Auto-generated method stub
		int id = getRandomRpcId();
		//String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_call\",\"params\":[\"{\"to\":\""+contractAddress+"\",\"data\":\"0x70a08231000000000000000000000000"+userAddress.toLowerCase()+"\"},\"latest\"],\"id\":"+id+"}";
		
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_call\",\"params\":[{\"to\":\""+contractAddress+"\",\"data\":\"0x70a08231000000000000000000000000"+userAddress.toLowerCase()+"\"},\"pending\"],\"id\":"+id+"}";
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		
		log.info("eth_call callbackText = " + callbackText);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		
		
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				String hexString = response.parseResultToString();
				if("DGD".equals(currency)){
					return EtherHexFormatUtils.getDGD(hexString);
				}else if("DAO".equals(currency)){
					return EtherHexFormatUtils.getDAO(hexString);
				}
			
			}else{
				log.info("Call ehter rpc method eth_getFilterChanges error，code："+response.getCode()+"，message："+response.getMessage());
			}
		}
		
		log.info(response.getCode());
		return null;
	}
	
	public String eth_sendTransactionDGD(String fromAddress, String contractAddress, String toAddress, BigDecimal amount) {
		// TODO Auto-generated method stub, \"value\": \""+EtherHexFormatUtils.toHexString(amount)+"\"
		int id = getRandomRpcId();
		String data = "0xa9059cbb000000000000000000000000" + toAddress.replaceAll("0x", "") + EtherHexFormatUtils.toDGDHexString(amount);
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendTransaction\",\"params\":[{\"data\": \"" + data + "\","
				+ "\"from\": \""+fromAddress+"\", \"to\": \""+contractAddress+"\"}],\"id\":"+id+"}";
		log.info("eth_sendTransactionDGD params = " + params);
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToString();
			}else{
				log.info("Call ehter rpc method eth_sendTransaction error，code："+response.getCode()+"，message："+response.getMessage());
				return "{\"code\": \""+response.getCode()+"\", \"message\": \""+response.getMessage()+"\"}";
			}
		}
		return null;
	}
	
	public String eth_sendTransactionDAO(String fromAddress, String contractAddress, String toAddress, BigDecimal amount) {
		// TODO Auto-generated method stub, \"value\": \""+EtherHexFormatUtils.toHexString(amount)+"\"
		int id = getRandomRpcId();
		String data = "0xa9059cbb000000000000000000000000" + toAddress.replaceAll("0x", "") + EtherHexFormatUtils.toDAOHexString(amount);
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendTransaction\",\"params\":[{\"data\": \"" + data + "\","
				+ "\"from\": \""+fromAddress+"\", \"to\": \""+contractAddress+"\"}],\"id\":"+id+"}";
		log.info("eth_sendTransactionDAO params = " + params);
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		log.info("eth_sendTransactionDAO callbackText = " + callbackText);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToString();
			}else{
				log.info("Call ehter rpc method eth_sendTransaction error，code："+response.getCode()+"，message："+response.getMessage());
				return "{\"code\": \""+response.getCode()+"\", \"message\": \""+response.getMessage()+"\"}";
			}
		}
		return null;
	}
	
	/**
	 * 代币的提币操作
	 * @param fromAddress ETH的打币地址
	 * @param toAddress	代币的接收地址
	 * @param contractAddress	代币的合约地址
	 * @param prefix	代币data的前缀
	 * @param amount	金额
	 * @return
	 */
	public String eth_sendTransaction(String fromAddress, String toAddress, String contractAddress, String prefix, BigDecimal amount) {
		// TODO Auto-generated method stub, \"value\": \""+EtherHexFormatUtils.toHexString(amount)+"\"
		int id = getRandomRpcId();
		String data = prefix + toAddress.replaceAll("0x", "") + EtherHexFormatUtils.toDGDHexString(amount);
		String params = "{\"jsonrpc\":\"2.0\",\"method\":\"eth_sendTransaction\",\"params\":[{\"data\": \"" + data + "\","
				+ "\"from\": \""+fromAddress+"\", \"to\": \""+contractAddress+"\"}],\"id\":"+id+"}";
		log.info("eth_sendTransactionDGD params = " + params);
		String callbackText = RpcFramework.callRpcJson("http://" + ip + ":" + port, rpcUser, rpcPassword, params);
		RpcResponse response = RpcResponse.parseObject(id, callbackText);
		//是否正确请求并返回
		if(response.isFinished()){
			if(response.resultTrue()){
				return response.parseResultToString();
			}else{
				log.info("Call ehter rpc method eth_sendTransaction error，code："+response.getCode()+"，message："+response.getMessage());
				return "{\"code\": \""+response.getCode()+"\", \"message\": \""+response.getMessage()+"\"}";
			}
		}
		return null;
	}
	
	public int getRandomRpcId(){
		Random r = new Random();
		return r.nextInt(100);
	}
	
	public static void main(String[] args) {
		
//		//旧链
//		String ip = "192.168.5.94";
//		String port = "9357";
//		String user = "user";
//		String password = "password";
		
//		//新莲
//		String ip = "192.168.4.21";
//		String port = "9997";
//		String user = "user";
//		String password = "password";
		
//		EtherWalletApiFactory api = new EtherWalletApiFactory(ip, port, user, password);
		
		/**
    	 * 0x325e8fb67018e4f314be20d858b2a7ef2a4cb589
    	 * 0x42b98d50c200a6751766faea53133c6807f02d7c
    	 * 0x6a7b1c056ef41a9c4c7a6b1aa35fd893b136eb81
    	 */
		
//		boolean net_listening = api.net_listening();
//		log.info(net_listening);
//		
//		String[] eth_accounts = api.eth_accounts();
//		for (String string : eth_accounts) {
//			log.info(string);
//		}
//		
//		long eth_blockNumber = api.eth_blockNumber();
//		log.info(eth_blockNumber);
		//0x00000000000000000000000000000000000000000000001b28c58d9696b40000
		//0x00000000000000000000000000000000000000000000001b28c58d9696b40000
		
//		BigDecimal eth_getBalance = api.eth_call("0xbb9bc244d798123fde783fcc1c72d3bb8c189413", "32be343b94f860124dc4fee278fdcbd38c102d88");
//		log.info("eth_call:" + eth_getBalance.toPlainString());
		
		
//		BigDecimal eth_getBalance = api.eth_getBalance("0x325e8fb67018E4f314BE20d858b2A7Ef2A4cB589", "latest");
//		log.info(eth_getBalance.toPlainString());
//		
//		long eth_getTransactionCount = api.eth_getTransactionCount("0xd0f7d5437930a888bfbe4533b5e5feebedff9769", "earliest");
//		log.info(eth_getTransactionCount);
		
		/**
		 * 0xbaf2816e46d09532ab9c994b26866989b47348f9fdba6595f3fa3a2a09c53ed4
		 * 
		 */
//		String eth_sendTransaction = api.eth_sendTransaction("0x42b98d50c200a6751766faea53133c6807f02d7c", "0x6ac119cf97e0d7f5b7842192e25a5879e8364147", new BigDecimal("0.13"));
//		log.info(eth_sendTransaction);
//		
//		EtherBlock eth_getBlockByHash = api.eth_getBlockByHash("0x4126c2aa54ef0630372003ee84367943f565e1c771c277601c27f92bdc9266b7", true);
//		log.info(eth_getBlockByHash.getNumber());
//		
//		EtherBlock eth_getBlockByNumber = api.eth_getBlockByNumber(HexFormatUtils.longToHex(1102357l), true);
//		log.info(eth_getBlockByNumber.getHash());
//		
//		EtherTransaction eth_getTransactionByHash = api.eth_getTransactionByHash("0x4cc6aec7bb0c36fafe1f81301fa1f682a12d0190aa12d7f8b4617c67a21157d9");
//		log.info(eth_getTransactionByHash.toString());
//		EtherTransaction eth_getTransactionByHash = api.eth_getTransactionByHash("0x4cc6aec7bb0c36fafe1f81301fa1f682a12d0190aa12d7f8b4617c67a21157d9");
//		log.info(eth_getTransactionByHash.getBlockNumber().longValue());
//		
//		EtherTransaction eth_getTransactionByBlockHashAndIndex = api.eth_getTransactionByBlockHashAndIndex("0x4126c2aa54ef0630372003ee84367943f565e1c771c277601c27f92bdc9266b7", 0);
//		log.info(eth_getTransactionByBlockHashAndIndex.getBlockNumber());
//		
//		EtherTransaction eth_getTransactionByBlockNumberAndIndex = api.eth_getTransactionByBlockNumberAndIndex(HexFormatUtils.longToHex(1102357l), 0);
//		log.info(eth_getTransactionByBlockNumberAndIndex.getBlockHash());
//		
		
//		String eth_newBlockFilter = api.eth_newBlockFilter();
//		log.info(eth_newBlockFilter);
//		
//		String eth_newPendingTransactionFilter = api.eth_newPendingTransactionFilter();
//		log.info(eth_newPendingTransactionFilter);
//		
//		boolean eth_uninstallFilter = api.eth_uninstallFilter("90986796543317578034955907177414657731");
//		log.info(eth_uninstallFilter);
//	
//		String[] eth_getFilterChanges = api.eth_getFilterChanges("90986796543317578034955907177414657731");
//		for (String string : eth_getFilterChanges) {
//			log.info(string);
//		}
		
//		log.info(HexFormatUtils.toHexString(new BigDecimal("0.25")));
		
		/***
		{"blockHash":"0xe72e3d6b520f4707ba1f55ac001c79a91f9d1401348e1396496791daa6e28e73",
			"blockNumber":"0x1749c2",
			"from":"0x401d3d0199d7cc174fe1e5a33b05918add947919",
			"gas":"0x24f25",
			"gasPrice":"0x4a817c800",
			"hash":"0xeb4aac5794b3b6f513f2844686490976fbc378d74097283b2c8725b63edb759b",
			"input":"0xa9059cbb0000000000000000000000009eefe31c0eac11e00547d38108fc53af00f2fc6e0000000000000000000000000000000000000000000000000000000005f5e100",
			"nonce":"0x0",
			"to":"0xe0b7927c4af23765cb51314a0e0521a9645f0e2a",
			"transactionIndex":"0x3",
			"value":"0x0"
		}
		 
		
		{"jsonrpc":"2.0",
			"id":55,
			"result":{
		"blockHash":"0xe72e3d6b520f4707ba1f55ac001c79a91f9d1401348e1396496791daa6e28e73",
		"blockNumber":"0x1749c2",
		"contractAddress":null,
		"cumulativeGasUsed":"0x1be9d",
		"from":"0x401d3d0199d7cc174fe1e5a33b05918add947919",
		"gasUsed":"0xc885",
		"logs":[{"address":"0xe0b7927c4af23765cb51314a0e0521a9645f0e2a",
			"blockHash":"0xe72e3d6b520f4707ba1f55ac001c79a91f9d1401348e1396496791daa6e28e73",
			"blockNumber":"0x1749c2",
			"data":"0x0000000000000000000000000000000000000000000000000000000005f5e100",//金额
			"logIndex":"0x0",
			"topics":["0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
			          "0x000000000000000000000000401d3d0199d7cc174fe1e5a33b05918add947919",
			          "0x0000000000000000000000009eefe31c0eac11e00547d38108fc53af00f2fc6e"],
			"transactionHash":"0xeb4aac5794b3b6f513f2844686490976fbc378d74097283b2c8725b63edb759b",
			"transactionIndex":"0x3"}],
			"root":"9a9db178931ffa6a018764e2c76f9a73e4c3368d4d61042602eb335ca879ce6a",
			"to":"0xe0b7927c4af23765cb51314a0e0521a9645f0e2a",
			"transactionHash":"0xeb4aac5794b3b6f513f2844686490976fbc378d74097283b2c8725b63edb759b",
			"transactionIndex":"0x3"}
	  }
	  
	  ///合约
	  
	    Transaction Receipt Event Logs 
		[0]	   Address    	0xe0b7927c4af23765cb51314a0e0521a9645f0e2a
		   Topics	[0] 0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef  (DGD Transfer)
		[1] 0x000000000000000000000000401d3d0199d7cc174fe1e5a33b05918add947919 (from=401d3d0199d7cc174fe1e5a33b05918add947919)
		[2] 0x0000000000000000000000009eefe31c0eac11e00547d38108fc53af00f2fc6e (to=9eefe31c0eac11e00547d38108fc53af00f2fc6e)
		Data	
		0x0000000000000000000000000000000000000000000000000000000005f5e100 (value=100000000)
***/

//		log.info(EtherHexFormatUtils.hexToInteger("0x0000000000000000000000000000000000000000000000000000000005f5e100"));
//		
//		log.info(EtherHexFormatUtils.hexToString("0x000000000000000000000000401d3d0199d7cc174fe1e5a33b05918add947919"));
	
//		String repTxt = "{\"jsonrpc\":\"2.0\",\"id\":79,\"result\":{\"blockHash\":\"0xe72e3d6b520f4707ba1f55ac001c79a91f9d1401348e1396496791daa6e28e73\",\"blockNumber\":\"0x1749c2\",\"contractAddress\":null,\"cumulativeGasUsed\":\"0x1be9d\",\"from\":\"0x401d3d0199d7cc174fe1e5a33b05918add947919\",\"gasUsed\":\"0xc885\",\"logs\":[{\"address\":\"0xe0b7927c4af23765cb51314a0e0521a9645f0e2a\",\"blockHash\":\"0xe72e3d6b520f4707ba1f55ac001c79a91f9d1401348e1396496791daa6e28e73\",\"blockNumber\":\"0x1749c2\",\"data\":\"0x0000000000000000000000000000000000000000000000000000000005f5e100\",\"logIndex\":\"0x0\",\"topics\":[\"0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef\",\"0x000000000000000000000000401d3d0199d7cc174fe1e5a33b05918add947919\",\"0x0000000000000000000000009eefe31c0eac11e00547d38108fc53af00f2fc6e\"],\"transactionHash\":\"0xeb4aac5794b3b6f513f2844686490976fbc378d74097283b2c8725b63edb759b\",\"transactionIndex\":\"0x3\"}],\"root\":\"9a9db178931ffa6a018764e2c76f9a73e4c3368d4d61042602eb335ca879ce6a\",\"to\":\"0xe0b7927c4af23765cb51314a0e0521a9645f0e2a\",\"transactionHash\":\"0xeb4aac5794b3b6f513f2844686490976fbc378d74097283b2c8725b63edb759b\",\"transactionIndex\":\"0x3\"}}";
//		log.info(RpcResponse.parseObject(79, repTxt).parseEtherTransactionReceipt(repTxt).getLogsTxid());
		
//		EtherTransactionReceipt eth_getTransactionReceipt = api.eth_getTransactionReceipt("0x4cc6aec7bb0c36fafe1f81301fa1f682a12d0190aa12d7f8b4617c67a21157d9");
//		log.info("logsTxToAddress="+eth_getTransactionReceipt.getLogsTxToAddress() + "\tDAO_TO_ADDRESS=" + eth_getTransactionReceipt.getTo());
		
//		EtherTransaction eth_getTransactionByHash = (EtherTransaction)api.eth_getTransactionByHashNew( "0x4cc6aec7bb0c36fafe1f81301fa1f682a12d0190aa12d7f8b4617c67a21157d9" )[1];
//		log.info(eth_getTransactionByHash.getBlockHash());
		
//		EtherBlock eth_getBlockByNumber = api.eth_getBlockByNumber(HexFormatUtils.longToHex(1102357l), true);
//		log.info(eth_getBlockByNumber.getHash());
		
//		EtherTransaction eth_getTransactionByHash = api.eth_getTransactionByHash("0x32743d046864e1127480a30b461cbc3d08ce7dbc");
//		log.info( eth_getTransactionByHash.getTo() );
//		
//		EtherTransactionReceipt eth_getTransactionReceipt = api.eth_getTransactionReceipt("0x04728f89346173177aad1424933f1c18443168754de292ef5e7bfe525606e8e6");
//		log.info( "getLogsTxToAddress=" + eth_getTransactionReceipt.getLogsTxToAddress() + "\tgetLogsTxid=" + eth_getTransactionReceipt.getLogsTxid() + "\tto=" + eth_getTransactionReceipt.getTo());
//		log.info("接收金额=" + EtherHexFormatUtils.getDAO(eth_getTransactionReceipt.getLogsData() ) );

//		BigDecimal eth_getBalance = api.eth_call("DGD", "0xe0b7927c4af23765cb51314a0e0521a9645f0e2a", "0x325e8fb67018e4f314be20d858b2a7ef2a4cb589".replaceAll("0x", ""));
//		log.info("eth_call:" + eth_getBalance.toPlainString());
		
//		String sendTxid = api.eth_sendTransactionDAO("0x325e8fb67018e4f314be20d858b2a7ef2a4cb589", "0x5520598c326c460bc83938d87118451502598b77", "0xe0b7927c4af23765cb51314a0e0521a9645f0e2a", "0xa9059cbb000000000000000000000000", new BigDecimal("0.001") );
//		log.info("发送DAO，发送交易id=" + sendTxid);
		
//		BigDecimal walletAmount = api.eth_call("DAO", "0xe0b7927c4af23765cb51314a0e0521a9645f0e2a", "0x325e8fb67018e4f314be20d858b2a7ef2a4cb589".replaceAll("0x", "") );
//		log.info("ETH的DAO钱包余额=" + walletAmount);
		
//		EtherBlock block = api.eth_getBlockByNumber(EtherBlockStatus.latest.getValue(), true);
//		log.info("ETH区块最新的高度=" + block.getNumber() );
		
//		BigDecimal eth_getBalance = api.eth_getBalance("0x325e8fb67018E4f314BE20d858b2A7Ef2A4cB589", "latest");
//		log.info(eth_getBalance.toPlainString());
		
//		String eth_sendTransaction = api.eth_sendTransaction("0x325e8fb67018E4f314BE20d858b2A7Ef2A4cB589", "0xac09d118b20097FBbFEF629Fb98028275c383dd4", new BigDecimal("0.13"));
//		log.info(eth_sendTransaction);
		
//		log.info("gasPrice=" + api.eth_gasPrice().toPlainString() );
		
		String oldIp = "192.168.5.94";
		String oldPort = "9357";
		
		String newIp = "192.168.4.21";
		String newPort = "9997";
				
		EtherWalletApiFactory oldApi = EtherWalletApiFactory.getInstance(oldIp, oldPort);
		EtherWalletApiFactory newApi = EtherWalletApiFactory.getInstance(newIp, newPort);
		
//		6.64316181		6.610901819379780926
//		0xac09d118b20097FBbFEF629Fb98028275c383dd4	充值地址
//		0x325e8fb67018e4f314be20d858b2a7ef2a4cb589	原提现地址
//		0xe3742310fdf9bdc28b9b1143717d8fc2a0254c21	旧钱包提现地址
//		0xd6c28332c8032764677900f5134f4c33695d2827	新钱包提现地址
		
//		BigDecimal eth_getBalance = newApi.eth_getBalance("0x325e8fb67018e4f314be20d858b2a7ef2a4cb589", "latest");
//		log.info(eth_getBalance.toPlainString());
		
		String old_eth_sendTransaction = oldApi.eth_sendTransaction("0xe3742310fdf9bdc28b9b1143717d8fc2a0254c21", "0xac09d118b20097FBbFEF629Fb98028275c383dd4", new BigDecimal("0.12"));
		log.info(old_eth_sendTransaction);
//		String new_eth_sendTransaction = newApi.eth_sendTransaction("0xe3742310fdf9BdC28b9B1143717D8fC2A0254c21", "0xac09d118b20097FBbFEF629Fb98028275c383dd4", new BigDecimal("0.26"));
//		log.info(new_eth_sendTransaction);
	}

}
