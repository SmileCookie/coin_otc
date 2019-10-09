package com.wallet.eth.bean;

import java.io.Serializable;
import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;

public class EtherTransaction implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * DATA, 32 Bytes - hash of the transaction.
	 */
    private String hash; 
    
    /**
     * QUANTITY - the number of transactions made by the sender prior to this one.
     */
    private BigInteger nonce; 
    
    /**
     * DATA, 32 Bytes - hash of the block where this transaction was in. null when its pending.
     */
    private String blockHash; 
    
    /**
     * QUANTITY - block number where this transaction was in. null when its pending.
     */
    private BigInteger blockNumber; 
    
    /**
     * QUANTITY - integer of the transactions index position in the block. null when its pending.
     */
    private BigInteger transactionIndex; 
    
    /**
     * DATA, 20 Bytes - address of the sender.
     */
    private String from; 
    
    /**
     * DATA, 20 Bytes - address of the receiver. null when its a contract creation transaction.
     */
    private String to; 
    
    /**
     * QUANTITY - value transferred in Wei.
     */
    private BigInteger value; 
    
    /**
     * QUANTITY - gas price provided by the sender in Wei.
     */
    private BigInteger gasPrice;
    
    /**
     * QUANTITY - gas provided by the sender.
     */
    private BigInteger gas; 
    
    /**
     * DATA - the data send along with the transaction.
     */
    private String input;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public BigInteger getNonce() {
		if(nonce == null){
			return BigInteger.ZERO;
		}
		return nonce;
	}

	public void setNonce(BigInteger nonce) {
		this.nonce = nonce;
	}

	public String getBlockHash() {
		return blockHash;
	}

	public void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public BigInteger getBlockNumber() {
		if(blockNumber == null){
			return BigInteger.ZERO;
		}
		return blockNumber;
	}

	public void setBlockNumber(BigInteger blockNumber) {
		this.blockNumber = blockNumber;
	}

	public BigInteger getTransactionIndex() {
		if(transactionIndex == null){
			return BigInteger.ZERO;
		}
		return transactionIndex;
	}

	public void setTransactionIndex(BigInteger transactionIndex) {
		this.transactionIndex = transactionIndex;
	}

	public String getFrom() {
		if(StringUtils.isEmpty(from)) {
			return "unknow";
		}
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public BigInteger getValue() {
		if(value == null){
			return BigInteger.ZERO;
		}
		return value;
	}

	public void setValue(BigInteger value) {
		this.value = value;
	}

	public BigInteger getGasPrice() {
		if(gasPrice == null){
			return BigInteger.ZERO;
		}
		return gasPrice;
	}

	public void setGasPrice(BigInteger gasPrice) {
		this.gasPrice = gasPrice;
	}

	public BigInteger getGas() {
		if(gas == null){
			return BigInteger.ZERO;
		}
		return gas;
	}

	public void setGas(BigInteger gas) {
		this.gas = gas;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

}
