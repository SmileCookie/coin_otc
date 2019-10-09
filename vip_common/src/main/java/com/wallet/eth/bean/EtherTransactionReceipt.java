package com.wallet.eth.bean;

import java.io.Serializable;
import java.math.BigInteger;

public class EtherTransactionReceipt implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * DATA, 32 Bytes - hash of the transaction.
	 */
    private String transactionHash; 
    
    /**
     * QUANTITY - integer of the transactions index position in the block.
     */
    private BigInteger transactionIndex; 
    
    /**
     * DATA, 32 Bytes - hash of the block where this transaction was in.
     */
    private String blockHash; 
    
    /**
     * QUANTITY - block number where this transaction was in.
     */
    private BigInteger blockNumber;
    
    /**
     * QUANTITY - The total amount of gas used when this transaction was executed in the block.
     */
    private BigInteger cumulativeGasUsed; 
    
    /**
     * QUANTITY - The amount of gas used by this specific transaction alone.
     */
    private BigInteger gasUsed; 
    
    /**
     * DATA, 20 Bytes - The contract address created, if the transaction was a contract creation, otherwise null.
     */
    private String contractAddress; 
    
    /**
     *	DATA, 20 Bytes - The address the transaction is send from.
     */
    private String from;
    
    /**
     *  DATA, 20 Bytes - The address the transaction is directed to.
     */
    private String to;
    
    //logs的内容 -----------------开始
    //地址
    private String logsAddress;
    
    private String logsBlockHash;
    
    private String logsBlockNumber;
    
    private String logsData;
    
    private String logsIndex;
    
    //topics里面的第一个，指的是这笔交易的hash值
    private String logsTxid;
    
    //topics里面的第二个，指的是这笔交易的发送地址
    private String logsTxFromAddress;
    
    //topics里面的第三个，指的是这笔交易的接收地址
    private String logsTxToAddress;
    
    private String logsTransactionHash;
    
    private String logsTransactionIndex;

	public String getTransactionHash() {
		return transactionHash;
	}

	public void setTransactionHash(String transactionHash) {
		this.transactionHash = transactionHash;
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

	public BigInteger getCumulativeGasUsed() {
		if(cumulativeGasUsed == null){
			return BigInteger.ZERO;
		}
		return cumulativeGasUsed;
	}

	public void setCumulativeGasUsed(BigInteger cumulativeGasUsed) {
		this.cumulativeGasUsed = cumulativeGasUsed;
	}

	public BigInteger getGasUsed() {
		if(gasUsed == null){
			return BigInteger.ZERO;
		}
		return gasUsed;
	}

	public void setGasUsed(BigInteger gasUsed) {
		this.gasUsed = gasUsed;
	}

	public String getContractAddress() {
		return contractAddress;
	}

	public void setContractAddress(String contractAddress) {
		this.contractAddress = contractAddress;
	}

	public String getFrom() {
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

	public String getLogsAddress() {
		return logsAddress;
	}

	public void setLogsAddress(String logsAddress) {
		this.logsAddress = logsAddress;
	}

	public String getLogsBlockHash() {
		return logsBlockHash;
	}

	public void setLogsBlockHash(String logsBlockHash) {
		this.logsBlockHash = logsBlockHash;
	}

	public String getLogsBlockNumber() {
		return logsBlockNumber;
	}

	public void setLogsBlockNumber(String logsBlockNumber) {
		this.logsBlockNumber = logsBlockNumber;
	}

	public String getLogsData() {
		return logsData;
	}

	public void setLogsData(String logsData) {
		this.logsData = logsData;
	}

	public String getLogsIndex() {
		return logsIndex;
	}

	public void setLogsIndex(String logsIndex) {
		this.logsIndex = logsIndex;
	}

	public String getLogsTxid() {
		return logsTxid;
	}

	public void setLogsTxid(String logsTxid) {
		this.logsTxid = logsTxid;
	}

	public String getLogsTxFromAddress() {
		return logsTxFromAddress;
	}

	public void setLogsTxFromAddress(String logsTxFromAddress) {
		this.logsTxFromAddress = logsTxFromAddress;
	}

	public String getLogsTxToAddress() {
		return logsTxToAddress;
	}

	public void setLogsTxToAddress(String logsTxToAddress) {
		this.logsTxToAddress = logsTxToAddress;
	}

	public String getLogsTransactionHash() {
		return logsTransactionHash;
	}

	public void setLogsTransactionHash(String logsTransactionHash) {
		this.logsTransactionHash = logsTransactionHash;
	}

	public String getLogsTransactionIndex() {
		return logsTransactionIndex;
	}

	public void setLogsTransactionIndex(String logsTransactionIndex) {
		this.logsTransactionIndex = logsTransactionIndex;
	}
	
}
