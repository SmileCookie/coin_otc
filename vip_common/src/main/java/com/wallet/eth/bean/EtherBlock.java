package com.wallet.eth.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class EtherBlock implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * QUANTITY - the block number. null when its pending block.
	 */
    private BigInteger number;
    
    /**
     * DATA, 32 Bytes - hash of the block. null when its pending block.
     */
    private String hash;
    
    /**
     * DATA, 32 Bytes - hash of the parent block.
     */
    private String parentHash;
    
    /**
     * DATA, 8 Bytes - hash of the generated proof-of-work. null when its pending block.
     */
    private String nonce;
    
    /**
     * DATA, 32 Bytes - SHA3 of the uncles data in the block.
     */
    private String sha3Uncles;
    
    /**
     * DATA, 256 Bytes - the bloom filter for the logs of the block. null when its pending block.
     */
    private String logsBloom;
    
    /**
     * DATA, 32 Bytes - the root of the transaction trie of the block.
     */
    private String transactionsRoot;
    
    /**
     * DATA, 32 Bytes - the root of the final state trie of the block.
     */
    private String stateRoot;
   
    /**
     * DATA, 32 Bytes - the root of the receipts trie of the block.
     */
    private String receiptsRoot;
    
    /**
     * DATA, 20 Bytes - the address of the beneficiary to whom the mining rewards were given.
     */
    private String miner;
    
    /**
     * QUANTITY - integer of the difficulty for this block.
     */
    private BigInteger difficulty;
    
    /**
     * QUANTITY - integer of the total difficulty of the chain until this block.
     */
    private BigInteger totalDifficulty;
    
    /**
     * DATA - the "extra data" field of this block.
     */
    private String extraData;
    
    /**
     * QUANTITY - integer the size of this block in bytes.
     */
    private BigInteger size;
    
    /**
     * QUANTITY - the maximum gas allowed in this block.
     */
    private BigInteger gasLimit;
    
    /**
     * QUANTITY - the total used gas by all transactions in this block.
     */
    private BigInteger gasUsed;
    
    /**
     * QUANTITY - the unix timestamp for when the block was collated.
     * 1424182926
     */
    private BigInteger timestamp;
    
    /**
     * Array - Array of transaction objects, or 32 Bytes transaction hashes depending on the last given parameter.
     */
    private List<EtherTransaction> transactions;
    
    /**
     * Array - Array of uncle hashes.
     */
    private String[] uncles;

	public BigInteger getNumber() {
		if(number == null){
			return BigInteger.ZERO;
		}
		return number;
	}

	public void setNumber(BigInteger number) {
		this.number = number;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getParentHash() {
		return parentHash;
	}

	public void setParentHash(String parentHash) {
		this.parentHash = parentHash;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getSha3Uncles() {
		return sha3Uncles;
	}

	public void setSha3Uncles(String sha3Uncles) {
		this.sha3Uncles = sha3Uncles;
	}

	public String getLogsBloom() {
		return logsBloom;
	}

	public void setLogsBloom(String logsBloom) {
		this.logsBloom = logsBloom;
	}

	public String getTransactionsRoot() {
		return transactionsRoot;
	}

	public void setTransactionsRoot(String transactionsRoot) {
		this.transactionsRoot = transactionsRoot;
	}

	public String getStateRoot() {
		return stateRoot;
	}

	public void setStateRoot(String stateRoot) {
		this.stateRoot = stateRoot;
	}

	public String getReceiptsRoot() {
		return receiptsRoot;
	}

	public void setReceiptsRoot(String receiptsRoot) {
		this.receiptsRoot = receiptsRoot;
	}

	public String getMiner() {
		return miner;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

	public BigInteger getDifficulty() {
		if(difficulty == null){
			return BigInteger.ZERO;
		}
		return difficulty;
	}

	public void setDifficulty(BigInteger difficulty) {
		this.difficulty = difficulty;
	}

	public BigInteger getTotalDifficulty() {
		if(totalDifficulty == null){
			return BigInteger.ZERO;
		}
		return totalDifficulty;
	}

	public void setTotalDifficulty(BigInteger totalDifficulty) {
		this.totalDifficulty = totalDifficulty;
	}

	public String getExtraData() {
		return extraData;
	}

	public void setExtraData(String extraData) {
		this.extraData = extraData;
	}

	public BigInteger getSize() {
		if(size == null){
			return BigInteger.ZERO;
		}
		return size;
	}

	public void setSize(BigInteger size) {
		this.size = size;
	}

	public BigInteger getGasLimit() {
		if(gasLimit == null){
			return BigInteger.ZERO;
		}
		return gasLimit;
	}

	public void setGasLimit(BigInteger gasLimit) {
		this.gasLimit = gasLimit;
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

	public BigInteger getTimestamp() {
		if(timestamp == null){
			return BigInteger.ZERO;
		}
		return timestamp;
	}

	public void setTimestamp(BigInteger timestamp) {
		this.timestamp = timestamp;
	}

	public List<EtherTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<EtherTransaction> transactions) {
		this.transactions = transactions;
	}

	public String[] getUncles() {
		return uncles;
	}

	public void setUncles(String[] uncles) {
		this.uncles = uncles;
	}
    
}
